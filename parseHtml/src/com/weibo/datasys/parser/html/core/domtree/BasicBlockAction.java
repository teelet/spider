/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.domtree;

import com.weibo.datasys.parser.html.data.BlockInfo;

public abstract class BasicBlockAction
{
	/**
	 * 当前节点操作类
	 * @return false 表示跳过该节点，true 继续处理
	 * @throws Exception 
	 */
	public abstract boolean action(BlockInfo currentBlock) throws Exception ;

}
