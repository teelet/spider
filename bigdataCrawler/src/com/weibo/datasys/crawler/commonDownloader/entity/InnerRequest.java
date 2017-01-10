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

import java.io.ByteArrayOutputStream;
import java.net.URL;

import com.weibo.datasys.crawler.commonDownloader.util.DnsKey;

public class InnerRequest extends DownRequest {

	private InnerRequest originalReq;

	private int redirectTimes;

	private long startDnsTime;

	private long endDnsTime;

	private long startProcessTime;

	private long endProcessTime;

	private long startConnTime;

	private long endConnTime;

	private long startReadTime;

	private long endReadTime;

	private Exception exception;

	private ByteArrayOutputStream byteContentBuffer;

	private DnsKey dnsKey;

	private DownResponse resp;

	public InnerRequest(DownRequest req) {
		this.cookie = req.cookie;
		this.method = req.method;
		this.userAgent = req.userAgent;
		this.postString = req.postString;
		this.contentType = req.contentType;
		this.referer = req.referer;
		this.url = req.url;
		this.originalReq = this;
		this.proxy = req.proxy;
		this.dnsKey = new DnsKey();
		this.dnsKey.attach(this);
		this.attach = req.attach;
		this.ifModifiedSince = req.ifModifiedSince;
		this.host = req.host;
		this.ip = req.ip;
		this.maxRedirectTimes = req.maxRedirectTimes;
		this.connectTimeout = req.connectTimeout;
		this.readTimeout = req.readTimeout;
		this.isSyncReq = req.isSyncReq;
		this.headers = req.headers;
	}

	public InnerRequest() {
		this.dnsKey = new DnsKey();
		this.dnsKey.attach(this);
	}

	/**
	 * 
	 * 复制一个具有相同cookie，agent，redirectTimes，method为get的请求
	 * 
	 * @return
	 */
	public InnerRequest copy() {
		InnerRequest req = new InnerRequest();
		req.originalReq = this.originalReq;
		req.cookie = this.cookie;
		req.method = "GET";
		req.userAgent = this.userAgent;
		req.redirectTimes = this.redirectTimes;
		req.attach = this.attach;
		req.proxy = this.proxy;
		req.ifModifiedSince = this.ifModifiedSince;
		req.referer = this.referer;
		req.host = this.host;
		req.ip = this.ip;
		req.maxRedirectTimes = this.maxRedirectTimes;
		req.connectTimeout = this.connectTimeout;
		req.readTimeout = this.readTimeout;
		req.isSyncReq = this.isSyncReq;
		req.headers = this.headers;
		return req;
	}

	public URL getOriginalUrl() {
		return originalReq.url;
	}

	public int getRedirectTimes() {
		return redirectTimes;
	}

	public void setRedirectTimes(int redirectTimes) {
		this.redirectTimes = redirectTimes;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public ByteArrayOutputStream getByteContentBuffer() {
		return byteContentBuffer;
	}

	public void setByteContentBuffer(ByteArrayOutputStream byteContentBuffer) {
		this.byteContentBuffer = byteContentBuffer;
	}

	public void setStartConnTime(long startConnTime) {
		this.startConnTime = startConnTime;
	}

	public void setEndConnTime(long endConnTime) {
		this.endConnTime = endConnTime;
	}

	public void setStartReadTime(long startReadTime) {
		this.startReadTime = startReadTime;
	}

	public void setEndReadTime(long endReadTime) {
		this.endReadTime = endReadTime;
	}

	public int getConnTime() {
		return (int) (endConnTime - startConnTime);
	}

	public int getReadTime() {
		return (int) (endReadTime - startReadTime);
	}

	public void setStartProcessTime(long startProcessTime) {
		this.startProcessTime = startProcessTime;
	}

	public void setEndProcessTime(long endProcessTime) {
		this.endProcessTime = endProcessTime;
	}

	public int getTotalProcessTime() {
		return (int) (endProcessTime - startProcessTime);
	}

	public DnsKey getDnsKey() {
		return dnsKey;
	}

	public void setStartDnsTime(long startDnsTime) {
		this.startDnsTime = startDnsTime;
	}

	public void setEndDnsTime(long endDnsTime) {
		this.endDnsTime = endDnsTime;
	}

	public int getDnsTime() {
		return (int) (endDnsTime - startDnsTime);
	}

	public DownResponse getResp() {
		return resp;
	}

	public void setResp(DownResponse resp) {
		this.resp = resp;
	}

	public DownRequest getOriginalReq() {
		return originalReq;
	}

	public void setOriginalReq(InnerRequest originalReq) {
		this.originalReq = originalReq;
	}
}
