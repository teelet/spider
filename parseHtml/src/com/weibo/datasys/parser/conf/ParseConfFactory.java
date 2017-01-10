/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.conf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import com.weibo.datasys.common.rule.ParseStrategy;

public class ParseConfFactory {

	private static Map<String, ParseConfig> allConfMap = new HashMap<String, ParseConfig>();


	public static Map<String, ParseConfig> getAllConfMap() {
		return allConfMap;
	}


	public static void setAllConfMap(Map<String, ParseConfig> allConfMap) {
		ParseConfFactory.allConfMap = allConfMap;
	}


	public static void buildParseConf(){
		
		HashSet<String> allHost = ParseStrategy.getAllHost();
		Iterator<String> itr = allHost.iterator();  
		while(itr.hasNext()){  
			String host = itr.next(); 
			ParseConfig oneConf = new ParseConfig();
			oneConf.setHost(host);
			oneConf.init(host);
			allConfMap.put(oneConf.getHost(), oneConf);
		}
	}
}
