/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.crawlUnit.fetcher;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.CommonDownloader;
import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.conf.IConfigurable;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.monitor.CrawlerMonitorInfo;

public abstract class AbstractFetcher implements IConfigurable {

	private static Logger logger = LoggerFactory.getLogger("fetchErrLog");

	protected static CommonDownloader comDownloader;

	static {
		int connTimeout = ConfigFactory.getInt("fetcher.connectTimeout", 5000);
		int readTimeout = ConfigFactory.getInt("fetcher.readTimeout", 5000);
		int maxRedirectTimes = ConfigFactory.getInt("fetcher.redirectTimes", 0);
		boolean disableDownErroLog = ConfigFactory.getBoolean(
				"fetcher.disableDownErroLog", true);
		int globalMaxConcurrent = ConfigFactory.getInt(
				"fetcher.globalMaxConcurrent", 50);
		// 初始化通用下载器
		comDownloader = new CommonDownloader(connTimeout, readTimeout,
				maxRedirectTimes, Math.min(100, globalMaxConcurrent), 1);
		comDownloader.setUseDNMS(false);
		comDownloader.setIgnoreDownException(disableDownErroLog);
		comDownloader.start();
	}

	/**
	 * 
	 * 获取CommonDownloader下载完成的结果，最多阻塞timeout ms，timeout==0则可以无限阻塞
	 * 
	 * @param timeout
	 * @return
	 */
	public static DownResponse getResponse(int timeout) {
		DownResponse resp = comDownloader.getAsyncResponse(timeout);
		if (resp != null
				&& (resp.getException() != null || resp.getRetCode() != 200)) {
			CrawlerMonitorInfo.addCounter("failedFetch", 1);
			Exception exception = resp.getException();
			if (exception != null) {
				CrawlerMonitorInfo.addCounter(
						"fetch_err_" + exception.getMessage(), 1);
				logger.warn("[Exception] - msg={} | url={}",
						exception.getMessage(), resp.getOriginalUrl());
			} else {
				CrawlerMonitorInfo.addCounter(
						"fetch_err_RetCode=" + resp.getRetCode(), 1);
				logger.warn("[RetCode] - ret={} | url={}", resp.getRetCode(),
						resp.getOriginalUrl());
			}
		}
		return resp;
	}

	/**
	 * @return 当前已上传总字节数
	 */
	public static long getUpBytes() {
		return comDownloader.getUpBytes();
	}

	/**
	 * @return 当前已下载总字节数
	 */
	public static long getDownBytes() {
		return comDownloader.getDownBytes();
	}

	/**
	 * 
	 * 采集未采集信息，返回已采集信息
	 * 
	 * @param crawlInfo
	 * @return
	 */
	public abstract CrawlInfo fetch(CrawlInfo crawlInfo);

	@Override
	public void configWithKeyValues(Map<String, String> paraMap) {
	}

}