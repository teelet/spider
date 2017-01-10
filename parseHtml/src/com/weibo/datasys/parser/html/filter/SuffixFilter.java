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

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.parser.product.demo.DemoColumnExtractor;

public class SuffixFilter {
	private static final Logger LOG = LoggerFactory
			.getLogger(DemoColumnExtractor.class);
	private static final String SPLIT_CHAR = ";";
	private static HashSet<String> urlSuffixFilterSet;

	public static void init() {
		urlSuffixFilterSet = new HashSet<String>();
		String urlSuffixFilterString = ConfigFactory.getString(
				"filter.urlsuffix", "");
		if (0 != urlSuffixFilterString.trim().length()) {
			String[] cutUrlSuffixFilters = urlSuffixFilterString
					.split(SPLIT_CHAR);
			for (String cutUrlSuffixFilter : cutUrlSuffixFilters) {
				urlSuffixFilterSet.add("." + cutUrlSuffixFilter.toLowerCase());
			}
		}
	}

	public static boolean matchBlackList(String origUrl) {
		boolean result = false;
		String tempUrl = origUrl.toLowerCase();
		for (String urlSuffixFilterValue : urlSuffixFilterSet) {
			if (tempUrl.endsWith(urlSuffixFilterValue)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
