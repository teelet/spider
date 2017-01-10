/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.domtree;

import java.util.ArrayList;

import com.weibo.datasys.parser.html.data.BlockInfo;

public class BlockManager
{


	
	public static BlockInfo getHeadBlock(ArrayList<BlockInfo> blockList) {
		return getBlock(blockList,"head");
	}
	
	public static BlockInfo getBodyBlock(ArrayList<BlockInfo> blockList) {
		return getBlock(blockList,"body");
	}
	
	private static BlockInfo getBlock(ArrayList<BlockInfo> blockList,String name){
		
		BlockInfo curBlock = null;
		if (blockList!=null) {
			for (int i = 0; i < blockList.size(); i++) {
				curBlock = blockList.get(i);
				if (null!=curBlock&&curBlock.getNodeName().trim().equalsIgnoreCase(name)) {
					break;
				}
			}
		}
	
		return curBlock;
	}

}
