package com.weibo.datasys.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

/**
 * 文件相关的处理工具类
 * 
 * 
 */
public class FileUtils {

	/**
	 * 根据文件路径创建文件
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static boolean createFile(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.exists()) {
			filePath = file.getAbsolutePath();
			int pos = filePath.lastIndexOf(File.separator);
			String dirStr = null;
			if (pos > 0) {
				dirStr = filePath.substring(0, pos);
				createDir(dirStr);
			}
			file.createNewFile();
		}
		return true;

	}

	/**
	 * 创建目录
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static boolean createDir(String filePath) throws IOException {
		File file = new File(filePath);
		boolean flag = true;
		while (flag) {
			if (file.exists()) {
				return true;
			} else if (!file.exists() && file.getParentFile().exists()) {
				file.mkdirs();
				return true;
			} else {
				createDir(file.getParent());
			}
		}
		return true;

	}

	/**
	 * 保存网页字节数组到本地文件 filePath 为要保存的文件的相对地址
	 */
	public static void saveToLocal(byte[] data, String filePath, boolean appendflag) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new FileOutputStream(new File(filePath), appendflag));
			for (int i = 0; i < data.length; i++)
				out.write(data[i]);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 读取文件里的内容
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {

		if (!isExists(filePath)) {
			return null;
		}
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);
			String line = br.readLine();
			if (line != null) {
				return StringUtils.trim(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean isExists(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 删除文件夹和文件
	 * 
	 * @param path
	 */
	public static void deleteDir(String path) {
		File delDir = new File(path);
		if (delDir.isDirectory()) {
			File[] files = delDir.listFiles();
			for (File delF : files) {
				deleteDir(delF.getAbsolutePath());
			}
			delDir.delete();
		} else {
			delDir.delete();
		}

	}

}
