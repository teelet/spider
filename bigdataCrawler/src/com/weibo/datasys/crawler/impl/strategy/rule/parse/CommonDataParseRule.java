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

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.parse.AbstractContentExtractRule;

/**
 * 
 * 通过正则匹配实现的内容提取规则
 * 
 * 
 */
public class CommonDataParseRule extends AbstractContentExtractRule {

	private Map<String, String> extractInfoMap = new LinkedHashMap<String, String>();

	private static final String TARGET_FIELD_PREFIX = "targetField:";

	private static final String BASE_FIELD_PREFIX = "baseField:";

	private static final String EXTEND_FIELD_PREFIX = "extendField:";

	/**
	 * @param task
	 */
	public CommonDataParseRule(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		for (Entry<String, String> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(TARGET_FIELD_PREFIX)) {
				// 默认属性名称为""
				String attr = "";
				int index = key.lastIndexOf(":");
				if (index != -1) {
					// 有指定属性名称则提取之
					attr = key.substring(index + 1);
				}
				extractInfoMap.put(attr, entry.getValue());
			}
		}
	}

	@Override
	public List<CommonData> apply(CommonData in) {
		List<CommonData> fields = new ArrayList<CommonData>();
		CommonData src = in.getCommonField("src");
		CommonData field = new CommonData();
		for (Entry<String, String> entry : extractInfoMap.entrySet()) {
			String attr = entry.getKey();
			String value = "";
			String srcFieldString = entry.getValue();
			if (srcFieldString.startsWith(BASE_FIELD_PREFIX)) {
				value = src.getBaseField(srcFieldString.replace(
						BASE_FIELD_PREFIX, ""));
			} else {
				value = src.getExtendField(srcFieldString.replace(
						EXTEND_FIELD_PREFIX, ""));
			}
			if (attr.equals("id")) {
				field.setId(value);
			} else {
				field.setBaseField(attr, value);
			}
		}
		fields.add(field);
		return fields;
	}

}
