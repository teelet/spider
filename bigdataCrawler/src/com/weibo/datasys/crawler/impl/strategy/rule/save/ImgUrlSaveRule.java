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

import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;

import com.weibo.datasys.common.dao.CommonDAO;
import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.IOUtil;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;

/**
 * Field存储规则实现类
 * 
 */
public class ImgUrlSaveRule extends AbstractSaveRule {

	private String dsname;

	private String db;

	private String table;

	private int subDirCount = 2;

	private int subDirLength = 2;

	public ImgUrlSaveRule(Task task) {
		super(task);
	}

	@Override
	public Null apply(ParseInfo in) {
		if (in.getPageData() != null) {
			String id = in.getThisCrawlInfo().getSeedData()
					.getExtendField("id");
			if (StringUtils.isEmptyString(id)) {
				id = in.getThisCrawlInfo().getSeedData().getUrlId();
			}
			String path = "/" + IOUtil.md5ToPath(id, subDirCount, subDirLength)
					+ "/" + id + ".p";
			CommonData data = new CommonData();
			data.setId(id);
			data.setBaseField("local_img", path);
			CommonDAO.getInstance().save(data, dsname, db, table, true, false);
		}
		return null;
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		this.dsname = paraMap.get("dsname");
		this.db = paraMap.get("db");
		this.table = paraMap.get("table");
		subDirCount = StringUtils.parseInt(paraMap.get("subDirCount"),
				subDirCount);
		subDirLength = StringUtils.parseInt(paraMap.get("subDirLength"),
				subDirLength);
		this.type = "ImgUrl";
	}

}
