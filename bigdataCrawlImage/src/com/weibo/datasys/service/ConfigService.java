package com.weibo.datasys.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.queue.QueueManager;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class ConfigService {
	
	private static XMLConfiguration config = null;
	private static String confPath = null;
	private static String logPath = null;
	private static File confDirPath = null;

	/**
	 * 
	 * 初始化系统运行环境，包括公共初始化和其他初始化，其他初始化操作通过继承BaseEnvManager进行定义
	 * 
	 * @throws Exception
	 */
	public static void init() throws Exception {
		System.out.println(new Date() + " - [ConfigServiceInit] - Start.");
		// 获取 程序 HOME目录，如果没有指定，退出
		String home = System.getProperty("home.dir");
		if (null == home) {
			System.out.println("Please set -Dhome.dir properties.");
			throw new Exception("-Dhome.dir properties not found.");
		}
		// 配置文件和日志文件路径
		logPath = home + "/conf/logback.xml";
		confPath = home + "/conf/config.xml";
		initLog();
		initConf();
		DBManager.init();
		QueueManager.init();
		WorkManager.init();
		System.out.println(new Date() + " - [ConfigServiceInit] - End.");
	}
	
	/**
	 * 初始化日志配置
	 * @throws Exception
	 */
	private static void initLog() throws Exception {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		lc.reset();
		try {
			configurator.doConfigure(logPath);
			System.out.println(new Date() + " - [initLog] -logPath="
					+ logPath);
		} catch (JoranException e) {
			e.printStackTrace();
			System.out.println(new Date()
					+ " - [FatalError] - InitLog Error. System Exit.");
			System.exit(1);
		}
	}
	
	/**
	 * 初始化日志配置
	 */
	public static void initConf() throws Exception {
		try {
			System.out.println(new Date() + " - [initConf] - confPath= "
					+ confPath);
			config = new XMLConfiguration(confPath);
			config.setReloadingStrategy(new FileChangedReloadingStrategy());
			confDirPath = new File(confPath).getParentFile();
		} catch (ConfigurationException e) {
			System.out
					.println(new Date()
							+ " - [FatalError] - InitConf Error. System Exit.");
			System.exit(1);
		}
	}
	
	public static String getSeedDS(){
		return config.getString("seedDS");
	}
	
	public static String getSeedDB(){
		return config.getString("seedDB");
	}
	
	public static String getSeedTable(){
		return config.getString("seedTable");
	}
	
	/**
	 * @return 配置文件所在目录路径
	 */
	public static File getConfigDirPath() {
		return confDirPath;
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
