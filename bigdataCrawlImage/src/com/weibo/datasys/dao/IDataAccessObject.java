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

import java.util.Collection;
import java.util.List;

import com.weibo.datasys.model.CommonData;

public interface IDataAccessObject<T extends CommonData> {

	/**
	 * 
	 * 保存Data
	 * 
	 * @param data
	 *            要保存的data
	 * @param dsname
	 *            要保存到的数据源名称
	 * @param db
	 *            要保存到的数据库名称
	 * @param table
	 *            要保存到的数据库表名
	 * @param updateIfDuplicate
	 *            当主键重复时，是否更新数据
	 * @param saveExtendColumn
	 *            是否保存扩展属性列
	 * @return 0==error; 1==insert; 2==update；3==nochange
	 */
	public int save(T data, String dsname, String db, String table,
			boolean updateIfDuplicate, boolean saveExtendColumn);

	/**
	 * 
	 * 保存Data
	 * 
	 * @param data
	 *            要保存的data
	 * @param dsname
	 *            要保存到的数据源名称
	 * @param db
	 *            要保存到的数据库名称
	 * @param table
	 *            要保存到的数据库表名
	 * @param updateIfDuplicate
	 *            当主键重复时，是否更新数据
	 * @param saveExtendColumn
	 *            是否保存扩展属性列
	 * @param isAutoIncrement
	 *            是否使用自动递增id
	 * @param isUseDefaultId
	 *            是否使用默认id列，否则使用BaseField第一列作为id
	 * @return
	 */
	int save(CommonData data, String dsname, String db, String table,
			boolean updateIfDuplicate, boolean saveExtendColumn,
			boolean isAutoIncrement, boolean isUseDefaultId);

	/**
	 * 
	 * 批量保存Data
	 * 
	 * @param datas
	 *            要保存的datas
	 * @param dsname
	 *            要保存到的数据源名称
	 * @param db
	 *            要保存到的数据库名称
	 * @param table
	 *            要保存到的数据库表名
	 * @param updateIfDuplicate
	 *            当主键重复时，是否更新数据
	 * @param saveExtendColumn
	 *            是否保存扩展属性列
	 * @return [0]：failedCount。[1]：insertCount。[2]：updateCount。
	 */
	public int[] saveBatch(Collection<? extends CommonData> datas,
			String dsname, String db, String table, boolean updateIfDuplicate,
			boolean saveExtendColumn);

	/**
	 * 
	 * 批量保存Data，附加自动递增id参数
	 * 
	 * @param datas
	 *            要保存的datas
	 * @param dsname
	 *            要保存到的数据源名称
	 * @param db
	 *            要保存到的数据库名称
	 * @param table
	 *            要保存到的数据库表名
	 * @param updateIfDuplicate
	 *            当主键重复时，是否更新数据
	 * @param saveExtendColumn
	 *            是否保存扩展属性列
	 * @param isAutoIncrement
	 *            是否使用自动递增id
	 * @param isUseDefaultId
	 *            是否使用默认id列，否则使用BaseField第一列作为id
	 * @return [0]：failedCount。[1]：insertCount。[2]：updateCount。
	 */
	public int[] saveBatch(Collection<? extends CommonData> datas,
			String dsname, String db, String table, boolean updateIfDuplicate,
			boolean saveExtendColumn, boolean isAutoIncrement,
			boolean isUseDefaultId);

	/**
	 * 
	 * 获取指定数量Data，count<=0则获取全部，默认id排序
	 * 
	 * @param count
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public List<? extends CommonData> getByCount(int count, String dsname,
			String db, String table);

	/**
	 * 
	 * 获取指定数量Data，count<=0则获取全部，使用指定orderBy，desc字段排序，并且从指定的offset开始返回数据
	 * 
	 * @param offset
	 * @param count
	 * @param orderBy
	 * @param desc
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public List<? extends CommonData> getByOffsetOrderCount(long offset,
			int count, String orderBy, boolean desc, String dsname, String db,
			String table);

	/**
	 * 
	 * 获取指定id的Data
	 * 
	 * @param id
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public T getById(String id, String dsname, String db, String table);

	/**
	 * 
	 * 根据唯一键获取Data
	 * 
	 * @param keyName
	 * @param value
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public T getByUniqueKey(String keyName, String value, String dsname,
			String db, String table);

	/**
	 * 
	 * 删除指定id的Data
	 * 
	 * @param id
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public int removeById(String id, String dsname, String db, String table);

	/**
	 * 
	 * 根据唯一键删除Data
	 * 
	 * @param keyName
	 * @param value
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public int removeByUniqueKey(String keyName, String value, String dsname,
			String db, String table);

	/**
	 * 
	 * 根据指定SQL获取data
	 * 
	 * @param sql
	 * @param dsname
	 * @return
	 */
	public List<? extends CommonData> getBySQL(String sql, String dsname);

	/**
	 * 
	 * 执行指定的更新or删除SQL语句
	 * 
	 * @param sql
	 * @param dsname
	 * @return
	 */
	public int modifyBySQL(String sql, String dsname);

	/**
	 * 
	 * 判断指定id是否存在
	 * 
	 * @param id
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public boolean isExist(String id, String dsname, String db, String table);

	/**
	 * 判断指定唯一键值是否存在
	 * 
	 * @param keyName
	 * @param value
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	public boolean isUniqueKeyExist(String keyName, String value,
			String dsname, String db, String table);

}
