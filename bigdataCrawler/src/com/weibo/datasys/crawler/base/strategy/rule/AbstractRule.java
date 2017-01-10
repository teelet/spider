/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy.rule;

import java.util.Map;

import com.weibo.datasys.crawler.base.entity.Task;

public abstract class AbstractRule<Input, Output> {

	protected String name = this.getClass().getSimpleName();

	protected Task task;

	public AbstractRule(Task task) {
		this.task = task;
	}

	/**
	 * 
	 * 使用指定参数配置当前规则，默认实现不作任何操作，子类根据需要实现
	 * 
	 * @param paraMap
	 */
	public void configWithParameters(Map<String, String> paraMap) {

	}

	/**
	 * 
	 * 将本规则应用到输入对象上，并返回输出对象
	 * 
	 * @param in
	 *            规则应用的对象
	 * @return 规则应用结果对象
	 */
	public abstract Output apply(Input in);

	/**
	 * @return 当前规则所属的Task
	 */
	public Task getTask() {
		return this.task;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
