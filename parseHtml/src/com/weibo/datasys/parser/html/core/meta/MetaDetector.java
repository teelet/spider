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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.weibo.datasys.parser.html.data.MetaTags;

public class MetaDetector
{
	public static final void getMetaTags(MetaTags metaTags, Node node, URL currURL)
	{

		metaTags.reset();
		getMetaTagsHelper(metaTags, node, currURL);
	}

	private static final void getMetaTagsHelper(MetaTags metaTags, Node node, URL curURL)
	{

		if (node.getNodeType() == Node.ELEMENT_NODE)
		{
			if ("body".equalsIgnoreCase(node.getNodeName()))
			{
				return;
			}
			if ("meta".equalsIgnoreCase(node.getNodeName().toLowerCase()))
			{
				metaProcess(metaTags, node, curURL);
			} else if ("base".equalsIgnoreCase(node.getNodeName()))
			{
				baseProcess(metaTags, node, curURL);
			} else if ("title".equalsIgnoreCase(node.getNodeName()))
			{
				metaTags.getGeneralTags().setProperty("title", node.getTextContent().trim());
			}

		}
		// 递归循环
		NodeList children = node.getChildNodes();
		if (children != null)
		{
			int len = children.getLength();
			for (int i = 0; i < len; i++)
			{
				getMetaTagsHelper(metaTags, children.item(i), curURL);
			}
		}
	}

	public static void baseProcess(MetaTags metaTags, Node node, URL curURL)
	{

		NamedNodeMap attrs = node.getAttributes();
		Node hrefNode = attrs.getNamedItem("href");

		if (hrefNode != null)
		{
			String urlString = hrefNode.getNodeValue();

			URL url = null;
			try
			{
				if (curURL == null)
					url = new URL(urlString);
				else
					url = new URL(curURL, urlString);
			} catch (Exception e)
			{
				// ;
			}

			if (url != null)
				metaTags.setBaseHref(url);
		}

	}

	public static void metaProcess(MetaTags metaTags, Node node, URL curURL)
	{

		NamedNodeMap attrs = node.getAttributes(); // 获得节点属性
		Node nameNode = null;
		Node equivNode = null;
		Node contentNode = null;
		// Retrieves name, http-equiv and content attribues
		for (int i = 0; i < attrs.getLength(); i++)
		{
			Node attr = attrs.item(i);
			String attrName = attr.getNodeName().toLowerCase();
			if (attrName.equals("name"))
			{
				nameNode = attr;
			} else if (attrName.equals("http-equiv"))
			{
				equivNode = attr;
			} else if (attrName.equals("content"))
			{
				contentNode = attr;
			}
		}
		// 1.分析含有name 和 content属性的meta标签对
		if (nameNode != null)
		{
			if (contentNode != null)
			{
				metaNameCotent(metaTags, nameNode, contentNode);
			}
		}
		
		
		
		// 2.分析含有http-equiv 和 content属性的meta标签对
		if (equivNode != null)
		{
			if (contentNode != null)
			{
				metaHttpEquiv(metaTags, equivNode, contentNode, curURL);
			}
		}

	}

	public static void metaNameCotent(MetaTags metaTags, Node nameNode, Node contentNode)
	{

		String name = nameNode.getNodeValue().toLowerCase().trim();
		
		metaTags.getGeneralTags().setProperty(name, contentNode.getNodeValue());
		if ("robots".equals(name))
		{
			if (contentNode != null)
			{
				String directives = contentNode.getNodeValue().toLowerCase();
				int index = directives.indexOf("none");

				if (index >= 0)
				{
					metaTags.setNoIndex();
					metaTags.setNoFollow();
				} else
				{
					index = directives.indexOf("all");
					if (index >= 0)
					{
						// do nothing...
					} else
					{

						index = directives.indexOf("noindex");
						if (index >= 0)
						{
							metaTags.setNoIndex();
						}

						index = directives.indexOf("nofollow");
						if (index >= 0)
						{
							metaTags.setNoFollow();
						}
					}
				}
				index = directives.indexOf("noarchive");
				if (index >= 0)
				{
					metaTags.setNoCache();
				}
			}

		}

	}

	public static void metaHttpEquiv(MetaTags metaTags, Node equivNode, Node contentNode,
			URL currURL)
	{

		String name = equivNode.getNodeValue().toLowerCase().trim();
		String content = contentNode.getNodeValue();
		metaTags.getHttpEquivTags().setProperty(name, content);
		if ("pragma".equals(name))
		{
			content = content.toLowerCase();
			int index = content.indexOf("no-cache");
			if (index >= 0)
				metaTags.setNoCache();
		} else if ("refresh".equals(name))
		{
			int idx = content.indexOf(';');
			String time = null;
			if (idx == -1)
			{
				time = content;
			} else
				time = content.substring(0, idx);
			try
			{
				metaTags.setRefreshTime(Integer.parseInt(time));
				metaTags.setRefresh(true);
			} catch (Exception e)
			{
			}
			URL refreshUrl = null;
			if (metaTags.getRefresh() && idx != -1)
			{
				idx = content.toLowerCase().indexOf("url=");
				if (idx == -1)
				{
					idx = content.indexOf(';') + 1;
				} else
					idx += 4;
				if (idx != -1)
				{
					String url = content.substring(idx);
					try
					{
						refreshUrl = new URL(url);
					} catch (Exception e)
					{
						try
						{
							refreshUrl = new URL(currURL, url);
						} catch (Exception e1)
						{
							refreshUrl = null;
						}
					}
				}
			}
			if (metaTags.getRefresh())
			{
				if (refreshUrl == null)
				{
					refreshUrl = currURL;
				}
				metaTags.setRefreshHref(refreshUrl);
			}
		}
	}

}
