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

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.TaskFactory;
import com.weibo.datasys.crawler.base.strategy.rule.filter.AbstractFilterRule;

/**
 * 
 * 同一host下的子文件夹过滤器
 * 
 */
public class SubFolderFilter extends AbstractFilterRule {
	
	private static Logger logger = LoggerFactory.getLogger(SubFolderFilter.class);

	private Map<String,Set<String>> filterMap = new HashMap<String,Set<String>>();

	/**
	 * @param task
	 */
	public SubFolderFilter(Task task) {
		super(task);
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		String home = System.getProperty("home.dir");
		String file = home + "/" + paraMap.get("file");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String tmp = "";
			while (null != (tmp = reader.readLine())) {
				tmp = tmp.trim().toLowerCase();
				if (StringUtils.isEmptyString(tmp) || tmp.startsWith("#")) {
					continue;
				}
				String[] subFolderPair = tmp.split(",");
				if(subFolderPair.length != 2){
					continue;
				}
				String host = subFolderPair[0];
				String subFolderName = subFolderPair[1];
				Set<String> filterSet = filterMap.get(host);
				if(null == filterSet){
					filterSet = new HashSet<String>();
					filterMap.put(host, filterSet);
				}
				filterSet.add(subFolderName);
			}
			reader.close();
		} catch (Exception e) {
		}
	}

	@Override
	public Boolean apply(String in) {
		boolean result = false;
		try {
			URL url = new URL(in);
			String host = url.getHost();
			Set<String> filterSet = filterMap.get(host);
			if(null == filterSet || filterSet.size() == 0){
				return result;
			}
			String path = url.getPath().trim().toLowerCase();
			path = path.substring(0, path.lastIndexOf("/"));
			String[] subFolders = path.split("/");
			for(int i=0;i<subFolders.length;i++){
				if(subFolders[i].equals("")){
					continue;
				}
				if (filterSet.contains(subFolders[i])) {
					result = true;
					break;
				} 
			}
		} catch (Exception e) {
			logger.error("[SubFolderFilter.apply] - {}", e.toString());
		}
		return result;
	}

}
