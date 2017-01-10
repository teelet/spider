/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.factory;

import com.weibo.datasys.parser.data.SqlData;
import com.weibo.datasys.parser.html.core.PageParse;
import com.weibo.datasys.parser.html.data.ParseData;
import com.weibo.datasys.parser.html.filter.BlackListFilter;
import com.weibo.datasys.parser.html.filter.SuffixFilter;
import com.weibo.datasys.parser.sql.BatchInserter;
import com.weibo.datasys.parser.sql.Reader;
import com.weibo.datasys.parser.sql.Updater;
import com.weibo.datasys.parser.sql.Writer;

public class Factory
{

	public void init()
	{
		SuffixFilter.init();
		BlackListFilter.init();
		SqlData.init();
	}

	public PageParse getPageParse(ParseData parseData)
	{
		return new PageParse(parseData);
	}

	public Reader getReader()
	{
		return new Reader();
	}

	public Writer getWriter()
	{
		return new Writer();
	}

	public BatchInserter getBatchInserter()
	{
		return new BatchInserter();
	}

	public Updater getUpdater()
	{
		return new Updater();
	}
}
