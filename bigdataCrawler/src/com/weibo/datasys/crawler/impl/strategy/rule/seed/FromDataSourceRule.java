/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.strategy.rule.seed;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.dao.CommonDAO;
import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.MD5Util;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.strategy.rule.seed.AbstractSeedGenerateRule;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.factory.SeedDataFactory;

/**
 * 
 * 从数据源生成种子的规则
 * 
 */
public class FromDataSourceRule extends AbstractSeedGenerateRule {

	private static Logger logger = LoggerFactory
			.getLogger(FromDataSourceRule.class);

	protected String dsname;

	protected String db;

	protected String[] tables = new String[0];

	protected String urlFieldName;

	protected String[] extendFieldNames;

	protected String[] selectSQLs = new String[0];

	/**
	 * @param task
	 */
	public FromDataSourceRule(Task task) {
		super(task);
	}

	@Override
	public List<SeedData> apply(Null in) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		try {
			List<CommonData> datas = new ArrayList<CommonData>();
			if (selectSQLs.length == 0) {
				for (String table : tables) {
					List<? extends CommonData> tmpDatas = CommonDAO
							.getInstance().getByCount(-1, dsname, db, table);
					if (tmpDatas != null) {
						logger.info("[GetSeedFromDS] - table={} | count={}",
								table, tmpDatas.size());
						datas.addAll(tmpDatas);
					}
				}
			} else {
				for (String selectSQL : selectSQLs) {
					List<? extends CommonData> tmpDatas = CommonDAO
							.getInstance().getBySQL(selectSQL, dsname);
					if (tmpDatas != null) {
						logger.info("[GetSeedFromDS] - sql={} | count={}",
								selectSQL, tmpDatas.size());
						datas.addAll(tmpDatas);
					}
				}
			}
			for (CommonData data : datas) {
				String url = data.getBaseField(urlFieldName);
				try {
					new URL(url);
				} catch (Exception e) {
					continue;
				}
				SeedData seedData = SeedDataFactory.buildBaseSeedData(url,
						this.task.getTaskId());
				SeedDataFactory.recognizeSeedLevel(seedData, this.task);
				seedDatas.add(seedData);
				for (String extFieldName : extendFieldNames) {
					seedData.setExtendField(extFieldName,
							data.getBaseField(extFieldName));
				}
				String linkId = task.getDeduplicator().getLinkId(seedData);
				if (!linkId.equals(seedData.getUrlId())) {
					seedData.setId(MD5Util.MD5(seedData.getId() + "_" + linkId));
				}
			}
			datas.clear();
			int beforeCount = seedDatas.size();
			logger.info("[DedupSeeds] - start. task={}", task.getTaskId());
			long s = System.currentTimeMillis();
			task.getDeduplicator().deDuplicateSeedDatas(seedDatas, task);
			long e = System.currentTimeMillis();
			logger.info(
					"[DedupSeeds] - task={} | before={} | after={} | cost={} ms",
					new Object[] { task.getTaskId(), beforeCount,
							seedDatas.size(), e - s });
		} catch (Exception e) {
			logger.error("[GenerateSeedFromDSError] - e.msg={}", e.getMessage());
		}
		return seedDatas;
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		dsname = paraMap.get("dsname");
		db = paraMap.get("db");
		String tableString = paraMap.get("tables");
		if (StringUtils.isNotEmpty(tableString)) {
			tables = paraMap.get("tables").split(";");
		}
		urlFieldName = paraMap.get("urlFieldName");
		String sqlString = paraMap.get("selectSQLs");
		if (StringUtils.isNotEmpty(sqlString)) {
			selectSQLs = sqlString.split(";");
		}
		extendFieldNames = paraMap.get("extendFieldNames").split(";");
	}

}
