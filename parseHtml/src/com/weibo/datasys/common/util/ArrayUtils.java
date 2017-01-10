/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.util;

/**
 * 
 * 数组工具类
 *
 */
public class ArrayUtils
{
	public static int[][] initArray(int rowNum, int columnNum)
	{
		int[][] links = new int[rowNum][columnNum];
		for (int i = 0; i < rowNum; ++i)
		{
			for (int j = 0; j < columnNum; ++j)
			{
				if (i == j)
				{
					links[i][j] = 0;
				} else
				{
					links[i][j] = -1;
				}
			}
		}
		return links;
	}

	public static int[][] copyArray(int[][] links, int rowNum, int columnNum)
	{
		int[][] result = new int[rowNum][columnNum];
		for (int i = 0; i < rowNum; ++i)
		{
			for (int j = 0; j < columnNum; ++j)
			{
				result[i][j] = links[i][j];
			}
		}
		return result;
	}
}
