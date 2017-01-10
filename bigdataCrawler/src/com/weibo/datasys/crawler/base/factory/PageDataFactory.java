/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.factory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.weibo.datasys.common.urlnormallize.HttpURL;
import com.weibo.datasys.common.util.MD5Util;
import com.weibo.datasys.crawler.base.entity.PageData;
import com.weibo.datasys.crawler.base.entity.SeedData;

/**
 * 
 * PageData构造工厂类
 * 
 */
public class PageDataFactory {

	private static final Charset UTF_8 = Charset.forName("utf-8");

	private PageDataFactory() {
	}

	/**
	 * 
	 * 基于url构建PageData，其余属性设为默认值
	 * 
	 * @param url
	 * @return
	 */
	public static PageData buildBasePageData(SeedData seedData) {
		String url = seedData.getUrl();
		String normalizeUrl = url;
		try {
			normalizeUrl = HttpURL.normalizeHttpURL(url, true, UTF_8);
			if (normalizeUrl == null) {
				normalizeUrl = url;
			}
		} catch (UnsupportedEncodingException e) {
		}
		PageData pageData = new PageData();
		pageData.setId(MD5Util.MD5(normalizeUrl));
		pageData.setNormalizeUrl(normalizeUrl);
		pageData.setUrl(url);
		pageData.setTaskId(seedData.getTaskId());
		return pageData;
	}

}
