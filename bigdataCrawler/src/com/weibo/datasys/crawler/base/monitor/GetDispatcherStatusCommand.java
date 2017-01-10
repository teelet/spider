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

import com.weibo.datasys.common.monitor.ICommand;
import com.sun.net.httpserver.HttpExchange;
import com.weibo.datasys.crawler.appMain.Main;

public class GetDispatcherStatusCommand implements ICommand {

	private static final long OneDayTime = 24 * 3600 * 1000;
	private static final long OneHourTime = 3600 * 1000;
	private static final long OneMinTime = 60 * 1000;

	public String excute(HttpExchange httpExchange) {
		String retString = "";
		double time = CrawlerMonitorInfo.getRunTime();
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
			builder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head>");
			builder.append("<strong>System Status: </strong>").append(
					"<br><br>");
			builder.append("<strong>System IP: </strong>").append(
					Main.getSystemIP()).append("<br><br>");
			builder.append("<strong>System Name: </strong>").append(
					Main.getSystemName()).append("<br><br>");
			builder.append("<strong>Run time: </strong>").append(runTime)
					.append("<br><br>");

			builder.append(DispatcherMonitorInfo.getReport().replaceAll("\\n",
					"<br>"));
			
			builder.append("<br><a href='/?cmd=showpage'>检视已下载页面</a>");

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
