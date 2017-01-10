/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.product.demo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.tools.javac.util.Pair;
import com.weibo.datasys.common.exception.ContentBlankException;
import com.weibo.datasys.common.exception.ImageUrlException;
import com.weibo.datasys.common.exception.ObjectInContentException;
import com.weibo.datasys.parser.conf.ParseConfFactory;
import com.weibo.datasys.parser.conf.ParseConfig;
import com.weibo.datasys.parser.data.Constants;
import com.weibo.datasys.parser.html.antispam.FilterTag;
import com.weibo.datasys.parser.html.core.DistanceCalculator;
import com.weibo.datasys.parser.html.core.PageParse;
import com.weibo.datasys.parser.html.core.charset.EncodingExtractor;
import com.weibo.datasys.parser.html.core.content.ContentExtractor;
import com.weibo.datasys.parser.html.core.domtree.BlockManager;
import com.weibo.datasys.parser.html.core.domtree.BlockScore;
import com.weibo.datasys.parser.html.core.domtree.BlockTree;
import com.weibo.datasys.parser.html.core.domtree.DomTreeMaker;
import com.weibo.datasys.parser.html.core.meta.MetaDetector;
import com.weibo.datasys.parser.html.core.meta.MetaExtractor;
import com.weibo.datasys.parser.html.core.meta.UrlDetector;
import com.weibo.datasys.parser.html.core.pubtime.PubtimeExtractor;
import com.weibo.datasys.parser.html.data.BlockInfo;
import com.weibo.datasys.parser.html.data.MetaTags;
import com.weibo.datasys.parser.html.data.PageData;
import com.weibo.datasys.parser.html.data.ParseData;
import com.weibo.datasys.parser.html.data.ParseDataFormat;
import com.weibo.datasys.parser.html.data.ScoreConfigData;
import com.weibo.datasys.parser.html.filter.BlackListFilter;
import com.weibo.datasys.parser.util.TextUtil;

public class DemoPageParse extends PageParse {

	private static final Logger LOG = LoggerFactory.getLogger(DemoPageParse.class);
	private ParseData parseData;

	private String host = "";
	private ParseConfig parseConfig = null;

	public DemoPageParse(ParseData parseData) {
		this.parseData = parseData;
	}

	public ParseData parse() {
		PageData webpage = parseData.getPageData();
		if (webpage == null || webpage.getHtml() == null || webpage.getHtml().length == 0
				|| webpage.getUrl().length() > Constants.URL_MAX_SIZE) {
			parseData.setCrawlerstate(31);
			return parseData;
		}

		String webUrl = webpage.getUrl();
		URL url = null;
		try {
			url = new URL(webUrl);
			host = url.getHost();
		} catch (MalformedURLException e1) {

			LOG.error("[ERROR]: Get url host error." + e1);
		}

		Map<String, ParseConfig> allConf = ParseConfFactory.getAllConfMap();

		if (allConf.containsKey(host))
			parseConfig = allConf.get(host);

		if (parseConfig == null) {
			parseConfig = allConf.get("default");
		}

		if (parseConfig == null) {
			parseData.setCrawlerstate(32);
			LOG.error("[ERROR]: ", "No parse config to be used");
			return parseData;
		}

		FilterTag.reloadAll(parseConfig);

		/******************** args **********************/
		URL curUrl = null;
		MetaTags metaTags = new MetaTags();
		String encoding = "";
		String crudeMainText = "";
		String description = "";
		String metaKeywords = "";
		String anchorText = "";
		String noAnchorText = "";
		String title = "";
		String origtitle = "";
		String originSite = "";
		String originPublish = "";
		String originAuthor = "";
		String content = "";
		int parsestate = 0;
		int column = 0;
		long pubtime = 0;
		boolean noindex = false;
		DocumentFragment doc = null;
		Node rootNode = null;
		BlockInfo rootBlock = null;
		BlockInfo origRootBlock = null;
		BlockInfo bodyBlock = null;
		Node origRoot = null;
		// Node rootNode1 = null;
		URL base = null;
		ArrayList<BlockInfo> blockList = null;
		HashMap<String, String> contentImages = new HashMap<String, String>();
		HashMap<String, String> pageLink = new HashMap<String, String>();
		try {
			encoding = EncodingExtractor.GetEncoding(webpage.getHtml(), webpage.getExtendField("charset"));
			String tmp = new String(webpage.getHtml(), encoding);
			// System.out.println(tmp);
			tmp = tmp.replaceAll("#src", "realsrc");
			byte[] htmlByte = tmp.getBytes(encoding);

			doc = DomTreeMaker.getDomRoot(htmlByte, encoding, webpage.getUrl());
			rootNode = (doc == null) ? null : doc;

			origRoot = (rootNode == null) ? null : rootNode.cloneNode(true);
			// rootNode1 = (rootNode == null) ? null : rootNode.cloneNode(true);

			if (rootNode != null) {
				try {
					curUrl = UrlDetector.strToURL(webpage.getNormalizeUrl());
					MetaDetector.getMetaTags(metaTags, rootNode, curUrl);
					base = UrlDetector.getBaseURL(metaTags, curUrl);
					MetaExtractor metaInfoExtractor = new MetaExtractor(metaTags);
					description = metaInfoExtractor.getNormalDescription();
					metaKeywords = metaInfoExtractor.getNormalKeywords();
					origtitle = metaInfoExtractor.getNormalMetaTitle();
					noindex = metaTags.getNoIndex();
					originPublish = metaInfoExtractor.getMediaId();
				} catch (Exception e) {
					LOG.error("[ERROR]:in parse metedata.{}", e);
				}

				if (base != null) {
					origRootBlock = BlockTree.createBlockTree(origRoot, parseConfig, FilterTag.NO_CONTENT,
							webpage.getUrl());
			
					blockList = DistanceCalculator.getBlockList(origRootBlock);

					if (null != origRootBlock) {
						TextUtil.Normalizer(origRootBlock.getAllText());
						BlockManager.getHeadBlock(blockList);
						bodyBlock = BlockManager.getBodyBlock(blockList);
						anchorText = bodyBlock != null ? TextUtil.Normalizer(bodyBlock.getAnchorText()) : anchorText;
						noAnchorText = bodyBlock != null ? TextUtil.Normalizer(bodyBlock.getNoAnchorText())
								: noAnchorText;
					}
					if (!noindex) {

						DemoBlockTree lowTree = new DemoBlockTree(
								new BlockScore(new ScoreConfigData("", 0, ScoreConfigData.LOW_HOLD)), webpage.getUrl());
						if (lowTree != null) {

							Node titleNode = getTitleNodeBlock(rootNode, parseConfig.getTitleBlock());
							if (titleNode == null) {
								titleNode = rootNode;
							}

							rootBlock = lowTree.createMainBlockTree(titleNode, parseConfig, FilterTag.NO_CONTENT,
									webpage.getUrl());
							DemoTitleExtractor titleExtractor = new DemoTitleExtractor(origtitle, lowTree.getTitleMap(),
									lowTree.getSpecialTitle());
							title = titleExtractor.getTitle();
						}

						BlockTree crudeMainTree = new BlockTree(
								new BlockScore(new ScoreConfigData("", 0, ScoreConfigData.LOW_HOLD)), webpage.getUrl());
						if (crudeMainTree != null) {

							rootBlock = crudeMainTree.createMainBlockTree(rootNode, parseConfig, FilterTag.NO_CONTENT,
									webpage.getUrl());
							crudeMainText = ContentExtractor.getContent(rootBlock, title, description, webpage.getUrl(),
									parseConfig);
							crudeMainText = TextUtil.RemoveTag(crudeMainText);
							pubtime = PubtimeExtractor.extractTime(crudeMainText, webpage.getUrl());

							if ("".endsWith(originPublish)) {
								originPublish = DemoOriginExtractor.publishExtractor(crudeMainText, webpage.getUrl(),
										parseConfig.getOriginalPattern());
							}

							originAuthor = DemoOriginExtractor.authorExtractor(crudeMainText, webpage.getUrl());
						}

						BlockTree contentMainTree = new BlockTree(
								new BlockScore(new ScoreConfigData("", 0, ScoreConfigData.MIDDLE_HOLD)),
								webpage.getUrl());
						if (contentMainTree != null) {

							Node contentNode = getContentNodeBlock(origRoot, parseConfig.getMainTextStart());
							if (contentNode == null) {
								parseData.setCrawlerstate(36);
								LOG.warn("Not Found Content Block " + webpage.getId());
								return parseData;
							}
							rootBlock = contentMainTree.createMainBlockTree(contentNode, parseConfig, FilterTag.CONTENT,
									webpage.getUrl());
							content = ContentExtractor.getContent(rootBlock, title, description, webpage.getUrl(),
									parseConfig);
							content = TextUtil.Normalizer(content);

							if ("".equals(content) || content == null) {
								throw new ContentBlankException("Blank Content");
							}

							HashMap<String, HashMap<String, String>> res = ContentExtractor.getContentImgs(rootBlock,
									title, description, webpage.getUrl());

							if (res.containsKey("images"))
								contentImages = res.get("images");
							if (res.containsKey("href"))
								pageLink = res.get("href");

						} // maintree !=null

					} // !noindex

				} // !base
			}

		} catch (ImageUrlException e) {
			parseData.setCrawlerstate(31);
			LOG.warn("Image Url un normal: " + webpage.getId());
			return parseData;
		} catch (ContentBlankException e) {
			parseData.setCrawlerstate(34);
			LOG.warn("Blank Content: " + webpage.getId());
			return parseData;
		} catch (ObjectInContentException e) {
			parseData.setCrawlerstate(35);
			LOG.warn("Object in Content: " + webpage.getId());
			return parseData;
		} catch (Exception e) {// parse exception
			parseData.setCrawlerstate(32);
			LOG.error("[ERROR]:{}\t[URL]:{}", e.getStackTrace().toString(), webpage.getId());
			return parseData;
		}

		originSite = DemoOriginExtractor.siteExtractor(webpage.getUrl());

		// 如果从网页上找不到来源 就用originSite 当做 来源
		if ("".endsWith(originPublish)) {
			originPublish = originSite;
		}

		column = DemoColumnExtractor.extractor(webpage.getUrl());

		metaKeywords = (metaKeywords.length() > Constants.KEYWORD_MAX_SIZE)
				? metaKeywords.substring(0, Constants.KEYWORD_MAX_SIZE) : metaKeywords;
		title = (title.length() > Constants.TITLE_MAX_SIZE) ? title.substring(0, Constants.TITLE_MAX_SIZE) : title;
		origtitle = (origtitle.length() > Constants.TITLE_MAX_SIZE) ? origtitle.substring(0, Constants.TITLE_MAX_SIZE)
				: origtitle;
		description = (description.length() > Constants.DESCRIPTION_MAX_SIZE)
				? description.substring(0, Constants.DESCRIPTION_MAX_SIZE) : description;
		content = (content.length() > Constants.CONTENT_MAX_SIZE) ? content.substring(0, Constants.CONTENT_MAX_SIZE)
				: content;

		if (title.length() < 2 && content.length() < 5) {
			parsestate = 1;
		} else if (title.length() < 2) {
			parsestate = 2;
		} else if (content.length() < 5) {
			parsestate = 3;
		}

		// LOG.debug("title:{}\tencoding:{}\tcrudeMainText:{}\tcategory:{}\tprovince:{}",
		// new Object[] { title, encoding, crudeMainText, category, province });
		// LOG.error("[ERROR]:{} ", webpage.getTaskId());
		parseData = ParseDataFormat.format(parseData, webpage.getId(), webpage.getUrl(), webpage.getNormalizeUrl(),
				origtitle, title, content, encoding, metaKeywords, description, column, pubtime, webpage.getFetchtime(),
				originSite, originPublish, originAuthor, 0, 0, 1, parsestate, contentImages, pageLink,
				webpage.getTaskId());
		if (1 == parsestate) {
			parseData.setParsestate(BlackListFilter.filter(title));
		}

		return parseData;
	}

	private Node getContentNodeBlock(Node root, Set<Pair<String, Pair<String, String>>> mainTextStart) {
		ArrayList<Node> allNodes = new ArrayList<Node>();
		travelTree(root, allNodes);

		Set<Pair<String, String>> matchText = new HashSet<Pair<String, String>>();
		Set<Pair<String, String>> matchRegx = new HashSet<Pair<String, String>>();

		Iterator<Pair<String, Pair<String, String>>> it = mainTextStart.iterator();

		while (it.hasNext()) {
			Pair<String, Pair<String, String>> str = it.next();
			if ("0".equals(str.fst))
				matchText.add(str.snd);
			if ("1".equals(str.fst))
				matchRegx.add(str.snd);
		}

		for (int i = 0; i < allNodes.size(); i++) {
			Node oneNode = allNodes.get(i);
			NamedNodeMap na = oneNode.getAttributes();
			if (na != null) {

				int ln = na.getLength();
				for (int k = 0; k < ln; k++) {
					Node pnode = na.item(k);
					if (matchText.contains(new Pair<String, String>(pnode.getNodeName(), pnode.getNodeValue()))) {
						return oneNode;
					}
				}

				// 正则匹配
				for (int k = 0; k < ln; k++) {
					Node pnode = na.item(k);
					if (matchRegx.size() > 0) {
						String nodeName = pnode.getNodeName();
						String nodeValue = pnode.getNodeValue();

						Iterator<Pair<String, String>> itr = matchRegx.iterator();

						while (itr.hasNext()) {
							Pair<String, String> str = itr.next();
							if (nodeName.equalsIgnoreCase(str.fst)) {
								Pattern pat = Pattern.compile(str.snd);
								Matcher publishMatcher = pat.matcher(nodeValue);
								if (publishMatcher.find()) {
									return oneNode;
								}
							}
						}
					}
				}
			}
		}
		return null;

	}

	private Node getTitleNodeBlock(Node root, Set<Pair<String, Pair<String, String>>> titleBlock) {

		if ((titleBlock == null) || (titleBlock.size() == 0))
			return root;

		Set<Pair<String, String>> matchText = new HashSet<Pair<String, String>>();
		Set<Pair<String, String>> matchRegx = new HashSet<Pair<String, String>>();

		Iterator<Pair<String, Pair<String, String>>> it = titleBlock.iterator();

		while (it.hasNext()) {
			Pair<String, Pair<String, String>> str = it.next();
			if ("0".equals(str.fst))
				matchText.add(str.snd);
			if ("1".equals(str.fst))
				matchRegx.add(str.snd);
		}

		ArrayList<Node> allNodes = new ArrayList<Node>();
		travelTree(root, allNodes);

		for (int i = 0; i < allNodes.size(); i++) {
			Node oneNode = allNodes.get(i);
			NamedNodeMap na = oneNode.getAttributes();
			if (na != null) {
				int ln = na.getLength();
				for (int k = 0; k < ln; k++) {
					Node pnode = na.item(k);
					if (matchText.contains(new Pair<String, String>(pnode.getNodeName(), pnode.getNodeValue()))) {
						return oneNode;
					}
				}

				// 正则匹配
				for (int k = 0; k < ln; k++) {
					Node pnode = na.item(k);
					if (matchRegx.size() > 0) {
						String nodeName = pnode.getNodeName();
						String nodeValue = pnode.getNodeValue();

						Iterator<Pair<String, String>> itr = matchRegx.iterator();

						while (itr.hasNext()) {
							Pair<String, String> str = itr.next();
							if (nodeName.equalsIgnoreCase(str.fst)) {
								Pattern pat = Pattern.compile(str.snd);
								Matcher publishMatcher = pat.matcher(nodeValue);
								if (publishMatcher.find()) {
									return oneNode;
								}
							}
						}
					}
				}
			}
		}
		return null;

	}

	private void travelTree(Node root, ArrayList<Node> allNodes) {
		if (root != null) {
			allNodes.add(root);
			NodeList children = root.getChildNodes();
			int childrenSize = children.getLength();
			for (int i = 0; i < childrenSize; i++) {
				Node child = children.item(i);
				travelTree(child, allNodes);
			}
		}
	}
}
