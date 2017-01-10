/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.crawler.impl.crawlUnit.fetcher.wxcs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.weibo.datasys.crawler.commonDownloader.entity.DownRequest;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.crawler.base.entity.SeedData;
import com.weibo.datasys.crawler.impl.crawlUnit.fetcher.DefaultFetcher;

/**
 * 无线城市Fetcher实现类
 * 
 * 
 */
public class WXCSFetcher extends DefaultFetcher {

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	@Override
	protected void configPostReq(SeedData seedData, DownRequest req) {
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("utf-8");
		// 根元素
		Element root = doc.addElement("root");
		// transactionid
		root.addElement("transactionid").addText("");
		// timestamp
		root.addElement("timestamp").addText(sdf.format(new Date()));
		// msgname
		String msgname = seedData.getExtendField(POST_FIELD_PREFIX + "msgname");
		msgname = StringUtils.nullToEmpty(msgname);
		root.addElement("msgname").addText(msgname);
		// result
		root.addElement("result").addText("");
		// resultdesc
		root.addElement("resultdesc").addText("");
		// body
		Element body = root.addElement("body");
		// dircode 目录编码, 根目录使用root表示
		String dircode = seedData.getExtendField(POST_FIELD_PREFIX + "dircode");
		dircode = StringUtils.nullToEmpty(dircode);
		body.addElement("dircode").addText(dircode);
		// areacode 地市区域，若为省编码则对全省目录进行查询
		String areacode = seedData.getExtendField(POST_FIELD_PREFIX + "areacode");
		areacode = StringUtils.nullToEmpty(areacode);
		body.addElement("areacode").addText(areacode);
		// nodeversion 目录结点版本号，空则全量查询
		String nodeversion = seedData.getExtendField(POST_FIELD_PREFIX
				+ "nodeversion");
		nodeversion = StringUtils.nullToEmpty(nodeversion);
		body.addElement("nodeversion").addText(nodeversion);
		// depth 查询目录深度，取值范围[0..10]，目录深度最多不超过10级。
		// 当该字段为空或者取值为0时，表示查询指定目录下所有子目录信息，即遍历到底，包括内容和应用。
		// 取值>0，表示返回该目录下指定层级的子目录以及目录下资源信息。
		String depth = seedData.getExtendField(POST_FIELD_PREFIX + "depth");
		depth = StringUtils.nullToEmpty(depth);
		body.addElement("depth").addText(depth);
		// returenres 是否返回目录下资源信息
		// 0：返回
		// 1：不返回
		// 默认不返回
		String returenres = seedData.getExtendField(POST_FIELD_PREFIX + "returenres");
		if (StringUtils.isEmptyString(returenres)) {
			returenres = "1";
		}
		body.addElement("returenres").addText(returenres);

		req.setContentType("text/xml");
		req.setPostString(doc.asXML());
	}
}
