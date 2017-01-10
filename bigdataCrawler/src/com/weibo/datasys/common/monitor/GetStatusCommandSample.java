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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;

import com.sun.net.httpserver.HttpExchange;


/**
*
*简单的显示系统运行状态的http命令实例
*
**/
public class GetStatusCommandSample implements ICommand {

	private static final long startTime = System.currentTimeMillis();
	private static final long OneDayTime = 24 * 3600 * 1000;
	private static final long OneHourTime = 3600 * 1000;
	private static final long OneMinTime = 60 * 1000;

	public String excute(HttpExchange httpExchange) {
		String retString = "";
		double time = System.currentTimeMillis() - startTime;
		String runTime = "";
		if (time < OneHourTime) {
			runTime = time / OneMinTime + " mins";
		} else if (time < OneDayTime) {
			runTime = time / OneHourTime + " hours";
		} else {
			runTime = time / OneDayTime + " days";
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
			builder.append("<strong>System Status: </strong>").append(
					"<br><br>");
			builder.append("<strong>System IP: </strong>").append(
					Inet4Address.getLocalHost().getHostAddress()).append(
					"<br><br>");
			builder.append("<strong>Run time: </strong>").append(runTime)
					.append("<br><br>");

			builder.append("</html>");

			writer.println(builder.toString());
			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
			retString = "GetStatusCommand execute failed: " + e.getMessage();
		}
		return retString;
	}

}
