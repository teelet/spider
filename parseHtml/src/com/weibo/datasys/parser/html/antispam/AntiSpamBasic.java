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

import com.weibo.datasys.parser.util.StringUtil;


public abstract class AntiSpamBasic
{

	/**
	 * 
	 * 计算输入文本的垃圾权重
	 */
	public abstract double spamWeight(String text) ;
	
	
	
	/*******************************already implemented*************************/

	
	
	/**
	 * 是否包含词重复（like匹配），单个词重复repeatHold以上，返回true
	 * @param strs 输入串
	 * @param septor 分隔符
	 * @param repeatHold 重复阈值
	 */
	public boolean  isRepeat(String strs, String septor,int repeatHold) {
		if(strs==null||strs.length()==0) {
			return false;
		}else {
			String [ ] arry= strs.trim().split(septor);
			for (int i = 0; i < arry.length; i++) {
				if(StringUtil.getNumOfString(strs,arry[i])>repeatHold) {
					return true;
				}
			}
			return false;
		}
	}

}
