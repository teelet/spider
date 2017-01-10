/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.appMain;

import java.net.Inet4Address;
import java.util.Arrays;
import java.util.Date;

import com.weibo.datasys.crawler.base.manager.EnvironmentManager;

public class Main {

	private static String systemName = "crawler";

	private static String systemIP;

	private static final long startTime = System.currentTimeMillis();

	private static boolean isTest = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new Date() + " - [SystemStarting] - args="
				+ Arrays.toString(args));
		if (args.length >= 1) {
			systemName = args[0];
		}
		try {
			if (args.length == 2) {
				systemIP = args[1];
			} else {
				systemIP = Inet4Address.getLocalHost().getHostAddress();
				isTest = true;
			}
			new EnvironmentManager().init();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(new Date() + " - [InitEnvError] - System exit.");
			System.exit(-1);
		}
	}

	/**
	 * @return the starttime
	 */
	public static long getStartTime() {
		return startTime;
	}

	/**
	 * @return the isTest
	 */
	public static boolean isTest() {
		return isTest;
	}

	/**
	 * @return the systemName
	 */
	public static String getSystemName() {
		return systemName;
	}

	/**
	 * @return the systemIP
	 */
	public static String getSystemIP() {
		return systemIP;
	}

}
