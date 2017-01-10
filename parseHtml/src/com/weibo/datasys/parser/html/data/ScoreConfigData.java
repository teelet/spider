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

import com.weibo.datasys.parser.html.antispam.KeywordsNormalizer;

public class ScoreConfigData
{

	/** 归一化后的keyword */
	private String metaKeyword = "";

	/** 网页语言类型 */
	private Integer language = 0;

	private double threshold = 0.0;
	
	public final static Double NO_HOLD = -1.0;

	public final static Double LOW_HOLD = 0.5;

	public final static Double MIDDLE_HOLD = 1.4;

	public final static Double HIGH_HOLD = 2.0;

	private KeywordsNormalizer keywordsNormalizer = new KeywordsNormalizer();

	public ScoreConfigData(String metaKeyword, Integer language, double threshold)
	{
		setMetaKeyword(metaKeyword);
		this.language = language;
		this.threshold = threshold;
	}

	/**
	 * 默认配置（keywords 为空，网页语言：中文,阈值：0）
	 * 
	 * 
	 * */
	public static ScoreConfigData getDefaultConfig()
	{
		return new ScoreConfigData("", 0, 0);
	}

	public String getMetaKeyword()
	{
		return metaKeyword;
	}

	/** 归一化后的keyword */
	public void setMetaKeyword(String metaKeyword)
	{
		this.metaKeyword = keywordsNormalizer.normalizeString(metaKeyword);
	}

	/** 网页语言类型,默认为0 中文 */
	public Integer getLanguage()
	{
		return language;
	}

	public void setLanguage(Integer language)
	{
		this.language = language;
	}

	public double getThreshold()
	{
		return threshold;
	}

	public void setThreshold(double threshold)
	{
		this.threshold = threshold;
	}

}
