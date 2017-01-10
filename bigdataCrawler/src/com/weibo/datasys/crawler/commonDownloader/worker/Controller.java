/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader.worker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.crawler.commonDownloader.CommonDownloader;
import com.weibo.datasys.crawler.commonDownloader.entity.DownRequest;
import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.commonDownloader.entity.InnerRequest;
import com.weibo.datasys.crawler.commonDownloader.manager.QueueManager;
import com.weibo.datasys.crawler.commonDownloader.util.DnsCache;
import com.weibo.datasys.crawler.commonDownloader.util.SelectionKeyComparator;

public class Controller implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(Controller.class);

	private Selector selector;

	private CommonDownloader downloader;

	private QueueManager queueManager;

	private Map<SelectionKey, ByteArrayOutputStream> incompleteKeyMap;

	private List<Thread> workThreads = new ArrayList<Thread>();

	private Set<SelectionKey> monitoredKeys;

	private DnsCache dnsCache;

	private AtomicLong upBytes = new AtomicLong();

	private AtomicLong downBytes = new AtomicLong();

	/**
	 * 
	 * 根据commonDownloader指定的参数启动Controller及其它工作线程
	 * 
	 * @param commonDownloader
	 * @throws IOException
	 */
	public Controller(CommonDownloader commonDownloader) throws IOException {
		this.downloader = commonDownloader;
		// 打开Selector
		selector = Selector.open();

		monitoredKeys = new ConcurrentSkipListSet<SelectionKey>(
				new SelectionKeyComparator());

		// 初始化队列
		queueManager = new QueueManager();
		// 初始化读取中key的buffer
		incompleteKeyMap = new ConcurrentHashMap<SelectionKey, ByteArrayOutputStream>();
		// 初始化DNSCache
		dnsCache = new DnsCache(downloader.getDnsIP(), downloader.getDnsPort(),
				downloader.isUseDNMS(), downloader.isUseLocalDNS());
		// 启动各种工作线程
		this.startWorkThreads();
	}

	@Override
	public void run() {
		while (!downloader.isClosed()) {
			try {
				int selectedCount = selector.select(1);
				if (selectedCount > 0) {
					// logger
					// .debug(
					// "selected {}/{} keys. {} monitoring. {} unprocessed. {} connectable. {} readable.",
					// new Object[] {
					// selectedCount,
					// selector.keys().size(),
					// selectorKeys.size(),
					// queueManager.getRequestQueueSize(),
					// queueManager
					// .getConnectableQueueSize(),
					// queueManager.getReadableQueueSize() });
					Set<SelectionKey> keys = selector.selectedKeys();
					Iterator<SelectionKey> keysIterator = keys.iterator();
					while (keysIterator.hasNext()) {
						SelectionKey key = keysIterator.next();
						// 锁定key，避免并发错误
						synchronized (key) {
							// 判断key是否有效
							if (key.isValid()) {
								// 设置key的关心操作集为0，避免在处理过程中该key被重复select
								key.interestOps(0);
								if (key.isConnectable()) {
									// 该key可连接则放进队列以完成连接
									queueManager.putConnectableKey(key);
								} else if (key.isReadable()) {
									// 该key可读放进队列以完成读取
									queueManager.putReadableKey(key);
								}
							}
						}
						keysIterator.remove();
					}// end of foreach selectedKey
				} else {
					// 没select到key则休息
					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}
				}
			} catch (Exception e) {
				logger.error("[controllerError] - run() error.", e);
			}
		}
	}

	/**
	 * 
	 * 注册一个未完成读取的SelectionKey，并创建与该Key相关的respBuffer，以便将来的某个时刻继续读取resp。
	 * 若该Key已被注册则直接返回其respBuffer
	 * 
	 * @param key
	 * @return 与该Key相关的respBuffer
	 */
	public ByteArrayOutputStream regIncompleteKey(SelectionKey key) {
		ByteArrayOutputStream buffer = incompleteKeyMap.get(key);
		if (buffer == null) {
			buffer = new ByteArrayOutputStream();
			this.incompleteKeyMap.put(key, buffer);
		}
		return buffer;
	}

	/**
	 * 
	 * 反注册指定SelectionKey，表明此Key已完成读取，此操作包括将key的关心操作集设为0，从incompleteKeyMap里移除key
	 * 
	 * @param key
	 */
	public void unRegIncompleteKey(SelectionKey key) {
		incompleteKeyMap.remove(key);
	}

	/**
	 * 
	 * 构造一个带有指定异常的DownResponse放进应答队列，该异常包含在指定的DownRequest中，
	 * 若指定的SelectionKey不为null，则会注销之，会阻塞
	 * 
	 * @param req
	 *            带有异常的InnerRequest
	 * 
	 * @param key
	 */
	public void putExceptionResp(InnerRequest req, SelectionKey key) {
		DownResponse resp = req.getResp();
		if (resp == null)
			resp = new DownResponse(req);
		this.putNormalResp(req, resp, key);
	}

	/**
	 * 
	 * 将指定DownResponse放进应答队列，并把相关的SelectionKey注销，会阻塞
	 * 
	 * @param req
	 * @param resp
	 * @param key
	 */
	public void putNormalResp(InnerRequest req, DownResponse resp,
			SelectionKey key) {
		if (key != null) {
			try {
				key.channel().close();
			} catch (Exception e) {
			}
			key.cancel();
		}
		// 记录处理结束时间
		req.setEndProcessTime(System.currentTimeMillis());
		// 将结果放进队列
		queueManager.putResponse(req.getOriginalReq(), resp);
	}

	/**
	 * 关闭跟控制器相关的资源，包括停止所有工作线程，使程序可以安全退出
	 */
	public void close() {
		// 关闭Selector
		try {
			selector.close();
		} catch (Exception e) {
			logger.error("[ControllerError] - close() error.", e);
		}
		// 关闭dns缓存
		if (dnsCache != null)
			dnsCache.close();
		// 清空所有队列
		queueManager.cleanUp();
	}

	private void startWorkThreads() {
		// 启动连接和写请求线程
		for (int i = 1; i <= downloader.getConnectorNum(); i++) {
			Connector connector = new Connector(this);
			Thread connThread = new Thread(connector, "connThread - " + i);
			workThreads.add(connThread);
			connThread.start();
			Thread writerThread = new Thread(new Writer(this),
					"writerThread - " + i);
			workThreads.add(writerThread);
			writerThread.start();
		}

		// 启动读取应答线程
		for (int i = 1; i <= downloader.getReaderNum(); i++) {
			Thread readerThread = new Thread(new Reader(this),
					"readerThread - " + i);
			workThreads.add(readerThread);
			readerThread.start();
		}

		// 启动连接超时监控线程
		Thread connMonitorThread = new Thread(new ConnectTimeMonitor(this),
				"connMonitorThread");
		workThreads.add(connMonitorThread);
		connMonitorThread.start();

		// 启动读取超时监控线程
		Thread readMonitorThread = new Thread(new ReadTimeMonitor(this),
				"readMonitorThread");
		workThreads.add(readMonitorThread);
		readMonitorThread.start();

		// 启动控制线程
		Thread controlThread = new Thread(this, "ctrlThread");
		workThreads.add(controlThread);
		controlThread.start();
	}

	/**
	 * 
	 * 获取一个异步DownResponse，若没有可用的DownResponse会等待 timeout ms,若 timeout==0 则无限等待
	 * 
	 * @return
	 */
	public DownResponse getAsyncResponse(int timeout) {
		return queueManager.pollAsyncResponse(timeout);
	}

	/**
	 * 
	 * 获取一个同步DownResponse，若没有可用的DownResponse会等待 timeout ms,若 timeout==0 则无限等待
	 * 
	 * @return
	 */
	public DownResponse getSyncResponse(DownRequest req, int timeout) {
		return queueManager.pollSyncResponse(req, timeout);
	}

	public void addRequest(InnerRequest req) {
		queueManager.putRequest(req);
	}

	public CommonDownloader getDownloader() {
		return this.downloader;
	}

	public InnerRequest pollRequest() {
		return queueManager.pollRequest();
	}

	public void putConnectableKey(SelectionKey key) {
		queueManager.putConnectableKey(key);
	}

	public SelectionKey pollConnectableKey() {
		return queueManager.pollConnectableKey();
	}

	public void putReadableKey(SelectionKey key) {
		queueManager.putReadableKey(key);
	}

	public SelectionKey pollReadableKey() {
		return queueManager.pollReadableKey();
	}

	public Selector getSelector() {
		return selector;
	}

	/**
	 * 
	 * 获取正在被监视(连接或读取超时监视)的Keys
	 * 
	 * @return
	 */
	public Set<SelectionKey> getMonitoredKeys() {
		return monitoredKeys;
	}

	public DnsCache getDnsCache() {
		return dnsCache;
	}

	public void addUpBytes(long b) {
		upBytes.addAndGet(b);
	}

	public void addDownBytes(long b) {
		downBytes.addAndGet(b);
	}

	public long getUpBytes() {
		return upBytes.get();
	}

	public long getDownBytes() {
		return downBytes.get();
	}
}
