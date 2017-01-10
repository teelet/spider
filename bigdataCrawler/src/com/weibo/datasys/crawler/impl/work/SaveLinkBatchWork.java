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
import com.weibo.datasys.crawler.base.dao.SeedDataDAO;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

/**
 * 批量保存当前链接的工作线程
 * 
 * @author zouyandi
 * 
 */
public class SaveLinkBatchWork extends SaveBatchBaseWork {

	private static Logger logger = LoggerFactory
			.getLogger(SaveLinkBatchWork.class);

	public SaveLinkBatchWork(String type) {
		super(type);
		saveBatch = 1000;
		maxWait = 10000;
	}

	@Override
	protected void doBatchSave(Task task) {
		StopWatch watch = new StopWatch();
		String taskId = task.getTaskId();
		SaveStrategy saveStrategy = task.getSaveStrategy();
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		List<LinkData> linkDatas = new ArrayList<LinkData>();
		for (int i = 0; i < saveBatch; i++) {
			Object[] datas = (Object[]) QueueManager.poll(type, taskId);
			if (datas == null) {
				break;
			}
			LinkData linkData = (LinkData) datas[0];
			if (linkData != null) {
				linkDatas.add(linkData);
			}
			SeedData seedData = (SeedData) datas[1];
			if (seedData != null) {
				seedDatas.add(seedData);
			}
		}
		watch.start();
		logger.debug("[SaveLinkBatch] - start. count={}",
				new Object[] { linkDatas.size() });
		// 批量保存link
		task.getDeduplicator().saveLinks(task, linkDatas);
		logger.debug("[SaveLinkBatch] - cost={} ms | count={}", new Object[] {
				watch.getElapsedTime(), linkDatas.size() });
		watch.start();
		logger.debug("[SaveSeedBatch] - start. count={}",
				new Object[] { seedDatas.size() });
		// 批量保存seed
		SeedDataDAO.getInstance().saveBatch(seedDatas,
				saveStrategy.getSeedDS(), saveStrategy.getSeedDB(),
				saveStrategy.getSeedTable() + "_read", true, true);
		logger.debug("[SaveSeedBatch] - cost={} ms | count={}", new Object[] {
				watch.getElapsedTime(), seedDatas.size() });
	}
}
