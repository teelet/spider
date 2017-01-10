/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy.rule.crawl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.util.HtmlTools;
import com.weibo.datasys.common.data.InvalidFomatException;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.dao.LinkDataDAO;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.SeedDataFactory;
import com.weibo.datasys.crawler.base.strategy.rule.AbstractRule;
import com.weibo.datasys.crawler.base.strategy.rule.parse.UrlExtractRule;
import com.weibo.datasys.crawler.base.strategy.rule.parse.UrlExtractRule.UrlExtractType;

/**
 * 
 * 爬取规则，定义了如何下载页面和提取外链
 * 
 * 
 */
public class CrawlRule extends AbstractRule<CrawlInfo, Set<SeedData>> {

	/**
	 * 规则级别，对应N层爬取架构的种子级别
	 */
	private int level;

	/**
	 * 调度因子：决定调度器分发种子时，该级别种子比例。0.0~1.0，多个规则的因子和为1.0
	 */
	private double dispatchFactor = 1.0;

	/**
	 * 是否存储本级别url的源页面
	 */
	private boolean isStorePage = true;

	/**
	 * 是否存储本级别提取出来的Field
	 */
	private boolean isStoreField = false;

	/**
	 * 识别url是否属于此级别的正则
	 */
	private Pattern recognizePattern;

	private List<UrlExtractRule> urlExtractRules = new ArrayList<UrlExtractRule>();

	private Map<String, String> httpReqParameters = new HashMap<String, String>();

	private int maxDepth = 0;

	public CrawlRule(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		setLevel(StringUtils.parseInt(paraMap.get("level"), 0));
		setDispatchFactor(StringUtils.parseDouble(
				paraMap.get("dispatchFactor"), 1.0));
		setStorePage(StringUtils.parseBoolean(paraMap.get("isStorePage"), true));
		setStoreField(StringUtils.parseBoolean(paraMap.get("isStoreField"),
				false));
		String recognizePatternString = paraMap.get("recognizePattern");
		if (recognizePatternString == null
				|| "".equals(recognizePatternString.trim())) {
			recognizePatternString = ".*";
		}
		recognizePattern = Pattern.compile(recognizePatternString);
	}

	@Override
	public Set<SeedData> apply(CrawlInfo crawlInfo) {
		SeedData thisSeedData = crawlInfo.getSeedData();
		Set<SeedData> seedDatas = new HashSet<SeedData>();
		if (urlExtractRules.size() > 0 && crawlInfo.getHtml() == null) {
			DownResponse resp = crawlInfo.getResp();
			// 获取编码设置
			String siteEncoding = task.getCrawlStrategy().getSiteEncoding();
			// 编码html
			String html = "";
			if (siteEncoding.equals("auto")) {
				html = HtmlTools.getStringContentOfHtml(resp.getContentByte(),
						resp.getHeaders());
			} else {
				html = HtmlTools.getStringContentOfHtml(resp.getContentByte(),
						siteEncoding);
			}
			crawlInfo.setHtml(html);
		}
		// 应用本规则下的所有外链提取规则
		for (UrlExtractRule urlExtractRule : urlExtractRules) {
			Set<String> outlinks = urlExtractRule.apply(crawlInfo);
			// 设置种子级别，默认为当前规则级别
			int seedLevel = level;
			if (urlExtractRule.getExtractType().equals(UrlExtractType.NEXT)) {
				// 若外链提取类别为下一级，则种子级别++
				seedLevel++;
			} else if (urlExtractRule.getExtractType().equals(
					UrlExtractType.LAST)) {
				// 若外链提取类别为上一级，则种子级别--
				seedLevel--;
			}
			// 构造外链的SeedData
			for (String outlink : outlinks) {
				SeedData seedData = SeedDataFactory.buildBaseSeedData(outlink,
						task.getTaskId());
				if (seedData.getNormalizeUrl().length() >= 1000) {
					// 归一化后url过长丢弃
					continue;
				}
				// 种子状态保持一致，源种子是更新，则新种子也更新
				if (thisSeedData.getState() == 1) {
					seedData.setState(1);
				}
				// 种子深度++
				seedData.setDepth(thisSeedData.getDepth() + 1);
				// 设置爬取级别
				seedData.setLevel(seedLevel);
				try {
					// 复制当前种子扩展字段到外链种子
					seedData.setExtendMap(thisSeedData.getExtendString());
				} catch (InvalidFomatException e) {
					e.printStackTrace();
				}
				// 设置外链的referer为当前种子
				seedData.setExtendField("referer", thisSeedData.getUrl());
				// 当实时外链种子深度等于当前最深，则转为非实时
				if (seedData.isInstant()) {
					if (seedData.getDepth() > maxDepth) {
						// 外链深度超过原有最大深度，更新当前最大深度，通过linkdb来检测
						maxDepth = LinkDataDAO.getInstance()
								.checkMaxDepth(task);
					}
					if (seedData.getDepth() >= maxDepth) {
						seedData.setInstant(false);
					}
				}
				seedDatas.add(seedData);
			}
		}
		return seedDatas;
	}

	/**
	 * @return 是否存在下一级爬取规则
	 */
	public boolean hasNextLevelRule() {
		return this.task.getCrawlStrategy().getCrawlRule(level + 1) != null;
	}

	/**
	 * 
	 * 添加url抽取规则
	 * 
	 * @param urlExtractRule
	 */
	public void addUrlExtractRule(UrlExtractRule urlExtractRule) {
		this.urlExtractRules.add(urlExtractRule);
	}

	/**
	 * 
	 * 设置http请求参数
	 * 
	 * @param httpReqParameters
	 */
	public void setHttpReqParameters(Map<String, String> httpReqParameters) {
		this.httpReqParameters = httpReqParameters;
	}

	/**
	 * @return http请求参数
	 */
	public Map<String, String> getHttpReqParameters() {
		return this.httpReqParameters;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
		setName(getName() + ".Lv" + level);
	}

	/**
	 * @return the dispatchFactor
	 */
	public double getDispatchFactor() {
		return dispatchFactor;
	}

	/**
	 * @param dispatchFactor
	 *            the dispatchFactor to set
	 */
	public void setDispatchFactor(double dispatchFactor) {
		this.dispatchFactor = dispatchFactor;
	}

	/**
	 * @return the isStorePage
	 */
	public boolean isStorePage() {
		return isStorePage;
	}

	/**
	 * @param isStorePage
	 *            the isStorePage to set
	 */
	public void setStorePage(boolean isStorePage) {
		this.isStorePage = isStorePage;
	}

	/**
	 * @return the recognizePattern
	 */
	public Pattern getRecognizePattern() {
		return recognizePattern;
	}

	/**
	 * @param recognizePattern
	 *            the recognizePattern to set
	 */
	public void setRecognizePattern(Pattern recognizePattern) {
		this.recognizePattern = recognizePattern;
	}

	/**
	 * @return the isStoreField
	 */
	public boolean isStoreField() {
		return isStoreField;
	}

	/**
	 * @param isStoreField
	 *            the isStoreField to set
	 */
	public void setStoreField(boolean isStoreField) {
		this.isStoreField = isStoreField;
	}

}
