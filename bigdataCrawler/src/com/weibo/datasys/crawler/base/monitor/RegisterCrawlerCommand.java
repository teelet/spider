/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.monitor;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.datasys.common.monitor.ICommand;
import com.sun.net.httpserver.HttpExchange;
import com.weibo.datasys.crawler.base.dispatcher.Dispatcher;

public class RegisterCrawlerCommand implements ICommand {

	private static final Pattern PATTERN = Pattern.compile("para=(\\S+)");

	public String excute(HttpExchange httpExchange) {
		String retString = "";
		List<String> taskIds = Collections.emptyList();
		String url = httpExchange.getRequestURI().toString();
		Matcher matcher = PATTERN.matcher(url);
		if (matcher.find()) {
			String crawlerId = matcher.group(1);
			taskIds = Dispatcher.registerCrawler(crawlerId);
		} else {
			retString = "[RegisterCrawlerCommand] - FAILED. taskId not defined.";
		}

		try {
			httpExchange.sendResponseHeaders(200, 0);
			OutputStream os = httpExchange.getResponseBody();
			PrintWriter writer;
			writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
			httpExchange.getResponseHeaders().set("Content-Type",
					"text;charset=UTF-8");
			StringBuilder builder = new StringBuilder();
			for (String taskId : taskIds) {
				builder.append(taskId).append(" ");
			}

			writer.println(builder.toString());
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
			retString = "RegisterCrawlerCommand execute failed: " + e.getMessage();
		}
		return retString;
	}
}
