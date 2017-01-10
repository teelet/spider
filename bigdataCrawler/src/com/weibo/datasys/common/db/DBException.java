/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.db;

public class DBException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DBException(String msg) {
		super(msg);
	}

	public DBException(Throwable t) {
		super(t);
	}

}
