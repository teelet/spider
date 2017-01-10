package com.weibo.datasys.parser.html.data;

import com.weibo.datasys.common.conf.ConfigFactory;
import com.weibo.datasys.common.exception.ImageUrlException;
import com.weibo.datasys.common.exception.ObjectInContentException;
import com.weibo.datasys.common.util.MD5Util;
import com.weibo.datasys.common.util.StringUtils;
import com.weibo.datasys.parser.conf.ParseConfig;
import com.weibo.datasys.parser.html.antispam.FilterTag;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class BlockInfo {
	private static final Logger LOG = LoggerFactory.getLogger(BlockInfo.class);
	private BlockInfo parentBlockInfo = null;
	private ArrayList<BlockInfo> chidrenBlockList = new ArrayList<BlockInfo>();
	private Node node = null;
	private String nodeName = "";
	private short nodeType = 0;
	private NamedNodeMap attrs = null;
	private String allText = "";
	private String anchorText = "";
	private String noAnchorText = "";
	private boolean isSpecialTag = false;
	private double score = 100.0D;
	private int distance = 0;
	private boolean isMainTextBlock = false;
	private String imgUrl = "";
	private String imageMd5Id = "";
	private String currentData = "";
	private boolean hasPSblingImg = false;
	private boolean hasNSblingImg = false;
	private boolean isFormatTag = false;
	private boolean isFormatNode = false;
	private int isLastFormattedChild = 0;
	private int isLastPChild = 0;
	private boolean isFormatNode4Img = false;
	private boolean isImgTag = false;
	private boolean isImgNode = false;
	private HashMap<String, String> imageAttrs;
	private HashMap<String, String> TagAttrs;
	private boolean isPreviousPageNode = false;
	private String HrefMd5Id = "";

	private boolean isNextPageNode = false;
	private boolean isPreviousPageTag = false;
	private boolean isNextPageTag = false;
	private String Href = "";

	private String htmlUrl = "";

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public String getHrefMd52id() {
		return HrefMd5Id;
	}

	public boolean isPreviousPageNode() {
		return isPreviousPageNode;
	}

	public boolean isNextPageNode() {
		return isNextPageNode;
	}

	public boolean isPreviousPageTag() {
		return isPreviousPageTag;
	}

	public boolean isNextPageTag() {
		return isNextPageTag;
	}

	public String getHref() {
		return Href;
	}

	public void setHref(String href) {
		Href = href;
	}

	public HashMap<String, String> getTagAttrs() {
		return TagAttrs;
	}

	public void setTagAttrs(HashMap<String, String> tagAttrs) {
		TagAttrs = tagAttrs;
	}

	public int getLastFormattedChild() {
		return isLastFormattedChild;
	}

	public int getLastPChild() {
		return isLastPChild;
	}

	public void setLastFormattedChild(String nodeName) {
		if (this.chidrenBlockList == null || this.chidrenBlockList.size() == 0) {
			isLastFormattedChild += 1;
		} else {
			int len = this.chidrenBlockList.size();
			BlockInfo curBlockInfo = (BlockInfo) this.chidrenBlockList.get(len - 1);
			curBlockInfo.setLastFormattedChild(nodeName);
		}
	}

	public void setLastPChild(String nodeName) {
		if (this.chidrenBlockList == null || this.chidrenBlockList.size() == 0) {
			isLastPChild += 1;
		} else {
			int len = this.chidrenBlockList.size();
			BlockInfo curBlockInfo = (BlockInfo) this.chidrenBlockList.get(len - 1);
			curBlockInfo.setLastPChild(nodeName);
		}
	}

	public HashMap<String, String> getImageAttrs() {
		return imageAttrs;
	}

	public void setImageAttrs(HashMap<String, String> imageAttrs) {
		this.imageAttrs = imageAttrs;
	}

	public boolean isFormatNode() {
		return isFormatNode;
	}

	public void setFormatNode(boolean isFormatNode) {
		this.isFormatNode = isFormatNode;
	}

	public String getCurrentData() {
		return currentData;
	}

	public void setCurrentData(String currentData) {
		this.currentData = currentData;
	}

	public boolean HasNSblingImg() {
		return hasNSblingImg;
	}

	public void setNSblingImg(boolean hasNSblingImg) {
		this.hasNSblingImg = hasNSblingImg;
	}

	public boolean HasPSblingImg() {
		return hasPSblingImg;
	}

	public void setSblingImg(boolean hasPSblingImg) {
		this.hasPSblingImg = hasPSblingImg;
	}

	public boolean isFormatNode4Img() {
		return isFormatNode4Img;
	}

	public void setFormatNode4Img(boolean isFormatNode4Img) {
		this.isFormatNode4Img = isFormatNode4Img;
	}

	public boolean isFormatTag() {
		return isFormatTag;
	}

	public void setFormatTag(boolean isFormatTag) {
		this.isFormatTag = isFormatTag;
	}

	public String getImageMd5Id() {
		return this.imageMd5Id;
	}

	public void setImageMd5Id(String md5Id) {
		this.imageMd5Id = md5Id;
	}

	public boolean isImgNode() {
		return this.isImgNode;
	}

	public void setImgNode(boolean isImgNode) {
		this.isImgNode = isImgNode;
	}

	public boolean isImgTag() {
		return this.isImgTag;
	}

	public void setImgTag(boolean isImgTag) {
		this.isImgTag = isImgTag;
	}

	public String getImgUrl() {
		return this.imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public short getNodeType() {
		return nodeType;
	}

	public void setNodeType(short nodeType) {
		this.nodeType = nodeType;
	}

	public BlockInfo(Node node, String url) {
		if (node != null) {
			this.node = node;
			this.nodeName = node.getNodeName();
			this.nodeType = node.getNodeType();
			this.attrs = node.getAttributes();
			this.htmlUrl = url;
		}
	}

	public void setInfo2(ParseConfig parseConfig, int filter) throws Exception {
		StringBuffer tagBuffer = new StringBuffer();
		StringBuffer anchorBuffer = new StringBuffer();
		StringBuffer noAnchBuffer = new StringBuffer();

		// 如果在解析正文过程中发现有object对象（视频）,就不继续了
		if (filter == FilterTag.CONTENT) {
			if (FilterTag.isObjectTag(this.node.getNodeName())) {
				throw new ObjectInContentException(this.node.getNodeName());
			}
		}

		// 如果是分段节点，标示一下最后一个孩子需要加的分段标签的数量，在解析内容的时候回用到
		if (FilterTag.isSegTag(this.node.getNodeName())) {
			if (this.chidrenBlockList != null && this.chidrenBlockList.size() > 0) {
				BlockInfo curBlockInfo = (BlockInfo) this.chidrenBlockList.get(this.chidrenBlockList.size() - 1);
				curBlockInfo.setLastPChild(this.node.getNodeName());
			} else {
				this.isLastPChild += 1;
			}
		}

		if (FilterTag.isNeedStyleTag(this.node.getNodeName())) {
			NamedNodeMap nn = this.node.getAttributes();

			HashMap<String, String> attrs = new HashMap<String, String>();
			if (nn != null) {
				int len = nn.getLength();
				for (int j = 0; j < len; j++) {
					Node n = nn.item(j);
					if (n != null) {
						String name = n.getNodeName().toLowerCase();
						if (!(("class".equals(name) || "id".equals(name)))) {
							attrs.put(n.getNodeName(), n.getNodeValue());
						}
					}
				}
			}
			this.TagAttrs = attrs;

		}
		// 是否需要记录节点属性

		// 图片节点
		if (FilterTag.isimgTag(this.node.getNodeName())) {
			NamedNodeMap nn = this.node.getAttributes();
			HashMap<String, String> attrs = new HashMap<String, String>();
			int len = nn.getLength();
			boolean hasWantedTag = false;
			for (int j = 0; j < len; j++) {
				Node n = nn.item(j);
				if (FilterTag.isWantedImage(n.getNodeName())) {
					hasWantedTag = true;
					break;
				}
			}
			// 以上至是为了检查是否有特殊的src属性，有的网站图片地址不是src
			for (int j = 0; j < len; j++) {
				Node n = nn.item(j);
				if (FilterTag.isWantedImageStyle(n.getNodeName())) {
					attrs.put(n.getNodeName(), n.getNodeValue());
				}
				if (hasWantedTag) {
					if (FilterTag.isWantedImage(n.getNodeName())) {
						this.imgUrl = n.getNodeValue();
						// if
						// ("http://img2.ali213.net/picfile/News/2016/10/27/584_2016102721828294.jpg".equals(this.imgUrl))
						// System.out.println("aa");
						// 广告图片不要
						if (FilterTag.isInvalidImage(this.imgUrl))
							continue;
						if ((this.imgUrl == null) || (this.imgUrl.equals("")))
							continue;
						this.imageMd5Id = MD5Util.MD5(this.imgUrl);
						// 是否有图片节点的限制
						if (!(FilterTag.HasRootFilter4Img(node) || FilterTag.HasPSblingFilter4Img(node)
								|| FilterTag.HasNSblingFilter4Img(node))) {
							this.isImgTag = true;
							String localurl = this.imgUrl;
							if (filter == FilterTag.CONTENT) {
								localurl = change2LocalUrl(this.imgUrl, this.imageMd5Id);
							}
							if (!"".equals(localurl)) {
								tagBuffer.append(localurl);
							}
							if ((this.chidrenBlockList != null) && (this.chidrenBlockList.size() != 0)) {
								break;
							}
							this.isImgNode = true;
						}
					}
				} else {
					// 如果没有，就还用src就行
					if ("src".equalsIgnoreCase(n.getNodeName())) {
						this.imgUrl = n.getNodeValue();
						// 广告图片不要
						if (FilterTag.isInvalidImage(this.imgUrl))
							continue;
						if ((this.imgUrl == null) || (this.imgUrl.equals("")))
							continue;
						this.imageMd5Id = MD5Util.MD5(this.imgUrl);
						if (!(FilterTag.HasRootFilter4Img(node) || FilterTag.HasPSblingFilter4Img(node)
								|| FilterTag.HasNSblingFilter4Img(node))) {
							this.isImgTag = true;
							String localurl = this.imgUrl;
							if (filter == FilterTag.CONTENT) {
								localurl = change2LocalUrl(this.imgUrl, this.imageMd5Id);
							}
							if (!"".equals(localurl)) {
								tagBuffer.append(localurl);
							}
							if ((this.chidrenBlockList != null) && (this.chidrenBlockList.size() != 0)) {
								break;
							}
							this.isImgNode = true;
						}
					}
				}
			}
			// 保留需要的图片属性
			if (this.isImgNode)
				this.imageAttrs = attrs;
		}
		// 特殊格式节点
		if (FilterTag.isSpecialFormatNode(this.node.getNodeName())) {
			if (!(FilterTag.HasRootFilter4Format(this.node))) {
				if (this.chidrenBlockList != null && this.chidrenBlockList.size() > 0) {
					BlockInfo curBlockInfo = (BlockInfo) this.chidrenBlockList.get(this.chidrenBlockList.size() - 1);
					curBlockInfo.setLastFormattedChild(this.node.getNodeName());
					// 如果没儿子 应该之前就删掉了 所以也就没有用了
					// http://www.ali213.net/news/html/2016-10/257387_2.html
					this.isFormatNode = true;
					this.isFormatTag = true;
				}
				// 如果 this.node 的上一个兄弟节点是 img的话 该特使格式的文本内容需要居中
				Node sibling = this.node.getPreviousSibling();
				if (sibling != null) {
					if (FilterTag.isimgTag(sibling.getNodeName())) {
						this.isFormatNode4Img = true;
					}
				}
			}
		}

		if (this.chidrenBlockList != null) {
			for (int i = 0; i < this.chidrenBlockList.size(); i++) {
				BlockInfo curBlockInfo = (BlockInfo) this.chidrenBlockList.get(i);
				Node node = curBlockInfo != null ? curBlockInfo.getNode() : null;
				if (node != null) {
					if (node.getNodeType() == Node.TEXT_NODE) {
						String mText = node.getNodeValue();
						// if (mText.contains("上一页"))
						// System.out.println("aa");
						// if (mText.contains("下一页"))
						// System.out.println("aa");
						// 如果是上下页需要保存下来
						if (FilterTag.isPreviousPage(mText)) {
							this.isPreviousPageNode = true;
							this.isPreviousPageTag = true;

							if (FilterTag.isLinkTag(this.node.getNodeName())) {
								NamedNodeMap nn = this.node.getAttributes();
								int len = nn.getLength();
								for (int j = 0; j < len; j++) {
									Node n = nn.item(j);
									if ("href".equalsIgnoreCase(n.getNodeName())) {
										String url = n.getNodeValue();

										// 反斜杠替换为斜杠
										url = url.replaceAll("\\\\", "/");
										// 处理相对url
										// if (url.contains("257599.html"))
										// System.out.println("aaa");
										if (!url.matches("https?://.+")) {
											try {
												URI WebUrl = new URI(this.htmlUrl);
												url = WebUrl.resolve(url).toString();
											} catch (Exception e) {
												LOG.error("add host to url error: " + this.htmlUrl);
											}
										}
										this.Href = url;
										this.HrefMd5Id = MD5Util.MD5(this.Href);
									}
								}
							}
						}
						if (FilterTag.isNextPage(mText)) {
							this.isNextPageNode = true;
							this.isNextPageTag = true;
							if (FilterTag.isLinkTag(this.node.getNodeName())) {
								NamedNodeMap nn = this.node.getAttributes();
								int len = nn.getLength();
								for (int j = 0; j < len; j++) {
									Node n = nn.item(j);
									if ("href".equalsIgnoreCase(n.getNodeName())) {
										String url = n.getNodeValue();

										// 反斜杠替换为斜杠
										url = url.replaceAll("\\\\", "/");
										// 处理相对url
										// if (url.contains("257599.html"))
										// System.out.println("aaa");
										if (!url.matches("https?://.+")) {
											try {
												URI WebUrl = new URI(this.htmlUrl);
												url = WebUrl.resolve(url).toString();
											} catch (Exception e) {
												LOG.error("add host to url error: " + this.htmlUrl);
											}
										}
										this.Href = url;
										this.HrefMd5Id = MD5Util.MD5(this.Href);
									}
								}
							}
						}
						if (mText != "" && !FilterTag.isInvalidText(mText)) {
							tagBuffer.append(node.getNodeValue());
							Node parentNode = node.getParentNode();
							if ((FilterTag.isLinkTag(parentNode))
									|| ((parentNode != null) && (FilterTag.isLinkTag(parentNode.getParentNode())))) {
								anchorBuffer.append(node.getNodeValue());
							} else {
								noAnchBuffer.append(node.getNodeValue());
							}
						}
					} else {
						// 如果是上下页需要保存下来
						this.isPreviousPageTag = curBlockInfo.isPreviousPageTag;
						this.isNextPageTag = curBlockInfo.isNextPageTag;

						if (FilterTag.isPTag(node.getNodeName())) {
							this.isSpecialTag = true;
						}

						if (FilterTag.isimgTag(node.getNodeName())) {
							NamedNodeMap nn = node.getAttributes();
							int len = nn.getLength();
							boolean hasWantedTag = false;
							for (int j = 0; j < len; j++) {
								Node n = nn.item(j);
								if (FilterTag.isWantedImage(n.getNodeName())) {
									hasWantedTag = true;
									break;
								}
							}
							// 如下code只是检查是否是想要的图片节点，如果是 设置一下标志，防止后面删掉
							for (int j = 0; j < len; j++) {
								Node n = nn.item(j);
								if (hasWantedTag) {
									if (FilterTag.isWantedImage(n.getNodeName())) {
										String imageuUrl = n.getNodeValue();
										// 广告图片不要
										if (FilterTag.isInvalidImage(imageuUrl))
											continue;
										if (!(FilterTag.HasRootFilter4Img(node) || FilterTag.HasPSblingFilter4Img(node)
												|| FilterTag.HasNSblingFilter4Img(node)))
											this.isImgTag = true;

									}
								} else {
									if ("src".equalsIgnoreCase(n.getNodeName())) {
										String imageuUrl = n.getNodeValue();
										// 广告图片不要
										if (FilterTag.isInvalidImage(imageuUrl))
											continue;
										if (!(FilterTag.HasRootFilter4Img(node) || FilterTag.HasPSblingFilter4Img(node)
												|| FilterTag.HasNSblingFilter4Img(node)))
											this.isImgTag = true;
									}
								}
							}
						}
						// 同上
						if (FilterTag.isSpecialFormatNode(node.getNodeName())) {
							if (!(FilterTag.HasRootFilter4Format(node))) {

								this.isFormatTag = true;

							}
						}
						if (!(curBlockInfo.isImgTag())) {
							anchorBuffer.append(curBlockInfo.getAnchorText());
							noAnchBuffer.append(curBlockInfo.getNoAnchorText());

							tagBuffer.append(curBlockInfo.getAllText());
						}
					}
				}
			}
		}

		if (FilterTag.HasOnly1ImgChild(this.chidrenBlockList)) {
			BlockInfo curBlockInfo = (BlockInfo) this.chidrenBlockList.get(0);
			this.isImgTag = true;
			if (!(curBlockInfo.isImgTag())) {
				anchorBuffer.append(curBlockInfo.getAnchorText());
				noAnchBuffer.append(curBlockInfo.getNoAnchorText());

				tagBuffer.append(curBlockInfo.getAllText());
			}
		}

		if (this.node.getNodeType() == Node.TEXT_NODE) {
			String mText = this.node.getNodeValue();
			// if (mText.contains("关注熊猫TV官方微博"))
			// System.out.println("aa");
			if (mText != "" && !FilterTag.isInvalidText(mText)) {
				tagBuffer.append(this.node.getNodeValue());
			}
		}

		this.allText = tagBuffer.toString();
		this.anchorText = anchorBuffer.toString();
		this.noAnchorText = noAnchBuffer.toString();

	}

	public void addChildrenBlock(BlockInfo blockInfo) {
		this.chidrenBlockList.add(blockInfo);
	}

	public Node getAttrNode(String name) {
		if (name == null) {
			return null;
		}
		if ((this.attrs == null) && (this.node != null)) {
			this.attrs = this.node.getAttributes();
		}
		if (this.attrs != null) {
			return this.attrs.getNamedItem(name);
		}
		return null;
	}

	public String getAttrNodeValue(String name) {
		Node attr = getAttrNode(name);
		String result = null;
		if (attr != null) {
			result = attr.getNodeValue();
		}
		return result == null ? "" : result;
	}

	public BlockInfo getParentBlockInfo() {
		return this.parentBlockInfo;
	}

	public void setParentBlock(BlockInfo parentBlockInfo) {
		this.parentBlockInfo = parentBlockInfo;
	}

	public ArrayList<BlockInfo> getChidrenBlockList() {
		return this.chidrenBlockList;
	}

	public Node getNode() {
		return this.node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	public String getAllText() {
		return this.allText == null ? "" : this.allText;
	}

	public void setAllText(String allText) {
		this.allText = allText;
	}

	public String getAnchorText() {
		return this.anchorText == null ? "" : this.anchorText;
	}

	public void setAnchorText(String anchorText) {
		this.anchorText = anchorText;
	}

	public boolean isSpecialTag() {
		return this.isSpecialTag;
	}

	public void setSpecialTag(boolean isSpecialTag) {
		this.isSpecialTag = isSpecialTag;
	}

	public boolean isMainTextBlock() {
		return this.isMainTextBlock;
	}

	public void setMainTextBlock(boolean isMainTextBlock) {
		this.isMainTextBlock = isMainTextBlock;
	}

	public double getScore() {
		return this.score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getDistance() {
		return this.distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getNoAnchorText() {
		return this.noAnchorText;
	}

	public void setNoAnchorText(String noAnchorText) {
		this.noAnchorText = noAnchorText;
	}

	public String change2LocalUrl(String imgUrl, String md5id) throws ImageUrlException {

		try {
			new URL(imgUrl);
		} catch (MalformedURLException e) {
			throw new ImageUrlException(e.getMessage());
		}

		SimpleDateFormat dirFormat = new SimpleDateFormat("yyyyMMdd");
		this.currentData = dirFormat.format(new Date());
		String suffix = imgUrl.substring(imgUrl.lastIndexOf("."));
		int subDirCount = 2;
		int subDirLength = 10;

		String relativePath = StringUtils.md5ToPath(StringUtils.getMD5Code(imgUrl.trim()), subDirCount, subDirLength);

		relativePath = this.currentData + "/" + md5id + suffix;
		relativePath = "http://" + ConfigFactory.getString("imageHost", "localhost") + "/" + relativePath;

		return relativePath;
	}

}
