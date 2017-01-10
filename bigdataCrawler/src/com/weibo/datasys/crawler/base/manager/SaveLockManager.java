/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.manager;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * 管理save线程的锁，用于读写数据库切换
 * 
 */
public class SaveLockManager {

	/**
	 * 标记是否已对save操作锁定，true则不允许再开始新的save操作
	 */
	private static AtomicBoolean isSaveLocked = new AtomicBoolean(false);

	/**
	 * save信号量，大于零表示存在未结束的save操作
	 */
	private static AtomicInteger saveSemaphores = new AtomicInteger();

	/**
	 * 锁定save操作，等待当前进行中的save操作结束后，在锁定期间不允许有新的save操作
	 * 
	 * @author zouyandi
	 */
	public synchronized static void lockSave() {
		if (isSaveLocked.compareAndSet(false, true)) {
			while (saveSemaphores.get() > 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	/**
	 * 解除save操作锁定
	 * 
	 * @author zouyandi
	 */
	public static synchronized void unlockSave() {
		isSaveLocked.compareAndSet(true, false);
	}

	/**
	 * @return save操作是否已锁定
	 * @author zouyandi
	 */
	public static boolean isSaveLocked() {
		return isSaveLocked.get();
	}

	/**
	 * 获取save信号量，获取成功才能开始新的save操作，若已锁定则获取失败
	 * 
	 * @return
	 * @author zouyandi
	 */
	public static boolean getSaveSemaphore() {
		boolean getOK = false;
		if (!isSaveLocked()) {
			getOK = true;
			saveSemaphores.incrementAndGet();
		}
		return getOK;
	}

	/**
	 * save操作结束，释放信号量
	 * 
	 * @author zouyandi
	 */
	public static void releaseSaveSemaphore() {
		saveSemaphores.decrementAndGet();
	}

	/**
	 * 获取当前锁定状态信息
	 * 
	 * @return
	 * @author zouyandi
	 */
	public static String getLockInfo() {
		StringBuilder builder = new StringBuilder();
		builder.append("[isLocked] - ").append(isSaveLocked())
				.append(" | [semaphores] - ").append(saveSemaphores);
		return builder.toString();
	}

}
