/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy.rule.concurrent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.ConcurrentManager;
import com.weibo.datasys.crawler.base.strategy.rule.AbstractRule;
import com.weibo.datasys.crawler.impl.crawlUnit.fetcher.DefaultFetcher;
import com.weibo.datasys.crawler.utils.URLUtil;

/**
 * 
 * 控制单个task中，所爬取的各个host的并发连接数
 * 
 */
public class ConcurrentControlRule extends AbstractRule<SeedData, Boolean> {

	private Map<String, Integer> maxConcurMap = new ConcurrentHashMap<String, Integer>();

	private int defaultMax = 5;

	private int globalMaxConcurrent = ConfigFactory.getInt(
			"fetcher.globalMaxConcurrent", 50);

	/**
	 * @param task
	 */
	public ConcurrentControlRule(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		defaultMax = StringUtils.parseInt(paraMap.get("defaultMax"), 5);
		String home = System.getProperty("home.dir");
		String file = home + "/" + paraMap.get("file");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String tmp = "";
			while (null != (tmp = reader.readLine())) {
				String[] splits = tmp.split("\\s");
				if (splits.length == 2) {
					maxConcurMap.put(splits[0],
							StringUtils.parseInt(splits[1], defaultMax));
				}
			}
			reader.close();
		} catch (Exception e) {
		}
	}

	@Override
	public Boolean apply(SeedData seedData) {
		boolean isInControl = false;
		try {
			// 首先判断是否满足全局并发数限制
			if (ConcurrentManager.getTotalConcurrent() < globalMaxConcurrent) {
				// 满足全局限制，判断domain和host限制
				String host = StringUtils.nullToEmpty(URLUtil.getHost(seedData
						.getUrl().replaceAll(
								DefaultFetcher.POST_URL_PREFIX_PATTERN
										.pattern(), "")));
				String domain = StringUtils.nullToEmpty(URLUtil
						.getFirstDomain(host));
				// 当前并发数
				AtomicInteger concurrent = null;
				// 最大并发数，初始为默认值
				Integer maxConcurrent = defaultMax;
				// 获取domain最大值
				Integer domainMaxConcurrent = maxConcurMap.get(domain);
				if (domainMaxConcurrent == null) {
					// 没有设置domain最大值则获取host最大值
					Integer hostMaxConcurrent = maxConcurMap.get(host);
					if (hostMaxConcurrent != null) {
						maxConcurrent = hostMaxConcurrent;
					}
					// 获取当前host并发数
					concurrent = ConcurrentManager.getConcurrent(
							seedData.getTaskId(), host);
				} else {
					// 设置了domain最大值则使用domain最大值
					maxConcurrent = domainMaxConcurrent;
					// 获取当前domain并发数
					concurrent = ConcurrentManager.getConcurrent(
							seedData.getTaskId(), domain);
				}
				// 当前并发数小于最大并发则返回true
				if (concurrent.get() < maxConcurrent) {
					concurrent.incrementAndGet();
					isInControl = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isInControl;
	}

	/**
	 * 
	 * 释放种子对应的host并发计数
	 * 
	 * @param seedData
	 */
	public void releaseConcurrent(SeedData seedData) {
		try {
			String host = StringUtils.nullToEmpty(URLUtil.getHost(seedData
					.getUrl().replaceAll(
							DefaultFetcher.POST_URL_PREFIX_PATTERN.pattern(),
							"")));
			String domain = StringUtils.nullToEmpty(URLUtil
					.getFirstDomain(host));
			AtomicInteger concurrent;
			// 如果设置了domain限制，则获取当前domain并发数，否则获取当前host并发数
			Integer domainMaxConcurrent = maxConcurMap.get(domain);
			if (domainMaxConcurrent == null) {
				// 获取当前host并发数
				concurrent = ConcurrentManager.getConcurrent(
						seedData.getTaskId(), host);
			} else {
				// 获取当前domain并发数
				concurrent = ConcurrentManager.getConcurrent(
						seedData.getTaskId(), domain);
			}
			if (concurrent.get() > 0) {
				concurrent.decrementAndGet();
			}
		} catch (Exception e) {
		}
	}

}
