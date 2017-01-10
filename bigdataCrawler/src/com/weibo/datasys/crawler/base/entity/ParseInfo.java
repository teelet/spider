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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.weibo.datasys.common.data.CommonData;

public class ParseInfo {

	private CrawlInfo thisCrawlInfo;

	private PageData pageData;

	private Set<SeedData> outlinks;

	/**
	 * key=fieldname<br>
	 * value=list of fields
	 */
	private Map<String, List<CommonData>> fields = new HashMap<String, List<CommonData>>();

	private boolean isFetchOK = false;

	private boolean isParseOK = false;

	/**
	 * 
	 * 添加一个抽取完成的Field
	 * 
	 * @param field
	 */
	public void addField(CommonData field) {
		List<CommonData> fieldList = this.fields.get(field
				.getExtendField("fieldname"));
		if (fieldList == null) {
			fieldList = new ArrayList<CommonData>();
			this.fields.put(field.getExtendField("fieldname"), fieldList);
		}
		fieldList.add(field);
	}

	/**
	 * 
	 * 添加一组Field
	 * 
	 * @param fields
	 * @author zouyandi
	 */
	public void addFields(List<CommonData> fields) {
		for (CommonData field : fields) {
			this.addField(field);
		}
	}

	/**
	 * @return 所有抽取完成的Field
	 */
	public Map<String, List<CommonData>> getFields() {
		return this.fields;
	}

	/**
	 * @param fieldName
	 * @return 指定名称的Field的集合
	 * @author zouyandi
	 */
	public List<CommonData> getFields(String fieldName) {
		return this.fields.get(fieldName);
	}

	/**
	 * @return the thisCrawlInfo
	 */
	public CrawlInfo getThisCrawlInfo() {
		return thisCrawlInfo;
	}

	/**
	 * @param thisCrawlInfo
	 *            the thisCrawlInfo to set
	 */
	public void setThisCrawlInfo(CrawlInfo thisCrawlInfo) {
		this.thisCrawlInfo = thisCrawlInfo;
	}

	/**
	 * @return the outlinks
	 */
	public Set<SeedData> getOutlinks() {
		return outlinks;
	}

	/**
	 * @param outlinks
	 *            the outlinks to set
	 */
	public void setOutlinks(Set<SeedData> outlinks) {
		this.outlinks = outlinks;
	}

	/**
	 * @return the pageData
	 */
	public PageData getPageData() {
		return pageData;
	}

	/**
	 * @param pageData
	 *            the pageData to set
	 */
	public void setPageData(PageData pageData) {
		this.pageData = pageData;
	}

	/**
	 * @return the isFetchOK
	 */
	public boolean isFetchOK() {
		return isFetchOK;
	}

	/**
	 * @param isFetchOK
	 *            the isFetchOK to set
	 */
	public void setFetchOK(boolean isFetchOK) {
		this.isFetchOK = isFetchOK;
	}

	/**
	 * @return the isParseOK
	 */
	public boolean isParseOK() {
		return isParseOK;
	}

	/**
	 * @param isParseOK
	 *            the isParseOK to set
	 */
	public void setParseOK(boolean isParseOK) {
		this.isParseOK = isParseOK;
	}

}
