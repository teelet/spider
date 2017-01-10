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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DownResponse {

	private int retCode;

	private byte[] contentByte;

	private Map<String, String> headers = new HashMap<String, String>();

	private InnerRequest req;

	public DownResponse(InnerRequest req) {
		this.req = req;
	}

	public URL getUrl() {
		return req.getUrl();
	}

	public URL getOriginalUrl() {
		return req.getOriginalUrl();
	}

	public int getDnsTime() {
		return req.getDnsTime();
	}

	public int getConnTime() {
		return req.getConnTime();
	}

	public int getReadTime() {
		return req.getReadTime();
	}

	public Exception getException() {
		return req.getException();
	}

	public int getRetCode() {
		return retCode;
	}

	public void setRetCode(int retCode) {
		this.retCode = retCode;
	}

	public byte[] getContentByte() {
		return contentByte;
	}

	public void setContentByte(byte[] contentByte) {
		this.contentByte = contentByte;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public DownRequest getRequest() {
		return req;
	}

	public int getRedirectTimes() {
		return req.getRedirectTimes();
	}

	public int getTotalProcessTime() {
		return req.getTotalProcessTime();
	}

	/**
	 * 
	 * 获取此DownResponse对应的DownRequest的附加对象
	 * 
	 * @return
	 */
	public Object getAttach() {
		return req.getAttach();
	}

}
