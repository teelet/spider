/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */
package com.weibo.datasys.parser.util;

public class CharProcessUtils
{

	
	public static String commonPunctuation=",.。、!?\"“”:;—_()@《》%…'‘’*+-<=>[]{}【】";	
	public static String rarePunctuation="#$&/\\|^~`〈〉「」『』〖〗〔〕‥";
	public static String nonSenseChar="★☆◎●◆◇■□◢◣◤◥▲△▼▽〓";
	
     public static void main(String[] args) {
//		System.out.println(isCHNCharWithSymbol('�'));
//    		System.out.println(textSpaceNormalizer("[PR]       ブログ录お力め!ブログ侯喇はlivedoorBlog 浮瑚 >>もっと にほんブログ录 > 痊ケ迟祁霞　　　　　ホテルデュシェルブル〖オ〖ナ〖のブログ プロフィ〖ル > ブログ淡祸 簇息キ〖ワ〖ド"));
//    	 for (int i = 0xC940; i <= 0xF9D5; i++) {
//    		 char q = (char) i;
//    		 System.out.print(isCHNChar('グ'));
//		}
    		
	}
	/**
	 * 对字符串进行反转,输入ABC 输出CBA
	 * 
	 * @param str
	 * @return
	 */
	public static String reverse(String str) {
		return str == null ? null : (new StringBuilder(str).reverse()
				.toString());
	}

	/**
	 * 判断一个字符是否中国日本韩国的字符，中文包括简体和繁体 不包括中国日本韩国的符号
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isCJKChar(char c) {
		return (((c >= 0x4E00) && (c <= 0x9FFF))
				|| ((c >= 0xF900) && (c <= 0xFAFF))
				||
				// 日文范围
				((c >= 0x3040) && (c <= 0x309F))
				|| ((c >= 0x30A0) && (c <= 0x30FF))
				|| ((c >= 0x31F0) && (c <= 0x31FF))
				||
				// 韩文范围
				((c >= 0xAC00) && (c <= 0xD7AF))
				|| ((c >= 0x1100) && (c <= 0x11FF)) || ((c >= 0x3130) && (c <= 0x318F)));
	}

	
	/**
	 * 判断一个字符是否包括简体和繁体 不包括中文的符号
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isCHNChar(char c) {
		return isSimpleCHNChar(c)|| isTraditionCHNChar(c) ;
	}
	
	/**
	 * 判断一个字符是否是简体汉字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isSimpleCHNChar(char c) {
		return (c >= 0x4E00) && (c <= 0x9fa5);
	}
	
	/**
	 * 判断一个字符是否是扩展繁体汉字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isTraditionCHNChar(char c) {
		return (c >= 0xF900) && (c <= 0xFA2d);
	}
	
	/**
	 * 判断一个字符是否是字母（包括全角和半角）
	 * 
	 * @return
	 */
	public static boolean isLetter(char c) {
		return isHalfLetter(c)||isFullLetter(c);
	}

	/**
	 * 判断一个字符是否是半角字母
	 * 
	 * @return
	 */
	public static boolean isHalfLetter(char c) {
		return  (c>='\u0061' && c<='\u007a') || (c>='\u0041' && c<='\u005a') ;
	}
	
	/**
	 * 判断一个字符是否是全角字母
	 * 
	 * @return
	 */
	public static boolean isFullLetter(char c) {
		return (c>='\uff21' && c<='\uff3a') || (c>='\uff41' && c<='\uff5a');
	}
	

	/**
	 * 判断一个字符是否是空格或tab'字符
	 * 
	 * @return
	 */
	public static boolean isSpace(char c) {
		return (c=='\u0009' || c=='\u0020' || c=='\u00a0' || c=='\u3000');
	}
	/**
	 * 判断一个字符是否是数字（包括全角和半角）
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isDigit(char c) {
		//半角||全角
		return isHalfDigit(c) ||isFullDigit(c) ;
	}

	/**
	 * 判断一个字符是否是半角数字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isHalfDigit(char c) {
		return (c>='\u0030' && c<='\u0039') ;
	}
	
	
	/**
	 * 判断一个字符是否是全角数字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isFullDigit(char c) {
		return  (c>='\uff10' && c<='\uff19');
	}
	
	
	/**
	 * 判断一个字符是否字母或是数字（包括全角和半角）
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isLetterOrDigit(char c) {
		return isDigit(c) || isLetter(c);
	}
	
	/**
	 * 判断一个字符是否为常用标点符号
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isCommonPunctuation(char c) {
		if(commonPunctuation.indexOf(c)!=-1) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 判断一个字符是否为不常见标点符号
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isRarePunctuation(char c) {
		if(rarePunctuation.indexOf(c)!=-1) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 判断一个字符是否为不合法标点符号
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isNonSenseChar(char c) {
		if(nonSenseChar.indexOf(c)!=-1) {
			return true;
		}else {
			return false;
		}
	}


}
