/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.thread;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.parser.factory.Factory;
import com.weibo.datasys.parser.html.core.PageParse;
import com.weibo.datasys.parser.html.data.PageData;
import com.weibo.datasys.parser.html.data.ParseData;
import com.weibo.datasys.parser.main.ThreadParser;
import com.weibo.datasys.parser.product.demo.DemoParseData;

public class ThreadWorker extends Thread {
	private static final Logger LOG = LoggerFactory
			.getLogger(ThreadWorker.class);
	private ExecutorService threadPool = Executors.newFixedThreadPool(2);

	public void run() {
		int failTimes = 0;
		String id = "";
		while (true) {
			try {
				PageData webpage = ThreadParser.readQueue.poll(1,
						TimeUnit.MINUTES);
				if (null != webpage) {
					id = webpage.getId();
					String parseDataClassName = ConfigFactory.getString("parseDataClass", "com.weibo.datasys.parser.product.demo.DemoParseData");
					Class<?> parseDataClass = Class.forName(parseDataClassName);
					Constructor<?>  parseDataClassConstructor = parseDataClass.getDeclaredConstructor(PageData.class);
					ParseData pData = (ParseData)parseDataClassConstructor.newInstance(webpage);
					PageParse parse = ThreadParser.factory.getPageParse(pData);
					List<PageParse> tasks = new ArrayList<PageParse>();
					tasks.add(parse);
					// 并发执行搜索
					List<Future<ParseData>> rets = threadPool.invokeAll(tasks,
							ThreadManager.workTimeOut, TimeUnit.SECONDS);
					for (Future<ParseData> ret : rets) {

						if (!ret.isCancelled()) {// 正常完成
							pData = ret.get();
							failTimes = 0;
							if (1 == pData.getCrawlerstate()) {
								ThreadParser.writeQueue.put(pData);
							} else {
								ThreadParser.updateQueue.put(pData);

							}
						} else {// 不正常完成
							pData.setCrawlerstate(4);
							ThreadParser.updateQueue.put(pData);
						}

					}
					pData = null;
					parse = null;
				} else if (0 == ThreadManager.parseType
						&& 0 == ThreadParser.readQueue.size()) {
					failTimes++;
					if (5 == failTimes) {
						LOG.info("{}线程退出。", this.getName());
						threadPool.shutdown();
						break;
					}
					Thread.sleep(60 * 1000);
				}
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.info("ThreadWork start exception: " + id);
			}
		}
		synchronized (this) {
			ThreadManager.workThreadNum--;
			if (!ThreadManager.alive()) {
				LOG.info("程序运行结束！");
			}
		}
	}
}
