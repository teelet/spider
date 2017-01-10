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

import java.util.HashMap;
import java.util.HashSet;

import com.weibo.datasys.parser.html.antispam.TitleAntiSpam;
import com.weibo.datasys.parser.util.StringUtil;
import com.weibo.datasys.parser.util.TextUtil;

public class TitleExtractor
{

	protected String title = ""; // title标签中的文本信息
	protected HashMap<Integer, String> titleMap = null; // h标签中的文本信息
	protected HashSet<String> splitTitleEndsWithSet = null;
	protected HashSet<String> splitTitleFirstEndsWithSet = null;
	protected HashSet<String> splitTitleLastEndsWithSet = null;
	protected HashSet<String> splitTitleEqualsSet = null;
	protected HashSet<String> splitTitleFirstEqualsSet = null;
	protected HashSet<String> splitTitleLastEqualsSet = null;
	protected HashSet<String> splitTitleContainsSet = null;
	protected HashSet<String> splitTitleFirstContainsSet = null;
	protected HashSet<String> splitTitleLastContainsSet = null;
	protected String specialTitle = "";
	protected int hTitleLocation = -1;
	protected boolean firstSegTitle;
	protected boolean lastSegTitle;
	// private double titleScore = 0; // 最终标题的分值
	public static final int MAXTITLE_LENGTH = 100; // 最大标题长度
	public static final double SCORE_TITLE_H = 0.4;
	public static final String[] endfilter = {"·", "_", "-", "―", "＿", "－", "—"};
	public static double TITLE_SPAM_THRESHOLD = TitleAntiSpam.THRESHHOLD; // 标题垃圾阈值

	public TitleExtractor(String metaTitle, HashMap<Integer, String> titleMap, String specialTitle)
	{
		this.title = metaTitle.trim();
		this.titleMap = titleMap;
		this.specialTitle = specialTitle;
		init();
	}

	public void init()
	{
		splitTitleEndsWithSet = new HashSet<String>();
		splitTitleEndsWithSet.add("网");
		splitTitleFirstEndsWithSet = new HashSet<String>();
		splitTitleLastEndsWithSet = new HashSet<String>();
		splitTitleEqualsSet = new HashSet<String>();
		splitTitleFirstEqualsSet = new HashSet<String>();
		splitTitleLastEqualsSet = new HashSet<String>();
		splitTitleContainsSet = new HashSet<String>();
		splitTitleFirstContainsSet = new HashSet<String>();
		splitTitleLastContainsSet = new HashSet<String>();
		firstSegTitle = true;
		lastSegTitle = false;
	}

	/**
	 * 提取meta标题
	 */
	public String getMetaTitle()
	{
		return title != null ? TextUtil.Normalizer(title) : "";
	}

	private String getSpecialTitle()
	{
		return specialTitle;
	}

	/**
	 * 提取标题
	 */
	public String getTitle()
	{

		// TitleAntiSpam titleAntiSpam = new TitleAntiSpam(); // title垃圾去除类
		boolean isAlive = true;
		String result = null;
		String hSimTitle = null;
		double score = 0;
		String hTitle;
		double hScore = 0;

		// 判断title标签内容是否有效
		// if (titleAntiSpam.spamWeight(title) >= TITLE_SPAM_THRESHOLD)
		// {
		// isAlive = false;
		// }
		if (null != specialTitle && specialTitle.trim().length() >= 2)
		{
			result = specialTitle;
		} else
		{
			if (titleMap.size() > 0)
				result = titleMap.get(1);
			/*
			for (int i = 0; i < titleMap.size(); i++)
			{
				hTitle = titleMap.get(i + 1);
				// 判断h标签内容是否有效
				// if (titleAntiSpam.spamWeight(hTitle) < TITLE_SPAM_THRESHOLD)
				// {
				if (isAlive)
				{
					hScore = SimilarityCal(title, hTitle);
					if (hScore > SCORE_TITLE_H && hScore > score)
					{
						score = hScore;
						hSimTitle = hTitle;
						hTitleLocation = i + 1;
					}
				} else
				{
					result = hTitle;
					hTitleLocation = i + 1;
					break;
				}
				// }
			}
			 */
			if (null == result)
			{
				if (isAlive && null == hSimTitle)
				{
					result = endFilter(title);
					// titleScore = titleAntiSpam.spamWeight(result);
				} else if (isAlive && null != hSimTitle)
				{
					result = hSimTitle;
				} else
				{
				}
			}
		}
		result = TextUtil.Normalizer(result);
		result = result.length() > MAXTITLE_LENGTH ? result.substring(0, MAXTITLE_LENGTH) : result;
		return result == null ? "" : result;
	}

	/**
	 * 计算两个字符串的相似度
	 */
	private double SimilarityCal(String query, String value)
	{

		
		if (query.contains(value)){
			return 10;
		}
		double score = 0;
		int queryLen = 0;

		HashSet<String> querySet = new HashSet<String>();
		for (int i = 0; i < query.length(); i++)
		{
			String c = query.substring(i, i + 1).trim();
			if (c.length() > 0 && !querySet.contains(c))
			{
				querySet.add(c);
				queryLen++;
			}
		}

		HashSet<String> valueSet = new HashSet<String>();
		int count = 0, valueLen = 0;
		for (int i = 0; i < value.length(); i++)
		{
			String c = value.substring(i, i + 1).trim();
			if (c.length() > 0 && !valueSet.contains(c))
			{
				if (querySet.contains(c))
				{
					count++;
				}
				valueLen++;
				valueSet.add(c);
			}
		}

		if (valueLen > 0 && queryLen > 0)
		{
			score = (double) count / (double) Math.max(queryLen, valueLen);

		}
		return score;
	}

	/**
	 * 根据分割字符对原始标题进行切分
	 */
	protected String endFilter(String str)
	{
		if (str == null)
		{
			return "";
		}
		String result = str;
		StringBuffer sBuffer = new StringBuffer();

		for (int i = 0; i < endfilter.length; i++)
		{
			String[] cutTitles = result.split(endfilter[i]);
			if (cutTitles.length > 1)
			{
				if (isFirstSegTitle())
				{
					sBuffer.append(cutTitles[0].trim());
				} else if (isLastSegTitle())
				{
					sBuffer.append(cutTitles[cutTitles.length - 1].trim());
				} else
				{
					int isEquals = isSplitTitleEquals(cutTitles);
					int isEndsWith = isSplitTitleEndsWith(cutTitles);
					int isContains = isSplitTitleContains(cutTitles);
					if (1 == isEquals)
					{
						addNoFirstBlockTitle(sBuffer, endfilter[i], cutTitles);
					} else if (2 == isEquals)
					{
						addNoLastBlockTitle(sBuffer, endfilter[i], cutTitles);
					} else if (1 == isEndsWith)
					{
						addNoFirstBlockTitle(sBuffer, endfilter[i], cutTitles);
					} else if (2 == isEndsWith)
					{
						addNoLastBlockTitle(sBuffer, endfilter[i], cutTitles);
					} else if (1 == isContains)
					{
						addNoFirstBlockTitle(sBuffer, endfilter[i], cutTitles);
					} else if (2 == isContains)
					{
						addNoLastBlockTitle(sBuffer, endfilter[i], cutTitles);
					}
				}
				if (sBuffer.length() != 0)
				{
					result = sBuffer.toString();
				}
				break;
			}

		}
		return result;
	}

	protected void addNoFirstBlockTitle(StringBuffer sBuffer, String segmentChar, String[] cutTitles)
	{
		for (int j = 1; j < cutTitles.length; ++j)
		{
			sBuffer.append(cutTitles[j]);
			if (null != cutTitles[j] && !cutTitles[j].trim().equals("") && j < cutTitles.length - 1)
			{
				sBuffer.append(segmentChar);
			}
		}
	}

	protected void addNoLastBlockTitle(StringBuffer sBuffer, String segmentChar, String[] cutTitles)
	{
		for (int j = 0; j < cutTitles.length - 1; ++j)
		{
			sBuffer.append(cutTitles[j]);
			if (j < cutTitles.length - 2 && null != cutTitles[j + 1]
					&& !cutTitles[j + 1].trim().equals(""))
			{
				sBuffer.append(segmentChar);
			}
		}
	}

	protected int isSplitTitleEndsWith(String[] cutTitles)
	{
		int result = 0;
		if (0 == result)
		{
			for (String data : splitTitleFirstEndsWithSet)
			{
				if (cutTitles[0].endsWith(data))
				{
					result = 1;
					break;
				}
			}
		}
		if (0 == result)
		{
			for (String data : splitTitleLastEndsWithSet)
			{
				if (cutTitles[cutTitles.length - 1].endsWith(data))
				{
					result = 2;
					break;
				}
			}
		}
		if (0 == result)
		{
			for (String data : splitTitleEndsWithSet)
			{
				if (cutTitles[0].endsWith(data) && !cutTitles[cutTitles.length - 1].endsWith(data))
				{
					result = 1;
					break;
				} else if (!cutTitles[0].endsWith(data)
						&& cutTitles[cutTitles.length - 1].endsWith(data))
				{
					result = 2;
					break;
				}
			}
		}
		return result;
	}

	protected int isSplitTitleEquals(String[] cutTitles)
	{
		int result = 0;
		if (0 == result)
		{
			for (String data : splitTitleFirstEqualsSet)
			{
				if (cutTitles[0].trim().equalsIgnoreCase(data))
				{
					result = 1;
					break;
				}
			}
		}
		if (0 == result)
		{
			for (String data : splitTitleLastEqualsSet)
			{
				if (cutTitles[cutTitles.length - 1].trim().equalsIgnoreCase(data))
				{
					result = 2;
					break;
				}
			}
		}
		if (0 == result)
		{
			for (String data : splitTitleEqualsSet)
			{
				if (cutTitles[0].equalsIgnoreCase(data)
						&& !cutTitles[cutTitles.length - 1].trim().equalsIgnoreCase(data))
				{
					result = 1;
					break;
				} else if (!cutTitles[0].equalsIgnoreCase(data)
						&& cutTitles[cutTitles.length - 1].trim().equalsIgnoreCase(data))
				{
					result = 2;
					break;
				}
			}
		}
		return result;
	}

	protected int isSplitTitleContains(String[] cutTitles)
	{
		int result = 0;
		if (0 == result)
		{
			for (String data : splitTitleFirstContainsSet)
			{
				if (cutTitles[0].contains(data))
				{
					result = 1;
					break;
				}
			}
		}
		if (0 == result)
		{
			for (String data : splitTitleLastContainsSet)
			{
				if (cutTitles[cutTitles.length - 1].contains(data))
				{
					result = 2;
					break;
				}
			}
		}
		if (0 == result)
		{
			for (String data : splitTitleContainsSet)
			{
				if (cutTitles[0].contains(data) && !cutTitles[cutTitles.length - 1].contains(data))
				{
					result = 1;
					break;
				} else if (!cutTitles[0].contains(data)
						&& cutTitles[cutTitles.length - 1].contains(data))
				{
					result = 2;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * title 是否合法
	 * 
	 */
	public static boolean titleIsNull(String title)
	{
		if (title == null || StringUtil.spaceNormalizer(title).length() == 0)
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * 恢复正文中类似标题的转译字段
	 */
	// public String FillContent(String content)
	// {
	// if (content==null) {
	// return "";
	// }
	// int tLocation;
	// for (int i = 0; i < titleMap.size(); i++)
	// {
	// tLocation = i + 1;
	// if (tLocation == hTitleLocation)
	// {
	// content = content.replace("{{title_" + tLocation + "}}", "");
	// } else
	// {
	// try {
	// content = content.replace("{{title_" + tLocation + "}}", titleMap
	// .get(tLocation));
	// } catch (Exception e) {
	// Iterator iterator = titleMap.entrySet().iterator();
	// while (iterator.hasNext()) {
	// Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>)
	// iterator.next();
	// System.out.println("key:"+entry.getKey()+"  value:"+entry.getValue());
	// }
	// System.out.println("tLocation:"+tLocation+" titleMap.size"+titleMap.size());
	// e.printStackTrace();
	//				
	// // TODO: handle exception
	// }
	//				
	// }
	// }
	// content = TextNormalizer.Normalizer(content);
	// return content;
	// }

	public HashMap<Integer, String> getTitleMap()
	{
		return titleMap;
	}

	public void setTitleMap(HashMap<Integer, String> titleMap)
	{
		this.titleMap = titleMap;
	}

	public int gethTitleLocation()
	{
		return hTitleLocation;
	}

	public void sethTitleLocation(int hTitleLocation)
	{
		this.hTitleLocation = hTitleLocation;
	}

	// public double getTitleScore() {
	// return titleScore;
	// }
	//
	//
	// public void setTitleScore(double titleScore) {
	// this.titleScore = titleScore;
	// }

	public void setTitle(String title)
	{
		this.title = title;
	}

	public boolean isFirstSegTitle()
	{
		return firstSegTitle;
	}

	public void setFirstSegTitle(boolean firstSegTitle)
	{
		this.firstSegTitle = firstSegTitle;
	}

	public boolean isLastSegTitle()
	{
		return lastSegTitle;
	}

	public void setLastSegTitle(boolean lastSegTitle)
	{
		this.lastSegTitle = lastSegTitle;
	}

}
