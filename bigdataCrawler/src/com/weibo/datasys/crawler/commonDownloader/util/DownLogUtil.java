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

import java.net.Proxy;

import org.slf4j.Logger;

import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.commonDownloader.worker.Controller;

public class DownLogUtil {

	/**
	 * 
	 * 尝试打印异常，若设置了不忽略下载异常则打印异常信息
	 * 
	 * 
	 * @param controller
	 * @param req
	 * @param msg
	 * @param logger
	 */
	public static void error(Controller controller, InnerRequest req,
			String msg, Logger logger) {
		// 不忽略下载异常才打印异常信息
		if (!controller.getDownloader().isIgnoreDownException()) {
			StringBuilder errorInfo = new StringBuilder();
			if (req.getProxy() != null
					&& !req.getProxy().equals(Proxy.NO_PROXY)) {
				errorInfo.append("proxy=").append(req.getProxy());
			}
			if (!req.getOriginalReq().equals(req)) {
				errorInfo.append(" originalUrl=").append(req.getOriginalUrl());
			}
			Exception e = req.getException();
			logger.error("[{}] - url={} e.msg={} {}", new Object[] { msg,
					req.getUrl(), e.getMessage(), errorInfo });
		}
	}
}
