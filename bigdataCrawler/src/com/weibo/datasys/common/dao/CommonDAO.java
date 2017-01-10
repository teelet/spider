/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */

package com.weibo.datasys.common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.common.data.CommonData;
import com.weibo.datasys.common.db.DBManager;
import com.weibo.datasys.common.util.StringUtils;


public class CommonDAO implements IDataAccessObject<CommonData> {

	private static Logger logger = LoggerFactory.getLogger(CommonDAO.class);

	private static final String GET_COLUMN_INFO_SQL = "show columns from db.table";

	private static final Pattern NUM_TYPE_PATTERN = Pattern
			.compile(".*((bit)|(numbric)|(real)|(decimal)|(int)|(float)|(double)).*");

	private static final Pattern TIME_TYPE_PATTERN = Pattern
			.compile(".*((time)|(date)|(year)).*");

	private static CommonDAO instance;

	protected CommonDAO() {
	}

	public static CommonDAO getInstance() {
		if (instance == null) {
			instance = new CommonDAO();
		}
		return instance;
	}

	@Override
	public int save(CommonData data, String dsname, String db, String table,
			boolean updateIfDuplicate, boolean saveExtendColumn,
			boolean isAutoIncrement, boolean isUseDefaultId) {
		int saveCount = -1;
		if (data == null) {
			return saveCount;
		}
		// 获取table的column元信息，获取失败直接返回
		Map<String, String> columnInfos = getColumnInfo(dsname, db, table);
		if (columnInfos.size() == 0) {
			return saveCount;
		}
		for (Iterator<Map.Entry<String, String>> it = columnInfos.entrySet()
				.iterator(); it.hasNext();) {
			Map.Entry<String, String> entry = it.next();
			if (!"id".equalsIgnoreCase(entry.getKey())
					&& !"extend".equalsIgnoreCase(entry.getKey())
					&& data.getBaseField(entry.getKey()) == null
					&& data.getBlobField(entry.getKey()) == null) {
				it.remove();
			} else if ("extend".equalsIgnoreCase(entry.getKey())
					&& data.getExtendFieldNames().size() == 0) {
				it.remove();
			}
		}
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			boolean isBatch = false;
			String sql = buildInsertSQL(dsname, db, table, columnInfos,
					updateIfDuplicate, saveExtendColumn, isBatch,
					isAutoIncrement, isUseDefaultId);
			ps = conn.prepareStatement(sql);
			// set parameters
			setParameters(ps, data, columnInfos, isUseDefaultId,
					saveExtendColumn, isAutoIncrement);
			while (saveCount == -1) {
				try {
					saveCount = ps.executeUpdate();
				} catch (SQLIntegrityConstraintViolationException e) {
					saveCount = 3;
				} catch (Exception e) {
					logger.error(
							"[SaveError] - try again after 1s. id={} | e.msg={}",
							data.getId(), e.getMessage());
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			logger.error("[SaveError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return saveCount;
	}

	@Override
	public int save(CommonData data, String dsname, String db, String table,
			boolean updateIfDuplicate, boolean saveExtendColumn) {
		boolean isAutoIncrement = false;
		boolean isUseDefaultId = true;
		return save(data, dsname, db, table, updateIfDuplicate,
				saveExtendColumn, isAutoIncrement, isUseDefaultId);
	}

	@Override
	public int[] saveBatch(Collection<? extends CommonData> inDatas,
			String dsname, String db, String table, boolean updateIfDuplicate,
			boolean saveExtendColumn) {
		boolean isAutoIncrement = false;
		boolean isUseDefaultId = true;
		return saveBatch(inDatas, dsname, db, table, updateIfDuplicate,
				saveExtendColumn, isAutoIncrement, isUseDefaultId);
	}

	@Override
	public int[] saveBatch(Collection<? extends CommonData> inDatas,
			String dsname, String db, String table, boolean updateIfDuplicate,
			boolean saveExtendColumn, boolean isAutoIncrement,
			boolean isUseDefaultId) {
		int[] saveCounts = new int[4];
		// 输入为空直接返回
		if (inDatas == null || inDatas.size() == 0) {
			return saveCounts;
		}
		// 获取table的column元信息，获取失败直接返回
		Map<String, String> columnInfos = getColumnInfo(dsname, db, table);
		if (columnInfos.size() == 0) {
			return saveCounts;
		}

		CommonData firstData = inDatas.iterator().next();
		for (Iterator<Map.Entry<String, String>> it = columnInfos.entrySet()
				.iterator(); it.hasNext();) {
			Entry<String, String> entry = it.next();
			if (!"id".equalsIgnoreCase(entry.getKey())
					&& !"extend".equalsIgnoreCase(entry.getKey())
					&& firstData.getBaseField(entry.getKey()) == null
					&& firstData.getBlobField(entry.getKey()) == null) {
				it.remove();
			} else if ("extend".equalsIgnoreCase(entry.getKey())
					&& firstData.getExtendFieldNames().size() == 0) {
				it.remove();
			}
		}

		// 转换为list处理
		List<? extends CommonData> inputDatas = new ArrayList<CommonData>(
				inDatas);
		// 每次批量插入一定数量，数量太大DB会崩溃
		int batchCount = 100000;
		// 记录已处理数量
		int processCount = 0;
		// 处理数量不等于输入数量则循环执行
		while (processCount != inputDatas.size()) {
			// 截取输入数据的一段进行处理
			int processStart = processCount;
			int processEnd = Math.min(processCount + batchCount,
					inputDatas.size());
			List<? extends CommonData> datas = inputDatas.subList(processStart,
					processEnd);
			// 开始批量处理
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = DBManager.getConnection(dsname);
				// 关闭自动提交事务
				conn.setAutoCommit(false);
				String sql = null;
				// 创建批量插入
				for (CommonData data : datas) {
					// build sql
					if (sql == null) {
						boolean isBatch = true;
						sql = buildInsertSQL(dsname, db, table, columnInfos,
								updateIfDuplicate, saveExtendColumn, isBatch,
								isAutoIncrement, isUseDefaultId);
						ps = conn.prepareStatement(sql);
					}
					// set parameters
					setParameters(ps, data, columnInfos, isUseDefaultId,
							saveExtendColumn, isAutoIncrement);
					// add batch
					ps.addBatch();
				}
				// 执行批量插入
				int[] results = ps.executeBatch();
				// 提交事务
				conn.commit();
				// 统计结果
				for (int r : results) {
					saveCounts[r]++;
				}
			} catch (Exception e) {
				logger.error("[SaveBatchError] - ", e);
			} finally {
				DBManager.releaseConnection(conn, ps);
			}
			// 记录已处理数量
			processCount += datas.size();
		}// end of while
		return saveCounts;
	}

	@Override
	public List<? extends CommonData> getByCount(int count, String dsname,
			String db, String table) {
		return getByOffsetOrderCount(0, count, null, false, dsname, db, table);
	}

	@Override
	public List<? extends CommonData> getByOffsetOrderCount(long offset,
			int count, String orderBy, boolean desc, String dsname, String db,
			String table) {
		List<CommonData> datas = new ArrayList<CommonData>();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = buildSelectByOffsetOrderCountSQL(offset, count,
					orderBy, desc, dsname, db, table);
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				CommonData data = wrap(rs);
				datas.add(data);
			}
		} catch (Exception e) {
			logger.error("[GetError] - ", e);
			datas = null;
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return datas;
	}

	@Override
	public CommonData getById(String id, String dsname, String db, String table) {
		return getByUniqueKey("id", id, dsname, db, table);
	}

	@Override
	public CommonData getByUniqueKey(String keyName, String value,
			String dsname, String db, String table) {
		CommonData data = null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = buildSelectByUniqueKeySQL(keyName, dsname, db, table);
			ps = conn.prepareStatement(sql);
			ps.setObject(1, value);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				data = wrap(rs);
			}
		} catch (Exception e) {
			logger.error("[GetByUniqueKeyError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return data;
	}

	@Override
	public List<? extends CommonData> getBySQL(String sql, String dsname) {
		List<CommonData> datas = new ArrayList<CommonData>();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				CommonData data = wrap(rs);
				datas.add(data);
			}
		} catch (Exception e) {
			logger.error("[GetBySQLError] - e.msg={}", e.getMessage());
			datas = null;
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return datas;
	}

	@Override
	public int modifyBySQL(String sql, String dsname) {
		int count = -1;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			ps = conn.prepareStatement(sql);
			while (count == -1) {
				try {
					count = ps.executeUpdate();
				} catch (Exception e) {
					logger.error(
							"[ModifyBySQLError] - try again after 1s. sql={} | e.msg={}",
							sql, e.getMessage());
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			logger.error("[ModifyBySQLError] - e.msg={}", e.getMessage());
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return count;
	}

	@Override
	public int removeById(String id, String dsname, String db, String table) {
		return removeByUniqueKey("id", id, dsname, db, table);
	}

	@Override
	public int removeByUniqueKey(String keyName, String value, String dsname,
			String db, String table) {
		int count = -1;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			String sql = buildRemoveByUniqueKeySQL(keyName, dsname, db, table);
			ps = conn.prepareStatement(sql);
			ps.setObject(1, value);
			count = ps.executeUpdate();
		} catch (Exception e) {
			logger.error("[RemoveByUniqueKeyError] - {}={}", keyName, value);
			logger.error("[RemoveByUniqueKeyError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return count;
	}

	@Override
	public boolean isExist(String id, String dsname, String db, String table) {
		return isUniqueKeyExist("id", id, dsname, db, table);
	}

	@Override
	public boolean isUniqueKeyExist(String keyName, String value,
			String dsname, String db, String table) {
		boolean isExist = false;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("select ").append(keyName).append(" from ")
					.append(db).append(".").append(table).append(" where ")
					.append(keyName).append(" =?");
			String sql = sqlBuilder.toString();
			ps = conn.prepareStatement(sql);
			ps.setObject(1, value);
			ResultSet rs = ps.executeQuery();
			isExist = rs.next();
		} catch (Exception e) {
			logger.error("[IsUniqueKeyExistError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return isExist;
	}

	/**
	 * 
	 * 将数据库返回结果包装成CommonData对象
	 * 
	 * @param rs
	 * 
	 * @return
	 * @throws Exception
	 */
	public CommonData wrap(ResultSet rs) throws Exception {
		CommonData data = new CommonData();
		int columnCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = rs.getMetaData().getColumnName(i);
			String columnType = rs.getMetaData().getColumnTypeName(i)
					.toLowerCase();
			if (columnName.equalsIgnoreCase("id")) {
				data.setId(rs.getString(i));
				data.setBaseField("id", rs.getString(i));
			} else if (columnName.equalsIgnoreCase("extend")) {
				data.setExtendMap(rs.getString(i));
			} else if (columnName.equalsIgnoreCase("base")) {
				continue;
			} else {
				Object fieldValueObject = null;
				if (TIME_TYPE_PATTERN.matcher(columnType).matches()) {
					byte[] valueBytes = rs.getBytes(i);
					String valueString = "";
					if (valueBytes != null) {
						valueString = new String(valueBytes);
					}
					fieldValueObject = valueString;
				} else {
					fieldValueObject = rs.getObject(i);
				}
				if (fieldValueObject instanceof byte[]) {
					data.setBlobField(columnName, (byte[]) fieldValueObject);
				} else {
					data.setBaseField(columnName, fieldValueObject);
				}
			}
		}
		if (StringUtils.isEmptyString(data.getId()) && columnCount > 0) {
			data.setId(rs.getString(1));
		}
		return data;
	}

	/**
	 * 填充sql语句参数
	 * 
	 * @param ps
	 * @param data
	 * @param columnInfos
	 * @param isUseDefaultId
	 * @param saveExtendColumn
	 * @throws SQLException
	 * @author zouyandi
	 */
	protected void setParameters(PreparedStatement ps, CommonData data,
			Map<String, String> columnInfos, boolean isUseDefaultId,
			boolean saveExtendColumn, boolean isAutoIncrement)
			throws SQLException {
		List<String> baseFieldNames = new ArrayList<String>(
				columnInfos.keySet());
		baseFieldNames.remove("extend");
		baseFieldNames.remove("id");
		int pIndex = 1;
		if (!isAutoIncrement && isUseDefaultId) {
			// 不自增长id且使用默认id列则设置id为参数1
			ps.setObject(pIndex, data.getId());
			pIndex++;
		}
		// 依次使用BaseField填充参数
		for (String fieldName : baseFieldNames) {
			String fieldType = columnInfos.get(fieldName);
			Object fieldValue = null;
			if (fieldType.contains("blob")) {
				fieldValue = data.getBlobField(fieldName);
			} else {
				fieldValue = data.getBaseField(fieldName);
			}
			if (fieldValue == null) {
				fieldValue = "";
			}
			// 列类型为数值型，转换为double填充
			if (NUM_TYPE_PATTERN.matcher(fieldType).matches()) {
				fieldValue = StringUtils.parseDouble(fieldValue.toString(), 0);
			}
			ps.setObject(pIndex, fieldValue);
			pIndex++;
		}
		if (saveExtendColumn && columnInfos.containsKey("extend")) {
			ps.setObject(pIndex, data.getExtendString());
		}
	}

	/**
	 * 获取指定表的列信息
	 * 
	 * @param dsname
	 * @param db
	 * @param table
	 * @return key=列名；value=列数据类型
	 * @author zouyandi
	 */
	protected Map<String, String> getColumnInfo(String dsname, String db,
			String table) {
		Map<String, String> columnInfos = new LinkedHashMap<String, String>();
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBManager.getConnection(dsname);
			ps = conn.prepareStatement(GET_COLUMN_INFO_SQL.replace("db", db)
					.replace("table", table));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String fieldName = rs.getString(1).toLowerCase();
				String fieldType = rs.getString(2).toLowerCase();
				columnInfos.put(fieldName, fieldType);
			}
		} catch (Exception e) {
			logger.error("[GetColumnInfoError] - ", e);
		} finally {
			DBManager.releaseConnection(conn, ps);
		}
		return columnInfos;
	}

	/**
	 * 
	 * 构造select by unique key语句
	 * 
	 * @param keyName
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	protected static String buildSelectByUniqueKeySQL(String keyName,
			String dsname, String db, String table) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select * from ").append(db).append(".")
				.append(table).append(" where ").append(keyName).append("=?");
		return sqlBuilder.toString();
	}

	/**
	 * 
	 * 构造remove by unique key语句
	 * 
	 * @param keyName
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	protected static String buildRemoveByUniqueKeySQL(String keyName,
			String dsname, String db, String table) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("delete from ").append(db).append(".").append(table)
				.append(" where ").append(keyName).append("=?");
		return sqlBuilder.toString();
	}

	/**
	 * 
	 * 构造select语句
	 * 
	 * @param offset
	 * 
	 * @param count
	 * @param orderBy
	 * @param desc
	 * @param dsname
	 * @param db
	 * @param table
	 * @return
	 */
	protected static String buildSelectByOffsetOrderCountSQL(long offset,
			int count, String orderBy, boolean desc, String dsname, String db,
			String table) {
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("select * from ").append(db).append(".")
				.append(table);
		if (orderBy != null && !orderBy.equals("")) {
			sqlBuilder.append(" order by ").append(orderBy);
			if (desc) {
				sqlBuilder.append(" desc ");
			}
		}
		if (count > 0) {
			sqlBuilder.append(" limit ").append(count);
		}
		if (offset > 0) {
			sqlBuilder.append(" offset ").append(offset);
		}
		return sqlBuilder.toString();
	}

	/**
	 * 
	 * 构造插入数据SQL
	 * 
	 * @param dsname
	 * @param db
	 * @param table
	 * @param columnInfos
	 * @param updateIfDuplicate
	 * @param saveExtendColumn
	 * @param isBatch
	 * @param isAutoIncrement
	 * @param isUseDefaultId
	 * 
	 * @return
	 */
	protected static String buildInsertSQL(String dsname, String db,
			String table, Map<String, String> columnInfos,
			boolean updateIfDuplicate, boolean saveExtendColumn,
			boolean isBatch, boolean isAutoIncrement, boolean isUseDefaultId) {
		List<String> baseFieldNames = new ArrayList<String>(
				columnInfos.keySet());
		if (!isUseDefaultId) {
			baseFieldNames.remove("id");
		}
		if (!saveExtendColumn) {
			baseFieldNames.remove("extend");
		}
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("insert ").append(db).append(".").append(table)
				.append("(");
		for (String field : baseFieldNames) {
			sqlBuilder.append(field).append(',');
		}
		sqlBuilder.setCharAt(sqlBuilder.length() - 1, ')');
		baseFieldNames.remove("id");
		baseFieldNames.remove("extend");
		sqlBuilder.append(" values(");
		if (isAutoIncrement) {
			// for auto increment id
			sqlBuilder.append("0");
		} else if (isUseDefaultId) {
			// for CommonData's id
			sqlBuilder.append("?");
		}
		if (baseFieldNames != null && baseFieldNames.size() > 0) {
			for (int i = 0; i < baseFieldNames.size(); i++) {
				if (i != 0 || isAutoIncrement || isUseDefaultId) {
					sqlBuilder.append(",");
				}
				sqlBuilder.append("?");
			}
		}
		if (saveExtendColumn && columnInfos.containsKey("extend")) {
			sqlBuilder.append(",?");
		}
		sqlBuilder.append(") ");
		if (updateIfDuplicate || isBatch) {
			sqlBuilder.append(" on DUPLICATE KEY UPDATE ");
			if (isUseDefaultId) {
				sqlBuilder.append("id=id ");
			} else {
				String fieldName = baseFieldNames.get(0);
				sqlBuilder.append(fieldName).append("=").append(fieldName);
			}
		}
		if (updateIfDuplicate) {
			if (baseFieldNames != null && baseFieldNames.size() > 0) {
				sqlBuilder.append(",");
				int fieldStartIndex = 0;
				if (!isUseDefaultId) {
					fieldStartIndex++;
				}
				for (int i = fieldStartIndex; i < baseFieldNames.size(); i++) {
					String fieldName = baseFieldNames.get(i);
					sqlBuilder.append(fieldName).append("=values(")
							.append(fieldName).append("),");
				}
			}
			if (saveExtendColumn && columnInfos.containsKey("extend")) {
				sqlBuilder.append("extend=values(extend)");
			} else {
				sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
			}
		}
		return sqlBuilder.toString();
	}

}
