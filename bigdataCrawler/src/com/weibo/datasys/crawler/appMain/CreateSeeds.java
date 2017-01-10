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
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.CommonDownloader;
import com.weibo.datasys.crawler.commonDownloader.entity.DownRequest;
import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.util.HtmlTools;
import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.urlnormallize.HttpURL;
import com.weibo.datasys.common.util.IOUtil;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.utils.URLUtil;

public class CreateSeeds {

	private static Logger logger = LoggerFactory.getLogger(CreateSeeds.class);
	private static CommonDownloader comDownloader;
	private static Collection<String> blackSet = new ArrayList<String>();

	/**
	 * @param args
	 * @author zouyandi
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ConfigFactory.init("conf/config.xml");
		comDownloader = new CommonDownloader(5000, 5000, 5, 1, 1);
		comDownloader.setUseDNMS(false);
		comDownloader.setIgnoreDownException(false);
		comDownloader.start();

		String inputFile = "C:\\Users\\zouyandi\\Desktop\\xgovcn_news.txt";

		String blackFile = "op/deployHost/10.10.0.213/homecrawler/conf/filters/url/notContain-xgovcn-news.txt";
		BufferedReader reader = IOUtil.getBufferedReader(blackFile);
		String tmp = "";
		while (null != (tmp = reader.readLine())) {
			if (StringUtils.isEmptyString(tmp)) {
				continue;
			} else {
				blackSet.add(tmp);
			}
		}
		reader.close();

		boolean doNor = true;
		boolean doCheck = false;
		doCreate(inputFile, doNor, doCheck);

		comDownloader.close();
	}

	/**
	 * 
	 * 扫描inputFile，输出seed、host、dead文件
	 * 
	 * @param inputFile
	 *            原始种子输入文件，按行分隔
	 * @param doNor
	 *            if true，进行归一化
	 * @param doCheck
	 *            if true，死链检测
	 * @author zouyandi
	 */
	private static void doCreate(String inputFile, boolean doNor,
			boolean doCheck) {
		try {
			Set<String> seedtSet = new LinkedHashSet<String>();
			Set<String> hostSet = new LinkedHashSet<String>();
			BufferedReader reader = IOUtil.getBufferedReader(inputFile);
			String seedFile = inputFile.replace(".txt", "").concat("_seed.txt");
			String hostFile = inputFile.replace(".txt", "").concat("_host.txt");
			String deadFile = inputFile.replace(".txt", "").concat("_dead.txt");
			BufferedWriter seedWriter = IOUtil.getBufferedWriter(seedFile);
			BufferedWriter hostWriter = IOUtil.getBufferedWriter(hostFile);
			BufferedWriter deadWriter = IOUtil.getBufferedWriter(deadFile);

			String urlString = "";
			int count = 0;
			while (null != (urlString = reader.readLine())) {
				if (StringUtils.isEmptyString(urlString)) {
					continue;
				}
				count++;
				boolean isBlackURL = false;
				for (String black : blackSet) {
					if (urlString.toLowerCase().contains(black)) {
						isBlackURL = true;
						break;
					}
				}
				if (isBlackURL) {
					logger.info("[blackURL] - ori={}", urlString);
					continue;
				}
				String finalUrlString = urlString;
				// 归一化url
				String norUrlString = finalUrlString;
				if (doNor) {
					norUrlString = normalizeSeed(norUrlString);
				}
				// 判重
				if (!seedtSet.add(norUrlString)) {
					logger.info("[duplicateURL] - ori={}", urlString);
					continue;
				}
				// 检查是否死链
				String aliveUrlString = norUrlString;
				if (doCheck) {
					aliveUrlString = checkAlive(urlString, norUrlString);
					if (aliveUrlString == null) {
						logger.info("[seedDead-{}] - ori={} ", new Object[] {
								count, urlString });
						deadWriter.append(urlString).append("\n");
						continue;
					}
					logger.info("[seedOK-{}] - ori={} | alive={}",
							new Object[] { count, urlString, aliveUrlString });
				}
				finalUrlString = aliveUrlString;
				// 输出最终url
				seedWriter.append(finalUrlString).append("\n");
				// 提取host
				HttpURL httpURL = HttpURL.parseURL(urlString);
				String host = httpURL.getHost();
				if (hostSet.add(host)) {
					// 输出host
					hostWriter.append(host).append("\n");
				} else {
					// logger.info("[duplicateHost] - ori={}", urlString);
				}
			}
			IOUtil.closeIO(reader, seedWriter, hostWriter, deadWriter);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public static String normalizeSeed(String urlString) {
		String norUrlString = urlString;
		try {
			// 保留www的归一化
			if (!norUrlString.startsWith("http://")) {
				norUrlString = "http://" + norUrlString;
			}
			norUrlString = URLUtil.processUselessChar(norUrlString);
			norUrlString = URLUtil.fixEndSlash(norUrlString);
			// norUrlString = urlString.replace("www", "w3");
			// norUrlString = HttpURL.normalizeHttpURL(norUrlString, true,
			// Charset
			// .defaultCharset());
			// norUrlString = norUrlString.replace("w3", "www");
		} catch (Exception e) {
			logger.error("", e);
		}
		return norUrlString;
	}

	public static String checkAlive(String oriUrl, String norUrl) {
		String aliveUrl = null;
		int retCode = 200;
		Exception exception = null;
		DownRequest req = new DownRequest();
		req.setUrl(norUrl);
		req.setMaxRedirectTimes(5);
		req
				.setUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64;Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E");
		DownResponse resp = comDownloader.downloadSync(req);
		retCode = resp.getRetCode();
		exception = resp.getException();
		if (retCode == 200) {
			if (resp.getRedirectTimes() > 0) {
				String finalUrl = resp.getUrl().toString();
				logger.info("[redirect] - ori={} | nor={} | final={}",
						new Object[] { oriUrl, norUrl, finalUrl });
				aliveUrl = finalUrl;
			} else {
				aliveUrl = norUrl;
			}
		} else {
			if (exception != null
					&& exception instanceof SocketTimeoutException) {
				aliveUrl = norUrl;
			} else {
				if (oriUrl.contains("index.") && !norUrl.contains("index.")) {
					logger.info("[checkWithIndex] - ori={} | nor={}", oriUrl,
							norUrl);
					aliveUrl = checkAlive(oriUrl, oriUrl);
				}
				if (aliveUrl == null) {
					logger.error("[deadUrl] - ori={} | nor={}", oriUrl, norUrl);
				}
			}
		}
		return aliveUrl;
	}

	public static List<String> getLinkByPattern(String url, String regx) {
		List<String> links = new ArrayList<String>();
		DownRequest req = new DownRequest();
		req.setUrl(url);
		DownResponse resp = comDownloader.downloadSync(req);
		String html = HtmlTools.getStringContentOfHtml(resp.getContentByte(),
				resp.getHeaders());
		Pattern linkPattern = Pattern.compile(regx);
		Matcher matcher = linkPattern.matcher(html);
		while (matcher.find()) {
			links.add(matcher.group(1));
		}
		return links;
	}

}
