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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * IO工具类
 *
 */
public final class IOUtil {

	private IOUtil() {
	}

	public static BufferedReader getBufferedReader(String file) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
		}
		return reader;
	}

	public static BufferedWriter getBufferedWriter(String file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (Exception e) {
		}
		return writer;
	}

	public static void closeIO(Closeable... closeables) {
		for (Closeable closeable : closeables) {
			if (closeable != null) {
				try {
					closeable.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * 根据md5值生成对应的文件路径，subDirCount*subDirLength<=32
	 * 
	 * @param md5
	 * @param subDirCount
	 *            文件路径层数，1~32
	 * @param subDirLength
	 *            文件夹名称长度，1~32
	 * @return
	 */
	public static String md5ToPath(String md5, int subDirCount, int subDirLength) {
		String path = "";
		if (StringUtils.isNotEmpty(md5) && subDirCount * subDirLength <= 32) {
			List<String> subDirs = new ArrayList<String>();
			for (int i = 0; i < subDirCount; i++) {
				int start = i * subDirLength;
				int end = i * subDirLength + subDirLength;
				String subDir = md5.substring(start, end);
				subDirs.add(subDir);
			}
			String lastDir = md5.substring(subDirs.size() * subDirLength);
			for (String subDir : subDirs) {
				path += subDir + "/";
			}
			path += lastDir;
		}
		return path;
	}
}
