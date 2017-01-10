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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.filter.AbstractFilterRule;

/**
 * 
 * 黑名单正则模式过滤器
 * 
 */
public class RegNotMatchFilter extends AbstractFilterRule {

	private Collection<Pattern> blackList = new ArrayList<Pattern>();

	/**
	 * @param task
	 */
	public RegNotMatchFilter(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		String home = System.getProperty("home.dir");
		String file = home + "/" + paraMap.get("file");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String tmp = "";
			while (null != (tmp = reader.readLine())) {
				Pattern pattern = Pattern.compile(tmp);
				blackList.add(pattern);
			}
			reader.close();
		} catch (Exception e) {
		}
	}

	@Override
	public Boolean apply(String in) {
		boolean result = true;
		for (Pattern black : blackList) {
			Matcher matcher = black.matcher(in);
			if (matcher.matches()) {
				result = false;
				break;
			}
		}
		return result;
	}

}
