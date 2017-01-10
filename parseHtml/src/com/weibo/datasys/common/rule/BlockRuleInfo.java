package com.weibo.datasys.common.rule;

public class BlockRuleInfo {
	
	private String[] valid;
	private String[] invalid;
	private String[] preserved;
	private String[] link;
	private String[] img;
	private String[] font;
	
	public String[] getValid() {
		return valid;
	}
	public void setValid(String[] valid) {
		this.valid = valid;
	}
	public String[] getInvalid() {
		return invalid;
	}
	public void setInvalid(String[] invalid) {
		this.invalid = invalid;
	}
	public String[] getPreserved() {
		return preserved;
	}
	public void setPreserved(String[] preserved) {
		this.preserved = preserved;
	}
	public String[] getLink() {
		return link;
	}
	public void setLink(String[] link) {
		this.link = link;
	}
	public String[] getImg() {
		return img;
	}
	public void setImg(String[] img) {
		this.img = img;
	}
	public String[] getFont() {
		return font;
	}
	public void setFont(String[] font) {
		this.font = font;
	}
	
	

}
