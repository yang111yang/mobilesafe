package com.itheima.mobilesafe74.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * 流和字符串相互转换的工具类
 * @author 刘建阳
 * @date 2016-9-6 下午5:08:47
 */
public class StreamUtil {

	/**
	 * 流转换成字符串
	 * @param is	流对象
	 * @return		流转换成的字符串	返回null代表异常
	 */
	public static String stream2String(InputStream is) {
		//1.在读取的过程中，将读取的内容存储至缓存中，然后一次性转换成字符串返回
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//2.读流的操作，读到没有位置(循环)
		byte[] buffer = new byte[1024];
		int temp = -1;
		try {
			while ((temp = is.read(buffer))!=-1) {
				bos.write(buffer,0,temp);
				
			}
			//返回读取的数据
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				is.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return null;
		
	}


}
