package com.cninsure.cp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;

import com.cninsure.cp.view.CustomDatePicker;

public class DateChoiceUtil {
	
	private static CustomDatePicker timePicker;
	private static SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

	
	/**获取时间（yyyy-MM-dd）并赋值到对应的TextView**/
	public static void setShortDatePickerDialog(final Context context,final TextView textTv) {
		final Calendar cal = Calendar.getInstance();
		textTv.setOnClickListener(new OnClickListener() {
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(View arg0) {
				DatePickerDialog pickerDialog=new DatePickerDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
						, new OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker arg0, int y, int m, int d) {
								String checkDate=y+"-"+(m+1)+"-"+d;
								textTv.setText(checkDate);
							}
				}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
				pickerDialog.show();
			}
		});
	}
	
	 public  void getTimeByCalendar(){
	        Calendar cal = Calendar.getInstance();
	        int year = cal.get(Calendar.YEAR);//获取年份
	        int month=cal.get(Calendar.MONTH);//获取月份
	        int day=cal.get(Calendar.DATE);//获取日
	        int hour=cal.get(Calendar.HOUR);//小时
	        int minute=cal.get(Calendar.MINUTE);//分           
	        int second=cal.get(Calendar.SECOND);//秒
	        int WeekOfYear = cal.get(Calendar.DAY_OF_WEEK);//一周的第几天
	        System.out.println("现在的时间是：公元"+year+"年"+month+"月"+day+"日      "+hour+"时"+minute+"分"+second+"秒       星期"+WeekOfYear);
	    }
	 
	 /**获取完整时间（yyyy-MM-dd HH:mm:ss）并赋值到对应的TextView，只能选择的时间范围2000年-2500年**/
		public static void showLongDatePickerDialog(final Context context, final TextView textTv) {
	        timePicker = new CustomDatePicker(context, "请选择时间", new CustomDatePicker.ResultHandler() {
	            @Override
	            public void handle(String time) {
	            	textTv.setText(time+":00");
	            }
	        }, "2000-01-01 00:00", "2500-12-31 23:59");//"2027-12-31 23:59"
	        timePicker.showSpecificTime(true);
	        timePicker.setIsLoop(true);
	        timePicker.show(sdf.format(new Date()));
	    }
		
		 /**获取完整时间（yyyy-MM-dd HH:mm:ss）并赋值到对应的TextView，只能选择的时间范围2000年-2500年**/
		public static void setLongDatePickerDialogOnClick(final Context context,final TextView textTv) {
			textTv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showLongDatePickerDialog(context, textTv);
				}
			});
		}
}
