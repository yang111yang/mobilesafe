package com.itheima.mobilesafe74.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.itheima.mobilesafe74.domain.ProcessInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class ProcessInfoProvider {

	/**
	 * 获取进程总数的方法
	 * @param ctx 上下文环境
	 * @return	进程的总数
	 */
	public static int getProcessCount(Context ctx){
		//1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2.获取正在运行的进程的总数
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//3.返回进程的总数
		return runningAppProcesses.size();
	}
	
	
	/**
	 * 获取可用内存大小的方法
	 * @param ctx 上下文环境
	 * @return 返回可用内存数	bytes
	 */
	public static long getAvailSpace(Context ctx){
		//1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2.构建存储可用内存的对象
		MemoryInfo memoryInfo = new MemoryInfo();
		//3.给memoryInfo对象(可用内存)赋值
		am.getMemoryInfo(memoryInfo);
		//4.获取memoryInfo中可用内存的大小
		return memoryInfo.availMem;
	}
	
	/**
	 * 获取总内存大小的方法
	 * @param ctx 上下文环境
	 * @return    返回总内存数	bytes  返回0 表示异常
	 */
	public static long getTotalSpace(Context ctx){
		/*//1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2.构建存储可用内存的对象
		MemoryInfo memoryInfo = new MemoryInfo();
		//3.给memoryInfo对象(可用内存)赋值
		am.getMemoryInfo(memoryInfo);
		//4.获取memoryInfo中可用内存的大小
		return memoryInfo.totalMem;*/
		
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		
		//内存大小都写入一个文件中了，读取proc/meminfo文件，读取第一行，获取数字字符，转换成bytes返回
		try {
			fileReader = new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			String lineOne = bufferedReader.readLine();
			char[] charArray = lineOne.toCharArray();
			StringBuffer stringBuffer = new StringBuffer();
			for (char c : charArray) {
				if (c>='0' && c<='9') {
					stringBuffer.append(c);
				}
			}
			return Long.parseLong(stringBuffer.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileReader != null && bufferedReader != null) {
				try {
					fileReader.close();
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	
	
	public static List<ProcessInfo> getProcessInfo(){
		return null;
		
	}
	
	
	
	
	
	
}
