/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.antispam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sun.tools.javac.util.Pair;
import com.weibo.datasys.parser.conf.ParseConfig;
import com.weibo.datasys.parser.html.data.BlockInfo;

public class FilterTag {

	
	public static int CONTENT = 0;
	public static int NO_CONTENT = 1;
	
	/**
	 * 噪音文本
	 */
	private static String[] blockNoiseStrs;

	//public static int BLOCK_NOISE_STRS = 1 << 0;

	/**
	 * 目录页url特征
	 */
	
	private static String[] ThemeNosieStrs;
	 
	/**
	 * 中文常用标点
	 */
	private static String[] chndots;
	/**
	 * 中文不合法标点
	 */
	private static String[] invalidChndots;
	/**
	 * 其它语言常用标点
	 */
	private static String[] foreigndots;

	/**
	 * 其它语言不合法标点
	 */
	
	private static String[] invalidForeigndotsStrings;
	/**
	 * 不合法节点名
	 */
	
	private static HashSet<String> invalidNodesSet = new HashSet<String>();

	/**
	 * 不合法节点id属性值
	 */

	private static HashSet<String> invalidIdValuesSet = new HashSet<String>();

	/**
	 * 不合法节点class属性值
	 */

	private static HashSet<String> invalidClassValuesSet = new HashSet<String>();
	/**
	 * 块节点
	 */
	
	private static HashSet<String> blockManagerSet = new HashSet<String>();

	/**
	 * 特殊p节点
	 */

	private static HashSet<String> pSet = new HashSet<String>();

	/**
	 * 特殊p节点
	 */
	private static HashSet<String> imgSet = new HashSet<String>();
	
	/**
	 * 图片节点的过滤条件1(祖先节点是如下的不要)
	 */
	private static HashSet<String> imgRootFilterSet = new HashSet<String>();
	
	/**
	 * 图片节点的过滤条件2(前兄弟节点是如下的不要)
	 */
	private static HashSet<String> imgPSiblingFilterSet = new HashSet<String>();
	
	/**
	 * 图片节点的过滤条件2(前兄弟节点是如下的不要)
	 */
	private static HashSet<String> imgNSiblingFilterSet = new HashSet<String>();
	
	/**
	 * 链接节点
	 */
	private static HashSet<String> linkSet = new HashSet<String>();
	/**
	 * 不做处理的节点
	 */
	private static HashSet<String> preserveNodesSet = new HashSet<String>();
	
	/**
	 * 分段节点
	 */
	private static HashSet<String> segNodesSet = new HashSet<String>();
	
	/**
	 * 内容分段节点
	 */
	
	//private static HashSet<String> contentSegNodesSet = new HashSet<String>();
	
	/**
	 * 需要保留的特殊格式节点
	 */

	private static HashSet<String> specialFormatlNodesSet = new HashSet<String>();

	
	/**
	 * 特殊格式节点过滤条件（祖先是如下不要）
	 */
	private static HashSet<String> formatRootFilterSet = new HashSet<String>();

	
	/**
	 *  图片的节点名 不是所有的都是img
	 */
	
	private static HashSet<String> imageTagsSet = new HashSet<String>();
	
	private static HashSet<String> imageStyle = new HashSet<String>();
	
	private static HashSet<String> invalidImages = new HashSet<String>();
	private static HashSet<String> invalidText = new HashSet<String>();
	private static HashSet<String> invalidHrefText = new HashSet<String>();
	private static HashSet<String> needStyleTagSet = new HashSet<String>();
	
	private static HashSet<String> previousPage = new HashSet<String>();
	private static HashSet<String> nextPage = new HashSet<String>();
	
	/**
	 * 
	 * @param parseConfig
	 */
	public static void reloadAll(ParseConfig parseConfig) {

		blockNoiseStrs = parseConfig.getBlockNoiseStrs();
		ThemeNosieStrs = parseConfig.getThemeNosieStrs();
		chndots = parseConfig.getChndots();
		invalidChndots = parseConfig.getInvalidChndots();
		foreigndots = parseConfig.getForeigndots();
		invalidForeigndotsStrings = parseConfig.getInvalidForeigndotsStrings();
		invalidNodesSet.clear();
		invalidNodesSet.addAll(Arrays.asList(parseConfig.getInvalidNodes()));
		blockManagerSet.clear();
		blockManagerSet.addAll(Arrays.asList(parseConfig.getBlockManagerNodes()));
		pSet.clear();
		pSet.addAll(Arrays.asList(parseConfig.getpNodes()));
		linkSet.clear();
		linkSet.addAll(Arrays.asList(parseConfig.getLinkNodes()));
		preserveNodesSet.clear();
		preserveNodesSet.addAll(Arrays.asList(parseConfig.getPreserveNodes()));
		invalidIdValuesSet.clear();
		invalidIdValuesSet.addAll(Arrays.asList(parseConfig.getInvalidIdValue()));
		segNodesSet.clear();
		segNodesSet.addAll(Arrays.asList(parseConfig.getSegNodes()));
		imgSet.clear();
		imgSet.addAll(Arrays.asList(parseConfig.getImgNodes()));
		specialFormatlNodesSet.clear();
		specialFormatlNodesSet.addAll(Arrays.asList(parseConfig.getSpeciaFormatlNode()));
		invalidClassValuesSet.clear();
		invalidClassValuesSet.addAll(Arrays.asList(parseConfig.getInvalidClassValue()));
		imgRootFilterSet.clear();
		imgRootFilterSet.addAll(Arrays.asList(parseConfig.getImgRootFilter()));
		imgPSiblingFilterSet.clear();
		imgPSiblingFilterSet.addAll(Arrays.asList(parseConfig.getImgPSiblingFilter()));
		imgNSiblingFilterSet.clear();
		imgNSiblingFilterSet.addAll(Arrays.asList(parseConfig.getImgNSiblingFilter()));
		formatRootFilterSet.clear();
		formatRootFilterSet.addAll(Arrays.asList(parseConfig.getFormatRootFilter()));
		imageTagsSet.clear();
		imageTagsSet.addAll(Arrays.asList(parseConfig.getImageTag()));
		imageStyle.clear();
		imageStyle.addAll(Arrays.asList(parseConfig.getImageStyle()));
		invalidImages.clear();
		invalidImages.addAll(Arrays.asList(parseConfig.getInvalidImages()));
		invalidText.clear();
		invalidText.addAll(Arrays.asList(parseConfig.getInvalidText()));
		invalidHrefText.clear();
		invalidHrefText.addAll(Arrays.asList(parseConfig.getInvalidHrefText()));
		needStyleTagSet.clear();
		needStyleTagSet.addAll(Arrays.asList(parseConfig.getNeedStyleTag()));
		previousPage.clear();
		previousPage.addAll(Arrays.asList(parseConfig.getPreviousPage()));
		nextPage.clear();
		nextPage.addAll(Arrays.asList(parseConfig.getNextPage()));
	}

	/**
	 * 判断是否为非法节点
	 */
	public static boolean isSpecialFormatNode(String tagname) {
		if (tagname == null) {
			return false;
		} else {
			return specialFormatlNodesSet.contains(tagname.toLowerCase().trim());
		}

	}

	/**
	 * 判断是否为特殊格式节点
	 */
	public static boolean filterInvalidTag(String tagname) {
		if (tagname == null) {
			return false;
		} else {
			return invalidNodesSet.contains(tagname.toLowerCase().trim());
		}

	}

	/**
	 * 判断是否为块节点
	 */
	public static boolean isBlockTag(String tagname) {
		if (tagname == null) {
			return false;
		} else {
			return blockManagerSet.contains(tagname.toLowerCase().trim());
		}
	}

	/**
	 * 判断是否为内容分段节点
	 */
	/*
	public static boolean isSegTag(String tagname) {
		if (tagname == null) {
			return false;
		} else {
			return contentSegNodesSet.contains(tagname.toLowerCase().trim());
		}
	}
	 */
	/**
	 * 判断是否为分段节点
	 */
	public static boolean isSegTag(String tagname) {
		if (tagname == null) {
			return false;
		} else {
			return segNodesSet.contains(tagname.toLowerCase().trim());
		}
	}

	/**
	 * 判断是否为特殊p节点
	 */
	public static boolean isPTag(String tagname) {
		if (tagname == null) {
			return false;
		} else {
			return pSet.contains(tagname.toLowerCase().trim());
		}
	}

	/**
	 * 判断是否为img节点
	 */
	public static boolean isimgTag(String tagname) {
		if (tagname == null) {
			return false;
		} else {
			return imgSet.contains(tagname.toLowerCase().trim());
		}
	}

	/**
	 * 判断是否为链接节点
	 */
	public static boolean isLinkTag(Node node) {
		if (node == null) {
			return false;
		} else {
			return linkSet.contains(node.getNodeName().toLowerCase().trim());
		}
	}
	
	public static boolean isImgRootFilterTag(Node node) {
		if (node == null) {
			return false;
		} else {
			return imgRootFilterSet.contains(node.getNodeName().toLowerCase().trim());
		}
	}
	
	public static boolean isImgNSblingFilterTag(Node node) {
		if (node == null) {
			return false;
		} else {
			return imgPSiblingFilterSet.contains(node.getNodeName().toLowerCase().trim());
		}
	}
	
	public static boolean isImgPSblingFilterTag(Node node) {
		if (node == null) {
			return false;
		} else {
			return imgNSiblingFilterSet.contains(node.getNodeName().toLowerCase().trim());
		}
	}
	
	
	public static boolean isFormatRootFilterTag(Node node) {
		if (node == null) {
			return false;
		} else {
			return formatRootFilterSet.contains(node.getNodeName().toLowerCase().trim());
		}
	}

	/**
	 * 判断是否为链接节点
	 */
	public static boolean isLinkTag(String tagName) {
		if (tagName == null) {
			return false;
		} else {
			return linkSet.contains(tagName.toLowerCase().trim());
		}
	}

	/**
	 * 判断url里是否包含目录页特征
	 */
	public static boolean isContainThemeNosie(String url) {
		if (url == null) {
			return false;
		} else {
			for (int i = 0; i < ThemeNosieStrs.length; i++) {
				if (url.contains(ThemeNosieStrs[i])) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 判断id值是否合法
	 */
	public static boolean isInvalidIdValue(String idValue) {
		if (idValue == null) {
			return false;
		} else {
			return invalidIdValuesSet.contains(idValue.toLowerCase().trim());
		}
	}

	/**
	 * 判断class值是否合法
	 */
	public static boolean isInvalidClassValue(String classValue) {
		if (classValue == null) {
			return false;
		} else {
			return invalidClassValuesSet.contains(classValue.toLowerCase().trim());
		}
	}

	/**
	 * 返回包含噪音文本的个数及长度.
	 * 
	 * @return new int[]{count,length}
	 */
	public static int[] filterNoiseText(String blockContent) {
		if (blockContent == null) {
			return new int[] { 0, 0 };
		}
		blockContent = blockContent.toLowerCase().trim();
		int count = 0;
		int length = 0;

		for (int i = 0; i < blockNoiseStrs.length; i++) {
			if (blockContent.contains(blockNoiseStrs[i])) {
				// System.out.println(blockNoiseStrs[i]);
				count++;
				length += blockNoiseStrs[i].length();
			}
		}
		int[] noiseinfo = new int[] { count, length };
		return noiseinfo;
	}

	/**
	 * 返回包含汉字文本中标点符号的个数.
	 * 
	 * @return new int[]{count,length}
	 */
	public static int getNumofChndots(String s) {
		if (s == null || s.trim().length() == 0) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < chndots.length; i++) {
			count += getNumOfSubstring(s, chndots[i]);
		}
		return count;
	}

	/**
	 * 返回包含汉字文本中不合法标点符号的个数.
	 * 
	 * @return new int[]{count,length}
	 */
	public static int getNumofInvalidChndots(String s) {
		if (s == null || s.trim().length() == 0) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < invalidChndots.length; i++) {
			count += getNumOfSubstring(s, invalidChndots[i]);
		}
		return count;
	}

	/**
	 * 返回包含其它语言文本中合法标点符号的个数.
	 * 
	 * @return new int[]{count,length}
	 */
	public static int getNumofForeigndots(String s) {
		if (s == null || s.trim().length() == 0) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < foreigndots.length; i++) {
			count += getNumOfSubstring(s, foreigndots[i]);
		}
		return count;
	}

	/**
	 * 返回包含其它语言文本中不合法标点符号的个数.
	 * 
	 * @return new int[]{count,length}
	 */
	public static int getNumofInvalidForeigndots(String s) {
		if (s == null || s.trim().length() == 0) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < invalidForeigndotsStrings.length; i++) {
			count += getNumOfSubstring(s, invalidForeigndotsStrings[i]);
		}
		return count;
	}

	/**
	 * 是否应该删除该节点
	 */
	public static boolean shouldBeRemove(Element eNode) {
		if (eNode == null) {
			return false;
		}
		String tagName = eNode.getNodeName();
		String nodeID = eNode.getAttribute("ID");
		String nodeClass = eNode.getAttribute("CLASS");
		tagName = tagName.toLowerCase().trim();

		if (FilterTag.isInvalidIdValue(nodeID)) {
			return true;
		} else if (FilterTag.filterInvalidTag(tagName)) {
			return true;
		} else if (FilterTag.isInvalidClassValue(nodeClass)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否应该做blockManager处理，输入为tagname
	 */
	public static boolean needBlockProcessByTextNode(Node node) {

		if (node == null) {
			return false;
		}
		boolean need = false;
		Node parentNode = node.getParentNode();
		if (parentNode != null && node.getParentNode().getNodeName().compareToIgnoreCase("body") == 0) {
			need = true;
		}
		return need;

	}

	/**
	 * 是否应该做blockManager处理，输入为tagname
	 */
	public static boolean needBlockProcessByPNode(Node node) {
		if (node == null) {
			return false;
		}

		if (node.getParentNode().getNodeName().compareToIgnoreCase("body") == 0) {
			return true;
		} else if (node.getParentNode().getNodeName().compareToIgnoreCase("span") == 0
				&& node.getParentNode().getParentNode().getNodeName().compareToIgnoreCase("body") == 0) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 是否应该删除该A节点处理，输入为链接节点
	 */
	public static boolean shouldRemoveANode(Node node) {
		if (node == null) {
			return false;
		}
		String text = node.getNodeValue();

		if (text != null && text.length() > 0) {
			int[] result = filterNoiseText(text.trim());
			if (result[0] > 2 || ((result[1] * 1.0) / (text.length() * 1.0)) > 0.5) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 是否应该删除该TextNode节点处理，输入为textnode节点
	 */
	public static boolean shouldRemoveTextNode(Node node) {
		if (node == null) {
			return false;
		}
		String text = node.getNodeValue();
		
		if (text != null && text.length() > 0) {
			int[] result = filterNoiseText(text.trim());
			/*
			 * if (result[0] > 2 || ((result[1] * 1.0) / (text.length() * 1.0))
			 * > 0.5) { return true; }
			 */
			if (result[0] > 0)
				return true;

		}
		return false;
	}

	/**
	 * 不做任何处理的节点
	 */
	public static boolean isPreserveNodes(String tagname) {
		if (tagname == null) {
			return false;
		} else {
			return preserveNodesSet.contains(tagname.toLowerCase().trim());
		}

	}

	/**
	 * s中出现子串sub的次数
	 */
	protected static int getNumOfSubstring(String s, String sub) {
		int i = 0;
		int num = 0;
		if (s != null) {
			while (s.indexOf(sub, i) != -1) {
				num++;
				i = s.indexOf(sub, i) + 1;
			}
		}
		return num;
	}
	/**
	 * 是否就有一个图片节点
	 * @param BlockList
	 * @return
	 */
	public static boolean HasOnly1ImgChild(ArrayList<BlockInfo> BlockList) {
		if (BlockList == null)
			return false;
		int htmlTag = 0;
		boolean hasImgTag = false;
		for (int i = 0; i < BlockList.size(); i++) {
			BlockInfo curBlockInfo = (BlockInfo) BlockList.get(i);
			Node node = curBlockInfo != null ? curBlockInfo.getNode() : null;
			if (node != null) {
				if (node.getNodeType() != Node.TEXT_NODE)
					htmlTag += 1;
				if (curBlockInfo.isImgTag())
					hasImgTag = true;
			}
		}

		if (htmlTag != 1)
			return false;
		return hasImgTag;
	}
	
	/**
	 * 图片祖先节点是否在filter中
	 * @param node
	 * @return
	 */
	public static boolean HasRootFilter4Img(Node node) {
		if (FilterTag.imgRootFilterSet.size() == 0)
			return false;
		
		if (node == null)
			return false;
		Node parent = node.getParentNode();
		if (FilterTag.isImgRootFilterTag(parent))
			return true;
		return HasRootFilter4Img(parent);
	}
	
	/**
	 * 图片节点的下一个兄弟节点是否在filter中
	 * @param node
	 * @return
	 */
	public static boolean HasNSblingFilter4Img(Node node) {
		if (node == null)
			return false;
		Node NSbling = node.getNextSibling();
		if (FilterTag.isImgNSblingFilterTag(NSbling))
			return true;
		else
			return false;

	}
	
	/**
	 * 图片节点的上一个兄弟节点是否在filter中
	 * @param node
	 * @return
	 */
	public static boolean HasPSblingFilter4Img(Node node) {
		if (node == null)
			return false;
		Node PSbling = node.getPreviousSibling();
		if (FilterTag.isImgPSblingFilterTag(PSbling))
			return true;
		else
			return false;
	}
	
	/**
	 * 特殊格式节点的祖先是否在filter中
	 * @param node
	 * @return
	 */
	public static boolean HasRootFilter4Format(Node node) {
		if (FilterTag.formatRootFilterSet.size() == 0)
			return false;
		
		if (node == null)
			return false;
		Node parent = node.getParentNode();
		if (FilterTag.isFormatRootFilterTag(parent))
			return true;
		return HasRootFilter4Format(parent);
	}
	
	/**
	 * 该数据块中是否有一个符合条件的图片节点
	 * @param node
	 * @return
	 */
	public static boolean hasImgChild(BlockInfo rootBlock){
		ArrayList<BlockInfo> children = rootBlock.getChidrenBlockList();
	
		for (int i = 0 ; i < children.size(); i++){
			if (children.get(i).isImgNode()){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 判断该图片是不是真正想要的
	 * @param name
	 * @return
	 */
	
	public static boolean isWantedImage(String name){
		return imageTagsSet.contains(name.toLowerCase());
	}
	public static boolean isWantedImageStyle(String name){
		return imageStyle.contains(name.toLowerCase());
	}
		
	public static boolean isInvalidImage(String image){
		return invalidImages.contains(image);
	}
	public static boolean isInvalidText(String text){
		Iterator<String> itr = invalidText.iterator();
		while (itr.hasNext()) {
			String invalidText = itr.next();
			if (text.contains(invalidText))
				return true;
		}
		return false;
	}
	
	public static boolean isObjectTag(String name){
		if ("object".equalsIgnoreCase(name)){
			return true;
		}
		if ("embed".equalsIgnoreCase(name)){
			return true;
		}
		if ("videojumper".equalsIgnoreCase(name)){
			return true;
		}
		if ("iframe".equalsIgnoreCase(name)){
			return true;
		}
		return false;
	}
	
	public static boolean isBrTag(String name){
		if ("br".equalsIgnoreCase(name))
			return true;
		return false;
	}
	
	public static boolean isInvalidHrefText(String text){
		Iterator<String> itr = invalidHrefText.iterator();
		while (itr.hasNext()) {
			String invalidText = itr.next();
			if (text.contains(invalidText))
				return true;
		}
		return false;
	}
	
	public static boolean isNeedStyleTag(String name){
		return needStyleTagSet.contains(name.toLowerCase());
	}
	
	public static boolean isPreviousPage(String name){
		return previousPage.contains(name.toLowerCase());
	}
	public static boolean isNextPage(String name){
		return nextPage.contains(name.toLowerCase());
	}

}
