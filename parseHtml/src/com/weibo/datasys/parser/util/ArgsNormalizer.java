/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.util;

public class ArgsNormalizer
{
	/**
	 * 参数判断(String)
	 */
	 public static boolean strIsValid(String arg) {
		 if (arg==null||arg.trim().length()==0) {
			return false;
		}else {
			return true;
		}
	 }
	 
	 /**
	  * 数组参数判断
	  */
	 public static boolean arraysIsValid(Object[] args) {
		 if (args==null||args.length==0) {
			return false;
		}else {
			return true;
		}
	 }

}
