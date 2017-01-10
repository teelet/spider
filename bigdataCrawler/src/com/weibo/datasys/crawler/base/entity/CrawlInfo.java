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

import com.weibo.datasys.crawler.commonDownloader.entity.DownResponse;
import com.weibo.datasys.crawler.base.manager.TaskManager;

public class CrawlInfo {

	private SeedData seedData;

	private DownResponse resp;

	private String html;

	public CrawlInfo(SeedData seedData) {
		this.seedData = seedData;
	}

	/**
	 * @return the resp
	 */
	public DownResponse getResp() {
		return resp;
	}

	/**
	 * @param resp
	 * @return 是否已完成下载
	 */
	public boolean setResp(DownResponse resp) {
		this.resp = resp;
		return isComplete();
	}

	/**
	 * @return 该爬取信息是否已完成爬取
	 */
	public boolean isComplete() {
		return resp != null;
	}

	/**
	 * @return the seedData
	 */
	public SeedData getSeedData() {
		return seedData;
	}

	/**
	 * 
	 * 检查当前CrawlInfo是否有效，当CrawlInfo所属Task被删除或停止时，CrawlInfo无效
	 * 
	 * @return 有效则返回有效的Task，无效则返回null
	 */
	public Task getValidTask() {
		Task task = TaskManager.getTask(seedData.getTaskId());
		if (task == null || !task.isRunning()) {
			task = null;
		}
		return task;
	}

	/**
	 * @return the html
	 */
	public String getHtml() {
		return html;
	}

	/**
	 * @param html
	 *            the html to set
	 */
	public void setHtml(String html) {
		this.html = html;
	}

}
