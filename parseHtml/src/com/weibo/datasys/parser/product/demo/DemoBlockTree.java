/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.product.demo;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.weibo.datasys.parser.html.core.domtree.BlockScore;
import com.weibo.datasys.parser.html.core.domtree.BlockTree;
import com.weibo.datasys.parser.util.StringUtil;

public class DemoBlockTree extends BlockTree
{

	public DemoBlockTree(BlockScore blockScore, String url)
	{
		super(blockScore, url);
	}

	@Override
	protected void setSpecialTitle(Node node)
	{

		if (!StringUtil.hasValue(specialTitle))
		{
			String result = null;
			Node subNode;
			String subNodeValue;
			String nodeName = node.getNodeName();
			NamedNodeMap attNodeMap = node.getAttributes();
			String tempUrl = url.toLowerCase();
			String[] cutUrls = tempUrl.split("/");
			if (cutUrls[2].equals("www.gov.cn")) // 中国政府网特殊标题策略
			{
				if (4 == cutUrls.length && cutUrls[3].matches("zmyw\\d{6}[a-z]"))
				{
					result = "周末要闻 " + cutUrls[3].substring(4, 8) + "年"
							+ cutUrls[3].substring(8, 10) + "第" + (cutUrls[3].charAt(10) - 'a' + 1)
							+ "期";
				} else if (5 <= cutUrls.length && cutUrls[4].startsWith("ft"))
				{
					if ("td".equalsIgnoreCase(nodeName))
					{
						subNode = attNodeMap.getNamedItem("class");
						if (null != subNode)
						{
							subNodeValue = subNode.getNodeValue();
							if (null != subNodeValue
									&& subNodeValue.trim().equalsIgnoreCase("txt18"))
							{
								result = node.getTextContent();
							}
						}
					}
				}
			}
			if (StringUtil.hasValue(result))
			{
				specialTitle = result;
			}
		}
	}
}
