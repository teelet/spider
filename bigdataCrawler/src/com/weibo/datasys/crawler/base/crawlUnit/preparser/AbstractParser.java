/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.crawlUnit.preparser;

import java.util.Map;

import com.weibo.datasys.common.conf.IConfigurable;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.ParseInfo;

public abstract class AbstractParser implements IConfigurable {

	/**
	 * 
	 * 解析已爬取信息，提取已解析信息
	 * 
	 * @param crawledInfo
	 * @return
	 */
	public abstract ParseInfo parse(CrawlInfo crawlInfo);

	@Override
	public void configWithKeyValues(Map<String, String> paraMap) {

	}

}
