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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.parse.AbstractContentExtractRule;

/**
 * 
 * 通过xpath匹配实现的内容提取规则
 * 
 * 
 */
public class XpathMatchRule extends AbstractContentExtractRule {

	private static Logger logger = LoggerFactory
			.getLogger(XpathMatchRule.class);

	private static final String XPATH_PREFIX = "xpath:";

	private static final char MULTI_VALUE_SPLITER = ';';

	private boolean isMultiMatch;

	private String baseNodeXpath = "//";

	private Map<String, String> attrXpathMap = new LinkedHashMap<String, String>();

	/**
	 * @param task
	 */
	public XpathMatchRule(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		setMultiMatch(Boolean.parseBoolean(paraMap.get("isMultiMatch")));
		baseNodeXpath = paraMap.get("baseNode");
		if (StringUtils.isEmptyString(baseNodeXpath)) {
			baseNodeXpath = "//";
		}
		for (Entry<String, String> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(XPATH_PREFIX)) {
				String attr = key.substring(XPATH_PREFIX.length());
				String xpath = entry.getValue();
				attrXpathMap.put(attr, xpath);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CommonData> apply(CommonData in) {
		List<CommonData> fields = new ArrayList<CommonData>();
		String xml = in.getBaseField("src");
		try {
			Document doc = DocumentHelper.parseText(xml);
			List<Node> baseNodes = doc.selectNodes(baseNodeXpath);
			for (int i = 0; i < baseNodes.size(); i++) {
				Node baseNode = baseNodes.get(i);
				CommonData fieldData = new CommonData();
				// 遍历xpath列表，填充Field属性
				for (Entry<String, String> entry : attrXpathMap.entrySet()) {
					String attr = entry.getKey();
					String xpath = entry.getValue();
					// 选取属性对应的xml节点，可能多个，多个值用分隔符分隔
					List<Node> valueNodes = baseNode.selectNodes(xpath);
					StringBuilder builder = new StringBuilder();
					for (Node valueNode : valueNodes) {
						String tmpValue = valueNode.getText();
						if (!StringUtils.isEmptyString(tmpValue)) {
							builder.append(tmpValue)
									.append(MULTI_VALUE_SPLITER);
						}
					}
					if (builder.length() > 0) {
						builder.setLength(builder.length() - 1);
					}
					String value = builder.toString();
					if ("id".equalsIgnoreCase(attr)) {
						fieldData.setId(value);
					} else {
						fieldData.setBaseField(attr, value);
					}
				}
				// 添加Field到集合里
				fields.add(fieldData);
				if (!isMultiMatch) {
					// 不允许多次提取，结束循环
					break;
				}
			}// end of for each baseNode
		} catch (Exception e) {
			logger.error("[XpathMatchRuleError] - ", e);
		}
		return fields;
	}

	/**
	 * @param isMultiMatch
	 *            the isMultiMatch to set
	 */
	public void setMultiMatch(boolean isMultiMatch) {
		this.isMultiMatch = isMultiMatch;
	}

	/**
	 * 添加attr，xpath映射
	 * 
	 * @param attrName
	 * @param xpath
	 */
	public void addExtractPattern(String attrName, String xpath) {
		this.attrXpathMap.put(attrName, xpath);
	}

}
