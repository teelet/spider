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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.ObjectUtils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.dao.CommonDAO;
import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.util.StopWatch;
import com.weibo.datasys.crawler.base.entity.ParseInfo;
import com.weibo.datasys.crawler.base.entity.Task;
import com.weibo.datasys.crawler.base.strategy.rule.save.AbstractSaveRule;
import com.weibo.datasys.crawler.utils.URLUtil;

public class StatisticSaveRule extends AbstractSaveRule {

	private static Logger logger = LoggerFactory
			.getLogger(StatisticSaveRule.class);

	private static Map<String, AtomicInteger> hostCountMap = new ConcurrentHashMap<String, AtomicInteger>();

	private String dsname;

	private String db;

	private String table;

	private AtomicInteger saveCount = new AtomicInteger();

	private AtomicLong lastSaveTime = new AtomicLong();

	public StatisticSaveRule(Task task) {
		super(task);
		this.type = "STATISTIC";
	}

	@Override
	public Null apply(ParseInfo in) {
		String url = in.getThisCrawlInfo().getSeedData().getUrl();
		String host = URLUtil.getHost(url);
		AtomicInteger count = hostCountMap.get(host);
		if (count == null) {
			synchronized (StatisticSaveRule.class) {
				if (count == null) {
					count = new AtomicInteger();
					hostCountMap.put(host, count);
				} else {
					count = hostCountMap.get(host);
				}
			}
		}
		count.incrementAndGet();
		saveResult();
		return null;
	}

	private synchronized void saveResult() {
		if (saveCount.incrementAndGet() % 10000 == 0
				|| System.currentTimeMillis() - lastSaveTime.get() >= 300000) {
			StopWatch watch = new StopWatch();
			watch.start();
			logger.info("[SaveStatisticResult] - Start.");
			List<CommonData> datas = new ArrayList<CommonData>();
			for (Entry<String, AtomicInteger> entry : hostCountMap.entrySet()) {
				CommonData data = new CommonData();
				data.setId(entry.getKey());
				data.setBaseField("count", entry.getValue().get());
				datas.add(data);
			}
			CommonDAO.getInstance().saveBatch(datas, this.dsname, this.db,
					this.table, true, false);
			logger.info(
					"[SaveStatisticResult] - Done. cost {}ms. saveCount={}",
					watch.getElapsedTime(), saveCount.get());
			lastSaveTime.set(System.currentTimeMillis());
		}
	}

	@Override
	public void configWithParameters(Map<String, String> paraMap) {
		this.dsname = paraMap.get("dsname");
		this.db = paraMap.get("db");
		this.table = paraMap.get("table");
	}

}
