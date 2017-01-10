/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy.rule.parse;

import java.util.List;

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.AbstractRule;

/**
 * 
 * 抽象内容提取规则类<br>
 * 输入为CommonData：src==提取源；extractname==提取内容的名称<br>
 * 输出为List&lt;CommonData&gt;
 * 
 * 
 */
public abstract class AbstractContentExtractRule extends
		AbstractRule<CommonData, List<CommonData>> {

	/**
	 * @param task
	 */
	public AbstractContentExtractRule(Task task) {
		super(task);
	}

}
