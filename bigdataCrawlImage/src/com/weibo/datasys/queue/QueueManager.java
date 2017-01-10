package com.weibo.datasys.queue;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {

	public static final int MAX_QUEUE_SIZE = 500000;

	/**
	 * 
	 * 队列类型：种子，存储
	 * 
	 * 
	 */
	public static enum QueueType {
		SEED, SAVE
	}

	private static Map<QueueType, ArrayBlockingQueue<Object>> queueMap = new ConcurrentHashMap<QueueType, ArrayBlockingQueue<Object>>();

	/**
	 * 初始化队列管理器
	 */
	public static void init() {
		QueueType[] types = QueueType.values();
		for (QueueType type : types) {
			ArrayBlockingQueue<Object> list = new ArrayBlockingQueue<Object>(MAX_QUEUE_SIZE);
			queueMap.put(type, list);
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
	public static Object poll(String type) {
		Object o = null;
		QueueType queueType = checkType(type);
		ArrayBlockingQueue<Object> queue = null;
		if (queueType != null) {
			queue = queueMap.get(queueType);
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
	public static void put(String type, Object object) {
		QueueType queueType = checkType(type);
		ArrayBlockingQueue<Object> queue = null;
		if (queueType != null) {
			queue = queueMap.get(queueType);
		}
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
	public static boolean offer(String type, Object object) {
		QueueType queueType = checkType(type);
		ArrayBlockingQueue<Object> queue = null;
		if (queueType != null) {
			queue = queueMap.get(queueType);
		}
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
	public static int getQueueSize(String type) {
		int size = -1;
		QueueType queueType = checkType(type);
		ArrayBlockingQueue<Object> queue = null;
		if (queueType != null) {
			queue = queueMap.get(queueType);
		}
		if (queue != null) {
			size = queue.size();
		}
		return size;
	}

	/**
	 * 清空队列管理器中所有队列
	 */
	public static void clear() {
		if (queueMap != null) {
			for (ArrayBlockingQueue<Object> queue : queueMap
					.values()) {
				queue.clear();
			}
			queueMap.clear();
		}
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
