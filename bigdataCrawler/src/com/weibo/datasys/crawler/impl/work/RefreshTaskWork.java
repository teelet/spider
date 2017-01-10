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

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.crawler.base.manager.TaskManager;
import com.weibo.datasys.crawler.base.work.BaseWork;

public class RefreshTaskWork extends BaseWork {

	private static final int refreshInterval = ConfigFactory.getInt(
			"taskManager.refreshTaskInterval", 300000);

	@Override
	protected void doWork() {
		try {
			Thread.sleep(refreshInterval);
			TaskManager.refresh();
		} catch (Exception e) {
		}
	}
}
