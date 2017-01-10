/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.html.data.ParseData;

public class PageParse implements Callable<ParseData>
{
	private static final Logger LOG = LoggerFactory.getLogger(PageParse.class);
	private ParseData parseData;

	public PageParse()
	{
	}

	public PageParse(ParseData parseData)
	{
		this.parseData = parseData;
	}

	public ParseData parse()
	{
		return null;
	}

	@Override
	public ParseData call() throws Exception
	{
		return parse();
	}
}
