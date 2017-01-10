/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.manager;

import java.util.HashMap;
import java.util.Map;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.db.DBManager;
import com.weibo.datasys.common.manager.BaseEnvManager;
import com.weibo.datasys.common.monitor.HttpMonitorServer;
import com.weibo.datasys.common.monitor.ICommand;
import com.weibo.datasys.crawler.appMain.Main;
import com.weibo.datasys.crawler.base.monitor.CrawlerMonitorInfo;
import com.weibo.datasys.crawler.base.monitor.DispatcherMonitorInfo;
import com.weibo.datasys.crawler.base.monitor.GetCrawlerStatusCommand;
import com.weibo.datasys.crawler.base.monitor.GetDispatcherStatusCommand;
import com.weibo.datasys.crawler.base.monitor.GetSeedsCommand;
import com.weibo.datasys.crawler.base.monitor.LockSaveWorkCommand;
import com.weibo.datasys.crawler.base.monitor.RegisterCrawlerCommand;
import com.weibo.datasys.crawler.base.monitor.RunTaskCommand;
import com.weibo.datasys.crawler.base.monitor.ShowPageCommand;
import com.weibo.datasys.crawler.base.monitor.StopTaskCommand;
import com.weibo.datasys.crawler.base.monitor.UnLockSaveWorkCommand;

public class EnvironmentManager extends BaseEnvManager {

	@Override
	protected void customInit() throws Exception {
		// 初始化数据库连接池
		DBManager.init();

		// 初始化队列管理器
		QueueManager.init();

		// 初始化任务管理器
		TaskManager.init();

		// 初始化监控信息类
		CrawlerMonitorInfo.init();
		DispatcherMonitorInfo.init();

		// 初始化工作线程管理器
		WorkManager.init();

		// 启动http监控服务器
		if (Main.getSystemName().equalsIgnoreCase("crawler")) {
			if (!HttpMonitorServer.isStarted()) {
				Map<String, ICommand> cmdMap = new HashMap<String, ICommand>();
				cmdMap.put("default", new GetCrawlerStatusCommand());
				cmdMap.put("runtask", new RunTaskCommand());
				cmdMap.put("stoptask", new StopTaskCommand());
				cmdMap.put("lock", new LockSaveWorkCommand());
				cmdMap.put("unlock", new UnLockSaveWorkCommand());
				HttpMonitorServer.startServer(ConfigFactory.getInt(
						"crawler.port", 10087), cmdMap);
			}
		} else if (Main.getSystemName().equalsIgnoreCase("dispatcher")) {
			Map<String, ICommand> cmdMap = new HashMap<String, ICommand>();
			cmdMap.put("default", new GetDispatcherStatusCommand());
			cmdMap.put("runtask", new RunTaskCommand());
			cmdMap.put("stoptask", new StopTaskCommand());
			cmdMap.put("getseed", new GetSeedsCommand());
			cmdMap.put("register", new RegisterCrawlerCommand());
			cmdMap.put("showpage", new ShowPageCommand());
			HttpMonitorServer.startServer(ConfigFactory.getInt(
					"dispatcher.port", 10088), cmdMap);
		}
	}

	@Override
	public void clear() {
		WorkManager.stopAllWorks();
		TaskManager.stopAllTask();
		QueueManager.clear();
		ConcurrentManager.clearAllConcurrent();
		DBManager.closeAllDataSource();
		CrawlerMonitorInfo.clear();
	}

}
