package com.yungoal.onealert.util;

import java.util.Objects;

/**
 * @author zj
 * @date 2019/7/25 11:38
 */
@SuppressWarnings({"ALL", "AlibabaUndefineMagicConstant"})
public class PathUtil {
	/**如果没打包后运行则debug为true*/
	public static boolean debug = false;
	/**项目所在路径*/
	public static final String PROJECT_PATH = initProjectPathAndDebug();
	/***
	 * 获取项目根路径，无论是打包成jar文件
	 * 为了保证调试时获取项目路径，而不是bin路径，增加逻辑： 如果以bin目录接，则返回上一层目录
	 */
	private static String initProjectPathAndDebug(){
		java.net.URL url = PathUtil.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null; 
		try {  
			filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");  
		}
		catch (Exception e) {  
			e.printStackTrace();  
		}
		assert filePath != null;
		String jar =".jar";
		if (filePath.endsWith(jar))  {
	    	filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);  
	    }
	   //如果以bin目录接，则说明是开发过程debug测试查询，返回上一层目录
		String bin1 ="bin/";
		String bin2 ="bin\\";
	    if (filePath.endsWith(bin1) || filePath.endsWith(bin2) )  {
	    	debug = true;
	    	filePath = filePath.substring(0, filePath.lastIndexOf("bin"));  
	    }
	    java.io.File file = new java.io.File(filePath);  
	    filePath = file.getAbsolutePath(); 
	    return filePath;
	}
	
	/***
	 * 这个方法打包位jar文件就无法获取项目路径了。
	 * @return 文件路径
	 */
	public static String getRealPath() {  
		String realPath = Objects.requireNonNull(PathUtil.class.getClassLoader().getResource("")).getFile();
		java.io.File file = new java.io.File(realPath);
		//去掉了最前面的斜杠/
		realPath = file.getAbsolutePath();
		try {  
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");  
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		return realPath;  
	}
}
