/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.filter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.filter.AbstractFilterRule;

/**
 * 
 * 必须符合指定正则的过滤器
 * 
 */
public class RegMatchFilter extends AbstractFilterRule {

	private Pattern pattern;

	private Matcher matcher;

	/**
	 * @param task
	 */
	public RegMatchFilter(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		pattern = Pattern.compile(paraMap.get("matchReg"));
	}

	@Override
	public Boolean apply(String in) {
		if (matcher == null) {
			matcher = pattern.matcher(in);
		} else {
			matcher.reset(in);
		}
		return matcher.matches();
	}

}
