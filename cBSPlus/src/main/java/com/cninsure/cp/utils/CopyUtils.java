package com.cninsure.cp.utils;

import android.content.Context;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

@SuppressWarnings("deprecation")
public class CopyUtils {
	
	public static void  copy(Context context,String msg){
		 // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager)context. getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(msg);
        ToastUtil.showToastShort(context, "已复制到剪贴板");
	}
	
	public static void setCopyOnclickListener(final Context context , View view , final String value){
		if (!TextUtils.isEmpty(value)) {
			view.setVisibility(View.VISIBLE);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					CopyUtils.copy(context,value);
//					 ToastUtil.showToastShort(context.getApplicationContext(), "已复制到剪贴板");
				}
			});
		}else {
			view.setVisibility(View.GONE);
		}
	}
	
	public static void setCopyOnclickListener(final Context context , View view , final String copyValue,final String AlertMsg){
		if (!TextUtils.isEmpty(copyValue)) {
			view.setVisibility(View.VISIBLE);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					CopyUtils.copy(context,copyValue);
					showAlertDialog(context,AlertMsg);
				}
			});
		}else {
			view.setVisibility(View.GONE);
		}
	}
	
	private static void showAlertDialog(Context context, String alertMsg) {
		DialogUtil.getAlertOneButton(context,alertMsg , null).show();
	}

}
