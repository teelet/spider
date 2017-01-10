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

import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.work.BaseWork;

public class SaveWork extends BaseWork {

	@Override
	protected void doWork() {
		// 获取所有运行中Tasks
		List<Task> tasks = TaskManager.getRunningTasks();
		for (Task task : tasks) {
			// 每个task都尝试获取已解析信息进行存储
			if (task.isRunning()) {
				String taskId = task.getTaskId();
				ParseInfo parseInfo = (ParseInfo) QueueManager.poll(
						QueueType.SAVE.name(), taskId);
				if (parseInfo != null) {
					// 成功获取下载信息，调用task对应Saver进行存储
					task.getSaver().save(parseInfo);
				}
			}
		}
	}

}
