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

import java.io.IOException;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.commonDownloader.util.ConnectUtil;
import com.weibo.datasys.crawler.commonDownloader.util.DownLogUtil;

public class Connector implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(Connector.class);

	private Controller controller;

	public Connector(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		while (!controller.getDownloader().isClosed()) {
			try {
				InnerRequest req = controller.pollRequest();
				if (req != null) {
					try {
						// 记录开始处理时间
						req.setStartProcessTime(System.currentTimeMillis());
						// 记录连接开始时间
						req.setStartConnTime(System.currentTimeMillis());
						// 记录dns开始时间
						req.setStartDnsTime(System.currentTimeMillis());
						// 添加DnsKey以便监控dns超时
						controller.getMonitoredKeys().add(req.getDnsKey());
						// 创建SocketChannel发起非阻塞连接
						SocketChannel channel = this.connect(req);
						// 检查req在dns过程中有没有发生异常，没有异常才注册Channel
						if (req.getException() == null) {
							// 将SocketChannel注册到Selector，关心操作为8即connect，同时添加附件，
							SelectionKey key = channel.register(controller
									.getSelector(), 8, req);
							// 将key添加到被监视集合里，监视连接超时
							controller.getMonitoredKeys().add(key);
						} else if (req.getDnsTime() < controller
								.getDownloader().getConnTimeout()) {
							// dns时间小于连接超时设置，说明不是dns超时而是UnknownHost，抛出异常
							throw req.getException();
						} else if (channel != null) {
							channel.close();
						}
					} catch (Exception e) {
						// 记录连接结束时间
						req.setEndConnTime(System.currentTimeMillis());
						req.setException(e);
						// 尝试打印异常
						DownLogUtil.error(controller, req, "ConnectorError",
								logger);
						// 将异常应答放入应答队列
						controller.putExceptionResp(req, null);
					}
				} else {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
				}
			} catch (Throwable e) {
				logger.error("[ConnectorThreadError] - ", e);
			}
		}
	}

	/**
	 * 
	 * 创建SocketChannel，发起连接
	 * 
	 * @return
	 * @throws IOException
	 */
	public SocketChannel connect(InnerRequest req) throws IOException {
		SocketAddress socketAddress = null;
		SocketChannel channel = null;
		try {
			socketAddress = ConnectUtil.getConnectSocketAddress(req, controller
					.getDnsCache());
			// 记录dns结束时间
			req.getDnsKey().cancel();
			req.setEndDnsTime(System.currentTimeMillis());
			channel = ConnectUtil.createChannel();
			channel.connect(socketAddress);
		} catch (UnknownHostException e) {
			req.getDnsKey().cancel();
			req.setEndDnsTime(System.currentTimeMillis());
			req.setException(e);
		}

		return channel;
	}
}