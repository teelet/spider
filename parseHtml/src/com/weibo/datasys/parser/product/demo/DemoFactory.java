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

import com.weibo.datasys.parser.factory.Factory;
import com.weibo.datasys.parser.html.core.PageParse;
import com.weibo.datasys.parser.html.data.ParseData;
import com.weibo.datasys.parser.sql.BatchInserter;
import com.weibo.datasys.parser.sql.Reader;
import com.weibo.datasys.parser.sql.Updater;

public class DemoFactory extends Factory
{

	@Override
	public void init()
	{
		super.init();
		DemoColumnExtractor.init();
		DemoOriginExtractor.init();
	}

	@Override
	public PageParse getPageParse(ParseData parseData)
	{
		return new DemoPageParse(parseData);
	}

	@Override
	public Reader getReader()
	{
		return new DemoDataReader();
	}

	@Override
	public BatchInserter getBatchInserter()
	{
		return new DemoDataInsert();
	}

	@Override
	public Updater getUpdater()
	{
		return new DemoDataUpdater();
	}

}
