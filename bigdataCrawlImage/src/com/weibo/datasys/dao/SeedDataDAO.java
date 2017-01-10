/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.model.CommonData;
import com.weibo.datasys.model.InvalidFomatException;
import com.weibo.datasys.model.SeedData;

public class SeedDataDAO extends CommonDAO {

	private static Logger logger = LoggerFactory.getLogger(SeedDataDAO.class);

	private static SeedDataDAO instance;

	protected SeedDataDAO() {
	}

	public static SeedDataDAO getInstance() {
		if (instance == null) {
			instance = new SeedDataDAO();
		}
		return instance;
	}


	@Override
	public List<SeedData> getByCount(int count, String dsname, String db,
			String table) {
		List<SeedData> datas = new ArrayList<SeedData>();
		List<? extends CommonData> commonDatas = super.getByCount(count,
				dsname, db, table);
		for (CommonData commonData : commonDatas) {
			try {
				SeedData data = wrap(commonData);
				datas.add(data);
			} catch (InvalidFomatException e) {
				logger.error("[WrapSeedDataError] - ", e);
			}
		}
		commonDatas.clear();
		return datas;
	}

	@Override
	public SeedData getById(String id, String dsname, String db, String table) {
		CommonData commonData = super.getById(id, dsname, db, table);
		SeedData data = null;
		try {
			data = wrap(commonData);
		} catch (InvalidFomatException e) {
			logger.error("[WrapSeedDataError] - ", e);
		}
		return data;
	}

	@Override
	public List<SeedData> getByOffsetOrderCount(long offset, int count,
			String orderBy, boolean desc, String dsname, String db, String table) {
		List<SeedData> datas = new ArrayList<SeedData>();
		List<? extends CommonData> commonDatas = super.getByOffsetOrderCount(
				offset, count, orderBy, desc, dsname, db, table);
		for (CommonData commonData : commonDatas) {
			try {
				SeedData data = wrap(commonData);
				datas.add(data);
			} catch (InvalidFomatException e) {
				logger.error("[WrapSeedDataError] - ", e);
			}
		}
		commonDatas.clear();
		return datas;
	}

	@Override
	public List<SeedData> getBySQL(String sql, String dsname) {
		List<SeedData> datas = new ArrayList<SeedData>();
		List<? extends CommonData> commonDatas = super.getBySQL(sql, dsname);
		for (CommonData commonData : commonDatas) {
			try {
				SeedData seedData = wrap(commonData);
				datas.add(seedData);
			} catch (InvalidFomatException e) {
				logger.error("[WrapSeedDataError] - ", e);
			}
		}
		return datas;
	}

	/**
	 * 
	 * 将CommonData包装成SeedData
	 * 
	 * @param commonData
	 * @return
	 * @throws InvalidFomatException
	 */
	private SeedData wrap(CommonData commonData) throws InvalidFomatException {
		SeedData data = null;
		if (commonData != null) {
			data = new SeedData();
			data.setId(commonData.getId());
			data.setBaseMap(commonData.getBaseString());
			data.setExtendMap(commonData.getExtendString());
		}
		return data;
	}

}
