/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 
 *通用数据实体类，具有可自定义的基础属性和扩展属性
 *
 **/
public class CommonData implements Serializable {

	private static final long serialVersionUID = 1L;

	protected LinkedHashMap<String, String> baseFieldsMap = new LinkedHashMap<String, String>();

	protected LinkedHashMap<String, String> extendFieldsMap = new LinkedHashMap<String, String>();

	protected LinkedHashMap<String, byte[]> blobFieldsMap = new LinkedHashMap<String, byte[]>();
	
	protected LinkedHashMap<String, CommonData> commonFieldsMap = new LinkedHashMap<String, CommonData>();

	protected String id = "";

	public CommonData() {
	}

	/**
	 * 
	 * 用于保证基础属性按照参数的顺序排列
	 * 
	 * @param initBaseFieldNames
	 */
	public CommonData(String... initBaseFieldNames) {
		for (String fieldName : initBaseFieldNames) {
			setBaseField(fieldName, null);
		}
	}

	/**
	 * @return the id，用于唯一标识该CommonData
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * 设置id，用于唯一标识该CommonData
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.setBaseField("id", id);
		this.id = id;
	}

	/**
	 * 
	 * 设置基础属性
	 * 
	 * @param fieldName
	 * @param value
	 */
	public void setBaseField(String fieldName, Object value) {
		if (value != null) {
			String valueString = String.valueOf(value);
			valueString = valueString.replaceAll(",", "，");
			valueString = valueString.replaceAll("=", "＝");
			this.baseFieldsMap.put(fieldName.toLowerCase(), valueString);
		} else {
			this.baseFieldsMap.put(fieldName.toLowerCase(), null);
		}
	}

	/**
	 * 
	 * 获取基础属性
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getBaseField(String fieldName) {
		String valueString = baseFieldsMap.get(fieldName.toLowerCase());
		if (valueString != null) {
			valueString = valueString.replaceAll("，", ",");
			valueString = valueString.replaceAll("＝", "=");
		}
		return valueString;
	}

	/**
	 * @return 格式化的基础属性字符串
	 */
	public String getBaseString() {
		return baseFieldsMap.toString();
	}

	/**
	 * 
	 * 用给定的格式化基础属性字符串填充基础属性Map
	 * 
	 * @param baseString
	 * @throws InvalidFomatException
	 */
	public void setBaseMap(String baseString) throws InvalidFomatException {
		baseString = baseString.replaceAll("[\\{\\}]", "");
		if (baseString.trim().equals("")) {
			return;
		}
		String[] maps = baseString.split(",");
		for (String map : maps) {
			String[] split = map.split("=", 2);
			if (split.length == 2) {
				setBaseField(split[0].trim(), split[1].trim());
			} else if (map.matches("[^=]+=")) {
				setBaseField(split[0].trim(), "");
			} else {
				throw new InvalidFomatException(baseString);
			}
		}
	}

	/**
	 * @return 基础属性名称集合
	 */
	public List<String> getBaseFieldNames() {
		List<String> fieldNames = new ArrayList<String>(baseFieldsMap.keySet());
		return fieldNames;
	}

	/**
	 * 
	 * 设置扩展属性
	 * 
	 * @param fieldName
	 * @param value
	 */
	public void setExtendField(String fieldName, Object value) {
		String valueString = String.valueOf(value);
		valueString = valueString.replaceAll(",", "，");
		valueString = valueString.replaceAll("=", "＝");
		this.extendFieldsMap.put(fieldName.toLowerCase(), valueString);
	}

	/**
	 * 
	 * 获取扩展属性
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getExtendField(String fieldName) {
		String valueString = extendFieldsMap.get(fieldName.toLowerCase());
		if (valueString != null) {
			valueString = valueString.replaceAll("，", ",");
			valueString = valueString.replaceAll("＝", "=");
		}
		return valueString;
	}

	/**
	 * @return 格式化的扩展属性字符串
	 */
	public String getExtendString() {
		return extendFieldsMap.toString();
	}

	/**
	 * 
	 * 用给定的格式化扩展属性字符串填充扩展属性Map
	 * 
	 * @param extendString
	 * @throws InvalidFomatException
	 */
	public void setExtendMap(String extendString) throws InvalidFomatException {
		extendString = extendString.replaceAll("[\\{\\}]", "");
		if (extendString.trim().equals("")) {
			return;
		}
		String[] maps = extendString.split(",");
		for (String map : maps) {
			String[] split = map.split("=", 2);
			if (split.length == 2) {
				setExtendField(split[0].trim(), split[1].trim());
			} else if (map.matches("[^=]+=")) {
				setExtendField(split[0].trim(), "");
			} else {
				throw new InvalidFomatException(extendString);
			}
		}
	}

	/**
	 * @return 扩展属性名称集合
	 */
	public List<String> getExtendFieldNames() {
		List<String> fieldNames = new ArrayList<String>(
				extendFieldsMap.keySet());
		return fieldNames;
	}

	/**
	 * 设置blob属性
	 * 
	 * @param fieldName
	 *            fieldName
	 * @param value
	 *            value
	 */
	public void setBlobField(String fieldName, byte[] value) {
		this.blobFieldsMap.put(fieldName.toLowerCase(), value);
	}

	/**
	 * 获取blob类型属性值
	 * 
	 * @param fieldName
	 *            fieldName
	 * @return
	 */
	public byte[] getBlobField(String fieldName) {
		return blobFieldsMap.get(fieldName.toLowerCase());
	}

	/**
	 * @return blob属性名称集合
	 */
	public List<String> getBlobFieldNames() {
		List<String> fieldNames = new ArrayList<String>(blobFieldsMap.keySet());
		return fieldNames;
	}
	
	/**
	 * 设置common属性
	 * 
	 * @param fieldName
	 *            fieldName
	 * @param value
	 *            value
	 */
	public void setCommonField(String fieldName, CommonData value) {
		this.commonFieldsMap.put(fieldName.toLowerCase(), value);
	}
	
	/**
	 * 获取Common属性
	 * 
	 * @param fieldName
	 * @return
	 */
	public CommonData getCommonField(String fieldName) {
		return commonFieldsMap.get(fieldName);
	}

	/**
	 * @return Common属性名称list
	 */
	public List<String> getCommonFieldNames() {
		List<String> fieldNames = new ArrayList<String>(
				commonFieldsMap.keySet());
		return fieldNames;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[CommonDataInfo] - [");
		sb.append("id=").append(id).append("\n");
		sb.append("baseMap= ").append(baseFieldsMap).append("\n");
		sb.append("extendMap= ").append(extendFieldsMap).append("\n");
		sb.append("blobMap= ").append(blobFieldsMap).append("\n");
		sb.append("commonMap= ").append(commonFieldsMap);
		sb.append("]\n");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		return ((CommonData) o).id.equals(this.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}
