/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.title;

import com.weibo.datasys.parser.html.antispam.SpamResource;
import com.weibo.datasys.parser.util.NormalizerBasic;
import com.weibo.datasys.parser.util.StringUtil;

public class TitleNormalizer extends NormalizerBasic
{


	@Override
	public String normalizeString(String title) {
		
		//空白替换处理
		title = normalSpaces(title);
		
		//连续空白处理
		title = StringUtil.spaceFormat(title);
		
		//小写转换
		title =  title.toLowerCase().trim();;
		
		//繁简转换
		title = SpamResource.toShort(title);
		
		
		return title;
		
	}
			
	

}
