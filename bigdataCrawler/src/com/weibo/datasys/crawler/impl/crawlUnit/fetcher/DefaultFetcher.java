/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.fetcher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.datasys.crawler.commonDownloader.entity.DownRequest;
import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.crawlUnit.fetcher.AbstractFetcher;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.monitor.CrawlerMonitorInfo;
import com.weibo.datasys.crawler.base.strategy.rule.crawl.CrawlRule;


public class DefaultFetcher extends AbstractFetcher {

	public static final Pattern POST_URL_PREFIX_PATTERN = Pattern
			.compile("\\[post\\]-\\w+-");

	public static final String HEADER_FIELD_PREFIX = "header_";

	public static final String POST_FIELD_PREFIX = "post_";

	@Override
	public CrawlInfo fetch(CrawlInfo crawlInfo) {
		SeedData seedData = crawlInfo.getSeedData();
		Task task = crawlInfo.getValidTask();
		// 任务已被删除or已停止则终止操作
		if (task != null) {
			CrawlRule crawlRule = task.getCrawlStrategy().getCrawlRule(
					seedData.getLevel());
			Map<String, String> reqParameters = crawlRule
					.getHttpReqParameters();
			DownRequest req = new DownRequest();
			req.attach(crawlInfo);

			// 设置cookies
			String cookies = seedData.getExtendField("cookies");
			if (StringUtils.isEmptyString(cookies)) {
				cookies = reqParameters.get("cookies");
				if (StringUtils.isEmptyString(cookies)) {
					cookies = ConfigFactory.getString("fetcher.cookies");
				}
			}
			req.setCookie(cookies);

			// 设置ip
			String ip = seedData.getExtendField("ip");
			if (StringUtils.isEmptyString(ip)) {
				ip = reqParameters.get("ip");
				if (StringUtils.isEmptyString(ip)) {
					ip = ConfigFactory.getString("fetcher.ip");
				}
			}
			req.setIp(ip);

			// 设置host
			String host = seedData.getExtendField("host");
			if (StringUtils.isEmptyString(host)) {
				host = reqParameters.get("host");
				if (StringUtils.isEmptyString(host)) {
					host = ConfigFactory.getString("fetcher.host");
				}
			}
			req.setHost(host);

			// 设置最大重定向次数
			String redirectTimes = seedData.getExtendField("redirectTimes");
			if (StringUtils.isEmptyString(redirectTimes)) {
				redirectTimes = reqParameters.get("redirectTimes");
				if (StringUtils.isEmptyString(redirectTimes)) {
					redirectTimes = ConfigFactory.getString(
							"fetcher.redirectTimes", "0");
				}
			}
			req.setMaxRedirectTimes(StringUtils.parseInt(redirectTimes, -1));

			// 设置超时
			String connectTimeout = seedData.getExtendField("connectTimeout");
			if (StringUtils.isEmptyString(connectTimeout)) {
				connectTimeout = reqParameters.get("connectTimeout");
				if (StringUtils.isEmptyString(connectTimeout)) {
					connectTimeout = ConfigFactory.getString(
							"fetcher.connectTimeout", "10000");
				}
			}
			req.setConnectTimeout(StringUtils.parseInt(connectTimeout, -1));

			String readTimeout = seedData.getExtendField("readTimeout");
			if (StringUtils.isEmptyString(readTimeout)) {
				readTimeout = reqParameters.get("readTimeout");
				if (StringUtils.isEmptyString(readTimeout)) {
					readTimeout = ConfigFactory.getString(
							"fetcher.readTimeout", "10000");
				}
			}
			req.setReadTimeout(StringUtils.parseInt(readTimeout, -1));

			// 设置UA
			String userAgent = seedData.getExtendField("userAgent");
			if (StringUtils.isEmptyString(userAgent)) {
				userAgent = reqParameters.get("userAgent");
				if (StringUtils.isEmptyString(userAgent)) {
					userAgent = ConfigFactory.getString("fetcher.userAgent");
				}
			}
			req.setUserAgent(userAgent);

			// 从种子扩展字段获取种子的referer
			String referer = seedData.getExtendField("referer");
			req.setReferer(referer);

			// 设置请求方法
			String url = seedData.getUrl();
			// 默认用GET方法
			String method = "GET";
			Matcher matcher = POST_URL_PREFIX_PATTERN.matcher(url);
			if (matcher.find()) {
				// url带有[post-\d+]-前缀则使用POST方法
				url = url.replaceAll(POST_URL_PREFIX_PATTERN.pattern(), "");
				method = "POST";
				// 设置post请求
				configPostReq(seedData, req);
			}
			req.setMethod(method);

			// 设置种子自定义的headers
			for (String extName : seedData.getExtendFieldNames()) {
				if (extName.startsWith(HEADER_FIELD_PREFIX)) {
					req.addHeader(
							extName.substring(HEADER_FIELD_PREFIX.length()),
							seedData.getExtendField(extName));
				}
			}

			// 对请求url里的中文字符进行编码
			String siteEncoding = task.getCrawlStrategy().getSiteEncoding();
			if ("auto".equals(siteEncoding)) {
				siteEncoding = "utf-8";
			}
			for (int i = 0; i < url.length(); i++) {
				String s = url.substring(i, i + 1);
				if (StringUtils.isChineseString(s)) {
					try {
						String encodeString = URLEncoder
								.encode(s, siteEncoding);
						url = url.replace(s, encodeString);
					} catch (UnsupportedEncodingException e) {
					}
				}
			}
			req.setUrl(url);
			comDownloader.downloadAsync(req);
			CrawlerMonitorInfo.addCounter("tryFetch", 1);
		}
		return crawlInfo;
	}

	/**
	 * 配置POST请求，包括<br>
	 * 1、构造该种子所需post的String，默认先从postString字段获取，
	 * 空则将带有POST_PREFIX的扩展字段拼接而成，子类根据需要改写<br>
	 * 2、设置req的postString属性<br>
	 * 3、设置req的contentType属性
	 * 
	 * @param seedData
	 * @param req
	 * @author zouyandi
	 */
	protected void configPostReq(SeedData seedData, DownRequest req) {
		String postString = seedData.getExtendField("postString");
		if (StringUtils.isEmptyString(postString)) {
			StringBuilder builder = new StringBuilder();
			for (String extName : seedData.getExtendFieldNames()) {
				if (extName.startsWith(POST_FIELD_PREFIX)) {
					String postKey = extName.substring(POST_FIELD_PREFIX
							.length());
					String postValue = seedData.getExtendField(extName);
					try {
						postValue = URLEncoder.encode(postValue, "utf-8");
					} catch (UnsupportedEncodingException e) {
					}
					builder.append(postKey).append("=").append(postValue)
							.append("&");
				}
			}
			if (builder.length() > 0) {
				builder.setLength(builder.length() - 1);
			}
			postString = builder.toString();
		}
		req.setPostString(postString);
		req.setContentType("application/x-www-form-urlencoded");
	}
}
