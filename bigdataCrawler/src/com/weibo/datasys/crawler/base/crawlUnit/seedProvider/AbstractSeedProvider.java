/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.crawlUnit.seedProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.IConfigurable;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.appMain.Main;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.SeedDataFactory;
import com.weibo.datasys.crawler.base.manager.CrawlingSetManager;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;

public abstract class AbstractSeedProvider implements IConfigurable {

	private static Logger logger = LoggerFactory
			.getLogger(AbstractSeedProvider.class);

	protected List<SeedData> initSeedDatas = new ArrayList<SeedData>();

	private boolean hasLoadedInitSeed = false;

	private void loadInitSeeds() {
		if (!hasLoadedInitSeed) {
			try {
				String home = System.getProperty("home.dir");
				File file = new File(home + "/conf/initSeeds.txt");
				if (file.exists() && file.isFile()) {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					String line = "";
					while (null != (line = reader.readLine())) {
						if (StringUtils.isEmptyString(line)
								|| line.startsWith("#")) {
							continue;
						}
						SeedData seedData = SeedDataFactory
								.buildFromFormatString(line);
						if (seedData != null) {
							initSeedDatas.add(seedData);
						}
					}
					reader.close();
				}
			} catch (Exception e) {
				logger.error("[loadInitSeedsError] - ", e);
			}
			hasLoadedInitSeed = true;
		}
	}

	/**
	 * 
	 * 获取一组待采集种子，首次调用返回初始化的种子(初始化种子在conf/initSeeds.txt中定义)，此后只返回子类自定义的种子
	 * 
	 * @param count
	 * @return
	 */
	public Collection<SeedData> getSeeds(int count, Task task) {
		long s = System.currentTimeMillis();
		Set<SeedData> finalSeedDatas = new HashSet<SeedData>();
		Map<String, Set<SeedData>> taskSeedsMap = new HashMap<String, Set<SeedData>>();
		int totalGetCount = 0;
		// 判断是否需要获取种子
		boolean needToGetSeeds = isNeedToGetSeeds(count);
		if (needToGetSeeds) {
			logger.info("[GetSeeds] - start. | crawlingSet={}",
					new Object[] { CrawlingSetManager.getCrawlingSetSize() });
			loadInitSeeds();
			if (initSeedDatas.size() > 0) {
				finalSeedDatas.addAll(initSeedDatas);
				initSeedDatas.clear();
			} else {
				// 获取子类自定义种子
				List<SeedData> customSeeds = getCustomSeeds(count, task);
				totalGetCount = customSeeds.size();
				if (task != null) {
					Set<SeedData> taskSeeds = new HashSet<SeedData>(customSeeds);
					taskSeedsMap.put(task.getTaskId(), taskSeeds);
				} else {
					// 没有指定获取种子的task，所以遍历所有种子，将种子按task分开，放进map里，以进行去重处理
					for (SeedData seedData : customSeeds) {
						String taskId = seedData.getTaskId();
						Set<SeedData> taskSeeds = taskSeedsMap.get(taskId);
						if (taskSeeds == null) {
							taskSeeds = new HashSet<SeedData>();
							taskSeedsMap.put(taskId, taskSeeds);
						}
						taskSeeds.add(seedData);
					}
				}
			}
			if (Main.getSystemName().equals("dispatcher")) {
				// 当进程为dispatcher时，按照task分类，移除已在爬取中的种子
				CrawlingSetManager.processCrawlingSeeds(taskSeedsMap);
			}

			// 将去重后种子放进最终返回的集合里
			for (Set<SeedData> taskSeeds : taskSeedsMap.values()) {
				finalSeedDatas.addAll(taskSeeds);
			}

			if (finalSeedDatas.size() > 0) {
				long e = System.currentTimeMillis();
				logger.info(
						"[GetSeeds] - end. cost={}ms | count=real:{}/total:{}/req:{} | crawlingSet={}",
						new Object[] { (e - s), finalSeedDatas.size(),
								totalGetCount, count,
								CrawlingSetManager.getCrawlingSetSize() });
			}
		}

		return finalSeedDatas;

	}

	/**
	 * 
	 * 由子类实现，获取一组子类定义的待采集种子
	 * 
	 * @param count
	 * @return
	 */
	protected abstract List<SeedData> getCustomSeeds(int count, Task task);

	/**
	 * 
	 * 判断是否需要获取种子，通过检查各运行中任务队列状态判断
	 * 
	 * @param count
	 * @return
	 */
	private boolean isNeedToGetSeeds(int count) {
		boolean needToGetSeeds = true;
		// 需要获取种子的队列数目
		int needFillQueueCount = 0;
		// 不需要获取种子，但是可以获取的队列数目
		int canFillQueueCount = 0;
		List<Task> tasks = TaskManager.getRunningTasks();
		for (Task task : tasks) {
			String taskId = task.getTaskId();
			int size = QueueManager.getQueueSize(QueueType.SEED.name(), taskId);
			if (size < count / 10) {
				needFillQueueCount++;
			} else {
				if (QueueManager.MAX_QUEUE_SIZE - size > count) {
					canFillQueueCount++;
				}
			}
		}
		// 有任务正在执行 && 存在需要获取的队列 && 需要获取队列数+可以获取队列数==正在运行任务数，则爬虫需要获取种子
		needToGetSeeds = tasks.size() > 0 && needFillQueueCount > 0
				&& (needFillQueueCount + canFillQueueCount) == tasks.size();
		return needToGetSeeds;
	}

	@Override
	public void configWithKeyValues(Map<String, String> paraMap) {

	}

}
