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

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.crawler.utils.URLUtil;

public class CrawlerTest {

	public static void main(String[] args) throws Exception {
		ConfigFactory.init("conf/config.xml");

		String url = URLUtil
				.fixEndSlash("http://roll.mil.news.sina.com.cn/col/zgjq/index.shtml/");
		System.out.println(url);
	}
}
