package com.weibo.datasys.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import com.weibo.datasys.dao.CommonDAO;
import com.weibo.datasys.model.CommonData;
import com.weibo.datasys.service.ConfigService;
import com.weibo.datasys.service.DBManager;
import com.weibo.datasys.util.StringUtils;

public class Test {
	
	private static final Pattern CHARSET_PATTERN = Pattern
			.compile("charset=\"?([\\w|\\-]+)\"?");
	private static String urlPattern;
	private static final Pattern GROUP_PATTERN = Pattern
			.compile("\\[group:(\\d+)\\]");
	private static List<Integer> extractGroupList;
	
	public static void main(String[] args){
		int subDirCount = 2;
		int subDirLength = 10;
		String url = "http://n.sinaimg.cn/mil/transform/20160817/5LUS-fxuxnap3786630.jpg";
		String relativePath = StringUtils.md5ToPath(StringUtils.getMD5Code(url.trim()),subDirCount,subDirLength);
		System.out.println(relativePath);
		URI testUri = null;
		try {
			testUri = new URI(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(testUri.getHost());;
	    
	    try {
			ConfigService.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//	    String html = "<a href=\"/1609/337020816283.html\" target=\"_blank\" title=\"救赎之路！国外媒体点评皇室战争的拯救之路\"><span class=\"news-time\">09-05</span><span class=\"news-title\">救赎之路！国外媒体点评皇室战争的拯救之路</span></a>";
	   String html = "<a href=\"http://cr.ptbus.com/700561/\" title=\"\" target=\"_blank\">";
	    //Pattern extractPattern = Pattern.compile(ConfigService.getString("extractPattern"));
	    urlPattern = ConfigService.getString("urlPattern");
	    List<Pattern> extractPatternList = new ArrayList<Pattern>();
	    List<String> extractPatternStringList = ConfigService.getList("extractPattern");
	    for(String extractPatternString : extractPatternStringList){
	    	System.out.println(extractPatternString);
	    	Pattern extractPattern = Pattern.compile(extractPatternString);
	    	extractPatternList.add(extractPattern);
	    }
	    setUrlPattern();
	    for(Pattern extractPattern : extractPatternList){
	    	Matcher matcher = extractPattern.matcher(html);
	    	while (matcher.find()) {
				// 根据extractPattern从html提取内容，然后将urlPattern中对应[group:n]替换为提取出来的内容
				String outlink = urlPattern;
				try {
					if (urlPattern == null || extractGroupList == null
							|| extractGroupList.size() == 0) {
						outlink = matcher.group();
					} else {
						for (int i : extractGroupList) {
							String groupString = "[group:" + i + "]";
							String extractString = matcher.group(i);
							outlink = outlink.replace(groupString,
									extractString);
						}
					}
					// 反斜杠替换为斜杠
					outlink = outlink.replaceAll("\\\\", "/");
					URI seedUri = new URI("http://cr.duowan.com/tag/322654431830.html");
					// 处理相对url
					if (!outlink.matches("https?://.+")) {
						try {
							outlink = seedUri.resolve(outlink).toString();
						} catch (Exception e) {
							// url不合法，忽略之
							continue;
						}
					}
					System.out.println(outlink);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
	    }
		
//		String flag = ConfigService.getString("testPath");
//		System.out.println(flag);
//		CommonDAO dao = CommonDAO.getInstance();
//		String sql = "select * from webgrab.crawler_webdb where id='00056d3c88c1eb32c76f918160fbc61e'";
//		List<? extends CommonData> dataList = dao.getBySQL(sql, ConfigService.getString("seedDS"));
//		System.out.println(dataList.size());
//		for(CommonData data : dataList){
//			String id = data.getBaseField("id");
//			String orig_url = data.getBaseField("url");
//			System.out.println(id);
//			System.out.println(orig_url);
//			byte[] zipHtml = data.getBlobField("html");
//			if(null == zipHtml){
//				System.out.println("zipHtml is null");
//			}
//			byte[] html = unZipHtml(zipHtml);
//			String text = getStringContentOfHtml(html,null);
//			System.out.println(text.length());
//			System.out.println(text);
//			int flagIndex = text.indexOf(flag);
//			System.out.println(flagIndex);
//		}
	}
			
			/**
			 * @param urlPattern
			 *            the urlPattern to set
			 */
			public static void setUrlPattern() {
				Matcher groupPatternMatcher = GROUP_PATTERN.matcher(urlPattern);
				extractGroupList = new ArrayList<Integer>();
				while (groupPatternMatcher.find()) {
					extractGroupList.add(StringUtils.parseInt(
							groupPatternMatcher.group(1), -1));
				}
			}
	
	/**
	 * 解压缩html
	 */
	public static byte[] unZipHtml(byte[] zipHtml)
	{
		try
		{
			byte[] buf = new byte[10240];
			int readcount = 0;
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(zipHtml));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while (-1 != (readcount = gis.read(buf)))
			{
				bos.write(buf, 0, readcount);
			}
			byte[] html = bos.toByteArray();
			gis.close();
			bos.close();
			return html;
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getStringContentOfHtml(byte[] byteContent,
			String charset) {
		String contentString = "";
		try {
			if (charset == null) {
				charset = "utf-8";
				contentString = new String(byteContent, charset);
				Matcher matcher = CHARSET_PATTERN.matcher(contentString);
				if (matcher.find()) {
					charset = matcher.group(1);
				}
			}
			contentString = new String(byteContent, charset);
		} catch (Exception e) {
		}
		return contentString;
	}

}
