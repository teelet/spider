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

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.apache.html.dom.HTMLDocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;

import com.weibo.datasys.parser.html.core.charset.CharsetDetector;

public class DomTreeMaker
{
	private static final Logger LOG = LoggerFactory.getLogger(DomTreeMaker.class);

	public static DocumentFragment getDomRoot(byte[] html, String encoding, String url)
	{
		DocumentFragment root = null;
		try
		{
			InputSource input = new InputSource(new ByteArrayInputStream(html));
			input.setEncoding(encoding);
			root = tryParseNeko(input, html, encoding, url);
		} catch (Exception e)
		{
			LOG.error("[Exception]:{}\t[URL]:{},", e, url);
			root = null;
		}
		return root;
	}

	private static DocumentFragment tryParseNeko(InputSource input, byte[] html, String en,
			String url)
	{
		DocumentFragment frag = null;
		// 第一次parse dom
		try
		{
			frag = parseNeko(input, en);
		} catch (UnsupportedEncodingException e)
		{
			try
			{
				CharsetDetector charsetDetector = new CharsetDetector();
				String encoding = charsetDetector.detectAllCharset(html);
				if (!encoding.equals(en))
				{
					input.setEncoding(encoding);
					LOG.debug("Retry  Parse Dom, Encoding = {}", encoding);
					frag = parseNeko(input, encoding);
				}
			} catch (Exception e1)
			{
				LOG.error("[Exception]:{}\t[URL]:{},", e1, url);
			}

		} catch (Exception e)
		{
			LOG.error("[Exception]:{}\t[URL]:{},", e, url);
		}
		return frag;
	}

	private static DocumentFragment parseNeko(InputSource input, String en) throws Exception
	{
		DOMFragmentParser parser = new DOMFragmentParser();
		parser.setFeature("http://cyberneko.org/html/features/augmentations", true);
		parser.setProperty("http://cyberneko.org/html/properties/default-encoding", en);
		parser.setFeature("http://cyberneko.org/html/features/scanner/ignore-specified-charset",
				true);
		parser.setFeature("http://cyberneko.org/html/features/balance-tags/ignore-outside-content",
				false);
		parser
				.setFeature("http://cyberneko.org/html/features/balance-tags/document-fragment",
						true);
		parser.setFeature("http://cyberneko.org/html/features/report-errors", false);

		HTMLDocumentImpl doc = new HTMLDocumentImpl();
		doc.setErrorChecking(false);
		DocumentFragment frag = doc.createDocumentFragment();
		parser.parse(input, frag);
		return frag;
	}

}
