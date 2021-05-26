package com.cninsure.cp.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.bumptech.glide.util.Util;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.LoginActivity;
import com.cninsure.cp.entity.User;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 20191021 by xiaoyang 进入activity时检验该对象是否为空，如果为空提示用户并重新登录。
 * @return
 */
public class UserInfoUtil {
 private static Context context;
 private static Dialog dialog;
	/**
	 * 20191021 by xiaoyang 进入activity时检验该对象是否为空，如果为空提示用户并重新登录。
	 * @return
	 */
	@SuppressWarnings("null")
	public static boolean USERIsNull(Context context0){
		context = context0;
		User USER = AppApplication.USER;
		if (USER!=null && USER.data!=null && USER.data.userId!=null) {
			return false;
		}else {
			ToastUtil.showToastLong(context, "登录已经失效，即将重新登录！");
			showDialog();
			timer=new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					handler.sendEmptyMessage(0);
					dialog.dismiss();
				}
			}, 5*1000);
			return true;
		}
	}
	
	private static Timer timer;
	private static Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			try {
				if (Util.isOnMainThread())
					startLogin();
			} catch (Exception e) {
				e.printStackTrace();
			}
			super.handleMessage(msg);
		}
	};

	public static void startLogin(){
		Intent intent = new Intent(context, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
		context.startActivity(new Intent(context, LoginActivity.class));
	}
	private static void showDialog(){
		try {
			dialog = DialogUtil.getAlertOneButton(context, "登录已经失效，即将重新登录!", null);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
