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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;

public class DemoColumnExtractor {
	private static final Logger LOG = LoggerFactory.getLogger(DemoColumnExtractor.class);
	private static final Pattern URL_PATTERN = Pattern.compile("http://[^/]+/([a-zA-Z]+)");
	private static final String SPLIT_CHAR = ",";
	private static final String SPLIT_COLUMN_CHAR = ",";
	private static HashMap<String, Integer> ColumnNameMap;

	public static void init() {
		String home = System.getProperty("home.dir");
		if (null == home) {
			System.out.println("Please set -Dhome.dir properties.");
			LOG.error("Please set -Dhome.dir properties.");
		}
		ColumnNameMap = new HashMap<String, Integer>();
		InputStream in = null;
		BufferedReader bReader = null;
		try {
			String columnConfig = home + "/" + ConfigFactory.getString("column.listpath", "conf/gov/columnConfig");

			// columnConfig
			// ="D:\\Java_Project\\parseHtml\\conf\\demo\\columnConfig";
			in = new FileInputStream(columnConfig);
			bReader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while (null != (line = bReader.readLine())) {
				String[] cutLine = line.trim().split(SPLIT_CHAR);
				if (2 == cutLine.length) {
					String[] cutColumns = cutLine[0].split(SPLIT_COLUMN_CHAR);
					if (cutColumns.length < 2) {
						for (String cutColum : cutColumns) {
							ColumnNameMap.put(cutColum, Integer.valueOf(cutLine[1]));
						}
					}

				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			try {
				if (null != bReader) {
					bReader.close();
				}
				if (null != in) {
					in.close();
				}
			} catch (Exception e2) {
				LOG.error("", e2);
			}
		}
	}

	public static int extractor(String origUrl) {
		int result = 0;
		Matcher urlMatcher = URL_PATTERN.matcher(origUrl);
		if (urlMatcher.find()) {
			String categoryKey = urlMatcher.group(1);
			if (ColumnNameMap.containsKey(categoryKey)) {
				result = ColumnNameMap.get(categoryKey);
			}
		}

		return result;
	}

}
