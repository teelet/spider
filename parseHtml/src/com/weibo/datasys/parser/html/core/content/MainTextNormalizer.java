/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.content;

import com.weibo.datasys.parser.html.antispam.SpamResource;
import com.weibo.datasys.parser.util.NormalizerBasic;
import com.weibo.datasys.parser.util.StringUtil;

public class MainTextNormalizer extends NormalizerBasic
{


	@Override
	public String normalizeString(String text) {
		
		//长度原因
		String mainText = text.length()>1000 ? text.substring(0, 1000):text; 
		
		//繁简转换
		mainText = SpamResource.toShort(mainText);
		
		//小写转换
		mainText  = mainText.toLowerCase();
		
		//空白替换处理
		mainText = normalSpaces(mainText);
		
		//连续空白处理
		mainText = StringUtil.spaceFormat(mainText);
		
		
		return mainText;
	}
		

}
