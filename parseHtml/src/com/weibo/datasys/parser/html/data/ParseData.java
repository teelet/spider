/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.data;

import java.util.ArrayList;
import java.util.HashMap;

public class ParseData
{
	private String id;

	private String origUrl = "";

	private String uniurl = "";

	private String origtitle = "";

	private String title = "";

	private String content = "";

	private String encoding = "";

	private String keyword = "";

	private String description = "";

	private String originSite = "";

	private String originPublish = "";
	
	private String originAuthor = "";
	private String taskId = "";
	private String previousPage = "";
	private String nextPage = "";

	public String getOriginAuthor() {
		return originAuthor;
	}

	public void setOriginAuthor(String originAuthor) {
		this.originAuthor = originAuthor;
	}

	private long pubtime = 0;

	private long fetchtime = 0;

	private int category = 0;

	private int ctsize = 0;

	private int province = 0;

	private int column = 0;

	private int pagetype = 0;

	private int language = 0;

	private int crawlerstate = 5; // 爬虫过滤2，解析过滤3(没有主机的图片url 31，异常32, 没正文 34， 有object 35)，超时过滤4，其他过滤5

	private int parsestate = 0; // 正常解析0，标题和正文均空1，仅标题空2，仅正文空3，标题正文重复4，标题黑名单过滤5
	
	private HashMap<String,String> contentImg = new HashMap<String,String>();
	private HashMap<String,String> href = new HashMap<String,String>();
	private PageData pageData;

	public ParseData()
	{

	}

	public ParseData(PageData pageData)
	{
		id = pageData.getId();
		this.pageData = pageData;
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getOrigUrl()
	{
		return origUrl;
	}

	public void setOrigUrl(String origUrl)
	{
		this.origUrl = origUrl;
	}

	public String getUniurl()
	{
		return uniurl;
	}

	public void setUniurl(String uniurl)
	{
		this.uniurl = uniurl;
	}

	public String getOrigtitle()
	{
		return origtitle;
	}

	public void setOrigtitle(String origtitle)
	{
		this.origtitle = origtitle;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public String getKeyword()
	{
		return keyword;
	}

	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public int getCategory()
	{
		return category;
	}

	public void setCategory(int category)
	{
		this.category = category;
	}

	public long getPubtime()
	{
		return pubtime;
	}

	public void setPubtime(long pubtime)
	{
		this.pubtime = pubtime;
	}

	public long getFetchtime()
	{
		return fetchtime;
	}

	public void setFetchtime(long fetchtime)
	{
		this.fetchtime = fetchtime;
	}

	public int getCtsize()
	{
		return ctsize;
	}

	public void setCtsize(int ctsize)
	{
		this.ctsize = ctsize;
	}

	public int getProvince()
	{
		return province;
	}

	public void setProvince(int province)
	{
		this.province = province;
	}

	public int getColumn()
	{
		return column;
	}

	public void setColumn(int column)
	{
		this.column = column;
	}

	public int getPagetype()
	{
		return pagetype;
	}

	public void setPagetype(int pagetype)
	{
		this.pagetype = pagetype;
	}

	public int getLanguage()
	{
		return language;
	}

	public void setLanguage(int language)
	{
		this.language = language;
	}

	public String getOriginSite()
	{
		return originSite;
	}

	public void setOriginSite(String originSite)
	{
		this.originSite = originSite;
	}

	public String getOriginPublish()
	{
		return originPublish;
	}

	public void setOriginPublish(String originPublish)
	{
		this.originPublish = originPublish;
	}

	public PageData getPageData()
	{
		return pageData;
	}

	public void setPageData(PageData pageData)
	{
		this.pageData = pageData;
	}

	public int getCrawlerstate()
	{
		return crawlerstate;
	}

	public void setCrawlerstate(int crawlerstate)
	{
		this.crawlerstate = crawlerstate;
	}

	public int getParsestate()
	{
		return parsestate;
	}

	public void setParsestate(int parsestate)
	{
		this.parsestate = parsestate;
	}
	
	public void setContentImg(HashMap<String,String> contentImgs){
		this.contentImg = contentImgs;
	}
	public HashMap<String,String> getContentIms(){
		return contentImg;
	}
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public HashMap<String, String> getHref() {
		return href;
	}

	public void setHref(HashMap<String,String> href){
		this.href = href;
		
		if (this.href != null){
			if (this.href.containsKey("up")){
				String value = this.href.get("up");
				String[] fields = value.split("\t");
				if (fields.length == 2)
					this.previousPage = fields[1];
			}
			if (this.href.containsKey("down")){
				String value = this.href.get("down");
				String[] fields = value.split("\t");
				if (fields.length == 2)
					this.nextPage = fields[1];
			}
		}
	}

	public String getPreviousPage() {
		return previousPage;
	}

	public String getNextPage() {
		return nextPage;
	}

	
	
}
