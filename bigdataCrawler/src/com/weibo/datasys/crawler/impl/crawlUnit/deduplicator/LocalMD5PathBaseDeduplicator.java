/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.deduplicator;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.weibo.datasys.common.util.IOUtil;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.crawlUnit.deduplicator.AbstractDeduplicator;
import com.weibo.datasys.crawler.base.entity.LinkData;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.LinkDataFactory;


public class LocalMD5PathBaseDeduplicator extends AbstractDeduplicator {

	private File baseDir = new File("./");;

	private int subDirCount = 2;

	private int subDirLength = 2;

	@Override
	public void deDuplicateSeedDatas(Collection<SeedData> seedDatas, Task task) {
		Iterator<SeedData> iterator = seedDatas.iterator();
		while (iterator.hasNext()) {
			SeedData seedData = iterator.next();
			String path = IOUtil.md5ToPath(getLinkId(seedData), subDirCount,
					subDirLength);
			File linkDir = new File(baseDir, path);
			if (linkDir.exists()) {
				iterator.remove();
			}
		}
	}

	/**
	 * @param seedData
	 * @return
	 */
	@Override
	public LinkData getExistLink(SeedData seedData) {
		LinkData linkData = null;
		String path = IOUtil.md5ToPath(getLinkId(seedData), subDirCount,
				subDirLength);
		File linkDir = new File(baseDir, path);
		if (linkDir.exists()) {
			linkData = LinkDataFactory.buildFromSeedData(seedData);
		}
		return linkData;
	}

	/**
	 * @param task
	 * @param linkDatas
	 */
	@Override
	public void saveLinks(Task task, Collection<LinkData> linkDatas) {
		for (LinkData linkData : linkDatas) {
			String path = IOUtil.md5ToPath(linkData.getId(), subDirCount,
					subDirLength);
			File linkDir = new File(baseDir, path);
			linkDir.mkdirs();
		}
	}

	@Override
	public void configWithKeyValues(Map<String, String> paraMap) {
		super.configWithKeyValues(paraMap);
		if (StringUtils.isNotEmpty(paraMap.get("baseDir"))) {
			baseDir = new File(paraMap.get("baseDir"));
			baseDir.mkdirs();
		}
		subDirCount = StringUtils.parseInt(paraMap.get("subDirCount"),
				subDirCount);
		subDirLength = StringUtils.parseInt(paraMap.get("subDirLength"),
				subDirLength);
	}

}
