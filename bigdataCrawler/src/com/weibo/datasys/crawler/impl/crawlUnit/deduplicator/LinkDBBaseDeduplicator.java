/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.deduplicator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.weibo.datasys.common.util.LRUCacheUtil;
import com.weibo.datasys.crawler.base.crawlUnit.deduplicator.AbstractDeduplicator;
import com.weibo.datasys.crawler.base.dao.LinkDataDAO;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.monitor.CrawlerMonitorInfo;
import com.weibo.datasys.crawler.base.monitor.DispatcherMonitorInfo;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;


public class LinkDBBaseDeduplicator extends AbstractDeduplicator {

	private static final String STUB_VALUE = "";

	/**
	 * LRU缓存，key=urlId，value=""
	 */
	private static Map<String, String> urlLRUMap = LRUCacheUtil
			.getLRUCache(10000000);

	@Override
	public void deDuplicateSeedDatas(Collection<SeedData> seedDatas, Task task) {
		Iterator<SeedData> iterator = seedDatas.iterator();
		while (iterator.hasNext()) {
			SeedData seedData = iterator.next();
			SaveStrategy saveStrategy = task.getSaveStrategy();
			// 先判断link是否在cache中
			boolean isInCache = null != urlLRUMap.put(getLinkId(seedData),
					STUB_VALUE);
			CrawlerMonitorInfo.addCounter("cacheRead", 1);
			DispatcherMonitorInfo.addCounter("cacheRead", 1);
			if (isInCache) {
				CrawlerMonitorInfo.addCounter("cacheHit", 1);
				DispatcherMonitorInfo.addCounter("cacheHit", 1);
			}
			boolean isLinkExist = isInCache;
			// link不在cache，再判断link是否在linkdb中
			if (!isLinkExist) {
				isLinkExist = LinkDataDAO.getInstance().isExist(
						getLinkId(seedData), saveStrategy.getLinkDS(),
						saveStrategy.getLinkDB(), saveStrategy.getLinkTable());
			}
			if (isLinkExist) {
				// link已存在，从原集合remove种子
				iterator.remove();
			}
		}

	}

	@Override
	public LinkData getExistLink(SeedData seedData) {
		Task task = TaskManager.getTask(seedData.getTaskId());
		SaveStrategy saveStrategy = task.getSaveStrategy();
		LinkData linkData = LinkDataDAO.getInstance().getById(
				getLinkId(seedData), saveStrategy.getLinkDS(),
				saveStrategy.getLinkDB(), saveStrategy.getLinkTable());
		return linkData;
	}

	@Override
	public void saveLinks(Task task, Collection<LinkData> linkDatas) {
		SaveStrategy saveStrategy = task.getSaveStrategy();
		LinkDataDAO.getInstance().saveBatch(linkDatas,
				saveStrategy.getLinkDS(), saveStrategy.getLinkDB(),
				saveStrategy.getLinkTable(), true, true);
	}

}
