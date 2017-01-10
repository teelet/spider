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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.html.data.ParseData;
import com.weibo.datasys.parser.main.ThreadParser;
import com.weibo.datasys.parser.sql.Updater;

public class ThreadUpdater extends Thread {

	private static final Logger LOG = LoggerFactory
			.getLogger(ThreadUpdater.class);
	private static int num = 0;

	public void run() {
		Updater updater = ThreadParser.factory.getUpdater();
		updater.init();
		int failTimes = 0;
		while (true) {
			try {
				ParseData pData = ThreadParser.updateQueue.poll(1,
						TimeUnit.MINUTES);
				if (null != pData) {
					failTimes = 0;
					updater.write(pData);
					synchronized (this) {
						num++;
					}
					if (num % 1000 == 0) {
						LOG
								.debug(
										"[{}]更新数据: ID:{}\t queueSize:[{},{},{}] Set:[{}]",
										new Object[] {
												num,
												pData.getId(),
												ThreadParser.readQueue.size(),
												ThreadParser.writeQueue.size(),
												ThreadParser.updateQueue.size(),
												ThreadManager.readDataList
														.size() });
					}
				} else if (0 == ThreadManager.parseType
						&& 0 == ThreadParser.updateQueue.size()) {
					updater.commit();
					failTimes++;
					if (50 == failTimes) {
						LOG.info("{}线程退出。", this.getName());
						break;
					}
					Thread.sleep(6 * 1000);
				}
				Thread.sleep(1);
			} catch (Exception e) {
				try {
					updater.commit();
				} catch (Exception e1) {
				}
				LOG.error("", e);
			}
		}
		updater.close();
		synchronized (this) {
			ThreadManager.updateThreadNum--;
			if (!ThreadManager.alive()) {
				LOG.info("程序运行结束！");
			}
		}
	}

}
