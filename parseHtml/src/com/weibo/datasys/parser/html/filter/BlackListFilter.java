/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.filter;

import java.io.BufferedReader;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.parser.util.FileUtil;

public class BlackListFilter {
	private static final Logger LOG = LoggerFactory
			.getLogger(BlackListFilter.class);
	private static final String[] TITLE_CONTAINS_FILTER = {};
	private static HashSet<String> titleEqualsFilter = new HashSet<String>();

	public static void init() {
		FileUtil fUtil = new FileUtil();
		try {
			String home = System.getProperty("home.dir");
			if (null == home) {
				System.out.println("Please set -Dhome.dir properties.");
				LOG.error("Please set -Dhome.dir properties.");
			}
			
			String titleEqualsFilterPath = home + "/" + ConfigFactory
					.getString("filter.titleEqualsBlackListPath");
			if (null != titleEqualsFilterPath) {
				BufferedReader bReader = fUtil.readFile(titleEqualsFilterPath);
				String line = "";
				while ((line = bReader.readLine()) != null) {
					if (!line.equals("")) {
						titleEqualsFilter.add(line);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("", e);
		} finally {
			try {
				fUtil.closeFile();
			} catch (Exception e2) {
				LOG.error("", e2);
			}
		}
	}

	public static int filter(String title) {
		int result = 0;
		if (titleEqualsFilter.contains(title)) {
			result = 5;
		} else {
			for (String filter : TITLE_CONTAINS_FILTER) {
				if (title.contains(filter)) {
					result = 6;
					break;
				}
			}
		}

		return result;
	}
}
