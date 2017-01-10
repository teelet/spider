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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.SaveLockManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;
import com.weibo.datasys.crawler.base.work.BaseWork;

public class SaveRuleApplyWork extends BaseWork {

	private static Logger logger = LoggerFactory
			.getLogger(SaveRuleApplyWork.class);

	protected String type;

	public SaveRuleApplyWork(String type) {
		this.type = type;
	}

	@Override
	protected void doWork() {
		if (SaveLockManager.isSaveLocked()) {
			try {
				logger.debug("[SaveLocked] - wait 3s.");
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			return;
		}
		// 获取信号量成功才执行save操作
		if (SaveLockManager.getSaveSemaphore()) {
			// 执行具体save操作
			doSaveWork();
			// save结束释放信号量
			SaveLockManager.releaseSaveSemaphore();
		}
	}

	/**
	 * 执行具体save操作，子类需重写
	 * 
	 */
	protected void doSaveWork() {
		// 获取所有运行中Tasks
		List<Task> tasks = TaskManager.getRunningTasks();
		for (Task task : tasks) {
			// 每个task都尝试获取待应用的saveRule进行存储
			if (task.isRunning()) {
				String taskId = task.getTaskId();
				Object[] objects = (Object[]) QueueManager.poll(type, taskId);
				if (objects != null) {
					AbstractSaveRule saveRule = (AbstractSaveRule) objects[0];
					ParseInfo parseInfo = (ParseInfo) objects[1];
					saveRule.apply(parseInfo);
				}
			}
		}
	}
}
