/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.save;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.util.IOUtil;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.CrawlInfo;
import com.weibo.datasys.crawler.base.entity.PageData;
import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.manager.QueueManager.QueueType;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;

/**
 * 
 * 网页信息保存到本地文件存储规则实现类
 */
public class PageToFileSaveRule extends AbstractSaveRule {

	private static Logger logger = LoggerFactory
			.getLogger(PageToFileSaveRule.class);

	private File baseDir = new File("page");

	private int subDirCount = 2;

	private int subDirLength = 2;

	/**
	 * @param task
	 */
	public PageToFileSaveRule(Task task) {
		super(task);
		this.type = QueueType.SAVE_PAGE.name();
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		if (StringUtils.isNotEmpty(paraMap.get("baseDir"))) {
			baseDir = new File(paraMap.get("baseDir"));
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
		}
		subDirCount = StringUtils.parseInt(paraMap.get("subDirCount"),
				subDirCount);
		subDirLength = StringUtils.parseInt(paraMap.get("subDirLength"),
				subDirLength);
	}

	@Override
	public Null apply(ParseInfo parseInfo) {
		CrawlInfo crawlInfo = parseInfo.getThisCrawlInfo();
		Task task = crawlInfo.getValidTask();
		if (task == null) {
			return null;
		}
		PageData pageData = parseInfo.getPageData();
		if (pageData != null) {
			try {
				String id = crawlInfo.getSeedData().getExtendField("id");
				if (StringUtils.isEmptyString(id)) {
					id = pageData.getId();
				}
				String path = IOUtil.md5ToPath(id, subDirCount, subDirLength);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String today = sdf.format(new Date());
				String pageDirString = baseDir.getPath() + File.separatorChar
						+ today + File.separatorChar + path;
				File pageDir = new File(pageDirString);
				pageDir.mkdirs();
				String pagePath = pageDir.getPath() + File.separatorChar + id
						+ ".p";
				OutputStream out = new BufferedOutputStream(
						new FileOutputStream(pagePath));
				out.write(pageData.getHtml());
				out.close();
				// logger.info("[SavePageToFile] - url={}", pageData.getUrl());
			} catch (Exception e) {
				logger.error("[PageToFileSaveRuleError] - ", e);
			}
			countSaveResult("Page", 1);
		}
		return null;
	}

}
