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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.commonDownloader.util.DownLogUtil;
import com.weibo.datasys.crawler.commonDownloader.util.WriteUtil;

public class Writer implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(Writer.class);

	private Controller controller;

	public Writer(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		while (!controller.getDownloader().isClosed()) {
			try {

				SelectionKey key = controller.pollConnectableKey();
				if (key != null) {
					InnerRequest req = (InnerRequest) key.attachment();
					SocketChannel channel = (SocketChannel) key.channel();
					try {
						// 记录连接结束时间
						req.setEndConnTime(System.currentTimeMillis());
						// 完成非阻塞连接
						if (channel.finishConnect()) {
							// 获取http请求的字节数组并放进ByteBuffer
							ByteBuffer byteBuffer = ByteBuffer.wrap(WriteUtil
									.createRequestBytes(req));
							// 通过SocketChannel发送http请求
							int writeCount = channel.write(byteBuffer);
							// 累计已上传字节数
							controller.addUpBytes(writeCount);
							// 记录读取开始时间
							req.setStartReadTime(System.currentTimeMillis());
							// 请求发送完毕，设置key的关心操作为1，即read
							key.interestOps(1);
						} else {
							throw new IOException("Con not finish connect.");
						}
					} catch (Exception e) {
						// 设置请求异常
						req.setException(e);
						// 尝试打印异常
						DownLogUtil.error(controller, req, "WriterError",
								logger);
						// 将异常应答放入应答队列
						controller.putExceptionResp(req, key);
					}
				} else {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
					}
				}
			} catch (Throwable e) {
				logger.error("[WriterThreadError] - ", e);
			}
		}
	}
}
