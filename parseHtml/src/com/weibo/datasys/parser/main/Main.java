/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */

package com.weibo.datasys.parser.main;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.parser.data.SqlData;
import com.weibo.datasys.parser.thread.ThreadManager;

public class Main {
	private static Logger logger = LoggerFactory.getLogger(Main.class);

	/**
	 * @param args
	 */
	private static String systemName = "run";
	private static String Id = "fe15b9720c958f75ed8545bd98370fca";
	
	public static String getId() {
		return Id;
	}

	public static String getSystemName() {
		return systemName;
	}

	public static void setSystemName(String systemName) {
		Main.systemName = systemName;
	}

	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		if (args.length == 2) {
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(args[0]));
			calendar.set(Calendar.MINUTE, Integer.parseInt(args[1]));
		} else {
			calendar.add(Calendar.SECOND, 1);
		}
		Date firstTime = calendar.getTime();
		long period = 4 * 3600 * 1000L;
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				try {
					ThreadParser.init();
					
					logger.info("[TimerTask] - start.");
					String tablesString = ConfigFactory.getString("jdbc.webdbTable", "");
					String[] tables = tablesString.split(";");
					if (tables.length <= 0){
						logger.error("[ERROR] " + "Not set parse tables");
						return;
					}
					for (int i = 0; i < tables.length; i++) {
						SqlData.webdbTableName = tables[i];
						logger.info("Parse table "+ SqlData.webdbTableName +" - start.");
						ThreadParser.parseOneTable();
						logger.info("Parse table "+ SqlData.webdbTableName +" - done.");
						while (ThreadManager.updateThreadNum > 0
								|| ThreadManager.readThreadNum > 0) {
							Thread.sleep(1000);
						}
						ThreadManager.init();
					}
					logger.info("[TimerTask] - end.");
				} catch (Exception e) {
					logger.error("[ERROR]" + e);
				}
			}
		}, firstTime, period);

	}
}
