package com.weibo.datasys.common.rule;

public class ImgRuleInfo {
	
	private String[] filterRoot;
	private String[] filterPSibling;
	private String[] filterNSibling;
	private String[] imageTag;
	public String[] getImageTag() {
		return imageTag;
	}
	public void setImageTag(String[] imageTag) {
		this.imageTag = imageTag;
	}
	public String[] getFilterRoot() {
		return filterRoot;
	}
	public void setFilterRoot(String[] filterRoot) {
		this.filterRoot = filterRoot;
	}
	public String[] getFilterPSibling() {
		return filterPSibling;
	}
	public void setFilterPSibling(String[] filterPSibling) {
		this.filterPSibling = filterPSibling;
	}
	public String[] getFilterNSibling() {
		return filterNSibling;
	}
	public void setFilterNSibling(String[] filterNSibling) {
		this.filterNSibling = filterNSibling;
	}
}
