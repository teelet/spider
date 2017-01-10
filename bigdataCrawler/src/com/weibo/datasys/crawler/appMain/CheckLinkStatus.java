/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.appMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.db.DBManager;

public class CheckLinkStatus {

	private static Logger logger = LoggerFactory
			.getLogger(CheckLinkStatus.class);

	private static final int maxLv = 1;

	private static final String ds = "crawlDS66.38";

	private static final String db = "search";

	private static final String linkdb = "crawler_linkdb";

	private static final String seedDB = "crawler_taskseed_mil_read";

	private static final String CHECK_DEPTH_SQL = "select max(depth) from "
			+ db + "." + linkdb;

	private static final String COUNT_LINK_BY_DEPTH_SQL = "select count(*) from "
			+ db + "." + linkdb + " where depth={depth}";

	private static final String COUNT_SEED_BY_DEPTH_SQL = "select count(*) from "
			+ db
			+ "."
			+ seedDB
			+ " where depth={depth} and (state=0 or state=-1) and level={level}";

	private static final String COUNT_DEAD_LINK_BY_DEPTH_SQL = "select count(*) from "
			+ db + "." + linkdb + " where depth={depth} and state=-404";

	private static final String COUNT_FAILED_SEED_BY_DEPTH_SQL = "select count(*) from "
			+ db
			+ "."
			+ seedDB
			+ " where depth={depth} and state<0 and state!=-404 and level={level}";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigFactory.init("conf/config.xml");
		DBManager.init();
		int maxDepth = checkMaxDepth();
		if (maxDepth != -1) {
			int totalLink = 0;
			for (int i =0; i <= maxDepth; i++) {
				int total = countTotalByDepth(i);
				totalLink += total;
				int deadlinkCount = countDeadLinkByDepth(i);
				int[] uncrawlCounts = countSeedByDepth(i);
				int[] failedCounts = countFailedSeedByDepth(i);
				logger.info(
						"[depth-{}] - total={} | uncrawl={} | dead={} | failed={}",
						new Object[] { i, total,
								Arrays.toString(uncrawlCounts), deadlinkCount,
								Arrays.toString(failedCounts) });
			}
			logger.info("[totalLink] - count={}", totalLink);
		}
	}

	private static int countDeadLinkByDepth(int depth) {
		int count = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(ds);
			ps = conn.prepareStatement(COUNT_DEAD_LINK_BY_DEPTH_SQL.replace(
					"{depth}", depth + ""));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("[countDeadLinkByDepthError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return count;
	}

	private static int checkMaxDepth() {
		int maxDepth = -1;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(ds);
			ps = conn.prepareStatement(CHECK_DEPTH_SQL);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				maxDepth = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("[checkMaxDepthError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return maxDepth;
	}

	private static int countTotalByDepth(int depth) {
		int count = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(ds);
			ps = conn.prepareStatement(COUNT_LINK_BY_DEPTH_SQL.replace(
					"{depth}", depth + ""));
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error("[countTotalByDepthError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return count;
	}

	private static int[] countSeedByDepth(int depth) {
		int[] counts = new int[maxLv + 1];
		for (int lv = 0; lv <= maxLv; lv++) {
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = DBManager.getConnection(ds);
				ps = conn.prepareStatement(COUNT_SEED_BY_DEPTH_SQL.replace(
						"{depth}", depth + "").replace("{level}", lv + ""));
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					counts[lv] = rs.getInt(1);
				}
			} catch (Exception e) {
				logger.error("[countSeedByDepthError] - ", e);
			} finally {
				DBManager.releaseConnection(conn, ps);
			}
		}
		return counts;
	}

	private static int[] countFailedSeedByDepth(int depth) {
		int[] counts = new int[maxLv + 1];
		for (int lv = 0; lv <= maxLv; lv++) {
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = DBManager.getConnection(ds);
				ps = conn.prepareStatement(COUNT_FAILED_SEED_BY_DEPTH_SQL
						.replace("{depth}", depth + "").replace("{level}",
								lv + ""));
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					counts[lv] = rs.getInt(1);
				}
			} catch (Exception e) {
				logger.error("[countFailedSeedByDepthError] - ", e);
			} finally {
				DBManager.releaseConnection(conn, ps);
			}
		}
		return counts;
	}

}
