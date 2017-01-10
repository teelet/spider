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

/**
 * @author jinkui
 *
 */
public class Test {

	public static void main(String[] args){
		String in = "http://games.sina.com.cn/o/z/hs/2016-10-06/fxwrhpn9221038.shtml";
		String subFolder = in.split("/")[1];
		System.out.println(subFolder);
	}
}
