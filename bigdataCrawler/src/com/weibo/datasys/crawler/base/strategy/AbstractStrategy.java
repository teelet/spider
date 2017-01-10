/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy;

import com.weibo.datasys.crawler.base.entity.Task;


public abstract class AbstractStrategy {

	protected Task task;

	protected AbstractStrategy(Task task) {
		this.task = task;
	}

	/**
	 * @return 当前策略所属的Task
	 */
	public Task getTask() {
		return this.task;
	}

}
