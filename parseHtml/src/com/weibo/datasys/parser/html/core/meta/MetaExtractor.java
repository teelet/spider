/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.meta;

import com.weibo.datasys.parser.html.data.MetaTags;
import com.weibo.datasys.parser.util.TextUtil;

public class MetaExtractor
{

	private MetaTags metaTags;

	public int MIN_DESCRIPTION_NUM = 10;

	private String normalDescription = "";

	private String normalKeywords = "";

	private String normalMetaTitle = "";
	
	private String normalMediaId = "";

	// private MainTextAntiSpam textAntiSpam = new MainTextAntiSpam();
	//	
	// private KeywordsAntiSpam keywordsAntiSpam = new KeywordsAntiSpam();

	public MetaExtractor()
	{
	}

	public MetaExtractor(MetaTags metaTags)
	{
		this.metaTags = metaTags;
	}

	// public String metaDesAfterAntispam(String description) {
	//		
	// description = TextNormalizer.Normalizer(description);
	// if (description.length() < MIN_DESCRIPTION_NUM
	// && textAntiSpam.spamWeight(description) >= MainTextAntiSpam.THRESHHOLD)
	// {
	// description = "";
	// }
	//		
	// return description;
	// }
	//	
	//	
	// public String metaKeywordsAfterAntispam(String metaKeywords) {
	// metaKeywords = TextNormalizer.Normalizer(metaKeywords);
	// if (keywordsAntiSpam.spamWeight(metaKeywords) >=
	// KeywordsAntiSpam.THRESHHOLD)
	// {
	// metaKeywords = "";
	// }else {
	// KeywordsNormalizer keywordsNormalizer = new KeywordsNormalizer();
	// metaKeywords = keywordsNormalizer.normalizeString(metaKeywords);
	// }
	// return metaKeywords;
	// }

	public String getNormalDescription()
	{
		if (metaTags != null)
		{
			Object tempdes = metaTags.getGeneralTags().get("description");
			normalDescription = tempdes != null ? removeLt((String) tempdes) : "";
			normalDescription = TextUtil.Normalizer(normalDescription.trim());
		}
		return normalDescription == null ? "" : normalDescription;
	}
	
	public String getMediaId(){
		if (metaTags != null)
		{
			Object tempkw = metaTags.getGeneralTags().getProperty("mediaid");
			normalMediaId = tempkw != null ? removeLt((String) tempkw) : "";
			normalMediaId = TextUtil.Normalizer(normalMediaId.trim());
		}
		return normalMediaId == null ? "" : normalMediaId;
	}

	public void setNormalDescription(String normalDescription)
	{

		this.normalDescription = normalDescription;
	}

	public String getNormalKeywords()
	{
		if (metaTags != null)
		{
			Object tempkw = metaTags.getGeneralTags().getProperty("keywords");
			normalKeywords = tempkw != null ? removeLt((String) tempkw) : "";
			normalKeywords = TextUtil.Normalizer(normalKeywords.trim());
		}
		return normalKeywords == null ? "" : normalKeywords;
	}

	public void setNormalKeywords(String normalKeywords)
	{
		this.normalKeywords = normalKeywords;
	}

	public String getNormalMetaTitle()
	{
		if (metaTags != null)
		{
			Object tempT = metaTags.getGeneralTags().getProperty("title");
			normalMetaTitle = tempT != null ? removeLt((String) tempT) : "";
			normalMetaTitle = TextUtil.Normalizer(normalMetaTitle.trim());
		}
		return normalMetaTitle == null ? "" : normalMetaTitle;
	}

	public void setNormalMetaTitle(String metaTitle)
	{
		this.normalMetaTitle = metaTitle;
	}

	private String removeLt(String text)
	{
		return text.replaceAll("<[^>]*>", "");
	}
}
