/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy.rule.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.AbstractRule;
import com.weibo.datasys.crawler.base.strategy.rule.filter.AbstractFilterRule;
import com.weibo.datasys.crawler.base.strategy.rule.process.AbstractProcessRule;
import com.weibo.datasys.crawler.impl.crawlUnit.fetcher.DefaultFetcher;
import com.weibo.datasys.crawler.utils.URLUtil;

/**
 * 
 * 外链提取规则
 * 
 */
public class UrlExtractRule extends AbstractRule<CrawlInfo, Set<String>> {

	private static Logger logger = LoggerFactory
			.getLogger(UrlExtractRule.class);

	/**
	 * url抽取类别
	 **/
	public enum UrlExtractType {
		/**
		 * 上一级url
		 */
		LAST,
		/**
		 * 同级url
		 */
		THIS,
		/**
		 * 下一级url
		 */
		NEXT
	}

	private UrlExtractType extractType = UrlExtractType.NEXT;

	/**
	 * http://news.sina.com.cn/c/(\S+)/(\S+).shtml"
	 */
	private Pattern extractPattern;
	
	/**
	 * http://lol.tuwan.com/345630/
	 */
	private Pattern extractPattern2;

	/**
	 * http://www.sina.com.cn/[group:1]/[group:2]"
	 */
	private String urlPattern;

	private String extractBegin;

	private String extractEnd;

	private boolean isHostLimited = false;

	private List<AbstractProcessRule> processRules = new ArrayList<AbstractProcessRule>();

	private List<AbstractFilterRule> filters = new ArrayList<AbstractFilterRule>();
	
	private List<Pattern> extractPatternList = new ArrayList<Pattern>();

	private int maxExtractNum = 0;

	private List<Integer> extractGroupList;
	
	private Map<String,Set<String>> startMap = new HashMap<String,Set<String>>();
	private Map<String,Set<String>> endMap = new HashMap<String,Set<String>>();

	private static final Pattern GROUP_PATTERN = Pattern
			.compile("\\[group:(\\d+)\\]");

	public UrlExtractRule(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		extractType = UrlExtractType.valueOf(paraMap.get("type").toUpperCase());
		extractPattern = Pattern.compile(paraMap.get("extractPattern"));
		extractPattern2 = Pattern.compile(paraMap.get("extractPattern2"));
		extractPatternList.add(extractPattern);
		extractPatternList.add(extractPattern2);
		setUrlPattern(paraMap.get("urlPattern"));
		extractBegin = paraMap.get("extractBegin");
		extractEnd = paraMap.get("extractEnd");
		maxExtractNum = StringUtils.parseInt(paraMap.get("maxExtractNum"), 0);
		isHostLimited = StringUtils.parseBoolean(paraMap.get("isHostLimited"),
				true);
		readConfFile(extractBegin,startMap);
		readConfFile(extractEnd,endMap);
		
	}
		
	private void readConfFile(String filePath,Map<String,Set<String>> confMap){
		String home = System.getProperty("home.dir");
		String file = home + "/" + filePath;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tmp = "";
			while (null != (tmp = reader.readLine())) {
				tmp = tmp.trim().toLowerCase();
				if (StringUtils.isEmptyString(tmp) || tmp.startsWith("#")) {
					continue;
				}
				String[] host_pair = tmp.split(",");
				if(host_pair.length != 2){
					continue;
				}
				String host = host_pair[0];
				String confValue = host_pair[1];
				Set<String> confSet = confMap.get(host);
				if(null == confSet){
					confSet = new HashSet<String>();
					confMap.put(host, confSet);
				}
				confSet.add(confValue);
			}
		}
		catch(Exception e){
			logger.error(e.toString());
		}
		finally{
			if(null != reader){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}	
	}

	@Override
	public Set<String> apply(CrawlInfo crawlInfo) {
		Set<String> outlinks = new HashSet<String>();
		String html = crawlInfo.getHtml();
		URI seedUri = null;
		try {
			String url = crawlInfo.getSeedData().getUrl();
			if (crawlInfo.getResp().getRedirectTimes() > 0) {
				url = crawlInfo.getResp().getUrl().toString();
			}
			url = url.replaceAll(
					DefaultFetcher.POST_URL_PREFIX_PATTERN.pattern(), "");
			seedUri = new URI(url);
		} catch (URISyntaxException e) {
			logger.warn("[URISyntaxException] - url={} e.msg={}", crawlInfo
					.getSeedData().getUrl(), e.getMessage());
			return outlinks;
		}
		// 输入不为空则提取url
		if (html != null && !html.trim().equals("")) {
			try{
				// 应用截断规则，缩小提取范围
				int startIndex = 0;
				int endIndex = html.length();
				Set<String> startSet = startMap.get(seedUri.getHost());
				if(null == startSet || startSet.size() == 0){
					logger.warn("url extract startMap is null:"+crawlInfo.getSeedData().getTaskId()+":"+crawlInfo.getSeedData().getUrl());
				}
				for(String startContent : startSet){
						int tmpIndex = html.indexOf(startContent);
						if(tmpIndex > 0){
							startIndex = html.indexOf(startContent);
							break;
						}
				}
				Set<String> endSet = endMap.get(seedUri.getHost());
				if(null == endSet || endSet.size() == 0){
					logger.warn("url extract endMap is null:"+crawlInfo.getSeedData().getTaskId()+":"+crawlInfo.getSeedData().getUrl());
				}
				for(String endContent : endSet){
						int tmpIndex = html.indexOf(endContent);
						if(tmpIndex > 0){
							endIndex = html.indexOf(endContent);
							break;
					}
				}
				if (startIndex > -1 && endIndex > -1 && startIndex < endIndex ) {
					html = html.substring(startIndex, endIndex);
				}
			}catch(Exception e){
				logger.warn("[URLExtractException] : ", e.toString());
			}
			
			// 开始提取
			for(Pattern pattern : extractPatternList){
				Matcher matcher = pattern.matcher(html);
				while (matcher.find()) {
					// 提取数量达到上限则停止
					if (maxExtractNum > 0 && outlinks.size() == maxExtractNum) {
						break;
					}
					// 根据extractPattern从html提取内容，然后将urlPattern中对应[group:n]替换为提取出来的内容
					String outlink = urlPattern;
					try {
						if (urlPattern == null || extractGroupList == null
								|| extractGroupList.size() == 0) {
							outlink = matcher.group();
						} else {
							for (int i : extractGroupList) {
								String groupString = "[group:" + i + "]";
								String extractString = matcher.group(i);
								outlink = outlink.replace(groupString,
										extractString);
							}
						}
						// 反斜杠替换为斜杠
						outlink = outlink.replaceAll("\\\\", "/");
						// 处理相对url
						if (!outlink.matches("https?://.+")) {
							try {
								outlink = seedUri.resolve(outlink).toString();
							} catch (Exception e) {
								// url不合法，忽略之
								continue;
							}
						}
						// url中的html实体转换
						outlink = URLUtil.processHtmlEntity(outlink);
						// 删除url中的无用字符
						outlink = URLUtil.processUselessChar(outlink);
						// 补充必要的结尾斜杠
						outlink = URLUtil.fixEndSlash(outlink);
						// url超长，扔掉
						if (outlink.length() >= 1000) {
							continue;
						}
						// 应用特殊处理规则处理url
						for (AbstractProcessRule processRule : processRules) {
							CommonData outlinkData = new CommonData();
							outlinkData.setBaseField("outlink", outlink);
							outlinkData = processRule.apply(outlinkData);
							outlink = outlinkData.getBaseField("outlink");
						}
						// 外链与当前链接相同则忽略
						if (outlink.equals(crawlInfo.getSeedData().getUrl())) {
							continue;
						}
						// 处理站内爬取host限制
						if (isHostLimited) {
							// 如果限定了站内爬取，且外链host与种子host不一致，则跳过
							String outHost = URLUtil.getHost(outlink);
							if (!seedUri.getHost().equals(outHost)) {
								continue;
							}
						}
						// 应用过滤器过滤url
						boolean isAccept = true;
						for (AbstractFilterRule filter : filters) {
							if (!filter.apply(outlink)) {
								isAccept = false;
								break;
							}
						}
						if (isAccept) {
							// 通过过滤则添加outlink到set
							outlinks.add(outlink);
						}
					} catch (Exception e) {
						logger.error(
								"[ExtractURLError] - outlink={}\n | url={}\n | e.msg={}",
								new Object[] { outlink, seedUri, e.getMessage() });
					}
				}
			}
		}
		return outlinks;
	}

	/**
	 * 
	 * 添加过滤器
	 * 
	 * @param filter
	 */
	public void addFilter(AbstractFilterRule filter) {
		this.filters.add(filter);
	}

	/**
	 * @return the extractType
	 */
	public UrlExtractType getExtractType() {
		return extractType;
	}

	/**
	 * @param extractType
	 *            the extractType to set
	 */
	public void setExtractType(UrlExtractType extractType) {
		this.extractType = extractType;
	}

	/**
	 * @param extractPattern
	 *            the extractPattern to set
	 */
	public void setExtractPattern(Pattern extractPattern) {
		this.extractPattern = extractPattern;
	}

	/**
	 * @param urlPattern
	 *            the urlPattern to set
	 */
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
		Matcher groupPatternMatcher = GROUP_PATTERN.matcher(urlPattern);
		extractGroupList = new ArrayList<Integer>();
		while (groupPatternMatcher.find()) {
			extractGroupList.add(StringUtils.parseInt(
					groupPatternMatcher.group(1), -1));
		}
	}

	/**
	 * @param extractBegin
	 *            the extractBegin to set
	 */
	public void setExtractBegin(String extractBegin) {
		this.extractBegin = extractBegin;
	}

	/**
	 * @param extractEnd
	 *            the extractEnd to set
	 */
	public void setExtractEnd(String extractEnd) {
		this.extractEnd = extractEnd;
	}

	/**
	 * @param maxExtractNum
	 *            the maxExtractNum to set
	 */
	public void setMaxExtractNum(int maxExtractNum) {
		this.maxExtractNum = maxExtractNum;
	}

	/**
	 * @param processRule
	 */
	public void addProcessRule(AbstractProcessRule processRule) {
		this.processRules.add(processRule);
	}

}
