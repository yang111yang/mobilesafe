package com.itheima.mobilesafe74.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * MD5加密
 * @author 刘建阳
 * @date 2016-9-8 上午10:40:16
 */
public class Md5Util {
	/**
	 * 给指定字符串按照MD5的算法去加密
	 * @param psd 需要加密的密码	加盐处理
	 * @return	处理后的密码
	 */
	public static String encoder(String psd){
		try {
			//0.对psd加盐
			psd = psd +"mobilesafe";
			// 1.指定加密的算法类型
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// 2.将需要加密的字符串转换成byte类型的数组，然后进行随机哈希过程
			byte[] bs = digest.digest(psd.getBytes());
			System.out.println("length" + bs.length);
			// 3.遍历bs，然后让其生成一个32位的字符串，固定写法
			
			StringBuffer sb = new StringBuffer();
			for (byte b : bs) {
				int i = b & 0xff;
				// 将int类型的i转换成16进制的字符
				String hexString = Integer.toHexString(i);
				// 判断hexString的长度，如果小于2，进行补位
				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				//进行字符串的拼接
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
