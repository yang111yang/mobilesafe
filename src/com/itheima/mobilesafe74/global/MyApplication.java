package com.itheima.mobilesafe74.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

/**
 * 处理全局逻辑的类
 * @author 刘建阳
 * @date 2016-10-13 上午9:06:18
 */
public class MyApplication extends Application {
	
	protected static final String tag = "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		//捕获全局的异常
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				ex.printStackTrace();
				Log.i(tag, "捕获到了一个异常");
				
				String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"error74.log";
				File file = new File(path);
				try {
					PrintWriter printWriter = new PrintWriter(file);
					ex.printStackTrace(printWriter);
					printWriter.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				
				//上传异常到服务器
				//结束应用
				System.exit(0);
				
			}
		});
		
		
		
	}
	
	
}
