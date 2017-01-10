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
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.html.data.ParseData;

public class Updater {
	private static final Logger LOG = LoggerFactory.getLogger(Updater.class);
	protected static String updateSQL = "";
	protected Connection conn = null;
	protected PreparedStatement updatePreparedStatement = null;

	public Updater() {
	}

	public void commit() throws Exception {
		updatePreparedStatement.executeBatch();
		conn.commit();
	}

	public void init() {
		try {
			conn = DBConnectionFactory.getConnection();
			conn.setAutoCommit(false);
			updatePreparedStatement = conn.prepareStatement(updateSQL);
		} catch (SQLException e) {
			LOG.error("init failed", e);
		}
	}

	public void close() {
		if (null != conn) {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				LOG.error("", e);
			}
		}
		ClearDBResource.closeStatment(updatePreparedStatement);
		ClearDBResource.closeConnection(conn);
	}

	public void write(ParseData pData) {
	}

}
