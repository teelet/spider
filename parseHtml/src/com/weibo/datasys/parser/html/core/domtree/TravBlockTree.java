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

public class TravBlockTree
{

	private BasicBlockAction basicBlockAction = null;
	
	private ArrayList<BasicBlockAction> actionList=null;
	public TravBlockTree(ArrayList<BasicBlockAction> actions ) {
		this.actionList = actions;
	}
	public TravBlockTree(BasicBlockAction basicBlockAction) {
		this.basicBlockAction = basicBlockAction;
	}
	
	public void recurTreeMulti(BlockInfo root) throws Exception {
		BlockWalker walker = new BlockWalker(root);
		while (walker.hasNext())
		{
			BlockInfo currentBlock = walker.nextBlock();
			if (currentBlock!=null) {
				for (int i = 0; i < actionList.size(); i++) {
					actionList.get(i).action(currentBlock);
				}
			}
		}
	}
	
	public void recurTree(BlockInfo root) throws Exception
	{
		BlockWalker walker = new BlockWalker(root);

		while (walker.hasNext())
		{
			BlockInfo currentBlock = walker.nextBlock();
			if (currentBlock!=null) {
				if (!basicBlockAction.action(currentBlock)) {
					walker.skipChildren();
				}
			}
			
		}
		
	}
	

}
