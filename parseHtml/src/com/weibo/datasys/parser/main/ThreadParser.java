/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.rule.ParseStrategy;
import com.weibo.datasys.parser.conf.ParseConfFactory;
import com.weibo.datasys.parser.factory.Factory;
import com.weibo.datasys.parser.html.data.PageData;
import com.weibo.datasys.parser.html.data.ParseData;
import com.weibo.datasys.parser.log.LogConfig;
import com.weibo.datasys.parser.sql.DBConnectionFactory;
import com.weibo.datasys.parser.thread.ThreadBatchInserter;
import com.weibo.datasys.parser.thread.ThreadManager;
import com.weibo.datasys.parser.thread.ThreadReader;
import com.weibo.datasys.parser.thread.ThreadUpdater;
import com.weibo.datasys.parser.thread.ThreadWorker;

public class ThreadParser {
	private static final Logger LOG = LoggerFactory.getLogger(ThreadParser.class);
	public static int readSize;
	public static int writeSize;
	public static ArrayBlockingQueue<PageData> readQueue;
	public static ArrayBlockingQueue<ParseData> writeQueue;
	public static ArrayBlockingQueue<ParseData> updateQueue;
	public static Factory factory;
	private static String confPath = "";
	private static String logPath = "";
	private static String hostFilterConfigPath = "";

	public static void main(String[] args) {
		try {
			init();
			for (int it = 0; it < ThreadManager.readThreadNum; ++it) {
				Thread readThread = new ThreadReader();
				readThread.setName("ReadThread - " + (it + 1));
				readThread.start();
			}
			Thread.sleep(3 * 1000);
			for (int it = 0; it < ThreadManager.workThreadNum; ++it) {
				Thread workThread = new ThreadWorker();
				workThread.setName("WorkThread - " + (it + 1));
				workThread.start();
			}
			for (int it = 0; it < ThreadManager.insertThreadNum; ++it) {
				Thread insertThread = new ThreadBatchInserter();
				insertThread.setName("InsertThread - " + (it + 1));
				insertThread.start();
			}
			for (int it = 0; it < ThreadManager.updateThreadNum; ++it) {
				Thread updateThread = new ThreadUpdater();
				updateThread.setName("UpdattThread - " + (it + 1));
				updateThread.start();
			}
		} catch (Exception e) {
			LOG.error("", e);
		}
	}

	public static void parseOneTable() {
		try {
			for (int it = 0; it < ThreadManager.readThreadNum; ++it) {
				Thread readThread = new ThreadReader();
				readThread.setName("ReadThread - " + (it + 1));
				readThread.start();
			}
			Thread.sleep(3 * 1000);
			for (int it = 0; it < ThreadManager.workThreadNum; ++it) {
				Thread workThread = new ThreadWorker();
				workThread.setName("WorkThread - " + (it + 1));
				workThread.start();
			}
			for (int it = 0; it < ThreadManager.insertThreadNum; ++it) {
				Thread insertThread = new ThreadBatchInserter();
				insertThread.setName("InsertThread - " + (it + 1));
				insertThread.start();
			}
			for (int it = 0; it < ThreadManager.updateThreadNum; ++it) {
				Thread updateThread = new ThreadUpdater();
				updateThread.setName("UpdattThread - " + (it + 1));
				updateThread.start();
			}
		} catch (Exception e) {
			LOG.error("", e);
		}

	}

	public static void init() throws Exception {

		LOG.info(new Date() + " - [EnvInit] - start.");
		String home = System.getProperty("home.dir");

		if (null == home) {
			throw new Exception("-Dhome.dir properties not found.");
		}
		// 配置文件和日志文件路径

		logPath = home + "/conf/logback.xml";
		confPath = home + "/conf/config.xml";
		hostFilterConfigPath = home + "/conf/strategy.xml";
		commonInit();
		LOG.info(new Date() + " - [EnvInit] - end.");

		//init parse config
		//parseConfInit();
		
		ParseStrategy.init(hostFilterConfigPath);
		ParseConfFactory.buildParseConf();
		
		DBConnectionFactory.init();
		ThreadManager.init();
		
		// 日后如果需要 也可以配置不同的parse factory
		String factoryclassName = ConfigFactory.getString("factoryClass", "com.weibo.datasys.parser.factory.Factory");
		Class<?> input = Class.forName(factoryclassName);
		java.lang.Object objectCopy = input.newInstance();
		//
		
		
		factory = (Factory) objectCopy;
		factory.init();
		readSize = ConfigFactory.getInt("queue.readQueueSize", 1000);
		writeSize = ConfigFactory.getInt("queue.updateQueueSize", 1000);
		readQueue = new ArrayBlockingQueue<PageData>(readSize);
		writeQueue = new ArrayBlockingQueue<ParseData>(writeSize);
		updateQueue = new ArrayBlockingQueue<ParseData>(writeSize);
		LOG.info("初始化完成。");
		
		
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
	private static void commonInit() throws Exception {
		LogConfig.config(logPath);
		ConfigFactory.init(confPath);
	}
	
	/*
	private static void parseConfInit() throws Exception {
		String home = System.getProperty("home.dir");
		if (null == home) {
			throw new Exception("-Dhome.dir properties not found.");
		}
		File parseConfigPath = new File(home + "/parseConfigs/");
		if (parseConfigPath.exists() && parseConfigPath.isDirectory()) {
			for (File taskFile : parseConfigPath.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith(".xml")) {
						return true;
					} else {
						return false;
					}
				}
			})) {
				BufferedReader reader = new BufferedReader(new FileReader(
						taskFile));
				String tmp = "";
				String xml = "";
				while (null != (tmp = reader.readLine())) {
					xml += tmp + "\n";
				}
				reader.close();
				ParseConfFactory.buildParseConf(xml);
			}
		}
	}
	*/
}
