/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 */
public class MD5Util {

	private MD5Util() {
	}

	/**
	 * 
	 * 二行制字节数组转十六进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		if (b == null) {
			return "";
		}
		StringBuffer hs = new StringBuffer();
		String stmp = null;
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1) {
				hs.append("0");
			}
			hs.append(stmp);
		}
		return hs.toString();
	}

	/**
	 * 
	 * 获取源字符串MD5摘要，返回十六进制字符串形式
	 * 
	 * @param src
	 * @return
	 */
	public static String MD5(String src) {
		if (src == null) {
			return "";
		}
		return MD5(src.getBytes());
	}

	/**
	 * 
	 * 获取源字节数组MD5摘要，返回十六进制字符串形式
	 * 
	 * @param src
	 * @return
	 */
	public static String MD5(byte[] src) {
		if (src == null) {
			return "";
		}
		byte[] result = null;
		try {
			MessageDigest alg = MessageDigest.getInstance("MD5");
			result = alg.digest(src);
		} catch (NoSuchAlgorithmException e) {

		}
		return byte2hex(result);
	}

	/**
	 * 
	 * 获取源字节数组MD5摘要，返回字节数组形式
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] MD5Bytes(byte[] src) {
		byte[] result = null;
		if (src != null) {
			try {
				MessageDigest alg = MessageDigest.getInstance("MD5");
				result = alg.digest(src);
			} catch (NoSuchAlgorithmException e) {

			}
		}
		return result;
	}
}
