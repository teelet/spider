/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.db.DBManager;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.TaskFactory;

public class TaskDAO {

	private static Logger logger = LoggerFactory.getLogger(TaskDAO.class);

	private static final String SAVE_SQL = "insert `db`.`table` "
			+ "values (?,?) " + " on DUPLICATE KEY UPDATE "
			+ "xml=IF(values(xml) is not null, values(xml), xml)";

	private static final String GET_ALL_SQL = "select * from db.table ";

	private static final String REMOVE_SQL = "delete from db.table where id=?";

	private static TaskDAO instance;

	private TaskDAO() {
	}

	public static TaskDAO getInstance() {
		if (instance == null) {
			instance = new TaskDAO();
		}
		return instance;
	}

	/**
	 * 
	 * 保存task，及其xml
	 * 
	 * @param task
	 * @param dsname
	 * @param db
	 * @param table
	 * @return 0==error; 1==insert; 2==no change; 3==update
	 */
	public int save(Task task, String dsname, String db, String table) {
		int saveCount = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = SAVE_SQL.replace("db", db).replace("table",
					table);
			ps = conn.prepareStatement(sql);
			ps.setString(1, task.getTaskId());
			ps.setString(2, task.getTaskXML());
			saveCount = ps.executeUpdate();
		} catch (Exception e) {
			logger.error("[SaveTaskError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return saveCount;
	}

	/**
	 * 
	 * 从数据库获取所有task
	 * 
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public List<Task> getAllTasks(String dsname, String db, String table) {
		List<Task> tasks = new ArrayList<Task>();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = GET_ALL_SQL.replace("db", db).replace("table", table);
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String xml = rs.getString(2);
				Task task = TaskFactory.buildTask(xml);
				tasks.add(task);
			}
		} catch (Exception e) {
			logger.error("[GetAllError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return tasks;
	}

	/**
	 * 
	 * 从数据库删除指定Task
	 * 
	 * @param taskId
	 * @param dsname
	 * @param db
	 * @param table
	 */
	public void remove(String taskId, String dsname, String db, String table) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = REMOVE_SQL.replace("db", db).replace("table", table);
			ps = conn.prepareStatement(sql);
			ps.setString(1, taskId);
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("[RemoveError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
	}

}
