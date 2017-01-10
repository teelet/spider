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

import java.util.HashSet;

import com.weibo.datasys.parser.html.antispam.SpamResource;

public class StringUtil
{

	/**
	 * 子串b在主串a中出现的次数
	 * 
	 * @param a
	 *            主串
	 * @param b
	 *            子串
	 */
	public static int getNumOfString(String a, String b)
	{
		int num = 0;
		int index = 0;
		if (a != null && b != null)
		{
			while (a.indexOf(b, index) != -1)
			{
				num++;
				index = a.indexOf(b, index) + b.length();
			}
		}
		return num;
	}

	/**
	 * 子串b在主串a中出现的次数
	 * 
	 * @param a
	 *            主串
	 * @param b
	 *            子串
	 * @param septor
	 *            分隔符
	 */
	public static int getNumOfString(String a, String b, String septor)
	{
		int num = 0;
		int i = 0;
		String key;
		while (b.indexOf(septor, i) != -1)
		{
			key = b.substring(i, b.indexOf(septor, i));
			if (a.indexOf(key) != -1)
				num++;
			i = b.indexOf(septor, i) + 1;
		}

		return num;
	}

	/**
	 * 返回串型数
	 * 
	 * @param strs
	 *            输入串
	 * @param septor
	 *            分隔符
	 */
	public static int getNumOfStrType(String strs, String septor)
	{
		if (strs == null || strs.length() == 0)
		{
			return 0;
		} else
		{
			String[] arry = strs.trim().split(septor);
			HashSet<String> strsSet = new HashSet<String>();
			for (String s : arry)
			{
				strsSet.add(s);
			}
			return strsSet.size();
		}
	}

	/**
	 * 最长子串的长度
	 * 
	 * @param str
	 *            主串
	 * @param septor
	 *            分割符
	 */
	public static int getMaxLenInStr(String str, String septor)
	{
		if (str == null || septor == null || str.length() == 0 || septor.length() == 0)
		{
			return 0;
		} else
		{
			str += septor;// 末尾加分割符，便于最后一个word长度的计算
		}
		int index = 0;
		int maxLen = 0;
		int curpos = 0;
		while ((curpos = str.indexOf(septor, index)) != -1)
		{
			maxLen = maxLen < (curpos - index) ? curpos - index : maxLen;
			index = curpos + 1;
		}

		return maxLen;
	}

	/**
	 * 计算各类字符的数量，返回数组中，依次为字符总数、汉字（繁简）字符数、英文字符数、数字、空白字符、其它字符、非常用字数
	 */
	public static Integer[] getInvalidWordNum(String text)
	{
		if (null == text || text.trim().equals(""))
		{
			return new Integer[] { 0, 0, 0, 0, 0, 0, 0 };
		} else
		{
			text = SpamResource.toShort(text);
		}
		Integer[] charNums = new Integer[] { 0, 0, 0, 0, 0, 0, 0 };
		// title总长度
		charNums[0] = text.length();
		for (int k = 0; k < text.length(); k++)
		{
			char ctext = text.charAt(k);
			if (CharProcessUtils.isCHNChar(ctext))
			{
				charNums[1]++;// 简繁体
				if (!SpamResource.isCommonShort(ctext))
				{
					charNums[6]++;
				}
			} else if (CharProcessUtils.isLetter(ctext))
			{
				charNums[2]++;// 英文
			} else if (CharProcessUtils.isDigit(ctext))
			{
				charNums[3]++;// 数字
			} else if (CharProcessUtils.isSpace(ctext))
			{// 不等于各种空格字符
				charNums[4]++;// 空白字符
			} else
			{
				charNums[5]++;// 其它字符
			}

		}
		return charNums;
	}

	/**
	 *空白字符归一化、 多个连续空白和回车换行替换
	 */
	public static String spaceNormalizer(String text)
	{
		if (text == null || text.length() == 0)
		{
			return "";
		}
		text = spaceFormat(text);
		text = normalRN(text);
		text = normalPTag(text);
		text = remveUnusedPTag(text);
		text = remveUnusedSpecialTag(text);
		text = text.trim();
		return text;
	}

	/**
	 *空白字符归一化
	 */
	public static String spaceFormat(String text)
	{
		if (text == null || text.length() == 0)
		{
			return "";
		}
		text = text.replaceAll("[\u0009\u0020\u00a0\u3000]+", "\u0020");
		return text;
	}

	/**
	 * 多个连续换行符替换成一个
	 */
	public static String normalRN(String text)
	{
		if (text == null || text.length() == 0)
		{
			return "";
		}
		text = text.replaceAll("\\s*\n+\\s*", "\r\n");
		return text;
	}
	
	/**
	 * <p></p> or <p>\r\n</p> 去掉
	 */
	public static String normalPTag(String text)
	{
		if (text == null || text.length() == 0)
		{
			return "";
		}
		text = text.replaceAll("\\s*<p>\r\n</p>+\\s*", "");
		text = text.replaceAll("\\s*<p></p>+\\s*", "");
		return text;
	}
	
	/**
	 */
	public static String remveUnusedPTag(String text)
	{
		if (text == null || text.length() == 0)
		{
			return "";
		}

		text = text.replaceAll("\\s*<p>+\\s*", "<p>");
		text = text.replaceAll("\\s*</p>+\\s*", "</p>");
		return text;
	}
	
	/**
	 * 
	 */
	public static String remveUnusedSpecialTag(String text)
	{
		if (text == null || text.length() == 0)
		{
			return "";
		}
		text = text.replaceAll("\\s*<strong>\\</strong>+\\s*", "");
		text = text.replaceAll("\\s*<strong></strong>+\\s*", "");
		text = text.replaceAll("\\s*<font>\\</font>+\\s*", "");
		text = text.replaceAll("\\s*<font></font>+\\s*", "");
		text = text.replaceAll("\\s*<span>\r\n</span>+\\s*", "");
		text = text.replaceAll("\\s*<span></span>+\\s*", "");
		text = text.replaceAll("\\s*<p></p>+\\s*", "");
		text = text.replaceAll("\\s*<p>\r\n</p>+\\s*", "");
		return text;
	}
	
	/**
	 * 
	 */
	public static String SpecialTagNormalizer(String text)
	{
		if (text == null || text.length() == 0)
		{
			return "";
		}
		text = text.replaceAll("<strong>", "");
		text = text.replaceAll("</strong>", "");
		text = text.replaceAll("<font>", "");
		text = text.replaceAll("</font>", "");
		text = text.replaceAll("<span>", "");
		text = text.replaceAll("</span>", "");
		text = text.replaceAll("<b>", "");
		text = text.replaceAll("</b>", "");
		return text;
	}

	/**
	 * value中存在字串array[i]的个数
	 * 
	 */
	public static int getNumInArray(String[] arrays, String value)
	{
		if (null == value || value.trim().equals(""))
		{
			return 0;
		}
		int num = 0;
		for (int i = 0; i < arrays.length; i++)
		{
			num += StringUtil.getNumOfString(value, arrays[i]);
		}
		return num;
	}
	
	public static boolean hasValue(String value)
	{
		boolean result =false;
		if(null!=value && !value.trim().equals(""))
		{
			result=true;
		}
		return result;
	}
}
