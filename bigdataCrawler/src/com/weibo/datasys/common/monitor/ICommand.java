/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.monitor;

import com.sun.net.httpserver.HttpExchange;

/**
 * HttpMonitorServer用于处理http请求的接口
 */

public interface ICommand {

	/**
	 * 
	 * 执行http命令，http应答的产生与发送应在命令执行过程中通过HttpExchange类提供的方法完成，
	 * 本方法的返回值只是命令执行成功或失败的信息
	 * 
	 * @param httpExchange
	 * @return 命令执行的结果
	 */
	String excute(HttpExchange httpExchange);

}
