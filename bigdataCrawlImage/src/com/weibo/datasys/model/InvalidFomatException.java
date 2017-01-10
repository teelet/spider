/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.model;

public class InvalidFomatException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidFomatException(String msg) {
		super(msg);
	}

	public InvalidFomatException(Throwable t) {
		super(t);
	}

}
