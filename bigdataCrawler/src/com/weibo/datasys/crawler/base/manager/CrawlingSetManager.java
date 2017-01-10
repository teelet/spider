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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.weibo.datasys.crawler.base.entity.SeedData;

/**
 * 
 * 爬取中种子集管理
 * 
 */
public class CrawlingSetManager {

	private static Map<String, Map<SeedData, String>> crawlingSet = new ConcurrentHashMap<String, Map<SeedData, String>>();

	/**
	 * 处理多个任务的爬取中种子
	 * 
	 * @param taskSeedsMap
	 * @author zouyandi
	 */
	public static void processCrawlingSeeds(
			Map<String, Set<SeedData>> taskSeedsMap) {
		for (Entry<String, Set<SeedData>> entry : taskSeedsMap.entrySet()) {
			processCrawlingSeeds(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 
	 * 处理指定任务的爬取中种子
	 * 
	 * @param seedDatas
	 * @param taskId
	 */
	public static void processCrawlingSeeds(String taskId,
			Collection<SeedData> seedDatas) {
		if (TaskManager.getTask(taskId).isRunning()) {
			Map<SeedData, String> taskCrawlingSet = getTaskSet(taskId);
			// 从爬取集中移除已爬取完种子
			taskCrawlingSet.keySet().retainAll(seedDatas);
			// 从待爬取集中移除正在爬取的种子
			Iterator<SeedData> iterator = seedDatas.iterator();
			while (iterator.hasNext()) {
				SeedData seedData = iterator.next();
				if (null != taskCrawlingSet.put(seedData, seedData.getUrlId())) {
					// 种子已在爬取中，移除之
					iterator.remove();
				}
			}
		}
	}

	/**
	 * 
	 * 移除指定任务下的所有爬取中种子
	 * 
	 * @param taskId
	 */
	public static void removeCrawlingSeedsOfTask(String taskId) {
		Map<SeedData, String> set = crawlingSet.remove(taskId);
		if (set != null) {
			set.clear();
		}
	}

	private static Map<SeedData, String> getTaskSet(String taskId) {
		Map<SeedData, String> set = crawlingSet.get(taskId);
		if (set == null) {
			set = new ConcurrentHashMap<SeedData, String>();
			crawlingSet.put(taskId, set);
		}
		return set;
	}

	/**
	 * @return 爬取集大小
	 */
	public static int getCrawlingSetSize() {
		int size = 0;
		for (Map<SeedData, String> set : crawlingSet.values()) {
			size += set.size();
		}
		return size;
	}

}
