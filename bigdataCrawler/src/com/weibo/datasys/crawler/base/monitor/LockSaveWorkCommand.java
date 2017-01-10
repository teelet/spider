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
import com.weibo.datasys.crawler.base.manager.SaveLockManager;

public class LockSaveWorkCommand implements ICommand {

	public String excute(HttpExchange httpExchange) {
		String retString = "";

		SaveLockManager.lockSave();

		try {
			httpExchange.sendResponseHeaders(200, 0);
			OutputStream os = httpExchange.getResponseBody();
			PrintWriter writer;
			writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
			httpExchange.getResponseHeaders().set("Content-Type",
					"text;charset=UTF-8");
			writer.close();
			retString = "LockSaveOK";
		} catch (Exception e) {
			e.printStackTrace();
			retString = "LockSaveWorkCommand execute failed: "
					+ e.getMessage();
		}
		return retString;
	}
}
