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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.AbstractRule;
import com.weibo.datasys.crawler.base.strategy.rule.filter.AbstractFilterRule;
import com.weibo.datasys.crawler.base.strategy.rule.process.AbstractProcessRule;

/**
 * 
 * Field提取规则类
 * 
 * 
 */
public class FieldRule extends
		AbstractRule<CrawlInfo, List<? extends CommonData>> {

	private String fieldName;

	private String extractPosition;

	private AbstractContentExtractRule contentRule;

	private List<AbstractProcessRule> processRules = new ArrayList<AbstractProcessRule>();

	private List<AbstractFilterRule> filters = new ArrayList<AbstractFilterRule>();

	/**
	 * @param task
	 */
	public FieldRule(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		fieldName = paraMap.get("name");
		extractPosition = paraMap.get("extractPosition");
	}

	@Override
	public List<? extends CommonData> apply(CrawlInfo crawlInfo) {
		List<CommonData> fields = new ArrayList<CommonData>();
		CommonData srcData = new CommonData();
		if ("url".equalsIgnoreCase(extractPosition)) {
			String src = crawlInfo.getSeedData().getUrl();
			srcData.setBaseField("src", src);
		} else if ("html".equalsIgnoreCase(extractPosition)) {
			String src = crawlInfo.getHtml();
			srcData.setBaseField("src", src);
		} else if ("SeedData".equalsIgnoreCase(extractPosition)) {
			CommonData src = crawlInfo.getSeedData();
			srcData.setCommonField("src", src);
		}
		// 构造提取Fields所需参数
		srcData.setBaseField("extractname", fieldName);
		// 应用内容提取规则提取Fields
		fields = this.contentRule.apply(srcData);
		// 对提取出来的Fields作后期处理和过滤
		processFields(fields);
		return fields;
	}

	public void processFields(List<CommonData> fields) {
		Iterator<CommonData> iterator = fields.iterator();
		while (iterator.hasNext()) {
			CommonData field = iterator.next();
			// 应用处理规则处理field
			for (AbstractProcessRule processRule : processRules) {
				field = processRule.apply(field);
			}
			// 对每个field中的各个属性content作过滤
			for (String name : field.getBaseFieldNames()) {
				String content = field.getBaseField(name);
				// 过滤content
				boolean isAccept = true;
				for (AbstractFilterRule filter : filters) {
					if (!filter.apply(content)) {
						isAccept = false;
						break;
					}
				}
				if (isAccept) {
					field.setBaseField(name, content);
					// 设置该field的名称
					field.setExtendField("fieldname", fieldName);
				} else {
					// 有一个属性被过滤则丢弃该field
					iterator.remove();
					break;
				}
			}// end of foreach baseFieldName
		}// end of foreach field
	}

	/**
	 * 
	 * 添加一条处理规则
	 * 
	 * @param processRule
	 */
	public void addProcessRule(AbstractProcessRule processRule) {
		this.processRules.add(processRule);
	}

	/**
	 * 
	 * 添加一个过滤器
	 * 
	 * @param filterRule
	 */
	public void addFilter(AbstractFilterRule filterRule) {
		this.filters.add(filterRule);
	}

	/**
	 * @param fieldName
	 *            the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param extractPosition
	 *            the extractPosition to set
	 */
	public void setExtractPosition(String extractPosition) {
		this.extractPosition = extractPosition;
	}

	/**
	 * @param contentRule
	 *            the contentRule to set
	 */
	public void setContentRule(AbstractContentExtractRule contentRule) {
		this.contentRule = contentRule;
	}

	@Override
	public String toString() {
		return super.toString() + "(fieldName=" + fieldName + ")";
	}
}
