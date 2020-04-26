package com.cninsure.cp.utils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.os.Environment;
import android.text.format.DateFormat;

public class PhotoPathUtil {
	
	/**获取照片存储路径
	 * 再SD卡根目录先创建一个CBSPlus文件夹，在其中以订单编号（orderUid）为文件夹名建立储存文件的目录
	 * */
	public static String getPictureCreatePath(String orderUid) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
//			 ToastUtil.showToastLong(activity, "SD卡不可用");
			return null;
		}
		@SuppressWarnings("static-access")
		String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";

//		String fileDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera";
		String fileDirPath = Environment.getExternalStorageDirectory().getPath()+
				File.separator+"CBSPlus"+File.separator+orderUid;
				
		File fileDir = new File(fileDirPath);
		if (!fileDir.exists() || !fileDir.isDirectory()) {
			fileDir.mkdirs();
		}
		
		String fileName = fileDirPath + File.separator + name;
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		return file.getAbsolutePath();
	}

}
