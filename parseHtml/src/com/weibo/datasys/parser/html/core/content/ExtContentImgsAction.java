/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.content;

import java.util.HashMap;
import com.weibo.datasys.parser.html.core.domtree.BasicBlockAction;
import com.weibo.datasys.parser.html.data.BlockInfo;

public class ExtContentImgsAction extends BasicBlockAction
{

	
	private HashMap<String,String> imgUrls = new HashMap<String,String>();

	public ExtContentImgsAction()
	{

	}
	
	@Override
	public boolean action(BlockInfo currentBlock) {
		if (currentBlock.isImgNode()){
			//System.out.println("yes");
			String url = currentBlock.getImgUrl().trim();
			String md5Id = currentBlock.getMd5Id();
			String currentDate = currentBlock.getCurrentData();
			if ("".equals(url)){
				return false;
			}
			if (imgUrls.containsKey(url)){
				return false;
			}
			imgUrls.put(url, md5Id + "\t" + currentDate);
		}

		return true;
	}
	public HashMap<String,String> getImgUrls() {
		return imgUrls;
	}
}
