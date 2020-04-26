package com.cninsure.cp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CallUtils {
	
	public static void call(Activity context, String phone){
		if (phone==null || TextUtils.isEmpty(phone)) {
			DialogUtil.getErrDialog(context, "手机号码为空!").show();
		}else {
			try {
				Intent intent=new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone));
				//调用checkSelfPermission检查是否有权限
				if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED) {
					context.startActivity(intent);
				}else{
					ActivityCompat.requestPermissions(context,new String []{Manifest.permission.CALL_PHONE},1);//无权限则询问开启权限
				}
			} catch (Exception e) {
				DialogUtil.getErrDialog(context, "拨号失败，请确认号码后手动拨号！").show();
				e.printStackTrace();
			} 
		}
	}

}
