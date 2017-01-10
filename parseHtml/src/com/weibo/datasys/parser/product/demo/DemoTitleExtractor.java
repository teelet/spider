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

import java.util.HashMap;

import com.weibo.datasys.parser.html.core.title.TitleExtractor;

public class DemoTitleExtractor extends TitleExtractor
{
	public DemoTitleExtractor(String metaTitle, HashMap<Integer, String> titleMap,
			String specialTitle)
	{
		super(metaTitle, titleMap, specialTitle);
	}

	@Override
	public void init()
	{
		super.init();
		splitTitleEqualsSet.add("中国政府网");
		splitTitleEqualsSet.add("中华人民共和国中央人民政府门户网站");
	}
}
