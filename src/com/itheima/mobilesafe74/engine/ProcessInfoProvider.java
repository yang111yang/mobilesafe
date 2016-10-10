package com.itheima.mobilesafe74.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.domain.ProcessInfo;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

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
	
	
	/**
	 * 获取进程相关信息的方法
	 * @param ctx 上下文环境
	 * @return    当前手机正在运行的进程的信息的集合
	 */
	public static List<ProcessInfo> getProcessInfo(Context ctx){
		//获取进程相关信息
		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		//1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//获取PackageManager对象
		PackageManager pm = ctx.getPackageManager();
		//2.获取正在运行的进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//3.遍历runningAppProcesses,获取进程的相关信息(名称，包名，图标，使用内存的大小，是否为系统进程(状态机))
		for (RunningAppProcessInfo info : runningAppProcesses) {
			//创建进程条目所显示内容所在的JavaBean对象
			ProcessInfo processInfo = new ProcessInfo();
			//4.获取进程包名
			processInfo.packageName = info.processName;
			//5.获取进程所占内存的大小
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			//6.返回数组中索引位置为0的对象，为当前进程的内存信息对象
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			//7.获取已使用的内存大小
			processInfo.memSize = memoryInfo.getTotalPrivateDirty()*1024;
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
				//8.获取应用的名称
				processInfo.name = applicationInfo.loadLabel(pm).toString();
				//9.获取应用的图标
				processInfo.icon = applicationInfo.loadIcon(pm);
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					//是系统进程
					processInfo.isSystem = true;
				}else{
					//非系统进程
					processInfo.isSystem = false;
				}
			} catch (NameNotFoundException e) {
				//异常，需要处理
				processInfo.name = processInfo.packageName;
				processInfo.icon = ctx.getResources().getDrawable(R.drawable.ic_launcher);
				processInfo.isSystem = true;
				e.printStackTrace();
			}
			processInfoList.add(processInfo);
			
		}
		return processInfoList;
	}


	/**
	 * 杀死进程的方法
	 * @param ctx			上下文环境
	 * @param processInfo   要杀死进程所在的对象
	 */
	public static void killProcess(Context ctx, ProcessInfo processInfo) {
		//1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2.杀死进程(权限)
		am.killBackgroundProcesses(processInfo.packageName);
	}


	public static void killAll(Context ctx) {
		//1.获取ActivityManager对象
		ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2.获取正在运行的进程的总数
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//3.遍历集合
		for (RunningAppProcessInfo info : runningAppProcesses) {
			//排除掉当前应用
			if (info.processName.equals(ctx.getPackageName())) {
				continue;
			}
			am.killBackgroundProcesses(info.processName);
		}
	}
	
	
	
	
	
	
}
