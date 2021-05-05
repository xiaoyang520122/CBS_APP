package com.cninsure.cp.utils;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class HttpUtils {
	
	public static void requestPost(final String minterface, final List<NameValuePair> params, final int typecode){
		new Thread(){
			@Override
			public void run() {
				super.run();
				try {
					HttpRequestTool.sendPost(minterface, params, typecode);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public static void requestGet(final String minterface, final List<String> params, final int typecode){
		new Thread(){
			@Override
			public void run() {
				super.run();
				HttpRequestTool.sendGet(minterface, params, typecode);
			}
		}.start();
	}
	
//	private static String str="http://([\w-]+\.)+[\w-]+(/[\w- ./?%&=]*)?"
	
	public static void openUrl(Activity activity,String url){
		  Intent intent = new Intent();     
          intent.setAction("android.intent.action.VIEW");           
          Uri content_url = Uri.parse(url);   
          intent.setData(content_url);  
          try {
			activity.startActivity(intent);
		} catch (Exception e) {
			ToastUtil.showToastLong(activity, "更新失败，请重试或者联系管理员！");
			e.printStackTrace();
		}
	}

}
