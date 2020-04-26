package com.cninsure.cp.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.loader.content.CursorLoader;
import android.util.Log;


public class UriUtils {

	public static String getFileUrl(Context context,Uri uri) {
		
		String path="";
		/**
		图片文件的路径 ，有2种格式  
		华为手机从图库选择的结果 ，以content开头  
		1、content://media/external/images/media/888737  
		华为手机从文件选择器的结果，或者系统拍照也是如下的结果，以file开头  
		2、file:///mnt/sdcard2/%E7%A8%8B%E5%BA%8F%E5%91%98%E6%97%A5%E5%B8%B8.jpg 
		* */
		String textPre = uri.toString();// 得到图片uri地址字符串
		Log.i("textPre", textPre);
		try {
			if (uri.toString().contains("content://")) {
				String[] proj = { MediaStore.Images.Media.DATA };
				CursorLoader loader = new CursorLoader(context, uri, proj, null, null, null);
				Cursor cursor = loader.loadInBackground();
				// 好像是android多媒体数据库的封装接口，具体的看Android文档
				// 按我个人理解 这个是获得用户选择的图片的索引值
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// 将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				// 最后根据索引值获取图片路径
				path = cursor.getString(column_index);
			} else {
				path = textPre.substring(7, textPre.length());
			}
		} catch (Exception e) {
			Log.i("textPre", "获取路径出错###！！");
			e.printStackTrace();
		}
		if (path==null) {
			return GetUriPathUtils.getPathByUri4kitkat(context,uri);
		}
		Log.i("textPre", "获取路径=="+path);
		return path;
	}
	
	
}
