/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.product.demo;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.data.SqlData;
import com.weibo.datasys.parser.html.data.PageData;
import com.weibo.datasys.parser.html.filter.SuffixFilter;
import com.weibo.datasys.parser.main.ThreadParser;
import com.weibo.datasys.parser.sql.Reader;
import com.weibo.datasys.parser.thread.ThreadManager;

public class DemoDataReader extends Reader {

	private static final Logger LOG = LoggerFactory
			.getLogger(DemoDataReader.class);

	public DemoDataReader() {
		selectSQL = "select * from " + SqlData.webdbTableName
				+ " where parsestate=0 order by fetchtime  limit "
				+ ThreadParser.readSize;
		if (com.weibo.datasys.parser.main.Main.getSystemName().equalsIgnoreCase("test")){
			selectSQL = "select * from " + SqlData.webdbTableName
					+ " where id='" + com.weibo.datasys.parser.main.Main.getId() + "'";
		}
		
		
		updateSQL = "update " + SqlData.webdbTableName
				+ " set parsestate=5 where id = ?";
	}

	@Override
	public ArrayList<PageData> read() {
		ArrayList<PageData> webPages = new ArrayList<PageData>();
		try {
			ResultSet rs = selectPreparedStatement.executeQuery();
			while (rs.next()) {
				String id = rs.getString("id");
				String url = rs.getString("url");
				if (!SuffixFilter.matchBlackList(url)) {
					PageData webPage = new PageData();
					webPage.setId(id);
					webPage.setUrl(url);
					webPage.setFetchtime(rs.getLong("fetchtime"));
					webPage.setNormalizeUrl(rs.getString("normalizeurl"));
					webPage.setAllHtml(rs.getBytes("html"));
					webPage.setExtendMap(rs.getString("extend"));
					webPage.setTaskId(rs.getString("taskid"));
					webPages.add(webPage);
				} else {
					updatePreparedStatement.setString(1, id);
					updatePreparedStatement.executeUpdate();
				}
			}
			if (0 == webPages.size()) {
				return null;
			}
			ThreadManager.readDataList.retainAll(webPages);
			Iterator<PageData> iterator = webPages.iterator();
			while (iterator.hasNext()) {
				PageData webPage = iterator.next();
				if (!ThreadManager.readDataList.add(webPage)) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			LOG.error("[ReadError] - e.msg={}", e);
		}
		return webPages;
	}

}
