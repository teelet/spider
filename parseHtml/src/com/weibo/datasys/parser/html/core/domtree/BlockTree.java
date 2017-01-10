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

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.weibo.datasys.parser.conf.ParseConfig;
import com.weibo.datasys.parser.html.antispam.FilterTag;
import com.weibo.datasys.parser.html.data.BlockInfo;

import com.weibo.datasys.parser.util.TextUtil;

public class BlockTree {

	/** 候选标题 */
	private HashMap<Integer, String> titleMap = new HashMap<Integer, String>(); // h标签中的文本信息
	protected String specialTitle = "";
	protected String origTitle = "";
	protected String url = "";

	private String maxWeightText = "";
	private double maxWeight = 0d;
	private BlockScore blockScore = null;
	private double threshhold = 0d;

	public BlockTree(BlockScore blockScore, String url) {

		this.blockScore = blockScore;
		this.url = url;
		configure();
	}

	public void configure() {
		this.threshhold = this.blockScore.getConfigData().getThreshold();
	}

	public static BlockInfo createBlockTree(Node root, ParseConfig parseConfig, int filter, String HtmlUrl) throws Exception {

		if (root != null) {
			BlockInfo rootBlock = new BlockInfo(root,HtmlUrl);
			NodeList nl = root.getChildNodes();
			Node childNode = null;
			for (int i = 0; i < nl.getLength(); i++) {
				childNode = nl.item(i);
				if (childNode != null) {
					BlockInfo childBlock = createBlockTree(nl.item(i), parseConfig, filter, HtmlUrl);
					if (childBlock != null) {
						rootBlock.addChildrenBlock(childBlock);
						childBlock.setParentBlock(rootBlock);
					} else {
						if (childNode.getParentNode() != null) {
							try {
								childNode.getParentNode().removeChild(childNode);
								i = i - 1;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				}

			}
			// node的信息提取
			rootBlock.setInfo2(parseConfig, filter);
			if (root.getNodeType() == Node.ELEMENT_NODE) {
				Element eNode = (Element) root;

				if (FilterTag.shouldBeRemove(eNode)) {
					return null;
				}
			}

			return rootBlock;
		} else {
			// root.getParentNode().removeChild(root);
			return null;
		}
	}

	public BlockInfo createMainBlockTree(Node root, ParseConfig parseConfig, int filter,String HtmlUrl) throws Exception {
		if (root != null) {
			BlockInfo rootBlock = new BlockInfo(root,HtmlUrl);
			NodeList nl = root.getChildNodes();
			Node childNode = null;
			for (int i = 0; i < nl.getLength(); i++) {
				childNode = nl.item(i);
				if (childNode != null) {
					BlockInfo childBlock = createMainBlockTree(nl.item(i), parseConfig, filter, HtmlUrl);
					if (childBlock != null) {
						rootBlock.addChildrenBlock(childBlock);
						childBlock.setParentBlock(rootBlock);
					} else {
						if (childNode.getParentNode() != null) {
							try {
								childNode.getParentNode().removeChild(childNode);
								i = i - 1;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				}
			}

			if (threshhold >= 0) {

				rootBlock.setInfo2(parseConfig, filter);
				String nodeName = root.getNodeName();

				if (root.getNodeType() == Node.TEXT_NODE) {
					if (FilterTag.needBlockProcessByTextNode(root)) {
						blockScore.blockWeight(rootBlock);
					} else if (FilterTag.shouldRemoveTextNode(root)) {
						return null;
					} else {
					}
				} else if (root.getNodeType() == Node.ELEMENT_NODE) {
					Element eNode = (Element) root;
					setSpecialTitle(eNode);
					// 不行的话也就配置一下都需要什么title
					if ("h1".equalsIgnoreCase(eNode.getNodeName()) || "h2".equalsIgnoreCase(eNode.getNodeName())) {
						if (null != root.getTextContent() && !root.getTextContent().trim().equals("")) {
							if ("csgo.766.com".equals(parseConfig.getHost())){
								if ("h2".equalsIgnoreCase(eNode.getNodeName())){
									
								}else{
									int titleNum = titleMap.size() + 1;
									titleMap.put(titleNum, TextUtil.Normalizer(root.getTextContent().trim()));
								}
							}else{
								int titleNum = titleMap.size() + 1;
								titleMap.put(titleNum, TextUtil.Normalizer(root.getTextContent().trim()));
							}
						}
					}
					if ((filter == FilterTag.CONTENT)&&(FilterTag.shouldBeRemove(eNode))) {
						return null;
					} else if (FilterTag.isPTag(nodeName)) {
						Node node = rootBlock.getNode();
						if (FilterTag.needBlockProcessByPNode(node)) {
							blockScore.blockWeight(rootBlock);
						}
					} else if (FilterTag.isLinkTag(nodeName)) {
						// 如果孩子是图片的话且图片的限制没有限制条件，就要改A节点
						// e.g.http://gl.ali213.net/
						if (FilterTag.hasImgChild(rootBlock) && (parseConfig.getImgRootFilter().length == 0
								|| (parseConfig.getImgRootFilter().length == 1)
										&& (parseConfig.getImgRootFilter()[0].equals("")))) 
						{
						
						}else if (aManager(rootBlock) == null) {
							return null;
						}

					} else if (FilterTag.isPreserveNodes(nodeName)) {
					} else if (rootBlock.isFormatTag()) {
						String mText = rootBlock.getAllText();
						if ("".equals(mText.trim())){
							return null;
						}
						
					} else if (rootBlock.isImgTag()) {
					} else if (rootBlock.isPreviousPageTag()){
					} else if (rootBlock.isNextPageTag()){
					} else if (FilterTag.isBrTag(nodeName)) {
					} else {
						blockScore.blockWeight(rootBlock);
					}
				} else {
					// text和element类型以外的节点

				}
				judgeMax(rootBlock);
				if (rootBlock.getScore() < threshhold && root.getParentNode() != null) {

					return null;
				}
			}
			return rootBlock;
		} else {
			return null;
		}
	}

	public BlockInfo aManager(BlockInfo blockInfo) {
		Node node = blockInfo.getNode();
		if (FilterTag.shouldRemoveANode(node)) {
			return null;
		}
		
		if (blockInfo.isPreviousPageTag() || blockInfo.isNextPageTag())
			return blockInfo;

		String mText = blockInfo.getAllText();
		if (mText.contains("http") && (!blockInfo.isImgTag()))
			return null;

		return blockInfo;

	}

	protected void setSpecialTitle(Node node) {
	}

	private void judgeMax(BlockInfo curBlock) {
		double curW = curBlock.getScore();
		if (curW < 100 && curW > maxWeight) {
			maxWeight = curW;
			maxWeightText = curBlock.getAllText();
		}
	}

	public HashMap<Integer, String> getTitleMap() {
		return titleMap == null ? new HashMap<Integer, String>() : titleMap;
	}

	public String getSpecialTitle() {
		return specialTitle;
	}

	public String getMaxWeightText() {
		return maxWeightText == null ? "" : maxWeightText;
	}

	public double getMaxWeight() {
		return maxWeight;
	}

	public String getOrigTitle() {
		return origTitle;
	}

	public void setOrigTitle(String origTitle) {
		this.origTitle = origTitle;
	}

}
