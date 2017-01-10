/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.charset;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncodingExtractor
{

	private static final int CHUNK_SIZE = 2000;
	private static String defaultCharEncoding = "gbk";
	// 检测字符集的正则表达式
	private static Pattern charsetPattern = Pattern
			.compile(
					"<meta\\s(http-equiv\\s*=\\s*[\"']?Content-Type[\"']?)?\\s*(content\\s*=\\s*[\"'].+?)?charset\\s*=\\s*[\"']?([^\"']+)",
					Pattern.CASE_INSENSITIVE);

	/**
	 * 从字节流获取编码类型
	 * 
	 * @param html
	 *            网页字节流
	 * @return 编码类型
	 */
	public static String GetEncoding(byte[] html, String headEncoding)
	{
		String encoding = null;
		try
		{
			// 取若干个字节进行检测，读取最大长度为CHUNK_SIZE
			int pReadingLength = html.length < CHUNK_SIZE ? html.length : CHUNK_SIZE;
			// 1.模式匹配
			try
			{
				String pReadingText = new String(html, 0, pReadingLength, Charset.forName("ASCII")
						.toString());
				// 寻找网页声明的字符集
				Matcher charsetMatcher = charsetPattern.matcher(pReadingText);
				while (charsetMatcher.find())
				{
					encoding = charsetMatcher.group(3);
					if (null != encoding)
					{
						break;
					}
				}
			} catch (UnsupportedEncodingException e1)
			{
				// log.info(e1.toString(), e1);
			}

			if (null == encoding)
			{
				encoding = headEncoding;
			}
			
			// 如果不一致先以为主 可能会有别的问题
			
			if (null != headEncoding)
				encoding = headEncoding;
			
			// 2. 网页未声明字符集，采用自动探测
			if (null == encoding)
			{
				CharsetDetector charsetDetector = new CharsetDetector();
				encoding = charsetDetector.detectAllCharset(html);

				// 当网页声明为GB2312而包含非GB2312的GBK字符时，自动探测返回结果为windows-1252，强制纠正为GBK
				if (encoding.equalsIgnoreCase("windows-1252"))
				{
					encoding = "gbk";
				}
			}
		} catch (Exception e)
		{
			encoding = null;
		}

		// 没有探测出来，使用默认字符集
		if (null == encoding)
		{
			encoding = defaultCharEncoding;
		}

		return encoding;
	}

}
