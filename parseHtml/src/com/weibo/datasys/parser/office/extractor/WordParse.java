/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.office.extractor;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.data.FileData;

public class WordParse
{
	private static final Logger LOG = LoggerFactory.getLogger(WordParse.class);

	public WordParse()
	{

	}

	public FileData extractorDoc(File filePath)
	{
		FileData fData = new FileData();
		fData.setName(filePath.getName());
		HWPFDocument doc = null;
		try
		{
			doc = new HWPFDocument(new FileInputStream(filePath));
			fData.setContent(doc.getRange().text());
		} catch (Exception e)
		{
			LOG.error("", e);
		}
		return fData;
	}

	public FileData extractorDocx(File filePath)
	{
		FileData fData = new FileData();
		
		return fData;
	}
}
