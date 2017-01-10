/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */

package com.weibo.datasys.parser.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.data.SqlData;

/**
 * @author Hu Jie Ru
 * 
 */
public final class HtmlParserByTable {
	private static Logger logger = LoggerFactory
			.getLogger(HtmlParserByTable.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ThreadParser.init();
		if (args.length == 0) {
			System.out.println("Usage: command tableName");
			System.exit(-1);
		}
		SqlData.webdbTableName = args[0];
		ThreadParser.parseOneTable();

	}
}
