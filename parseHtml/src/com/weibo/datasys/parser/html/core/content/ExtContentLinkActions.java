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
import java.util.Map.Entry;

import com.weibo.datasys.parser.html.core.domtree.BasicBlockAction;
import com.weibo.datasys.parser.html.data.BlockInfo;

public class ExtContentLinkActions extends BasicBlockAction
{

	
	private HashMap<String,HashMap<String,String>> imgUrls = new HashMap<String,HashMap<String,String>>();

	public ExtContentLinkActions()
	{
		imgUrls.put("images", new HashMap<String,String>());
		imgUrls.put("href", new HashMap<String,String>());
	}
	
	@Override
	public boolean action(BlockInfo currentBlock) {
		
		if (currentBlock.isImgNode()){
			//System.out.println("yes");
			String url = currentBlock.getImgUrl().trim();
			String md5Id = currentBlock.getImageMd5Id();
			String currentDate = currentBlock.getCurrentData();
			if ("".equals(url)){
				return false;
			}
			if (imgUrls.containsKey(url)){
				return false;
			}
			
			HashMap<String, String> imageAttrs = currentBlock.getImageAttrs();
			
			String width = "None";
			String height = "None";
			for (Entry<String, String> entry : imageAttrs.entrySet()) {
				String attrName = entry.getKey();
				String attrValue = entry.getValue();
				if ("width".equalsIgnoreCase(attrName)){
					width = attrValue;
				}
				if ("height".equalsIgnoreCase(attrName)){
					height = attrValue;
				}
			}
			
			imgUrls.get("images").put(url, md5Id + "\t" + currentDate + "\t" + width + "\t" + height);
		}
		
		if (currentBlock.isPreviousPageNode()){
			//System.out.println("yes");
			String url = currentBlock.getHref();
			String md5Id = currentBlock.getHrefMd52id();
			//String currentDate = currentBlock.getCurrentData();
			if ("".equals(url)){
				return false;
			}
			if (imgUrls.containsKey(url)){
				return false;
			}
			
			imgUrls.get("href").put("up",url + "\t" + md5Id);
		}
		
		if (currentBlock.isNextPageNode()){
			//System.out.println("yes");
			String url = currentBlock.getHref();
			String md5Id = currentBlock.getHrefMd52id();
			//String currentDate = currentBlock.getCurrentData();
			if ("".equals(url)){
				return false;
			}
			if (imgUrls.containsKey(url)){
				return false;
			}
			
			imgUrls.get("href").put("down",url + "\t" + md5Id);
		}

		return true;
	}
	public HashMap<String,HashMap<String,String>> getLinkUrls() {
		return imgUrls;
	}
}
