/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.strategy.rule.process;

import java.util.Map;

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.AbstractRule;

public abstract class AbstractProcessRule extends
		AbstractRule<CommonData, CommonData> {

	protected String[] processFields = new String[0];

	/**
	 * @param task
	 */
	public AbstractProcessRule(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		String processFieldsString = paraMap.get("processFields");
		if (StringUtils.isNotEmpty(processFieldsString)) {
			processFields = processFieldsString.split(";");
		}
	}

	@Override
	public CommonData apply(CommonData in) {
		for (String processField : processFields) {
			String processValue = in.getBaseField(processField);
			processValue = applyCustomProcess(processValue);
			in.setBaseField(processField, processValue);
		}
		return in;
	}

	protected abstract String applyCustomProcess(String processValue);

}
