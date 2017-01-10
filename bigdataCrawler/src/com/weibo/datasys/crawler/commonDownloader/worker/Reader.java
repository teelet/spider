/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader.worker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.commonDownloader.util.DownLogUtil;
import com.weibo.datasys.crawler.commonDownloader.util.ReadUtil;

public class Reader implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(Reader.class);

	private ByteBuffer byteBuffer = ByteBuffer.allocate(10240);

	private Controller controller;

	public Reader(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		while (!controller.getDownloader().isClosed()) {
			try {
				SelectionKey key = controller.pollReadableKey();
				if (key != null) {
					InnerRequest req = (InnerRequest) key.attachment();
					try {
						// Try to read the http response
						boolean isReadEnd = readResponse(key);
						if (isReadEnd) {
							// 如果response读取结束，即完全读取或出现异常，则根据读取结果构造DownResponse对象
							// 如果req中包含异常(读取时发生的异常)，则抛出此异常，进入异常处理流程
							if (req.getException() != null) {
								throw req.getException();
							}

							// 因为在读取应答过程中，当从SocketChannel读到0字节的时候，会尝试解析应答流构造DownResponse
							// 所以取出req中的resp，看看是否解析成功了，避免重复解析
							DownResponse resp = req.getResp();
							if (resp == null) {
								// resp为null则解析http应答，构造DownResponse对象
								resp = ReadUtil.parseResponse(req, controller);
								req.setResp(resp);
							}

							if (resp != null) {
								// resp不为null，检查应答码
								int retCode = resp.getRetCode();
								if (retCode >= 300 && retCode != 304) {
									// 应答码不为200且不为重定向类别，则制造异常
									if (retCode >= 400) {
										// 抛出应答码异常
										throw new IOException("RetCode="
												+ retCode);
									} else {
										// 如果应答属于重定向类别，且未超过重定向次数上限，则忽略此应答，不放进队列，同时把SelectionKey注销
										if (resp.getRedirectTimes() <= req
												.getMaxRedirectTimes()) {
											((SocketChannel) key.channel())
													.socket().close();
											key.cancel();
											key.channel().close();
											continue;
										}
									}
								}
							}
							// 如无意外将应答放进队列
							controller.putNormalResp(req, resp, key);
						} else {
							// 如果没读取完，则什么也不干，进入下一循环，读取下一个准备好的key
						}

					} catch (Exception e) {
						req.setException(e);
						// 尝试打印异常
						DownLogUtil.error(controller, req, "ReaderError",
								logger);
						// 将异常应答放入应答队列
						controller.putExceptionResp(req, key);
					}
				} else {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
				}
			} catch (Throwable e) {
				logger.error("[ReaderThreadError] - ", e);
			}
		}
	}

	/**
	 * 
	 * Try to read the http response which is ready for reading from the
	 * specific SelectionKey.
	 * 
	 * @param key
	 *            SelectionKey that contains http response ready for read.
	 * @return true if the whole resp is read OR exception occured; false if
	 *         resp is not read completely
	 */
	private boolean readResponse(SelectionKey key) {
		boolean isReadEnd = false;
		byteBuffer.clear();
		InnerRequest req = (InnerRequest) key.attachment();
		SocketChannel channel = (SocketChannel) key.channel();
		// 尝试注册当前读取的Key，并获取其相关respBuffer
		ByteArrayOutputStream respBuffer = controller.regIncompleteKey(key);
		// 将respBuffer与req关联
		req.setByteContentBuffer(respBuffer);
		// Start reading
		int readCount = 0;
		try {
			// 循环从Channel缓冲区读取resp，直到返回0或-1
			while ((readCount = channel.read(byteBuffer)) > 0) {
				respBuffer.write(byteBuffer.array(), 0, readCount);
				byteBuffer.clear();
				// 累计已下载字节数
				controller.addDownBytes(readCount);
			}
			// 如果readCount==0，则判断resp是否已完整，以免被恶心网站拖住流结尾符
			if (readCount == 0 && ReadUtil.isRespComplete(req, controller)) {
				readCount = -1;
			}
		} catch (IOException ex) {
			req.setException(ex);
			readCount = -1;
		}
		if (readCount == -1) {
			// readCount == -1，不管读取成功与否都结束读取过程了，记录endReadTime
			req.setEndReadTime(System.currentTimeMillis());
			controller.unRegIncompleteKey(key);
			isReadEnd = true;
		} else {
			// 没读取完毕，设置key的关心操作为1，即read
			key.interestOps(1);
		}
		return isReadEnd;
	}
}
