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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * LRU缓存工具类
 * 
 */
public final class LRUCacheUtil {

	private LRUCacheUtil() {
	}

	/**
	 * 
	 * 获取指定大小的，同步的，LRU缓存Map
	 * 
	 * @param <K>
	 * @param <V>
	 * @param capacity
	 * @return
	 */
	public static <K, V> Map<K, V> getLRUCache(final int capacity) {
		Map<K, V> LRUMap = Collections.synchronizedMap(new LinkedHashMap<K, V>(
				capacity, 0.75F, true) {
			private static final long serialVersionUID = 352094108437285631L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > capacity;
			}
		});
		return LRUMap;
	}

}
