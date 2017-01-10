/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.utils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * URL处理工具类
 * 
 */
public final class URLUtil {

	// 前面为顶级域名，后面部分国家的域名，但是被商业公司购买后用于一般网域
	private static String regex = "aero|arpa|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel"
			+ "cc|co|me|tv|ws|so";

	private static String cnRegion = "ac|bj|sh|tj|cq|he|sx|nm|ln|jl|hl|js|zj|ah|fj|jx|sd|ha|hb|hn|gd|gx|hi|sc|gz|yn|xz|sn|gs|qh|nx|xj|tw|hk|mo";

	private URLUtil() {
	}

	public static String getFirstDomain(String host) {
		String firstDomain = "";
		String[] domains = host.split("\\.");
		if (host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+") || domains.length < 3) {
			firstDomain = host;
		} else if (host.matches("[^\\.]+\\.gov\\.cn")) {
			// 特殊处理中国政府网(www.gov.cn)
			firstDomain = "gov.cn";
		} else {
			firstDomain = "." + domains[domains.length - 2] + "."
					+ domains[domains.length - 1];
			if (domains[domains.length - 1].equals("cn")
					&& domains[domains.length - 2].matches(cnRegion)) {
				firstDomain = domains[domains.length - 3] + firstDomain;
			} else if (!domains[domains.length - 1].matches(regex)
					&& domains[domains.length - 2].matches(regex)) {
				firstDomain = domains[domains.length - 3] + firstDomain;
			} else {
				firstDomain = firstDomain.replaceFirst("\\.", "");
			}
		}
		return firstDomain;
	}

	/**
	 * 
	 * 获取指定urlString的host部分，若url不合法，不抛出异常而是返回null
	 * 
	 * @param urlString
	 * @return
	 */
	public static String getHost(String urlString) {
		String host = null;
		try {
			URL url = new URL(urlString);
			host = url.getHost();
		} catch (Exception e) {
		}
		return host;
	}

	/**
	 * 提取urlSrc的domain，若url不合法，不抛出异常而是返回null
	 * 
	 * @param urlSrc
	 * @return 提取的domain或null
	 */
	public static String getDomain(String urlSrc) {
		String domain = null;
		try {
			if (!urlSrc.startsWith("http")) {
				urlSrc = "http://" + urlSrc;
			}
			URL url = new URL(urlSrc);
			String host = url.getHost();
			domain = getFirstDomain(host);
		} catch (MalformedURLException e) {
		}
		return domain;
	}

	/**
	 * 
	 * 处理url中的html实体，如"&amp;amp;"替换为"&"
	 * 
	 * @param url
	 * @return
	 */
	public static String processHtmlEntity(String url) {
		url = url.replaceAll("%3[Bb]", ";");
		return url.replaceAll("(&amp;)|(&#38;)", "&").replaceAll("amp;", "");
	}

	/**
	 * 
	 * 处理url中的无用字符，如用于页内跳转的结尾的#\w*
	 * 
	 * @param url
	 * @return
	 */
	public static String processUselessChar(String url) {
		url = url.replaceAll("#\\w*$", "");
		url = url.replaceAll("\\.\\./", "");
		return url;
	}

	/**
	 * 
	 * 补充无后缀，且不以/\d+形式结尾的url的结尾斜杠
	 * 
	 * @param url
	 * @return
	 */
	public static String fixEndSlash(String url) {
		if (url.matches("[^\\?]*/[^\\?\\./&]+$")) {
			if (!url.matches(".*/\\d+$")) {
				url = url + "/";
			}
		} else if (url.matches(".*\\.[^\\?\\./&]+/$")) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

}
