/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.thread;

import java.util.HashSet;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.parser.html.data.PageData;

public class ThreadManager {
	public static int readThreadNum;
	public static int workThreadNum;
	public static int insertThreadNum;
	public static int updateThreadNum;
	public static int writeThreadNum;
	public static int parseType;
	public static int workTimeOut;
	public static HashSet<PageData> readDataList;

	public static void init() {
		readDataList = new HashSet<PageData>();
		readThreadNum = ConfigFactory.getInt("thread.readThreadNum", 1);
		workThreadNum = ConfigFactory.getInt("thread.workThreadNum", 1);
		writeThreadNum = ConfigFactory.getInt("thread.writeThreadNum", 1);
		insertThreadNum = 1;
		updateThreadNum = 1;
		parseType = ConfigFactory.getInt("thread.parseType", 0);
		workTimeOut = ConfigFactory.getInt("thread.workTimeOut", 3);
	}

	public static boolean alive() {
		boolean result = true;
		if (readThreadNum <= 0 && workThreadNum <= 0 && insertThreadNum <= 0
				&& updateThreadNum <= 0) {
			result = false;
		}
		return result;
	}
}
