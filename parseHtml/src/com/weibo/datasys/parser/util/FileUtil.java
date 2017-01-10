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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil
{
	private InputStream in = null;
	private BufferedReader bReader = null;

	public FileUtil()
	{
	}

	public BufferedReader readFile(String filePath) throws FileNotFoundException
	{
		in = new FileInputStream(new File(filePath));
		bReader = new BufferedReader(new InputStreamReader(in));
		return bReader;
	}

	public void closeFile() throws IOException
	{
		if (bReader != null)
		{
			bReader.close();
		}
		if (in != null)
		{
			in.close();
		}
	}
}
