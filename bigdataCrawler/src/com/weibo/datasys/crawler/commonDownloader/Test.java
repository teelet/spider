/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.entity.DownRequest;
import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.util.HtmlTools;

@SuppressWarnings("unused")
public class Test {

	private static CommonDownloader downloader;

	private static Logger logger = LoggerFactory.getLogger(Test.class);

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 6) {
			System.out.println("connTimeout=" + args[0]);
			System.out.println("readTimeout=" + args[1]);
			System.out.println("connThreadNum=" + args[2]);
			System.out.println("readThreadNum=" + args[3]);
			downloader = new CommonDownloader(Integer.parseInt(args[0]),
					Integer.parseInt(args[1]), 0, Integer.parseInt(args[2]),
					Integer.parseInt(args[3]));
			downloader.setUseDNMS(Boolean.parseBoolean(args[4]));
			downloader.start();
			List<String> urls = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(args[5]));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				if (!"".equals(temp.trim())) {
					urls.add(temp);
				}
			}
			br.close();
			System.out.println("input url count = " + urls.size() + "\n");
			System.out.println("=========================");
			testURLDownloader(urls);
		} else {
			downloader = new CommonDownloader(5000, 5000, 5, 10, 20);
			// downloader.setUseDNMS(Boolean.parseBoolean("true"));
			downloader.start();
			List<String> urls = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader("topcn"));
			String temp = "";
			while ((temp = br.readLine()) != null) {
				if (!"".equals(temp.trim()) && urls.size() < 1000) {
					urls.add(temp);
				} else {
					break;
				}
			}
			br.close();
			System.out.println("input url count = " + urls.size() + "\n");
			System.out.println("=========================");
			testURLDownloader(urls);
		}
		logger.debug("Test End.");
	}

	private static void clearList(List<String> urls) {
		urls.clear();
		urls
				.add("http://news.people.com.cn/210801/211150/index.js?_=1308020023740");
	}

	private static void testURLDownloader(List<String> urls) throws IOException {

		clearList(urls);

		long s = System.currentTimeMillis();
		for (String url : urls) {
			DownRequest request = new DownRequest();
			request.setMethod("GET");
			request.setUserAgent("Mozilla/5.0");
			request.setUrl(url);
			// Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress(
			// "221.130.18.46", 80));
			// request.setProxy(proxy);
			// request.setIfModifiedSince("Thu, 26 May 2011 14:40:57 GMT");
			// request.setCookie("pt2gguin=o1660381579; EXPIRES=Fri 02-Jan-2020 00:00:00 GMT; PATH=/; DOMAIN=qq.com;uin=o1660381579; PATH=/; DOMAIN=qq.com;skey=@SHJI82djf; PATH=/; DOMAIN=qq.com;clientuin=; EXPIRES=Fri 02-Jan-1970 00:00:00 GMT; PATH=/; DOMAIN=qq.com;clientkey=; EXPIRES=Fri 02-Jan-1970 00:00:00 GMT; PATH=/; DOMAIN=qq.com;zzpaneluin=; EXPIRES=Fri 02-Jan-1970 00:00:00 GMT; PATH=/; DOMAIN=qq.com;zzpanelkey=; EXPIRES=Fri 02-Jan-1970 00:00:00 GMT; PATH=/; DOMAIN=qq.com;ptisp=cn; PATH=/; DOMAIN=qq.com;luin=o1660381579; PATH=/; DOMAIN=qq.com; EXPIRES=Thu 07-Jul-2011 11:02:42 GMT;lskey=00010000e0b14d3809847d97c114f980fd28436db5cfd7dcb03ebf5b0ddf2a0f63a1e6befb4e86d3d7caba3e; PATH=/; DOMAIN=qq.com; EXPIRES=Thu 07-Jul-2011 11:02:42 GMT;ptuserinfo=6173706972653131; PATH=/; DOMAIN=ptlogin2.qq.com;ptcz=4dc0b711b117ed1c34f059116a321a746464be9966acef388af8841a30a54109; EXPIRES=Fri 02-Jan-2020 00:00:00 GMT; PATH=/; DOMAIN=ptlogin2.qq.com;airkey=; EXPIRES=Fri 02-Jan-1970 00:00:00 GMT; PATH=/; DOMAIN=qq.com;");
			downloader.downloadAsync(request);
		}

		int count = 0;
		int successCount = 0;
		int dnsTime = 0;
		int connTime = 0;
		int readTime = 0;
		int maxReadTime = 0;
		int maxConnTime = 0;
		int maxDnsTime = 0;
		int connTimeoutCount = 0;
		int readTimeoutCount = 0;
		int resetCount = 0;
		int totalUsedTime = 0;
		int size = urls.size();
		while (count < size) {
			DownResponse resp = downloader.getAsyncResponse();
			// logger.debug("[GotResp] - url={} time={} ms ",
			// new Object[] { resp.getUrl().toExternalForm(),
			// resp.getTotalProcessTime() });
			boolean b = urls.remove(resp.getOriginalUrl().toExternalForm());
			totalUsedTime += resp.getTotalProcessTime();
			count++;
			dnsTime += resp.getDnsTime();
			connTime += resp.getConnTime();
			readTime += resp.getReadTime();
			if (resp.getConnTime() > maxConnTime) {
				maxConnTime = resp.getConnTime();
			}
			if (resp.getReadTime() > maxReadTime) {
				maxReadTime = resp.getReadTime();
			}
			if (resp.getDnsTime() > maxDnsTime) {
				maxDnsTime = resp.getDnsTime();
			}
			if (resp.getException() == null) {
				String html = HtmlTools.getStringContentOfHtml(resp
						.getContentByte(), "utf-8");
				System.out.println(html);
				successCount++;
			} else {
				String msg = resp.getException().getMessage();
				if (msg.contains("Reader timed out")) {
					readTimeoutCount++;
				} else if (msg.contains("Connector timed out")) {
					connTimeoutCount++;
				} else if (msg.contains("强迫关闭") || msg.contains("reset")) {
					resetCount++;
				}
			}
		}
		long e = System.currentTimeMillis();
		System.out.println("CommonDownloader: down " + successCount + "/"
				+ size + " urls, used " + (e - s) + " ms, avg " + (e - s)
				/ size + " ms/url, actually process time= " + totalUsedTime
				/ size + " ms/url");
		System.out.println("total dnsTime= " + dnsTime + " ms, avg " + dnsTime
				/ size + " ms, max " + maxDnsTime + " ms");
		System.out.println("total connTime= " + connTime + " ms, avg "
				+ connTime / size + " ms, max " + maxConnTime + " ms");
		System.out.println("total readTime= " + readTime + " ms, avg "
				+ readTime / size + " ms, max " + maxReadTime + " ms");
		System.out.println("connTimeoutCount=" + connTimeoutCount);
		System.out.println("readTimeoutCount=" + readTimeoutCount);
		System.out.println("resetCount=" + resetCount);
		downloader.close();
	}
}
