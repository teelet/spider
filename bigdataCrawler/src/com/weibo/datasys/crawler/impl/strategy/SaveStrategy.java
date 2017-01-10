/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy;

import java.util.ArrayList;
import java.util.List;

import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.AbstractStrategy;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;

public class SaveStrategy extends AbstractStrategy {
	
	private String seedDS;
	
	private String seedDB;
	
	private String seedTable;
	
	private String linkDS;
	
	private String linkDB;
	
	private String linkTable;
	
	private String pageDS;
	
	private String pageDB;
	
	private String pageTable;

	private List<AbstractSaveRule> saveRules = new ArrayList<AbstractSaveRule>();

	public SaveStrategy(Task task) {
		super(task);
	}

	/**
	 * 
	 * 添加一个存储规则
	 * 
	 * @param saveRule
	 */
	public void addSaveRule(AbstractSaveRule saveRule) {
		this.saveRules.add(saveRule);
	}

	/**
	 * @return the saveRules
	 */
	public List<AbstractSaveRule> getSaveRules() {
		return saveRules;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SaveStrategyInfo - [taskId=").append(
				this.task.getTaskId()).append(" | saveRules=")
				.append(saveRules);
		return builder.toString();
	}

	/**
	 * @return the seedDS
	 */
	public String getSeedDS() {
		return seedDS;
	}

	/**
	 * @param seedDS the seedDS to set
	 */
	public void setSeedDS(String seedDS) {
		this.seedDS = seedDS;
	}

	/**
	 * @return the seedDB
	 */
	public String getSeedDB() {
		return seedDB;
	}

	/**
	 * @param seedDB the seedDB to set
	 */
	public void setSeedDB(String seedDB) {
		this.seedDB = seedDB;
	}

	/**
	 * @return the seedTable
	 */
	public String getSeedTable() {
		return seedTable;
	}

	/**
	 * @param seedTable the seedTable to set
	 */
	public void setSeedTable(String seedTable) {
		this.seedTable = seedTable;
	}

	/**
	 * @return the linkDS
	 */
	public String getLinkDS() {
		return linkDS;
	}

	/**
	 * @param linkDS the linkDS to set
	 */
	public void setLinkDS(String linkDS) {
		this.linkDS = linkDS;
	}

	/**
	 * @return the linkDB
	 */
	public String getLinkDB() {
		return linkDB;
	}

	/**
	 * @param linkDB the linkDB to set
	 */
	public void setLinkDB(String linkDB) {
		this.linkDB = linkDB;
	}

	/**
	 * @return the linkTable
	 */
	public String getLinkTable() {
		return linkTable;
	}

	/**
	 * @param linkTable the linkTable to set
	 */
	public void setLinkTable(String linkTable) {
		this.linkTable = linkTable;
	}

	/**
	 * @return the pageDS
	 */
	public String getPageDS() {
		return pageDS;
	}

	/**
	 * @param pageDS the pageDS to set
	 */
	public void setPageDS(String pageDS) {
		this.pageDS = pageDS;
	}

	/**
	 * @return the pageDB
	 */
	public String getPageDB() {
		return pageDB;
	}

	/**
	 * @param pageDB the pageDB to set
	 */
	public void setPageDB(String pageDB) {
		this.pageDB = pageDB;
	}

	/**
	 * @return the pageTable
	 */
	public String getPageTable() {
		return pageTable;
	}

	/**
	 * @param pageTable the pageTable to set
	 */
	public void setPageTable(String pageTable) {
		this.pageTable = pageTable;
	}

}
