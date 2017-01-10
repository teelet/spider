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
import com.weibo.datasys.common.util.DBUtils;
import com.weibo.datasys.crawler.base.crawlUnit.seedProvider.AbstractSeedProvider;
import com.weibo.datasys.crawler.base.dao.SeedDataDAO;
import com.weibo.datasys.crawler.base.dispatcher.Dispatcher;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

/**
 * 
 * 基于Seed库的首次抓取种子提供器
 * 
 * 
 */
public class SeedDBProvider extends AbstractSeedProvider {

	private static Logger logger = LoggerFactory
			.getLogger(SeedDBProvider.class);

	private static int maxFetchFailedTime = -1
			* ConfigFactory.getInt("maxFetchFailedTime", 2);

	private static final String SELECT_NORMAL_SQL = "select * from db.table where "
			+ "depth={depth} and state=0 and level={level} and taskid='{taskid}' limit {limit}";

	private static final String SELECT_FAILED_SQL = "select * from db.table where "
			+ "depth={depth} and state<0 and state>{maxFetchFailedTime} and level={level} and taskid='{taskid}' limit {limit}";

	private int lastGetSeedCount = -1;

	private long lastSwitchTime = 0;

	private int switchTimes = 0;

	private int maxWaitUnit = 10000;

	@Override
	protected List<SeedData> getCustomSeeds(int count, Task task) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		// 获取曾经失败的种子，最多占每次取种子量的1/4
		int failedSeedCount = count / 4;
		seedDatas.addAll(getFailedSeeds(failedSeedCount, task));
		// 获取正常的种子，最少占每次取种子量的3/4
		seedDatas.addAll(getDepthBaseSeeds(count - seedDatas.size(), task));

		// 若本次从read种子库获取不到种子，则如果上次成功获取到种子，或切换时间超过阈值，就切换read|write种子库
		if (seedDatas.size() == 0) {
			// 计算最长等待时间
			int maxWaitTime = Math.min(Math.max(1, switchTimes), 7)
					* maxWaitUnit;
			if (lastGetSeedCount != 0
					|| System.currentTimeMillis() - lastSwitchTime >= maxWaitTime) {
				logger.info("[SwitchR|WSeedDB] - start.");
				Dispatcher.addSaveLock();
				switchReadWriteSeedTable(task);
				Dispatcher.releaseSaveLock();
				lastSwitchTime = System.currentTimeMillis();
				logger.info("[SwitchR|WSeedDB] - done.");
				// 记录连续切换次数
				switchTimes++;
			}
		} else {
			// 获取到种子，计数清零
			switchTimes = 0;
		}
		// 记录本次获取到的种子数
		lastGetSeedCount = seedDatas.size();

		return seedDatas;
	}

	private void switchReadWriteSeedTable(Task task) {
		SaveStrategy saveStrategy = task.getSaveStrategy();
		String dsname = saveStrategy.getSeedDS();
		String db = saveStrategy.getSeedDB();
		String readTable = saveStrategy.getSeedTable() + "_read";
		String writeTable = saveStrategy.getSeedTable() + "_write";
		//String sampleTable = saveStrategy.getSeedTable() + "_sample";
		String sampleTable = "crawler_taskseed_sample";
		// 删除readTable
		logger.info("[DropReadDB] - start. table={}", readTable);
		DBUtils.dropTable(dsname, db, readTable);
		logger.info("[DropReadDB] - done.");
		// 重命名writeTable到readTable
		logger.info("[RenameWriteDB] - start. oldTable={} | newTable={}",
				writeTable, readTable);
		DBUtils.renameTable(dsname, db, writeTable, readTable);
		logger.info("[RenameWriteDB] - done.");
		// 新建writeTable
		logger.info("[CreateNewWriteDB] - start. table={}", writeTable);
		DBUtils.createTableAsSample(dsname, db, writeTable, sampleTable, false);
		logger.info("[CreateNewWriteDB] - done.");
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
		// 种子level数
		int levelCount = task.getCrawlStrategy().getCrawlRuleCount();
		// 种子最大深度
		int maxDepth = SeedDataDAO.getInstance().checkMaxDepth(task);
		// 最大种子深度不为-1才去select
		if (maxDepth != -1) {
			// 按深度从0开始，深度浅的优先select
			int count4Depth = count;
			for (int depth = 0; depth <= maxDepth; depth++) {
				// 种子分级选取，每个级别所占比例由任务策略定义，从高级别到低级别顺序选取
				int extraSeedCount = 0;
				for (int level = levelCount - 1; level >= 0; level--) {
					// 获取调度因子
					double dispatchFactor = task.getCrawlStrategy()
							.getCrawlRule(level).getDispatchFactor();
					// 计算当前级别可获取数量
					int seedCountOfLevel = (int) (count4Depth * dispatchFactor)
							+ extraSeedCount;
					// 取整为零则忽略该级别
					if (seedCountOfLevel == 0) {
						continue;
					}
					// build sql
					String sql = SELECT_FAILED_SQL
							.replace("db", saveStrategy.getSeedDB())
							.replace("table",
									saveStrategy.getSeedTable() + "_read")
							.replace("{depth}", "" + depth)
							.replace("{level}", "" + level)
							.replace("{maxFetchFailedTime}",
									"" + maxFetchFailedTime)
							.replace("{taskid}", task.getTaskId())
							.replace("{limit}", "" + seedCountOfLevel);
					// select 种子
					List<SeedData> tmpDatas = SeedDataDAO.getInstance()
							.getBySQL(sql, saveStrategy.getSeedDS());
					int getCount = tmpDatas.size();
					// 当前level获取种子不足请求值，则下一level可获取量增加
					extraSeedCount = seedCountOfLevel - getCount;
					seedDatas.addAll(tmpDatas);
					logger.info(
							"[GetRetrySeeds] - d={} | lv={} | count={} | extra={} | task={}",
							new Object[] { depth, level, getCount,
									extraSeedCount, task.getTaskId() });
				}// end of for lv
					// 只要某一深度的种子没爬取完，就不再select下一深度的种子，保证绝对广度优先
				if (seedDatas.size() > 0) {
					break;
				}
			}// end of for depth
		}// end of max!=-1
		return seedDatas;
	}

	private List<SeedData> getDepthBaseSeeds(int count, Task task) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		SaveStrategy saveStrategy = task.getSaveStrategy();
		// 种子level数
		int levelCount = task.getCrawlStrategy().getCrawlRuleCount();
		// 种子最大深度
		int maxDepth = SeedDataDAO.getInstance().checkMaxDepth(task);
		// 最大种子深度不为-1才去select
		if (maxDepth != -1) {
			// 按深度从0开始，深度浅的优先select
			int count4Depth = count;
			for (int depth = 0; depth <= maxDepth; depth++) {
				// 种子分级选取，每个级别所占比例由任务策略定义，从高级别到低级别顺序选取
				int extraSeedCount = 0;
				for (int level = levelCount - 1; level >= 0; level--) {
					// 获取调度因子
					double dispatchFactor = task.getCrawlStrategy()
							.getCrawlRule(level).getDispatchFactor();
					// 计算当前级别可获取数量
					int seedCountOfLevel = (int) (count4Depth * dispatchFactor)
							+ extraSeedCount;
					// 取整为零则忽略该级别
					if (seedCountOfLevel == 0) {
						continue;
					}
					// build sql
					String sql = SELECT_NORMAL_SQL
							.replace("db", saveStrategy.getSeedDB())
							.replace("table",
									saveStrategy.getSeedTable() + "_read")
							.replace("{depth}", "" + depth)
							.replace("{level}", "" + level)
							.replace("{taskid}", task.getTaskId())
							.replace("{limit}", "" + seedCountOfLevel);
					// select 种子
					List<SeedData> tmpDatas = SeedDataDAO.getInstance()
							.getBySQL(sql, saveStrategy.getSeedDS());
					int getCount = tmpDatas.size();
					// 当前level获取种子不足请求值，则下一level可获取量增加
					extraSeedCount = seedCountOfLevel - getCount;
					seedDatas.addAll(tmpDatas);
					logger.info(
							"[GetSeeds] - d={} | lv={} | count={} | extra={} | task={}",
							new Object[] { depth, level, getCount,
									extraSeedCount, task.getTaskId() });
				}// end of for lv
					// 只要某一深度的种子没爬取完，就不再select下一深度的种子，保证绝对广度优先
				if (seedDatas.size() > 0) {
					break;
				}
			}// end of for depth
				// 对种子随机排序
			Collections.sort(seedDatas, new Comparator<SeedData>() {
				private Random random = new Random(System.currentTimeMillis());

				@Override
				public int compare(SeedData o1, SeedData o2) {
					return random.nextInt() % 2;
				}
			});
		}// end of max!=-1
		return seedDatas;
	}

}
