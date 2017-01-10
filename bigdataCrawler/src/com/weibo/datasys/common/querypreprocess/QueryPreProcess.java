/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.querypreprocess;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * query预处理类
 *
 */
public class QueryPreProcess {
	// 日志
	protected final static Logger logger = LoggerFactory.getLogger(QueryPreProcess.class);
	// 本类实例
	private static volatile QueryPreProcess instance = null;
	// 繁体字对应简体字的哈希表
	private static Map<String, String> traditionToSimple = new HashMap<String, String>();
	private static Map<String, String> _traditionToSimple = null;
	// 符号
	public static final String BLANK = " ";
	public static final String TAB = "\t";

	/**
	 * 构造函数
	 */
	private QueryPreProcess() {
		try {
			InputStream in = this.getClass().getClassLoader().getResource("sttransfer.txt").openStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			_traditionToSimple = new HashMap<String, String>();
			String record = null;
			String[] tmp;
			int line = 0;
			while ((record = br.readLine()) != null) {
				if (record.startsWith("#") || record.trim().equals(""))
					continue;
				// 分隔字段
				tmp = record.split(TAB);
				if (tmp.length < 2) {
					logger.error("Invalid record from sttransfer.txt : " + record);
					continue;
				}
				String tradition = tmp[0].trim();
				String simple = tmp[1].trim();
				if (!_traditionToSimple.containsKey(tradition))
					_traditionToSimple.put(tradition, simple);
				line++;
			}
			// 替换词典，并清除原有词典，便于垃圾回收
			if (_traditionToSimple.size() > 0) {
				traditionToSimple = _traditionToSimple;
			}
			br.close();
			// 记录日志
			if (_traditionToSimple.size() < 1)// //0条记录
				logger.info("There is no record in sttransfer.txt");
			else
				logger.info("There are " + line + " records in sttransfer.txt");// //多条记录
		} catch (Exception e) {
			logger.error("Read sttransfer.txt failed", e);
		}
	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static QueryPreProcess getInstance() {
		if (null == instance) {
			synchronized (QueryPreProcess.class) {
				if (null == instance)
					instance = new QueryPreProcess();
			}
		}
		return instance;
	}

	/**
	 * 转换至UTF-8编码
	 * 
	 * @return
	 */
	private String convertEncoding(String query) {

		CharsetEncoder encoder = java.nio.charset.Charset.forName("UTF-8").newEncoder();
		// 不能转换成UTF-8编码的返回空字符串，能转换的进行转换
		if (!java.nio.charset.Charset.forName("UTF-8").newEncoder().canEncode(query)) {
			query = "";
		} else {
			try {
				// 转换为UTF-8编码
				CharBuffer charBuffer = CharBuffer.wrap(query.toCharArray());
				ByteBuffer byteBuffer = encoder.encode(charBuffer);
				byte[] array = new byte[byteBuffer.limit()];
				for (int i = 0; i < byteBuffer.limit(); i++) {
					array[i] = byteBuffer.get();
				}
				query = new String(array, "UTF-8");
			} catch (Exception e) {
				logger.error("Convert to UTF-8 failed, query is:" + query, e);
				query = "";
			}
		}
		return query;
	}

	/**
	 * 全角转换为半角
	 * 
	 * @return
	 */
	private String convertSBCCase2DBCCase(String query) {
		char[] ch = query.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == 12288) {
				ch[i] = (char) 32;
				continue;
			}
			if (ch[i] > 65280 && ch[i] < 65375)
				ch[i] = (char) (ch[i] - 65248);
		}
		return new String(ch);
	}

	/**
	 * 将句中多个空格变为一个空格，并去掉句子两端的空格
	 * 
	 * @param query
	 * @return
	 */
	private String removeNeedlessBlanks(String query) {
		query = query.trim();
		String regExBlank = " +"; // 表示一个或多个空格
		Pattern pat = Pattern.compile(regExBlank);
		Matcher matBlank = pat.matcher(query);
		query = matBlank.replaceAll(BLANK);
		return query;
	}

	/**
	 * 将繁体转换为简体
	 * 
	 * @param query
	 * @return
	 */
	private String convertTraditional2Simplified(String query) {
		StringBuilder stringbuilder = new StringBuilder();
		for (int i = 0; i < query.length(); i++) {
			if (traditionToSimple.containsKey(Character.toString(query.charAt(i)))) {
				stringbuilder.append(traditionToSimple.get(Character.toString(query.charAt(i))));
			} else {
				stringbuilder.append(query.charAt(i));
			}
		}
		return stringbuilder.toString();
	}

	public String processQuery(String query) {
		query = convertEncoding(query);// 转换至UTF-8编码
		query = convertSBCCase2DBCCase(query);// 全角转换为半角
		query = removeNeedlessBlanks(query);// 将句中多个空格变为一个空格，并去掉句子两端的空格
		query = query.toLowerCase();// 将大写字母变成小写字母
		query = convertTraditional2Simplified(query);// 将繁体转换为简体
		return query;
	}

	public static void main(String[] args) {
		QueryPreProcess qpp = QueryPreProcess.getInstance();

		System.out.println("１２３ａｄｆＤＥＦ，：；！？（）");
		System.out.println(qpp.convertSBCCase2DBCCase("１２３ａｄｆＤＥＦ，：；！？（）"));
		System.out.println("‘’“”。《》￥……——【】");
		System.out.println(qpp.convertSBCCase2DBCCase("‘’“”。《》￥……——【】"));

		System.out.println("   多余 空    格 ");
		System.out.println(qpp.removeNeedlessBlanks("   多余 空    格 "));

		System.out.println("ABC应该是abc");
		System.out.println("ABC应该是abc".toLowerCase());

		System.out.println("萬與醜專業叢東絲丟兩|廠廣闢彆蔔瀋衝種蟲");
		System.out.println(qpp.convertTraditional2Simplified("萬與醜專業叢東絲丟兩|廠廣闢彆蔔瀋衝種蟲"));

		System.out.println("１２３ａｄｆＤＥＦ，：；！？（）     萬與醜專業叢東絲    丟兩    ");
		System.out.println(qpp.processQuery("１２３ａｄｆＤＥＦ，：；！？（）     萬與醜專業叢東絲    丟兩    "));
	}

}
