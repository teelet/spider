/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlTools {

	private static final Pattern CHARSET_PATTERN = Pattern
			.compile("charset=\"?([\\w|\\-]+)\"?");

	public static String getStringContentOfHtml(byte[] byteContent) {
		return getStringContentOfHtml(byteContent,
				new HashMap<String, String>());
	}

	public static String getStringContentOfHtml(byte[] byteContent,
			String charset) {
		String contentString = "";
		try {
			if (charset == null) {
				charset = "utf-8";
				contentString = new String(byteContent, charset);
				Matcher matcher = CHARSET_PATTERN.matcher(contentString);
				if (matcher.find()) {
					charset = matcher.group(1);
				}
			}
			contentString = new String(byteContent, charset);
		} catch (Exception e) {
		}
		return contentString;
	}

	public static String getStringContentOfHtml(byte[] byteContent,
			Map<String, String> header) {
		String charset = null;
		if (header != null) {
			String contentType = header.get("content-type");
			if (contentType == null) {
				contentType = header.get("Content-Type");
			}
			if (contentType != null) {
				Matcher matcher = CHARSET_PATTERN.matcher(contentType);
				if (matcher.find()) {
					charset = matcher.group(1);
				}
			}
		}
		return getStringContentOfHtml(byteContent, charset);
	}

}
