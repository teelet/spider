/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.model.CommonData;
import com.weibo.datasys.model.DBException;
import com.weibo.datasys.util.StringUtils;


/**
 * 
 *SQL数据库资源管理类
 *
 **/
public class DBManager {

	private static Logger logger = LoggerFactory.getLogger(DBManager.class);

	/**
	 * 默认数据源配置
	 */
	private static Map<String, CommonData> defaultDSConfigMap = new ConcurrentHashMap<String, CommonData>();

	/**
	 * 多数据源连接池
	 */
	private static Map<String, BasicDataSource> dsMap;

	/**
	 * 初始化DBManager，根据配置，初始化所有数据源
	 */
	public static void init() {
		if (dsMap == null || dsMap.isEmpty()) {
			logger.info("[InitDBManager] - Start.");
			dsMap = new HashMap<String, BasicDataSource>();
			// 从配置文件获取默认数据源配置
			getDefaultDSConfig();
			for (CommonData configData : defaultDSConfigMap.values()) {
				try {
					initDataSource(configData);
				} catch (Exception e) {
					logger.error("[InitDefaultDataSourceError] - ", e);
				}
			}
			logger.info("[InitDBManager] - Done.");
		} else {
			// 已初始化则输出警告
			logger.info("[InitDBManager] - Already initialized.");
		}
	}

	/**
	 * 
	 * 初始化指定名称的数据源
	 * 
	 * @param dsname
	 *            要初始化的数据源名称
	 */
	public static void initDataSource(String dsname) throws DBException {
		try {
			// 从默认配置获取配置数据
			CommonData configData = defaultDSConfigMap.get(dsname);
			initDataSource(configData);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	/**
	 * 
	 * 使用指定配置数据初始化数据源
	 * 
	 * @param configData
	 */
	private static void initDataSource(CommonData configData) {
		String dsname = configData.getBaseField("dsname");

		BasicDataSource dataSource = new BasicDataSource();
		// 驱动类名
		dataSource.setDriverClassName(configData
				.getBaseField("driverClassName"));
		// 数据库连接地址
		dataSource.setUrl(configData.getBaseField("connectURL"));
		// 用户名密码
		dataSource.setUsername(configData.getBaseField("username"));
		dataSource.setPassword(configData.getBaseField("password"));
		// 初始连接数
		dataSource.setInitialSize(StringUtils.parseInt(
				configData.getBaseField("initialSize"), 1));
		// 最小空闲连接
		dataSource.setMinIdle(StringUtils.parseInt(
				configData.getBaseField("minIdle"), 1));
		// 最大空闲连接
		dataSource.setMaxIdle(StringUtils.parseInt(
				configData.getBaseField("maxIdle"), 1));
		// 最大活动连接数
		dataSource.setMaxActive(StringUtils.parseInt(
				configData.getBaseField("maxActive"), 1));
		// 没有可用连接时,抛出异常前的最大等待时间(ms)
		dataSource.setMaxWait(StringUtils.parseInt(
				configData.getBaseField("maxWait"), 1000));

		// 连接空闲时验证连接是否有效
		dataSource.setTestWhileIdle(true);
		// 验证sql语句
		dataSource.setValidationQuery("select 'test'");
		// 验证超时时间
		dataSource.setValidationQueryTimeout(5000);
		// 回收无效连接间隔
		dataSource.setTimeBetweenEvictionRunsMillis(3600000);
		// 连接被回收前，最小空闲时间
		dataSource.setMinEvictableIdleTimeMillis(3600000);

		dsMap.put(dsname, dataSource);

		logger.info("[InitDataSourceOK] - dsname={}", dsname);
	}

	/**
	 * 
	 * 移除指定名称的数据源，数据源将被关闭，且移除其配置数据缓存，移除不存在的数据源不会进行任何操作
	 * 
	 * @param dsname
	 */
	public static void removeDataSource(String dsname) {
		try {
			BasicDataSource ds = dsMap.remove(dsname);
			if (ds != null) {
				ds.close();
				logger.info("[RemoveDataSourceOK] - dsname={}", dsname);
			}
		} catch (Exception e) {
			logger.error("[RemoveDataSourceError] - ", e);
		}
	}

	/**
	 * 
	 * 关闭指定名称的数据源，若需要重新打开可以调用{@code DBManager.initDataSource(String)}
	 * ，关闭不存在的数据源不会执行任何操作
	 * 
	 * @param dsname
	 */
	public static void closeDataSource(String dsname) {
		try {
			BasicDataSource ds = dsMap.get(dsname);
			if (ds != null) {
				ds.close();
				logger.info("[CloseDataSourceOK] - dsname={}", dsname);
			}
		} catch (Exception e) {
			logger.error("[CloseDataSourceError] - ", e);
		}
	}

	/**
	 * 关闭所有数据源，若需要全部打开可以调用{@code DBManager.init()}
	 */
	public static void closeAllDataSource() {
		for (String dsname : dsMap.keySet()) {
			closeDataSource(dsname);
		}
		dsMap.clear();
		dsMap = null;
	}

	/**
	 * 获取指定数据源名称的连接对象，若没有可用连接，会一直重试
	 * 
	 * @return
	 * @throws DBException
	 *             不存在指定名称的数据源
	 */
	public static Connection getConnection(String dsname) throws DBException {
		Connection connection = null;
		BasicDataSource ds = dsMap.get(dsname);
		if (ds == null) {
			throw new DBException("NO DataSource for dsname: " + dsname);
		}
		try {
			connection = ds.getConnection();
			connection.setAutoCommit(true);
		} catch (Exception e) {
			logger.error("[GetConnError] - e.msg={} | ds={}.", e.getMessage(),
					dsname);
			throw new DBException(e);
		}
		return connection;
	}

	/**
	 * 
	 * 释放数据库连接资源到连接池
	 * 
	 * @param conn
	 * @param ps
	 */
	public static void releaseConnection(Connection conn, PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (Exception e) {
				logger.error("[ClosePSError] - ", e);
			}
		}
		if (conn != null) {
			try {
				if (!conn.isClosed())
					conn.close();
			} catch (Exception e) {
				logger.error("[CloseConnectionError] - ", e);
			}
		}
	}

	/**
	 * 从配置文件获取默认数据源配置
	 */
	private static void getDefaultDSConfig() {
		String xpath = "dataSources.dataSource";
		int dsCount = ConfigService.getList(xpath + ".dsname").size();
		for (int i = 0; i < dsCount; i++) {
			String dsXpath = xpath + "(" + i + ")";
			// 数据库驱动类
			String driverClassName = ConfigService.getString(dsXpath
					+ ".driverClassName", "com.mysql.jdbc.Driver");
			// 初始连接数
			int initialSize = ConfigService.getInt(dsXpath + ".initialSize", 1);
			// 最小空闲连接
			int minIdle = ConfigService.getInt(dsXpath + ".minIdle", 1);
			// 最大空闲连接
			int maxIdle = ConfigService.getInt(dsXpath + ".maxIdle", 1);
			// 最大活动连接数
			int maxActive = ConfigService.getInt(dsXpath + ".maxActive", 1);
			// 没有可用连接时,抛出异常前的最大等待时间(ms)
			int maxWait = ConfigService.getInt(dsXpath + ".maxWait", 1000);
			// 数据源名称
			String dsName = ConfigService.getString(dsXpath + ".dsname");
			// 连接字符串
			String connectURL = ConfigService
					.getString(dsXpath + ".connectURL");
			// 数据库名称
			String db = ConfigService.getString(dsXpath + ".db");
			// 用户名
			String username = ConfigService.getString(dsXpath + ".username");
			// 密码
			String password = ConfigService.getString(dsXpath + ".password");

			CommonData configData = new CommonData();
			configData.setBaseField("dsName", dsName);
			configData.setBaseField("connectURL", connectURL);
			configData.setBaseField("db", db);
			configData.setBaseField("username", username);
			configData.setBaseField("password", password);
			configData.setBaseField("driverClassName", driverClassName);
			configData.setBaseField("initialSize", initialSize);
			configData.setBaseField("minIdle", minIdle);
			configData.setBaseField("maxIdle", maxIdle);
			configData.setBaseField("maxActive", maxActive);
			configData.setBaseField("maxWait", maxWait);

			defaultDSConfigMap.put(dsName, configData);
		}
	}
}
