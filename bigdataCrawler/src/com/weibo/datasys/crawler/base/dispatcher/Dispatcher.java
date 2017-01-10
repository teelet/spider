/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.dispatcher;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.util.DBUtils;
import com.weibo.datasys.common.util.StopWatch;
import com.weibo.datasys.crawler.base.strategy.rule.seed.AbstractSeedGenerateRule;
import com.weibo.datasys.crawler.base.dao.SeedDataDAO;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.LinkDataFactory;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.monitor.DispatcherMonitorInfo;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

/**
 * 
 * 调度器
 * 
 */
public class Dispatcher {

	private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	private static Map<String, String> crawlers = new ConcurrentHashMap<String, String>();

	private static AtomicInteger seedLock = new AtomicInteger();

	private Dispatcher() {
	}

	/**
	 * 对种子库进行独占操作，加锁，同时通知各个crawler已加锁
	 */
	public static void addSaveLock() {
		if (seedLock.incrementAndGet() > 0) {
			for (String crawlerId : crawlers.keySet()) {
				String url = "http://" + crawlerId.replace("_", ":")
						+ "/?cmd=lock";
				try {
					HttpURLConnection conn = (HttpURLConnection) new URL(url)
							.openConnection();
					conn.setConnectTimeout(60000);
					conn.setReadTimeout(60000);
					conn.connect();
					conn.getResponseCode();
					conn.disconnect();
				} catch (Exception e) {
					logger.error(
							"[lockCrawlerError] - crawlerId={} | e.msg={}",
							new Object[] { crawlerId, e.getMessage() });
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * 释放对种子库进行独占操作的锁，若没有锁存在，通知各个爬虫已解锁
	 */
	public static void releaseSaveLock() {
		if (seedLock.decrementAndGet() == 0) {
			for (String crawlerId : crawlers.keySet()) {
				String url = "http://" + crawlerId.replace("_", ":")
						+ "/?cmd=unlock";
				try {
					HttpURLConnection conn = (HttpURLConnection) new URL(url)
							.openConnection();
					conn.setConnectTimeout(60000);
					conn.setReadTimeout(60000);
					conn.connect();
					conn.getResponseCode();
					conn.disconnect();
				} catch (Exception e) {
					logger.error(
							"[unlockCrawlerError] - crawlerId={} | e.msg={}",
							new Object[] { crawlerId, e.getMessage() });
				}
			}
		}
	}

	/**
	 * 
	 * 向Dispatcher注册指定CrawlerID，Dispatcher向Crawler返回当前running的task列表
	 * 
	 * @param crawlerId
	 * @return
	 */
	public static List<String> registerCrawler(String crawlerId) {
		List<String> runningTaskIds = new ArrayList<String>();
		crawlers.put(crawlerId, "");
		List<Task> tasks = TaskManager.getRunningTasks();
		for (Task task : tasks) {
			runningTaskIds.add(task.getTaskId());
		}
		return runningTaskIds;
	}

	/**
	 * 
	 * 启动task：先启动本地task，再启动各个crawler的task
	 * 
	 * @param taskId
	 * @return
	 */
	public static boolean runTask(String taskId) {
		Task task = TaskManager.getTask(taskId);
		if (task.isRunning()) {
			return false;
		}
		SaveStrategy saveStrategy = task.getSaveStrategy();
		// 创建种子库读写表
		createSeedTables(task);
		// 创建link表
		crateLinkTable(task);
		// 应用task的种子生成规则，生成任务所需的初始化种子
		StopWatch watch = new StopWatch();
		watch.start();
		AbstractSeedGenerateRule seedRule = task.getCrawlStrategy()
				.getSeedRule();
		List<SeedData> seedDatas = seedRule.apply(null);
		if (seedDatas.size() > 0) {
			List<LinkData> linkDatas = new ArrayList<LinkData>();
			for (SeedData seedData : seedDatas) {
				// 设置初始化的种子为实时种子
				seedData.setInstant(true);
				LinkData linkData = LinkDataFactory.buildFromSeedData(seedData);
				linkDatas.add(linkData);
			}
			// 将任务初始种子批量放进任务种子库及链接库
			SeedDataDAO.getInstance().saveBatch(seedDatas,
					saveStrategy.getSeedDS(), saveStrategy.getSeedDB(),
					saveStrategy.getSeedTable() + "_read", true, true);
			task.getDeduplicator().saveLinks(task, linkDatas);
		}
		logger.info(
				"[GenerateTaskSeed] - cost={} ms | count={} | task={}",
				new Object[] { watch.getElapsedTime(), seedDatas.size(),
						task.getTaskId() });
		// 启动本地任务
		boolean result = TaskManager.runTask(taskId);
		if (result) {
			// 本地启动成功，启动远程crawler task
			for (String crawlerId : crawlers.keySet()) {
				String url = "http://" + crawlerId.replace("_", ":")
						+ "/?cmd=runtask&para=" + taskId;
				try {
					HttpURLConnection conn = (HttpURLConnection) new URL(url)
							.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					conn.getResponseCode();
					conn.disconnect();
				} catch (Exception e) {
					logger.error(
							"[runCrawlerTaskError] - taskId={} | crawlerId={} | e.msg={}",
							new Object[] { taskId, crawlerId, e.getMessage() });
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * 重新加载指定task的初始种子，使得task可以爬取增量数据
	 * 
	 * @param taskId
	 */
	public static boolean reloadTaskSeeds(String taskId) {
		// 首先停止本地任务
		boolean result = TaskManager.stopTask(taskId);
		if (result) {
			// 其次应用task的种子生成规则，生成任务所需的初始化种子
			StopWatch watch = new StopWatch();
			watch.start();
			Task task = TaskManager.getTask(taskId);
			AbstractSeedGenerateRule seedRule = task.getCrawlStrategy()
					.getSeedRule();
			List<SeedData> seedDatas = seedRule.apply(null);
			SaveStrategy saveStrategy = task.getSaveStrategy();
			for (SeedData seedData : seedDatas) {
				// 设置增量爬取的种子为实时种子
				seedData.setInstant(true);
			}
			// 批量保存种子到ReadSeedDB
			SeedDataDAO.getInstance().saveBatch(seedDatas,
					saveStrategy.getSeedDS(), saveStrategy.getSeedDB(),
					saveStrategy.getSeedTable() + "_read", true, true);
			logger.info("[ReloadTaskSeed] - cost={} ms | count={} | task={}",
					new Object[] { watch.getElapsedTime(), seedDatas.size(),
							task.getTaskId() });
			// 最后重新启动本地任务
			result = TaskManager.runTask(taskId);
		}
		return result;
	}

	/**
	 * 
	 * 停止task：先停止本地task，再停止各个crawler的task
	 * 
	 * @param taskId
	 * @return
	 */
	public static boolean stopTask(String taskId) {
		boolean result = TaskManager.stopTask(taskId);
		if (result) {
			// 本地停止成功，停止远程crawler task
			for (String crawlerId : crawlers.keySet()) {
				String url = "http://" + crawlerId.replace("_", ":")
						+ "/?cmd=stoptask&para=" + taskId;
				try {
					HttpURLConnection conn = (HttpURLConnection) new URL(url)
							.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					conn.connect();
					conn.getResponseCode();
					conn.disconnect();
				} catch (Exception e) {
					logger.error(
							"[stopCrawlerTaskError] - taskId={} | crawlerId={} | e.msg={}",
							new Object[] { taskId, crawlerId, e.getMessage() });
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * 为指定爬虫id分发一组指定数量的种子
	 * 
	 * @param count
	 * @param crawlerId
	 * @return
	 */
	public static List<SeedData> dispatchSeeds(int count, String crawlerId) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		crawlers.put(crawlerId, "");
		List<Task> tasks = TaskManager.getRunningTasks();
		if (tasks.size() > 0) {
			int avgCount = count / tasks.size();
			for (Task task : tasks) {
				String taskId = task.getTaskId();
				int priority = task.getCrawlStrategy().getPriority();
				int getCount = (int) (avgCount * (1 + 0.3 * priority));
				int realCount = 0;
				for (int i = 0; i < getCount; i++) {
					SeedData seedData = (SeedData) QueueManager.poll(
							QueueType.SEED.name(), taskId);
					if (seedData != null) {
						realCount++;
						seedDatas.add(seedData);
					} else {
						break;
					}
				}
				if (realCount > 0)
					logger.info(
							"[dispatchSeed] - taskId={} | count=real:{}/get:{}",
							new Object[] { task.getTaskId(), realCount,
									getCount });
			}
			if (seedDatas.size() > 0)
				logger.info(
						"[dispatchSeed] - taskCount={} | seedCount=real:{}/req:{}",
						new Object[] { tasks.size(), seedDatas.size(), count });
			DispatcherMonitorInfo.addCounter("dispatch", seedDatas.size());
		}
		return seedDatas;
	}

	/**
	 * 
	 * 检查指定爬虫是否正常
	 * 
	 * @param crawlerId
	 * @return
	 */
	public static boolean checkCrawlerAlive(String crawlerId) {
		boolean isAlive = false;
		String url = "http://" + crawlerId.replace("_", ":") + "/";
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();
			if (200 == conn.getResponseCode()) {
				isAlive = true;
			}
			conn.disconnect();
		} catch (Exception e) {
			logger.info("[crawlerNotAlive] - crawlerId={} | e.msg={}",
					crawlerId, e.getMessage());
		}
		return isAlive;
	}

	/**
	 * 
	 * 获取所有已注册爬虫id
	 * 
	 * @return
	 */
	public static Collection<String> getCrawlers() {
		return crawlers.keySet();
	}

	/**
	 * 
	 * 创建 read、write 种子库(不存在的话)
	 * 
	 * @param task
	 */
	public static void createSeedTables(Task task) {
		SaveStrategy saveStrategy = task.getSaveStrategy();
		String dsname = saveStrategy.getSeedDS();
		String db = saveStrategy.getSeedDB();
		String readTable = saveStrategy.getSeedTable() + "_read";
		String writeTable = saveStrategy.getSeedTable() + "_write";
		//String sampleTable = saveStrategy.getSeedTable() + "_sample";
		String sampleTable = "crawler_taskseed_sample";
		boolean isReadTableExist = DBUtils.isTableExist(dsname, db, readTable);
		if (!isReadTableExist) {
			logger.info("[CreateReadSeedDB] - creating. table={}", readTable);
			DBUtils.createTableAsSample(dsname, db, readTable, sampleTable,
					false);
			logger.info("[CreateReadSeedDB] - done.");
		}
		boolean isWriteTableExist = DBUtils
				.isTableExist(dsname, db, writeTable);
		if (!isWriteTableExist) {
			logger.info("[CreateWriteSeedDB] - creating. table={}", writeTable);
			DBUtils.createTableAsSample(dsname, db, writeTable, sampleTable,
					false);
			logger.info("[CreateWriteSeedDB] - done.");
		}
	}
	
	/**
	 * 创建link库
	 * @param task
	 */
	public static void crateLinkTable(Task task){
		SaveStrategy saveStrategy = task.getSaveStrategy();
		String dsname = saveStrategy.getLinkDS();
		String db = saveStrategy.getLinkDB();
		String linkTable = saveStrategy.getLinkTable();
		//String sampleTable = saveStrategy.getLinkTable() + "_sample";
		String sampleTable = "crawler_linkdb";
		boolean isLinkTableExist = DBUtils.isTableExist(dsname, db, linkTable);
		if (!isLinkTableExist) {
			logger.info("[CreateLinkDB] - creating. table={}", linkTable);
			DBUtils.createTableAsSample(dsname, db, linkTable, sampleTable,
					false);
			logger.info("[CreateLinkDB] - done.");
		}
	}

}
