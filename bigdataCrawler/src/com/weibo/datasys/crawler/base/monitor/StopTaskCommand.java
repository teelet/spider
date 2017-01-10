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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.datasys.common.monitor.ICommand;
import com.sun.net.httpserver.HttpExchange;
import com.weibo.datasys.crawler.appMain.Main;
import com.weibo.datasys.crawler.base.dispatcher.Dispatcher;
import com.weibo.datasys.crawler.base.manager.TaskManager;

public class StopTaskCommand implements ICommand {

	private static final Pattern PATTERN = Pattern.compile("para=(\\S+)");

	public String excute(HttpExchange httpExchange) {
		String retString = "";
		String url = httpExchange.getRequestURI().toString();
		Matcher matcher = PATTERN.matcher(url);
		if (matcher.find()) {
			String taskId = matcher.group(1);
			boolean result;
			if (Main.getSystemName().equalsIgnoreCase("crawler")) {
				result = TaskManager.stopTask(taskId);
			} else {
				result = Dispatcher.stopTask(taskId);
			}
			retString = "[StopTaskCommand] - OK=" + result + ". id=" + taskId;
		} else {
			retString = "[StopTaskCommand] - FAILED. taskId not defined.";
		}

		try {
			httpExchange.sendResponseHeaders(200, 0);
			OutputStream os = httpExchange.getResponseBody();
			PrintWriter writer;
			writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
			httpExchange.getResponseHeaders().set("Content-Type",
					"text;charset=UTF-8");
			StringBuilder builder = new StringBuilder();
			builder.append("<html>");
			builder.append(retString).append("\n");
			builder.append("<a href='/'>back to index</a>");
			builder.append("</html>");

			writer.println(builder.toString());
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
			retString = "RunTaskCommand execute failed: " + e.getMessage();
		}
		return retString;
	}
}
