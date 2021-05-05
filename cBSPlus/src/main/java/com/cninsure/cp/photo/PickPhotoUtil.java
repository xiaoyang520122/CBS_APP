package com.cninsure.cp.photo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.cninsure.cp.utils.DialogUtil;

import java.io.File;

public class PickPhotoUtil {

	public final static int PHOTO_REQUEST_CAMERAPHOTO=1; //委托文件拍照
	public final static int PHOTO_REQUEST_ALBUMPHOTO=2; //委托文件相册选择
	public final static int PHOTO_REQUEST_CAMERAPHOTO_W=3; //作业文件拍照
	public final static int PHOTO_REQUEST_ALBUMPHOTO_W=4; //作业文件相册选择
	public final static int PHOTO_REQUEST_ALBUMPHOTO_CX_FILE=7; //作业文件相册选择

	/** 调用系统相机拍照并储存到指定路径 */
	public static void cameraPhotoToUrl(Activity context, String folderName,int code) {
		File tempFile = new File(folderName);
//		Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//		// 指定调用相机拍照后照片的储存路径
//		cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
//		context.startActivityForResult(cameraintent, code);

		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (Build.VERSION.SDK_INT < 24) {
			Uri uri = Uri.fromFile(tempFile);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			context.startActivityForResult(openCameraIntent,
					code);
		} else {
			//适配Android7.0
			ContentValues contentValues = new ContentValues(1);
			contentValues.put(MediaStore.Images.Media.DATA,folderName);
			Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
			context.grantUriPermission("com.example.lab.android.nuc.chat", uri, Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
			openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			context.startActivityForResult(openCameraIntent, code);
		}


	}
	
	/** 调用系统相册挑选照片 */
	public static void albumPhoto(Activity context,int code) {
//		Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//		getAlbum.setType("image/*");
//		context.startActivityForResult(getAlbum, code);
		
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	    intent.setType("*/*"); 
	    intent.addCategory(Intent.CATEGORY_OPENABLE);
	 
	    try {
	    	context.startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), code);
	    } catch (android.content.ActivityNotFoundException ex) {
	        DialogUtil.getAlertOneButton(context, "抱歉，无法打开文件管理器！您可以安装一个文件管理器再试一次。", null).show();
	    }
	}
	
}
