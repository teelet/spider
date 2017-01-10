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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.dao.CommonDAO;
import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.StopWatch;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.monitor.CrawlerMonitorInfo;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;

/**
 * Field存储规则实现类
 * 
 * @author zouyandi
 * 
 */
public class FieldSaveRule extends AbstractSaveRule {

	private static Logger logger = LoggerFactory.getLogger(FieldSaveRule.class);

	private String dsname;

	private String db;

	private String table;

	public FieldSaveRule(Task task) {
		super(task);
	}

	@Override
	public Null apply(ParseInfo in) {
		List<CommonData> fields = in.getFields(this.type);
		if (fields != null && fields.size() > 0) {
			boolean isUseDefaultId = !StringUtils.isEmptyString(fields.get(0)
					.getId());
			if (!isUseDefaultId) {
				Set<String> idSet = new HashSet<String>();
				Iterator<CommonData> iterator = fields.iterator();
				while (iterator.hasNext()) {
					CommonData field = iterator.next();
					if (!idSet.add(field.getBaseField(field.getBaseFieldNames()
							.get(0)))) {
						iterator.remove();
					}
				}
			}
			StopWatch watch = new StopWatch();
			watch.start();
			logger.debug("[SaveFieldBatch] - start. type={} | count={}",
					new Object[] { this.type, fields.size() });
			CommonDAO.getInstance().saveBatch(fields, dsname, db, table, true,
					false, false, isUseDefaultId);
			CrawlerMonitorInfo.addCounter("saveField_" + this.type,
					fields.size());
			logger.debug(
					"[SaveFieldBatch] - end. type={} | cost {}ms | count={}",
					new Object[] { this.type, watch.getElapsedTime(),
							fields.size() });
		}
		return null;
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		this.dsname = paraMap.get("dsname");
		this.db = paraMap.get("db");
		this.table = paraMap.get("table");
		// Field存储，用Field规则名称作为队列类型
		this.type = getName();
	}

}
