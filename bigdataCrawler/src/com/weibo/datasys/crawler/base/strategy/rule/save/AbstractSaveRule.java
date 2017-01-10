/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy.rule.save;

import org.apache.commons.lang.ObjectUtils.Null;

import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.monitor.CrawlerMonitorInfo;
import com.weibo.datasys.crawler.base.strategy.rule.AbstractRule;

public abstract class AbstractSaveRule extends AbstractRule<ParseInfo, Null> {

	protected String type;

	/**
	 * @param task
	 */
	public AbstractSaveRule(Task task) {
		super(task);
	}

	/**
	 * @return 该存储规则对应的存储队列类型
	 */
	public String getQueueType() {
		return type;
	}

	/**
	 * @param counterName
	 * @param result
	 *            1==insert; 2==update; 3==nochange
	 * @author zouyandi
	 */
	protected void countSaveResult(String counterName, int result) {
		CrawlerMonitorInfo.addCounter("save" + counterName, 1);
		switch (result) {
		case 1:
			CrawlerMonitorInfo.addCounter("insert" + counterName, 1);
			break;
		case 2:
			CrawlerMonitorInfo.addCounter("update" + counterName, 1);
			break;
		case 3:
			CrawlerMonitorInfo.addCounter("nochange" + counterName, 1);
			break;
		default:
			break;
		}
	}

}