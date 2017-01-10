/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.StringUtils;


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
	 * 动态数据源配置
	 */
	private static Map<String, CommonData> dynDSConfigMap = new ConcurrentHashMap<String, CommonData>();

	/**
	 * 多数据源连接池
	 */
	private static Map<String, BasicDataSource> dsMap;

	/**
	 * 初始化DBManager，根据配置，初始化所有数据源
	 */
	public synchronized static void init() {
		if (dsMap == null || dsMap.isEmpty()) {
			// 没有初始化则初始化
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
			for (CommonData configData : dynDSConfigMap.values()) {
				try {
					initDataSource(configData);
				} catch (Exception e) {
					logger.error("[InitDynamicDataSourceError] - ", e);
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
			if (null == configData) {
				// 默认配置不存在则获取动态配置
				configData = dynDSConfigMap.get(dsname);
			}
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
	 * 动态添加一个数据源，数据源配置数据由configData指定
	 * 
	 * @param configData
	 *            配置数据，需要的基础属性包括：(不区分大小写)<br>
	 *            dsname：数据源名称，数据源的唯一标识<br>
	 *            driverClassName：驱动类名<br>
	 *            connectURL：数据库连接url<br>
	 *            db：数据库名称<br>
	 *            username：用户名<br>
	 *            password：密码<br>
	 *            initialSize：连接池初始大小，默认1<br>
	 *            minIdle：最小空闲连接数，默认1<br>
	 *            maxIdle：最大空闲连接数，默认1<br>
	 *            maxActive：最大活动连接数，默认1<br>
	 *            maxWait：没有可用连接时，抛出异常钱等待最大时间，ms，默认1000<br>
	 * 
	 * @throws DBException
	 *             配置不合法，或同名数据源已存在则抛出异常
	 */
	public static void addDataSource(CommonData configData) throws DBException {
		try {
			String dsname = configData.getBaseField("dsname");
			if (dsMap.get(dsname) != null) {
				throw new DBException("DataSource: " + dsname
						+ " alread existed, please remove it first.");
			}
			dynDSConfigMap.put(dsname, configData);
			initDataSource(configData);
			logger.info("[AddDataSourceOK] - dsname={}", dsname);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	/**
	 * 
	 * 移除指定名称的数据源，数据源将被关闭，且移除其配置数据缓存，移除不存在的数据源不会进行任何操作
	 * 
	 * @param dsname
	 */
	public static void removeDataSource(String dsname) {
		try {
			dynDSConfigMap.remove(dsname);
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
		int dsCount = ConfigFactory.getList(xpath + ".dsname").size();
		for (int i = 0; i < dsCount; i++) {
			String dsXpath = xpath + "(" + i + ")";
			// 数据库驱动类
			String driverClassName = ConfigFactory.getString(dsXpath
					+ ".driverClassName", "com.mysql.jdbc.Driver");
			// 初始连接数
			int initialSize = ConfigFactory.getInt(dsXpath + ".initialSize", 1);
			// 最小空闲连接
			int minIdle = ConfigFactory.getInt(dsXpath + ".minIdle", 1);
			// 最大空闲连接
			int maxIdle = ConfigFactory.getInt(dsXpath + ".maxIdle", 1);
			// 最大活动连接数
			int maxActive = ConfigFactory.getInt(dsXpath + ".maxActive", 1);
			// 没有可用连接时,抛出异常前的最大等待时间(ms)
			int maxWait = ConfigFactory.getInt(dsXpath + ".maxWait", 1000);
			// 数据源名称
			String dsName = ConfigFactory.getString(dsXpath + ".dsname");
			// 连接字符串
			String connectURL = ConfigFactory
					.getString(dsXpath + ".connectURL");
			// 数据库名称
			String db = ConfigFactory.getString(dsXpath + ".db");
			// 用户名
			String username = ConfigFactory.getString(dsXpath + ".username");
			// 密码
			String password = ConfigFactory.getString(dsXpath + ".password");

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
