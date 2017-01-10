/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.db.DBManager;

/**
 * 
 * 数据库工具类
 *
 */
public class DBUtils {

	private static Logger logger = LoggerFactory.getLogger(DBUtils.class);

	/**
	 * 
	 * 创建一个新的数据库表，表结构与样例表一致
	 * 
	 * @param dsname
	 *            数据源名称
	 * @param db
	 *            数据库名称
	 * @param tableTobeCreated
	 *            待创建表名
	 * @param sampleTable
	 *            创建表的样例表
	 * @param cleanIfExist
	 *            如果待创建的表已存在是否删除
	 */
	public static void createTableAsSample(String dsname, String db,
			String tableTobeCreated, String sampleTable, boolean cleanIfExist) {
		String createIFNotExistsSQL = "CREATE TABLE IF NOT EXISTS " + db + "."
				+ tableTobeCreated + " LIKE " + db + "." + sampleTable;
		String cleanTableSQL = "drop table " + db + "." + tableTobeCreated;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			if (cleanIfExist) {
				try {
					ps = conn.prepareStatement(cleanTableSQL);
					ps.execute();
				} catch (Exception e) {
					logger.error("[createTableAsSampleError] - {}",
							e.getMessage());
				} finally {
					ps.close();
				}
			}
			ps = conn.prepareStatement(createIFNotExistsSQL);
			try {
				ps.execute();
			} catch (Exception e) {
				logger.error("[createTableAsSampleError] - {}", e.getMessage());
			} finally {
				ps.close();
			}
		} catch (Exception e) {
			logger.error("[createTableAsSampleError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
	}

	/**
	 * 
	 * 判断指定table是否存在
	 * 
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public static boolean isTableExist(String dsname, String db, String table) {
		boolean isTableExist = false;
		String isExistsSQL = "show tables from " + db + " LIKE " + "'" + table
				+ "'";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			ps = conn.prepareStatement(isExistsSQL);
			ResultSet rs = ps.executeQuery();
			isTableExist = rs.first();
		} catch (Exception e) {
			logger.error("[isTableExistError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return isTableExist;
	}

	/**
	 * 
	 * 删除指定table
	 * 
	 * @param dsname
	 * @param db
	 * @param table
	 */
	public static void dropTable(String dsname, String db, String table) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "drop table IF EXISTS " + db + "." + table;
		try {
			conn = DBManager.getConnection(dsname);
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			ps.execute("lock tables " + db + "." + table + " write");
			ps.executeUpdate();
			conn.commit();
			ps.execute("unlock tables");
			conn.setAutoCommit(true);
		} catch (Exception e) {
			logger.error("[dropTableError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
	}

	/**
	 * 
	 * 重命名oldTable到newTable
	 * 
	 * @param dsname
	 * @param db
	 * @param oldTable
	 * @param newTable
	 */
	public static void renameTable(String dsname, String db, String oldTable,
			String newTable) {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "rename table " + db + "." + oldTable + " to " + db + "."
				+ newTable;
		try {
			conn = DBManager.getConnection(dsname);
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("[renameTableError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
	}

}
