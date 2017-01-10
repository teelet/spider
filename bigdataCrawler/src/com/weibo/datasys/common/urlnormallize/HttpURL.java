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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * 
 * URL工具类
 * 
 */
public class HttpURL {

	private static final int MIN_URL_LEN = 4; // url 最小长度
	private static final int MIN_AUTH_LEN = 4; // authority 最小长度
	private static final int MIN_HOST_LEN = 4; // host 最小长度
	private static final int MIN_TOP_DOMAIN_LEN = 2; // top domain 的最小长度
	private static final int MIN_SENSE_KEY_VALUE_LEN = 3; // query最小长度
	private static final int MAX_URL_LEN = 384; // url 最大长度
	private static final int MAX_DETECT_SCHEME_LEN = 8; // 探测 scheme 的最大长度

	private static final String DEFAULT_SCHEME = "http"; // 默认scheme为http
	private static final String EMPTY_USER_INFO = ""; // 空userinfo
	private static final String EMPTY_PORT = ""; // 空port
	private static final String DEFAULT_HTTP_PORT = "80"; // 默认端口
	private static final String EMPTY_PATH = ""; // 空路径
	private static final String DEFAULT_NORMALIZED_PATH = "/"; // 默认原始路径的归一化后路径
	private static final String EMPTY_QUERY = ""; // 空query
	private static final String EMPTY_FRAGMENT = ""; // 空fragment
	private static final String EMPTY_STRING = ""; // 空String

	private static final String[] VALID_SCHEME = { "http", "https" }; // 合法的scheme为http和https
	private static final int IP_SEG_MIN = 0; // ipv4 每一个段的最小值
	private static final int IP_SEG_MAX = 255; // ipv4 每一个段的最大值
	private static final int PORT_NUM_MIN = 1; // 端口的最小值
	private static final int PORT_NUM_MAX = 65535; // 端口的最大值
	private static final HashSet<String> DEFAULT_FILE_NAME_SET = new HashSet<String>(); // 默认文件名
	private static final int QUESTION_MARK_CODE_POINT = 0x3F; // ? 对应的code point
	private static final byte ERROR_ENCODE_BYTE = 0x3F; // 错误返回的encode字节

	private static final boolean[] UNRESERVED_CHAR_LABEL = new boolean[CharacterProcessor.ASCII_CHAR_SIZE]; // 非保留字符的标志
	private static final boolean[] P_CHAR_EXCLUDE_PCT_LABEL = new boolean[CharacterProcessor.ASCII_CHAR_SIZE];// pchar的标志

	private static final int ONE_BYTE_SIZE = 256; // byte对应的个数
	private static final String[] ENCODE_STRING = new String[ONE_BYTE_SIZE]; // byte对应的PCT

	private static final Charset DEFAULT_CHAR_SET = Charset.forName("utf-8"); // 默认的编码格式

	static {
		String digit_char = "0123456789"; // 数字
		String lowercase_letter = "abcdefghijklmnopqrstuvwxyz"; // 小写字母
		String uppercase_letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // 大写字母
		String unreserved_char_exclude_letter_digit = "-._~"; // 除字母数字外的非保留字符

		int tmp_len = digit_char.length();
		int tmp_idx = 0;
		for (int i = 0; i < tmp_len; ++i) {
			tmp_idx = CharacterProcessor.getAsciiCharOrdinal(digit_char
					.charAt(i));
			UNRESERVED_CHAR_LABEL[tmp_idx] = true; // 设置非保留字符中的数字
			P_CHAR_EXCLUDE_PCT_LABEL[tmp_idx] = true; // 设置pchar中的数字
		}

		tmp_len = lowercase_letter.length();
		for (int i = 0; i < tmp_len; ++i) {
			tmp_idx = CharacterProcessor.getAsciiCharOrdinal(lowercase_letter
					.charAt(i));
			UNRESERVED_CHAR_LABEL[tmp_idx] = true; // 设置非保留字符中的小写字母
			P_CHAR_EXCLUDE_PCT_LABEL[tmp_idx] = true; // 设置pchar中的小写字母
		}

		tmp_len = uppercase_letter.length();
		for (int i = 0; i < tmp_len; ++i) {
			tmp_idx = CharacterProcessor.getAsciiCharOrdinal(uppercase_letter
					.charAt(i));
			UNRESERVED_CHAR_LABEL[tmp_idx] = true; // 设置非保留字符中的大写字母
			P_CHAR_EXCLUDE_PCT_LABEL[tmp_idx] = true; // 设置pchar中的大写字母
		}
		tmp_len = unreserved_char_exclude_letter_digit.length();
		for (int i = 0; i < tmp_len; ++i) {
			tmp_idx = CharacterProcessor
					.getAsciiCharOrdinal(unreserved_char_exclude_letter_digit
							.charAt(i));
			UNRESERVED_CHAR_LABEL[tmp_idx] = true; // 设置除字母数字外的非保留字符
			P_CHAR_EXCLUDE_PCT_LABEL[tmp_idx] = true; // 设置除字母数字外的pchar
		}

		String sub_delims = "!$&'()*+,;="; // 子分隔符
		tmp_len = sub_delims.length();
		for (int i = 0; i < tmp_len; ++i) {
			tmp_idx = CharacterProcessor.getAsciiCharOrdinal(sub_delims
					.charAt(i));
			P_CHAR_EXCLUDE_PCT_LABEL[tmp_idx] = true; // 设置作为子分隔符的pchar
		}

		P_CHAR_EXCLUDE_PCT_LABEL[CharacterProcessor.getAsciiCharOrdinal(':')] = true; // :
																						// 也可以是pchar
		P_CHAR_EXCLUDE_PCT_LABEL[CharacterProcessor.getAsciiCharOrdinal('@')] = true; // @
																						// 也可以使pchar

		for (int i = 0; i < ONE_BYTE_SIZE; ++i) {
			ENCODE_STRING[i] = encodeInt2PCT(i); // 设置 byte 对应的PCT
		}

		String[] default_file_names = { // 默认文件名
		"index.asp", "index.aspx", "index.jsp", "index.php", "index.xml",
				"index.htm", "index.html", "index.shtml", "index.xhtml",
				"indexpage.asp", "indexpage.aspx", "indexpage.jsp",
				"indexpage.php", "indexpage.xml", "indexpage.htm",
				"indexpage.html", "indexpage.shtml", "indexpage.xhtml",

				"default.asp", "default.aspx", "default.jsp", "default.php",
				"default.xml", "default.htm", "default.html", "default.shtml",
				"default.xhtml",

				"home.asp", "home.aspx", "home.jsp", "home.php", "home.xml",
				"home.htm", "home.html", "home.shtml", "home.xhtml",
				"homepage.asp", "homepage.aspx", "homepage.jsp",
				"homepage.php", "homepage.xml", "homepage.htm",
				"homepage.html", "homepage.shtml", "homepage.xhtml", };

		tmp_len = default_file_names.length;
		for (int i = 0; i < tmp_len; ++i) {
			DEFAULT_FILE_NAME_SET.add(default_file_names[i]); // 设置默认文件名
		}
	}

	// 是不是非ascii编码的字符
	private static boolean isNonAsciiCodingFormatFlagInPath(char c) {
		return CharacterProcessor.isNonAsciiChar(c)
				|| c == CharacterProcessor.ASCII_NULL_CHAR;
	}

	// 是不是非保留字符
	private static boolean isUnreservedChar(char c) {
		return CharacterProcessor.isAsciiChar(c) ? UNRESERVED_CHAR_LABEL[c]
				: false;
	}

	private String scheme = null; // 协议
	private String authority = null; // 授权
	private String userInfo = null; // 用户信息
	private String host = null; // 主机
	private String port = null; // 端口
	private String path = null; // 路径
	private String query = null; // 查询串
	private String fragment = null; // 定位符

	public String getScheme() {
		return scheme;
	}

	public String getAuthority() {
		return authority;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getPath() {
		return path;
	}

	public String getQuery() {
		return query;
	}

	public String getFragment() {
		return fragment;
	}

	// 构造函数
	private HttpURL(String scheme, String authority, String user_info,
			String host, String port, String path, String query, String fragment) {

		this.scheme = scheme;
		this.authority = authority;
		this.userInfo = user_info;
		this.host = host;
		this.port = port;
		this.path = path;
		this.query = query;
		this.fragment = fragment;

	}

	// 打印函数
	public void print() {
		System.out.println("scheme:\t\t" + scheme);
		System.out.println("authority:\t" + authority);
		System.out.println("userInfo:\t" + userInfo);
		System.out.println("host:\t\t" + host);
		System.out.println("port:\t\t" + port);
		System.out.println("path:\t\t" + path);
		System.out.println("query:\t\t" + query);
		System.out.println("fragment:\t" + fragment);
	}

	// 解析函数，将字符串解析成类
	public static HttpURL parseURL(String spec) {

		// 判断是否是空，这里注释掉，方便定位输入null的错误
		// if(spec==null)
		// return null;

		// 设置末尾的下标的初始值
		int end = spec.length();
		// 检查长度
		if (end < MIN_URL_LEN || end > MAX_URL_LEN) {
			return null;
		}

		// 设置首位下标的初始值
		int start = 0;

		// 处理末尾的空白字符
		while (end > 0
				&& CharacterProcessor.isGenericWhiteSpace(spec.charAt(end - 1))) {
			--end;
		}

		// 处理开始的空白字符
		while (start < end
				&& CharacterProcessor.isGenericWhiteSpace(spec.charAt(start))) {
			++start;
		}

		// 处理以 url: 开头的标记
		if (spec.regionMatches(true, start, "url:", 0, 4)) {
			start += 4;
			// 再次处理开始的空白字符
			while (start < end
					&& CharacterProcessor.isGenericWhiteSpace(spec
							.charAt(start))) {
				++start;
			}
		}

		// 再次检查长度
		if (end - start < MIN_URL_LEN) {
			return null;
		}

		// 设置探测末尾坐标
		int detect_end = start + MAX_DETECT_SCHEME_LEN;
		if (detect_end > end) {
			detect_end = end;
		}

		char tmp_c = '\u0000'; // 存储临时扫描字符
		String raw_scheme = null; // 粗糙的scheme
		String raw_authority = null; // 粗糙的authority
		String raw_user_info = null; // 粗糙的userinfo
		String raw_host = null; // 粗糙的host

		int at_idx_in_spec = -1; // @的位置

		// 探测scheme及authority
		int detect_idx = start;
		for (; detect_idx < detect_end; ++detect_idx) {
			tmp_c = spec.charAt(detect_idx);
			if (tmp_c == ':') { // 探测到scheme的分割符
				if (end - detect_idx <= MIN_URL_LEN) { // 检查长度
					return null;
				}
				if (spec.charAt(detect_idx + 1) != '/'
						|| spec.charAt(detect_idx + 2) != '/') { // 检查后续字符
					return null;
				}
				if (at_idx_in_spec != -1) { // scheme中不能含有@
					return null;
				}
				raw_scheme = spec.substring(start, detect_idx); // 存储scheme
				start = detect_idx + 3;
				break;
			} else if (tmp_c == '/' || tmp_c == '?' || tmp_c == '#') { // 检测到其它部分的分隔符
				if (detect_idx == start) {
					if (tmp_c == '/' && spec.charAt(detect_idx + 1) == '/') { // 网络路径开头
						start = detect_idx + 2;
						break;
					} else {
						return null;
					}
				}
				raw_authority = spec.substring(start, detect_idx); // 存储authority
				if (at_idx_in_spec != -1) { // 探测到含有userinfo
					raw_user_info = spec.substring(start, at_idx_in_spec); // 存储userinfo
					raw_host = spec.substring(at_idx_in_spec + 1, detect_idx);// 存储host
				} else {
					raw_host = raw_authority; // 存储host
				}
				start = detect_idx;
				break;
			} else if (tmp_c == '@' && at_idx_in_spec == -1) { // 第一次探测到userinfo的分割符
				at_idx_in_spec = detect_idx;
			}
		}

		// 探测的扫尾工作，这里肯定不含 :
		if (detect_idx == end) {
			raw_authority = spec.substring(start, end); // 存储authority
			if (at_idx_in_spec == -1) {
				raw_host = raw_authority; // 存储host
			} else { // 含有userinfo
				raw_user_info = spec.substring(start, at_idx_in_spec); // 存储user_info
				raw_host = spec.substring(at_idx_in_spec + 1, end); // 存储host
			}

			return new HttpURL(raw_scheme, raw_authority, raw_user_info,
					raw_host, null, EMPTY_PATH, null, null);
		}

		int colon_idx_in_spec = -1; // : 的位置
		String raw_port = null; // 粗糙的port

		// 继续解析authority
		if (raw_authority == null) {
			int i = (detect_idx == detect_end ? detect_end : start); // 设置起始扫描位置
			for (; i < end; ++i) {
				tmp_c = spec.charAt(i);
				if (tmp_c == '/' || tmp_c == '?' || tmp_c == '#') { // 检测到其它分隔符
					break;
				} else if (tmp_c == ':') { // 记录最新探测到的 :
					colon_idx_in_spec = i;
				} else if (tmp_c == '@' && at_idx_in_spec == -1) { // 第一次探测到userinfo的分割符
					at_idx_in_spec = i;
					colon_idx_in_spec = -1;
				}
			}

			if (i - start < MIN_AUTH_LEN) { // 检查长度
				return null;
			}
			raw_authority = spec.substring(start, i); // 存储authority
			int host_begin_idx = start; // 设置host起始的初始下标
			int host_end_idx = i; // 设置host结束的初始下标
			if (at_idx_in_spec != -1) { // 存在 @
				host_begin_idx = at_idx_in_spec + 1;
				if (host_end_idx - host_begin_idx < MIN_HOST_LEN) { // host 长度太短
					return null;
				}
				raw_user_info = spec.substring(start, at_idx_in_spec); // 存储user_info
			}
			if (colon_idx_in_spec != -1) { // 探测到:
				if (spec.charAt(host_begin_idx) == '[') { // IPV6或IP-future的host
					int right_s_b_idx = raw_authority.lastIndexOf(']'); // 检查结尾字符
																		// ]
					if (right_s_b_idx == -1) { // 不含结尾，格式错误
						return null;
					} else {
						right_s_b_idx += start; // 由raw_authority中的下标转换为spec中的下标
						if (right_s_b_idx - host_begin_idx < MIN_HOST_LEN) { // host
																				// 长度太短
							return null;
						}
						if (colon_idx_in_spec <= right_s_b_idx) { // [ ] 中的 :
							if (right_s_b_idx != host_end_idx - 1) { // 此时host结尾必须是
																		// ]
								return null;
							}
							colon_idx_in_spec = -1; // 不算port分隔符
						} else {
							if (colon_idx_in_spec != right_s_b_idx + 1) { // :
																			// 必须刚好在
																			// ]
																			// 后
								return null;
							}
							raw_port = spec.substring(colon_idx_in_spec + 1, i); // 存储port
							host_end_idx = colon_idx_in_spec; // 更新host末尾下标
						}
					}
				} else {
					raw_port = spec.substring(colon_idx_in_spec + 1, i); // 存储port
					host_end_idx = colon_idx_in_spec; // 更新host末尾下标
				}
			}
			raw_host = spec.substring(host_begin_idx, host_end_idx); // 存储host
			start = i;

			if (i == end) {
				return new HttpURL(raw_scheme, raw_authority, raw_user_info,
						raw_host, raw_port, EMPTY_PATH, null, null);
			}
		}

		// 解析path
		String raw_path = EMPTY_PATH;
		if (tmp_c == '/') { // path不为 空
			int i = start + 1; // 设置起始扫描位置
			if (i == end) { // 到尾部了
				return new HttpURL(raw_scheme, raw_authority, raw_user_info,
						raw_host, raw_port, DEFAULT_NORMALIZED_PATH, null, null);
			}

			for (; i < end; ++i) { // 扫描结束path的结束标志
				tmp_c = spec.charAt(i);
				if (tmp_c == '?' || tmp_c == '#') {
					break;
				}
			}

			if (i == start + 1) { // 存储path
				raw_path = DEFAULT_NORMALIZED_PATH;
			} else {
				raw_path = spec.substring(start, i);
			}
			start = i;

			if (i == end) { // 到尾部了
				return new HttpURL(raw_scheme, raw_authority, raw_user_info,
						raw_host, raw_port, raw_path, null, null);
			}
		}

		// 解析query
		String raw_query = null;
		if (tmp_c == '?') { // 是有query的

			int i = ++start; // 设置起始扫描位置
			if (i == end) { // 到尾部了
				return new HttpURL(raw_scheme, raw_authority, raw_user_info,
						raw_host, raw_port, raw_path, EMPTY_QUERY, null);
			}
			for (; i < end; ++i) { // 扫描query的结束标志
				tmp_c = spec.charAt(i);
				if (tmp_c == '#') {
					break;
				}
			}

			if (i == start) { // 存储query
				raw_query = EMPTY_QUERY;
			} else {
				raw_query = spec.substring(start, i);
			}
			start = i;

			if (i == end) { // 到尾部了
				return new HttpURL(raw_scheme, raw_authority, raw_user_info,
						raw_host, raw_port, raw_path, raw_query, null);
			}
		}

		// 解析fragement
		String raw_fragment = null;
		if (tmp_c == '#') {
			++start; // 设置起始位置
			if (start == end) { // 到尾部了
				raw_fragment = EMPTY_FRAGMENT;
			} else {
				raw_fragment = spec.substring(start, end);
			}
		}

		// 最终返回
		return new HttpURL(raw_scheme, raw_authority, raw_user_info, raw_host,
				raw_port, raw_path, raw_query, raw_fragment);
	}

	// 检查index处后是否是PCT格式
	private static boolean checkPCTFormatAfterPC(String spec, int index) {
		return (spec.length() > index + 2)
				&& CharacterProcessor.isAsciiHexDigit(spec.charAt(++index))
				&& CharacterProcessor.isAsciiHexDigit(spec.charAt(++index));
	}

	// 检查index处后是否是大写的PCT格式
	private static boolean checkUpperCasePCTFormatAfterPC(String spec, int index) {
		return (spec.length() > index + 2)
				&& CharacterProcessor.isAsciiUpperCaseHexDigit(spec
						.charAt(++index))
				&& CharacterProcessor.isAsciiUpperCaseHexDigit(spec
						.charAt(++index));
	}

	// 将一个PCT反转义为char
	private static char unsafeDecodeOnePCT2Char(String spec, int index) {
		return (char) ((CharacterProcessor.parseAsciiHexDigit(spec
				.charAt(++index)) << 4) | CharacterProcessor
				.parseAsciiHexDigit(spec.charAt(++index)));
	}

	// 将一个PCT反转义为int
	private static int unsafeDecodeOnePCT2Idx(String spec, int index) {
		return (CharacterProcessor.parseAsciiHexDigit(spec.charAt(++index)) << 4)
				| CharacterProcessor.parseAsciiHexDigit(spec.charAt(++index));
	}

	// 将一个byte转义为PCT
	private static String encodeOneBye2PCT(byte b) {
		char[] v = new char[3];
		int num;
		v[0] = '%';
		num = (b & 0xF0) >>> 4; // 设置第一个数字
		if (num < 10) {
			v[1] = (char) (CharacterProcessor.ASCII_DIGIT_BASELINE + num);
		} else {
			v[1] = (char) (CharacterProcessor.ASCII_UPPERCASE_HEX_LETTER_BASELINE + num);
		}

		num = b & 0x0F; // 设置第二个数字
		if (num < 10) {
			v[2] = (char) (CharacterProcessor.ASCII_DIGIT_BASELINE + num);
		} else {
			v[2] = (char) (CharacterProcessor.ASCII_UPPERCASE_HEX_LETTER_BASELINE + num);
		}
		return new String(v);
	}

	// 将一个int转义为PCT
	private static String encodeInt2PCT(int i) {
		if (i < 0 || i >= ONE_BYTE_SIZE) {
			return null;
		}
		return encodeOneBye2PCT((byte) i);
	}

	// 寻找int对应的PCT
	private static String findPCT4OneInt(int i) {
		return (i < 0 || i >= ONE_BYTE_SIZE) ? null : ENCODE_STRING[i];
	}

	// 寻找byte对应的PCT
	private static String findPCT4OneByte(byte b) {
		return findPCT4OneInt(b & 0xFF);
	}

	// 是否是默认的文件名
	private static boolean isDefaultFileName(String name) {
		return DEFAULT_FILE_NAME_SET.contains(name);
	}

	// 是否是合法的scheme
	private static boolean isValidScheme(String scheme) {
		for (String valid_scheme : VALID_SCHEME) {
			if (scheme.equalsIgnoreCase(valid_scheme))
				return true;
		}
		return false;
	}

	// 归一化scheme
	private static String normalizeScheme(String scheme) {
		return isValidScheme(scheme) ? DEFAULT_SCHEME : null;
	}

	// 归一化 user_info, 目前做法是直接返回空
	private static String normalizeUserInfo(String user_info) {
		return EMPTY_USER_INFO;
	}

	// 归一化host
	private static String normalizeHost(String host) {
		int len = host.length();
		if (len < MIN_HOST_LEN)
			return null;

		if (host.charAt(0) == '[') { // IPV6 或 IP-future
			if (host.charAt(len - 1) == ']')
				return normalizeIPV6Host(host);
			else
				return null;
		}

		boolean no_trailing_dot = true;
		if (host.charAt(len - 1) == '.') { // 去除结尾的点
			if (len <= MIN_HOST_LEN) {
				return null;
			} else {
				no_trailing_dot = false;
				host = host.substring(0, --len);
			}
		} else if (host.regionMatches(true, len - 3, "%2E", 0, 3)) { // 去除结尾转义后的点
			if (len < MIN_HOST_LEN + 3) {
				return null;
			} else {
				no_trailing_dot = false;
				len -= 3;
				host = host.substring(0, len);
			}
		}

		boolean all_is_digit = true; // 全数字标志
		int need_process_idx = -1; // 需要处理的坐标
		int dot_cnt = 0; // 点的个数

		char last_c = '.'; // 上一个字符
		char tmp_c = '\u0000'; // 存储当前临时字符
		for (int i = 0; i < len; ++i) {
			tmp_c = host.charAt(i);
			if (tmp_c == '%') { // 当前是%
				need_process_idx = i;
				break;
			} else if (last_c == '.') { // 上一个字符是 .
				if (CharacterProcessor.isAsciiLowerCaseLetter(tmp_c)) { // 是小写字母
					all_is_digit = false;
				} else if (CharacterProcessor.isAsciiDigit(tmp_c)) { // 是数字
					// do nothing here
				} else if (CharacterProcessor.isAsciiUpperCaseLetter(tmp_c)) { // 是大写字母
					all_is_digit = false;
					need_process_idx = i;
					break;
				} else { // 其它
					return null;
				}
			} else if (tmp_c == '.') { // 当前字符是 .
				++dot_cnt;
				if (CharacterProcessor.isAsciiLowerCaseLetter(last_c)
						|| CharacterProcessor.isAsciiDigit(last_c)) { // 小写字母或数字
					// do nothing here
				} else { // 返回null
					return null;
				}
			} else { // 其它
				if (CharacterProcessor.isAsciiLowerCaseLetter(tmp_c)
						|| tmp_c == '-') { // 是小写字母或-
					all_is_digit = false;
				} else if (CharacterProcessor.isAsciiDigit(tmp_c)) { // 是数字
					// do nothing here
				} else if (CharacterProcessor.isAsciiUpperCaseLetter(tmp_c)) { // 是大写字母
					all_is_digit = false;
					need_process_idx = i;
					break;
				} else { // 返回null
					return null;
				}
			}

			last_c = tmp_c;
		}

		if (need_process_idx == -1) { // 不需要二次处理
			if (last_c == '.' || last_c == '-') { // 结尾不能是 . 或 -
				return null;
			}

			if (all_is_digit) { // 处理全数字host
				if (dot_cnt == 3 && no_trailing_dot) {
					return normalizeIPV4Host(StringProcessor
							.splitString2ArrayList(host, '.'));
				} else {
					return null;
				}
			} else { // 处理非全数字host
				if (dot_cnt > 0) {
					if (host.regionMatches(0, "www.", 0, 4)) { // 去除开始的www
						if (dot_cnt < 2 || len < MIN_HOST_LEN + 4) { // 检查格式及host长度
							return null;
						} else {
							if (len - host.lastIndexOf('.') <= MIN_TOP_DOMAIN_LEN) { // 检查top
																						// domain的长度
								return null;
							} else {
								return host.substring(4);
							}
						}
					} else { // 直接返回host
						if (len - host.lastIndexOf('.') <= MIN_TOP_DOMAIN_LEN) { // 检查top
																					// domain的长度
							return null;
						} else {
							return host;
						}
					}
				} else {
					return null;
				}
			}
		} else { // 需二次处理
			StringBuilder sb = new StringBuilder();
			if (need_process_idx != 0) {
				sb.append(host, 0, need_process_idx); // copy已处理过的
			}

			for (int i = need_process_idx; i < len; ++i) { // 二次处理
				tmp_c = host.charAt(i);
				if (tmp_c == '%') { // 当前是%
					if (checkPCTFormatAfterPC(host, i)) { // 检查一下PCT格式
						tmp_c = (char) (unsafeDecodeOnePCT2Idx(host, i)); // 反转义
						i += 2;
					} else {
						return null;
					}
				}

				if (last_c == '.') { // 上一个字符是 .
					if (CharacterProcessor.isAsciiLowerCaseLetter(tmp_c)) { // 是小写字母
						all_is_digit = false;
					} else if (CharacterProcessor.isAsciiDigit(tmp_c)) { // 是数字
						// do nothing here
					} else if (CharacterProcessor.isAsciiUpperCaseLetter(tmp_c)) { // 是大写字母
						all_is_digit = false;
						tmp_c=CharacterProcessor.toAsciiLowerCaseChar(tmp_c); //大写转小写
					} else {
						return null;
					}
				} else if (tmp_c == '.') { // 当前字符是 .
					++dot_cnt;
					if (CharacterProcessor.isAsciiLowerCaseLetter(last_c)
							|| CharacterProcessor.isAsciiDigit(last_c)) { // 小写字母或数字
						// do nothing here
					} else {
						return null;
					}
				} else {
					if (CharacterProcessor.isAsciiLowerCaseLetter(tmp_c)
							|| tmp_c == '-') { // 小写
						all_is_digit = false;
					} else if (CharacterProcessor.isAsciiDigit(tmp_c)) { // 是数字
						// do nothing here
					} else if (CharacterProcessor.isAsciiUpperCaseLetter(tmp_c)) { // 是大写字母
						all_is_digit = false;
						tmp_c=CharacterProcessor.toAsciiLowerCaseChar(tmp_c); //大写转小写
					} else {
						return null;
					}
				}
				sb.append(tmp_c);
				last_c = tmp_c;
			}

			if (all_is_digit || last_c == '.' || last_c == '-') { // 全是数字，或者结尾是.
																	// 或 -
																	// ，返回null
				return null;
			}

			if (dot_cnt > 0) {
				if (sb.charAt(0) == 'w' && sb.charAt(1) == 'w'
						&& sb.charAt(2) == 'w' && sb.charAt(3) == '.') { // 去除www
					if (dot_cnt < 2 || sb.length() < MIN_HOST_LEN + 4) { // 检查格式及host长度
						return null;
					} else {
						if (sb.length() - sb.lastIndexOf(".") <= MIN_TOP_DOMAIN_LEN) { // 检查top
																						// domain的长度
							return null;
						} else {
							return sb.substring(4);
						}
					}
				} else {
					if (sb.length() - sb.lastIndexOf(".") <= MIN_TOP_DOMAIN_LEN) { // 检查top
																					// domain的长度
						return null;
					} else {
						return sb.toString();
					}
				}
			} else {
				return null;
			}
		}
	}

	// ----------------归一化IPV4Host，效率不够高，需改
	private static String normalizeIPV4Host(ArrayList<String> parts) {
		if (parts.size() != 4)
			return null;

		int a, b, c, d; // 各部分检查
		try {
			a = Integer.parseInt(parts.get(0));
			if (a < IP_SEG_MIN || a > IP_SEG_MAX)
				return null;
			b = Integer.parseInt(parts.get(1));
			if (b < IP_SEG_MIN || b > IP_SEG_MAX)
				return null;
			c = Integer.parseInt(parts.get(2));
			if (c < IP_SEG_MIN || c > IP_SEG_MAX)
				return null;
			d = Integer.parseInt(parts.get(3));
			if (d < IP_SEG_MIN || d > IP_SEG_MAX)
				return null;
		} catch (NumberFormatException e) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(a);
		sb.append('.');
		sb.append(b);
		sb.append('.');
		sb.append(c);
		sb.append('.');
		sb.append(d);

		return sb.toString();
	}

	// 归一化IPV6Host, 目前做法是直接舍弃掉
	private static String normalizeIPV6Host(String host) {
		return null;
	}

	// ---------------归一化port，效率不够高，需改
	private static String normalizePort(String port) {
		if (port.length() == 0 || port.equals(DEFAULT_HTTP_PORT)) {
			return EMPTY_PORT;
		}
		int port_num = -1;
		try {
			port_num = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			return null;
		}

		if (port_num == 80) {
			return EMPTY_PORT;
		} else if (port_num >= PORT_NUM_MIN && port_num <= PORT_NUM_MAX) {
			return Integer.toString(port_num);
		} else {
			return null;
		}
	}

	// 归一化path
	private static String normalizePath(String path,
			boolean contain_query_or_fragment, Charset cs)
			throws UnsupportedEncodingException {
		final int len = path.length();
		if (len == 0 || path.equals(DEFAULT_NORMALIZED_PATH)) { // 初查
			return DEFAULT_NORMALIZED_PATH;
		}
		if (path.charAt(0) != '/') { // path必须以 / 开头
			return null;
		}

		int tmp_code = 0; // 临时存储 codepoint
		char tmp_c = '\u0000'; // 临时存储char
		char test_c = '\u0000'; // 尝试用的char
		int need_process_idx = -1; // 需要处理的坐标
		int last_slash_idx = -1; // 上一个 / 的坐标
		int slash_cnt = 0; // / 的计数器
		boolean can_decode_pct = true; // 判断是否需要进行PCT解码
		int tail_idx = len - 1; // 末尾字符下标
		int tail_2_idx = len - 2; // 倒数第二个字符的下标
		int tail_3_idx = len - 3; // 倒数第三个字符的下标
		boolean path_end_with_slash = (path.charAt(tail_idx) == '/'); // 是否以 /
																		// 结尾

		for (int i = 0; i < len; ++i) {
			tmp_code = path.codePointAt(i);
			if (tmp_code > CharacterProcessor.SINGLE_CHAR_CODE_MAX) { // 双字节的
				need_process_idx = i;
				break;
			}
			tmp_c = (char) tmp_code; // 当前char

			if (tmp_c == '%') { // 是%
				if (checkUpperCasePCTFormatAfterPC(path, i)) { // 是不是大写的PCT
					if (can_decode_pct) { // 尝试解码
						test_c = unsafeDecodeOnePCT2Char(path, i);
						if (isNonAsciiCodingFormatFlagInPath(test_c)) { // 后续是否需要进行PCT解码
							can_decode_pct = false;
						} else {
							if (isUnreservedChar(test_c)) { // 是非保留字符
								need_process_idx = i;
								break;
							}
						}
					}
				} else { // PCT不是大写
					need_process_idx = i;
					break;
				}

				i += 2;
			} else if (tmp_c == '/') { // 是 /
				++slash_cnt;
				last_slash_idx = i;

				if (i < tail_3_idx) { // 剩下3及以上个字符
					test_c = path.charAt(i + 1);
					if (test_c == '.' && path.charAt(i + 2) == '.'
							&& path.charAt(i + 3) == '/') { // 是 /../
						need_process_idx = i;
						break;
					} else if (test_c == '.' && path.charAt(i + 2) == '/') { // 是
																				// /./
						need_process_idx = i;
						break;
					} else if (test_c == '/') { // 是 //
						need_process_idx = i;
						break;
					}
				} else if (i == tail_3_idx) { // 剩下2个字符
					test_c = path.charAt(i + 1);
					if (test_c == '.' && path.charAt(i + 2) == '.') { // 是 /..
						need_process_idx = i;
						break;
					} else if (test_c == '.' && path.charAt(i + 2) == '/') { // 是
																				// /./
						return path.substring(0, i + 1);
					} else if (test_c == '/') { // 是 //
						need_process_idx = i;
						break;
					}
				} else if (i == tail_2_idx) { // 剩下1个字符
					test_c = path.charAt(i + 1);
					if (test_c == '.') { // 是 /.
						return path.substring(0, i + 1);
					} else if (test_c == '/') { // 是//
						need_process_idx = i;
						break;
					}
				} else if (i == tail_idx) { // 是末尾
					break; // 不设置need_process_idx
				}

				can_decode_pct = true;
			} else if (isUnreservedChar(tmp_c)
					&& !CharacterProcessor.isAsciiUpperCaseLetter(tmp_c)) { // 是非保留字符，且不是大写
				can_decode_pct = true;
			} else {
				need_process_idx = i;
				break;
			}
		}

		if (need_process_idx == -1) { // 不需要二次处理
			if (path_end_with_slash) { // path以 / 结束
				if (contain_query_or_fragment) { // 含有query，直接返回
					return path;
				} else { // 不含query
					int scecond_to_last_slash_idx = path.lastIndexOf('/',
							tail_2_idx);
					if (scecond_to_last_slash_idx == -1) { // 出错返回
						return null;
					}
					String last_part = path.substring(
							scecond_to_last_slash_idx + 1, last_slash_idx); // 最后一个part
					if (unsafeIsFileFormat(last_part)) { // 是文件名的格式
						if (isDefaultFileName(last_part)) { // 是默认文件名
							return path.substring(0,
									scecond_to_last_slash_idx + 1);
						} else { // 不是默认文件名
							return path.substring(0, last_slash_idx);
						}
					} else { // 不是文件名的格式
						return path;
					}
				}
			} else { // path不以 / 结束
				String last_part = path.substring(last_slash_idx + 1); // 最后一个part
				if (unsafeIsFileFormat(last_part)) { // 是文件名的格式
					if (isDefaultFileName(last_part)) { // 是默认文件名
						return path.substring(0, last_slash_idx + 1);
					} else { // 不是默认文件名
						return path;
					}
				} else { // 不是文件名的格式
					if (contain_query_or_fragment) { // 含有query
						return path;
					} else { // 补 /
						return path + '/';
					}
				}
			}
		}

		ArrayList<String> segments = StringProcessor.splitString2ArrayList(
				path, '/');// 用 / 分割path
		final int seg_num = segments.size(); // 分割的段数
		ArrayList<String> stack = new ArrayList<String>(); // 模拟寻找路径的栈
		boolean path_end_with_dot_dir = false; // 末尾是否包含 . 或 ..

		if (slash_cnt > seg_num) { // 出错返回
			return null;
		}

		for (int i = 1; i < slash_cnt; ++i) { // 不需处理的部分入栈
			stack.add(segments.get(i));
		}

		String one_part = null;
		for (int i = slash_cnt; i < seg_num; ++i) {
			one_part = normalizeOnePathPart(segments.get(i), cs); // 归一化当前part
			if (one_part == null) { // 出错返回
				return null;
			} else if (one_part.length() == 0) { // 空part
				// do nothing here
			} else if (one_part.equals(".")) { // 是 .
				path_end_with_dot_dir = true;
			} else if (one_part.equals("..")) { // 是 ..
				path_end_with_dot_dir = true;
				if (!stack.isEmpty()) {
					stack.remove(stack.size() - 1); // 前一part出栈
				}
			} else {
				path_end_with_dot_dir = false;
				stack.add(one_part); // 入栈
			}
		}

		int stack_sze = stack.size();
		if (stack_sze == 0) {
			return DEFAULT_NORMALIZED_PATH; // 返回default
		} else if (stack_sze == 1) { // 只有一个part时
			String last_part = stack.get(0); // 最后一个part
			if (contain_query_or_fragment) { // 含有query
				if (path_end_with_slash || path_end_with_dot_dir) { // 结尾是dir
					return '/' + last_part + '/';
				} else { // 结尾不是
					return '/' + last_part;
				}
			} else { // 不含query
				if (path_end_with_dot_dir) { // 结尾是dir
					return '/' + last_part + '/';
				} else { // 结尾需判断
					if (unsafeIsFileFormat(last_part)) { // 是文件名格式
						if (isDefaultFileName(last_part)) { // 是默认文件名
							return DEFAULT_NORMALIZED_PATH;
						} else { // 不是默认文件名
							return '/' + last_part;
						}
					} else { // 结尾是dir的形式
						return '/' + last_part + '/'; // 补 /
					}
				}
			}
		} else {
			StringBuilder sb = new StringBuilder();
			--stack_sze;
			for (int i = 0; i < stack_sze; ++i) { // 拼接前面的part
				sb.append('/');
				sb.append(stack.get(i));
			}
			sb.append('/');

			String last_part = stack.get(stack_sze); // 最后一个part
			if (contain_query_or_fragment) { // 含有query
				if (path_end_with_slash || path_end_with_dot_dir) { // 结尾是dir
					sb.append(last_part);
					sb.append('/');
				} else { // 结尾不是dir
					sb.append(last_part);
				}
			} else { // 不含query
				if (path_end_with_dot_dir) { // 结尾是dir
					sb.append(last_part);
					sb.append('/');
				} else { // 结尾需判断
					if (unsafeIsFileFormat(last_part)) { // 是文件名格式
						if (!isDefaultFileName(last_part)) { // 不是默认文件名
							sb.append(last_part);
						}
					} else { // 结尾是dir的形式
						sb.append(last_part);
						sb.append('/');
					}
				}
			}
			return sb.toString();
		}
	}

	// 检查是否是文件名格式
	private static boolean unsafeIsFileFormat(String str) {
		final int len = str.length();
		if (len < 3) {
			return false;
		} else {
			int idx = str.indexOf('.', 1);
			if (idx == -1 || idx == len - 1) {
				return false;
			}
			return true;
		}
	}

	// 归一化每一个path 的 part
	private static String normalizeOnePathPart(String one_part, Charset cs)
			throws UnsupportedEncodingException {
		return processStringPart(one_part, cs);
	}

	// 字符转码成PCT
	private static byte[] encodeSingleCodePointString2Bytes(
			String single_code_point_str, Charset cs)
			throws UnsupportedEncodingException {
		if (cs == null) {
			cs = DEFAULT_CHAR_SET;
		}
		int c = single_code_point_str.codePointAt(0);
		byte[] bs = single_code_point_str.getBytes(cs);
		if ((bs != null && bs.length > 1) || c == QUESTION_MARK_CODE_POINT) {
			return bs;
		} else {
			if (bs == null || bs.length == 0 || bs[0] == ERROR_ENCODE_BYTE) {
				throw new UnsupportedEncodingException();
			} else {
				return bs;
			}
		}
	}

	// 归一化String
	private static String processStringPart(String str, Charset cs)
			throws UnsupportedEncodingException {
		final int len = str.length();
		if (len == 0)
			return EMPTY_STRING;
		else {
			int tmp_code = 0; // 临时存储codepoint
			char tmp_c = '\u0000'; // 临时存储char
			char test_c = '\u0000'; // 尝试用的char
			boolean can_decode_pct = true; // 判断是否需要进行PCT解码

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < len; ++i) {
				tmp_code = str.codePointAt(i);
				if (tmp_code > CharacterProcessor.SINGLE_CHAR_CODE_MAX) { // 双字节
					byte[] bs = encodeSingleCodePointString2Bytes(str
							.substring(i, i + 2), cs); // 编码
					// if(bs==null || bs.length==0){
					// return null;
					// }
					for (byte b : bs) {
						sb.append(findPCT4OneByte(b)); // 拼接PCT
					}

					++i;
					continue;
				}

				tmp_c = (char) tmp_code;

				if (tmp_c == '%') { // 是 %
					if (checkPCTFormatAfterPC(str, i)) { // 检查PCT格式
						if (checkUpperCasePCTFormatAfterPC(str, i)) { // 检查是否是大写PCT
							if (can_decode_pct) {
								test_c = unsafeDecodeOnePCT2Char(str, i); // 尝试解码
								if (isNonAsciiCodingFormatFlagInPath(test_c)) { // 后续是否需要进行PCT解码
									can_decode_pct = false;
									sb.append(str, i, i + 3);
								} else {
									if (isUnreservedChar(test_c)) { // 是否是非保留字符
										// test_c =
										// CharacterProcessor.toAsciiLowerCaseChar(test_c);
										sb.append(test_c);
									} else {
										sb.append(str, i, i + 3);
									}
								}
							} else { // 直接拼接
								sb.append(str, i, i + 3);
							}
						} else {
							if (can_decode_pct) {
								test_c = unsafeDecodeOnePCT2Char(str, i); // 尝试解码
								if (isNonAsciiCodingFormatFlagInPath(test_c)) { // 后续是否需要进行PCT解码
									can_decode_pct = false;
									sb.append('%'); // 拼装
									sb.append(CharacterProcessor
											.toAsciiUpperCaseChar(str
													.charAt(i + 1)));
									sb.append(CharacterProcessor
											.toAsciiUpperCaseChar(str
													.charAt(i + 2)));
								} else {
									if (isUnreservedChar(test_c)) { // 是非保留字符
										// test_c =
										// CharacterProcessor.toAsciiLowerCaseChar(test_c);
										sb.append(test_c);
									} else { // 拼装
										sb.append('%');
										sb.append(CharacterProcessor
												.toAsciiUpperCaseChar(str
														.charAt(i + 1)));
										sb.append(CharacterProcessor
												.toAsciiUpperCaseChar(str
														.charAt(i + 2)));
									}
								}
							} else { // 拼装
								sb.append('%');
								sb
										.append(CharacterProcessor
												.toAsciiUpperCaseChar(str
														.charAt(i + 1)));
								sb
										.append(CharacterProcessor
												.toAsciiUpperCaseChar(str
														.charAt(i + 2)));
							}
						}
						i += 2;
					} else { // % 需转义
						sb.append(findPCT4OneInt(tmp_c));
						can_decode_pct = true;
					}
				} else if (isUnreservedChar(tmp_c)) { // 是非保留字符
					// tmp_c = CharacterProcessor.toAsciiLowerCaseChar(tmp_c);//
					// 大写转小写
					sb.append(tmp_c);
					can_decode_pct = true;
				} else {
					if (CharacterProcessor.isAsciiChar(tmp_c)) { // 非保留的ascii需转义成PCT
						sb.append(findPCT4OneInt(tmp_c));
					} else { // 其余字符转义成PCT
						byte[] bs = encodeSingleCodePointString2Bytes(str
								.substring(i, i + 1), cs); // 编码
						// if(bs==null || bs.length==0){
						// return null;
						// }
						for (byte b : bs) {
							sb.append(findPCT4OneByte(b));
						}
					}
					can_decode_pct = true;
				}
			}
			return sb.toString();
		}
	}

	// 比较器
	private static Comparator<Entry<String, String>> comp = new Comparator<Entry<String, String>>() {
		public int compare(Entry<String, String> o1, Entry<String, String> o2) {
			return o1.getKey().compareTo(o2.getKey());
		}
	};

	// 归一化 query
	private static String normalizeQuery(String query, Charset cs)
			throws UnsupportedEncodingException {
		if (query.length() < MIN_SENSE_KEY_VALUE_LEN) {
			return EMPTY_QUERY;
		}

		ArrayList<String> parts = StringProcessor.splitString2ArrayList(query,
				'&'); // 按 & 分割
		final int part_num = parts.size();
		if (part_num == 0) { // 分割数错误
			return null;
		} else if (part_num == 1) { // 只有一个part
			String one_part = parts.get(0);
			final int part_len = one_part.length();
			if (part_len < MIN_SENSE_KEY_VALUE_LEN) {
				return EMPTY_QUERY;
			}
			int idx = one_part.indexOf('=');
			if (idx <= 0 || idx == part_len - 1) { // 检查 = 的位置
				return EMPTY_QUERY;
			} else {
				String key = normalizeOneQueryKey(one_part.substring(0, idx),
						cs); // 归一化key
				if (key == null) {
					return null;
				}
				String value = normalizeOneQueryValue(
						one_part.substring(++idx), cs); // 归一化value
				if (value == null) {
					return null;
				}
				return key + '=' + value; // 返回
			}
		} else { // 两个以上的part
			HashMap<String, String> query_arg = new HashMap<String, String>(); // 建立key-value的hashmap
			for (String one_part : parts) {
				final int part_len = one_part.length();
				if (part_len < MIN_SENSE_KEY_VALUE_LEN) // 长度检查
					continue;
				int idx = one_part.indexOf('=');
				if (idx <= 0 || idx == part_len - 1) // 检查 = 的位置
					continue;

				String key = one_part.substring(0, idx); // 归一化key
				if (query_arg.containsKey(key))
					continue;
				key = normalizeOneQueryKey(key, cs);
				if (key == null)
					return null;
				if (query_arg.containsKey(key)) // 判重
					continue;

				String value = normalizeOneQueryValue(
						one_part.substring(++idx), cs); // 归一化value
				if (value == null)
					return null;
				query_arg.put(key, value); // 放入map
			}

			if (query_arg.size() == 0) { // 空query
				return EMPTY_QUERY;
			} else {
				ArrayList<Entry<String, String>> kvs = new ArrayList<Entry<String, String>>(
						query_arg.entrySet());
				Collections.sort(kvs, comp); // 排序
				StringBuilder sb = new StringBuilder();
				Entry<String, String> tmp_kv = kvs.get(0); // 拼接
				sb.append(tmp_kv.getKey());
				sb.append('=');
				sb.append(tmp_kv.getValue());
				for (int i = 1; i < kvs.size(); ++i) {
					tmp_kv = kvs.get(i);
					sb.append('&');
					sb.append(tmp_kv.getKey());
					sb.append('=');
					sb.append(tmp_kv.getValue());
				}
				return sb.toString();
			}
		}
	}

	// 归一化key
	private static String normalizeOneQueryKey(String key, Charset cs)
			throws UnsupportedEncodingException {
		return processStringPart(key, cs);
	}

	// 归一化value
	private static String normalizeOneQueryValue(String value, Charset cs)
			throws UnsupportedEncodingException {
		return processStringPart(value, cs);
	}

	// 归一化函数
	private String normalize(boolean show_http_scheme, Charset cs)
			throws UnsupportedEncodingException {
		// 归一化scheme
		String normalized_scheme = null;
		if (scheme == null) {
			normalized_scheme = DEFAULT_SCHEME;
		} else {
			normalized_scheme = normalizeScheme(scheme);
		}
		if (normalized_scheme == null) {
			return null;
		}

		// 归一化userinfo
		String normalized_user_info = null;
		if (userInfo != null) {
			normalized_user_info = normalizeUserInfo(userInfo);
			if (normalized_user_info == null) {
				return null;
			}
		}

		// 归一化host
		String normalized_host = normalizeHost(host);
		if (normalized_host == null) {
			return null;
		}

		// 归一化port
		String normalized_port = null;
		if (port != null) {
			normalized_port = normalizePort(port);
			if (normalized_port == null) {
				return null;
			}
		}

		// 归一化path
		String normalized_path = normalizePath(path, query != null
				|| fragment != null, cs);
		if (normalized_path == null)
			return null;

		// 归一化query
		String normalized_query = null;
		if (query != null) {
			normalized_query = normalizeQuery(query, cs);
			if (normalized_query == null)
				return null;
		}

		// 整体拼接
		StringBuilder sb = new StringBuilder();
		if (show_http_scheme) { // 带 http:// 头
			sb.append(normalized_scheme);
			sb.append("://");
		}
		if (normalized_user_info != null && normalized_user_info.length() > 0) {
			sb.append(normalized_user_info);
			sb.append('@');
		}
		sb.append(normalized_host);
		if (normalized_port != null && normalized_port.length() > 0) {
			sb.append(':');
			sb.append(normalized_port);
		}

		sb.append(normalized_path);
		if (normalized_query != null && normalized_query.length() != 0) {
			sb.append('?');
			sb.append(normalized_query);
		}

		String result = sb.toString();
		if (result.matches(".*/[^\\?\\./&]+$")) {
			result = result + "/";
		}
		return result;
	}

	// 归一化url字符串，能够保证多线程安全
	public static String normalizeHttpURL(String url, boolean show_http_scheme,
			Charset cs) throws UnsupportedEncodingException {
		HttpURL hu = HttpURL.parseURL(url);
		return hu == null ? null : hu.normalize(show_http_scheme, cs);
	}

	// 取出并归一化host
	public static String getAndNormalizeHttpURLHost(String url) {
		HttpURL hu = HttpURL.parseURL(url);
		return hu == null ? null : normalizeHost(hu.host);
	}

	// public static void main(String[] args){
	// String str="www.google.i%2ehh:80/haha";
	// System.out.println(str.length());
	// String new_result=null;
	// long begin=System.currentTimeMillis();
	// for(int i=0;i<1;++i){
	// new_result=HttpURL.normalizeHttpURL(str,true);
	// }
	// long end=System.currentTimeMillis();
	// System.out.println("Running time (ms): "+(end-begin));
	// System.out.println(new_result);
	// }

}
