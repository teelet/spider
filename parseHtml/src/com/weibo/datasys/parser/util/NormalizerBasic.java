/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.util;

public abstract class NormalizerBasic
{

	/**
	 * 归一化字符串
	 */
	public abstract String normalizeString(String text);

	/**
	 * 空白字符的归一化
	 * 
	 * @param text
	 *            输入串
	 */
	protected String normalSpaces(String text)
	{
		text = StringUtil.spaceFormat(text);
		return text;
	}


}
