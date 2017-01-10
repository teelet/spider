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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {

	public static final int MAX_QUEUE_SIZE = 500000;

	/**
	 * 
	 * 队列类型：种子，解析，存储
	 * 
	 * 
	 */
	public static enum QueueType {
		SEED, PARSE, SAVE, SAVE_LINK,SAVE_IMGLINK, SAVE_PAGE, DEDUP_OUTLINK, SAVE_LINK_BATCH, SAVE_PAGE_BATCH, SAVE_OUTLINK_BATCH, SAVE_OUTLINK_INSTANT,SAVE_IMGLINK_BATCH,SAVE_IMGLINK_INSTANT
	}

	private static Map<QueueType, Map<String, ArrayBlockingQueue<Object>>> queueMap = new ConcurrentHashMap<QueueType, Map<String, ArrayBlockingQueue<Object>>>();

	private static Set<String> dynQueueTypes = Collections
			.synchronizedSet(new LinkedHashSet<String>());

	private static Map<String, Map<String, ArrayBlockingQueue<Object>>> dynQueueMap = new ConcurrentHashMap<String, Map<String, ArrayBlockingQueue<Object>>>();

	/**
	 * 初始化队列管理器
	 */
	public static void init() {
		QueueType[] types = QueueType.values();
		for (QueueType type : types) {
			Map<String, ArrayBlockingQueue<Object>> map = new ConcurrentHashMap<String, ArrayBlockingQueue<Object>>();
			queueMap.put(type, map);
		}
	}

	/**
	 * 
	 * 获取指定类型队列里的头元素，不阻塞，nullable，优先预定义队列
	 * 
	 * @param type
	 * @param taskId
	 * @return
	 */
	public static Object poll(String type, String taskId) {
		Object o = null;
		QueueType queueType = checkType(type);
		ArrayBlockingQueue<Object> queue = null;
		if (queueType != null) {
			queue = queueMap.get(queueType).get(taskId);
		} else {
			queue = dynQueueMap.get(type).get(taskId);
		}
		if (queue != null) {
			o = queue.poll();
		}
		return o;
	}

	/**
	 * 
	 * 向指定类型队列里放入指定对象，会阻塞，优先预定义队列
	 * 
	 * @param type
	 * @param taskId
	 * @param object
	 */
	public static void put(String type, String taskId, Object object) {
		ArrayBlockingQueue<Object> queue = getTaskQueueWithCheck(type,
				taskId);
		try {
			if (queue != null) {
				queue.put(object);
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 
	 * 向指定类型动态队列放入指定对象，不阻塞，队列满则返回false，优先预定义队列
	 * 
	 * @param type
	 * @param taskId
	 * @param object
	 * @return
	 */
	public static boolean offer(String type, String taskId, Object object) {
		ArrayBlockingQueue<Object> queue = getTaskQueueWithCheck(type,
				taskId);
		boolean offerOK = false;
		if (queue != null) {
			offerOK = queue.offer(object);
		}
		return offerOK;
	}

	/**
	 * 
	 * 获取指定动态队列的大小，队列不存在返回-1，优先预定义队列
	 * 
	 * @param type
	 * @param taskId
	 * @return
	 */
	public static int getQueueSize(String type, String taskId) {
		int size = -1;
		QueueType queueType = checkType(type);
		ArrayBlockingQueue<Object> queue = null;
		if (queueType != null) {
			queue = queueMap.get(queueType).get(taskId);
		} else {
			queue = dynQueueMap.get(type).get(taskId);
		}
		if (queue != null) {
			size = queue.size();
		}
		return size;
	}

	/**
	 * 
	 * 移除并清空指定task的所有队列，包括动态队列
	 * 
	 * @param taskId
	 */
	public static void removeTaskQueue(String taskId) {
		for (QueueType type : QueueType.values()) {
			ArrayBlockingQueue<Object> queue = queueMap.get(type)
					.remove(taskId);
			if (queue != null) {
				queue.clear();
			}
		}
		Iterator<String> iterator = dynQueueTypes.iterator();
		while (iterator.hasNext()) {
			String type = iterator.next();
			ArrayBlockingQueue<Object> queue = dynQueueMap.get(type).remove(
					taskId);
			if (queue != null) {
				queue.clear();
			}
			if (dynQueueMap.get(type).size() == 0) {
				iterator.remove();
			}
		}
	}

	/**
	 * 清空队列管理器中所有队列
	 */
	public static void clear() {
		if (queueMap != null) {
			for (Map<String, ArrayBlockingQueue<Object>> map : queueMap
					.values()) {
				for (ArrayBlockingQueue<Object> queue : map.values()) {
					queue.clear();
				}
				map.clear();
			}
			queueMap.clear();
		}
		if (dynQueueMap != null) {
			for (Map<String, ArrayBlockingQueue<Object>> map : dynQueueMap
					.values()) {
				for (ArrayBlockingQueue<Object> queue : map.values()) {
					queue.clear();
				}
				map.clear();
			}
			dynQueueMap.clear();
		}
		dynQueueTypes.clear();
	}

	/**
	 * 
	 * 获取当前存在的动态队列类型集合
	 * 
	 * @return
	 */
	public static Collection<String> getDynQueueTypes() {
		return dynQueueTypes;
	}

	/**
	 * 
	 * 获取指定task的队列，队列不存在且task运行中则新建一个，优先预定义队列
	 * 
	 * @param type
	 * @param taskId
	 * @return
	 */
	private synchronized static ArrayBlockingQueue<Object> getTaskQueueWithCheck(
			String type, String taskId) {
		ArrayBlockingQueue<Object> queue = null;
		QueueType queueType = checkType(type);
		if (queueType != null) {
			queue = queueMap.get(queueType).get(taskId);
			if (queue == null && TaskManager.getTask(taskId).isRunning()) {
				queue = new ArrayBlockingQueue<Object>(MAX_QUEUE_SIZE);
				queueMap.get(queueType).put(taskId, queue);
			}
		} else {
			if (dynQueueTypes.add(type)) {
				Map<String, ArrayBlockingQueue<Object>> map = new ConcurrentHashMap<String, ArrayBlockingQueue<Object>>();
				dynQueueMap.put(type, map);
			}
			queue = dynQueueMap.get(type).get(taskId);
			if (queue == null && TaskManager.getTask(taskId).isRunning()) {
				queue = new ArrayBlockingQueue<Object>(MAX_QUEUE_SIZE);
				dynQueueMap.get(type).put(taskId, queue);
			}
		}
		return queue;
	}

	/**
	 * 检查是否是预定义队列类型，是则返回预定类型，否则返回null
	 * 
	 * @param type
	 * @return
	 */
	private static QueueType checkType(String type) {
		QueueType queueType = null;
		try {
			queueType = QueueType.valueOf(type);
		} catch (Exception e) {
		}
		return queueType;
	}
}
