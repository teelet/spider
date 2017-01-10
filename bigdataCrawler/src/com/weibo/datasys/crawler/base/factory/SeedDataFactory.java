/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.factory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.weibo.datasys.common.data.InvalidFomatException;
import com.weibo.datasys.common.urlnormallize.HttpURL;
import com.weibo.datasys.common.util.MD5Util;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.strategy.rule.crawl.CrawlRule;

/**
 * 
 * 种子构造工厂类
 * 
 * @author zouyandi
 * 
 */
public class SeedDataFactory {

	private static final Charset UTF_8 = Charset.forName("utf-8");

	private SeedDataFactory() {
	}

	/**
	 * 
	 * 基于url和taskId构建SeedData，其余属性设为默认值
	 * 
	 * @param url
	 * @param taskId
	 * @return
	 */
	public static SeedData buildBaseSeedData(String url, String taskId) {
		String normalizeUrl = url;
		try {
			normalizeUrl = HttpURL.normalizeHttpURL(url, true, UTF_8);
			if (normalizeUrl == null) {
				normalizeUrl = url;
			}
		} catch (UnsupportedEncodingException e) {
		}
		SeedData seedData = new SeedData();
		seedData.setId(MD5Util.MD5(normalizeUrl + "_" + taskId));
		seedData.setUrlId(MD5Util.MD5(normalizeUrl));
		seedData.setLevel(0);
		seedData.setNormalizeUrl(normalizeUrl);
		seedData.setUrl(url);
		seedData.setState(0);
		seedData.setDepth(0);
		seedData.setTaskId(taskId);
		return seedData;
	}

	/**
	 * 
	 * 构建与指定linkdata相应的seeddata
	 * 
	 * @param linkData
	 * @return
	 * @author zouyandi
	 */
	public static SeedData buildFromLinkData(LinkData linkData, Task task) {
		String taskId = task.getTaskId();
		SeedData seedData = new SeedData();
		seedData.setId(MD5Util.MD5(linkData.getNormalizeUrl() + "_" + taskId));
		seedData.setUrlId(linkData.getId());
		seedData.setNormalizeUrl(linkData.getNormalizeUrl());
		seedData.setUrl(linkData.getUrl());
		seedData.setState(linkData.getState());
		seedData.setDepth(linkData.getDepth());
		seedData.setTaskId(taskId);
		recognizeSeedLevel(seedData, task);
		try {
			seedData.setExtendMap(linkData.getExtendString());
		} catch (InvalidFomatException e) {
		}
		return seedData;
	}

	/**
	 * 通过格式化的字符串构造SeedData
	 * 
	 * @param seedString
	 *            url taskId [key=value]...
	 * @return
	 * @author zouyandi
	 */
	public static SeedData buildFromFormatString(String seedString) {
		SeedData seedData = null;
		String[] splits = seedString.split("\\s+");
		if (splits.length >= 2) {
			String url = splits[0];
			String taskId = splits[1];
			Task task = TaskManager.getTask(taskId);
			if (task != null) {
				seedData = SeedDataFactory.buildBaseSeedData(url, taskId);
				if (splits.length >= 3) {
					for (int i = 2; i < splits.length; i++) {
						String[] extSplits = splits[i].split("=", 2);
						if (extSplits.length == 2) {
							seedData.setExtendField(extSplits[0], extSplits[1]);
						}
					}
				}
				recognizeSeedLevel(seedData, task);
			}
		}
		return seedData;
	}

	/**
	 * 识别并设置种子的爬取级别
	 * 
	 * @param seedData
	 * @param task
	 * @return
	 * @author zouyandi
	 */
	public static int recognizeSeedLevel(SeedData seedData, Task task) {
		int seedLv = 0;
		int crawlRuleCount = task.getCrawlStrategy().getCrawlRuleCount();
		for (int lv = 0; lv <= crawlRuleCount - 1; lv++) {
			CrawlRule crawlRule = task.getCrawlStrategy().getCrawlRule(lv);
			if (crawlRule != null) {
				if (crawlRule.getRecognizePattern().matcher(seedData.getUrl())
						.matches()) {
					seedLv = lv;
					seedData.setLevel(lv);
					break;
				}
			}
		}
		return seedLv;
	}

}
