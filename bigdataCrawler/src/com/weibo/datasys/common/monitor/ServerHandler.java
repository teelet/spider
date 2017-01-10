/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.monitor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


/**
* 
* 通过调用ICommand接口处理http请求
* 
**/
public class ServerHandler implements HttpHandler {

	private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

	private static Pattern cmdPattern = Pattern.compile("cmd=(\\w+)");

	public void handle(HttpExchange httpExchange) {
		Matcher matcher = cmdPattern.matcher(httpExchange.getRequestURI()
				.toString());
		String cmd = "default";
		if (matcher.find()) {
			// 获取命令名称
			cmd = matcher.group(1);
		}
		// 获取命令处理类
		ICommand command = HttpMonitorServer.cmdMap.get(cmd);
		if (command != null) {
			try {
				// 执行命令
				String cmdResult = command.excute(httpExchange);
				if (!"".equals(cmdResult)) {
					logger.info(cmdResult);
				}
				httpExchange.close();
				return;
			} catch (Exception e) {
				logger.error("[ProcessHttpReqError] - ", e);
			}
		}
		// 命令格式错误
		try {
			httpExchange.sendResponseHeaders(404, 0);
			httpExchange.close();
		} catch (IOException e) {
			logger.error("ProcessHttpReqError. {}", e.getMessage());
		}
	}
}
