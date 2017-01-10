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

import org.w3c.dom.Node;

import com.weibo.datasys.parser.html.core.domtree.BasicBlockAction;
import com.weibo.datasys.parser.html.data.BlockInfo;

public class CalculateDistanceAction  extends BasicBlockAction
{
	public static final float CHARACTER_DISTANCE=0.05f;//假定每行有1/CHARACTER_DISTANCE个字符
	public static final float IMG_DISTANCE=0.2f;		//假定1/IMG_DISTANCE张图片为一行
	private ArrayList<BlockInfo> blockList=new ArrayList<BlockInfo>();
	float curDistance=0.0f;
	@Override
	public boolean action(BlockInfo currentBlock) {
		boolean result=true;
		short nodeType=currentBlock.getNode().getNodeType();
		String nodeName=currentBlock.getNodeName();
		if(nodeType==Node.TEXT_NODE)
		{
			String text=currentBlock.getNode().getNodeValue().trim();
			curDistance+=text.length()*CHARACTER_DISTANCE;
		}
		else if("td".equalsIgnoreCase(nodeName)||"div".equals(nodeName))
		{
			curDistance=new Double(Math.ceil(curDistance)).floatValue()-CHARACTER_DISTANCE;
		}
		else if("br".equalsIgnoreCase(nodeName))
			curDistance+=1.0f;
		else if("img".equalsIgnoreCase(nodeName)||"input".equalsIgnoreCase(nodeName))
			curDistance+=IMG_DISTANCE;
		else if("head".equalsIgnoreCase(nodeName))
			result=false;
		currentBlock.setDistance(Math.round(curDistance+0.49f));
		
		blockList.add(currentBlock);
		return result;
	}
	public ArrayList<BlockInfo> getBlockList() {
		return blockList;
	}
	
	

}
