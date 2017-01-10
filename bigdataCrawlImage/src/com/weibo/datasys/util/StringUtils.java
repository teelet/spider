/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 字符串工具类
 * 
 **/
public class StringUtils {

	private static final String PURE_CHINESE_REG = "[\u3400-\u4DB5\u4E00-\u9FBB\uF900-\uFA2D\uFA30-\uFA6A\uFA70-\uFAD9]+";

	private static final String PURE_ENG_REG = "[\\w\\s]+";

	private static final String MIX_ENG_REG = "[\\pZ\\pP\\pS\\pN\\w\\s]+";

	private static final String PUNCTUATION_REG = "[\\pP\\pS\\pZ\\pM\\s]+";

	private static final Pattern DEFAULT_UNICODE_PATTERN = Pattern
			.compile("&#\\d+;");

	private static final Pattern PURE_CHINESE_REG_P = Pattern
			.compile(PURE_CHINESE_REG);

	private static final Pattern PURE_ENG_REG_P = Pattern.compile(PURE_ENG_REG);

	private static final Pattern MIX_ENG_REG_P = Pattern.compile(MIX_ENG_REG);

	private static final Pattern PUNCTUATION_REG_P = Pattern
			.compile(PUNCTUATION_REG);
	
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	/**
	 * 
	 * 判断字符串是否为空
	 * 
	 */
	public static boolean isEmptyString(String s) {
		return null == s || s.equals("");
	}

	/**
	 * 
	 * 判断字符串是否为空
	 * 
	 */
	public static boolean isNotEmpty(String s) {
		return null != s && !s.equals("");
	}

	/**
	 * 
	 * 删除字符串中所有unicode空白字符
	 * 
	 */
	public static String deleteWhiteSpace(String s) {
		StringBuilder r = new StringBuilder();
		if (s != null) {
			for (int i = 0; i < s.length(); i++) {
				if (!Character.isWhitespace(s.codePointAt(i))) {
					r.append(s.charAt(i));
				}
			}
		}
		return r.toString();
	}

	/**
	 * 
	 * 判断字符串是否为混合中文(中文，空白，英文，标点和数学符号)字符串
	 * 
	 */
	public static boolean isMixChineseString(String input) {
		boolean result = false;
		if (input == null) {
			return result;
		}
		try {
			boolean hasChinese = false;
			boolean hasInvalid = false;
			Matcher chineseMatcher = PURE_CHINESE_REG_P.matcher(input);
			Matcher enMatcher = MIX_ENG_REG_P.matcher(input);
			for (int i = 0; i < input.length(); i++) {
				String s = input.substring(i, i + 1);
				chineseMatcher.reset(s);
				enMatcher.reset(s);
				if (chineseMatcher.matches()) {
					hasChinese = true;
				} else if (!enMatcher.matches()) {
					hasInvalid = true;
					break;
				}
			}
			if (hasChinese && !hasInvalid) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 判断字符串是否为混合英文(英文，空白、标点、数学符号)字符串
	 * 
	 */
	public static boolean isMixEnglishString(String input) {
		boolean result = false;
		try {
			result = MIX_ENG_REG_P.matcher(input).matches();
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 
	 * 判断字符串是否为纯中文字符串
	 * 
	 */
	public static boolean isChineseString(String input) {
		boolean result = false;
		try {
			result = PURE_CHINESE_REG_P.matcher(input).matches();
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 
	 * 判断字符是否为中文字符
	 * 
	 */
	public static boolean isChineseChar(char c) {
		return isChineseString(String.valueOf(c));
	}

	/**
	 * 
	 * 判断字符串是否为纯英文、数字字符串
	 * 
	 */
	public static boolean isEnglishString(String input) {
		boolean result = false;
		try {
			result = PURE_ENG_REG_P.matcher(input).matches();
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 
	 * 判断字符是否为英文、数字字符
	 * 
	 */
	public static boolean isEnglishChar(char c) {
		return isEnglishString(String.valueOf(c));
	}

	/**
	 * 
	 * 判断字符串是否为标点，分隔符，标记符，数学符号或空白字符的组合
	 * 
	 */
	public static boolean isPunctuationString(String s) {
		boolean result = false;
		try {
			result = PUNCTUATION_REG_P.matcher(s).matches();
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 
	 * 判断字符是否为标点，分隔符，标记符，数学符号或空白字符之一
	 * 
	 */
	public static boolean isPunctuationChar(char c) {
		boolean result = false;
		try {
			result = PUNCTUATION_REG_P.matcher(String.valueOf(c)).matches();
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 
	 * 判读字符是否为数字
	 * 
	 */
	public static boolean isDigit(char ch) {
		boolean result = false;
		if (ch >= '0' && ch <= '9') {
			result = true;
		}
		return result;
	}

	/**
	 * 
	 * 判读字符是否为英文字母
	 * 
	 */
	public static boolean isLetter(char ch) {
		boolean result = false;
		if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
			result = true;
		}
		return result;
	}

	/**
	 * 
	 * 字符串转double
	 * 
	 */
	public static double parseDouble(String s, double defaultValue) {
		double n = defaultValue;
		try {
			n = Double.parseDouble(s);
		} catch (Exception e) {
		}
		return n;
	}

	/**
	 * 
	 * 字符串转int
	 * 
	 */
	public static int parseInt(String s, int defaultValue) {
		return (int) parseDouble(s, defaultValue);
	}

	/**
	 * 
	 * 字符串转long
	 * 
	 */
	public static long parseLong(String s, long defaultValue) {
		return (long) parseDouble(s, defaultValue);
	}

	/**
	 * 
	 * 字符串转float
	 * 
	 */
	public static float parseFloat(String s, float defaultValue) {
		return (float) parseDouble(s, defaultValue);
	}

	/**
	 * 
	 * 字符串转布尔型
	 * 
	 */
	public static boolean parseBoolean(String s, boolean defaultValue) {
		boolean b = defaultValue;
		try {
			b = Boolean.parseBoolean(s);
		} catch (Exception e) {
		}
		return b;
	}

	/**
	 * 
	 * 字符串非空则转换成小写
	 * 
	 */
	public static String parseLowerString(String s) {
		if (!isEmptyString(s)) {
			s = s.trim().toLowerCase();
		}
		return s;
	}

	/**
	 * 
	 * 字符串非空则转换成大写
	 * 
	 */
	public static String parseUpperString(String s) {
		if (!isEmptyString(s)) {
			s = s.trim().toUpperCase();
		}
		return s;
	}

	/**
	 * 
	 * 将"null"转换为null
	 * 
	 */
	public static String nullStringToNull(String s) {
		if (s != null && "null".equalsIgnoreCase(s)) {
			s = null;
		}
		return s;
	}

	/**
	 * 
	 * 将null字符串转换为""
	 * 
	 */
	public static String nullToEmpty(String s) {
		return s == null ? "" : s;
	}

	/**
	 * 
	 * 数字转换为字符串，数字为0则返回空字符串
	 * 
	 */
	public static String zeroToEmpty(double d) {
		return d == 0 ? "" : String.valueOf(d);
	}

	/**
	 * 
	 * 将输入的unicode编码转换为String
	 * 
	 */
	public static String unicodeToString(String input) {
		return unicodeToString(input, null);
	}

	/**
	 * 
	 * 将输入的unicode编码转换为String
	 * 
	 */
	public static String unicodeToString(String input, Pattern unicodePattern) {
		if (unicodePattern == null) {
			unicodePattern = DEFAULT_UNICODE_PATTERN;
		}

		String result = input;

		Matcher matcher = unicodePattern.matcher(input);
		while (matcher.find()) {
			String origCode = matcher.group();
			String code = origCode.replaceAll("\\D", "");
			String string = new String(Character.toChars(parseInt(code, 0)));
			result = result.replace(origCode, string);
		}

		return result;
	}

	/**
	 * 
	 * 将输入字符串转换为unicode形式，&amp;#分隔
	 * 
	 */
	public static String stringToUnicode(String input) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			int code = input.codePointAt(i);
			builder.append("&#").append(code).append(";");
		}
		return builder.toString();
	}

		// 返回形式为数字跟字符串
		private static String byteToArrayString(byte bByte) {
			int iRet = bByte;
			if (iRet < 0) {
				iRet += 256;
			}
			int iD1 = iRet / 16;
			int iD2 = iRet % 16;
			return strDigits[iD1] + strDigits[iD2];
		}

	
	// 转换字节数组为16进制字串
		private static String byteToString(byte[] bByte) {
			StringBuffer sBuffer = new StringBuffer();
			for (int i = 0; i < bByte.length; i++) {
				sBuffer.append(byteToArrayString(bByte[i]));
			}
			return sBuffer.toString();
		}

		public static String getMD5Code(String strObj) {
			String resultString = null;
			try {
				resultString = new String(strObj);
				MessageDigest md = MessageDigest.getInstance("MD5");
				// md.digest() 该函数返回值为存放哈希值结果的byte数组
				resultString = byteToString(md.digest(strObj.getBytes()));
			} catch (NoSuchAlgorithmException ex) {
				ex.printStackTrace();
			}
			return resultString;
		}
		
		/**
		 * 根据md5值生成对应的文件路径，subDirCount*subDirLength<=32
		 * 
		 * @param md5
		 * @param subDirCount
		 *            文件路径层数，1~32
		 * @param subDirLength
		 *            文件夹名称长度，1~32
		 * @return
		 */
		public static String md5ToPath(String md5, int subDirCount, int subDirLength) {
			String path = "";
			if (StringUtils.isNotEmpty(md5) && subDirCount * subDirLength <= 32) {
				List<String> subDirs = new ArrayList<String>();
				for (int i = 0; i < subDirCount; i++) {
					int start = i * subDirLength;
					int end = i * subDirLength + subDirLength;
					String subDir = md5.substring(start, end);
					subDirs.add(subDir);
				}
				String lastDir = md5.substring(subDirs.size() * subDirLength);
				for (String subDir : subDirs) {
					path += subDir + "/";
				}
				path += lastDir;
			}
			return path;
		}

}
