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

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Node;

import com.weibo.datasys.parser.conf.ParseConfig;
import com.weibo.datasys.parser.html.core.domtree.TravBlockTree;
import com.weibo.datasys.parser.html.data.BlockInfo;
import com.weibo.datasys.parser.util.TextUtil;

public class ContentExtractor
{

	private static final int MIN_CONTENT_NUM = 5;
	private static final int LINK_AS_TEXT_NUM = 90;

	/**
	 * 获取正文，没有做反垃圾和description 选择
	 * @throws Exception 
	 */
	public static String getContent(BlockInfo root, String title,ParseConfig parseConfig) throws Exception
	{
		String result = null;
		ExtContentAction extContentAction = new ExtContentAction();
		extContentAction.setParseConfig(parseConfig);
		TravBlockTree travBlockTree = new TravBlockTree(extContentAction);
		travBlockTree.recurTree(root);
		StringBuffer sBuffer = extContentAction.getContent();
		result = getContent(sBuffer.toString(), title);
		return result == null ? "" : result;
	}
	
	public static HashMap<String,HashMap<String,String>> getContentLinks(BlockInfo root, String title) throws Exception
	{
		
		ExtContentLinkActions extContentLinksAction = new ExtContentLinkActions();

		TravBlockTree travBlockTree = new TravBlockTree(extContentLinksAction);
		travBlockTree.recurTree(root);
		HashMap<String,HashMap<String,String>> result = extContentLinksAction.getLinkUrls();
		return result;
	}

	public static String getContent(BlockInfo root, String title, String description, String url,ParseConfig parseConfig) throws Exception
	{
		String result = getContent(root, title,parseConfig);
		if (result.trim().length() < MIN_CONTENT_NUM && url.matches("http://[^/]+/?(\\w+/)?")
				&& description.trim().length() > MIN_CONTENT_NUM)
		{
			result = description;
		}
		return result;
	}
	
	
	public static HashMap<String,HashMap<String,String>> getContentImgs(BlockInfo root, String title, String description, String url) throws Exception
	{
		HashMap<String,HashMap<String,String>> result = getContentLinks(root, title);
		return result;
	}

	/**
	 * 最大块的正文处理。（替换title标记）
	 */
	public static String getContent(String content, String title)
	{
		String result = TextUtil.Normalizer(content);
		return result == null ? "" : result;
	}


	public static boolean ContentIsSpam(String con)
	{
		if (con != null && con.trim().length() >= MIN_CONTENT_NUM)
		{
			return false;
		} else
		{
			return true;
		}
	}

	public static String getLinkAsText(HashMap<String, String> outlinks)
	{
		String content = "";
		StringBuffer sBuffer = new StringBuffer();
		for (String key : outlinks.keySet())
		{
			sBuffer.append(outlinks.get(key) + " ");
			if (sBuffer.length() > LINK_AS_TEXT_NUM)
			{
				break;
			}
			content = sBuffer.toString();
			content = content.trim().length() > LINK_AS_TEXT_NUM ? content.substring(0,
					LINK_AS_TEXT_NUM) : content;
		}
		return content == null ? "" : content;
	}

}
