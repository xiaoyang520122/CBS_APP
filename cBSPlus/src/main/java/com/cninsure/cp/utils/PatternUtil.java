package com.cninsure.cp.utils;

import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class PatternUtil {
	
	public final static String emailPattern="^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
	
	/**判断Edittext输入内容是否邮箱的正则表达式，不正确提示用户*/
	public static void setEmailInput(final EditText editText) {
		
//		editText.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				   // 输入的内容变化的监听
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				  // 输入前的监听
//			}
//			
//			@Override
//			public void afterTextChanged(Editable arg0) {
//				 // 输入后的监听 邮箱名称允许汉字、字母、数字，域名只允许英文域名
//				 boolean isMatch = Pattern.matches(emailPattern, editText.getText().toString());
//				 if (!isMatch) {
//					 editText.setError("请输入正确的邮箱地址！");
//				}
//			}
//		});
		
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				 // 输入后的监听 邮箱名称允许汉字、字母、数字，域名只允许英文域名
				 boolean isMatch = Pattern.matches(emailPattern, editText.getText().toString());
				 if (!arg1 && !isMatch) {
					 editText.setError("请输入正确的邮箱地址！");
				}
			}
		});
		
	}

	
	/**判断字符串是否为邮箱地址！**/
	public static boolean isEmail(String msg){
		 boolean isMatch = Pattern.matches(emailPattern, msg);
		 return isMatch;
	}
}
