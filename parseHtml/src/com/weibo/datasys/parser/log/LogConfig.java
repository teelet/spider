/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.log;

import java.util.Date;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * 
 *日志配置加载
 *
 **/
public class LogConfig {

	public static void config(String configFilePath) {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		try {
			configurator.doConfigure(configFilePath);
			System.out.println(new Date() + " - [ConfigLog] -configPath="
					+ configFilePath);
		} catch (JoranException e) {
			e.printStackTrace();
			System.out.println(new Date()
					+ " - [FatalError] - Init LogConfig Error. System Exit.");
			System.exit(1);
		}
	}
}
