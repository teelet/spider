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

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.html.data.PageData;
import com.weibo.datasys.parser.main.ThreadParser;
import com.weibo.datasys.parser.sql.Reader;

public class ThreadReader extends Thread {
	private static final Logger LOG = LoggerFactory
			.getLogger(ThreadReader.class);

	public void run() {
		int failTimes = 0;
		Reader reader = ThreadParser.factory.getReader();
		reader.init();
		while (true) {
			ArrayList<PageData> webpages = reader.read();
			try {
				if (null == webpages || webpages.size() == 0) {
					if (0 == ThreadManager.parseType) {
						failTimes++;
						if (50 == failTimes) {
							LOG.info("{}线程退出。", this.getName());
							break;
						}
						Thread.sleep(6 * 1000);
					}
				} else {
					failTimes = 0;
					for (PageData webpage : webpages) {
						ThreadParser.readQueue.put(webpage);
					}
				}
				Thread.sleep(1);
			} catch (Exception e) {
				LOG.error("", e);
			}
		}
		reader.close();
		synchronized (this) {
			ThreadManager.readThreadNum--;
			if (!ThreadManager.alive()) {
				LOG.info("程序运行结束！");
			}
		}
	}
}
