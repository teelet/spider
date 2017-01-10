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

import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.PageData;
import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.PageDataFactory;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.work.BaseWork;

public class ParseWork extends BaseWork {

	@Override
	protected void doWork() {
		// 获取所有运行中Tasks
		List<Task> tasks = TaskManager.getRunningTasks();
		for (Task task : tasks) {
			// 每个task都尝试获取下载信息进行解析
			if (task.isRunning()) {
				String taskId = task.getTaskId();
				CrawlInfo crawlInfo = (CrawlInfo) QueueManager.poll(
						QueueType.PARSE.name(), taskId);
				if (crawlInfo != null) {
					// 成功获取下载信息，调用task对应Parser进行预解析
					ParseInfo parseInfo = task.getParser().parse(crawlInfo);
					// 根据解析结果，设置种子状态
					SeedData thisSeedData = parseInfo.getThisCrawlInfo()
							.getSeedData();
					int seedState = thisSeedData.getState();
					if (parseInfo.isFetchOK()) {
						// 抓取成功设为2
						thisSeedData.setState(2);
					} else {
						// 抓取失败
						if (crawlInfo.getResp().getRetCode() == 404) {
							// 404网页直接不再爬取
							thisSeedData.setState(-404);
							PageData pageData = PageDataFactory.buildBasePageData(thisSeedData);
							pageData.setHtml("404 default page".getBytes());
							pageData.zipHtml();
							parseInfo.setPageData(pageData);
						} else if (seedState < 0) {
							// 已失败n次，累加失败次数
							seedState--;
							thisSeedData.setState(seedState);
						} else {
							// 第一次失败，状态设为-1
							thisSeedData.setState(-1);
						}
					}
					// 解析后信息放进存储队列
					QueueManager.put(QueueType.SAVE.name(), taskId, parseInfo);
				}
			}
		}
	}

}
