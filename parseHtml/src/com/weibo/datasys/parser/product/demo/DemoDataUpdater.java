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

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.data.SqlData;
import com.weibo.datasys.parser.html.data.PageData;
import com.weibo.datasys.parser.html.data.ParseData;
import com.weibo.datasys.parser.sql.Updater;

public class DemoDataUpdater extends Updater
{

	private static final Logger LOG = LoggerFactory.getLogger(DemoDataUpdater.class);
	private long writeCount = 0;

	public DemoDataUpdater()
	{
		updateSQL = "insert into "
				+ SqlData.webdbTableName
				+ " (id,url,normalizeUrl,html,fetchtime,parsestate,extend) values (?,?,?,?,?,?,?) on DUPLICATE KEY UPDATE "
				+ "url=url,normalizeUrl=normalizeUrl,html=html,fetchtime=fetchtime,parsestate=values(parsestate),extend=extend";
	}

	@Override
	public void write(ParseData pData)
	{
		try
		{
			PageData webpage = pData.getPageData();
			updatePreparedStatement.setString(1, pData.getId());
			updatePreparedStatement.setString(2, webpage.getUrl());
			updatePreparedStatement.setString(3, webpage.getNormalizeUrl());
			ByteArrayInputStream in = new ByteArrayInputStream(webpage.getZipHtml());
			updatePreparedStatement.setBlob(4, in, in.available());
			updatePreparedStatement.setLong(5, webpage.getFetchtime());
			updatePreparedStatement.setInt(6, pData.getCrawlerstate());
			updatePreparedStatement.setString(7, webpage.getFormatExtendString());
			updatePreparedStatement.addBatch();
			writeCount++;
			if (0 == writeCount % SqlData.batchNum)
			{
				commit();
			}
		} catch (Exception e)
		{
			LOG.error("", e);
		}
	}

}
