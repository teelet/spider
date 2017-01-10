package com.weibo.datasys.main;

import java.util.Arrays;
import java.util.Date;

import com.weibo.datasys.service.ConfigService;

public class ImageMain {
	
	private static final long startTime = System.currentTimeMillis();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(new Date() + " - [SystemStarting] - args="
				+ Arrays.toString(args));
		try {
			ConfigService.init();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(new Date() + " - [InitEnvError] - System exit.");
			System.exit(-1);
		}
	}

	/**
	 * @return the starttime
	 */
	public static long getStartTime() {
		return startTime;
	}

}
