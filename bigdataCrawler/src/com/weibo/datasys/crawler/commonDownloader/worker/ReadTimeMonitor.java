/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader.worker;

import java.net.SocketTimeoutException;
import java.nio.channels.SelectionKey;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.commonDownloader.util.DownLogUtil;

public class ReadTimeMonitor implements Runnable {

	private static Logger logger = LoggerFactory
			.getLogger(ReadTimeMonitor.class);

	private Controller controller;

	private Set<SelectionKey> selectorKeys;

	private int defaultReadTimeout;

	public ReadTimeMonitor(Controller controller) {
		this.controller = controller;
		selectorKeys = controller.getMonitoredKeys();
		this.defaultReadTimeout = controller.getDownloader().getReadTimeout();
	}

	@Override
	public void run() {
		while (!controller.getDownloader().isClosed()) {
			if (selectorKeys.size() > 0) {

				for (SelectionKey key : selectorKeys) {
					// 如果key的关心操作集为1，说明该key在等待读取中，这种情况下才去判定是否超时
					// 如果key的关心操作集为0，说明该key在读取中，由于是非阻塞读取，所以忽略读取中的key，以免发生并发异常
					synchronized (key) {
						if (key.isValid() && key.interestOps() == 1) {
							InnerRequest req = (InnerRequest) key.attachment();
							req.setEndReadTime(System.currentTimeMillis());
							// 优先使用请求自带超时设置，没有则使用默认设置
							int readTimeout = req.getReadTimeout();
							if (readTimeout <= 0) {
								readTimeout = defaultReadTimeout;
							}
							if (req.getReadTime() >= readTimeout) {
								// 读取超时了，终止读取操作
								key.cancel();
								// 尝试打印异常
								req.setException(new SocketTimeoutException(
								"Reader timed out."));
								DownLogUtil.error(controller, req,
										"ReaderTimedOut", logger);
								controller.unRegIncompleteKey(key);
								controller.putExceptionResp(req, key);
							}
						} else if (!key.isValid()) {
							selectorKeys.remove(key);
						}
					}// end of syn
				}// end of for
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
