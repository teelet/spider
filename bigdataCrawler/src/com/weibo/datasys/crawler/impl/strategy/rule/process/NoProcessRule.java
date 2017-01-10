/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.process;

import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.process.AbstractProcessRule;

public class NoProcessRule extends AbstractProcessRule {

	/**
	 * @param task
	 */
	public NoProcessRule(Task task) {
		super(task);
	}

	@Override
	protected String applyCustomProcess(String processValue) {
		return processValue;
	}

}
