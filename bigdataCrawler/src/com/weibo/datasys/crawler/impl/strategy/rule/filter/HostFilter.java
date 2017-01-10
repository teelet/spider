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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.filter.AbstractFilterRule;

/**
 * 
 * Host过滤器
 * 
 */
public class HostFilter extends AbstractFilterRule {

	private Collection<Pattern> hostPatterns = new ArrayList<Pattern>();

	private Set<String> hostSet = new HashSet<String>();

	/**
	 * @param task
	 */
	public HostFilter(Task task) {
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
				if (StringUtils.isEmptyString(tmp) || tmp.startsWith("#")) {
					continue;
				}
				if (tmp.matches(".*[\\*\\+\\(\\)\\|].*")) {
					hostPatterns.add(Pattern.compile(tmp));
				} else {
					hostSet.add(tmp);
				}
			}
			reader.close();
		} catch (Exception e) {
		}
	}

	@Override
	public Boolean apply(String in) {
		boolean result = false;
		try {
			URL url = new URL(in);
			String host = url.getHost();
			if (hostSet.contains(host)) {
				result = true;
			} else {
				for (Pattern pattern : hostPatterns) {
					Matcher matcher = pattern.matcher(host);
					if (matcher.matches()) {
						result = true;
						break;
					}
				}
			}
		} catch (Exception e) {
		}
		return result;
	}

}
