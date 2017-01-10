/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.factory;
import com.weibo.datasys.common.data.InvalidFomatException;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.TaskManager;

/**
 * 
 * LinkData构造工厂类
 * 
 * 
 */
public class LinkDataFactory {

	private LinkDataFactory() {
	}

	/**
	 * 
	 * 构造基于seedData的LinkData，fetchtime设为默认值，其余属性与seedData一致
	 * 
	 * @param seedData
	 * @return
	 */
	public static LinkData buildFromSeedData(SeedData seedData) {
		LinkData linkData = new LinkData();
		Task task = TaskManager.getTask(seedData.getTaskId());
		String id = task.getDeduplicator().getLinkId(seedData);
		linkData.setId(id);
		linkData.setUrl(seedData.getUrl());
		linkData.setTaskId(seedData.getTaskId());
		linkData.setNormalizeUrl(seedData.getNormalizeUrl());
		linkData.setState(seedData.getState());
		linkData.setDepth(seedData.getDepth());
		linkData.setFetchTime(0);
		try {
			// 保持seed和link的扩展字段一致
			linkData.setExtendMap(seedData.getExtendString());
		} catch (InvalidFomatException e) {
		}
		return linkData;
	}

}
