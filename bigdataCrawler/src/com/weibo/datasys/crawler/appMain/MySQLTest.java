/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.appMain;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.db.DBManager;
import com.weibo.datasys.common.util.StopWatch;
import com.weibo.datasys.crawler.base.dao.SeedDataDAO;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.factory.SeedDataFactory;

public class MySQLTest {

	private static Logger logger = LoggerFactory.getLogger(MySQLTest.class);

	public static void main(String[] args) throws Exception {
		ConfigFactory.init("conf/config.xml");
		DBManager.init();

		SeedData data = SeedDataFactory.buildBaseSeedData(
				"http://www.cpd.com.cn/epaper/rmgab/2012-09-14/02b-,8,.html",
				"task-test");
		data.setDepth(Integer.MAX_VALUE);
		SeedDataDAO.getInstance().save(data, "crawlDS180", "xgovcn",
				"taskseed_test", true, true);

		for (int i = 1; i <= 5; i++) {
			final int num = i;
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					StopWatch watch = new StopWatch();
					watch.start();
					for (int i = 0; i < 1000; i++) {
						doBatch(i, num);
					}
					logger.info("[DoAllBatch-{}] - done. cost={} ms", num,
							watch.getElapsedTime());
				}
			}, "t-" + i);
			thread.start();
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < 10000; i++) {
						doSelect(i, num);
					}
				}
			}, "tt-" + i);
			// thread.start();
		}

	}

	public static void doBatch(int batchNum, int threadNum) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		for (int i = 0; i < 1000; i++) {
			SeedData seedData = SeedDataFactory.buildBaseSeedData(
					"http://123.com" + "_" + batchNum + "_" + i, "t");
			seedData.setState(0);
			seedDatas.add(seedData);
		}
		StopWatch watch = new StopWatch();
		watch.start();
		SeedDataDAO.getInstance().saveBatch(seedDatas,
				"crawlDS181", "testdb", "taskseed_" + threadNum, true, true,
				false, true);
		logger.info("[DoBatch-{}-{}] - done. cost={} ms", new Object[] {
				threadNum, batchNum, watch.getElapsedTime() });
	}

	public static void doSelect(int batchNum, int threadNum) {
		StopWatch watch = new StopWatch();
		watch.start();
		SeedDataDAO.getInstance().getByCount(10000, "crawlDS181", "testdb",
				"taskseed_" + threadNum);
		// logger.info("[DoSelect-{}-{}] - done. cost={} ms", new Object[] {
		// threadNum, batchNum, watch.getElapsedTime() });
	}

}
