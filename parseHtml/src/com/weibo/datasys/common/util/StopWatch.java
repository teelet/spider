/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.util;

/**
 * 
 * 时间间隔计算工具类
 *
 */
public class StopWatch {

	private long start = 0;
	private long stop = 0;
	private boolean running = false;
	
	/**
	 * 启动
	 */
	public void start() {
		this.start = System.currentTimeMillis();
		this.running = true;
	}
	
	/**
	 * 停止
	 */
	public void stop() {
		this.stop = System.currentTimeMillis();
		this.running = false;
	}

	/**
	 * 获取以豪秒为单位的时间长度
	 * @return
	 */
	public long getElapsedTime() {
		long elapsed;
		if (running) {
			elapsed = (System.currentTimeMillis() - start);
		} else {
			elapsed = (stop - start);
		}
		return elapsed;
	}

	/**
	 * 获取以秒为单位的时间长度
	 * @return
	 */
	public long getElapsedTimeSecs() {
		long elapsed;
		if (running) {
			elapsed = ((System.currentTimeMillis() - start) / 1000);
		} else {
			elapsed = ((stop - start) / 1000);
		}
		return elapsed;
	}
}
