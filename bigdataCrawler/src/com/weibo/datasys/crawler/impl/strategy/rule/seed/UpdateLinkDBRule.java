/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.seed;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.base.strategy.rule.seed.AbstractSeedGenerateRule;
import com.weibo.datasys.crawler.base.dao.LinkDataDAO;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

public class UpdateLinkDBRule extends AbstractSeedGenerateRule {

	private static Logger logger = LoggerFactory
			.getLogger(UpdateLinkDBRule.class);

	/**
	 * @param task
	 */
	public UpdateLinkDBRule(Task task) {
		super(task);
	}

	@Override
	public List<SeedData> apply(Null in) {
		long s = System.currentTimeMillis();
		logger.info("[UpdateLinkDBRule] - set state to 1. start.");
		SaveStrategy saveStrategy = task.getSaveStrategy();
		String sql = "update " + saveStrategy.getLinkDB() + "."
				+ saveStrategy.getLinkTable() + " set state=1 where state=0";
		int updateCount = LinkDataDAO.getInstance().modifyBySQL(sql,
				saveStrategy.getLinkDS());
		long e = System.currentTimeMillis();
		logger
				.info(
						"[UpdateLinkDBRule] - set state to 1. done. cost={} ms. count={}",
						(e - s), updateCount);
		return Collections.emptyList();
	}
}
