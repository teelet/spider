/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.preparser;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.util.HtmlTools;
import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.appMain.Main;
import com.weibo.datasys.crawler.base.crawlUnit.preparser.AbstractParser;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.PageData;
import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.PageDataFactory;
import com.weibo.datasys.crawler.base.strategy.rule.crawl.CrawlRule;
import com.weibo.datasys.crawler.base.strategy.rule.parse.FieldRule;

public class DefaultParser extends AbstractParser {

	private static Logger logger = LoggerFactory.getLogger(DefaultParser.class);

	private byte[] defaultFileBytes = new byte[0];

	private String acceptContentType = "text";

	@Override
	public void configWithKeyValues(Map<String, String> paraMap) {
		String type = paraMap.get("acceptContentType");
		if (StringUtils.isNotEmpty(type)) {
			acceptContentType = type;
		}
		String defaultFilePath = paraMap.get("defaultFile");
		if (StringUtils.isNotEmpty(defaultFilePath)) {
			try {
				String home = System.getProperty("home.dir");
				FileInputStream in = new FileInputStream(home + "/"
						+ defaultFilePath);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[10240];
				int readCount = 0;
				while (-1 != (readCount = in.read(buffer))) {
					out.write(buffer, 0, readCount);
				}
				in.close();
				defaultFileBytes = out.toByteArray();
			} catch (Exception e) {
				logger.error("[InitDefaultParserErr] - e.msg={}",
						e.getMessage());
			}
		}
	}

	@Override
	public ParseInfo parse(CrawlInfo crawlInfo) {
		ParseInfo parseInfo = new ParseInfo();
		parseInfo.setThisCrawlInfo(crawlInfo);

		Task task = crawlInfo.getValidTask();
		if (task != null) {
			// 判断是否内容页
			boolean isContentPage = !task.getCrawlStrategy()
					.getCrawlRule(crawlInfo.getSeedData().getLevel())
					.hasNextLevelRule();
			// 是否需要存储源页面
			boolean isStorePage = task.getCrawlStrategy()
					.getCrawlRule(crawlInfo.getSeedData().getLevel())
					.isStorePage();
			// 是否需要存储Field
			boolean isStoreField = task.getCrawlStrategy()
					.getCrawlRule(crawlInfo.getSeedData().getLevel())
					.isStoreField();
			// 判断是否下载成功
			boolean isFetchOK = false;
			DownResponse resp = crawlInfo.getResp();
			if (resp.getRetCode() == 200 && resp.getException() == null) {
				isFetchOK = true;
			}
			parseInfo.setFetchOK(isFetchOK);

			// 如果需要存储源页面，则构造PageData
			if (isStorePage) {
				PageData pageData = null;
				String contentType = crawlInfo.getResp().getHeaders()
						.get("Content-Type");
				if (isFetchOK && StringUtils.isNotEmpty(contentType)
						&& contentType.contains(acceptContentType)) {
					pageData = parsePageData(crawlInfo, task);
				} else if (defaultFileBytes.length > 0) {
					// 抓取失败且设置了默认页面，则使用默认页面
					SeedData seedData = crawlInfo.getSeedData();
					pageData = PageDataFactory.buildBasePageData(seedData);
					pageData.setFetchtime(System.currentTimeMillis());
					pageData.setHtml(defaultFileBytes);
					pageData.zipHtml();
					// 种子扩展字段填充到PageData扩展字段
					for (String key : seedData.getExtendFieldNames()) {
						pageData.setExtendField(key,
								seedData.getExtendField(key));
					}
				}
				parseInfo.setPageData(pageData);
			}

			// 如果抓取成功且是内容页，再进行field提取
			if (isFetchOK && isContentPage) {
				// 应用FieldRule，提取Fields
				List<CommonData> fields = parseFields(crawlInfo, task);
				// 需要存储Field则添加Fields到ParseInfo
				if (isStoreField) {
					parseInfo.addFields(fields);
				}
				// PageData不为空则添加Field信息到PageData的扩展字段
				PageData pageData = parseInfo.getPageData();
				if (pageData != null) {
					for (CommonData field : fields) {
						String fieldName = field.getExtendField("fieldname");
						String fieldValue = StringUtils.nullToEmpty(field
								.getBaseField(fieldName));
						pageData.setExtendField(fieldName, fieldValue);
					}
				}
			}

			// 抓取成功，应用CrawlRule，提取outlinks
			if (isFetchOK) {
				// 原cookies为空，且有新的cookies，则设置新cookies
				String cookies = resp.getHeaders().get("cookies");
				if (!StringUtils.isEmptyString(cookies)) {
					if (StringUtils.isEmptyString(crawlInfo.getSeedData()
							.getExtendField("cookies"))) {
						crawlInfo.getSeedData().setExtendField("cookies",
								cookies);
					}
				}
				Set<SeedData> outlinks = parseOutlinks(crawlInfo, task);
				parseInfo.setOutlinks(outlinks);
			}
			parseInfo.setParseOK(true);
		} else {
			parseInfo.setParseOK(false);
		}

		return parseInfo;
	}

	/**
	 * 
	 * 应用FieldRule，提取Fields
	 * 
	 * @param crawlInfo
	 * @param task
	 * @return
	 */
	private List<CommonData> parseFields(CrawlInfo crawlInfo, Task task) {
		List<CommonData> fields = new ArrayList<CommonData>();
		Map<String, FieldRule> fieldRules = task.getParseStrategy()
				.getFieldRules();
		if (fieldRules.size() > 0 && crawlInfo.getHtml() == null) {
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
		for (FieldRule fieldRule : fieldRules.values()) {
			List<? extends CommonData> tmpFields = fieldRule.apply(crawlInfo);
			fields.addAll(tmpFields);
		}
		return fields;
	}

	/**
	 * 
	 * 应用CrawlRule，提取outlinks
	 * 
	 * @param crawlInfo
	 * @param task
	 * @return
	 */
	private Set<SeedData> parseOutlinks(CrawlInfo crawlInfo, Task task) {
		Set<SeedData> outlinks = Collections.emptySet();
		SeedData seedData = crawlInfo.getSeedData();
		// 当前种子depth<=max-1，则提取外链，外链depth<=max
		if (seedData.getDepth() < task.getCrawlStrategy().getMaxCrawlDepth()) {
			// 获取CrawlRule
			CrawlRule crawlRule = task.getCrawlStrategy().getCrawlRule(
					seedData.getLevel());
			// 应用规则
			outlinks = crawlRule.apply(crawlInfo);
			if (Main.isTest()) {
				int i = 1;
				for (SeedData outlink : outlinks) {
					if (outlink.getLevel() == 0) {
						logger.debug("[outlink-{}-{}] - url={}", new Object[] {
								outlink.getLevel(), i++, outlink.getUrl() });
					}
				}
				i = 1;
				for (SeedData outlink : outlinks) {
					if (outlink.getLevel() == 1) {
						logger.debug("[outlink-{}-{}] - url={}", new Object[] {
								outlink.getLevel(), i++, outlink.getUrl() });
					}
				}
				logger.debug("[outlinkNum] - {} - url={}", outlinks.size(),
						seedData.getUrl());
			}
		}
		return outlinks;
	}

	/**
	 * 
	 * 解析原始页面，提取PageData
	 * 
	 * @param crawlInfo
	 * @return
	 */
	private PageData parsePageData(CrawlInfo crawlInfo, Task task) {
		PageData pageData = null;
		SeedData seedData = crawlInfo.getSeedData();
		DownResponse resp = crawlInfo.getResp();
		pageData = PageDataFactory.buildBasePageData(seedData);
		pageData.setFetchtime(System.currentTimeMillis());
		pageData.setHtml(resp.getContentByte());
		pageData.zipHtml();
		// 种子扩展字段填充到PageData扩展字段
		for (String key : seedData.getExtendFieldNames()) {
			pageData.setExtendField(key, seedData.getExtendField(key));
		}
		// http header，填充扩展字段
		for (Entry<String, String> entry : resp.getHeaders().entrySet()) {
			pageData.setExtendField(entry.getKey(), entry.getValue());
		}
		return pageData;
	}

}
