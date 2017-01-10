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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.conf.ConfigFactory;

public class DemoOriginExtractor {
	private static final Logger LOG = LoggerFactory.getLogger(DemoOriginExtractor.class);
	private static final Pattern PUBLISH_PATTERN = Pattern.compile("(\\s|^|>|文章)来源[：|:]\\s{0,1}([^\\s|<]+)");

	//http://lol.replays.net/news/page/20161103/104836.html 编辑当做作者
	private static final Pattern AUTHOR_PATTERN = Pattern.compile("(\\s|^|>)作者[：|:]\\s{0,1}([^\\s|<]+)");
	private static final Pattern AUTHOR_PATTERN1 = Pattern.compile("(\\s|^|>)编辑[：|:]\\s{0,1}([^\\s|<]+)");
	private static final String SPLIT_CHAR = ",";
	private static HashMap<String, String> originNameMap;

	public static void init() {

		String home = System.getProperty("home.dir");
		if (null == home) {
			System.out.println("Please set -Dhome.dir properties.");
			LOG.error("Please set -Dhome.dir properties.");
		}

		originNameMap = new HashMap<String, String>();
		InputStream in = null;
		BufferedReader bReader = null;
		try {
			String originSiteConfig = home + "/"
					+ ConfigFactory.getString("origin.listpath", "conf/gov/originSiteConfig");

			// originSiteConfig
			// ="D:\\Java_Project\\parseHtml\\conf\\demo\\originSiteConfig";
			in = new FileInputStream(originSiteConfig);
			bReader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while (null != (line = bReader.readLine())) {
				String[] cutLine = line.trim().split(SPLIT_CHAR);
				if (2 == cutLine.length) {
					originNameMap.put(cutLine[0], cutLine[1]);
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

	public static String siteExtractor(String origUrl) {
		String result = "";
		try {
			URL url = new URL(origUrl);
			String urlHost = url.getHost().toString();
			if (originNameMap.containsKey(urlHost)) {
				result = originNameMap.get(urlHost);
			}
		} catch (MalformedURLException e) {
			LOG.error("ERROE siteExtractor:{}", e);
		}

		return result;
	}

	public static String publishExtractor(String text, String origUrl, Pattern specialReg) {
		String result = "";
		
		Matcher publishMatcher = null;
		if (specialReg == null){
			publishMatcher = PUBLISH_PATTERN.matcher(text);
		}else{
			publishMatcher = specialReg.matcher(text);
		}
		if (publishMatcher.find()) {
			String originPublish = publishMatcher.group(2);
			if (null != originPublish && originPublish.length() > 0) {
				result = originPublish;
			}
		}

		return result;
	}

	public static String authorExtractor(String text, String origUrl) {
		String result = "";
		URL url;
		try {
			url = new URL(origUrl);

			Matcher publishMatcher = AUTHOR_PATTERN.matcher(text);
			if (publishMatcher.find()) {
				String originPublish = publishMatcher.group(2);
				if (null != originPublish && originPublish.length() > 0) {
					result = originPublish;
				}
			}else{
				 publishMatcher = AUTHOR_PATTERN1.matcher(text);
				 if (publishMatcher.find()) {
						String originPublish = publishMatcher.group(2);
						if (null != originPublish && originPublish.length() > 0) {
							result = originPublish;
						}
				 }
			}

		} catch (MalformedURLException e) {
		}
		return result;
	}
}
