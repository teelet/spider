/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.pubtime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubtimeExtractor
{

	private static final Logger LOG = LoggerFactory.getLogger(PubtimeExtractor.class);
	public static final int MAX_MATCH_COUNT = 10;

	public static long extractTime(String content, String url)
	{
		long time = extractTime(content, url, false);
		return time;
	}

	public static long extractTime(String content, String url, boolean retry)
	{
		ArrayList<Long> time = new ArrayList<Long>();
		long result = 0;
		String all = "";
		String month = "08";
		String year = "2012";
		String day = "01";
		String hour = "00";
		String minute = "00";
		String second = "00";
		Pattern pattern = null;
		if (!retry)
		{
			pattern = Pattern
					.compile("[^\\d](\\d{2}|\\d{4})[年|\\-|\\.|/]\\s*([0-1]?[0-9])[月|\\-|\\.|/]\\s*([0-3]?[0-9])[日|号]?\\s*(([0-2]?[0-9])[:|：]([0-5]?[0-9])[:|：]([0-5]?[0-9])|([0-2]?[0-9])[:|：|时]([0-5]?[0-9])分?)($|[^\\d])");
		} else
		{
			pattern = Pattern
					.compile("[^\\d](\\d{2}|\\d{4})[年|\\-|\\.|/]\\s*([0-1]?[0-9])[月|\\-|\\.|/]\\s*([0-3]?[0-9])[日|号]?($|[^\\d])");
		}
		Matcher matcher = pattern.matcher(content);

		try
		{
			int matchTimes = 2;
			while (matcher.find())
			{
				all = matcher.group();
				if (matchTimes <= 0)
				{
					break;
				}
				if (all.contains("://") || all.contains("http"))
				{
					continue;
				} else
				{
					matchTimes--;
				}
				if (matcher.groupCount() >= 3)
				{
					year = matcher.group(1);
					if (2 == year.length())
					{
						Calendar calendar = Calendar.getInstance();
						year = String.valueOf(calendar.get(Calendar.YEAR)).substring(0, 2) + year;
					}
					month = (1 == matcher.group(2).length()) ? "0" + matcher.group(2) : matcher
							.group(2);
					day = (1 == matcher.group(3).length()) ? "0" + matcher.group(3) : matcher
							.group(3);
					if (!retry)
					{
						if (null != matcher.group(5))
						{
							hour = matcher.group(5).length() == 1 ? "0" + matcher.group(5)
									: matcher.group(5);
							minute = matcher.group(6).length() == 1 ? "0" + matcher.group(6)
									: matcher.group(7);
							second = matcher.group(7).length() == 1 ? "0" + matcher.group(7)
									: matcher.group(7);
						} else if (null != matcher.group(8))
						{
							hour = matcher.group(8).length() == 1 ? "0" + matcher.group(8)
									: matcher.group(8);
							minute = matcher.group(9).length() == 1 ? "0" + matcher.group(9)
									: matcher.group(9);
						}
					}
				}
				if (check(year, month, day))
				{
					String date = year + "年" + month + "月" + day + "日" + hour + ":" + minute + ":"
							+ second;
					time.add(toMillisecond(date));
					// break;
				}
			}
			if (0 == time.size() && !retry)
			{
				result = extractTime(content, url, true);
			} else if (0 != time.size())
			{
				long nowSubNum = 0;
				long nowTime = System.currentTimeMillis();
				long subNum = nowTime;
				for (int i = 0; i < time.size(); i++)
				{
					nowSubNum = nowTime - time.get(i);
					if (nowSubNum > 0 && nowSubNum < subNum)
					{
						subNum = nowSubNum;
						result = time.get(i);
					}
				}
			}
		} catch (Exception e)
		{
			LOG.error("[Exception]:{}\t[URL]:{}", e, url);
		}
		return result;
	}

	public static long toMillisecond(String date) throws Exception
	{
		long time = 0;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss",
				Locale.CHINA);
		time = simpleDateFormat.parse(date).getTime();
		return time;
	}

	public static long chooseTime(long tagTime, long contentTime)
	{
		if (0 == tagTime)
		{
			return contentTime;
		} else if (0 == contentTime)
		{
			return tagTime;
		} else
		{
			if (String.valueOf(tagTime).split("0").length < String.valueOf(contentTime).split("0").length)
				return contentTime;
			else
				return tagTime;
		}
	}

	/**
	 * check 时间
	 * 
	 * @param time
	 * @return
	 */
	public static boolean check(String yearStr, String monthStr, String dayStr)
	{
		boolean result = false;
		Calendar rightNow = Calendar.getInstance();

		int nowYear = rightNow.get(Calendar.YEAR);
		int nowMonth = rightNow.get(Calendar.MONTH) + 1;
		int nowDay = rightNow.get(Calendar.DAY_OF_MONTH);

		try
		{
			int year = Integer.parseInt(yearStr);
			int month = Integer.parseInt(monthStr);
			int day = Integer.parseInt(dayStr);

			if (year < nowYear && year > 1990)
			{
				result = true;
			} else if (year == nowYear && month < nowMonth)
			{
				result = true;
			} else if (year == nowYear && month == nowMonth && day <= nowDay)
			{
				result = true;
			}

		} catch (Exception e)
		{
		}
		return result;

	}

	public static String postProcess(long time, String content)
	{
		int checkLength = 100;
		if (content.length() > checkLength)
		{
			String del = content.substring(0, checkLength);
			// del = del.replace(time, "");
			del = del.replace("发布时间:", "");
			del = del.replace("发布时间：", "");
			del = del.replace("时间：", "");
			del = del.replace("()", "");
			del = del.replace("（）", "");
			content = del + content.substring(checkLength);

		}
		return content;
	}

	public static void main(String argv[]) throws Exception
	{

		String time = "中国有色矿业集团与江西省开展全面经济技术合作\r\n中央政府门户网站 www.gov.cn 2007年05月08日 来源：国资委网站【字体：\r\n2007年4月27日，中国有";
		System.out.println(new Date(extractTime(time, "")));
	}

}
