/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.appMain;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.util.IOUtil;
import com.weibo.datasys.common.util.StringUtils;

public class CreateArithmeticSeeds {

	private static Logger logger = LoggerFactory
			.getLogger(CreateArithmeticSeeds.class);

	private static final String paraPattern = "(*)";

	public static void main(String[] args) {
		ConfigFactory.init("conf/config.xml");
		try {
			String inputFile = "C:\\Users\\zouyandi\\Desktop\\in.txt";
			int startNum = 1;
			int seedCount = 44;
			int diff = 1;
			BufferedReader reader = IOUtil.getBufferedReader(inputFile);
			String outFile = inputFile.replace(".txt", "").concat("_out.txt");
			BufferedWriter outWriter = IOUtil.getBufferedWriter(outFile);
			String urlString = "";
			while (null != (urlString = reader.readLine())) {
				if (StringUtils.isEmptyString(urlString)) {
					continue;
				}
				int maxNum = startNum + diff * (seedCount - 1);
				for (int i = startNum; i <= maxNum; i += diff) {
					String out = urlString.replace(paraPattern, "" + i);
					outWriter.append(out).append("\n");
				}
			}
			IOUtil.closeIO(reader, outWriter);
		} catch (Exception e) {
			logger.error("", e);
		}

	}

}
