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

import java.io.IOException;

import com.weibo.datasys.parser.html.core.title.TitleNormalizer;
import com.weibo.datasys.parser.html.data.TitleInfo;
import com.weibo.datasys.parser.util.ArgsNormalizer;
import com.weibo.datasys.parser.util.StringUtil;

public class TitleAntiSpam extends AntiSpamBasic
{
	private final static String[] EQUAL_FILTER = { "网页已删除", "本网页已删除" };

	/** 40 标题的最大长度 */
	private final int TITLE_MAXLEN = 100; // title 最大长度

	/** 1.0 非中、英、数字的字符最大比例 */
	private final double OTHERCHAR_MAXPROP = 1.0;

	/** 0.0 中、英、数字的字符比例阈值 */
	private final double NORMALCHAR_MINPROP = 0.0;

	/** 0.5 非常用汉字过滤阈值（非常用字过滤） */
	private final double NONCOMMON_CHAR_MAXPROP = 0.5;

	/** 8 主串长度阈值（非常用字过滤） */
	private final int NONCOMMON_TITLE_MAXLEN = 8;

	/** 0.8 无意义title阈值（分词过滤） */
	private final double NONSENSE_TITLE_THRESHHOLD = 0.8;

	/** 8 判断无意义title时，所要求的最小长度（分词过滤） */
	private final int NONSENSE_TITLE_MINLEN = 8;

	/** len weight */
	private double lenWeight = 0.0;

	/** charType weight */
	private double charTypeWeight = 0.0;

	/** noncommon weight */
	private double noncomWeight = 0.0;

	/** segword weight */
	private double segWeight = 0.0;

	/** 过滤阈值 */
	public final static double THRESHHOLD = 1.5;

	/** 总分值 */
	public final static double ALLSCORE = 6;

	// ---------------------test list----------------
	// public static List nonCommonCharList = new ArrayList<String>();
	// public static List chartypeTitleList = new ArrayList<String>();
	// public static List maxLenTitleList = new ArrayList<String>();
	// public static List segTitleList = new ArrayList<String>();
	// public static List normalTitleList = new ArrayList<String>();
	// public static List highWeightTitleList = new ArrayList<String>();

	/**
	 * {@link TitleAntiSpam} 标题反垃圾主方法 阈值1.5 总分6
	 */
	@Override
	public double spamWeight(String title)
	{

		if (!ArgsNormalizer.strIsValid(title))
		{
			return ALLSCORE;
		}

		double weight = 0.0;// title的权重
		// 获取title 信息
		TitleInfo titleInfo = getTitleInfo(title);

		// 1.长度因素

		lenWeight = getTitleLenWeight(titleInfo);
		// if (lenWeight>=1) {
		// maxLenTitleList.add("weight="+lenWeight+"-----"+titleInfo.getRawTitle());
		// }
		// 2.字符类型因素
		charTypeWeight = getTitleCharTypeWeight(titleInfo);
		// if (charTypeWeight>=2) {
		// chartypeTitleList.add("weight="+charTypeWeight+"-----"+titleInfo.getRawTitle());
		// }
		// 3.非常用字的比例
		noncomWeight = getNonCommonWeight(titleInfo);
		// if (noncomWeight>=1.5) {
		// nonCommonCharList.add("weight="+noncomWeight+"-----"+titleInfo.getRawTitle());
		// }
		// 分词规则
		segWeight = getSegmentWordsWeight(titleInfo);
		// if (segWeight>=1.5) {
		// segTitleList.add("weight="+segWeight+"-----"+titleInfo.getRawTitle());
		// }
		weight = lenWeight + charTypeWeight + noncomWeight + segWeight;

		// if (weight>1.5) {
		// highWeightTitleList.add("weight="+weight+"-----"+titleInfo.getRawTitle());
		// }else {
		// normalTitleList.add("weight="+weight+"-----"+titleInfo.getRawTitle());
		// }
		weight = weight < 0 ? 0 : weight;
		weight = weight > 6 ? 6 : weight;
		return weight;

	}

	/**
	 * 长度规则
	 * 
	 * @param titleInfo
	 *            标题信息
	 */
	private double getTitleLenWeight(TitleInfo titleInfo)
	{
		// 总分 1
		double weight = 0.0;
		Integer titleLen = titleInfo.getLen();
		if (titleLen > TITLE_MAXLEN)
		{
			return 1;
		} else
		{
			weight += 1.0 * (titleLen * 1.0) / (1.0 * TITLE_MAXLEN);// 长度影响权重
		}
		return weight;
	}

	/**
	 * 字符类型规则
	 * 
	 * @param titleInfo
	 *            标题信息
	 */
	private double getTitleCharTypeWeight(TitleInfo titleInfo)
	{
		// 总分 2
		double weight = 0.0;
		Integer charNum = titleInfo.getCharNum();
		if (charNum == 0)
		{
			return 0;
		}
		double chnHold = (1.0 * titleInfo.getChnNum()) / (charNum * 1.0); // 汉字（简繁）的比例
		double engHold = (1.0 * titleInfo.getEnNum()) / (charNum * 1.0); // 英文的比例
		double digitHold = (1.0 * titleInfo.getDigitNum()) / (charNum * 1.0); // 数字的比例
		double spaceHold = (1.0 * titleInfo.getSpaceNum()) / (charNum * 1.0); // 空白的比例
		double otherHold = (1.0 * titleInfo.getOtherCharNum()) / (charNum * 1.0); // 其它符号的比例

		weight += (spaceHold + otherHold) * 1.5;
		weight += 0.5 * digitHold / (chnHold + digitHold + engHold);
		return weight;
	}

	/**
	 * 非常用字规则
	 * 
	 * @param titleInfo
	 *            标题信息
	 */
	private double getNonCommonWeight(TitleInfo titleInfo)
	{
		// 总分 1.5
		double weight = 0.0;
		Integer chnNum = titleInfo.getChnNum();
		Integer nonCommonChnNum = titleInfo.getNonCommonChnNum();
		Integer titleLen = titleInfo.getLen();
		if (chnNum > 0)
		{// 汉字数量大于零
			Double nonCommonScale = (1.0 * nonCommonChnNum) / (1.0 * chnNum);
			// 非常用字比例和汉字数量都满足相应的比例时，直接标记为垃圾
			if (nonCommonScale > NONCOMMON_CHAR_MAXPROP && chnNum > NONCOMMON_TITLE_MAXLEN)
			{
				return 1.5;
			} else
			{
				if (nonCommonScale > 0)
				{
					weight += 1.5 * nonCommonScale * (chnNum * 1.0) / (titleLen * 1.0);
				} else
				{
					weight -= 0.5;
				}
			}
		}
		return weight;
	}

	/**
	 * 分词因素规则
	 * 
	 * @param titleInfo
	 *            标题信息
	 */
	private double getSegmentWordsWeight(TitleInfo titleInfo)
	{
		// 总分 1.5
		double weight = 0.0;
		Integer segtitleNum = titleInfo.getSegtitleNum();
		Integer titleLen = titleInfo.getLen();
		double nonsenseHold = (segtitleNum * 1.0) / (titleLen * 1.0);
		double param = 1.0;// 影响因子
		if (titleLen <= TITLE_MAXLEN)
		{
			param = (titleLen * 1.0) / (1.0 * TITLE_MAXLEN);
		}
		if (nonsenseHold > NONSENSE_TITLE_THRESHHOLD && titleLen > NONSENSE_TITLE_MINLEN)
		{
		}
		weight += nonsenseHold * 1.5 * param;
		// weight -=(1-nonsenseHold)*1.5*param;
		return weight;
	}

	public TitleInfo getTitleInfo(String title)
	{
		TitleNormalizer titleNormalizer = new TitleNormalizer();
		String normalizeTitle = titleNormalizer.normalizeString(title);
		Integer titlelen = normalizeTitle.length();
		Integer[] charNums = StringUtil.getInvalidWordNum(normalizeTitle);
		// Integer segtitleNum = AntiSpamUtil.getSegStrLen(normalizeTitle);

		TitleInfo titleInfo = new TitleInfo();
		titleInfo.setRawTitle(title);
		titleInfo.setNormallizeTitle(normalizeTitle);
		titleInfo.setLen(titlelen);

		titleInfo.setCharNum(charNums[0]);
		titleInfo.setChnNum(charNums[1]);
		titleInfo.setEnNum(charNums[2]);
		titleInfo.setDigitNum(charNums[3]);
		titleInfo.setSpaceNum(charNums[4]);
		titleInfo.setOtherCharNum(charNums[5]);
		titleInfo.setNonCommonChnNum(charNums[6]);

		// titleInfo.setSegtitleNum(segtitleNum);

		return titleInfo;
	}

	public static void main(String[] args) throws IOException
	{
		String path = "E:/statisResult/title/";
		// TitleAntiSpam titleAntiSpam = new TitleAntiSpam();
		// System.out.println(titleAntiSpam.spamWeight(""));
		// BufferedReader fr = new BufferedReader(new FileReader(new File(path
		// + "title0")));
		// String s = null;
		// int i =0;
		// while ((s = fr.readLine()) != null) {
		// titleAntiSpam.spamWeight(s);
		// System.out.println(i++);
		// }
		// if (fr != null)
		// fr.close();
		//
		// printlist(nonCommonCharList, path+"noncom");
		// printlist(chartypeTitleList, path+"chartype");
		// printlist(segTitleList, path+"seg");
		// printlist(normalTitleList, path+"normal");
		// printlist(highWeightTitleList, path+"high");

	}

	// public static void printlist(List list,String name) throws IOException {
	// BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new
	// FileOutputStream(new File(name)),"utf-8"));
	// for (int i = 0; i < list.size(); i++) {
	// bw2.append(list.get(i) + "\r\n");
	// }
	// if (bw2 != null) {
	// bw2.flush();
	// bw2.close();
	// }
	// }

	@Override
	public String toString()
	{
		return "TitleAntiSpam [NONCOMMON_CHAR_MAXPROP=" + NONCOMMON_CHAR_MAXPROP
				+ ", NONCOMMON_TITLE_MAXLEN=" + NONCOMMON_TITLE_MAXLEN + ", NONSENSE_TITLE_MINLEN="
				+ NONSENSE_TITLE_MINLEN + ", NONSENSE_TITLE_THRESHHOLD="
				+ NONSENSE_TITLE_THRESHHOLD + ", NORMALCHAR_MINPROP=" + NORMALCHAR_MINPROP
				+ ", OTHERCHAR_MAXPROP=" + OTHERCHAR_MAXPROP + ", TITLE_MAXLEN=" + TITLE_MAXLEN
				+ ", charTypeWeight=" + charTypeWeight + ", lenweight=" + lenWeight
				+ ", noncomWeight=" + noncomWeight + ", segWeight=" + segWeight + "]";
	}

}
