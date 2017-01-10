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

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.datasys.common.monitor.ICommand;
import com.weibo.datasys.common.util.StringUtils;
import com.sun.net.httpserver.HttpExchange;
import com.weibo.datasys.crawler.base.dispatcher.Dispatcher;
import com.weibo.datasys.crawler.base.entity.SeedData;

public class GetSeedsCommand implements ICommand {

	private static final Pattern PATTERN = Pattern.compile("para=(\\S+)");

	public String excute(HttpExchange httpExchange) {
		String retString = "";
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		String url = httpExchange.getRequestURI().toString();
		Matcher matcher = PATTERN.matcher(url);
		if (matcher.find()) {
			String[] splits = matcher.group(1).split(",");
			if (splits.length == 2) {
				int count = StringUtils.parseInt(splits[0], 0);
				String crawlerId = splits[1];
				seedDatas = Dispatcher.dispatchSeeds(count, crawlerId);
			} else {
				retString = "wrong num of paras. expected 2. url=" + url;
			}
		}

		try {
			httpExchange.sendResponseHeaders(200, 0);
			httpExchange.getResponseHeaders().set("Content-Type",
					"text;charset=UTF-8");
			OutputStream os = httpExchange.getResponseBody();
			ObjectOutputStream oos = new ObjectOutputStream(os);

			oos.writeObject(seedDatas);

			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
			retString = "GetSeedsCommand execute failed: " + e.getMessage();
		}
		return retString;
	}

}
