/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.conf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.sun.tools.javac.util.Pair;
import com.weibo.datasys.common.rule.BlockRuleInfo;
import com.weibo.datasys.common.rule.FontRuleInfo;
import com.weibo.datasys.common.rule.ImgRuleInfo;
import com.weibo.datasys.common.rule.ParseStrategy;

public class ParseConfig {

	/**
	 * 正文起始位置
	 */
	private Set<Pair<String,Pair<String, String>>> MainTextStart = new HashSet<Pair<String,Pair<String, String>>>();
	
	/**
	 * title起始位置
	 */
	private Set<Pair<String,Pair<String, String>>> titleBlock = new HashSet<Pair<String,Pair<String, String>>>();
	
	/**
	 * url host 名字
	 */
	private String host = "";

	/**
	 * 噪音文本
	 */
	private String blockNoiseStr = "信息来源:;首页 >;首页>;>>本网快讯;新闻中心>>;热门评论;所有评论;我也来说两句;阅读并接受上述条款;"
			+ "如您对管理有意见请向新闻跟帖管理员反映;未经本网授权，不得转载;与本网站立场无关;" + "联系方式;版权所有;免责声明;免责申明;诚聘英才;联系我们;广告服务;"
			+ "copyright;all rights reserved;©;网站无关;新闻字号;进入论坛;查看文章评论;"
			+ "输入您的搜索字词;返回首页;发表评论;匿名发表;我要说说;相关报道;网友评论;打印本页;推荐给好友;"
			+ "返回首页;关闭;打印;保存;留言;手机点评;纠错;手机看新闻;文章摘要;加入 4399;微信订阅号;" 
			+ "更多的曝光资讯;火热的资讯;更多惊喜;独家礼包;更多精彩内容;最新相关内容;相关文章阅读;更多相关资讯;更多相关讨论;热门搜索;相关推荐;相关标签";
	private String[] blockNoiseStrs;

	/**
	 * 目录页url特征
	 */
	private String[] ThemeNosieStrs = { "index.htm", "index.html", "index.asp", "default.aspx", "index.php",
			"index.jsp" };

	/**
	 * 中文常用标点
	 */
	private String[] chndots = { "。", "？", "！", "?", "!" };

	/**
	 * 中文不合法标点
	 */
	private String[] invalidChndots = { "。。。" };

	/**
	 * 其它语言常用标点
	 */
	private String[] foreigndots = { ".", "?", "!" };

	/**
	 * 其它语言不合法标点
	 */
	private String[] invalidForeigndotsStrings = { "www.", ".com", ".org", ".net", ".cn", ".html", ".net", ".aspx",
			"Ltd.", "bbl.", "Co.", "toz.", "lb.", "bu.", "1.", "2.", "3.", "4.", "5.", "6.", "7.", "8.", "9.", "0.",
			"...", ".shtm", ".htm" };

	/**
	 * 不合法节点名
	 */
	private String[] invalidNodes = { "script", "style", "textarea", "option", "iframe", "marquee", "comment",
			"select"};
	// private HashSet<String> invalidNodesSet = new HashSet<String>();

	/**
	 * 不合法节点id属性值
	 */
	private String[] invalidIdValue = { "foot", "footer", "ad", "下载" };
	// private HashSet<String> invalidIdValuesSet = new HashSet<String>();

	/**
	 * 不合法节点class属性值
	 */
	private String[] invalidClassValue = { "ad_text" };
	// private HashSet<String> invalidClassValuesSet = new HashSet<String>();

	/**
	 * 块节点
	 */
	private String[] blockManagerNodes = { "li", "fieldset", "center", "dl", "dt", "table", "td", };
	// private HashSet<String> blockManagerSet = new HashSet<String>();

	/**
	 * 特殊p节点
	 */
	private String[] pNodes = { "p", "pre" };
	// private HashSet<String> pSet = new HashSet<String>();

	/**
	 * 图片节点
	 */
	private String[] imgNodes = { "img" };
	// private HashSet<String> imgSet = new HashSet<String>();

	/**
	 * 图片节点的过滤条件1(祖先节点是如下的不要)
	 */
	private String[] imgRootFilter = {"a"};
	// private HashSet<String> imgRootFilterSet = new HashSet<String>();

	/**
	 * 图片节点的过滤条件2(前兄弟节点是如下的不要)
	 */
	private String[] imgPSiblingFilter = {"a"};
	// private HashSet<String> imgPSiblingFilterSet = new HashSet<String>();

	/**
	 * 图片节点的过滤条件2(前兄弟节点是如下的不要)
	 */
	private String[] imgNSiblingFilter = {"a"};
	// private HashSet<String> imgNSiblingFilterSet = new HashSet<String>();

	/**
	 * 链接节点
	 */
	private String[] linkNodes = { "a" };
	// private HashSet<String> linkSet = new HashSet<String>();

	/**
	 * 不做处理的节点
	 */
	private String[] preserveNodes = { "title", "meta", "body", "html", "head", "br" };
	// private HashSet<String> preserveNodesSet = new HashSet<String>();

	/**
	 * 分段节点
	 */
	private String[] segNodes = { "div", "p"};
	// private HashSet<String> segNodesSet = new HashSet<String>();

	/**
	 * 需要保留的特殊格式节点
	 */
	private String[] speciaFormatlNode = { "strong", "span", "font" };
	// private HashSet<String> speciaFormatlNodesSet = new HashSet<String>();

	/**
	 * 特殊格式节点过滤条件（祖先是如下不要）
	 */
	private String[] formatRootFilter = {};
	// private HashSet<String> formatRootFilterSet = new HashSet<String>();

	private String[] imageTag = {"src"};
	/**
	 * 
	 * 图片格式格式
	 */
	private String[] imageStyle = {"style","width","height","border"};
	
	/**
	 * 是否需要保留格式几点
	 */
	
	private String[] needStyleTag = {"p","font","span","div"};
	

	private String[] invalidImages = {};
	private String[] invalidText = {};
	private String[] invalidHrefText = {};
	private Pattern originalPattern = null;
	private Pattern authorPattern = null;
	
	private String[] previousPage = {};
	private String[] nextPage = {};
	
	public Set<Pair<String, Pair<String, String>>> getTitleBlock() {
		return titleBlock;
	}

	public void setTitleBlock(Set<Pair<String, Pair<String, String>>> titleBlock) {
		this.titleBlock = titleBlock;
	}

	public String[] getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(String[] previousPage) {
		this.previousPage = previousPage;
	}

	public String[] getNextPage() {
		return nextPage;
	}

	public void setNextPage(String[] nextPage) {
		this.nextPage = nextPage;
	}

	public String[] getInvalidHrefText() {
		return invalidHrefText;
	}
	
	public String[] getNeedStyleTag() {
		return needStyleTag;
	}

	public void setNeedStyleTag(String[] needStyleTag) {
		this.needStyleTag = needStyleTag;
	}
	
	public String[] getImageStyle() {
		return imageStyle;
	}

	public void setImageStyle(String[] imageStyle) {
		this.imageStyle = imageStyle;
	}

	public String[] getImageTag() {
		return imageTag;
	}

	public void setImageTag(String[] imageTag) {
		this.imageTag = imageTag;
	}

	public ParseConfig() {
		blockNoiseStrs = blockNoiseStr.split(";");

	}

	public Set<Pair<String,Pair<String, String>>> getMainTextStart() {
		return MainTextStart;
	}

	public void setMainTextStart(Set<Pair<String,Pair<String, String>>> mainTextStart) {
		this.MainTextStart = mainTextStart;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String[] getBlockNoiseStrs() {
		return blockNoiseStrs;
	}

	public void setBlockNoiseStrs(String[] blockNoiseStrs) {
		this.blockNoiseStrs = blockNoiseStrs;
	}

	public String[] getThemeNosieStrs() {
		return ThemeNosieStrs;
	}

	public void setThemeNosieStrs(String[] themeNosieStrs) {
		ThemeNosieStrs = themeNosieStrs;
	}

	public String[] getChndots() {
		return chndots;
	}

	public void setChndots(String[] chndots) {
		this.chndots = chndots;
	}

	public String[] getInvalidChndots() {
		return invalidChndots;
	}

	public void setInvalidChndots(String[] invalidChndots) {
		this.invalidChndots = invalidChndots;
	}

	public String[] getForeigndots() {
		return foreigndots;
	}

	public void setForeigndots(String[] foreigndots) {
		this.foreigndots = foreigndots;
	}

	public String[] getInvalidForeigndotsStrings() {
		return invalidForeigndotsStrings;
	}

	public void setInvalidForeigndotsStrings(String[] invalidForeigndotsStrings) {
		this.invalidForeigndotsStrings = invalidForeigndotsStrings;
	}

	public String[] getInvalidNodes() {
		return invalidNodes;
	}

	public void setInvalidNodes(String[] invalidNodes) {
		this.invalidNodes = invalidNodes;
	}

	public String[] getInvalidIdValue() {
		return invalidIdValue;
	}

	public void setInvalidIdValue(String[] invalidIdValue) {
		this.invalidIdValue = invalidIdValue;
	}

	public String[] getInvalidClassValue() {
		return invalidClassValue;
	}

	public void setInvalidClassValue(String[] invalidClassValue) {
		this.invalidClassValue = invalidClassValue;
	}

	public String[] getBlockManagerNodes() {
		return blockManagerNodes;
	}

	public void setBlockManagerNodes(String[] blockManagerNodes) {
		this.blockManagerNodes = blockManagerNodes;
	}

	public String[] getpNodes() {
		return pNodes;
	}

	public void setpNodes(String[] pNodes) {
		this.pNodes = pNodes;
	}

	public String[] getImgNodes() {
		return imgNodes;
	}

	public void setImgNodes(String[] imgNodes) {
		this.imgNodes = imgNodes;
	}

	public String[] getLinkNodes() {
		return linkNodes;
	}

	public void setLinkNodes(String[] linkNodes) {
		this.linkNodes = linkNodes;
	}

	public String[] getPreserveNodes() {
		return preserveNodes;
	}

	public void setPreserveNodes(String[] preserveNodes) {
		this.preserveNodes = preserveNodes;
	}

	public String[] getSegNodes() {
		return segNodes;
	}

	public void setSegNodes(String[] segNodes) {
		this.segNodes = segNodes;
	}

	public String[] getSpeciaFormatlNode() {
		return speciaFormatlNode;
	}

	public void setSpeciaFormatlNode(String[] speciaFormatlNode) {
		this.speciaFormatlNode = speciaFormatlNode;
	}

	public String[] getImgRootFilter() {
		return imgRootFilter;
	}

	public void setImgRootFilter(String[] imgRootFilter) {
		this.imgRootFilter = imgRootFilter;
	}

	public String[] getImgPSiblingFilter() {
		return imgPSiblingFilter;
	}

	public void setImgPSiblingFilter(String[] imgPSiblingFilter) {
		this.imgPSiblingFilter = imgPSiblingFilter;
	}

	public String[] getImgNSiblingFilter() {
		return imgNSiblingFilter;
	}

	public void setImgNSiblingFilter(String[] imgNSiblingFilter) {
		this.imgNSiblingFilter = imgNSiblingFilter;
	}

	public String[] getFormatRootFilter() {
		return formatRootFilter;
	}

	public void setFormatRootFilter(String[] formatRootFilter) {
		this.formatRootFilter = formatRootFilter;
	}

	public String getBlockNoiseStr() {
		return blockNoiseStr;
	}

	public String[] getInvalidImages() {
		return invalidImages;
	}

	public String[] getInvalidText() {
		return invalidText;
	}

	public void init(String host) {
		Set<Pair<String, Pair<String, String>>> startPos = ParseStrategy.getStartPosition(host);
		if (startPos != null){
			this.MainTextStart.clear();
			Iterator<Pair<String, Pair<String, String>>> itr = startPos.iterator();
			while (itr.hasNext()) {
				Pair<String, Pair<String, String>> onePair = itr.next();
				this.MainTextStart.add(onePair);
			}
		}
		
		Set<Pair<String, Pair<String, String>>> titleBlock = ParseStrategy.getTitleBlock(host);
		if (titleBlock != null){
			this.titleBlock.clear();
			Iterator<Pair<String, Pair<String, String>>> itr = titleBlock.iterator();
			while (itr.hasNext()) {
				Pair<String, Pair<String, String>> onePair = itr.next();
				this.titleBlock.add(onePair);
			}
		}
		
		Set<Pair<String, String>> invalidElment = ParseStrategy.getInvalidElements(host);
		if (invalidElment != null) {
			ArrayList<String> idElment = new ArrayList<String>();
			ArrayList<String> classElment = new ArrayList<String>();

			Iterator<Pair<String, String>> itr = invalidElment.iterator();
			while (itr.hasNext()) {
				Pair<String, String> onePair = itr.next();
				String elmentName = onePair.fst;
				if ("id".equalsIgnoreCase(elmentName))
					idElment.add(onePair.snd);
				if ("class".equalsIgnoreCase(elmentName))
					classElment.add(onePair.snd);
			}
			if (idElment.size() > 0) {
				int size = idElment.size();
				this.invalidIdValue = (String[]) idElment.toArray(new String[size]);
			}
			if (classElment.size() > 0) {
				int size = classElment.size();
				this.invalidClassValue = (String[]) classElment.toArray(new String[size]);
			}
		}

		BlockRuleInfo blockRuleInfo = ParseStrategy.getBlockRule(host);
		if (blockRuleInfo == null){
			blockRuleInfo = ParseStrategy.getBlockRule("default");
		}
		if (blockRuleInfo != null) {
			String[] valid = blockRuleInfo.getValid();
			String[] invalid = blockRuleInfo.getInvalid();
			String[] preserved = blockRuleInfo.getPreserved();
			String[] link = blockRuleInfo.getLink();
			String[] img = blockRuleInfo.getImg();
			String[] font = blockRuleInfo.getFont();
			// 对于不合法节点名
			if (invalid != null) {

				this.invalidNodes = invalid;
			}

			// 对于分段节点
			if (valid != null) {

				this.segNodes = valid;
			}
			// 对应不做处理的节点
			if (preserved != null) {
				this.preserveNodes = preserved;
			}
			// 对应链接节点
			if (link != null) {
				this.linkNodes = link;
			}

			// 对应图片节点
			if (img != null) {
				this.imgNodes = img;
			}
			// 对应特殊格式节点
			if (font != null) {
				this.speciaFormatlNode = font;
			}

		}
		ImgRuleInfo imgRuleInfo = ParseStrategy.getImgRule(host);
		FontRuleInfo fontRuleInfo = ParseStrategy.getFontRule(host);
		if (imgRuleInfo != null) {
			String[] rootFilter = imgRuleInfo.getFilterRoot();
			String[] PSiblingFilter = imgRuleInfo.getFilterPSibling();
			String[] NSiblingFilter = imgRuleInfo.getFilterNSibling();
			String[] imageTag = imgRuleInfo.getImageTag();
			if (rootFilter != null) {
				this.imgRootFilter = rootFilter;
			}
			if (PSiblingFilter != null) {
				this.imgPSiblingFilter = PSiblingFilter;
			}
			if (NSiblingFilter != null) {
				this.imgNSiblingFilter = NSiblingFilter;

			}
			if (imageTag != null){
				this.imageTag = imageTag;
			}
		}
		if (fontRuleInfo != null) {
			String[] filterRoot = fontRuleInfo.getFilterRoot();
			if (filterRoot != null) {
				this.formatRootFilter = filterRoot;
			}
		}
		
		Set<String> invalidImage = ParseStrategy.getInvalidImage(host);

		if (invalidImage != null) {
			this.invalidImages = (String[]) invalidImage.toArray(new String[invalidImage.size()]);
		}
		
		Set<String> invalidText = ParseStrategy.getInvalidText(host);

		if (invalidText != null) {
			this.invalidText = (String[]) invalidText.toArray(new String[invalidText.size()]);
		}
		
		Set<String> invalidHrefText = ParseStrategy.getInvalidHrefText(host);

		if (invalidHrefText != null) {
			this.invalidHrefText = (String[]) invalidHrefText.toArray(new String[invalidHrefText.size()]);
		}
		
		Map<String,Pattern> regex = ParseStrategy.getPattern(host);
		if (regex != null){
			if (regex.containsKey("author"))
				this.authorPattern = regex.get("author");
			if (regex.containsKey("original"))
				this.originalPattern = regex.get("original");
		}
		
		Map<String,Set<String>> res = ParseStrategy.getContinuesPage(host);
		if (res != null){
			if (res.containsKey("up")){
				Set<String> tmpSet = res.get("up");
				this.previousPage = (String[]) tmpSet.toArray(new String[tmpSet.size()]);
			}
			if (res.containsKey("down")){
				Set<String> tmpSet = res.get("down");
				this.nextPage = (String[]) tmpSet.toArray(new String[tmpSet.size()]);
			}
		}	
	}

	public Pattern getOriginalPattern() {
		return originalPattern;
	}

	public void setOriginalPattern(Pattern originalPattern) {
		this.originalPattern = originalPattern;
	}

	public Pattern getAuthorPattern() {
		return authorPattern;
	}

	public void setAuthorPattern(Pattern authorPattern) {
		this.authorPattern = authorPattern;
	}
}