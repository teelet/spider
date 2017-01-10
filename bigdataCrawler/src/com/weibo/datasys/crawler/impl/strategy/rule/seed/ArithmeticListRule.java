/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.seed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.strategy.rule.seed.AbstractSeedGenerateRule;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.SeedDataFactory;

/**
 * 
 * 等差数列种子列表生成规则
 * 
 */
public class ArithmeticListRule extends AbstractSeedGenerateRule {

	private static Logger logger = LoggerFactory
			.getLogger(ArithmeticListRule.class);

	private List<String> baseURLs = new ArrayList<String>();

	private int startNum;

	private int seedCount;

	private int diff;

	private static final String paraPattern = "(*)";

	/**
	 * @param task
	 */
	public ArithmeticListRule(Task task) {
		super(task);
	}

	@Override
	public List<SeedData> apply(Null in) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		int maxNum = startNum + diff * (seedCount - 1);
		for (int i = startNum; i <= maxNum; i += diff) {
			for (String baseURL : baseURLs) {
				String url = baseURL.replace(paraPattern, "" + i);
				int indexOfSpace = url.indexOf(" ");
				if (indexOfSpace > 0) {
					url = url.replaceFirst(" ", " " + task.getTaskId() + " ");
				} else {
					url = url + " " + task.getTaskId();
				}
				SeedData seedData = SeedDataFactory.buildFromFormatString(url);
				seedDatas.add(seedData);
			}
		}
		return seedDatas;
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		String baseURL = paraMap.get("baseurl");
		if (baseURL != null) {
			baseURLs.add(baseURL);
		} else {
			String home = System.getProperty("home.dir");
			String file = home + "/" + paraMap.get("baseURLList");
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				baseURL = "";
				while (null != (baseURL = reader.readLine())) {
					baseURLs.add(baseURL);
				}
				reader.close();
			} catch (Exception e) {
				logger.error("[loadBaseURLsError] - e.msg={}", e.getMessage());
			}
		}
		startNum = StringUtils.parseInt(paraMap.get("start"), 0);
		seedCount = StringUtils.parseInt(paraMap.get("count"), 0);
		diff = StringUtils.parseInt(paraMap.get("diff"), 0);
	}

}
