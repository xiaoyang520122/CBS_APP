package com.cninsure.cp.utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cninsure.cp.utils.regex.RegexUtils;

public class SetTextUtil {
	public final static int ID_CARD = 1; //是否为身份证号码
	public final static int MOBILE = 2; //是否为手机号码
	public final static int PHONE = 3; //是否为固定电话号码
	public final static int BANK_CARD = 4; //是否为银行卡号
	public final static int CAR_NO = 5; //是否为车牌号
	public final static int VIN = 6; //是否为车架号 VIN码
	public final static int ENGIN = 7; //是否为 发动机号
//	public final static int MOBILE = 8; //是否为
//	public final static int MOBILE = 9; //是否为

	public static void setTextViewText(TextView Tv , String msg){
		if (Tv!=null && msg!=null && !"null".equals(msg)) {
			Tv.setText(msg);
		}else if (Tv!=null){
			Tv.setText("");
		}
	}
	
	public static void setEditText(EditText ETv , String msg){
		if (ETv!=null && msg!=null) {
			ETv.setText(msg);
		}else if (ETv!=null){
			ETv.setText("");
		}
	}

	/**显示文本，并判断是否为指定类型**/
	public static void setTextViewText(TextView Tv , String msg,int type){
		if (Tv!=null && msg!=null && !"null".equals(msg)) {
			Tv.setText(msg);
			if (isAppointType(msg,type)) Tv.setError("格式错误，请验证！");
		}else if (Tv!=null){
			Tv.setText("");
		}
	}

	public static void setEditText(EditText ETv , String msg,int type){
		if (ETv!=null && msg!=null) {
			ETv.setText(msg);
			if (isAppointType(msg,type)) ETv.setError("格式错误，请验证！");
		}else if (ETv!=null){
			ETv.setText("");
		}
	}
	
	public static void setOnclickShowLongDatePickerDialog(final TextView Tv,final Context context){
		Tv .setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateChoiceUtil.showLongDatePickerDialog(context, Tv);
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

	/**
	 * public static int ID_CARD = 1; //是否为身份证号码
	 * 	public static int MOBILE = 2; //是否为手机号码
	 * 	public static int PHONE = 3; //是否为固定电话号码
	 * 	public static int BANK_CARD = 4; //是否为银行卡号
	 * 	public static int CAR_NO = 5; //是否为车牌号
	 * @param strParems
	 * @param type
	 * @return
	 */
	private static boolean isAppointType(String strParems,int type){
		switch (type){
			case ID_CARD: return RegexUtils.checkIdCard(strParems);
			case MOBILE: return RegexUtils.checkMobile(strParems);
			case PHONE: return RegexUtils.checkPhone(strParems);
			case BANK_CARD: return RegexUtils.checkBankCard(strParems);
			case CAR_NO: return RegexUtils.checkCarNo(strParems);
			case VIN: return RegexUtils.checkVin(strParems);
			case ENGIN: return RegexUtils.checkEngineNumber(strParems);
			default:
				return true;
		}
	}
}
