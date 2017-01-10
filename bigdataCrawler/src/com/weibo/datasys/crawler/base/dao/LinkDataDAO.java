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

import com.weibo.datasys.common.dao.CommonDAO;
import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.data.InvalidFomatException;
import com.weibo.datasys.common.db.DBManager;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

public class LinkDataDAO extends CommonDAO {

	private static Logger logger = LoggerFactory.getLogger(LinkDataDAO.class);

	private static LinkDataDAO instance;

	private static final String CHECK_DEPTH_SQL = "select max(depth) from db.table";

	protected LinkDataDAO() {
	}

	public static LinkDataDAO getInstance() {
		if (instance == null) {
			instance = new LinkDataDAO();
		}
		return instance;
	}

	/**
	 * 
	 * 获取所有已发现种子的最大深度
	 * 
	 * @param task
	 * @return
	 */
	public int checkMaxDepth(Task task) {
		int maxDepth = -1;
		SaveStrategy saveStrategy = task.getSaveStrategy();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(saveStrategy.getLinkDS());
			String sql = CHECK_DEPTH_SQL
					.replace("db", saveStrategy.getLinkDB()).replace("table",
							saveStrategy.getLinkTable());
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				maxDepth = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("[CheckMaxDepthError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return maxDepth;
	}

	@Override
	public List<LinkData> getByCount(int count, String dsname, String db,
			String table) {
		List<LinkData> linkDatas = new ArrayList<LinkData>();
		List<? extends CommonData> commonDatas = super.getByCount(count,
				dsname, db, table);
		for (CommonData commonData : commonDatas) {
			try {
				LinkData linkData = wrap(commonData);
				linkDatas.add(linkData);
			} catch (InvalidFomatException e) {
				logger.error("[WrapLinkDataError] - ", e);
			}
		}
		commonDatas.clear();
		return linkDatas;
	}

	@Override
	public LinkData getById(String id, String dsname, String db, String table) {
		CommonData commonData = super.getById(id, dsname, db, table);
		LinkData linkData = null;
		try {
			linkData = wrap(commonData);
		} catch (InvalidFomatException e) {
			logger.error("[WrapLinkDataError] - ", e);
		}
		return linkData;
	}

	@Override
	public List<LinkData> getByOffsetOrderCount(long offset, int count,
			String orderBy, boolean desc, String dsname, String db, String table) {
		List<LinkData> linkDatas = new ArrayList<LinkData>();
		List<? extends CommonData> commonDatas = super.getByOffsetOrderCount(
				offset, count, orderBy, desc, dsname, db, table);
		for (CommonData commonData : commonDatas) {
			try {
				LinkData linkData = wrap(commonData);
				linkDatas.add(linkData);
			} catch (InvalidFomatException e) {
				logger.error("[WrapLinkDataError] - ", e);
			}
		}
		commonDatas.clear();
		return linkDatas;
	}

	@Override
	public List<LinkData> getBySQL(String sql, String dsname) {
		List<LinkData> linkDatas = new ArrayList<LinkData>();
		List<? extends CommonData> commonDatas = super.getBySQL(sql, dsname);
		for (CommonData commonData : commonDatas) {
			try {
				LinkData linkData = wrap(commonData);
				linkDatas.add(linkData);
			} catch (InvalidFomatException e) {
				logger.error("[WrapLinkDataError] - ", e);
			}
		}
		return linkDatas;
	}

	/**
	 * 
	 * 将CommonData包装成LinkData
	 * 
	 * @param commonData
	 * @return
	 * @throws InvalidFomatException
	 */
	private LinkData wrap(CommonData commonData) throws InvalidFomatException {
		LinkData linkData = null;
		if (commonData != null) {
			linkData = new LinkData();
			linkData.setId(commonData.getId());
			linkData.setBaseMap(commonData.getBaseString());
			linkData.setExtendMap(commonData.getExtendString());
		}
		return linkData;
	}

	/**
	 * 
	 * 获取linkdb中state!=-404的链接数量
	 * 
	 * @param linkDS
	 * @param linkDB
	 * @param linkTable
	 * @return
	 * @author zouyandi
	 */
	public int getAliveLinkCount(String dsname, String db, String table) {
		int count = -1;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = "select count(*) from db.table where depth>=0 and state!=-404"
					.replace("db", db).replace("table", table);
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("[GetAliveLinkCountError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return count;
	}
}
