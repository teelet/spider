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

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class DnsKey extends SelectionKey {

	private boolean isValid = true;

	@Override
	public void cancel() {
		this.isValid = false;
	}

	@Override
	public SelectableChannel channel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int interestOps() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public SelectionKey interestOps(int ops) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return this.isValid;
	}

	@Override
	public int readyOps() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Selector selector() {
		// TODO Auto-generated method stub
		return null;
	}
}
