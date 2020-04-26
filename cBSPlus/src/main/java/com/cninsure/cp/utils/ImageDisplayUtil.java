package com.cninsure.cp.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cninsure.cp.activity.yjx.YjxSurveyActivity;
import com.cninsure.cp.photo.DisplayPhotoActivity;

/**调用系统照片浏览器显示照片，如果是网络图片就先下载，如果是本地图片就直接显示*/

public class ImageDisplayUtil {
	
	/**调用系统照片浏览器显示照片，如果是网络图片就先下载，如果是本地图片就直接显示*/
	public static void display(Context context ,String url){
		if (url.indexOf("://")>-1) { //网络图片
			disPlayNetImg(context,url);
		}else {
			displaylocalImg(context,url);
		}
	}
	
	/**用自定义图片浏览器打开图片预览**/
	public static  void displayByMyView(Context context ,String url){
		displayImageByMyView(url, context);
	}
	
	/**显示网络图片到系统照片浏览器*/
	public static void disPlayNetImg(final Context context ,String url){
		LoadDialogUtil.setMessageAndShow(context, "图片加载中……");
		final Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		
		Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {  
            @Override  
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {  
            	//将bitmap转换为uri
				Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), resource, null, null));
				intent.setDataAndType(uri, "image/*");
				//设置intent数据和图片格式
				context.startActivity(intent);
				LoadDialogUtil.dismissDialog(5);
            }
        }); //方法中设置asBitmap可以设置回调类型  
	}
	
	/**调用系统图片浏览器查看==本地==照片**/
	public static void displaylocalImg(Context context ,String url){
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		File file = new File(url);
		//下方是是通过Intent调用系统的图片查看器的关键代码
		intent.setDataAndType(Uri.fromFile(file), "image/*");
		context.startActivity(intent);
	}
	
	public static void displayImageByMyView(String largeUrlList,final Context context){
		if (largeUrlList != null) {
			final Intent intent = new Intent(context, DisplayPhotoActivity.class);
			if (largeUrlList.indexOf("://")==-1) {
				intent.putExtra("largeUrlList", largeUrlList);
				context.startActivity(intent);
			}else {
				LoadDialogUtil.setMessageAndShow(context, "图片加载中……");
				Glide.with(context).load(largeUrlList).asBitmap().override(1920, 1080).format(DecodeFormat.PREFER_ARGB_8888).into(new SimpleTarget<Bitmap>() {  
	                @Override  
	                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {  
//	                	//将bitmap转换为uri
	                	@SuppressWarnings("unused")
//						String uriString = MediaStore.Images.Media.insertImage(context.getContentResolver(), resource, null, null);
//	                	OpenFileUtil.openFileByPath(context, uriString);
						Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), resource, null, null));
//						//设置intent数据和图片格式
						intent.putExtra("uri", uri.toString());
						context.startActivity(intent);
						LoadDialogUtil.dismissDialog(5);
	                }
	            }); //方法中设置asBitmap可以设置回调类型  
			}
		}
	}
}
