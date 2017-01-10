/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.preparser;

import com.weibo.datasys.crawler.base.crawlUnit.preparser.AbstractParser;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.ParseInfo;

public class StatisticParser extends AbstractParser {

	@Override
	public ParseInfo parse(CrawlInfo crawlInfo) {
		ParseInfo parseInfo = new ParseInfo();
		parseInfo.setThisCrawlInfo(crawlInfo);
		return parseInfo;
	}

}
