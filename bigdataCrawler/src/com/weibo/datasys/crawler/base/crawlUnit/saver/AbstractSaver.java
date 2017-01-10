/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.crawlUnit.saver;

import java.util.Map;

import com.weibo.datasys.common.conf.IConfigurable;
import com.weibo.datasys.crawler.base.entity.ParseInfo;

public abstract class AbstractSaver implements IConfigurable {

	/**
	 * 
	 * 保存已解析信息
	 * 
	 * @param parseInfo
	 */
	public abstract void save(ParseInfo parseInfo);

	@Override
	public void configWithKeyValues(Map<String, String> paraMap) {

	}

}
