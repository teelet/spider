/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader.entity;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DownRequest {
	protected URL url;

	protected String method;

	protected String postString;

	protected String contentType;

	protected String userAgent = "";

	protected String cookie;

	protected String referer = "";

	protected Proxy proxy;

	protected Object attach;

	protected String ifModifiedSince;

	protected String host;

	protected String ip;

	protected int maxRedirectTimes;

	protected int connectTimeout;

	protected int readTimeout;

	protected boolean isSyncReq = false;

	protected Map<String, String> headers = new HashMap<String, String>();

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			System.out.println(new Date()
					+ " - [MalformedURLException] - e.msg=" + e.getMessage()
					+ " | url=" + url);
		}
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method.toUpperCase();
	}

	public String getPostString() {
		return postString;
	}

	public void setPostString(String postString) {
		this.postString = postString;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	/**
	 * 
	 * 取出此DownRequest的附加对象，若没有则返回nulls
	 * 
	 * @return
	 */
	public Object getAttach() {
		return attach;
	}

	/**
	 * 
	 * 为此DownRequest添加一个附加对象，以便将来取用
	 * 
	 * @param attach
	 */
	public void attach(Object attach) {
		this.attach = attach;
	}

	public String getIfModifiedSince() {
		return ifModifiedSince;
	}

	public void setIfModifiedSince(String ifModifiedSince) {
		this.ifModifiedSince = ifModifiedSince;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the maxRedirectTimes
	 */
	public int getMaxRedirectTimes() {
		return maxRedirectTimes;
	}

	/**
	 * @param maxRedirectTimes
	 *            the maxRedirectTimes to set
	 */
	public void setMaxRedirectTimes(int maxRedirectTimes) {
		this.maxRedirectTimes = maxRedirectTimes;
	}

	/**
	 * @return the connectTimeout
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * @param connectTimeout
	 *            the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @return the readTimeout
	 */
	public int getReadTimeout() {
		return readTimeout;
	}

	/**
	 * @param readTimeout
	 *            the readTimeout to set
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * 
	 * 是否同步下载请求，默认false
	 * 
	 * @return the isSyncReq
	 */
	public boolean isSyncReq() {
		return isSyncReq;
	}

	/**
	 * 
	 * 设置：是否同步下载请求，默认false
	 * 
	 * @param isSyncReq
	 *            the isSyncReq to set
	 */
	public void setSyncReq(boolean isSyncReq) {
		this.isSyncReq = isSyncReq;
	}

	/**
	 * 添加一个header值对
	 * 
	 * @param key
	 * @param value
	 * @author zouyandi
	 */
	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	/**
	 * 设置完整的headers
	 * 
	 * @param headers
	 * @author zouyandi
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	/**
	 * 获取全部headers
	 * 
	 * @return
	 * @author zouyandi
	 */
	public Map<String, String> getHeaders() {
		return this.headers;
	}
}
