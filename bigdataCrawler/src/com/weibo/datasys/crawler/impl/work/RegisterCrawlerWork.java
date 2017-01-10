/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.work;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.crawler.appMain.Main;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.work.BaseWork;

/**
 * 
 * 每分钟向Dispatcher注册一次爬虫的工作线程
 * 
 */
public class RegisterCrawlerWork extends BaseWork {

	private String dispatcherURL;

	public RegisterCrawlerWork() {
		String dispatcherHost = ConfigFactory.getString("dispatcher.host",
				"127.0.0.1");
		String dispatcherPort = ConfigFactory.getString("dispatcher.port",
				"10088");
		String crawlerPort = ConfigFactory.getString("crawler.port", "10087");
		dispatcherURL = "http://" + dispatcherHost + ":" + dispatcherPort
				+ "/?cmd=register&para=" + Main.getSystemIP() + "_"
				+ crawlerPort;
	}

	@Override
	protected void doWork() {
		try {
			// 通过http接口注册Crawler，返回结果为当前running的task列表
			HttpURLConnection conn = (HttpURLConnection) new URL(dispatcherURL)
					.openConnection();
			conn.setReadTimeout(60 * 1000);
			conn.connect();
			InputStream in = conn.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String temp = "";
			String taskIds = "";
			while ((temp = reader.readLine()) != null) {
				taskIds += temp;
			}
			reader.close();
			conn.disconnect();
			if (!"".equals(taskIds)) {
				String[] splits = taskIds.split("\\s");
				for (String taskId : splits) {
					if (!TaskManager.getTask(taskId).isRunning()) {
						TaskManager.runTask(taskId);
					}
				}
			}
		} catch (Exception e) {
		}

		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
		}

	}

}
