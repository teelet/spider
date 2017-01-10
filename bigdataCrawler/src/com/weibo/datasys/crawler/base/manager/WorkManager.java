/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.crawler.appMain.Main;
import com.weibo.datasys.crawler.base.exception.TaskException;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.work.BaseWork;
import com.weibo.datasys.crawler.impl.work.AsyncRespCollectWork;
import com.weibo.datasys.crawler.impl.work.FetchSpeedMonitorWork;
import com.weibo.datasys.crawler.impl.work.FetchWork;
import com.weibo.datasys.crawler.impl.work.ParseWork;
import com.weibo.datasys.crawler.impl.work.RefreshTaskWork;
import com.weibo.datasys.crawler.impl.work.RegisterCrawlerWork;
import com.weibo.datasys.crawler.impl.work.ReloadTaskSeedWork;
import com.weibo.datasys.crawler.impl.work.SaveLinkBatchWork;
import com.weibo.datasys.crawler.impl.work.SaveOutLinkBatchWork;
import com.weibo.datasys.crawler.impl.work.SaveOutLinkInstantWork;
import com.weibo.datasys.crawler.impl.work.SavePageBatchWork;
import com.weibo.datasys.crawler.impl.work.SaveRuleApplyWork;
import com.weibo.datasys.crawler.impl.work.SaveWork;
import com.weibo.datasys.crawler.impl.work.SeedProvideWork;


public class WorkManager {

	private static Logger logger = LoggerFactory.getLogger(WorkManager.class);

	private static Map<Thread, BaseWork> workThreads = new LinkedHashMap<Thread, BaseWork>();

	private static Set<String> initedTypes = new HashSet<String>();

	/**
	 * 初始化
	 * 
	 * @throws TaskException
	 */
	public static void init() throws TaskException {
		try {
			if (workThreads.size() == 0) {
				initSeedProvideWork();
				initRefreshTaskWork();
				if (Main.getSystemName().equalsIgnoreCase("crawler")) {
					initInfoCollectWork();
					initFetchWork();
					initParseWork();
					initSaveWork();
					initDynSaveWorkMonitor();
					initSpeedMonitorWork();
					initRegisterWork();
				} else {
					initReloadTaskSeedWork();
				}
			} else {
				logger
						.warn("[initWorkManager] - already init. stop all works first.");
			}
		} catch (Exception e) {
			throw new TaskException("init work threads error.", e);
		}
	}

	private static void initReloadTaskSeedWork() {
		BaseWork worker = new ReloadTaskSeedWork();
		Thread work = new Thread(worker, "work-reloadTaskSeed");
		work.start();
		workThreads.put(work, worker);
	}

	public static void stopAllWorks() {
		logger.info("[stopAllWorks] - start.");
		for (Entry<Thread, BaseWork> entry : workThreads.entrySet()) {
			entry.getValue().stopWork();
			while (entry.getKey().isAlive()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
		}
		logger.info("[stopAllWorks] - end.");
	}

	public static Collection<Thread> getWorkThreads() {
		return workThreads.keySet();
	}

	private static void initSeedProvideWork() throws Exception {
		BaseWork worker = new SeedProvideWork();
		Thread work = new Thread(worker, "work-seedProvider");
		work.start();
		workThreads.put(work, worker);
	}

	private static void initFetchWork() {
		int threadNum = ConfigFactory.getInt("workThreads.fetcher", 1);
		for (int i = 0; i < threadNum; i++) {
			BaseWork worker = new FetchWork();
			Thread work = new Thread(worker, "work-fetch-" + i);
			work.start();
			workThreads.put(work, worker);
		}
	}

	private static void initParseWork() {
		int threadNum = ConfigFactory.getInt("workThreads.parser", 1);
		for (int i = 0; i < threadNum; i++) {
			BaseWork worker = new ParseWork();
			Thread work = new Thread(worker, "work-parse-" + i);
			work.start();
			workThreads.put(work, worker);
		}
	}

	private static void initSaveWork() {
		BaseWork worker;
		Thread work;
		// 基础savework
		int threadNum = ConfigFactory.getInt("workThreads.saver.base", 1);
		for (int i = 0; i < threadNum; i++) {
			worker = new SaveWork();
			work = new Thread(worker, "work-saver-" + i);
			work.start();
			workThreads.put(work, worker);
		}
		// 保存当前链接
		threadNum = ConfigFactory.getInt("workThreads.saver.link", 1);
		for (int i = 0; i < threadNum; i++) {
			worker = new SaveRuleApplyWork(QueueType.SAVE_LINK.name());
			work = new Thread(worker, "work-save-link-" + i);
			work.start();
			workThreads.put(work, worker);
		}
		worker = new SaveLinkBatchWork(QueueType.SAVE_LINK_BATCH.name());
		work = new Thread(worker, "work-save-link-batch");
		work.start();
		workThreads.put(work, worker);
		//保存图片链接
		threadNum = ConfigFactory.getInt("workThreads.saver.imglink", 1);
		for (int i = 0; i < threadNum; i++) {
			worker = new SaveRuleApplyWork(QueueType.SAVE_IMGLINK.name());
			work = new Thread(worker, "work-save-imglink-" + i);
			work.start();
			workThreads.put(work, worker);
		}
		worker = new SaveOutLinkBatchWork(QueueType.SAVE_IMGLINK_BATCH.name());
		work = new Thread(worker, "work-save-imglink-batch");
		work.start();
		workThreads.put(work, worker);
		worker = new SaveOutLinkInstantWork(QueueType.SAVE_IMGLINK_INSTANT
				.name());
		work = new Thread(worker, "work-save-imglink-instant");
		work.start();
		workThreads.put(work, worker);
		// 保存外链
		threadNum = ConfigFactory.getInt("workThreads.saver.outlink", 1);
		for (int i = 0; i < threadNum; i++) {
			worker = new SaveRuleApplyWork(QueueType.DEDUP_OUTLINK.name());
			work = new Thread(worker, "work-dedup-outlink-" + i);
			work.start();
			workThreads.put(work, worker);
		}
		worker = new SaveOutLinkBatchWork(QueueType.SAVE_OUTLINK_BATCH.name());
		work = new Thread(worker, "work-save-outlink-batch");
		work.start();
		workThreads.put(work, worker);
		worker = new SaveOutLinkInstantWork(QueueType.SAVE_OUTLINK_INSTANT
				.name());
		work = new Thread(worker, "work-save-outlink-instant");
		work.start();
		workThreads.put(work, worker);
		// 保存页面
		threadNum = ConfigFactory.getInt("workThreads.saver.page", 1);
		for (int i = 0; i < threadNum; i++) {
			worker = new SaveRuleApplyWork(QueueType.SAVE_PAGE.name());
			work = new Thread(worker, "work-save-page-" + i);
			work.start();
			workThreads.put(work, worker);
		}
		worker = new SavePageBatchWork(QueueType.SAVE_PAGE_BATCH.name());
		work = new Thread(worker, "work-save-page-batch");
		work.start();
		workThreads.put(work, worker);
	}

	private static void initDynSaveWorkMonitor() {
		BaseWork worker = new BaseWork() {
			@Override
			protected void doWork() {
				for (String type : QueueManager.getDynQueueTypes()) {
					if (initedTypes.add(type)) {
						int threadNum = ConfigFactory.getInt(
								"workThreads.saver.base", 1);
						for (int i = 0; i < threadNum; i++) {
							BaseWork worker = new SaveRuleApplyWork(type);
							Thread work = new Thread(worker, "work-save-"
									+ type + "-" + i);
							work.start();
							workThreads.put(work, worker);
						}
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		};
		Thread work = new Thread(worker, "work-dynSaveMonitor");
		work.start();
		workThreads.put(work, worker);
	}

	private static void initRefreshTaskWork() {
		BaseWork worker = new RefreshTaskWork();
		Thread work = new Thread(worker, "work-refreshTask");
		work.start();
		workThreads.put(work, worker);
	}

	private static void initInfoCollectWork() {
		BaseWork worker = new AsyncRespCollectWork();
		Thread work = new Thread(worker, "work-infoCollect");
		work.start();
		workThreads.put(work, worker);
	}

	private static void initSpeedMonitorWork() {
		BaseWork worker = new FetchSpeedMonitorWork();
		Thread work = new Thread(worker, "work-speedMonitor");
		work.start();
		workThreads.put(work, worker);

	}

	private static void initRegisterWork() {
		BaseWork worker = new RegisterCrawlerWork();
		Thread work = new Thread(worker, "work-registerCrawler");
		work.start();
		workThreads.put(work, worker);
	}

}
