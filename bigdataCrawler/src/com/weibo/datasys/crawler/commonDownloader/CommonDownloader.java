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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.entity.DownRequest;
import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.commonDownloader.worker.Controller;

public class CommonDownloader {

	private static Logger logger = LoggerFactory
			.getLogger(CommonDownloader.class);

	private int connTimeout = 3000;

	private int readTimeout = 3000;

	private int maxRedirectTimes = 0;

	private int connectorNum = 1;

	private int readerNum = 1;

	private boolean isClosed = true;

	private boolean useLocalDNS = true;

	private boolean useDNMS = false;

	private Controller controller;

	private String dnsIP;

	private int dnsPort;

	/**
	 * 是否忽略下载异常，是则不打印各种下载异常信息，默认true
	 */
	private boolean ignoreDownException = true;

	/**
	 * 
	 * 阻塞模式下载
	 * 
	 * @param req
	 *            下载请求
	 * @return req对应的下载结果
	 */
	public DownResponse downloadSync(DownRequest req) {
		req.setSyncReq(true);
		InnerRequest innerReq = new InnerRequest(req);
		controller.addRequest(innerReq);
		DownResponse resp = controller.getSyncResponse(innerReq, 0);
		return resp;
	}

	/**
	 * 
	 * 异步模式下载，下载结果可通过 getAsyncResponse() 获得，不保证按请求提交顺序返回结果
	 * 
	 * @param req
	 */
	public void downloadAsync(DownRequest req) {
		InnerRequest innerReq = new InnerRequest(req);
		controller.addRequest(innerReq);
	}

	/**
	 * 
	 * 获取异步模式下载的结果，会阻塞
	 * 
	 * @return
	 */
	public DownResponse getAsyncResponse() {
		return getAsyncResponse(0);
	}

	/**
	 * 
	 * 获取异步模式下载的结果，最多阻塞timeout ms，timeout==0则无限阻塞
	 * 
	 * @param timeout
	 * @return
	 */
	public DownResponse getAsyncResponse(int timeout) {
		return controller.getAsyncResponse(timeout);
	}

	/**
	 * 
	 * 默认构造函数，所有超时默认为3s，不跟随重定向，connectorNum和readerNum各为1
	 * 
	 */
	public CommonDownloader() {
	}

	/**
	 * 
	 * 完整版构造函数
	 * 
	 * @param connTimeout
	 *            连接超时，ms
	 * @param readTimeout
	 *            读取超时，ms
	 * @param redirectTimes
	 *            许跟随重定向次数，0则不允许重定向
	 * @param connectorNum
	 *            发送请求的线程数
	 * @param readerNum
	 *            获取应答的线程数
	 */
	public CommonDownloader(int connTimeout, int readTimeout,
			int redirectTimes, int connectorNum, int readerNum) {
		this.connTimeout = connTimeout;
		this.readTimeout = readTimeout;
		this.maxRedirectTimes = redirectTimes;
		this.connectorNum = connectorNum;
		this.readerNum = readerNum;
	}

	/**
	 * 启动ComonDownloader
	 */
	public CommonDownloader start() {
		if (this.isClosed) {
			try {
				this.isClosed = false;
				controller = new Controller(this);
			} catch (Exception e) {
				logger
						.error(
								"Init CommonDownloader error! CommonDownloader will be closed!",
								e);
				this.close();
			}
		}
		return this;
	}

	/**
	 * 关闭CommonDownloader并释放相关资源
	 */
	public void close() {
		this.isClosed = true;
		// 关闭控制器
		if (controller != null) {
			controller.close();
		}
	}

	public int getConnTimeout() {
		return connTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public int getMaxRedirectTimes() {
		return maxRedirectTimes;
	}

	public int getConnectorNum() {
		return connectorNum;
	}

	public int getReaderNum() {
		return readerNum;
	}

	public boolean isClosed() {
		return isClosed;
	}

	/**
	 * 
	 * 返回是否使用本地DNS，默认true
	 * 
	 * @return
	 */
	public boolean isUseLocalDNS() {
		return useLocalDNS;
	}

	/**
	 * 
	 * 设置是否使用本地DNS，默认true
	 * 
	 * @param useLocalDNS
	 */
	public void setUseLocalDNS(boolean useLocalDNS) {
		this.useLocalDNS = useLocalDNS;
	}

	/**
	 * 
	 * 返回是否使用DNMS(域名管理系统)来进行域名解析，默认false
	 * 
	 * @return
	 */
	public boolean isUseDNMS() {
		return useDNMS;
	}

	/**
	 * 
	 * 设置是否使用DNMS(域名管理系统)来进行域名解析，默认false
	 * 
	 * @param useDNMS
	 */
	public void setUseDNMS(boolean useDNMS) {
		this.useDNMS = useDNMS;
	}

	public String getDnsIP() {
		return dnsIP;
	}

	public void setDnsIP(String dnsIP) {
		this.dnsIP = dnsIP;
	}

	public int getDnsPort() {
		return dnsPort;
	}

	public void setDnsPort(int dnsPort) {
		this.dnsPort = dnsPort;
	}

	/**
	 * @return 当前已上传总字节数
	 */
	public long getUpBytes() {
		return this.controller.getUpBytes();
	}

	/**
	 * @return 当前已下载总字节数
	 */
	public long getDownBytes() {
		return this.controller.getDownBytes();
	}

	/**
	 * 
	 * 设置是否忽略下载异常，是则不打印各种下载异常信息，默认true
	 * 
	 * @param isIgnore
	 */
	public void setIgnoreDownException(boolean isIgnore) {
		this.ignoreDownException = isIgnore;
	}

	/**
	 * @return 是否忽略下载异常，是则不打印各种下载异常信息，默认true
	 */
	public boolean isIgnoreDownException() {
		return this.ignoreDownException;
	}

}
