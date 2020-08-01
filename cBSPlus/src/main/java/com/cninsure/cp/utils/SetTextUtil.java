package com.cninsure.cp.utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class SetTextUtil {

	public static void setTextViewText(TextView Tv , String msg){
		if (Tv!=null && msg!=null && !"null".equals(msg)) {
			Tv.setText(msg);
		}
	}
	
	public static void setEditText(EditText ETv , String msg){
		if (ETv!=null && msg!=null) {
			ETv.setText(msg);
		}
	}
	
	public static void setOnclickShowLongDatePickerDialog(final TextView Tv,final Context context){
		Tv .setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateChoiceUtil.setLongDatePickerDialog(context, Tv);
			}
		});
	}

	public static void setTvTextForArr(TextView Tv , String[] strArr, int position) {
		if (position > -1 && strArr != null && strArr.length > position) {
			String msg = strArr[position];
			if (Tv != null && msg != null && !"null".equals(msg)) {
				Tv.setText(msg);
			}
		}
	}
	
	/**为textView添加一个listener，如果内容有变化就修改对应保存对象中去*/
//	public static void setETVChangedListener(final EditText ETv ,  final String msg){
//		ETv.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				msg=ETv.getText().toString();
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
//			@Override
//			public void afterTextChanged(Editable arg0) {}
//		});
//	}
}
