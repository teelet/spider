/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.thread;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.weibo.datasys.parser.html.data.ParseData;
import com.weibo.datasys.parser.main.ThreadParser;
import com.weibo.datasys.parser.sql.Writer;

public class ThreadWriter extends Thread
{
	private static final Logger LOG = LoggerFactory.getLogger(ThreadWriter.class);

	public void run()
	{
		Writer write = ThreadParser.factory.getWriter();
		write.init();
		int failTimes = 0;
		while (true)
		{
			try
			{
				ParseData parseData = ThreadParser.writeQueue.poll(1, TimeUnit.MINUTES);
				if (null != parseData)
				{
					failTimes = 0;
					write.write(parseData);
					ThreadParser.updateQueue.put(parseData);
				} else if (0 == ThreadManager.parseType && 0 == ThreadParser.writeQueue.size())
				{
					failTimes++;
					if (5 == failTimes)
					{
						LOG.info("{}线程退出。", this.getName());
						break;
					}
					Thread.sleep(60 * 1000);
				}
				Thread.sleep(1);
			} catch (Exception e)
			{
				LOG.error("", e);
			}
		}
		write.close();
		synchronized (this)
		{
			ThreadManager.writeThreadNum--;
			if (!ThreadManager.alive())
			{
				LOG.info("程序运行结束！");
			}
		}
	}
}
