package com.weibo.datasys.work;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseWork implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(BaseWork.class);

	protected boolean isStop = false;

	@Override
	public void run() {
		this.isStop = false;
		logger.info("[WorkThreadStarted] -work={} | thread={}", new Object[] {
				this.getClass().getName(), Thread.currentThread().getName() });
		// 当前Work没有收到停止命令则一直工作
		while (!isStop) {
			try {
				// 执行工作
				doWork();
				// 执行完一次停顿1ms
				Thread.sleep(1);
			} catch (Throwable e) {
				logger.error("[WorkError] - ", e);
				logger.error("[WorkError] - work={} | thread={}", new Object[] {
						this.getClass().getName(),
						Thread.currentThread().getName() });
			}
		}
		logger.info("[WorkThreadStopped] - task={} | work={} | thread={}",
				new Object[] { this, Thread.currentThread().getName() });
	}

	/**
	 * 实际干活的方法，需要子类实现，只需要干一次活，不需要多次循环
	 */
	protected abstract void doWork();

	public synchronized void stopWork() {
		this.isStop = true;
	}

}
