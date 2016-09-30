package com.itheima.mobilesafe74.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackUp {

	private static int index;

	/**
	 * 备份短信的方法
	 * @param ctx				上下文环境
	 * @param path				备份短信存储的路径
	 * @param progressDialog	进度条对话框对象
	 */
	public static void backup(Context ctx, String path,
			CallBack callBack) {
		Cursor cursor = null;
		FileOutputStream fos = null;
		try {
			//1.获取备份短信写入的文件
			File file = new File(path);
			//2.获取内容解析器,获取短信数据库中数据的过程
			cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address","date","type","body"}, null, null, null);
			//3.获取文件相应的输出流
			fos = new FileOutputStream(file);
			//4.序列化数据库中读取的数据，放置到xml中
			XmlSerializer newSerializer = Xml.newSerializer();
			//5.给xml做相应的设置
			newSerializer.setOutput(fos, "utf-8");
			//DTD(xml的规范)
			newSerializer.startDocument("utf-8", true);
			newSerializer.startTag(null, "smss");
			//6.备份短信总数的指定
			int count = cursor.getCount();
//			progressDialog.setMax(count);
			if (callBack!=null) {
				callBack.setMax(count);
			}
			//7.读取数据库中的每一行数据，写入到xml中
			while (cursor.moveToNext()) {
				newSerializer.startTag(null, "sms");
				
				newSerializer.startTag(null, "address");
				newSerializer.text(cursor.getString(0));
				newSerializer.endTag(null, "address");
				
				newSerializer.startTag(null, "date");
				newSerializer.text(cursor.getString(1));
				newSerializer.endTag(null, "date");
				
				newSerializer.startTag(null, "type");
				newSerializer.text(cursor.getString(2));
				newSerializer.endTag(null, "type");
				
				newSerializer.startTag(null, "body");
				newSerializer.text(cursor.getString(3));
				newSerializer.endTag(null, "body");
				
				newSerializer.endTag(null, "sms");
				
				//8.更新进度条,可以在子线程中更新UI
				index++;
//				progressDialog.setProgress(index);
				if (callBack!=null) {
					callBack.setProgress(index);
				}
				Thread.sleep(500);
			}
			
			newSerializer.endTag(null, "smss");
			newSerializer.endDocument();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (fos != null && cursor != null) {
				try {
					fos.close();
					cursor.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*回调
	1.定义一个接口
	2.定义接口中未实现的业务逻辑方法(短信总数的设置，备份过程中短信百分比的更新)
	3.传递一个实现了此接口的类的对象(至备份短信得工具类中)，接口的实现类，一定实现了上述两个未实现的方法(就决定了使用对话框，还是进度条)
	4.获取传递进来的对象，在合适的地方(设置总数，设置百分比)做方法的调用*/
	
	public interface CallBack{
		
		//短信总数未实现的方法
		public void setMax(int max);
		
		//更新百分比的方法
		public void setProgress(int index);
	}
	
	
}
