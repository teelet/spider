/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.parse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.parse.AbstractContentExtractRule;

/**
 * 
 * 通过正则匹配实现的内容提取规则
 * 
 * 
 */
public class RegExMatchRule extends AbstractContentExtractRule {

	/**
	 * 定义如何提取Field的一个属性
	 * 
	 * @author zouyandi
	 * 
	 */
	private class ExtractInfo {

		/**
		 * http://news.sina.com.cn/c/(\S+)/(\S+).shtml"
		 */
		Pattern extractPattern;

		/**
		 * [group:1],[group:2]"
		 */
		String contentPattern;

		List<Integer> extractGroupList;

		void setExtractPattern(Pattern extractPattern) {
			this.extractPattern = extractPattern;
		}

		void setContentPattern(String contentPattern) {
			this.contentPattern = contentPattern;
			Matcher groupPatternMatcher = GROUP_PATTERN.matcher(contentPattern);
			extractGroupList = new ArrayList<Integer>();
			while (groupPatternMatcher.find()) {
				extractGroupList.add(StringUtils.parseInt(
						groupPatternMatcher.group(1), -1));
			}
		}

	}

	private boolean isMultiMatch;

	private Map<String, ExtractInfo> extractInfoMap = new LinkedHashMap<String, ExtractInfo>();

	private static final Pattern GROUP_PATTERN = Pattern
			.compile("\\[group:(\\d+)\\]");

	private static final String EXTRACT_PATTERN_PREFIX = "extractPattern";

	private static final String CONTENT_PATTERN_PREFIX = "contentPattern";

	private static final String FIELD_NAME_ATTR = "[FIELDNAME]";

	/**
	 * @param task
	 */
	public RegExMatchRule(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		setMultiMatch(Boolean.parseBoolean(paraMap.get("isMultiMatch")));
		for (Entry<String, String> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(EXTRACT_PATTERN_PREFIX)
					|| key.startsWith(CONTENT_PATTERN_PREFIX)) {
				// 默认属性名称为FieldName
				String attr = FIELD_NAME_ATTR;
				int index = key.lastIndexOf(":");
				if (index != -1) {
					// 有指定属性名称则提取之
					attr = key.substring(index + 1);
				}
				// 提取的相关模式
				String pattern = entry.getValue();
				ExtractInfo extractInfo = extractInfoMap.get(attr);
				if (extractInfo == null) {
					extractInfo = new ExtractInfo();
					extractInfoMap.put(attr, extractInfo);
				}
				if (key.contains(EXTRACT_PATTERN_PREFIX)) {
					Pattern extractPattern = Pattern.compile(pattern);
					extractInfo.setExtractPattern(extractPattern);
				} else if (key.contains(CONTENT_PATTERN_PREFIX)) {
					extractInfo.setContentPattern(pattern);
				}
			}
		}
	}

	@Override
	public List<CommonData> apply(CommonData in) {
		List<CommonData> fields = new ArrayList<CommonData>();
		String src = in.getBaseField("src");
		String fieldName = in.getBaseField("extractname");

		// 生成所有attr对应的Matcher
		Map<String, Matcher> matcherMap = new LinkedHashMap<String, Matcher>();
		for (Entry<String, ExtractInfo> entry : extractInfoMap.entrySet()) {
			ExtractInfo extractInfo = entry.getValue();
			Matcher matcher = extractInfo.extractPattern.matcher(src);
			matcherMap.put(entry.getKey(), matcher);
		}

		// match一次生成一个Field
		while (checkMatchersFind(matcherMap)) {
			CommonData field = new CommonData();
			for (Entry<String, Matcher> entry : matcherMap.entrySet()) {
				String attr = entry.getKey();
				ExtractInfo extractInfo = extractInfoMap.get(attr);
				if (attr.equals(FIELD_NAME_ATTR)) {
					// 没有指定属性名，使用FieldName
					attr = fieldName;
				}
				Matcher matcher = entry.getValue();
				String content = extractInfo.contentPattern;
				if (extractInfo.contentPattern == null
						|| extractInfo.extractGroupList == null
						|| extractInfo.extractGroupList.size() == 0) {
					content = matcher.group();
				} else {
					for (int i : extractInfo.extractGroupList) {
						String groupString = "[group:" + i + "]";
						String extractString = matcher.group(i);
						content = content.replace(groupString, extractString);
					}
				}
				if (attr.equalsIgnoreCase("id")) {
					// 属性为id则设置成id
					field.setId(content);
				} else {
					field.setBaseField(attr, content);
				}
			}// end of foreach attr matcher
			fields.add(field);
			if (!isMultiMatch) {
				break;
			}
		}
		return fields;
	}

	/**
	 * @param matcherMap
	 * @return
	 * @author zouyandi
	 */
	private boolean checkMatchersFind(Map<String, Matcher> matcherMap) {
		boolean isFind = true;
		int index = 0;
		for (Matcher matcher : matcherMap.values()) {
			if (!matcher.find()) {
				if (index != 0) {
					matcher.reset();
					if (!matcher.find()) {
						isFind = false;
					}
				} else {
					isFind = false;
				}
			}
			index++;
		}
		return isFind;
	}

	/**
	 * @param isMultiMatch
	 *            the isMultiMatch to set
	 */
	public void setMultiMatch(boolean isMultiMatch) {
		this.isMultiMatch = isMultiMatch;
	}

}
