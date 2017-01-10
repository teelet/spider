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

import java.util.HashMap;

public class ParseDataFormat
{

	public static ParseData format(ParseData pData, String id, String origUrl, String uniUrl,
			String origTitle, String title, String content, String encoding, String keywords,
			String description, int column, long pubtime, long fetchTime, String originSite,
			String originPublish,String originAuthor, int pagetype, int language, int crawlerstate, int parsestate,
			HashMap<String,String> contentImg,HashMap<String,String> href,String taskid)
	{
		pData.setId(id);
		pData.setOrigUrl(origUrl);
		pData.setUniurl(uniUrl);
		pData.setOrigtitle(origTitle);
		pData.setTitle(title);
		pData.setContent(content);
		pData.setEncoding(encoding);
		pData.setKeyword(keywords);
		pData.setDescription(description);
		pData.setColumn(column);
		pData.setPubtime(pubtime);
		pData.setFetchtime(fetchTime);
		pData.setCtsize(content.length());
		pData.setOriginSite(originSite);
		pData.setOriginPublish(originPublish);
		pData.setOriginAuthor(originAuthor);
		pData.setPagetype(pagetype);
		pData.setLanguage(language);
		pData.setCrawlerstate(crawlerstate);
		pData.setParsestate(parsestate);
		pData.setContentImg(contentImg);
		pData.setHref(href);
		pData.setTaskId(taskid);
		return pData;
	}
	
}
