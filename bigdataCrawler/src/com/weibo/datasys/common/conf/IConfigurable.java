/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.common.conf;

import java.util.Map;

/**
 * 
 * 可配置接口
 * 
 */
public interface IConfigurable {

	/**
	 * 根据参数Map进行配置
	 * 
	 * @param paraMap
	 * @throws Exception 
	 */
	public void configWithKeyValues(Map<String, String> paraMap) throws Exception;

}
