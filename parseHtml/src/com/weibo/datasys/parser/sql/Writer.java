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

public class Writer
{

	private static final Logger LOG = LoggerFactory.getLogger(Writer.class);
	protected static String replaceSQL = "";
	protected static String updateNotWLSQL = "";
	protected static String selectSQL = "";
	protected static String updateSQL = "";
	protected Connection replaceConn = null, updateConn = null;
	protected PreparedStatement replacePreparedStatement = null;
	protected PreparedStatement updatePreparedStatement = null;
	protected PreparedStatement updateNotWLPreparedStatement = null;
	protected PreparedStatement selectPreparedStatement = null;
	protected PreparedStatement selectWLPreparedStatement = null;

	public Writer()
	{
	}

	public void init()
	{
		try
		{
			replaceConn = DBConnectionFactory.getConnection();
			updateConn = DBConnectionFactory.getConnection();
			replacePreparedStatement = replaceConn.prepareStatement(replaceSQL);
			selectPreparedStatement = updateConn.prepareStatement(selectSQL);
			updatePreparedStatement = updateConn.prepareStatement(updateSQL);
			updateNotWLPreparedStatement = updateConn.prepareStatement(updateNotWLSQL);
		} catch (SQLException e)
		{
			LOG.error("", e);
		}
	}

	public void close()
	{
		ClearDBResource.closeStatment(replacePreparedStatement);
		ClearDBResource.closeStatment(selectPreparedStatement);
		ClearDBResource.closeStatment(updatePreparedStatement);
		ClearDBResource.closeStatment(updateNotWLPreparedStatement);
		ClearDBResource.closeConnection(replaceConn);
		ClearDBResource.closeConnection(updateConn);
	}

	public void write(ParseData pData)
	{
	}

	public void commit()
	{
	}
}
