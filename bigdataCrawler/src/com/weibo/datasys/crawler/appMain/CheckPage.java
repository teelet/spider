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

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.db.DBManager;
import com.weibo.datasys.common.urlnormallize.HttpURL;
import com.weibo.datasys.common.util.MD5Util;
import com.weibo.datasys.crawler.base.dao.PageDataDAO;
import com.weibo.datasys.crawler.base.entity.PageData;

public class CheckPage {

	private static Logger logger = LoggerFactory.getLogger(CheckPage.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ConfigFactory.init("conf/config.xml");
		DBManager.init();
		String dsname = "crawlDS66.38";
		String db = "search";
		String[] tables = new String[] { "crawler_webdb" };
		String pageUrl = "http://finance.sina.com.cn/stock/jsy/20130722/163616203817.shtml";
		String norUrl = HttpURL.normalizeHttpURL(pageUrl, true,
				Charset.forName("utf-8"));
		String id = MD5Util.MD5(norUrl);
		PageData pageData = null;
		for (String table : tables) {
			pageData = PageDataDAO.getInstance().getById(id, dsname, db, table);
			if (pageData != null) {
				logger.info("[FoundPage] - table={} | id={}", table,
						pageData.getId());
				pageData.unZipHtml();
				String html = new String(pageData.getHtml(), "gb2312");
				logger.debug(html);
				break;
			}
		}
		if (pageData == null) {
			logger.info("[PageNotFound] - id={}", id);
		}
	}

}
