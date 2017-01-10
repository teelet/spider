/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;

public class SqlData {
	private static final Logger LOG = LoggerFactory.getLogger(SqlData.class);
	private static final String ERROR_INFO = "error";
	public static String webdbTableName;
	public static String parsedbTableName;
	public static int batchNum;
	public static String parsedbDBName;

	public static void init() {
		webdbTableName = ConfigFactory.getString("jdbc.webdbTable", ERROR_INFO);
		parsedbTableName = ConfigFactory.getString("jdbc.parsedbTable",
				ERROR_INFO);
		batchNum = ConfigFactory.getInt("jdbc.batchNum", 1);
		parsedbDBName = ConfigFactory.getString("jdbc.parsedb", "webgrab");
		if (webdbTableName.equals(ERROR_INFO)
				|| parsedbTableName.equals(ERROR_INFO)) {
			LOG.error("Wrong Mysql Config!");
			System.exit(1);
		}
	}
}
