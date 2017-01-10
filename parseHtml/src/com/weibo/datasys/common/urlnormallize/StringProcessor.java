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

import java.util.ArrayList;


/**
 *
 * 字符创处理类
 * 
 */
public class StringProcessor {
	/**
	 * 按separator切分str
	 * @param str
	 * @param separator
	 * @return
	 */
	public static ArrayList<String> splitString2ArrayList(String str, char separator){
		int start=0;
		int end;
		ArrayList<String> result=new ArrayList<String>();
		while( (end=str.indexOf(separator, start))!=-1){
			result.add(str.substring(start, end));
			start=end+1;
		}
		result.add(str.substring(start));
		return result;
	}
	
	/**
	 * 按separator切分str,最多切分成n块
	 * @param str
	 * @param separator
	 * @param n
	 * @return
	 */
	public static ArrayList<String> splitString2ArrayList(String str, char separator, int n){
		int start=0;
		int end;
		int cnt=0;

		ArrayList<String> result=new ArrayList<String>();
		
		if(n>1){
			--n;
			while( (end=str.indexOf(separator, start))!=-1){
				result.add(str.substring(start, end));
				start=end+1;
				++cnt;
				if(cnt>=n)
					break;
			}
		}
		else if(n<1){
			while( (end=str.indexOf(separator, start))!=-1){
				result.add(str.substring(start, end));
				start=end+1;
			}
		}
		
		result.add(str.substring(start));
		return result;
	}
	
	//按 sep_str切分str
	public static ArrayList<String> splitString2ArrayList(String str, String sep_str){
		final int sep_len=sep_str.length();
		int start=0;
		int end;
		
		ArrayList<String> result=new ArrayList<String>();
		
		if(sep_len!=0){
			while( (end=str.indexOf(sep_str, start))!=-1){
				result.add(str.substring(start, end));
				start=end+sep_len;
			}
		}
		
		result.add(str.substring(start));
		return result;
	}
	
	/**
	 * 按sep_str切分str,最多切分成n块
	 * @param str
	 * @param sep_str
	 * @param n
	 * @return
	 */
	public static ArrayList<String> splitString2ArrayList(String str, String sep_str, int n){
		final int sep_len=sep_str.length();
		int start=0;
		int end;
		int cnt=0;
		
		ArrayList<String> result=new ArrayList<String>();
		
		if(sep_len!=0){
			if(n>1){
				--n;
				while( (end=str.indexOf(sep_str, start))!=-1){
					result.add(str.substring(start, end));
					start=end+sep_len;
					++cnt;
					if(cnt>=n)
						break;
				}
			}
			else if(n<1){
				while( (end=str.indexOf(sep_str, start))!=-1){
					result.add(str.substring(start, end));
					start=end+sep_len;
				}
			}
		}
		
		result.add(str.substring(start));
		return result;
	}
	
	/**
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static int countChar(String str, char c){
		int cnt=0;
		int start=0;
		int end;
		while((end=str.indexOf(c, start))!=-1){
			++cnt;
			start=end+1;
		}
		return cnt;
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static ArrayList<String> simpleCutWord(String str){
		int start=0;
		int len=str.length();
		ArrayList<String> ret=new ArrayList<String>();
		int i=0;
		while(i<len){
			char c=str.charAt(i);
			if(CharacterProcessor.isChineseChar(c)){
				if(start<i){
					ret.add(str.substring(start,i));
				}
				ret.add(str.substring(i, i+1));
				start=i+1;	
			}
			else if(CharacterProcessor.isAsciiLowerCaseLetter(c)||CharacterProcessor.isAsciiDigit(c)){
			}
			else{
				if(start<i){
					ret.add(str.substring(start,i));
				}
				start=i+1;
			}
			++i;
		}
		if(start<len){
			ret.add(str.substring(start));
		}
		return ret;
	}
	
}
