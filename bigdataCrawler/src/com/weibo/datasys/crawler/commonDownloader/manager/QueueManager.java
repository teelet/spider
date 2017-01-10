/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader.manager;

import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.entity.DownRequest;
import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;

public class QueueManager {

	private static Logger logger = LoggerFactory.getLogger(QueueManager.class);

	private ArrayBlockingQueue<InnerRequest> requestQueue = new ArrayBlockingQueue<InnerRequest>(
			100000);

	private ArrayBlockingQueue<SelectionKey> connectableQueue = new ArrayBlockingQueue<SelectionKey>(
			100000);

	private ArrayBlockingQueue<SelectionKey> readableQueue = new ArrayBlockingQueue<SelectionKey>(
			100000);

	private Map<DownRequest, DownResponse> asyncRespMap = new ConcurrentHashMap<DownRequest, DownResponse>();

	private Map<DownRequest, DownResponse> syncRespMap = new ConcurrentHashMap<DownRequest, DownResponse>();

	/**
	 * 
	 * 将一个DownRequest放进队列，可能阻塞
	 * 
	 * @param req
	 */
	public void putRequest(InnerRequest req) {
		try {
			requestQueue.put(req);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 
	 * 从队列中取出一个DownRequest，nullable
	 * 
	 * @return
	 */
	public InnerRequest pollRequest() {
		return requestQueue.poll();
	}

	public void putConnectableKey(SelectionKey key) {
		try {
			connectableQueue.put(key);
		} catch (InterruptedException e) {
		}
	}

	public SelectionKey pollConnectableKey() {
		return connectableQueue.poll();
	}

	public void putReadableKey(SelectionKey key) {
		try {
			readableQueue.put(key);
		} catch (InterruptedException e) {
		}
	}

	public SelectionKey pollReadableKey() {
		return readableQueue.poll();
	}

	/**
	 * 
	 * 根据请求是同步还是异步，将应答放进相应队列
	 * 
	 * @param req
	 * @param resp
	 */
	public void putResponse(DownRequest req, DownResponse resp) {
		if (req.isSyncReq()) {
			syncRespMap.put(req, resp);
		} else {
			asyncRespMap.put(req, resp);
		}
	}

	/**
	 * 
	 * 获取一个异步DownResponse，若respQueue为空会等待 timeout ms,若 timeout==0 则无限等待
	 * 
	 * @return
	 */
	public DownResponse pollAsyncResponse(int timeout) {
		if (timeout == 0) {
			timeout = Integer.MAX_VALUE;
		}
		int waitTime = 0;
		DownResponse resp = null;
		while (resp == null && waitTime < timeout) {
			long s = System.currentTimeMillis();
			try {
				if (!asyncRespMap.isEmpty()) {
					DownRequest req = asyncRespMap.keySet().iterator().next();
					resp = asyncRespMap.remove(req);
					return resp;
				} else {
					Thread.sleep(1);
				}
			} catch (Exception e) {
				logger.error("[QueueError] - pollResponse error.", e);
			}
			long e = System.currentTimeMillis();
			waitTime += (e - s);
		}
		return resp;
	}

	/**
	 * 
	 * 获取指定DownRequest对应的同步下载DownResponse，若指定DownRequest未下载完会等待 timeout ms,若
	 * timeout==0 则无限等待
	 * 
	 * @param req
	 * @param timeout
	 * @return
	 */
	public DownResponse pollSyncResponse(DownRequest req, int timeout) {
		if (timeout == 0) {
			timeout = Integer.MAX_VALUE;
		}
		int waitTime = 0;
		DownResponse resp = null;
		while (resp == null && waitTime < timeout) {
			long s = System.currentTimeMillis();
			try {
				resp = syncRespMap.get(req);
				if (resp == null) {
					Thread.sleep(1);
				}
			} catch (Exception e) {
				logger.error("[QueueError] - pollSyncResponse error.", e);
			}
			long e = System.currentTimeMillis();
			waitTime += (e - s);
		}
		return resp;
	}

	public void cleanUp() {
		requestQueue.clear();
		connectableQueue.clear();
		readableQueue.clear();
		asyncRespMap.clear();
	}

	public int getRequestQueueSize() {
		return requestQueue.size();
	}

	public int getConnectableQueueSize() {
		return connectableQueue.size();
	}

	public int getReadableQueueSize() {
		return readableQueue.size();
	}
}
