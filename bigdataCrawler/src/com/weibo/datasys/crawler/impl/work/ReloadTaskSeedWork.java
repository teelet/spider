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

import com.weibo.datasys.crawler.base.dispatcher.Dispatcher;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.work.BaseWork;

public class ReloadTaskSeedWork extends BaseWork {

	private static Logger logger = LoggerFactory
			.getLogger(ReloadTaskSeedWork.class);

	@Override
	protected void doWork() {
		try {
			List<Task> tasks = TaskManager.getRunningTasks();
			for (Task task : tasks) {
				if (System.currentTimeMillis() - task.getStartTime() >= task
						.getTaskCycle()) {
					logger.info("[ReloadTaskSeed] - start. task={}", task);
					Dispatcher.reloadTaskSeeds(task.getTaskId());
					logger.info("[ReloadTaskSeed] - done. task={}", task);
				}
			}
			Thread.sleep(10000);
		} catch (Exception e) {
			logger.error("[RestartTaskError] - ", e);
		}
	}
}
