package com.weibo.datasys.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.work.BaseWork;
import com.weibo.datasys.work.FetchWork;
import com.weibo.datasys.work.SaveWork;
import com.weibo.datasys.work.SeedProvideWork;


public class WorkManager {

	private static Logger logger = LoggerFactory.getLogger(WorkManager.class);

	private static Map<Thread, BaseWork> workThreads = new LinkedHashMap<Thread, BaseWork>();

	/**
	 * 初始化
	 * 
	 * @throws TaskException
	 */
	public static void init() throws Exception {
		try {
			if (workThreads.size() == 0) {
				initSeedProvideWork();
				initFetchWork();
				initSaveWork();
			} 
		} catch (Exception e) {
			throw new Exception("init work threads error.", e);
		}
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
		int threadNum = ConfigService.getInt("workThreads.fetcher", 1);
		for (int i = 0; i < threadNum; i++) {
			BaseWork worker = new FetchWork();
			Thread work = new Thread(worker, "work-fetch-" + i);
			work.start();
			workThreads.put(work, worker);
		}
	}

	private static void initSaveWork() {
		int threadNum = ConfigService.getInt("workThreads.saver", 1);
		for (int i = 0; i < threadNum; i++) {
			BaseWork worker = new SaveWork();
			Thread work = new Thread(worker, "work-saver-" + i);
			work.start();
			workThreads.put(work, worker);
		}

	}
}

