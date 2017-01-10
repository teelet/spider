/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.unittest;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.weibo.datasys.common.querypreprocess.QueryPreProcess;

public class QueryPreProcessTest extends TestCase {

	public QueryPreProcessTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// 测试私有方法时通过java反射机制绕过权限
	public void privateMethodTest(String methodName, String input, String expectedOutput) {
		QueryPreProcess qpp = QueryPreProcess.getInstance();
		Class<? extends QueryPreProcess> c = qpp.getClass();
		try {
			Method method = c.getDeclaredMethod(methodName, new Class[] { String.class });
			method.setAccessible(true); // 设为true可以无视java的封装
			Object result = method.invoke(qpp, new Object[] { input });
			System.out.print(result);
			Assert.assertEquals(expectedOutput, result);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Test
	public void testConvertSBCCase2DBCCase() {
		privateMethodTest("convertSBCCase2DBCCase", "１２３ａｄｆＤＥＦ，：；！？（）｜|‘’“”。《》￥……——【】",
				"123adfDEF,:;!?()||‘’“”。《》￥……——【】");
	}

	@Test
	public void testRemoveNeedlessBlanks() {
		privateMethodTest("removeNeedlessBlanks", "   多余 空    格 ", "多余 空 格");
	}

	@Test
	public void testConvertTraditional2Simplified() {
		privateMethodTest("convertTraditional2Simplified", "萬與醜專業叢東絲丟兩|廠廣闢彆蔔瀋衝種蟲", "万与丑专业丛东丝丢两|厂广闢别蔔渖冲种虫");
	}

	@Test
	public void testProcessQuery() {
		QueryPreProcess qpp = QueryPreProcess.getInstance();
		String result = qpp.processQuery("１２３ａｄｆＤＥＦ，：；！？（）     萬與醜專業叢東絲    丟兩    ");
		System.out.print(result);
		Assert.assertEquals("123adfdef,:;!?() 万与丑专业丛东丝 丢两", result);
	}
}
