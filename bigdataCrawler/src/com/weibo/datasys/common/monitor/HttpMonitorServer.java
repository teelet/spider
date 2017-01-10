/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.monitor;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;

/**
 * 
 *轻量级程序内嵌Http监控Server
 *
 **/
public class HttpMonitorServer {

	private static Logger logger = LoggerFactory
			.getLogger(HttpMonitorServer.class);

	private static HttpServer hs;

	public static Map<String, ICommand> cmdMap = new HashMap<String, ICommand>();

	/**
	 * 
	 * http命令格式：http//ip:port/cmd=cmdName&amp;para=p1,p2,p3<br>
	 * 默认命令：http//ip:port/
	 * 
	 * @param port
	 *            http server 监听的端口
	 * @param defaultCommand
	 *            默认命令实例
	 */
	public static void startServer(int port, ICommand defaultCommand) {
		cmdMap.put("default", defaultCommand);
		startServer(port, cmdMap);
	}

	/**
	 * 
	 * http命令格式：http//ip:port/cmd=cmdName&amp;para=p1,p2,p3<br>
	 * 默认命令：http//ip:port/
	 * 
	 * @param port
	 *            http server 监听的端口
	 * @param cmdMap
	 *            http server支持的命令集合
	 */
	public static void startServer(int port, Map<String, ICommand> cmdMap) {
		if (isStarted()) {
			return;
		}
		try {
			HttpMonitorServer.cmdMap = cmdMap;
			hs = HttpServer.create(new InetSocketAddress(port), 1000);
			ExecutorService threads = Executors.newCachedThreadPool();
			hs.createContext("/", new ServerHandler());
			hs.setExecutor(threads);
			hs.start();
			String ip = "127.0.0.1";
			try {
				ip = Inet4Address.getLocalHost().getHostAddress();
			} catch (Exception e) {
			}
			System.out.println(new Date() + " - [HttpMonitorStarted] - http://"
					+ ip + ":" + port + "/");
		} catch (Exception e) {
			logger.error(new Date() + " - [HttpMonitorStartError] - ", e);
		}
	}

	public static void close() {
		if (hs != null) {
			hs.stop(10);
			hs = null;
		}
		System.out.println("[HttpMonitorClosed]");
	}

	public static boolean isStarted() {
		return hs != null;
	}
}
