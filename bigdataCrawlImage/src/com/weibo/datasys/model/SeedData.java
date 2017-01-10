package com.weibo.datasys.model;

import com.weibo.datasys.util.StringUtils;

/**
 * 
 * id：<br>>
 * url：种子url<br>
 * normalizeurl：归一化url<br>
 * state：种子爬取状态：0=初始状态，1=进入采集队列，准备爬取，2=采集完成，-1=采集失败
 * 
 * 
 */
public class SeedData extends CommonData {

	private static final long serialVersionUID = 1L;

	public SeedData() {
		super("id", "url","fetchstate", "day","localpath","size");
	}

	public void setId(String id) {
		setBaseField("id", id);
	}

	public String getId() {
		return getBaseField("id");
	}
	

	public void setUrl(String url) {
		setBaseField("url", url);
	}

	public String getUrl() {
		return getBaseField("url");
	}
	
	
	public void setDay(String day){
		setBaseField("day",day);
	}
	
	public String getDay(){
		return getBaseField("day");
	}

	public void setLocalPath(String localPath) {
		setBaseField("localpath", localPath);
	}

	public String getLocalPath() {
		return getBaseField("localpath");
	}


	public void setState(int state) {
		setBaseField("fetchstate", state);
	}

	public int getState() {
		return StringUtils.parseInt(getBaseField("fetchstate"),0);
	}
	
	public void setSize(String size){
		setBaseField("size",size);
	}
	
	public String getSize(){
		return getBaseField("size");
	}

}

