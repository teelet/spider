/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.manager;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.log.LogConfig;
import com.weibo.datasys.common.util.StringUtils;

/**
*
* 初始化系统运行环境的基础类，提供日志，配置初始化。需要更多环境初始化操作请继承实现customInit()方法
* 
 **/
public class BaseEnvManager {

	private static final long SYSTEM_START_TIME = System.currentTimeMillis();

	protected static String confPath;
	protected static String logPath;

	/**
	 * 
	 * 初始化系统运行环境，包括公共初始化和其他初始化，其他初始化操作通过继承BaseEnvManager进行定义
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		System.out.println(new Date() + " - [EnvInit] - Start.");
		// 获取 程序 HOME目录，如果没有指定，退出
		String home = System.getProperty("home.dir");
		if (null == home) {
			System.out.println("Please set -Dhome.dir properties.");
			throw new Exception("-Dhome.dir properties not found.");
		}
		// 配置文件和日志文件路径
		confPath = home + "/conf/config.xml";
		logPath = home + "/conf/logback.xml";
		// 初始化包括两个操作：公共初始化和由子类定义的其他初始化
		this.commonInit();
		this.customInit();
		System.out.println(new Date() + " - [EnvInit] - End.");
	}

	/**
	 * 
	 * 公共初始化操作，包括日志和配置文件初始化
	 * 
	 * @param logPath
	 * @param confPath
	 * 
	 * @throws Exception
	 */
	protected void commonInit() throws Exception {
		// 初始化日志配置
		if (!confPath.contains(":")) {
			LogConfig.config(logPath);
		} else {
			System.out.println(new Date()
					+ " - [ConfigLog] - Windows debug, skip log config.");
		}
		// 初始化程序配置
		ConfigFactory.init(confPath);
	}

	/**
	 * 其他初始化操作，由子类具体定义，默认没有操作
	 */
	protected void customInit() throws Exception {
	}

	/**
	 * 清理程序运行环境，确保可以再次调用init()方法重新进行程序环境初始化，基类默认实现不做任何操作
	 */
	public void clear() {

	}

	/**
	 * @return 系统启动时间
	 */
	public static long getSystemStartTime() {
		return SYSTEM_START_TIME;
	}

	/**
	 * @return 系统IP
	 */
	public static String getSystemIP() {
		String ip = System.getProperty("ip");
		if (StringUtils.isEmptyString(ip)) {
			try {
				ip = Inet4Address.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
			}
		}
		return ip;
	}

}
