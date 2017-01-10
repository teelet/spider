/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.work;

import com.weibo.datasys.crawler.base.crawlUnit.fetcher.AbstractFetcher;
import com.weibo.datasys.crawler.base.monitor.CrawlerMonitorInfo;
import com.weibo.datasys.crawler.base.work.BaseWork;

/**
 * 
 * 监控抓取速度的工作线程
 * 
 */
public class FetchSpeedMonitorWork extends BaseWork {

	@Override
	protected void doWork() {
		long s = System.currentTimeMillis();
		long lastUpBytes = AbstractFetcher.getUpBytes();
		long lastDownBytes = AbstractFetcher.getDownBytes();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		long e = System.currentTimeMillis();
		long nowUpBytes = AbstractFetcher.getUpBytes();
		long nowDownBytes = AbstractFetcher.getDownBytes();

		long upSpeed = (nowUpBytes - lastUpBytes) / (e - s) * 1000;
		long downSpeed = (nowDownBytes - lastDownBytes) / (e - s) * 1000;
		
		CrawlerMonitorInfo.getCounter("upSpeed").set(upSpeed);
		CrawlerMonitorInfo.getCounter("downSpeed").set(downSpeed);

	}

}
