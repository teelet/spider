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

import java.util.Collection;
import java.util.List;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.crawler.appMain.Main;
import com.weibo.datasys.crawler.base.crawlUnit.seedProvider.AbstractSeedProvider;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.work.BaseWork;
import com.weibo.datasys.crawler.impl.crawlUnit.seedProvider.DispatcherProvider;

/**
 * 
 * 获取种子的工作线程，调用AbstractSeedProvider实现
 * 
 * 
 */
public class SeedProvideWork extends BaseWork {

	private int getSeedBatchCount = 1000;

	private AbstractSeedProvider seedProvider4Crawler;

	private int getNoSeedTimes = 0;

	private final int maxWaitUnit = 1000;

	public SeedProvideWork() {
		getSeedBatchCount = ConfigFactory.getInt(
				"seedProvider.getSeedBatchCount", 1000);
		if (Main.getSystemName().equalsIgnoreCase("crawler")) {
			// crawler专用种子提供器
			seedProvider4Crawler = new DispatcherProvider();
		}
	}

	@Override
	protected void doWork() {
		boolean isGotSeeds = false;
		if (seedProvider4Crawler != null) {
			// crawler 通过 dispatcher 获取种子
			Collection<SeedData> seedDatas = seedProvider4Crawler.getSeeds(
					getSeedBatchCount, null);
			isGotSeeds = seedDatas.size() > 0;
			for (SeedData seedData : seedDatas) {
				QueueManager.put(QueueType.SEED.name(), seedData.getTaskId(),
						seedData);
			}
		} else {
			// dispatcher 通过 task 自定义的 seedProvider 获取种子
			// 获取所有运行中Tasks
			List<Task> tasks = TaskManager.getRunningTasks();
			for (Task task : tasks) {
				// 每个task都尝试获取种子放进相应队列
				if (task.isRunning()) {
					String taskId = task.getTaskId();
					AbstractSeedProvider seedProvider = task.getSeedProvider();
					Collection<SeedData> seedDatas = seedProvider.getSeeds(
							getSeedBatchCount, task);
					isGotSeeds = seedDatas.size() > 0;
					for (SeedData seedData : seedDatas) {
						QueueManager.put(QueueType.SEED.name(), taskId,
								seedData);
					}
				}
			}
		}
		if (isGotSeeds) {
			getNoSeedTimes = 0;
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		} else {
			getNoSeedTimes++;
			int wait = (1 + Math.min(getNoSeedTimes, 10)) * maxWaitUnit;
			try {
				Thread.sleep(wait);
			} catch (Exception e) {
			}
		}
	}
}
