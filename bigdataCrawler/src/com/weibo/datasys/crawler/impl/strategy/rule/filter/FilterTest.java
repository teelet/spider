/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.filter;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jinkui
 *
 */
public class FilterTest {
	
	public static void main(String[] args){
		String url = " http://gg.163.com/special/lol-news/";
		String[] filters = {"lol","hsct","lol-news"};
		Set<String> filterSet = new HashSet<String>();
		filterSet.addAll(Arrays.asList(filters));
		if(apply(url,filterSet)){
			System.out.println("haha");
		}
		
	}
	public static Boolean apply(String in,Set<String> filterSet)  {
		boolean result = false;
		try {
			URL url = new URL(in);
			String path = url.getPath().trim().toLowerCase();
			path = path.substring(0, path.lastIndexOf("/"));
			String[] subFolders = path.split("/");
			for(int i=0;i<subFolders.length;i++){
				if (filterSet.contains(subFolders[i])) {
					result = true;
					break;
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
