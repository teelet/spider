/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.meta;

import java.net.URL;

import com.weibo.datasys.parser.html.data.MetaTags;

public class UrlDetector
{
	public static URL strToURL(String url)
	{
		URL curUrl = null;
		try
		{
			curUrl = new URL(url);
		} catch (Exception e)
		{
		}
		return curUrl;
	}

	public static URL getBaseTagURL(MetaTags metaTags)
	{
		String baseStr = metaTags.getGeneralTags().getProperty("base");
		URL metabaseUrl = null;
		if (baseStr != null)
		{
			metabaseUrl = strToURL(baseStr);
		}
		return metabaseUrl;
	}

	public static URL getBaseURL(MetaTags metaTags, URL curUrl)
	{
		URL base = null;
		URL metabaseUrl = getBaseTagURL(metaTags);
		base = (metabaseUrl != null) ? metabaseUrl : curUrl;
		return base;
	}

}
