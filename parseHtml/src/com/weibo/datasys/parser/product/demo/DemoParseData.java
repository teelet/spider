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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.html.data.PageData;
import com.weibo.datasys.parser.html.data.ParseData;

public class DemoParseData extends ParseData {
	private static Logger logger = LoggerFactory
			.getLogger(DemoParseData.class);


	/**
	 * @param pageData
	 */
	public DemoParseData(PageData pageData) {
		super(pageData);
		// TODO Auto-generated constructor stub
	}

}
