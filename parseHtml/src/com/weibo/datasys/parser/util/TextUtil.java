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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil
{

	private static HashMap<String, String> characterTranslateMap; // html转义字符Map
	private static Pattern patternSpecialCharacters = null;
	private static String specialCharPattern = "&nbsp;|&lt;|&gt;|&amp;|&quot;|&copy;|&reg;|&times;|&divide;|&dividee;";
	static
	{

		// 初始化characterTranslateMap
		characterTranslateMap = new HashMap<String, String>();
		characterTranslateMap.put("&nbsp;", " ");
		characterTranslateMap.put("&lt;", "<");
		characterTranslateMap.put("&gt;", ">");
		characterTranslateMap.put("&amp;", "&");
		characterTranslateMap.put("&quot;", "\"");
		characterTranslateMap.put("&copy;", "©");
		characterTranslateMap.put("&reg;", "®");
		characterTranslateMap.put("&times;", "×");
		characterTranslateMap.put("&divide;", "÷");
		characterTranslateMap.put("&dividee;", "dsf");

		patternSpecialCharacters = Pattern.compile(specialCharPattern);
	}

	/**
	 * 规整字符串，对HTML字符解码，删除多余空格等
	 * 
	 * @param text
	 * @return
	 */
	public static String Normalizer(String text)
	{
		StringBuffer result = new StringBuffer();
		if (text != null)
		{
			// 将文本中html转义字符全部替换
			Matcher matcher = patternSpecialCharacters.matcher(text);
			while (matcher.find())
			{
				String strFind = matcher.group();
				if (characterTranslateMap.containsKey(strFind))
					matcher.appendReplacement(result, characterTranslateMap.get(strFind));
			}
			matcher.appendTail(result);
		}
		String normalizedText = result.toString();
		normalizedText = StringUtil.spaceNormalizer(normalizedText);
		return normalizedText;
	}
	
	/**
	 * 规整字符串，对HTML字符解码，删除特殊格式
	 * 
	 * @param text
	 * @return
	 */
	public static String RemoveTag(String text)
	{
		StringBuffer result = new StringBuffer();
		if (text != null)
		{
			// 将文本中html转义字符全部替换
			Matcher matcher = patternSpecialCharacters.matcher(text);
			while (matcher.find())
			{
				String strFind = matcher.group();
				if (characterTranslateMap.containsKey(strFind))
					matcher.appendReplacement(result, characterTranslateMap.get(strFind));
			}
			matcher.appendTail(result);
		}
		String normalizedText = result.toString();
		normalizedText = StringUtil.SpecialTagNormalizer(normalizedText);
		return normalizedText;
	}

	public static Map<String, String> text2Map(String text)
	{
		Map<String, String> result = new HashMap<String, String>();
		text = (text.startsWith("{")) ? text.substring(1, text.length()) : text;
		text = (text.endsWith("}")) ? text.substring(0, text.length() - 1) : text;
		String[] cutText = text.split(",");
		for (String cutT : cutText)
		{
			String[] cutKV = cutT.split("=", 2);
			result.put(cutKV[0], cutKV[1]);
		}
		return result;
	}

	// public static void main(String[] args) throws
	// UnsupportedEncodingException {
	// long begin = System.currentTimeMillis();
	// for (int i = 0; i <100000; i++) {
	// System.out.println(TextNormalizer.Normalizer("&nbsp;|&lt;|&gt;|&amp;|&quot;|&copy;\r\n\r\n|&reg;|&times;|&divide;|&dividee;"));
	// }
	// System.out.println( System.currentTimeMillis()-begin);
	// }

}
