package com.cninsure.cp.ocr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.utils.ImageUtil;
import com.cninsure.cp.utils.ToastUtil;

public class WaterMaskUtil {

	private static SimpleDateFormat sf;
	
	@SuppressWarnings("resource")
	@SuppressLint("SimpleDateFormat")
	public static void set(Context context,File file){
		sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// 将文件存储在SD卡的根目录，并以系统时间将文件命名
			if (!file.exists()) {
//				ToastUtil.showToastLong(context, "jpgFile文件不存在！！="+photopath);
				return;
			}else {
				ToastUtil.showToastShort(context, "文件存在");
			}
			// 文件输出流对象
			FileOutputStream outStream = new FileOutputStream(file);
			// 将文件数据存储到文件中
//			Bitmap bitmap=getDiskBitmap(photopath);
			Bitmap bitmap=getDiskBitmap(context,file);
			if (bitmap==null) {
				ToastUtil.showToastLong(context, "bitmap为空！！"+file.getPath());
				return;
			}
			// 将文件转化为bitmap以便添加日期水印
			bitmap = ImageUtil.drawTextToRightBottom(context, bitmap,
					AppApplication.getUSER().data.name+" "+sf.format(new Date()), 10, Color.parseColor("#FF0000"), 5, 5);
			Bitmap watermark=BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_water_mask);
			//为bitmap以便添加图片水印
			bitmap=ImageUtil.createWaterMaskLeftBottom(context, bitmap, watermark, 5, 5);
			//再将bitmap转化为字节数组
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
			byte[] datas = baos.toByteArray();  
			// 将文件数据存储到文件中
			outStream.write(datas);
			// 关闭输出流
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    
	private static Bitmap getDiskBitmap(Context context,File file) {
		Bitmap bitmap = null;
		try {
//			File file = new File(pathString);
			if (file.exists()) {
//				bitmap = BitmapFactory.decodeFile(pathString);
				Uri photoUri1=Uri.fromFile(file);
				return getBitmapFormUri((Activity) context,photoUri1);
			}
//			FileInputStream fis = new FileInputStream(pathString); 
//			bitmap=BitmapFactory.decodeStream(fis);
//			bitmap = BitmapFactory.decodeFile(pathString);//	"/sdcard/bitmap.png"
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		return bitmap;
	}
	
	
	/** 
     * 通过uri获取图片并进行压缩 
     * 
     * @param uri 
     */  
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) {  
    	try {
        InputStream input = ac.getContentResolver().openInputStream(uri);  
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();  
        onlyBoundsOptions.inJustDecodeBounds = true;  
        onlyBoundsOptions.inDither = true;//optional  
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional  
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);  
        input.close();  
        int originalWidth = onlyBoundsOptions.outWidth;  
        int originalHeight = onlyBoundsOptions.outHeight;  
        if ((originalWidth == -1) || (originalHeight == -1))  
            return null;  
        //图片分辨率以480x800为标准  
        float hh = 800f;//这里设置高度为800f  
        float ww = 480f;//这里设置宽度为480f  
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;//be=1表示不缩放  
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (originalWidth / ww);  
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (originalHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        //比例压缩  
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();  
        bitmapOptions.inSampleSize = be;//设置缩放比例  
        bitmapOptions.inDither = true;//optional  
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional  
        Bitmap bitmap;
			input = ac.getContentResolver().openInputStream(uri);  
			bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);  
			input.close();
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
   
        return null;
    }  

}
