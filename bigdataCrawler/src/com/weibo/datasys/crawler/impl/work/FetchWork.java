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

import java.util.List;

import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.strategy.rule.concurrent.ConcurrentControlRule;
import com.weibo.datasys.crawler.base.work.BaseWork;

public class FetchWork extends BaseWork {

	@Override
	protected void doWork() {
		// 获取所有运行中Tasks
		List<Task> tasks = TaskManager.getRunningTasks();
		for (Task task : tasks) {
			// 每个task都尝试获取种子进行抓取
			if (task.isRunning()) {
				String taskId = task.getTaskId();
				SeedData seedData = (SeedData) QueueManager.poll(
						QueueType.SEED.name(), taskId);
				if (seedData != null) {
					// 应用并发控制规则
					ConcurrentControlRule concurrentRule = task
							.getCrawlStrategy().getConcurrentRule();
					if (!concurrentRule.apply(seedData)) {
						// 不满足并发条件的种子放入队列尾部
						QueueManager.put(QueueType.SEED.name(), taskId,
								seedData);
						continue;
					}
					CrawlInfo unCrawlInfo = new CrawlInfo(seedData);
					CrawlInfo crawlInfo = null;
					boolean needFetch = true;
					// 如果是更新爬取的种子，且不是内容页，needFetch置为false
					if ("true".equals(seedData.getExtendField("isupdate"))) {
						boolean isContentPage = !task.getCrawlStrategy()
								.getCrawlRule(seedData.getLevel())
								.hasNextLevelRule();
						if (!isContentPage) {
							needFetch = false;
						}
					}
					if (needFetch) {
						crawlInfo = task.getFetcher().fetch(unCrawlInfo);
					} else {
						// 不需爬取，直接构造空下载结果，以便后续流程走完
						unCrawlInfo.setResp(new DownResponse(null));
						crawlInfo = unCrawlInfo;
					}
					if (crawlInfo.getValidTask() == null) {
						continue;
					}
					if (crawlInfo.isComplete()) {
						// 抓取完成放进解析队列移除并发计数
						concurrentRule.releaseConcurrent(seedData);
						QueueManager.put(QueueType.PARSE.name(), taskId,
								crawlInfo);
					}
				}
			}// end of if task is running
		}// end of foreach taskId
	}
}
