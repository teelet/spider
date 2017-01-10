/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.office;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.data.FileData;
import com.weibo.datasys.parser.office.extractor.WordParse;

public class OfficParser
{
	private static final Logger LOG = LoggerFactory.getLogger(OfficParser.class);

	public static void main(String[] args)
	{
		File filePath = new File("C:/Users/Sol/Desktop/test");
		File[] subFiles = filePath.listFiles();
		for (File subFile : subFiles)
		{
			WordParse wordParse = new WordParse();
			FileData fData = wordParse.extractorDoc(subFile);
			LOG.debug("-------------------------------------");
			LOG.debug("FileName:{}", fData.getName());
			LOG.debug("FileContent:{}", fData.getContent());
		}
	}
}
