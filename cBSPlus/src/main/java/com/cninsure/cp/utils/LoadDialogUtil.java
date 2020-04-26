package com.cninsure.cp.utils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.cninsure.cp.view.LoadingDialog;

public class LoadDialogUtil {

	private static LoadingDialog loadDialog;
	
	/**设置显示内容并返回Dialog对象*/
	private static Dialog setMessage(Context context,String Msg){
		if (loadDialog==null) {
			loadDialog=new LoadingDialog(context);
		}
		return loadDialog.setMessage(Msg);
	}
	
	/**设置显示内容并显示*/
	public static Dialog setMessageAndShow(Context context,String Msg){
		try {
			if (loadDialog!=null && loadDialog.isShowing()) {
				loadDialog.dismiss();
			}
			setMessage(context,Msg).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loadDialog;
	}
	
	/**关闭显示的Dialog**/
	public static void dismissDialog(){
		if (loadDialog!=null && loadDialog.isShowing()) {
			try {
				loadDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
	
	/** 关闭显示的Dialog **/
	public static void dismissDialog(int i) {
		Log.d("LoadDialogUtil", i+"");
		if (loadDialog != null && loadDialog.isShowing()) {
			try {
				loadDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void changeMsg(String msg){
		if (loadDialog!=null && loadDialog.isShowing()) {
			loadDialog.setMessage(msg);
		}
	}

}
