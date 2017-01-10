/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * 
 *程序通用配置加载
 *
 **/
public class ConfigFactory {

	private static final String CONFIG_FILE_DEFAULT_PATH = "./conf/config.xml";
	private static XMLConfiguration config = null;
	private static File configDirPath;

	private ConfigFactory() {
	}

	public static void init(String configFilePath) {
		// 如果未配置获取默认值
		if (configFilePath == null) {
			configFilePath = CONFIG_FILE_DEFAULT_PATH;
		}
		try {
			System.out.println(new Date() + " - [ConfigInit] - ConfigPath= "
					+ configFilePath);
			config = new XMLConfiguration(configFilePath);
			config.setReloadingStrategy(new FileChangedReloadingStrategy());
			configDirPath = new File(configFilePath).getParentFile();
		} catch (ConfigurationException e) {
			System.out
					.println(new Date()
							+ " - [FatalError] - Init ConfigFactory Error. System Exit.");
			System.exit(1);
		}
	}

	/**
	 * @return 配置文件所在目录路径
	 */
	public static File getConfigDirPath() {
		return configDirPath;
	}

	/**
	 * 获取配置的字符串值
	 * 
	 * @param configXPath
	 *            配置项路径
	 * @return
	 */
	public static String getString(String configXPath) {
		return config.getString(configXPath, null);
	}

	/**
	 * 获取配置的字符串值
	 * 
	 * @param configXPath
	 *            配置项路径
	 * @param defaultValue
	 *            配置项的默认值
	 * @return
	 */
	public static String getString(String configXPath, String defaultValue) {
		return config.getString(configXPath, defaultValue);
	}

	/**
	 * 获取配置的double数值
	 * 
	 * @param configXPath
	 *            配置项路径
	 * @param defaultValue
	 *            配置项的默认值
	 * @return
	 */
	public static double getDouble(String configXPath, double defaultValue) {
		return config.getDouble(configXPath, defaultValue);
	}

	/**
	 * 获取配置的浮点数值
	 * 
	 * @param configXPath
	 *            配置项路径
	 * @param defaultValue
	 *            配置项的默认值
	 * @return
	 */
	public static float getFloat(String configXPath, float defaultValue) {
		return config.getFloat(configXPath, defaultValue);
	}

	/**
	 * 获取配置的整型值
	 * 
	 * @param configXPath
	 *            配置项路径
	 * @param defaultValue
	 *            配置项的默认值
	 * @return
	 */
	public static int getInt(String configXPath, int defaultValue) {
		return config.getInt(configXPath, defaultValue);
	}

	/**
	 * 获取配置的长整型值
	 * 
	 * @param configXPath
	 *            配置项路径
	 * @param defaultValue
	 *            配置项的默认值
	 * @return
	 */
	public static long getLong(String configXPath, long defaultValue) {
		return config.getLong(configXPath, defaultValue);
	}

	/**
	 * 获取配置的boolean值
	 * 
	 * @param configXPath
	 *            配置项路径
	 * @param defaultValue
	 *            配置项的默认值
	 * @return
	 */
	public static boolean getBoolean(String configXPath, boolean defaultValue) {
		return config.getBoolean(configXPath, defaultValue);
	}

	/**
	 * 获取配置的List值
	 * 
	 * @param configXPath
	 *            配置项路径
	 * @return
	 */
	public static List<String> getList(String configXPath) {
		List<String> values = new ArrayList<String>();
		for (Object o : config.getList(configXPath)) {
			values.add(o.toString());
		}
		return values;
	}

	/**
	 * 
	 * 设置String配置项
	 * 
	 * @param configXPath
	 * @param value
	 */
	public static void setString(String configXPath, String value) {
		config.setProperty(configXPath, value);
	}

	/**
	 * 
	 * 设置int配置项
	 * 
	 * @param configXPath
	 * @param value
	 */
	public static void setInt(String configXPath, int value) {
		config.setProperty(configXPath, value);
	}

	/**
	 * 
	 * 设置Float配置项
	 * 
	 * @param configXPath
	 * @param value
	 */
	public static void setFloat(String configXPath, float value) {
		config.setProperty(configXPath, value);
	}

	/**
	 * 
	 * 设置Long配置项
	 * 
	 * @param configXPath
	 * @param value
	 */
	public static void setLong(String configXPath, long value) {
		config.setProperty(configXPath, value);
	}

	/**
	 * 
	 * 设置Boolean配置项
	 * 
	 * @param configXPath
	 * @param value
	 */
	public static void setBoolean(String configXPath, boolean value) {
		config.setProperty(configXPath, value);
	}

	/**
	 * 删除指定配置项
	 * 
	 * @param key
	 */
	public static void remove(String key) {
		config.clearTree(key);
	}

	/**
	 * 保存配置项
	 */
	public static void save() {
		try {
			config.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();

		}
	}

}
