/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.seedProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.util.StopWatch;
import com.weibo.datasys.crawler.base.crawlUnit.seedProvider.AbstractSeedProvider;
import com.weibo.datasys.crawler.base.dao.LinkDataDAO;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.SeedDataFactory;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

/**
 * 
 * 基于LinkDB的更新抓取种子提供器
 * 
 */
public class LinkDBProvider extends AbstractSeedProvider {

	private static Logger logger = LoggerFactory
			.getLogger(LinkDBProvider.class);

	private static final String SQL = "select * from db.table where depth={depth} and state=1 limit {limit}";

	@Override
	protected List<SeedData> getCustomSeeds(int count, Task task) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		seedDatas = getUpdateSeeds(count, task);
		return seedDatas;
	}

	private List<SeedData> getUpdateSeeds(int count, Task task) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		SaveStrategy saveStrategy = task.getSaveStrategy();
		// 种子最大深度
		int maxDepth = LinkDataDAO.getInstance().checkMaxDepth(task);
		// 最大种子深度不为-1才去select
		if (maxDepth != -1) {
			// 按深度从0开始，深度浅的优先select
			int count4Depth = count;
			for (int depth = 0; depth <= maxDepth; depth++) {
				String sql = SQL.replace("db", saveStrategy.getLinkDB())
						.replace("table", saveStrategy.getLinkTable()).replace(
								"{depth}", "" + depth).replace("{limit}",
								"" + count4Depth);
				// select 种子
				StopWatch watch = new StopWatch();
				watch.start();
				List<LinkData> linkDatas = LinkDataDAO.getInstance().getBySQL(
						sql, saveStrategy.getLinkDS());
				logger
						.info(
								"[GetSeeds] - cost={} ms | d={} | count={} | c4d={} | task={}",
								new Object[] { watch.getElapsedTime(), depth,
										linkDatas.size(), count4Depth,
										task.getTaskId() });
				for (LinkData linkData : linkDatas) {
					SeedData seedData = SeedDataFactory.buildFromLinkData(
							linkData, task);
					seedData.setExtendField("isupdate", true);
					seedDatas.add(seedData);
				}
				// 只要某一深度的种子没爬取完，就不再select下一深度的种子，保证绝对广度优先
				if (seedDatas.size() > 0) {
					break;
				}
			}// end of for depth
			// 对种子随机排序
			Collections.sort(seedDatas, new Comparator<SeedData>() {
				private Random random = new Random(System.currentTimeMillis());

				@Override
				public int compare(SeedData o1, SeedData o2) {
					return random.nextInt();
				}
			});
		}// end of max!=-1
		return seedDatas;
	}
}
