/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy.rule.filter;

import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.AbstractRule;

/**
 * 
 * 抽象过滤规则，过滤输入字符串，返回：true：字符串符合过滤规则；false：不符合过滤规则
 * 
 */
public abstract class AbstractFilterRule extends AbstractRule<String, Boolean> {

	/**
	 * @param task
	 */
	public AbstractFilterRule(Task task) {
		super(task);
	}

}
