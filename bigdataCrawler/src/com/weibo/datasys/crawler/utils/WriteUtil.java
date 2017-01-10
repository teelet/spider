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

import java.net.Proxy;
import java.net.URL;
import java.util.Map.Entry;

import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;

public class WriteUtil {

	/**
	 * 
	 * 根据指定req构造http请求的字节数组
	 * 
	 * @param req
	 * @return
	 */
	public static byte[] createRequestBytes(InnerRequest req) {
		URL url = req.getUrl();

		String path = url.toExternalForm();
		if (req.getProxy() == null || Proxy.NO_PROXY.equals(req.getProxy())) {
			path = "".equals(url.getFile()) ? "/" : url.getFile();
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
		}

		String host = req.getHost();
		if (null == host || "".equals(host)) {
			host = url.getHost();
		}

		String portString = (url.getPort() == -1) ? "" : ":" + url.getPort();

		String method = req.getMethod();
		if (method == null || method.equals("")) {
			method = "GET";
		}
		StringBuffer reqStr = new StringBuffer(method).append(" ");
		reqStr.append(path);
		reqStr.append(" HTTP/1.1\r\n");

		reqStr.append("Accept: text/html, application/xhtml+xml, */*\r\n");
		if (req.getReferer() != null) {
			reqStr.append("Referer: ").append(req.getReferer()).append("\r\n");
		}
		reqStr.append("Accept-Encoding: gzip\r\n");
		reqStr.append("User-Agent: ").append(req.getUserAgent()).append("\r\n");
		reqStr.append("Host: ").append(host).append(portString).append("\r\n");
		if (req.getIfModifiedSince() != null) {
			reqStr.append("If-Modified-Since: ").append(
					req.getIfModifiedSince()).append("\r\n");
		}
		if (req.getCookie() != null) {
			reqStr.append("Cookie: ").append(req.getCookie()).append("\r\n");
		}

		// 自定义headers
		for (Entry<String, String> entry : req.getHeaders().entrySet()) {
			reqStr.append(entry.getKey()).append(": ").append(entry.getValue())
					.append("\r\n");
		}

		if (method.equals("POST") && req.getPostString() != null) {
			String contentType = req.getContentType();
			if (contentType == null) {
				contentType = "application/x-www-form-urlencoded";
			}
			reqStr.append("Content-Type: ").append(contentType).append("\r\n");
			reqStr.append("Content-Length: ").append(
					req.getPostString().length()).append("\r\n");
			reqStr.append("\r\n");
			reqStr.append(req.getPostString());
		} else {
			reqStr.append("\r\n");
		}

		byte[] reqBytes = reqStr.toString().getBytes();
		return reqBytes;
	}
}
