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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.base.crawlUnit.fetcher.AbstractFetcher;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.monitor.CrawlerMonitorInfo;

public class FileFetcher extends AbstractFetcher {

	@Override
	public CrawlInfo fetch(CrawlInfo crawlInfo) {
		SeedData seedData = crawlInfo.getSeedData();
		Task task = crawlInfo.getValidTask();
		// 任务已被删除or已停止则终止操作
		if (task != null) {
			InnerRequest req = new InnerRequest();
			DownResponse resp = new DownResponse(req);
			resp.setRetCode(200);
			String id = seedData.getUrlId();
			String fileString = "page/" + id + ".xml";
			try {
				InputStream in = new FileInputStream(fileString);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buffer = new byte[10240];
				int readCount = 0;
				while (-1 != (readCount = in.read(buffer))) {
					out.write(buffer, 0, readCount);
				}
				in.close();
				resp.setContentByte(out.toByteArray());
				crawlInfo.setResp(resp);
			} catch (Exception e) {
				e.printStackTrace();
			}
			CrawlerMonitorInfo.addCounter("tryFetch", 1);
		}
		return crawlInfo;
	}

}
