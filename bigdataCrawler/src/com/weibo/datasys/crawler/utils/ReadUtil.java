/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.CommonDownloader;
import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.commonDownloader.worker.Controller;

public class ReadUtil {

	private static Logger logger = LoggerFactory.getLogger(ReadUtil.class);

	public static boolean isRespComplete(InnerRequest req, Controller controller) {
		boolean isComplete = false;
		try {
			DownResponse resp = parseResponse(req, controller);
			// 解析应答完毕没有异常，先判断是否200应答
			if (resp.getRetCode() == 200) {
				// 然后判断内容长度是否为0,
				String lengthString = resp.getHeaders().get("Content-Length");
				int contentLength = Integer.MAX_VALUE;
				if (lengthString != null) {
					contentLength = Integer.parseInt(lengthString);
				}
				String contentEncoding = resp.getHeaders().get(
						"Content-Encoding");
				String transferEncoding = resp.getHeaders().get(
						"Transfer-Encoding");
				// 内容长度不为0才继续判断完整性
				if (contentLength != 0) {
					// 如果内容没有被压缩或chunked才需要特殊判断完整性
					if ((contentEncoding == null || !contentEncoding
							.matches("gzip|deflate"))
							&& (transferEncoding == null || !transferEncoding
									.equals("chunked"))) {
						// 若内容长度不足再继续判断完整性
						if (resp.getContentByte().length < contentLength) {
							// 将应答内容转换成String
							String content = HtmlTools.getStringContentOfHtml(
									resp.getContentByte(), "utf-8")
									.toLowerCase();
							// 有rss或html起始标签却没有结束标签则不完整
							if (content.contains("<rss")
									|| content.contains("<html")) {
								if (!content.contains("</rss>")
										&& !content.contains("</html>")) {
									throw new IOException("Incomplete rss/html");
								}
							} else {
								throw new IOException(
										"Incomplete content length.");
							}
						}
					}// end of 没有压缩和chunked
				}// end of Content-Length != 0
			}// end of retCode==200
			isComplete = true;
			req.setResp(resp);
		} catch (Exception e) {
			// logger.debug("[IncompleteResp] - url={} e.msg={} e={}",
			// new Object[] { req.getUrl().toExternalForm(),
			// e.getMessage(), e.getClass() });
		}
		return isComplete;
	}

	/**
	 * 
	 * 解析http response，构造DownResponse对象
	 * 
	 * @param req
	 * @param downloader
	 * @return
	 * @throws IOException
	 */
	public static DownResponse parseResponse(InnerRequest req,
			Controller controller) throws IOException {

		CommonDownloader downloader = controller.getDownloader();
		DownResponse resp = new DownResponse(req);

		// 按行读取所用的 StringBuffer
		StringBuffer line = new StringBuffer();
		// 初始化buffer
		int bufferSize = 10240;
		byte[] arrayBuffer = new byte[bufferSize];

		// 获得请求对象中的应答内容内存流
		ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
		byteOS.write(req.getByteContentBuffer().toByteArray());

		// 用 PushbackInputStream 包装应答内容的内存流，以实现按行读取
		PushbackInputStream pushIn = new PushbackInputStream(
				new ByteArrayInputStream(byteOS.toByteArray()));

		// 重置内存流以复用之
		byteOS.reset();

		// 读取 http 响应码
		int retCode = 0;
		if (readLine(pushIn, line, true) > 0) {
			if (line.length() >= 12) {
				retCode = Integer.parseInt(line.substring(9, 12));
			} else {
				resp.setRetCode(-1);
				resp.setContentByte(req.getByteContentBuffer().toByteArray());
				return resp;
			}
		}
		resp.setRetCode(retCode);

		// 读取headers并保存到response里
		getHeaders(pushIn, resp);

		if (retCode != 304) {
			// retCode 为 200
			if (retCode == 200) {
				// 读取 ByteContent
				byte[] byteContent = getByteContent(pushIn, line, arrayBuffer,
						byteOS, resp);
				resp.setContentByte(byteContent);
			}
			if (retCode >= 300 && retCode < 400) {
				// retCode 为 重定向代码
				req.setRedirectTimes(req.getRedirectTimes() + 1);
				// 如果允许重定向的总次数大于等于请求当前已重定向次数，则跟随重定向
				int maxRedirectTimes = req.getMaxRedirectTimes();
				if (maxRedirectTimes < 0) {
					maxRedirectTimes = downloader.getMaxRedirectTimes();
				}
				if (maxRedirectTimes >= req.getRedirectTimes()) {
					String location = resp.getHeaders().get("Location");
					if (location == null) {
						location = resp.getHeaders().get("location");
					}
					// Header里的Location字段不为空，则
					if (location != null) {
						// 创建重定向的新请求
						req = getRedirectReq(req, location, resp.getHeaders()
								.get("Set-Cookie"));
						// 将新请求放进请求队列
						controller.addRequest(req);
					} else {
						throw new IOException("找不到重定向Location");
					}
				} else {
					// 重定向次数达到上限
					// 读取 ByteContent
					byte[] byteContent = getByteContent(pushIn, line,
							arrayBuffer, byteOS, resp);
					resp.setContentByte(byteContent);
				}
			}// end of handle redirect
		}
		// 应答码为其他类则忽略之留给外部处理

		pushIn.close();
		return resp;
	}

	/**
	 * 
	 * 创建重定向的新请求
	 * 
	 * @param req
	 *            当前请求
	 * @param location
	 *            重定向的目的地址
	 * @param cookie
	 *            新请求需要附带的cookie
	 * @return
	 * @throws IOException
	 */
	private static InnerRequest getRedirectReq(InnerRequest req,
			String location, String cookie) throws IOException {
		// 处理相对路径
		if (!location.startsWith("http://")) {
			int index = location.indexOf("/");
			if (index == -1)
				location = "/" + location;
			String portString = "";
			if (req.getUrl().getPort() != -1) {
				portString = ":" + req.getUrl().getPort();
			}
			location = "http://" + req.getUrl().getHost() + portString
					+ location;
		}

		// 复制当前请求
		req = req.copy();
		// 设置url为重定向的url
		req.setUrl(location);
		// 如果有cookie则设置cookie
		if (cookie != null) {
			cookie += ";" + req.getCookie();
			req.setCookie(cookie);
		}
		return req;
	}

	/**
	 * 
	 * 从 PushbackInputStream 里读取 http headers，并保存到DownResponse中
	 * 
	 * @param pushIn
	 * @param resp
	 * @throws IOException
	 */
	private static void getHeaders(InputStream pushIn, DownResponse resp)
			throws IOException {
		StringBuffer line = new StringBuffer();
		do {
			readLine(pushIn, line, true);
			if (line.length() > 0) {
				String[] headerStrings = line.toString().split(": ");
				if (headerStrings.length < 2) {
					headerStrings = line.toString().split(":");
					if (headerStrings.length < 2) {
						continue;
					}
				}
				if (headerStrings[0].equals("Set-Cookie")) {
					// 拼接完整 Set-Cookie header
					if (resp.getHeaders().containsKey(headerStrings[0])) {
						headerStrings[1] += ";"
								+ resp.getHeaders().get(headerStrings[0]);
					}
					// 拼接只包含cookie值的cookies header
					String cookies = headerStrings[1];
					int index = cookies.indexOf(";");
					if (index > 0) {
						cookies = cookies.substring(0, index);
					}
					if (resp.getHeaders().containsKey("cookies")) {
						cookies += ";" + resp.getHeaders().get("cookies");
					}
					resp.getHeaders().put("cookies", cookies);
				}
				resp.getHeaders().put(headerStrings[0], headerStrings[1]);
			}
		} while (line.length() != 0);
	}

	/**
	 * 
	 * 从 PushbackInputStream 里读取http应答的content部分，以字节数组形式返回
	 * 
	 * @param pushIn
	 * @param byteOut
	 * @param buffer
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	private static byte[] getByteContent(InputStream pushIn, StringBuffer line,
			byte[] buffer, ByteArrayOutputStream byteOut, DownResponse resp)
			throws IOException {

		byte[] byteContent;

		String transferEncoding = resp.getHeaders().get("Transfer-Encoding");
		if ("chunked".equals(transferEncoding)) {
			// content 被 chunked 则 调用 readChunkedContent 方法
			byteContent = readChunkedContent(pushIn, line, buffer, byteOut);
		} else {
			String lengthString = resp.getHeaders().get("Content-Length");
			int contentLength = Integer.MAX_VALUE;
			if (lengthString != null) {
				contentLength = Integer.parseInt(lengthString);
				if (contentLength == 0) {
					return new byte[0];
				}
				if (contentLength > pushIn.available()) {
					throw new IOException("Incomplete plain content.");
				}
			}
			byteContent = readPlainContent(pushIn, contentLength, buffer,
					byteOut);
		}

		// content被压缩则解压
		String contentEncoding = resp.getHeaders().get("Content-Encoding");
		if (contentEncoding != null) {
			contentEncoding = contentEncoding.toLowerCase();
			InputStream in = null;
			if (contentEncoding.contains("gzip")) {
				// 用 GZIPInputStream 解压
				in = new GZIPInputStream(new ByteArrayInputStream(byteContent));
			} else if (contentEncoding.contains("deflate")) {
				// 用 InflaterInputStream 解压
				in = new InflaterInputStream(new ByteArrayInputStream(
						byteContent));
			}
			// 将解压后的 Bytes 读取到 ByteArrayOutputStream
			byteOut.reset();
			try {
				int i = 0;
				while (-1 != (i = in.read(buffer))) {
					byteOut.write(buffer, 0, i);
				}
			} catch (IOException e) {
				byteContent = byteOut.toByteArray();
				String content = HtmlTools.getStringContentOfHtml(byteContent,
						"utf-8").toLowerCase();
				// 有rss或html起始标签却没有结束标签则不完整
				if (content.contains("<rss") || content.contains("<html")) {
					if (!content.contains("</rss>")
							&& !content.contains("</html>")) {
						throw new IOException("GzipEOF & Incomplete rss/html");
					}
				}
			}
			byteContent = byteOut.toByteArray();
		}// end of 解压

		return byteContent;
	}

	/**
	 * 
	 * 读取 plain content
	 * 
	 * @param pushIn
	 * @param contentLength
	 * @param buffer
	 * @param byteOut
	 * 
	 * @return
	 * @throws IOException
	 */
	private static byte[] readPlainContent(InputStream pushIn,
			int contentLength, byte[] buffer, ByteArrayOutputStream byteOut)
			throws IOException {
		// 读取content的bytes
		int i = 0;
		do {
			i = pushIn.read(buffer);
			if (i != -1) {
				byteOut.write(buffer, 0, i);
			}
		} while (i >= 0 && byteOut.size() < contentLength);
		return byteOut.toByteArray();
	}

	/**
	 * 
	 * 读取被 chunked 的 content
	 * 
	 * @param in
	 * @param line
	 * @param byteOut
	 * @param buffer
	 * @return
	 * @throws Exception
	 */
	private static byte[] readChunkedContent(InputStream in, StringBuffer line,
			byte[] buffer, ByteArrayOutputStream byteOut) throws IOException {
		boolean doneChunks = false;
		int bufferSize = buffer.length;
		while (!doneChunks) {
			int lineLength = readLine(in, line, false);
			if (lineLength == 0) {
				throw new IOException("Unexpected end of chunked content.");
			}
			String chunkLenStr;
			int pos = line.indexOf(";");
			if (pos < 0) {
				chunkLenStr = line.toString();
			} else {
				chunkLenStr = line.substring(0, pos);
			}
			chunkLenStr = chunkLenStr.trim();
			int chunkLen = 0;
			chunkLen = Integer.parseInt(chunkLenStr, 16);
			if (chunkLen == 0) {
				doneChunks = true;
				break;
			}
			// read one chunk
			int chunkBytesRead = 0;
			while (chunkBytesRead < chunkLen) {

				int toRead = (chunkLen - chunkBytesRead) < bufferSize ? (chunkLen - chunkBytesRead)
						: bufferSize;
				int len = in.read(buffer, 0, toRead);
				if (len < 0) {
					throw new IOException("Chunked Content incomplete.");
				}
				byteOut.write(buffer, 0, len);
				chunkBytesRead += len;
			}
			readLine(in, line, false);
		}
		return byteOut.toByteArray();
	}

	/**
	 * 
	 * 从 PushbackInputStream 里读取一行字符，并将字符序列放进StringBuffer中
	 * 
	 * @param in
	 * @param line
	 * @param allowContinuedLine
	 * @return
	 * @throws
	 * @throws IOException
	 * @throws Exception
	 */
	private static int readLine(InputStream in, StringBuffer line,
			boolean allowContinuedLine) throws IOException {

		line.setLength(0);

		for (int c = in.read(); c != -1; c = in.read()) {
			switch (c) {
			case '\r':
				if (peek(in) == '\n') {
					in.read();
				}
			case '\n':
				if (line.length() > 0) {
					if (allowContinuedLine)
						switch (peek(in)) {
						case ' ':
						case '\t': // line is continued
							in.read();
							continue;
						}
				}
				return line.length(); // else complete
			default:
				line.append((char) c);
			}
		}
		return line.length();
	}

	/**
	 * 
	 * 从 PushbackInputStream 中读取一个字节，但流的当前位置不改变，用于检测流的下一个字节是什么
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static int peek(InputStream in) throws IOException {
		PushbackInputStream pin = (PushbackInputStream) in;
		int value = pin.read();
		pin.unread(value);
		return value;
	}

}
