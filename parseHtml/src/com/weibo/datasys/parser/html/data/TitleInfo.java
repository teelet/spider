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

public class TitleInfo
{


	/**原始title*/
	private String rawTitle ="";
	
	/**归一化后的title*/
	private String normallizeTitle ="";
	
	/**title长度*/
	private Integer len =0;
	
	/**字符数*/
	private Integer charNum =0;
	
	/**汉字数*/
	private Integer chnNum =0;
	
	/**非常用字数*/
	private Integer nonCommonChnNum=0;
	
	/**英文数*/
	private Integer enNum =0;
	
	/**空白数*/
	private Integer spaceNum =0;
	
	/**数字个数*/
	private Integer digitNum =0;
	
	/**其它字符数*/
	private Integer otherCharNum =0;
	
	/**分词后的个数*/
	private Integer segtitleNum =0;
	
	
	public String getRawTitle() {
		return rawTitle;
	}
	
	public void setRawTitle(String rawTitle) {
		this.rawTitle = rawTitle;
	}
	
	public String getNormallizeTitle() {
		return normallizeTitle;
	}
	
	public void setNormallizeTitle(String normallizeTitle) {
		this.normallizeTitle = normallizeTitle;
	}
	
	public Integer getLen() {
		return len;
	}
	
	public void setLen(Integer len) {
		this.len = len;
	}
	
	public Integer getCharNum() {
		return charNum;
	}
	
	public void setCharNum(Integer charNum) {
		this.charNum = charNum;
	}
	
	public Integer getChnNum() {
		return chnNum;
	}
	
	public void setChnNum(Integer chnNum) {
		this.chnNum = chnNum;
	}
	
	public Integer getEnNum() {
		return enNum;
	}
	
	public void setEnNum(Integer enNum) {
		this.enNum = enNum;
	}
	
	public Integer getSpaceNum() {
		return spaceNum;
	}
	
	public void setSpaceNum(Integer spaceNum) {
		this.spaceNum = spaceNum;
	}
	
	public Integer getDigitNum() {
		return digitNum;
	}
	
	public void setDigitNum(Integer digitNum) {
		this.digitNum = digitNum;
	}
	
	public Integer getOtherCharNum() {
		return otherCharNum;
	}
	
	public void setOtherCharNum(Integer otherCharNum) {
		this.otherCharNum = otherCharNum;
	}
	
	public Integer getNonCommonChnNum() {
		return nonCommonChnNum;
	}
	
	public void setNonCommonChnNum(Integer nonCommonChnNum) {
		this.nonCommonChnNum = nonCommonChnNum;
	}
	
	public Integer getSegtitleNum() {
		return segtitleNum;
	}
	
	public void setSegtitleNum(Integer segtitleNum) {
		this.segtitleNum = segtitleNum;
	}
	
	@Override
	public String toString() {
		return "TitleInfo [charNum=" + charNum + ", chnNum=" + chnNum
				+ ", digitNum=" + digitNum + ", enNum=" + enNum + ", len="
				+ len + ", nonCommonChnNum=" + nonCommonChnNum
				+ ", normallizeTitle=" + normallizeTitle + ", otherCharNum="
				+ otherCharNum + ", rawTitle=" + rawTitle + ", segtitleNum="
				+ segtitleNum + ", spaceNum=" + spaceNum + "]";
	}
	
	
	
	

}
