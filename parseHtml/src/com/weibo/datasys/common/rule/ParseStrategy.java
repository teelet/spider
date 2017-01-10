package com.weibo.datasys.common.rule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.tools.javac.util.Pair;
import com.weibo.datasys.common.rule.BlockRuleInfo;
import com.weibo.datasys.common.rule.FontRuleInfo;
import com.weibo.datasys.common.rule.ImgRuleInfo;


public class ParseStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(ParseStrategy.class);
	private static Map<String, Set<Pair<String, Pair<String, String>>>> startPositionMap = new HashMap<String, Set<Pair<String, Pair<String, String>>>>();
	private static Map<String, Set<Pair<String, String>>> invalidElementsMap = new HashMap<String, Set<Pair<String, String>>>();
	private static Map<String, BlockRuleInfo> blockRuleMap = new HashMap<String, BlockRuleInfo>();
	private static Map<String, ImgRuleInfo> imgRuleMap = new HashMap<String, ImgRuleInfo>();
	private static Map<String, FontRuleInfo> fontRuleMap = new HashMap<String, FontRuleInfo>();
	private static Map<String, Set<String>> invalidImageMap = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> invalidTextMap = new HashMap<String, Set<String>>();
	private static Map<String, Set<String>> invalidHrefTextMap = new HashMap<String, Set<String>>();
	private static Map<String,Map<String,Pattern>> RegexPattern = new HashMap<String, Map<String,Pattern>>();
	private static Map<String,Map<String,Set<String>>> continuesPage = new HashMap<String, Map<String,Set<String>>>();
	
	private static Map<String, Set<Pair<String, Pair<String, String>>>> titleBlockMap = new HashMap<String, Set<Pair<String, Pair<String, String>>>>();

	
	public static Map<String,Set<String>> getContinuesPage(String host) {
		Map<String,Set<String>> result = null;
		if (null != host) {
			result = continuesPage.get(host);
		}
		return result;
	}
	
	public static Set<String> getInvalidImage(String host) {
		Set<String> result = null;
		if (null != host) {
			result = invalidImageMap.get(host);
		}
		return result;
	}



	public static Set<String> getInvalidText(String host) {
		Set<String> result = null;
		if (null != host) {
			result = invalidTextMap.get(host);
		}
		return result;
	}
	
	
	public static Set<String> getInvalidHrefText(String host) {
		Set<String> result = null;
		if (null != host) {
			result = invalidHrefTextMap.get(host);
		}
		return result;
	}

	private static final String DEFAULT_HOST = "default";

	@SuppressWarnings("unchecked")
	public static void init(String strategyFile) {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(strategyFile));
			String tmp = "";
			String xml = "";
			while (null != (tmp = reader.readLine())) {
				xml += tmp + "\n";
			}
			// 构建xml dom
			Document doc = DocumentHelper.parseText(xml);
			// 获取内容开始位置
			String startPositionFile = doc.selectSingleNode("//ParseStrategy/startPosition").getText().trim();
			readStartPosition(startPositionFile, startPositionMap);
			// 获取非法属性的element节点
			String invalidElementFile = doc.selectSingleNode("//ParseStrategy/invalidElement").getText().trim();
			readInvalidElements(invalidElementFile, invalidElementsMap);
			// 获取非法属性的图片节点
			String invalidImageFile = doc.selectSingleNode("//ParseStrategy/invalidImage").getText().trim();
			readInvalidImages(invalidImageFile, invalidImageMap);
			// 获取非法属性的内容节点
			String invalidTextFile = doc.selectSingleNode("//ParseStrategy/invalidText").getText().trim();
			readInvalidText(invalidTextFile, invalidTextMap);
			// 获取非法的超链接内容
			String invalidHrefTextFile = doc.selectSingleNode("//ParseStrategy/invalidHrefText").getText().trim();
			readInvalidText(invalidHrefTextFile, invalidHrefTextMap);
			// 获取上下页内容
			String continuesPageFile = doc.selectSingleNode("//ParseStrategy/continuesPage").getText().trim();
			readContinuesPage(continuesPageFile, continuesPage);
			
			// 读取blockrule规则
			List<Node> blockRuleList = doc.selectNodes("//ParseStrategy/blockRule/hostPair");
			readBlockRule(blockRuleList, blockRuleMap);
			// 读取imgrule规则
			List<Node> imgRuleList = doc.selectNodes("//ParseStrategy/imgRule/hostPair");
			readImgRule(imgRuleList, imgRuleMap);
			// 读取fontrule规则
			List<Node> fontRuleList = doc.selectNodes("//ParseStrategy/fontRule/hostPair");
			readFontRule(fontRuleList, fontRuleMap);
			
			List<Node> RegexRuleList = doc.selectNodes("//ParseStrategy/RegexRule/hostPair");
			readRegexRule(RegexRuleList, RegexPattern);

			String titleBlockFile = doc.selectSingleNode("//ParseStrategy/titleBlcok").getText().trim();
			readStartPosition(titleBlockFile, titleBlockMap);
			
		} catch (Exception e) {
			System.out.println(new Date() + " - [FatalError] - Init Strategy Error. System Exit.");
			System.exit(1);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取配置文件
	 * 
	 * @param configFile
	 * @param configMap
	 */
	private static void readStartPosition(String configFile, Map<String, Set<Pair<String, Pair<String, String>>>> configMap) {

		String home = System.getProperty("home.dir");

		if (null == home) {
			LOG.error("-Dhome.dir properties not found.");
			return;
		}

		BufferedReader configReader = null;
		try {
			configReader = new BufferedReader(new FileReader(home + "/" + configFile));
			String line = null;
			while (null != (line = configReader.readLine())) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] fields = line.split(",");
				if (fields.length != 4) {
					continue;
				}
				String host = fields[0].trim().toLowerCase();
				String attributeName = fields[1].trim();
				String attributeValue = fields[2].trim();
				String isRegx = fields[3].trim();
				if (host == "" || attributeName == "" || attributeValue == "") {
					continue;
				}
				Set<Pair<String, Pair<String, String>>> configSet = configMap.get(host);
				if (null == configSet) {
					configSet = new HashSet<Pair<String, Pair<String, String>>>();
					configMap.put(host, configSet);
				}
				Pair<String, String> attributePair = new Pair<String, String>(attributeName, attributeValue);
				Pair<String,Pair<String, String>> onePair = new Pair<String, Pair<String, String>>(isRegx,attributePair);
				configSet.add(onePair);
			}

		} catch (Exception e) {
			System.out.println(new Date() + " - [FatalError] - Init Strategy Error. System Exit.");
			System.exit(1);
		} finally {
			if (null != configReader) {
				try {
					configReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 读取配置文件
	 * 
	 * @param configFile
	 * @param configMap
	 */
	private static void readInvalidElements(String configFile, Map<String, Set<Pair<String, String>>> configMap) {

		String home = System.getProperty("home.dir");

		if (null == home) {
			LOG.error("-Dhome.dir properties not found.");
			return;
		}

		BufferedReader configReader = null;
		try {
			configReader = new BufferedReader(new FileReader(home + "/" + configFile));
			String line = null;
			while (null != (line = configReader.readLine())) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] fields = line.split(",");
				if (fields.length != 3) {
					continue;
				}
				String host = fields[0].trim().toLowerCase();
				String attributeName = fields[1].trim().toLowerCase();
				String attributeValue = fields[2].trim().toLowerCase();
				if (host == "" || attributeName == "" || attributeValue == "") {
					continue;
				}
				Set<Pair<String, String>> configSet = configMap.get(host);
				if (null == configSet) {
					configSet = new HashSet<Pair<String, String>>();
					configMap.put(host, configSet);
				}
				Pair<String, String> attributePair = new Pair<String, String>(attributeName, attributeValue);
				configSet.add(attributePair);
			}
		} catch (Exception e) {
			System.out.println(new Date() + " - [FatalError] - Init Strategy Error. System Exit.");
			System.exit(1);
		} finally {
			if (null != configReader) {
				try {
					configReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	
	/**
	 * 读取配置文件
	 * 
	 * @param configFile
	 * @param configMap
	 */
	private static void readInvalidText(String configFile, Map<String, Set<String>> configMap) {

		String home = System.getProperty("home.dir");

		if (null == home) {
			LOG.error("-Dhome.dir properties not found.");
			return;
		}

		BufferedReader configReader = null;
		try {
			configReader = new BufferedReader(new FileReader(home + "/" + configFile));
			String line = null;
			while (null != (line = configReader.readLine())) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] fields = line.split(",");
				if (fields.length != 2) {
					continue;
				}
				String host = fields[0].trim();
				String text = fields[1].trim();
		
				if (host == "" || text == "") {
					continue;
				}
				Set<String> configSet = configMap.get(host);
				if (null == configSet) {
					configSet = new HashSet<String>();
					configMap.put(host, configSet);
				}
				
				configSet.add(text);
			}
		} catch (Exception e) {
			System.out.println(new Date() + " - [FatalError] - Init Strategy Error. System Exit.");
			System.exit(1);
		} finally {
			if (null != configReader) {
				try {
					configReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	
	/**
	 * 读取配置文件
	 * 
	 * @param configFile
	 * @param configMap
	 */
	private static void readContinuesPage(String configFile, Map<String, Map<String,Set<String>>> configMap) {

		String home = System.getProperty("home.dir");

		if (null == home) {
			LOG.error("-Dhome.dir properties not found.");
			return;
		}

		BufferedReader configReader = null;
		try {
			configReader = new BufferedReader(new FileReader(home + "/" + configFile));
			String line = null;
			while (null != (line = configReader.readLine())) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] fields = line.split(",");
				if (fields.length != 3) {
					continue;
				}
				String host = fields[0].trim();
				String uOrd = fields[1].trim();
				String text = fields[2].trim();
		
				if (host == "" || text == "" || uOrd == "") {
					continue;
				}
				Map<String,Set<String>> configSet = configMap.get(host);
				if (null == configSet) {
					configSet = new HashMap<String,Set<String>>();
					configMap.put(host, configSet);
				}
				Set<String> tmpSet = configSet.get(uOrd);
				if (null == tmpSet){
					tmpSet = new HashSet<String>();
					configSet.put(uOrd, tmpSet);
				}
				tmpSet.add(text);
			}
		} catch (Exception e) {
			System.out.println(new Date() + " - [FatalError] - Init Strategy Error. System Exit.");
			System.exit(1);
		} finally {
			if (null != configReader) {
				try {
					configReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
	
	
	/**
	 * 读取配置文件
	 * 
	 * @param configFile
	 * @param configMap
	 */
	private static void readInvalidImages(String configFile, Map<String, Set<String>> configMap) {

		String home = System.getProperty("home.dir");

		if (null == home) {
			LOG.error("-Dhome.dir properties not found.");
			return;
		}

		BufferedReader configReader = null;
		try {
			configReader = new BufferedReader(new FileReader(home + "/" + configFile));
			String line = null;
			while (null != (line = configReader.readLine())) {
				if (line.startsWith("#")) {
					continue;
				}
				String[] fields = line.split(",");
				if (fields.length != 2) {
					continue;
				}
				String host = fields[0].trim().toLowerCase();
				String Image = fields[1].trim().toLowerCase();
	
				if (host == "" || Image == "") {
					continue;
				}
				Set<String> configSet = configMap.get(host);
				if (null == configSet) {
					configSet = new HashSet<String>();
					configMap.put(host, configSet);
				}
				configSet.add(Image);
			}
		} catch (Exception e) {
			System.out.println(new Date() + " - [FatalError] - Init Strategy Error. System Exit.");
			System.exit(1);
		} finally {
			if (null != configReader) {
				try {
					configReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 读取blockrule配置
	 * 
	 * @param nodeList
	 * @param configMap
	 */
	private static void readBlockRule(List<Node> nodeList, Map<String, BlockRuleInfo> configMap) {
		if (null == nodeList || nodeList.size() == 0 || null == configMap) {
			return;
		}
		for (Node node : nodeList) {
			String host = node.selectSingleNode("host").getText().trim().toLowerCase();
			BlockRuleInfo blockRule = new BlockRuleInfo();
			Node validText = node.selectSingleNode("valid");
			if (validText != null) {

				String[] valid = validText.getText().trim().toLowerCase().split(",");
				blockRule.setValid(valid);
			}

			Node invalidText = node.selectSingleNode("invalid");
			if (invalidText != null) {

				String[] invalid = invalidText.getText().trim().toLowerCase().split(",");
				blockRule.setInvalid(invalid);
			}
			Node preservedText = node.selectSingleNode("preserved");
			if (preservedText != null) {

				String[] preserved = preservedText.getText().trim().toLowerCase().split(",");
				blockRule.setPreserved(preserved);
			}

			Node linkText = node.selectSingleNode("link");
			if (linkText != null) {

				String[] link = linkText.getText().trim().toLowerCase().split(",");
				blockRule.setLink(link);
			}
			Node imgText = node.selectSingleNode("img");
			if (imgText != null) {
				String[] img = imgText.getText().trim().toLowerCase().split(",");
				blockRule.setImg(img);
			}
			Node fontText = node.selectSingleNode("font");
			if (fontText != null) {
				String[] font = fontText.getText().trim().toLowerCase().split(",");
				blockRule.setFont(font);
			}
			configMap.put(host, blockRule);
		}
	}

	/**
	 * 读取imgrule配置
	 * 
	 * @param nodeList
	 * @param configMap
	 */
	private static void readImgRule(List<Node> nodeList, Map<String, ImgRuleInfo> configMap) {
		if (null == nodeList || nodeList.size() == 0 || null == configMap) {
			return;
		}
		for (Node node : nodeList) {
			String host = node.selectSingleNode("host").getText().trim().toLowerCase();
			ImgRuleInfo imgRule = new ImgRuleInfo();

			Node filterRootText = node.selectSingleNode("filterRoot");
			if (filterRootText != null) {

				String[] filterRoot = filterRootText.getText().trim().toLowerCase().split(",");
				imgRule.setFilterRoot(filterRoot);
			}
			Node filterPSiblingText = node.selectSingleNode("filterPSibling");
			if (filterPSiblingText != null) {
				String[] filterPSibling = filterPSiblingText.getText().trim().toLowerCase().split(",");
				imgRule.setFilterPSibling(filterPSibling);
			}
			Node filterNSiblingText = node.selectSingleNode("filterNSibling");
			if (filterNSiblingText != null) {
				String[] filterNSibling = filterNSiblingText.getText().trim().toLowerCase().split(",");
				imgRule.setFilterNSibling(filterNSibling);
			}
			Node imageTagText = node.selectSingleNode("imageTag");
			if (imageTagText != null) {
				String[] imageTag = imageTagText.getText().trim().toLowerCase().split(",");
				imgRule.setImageTag(imageTag);
			}

			configMap.put(host, imgRule);
		}
	}

	/**
	 * 读取fontrule配置
	 * 
	 * @param nodeList
	 * @param configMap
	 */
	private static void readFontRule(List<Node> nodeList, Map<String, FontRuleInfo> configMap) {
		if (null == nodeList || nodeList.size() == 0 || null == configMap) {
			return;
		}
		for (Node node : nodeList) {
			String host = node.selectSingleNode("host").getText().trim().toLowerCase();
			FontRuleInfo fontRule = new FontRuleInfo();
			Node filterRootText = node.selectSingleNode("filterRoot");
			if (filterRootText != null) {

				String[] filterRoot = filterRootText.getText().trim().toLowerCase().split(",");
				fontRule.setFilterRoot(filterRoot);
			}
			configMap.put(host, fontRule);
		}
	}

	/**
	 * 读取regexrule配置
	 * 
	 * @param nodeList
	 * @param configMap
	 */
	private static void readRegexRule(List<Node> nodeList, Map<String, Map<String,Pattern>> configMap) {
		if (null == nodeList || nodeList.size() == 0 || null == configMap) {
			return;
		}
		for (Node node : nodeList) {
			String host = node.selectSingleNode("host").getText().trim().toLowerCase();
			
			Node originalText = node.selectSingleNode("OriginalRegex");
			Pattern originalPattern = null;
			Pattern authorPattern = null;
			if (originalText != null) {
				originalPattern = Pattern.compile(originalText.getText().trim());	
			}
			Node authorText = node.selectSingleNode("AuthorRegex");
			if (authorText != null) {
				authorPattern = Pattern.compile(authorText.getText().trim());	
			}
			
			Map<String,Pattern> configSet = configMap.get(host);
			if (null == configSet) {
				configSet = new HashMap<String,Pattern>();
			}
			configSet.put("original", originalPattern);
			configSet.put("author", authorPattern);
			configMap.put(host, configSet);
		}
	}

	
	/**
	 * 获取host对应的标识正文element的属性信息
	 * 
	 * @param host
	 * @return
	 */
	public static Set<Pair<String, Pair<String, String>>> getStartPosition(String host) {
		Set<Pair<String, Pair<String, String>>> result = null;
		if (null != host) {
			result = startPositionMap.get(host);
		}
		return result;
	}
	
	
	/**
	 * 获取host对应的标识title的element的属性信息
	 * 
	 * @param host
	 * @return
	 */
	public static Set<Pair<String, Pair<String, String>>> getTitleBlock(String host) {
		Set<Pair<String, Pair<String, String>>> result = null;
		if (null != host) {
			result = titleBlockMap.get(host);
		}
		return result;
	}


	/**
	 * 获取host对应的非法element的属性信息
	 * 
	 * @param host
	 * @return
	 */
	public static Set<Pair<String, String>> getInvalidElements(String host) {
		Set<Pair<String, String>> result = null;
		if (null != host) {
			result = invalidElementsMap.get(host);
		}
		return result;
	}

	/**
	 * 获取blockrule信息
	 * 
	 * @param host
	 * @return
	 */
	public static BlockRuleInfo getBlockRule(String host) {
		BlockRuleInfo result = null;
		if (null != host) {
			result = blockRuleMap.get(host);
		}
		if (null == host) {
			result = blockRuleMap.get(DEFAULT_HOST);
		}
		return result;
	}

	/**
	 * 获取imgrule信息
	 * 
	 * @param host
	 * @return
	 */
	public static ImgRuleInfo getImgRule(String host) {
		ImgRuleInfo result = null;
		if (null != host) {
			result = imgRuleMap.get(host);
		}
		if (null == host) {
			result = imgRuleMap.get(DEFAULT_HOST);
		}
		return result;
	}

	/**
	 * 获取fontrule信息
	 * 
	 * @param host
	 * @return
	 */
	public static FontRuleInfo getFontRule(String host) {
		FontRuleInfo result = null;
		if (null != host) {
			result = fontRuleMap.get(host);
		}
		if (null == host) {
			result = fontRuleMap.get(DEFAULT_HOST);
		}
		return result;
	}
	
	
	/**
	 * 获取pattern信息
	 * 
	 * @param host
	 * @return
	 */
	public static Map<String,Pattern> getPattern(String host) {
		Map<String,Pattern> result = null;
		if (null != host) {
			result = RegexPattern.get(host);
		}
		if (null == host) {
			result = RegexPattern.get(DEFAULT_HOST);
		}
		return result;
	}

	/**
	 * 获取所有host信息
	 */

	public static HashSet<String> getAllHost() {
		HashSet<String> result = new HashSet<String>();
		result.addAll(fontRuleMap.keySet());
		result.addAll(invalidElementsMap.keySet());
		result.addAll(blockRuleMap.keySet());
		result.addAll(imgRuleMap.keySet());
		result.addAll(startPositionMap.keySet());
		result.addAll(invalidImageMap.keySet());
		result.addAll(invalidTextMap.keySet());
		result.addAll(RegexPattern.keySet());
		result.addAll(invalidHrefTextMap.keySet());
		result.addAll(continuesPage.keySet());
		result.addAll(titleBlockMap.keySet());
		result.add("default");
		return result;
	}

}
