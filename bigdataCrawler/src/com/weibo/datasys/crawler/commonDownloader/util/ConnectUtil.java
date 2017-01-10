/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader.util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;

public class ConnectUtil {

	/**
	 * 
	 * 通过DNS查找请求url的IP地址
	 * 
	 * @param req
	 * @param dnsCache
	 * @return
	 * @throws UnknownHostException
	 */
	public static SocketAddress getConnectSocketAddress(InnerRequest req,
			DnsCache dnsCache) throws UnknownHostException {
		URL url = req.getUrl();
		Proxy proxy = req.getProxy();
		SocketAddress sockAddr = null;
		if (proxy == null || Proxy.NO_PROXY.equals(proxy)) {
			// 不使用代理
			// 设置连接端口
			int sockPort = url.getPort() == -1 ? 80 : url.getPort();

			// 默认使用请求自带的IP，没有设置则调用 IDnsCache 接口获取url对应ip
			String sockIP = req.getIp();
			if (sockIP == null || sockIP.trim().length() == 0)
				sockIP = dnsCache.getIP(url.getHost());
			sockAddr = new InetSocketAddress(sockIP, sockPort);
		} else {
			// 使用代理
			sockAddr = proxy.address();
		}
		return sockAddr;
	}

	/**
	 * 
	 * 打开一个非阻塞SocketChannel
	 * 
	 * @return
	 * @throws IOException
	 */
	public static SocketChannel createChannel() throws IOException {
		// 打开一个SocketChannel
		SocketChannel socketChannel = SocketChannel.open();
		// 设置SocketChannel为非阻塞模式
		socketChannel.configureBlocking(false);
		return socketChannel;
	}

}
