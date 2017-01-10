/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.antispam;

import com.weibo.datasys.parser.util.NormalizerBasic;
import com.weibo.datasys.parser.util.StringUtil;

public class KeywordsNormalizer extends NormalizerBasic
{

	/** 2 特殊分隔符的最少个数 */
	private final static int MIN_SPECIAL_SEPARATOR = 2;

	/** 常规分隔符，基本不出现在短语里的符号 */
	private final static String[] KEYWORDS_GENERAL_SEPARATOR = { ",", "，", "、", ";", "；" };

	/** 可能为分隔符或者短语的一部分 */
	private final static String[] KEYWORDS_SPECIAL_SEPARATOR = { "_", "|", "/", "-" };

	public final static String Splitor = "::sp::";

	private boolean generalSplitFlag = false;

	/**
	 * keywords的归一化主方法
	 * 
	 * @param keywords
	 *            为未经过处理的原始keywords
	 */
	@Override
	public String normalizeString(String keywords)
	{
		// keywords合法性检验
		if (null == keywords || keywords.trim().equals(""))
		{
			return "";
		}
		// 小写转换
		keywords = keywords.toLowerCase().trim();

		// 特殊符号处理
		keywords = normalSpecialChar(keywords);

		// 常用分隔符
		keywords = normalCommonSpliter(keywords);

		// 空白替换处理
		keywords = normalSpaces(keywords);

		// 连续空白处理
		keywords = StringUtil.spaceFormat(keywords);

		// 选择最有可能分隔符
		keywords = normalPossibleSpliter(keywords, !generalSplitFlag);

		return StringUtil.spaceFormat(keywords);
	}

	/**
	 * 特殊符号替换
	 * 
	 * @param keywords
	 *            为未经过处理的原始keywords
	 */
	private String normalSpecialChar(String keywords)
	{

		keywords = keywords.replaceAll("｜", "|");
		return keywords;
	}

	/**
	 * 常用分隔符替换
	 * 
	 * @param keywords
	 *            为未经过处理的原始keywords
	 */
	private String normalCommonSpliter(String keywords)
	{
		for (int i = 0; i < KEYWORDS_GENERAL_SEPARATOR.length; i++)
		{
			if (keywords.contains(KEYWORDS_GENERAL_SEPARATOR[i]))
			{
				generalSplitFlag = true;
				keywords = keywords.replaceAll(KEYWORDS_GENERAL_SEPARATOR[i], Splitor);
			}
		}
		return keywords;
	}

	/**
	 * 选择最有可能分隔符替换
	 * 
	 * @param keywords
	 *            为未经过处理的原始keywords
	 */
	private String normalPossibleSpliter(String keywords, boolean containsCommon)
	{
		if (containsCommon)
		{
			return keywords;
		}

		// 计算空格数量
		int spaceNum = StringUtil.getNumOfString(keywords, " ") + 1;
		int tempNum = 0;
		String tempSplitStr;
		String maxSpliter = null;
		// 获得出现次数最多的分隔符
		for (int k = 0; k < KEYWORDS_SPECIAL_SEPARATOR.length; k++)
		{
			tempSplitStr = KEYWORDS_SPECIAL_SEPARATOR[k];
			// keywords中去除连续分隔符后，计算分隔符的数量
			if (tempSplitStr.equals("|") || tempSplitStr.equals(".") || tempSplitStr.equals("/"))
			{
				tempNum = StringUtil.getNumOfString(keywords.replaceAll("(\\" + tempSplitStr + ")+",
						"\\" + tempSplitStr), tempSplitStr) + 1;
			} else
			{
				tempNum = StringUtil.getNumOfString(keywords.replaceAll("(" + tempSplitStr + ")+",
						tempSplitStr), tempSplitStr) + 1;
			}

			maxSpliter = (spaceNum < tempNum && tempNum > MIN_SPECIAL_SEPARATOR) ? tempSplitStr
					: maxSpliter;
		}

		if (maxSpliter != null)
		{// 替换非常规的分隔符为空格
			if (maxSpliter.equals("|") || maxSpliter.equals(".") || maxSpliter.equals("/"))
			{
				keywords = keywords.replaceAll("(\\" + maxSpliter + ")+", Splitor);
			} else
			{
				keywords = keywords.replaceAll("(" + maxSpliter + ")+", Splitor);
			}
		}
		return keywords;
	}

}
