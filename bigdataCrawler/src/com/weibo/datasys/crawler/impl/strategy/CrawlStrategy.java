/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy;

import java.util.HashMap;
import java.util.Map;

import com.weibo.datasys.crawler.base.strategy.rule.seed.AbstractSeedGenerateRule;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.AbstractStrategy;
import com.weibo.datasys.crawler.base.strategy.rule.concurrent.ConcurrentControlRule;
import com.weibo.datasys.crawler.base.strategy.rule.crawl.CrawlRule;

public class CrawlStrategy extends AbstractStrategy {

	private int priority;

	private String siteEncoding;

	private int maxCrawlDepth = Integer.MAX_VALUE;

	private AbstractSeedGenerateRule seedRule;

	private ConcurrentControlRule concurrentRule;

	private Map<Integer, CrawlRule> crawlRules = new HashMap<Integer, CrawlRule>();

	public CrawlStrategy(Task task) {
		super(task);
	}

	/**
	 * 
	 * 添加爬取规则到当前策略中
	 * 
	 * @param crawlRule
	 */
	public void addCrawlRule(CrawlRule crawlRule) {
		int level = crawlRule.getLevel();
		this.crawlRules.put(level, crawlRule);
	}

	/**
	 * 
	 * 获取指定级别的爬取规则
	 * 
	 * @param level
	 * @return
	 */
	public CrawlRule getCrawlRule(int level) {
		CrawlRule crawlRule = this.crawlRules.get(level);
		return crawlRule;
	}

	/**
	 * @return 爬取策略包含的爬取规则个数
	 */
	public int getCrawlRuleCount() {
		return this.crawlRules.size();
	}

	/**
	 * @return the siteEncoding
	 */
	public String getSiteEncoding() {
		return siteEncoding;
	}

	/**
	 * @param siteEncoding
	 *            the siteEncoding to set
	 */
	public void setSiteEncoding(String siteEncoding) {
		this.siteEncoding = siteEncoding;
	}

	/**
	 * @return the seedRule
	 */
	public AbstractSeedGenerateRule getSeedRule() {
		return seedRule;
	}

	/**
	 * @param seedRule
	 *            the seedRule to set
	 */
	public void setSeedRule(AbstractSeedGenerateRule seedRule) {
		this.seedRule = seedRule;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CrawlStrategyInfo - [taskId=").append(
				this.task.getTaskId()).append(" | siteEncoding=").append(
				siteEncoding).append(" | maxCrawlDepth=").append(maxCrawlDepth)
				.append(" | seedRule=").append(seedRule).append(
						" | crawlRules=").append(crawlRules);
		return builder.toString();
	}

	/**
	 * @return the concurrentRule
	 */
	public ConcurrentControlRule getConcurrentRule() {
		return concurrentRule;
	}

	/**
	 * @param concurrentRule
	 *            the concurrentRule to set
	 */
	public void setConcurrentRule(ConcurrentControlRule concurrentRule) {
		this.concurrentRule = concurrentRule;
	}

	/**
	 * @return the maxCrawlDepth
	 */
	public int getMaxCrawlDepth() {
		return maxCrawlDepth;
	}

	/**
	 * @param maxCrawlDepth
	 *            the maxCrawlDepth to set
	 */
	public void setMaxCrawlDepth(int maxCrawlDepth) {
		this.maxCrawlDepth = maxCrawlDepth;
	}

}
