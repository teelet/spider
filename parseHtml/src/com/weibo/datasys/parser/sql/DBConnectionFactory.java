/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.weibo.datasys.common.conf.ConfigFactory;

public class DBConnectionFactory {

	private static DataSource ds = null;

	public static void init() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(ConfigFactory
				.getString("jdbc.driverClassName"));
		dataSource.setUrl(ConfigFactory.getString("jdbc.url"));
		dataSource.setUsername(ConfigFactory.getString("jdbc.username"));
		dataSource.setPassword(ConfigFactory.getString("jdbc.password"));

		dataSource.setInitialSize(ConfigFactory.getInt("jdbc.initialSize", 1));
		dataSource.setMinIdle(ConfigFactory.getInt("jdbc.minIdle", 2));
		dataSource.setMaxIdle(ConfigFactory.getInt("jdbc.maxIdle", 10));
		dataSource.setMaxWait(ConfigFactory.getInt("jdbc.maxWait", 1000));
		dataSource.setMaxActive(ConfigFactory.getInt("jdbc.maxActive", 2));
		dataSource.addConnectionProperty("autoReconnect", "true");
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

		ds = dataSource;
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	public static void main(String[] args) throws SQLException {
		ConfigFactory.init("conf/config.xml");
		init();
		Connection conn = getConnection();
		System.out.println(conn.getCatalog());
	}
}
