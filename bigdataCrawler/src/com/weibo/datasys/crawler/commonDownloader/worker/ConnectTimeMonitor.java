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

public class ConnectTimeMonitor implements Runnable {

	private static Logger logger = LoggerFactory
			.getLogger(ConnectTimeMonitor.class);

	private Controller controller;

	private Set<SelectionKey> selectorKeys;

	private int defaultConnTimeout;

	public ConnectTimeMonitor(Controller controller) {
		this.controller = controller;
		selectorKeys = controller.getMonitoredKeys();
		this.defaultConnTimeout = controller.getDownloader().getConnTimeout();
	}

	@Override
	public void run() {
		while (!controller.getDownloader().isClosed()) {
			if (selectorKeys.size() > 0) {
				for (SelectionKey key : selectorKeys) {
					// 如果key的关心操作集为8，说明该key在等待连接中，这种情况下才去判定是否超时
					synchronized (key) {
						if (key.isValid() && key.interestOps() == 8) {
							String msg = "ConnectTimeOut";
							InnerRequest req = (InnerRequest) key.attachment();
							if (key.selector() == null) {
								req.setEndDnsTime(System.currentTimeMillis());
								msg = "DNSTimeOut";
							}
							req.setEndConnTime(System.currentTimeMillis());
							// 优先使用请求自带超时设置，没有则使用默认设置
							int connTimeout = req.getConnectTimeout();
							if (connTimeout <= 0) {
								connTimeout = defaultConnTimeout;
							}
							if (req.getConnTime() >= connTimeout) {
								// 连接超时了，终止连接操作
								key.cancel();
								// 尝试打印异常
								req.setException(new SocketTimeoutException(
										"Connector timed out: " + msg));
								DownLogUtil.error(controller, req, msg, logger);
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
