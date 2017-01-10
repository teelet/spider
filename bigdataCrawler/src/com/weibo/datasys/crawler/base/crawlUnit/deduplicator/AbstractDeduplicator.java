/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.crawlUnit.deduplicator;

import java.util.Collection;
import java.util.Map;

import com.weibo.datasys.common.conf.IConfigurable;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;

public abstract class AbstractDeduplicator implements IConfigurable {

	protected String extendIdFieldName = "id";

	/**
	 * 对指定种子集合进行去重，会修改原集合
	 * 
	 * @param seedDatas
	 * @param task
	 */
	public abstract void deDuplicateSeedDatas(Collection<SeedData> seedDatas,
			Task task);

	/**
	 * 获取与指定seed对应的link，若不存在返回null
	 * 
	 * @param seedData
	 * @return
	 */
	public abstract LinkData getExistLink(SeedData seedData);

	/**
	 * 批量保存link到链接库
	 * 
	 * @param task
	 * @param linkDatas
	 */
	public abstract void saveLinks(Task task, Collection<LinkData> linkDatas);

	public String getLinkId(SeedData seedData) {
		String id = seedData.getUrlId();
		if (StringUtils.isNotEmpty(extendIdFieldName)) {
			id = seedData.getExtendField(extendIdFieldName);
		}
		return id;
	}

	@Override
	public void configWithKeyValues(Map<String, String> paraMap) {
		extendIdFieldName = paraMap.get("extendIdFieldName");
	}

	public String getExtendIdFieldName() {
		return extendIdFieldName;
	}
}
