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

import com.weibo.datasys.parser.html.antispam.FilterTag;
import com.weibo.datasys.parser.html.data.BlockInfo;
import com.weibo.datasys.parser.html.data.ScoreConfigData;
import com.weibo.datasys.parser.util.StringUtil;

public class BlockScore
{

	private ScoreConfigData configData;

	public BlockScore(ScoreConfigData configData)
	{
		if (configData != null)
		{
			this.configData = configData;
		} else
		{
			this.configData = ScoreConfigData.getDefaultConfig();
		}

	}

	public String normalizeString(String text)
	{
		if (null == text || text.trim().equals(""))
		{
			return "";
		}
		text = StringUtil.spaceFormat(text);

		return text;
	}

	public void blockWeight(BlockInfo blockInfo)
	{
		double initial = 1.0;
		boolean isSpecialTag = blockInfo.isSpecialTag();

		String mText = normalizeString(blockInfo.getAllText());	
		String aText = normalizeString(blockInfo.getAnchorText());
		String noAnchorText = blockInfo.getNoAnchorText();
		if (mText.length() > 0)
		{
			double vv = (double) aText.length() / mText.length();
			if (vv < 0.5)
			{
				initial = initial * (1.0 - vv);
			} else
			{
				initial = 0;
			}

		} else
		{
			initial = 0;
		}

		String keywords = configData.getMetaKeyword();
		int keywordNum = StringUtil.getNumOfString(keywords, " ");
		int keyNumInText = StringUtil.getNumOfString(mText, keywords, " ");
		if (keywordNum != 0)
		{
			initial = initial * (1.0 + (double) keyNumInText / keywordNum * 0.8);
		}

		if (isSpecialTag == true)
			initial = initial * 1.2;
		//FilterTag.reload(parseConfig, FilterTag.BLOCK_NOISE_STRS);
		int[] noiseinfo = FilterTag.filterNoiseText(mText);
		if (noiseinfo[0] > 0 && noiseinfo[0] <= 2)
		{
			if (((noiseinfo[1] * 1.0) / (1.0 * mText.length())) > 0.3)
			{
				initial = 0.5;
			} else
			{
				initial = initial * 0.7;
			}
		} else if (noiseinfo[0] > 2)
		{
			initial = initial * 0.3;
		}
		int language = configData.getLanguage();
		if (language > 5)
		{
			initial = initial * (1.0 + (double) mText.length() / 750);
		} else
		{
			initial = initial * (1.0 + (double) mText.length() / 350);
		}
		int dotTotal = 0;
		
		//FilterTag.reload(parseConfig, FilterTag.FOREIGNDOTS | FilterTag.INVALID_CHNDOTS);
		if (language > 5)
		{
			dotTotal = FilterTag.getNumofForeigndots(noAnchorText);
			dotTotal = dotTotal - FilterTag.getNumofInvalidForeigndots(noAnchorText);
		} else
		{
			//FilterTag.reload(parseConfig, FilterTag.BLOCK_NOISE_STRS);
			dotTotal = FilterTag.getNumofChndots(noAnchorText);
			dotTotal = dotTotal - 2 * FilterTag.getNumofInvalidChndots(noAnchorText);
		}
		initial = initial * (1.0 + (double) dotTotal / 5);
		
		blockInfo.setScore(initial);
	}

	public ScoreConfigData getConfigData()
	{
		return configData;
	}

	public void setConfigData(ScoreConfigData configData)
	{
		this.configData = configData;
	}

}
