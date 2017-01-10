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
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.db.DBManager;
import com.weibo.datasys.parser.data.SqlData;
import com.weibo.datasys.parser.html.data.ParseData;

public class BatchInserter {

	private static final Logger LOG = LoggerFactory.getLogger(BatchInserter.class);
	protected String replaceSQL = "";
	protected String replaceSQL1 = "";
	protected String replaceSQL2 = "";
	protected Connection replaceConn = null;
	protected Connection replaceConn1 = null;
	protected Connection replaceConn2 = null;
	
	protected PreparedStatement replacePreparedStatement = null;
	protected PreparedStatement replacePreparedStatement1 = null;
	protected PreparedStatement replacePreparedStatement2 = null;

	private String sampleTable = "parser_sample";
	private String sampleTable1 = "parser_sample_img";
	private String parsedTable = SqlData.parsedbTableName;
	private String db = SqlData.parsedbDBName;
	
	public BatchInserter() {
	}

	private boolean existsTable(String db,String table){
		String isExistsSQL = "show tables from " + db + " LIKE " + "'" + table + "'";
		Connection Conn = null;
		PreparedStatement ps = null;
		boolean isTableExist = false;
		try {
			Conn = DBConnectionFactory.getConnection();
			ps = Conn.prepareStatement(isExistsSQL);
			ResultSet rs = ps.executeQuery();
			isTableExist = rs.first();
		} catch (Exception e) {
			LOG.error("[isTableExistError] - ", e);
		} finally {
			DBManager.releaseConnection(Conn, ps);
		}
		return isTableExist;	
	}
	
	
	private void createTable(String db, String table,String sample){
		Connection Conn = null;
		PreparedStatement ps = null;
		LOG.info("[CreateParsedDB] - creating. table={}", table);
		String createIFNotExistsSQL = "CREATE TABLE IF NOT EXISTS " + db + "." + table + " LIKE " + db + "."
				+ sample;
		try {
			Conn = DBConnectionFactory.getConnection();
			ps = Conn.prepareStatement(createIFNotExistsSQL);
			try {
				ps.execute();
			} catch (Exception e) {
				LOG.error("[createTableAsSampleError] - {}", e.getMessage());
			} finally {
				ps.close();
			}
		} catch (Exception e) {
			LOG.error("[createTableAsSampleError] - ", e);
		} finally {
			DBManager.releaseConnection(Conn, ps);
		}
		LOG.info("[CreateParsedDB] - done.");
	}
	

	
	public void createTable() {
		
		boolean isTableExist = existsTable(db,parsedTable);
		if (!isTableExist){
			LOG.info(db + "."+parsedTable + " is not existst");
			createTable(db,parsedTable,sampleTable);
		}
		
		isTableExist = existsTable(db,parsedTable+"_img");
		if (!isTableExist){
			LOG.info(db + "."+parsedTable + "_img is not existst");
			createTable(db,parsedTable+"_img",sampleTable1);
		}

	}

	public void commit() throws SQLException {
		replacePreparedStatement.executeBatch();
		replaceConn.commit();
	}
	
	public void commit1() throws SQLException {
		replacePreparedStatement1.executeBatch();
		replaceConn1.commit();
	}
	
	public void commit2() throws SQLException {
		replacePreparedStatement2.executeBatch();
		replaceConn2.commit();
	}
	

	public void init() {
		try {
			createTable();
			replaceConn = DBConnectionFactory.getConnection();
			replaceConn.setAutoCommit(false);
			replacePreparedStatement = replaceConn.prepareStatement(replaceSQL);
			
			replaceConn1 = DBConnectionFactory.getConnection();
			replaceConn1.setAutoCommit(false);
			replacePreparedStatement1 = replaceConn1.prepareStatement(replaceSQL1);
			
			replaceConn2 = DBConnectionFactory.getConnection();
			replaceConn2.setAutoCommit(false);
			replacePreparedStatement2 = replaceConn2.prepareStatement(replaceSQL2);
			
			
		} catch (SQLException e) {
			LOG.error("error in init db tavle: ", e);
		}
	}

	public void close() {
		if (null != replaceConn) {
			try {
				replaceConn.setAutoCommit(true);
			} catch (SQLException e) {
				LOG.error("error in close in replaceConn", e);
			}
		}
		ClearDBResource.closeStatment(replacePreparedStatement);
		ClearDBResource.closeConnection(replaceConn);
		
		if (null != replaceConn1) {
			try {
				replaceConn1.setAutoCommit(true);
			} catch (SQLException e) {
				LOG.error("error in close in replaceConn1", e);
			}
		}
		ClearDBResource.closeStatment(replacePreparedStatement1);
		ClearDBResource.closeConnection(replaceConn1);
		
		if (null != replaceConn2) {
			try {
				replaceConn2.setAutoCommit(true);
			} catch (SQLException e) {
				LOG.error("error in close in replaceConn2", e);
			}
		}
		ClearDBResource.closeStatment(replacePreparedStatement2);
		ClearDBResource.closeConnection(replaceConn2);
		
	}

	public void write(ParseData pData) throws SQLException {
	}

}
