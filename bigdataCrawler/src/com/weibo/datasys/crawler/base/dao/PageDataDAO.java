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

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.db.DBManager;
import com.weibo.datasys.crawler.base.entity.PageData;

public class PageDataDAO {

	private static Logger logger = LoggerFactory.getLogger(PageDataDAO.class);

	private static final String SAVE_SQL = "insert db.table "
			+ "values (?,?,?,?,?,?,?,?) "
			+ "on DUPLICATE KEY UPDATE "
			+ "url=IF(values(url)<>'', values(url), url), "
			+ "normalizeUrl=IF(values(normalizeUrl)<>'', values(normalizeUrl), normalizeUrl), "
			+ "html=IF(values(html) is not null, values(html), html), "
			+ "fetchtime=IF(values(fetchtime)<>0, values(fetchtime), fetchtime), "
			+ "parsestate=values(parsestate), "
			+ "taskid=values(taskid), "
			+ "extend=IF(values(extend)<>'', values(extend), extend)";

	private static final String SELECT_SQL = "select * from db.table where id=?";

	private static final String REMOVE_SQL = "delete from db.table where id=?";

	private static PageDataDAO instance;

	private PageDataDAO() {
	}

	public static PageDataDAO getInstance() {
		if (instance == null) {
			instance = new PageDataDAO();
		}
		return instance;
	}

	/**
	 * 
	 * 保存PageData
	 * 
	 * @param data
	 * @param dsname
	 * @param db
	 * @param table
	 * @return 0==error; 1==insert; 2==update; 3==nochange
	 */
	public int save(PageData data, String dsname, String db, String table) {
		int saveCount = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = SAVE_SQL.replace("db", db).replace("table", table);
			ps = conn.prepareStatement(sql);
			ps.setObject(1, data.getId());
			ps.setObject(2, data.getUrl());
			ps.setObject(3, data.getNormalizeUrl());
			ps.setBlob(4, new ByteArrayInputStream(data.getZipHtml()));
			ps.setObject(5, data.getFetchtime());
			ps.setObject(6, data.getParsestate());
			ps.setObject(7, data.getTaskId());
			ps.setObject(8, data.getFormatExtendString());
			saveCount = ps.executeUpdate();
		} catch (Exception e) {
			logger.error("[SavePageError] - url={}", data.getUrl());
			logger.error("[SavePageError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return saveCount;
	}

	/**
	 * 
	 * 批量保存PageData
	 * 
	 * @param datas
	 * @param dsname
	 * @param db
	 * @param table
	 * @return [0]：failedCount。[1]：insertCount。[2]：updateCount。
	 * @author zouyandi
	 */
	public int[] save(List<PageData> datas, String dsname, String db,
			String table) {
		int[] saveCounts = new int[4];
		if (datas != null && datas.size() > 0) {
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = DBManager.getConnection(dsname);
				// 关闭自动提交事务
				conn.setAutoCommit(false);
				String sql = SAVE_SQL.replace("db", db).replace("table", table);
				ps = conn.prepareStatement(sql);
				for (PageData data : datas) {
					ps.setObject(1, data.getId());
					ps.setObject(2, data.getUrl());
					ps.setObject(3, data.getNormalizeUrl());
					ByteArrayInputStream in = new ByteArrayInputStream(data
							.getZipHtml());
					ps.setBlob(4, in, in.available());
					ps.setObject(5, data.getFetchtime());
					ps.setObject(6, data.getParsestate());
					ps.setObject(7, data.getTaskId());
					ps.setObject(8, data.getFormatExtendString());
					// add batch
					ps.addBatch();
				}
				// 执行批量插入
				int[] results = ps.executeBatch();
				// 提交事务
				conn.commit();
				// 统计结果
				for (int r : results) {
					saveCounts[r]++;
				}
			} catch (Exception e) {
				logger.error("[SavePageBatchError] - ", e);
			} finally {
				DBManager.releaseConnection(conn, ps);
			}
		}
		return saveCounts;
	}

	public PageData getById(String id, String dsname, String db, String table) {
		PageData pageData = null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = SELECT_SQL.replace("db", db).replace("table", table);
			ps = conn.prepareStatement(sql);
			ps.setObject(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				pageData = new PageData();
				pageData.setId(rs.getString(1));
				pageData.setUrl(rs.getString(2));
				pageData.setNormalizeUrl(rs.getString(3));
				byte[] zipBytes = rs.getBytes(4);
				pageData.setZipHtml(zipBytes);
				pageData.unZipHtml();
				pageData.setFetchtime(rs.getLong(5));
				pageData.setParsestate(rs.getInt(6));
				pageData.setTaskId(rs.getString(7));
			}
		} catch (Exception e) {
			logger.error("[GetByIdError] - id={}", id);
			logger.error("[GetByIdError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return pageData;
	}

	public void removeById(String id, String dsname, String db, String table) {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = REMOVE_SQL.replace("db", db).replace("table", table);
			ps = conn.prepareStatement(sql);
			ps.setObject(1, id);
			ps.executeUpdate();
		} catch (Exception e) {
			logger.error("[RemoveError] - id={}", id);
			logger.error("[RemoveError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
	}

}
