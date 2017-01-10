/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy;

import java.util.LinkedHashMap;
import java.util.Map;

import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.AbstractStrategy;
import com.weibo.datasys.crawler.base.strategy.rule.parse.FieldRule;

public class ParseStrategy extends AbstractStrategy {

	private Map<String, FieldRule> fieldRules = new LinkedHashMap<String, FieldRule>();

	public ParseStrategy(Task task) {
		super(task);
	}

	/**
	 * 
	 * 添加一个Field提取规则
	 * 
	 * @param fieldRule
	 */
	public void addFieldRule(FieldRule fieldRule) {
		this.fieldRules.put(fieldRule.getFieldName(), fieldRule);
	}

	/**
	 * @return the fieldRules
	 */
	public Map<String, FieldRule> getFieldRules() {
		return fieldRules;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParseStrategyInfo - [taskId=").append(
				this.task.getTaskId()).append(" | fieldRules=").append(
				fieldRules);
		return builder.toString();
	}

}
