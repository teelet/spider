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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.util.StopWatch;
import com.weibo.datasys.crawler.base.dao.PageDataDAO;
import com.weibo.datasys.crawler.base.entity.PageData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

public class SavePageBatchWork extends SaveBatchBaseWork {

	private static Logger logger = LoggerFactory
			.getLogger(SavePageBatchWork.class);

	public SavePageBatchWork(String type) {
		super(type);
		saveBatch = 1000;
		maxWait = 60000;
	}

	@Override
	protected void doBatchSave(Task task) {
		StopWatch watch = new StopWatch();
		String taskId = task.getTaskId();
		SaveStrategy saveStrategy = task.getSaveStrategy();
		List<PageData> pageDatas = new ArrayList<PageData>();
		for (int i = 0; i < saveBatch; i++) {
			PageData pageData = (PageData) QueueManager.poll(type, taskId);
			if (pageData == null) {
				break;
			}
			pageDatas.add(pageData);
		}
		watch.start();
		logger.debug("[SavePageBatch] - start. count={}", new Object[] { pageDatas
				.size() });
		// 批量保存page
		PageDataDAO.getInstance().save(pageDatas, saveStrategy.getPageDS(),
				saveStrategy.getPageDB(), saveStrategy.getPageTable());
		logger.debug("[SavePageBatch] - cost={} ms | count={}", new Object[] {
				watch.getElapsedTime(), pageDatas.size() });
	}
}
