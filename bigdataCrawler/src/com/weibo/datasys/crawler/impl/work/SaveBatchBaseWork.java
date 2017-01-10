/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.work;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;

public class SaveBatchBaseWork extends SaveRuleApplyWork {

	protected int saveBatch = 10000;

	protected int maxWait = 60000;

	private Map<String, Long> waitTimeMap = new ConcurrentHashMap<String, Long>();

	public SaveBatchBaseWork(String type) {
		super(type);
	}

	@Override
	protected void doSaveWork() {
		// 获取所有运行中Tasks
		List<Task> tasks = TaskManager.getRunningTasks();
		for (Task task : tasks) {
			// 每个task都检查BATCH队列，队列累积到一定量，或一段时间没有增加，则执行批量存储
			if (task.isRunning()) {
				String taskId = task.getTaskId();
				int size = QueueManager.getQueueSize(type, taskId);
				if (size >= saveBatch) {
					// 队列大小大于等于保存批量，开始批量保存
					doBatchSave(task);
					// 更新等待时间map
					waitTimeMap.put(taskId, System.currentTimeMillis());
				} else {
					// 队列大小不满足保存批量，判断等待时间
					Long waitStartTime = waitTimeMap.get(task.getTaskId());
					if (waitStartTime == null) {
						waitStartTime = System.currentTimeMillis();
						waitTimeMap.put(taskId, waitStartTime);
					}
					long waitTime = System.currentTimeMillis() - waitStartTime;
					if (waitTime >= maxWait && size > 0) {
						doBatchSave(task);
						// 更新等待时间map
						waitTimeMap.put(taskId, System.currentTimeMillis());
					}
				}
			}// end of if task running
		}
	}

	/**
	 * 执行批量保存的具体方法，需子类实现
	 * 
	 * @param task
	 * @author zouyandi
	 */
	protected void doBatchSave(Task task) {
		//do nothing
	}
}
