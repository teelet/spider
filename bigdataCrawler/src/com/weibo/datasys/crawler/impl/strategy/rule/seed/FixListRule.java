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
 * 固定列表种子生成规则，通过读取文件获取种子列表
 * 
 */
public class FixListRule extends AbstractSeedGenerateRule {

	private static Logger logger = LoggerFactory.getLogger(FixListRule.class);

	protected String listPath;

	protected int seedLevel = 0;

	/**
	 * @param task
	 */
	public FixListRule(Task task) {
		super(task);
	}

	@Override
	public List<SeedData> apply(Null in) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		try {
			if (listPath != null) {
				String taskId = this.task.getTaskId();
				BufferedReader reader = new BufferedReader(new FileReader(
						listPath));
				String line = "";
				while (null != (line = reader.readLine())) {
					if (StringUtils.isEmptyString(line) || line.startsWith("#")) {
						continue;
					}
					int indexOfSpace = line.indexOf(" ");
					if (indexOfSpace > 0) {
						line = line.replaceFirst(" ", " " + taskId + " ");
					} else {
						line = line + " " + taskId;
					}
					SeedData seedData = SeedDataFactory
							.buildFromFormatString(line);
					seedData.setLevel(seedLevel);
					seedDatas.add(seedData);
				}
				reader.close();
			}
		} catch (Exception e) {
			logger.error("[GenerateSeedError] - e.msg={}", e.getMessage());
		}
		return seedDatas;
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		String home = System.getProperty("home.dir");
		listPath = home + "/" + paraMap.get("listPath");
		seedLevel = StringUtils.parseInt(paraMap.get("seedLevel"), 0);
	}

}
