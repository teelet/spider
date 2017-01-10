/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy.rule.seed;

import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;

import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.AbstractRule;

public abstract class AbstractSeedGenerateRule extends
		AbstractRule<Null, List<SeedData>> {

	/**
	 * @param task
	 */
	protected AbstractSeedGenerateRule(Task task) {
		super(task);
	}

}
