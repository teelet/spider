/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.html.core.charset;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

public class CharsetDetector
{

	private boolean found = false;

	private String result;

	private int lang;

	public String[] detectChineseCharset(byte[] input) {
        //lang为一个整数，用以提示语言线索 
		lang = nsPSMDetector.CHINESE;
		String[] prob;
        
		nsDetector det = new nsDetector(lang);
		// 设置一个Observer
		det.Init(new nsICharsetDetectionObserver() {
			public void Notify(String charset) {
				found = true;
				result = charset;
			}
		});

		int len = input.length;
		boolean isAscii = true;;//假定当前的串是ASCII编码

		if (isAscii) {
			isAscii = det.isAscii(input, len);
		}
		if (!isAscii) {
			det.DoIt(input, len, false);
		}

		det.DataEnd();

		if (isAscii) {
			found = true;
			prob = new String[] { "ASCII" };//假设成功
		} else if (found) {
			prob = new String[] { result };//发现字符集
		} else {
			prob = det.getProbableCharsets();//返回可能的字符集
		}

		return prob;
	}

	public String detectAllCharset(byte[] input) {

		lang = nsPSMDetector.ALL;
		String[] charset = detectChineseCharset(input);
		return charset[0];
	}
	


}
