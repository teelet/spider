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
import com.weibo.datasys.crawler.base.dao.LinkDataDAO;
import com.weibo.datasys.crawler.base.dao.SeedDataDAO;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.LinkDataFactory;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

public class SaveOutLinkBatchWork extends SaveBatchBaseWork {

	private static Logger logger = LoggerFactory
			.getLogger(SaveOutLinkBatchWork.class);

	public SaveOutLinkBatchWork(String type) {
		super(type);
		saveBatch = 1000;
		maxWait = 30000;
	}

	@Override
	protected void doBatchSave(Task task) {
		StopWatch watch = new StopWatch();
		String taskId = task.getTaskId();
		SaveStrategy saveStrategy = task.getSaveStrategy();
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		List<LinkData> linkDatas = new ArrayList<LinkData>();
		for (int i = 0; i < saveBatch; i++) {
			SeedData seedData = (SeedData) QueueManager.poll(type, taskId);
			if (seedData == null) {
				break;
			}
			LinkData linkData = LinkDataFactory.buildFromSeedData(seedData);
			seedDatas.add(seedData);
			linkDatas.add(linkData);
		}
		watch.start();
		logger.debug("[SaveOutLinkBatch] - start. count={}",
				new Object[] { linkDatas.size() });
		// 批量保存link
		LinkDataDAO.getInstance().saveBatch(linkDatas,
				saveStrategy.getLinkDS(), saveStrategy.getLinkDB(),
				saveStrategy.getLinkTable(), false, true);
		watch.stop();
		logger.debug("[SaveOutLinkBatch] - cost={} ms | count={}",
				new Object[] { watch.getElapsedTime(), linkDatas.size() });
		watch.start();
		logger.debug("[SaveOutSeedBatch] - start. count={}",
				new Object[] { seedDatas.size() });
		// 批量保存seed
		SeedDataDAO.getInstance().saveBatch(seedDatas,
				saveStrategy.getSeedDS(), saveStrategy.getSeedDB(),
				saveStrategy.getSeedTable() + "_write", false, true);
		watch.stop();
		logger.debug("[SaveOutSeedBatch] - cost={} ms | count={}",
				new Object[] { watch.getElapsedTime(), seedDatas.size() });
	}
}
