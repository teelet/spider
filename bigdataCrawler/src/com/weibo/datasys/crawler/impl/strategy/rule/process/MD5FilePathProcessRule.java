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

import java.util.Map;

import com.weibo.datasys.common.util.IOUtil;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.process.AbstractProcessRule;

public class MD5FilePathProcessRule extends AbstractProcessRule {

	private int subDirCount = 2;

	private int subDirLength = 2;

	/**
	 * @param task
	 */
	public MD5FilePathProcessRule(Task task) {
		super(task);
	}

	@Override
	protected String applyCustomProcess(String processValue) {
		String path = IOUtil.md5ToPath(processValue, subDirCount, subDirLength);
		return path;
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		super.configWithParameters(paraMap);
		subDirCount = StringUtils.parseInt(paraMap.get(subDirCount),
				subDirCount);
		subDirLength = StringUtils.parseInt(paraMap.get(subDirLength),
				subDirLength);
	}

}
