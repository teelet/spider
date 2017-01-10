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
import java.util.Stack;

import com.weibo.datasys.parser.html.data.BlockInfo;

public class BlockWalker
{

	 private BlockInfo currentBlock;
	  private ArrayList<BlockInfo> currentChildren;
	  private Stack<BlockInfo> blocks;
	  
	  /**
	   * Starts the <code>Node</code> tree from the root node.
	   * 
	   * @param rootNode
	   */
	  public BlockWalker(BlockInfo rootBlock) {

		  blocks = new Stack<BlockInfo>();
		  blocks.add(rootBlock);
	  }
	  
	  /**
	   */
	  public BlockInfo nextBlock() {
	    
	    // if no next node return null
	    if (!hasNext()) {
	      return null;
	    }
	    
	    // pop the next node off of the stack and push all of its children onto
	    // the stack
	    currentBlock = blocks.pop();
	    if (currentBlock==null) {
			return null;
		}
	    currentChildren =currentBlock.getChidrenBlockList();
	    int childLen = (currentChildren != null) ? currentChildren.size() : 0;
	    
	    // put the children node on the stack in first to last order
	    for (int i = childLen - 1; i >= 0; i--) {
	    	blocks.add(currentChildren.get(i));
	    }
	    
	    return currentBlock;
	  }
	  
	  /**
	   *
	   */
	  public void skipChildren() {
	    
	    int childLen = (currentChildren != null) ? currentChildren.size() : 0;
	    
	    for (int i = 0 ; i < childLen ; i++) {
	    	BlockInfo child = blocks.peek();
	      if (child.equals(currentChildren.get(i))) {
	    	  blocks.pop();
	      }
	    }
	  }
	  
	  /**
	   * Returns true if there are more nodes on the current stack.
	   * @return
	   */
	  public boolean hasNext() {
	    return (blocks.size() > 0);
	  }

}
