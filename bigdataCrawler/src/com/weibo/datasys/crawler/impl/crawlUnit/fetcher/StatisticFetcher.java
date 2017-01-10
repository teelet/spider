/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.fetcher;

import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.base.crawlUnit.fetcher.AbstractFetcher;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.monitor.CrawlerMonitorInfo;

public class StatisticFetcher extends AbstractFetcher {

	@Override
	public CrawlInfo fetch(CrawlInfo crawlInfo) {
		// 不需要真的fetch，构造假应答返回即可
		DownResponse resp = new DownResponse(new InnerRequest());
		resp.setRetCode(999);
		crawlInfo.setResp(resp);
		CrawlerMonitorInfo.addCounter("tryFetch", 1);
		return crawlInfo;
	}
}
