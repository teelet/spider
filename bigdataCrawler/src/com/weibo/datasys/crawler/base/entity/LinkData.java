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
 * url：种子url<br>
 * normalizeurl：归一化url<br>
 * taskid:所属任务
 * state：link状态：0=正常，-1=黑名单，1=等待更新
 * depth：种子爬取深度
 * 
 * @author zouyandi
 * 
 */
public class LinkData extends CommonData {

	private static final long serialVersionUID = 1L;

	public LinkData() {
		super("url", "normalizeurl", "fetchtime", "state", "depth", "taskid");
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

	public void setFetchTime(long fetchtime) {
		setBaseField("fetchtime", fetchtime);
	}

	public long getFetchTimel() {
		return StringUtils.parseLong(getBaseField("fetchtime"), 0);
	}

	public void setState(int state) {
		setBaseField("state", state);
	}

	public void setDepth(int depth) {
		setBaseField("depth", depth);
	}

	public int getDepth() {
		return StringUtils.parseInt(getBaseField("depth"), 0);
	}

	public int getState() {
		return StringUtils.parseInt(getBaseField("state"), 0);
	}
	
	public String getTaskId(){
		return getBaseField("taskid");
	}
	
	public void setTaskId(String taskid){
		setBaseField("taskid",taskid);
	}

}
