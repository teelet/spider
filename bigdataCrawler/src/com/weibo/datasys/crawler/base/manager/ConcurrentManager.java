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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * 并发管理器
 * 
 */
public class ConcurrentManager {

	private static Map<String, Map<String, AtomicInteger>> hostConcurMap = new ConcurrentHashMap<String, Map<String, AtomicInteger>>();

	public synchronized static AtomicInteger getConcurrent(String taskId, String host) {
		Map<String, AtomicInteger> concurMap = hostConcurMap.get(taskId);
		if (concurMap == null) {
			concurMap = new ConcurrentHashMap<String, AtomicInteger>();
			hostConcurMap.put(taskId, concurMap);
		}
		AtomicInteger concurrent = concurMap.get(host);
		if (concurrent == null) {
			concurrent = new AtomicInteger();
			concurMap.put(host, concurrent);
		}
		return concurrent;
	}

	public static void clearTaskConcurrent(String taskId) {
		Map<String, AtomicInteger> concurMap = hostConcurMap.remove(taskId);
		if (concurMap != null) {
			concurMap.clear();
		}
	}

	public static void clearAllConcurrent() {
		for (String taskId : hostConcurMap.keySet()) {
			clearTaskConcurrent(taskId);
		}
		hostConcurMap.clear();
	}

	public static int getTotalConcurrent() {
		int total = 0;
		for (Map<String, AtomicInteger> map : hostConcurMap.values()) {
			for (AtomicInteger counter : map.values()) {
				total += counter.get();
			}
		}
		return total;
	}

	public static String getConcurrentInfo() {
		StringBuilder builder = new StringBuilder().append("\n");
		int total = 0;
		int crawlingHostCount = 0;
		for (Map<String, AtomicInteger> map : hostConcurMap.values()) {
			for (Entry<String, AtomicInteger> entry : map.entrySet()) {
				int count = entry.getValue().get();
				if (count != 0) {
					total += count;
					crawlingHostCount++;
					builder.append("{").append(entry.getKey()).append("=")
							.append(count).append("} , ");
				}
			}
		}
		builder.append("\nTotalConcurrent: ").append(total).append(" in ")
				.append(crawlingHostCount).append(" hosts.");
		return builder.toString();
	}

}
