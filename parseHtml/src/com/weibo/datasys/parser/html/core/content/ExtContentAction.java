/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import org.w3c.dom.Node;

import com.weibo.datasys.parser.conf.ParseConfig;
import com.weibo.datasys.parser.html.antispam.FilterTag;
import com.weibo.datasys.parser.html.core.domtree.BasicBlockAction;
import com.weibo.datasys.parser.html.data.BlockInfo;

public class ExtContentAction extends BasicBlockAction {

	private static final int MIN_NODE_TEXT_LENGTH = 2;
	//private static final Logger LOG = LoggerFactory.getLogger(ExtContentAction.class);
	private HashSet<String> stopChar = null;
	private String[] stopStrs = { "。", "！", "？", "：", "：", "；", "…", "、", "，", "“", "”", "‘", "’", "!", "?", ":", ";",
			",", "\"", "'", ">", "》" };

	private StringBuffer content = new StringBuffer();
	private boolean addPFlag = true;
	private boolean addEndPFlag = false;
	private boolean addDivFlag = false;
	private boolean addFormattedFlag = false;
	private String FormattedTag = "";
	private ParseConfig parseConfig = null;

	private ArrayList<String> formatTagDequeue = new ArrayList<String>();

	private ArrayList<String> SegTagDequeue = new ArrayList<String>();

	public ParseConfig getParseConfig() {
		return parseConfig;
	}

	public void setParseConfig(ParseConfig parseConfig) {
		this.parseConfig = parseConfig;
	}

	public ExtContentAction() {
		stopChar = new HashSet<String>();
		stopChar.addAll(Arrays.asList(stopStrs));
	}

	@Override
	public boolean action(BlockInfo currentBlock) throws Exception {
		String nodeName = currentBlock.getNodeName();
		short nodeType = currentBlock.getNodeType();

		if (nodeName.equalsIgnoreCase("head") || nodeType == Node.COMMENT_NODE) {
			return false;
		}

		if (nodeType == Node.TEXT_NODE) {
			String text = currentBlock.getAllText();
			// if (text.contains("]原创翻译"))
			// System.out.println("a");
			if (text.length() >= MIN_NODE_TEXT_LENGTH) {
				content.append(text);

				String endWord = text.substring(text.length() - 1);
				if (!stopChar.equals(endWord)) {
					content.append(" ");
				}
			}
		}

		// br节点 加一个br
		if (FilterTag.isBrTag(nodeName)) {
			content.append("<br>");
		}

		// 特殊格式节点
		if (currentBlock.isFormatNode()) {
			FormattedTag = nodeName.toLowerCase();
			// 图片注释居中
			if (currentBlock.isFormatNode4Img()) {
				content.append("<div style='text-align:center'>");
				addDivFlag = true;
			}
			content.append("<");
			content.append(FormattedTag);
			HashMap<String,String> TagAttrs = currentBlock.getTagAttrs();
			
			if (TagAttrs != null){
			for (Entry<String, String> entry : TagAttrs.entrySet()) {
				content.append(" ");
				String attrName = entry.getKey();
				String attrValue = entry.getValue();
				content.append(attrName);
				content.append("=");
				content.append("'");
				content.append(attrValue);
				content.append("'");
			}
			}
			content.append(">");
			formatTagDequeue.add(FormattedTag);
			addFormattedFlag = true;
		}

		// 图片节点
		if (currentBlock.isImgNode()) {
			if (addEndPFlag) {
				content.append("</p>");
				addEndPFlag = false;
				addPFlag = true;
			}
			HashMap<String, String> imageAttrs = currentBlock.getImageAttrs();
			// 如果多个小图片是一行的话 ，也把持这种格式
			if ((currentBlock.HasNSblingImg()) && (!currentBlock.HasPSblingImg())) {
				//content.append("<div style='text-align:center'><img alt='图片未加载' src='");
				content.append("<img alt='图片未加载' src='");
				String text = currentBlock.getAllText();
				content.append(text);
				content.append("'");
				for (Entry<String, String> entry : imageAttrs.entrySet()) {
					content.append(" ");
					String attrName = entry.getKey();
					String attrValue = entry.getValue();
					if (!("alt".equalsIgnoreCase(attrName))) {
						content.append(attrName);
						content.append("=");
						content.append("'");
						content.append(attrValue);
						content.append("'");
					}
				}

				content.append(">");
			} else if ((currentBlock.HasNSblingImg()) && (currentBlock.HasPSblingImg())) {
				content.append("<img alt='图片未加载' src='");
				String text = currentBlock.getAllText();
				content.append(text);
				content.append("'");
				for (Entry<String, String> entry : imageAttrs.entrySet()) {
					content.append(" ");
					String attrName = entry.getKey();
					String attrValue = entry.getValue();
					if (!("alt".equalsIgnoreCase(attrName))) {
						content.append(attrName);
						content.append("=");
						content.append("'");
						content.append(attrValue);
						content.append("'");
					}
				}
				content.append(">");
			} else if ((!currentBlock.HasNSblingImg()) && (currentBlock.HasPSblingImg())) {
				content.append("<img alt='图片未加载' src='");
				String text = currentBlock.getAllText();
				content.append(text);
				content.append("'");
				for (Entry<String, String> entry : imageAttrs.entrySet()) {
					content.append(" ");
					String attrName = entry.getKey();
					String attrValue = entry.getValue();
					if (!("alt".equalsIgnoreCase(attrName))) {
						content.append(attrName);
						content.append("=");
						content.append("'");
						content.append(attrValue);
						content.append("'");
					}
				}
				content.append(">");
				//content.append("></div>");
			} else {
				content.append("<img alt='图片未加载' src='");
				//content.append("<div style='text-align:center'><img alt='图片未加载' src='");
				String text = currentBlock.getAllText();
				content.append(text);
				content.append("'");
				for (Entry<String, String> entry : imageAttrs.entrySet()) {
					content.append(" ");
					String attrName = entry.getKey();
					String attrValue = entry.getValue();
					if (!("alt".equalsIgnoreCase(attrName))) {
						content.append(attrName);
						content.append("=");
						content.append("'");
						content.append(attrValue);
						content.append("'");
					}
				}
				content.append(">");
				//content.append("></div>");
			}
		}

		
		// 其实addFormattedFlag 感觉是可以去掉，直接用lastChildFormatedTag是不是为0就行，但是还没时间去改和验证
		if (addFormattedFlag) {
			// 如果是最后一个孩子 填、
			// <span>
			// ''
			// <span>
			int lastChildFormatedTag = currentBlock.getLastFormattedChild();
			if (lastChildFormatedTag > formatTagDequeue.size()) {
				throw new Exception("Mismatch Format tag");
			}
			while (lastChildFormatedTag > 0) {
				String tag = formatTagDequeue.get(formatTagDequeue.size() - 1);
				formatTagDequeue.remove(formatTagDequeue.size() - 1);
				content.append("</");
				content.append(tag);
				content.append(">");
				lastChildFormatedTag--;
				if (formatTagDequeue.size() == 0)
					addFormattedFlag = false;

			}
			if (addDivFlag) {
				content.append("</div>");
				addDivFlag = false;
			}
		}


		if (FilterTag.isSegTag(nodeName)) {
			content.append("<");
			content.append(nodeName.toLowerCase());
			HashMap<String,String> TagAttrs = currentBlock.getTagAttrs();
			if (TagAttrs != null){
			for (Entry<String, String> entry : TagAttrs.entrySet()) {
				content.append(" ");
				String attrName = entry.getKey();
				String attrValue = entry.getValue();
				content.append(attrName);
				content.append("=");
				content.append("'");
				content.append(attrValue);
				content.append("'");
			}
			}
			content.append(">");
			SegTagDequeue.add("</"+nodeName.toLowerCase()+">");
		}
		
		int lastChildPTag = currentBlock.getLastPChild();
		if (lastChildPTag > SegTagDequeue.size()) {
			throw new Exception("Mismatch Format P tag");
		}
		while (lastChildPTag > 0) {
			String tag = SegTagDequeue.get(SegTagDequeue.size() - 1);
			SegTagDequeue.remove(SegTagDequeue.size() - 1);
			content.append(tag);
			lastChildPTag--;
		}

		return true;
	}

	public StringBuffer getContent() {
		/*
		if ((content != null && !(content.length() <= 0)) && !content.toString().endsWith("</p>")) {
			content.append("</p>");
		}
		*/
		return content == null ? new StringBuffer() : content;
	}
}
