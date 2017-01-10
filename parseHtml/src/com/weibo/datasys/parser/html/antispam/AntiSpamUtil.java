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

import com.weibo.datasys.parser.util.ArgsNormalizer;
import com.weibo.datasys.parser.util.CharProcessUtils;

public class AntiSpamUtil
{

	
	/**
	 * {@link #str}中不可读字符的个数
	 */
	public static int  getUnreadNum(String str) {
		if(str ==null||str.length()==0) {
			return 0;
		}
		int unreadableNum=0;
		for (int i = 0; i < str.length(); i++)
		{
    		char cText = str.charAt(i);
    		if (CharProcessUtils.isCHNChar(cText) || CharProcessUtils.isLetterOrDigit(cText))
			{//中英文和数字编码范围
//    			System.out.println(cText);
			} else
			{//包含空格
				unreadableNum++;
			}
		}
		return unreadableNum;
	}
	
	
	/**
	 *  {@link #str}中汉字数和常用汉字数
	 * @return num[0]为汉字数 ，num[1]为非常用汉字数
	 */
	public static Integer[]  getNonCommonScale(String str) {
		Integer [] nums = {0,0};
		if(!ArgsNormalizer.strIsValid(str)) {
			return nums;
		}else {
			str = SpamResource.toShort(str);
		}
		
		int ncNum = 0;
		int cnNum = 0;
		for (int i = 0; i < str.length(); i++) {
			char cText = str.charAt(i);
    		if (CharProcessUtils.isCHNChar(cText))
			{//中文编码范围
    			cnNum++;
				if(!SpamResource.isCommonShort(cText)) {
					ncNum++;
				}
			}
		}
		nums[1] = ncNum;
		nums[0] = cnNum;
		return nums;
	}
	

	
	
	/**
	 * {@link #str} 是否包含中文字符,不包含符号
	 */
	public static boolean  isContainChn(String str) {
		int cnNum = 0;
		for (int i = 0; i < str.length(); i++)
		{
    		char cText = str.charAt(i);
    		if (CharProcessUtils.isCHNChar(cText))
			{//中文编码范围
				cnNum++;
			}
		}
		return cnNum==0 ? false : true;
	}
	
	
	
	/**
	 * 分词后的长度
	 */
	// public static int getSegStrLen(String str) {
	// if (!ArgsNormalizer.strIsValid(str)) {
	// return 0;
	// }
	// String[] segwords = SpamResource.GetWordArray(str);// 分词
	// return segwords==null?0:segwords.length;
	// }
	

}
