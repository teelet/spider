/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.seedProvider;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.crawler.appMain.Main;
import com.weibo.datasys.crawler.base.crawlUnit.seedProvider.AbstractSeedProvider;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;

public class DispatcherProvider extends AbstractSeedProvider {

	private static Logger logger = LoggerFactory
			.getLogger(DispatcherProvider.class);

	private String dispatcherURL;

	private String crawlerPort = ConfigFactory.getString("crawler.port",
			"10087");

	public DispatcherProvider() {
		String dispatcherHost = ConfigFactory.getString("dispatcher.host",
				"127.0.0.1");
		String dispatcherPort = ConfigFactory.getString("dispatcher.port",
				"10087");
		dispatcherURL = "http://" + dispatcherHost + ":" + dispatcherPort
				+ "/?cmd=getseed&para=";
	}

	@Override
	@SuppressWarnings("unchecked")
	protected List<SeedData> getCustomSeeds(int count, Task task) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(dispatcherURL
					+ count + "," + Main.getSystemIP() + "_" + crawlerPort)
					.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(0);
			conn.connect();
			InputStream in = conn.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(in);
			seedDatas = (List<SeedData>) ois.readObject();
			ois.close();
			conn.disconnect();
		} catch (Exception e) {
			logger.error("[getSeedsFromDispatcherError] - e.msg={}", e
					.getMessage());
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e1) {
			}
		}
		return seedDatas;
	}
}
