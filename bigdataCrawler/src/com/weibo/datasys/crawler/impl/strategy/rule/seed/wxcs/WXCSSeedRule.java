/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.seed.wxcs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.SeedDataFactory;
import com.weibo.datasys.crawler.impl.strategy.rule.seed.FixListRule;

/**
 * 
 * 从数据源生成种子的规则
 * 
 */
public class WXCSSeedRule extends FixListRule {

	private static Logger logger = LoggerFactory.getLogger(WXCSSeedRule.class);

	/**
	 * @param task
	 */
	public WXCSSeedRule(Task task) {
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
					String[] lineSplits = line.split(" : ");
					if (lineSplits.length != 2) {
						logger.info("[invalidLine] - {}", line);
						continue;
					}
					line = lineSplits[1];
					if (StringUtils.isEmptyString(line) || line.startsWith("#")) {
						continue;
					}
					String id = lineSplits[0];
					int indexOfSpace = line.indexOf(" ");
					if (indexOfSpace > 0) {
						line = line.replaceFirst(" ", " " + taskId + " ");
					} else {
						line = line + " " + taskId;
					}
					SeedData seedData = SeedDataFactory
							.buildFromFormatString(line);
					seedData.setLevel(seedLevel);
					seedData.setId(id);
					seedData.setExtendField("appid", id);
					seedDatas.add(seedData);
				}
				reader.close();
			}
		} catch (Exception e) {
			logger.error("[GenerateSeedError] - e.msg={}", e.getMessage());
			logger.error("[GenerateSeedError] - ", e);
		}
		return seedDatas;
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		super.configWithParameters(paraMap);
	}

}
