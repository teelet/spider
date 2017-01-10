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
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.html.data.PageData;
import com.weibo.datasys.parser.main.ThreadParser;

public class Reader
{

	private static final Logger LOG = LoggerFactory.getLogger(Reader.class);
	protected static String selectSQL = "select * from webdb where parsestate=0 order by fetchtime  limit "
			+ ThreadParser.readSize;
	protected static String updateSQL = "update webdb set parsestate=1 where id = ?";
	protected Connection conn = null;
	protected PreparedStatement selectPreparedStatement = null;
	protected PreparedStatement updatePreparedStatement = null;

	public Reader()
	{
	}

	public void init()
	{
		try
		{
			conn = DBConnectionFactory.getConnection();
			selectPreparedStatement = conn.prepareStatement(selectSQL);
			updatePreparedStatement = conn.prepareStatement(updateSQL);
		} catch (SQLException e)
		{
			LOG.error("", e);
		}
	}

	public ArrayList<PageData> read()
	{
		ArrayList<PageData> webPages = new ArrayList<PageData>();
		try
		{
			ResultSet rs = selectPreparedStatement.executeQuery();
			while (rs.next())
			{
				PageData webPage = new PageData();
				webPage.setId(rs.getString("id"));
				webPage.setUrl(rs.getString("url"));
				webPage.setFetchtime(rs.getLong("fetchtime"));
				webPage.setNormalizeUrl(rs.getString("normalizeurl"));
				webPage.setAllHtml(rs.getBytes("html"));
				webPage.setExtendMap(rs.getString("extend"));
				webPages.add(webPage);
				updatePreparedStatement.setString(1, webPage.getId());
				updatePreparedStatement.executeUpdate();
			}
		} catch (Exception e)
		{
			LOG.error("[ReadError] - e.msg={}", e.getMessage());
		}
		return webPages;
	}

	public void close()
	{
		ClearDBResource.closeStatment(updatePreparedStatement);
		ClearDBResource.closeStatment(selectPreparedStatement);
		ClearDBResource.closeConnection(conn);
	}

}
