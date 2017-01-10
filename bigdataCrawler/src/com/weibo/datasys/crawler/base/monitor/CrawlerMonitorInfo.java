/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.monitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import com.weibo.datasys.common.manager.BaseEnvManager;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.ConcurrentManager;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.SaveLockManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.manager.WorkManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;

public class CrawlerMonitorInfo {

	private static final long OneDayTime = 24 * 3600 * 1000;

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static String[] counterNames = new String[] { "tryFetch",
			"failedFetch", "saveLink", "insertLink", "nochangeLink",
			"updateLink", "saveSeed", "insertSeed", "nochangeSeed",
			"updateSeed", "savePage", "insertPage", "nochangePage",
			"updatePage" };

	private static Map<String, AtomicLong> counterMap = new ConcurrentSkipListMap<String, AtomicLong>();

	public static void init() {
		for (String counterName : counterNames) {
			getCounter(counterName);
		}
		getReport();
	}

	public static void clear() {
		counterMap.clear();
	}

	/**
	 * @return 监控信息报告
	 */
	public static String getReport() {
		StringBuilder builder = new StringBuilder();
		builder.append("StartTime: ")
				.append(sdf.format(new Date(BaseEnvManager.getSystemStartTime())))
				.append("\n");
		builder.append("CurrentTime: ")
				.append(sdf.format(new Date(System.currentTimeMillis())))
				.append("\n\n");

		builder.append("CONCURRENT INFO: ");
		builder.append(ConcurrentManager.getConcurrentInfo());
		builder.append("\n\n");

		builder.append("TASKS INFO: \n");
		for (Task task : TaskManager.getExistTasks()) {
			builder.append(task);
			if (task.isRunning()) {
				builder.append(" <a href='/?cmd=stoptask&para=")
						.append(task.getTaskId()).append("'>stop task</a>");
			} else {
				builder.append(" <a href='/?cmd=runtask&para=")
						.append(task.getTaskId()).append("'>run task</a>");
			}
			builder.append("\n");
			builder.append("----TASK QUEUE INFO: \n");
			for (QueueType type : QueueType.values()) {
				builder.append("----")
						.append(type)
						.append(": ")
						.append(QueueManager.getQueueSize(type.name(),
								task.getTaskId())).append("/")
						.append(QueueManager.MAX_QUEUE_SIZE).append("\n");
			}
			builder.append("\n");
			for (String type : QueueManager.getDynQueueTypes()) {
				builder.append("----")
						.append(type)
						.append(": ")
						.append(QueueManager.getQueueSize(type,
								task.getTaskId())).append("/")
						.append(QueueManager.MAX_QUEUE_SIZE).append("\n");
			}
			builder.append("\n");
		}
		builder.append("\n");

		builder.append("FETCH SPEED: \n");
		builder.append("Upload: ").append(getCounter("upSpeed"))
				.append(" Bytes/Sec.\n");
		builder.append("Download: ")
				.append(getCounter("downSpeed").get() / 1024F)
				.append(" KBytes/Sec.\n\n");

		builder.append("SAVE LOCKS: ").append(SaveLockManager.getLockInfo())
				.append("\n\n");

		builder.append("COUNTERS: \n");
		for (Entry<String, AtomicLong> entry : counterMap.entrySet()) {
			builder.append(entry.getKey()).append(": ")
					.append(entry.getValue()).append("\n");
		}
		builder.append("\n\n");

		builder.append("AVG.COST: \n");
		for (Entry<String, AtomicLong> entry : counterMap.entrySet()) {
			String counterName = entry.getKey();
			AtomicLong counter = getCounter(counterName);
			long cost = (counter.get() == 0 ? 0
					: (getRunTime() / counter.get()));
			builder.append(counterName).append(" avg.cost: ").append(cost)
					.append(" ms\n");
		}
		builder.append("\n\n");

		builder.append("COUNT PER DAY: \n");
		for (Entry<String, AtomicLong> entry : counterMap.entrySet()) {
			String counterName = entry.getKey();
			AtomicLong counter = getCounter(counterName);
			long countPerDay = OneDayTime * counter.get() / getRunTime();
			builder.append(counterName).append(": ").append(countPerDay)
					.append(" /day\n");
		}
		builder.append("\n\n");

		builder.append("WORK THREAD INFO: \n");
		for (Thread work : WorkManager.getWorkThreads()) {
			builder.append("ThreadName: ").append(work.getName())
					.append(".  Alive: ").append(work.isAlive()).append("\n");
		}

		return builder.toString();
	}

	/**
	 * 
	 * 指定计数器增加指定值
	 * 
	 * @param counterName
	 *            counterName
	 * @param value
	 *            value
	 * @return
	 */
	public static long addCounter(String counterName, int value) {
		AtomicLong counter = getCounter(counterName);
		return counter.addAndGet(value);
	}

	/**
	 * 
	 * 获取指定名称counter，不存在则新建一个
	 * 
	 * @param counterName
	 *            counterName
	 * @return
	 */
	public static synchronized AtomicLong getCounter(String counterName) {
		AtomicLong counter = counterMap.get(counterName);
		if (counter == null) {
			counter = new AtomicLong();
			counterMap.put(counterName, counter);
		}
		return counter;
	}

	/**
	 * @return 系统已运行的时间，ms
	 */
	public static long getRunTime() {
		return System.currentTimeMillis() - BaseEnvManager.getSystemStartTime();
	}

}
