/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.weibo.datasys.crawler.base.crawlUnit.deduplicator.AbstractDeduplicator;
import com.weibo.datasys.crawler.base.crawlUnit.fetcher.AbstractFetcher;
import com.weibo.datasys.crawler.base.crawlUnit.preparser.AbstractParser;
import com.weibo.datasys.crawler.base.crawlUnit.saver.AbstractSaver;
import com.weibo.datasys.crawler.base.crawlUnit.seedProvider.AbstractSeedProvider;
import com.weibo.datasys.crawler.impl.strategy.CrawlStrategy;
import com.weibo.datasys.crawler.impl.strategy.ParseStrategy;
import com.weibo.datasys.crawler.impl.strategy.SaveStrategy;

public class Task {

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public enum TaskStatus {
		RUNNING, STOP
	}

	protected TaskStatus status = TaskStatus.STOP;

	private long startTime;

	private String taskId;

	private String taskName;

	private long taskCycle;

	private CrawlStrategy crawlStrategy;

	private ParseStrategy parseStrategy;

	private SaveStrategy saveStrategy;

	private AbstractFetcher fetcher;

	private AbstractParser parser;

	private AbstractSaver saver;

	private AbstractSeedProvider seedProvider;
	
	private AbstractDeduplicator deduplicator;

	private String taskXML;

	@Override
	public boolean equals(Object o) {
		return ((Task) o).taskId.equals(this.taskId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Task - [id=").append(taskId).append(" | name=").append(
				taskName).append(" | priority=").append(
				crawlStrategy.getPriority()).append(" | status=")
				.append(status).append(" | start=").append(
						sdf.format(new Date(startTime))).append("]");
		return builder.toString();
	}

	/**
	 * @return 该Task是否正在运行
	 */
	public boolean isRunning() {
		synchronized (status) {
			return this.status.equals(TaskStatus.RUNNING);
		}
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(TaskStatus status) {
		synchronized (status) {
			this.status = status;
		}
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId
	 *            the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName
	 *            the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the taskCycle
	 */
	public long getTaskCycle() {
		return taskCycle;
	}

	/**
	 * @param taskCycle
	 *            the taskCycle to set
	 */
	public void setTaskCycle(long taskCycle) {
		this.taskCycle = taskCycle;
	}

	/**
	 * @return the crawlStrategy
	 */
	public CrawlStrategy getCrawlStrategy() {
		return crawlStrategy;
	}

	/**
	 * @param crawlStrategy
	 *            the crawlStrategy to set
	 */
	public void setCrawlStrategy(CrawlStrategy crawlStrategy) {
		this.crawlStrategy = crawlStrategy;
	}

	/**
	 * @return the parseStrategy
	 */
	public ParseStrategy getParseStrategy() {
		return parseStrategy;
	}

	/**
	 * @param parseStrategy
	 *            the parseStrategy to set
	 */
	public void setParseStrategy(ParseStrategy parseStrategy) {
		this.parseStrategy = parseStrategy;
	}

	/**
	 * @return the saveStrategy
	 */
	public SaveStrategy getSaveStrategy() {
		return saveStrategy;
	}

	/**
	 * @param saveStrategy
	 *            the saveStrategy to set
	 */
	public void setSaveStrategy(SaveStrategy saveStrategy) {
		this.saveStrategy = saveStrategy;
	}

	/**
	 * @return the fetcher
	 */
	public AbstractFetcher getFetcher() {
		return fetcher;
	}

	/**
	 * @param fetcher
	 *            the fetcher to set
	 */
	public void setFetcher(AbstractFetcher fetcher) {
		this.fetcher = fetcher;
	}

	/**
	 * @return the parser
	 */
	public AbstractParser getParser() {
		return parser;
	}

	/**
	 * @param parser
	 *            the parser to set
	 */
	public void setParser(AbstractParser parser) {
		this.parser = parser;
	}

	/**
	 * @return the saver
	 */
	public AbstractSaver getSaver() {
		return saver;
	}

	/**
	 * @param saver
	 *            the saver to set
	 */
	public void setSaver(AbstractSaver saver) {
		this.saver = saver;
	}

	/**
	 * @return the taskXML
	 */
	public String getTaskXML() {
		return taskXML;
	}

	/**
	 * @param taskXML
	 *            the taskXML to set
	 */
	public void setTaskXML(String taskXML) {
		this.taskXML = taskXML;
	}

	/**
	 * @return the seedProvider
	 */
	public AbstractSeedProvider getSeedProvider() {
		return seedProvider;
	}

	/**
	 * @param seedProvider
	 *            the seedProvider to set
	 */
	public void setSeedProvider(AbstractSeedProvider seedProvider) {
		this.seedProvider = seedProvider;
	}

	/**
	 * @return the deduplicator
	 */
	public AbstractDeduplicator getDeduplicator() {
		return deduplicator;
	}

	/**
	 * @param deduplicator the deduplicator to set
	 */
	public void setDeduplicator(AbstractDeduplicator deduplicator) {
		this.deduplicator = deduplicator;
	}

}
