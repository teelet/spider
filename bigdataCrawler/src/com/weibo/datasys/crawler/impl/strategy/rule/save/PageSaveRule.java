/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.save;

import org.apache.commons.lang.ObjectUtils.Null;

import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.PageData;
import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;

/**
 * 
 * webdb网页信息存储规则实现类
 * 
 */
public class PageSaveRule extends AbstractSaveRule {

	/**
	 * @param task
	 */
	public PageSaveRule(Task task) {
		super(task);
		this.type = QueueType.SAVE_PAGE.name();
	}

	@Override
	public Null apply(ParseInfo parseInfo) {
		CrawlInfo crawlInfo = parseInfo.getThisCrawlInfo();
		Task task = crawlInfo.getValidTask();
		if (task == null) {
			return null;
		}
		PageData pageData = parseInfo.getPageData();
		if (pageData != null) {
			QueueManager.put(QueueType.SAVE_PAGE_BATCH.name(), task.getTaskId(),
					pageData);
			countSaveResult("Page", 1);
		}
		return null;
	}

}
