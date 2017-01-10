/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.save;

import org.apache.commons.lang.ObjectUtils.Null;

import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.LinkDataFactory;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;

/**
 * 
 * linkdb存储规则实现类
 * 
 */
public class LinkSaveRule extends AbstractSaveRule {

	/**
	 * @param task
	 */
	public LinkSaveRule(Task task) {
		super(task);
		this.type = QueueType.SAVE_LINK.name();
	}

	@Override
	public Null apply(ParseInfo parseInfo) {
		CrawlInfo crawlInfo = parseInfo.getThisCrawlInfo();
		SeedData seedData = crawlInfo.getSeedData();
		Task task = crawlInfo.getValidTask();
		if (task == null) {
			return null;
		}
		boolean isNeedToSaveLink = false;
		boolean isNeedToSaveSeed = false;

		LinkData linkData = null;
		// 判断是否更新抓取
		boolean isUpdate = StringUtils.parseBoolean(
				seedData.getExtendField("isupdate"), false);
		if (!isUpdate) {
			// 非更新抓取，检查当前种子的link是否已在linkdb中
			linkData = task.getDeduplicator().getExistLink(seedData);
			// 非更新抓取，link是首次爬取，更新种子状态到种子库中
			isNeedToSaveSeed = true;
		}
		if (linkData == null) {
			// link不在linkdb中，或是更新抓取，构造与seed对应的link
			linkData = LinkDataFactory.buildFromSeedData(seedData);
			linkData.setState(0);
			isNeedToSaveLink = true;
		}

		// 当前种子是死链，设置link为死链
		if (seedData.getState() == -404) {
			linkData.setState(-404);
			isNeedToSaveLink = true;
		}

		if (isNeedToSaveLink || isNeedToSaveSeed) {
			Object[] saveDatas = new Object[2];
			// 有需要才保存link
			if (isNeedToSaveLink) {
				// 设置爬取时间为当前
				linkData.setFetchTime(System.currentTimeMillis());
				saveDatas[0] = linkData;
				countSaveResult("Link", 2);
			}
			// 有需要才保存seed
			if (isNeedToSaveSeed) {
				saveDatas[1] = seedData;
				countSaveResult("Seed", 2);
			}
			QueueManager.put(QueueType.SAVE_LINK_BATCH.name(),
					task.getTaskId(), saveDatas);
		}

		return null;
	}

}
