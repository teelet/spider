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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.dao.CommonDAO;
import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.crawler.base.strategy.rule.seed.AbstractSeedGenerateRule;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.base.entity.Task;

/**
 * 
 * 固定列表种子生成规则，通过读取文件获取种子列表
 * 
 */
public class CommonRule extends AbstractSeedGenerateRule {

	private static Logger logger = LoggerFactory.getLogger(CommonRule.class);

	protected static final String SQL = "select * from db.table where taskid='{taskid}'";

	protected String dsname;

	protected String db;

	protected String table;

	protected List<AbstractSeedGenerateRule> seedRules = new ArrayList<AbstractSeedGenerateRule>();

	/**
	 * @param task
	 */
	public CommonRule(Task task) {
		super(task);
	}

	@Override
	public List<SeedData> apply(Null in) {
		List<SeedData> seedDatas = new ArrayList<SeedData>();
		try {
			for (AbstractSeedGenerateRule seedRule : seedRules) {
				List<SeedData> tmpDatas = seedRule.apply(in);
				logger.info("[CommonSeedRule] - apply rule=[{}] | count={}",
						seedRule.getName(), tmpDatas.size());
				seedDatas.addAll(tmpDatas);
			}
		} catch (Exception e) {
			logger.error("[GenerateSeedError] - e.msg={}", e.getMessage());
		}
		return seedDatas;
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		dsname = paraMap.get("dsname");
		db = paraMap.get("db");
		table = paraMap.get("table");
		String sql = SQL.replace("db", db).replace("table", table)
				.replace("{taskid}", this.task.getTaskId());
		List<? extends CommonData> seedRuleDatas = CommonDAO.getInstance()
				.getBySQL(sql, dsname);
		for (CommonData seedRuleData : seedRuleDatas) {
			AbstractSeedGenerateRule seedRule = parseSeedRule(seedRuleData);
			seedRules.add(seedRule);
		}
	}

	/**
	 * @param seedRuleData
	 * @return
	 */
	private AbstractSeedGenerateRule parseSeedRule(CommonData seedRuleData) {
		AbstractSeedGenerateRule seedRule = null;
		String seedRuleString = seedRuleData.getBaseField("seedrule");
		try {
			JSONObject jsonObject = new JSONObject(seedRuleString);
			String ruleName = jsonObject.getString("rulename");
			seedRule = (AbstractSeedGenerateRule) Class
					.forName(
							"com.panguso.ps.crawler.impl.strategy.rule.seed."
									+ ruleName).getConstructor(Task.class)
					.newInstance(this.task);
			Map<String, String> paraMap = new HashMap<String, String>();
			jsonObject = jsonObject.getJSONObject("paras");
			for (String paraKey : JSONObject.getNames(jsonObject)) {
				paraMap.put(paraKey, jsonObject.getString(paraKey));
			}
			seedRule.configWithParameters(paraMap);
		} catch (Exception e) {
			logger.error("[parseSeedRuleError] - seedRuleString={} | e.msg={}",
					seedRuleString, e.getMessage());
		}
		return seedRule;
	}

}
