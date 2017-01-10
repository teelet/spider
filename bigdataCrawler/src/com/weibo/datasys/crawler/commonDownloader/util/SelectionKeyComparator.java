/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.commonDownloader.util;

import java.nio.channels.SelectionKey;
import java.util.Comparator;

public class SelectionKeyComparator implements Comparator<SelectionKey> {

	@Override
	public int compare(SelectionKey k1, SelectionKey k2) {
		int reslut = 0;
		if (!k1.equals(k2)) {
			reslut = k1.hashCode() > k2.hashCode() ? 1 : -1;
		}
		return reslut;
	}

}
