package com.cninsure.cp.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
	private static Toast mToast;
	public static boolean isNoMoreThanTwo(Context paramContext, String paramString) {
		int i = 1;
		if ((paramString.contains(".")) && (paramString.substring(1 + paramString.indexOf(".")).length() > 2)) {
//			Toast.makeText(paramContext, "输入金额最多保留两位小数", i).show();
			 if(mToast == null) {    
		            mToast = Toast.makeText(paramContext, "输入金额最多保留两位小数", i);    
		        } else {    
		            mToast.setText("输入金额最多保留两位小数");      
		            mToast.setDuration(i);    
		        }    
		        mToast.show();    
			i = 0;
		}
		return i == 0;
	}

	public static void showToastCaptchaSuccess(Context paramContext) {
		try {
//			Toast.makeText(paramContext, "验证码发送成功", 0).show();
			 if(mToast == null) {    
		            mToast = Toast.makeText(paramContext, "验证码发送成功", Toast.LENGTH_SHORT);    
		        } else {    
		            mToast.setText("验证码发送成功");      
		            mToast.setDuration(Toast.LENGTH_SHORT);    
		        }    
		        mToast.show();   
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public static void showToastLong(Context paramContext, String paramString) {
		try {
			 if(mToast == null) {    
		            mToast = Toast.makeText(paramContext, paramString, Toast.LENGTH_LONG);    
		        } else {    
		            mToast.setText(paramString);      
		            mToast.setDuration(Toast.LENGTH_LONG);    
		        }    
			 mToast.setGravity(Gravity.BOTTOM, 0, 100); 
		        mToast.show();   
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public static void showToastShort(Context paramContext, String paramString) {
		try {
//			Toast.makeText(paramContext, paramString, 0).show();
			 if(mToast == null) {    
		            mToast = Toast.makeText(paramContext, paramString, Toast.LENGTH_SHORT);    
		        } else {    
		            mToast.setText(paramString);      
		            mToast.setDuration(Toast.LENGTH_SHORT);    
		        } 
			 mToast.setGravity(Gravity.BOTTOM, 0, 100); 
		        mToast.show();   
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
	
	public static void getToastShort(Context paramContext, String paramString) {
		try {
			 if(mToast == null) {    
		            mToast = Toast.makeText(paramContext, paramString, Toast.LENGTH_SHORT);    
		        } else {    
		            mToast.setText(paramString);      
		            mToast.setDuration(Toast.LENGTH_SHORT);  
		            mToast.setGravity(Gravity.TOP, 0, 100); 
		        }    
		        mToast.show();   
			return ;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
}
