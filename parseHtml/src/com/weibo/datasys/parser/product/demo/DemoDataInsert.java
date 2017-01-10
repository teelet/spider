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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.data.SqlData;
import com.weibo.datasys.parser.html.data.ParseData;
import com.weibo.datasys.parser.sql.BatchInserter;

public class DemoDataInsert extends BatchInserter {

	private static final Logger LOG = LoggerFactory.getLogger(DemoDataInsert.class);
	private long writeCount = 0;

	public DemoDataInsert() {

		replaceSQL = "insert into " + SqlData.parsedbTableName
				+ " (id,origurl,fetchtime,pubtime,title,content,encoding,keyword,description,csize,parsestate,pagetype,language,original,author,previouspage,nextpage,taskid) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " + "on DUPLICATE KEY UPDATE "
				+ "fetchtime=values(fetchtime), pubtime=values(pubtime), "
				+ "title=values(title), content=values(content), encoding=values(encoding),keyword=values(keyword), "
				+ "description=values(description), csize=values(csize), "
				+ "parsestate=values(parsestate),pagetype=values(pagetype), "
				+ "language=values(language),original=values(original),author=values(author),"
				+ "previouspage=values(previouspage),nextpage=values(nextpage),taskid=values(taskid)";

		replaceSQL1 = "insert into " + SqlData.parsedbTableName + "_img" + "(id,article_id,url,width,height,fetchstate,day) "
				+ "values(?,?,?,?,?,?,?) " + "on DUPLICATE KEY UPDATE " + "url = values(url),day =values(day),"
				+ "article_id = values(article_id),width = values(width),height = values(height)";
		
		replaceSQL2 = "insert into article_id_pool (md5) values(?)"; 
	}

	@Override
	public void write(ParseData pData) {
		try {
			String content = pData.getContent();
			replacePreparedStatement.setString(1, pData.getId());
			replacePreparedStatement.setString(2, pData.getOrigUrl());
			replacePreparedStatement.setLong(3, pData.getFetchtime());
			replacePreparedStatement.setLong(4, pData.getPubtime());
			replacePreparedStatement.setString(5, pData.getTitle());
			replacePreparedStatement.setString(6, content);
			replacePreparedStatement.setString(7, pData.getEncoding());
			replacePreparedStatement.setString(8, pData.getKeyword());
			replacePreparedStatement.setString(9, pData.getDescription());
			pData.setCtsize(pData.getContent().length());
			replacePreparedStatement.setInt(10, pData.getCtsize());
			replacePreparedStatement.setInt(11, pData.getParsestate());
			replacePreparedStatement.setInt(12, pData.getPagetype());
			replacePreparedStatement.setInt(13, pData.getLanguage());
			replacePreparedStatement.setString(14, pData.getOriginPublish());
			replacePreparedStatement.setString(15, pData.getOriginAuthor());
			
			replacePreparedStatement.setString(16, pData.getPreviousPage());
			replacePreparedStatement.setString(17, pData.getNextPage());
			replacePreparedStatement.setString(18, pData.getTaskId());
			replacePreparedStatement.addBatch();
			
			
			replacePreparedStatement2.setString(1, pData.getId());
			replacePreparedStatement2.addBatch();
			
			writeCount++;

			HashMap<String, String> contentimgs = pData.getContentIms();
			for (Entry<String, String> entry : contentimgs.entrySet()) {
				String url = entry.getKey();
				String value = entry.getValue();
				String[] fields = value.split("\t");
				if (fields.length != 4) {
					LOG.error("[ERROR]:{}\t[IMG_URL]:{}", url, pData.getOrigUrl());
					continue;

				}
				String id = fields[0];
				String day = fields[1];
				String width = fields[2];
				String height = fields[3];
				if ("".equals(day)) {
					SimpleDateFormat dirFormat = new SimpleDateFormat("yyyyMMdd");
					day = dirFormat.format(new Date());
				}
				replacePreparedStatement1.setString(1, id);
				replacePreparedStatement1.setString(2, pData.getId());
				replacePreparedStatement1.setString(3, url);
				replacePreparedStatement1.setString(4, width);
				replacePreparedStatement1.setString(5, height);
				replacePreparedStatement1.setInt(6, 0);
				replacePreparedStatement1.setString(7, day);
				replacePreparedStatement1.addBatch();
			}

			if (0 == writeCount % SqlData.batchNum) {
				commit();
				commit1();
				commit2();
			}
		} catch (Exception e) {
			LOG.error("[ERROR]:{}\t[URL]:{}", e, pData.getOrigUrl());
		}
	}
}
