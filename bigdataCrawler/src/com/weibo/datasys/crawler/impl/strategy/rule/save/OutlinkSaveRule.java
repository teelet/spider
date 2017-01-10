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

import java.util.Set;

import org.apache.commons.lang.ObjectUtils.Null;

import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;

/**
 * 
 * 外链seed存储规则实现类
 * 
 */
public class OutlinkSaveRule extends AbstractSaveRule {

	/**
	 * @param task
	 */
	public OutlinkSaveRule(Task task) {
		super(task);
		this.type = QueueType.DEDUP_OUTLINK.name();
	}

	@Override
	public Null apply(ParseInfo parseInfo) {
		Set<SeedData> outlinks = parseInfo.getOutlinks();
		if (outlinks == null || outlinks.size() == 0) {
			return null;
		}
		// 执行去重
		task.getDeduplicator().deDuplicateSeedDatas(outlinks, task);

		// 去重完毕放入保存队列
		for (SeedData seedData : outlinks) {
			if (seedData.isInstant()) {
				// 实时外链放入实时队列
				QueueManager.put(QueueType.SAVE_OUTLINK_INSTANT.name(),
						seedData.getTaskId(), seedData);
			} else {
				// 非实时外链放入批量保存队列
				QueueManager.put(QueueType.SAVE_OUTLINK_BATCH.name(),
						seedData.getTaskId(), seedData);
			}
			countSaveResult("Seed", 1);
			countSaveResult("Link", 1);
		}
		return null;
	}

}
