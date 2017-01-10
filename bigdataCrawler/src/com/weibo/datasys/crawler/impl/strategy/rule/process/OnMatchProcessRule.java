/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.process;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.process.AbstractProcessRule;


public class OnMatchProcessRule extends AbstractProcessRule {

	private static final String OP_DATA_PREFIX = "opData";

	private Pattern pattern;

	private String op;

	private List<String> opDatas = new ArrayList<String>();

	/**
	 * @param task
	 */
	public OnMatchProcessRule(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		super.configWithParameters(paraMap);
		pattern = Pattern.compile(paraMap.get("matchReg"));
		op = paraMap.get("op");
		for (Entry<String, String> entry : paraMap.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(OP_DATA_PREFIX)) {
				opDatas.add(entry.getValue());
			}
		}
	}

	@Override
	public CommonData apply(CommonData in) {
		for (String processField : processFields) {
			String processValue = in.getBaseField(processField);

			in.setBaseField(processField, processValue);
		}
		return in;
	}

	@Override
	protected String applyCustomProcess(String processValue) {
		Matcher matcher = pattern.matcher(processValue);
		if (matcher.matches()) {
			if (op.equals("add") && opDatas.size() == 1) {
				processValue = processValue + opDatas.get(0);
			} else if (op.equals("replace") && opDatas.size() == 2) {
				processValue = processValue.replaceAll(opDatas.get(0),
						opDatas.get(1));
			} else if (op.equals("timetolong") && opDatas.size() == 1) {
				SimpleDateFormat sdf = new SimpleDateFormat(opDatas.get(0));
				try {
					Date date = sdf.parse(processValue);
					String timePattern = opDatas.get(0);
					if (!timePattern.contains("y")) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date);
						calendar.set(Calendar.YEAR,
								Calendar.getInstance().get(Calendar.YEAR));
						date = calendar.getTime();
					}
					processValue = date.getTime() + "";
				} catch (ParseException e) {
				}
			}
		}
		return processValue;
	}

}
