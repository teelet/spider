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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.monitor.ICommand;
import com.weibo.datasys.common.urlnormallize.HttpURL;
import com.weibo.datasys.common.util.MD5Util;
import com.sun.net.httpserver.HttpExchange;
import com.weibo.datasys.crawler.base.dao.PageDataDAO;
import com.weibo.datasys.crawler.base.entity.PageData;

public class ShowPageCommand implements ICommand {

	public String excute(HttpExchange httpExchange) {
		String retString = "";
		StringBuilder respBuilder = new StringBuilder();
		try {
			InputStream in = httpExchange.getRequestBody();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String reqString = reader.readLine();
			reader.close();
			if (reqString != null) {
				String pageUrl = reqString.substring(4);
				pageUrl = URLDecoder.decode(pageUrl, "utf-8");
				String norUrl = HttpURL.normalizeHttpURL(pageUrl, true, Charset
						.forName("utf-8"));
				String id = MD5Util.MD5(norUrl);
				PageData pageData = PageDataDAO.getInstance().getById(
						id,
						"crawlDS",
						ConfigFactory.getString("taskManager.taskDB.db",
								"govcn"), "webdb");
				if (pageData != null) {
					respBuilder.append(new String(pageData.getHtml(), "gbk"));
				} else {
					respBuilder.append("<html>");
					respBuilder.append("url not found in webDB<br>");
					respBuilder.append("<a href='").append(pageUrl).append(
							"'>浏览器直接访问url</a>");
					respBuilder.append("</html>");
				}
			} else {
				buildInputResp(respBuilder);
			}

			httpExchange.sendResponseHeaders(200, 0);
			httpExchange.getResponseHeaders().set("Content-Type",
					"text;charset=UTF-8");
			OutputStream os = httpExchange.getResponseBody();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(os,
					"UTF-8"));
			writer.println(respBuilder.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			retString = "ShowPageCommand execute failed: " + e.getMessage();
		}
		return retString;
	}

	/**
	 * @param respBuilder
	 */
	private void buildInputResp(StringBuilder builder) {
		builder.append("<html>");
		builder.append("<form action=\"/?cmd=showpage\" method=\"post\">");
		builder
				.append("<input type=\"text\" name=\"url\"  style=\"width:474px;\" />");
		builder.append("<input type=\"submit\"/>");
		builder.append("</form>");
		builder.append("</html>");
	}

}
