package com.cninsure.cp.utils;

import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;


public class CheckHttpResult {
	
	public static int checkList(List<NameValuePair> values,Context context){
		int rcode=Integer.valueOf(values.get(1).getValue());
		int typecode=Integer.valueOf(values.get(0).getName());
		if (rcode==200) {
			return typecode;
		}else if (rcode==400 && typecode!=HttpRequestTool.UPLOAD_LOCATION) {
			DialogUtil.getAlertOneButton(context, values.get(0).getValue(), null).show();
		}else if (rcode==400 && typecode==HttpRequestTool.UPLOAD_LOCATION) {
			ToastUtil.showToastShort(context,  values.get(0).getValue());
		}else {
//			ToastUtil.showToastLong(context, "网络错误请重新登录或联系管理员！="+values.get(0).getValue());
		}
		return 0;
	}
	

/**
 * 如果调用接口返回码为400或401需要退出当前界面或者跳到登录界面的调用该接口
 */
	public static int checkList(List<NameValuePair> values,final Activity context,final int... code){
		int rcode=Integer.valueOf(values.get(1).getValue());
		final int typecode=Integer.valueOf(values.get(0).getName());
		if (rcode==200) {
			return typecode;
		}else if (rcode==400 && typecode!=HttpRequestTool.UPLOAD_LOCATION) {
			Dialog dialog=DialogUtil.getAlertOneButton(context, values.get(0).getValue(), null);
			dialog.show();
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					for (int i = 0; i < code.length; i++) {
						if (typecode==code[i]) {
							context.finish();
						}
					}
				}
			});
		}else {
			ToastUtil.showToastLong(context, typecode+"-请求失败！返回码："+rcode+values.get(0).getValue());
		}
		return 0;
	}
}
