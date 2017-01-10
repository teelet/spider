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

import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.base.crawlUnit.fetcher.AbstractFetcher;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.strategy.rule.concurrent.ConcurrentControlRule;
import com.weibo.datasys.crawler.base.work.BaseWork;

/**
 * 
 * 异步下载结果收集的工作线程
 * 
 * 
 */
public class AsyncRespCollectWork extends BaseWork {

	@Override
	protected void doWork() {
		// 获取一个下载结果
		DownResponse resp = AbstractFetcher.getResponse(1000);
		if (resp != null) {
			// 获取下载结果对应的CrawlInfo
			CrawlInfo crawlInfo = (CrawlInfo) resp.getAttach();
			crawlInfo.setResp(resp);
			if (crawlInfo.isComplete()) {
				SeedData seedData = crawlInfo.getSeedData();
				String taskId = seedData.getTaskId();
				Task task = TaskManager.getTask(taskId);
				if (task.isRunning()) {
					// 已完成且task有效放进解析队列
					QueueManager.put(QueueType.PARSE.name(), crawlInfo.getSeedData()
							.getTaskId(), crawlInfo);
					// 移除并发信息
					ConcurrentControlRule concurrentRule = task.getCrawlStrategy()
							.getConcurrentRule();
					concurrentRule.releaseConcurrent(seedData);
				}
			}
		}
	}
}
