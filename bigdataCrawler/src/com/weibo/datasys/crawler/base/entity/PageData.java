/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.base.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 
 * 原始网页数据
 * 
 * @author zouyandi
 * 
 */
public class PageData implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 归一化url的MD5
	 */
	private String id;
	
	/**
	 * 任务id
	 */
	private String taskid;

	/**
	 * 原始url
	 */
	private String url;

	/**
	 * 归一化url
	 */
	private String normalizeUrl;

	/**
	 * 原始网页
	 */
	private byte[] html;

	/**
	 * 压缩后的原始网页
	 */
	private byte[] zipHtml;

	/**
	 * 抓取时间
	 */
	private long fetchtime;

	/**
	 * 解析状态
	 */
	private int parsestate;

	private Map<String, String> extend = new LinkedHashMap<String, String>();

	/**
	 * 
	 * 设置扩展字段
	 * 
	 * @param key
	 * @param value
	 */
	public void setExtendField(String key, String value) {
		key = key.replaceAll(",", "，");
		key = key.replaceAll("=", "＝");
		value = value.replaceAll(",", "，");
		value = value.replaceAll("=", "＝");
		extend.put(key, value);
	}

	/**
	 * 
	 * 用格式化字符串设置扩展Map
	 * 
	 * @param extendString
	 */
	public void setExtendMap(String extendString) {
		extendString = extendString.replaceAll("[\\{\\}]", "");
		extendString = extendString.replace("＝", "=");
		if (extendString.trim().equals("")) {
			return;
		}
		String[] maps = extendString.split(",");
		for (String map : maps) {
			String[] cutMaps = map.split(";");
			if (2 == cutMaps.length) {
				for (String cutMap : cutMaps) {
					String[] split = cutMap.split("=");
					if (split.length == 2) {
						setExtendField(split[0].trim(), split[1].trim());
					} else if (cutMap.matches("[^=]+=")) {
						setExtendField(split[0].trim(), "");
					}
				}
			} else {
				String[] split = map.split("=");
				if (split.length == 2) {
					setExtendField(split[0].trim(), split[1].trim());
				} else if (map.matches("[^=]+=")) {
					setExtendField(split[0].trim(), "");
				}
			}
		}
	}

	/**
	 * 
	 * 获取扩展字段
	 * 
	 * @param key
	 * @return
	 */
	public String getExtendField(String key) {
		return extend.get(key);
	}

	/**
	 * @return 格式化的扩展字段字符串
	 */
	public String getFormatExtendString() {
		return extend.toString();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	public String getTaskId(){
		return taskid;
	}
	
	public void setTaskId(String taskid){
		this.taskid = taskid;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the normalizeUrl
	 */
	public String getNormalizeUrl() {
		return normalizeUrl;
	}

	/**
	 * @param normalizeUrl
	 *            the normalizeUrl to set
	 */
	public void setNormalizeUrl(String normalizeUrl) {
		this.normalizeUrl = normalizeUrl;
	}

	/**
	 * @return the html
	 */
	public byte[] getHtml() {
		return html;
	}

	/**
	 * @param html
	 *            the html to set
	 */
	public void setHtml(byte[] html) {
		this.html = html;
	}

	/**
	 * @return the fetchtime
	 */
	public long getFetchtime() {
		return fetchtime;
	}

	/**
	 * @param fetchtime
	 *            the fetchtime to set
	 */
	public void setFetchtime(long fetchtime) {
		this.fetchtime = fetchtime;
	}

	/**
	 * @return the parsestate
	 */
	public int getParsestate() {
		return parsestate;
	}

	/**
	 * @param parsestate
	 *            the parsestate to set
	 */
	public void setParsestate(int parsestate) {
		this.parsestate = parsestate;
	}

	/**
	 * 
	 * 设置压缩后的html
	 * 
	 * @param zipHtml
	 */
	public void setZipHtml(byte[] zipHtml) {
		this.zipHtml = zipHtml;
	}

	/**
	 * @return 压缩后的html
	 */
	public byte[] getZipHtml() {
		return zipHtml;
	}

	/**
	 * 压缩原html
	 */
	public void zipHtml() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gos = new GZIPOutputStream(bos);
			gos.write(html);
			gos.close();
			zipHtml = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压缩html
	 */
	public void unZipHtml() {
		try {
			byte[] buf = new byte[10240];
			int readcount = 0;
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(
					zipHtml));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while (-1 != (readcount = gis.read(buf))) {
				bos.write(buf, 0, readcount);
			}
			html = bos.toByteArray();
			gis.close();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 设置压缩后html，并进行解压
	 * 
	 * @param zipHtml
	 */
	public void setAllHtml(byte[] zipHtml) {
		this.zipHtml = zipHtml;
		unZipHtml();
	}

}
