/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.seedProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.crawler.base.crawlUnit.seedProvider.AbstractSeedProvider;
import com.weibo.datasys.crawler.base.dao.SeedDataDAO;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

public class ImgDBProvider extends AbstractSeedProvider{

	private static Logger logger = LoggerFactory
			.getLogger(ImgDBProvider.class);
	private static int maxFetchFailedTime = -1
			* ConfigFactory.getInt("maxFetchFailedTime", 2);

	private static final String SELECT_NORMAL_SQL = "select * from db.table where "
			+ " state=0 and taskid='{taskid}' limit {limit}";

	private static final String SELECT_FAILED_SQL = "select * from db.table where "
			+ " state<0 and state>{maxFetchFailedTime} and taskid='{taskid}' limit {limit}";

	@Override
	protected List<SeedData> getCustomSeeds(int count, Task task) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		// 获取曾经失败的种子，最多占每次取种子量的1/4
		int failedSeedCount = count / 4;
		seedDatas.addAll(getFailedSeeds(failedSeedCount, task));
		// 获取正常的种子，最少占每次取种子量的3/4
		seedDatas.addAll(getBaseSeeds(count - seedDatas.size(), task));
		// 记录本次获取到的种子数
		int seedCount = seedDatas.size();
		logger.info("[getCustomSeeds] - count={} | task={}",
				new Object[] { seedCount,task.getTaskId() });
		return seedDatas;
	}

	/**
	 * 
	 * 获取爬取失败次数在可接受范围内的种子
	 * 
	 * @param count
	 * @param task
	 * @return
	 */
	private List<SeedData> getFailedSeeds(int count, Task task) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		SaveStrategy saveStrategy = task.getSaveStrategy();
		// build sql
		String sql = SELECT_FAILED_SQL
						.replace("db", saveStrategy.getSeedDB())
						.replace("table",
									saveStrategy.getSeedTable())
						.replace("{maxFetchFailedTime}",
									"" + maxFetchFailedTime)
						.replace("{taskid}", task.getTaskId())
						.replace("{limit}", "" + count);
		// select 种子
		List<SeedData> tmpDatas = SeedDataDAO.getInstance()
							.getBySQL(sql, saveStrategy.getSeedDS());
		int getCount = tmpDatas.size();			
		seedDatas.addAll(tmpDatas);
		logger.info("[getFailedSeeds] - count={} | task={}",
				new Object[] { getCount,task.getTaskId() });
		return seedDatas;
	}

	private List<SeedData> getBaseSeeds(int count, Task task) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		SaveStrategy saveStrategy = task.getSaveStrategy();
		// build sql
		String sql = SELECT_NORMAL_SQL
						.replace("db", saveStrategy.getSeedDB())
						.replace("table",
									saveStrategy.getSeedTable() )
						.replace("{taskid}", task.getTaskId())
						.replace("{limit}", "" + count);
		// select 种子
		List<SeedData> tmpDatas = SeedDataDAO.getInstance()
							.getBySQL(sql, saveStrategy.getSeedDS());
		int getCount = tmpDatas.size();
				
		logger.info(
				"[getBaseSeeds] - count={} | task={}",
				new Object[] { getCount,task.getTaskId() });
				
		// 对种随机排序
		Collections.sort(seedDatas, new Comparator<SeedData>() {
			private Random random = new Random(System.currentTimeMillis());

			@Override
			public int compare(SeedData o1, SeedData o2) {
				return random.nextInt() % 2;
			}
		});
		return seedDatas;
	}

}
