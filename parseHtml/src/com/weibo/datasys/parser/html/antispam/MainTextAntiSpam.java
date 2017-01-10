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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.weibo.datasys.parser.html.core.content.MainTextNormalizer;
import com.weibo.datasys.parser.html.data.MainTextInfo;
import com.weibo.datasys.parser.util.ArgsNormalizer;
import com.weibo.datasys.parser.util.StringUtil;

public class MainTextAntiSpam extends AntiSpamBasic
{
	/** 0.5 非常用汉字过滤阈值（非常用字过滤） */
	private final double NONCOMMON_CHAR_MAXPROP = 0.35;

	/** 8 主串长度阈值（非常用字过滤） */
	private final int NONCOMMON_MAINTEXT_MAXLEN = 20;

	/** 过滤阈值 */
	public final static double THRESHHOLD = 1.5;

	/** 总分值 */
	public final static double ALLSCORE = 5;

	// ---------------------test list----------------
	// public static List nonCommonCharList = new ArrayList<String>();
	// public static List chartypeList = new ArrayList<String>();
	// public static List segList = new ArrayList<String>();
	// public static List normalTextList = new ArrayList<String>();
	// public static List highWeightAnchorList = new ArrayList<String>();

	/**
	 * 正文过滤主方法 阈值1.5 总分5
	 */
	@Override
	public double spamWeight(String allText)
	{
		if (!ArgsNormalizer.strIsValid(allText))
		{
			return ALLSCORE;
		}

		// 归一化
		MainTextInfo mainTextInfo = getMainTextInfo(allText);

		double weight = 0.0;// anchorText的权重
		// 0.黑名单

		// 1.过滤含有非中英文字符的词组
		weight += getStrCharTypeWeight(mainTextInfo);
		// if(weight>=2) {
		// chartypeList.add("weight="+weight+"-----"+mainTextInfo.getRawMainText());
		// }
		// 2.非常用字规则
		double nw = getNonCommonWeight(mainTextInfo);
		// weight+=getNonCommonWeight(mainTextInfo);
		weight += nw;
		// if(nw>=1.5) {
		// nonCommonCharList.add("weight="+nw+"-----"+mainTextInfo.getRawMainText());
		// }
		// 3.分词
		double sw = getSegmentWeight(mainTextInfo);
		weight += sw;
		// if(sw>=1.5) {
		// segList.add("weight="+sw+"-----"+mainTextInfo.getRawMainText());
		// }
		// if(weight >=1.5)
		// {
		// highWeightAnchorList.add("weight="+weight+"-----"+mainTextInfo.getRawMainText());
		// }else {
		// normalTextList.add("weight="+weight+"-----"+mainTextInfo.getRawMainText());
		// }
		weight = weight < 0 ? 0 : weight;
		weight = weight > 5 ? 5 : weight;
		return weight;
	}

	/**
	 * 分词检测规则，返回惩罚权重
	 */
	private double getSegmentWeight(MainTextInfo mainTextInfo)
	{
		// 总分1.5
		double weight = 0.0;// 初始权重
		int textlen = mainTextInfo.getLen();
		int chnNum = mainTextInfo.getChnNum();// 汉字数量
		// int nonCommonChnNum = mainTextInfo.getNonCommonChnNum();
		int segTextNum = mainTextInfo.getSegTextNum();

		if (textlen == 0 || chnNum == 0)
		{
			return weight;
		}
		// 不常用汉字比例
		// double nonCommonScale = (1.0 * nonCommonChnNum)/ (1.0 * chnNum);

		double nonsenseHold = (segTextNum * 1.0) / (textlen * 1.0);
		// 惩罚权重计算
		if (textlen > 15)
		{
			weight += (nonsenseHold - 0.5) * 1.5;
		}

		return weight < 0 ? 0 : weight;
	}

	/**
	 * 非常用汉字检测规则，返回惩罚权重 总分 1.5
	 */
	private double getNonCommonWeight(MainTextInfo mainTextInfo)
	{
		// 总分 1.5
		double weight = 0.0;// 初始权重
		int chnNum = mainTextInfo.getChnNum();// 汉字数量
		int textlen = mainTextInfo.getLen();
		int nonCommonChnNum = mainTextInfo.getNonCommonChnNum();

		if (chnNum > 0)
		{
			// 不常用汉字比例
			double nonCommonScale = (1.0 * nonCommonChnNum) / (1.0 * chnNum);
			// 惩罚权重计算
			// 直接标记为垃圾（非常用字比例和汉字数量都满足相应的比例时）
			if (nonCommonScale > NONCOMMON_CHAR_MAXPROP && chnNum > NONCOMMON_MAINTEXT_MAXLEN)
			{
				return 1.5;
			} else
			{
				// 参数设置
				if (nonCommonScale > 0)
				{
					weight += 1.5 * nonCommonScale * (chnNum * 1.0) / (textlen * 1.0);
				} else
				{
					weight -= 0.5;
				}
			}
		}

		return weight;
	}

	/**
	 * 非中英文字符检测规则，返回惩罚权重 总分2
	 */
	private double getStrCharTypeWeight(MainTextInfo mainTextInfo)
	{
		// 总分2
		double weight = 0.0;// 初始权重
		Integer charNum = mainTextInfo.getCharNum();
		Integer mainTextLen = mainTextInfo.getLen();
		if (charNum == 0)
		{
			return 0;
		}
		double chnHold = (1.0 * mainTextInfo.getChnNum()) / (charNum * 1.0); // 汉字（简繁）的比例
		double engHold = (1.0 * mainTextInfo.getEnNum()) / (charNum * 1.0); // 英文的比例
		double digitHold = (1.0 * mainTextInfo.getDigitNum()) / (charNum * 1.0); // 数字的比例
		double spaceHold = (1.0 * mainTextInfo.getSpaceNum()) / (charNum * 1.0); // 空白的比例
		double otherHold = (1.0 * mainTextInfo.getOtherCharNum()) / (charNum * 1.0); // 其它符号的比例

		// 没有字符或没有中英文

		weight += (spaceHold + otherHold) * 1.5;
		weight += 0.5 * digitHold / (chnHold + digitHold + engHold);
		return weight;

		// if ((otherHold+digitHold) >=
		// OTHERCHAR_MAXPROP||(chnHold+engHold)<=NORMALCHAR_MINPROP) {
		// // chnList.add("filter::"+mainText);
		// return AntiSpamUtil.MainText_SPAM_FLAG;
		// } else {
		// if(mainTextLen<=30) {
		// weight +=digitHold*0.2;
		// weight += otherHold * 0.5;
		// }else {
		// weight +=digitHold*0.9;
		// weight += otherHold*0.9;
		// }
		//					
		// }
		// return weight;
	}

	public MainTextInfo getMainTextInfo(String text)
	{
		MainTextNormalizer mainTextNormalizer = new MainTextNormalizer();
		String normalizeText = mainTextNormalizer.normalizeString(text);
		Integer textlen = normalizeText.length();
		Integer[] charNums = StringUtil.getInvalidWordNum(normalizeText);
		// Integer segTextNum = AntiSpamUtil.getSegStrLen(normalizeText);

		MainTextInfo mainTextInfo = new MainTextInfo();
		mainTextInfo.setRawMainText(text);
		mainTextInfo.setNormallizeMainText(normalizeText);
		mainTextInfo.setLen(textlen);

		mainTextInfo.setCharNum(charNums[0]);
		mainTextInfo.setChnNum(charNums[1]);
		mainTextInfo.setEnNum(charNums[2]);
		mainTextInfo.setDigitNum(charNums[3]);
		mainTextInfo.setSpaceNum(charNums[4]);
		mainTextInfo.setOtherCharNum(charNums[5]);
		mainTextInfo.setNonCommonChnNum(charNums[6]);

		// mainTextInfo.setSegTextNum(segTextNum);

		return mainTextInfo;
	}

	// public static void main(String[] args) throws IOException {
	// String path = "E:/statisResult/maintext/";
	// //
	// MainTextAntiSpam mainTextAntiSpam = new MainTextAntiSpam();
	// System.out.println(mainTextAntiSpam.spamWeight(""));
	// //
	// mainTextAntiSpam.mainTextIsSpamByText(" 法律:人生の流れを変える情報のリンク集 人生の流れを変える情報のリンク集トップへ  >  法律 カテゴリー 法律登録サイト一覧 人生の流れを変える情報のリンク集の法律カテゴリです。法律サイト様は相互リンク登録お願いします。 トップ Copyright By 人生の流れを変える情報のリンク集");
	// BufferedReader fr = new BufferedReader(new InputStreamReader(new
	// FileInputStream(new File(path
	// + "textlog2")),"utf-8"));
	// String s = null;
	// int i =0;
	// while ((s = fr.readLine()) != null) {
	// mainTextAntiSpam.mainTextIsSpamByText(s);
	// System.out.println(i++);
	// }
	// if (fr != null)
	// fr.close();
	//		
	// printlist(nonCommonCharList, path+"noncom");
	// printlist(chartypeList, path+"chartype");
	// printlist(segList, path+"seg");
	// printlist(normalTextList, path+"normal");
	// printlist(highWeightAnchorList, path+"high");

	// }

	public static void printlist(List list, String name) throws IOException
	{
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
				new File(name)), "utf-8"));
		for (int i = 0; i < list.size(); i++)
		{
			bw2.append(list.get(i) + "\r\n");
		}
		if (bw2 != null)
		{
			bw2.flush();
			bw2.close();
		}
	}
}
