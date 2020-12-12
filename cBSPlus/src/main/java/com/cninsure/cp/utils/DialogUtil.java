package com.cninsure.cp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.cninsure.cp.R;

public class DialogUtil {
	
	private static Dialog defuldialog;

	/** 通过传递的字符串进行显示操作 **/
	public static Dialog getItemDialog(Context context, DialogInterface.OnClickListener listener, String... itemMsg) {
		Dialog dilog = new AlertDialog.Builder(context).setTitle("操作类型").setItems(itemMsg, listener).setNeutralButton("取消", null).create();
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=dilog;
		return defuldialog;
	}

	/** 错误提示！ **/
	public static Dialog getErrDialog(Context context, String Msg) {
		Dialog dilog = new AlertDialog.Builder(context).setTitle("提示！").setIcon(R.drawable.c).setMessage(Msg).setNeutralButton("确定", null).create();
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=dilog;
		return defuldialog;
	}

	/** 错误提示！传入需要关闭Dialog时执行的事件 **/
	public static Dialog getErrDialogAndFinish(Context context, String Msg, DialogInterface.OnDismissListener listener) {
		Dialog dilog = new AlertDialog.Builder(context).setTitle("提示！").setIcon(R.drawable.c).setMessage(Msg).setNeutralButton("确定", null).create();
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		dilog.setOnDismissListener(listener);
		defuldialog=dilog;
		return defuldialog;
	}



	/** 错误提示！传入需要关闭Dialog时执行的事件 **/
	public static Dialog getRightDialogAndFinish(Context context, String Msg, DialogInterface.OnDismissListener listener) {
		Dialog dilog = new AlertDialog.Builder(context).setTitle("提示！").setIcon(R.drawable.choice_green48).setMessage(Msg).setNeutralButton("确定", null).create();
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		dilog.setOnDismissListener(listener);
		defuldialog=dilog;
		return defuldialog;
	}
	
	/** 操作成功提示！ **/
	public static Dialog getRightDialog(Context context,String buttonStr, String Msg) {
		Dialog dilog = new AlertDialog.Builder(context).setTitle("提示！").setIcon(R.drawable.choice_green48).setMessage(Msg).setNeutralButton(buttonStr, null).create();
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=dilog;
		return defuldialog;
	}
	
	public static Dialog getDialogByView(Context context, View view) {
		Builder buile = new AlertDialog.Builder(context);
		buile.setView(view);
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=buile.create();
		return defuldialog;
	}

	public static Dialog getAlertDialog(Context paramContext, String paramString) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
		localBuilder.setTitle("提示信息");
		localBuilder.setMessage(paramString);
		localBuilder.setPositiveButton("确定", null);
		localBuilder.setNegativeButton("取消", null);
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=localBuilder.create();
		defuldialog.setCancelable(true);
		defuldialog.setCanceledOnTouchOutside(true);
		return defuldialog;
	}
	
	public static Dialog getAlertOneButton(Context paramContext, String paramString,DialogInterface.OnClickListener listener) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
		localBuilder.setTitle("提示信息");
		localBuilder.setMessage(paramString);
		localBuilder.setPositiveButton("确定", listener);
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=localBuilder.create();
		defuldialog.setCancelable(true);
		defuldialog.setCanceledOnTouchOutside(true);
		return defuldialog;
	}
	
	public static Dialog getAlertOnelistener(Context paramContext, String paramString,DialogInterface.OnClickListener listener) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
		localBuilder.setTitle("提示信息");
		localBuilder.setMessage(paramString);
		localBuilder.setPositiveButton("确定", listener);
		localBuilder.setNegativeButton("取消", null);
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=localBuilder.create();
		defuldialog.setCancelable(true);
		defuldialog.setCanceledOnTouchOutside(true);
		return defuldialog;
	}
	public static Dialog getAlertOnelistener(Context paramContext, String title,String paramString,DialogInterface.OnClickListener listener) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
		localBuilder.setTitle(title);
		localBuilder.setMessage(paramString);
		localBuilder.setPositiveButton("确定", listener);
		localBuilder.setNegativeButton("取消", null);
		try {
			if (defuldialog!=null && defuldialog.isShowing()) {
				dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		defuldialog=localBuilder.create();
		defuldialog.setCancelable(true);
		defuldialog.setCanceledOnTouchOutside(true);
		return defuldialog;
	}
	public static Dialog getDialogByViewOnlistener(Context context, View view,String title,DialogInterface.OnClickListener listener) {
		Builder buile = new AlertDialog.Builder(context);
		buile.setTitle(title);
		buile.setView(view);
		buile.setNegativeButton("取消", null);
		buile.setPositiveButton("确定", listener);
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=buile.create();
		defuldialog.setCancelable(true);
		defuldialog.setCanceledOnTouchOutside(true);
//		defuldialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		return defuldialog;
	}
	
	public static Dialog getAlertOnelistener(Context paramContext, String paramString,DialogInterface.OnClickListener listener
			,String buttonStr1,String buttonStr2,DialogInterface.OnClickListener listener2) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
		localBuilder.setTitle("提示信息");
		localBuilder.setMessage(paramString);
		localBuilder.setPositiveButton(buttonStr1, listener);
		localBuilder.setNegativeButton(buttonStr2, listener2);
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=localBuilder.create();
		defuldialog.setCancelable(true);
		defuldialog.setCanceledOnTouchOutside(true);
		return defuldialog;
	}

	public static void setOnclickToShowDialogAlert(Context context, String alertMsg,String title,View onclickView) {
		onclickView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAlertDialog(context,alertMsg,title).show();
			}
		});
	}

	public static Dialog getAlertDialog(Context context, String alertMsg,String title) {
		if ( alertMsg==null || "null".equals(alertMsg)) {
			alertMsg = "无";
		}
		Builder buile = new AlertDialog.Builder(context);
		buile.setTitle(title);
		buile.setMessage(alertMsg);
		buile.setPositiveButton("确定", null);
		if (defuldialog!=null && defuldialog.isShowing()) {
			dismiss();
		}
		defuldialog=buile.create();
		defuldialog.setCancelable(true);
		defuldialog.setCanceledOnTouchOutside(true);
		return defuldialog;
	}

	public static void dismiss(){
		try {
			if (defuldialog!=null && defuldialog.isShowing()) {
				defuldialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void show() {
		if (defuldialog!=null) {
			try {
				defuldialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
