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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.strategy.rule.seed.AbstractSeedGenerateRule;
import com.weibo.datasys.crawler.base.crawlUnit.deduplicator.AbstractDeduplicator;
import com.weibo.datasys.crawler.base.crawlUnit.fetcher.AbstractFetcher;
import com.weibo.datasys.crawler.base.crawlUnit.preparser.AbstractParser;
import com.weibo.datasys.crawler.base.crawlUnit.saver.AbstractSaver;
import com.weibo.datasys.crawler.base.crawlUnit.seedProvider.AbstractSeedProvider;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.exception.TaskException;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.strategy.rule.concurrent.ConcurrentControlRule;
import com.weibo.datasys.crawler.base.strategy.rule.crawl.CrawlRule;
import com.weibo.datasys.crawler.base.strategy.rule.filter.AbstractFilterRule;
import com.weibo.datasys.crawler.base.strategy.rule.parse.AbstractContentExtractRule;
import com.weibo.datasys.crawler.base.strategy.rule.parse.FieldRule;
import com.weibo.datasys.crawler.base.strategy.rule.parse.UrlExtractRule;
import com.weibo.datasys.crawler.base.strategy.rule.process.AbstractProcessRule;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;
import com.weibo.datasys.crawler.impl.crawlUnit.deduplicator.LinkDBBaseDeduplicator;
import com.weibo.datasys.crawler.impl.strategy.CrawlStrategy;
import com.weibo.datasys.crawler.impl.strategy.ParseStrategy;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

/**
 * @author zouyandi
 * 
 */
public class TaskFactory {
	
	private static Logger logger = LoggerFactory.getLogger(TaskFactory.class);

	/**
	 * 
	 * 根据指定xml内容构建Task实例
	 * 
	 * @param xml
	 * @return
	 * @throws TaskException
	 */
	public static Task buildTask(String xml) throws TaskException {
		Task task = null;
		try {
			// 构建xml dom
			Document doc = DocumentHelper.parseText(xml);
			// 创建task
			task = new Task();
			// 设置task xml
			task.setTaskXML(xml);
			// 设置 task id，name
			String taskId = doc.selectSingleNode("//Config/taskId").getText()
					.trim();
			task.setTaskId(taskId);
			String taskName = doc.selectSingleNode("//Config/taskName")
					.getText().trim();
			task.setTaskName(taskName);
			// task 周期
			String cycleString = doc.selectSingleNode("//Config/taskCycle")
					.getText().trim();
			task.setTaskCycle(StringUtils.parseLong(cycleString, 0));
			// 构建爬取单元各模块实例
			buildCrawlUnit(task, doc);
			// 构建爬取策略实例
			buildCrawlStrategy(task, doc);
			// 构建解析策略实例
			buildParseStrategy(task, doc);
			// 构建存储策略实例
			buildSaveStrategy(task, doc);
		} catch (Exception e) {
			logger.error("[buildTask] - {}", e.toString());
			throw new TaskException("build task error.", e);
		}
		return task;
	}

	/**
	 * 
	 * 构建存储策略实例
	 * 
	 * @param task
	 * @param doc
	 */
	@SuppressWarnings("unchecked")
	private static void buildSaveStrategy(Task task, Document doc)
			throws Exception {
		SaveStrategy saveStrategy = new SaveStrategy(task);
		task.setSaveStrategy(saveStrategy);
		// 种子库配置
		saveStrategy.setSeedDS(doc
				.selectSingleNode("//Config/SaveStrategy/seedDB/dsname")
				.getText().trim());
		saveStrategy.setSeedDB(doc
				.selectSingleNode("//Config/SaveStrategy/seedDB/db").getText()
				.trim());
		saveStrategy.setSeedTable(doc
				.selectSingleNode("//Config/SaveStrategy/seedDB/table")
				.getText().trim());
		// 链接库配置
		saveStrategy.setLinkDS(doc
				.selectSingleNode("//Config/SaveStrategy/linkDB/dsname")
				.getText().trim());
		saveStrategy.setLinkDB(doc
				.selectSingleNode("//Config/SaveStrategy/linkDB/db").getText()
				.trim());
		saveStrategy.setLinkTable(doc
				.selectSingleNode("//Config/SaveStrategy/linkDB/table")
				.getText().trim());
		// 网页库配置
		saveStrategy.setPageDS(doc
				.selectSingleNode("//Config/SaveStrategy/pageDB/dsname")
				.getText().trim());
		saveStrategy.setPageDB(doc
				.selectSingleNode("//Config/SaveStrategy/pageDB/db").getText()
				.trim());
		saveStrategy.setPageTable(doc
				.selectSingleNode("//Config/SaveStrategy/pageDB/table")
				.getText().trim());

		List<Node> saveRuleNodes = doc
				.selectNodes("//Config/SaveStrategy/saveRule");
		for (Node saveRuleNode : saveRuleNodes) {
			String saveRuleName = saveRuleNode.selectSingleNode("@name")
					.getText().trim();
			String saveRuleClass = saveRuleNode.selectSingleNode("@class")
					.getText().trim();
			AbstractSaveRule saveRule = (AbstractSaveRule) Class
					.forName(saveRuleClass).getConstructor(Task.class)
					.newInstance(task);
			saveRule.setName(saveRuleName);
			saveRule.configWithParameters(getParaMap(saveRuleNode));
			saveStrategy.addSaveRule(saveRule);
		}
	}

	/**
	 * 
	 * 构建解析策略实例
	 * 
	 * @param task
	 * @param doc
	 */
	@SuppressWarnings("unchecked")
	private static void buildParseStrategy(Task task, Document doc)
			throws Exception {
		ParseStrategy parseStrategy = new ParseStrategy(task);
		task.setParseStrategy(parseStrategy);
		// Field提取规则
		List<Node> fieldRuleNodes = doc
				.selectNodes("//Config/ParseStrategy/FieldRule");
		for (Node fieldRuleNode : fieldRuleNodes) {
			FieldRule fieldRule = new FieldRule(task);
			fieldRule.configWithParameters(getParaMap(fieldRuleNode));
			parseStrategy.addFieldRule(fieldRule);
			// 内容提取规则
			Node contentRuleNode = fieldRuleNode
					.selectSingleNode("contentExtractRule");
			String contentRuleClass = contentRuleNode
					.selectSingleNode("@class").getText().trim();
			AbstractContentExtractRule contentRule = (AbstractContentExtractRule) Class
					.forName(contentRuleClass).getConstructor(Task.class)
					.newInstance(task);
			contentRule.configWithParameters(getParaMap(contentRuleNode));
			fieldRule.setContentRule(contentRule);
			// 内容处理规则s
			List<Node> processRuleNodes = fieldRuleNode
					.selectNodes("processRule");
			for (Node processRuleNode : processRuleNodes) {
				String processRuleClass = processRuleNode
						.selectSingleNode("@class").getText().trim();
				AbstractProcessRule processRule = (AbstractProcessRule) Class
						.forName(processRuleClass).getConstructor(Task.class)
						.newInstance(task);
				processRule.configWithParameters(getParaMap(processRuleNode));
				fieldRule.addProcessRule(processRule);
			}
			// 过滤器规则s
			List<Node> filterNodes = fieldRuleNode.selectNodes("filterRule");
			for (Node filterNode : filterNodes) {
				// 反射获取规则实例
				String filterClass = filterNode.selectSingleNode("@class")
						.getText().trim();
				AbstractFilterRule filterRule = (AbstractFilterRule) Class
						.forName(filterClass).getConstructor(Task.class)
						.newInstance(task);
				// 获取参数配置规则
				filterRule.configWithParameters(getParaMap(filterNode));
				fieldRule.addFilter(filterRule);
			}
		}
	}

	/**
	 * 
	 * 构建爬取策略实例
	 * 
	 * @param task
	 * @param doc
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static void buildCrawlStrategy(Task task, Document doc)
			throws Exception {
		CrawlStrategy crawlStrategy = new CrawlStrategy(task);
		task.setCrawlStrategy(crawlStrategy);
		// 优先级
		String priorityString = doc
				.selectSingleNode("//Config/CrawlStrategy/priority").getText()
				.trim();
		crawlStrategy.setPriority(StringUtils.parseInt(priorityString, 0));
		// 网站编码
		String siteEncoding = doc
				.selectSingleNode("//Config/CrawlStrategy/siteEncoding")
				.getText().trim();
		crawlStrategy.setSiteEncoding(siteEncoding);
		// 最大爬取深度
		String maxCrawlDepthString = doc
				.selectSingleNode("//Config/CrawlStrategy/maxCrawlDepth")
				.getText().trim();
		int maxCrawlDepth = StringUtils.parseInt(maxCrawlDepthString,
				Integer.MAX_VALUE);
		if (maxCrawlDepth <= 0) {
			maxCrawlDepth = Integer.MAX_VALUE;
		}
		crawlStrategy.setMaxCrawlDepth(maxCrawlDepth);
		// 种子生成规则
		Node seedRuleNode = doc
				.selectSingleNode("//Config/CrawlStrategy/seedGenerateRule");
		String seedRuleClass = seedRuleNode.selectSingleNode("@class")
				.getText().trim();
		// 反射获取规则实例
		AbstractSeedGenerateRule seedRule = (AbstractSeedGenerateRule) Class
				.forName(seedRuleClass).getConstructor(Task.class)
				.newInstance(task);
		seedRule.configWithParameters(getParaMap(seedRuleNode));
		crawlStrategy.setSeedRule(seedRule);
		// 并发控制规则
		ConcurrentControlRule concurrentRule = new ConcurrentControlRule(task);
		crawlStrategy.setConcurrentRule(concurrentRule);
		Node controlRuleNode = doc
				.selectSingleNode("//Config/CrawlStrategy/concurrentControlRule");
		concurrentRule.configWithParameters(getParaMap(controlRuleNode));
		// 爬取规则
		List<Node> crawlRuleNodes = doc
				.selectNodes("//Config/CrawlStrategy/crawlRule");
		for (Node crawlRuleNode : crawlRuleNodes) {
			CrawlRule crawlRule = new CrawlRule(task);
			// 获取参数配置规则
			crawlRule.configWithParameters(getParaMap(crawlRuleNode));
			// 添加规则到策略中
			crawlStrategy.addCrawlRule(crawlRule);
			// url提取规则
			List<Node> urlExtractNodes = crawlRuleNode
					.selectNodes("urlExtractRule");
			for (Node urlExtractNode : urlExtractNodes) {
				UrlExtractRule urlExtractRule = new UrlExtractRule(task);
				// 获取参数配置规则
				urlExtractRule.configWithParameters(getParaMap(urlExtractNode));
				crawlRule.addUrlExtractRule(urlExtractRule);
				// 特殊处理规则s
				List<Node> processNodes = urlExtractNode
						.selectNodes("processRule");
				for (Node processNode : processNodes) {
					// 反射获取规则实例
					String processClass = processNode
							.selectSingleNode("@class").getText().trim();
					AbstractProcessRule processRule = (AbstractProcessRule) Class
							.forName(processClass).getConstructor(Task.class)
							.newInstance(task);
					// 获取参数配置规则
					processRule.configWithParameters(getParaMap(processNode));
					urlExtractRule.addProcessRule(processRule);
				}
				// 过滤规则s
				List<Node> filterNodes = urlExtractNode
						.selectNodes("filterRule");
				for (Node filterNode : filterNodes) {
					// 反射获取规则实例
					String filterClass = filterNode.selectSingleNode("@class")
							.getText().trim();
					AbstractFilterRule filterRule = (AbstractFilterRule) Class
							.forName(filterClass).getConstructor(Task.class)
							.newInstance(task);
					// 获取参数配置规则
					filterRule.configWithParameters(getParaMap(filterNode));
					urlExtractRule.addFilter(filterRule);
				}
			}
			// http请求参数
			Node httpParaNode = crawlRuleNode.selectSingleNode("httpRequest");
			Map<String, String> paraMap = getParaMap(httpParaNode);
			crawlRule.setHttpReqParameters(paraMap);
		}
	}

	/**
	 * 
	 * 构建爬取单元各模块实例
	 * 
	 * @param task
	 * @param doc
	 * @throws Exception
	 */
	private static void buildCrawlUnit(Task task, Document doc)
			throws Exception {
		String fetcherName = doc.selectSingleNode("//Config/Fetcher").getText()
				.trim();
		AbstractFetcher fetcher = (AbstractFetcher) Class.forName(fetcherName)
				.newInstance();
		fetcher.configWithKeyValues(getParaMap(doc
				.selectSingleNode("//Config/Fetcher")));
		String parserName = doc.selectSingleNode("//Config/Parser").getText()
				.trim();
		AbstractParser parser = (AbstractParser) Class.forName(parserName)
				.newInstance();
		parser.configWithKeyValues(getParaMap(doc
				.selectSingleNode("//Config/Parser")));
		String saverName = doc.selectSingleNode("//Config/Saver").getText()
				.trim();
		AbstractSaver saver = (AbstractSaver) Class.forName(saverName)
				.newInstance();
		saver.configWithKeyValues(getParaMap(doc
				.selectSingleNode("//Config/Saver")));
		String seedProviderName = doc.selectSingleNode("//Config/SeedProvider")
				.getText().trim();
		AbstractSeedProvider seedProvider = (AbstractSeedProvider) Class
				.forName(seedProviderName).newInstance();
		seedProvider.configWithKeyValues(getParaMap(doc
				.selectSingleNode("//Config/SeedProvider")));
		Node deduplicatorNode = doc.selectSingleNode("//Config/Deduplicator");
		AbstractDeduplicator deduplicator = new LinkDBBaseDeduplicator();
		if (deduplicatorNode != null) {
			String deduplicatorName = deduplicatorNode.getText().trim();
			if (StringUtils.isNotEmpty(deduplicatorName)) {
				deduplicator = (AbstractDeduplicator) Class.forName(
						deduplicatorName).newInstance();
			}
			deduplicator.configWithKeyValues(getParaMap(deduplicatorNode));
		} else {
			deduplicator
					.configWithKeyValues(new LinkedHashMap<String, String>());
		}

		task.setFetcher(fetcher);
		task.setParser(parser);
		task.setSaver(saver);
		task.setSeedProvider(seedProvider);
		task.setDeduplicator(deduplicator);
	}

	/**
	 * 
	 * 提取指定node下的参数节点，包装成Map
	 * 
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> getParaMap(Node node) {
		Map<String, String> paraMap = new LinkedHashMap<String, String>();
		if (node != null) {
			List<Node> paraNodes = node.selectNodes("parameter");
			for (Node paraNode : paraNodes) {
				String key = paraNode.selectSingleNode("@key").getText().trim();
				String value = paraNode.getText().trim();
				Node valueNode = paraNode.selectSingleNode("@value");
				if (valueNode != null) {
					value = paraNode.selectSingleNode("@value").getText()
							.trim();
				}
				paraMap.put(key, value);
			}
		}
		return paraMap;
	}

}
