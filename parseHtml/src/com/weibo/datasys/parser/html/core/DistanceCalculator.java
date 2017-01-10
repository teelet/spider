/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */

package com.weibo.datasys.parser.html.core;

import java.util.ArrayList;

import com.weibo.datasys.parser.html.core.domtree.TravBlockTree;
import com.weibo.datasys.parser.html.data.BlockInfo;

public class DistanceCalculator
{
	public static ArrayList<BlockInfo> getBlockList(BlockInfo root) throws Exception
	{
		ArrayList<BlockInfo> result = null;
		CalculateDistanceAction distanceAction = new CalculateDistanceAction();
		
		TravBlockTree travBlockTree = new TravBlockTree(distanceAction);
		travBlockTree.recurTree(root);
		result=distanceAction.getBlockList();
		return result;
	}


}
