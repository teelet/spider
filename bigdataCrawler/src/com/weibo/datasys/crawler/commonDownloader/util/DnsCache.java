/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DnsCache implements IDnsCache {

	private static Logger logger = LoggerFactory.getLogger(DnsCache.class);

	private MemcachedClient mcc;

	private boolean useDNSM;

	private boolean useLocalDNS;

	public DnsCache(String ip, int port, boolean useDNSM, boolean useLocalDNS) {
		this.useDNSM = useDNSM;
		this.useLocalDNS = useLocalDNS;
		if (useDNSM) {
			try {
				List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
				list.add(new InetSocketAddress(ip, port));
				mcc = new MemcachedClient(list);
			} catch (Exception e) {
				logger.error("Init DnsCache error: {}", e.getMessage());
			}
		}
	}

	@Override
	public String getIP(String host) throws UnknownHostException {
		String IP = "";
		try {
			if (useDNSM && mcc != null) {
				IP = (String) mcc.get(host);
			}
		} catch (Exception e) {
			logger.error("DnsCache error: {}", e.getMessage());
		}
		if (null == IP || "".equals(IP)) {
			if (useLocalDNS) {
				IP = InetAddress.getByName(host).getHostAddress();
				try {
					if (mcc != null) {
						mcc.set(host, 0, IP);
					}
				} catch (Exception e) {
					logger.error("DnsCache error: {}", e.getMessage());
				}
			} else {
				throw new UnknownHostException("Can not find IP of " + host
						+ " from DnsCache.");
			}
		}
		return IP;
	}

	@Override
	public void close() {
		if (mcc != null) {
			mcc.shutdown(1000, TimeUnit.MILLISECONDS);
		}
	}
}
