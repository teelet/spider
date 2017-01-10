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

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.StringUtils;

/**
 * 
 * id：MD5(normalizeurl_taskid)<br>
 * urlid：MD5(normalizeurl)<br>
 * taskid：种子所属任务id<br>
 * url：种子url<br>
 * normalizeurl：归一化url<br>
 * level：种子所属层级<br>
 * state：种子爬取状态：0=首次采集，1=更新采集，2=采集完成，-n=失败n次 depth：种子爬取深度
 * 
 * 
 */
public class SeedData extends CommonData {

	private static final long serialVersionUID = 1L;

	private int initState = Integer.MAX_VALUE;

	public SeedData() {
		super("urlid", "taskid", "url", "normalizeurl", "level", "state",
				"depth");
	}

	public void setUrlId(String urlid) {
		setBaseField("urlid", urlid);
	}

	public String getUrlId() {
		return getBaseField("urlid");
	}

	public void setTaskId(String taskId) {
		setBaseField("taskid", taskId);
	}

	public String getTaskId() {
		return getBaseField("taskid");
	}

	public void setUrl(String url) {
		setBaseField("url", url);
	}

	public String getUrl() {
		return getBaseField("url");
	}

	public void setNormalizeUrl(String url) {
		setBaseField("normalizeurl", url);
	}

	public String getNormalizeUrl() {
		return getBaseField("normalizeurl");
	}

	public void setLevel(int level) {
		setBaseField("level", level);
	}

	public int getLevel() {
		return StringUtils.parseInt(getBaseField("level"), 0);
	}

	public void setState(int state) {
		setBaseField("state", state);
	}

	public int getState() {
		return StringUtils.parseInt(getBaseField("state"), 0);
	}

	public void setDepth(int depth) {
		setBaseField("depth", depth);
	}

	public int getDepth() {
		return StringUtils.parseInt(getBaseField("depth"), 0);
	}

	public boolean isInstant() {
		return StringUtils.parseBoolean(getExtendField("isinstant"), false);
	}

	public void setInstant(boolean isInstant) {
		setExtendField("isinstant", isInstant);
	}

	@Override
	public int hashCode() {
		// state用于区分失败种子与正常种子
		// state<0的种子，允许再次爬取，属于不同hash，其余情况属于相同的种子
		if (initState == Integer.MAX_VALUE) {
			initState = getState();
		}
		boolean isSeedDifferent = initState < 0;
		int hashCode = id.hashCode();
		if (isSeedDifferent) {
			hashCode = (id + "_" + initState).hashCode();
		}
		return hashCode;
	}

}
