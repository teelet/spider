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

public class MainTextInfo
{

	/**原始正文*/
	private String rawMainText ="";
	
	/**归一化后的正文*/
	private String normallizeMainText ="";
	
	/**正文长度*/
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
	private Integer segTextNum =0;
	
	@Override
	public String toString() {
		return "MainTextInfo [charNum=" + charNum + ", chnNum=" + chnNum
				+ ", digitNum=" + digitNum + ", enNum=" + enNum + ", len="
				+ len + ", nonCommonChnNum=" + nonCommonChnNum
				+ ", normallizeMainText=" + normallizeMainText
				+ ", otherCharNum=" + otherCharNum + ", rawMainText="
				+ rawMainText + ", segTextNum=" + segTextNum + ", spaceNum="
				+ spaceNum + "]";
	}

	public String getRawMainText() {
		return rawMainText;
	}

	public void setRawMainText(String rawMainText) {
		this.rawMainText = rawMainText;
	}

	public String getNormallizeMainText() {
		return normallizeMainText;
	}

	public void setNormallizeMainText(String normallizeMainText) {
		this.normallizeMainText = normallizeMainText;
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

	public Integer getNonCommonChnNum() {
		return nonCommonChnNum;
	}

	public void setNonCommonChnNum(Integer nonCommonChnNum) {
		this.nonCommonChnNum = nonCommonChnNum;
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

	public Integer getSegTextNum() {
		return segTextNum;
	}

	public void setSegTextNum(Integer segTextNum) {
		this.segTextNum = segTextNum;
	}

	

}
