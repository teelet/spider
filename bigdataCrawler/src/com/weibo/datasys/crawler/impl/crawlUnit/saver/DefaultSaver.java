/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.saver;

import java.util.List;

import com.weibo.datasys.crawler.base.crawlUnit.saver.AbstractSaver;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;

public class DefaultSaver extends AbstractSaver {

	@Override
	public void save(ParseInfo parseInfo) {
		CrawlInfo crawlInfo = parseInfo.getThisCrawlInfo();
		Task task = crawlInfo.getValidTask();
		if (task != null) {
			List<AbstractSaveRule> saveRules = task.getSaveStrategy()
					.getSaveRules();
			// 将任务中每个存储规则放进相应队列中分别处理
			for (AbstractSaveRule saveRule : saveRules) {
				Object[] objects = new Object[] { saveRule, parseInfo };
				QueueManager.put(saveRule.getQueueType(), task.getTaskId(),
						objects);
			}
		}
	}
}
