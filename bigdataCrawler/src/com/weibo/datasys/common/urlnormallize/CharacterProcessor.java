/**
 *  Copyright (c)  2016-2020 Weibo, Inc.
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of Weibo, 
 *  Inc. ("Confidential Information"). You shall not
 *  disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into with Weibo.
 */

package com.weibo.datasys.common.urlnormallize;


/**
 * 
 * 字符处理类
 * 
 */
public class CharacterProcessor {
	
	public static final char ASCII_DIGIT_BASELINE='\u0030';				//ASCII数字字符的baseline
	public static final char ASCII_DIGIT_LOWER_BOUND='\u0030';			//ASCII数字字符的下界
	public static final char ASCII_DIGIT_UPPER_BOUND='\u0039';			//ASCII数字字符的上界
	
	public static final char ASCII_UPPERCASE_HEX_LETTER_BASELINE='\u0037';		//大写十六进制字母的baseline
	public static final char ASCII_UPPERCASE_HEX_LETTER_LOWER_BOUND='\u0041';	//大写十六进制字母的下界
	public static final char ASCII_UPPERCASE_HEX_LETTER_UPPER_BOUND='\u0046';	//大写十六进制字母的上界
	public static final char ASCII_LOWERCASE_HEX_LETTER_BASELINE='\u0057';		//小写十六进制字母的baseline
	public static final char ASCII_LOWERCASE_HEX_LETTER_LOWER_BOUND='\u0061';	//小写十六进制字母的下界
	public static final char ASCII_LOWERCASE_HEX_LETTER_UPPER_BOUND='\u0066';	//小写十六进制字母的上界
	
	public static final int ASCII_UPPER_CASE_LETTER_NUM=26;						//ASCII大写字母的个数
	public static final char ASCII_UPPERCASE_LEETER_LOWER_BOUND='\u0041';		//ASCII大写字母的下界
	public static final char ASCII_UPPERCASE_LEETER_UPPER_BOUND='\u005a';		//ASCII大写字母的上界
	public static final int ASCII_LOWER_CASE_LETTER_NUM=26;						//Ascii小写字母的个数
	public static final char ASCII_LOWERCASE_LEETER_LOWER_BOUND='\u0061';		//Ascii小写字母的下界
	public static final char ASCII_LOWERCASE_LEETER_UPPER_BOUND='\u007a';		//Ascii小写字母的上界
	public static final int ASCII_UPPERCASE_LOWERCASE_LETTER_DIFF=0x20;			//Ascii大小写字母的转换差
	
	public static final int ASCII_CHAR_SIZE=128;						//ascii字符的个数
	public static final char ASCII_CHAR_LOWER_BOUND='\u0000';			//ascii字符的下界
	public static final char ASCII_CHAR_UPPER_BOUND='\u007f';			//ascii字符的上界
	public static final char ASCII_NULL_CHAR='\u0000';					//ascii空字符
	
	public static final char SPACE_HTML='\u3000';						//html中的空格
	public static final char SPACE_00A0='\u00a0';						//00A0
	public static final char SPACE_ASCII='\u0020';						//ascii空格
	public static final char TAB_ASCII='\u0009';						//ascii tab
	
	public static final char CJK_UNIFIED_IDEOGRAPHS_LOWER_BOUND='\u4e00';	//中日韩统一表意文字下界
	public static final char CJK_UNIFIED_IDEOGRAPHS_UPPER_BOUND='\u9fff';	//中日韩统一表意文字上界
	public static final char CJK_COMPAT_IDEOGRAPHS_LOWER_BOUND='\uf900';	//中日朝兼容表意文字下界
	public static final char CJK_COMPAT_IDEOGRAPHS_UPPER_BOUND='\ufaff';	//中日朝兼容表意文字上界
	
	public static final char FULL_WIDTH_CHAR_CORRESPOND_TO_ASCII_LOWER_BOUND='\uff01';	//全角对应ascii区域的下界
	public static final char FULL_WIDTH_CHAR_CORRESPOND_TO_ASCII_UPPER_BOUND='\uff5e';	//全角对应ascii区域的上界
	public static final char FULL_WIDTH_DIGIT_LOWER_BOUND='\uff10';		//全角数字下界
	public static final char FULL_WIDTH_DIGIT_UPPER_BOUND='\uff19';		//全角数字上界
	public static final char FULL_WIDTH_UPPERCASE_LETTER_LOWER_BOUND='\uff21';	//全角大写字母下界
	public static final char FULL_WIDTH_UPPERCASE_LETTER_UPPER_BOUND='\uff3a';	//全角大写字母上界
	public static final char FULL_WIDTH_LOWERCASE_LETTER_LOWER_BOUND='\uff41';	//全角小写字母下界
	public static final char FULL_WIDTH_LOWERCASE_LETTER_UPPER_BOUND='\uff5a';	//全角小写字母上界
	public static final int FULL_WIDTH_ASCII_CHAR_DIFF=0xfee0;		//全角与对应ascii区域的转换差
	
	public static final int SINGLE_CHAR_CODE_MAX=65535;				//单个char的最大unicode值
	
	
	//是否是ascii数字
	public static boolean isAsciiDigit(char c){
		return c>=ASCII_DIGIT_LOWER_BOUND && c<=ASCII_DIGIT_UPPER_BOUND;
	}
		
	//是否是ascii小写字母
	public static boolean isAsciiLowerCaseLetter(char c){
		return c>=ASCII_LOWERCASE_LEETER_LOWER_BOUND && c<=ASCII_LOWERCASE_LEETER_UPPER_BOUND;
	}
	
	//是否是ascii大写字母
	public static boolean isAsciiUpperCaseLetter(char c){
		return c>=ASCII_UPPERCASE_LEETER_LOWER_BOUND && c<=ASCII_UPPERCASE_LEETER_UPPER_BOUND;
	}
	
	//不是足够安全的ascii大写字母转小写字母
	public static char unsafeAsciiUpperCaseLetter2AsciiLowerCaseLetter(char c){
		return (char)(c+ASCII_UPPERCASE_LOWERCASE_LETTER_DIFF);
	}
	
	//不是足够安全的ascii小写字母转大写字母
	public static char unsafeAsciiLowerCaseLetter2AsciiUpperCaseLetter(char c){
		return (char)(c-ASCII_UPPERCASE_LOWERCASE_LETTER_DIFF);
	}
	
	//ascii大写字符转小写字符
	public static char toAsciiLowerCaseChar(char c){
		return isAsciiUpperCaseLetter(c) ? unsafeAsciiUpperCaseLetter2AsciiLowerCaseLetter(c) : c;
	}
	
	//ascii小写字符转大写字符
	public static char toAsciiUpperCaseChar(char c){
		return isAsciiLowerCaseLetter(c) ? unsafeAsciiLowerCaseLetter2AsciiUpperCaseLetter(c) : c;
	}
	
	//是不是ascii字符
	public static boolean isAsciiChar(char c){
		return c>=ASCII_CHAR_LOWER_BOUND && c<=ASCII_CHAR_UPPER_BOUND;
	}
	
	//是不是非ascii字符
	public static boolean isNonAsciiChar(char c){
		return c>ASCII_CHAR_UPPER_BOUND || c<ASCII_CHAR_LOWER_BOUND;
	}
	
	//是不是ascii的十六进制大写字母
	public static boolean isAsciiUpperCaseHexLetter(char c){
		return c>=ASCII_UPPERCASE_HEX_LETTER_LOWER_BOUND && c<=ASCII_UPPERCASE_HEX_LETTER_UPPER_BOUND;
	}
	
	//是不是ascii的十六进制小写字母
	public static boolean isAsciiLowerCaseHexLetter(char c){
		return c>=ASCII_LOWERCASE_HEX_LETTER_LOWER_BOUND && c<=ASCII_LOWERCASE_HEX_LETTER_UPPER_BOUND;
	}
	
	//是不是ascii的大写十六进制数字
	public static boolean isAsciiUpperCaseHexDigit(char c){
		return isAsciiDigit(c) ? true : isAsciiUpperCaseHexLetter(c);	
	}
	
	//是不是ascii的十六进制字母
	public static boolean isAsciiHexLetter(char c){
		return isAsciiUpperCaseHexLetter(c) || isAsciiLowerCaseHexLetter(c);
	}
	
	//是不是ascii的十六进制数字
	public static boolean isAsciiHexDigit(char c){
		return isAsciiDigit(c) ? true : isAsciiHexLetter(c);			
	}
	
	//将ascii十六进制数字转换成int，非ascii十六进制转换成-1
	public static int parseAsciiHexDigit(char c){
		if(isAsciiDigit(c)){
			return c-ASCII_DIGIT_BASELINE;
		}
		else if(isAsciiUpperCaseHexLetter(c)){
			return c-ASCII_UPPERCASE_HEX_LETTER_BASELINE;
		}
		else if(isAsciiLowerCaseHexLetter(c)){
			return c-ASCII_LOWERCASE_HEX_LETTER_BASELINE;
		}
		else{
			return -1;
		}
	}
	
	//ascii字符转换成int，非ascii字符对应-1
	public static int getAsciiCharOrdinal(char c){
		return (c>=ASCII_CHAR_LOWER_BOUND && c<=ASCII_CHAR_UPPER_BOUND) ? c : -1;
	}
	
	//小写字母的序数
	public static int getAsciiLowerCaseLetterOrdinal(char c){
		return isAsciiLowerCaseLetter(c) ? c-ASCII_LOWERCASE_LEETER_LOWER_BOUND : -1;
	}
	
	//大写字母的序数
	public static int getAsciiUpperCaseLetterOrdinal(char c){
		return isAsciiUpperCaseLetter(c) ? c-ASCII_UPPERCASE_LEETER_LOWER_BOUND : -1;
	}
	
	//是否是广义的空格
	public static boolean isGenericWhiteSpace(char c){
		return c<=SPACE_ASCII || c==SPACE_HTML || c==SPACE_00A0;
	}
	
	//是否是中文字符
	public static boolean isChineseChar(char c){
		return (c>=CJK_UNIFIED_IDEOGRAPHS_LOWER_BOUND && c<=CJK_UNIFIED_IDEOGRAPHS_UPPER_BOUND) 
				|| ( c>=CJK_COMPAT_IDEOGRAPHS_LOWER_BOUND && c<=CJK_COMPAT_IDEOGRAPHS_UPPER_BOUND );
	}
	
	//是否是有ascii对应区域的全角字符区
	public static boolean isFullWidthCharCorrespond2Ascii(char c){
		return c>=FULL_WIDTH_CHAR_CORRESPOND_TO_ASCII_LOWER_BOUND && c<=FULL_WIDTH_CHAR_CORRESPOND_TO_ASCII_UPPER_BOUND;
	}
	
	//是否是全角数字
	public static boolean isFullWidthDigit(char c){
		return c>=FULL_WIDTH_DIGIT_LOWER_BOUND && c<=FULL_WIDTH_DIGIT_UPPER_BOUND;
	}
	
	//是否是全角小写字母
	public static boolean isFullWidthLowerCaseLetter(char c){
		return c>=FULL_WIDTH_LOWERCASE_LETTER_LOWER_BOUND && c<=FULL_WIDTH_LOWERCASE_LETTER_UPPER_BOUND;
	}
	
	//是否是全角大写字母
	public static boolean isFullWitdthUpperCaseLetter(char c){
		return c>=FULL_WIDTH_UPPERCASE_LETTER_LOWER_BOUND && c<=FULL_WIDTH_UPPERCASE_LETTER_UPPER_BOUND;
	}

	//不安全的全角字符转对应的ascii字符
	public static char unsafeFullWidthChar2AsciiChar(char c){
		return c-=FULL_WIDTH_ASCII_CHAR_DIFF;
	}
}
