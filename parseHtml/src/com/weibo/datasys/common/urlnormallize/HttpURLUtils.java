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
import java.util.HashSet;

public class HttpURLUtils {
	
//------------------------------------------- first part---------------------------------------------------//
	private static String[][][] gtldTable=new String[CharacterProcessor.ASCII_LOWER_CASE_LETTER_NUM]
			[CharacterProcessor.ASCII_LOWER_CASE_LETTER_NUM][];//通用顶级域名
	static{
		
		//aero,arpa,arts,asia
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=new String[]{"aero"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=new String[]{"arpa","arts"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]=new String[]{"asia"};

		//biz
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('b')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]=new String[]{"biz"};

		//cat,com,coop
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=new String[]{"cat"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=new String[]{"com","coop"};

		//edu
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('d')]=new String[]{"edu"};

		//firm
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('f')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]=new String[]{"firm"};

		//geo,gov
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=new String[]{"geo"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=new String[]{"gov"};

		//idv,info,int
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('d')]=new String[]{"idv"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]=new String[]{"info","int"};

		//jobs
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('j')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=new String[]{"jobs"};

		//kid
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('k')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]=new String[]{"kid"};

		//mail,mil,mobi,museum
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=new String[]{"mail"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]=new String[]{"mil"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=new String[]{"mobi"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('u')]=new String[]{"museum"};

		//name,net,nom
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=new String[]{"name"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=new String[]{"net"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=new String[]{"nom"};

		//org
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=new String[]{"org"};

		//post,pro
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=new String[]{"post"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=new String[]{"pro"};

		//rec,root
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=new String[]{"rec"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=new String[]{"root"};

		//sco,store
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]=new String[]{"sco"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]=new String[]{"store"};

		//tel,travel
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=new String[]{"tel"};
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=new String[]{"travel"};

		//web
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('w')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=new String[]{"web"};

		//xxx
		gtldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('x')]
				[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('x')]=new String[]{"xxx"};
	}
	
	//判断是否是通用顶级域名
	private static boolean isGenericTopLevelDomain(String str){
		if(str.length()<3)
			return false;
		int i=CharacterProcessor.getAsciiLowerCaseLetterOrdinal(str.charAt(0));
		if(i==-1)
			return false;
		int j=CharacterProcessor.getAsciiLowerCaseLetterOrdinal(str.charAt(1));
		if(j==-1)
			return false;
		String[] top_domain_list=gtldTable[i][j];
		if(top_domain_list==null)
			return false;
		for(String top_domain : top_domain_list){
			if(top_domain.equals(str))
				return true;
		}
		return false;
	}
	
//------------------------------------------- end of first part -------------------------------------------//	

	
	
	
	
	
	
//------------------------------------------- second part -------------------------------------------------//
	private static boolean[][] cctldTable=new boolean[CharacterProcessor.ASCII_LOWER_CASE_LETTER_NUM]
			[CharacterProcessor.ASCII_LOWER_CASE_LETTER_NUM];//国家及地区顶级域名	
	static{
		String[] top_c_str={
				"ac","ad","ae","af","ag","ai","al","am","an","ao","aq","ar","as","at","au","aw","ax","az",
				"ba","bb","bd","be","bf","bg","bh","bi","bj","bm","bn","bo","br","bs","bt","bv","bw","by","bz",
				"ca","cc","cd","cf","cg","ch","ci","ck","cl","cm","cn","co","cr","cu","cv","cx","cy","cz",
				"de","dj","dk","dm","do","dz",
				"ec","ee","eg","eh","er","es","et","eu",
				"fi","fj","fk","fm","fo","fr",
				"ga","gd","ge","gf","gg","gh","gi","gl","gm","gn","gp","gq","gr","gs","gt","gu","gw","gy",
				"hk","hm","hn","hr","ht","hu",
				"id","ie","il","im","in","io","iq","ir","is","it",
				"je","jm","jo","jp",
				"ke","kg","kh","ki","km","kn","kp","kr","kw","ky","kz",
				"la","lb","lc","li","lk","lr","ls","lt","lu","lv","ly",
				"ma","mc","md","me","mg","mh","mk","ml","mm","mn","mo","mp","mq","mr","ms","mt","mu","mv","mw","mx","my","mz",
				"na","nc","ne","nf","ng","ni","nl","no","np","nr","nu","nz",
				"om",
				"pa","pe","pf","pg","ph","pk","pl","pm","pn","pr","ps","pt","pw","py",
				"qa",
				"re","ro","rs","ru","rw",
				"sa","sb","sc","sd","se","sg","sh","si","sj","sk","sl","sm","sn","so","sr","ss","st","sv","sy","sz",
				"tc","td","tf","tg","th","tj","tk","tl","tm","tn","to","tp","tr","tt","tv","tw","tz",
				"ua","ug","uk","um","us","uy","uz",
				"va","vc","ve","vg","vi","vn","vu",
				"wf","ws",
				"ye","yt","yu","yr",
				"za","zm","zw"

				//may cause problem: ac,ax,bv,cn,eh,hm,kp,pm,rs,sj,ss,tf,tl,tp,um,yt,yu,yr
				//special domain: tv,cc,
		};
		
		for(String temp : top_c_str){
			cctldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal(temp.charAt(0))]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal(temp.charAt(1))]=true;
		}
	}
	
	private static boolean isCountryCodeTopLevelDomain(String str){
		if(str.length()!=2)
			return false;
		int i=CharacterProcessor.getAsciiLowerCaseLetterOrdinal(str.charAt(0));
		if(i==-1)
			return false;
		int j=CharacterProcessor.getAsciiLowerCaseLetterOrdinal(str.charAt(1));
		if(j==-1)
			return false;
		return cctldTable[i][j];
	}
	
	private static boolean isOldValidTopLevelDomain(String str){
		return isGenericTopLevelDomain(str) || isCountryCodeTopLevelDomain(str);
	}
//------------------------------------------- end of second part ------------------------------------------//

	
	
	
	
	
	
	
	
//------------------------------------------- third part -------------------------------------------------//
	private static Object[][] csldTable=new Object[CharacterProcessor.ASCII_LOWER_CASE_LETTER_NUM]
			[CharacterProcessor.ASCII_LOWER_CASE_LETTER_NUM];//国家及地区二级域名
	static{
		
		//ad	:	Andorra
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("nom");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('d')]=tmp_set;
		}
		
		//ae	:	United Arab Emirates
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("gov");
			tmp_set.add("mil");
			tmp_set.add("name");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("pro");
			tmp_set.add("sch");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=tmp_set;
		}
		
		//af	:	Afghanistan
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('f')]=tmp_set;
		}
		
		//ag	:	Antigua and Barbuda
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("co");
			tmp_set.add("com");
			tmp_set.add("net");
			tmp_set.add("nom");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]=tmp_set;
		}
		
		//ai	:	Anguilla
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("net");
			tmp_set.add("off");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]=tmp_set;
		}
		
		//al	:	Albania
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('l')]=tmp_set;
		}
		
		//ar	:	Argentina
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("int");
			tmp_set.add("mil");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=tmp_set;
		}
		
		//at	:	Austria
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("gv");
			tmp_set.add("or");
			tmp_set.add("priv");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]=tmp_set;
		}
		
		//au	:	Australia
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("asn");
			tmp_set.add("com");
			tmp_set.add("conf");
			tmp_set.add("csiro");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("id");
			tmp_set.add("info");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('u')]=tmp_set;
		}
		
		//bb	:	Barbados
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("biz");
			tmp_set.add("co");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("info");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("store");
			tmp_set.add("tv");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('b')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('b')]=tmp_set;
		}
		
		//bh	:	Bahrain
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("biz");
			tmp_set.add("cc");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("info");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('b')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('h')]=tmp_set;
		}
		
		//br	:	Brazil
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("adm");
			tmp_set.add("adv");
			tmp_set.add("agr");
			tmp_set.add("am");
			tmp_set.add("arq");
			tmp_set.add("art");
			tmp_set.add("ato");
			tmp_set.add("bio");
			tmp_set.add("bmd");
			tmp_set.add("cim");
			tmp_set.add("cng");
			tmp_set.add("cnt");
			tmp_set.add("com");
			tmp_set.add("ecn");
			tmp_set.add("edu");
			tmp_set.add("eng");
			tmp_set.add("esp");
			tmp_set.add("etc");
			tmp_set.add("eti");
			tmp_set.add("far");
			tmp_set.add("fm");
			tmp_set.add("fnd");
			tmp_set.add("fot");
			tmp_set.add("fst");
			tmp_set.add("g12");
			tmp_set.add("ggf");
			tmp_set.add("gov");
			tmp_set.add("imb");
			tmp_set.add("ind");
			tmp_set.add("inf");
			tmp_set.add("jor");
			tmp_set.add("lel");
			tmp_set.add("mat");
			tmp_set.add("med");
			tmp_set.add("mil");
			tmp_set.add("mus");
			tmp_set.add("net");
			tmp_set.add("nom");
			tmp_set.add("not");
			tmp_set.add("ntr");
			tmp_set.add("odo");
			tmp_set.add("oop");
			tmp_set.add("org");
			tmp_set.add("ppg");
			tmp_set.add("pro");
			tmp_set.add("psc");
			tmp_set.add("psi");
			tmp_set.add("qsl");
			tmp_set.add("rec");
			tmp_set.add("slg");
			tmp_set.add("srv");
			tmp_set.add("tmp");
			tmp_set.add("trd");
			tmp_set.add("tur");
			tmp_set.add("tv");
			tmp_set.add("vet");
			tmp_set.add("zlg");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('b')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=tmp_set;
		}
		
		//ca	:	Canada
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ab");
			tmp_set.add("bc");
			tmp_set.add("mb");
			tmp_set.add("nb");
			tmp_set.add("nf");
			tmp_set.add("nl");
			tmp_set.add("ns");
			tmp_set.add("nt");
			tmp_set.add("nu");
			tmp_set.add("on");
			tmp_set.add("pe");
			tmp_set.add("qc");
			tmp_set.add("sk");
			tmp_set.add("yk");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=tmp_set;
		}
		
		//ck	:	Cook Islands
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("co");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('k')]=tmp_set;
		}
		
		//cl	:	Chile
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("gob");
			tmp_set.add("gov");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('l')]=tmp_set;
		}
		
		//cn	:	China		******************************************************************
		{
			HashSet<String> tmp_set=new HashSet<String>();
			//com,edu,org,gov,mil,net,ac
			tmp_set.add("ac");	
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("mil");
			tmp_set.add("net");
			tmp_set.add("org");
			//"bj","sh","tj","cq","he","sx","nm","ln","jl","hl","js","zj","ah","fj","jx","sd","ha",
			//"hb","hn","gd","gx","hi","sc","gz","yn","xz","sn","gs","qh","nx","xj","tw","hk","mo",
			tmp_set.add("ah");
			tmp_set.add("bj");
			tmp_set.add("cq");
			tmp_set.add("fj");
			tmp_set.add("gd");
			tmp_set.add("gs");
			tmp_set.add("gx");
			tmp_set.add("gz");
			tmp_set.add("ha");
			tmp_set.add("hb");
			tmp_set.add("he");
			tmp_set.add("hi");
			tmp_set.add("hk");
			tmp_set.add("hl");
			tmp_set.add("hn");
			tmp_set.add("jl");
			tmp_set.add("js");
			tmp_set.add("jx");
			tmp_set.add("ln");
			tmp_set.add("mo");
			tmp_set.add("nm");
			tmp_set.add("nx");
			tmp_set.add("qh");
			tmp_set.add("sc");
			tmp_set.add("sd");
			tmp_set.add("sh");
			tmp_set.add("sn");
			tmp_set.add("sx");
			tmp_set.add("tj");
			tmp_set.add("tw");
			tmp_set.add("xj");
			tmp_set.add("xz");
			tmp_set.add("yn");
			tmp_set.add("zj");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]=tmp_set;
		}
		
		//dm	:	Dominica
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('d')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]=tmp_set;
		}
		
		//ec	:	Ecuador
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("fin");
			tmp_set.add("gov");
			tmp_set.add("info");
			tmp_set.add("med");
			tmp_set.add("mil");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("pro");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]=tmp_set;
		}
		
		//ee	:	Estonia
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("fie");
			tmp_set.add("med");
			tmp_set.add("org");
			tmp_set.add("pri");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=tmp_set;
		}
		
		//eg	:	Egypt
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("eun");
			tmp_set.add("gov");
			tmp_set.add("mil");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("sci");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]=tmp_set;
		}
		
		//es	:	Spain
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gob");
			tmp_set.add("nom");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]=tmp_set;
		}
		
		//fj	:	Fiji
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("biz");
			tmp_set.add("com");
			tmp_set.add("info");
			tmp_set.add("mil");
			tmp_set.add("name");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("pro");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('f')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('j')]=tmp_set;
		}
		
		//fr	:	France
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("asso");
			tmp_set.add("com");
			tmp_set.add("gouv");
			tmp_set.add("nom");
			tmp_set.add("prd");
			tmp_set.add("presse");
			tmp_set.add("tm");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('f')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=tmp_set;
		}
		
		//ge	:	Georgia
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("gov");
			tmp_set.add("net");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=tmp_set;
		}
		
		//gl	:	Greenland
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("co");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('l')]=tmp_set;
		}
		
		//gr	:	Greece
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=tmp_set;
		}
		
		//gy	:	Guyana
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("co");
			tmp_set.add("com");
			tmp_set.add("net");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('y')]=tmp_set;
		}
		
		
		//hk	:	Hong Kong	*******************************************************************
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("org");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("idv");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('h')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('k')]=tmp_set;
		}
		
		//hr	:	Croatia/Hrvatska
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("from");
			tmp_set.add("iz");
			tmp_set.add("name");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('h')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=tmp_set;
		}
		
		//hu	:	Hungary
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("2000");
			tmp_set.add("agrar");
			tmp_set.add("bolt");
			tmp_set.add("casino");
			tmp_set.add("city");
			tmp_set.add("co");
			tmp_set.add("erotica");
			tmp_set.add("erotika");
			tmp_set.add("film");
			tmp_set.add("forum");
			tmp_set.add("games");
			tmp_set.add("hotel");
			tmp_set.add("info");
			tmp_set.add("ingatlan");
			tmp_set.add("jogasz");
			tmp_set.add("konyvelo");
			tmp_set.add("lakas");
			tmp_set.add("media");
			tmp_set.add("news");
			tmp_set.add("org");
			tmp_set.add("priv");
			tmp_set.add("reklam");
			tmp_set.add("sex");
			tmp_set.add("shop");
			tmp_set.add("sport");
			tmp_set.add("suli");
			tmp_set.add("szex");
			tmp_set.add("tm");
			tmp_set.add("tozsde");
			tmp_set.add("utazas");
			tmp_set.add("video");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('h')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('u')]=tmp_set;
		}
		
		//ie	:	Republic of Ireland
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("gov");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=tmp_set;
		}
		
		//il	:	Israel
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("gov");
			tmp_set.add("idf");
			tmp_set.add("k12");
			tmp_set.add("muni");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('l')]=tmp_set;
		}
		
		//im	:	Isle of Man
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("com");
			tmp_set.add("gov");
			tmp_set.add("ltd");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("plc");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]=tmp_set;
		}
		
		//in	:	India
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("edu");
			tmp_set.add("firm");
			tmp_set.add("gen");
			tmp_set.add("gov");
			tmp_set.add("ind");
			tmp_set.add("mil");
			tmp_set.add("net");
			tmp_set.add("nic");
			tmp_set.add("org");
			tmp_set.add("res");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('i')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]=tmp_set;
		}
		
		//lv	:	Latvia
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("asn");
			tmp_set.add("com");
			tmp_set.add("conf");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("id");
			tmp_set.add("mil");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('l')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('v')]=tmp_set;
		}

		//jp	:	Japan
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("ad");
			tmp_set.add("co");
			tmp_set.add("ed");
			tmp_set.add("go");
			tmp_set.add("gr");
			tmp_set.add("lg");
			tmp_set.add("ne");
			tmp_set.add("or");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('j')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]=tmp_set;
		}
		
		//kr	:	Korea, Republic of
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("es");
			tmp_set.add("go");
			tmp_set.add("hs");
			tmp_set.add("kg");
			tmp_set.add("mil");
			tmp_set.add("ms");
			tmp_set.add("ne");
			tmp_set.add("or");
			tmp_set.add("pe");
			tmp_set.add("re");
			tmp_set.add("sc");
			tmp_set.add("seoul");
			tmp_set.add("busan");
			tmp_set.add("daegu");
			tmp_set.add("incheon");
			tmp_set.add("gwangju");
			tmp_set.add("daejeon");
			tmp_set.add("ulsan");
			tmp_set.add("gyeonggi");
			tmp_set.add("gangwon");
			tmp_set.add("chungbuk");
			tmp_set.add("chungnam");
			tmp_set.add("jeonbuk");
			tmp_set.add("jeonnam");
			tmp_set.add("gyeongbuk");
			tmp_set.add("gyeongnam");
			tmp_set.add("jeju");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('k')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=tmp_set;
		}
		
		//ky	:	Cayman Islands
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('k')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('y')]=tmp_set;
		}
		
		//lk	:	Sri Lanka
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("assn");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("grp");
			tmp_set.add("hotel");
			tmp_set.add("ltd");
			tmp_set.add("ngo");
			tmp_set.add("org");
			tmp_set.add("soc");
			tmp_set.add("web");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('l')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('k')]=tmp_set;
		}
		
		//ly	:	Libyan Arab Jamahiriya
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("id");
			tmp_set.add("med");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("plc");
			tmp_set.add("sch");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('l')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('y')]=tmp_set;
		}
		
		//mc	:	Monaco
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("asso");
			tmp_set.add("tm");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]=tmp_set;
		}
		
		//me	:	Montenegro
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("co");
			tmp_set.add("its");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("priv");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=tmp_set;
		}

		//mo	:	Macau
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=tmp_set;
		}
		
		//mu	:	Mauritius
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("com");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('u')]=tmp_set;
		}
		
		//mx	:	Mexico
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gob");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('x')]=tmp_set;
		}
		
		//my	:	Malaysia
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("mil");
			tmp_set.add("name");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('y')]=tmp_set;
		}
		
		//na 	:	Namibia
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("alt");
			tmp_set.add("co");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=tmp_set;
		}
		
		//nz	:	New Zealand
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("cri");
			tmp_set.add("geek");
			tmp_set.add("gen");
			tmp_set.add("govt");
			tmp_set.add("iwi");
			tmp_set.add("maori");
			tmp_set.add("mil");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("parliament");
			tmp_set.add("school");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('z')]=tmp_set;
		}
		
		//no	:	Norway
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("priv");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=tmp_set;
		}
		
		//pa	:	Panama
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("abo");
			tmp_set.add("ac");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gob");
			tmp_set.add("ing");
			tmp_set.add("med");
			tmp_set.add("net");
			tmp_set.add("nom");
			tmp_set.add("org");
			tmp_set.add("sld");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=tmp_set;
		}
		
		//ph	:	Philippines
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("i");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('h')]=tmp_set;
		}
		
		//pk	:	Pakistan
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("biz");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("fam");
			tmp_set.add("gob");
			tmp_set.add("gok");
			tmp_set.add("gon");
			tmp_set.add("gop");
			tmp_set.add("gos");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("web");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('k')]=tmp_set;
		}
		
		//pl	:	Poland
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("biz");
			tmp_set.add("com");
			tmp_set.add("info");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("waw");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('l')]=tmp_set;
		}
		
		//pr	:	Puerto Rico
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("biz");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("info");
			tmp_set.add("isla");
			tmp_set.add("name");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("pro");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=tmp_set;
		}
		
		//pt	:	Portugal
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("int");
			tmp_set.add("net");
			tmp_set.add("nome");
			tmp_set.add("org");
			tmp_set.add("publ");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]=tmp_set;
		}
		
		//ro	:	Romania
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("arts");
			tmp_set.add("com");
			tmp_set.add("firm");
			tmp_set.add("info");
			tmp_set.add("nom");
			tmp_set.add("nt");
			tmp_set.add("org");
			tmp_set.add("rec");
			tmp_set.add("store");
			tmp_set.add("tm");
			//tmp_set.add("www");	may have some problems.....
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=tmp_set;
		}
		
		//ru	:	Russia
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("pp");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('u')]=tmp_set;
		}
		
		//sa	:	Saudi Arabia
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("med");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("pub");
			tmp_set.add("sch");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=tmp_set;
		}
		
		//sc	:	Seychelles
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]=tmp_set;
		}
		
		//se	:	Sweden
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ab");
			tmp_set.add("ac");
			tmp_set.add("bd");
			tmp_set.add("c");
			tmp_set.add("d");
			tmp_set.add("e");
			tmp_set.add("f");
			tmp_set.add("g");
			tmp_set.add("h");
			tmp_set.add("i");
			tmp_set.add("k");
			tmp_set.add("m");
			tmp_set.add("mil");
			tmp_set.add("n");
			tmp_set.add("o");
			tmp_set.add("org");
			tmp_set.add("parti");
			tmp_set.add("pp");
			tmp_set.add("press");
			tmp_set.add("s");
			tmp_set.add("t");
			tmp_set.add("tm");
			tmp_set.add("u");
			tmp_set.add("w");
			tmp_set.add("x");
			tmp_set.add("y");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=tmp_set;
		}
		
		//sg	:	Singapore
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("idn");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("per");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]=tmp_set;
		}
		
		//sv	:	El Salvador
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gob");
			tmp_set.add("org");
			tmp_set.add("red");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('v')]=tmp_set;
		}
		
		//th	:	Thailand
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("go");
			tmp_set.add("in");
			tmp_set.add("mi");
			tmp_set.add("net");
			tmp_set.add("or");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('h')]=tmp_set;
		}
		
		//tl	:	Timor-Leste
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('l')]=tmp_set;
		}
		
		//tm	:	Turkmenistan
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("co");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("mil");
			tmp_set.add("net");
			tmp_set.add("nom");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]=tmp_set;
		}
		
		//tp	:	East Timor
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('p')]=tmp_set;
		}
		
		//tr	:	Turkey
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("av");
			tmp_set.add("bbs");
			tmp_set.add("bel");
			tmp_set.add("biz");
			tmp_set.add("com");
			tmp_set.add("dr");
			tmp_set.add("edu");
			tmp_set.add("gen");
			tmp_set.add("gov");
			tmp_set.add("info");
			tmp_set.add("k12");
			tmp_set.add("mil");
			tmp_set.add("name");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("pol");
			tmp_set.add("tel");
			tmp_set.add("tv");
			tmp_set.add("web");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('r')]=tmp_set;
		}
		
		//tw	:	Taian		*******************************************************************
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("club");	
			tmp_set.add("com");
			tmp_set.add("ebiz");
			tmp_set.add("edu");
			tmp_set.add("game");
			tmp_set.add("gov");
			tmp_set.add("idv");
			tmp_set.add("mil");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('t')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('w')]=tmp_set;
		}
		
		//ua	:	Ukraine
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("cherkassy");
			tmp_set.add("chernigov");
			tmp_set.add("chernovtsy");
			tmp_set.add("ck");
			tmp_set.add("cn");
			tmp_set.add("com");
			tmp_set.add("crimea");
			tmp_set.add("cv");
			tmp_set.add("dn");
			tmp_set.add("dnepropetrovsk");
			tmp_set.add("donetsk");
			tmp_set.add("dp");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("if");
			tmp_set.add("in");
			tmp_set.add("ivano-frankivsk");
			tmp_set.add("kh");
			tmp_set.add("kharkov");
			tmp_set.add("kherson");
			tmp_set.add("khmelnitskiy");
			tmp_set.add("kiev");
			tmp_set.add("kirovograd");
			tmp_set.add("km");
			tmp_set.add("kr");
			tmp_set.add("ks");
			tmp_set.add("kv");
			tmp_set.add("lg");
			tmp_set.add("lugansk");
			tmp_set.add("lutsk");
			tmp_set.add("lviv");
			tmp_set.add("mk");
			tmp_set.add("net");
			tmp_set.add("nikolaev");
			tmp_set.add("od");
			tmp_set.add("odessa");
			tmp_set.add("org");
			tmp_set.add("pl");
			tmp_set.add("poltava");
			tmp_set.add("rovno");
			tmp_set.add("rv");
			tmp_set.add("sebastopol");
			tmp_set.add("sumy");
			tmp_set.add("te");
			tmp_set.add("ternopil");
			tmp_set.add("uzhgorod");
			tmp_set.add("vinnica");
			tmp_set.add("vn");
			tmp_set.add("zaporizhzhe");
			tmp_set.add("zhitomir");
			tmp_set.add("zp");
			tmp_set.add("zt");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('u')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=tmp_set;
		}
		
		//uk
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("co");
			tmp_set.add("gov");
			tmp_set.add("ltd");
			tmp_set.add("me");
			tmp_set.add("mod");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("net");
			tmp_set.add("nhs");
			tmp_set.add("nic");
			tmp_set.add("sch");
			tmp_set.add("plc");
			tmp_set.add("parliament");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('u')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('k')]=tmp_set;
		}	
		
		
		//us	:	United States
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ak");
			tmp_set.add("al");
			tmp_set.add("ar");
			tmp_set.add("az");
			tmp_set.add("ca");
			tmp_set.add("co");
			tmp_set.add("ct");
			tmp_set.add("dc");
			tmp_set.add("de");
			tmp_set.add("dni");
			tmp_set.add("fed");
			tmp_set.add("fl");
			tmp_set.add("ga");
			tmp_set.add("hi");
			tmp_set.add("ia");
			tmp_set.add("id");
			tmp_set.add("il");
			tmp_set.add("in");
			tmp_set.add("isa");
			tmp_set.add("kids");
			tmp_set.add("ks");
			tmp_set.add("ky");
			tmp_set.add("la");
			tmp_set.add("ma");
			tmp_set.add("md");
			tmp_set.add("me");
			tmp_set.add("mi");
			tmp_set.add("mn");
			tmp_set.add("mo");
			tmp_set.add("ms");
			tmp_set.add("mt");
			tmp_set.add("nc");
			tmp_set.add("nd");
			tmp_set.add("ne");
			tmp_set.add("nh");
			tmp_set.add("nj");
			tmp_set.add("nm");
			tmp_set.add("nsn");
			tmp_set.add("nv");
			tmp_set.add("ny");
			tmp_set.add("oh");
			tmp_set.add("ok");
			tmp_set.add("or");
			tmp_set.add("pa");
			tmp_set.add("ri");
			tmp_set.add("sc");
			tmp_set.add("sd");
			tmp_set.add("tn");
			tmp_set.add("tx");
			tmp_set.add("ut");
			tmp_set.add("va");
			tmp_set.add("vt");
			tmp_set.add("wa");
			tmp_set.add("wi");
			tmp_set.add("wv");
			tmp_set.add("wy");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('u')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]=tmp_set;
		}
		
		//vc	:	Saint Vincent and the Grenadines
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('v')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]=tmp_set;
		}
		
		//vn	:	Vietnam
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("ac");
			tmp_set.add("biz");
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("health");
			tmp_set.add("info");
			tmp_set.add("int");
			tmp_set.add("name");
			tmp_set.add("net");
			tmp_set.add("org");
			tmp_set.add("pro");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('v')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]=tmp_set;
		}
		
		//ws	:	
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("com");
			tmp_set.add("edu");
			tmp_set.add("gov");
			tmp_set.add("net");
			tmp_set.add("org");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('w')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('s')]=tmp_set;
		}
		
		//za	:	South Africa
		{
			HashSet<String> tmp_set=new HashSet<String>();
			tmp_set.add("co");
			csldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('z')]
					[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=tmp_set;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static HashSet<String> getCountrySecondLevelDomainSet(String country){
		if(country.length()!=2){
			return null;
		}
		int i=CharacterProcessor.getAsciiLowerCaseLetterOrdinal(country.charAt(0));
		if(i==-1)
			return null;
		int j=CharacterProcessor.getAsciiLowerCaseLetterOrdinal(country.charAt(1));
		if(j==-1)
			return null;
		
		Object raw_object=csldTable[i][j];
		if(raw_object==null)
			return null;
		else{
			return (HashSet<String>)raw_object;
		}
			
	}
	
	private static boolean isCountrySecondLevelDomain(HashSet<String> csld_set, String second_domain){
		return csld_set.contains(second_domain);
	}
//------------------------------------------- end of third part ------------------------------------------//

	
	
	
	
	
	
	
	
//------------------------------------------- fourth part -------------------------------------------------//
	private static String[][] alternativeCandiateCsldTable=new String[CharacterProcessor.ASCII_LOWER_CASE_LETTER_NUM][];//候选国家及地区二级域名
	static{
		//ac
		alternativeCandiateCsldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('a')]=new String[]{"ac"};
		//com
		alternativeCandiateCsldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('c')]=new String[]{"com","co"};
		//edu
		alternativeCandiateCsldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('e')]=new String[]{"edu"};
		//gov
		alternativeCandiateCsldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('g')]=new String[]{"gov"};
		//mil
		alternativeCandiateCsldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('m')]=new String[]{"mil"};
		//net
		alternativeCandiateCsldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('n')]=new String[]{"net"};
		//org
		alternativeCandiateCsldTable[CharacterProcessor.getAsciiLowerCaseLetterOrdinal('o')]=new String[]{"org"};
	}
	private static boolean isAlternativeCandiateCountrySecondLevelDomain(String str){
		if(str.length()<1){
			return false;
		}
		
		int i=CharacterProcessor.getAsciiLowerCaseLetterOrdinal(str.charAt(0));
		if(i==-1)
			return false;
		String[] second_domain_list=alternativeCandiateCsldTable[i];
		if(second_domain_list==null)
			return false;
		for(String temp : second_domain_list){
			if(temp.equals(str))
				return true;
		}
		return false;
	}
//------------------------------------------- end of fourth part ------------------------------------------//
	
	
	
	
	
	
//------------------------------------------- fifth part -------------------------------------------------//
	//判断是否是IPv4 host
	private static boolean isIPV4Host(ArrayList<String> parts){
		if(parts.size()!=4){
			return false;
		}
		for(String temp : parts){
			int len=temp.length();
			if(len==0)
				return false;
			for(int i=0;i<len;++i){
				if(!CharacterProcessor.isAsciiDigit(temp.charAt(i)))
					return false;
			}
		}
		return true;
	}
	
	//是否是IP host
	public static boolean isIPHost(String host){
		return isIPV4Host(StringProcessor.splitString2ArrayList(host, '.'));
	}
	
	//获取domain
	public static String getDomain(String host){
		ArrayList<String> parts=StringProcessor.splitString2ArrayList(host,'.');
		final int num=parts.size();
		
		if(num>=3){
			String last_part=parts.get(num-1);
			String second_to_last_part=parts.get(num-2);
			
			final int last_2_idx=host.length()-last_part.length()-second_to_last_part.length()-1;
			
			if(isGenericTopLevelDomain(last_part)){	//通用顶级域名
				return host.substring(last_2_idx);
			}
			else if(isCountryCodeTopLevelDomain(last_part)){	//国家及地区的域名
				HashSet<String> csld_set=getCountrySecondLevelDomainSet(last_part);	//查国家及地区二级域名表
				if(csld_set!=null){					//查到了
					if(isCountrySecondLevelDomain(csld_set,second_to_last_part)){	//在表里
						String third_to_last_domain=parts.get(num-3);
						if(!third_to_last_domain.equals("www")){	//处理www开头的
							return host.substring(last_2_idx-third_to_last_domain.length()-1);
						}
						else{
							return host.substring(last_2_idx);
						}
					}
					else{							//不在表里
						return host.substring(last_2_idx);
					}
				}
				else{								//没有表，用候选表的hold住
					if(isAlternativeCandiateCountrySecondLevelDomain(second_to_last_part)){	//在候选表里
						String third_to_last_domain=parts.get(num-3);
						if(!third_to_last_domain.equals("www")){	//处理www开头的
							return host.substring(last_2_idx-third_to_last_domain.length()-1);
						}
						else{
							return host.substring(last_2_idx);
						}
					}
					else{	//不在候选表里
						return host.substring(last_2_idx);
					}
				}
			}
			else if(num==4 && isIPV4Host(parts)){
				return host;
			}
			else{
				return host.substring(last_2_idx);
			}
		}
		else{	//其它......
			return host;
		}
	}
	
	private static long getHostBeginEndIndex(String url){
		int start=0;		//host起始的初始下标
		if(url.regionMatches(0, "http://", 0, 7)){	//是否是http开头
			start=7;
		}
		
		final int len=url.length();
		int end=len;		//host结束的初始下标
		int at_idx=-1;		// @ 的下标
		int colon_idx=-1;	// : 的下标
		
		for(int i=start;i<len;i++){
			char c=url.charAt(i);
			if(c=='/' || c=='?' || c=='#'){		//authoroty 结束标志符
				end=i;
				break;
			}
			else if(c==':'){		//记录最新探测到的 : 
				colon_idx=i;
			}
			else if(c=='@' && at_idx==-1){	//第一次探测到 @
				at_idx=i;
				start=at_idx+1;
				colon_idx=-1;	
			}
		}
		
		if(start!=end){			//起始和结尾相同，则为空字符串
			//这里的处理具有容错性
			if(colon_idx!=-1){		//探测到 :
				if(url.charAt(start)=='['){		// ipv6 or ip-v-future
					int right_s_b_idx=url.lastIndexOf(']',end);		//检查 ] 和  : 的相对位置
					if(right_s_b_idx<colon_idx){					// ] 在 : 左边
						end=colon_idx;								
					}
				}
				else{
					end=colon_idx;
				}
			}
		}
		
		long tmp=0;
		tmp=start;
		tmp<<=32;
		tmp=tmp|(end&0xFFFFFFFFL);
		return tmp;
	}
	
	//获取host
	public static String getHostFromNormalizedURL(String url){
		long tmp=getHostBeginEndIndex(url);
		int end=(int)tmp;
		int start=(int)(tmp>>>32);
		if(start==end){
			return "";
		}
		else{
			return url.substring(start, end);
		}		
	}
	
	
	/**
	 * 将url切分为domain、host、suffix三段
	 * @return String array in which the first element is domain, the second is host and the third is suffix.
	 */
	public static String[] splitDomainHostSuffixFromNormalizedURL(String url){
		
		long tmp=getHostBeginEndIndex(url);
		int end=(int)tmp;
		int start=(int)(tmp>>>32);
		
		String host=null;
		if(start==end){			//起始和结尾相同，则为空字符串
			host="";
		}
		else{
			host=url.substring(start,end);
		}
		

		String domain=getDomain(host);			//获取domain
		
		String[] domain_host_suffix=new String[3];
		domain_host_suffix[0]=domain;
		domain_host_suffix[1]=host;
		
		if(end<url.length()){						//获取suffix
			domain_host_suffix[2]=url.substring(end);
		}
		else{
			domain_host_suffix[2]="";
		}
		
		return domain_host_suffix;
	}
//------------------------------------------- end of fifth part ------------------------------------------//
	
	
	
	
	
	
//------------------------------------------- sixth part -------------------------------------------------//
	public static int getHostDepth(String host, String domain){	// anti host-name spam
		final int host_len=host.length();
		final int domain_len=domain.length();
		if(host_len==domain_len)
			return 1;
		
		final int detect_len=host_len-domain_len;
		int diff=1;
		for(int i=0;i<detect_len;++i){
			if(host.charAt(i)=='.'){
				++diff;
			}
		}
		
		if(diff==1 || diff>=3){
			return diff;
		}
		else{	//diff==2 here
			if( host.regionMatches(0, "www.", 0, 4)){
				return 1;
			}
			else{
				return 2;
			}
		}
	}
	
	public static int getQueryDepth(String url_part){
		int question_mark_idx=url_part.indexOf('?');
		if(question_mark_idx==-1){
			return 0;
		}
		int end=url_part.indexOf('#', question_mark_idx+1);
		if(end==-1){
			end=url_part.length();
		}
		int amp_cnt=1;
		int equ_cnt=0;
		for(int i=question_mark_idx+1; i<end;++i){
			char c=url_part.charAt(i);
			if(c=='&'){
				++amp_cnt;
			}
			else if(c=='='){
				++equ_cnt;
			}
		}
		return amp_cnt>equ_cnt ? amp_cnt : equ_cnt;
	}
	
	public static int getSuffixDepth(String suffix){
		final int len=suffix.length();
		if(len<=1)				//suffix
			return 0;
		
		int i=0;
		char c='\u0000';
		for(;i<len;++i){			//go to the first / ? or #
			c=suffix.charAt(i);
			if(c=='/' || c=='?' || c=='#')
				break;
		}							
		
		if(i>=len-1 || c=='#'){		//是否直接返回
			return 0;
		}
		else if(c=='?'){		//计算query的深度
			++i;
			int amp_cnt=1;
			int equ_cnt=0;
			for(;i<len;++i){
				c=suffix.charAt(i);
				if(c=='&'){
					++amp_cnt;
				}
				else if(c=='='){
					++equ_cnt;
				}
				else if(c=='#'){
					break;
				}
			}
			return amp_cnt>equ_cnt ? amp_cnt : equ_cnt;
		}
		else{		// 此时 c=='/' 且 c不是suffix中最后一个字符
			++i;	// 前移一步
			int slashCnt=0;		// slash 计数
			int underlineCnt=0;	// 下划线计数
			int hyphenCnt=0;	// 连字符计数
			int dotCnt=0;		// dot计数
			char last_c='/';	// 记录上一个字符
			for( ;i<len ;++i){
				c=suffix.charAt(i);
				if(c=='/'){
					slashCnt++;
				}
				else if(c=='-'){
					hyphenCnt++;
				}
				else if(c=='_'){
					underlineCnt++;
				}
				else if(c=='.'){
					dotCnt++;
				}
				else if(c=='?' || c=='#'){
					break;
				}
				last_c=c;
			}
			
			if(last_c!='/')		//跳出前上一个字符不是 / , 补一下
				slashCnt++;
			
			int sum=hyphenCnt+underlineCnt/2+dotCnt/2-slashCnt;
			if(sum>0)
				sum+=slashCnt;
			else
				sum=slashCnt;
			
			if(i>=len-1 || c=='#'){
				return sum;
			}
			else{		//计算query深度
				++i;
				int amp_cnt=1;
				int equ_cnt=0;
				for(;i<len;++i){
					c=suffix.charAt(i);
					if(c=='&'){
						++amp_cnt;
					}
					else if(c=='='){
						++equ_cnt;
					}
					else if(c=='#'){
						break;
					}
				}
				return sum + (amp_cnt>equ_cnt ? amp_cnt : equ_cnt);
			}
		}
	}
	
	private static String[] untrustWords={
		"bbs","blog","forum","space","club","luntan","shequ","weibo","twitter",".t.","boke","tieba"
	};
	
	private static int addUntrustDepth(String[] domian_host_suffix){
		String host=domian_host_suffix[1];
		String suffix=domian_host_suffix[2];
		
		if(host.regionMatches(0, "t.", 0, 2)){
			return 1;
		}
		
		for(String temp : untrustWords){
			if(host.contains(temp))
				return 1;
			if(suffix.contains(temp))
				return 1;
		}
		
		return 0;
	}
	
	public static int getFinalDepth(String[] domain_host_suffix, boolean detect_untrust){
		int raw_depth=getHostDepth(domain_host_suffix[1],domain_host_suffix[0])+getSuffixDepth(domain_host_suffix[2]);
		return detect_untrust ? raw_depth+addUntrustDepth(domain_host_suffix) : raw_depth;
	}	
//------------------------------------------- end of sixth part ------------------------------------------//

	
	
	

	
	
	

//------------------------------------------- seventh part -------------------------------------------------//
//	public static boolean isCNHostPage(String host){
////		if(host==null)
////			return false;
//		
//		int end=host.length();
//		if(end<=4)				//最小长度是4
//			return false;
//		
//		char end_char=host.charAt(--end);
//		if(end_char=='n'){		//检测 .cn
//			if(host.charAt(--end)=='c' && host.charAt(--end)=='.'){
//				return true;
//			}
//			else{
//				return false;
//			}
//		}
////		else if(end_char=='/'){			//检测 .cn/
////			if(host.charAt(--end)=='n' && host.charAt(--end)=='c' && host.charAt(--end)=='.'){
////				return true;
////			}
////			else{
////				return false;
////			}
////		}
//		else{
//			return false;
//		}
//	}
	
	public static boolean urlIsHostPage(String url, boolean can_contain_port){
		long tmp=getHostBeginEndIndex(url);
		int end=(int)tmp;
		return onlySuffixCheckIsHostPage(url.substring(end), can_contain_port);
	}
	
	/**
	 * 通过suffix判断是否首页
	 * @param suffix URL切分出来的suffix
	 * @param can_contain_port
	 * @return 是否首页
	 */
	public static boolean onlySuffixCheckIsHostPage(String suffix, boolean can_contain_port){
		final int len=suffix.length();
		if(len==0){
			return true;
		}
		
		char c='\u0000';
		int i=0;
		for(;i<len;++i){			//go to the first / ? or #
			c=suffix.charAt(i);
			if(c=='/' || c=='?' || c=='#')
				break;
		}
		
		if(!can_contain_port && i!=0){
			return false;
		}
		
		if(c=='/'){
			if(i>=len-1){
				return true;
			}
			else{
				return false;
			}
		}
		else if(c=='?' || c=='#'){		//出现 ? 或 # , 肯定不是host page 的url
			return false;
		}
		else{
			return true;
		}
	}
	
	public static boolean isCNHost(String host){
		return host.endsWith(".cn");
	}
	
	
	public enum LinkType{
		DIFF_DOMAIN,
		SAME_DOMAIN_DIFF_HOST,
		SAME_HOST
	}
	
	public static LinkType checkLinkType(String[] from_domain_host_suffix, String[] to_domain_host_suffix){
		if(from_domain_host_suffix[0].equals(to_domain_host_suffix[0])){
			if(from_domain_host_suffix[1].equals(to_domain_host_suffix[1])){
				return LinkType.SAME_HOST;
			}
			else{
				return LinkType.SAME_DOMAIN_DIFF_HOST;
			}
		}
		else{
			return LinkType.DIFF_DOMAIN;
		}
	}
//------------------------------------------- end of seventh part ------------------------------------------//

	
	
	
	
//------------------------------------------- begin of temp part -------------------------------------------//
	private static boolean[] validChar;
	static{
		String v_c_str="0123456789abcdefghijklmnopqrstuvwxyz$-_.+!*'(),&/:;=?@%<>\"#{}|\\^~[]`ABCDEFGHIJKLMNOPQRSTUVWXYZ";
						//数字	  //字母						//特殊	   //保留      //不安全		  //大写字母
		validChar=new boolean[128];
		for(int i=0;i<128;++i){
			validChar[i]=false;
		}
		final int len=v_c_str.length();
		for(int i=0;i<len;++i){
			validChar[v_c_str.charAt(i)]=true;
		}
	}
	
	/**
	 * 判断URL的格式是否正常
	 * @param url
	 * @return
	 */
	public static boolean isBadFormat(String url){
		
		final int len=url.length();
		int begin=0;		//host起始的初始下标
		if(url.regionMatches(0, "http://", 0, 7)){	//是否是http开头
			begin=7;
		}
						
		if(begin==0){
			if(len<4 || len>248){
				return true;
			}
		}
		else{
			if(len<11 || len>255){
				return true;
			}
		}
		
		int i=begin;
		int dot_cnt=0;
		char c=0;
		char last_c='.';
		boolean all_is_digit=true;
		for(;i<len;++i){
			c=url.charAt(i);
			if(last_c=='.'){	//check每一段的首字符
				if( c>='\u0061'&&c<='\u007a' ){
					all_is_digit=false;
					last_c=c;
					continue;
				}
				else if( c>='\u0030'&&c<='\u0039' ){
					last_c=c;
					continue;
				}
				else{
					return true;
				}
			}
			else if(c=='.'){		//check上一个段的尾字符
				if( last_c>='\u0061'&& last_c<='\u007a' ){
					all_is_digit=false;
					last_c=c;
					++dot_cnt;
					continue;
				}
				else if( last_c>='\u0030'&& last_c<='\u0039' ){
					last_c=c;
					++dot_cnt;
					continue;
				}
				else{
					return true;
				}
			}
			else if( (c>='\u0061'&&c<='\u007a')||(c=='-') ){//ascii小写字母或'-'
				all_is_digit=false;
				last_c=c;
				continue;
			}
			else if( c>='\u0030'&&c<='\u0039' ){	//ascii数字
				last_c=c;
				continue;
			}
			else if( c=='/' || c==':' || c=='#' || c=='?' ){	//结束
				if((last_c>='\u0061'&& last_c<='\u007a')){
					all_is_digit=false;
					last_c=c;
					break;
				}
				else if( last_c>='\u0030'&& last_c<='\u0039' ){
					last_c=c;
					break;
				}
				else{
					return true;
				}
			}
			else{
				return true;
			}
		}
		
		if(dot_cnt<1 || dot_cnt>5)	//级数过多的主机
			return true;
		
		if(i==len-1){
			if(!all_is_digit){
				String top_domain_name=url.substring(url.lastIndexOf('.', len-1)+1,len-1);
				if(!isOldValidTopLevelDomain(top_domain_name)){
					return true;
				}
			}
			return false;
		}
		if(i==len){//直接就是host
			if((last_c>='\u0061'&& last_c<='\u007a')){
				String top_domain_name=url.substring(url.lastIndexOf('.')+1);
				if(!isOldValidTopLevelDomain(top_domain_name)){
					return true;
				}
				return false;
			}
			else if(c>='\u0030'&&c<='\u0039'){
				if(!all_is_digit){
					String top_domain_name=url.substring(url.lastIndexOf('.')+1);
					if(!isOldValidTopLevelDomain(top_domain_name)){
						return true;
					}
				}
				return false;
			}
			else
				return true;
		}
	
		++i;
		for(;i<len;++i){
			c=url.charAt(i);
			if(c>'\u007F')
				return true;
			if(!validChar[c]){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean rawIsNeedParseIP(String host){
		final int len=host.length();
		if(len<4)
			return false;
		
		char c=0;
		for(int i=0;i<len;++i){
			c=host.charAt(i);
			if((c>='\u0061'&&c<='\u007a')||(c=='-'))	//发现有字母或-
				return true;
		}
		
		return false;
	}
//------------------------------------------- end of temp part -------------------------------------------//
	
	public static String getFile(String suffix)
	{
		int start=suffix.lastIndexOf('/');
		int tmp_end1=suffix.lastIndexOf('#',start);
		int tmp_end2=suffix.lastIndexOf('?',start);
		int end=suffix.length();
		if(tmp_end1>-1&&tmp_end1<end)
			end=tmp_end1;
		if(tmp_end2>-1&&tmp_end2<end)
			end=tmp_end2;
		return suffix.substring(start,end);
	}
}
