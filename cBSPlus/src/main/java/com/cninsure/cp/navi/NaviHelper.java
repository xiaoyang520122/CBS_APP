package com.cninsure.cp.navi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.cninsure.cp.utils.DialogUtil;

public class NaviHelper {

	private static Context context;

	public static void startNavi(Context context1, Float Latitude, Float Longitude,final String address,final String phone) {
		context = context1;
		if (Latitude!=null && Longitude!=null && Latitude > 0 && Longitude > 0) {
			Intent intent = new Intent(context, NavigationActivtity.class);
			intent.putExtra("Latitude", Latitude);
			intent.putExtra("Longitude", Longitude);
			intent.putExtra("biaodiAddress", address);
			intent.putExtra("phoneNumber", phone);
			context.startActivity(intent);
		} else {
			DialogUtil.getAlertOnelistener(context1, "提示信息！", "由于无经纬度信息，无法完成线路规划，接下来我们将尝试用地址进行线路规划，规划结果只供参考，请问是否继续?", 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(context, NavigationActivtity.class);
					intent.putExtra("biaodiAddress", address);
					intent.putExtra("phoneNumber", phone);
					context.startActivity(intent);
				}
			}).show();
//			ToastUtil.showToastLong(context1, "定位信息无效，无法完成线路规划！");
		}
	}

	public static void setNaviOnclick(View view,Context context1, Float Latitude, Float Longitude,final String address,final String phone){
		//设置线路规划
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				NaviHelper.startNavi(context1, Latitude, Longitude,	address, phone);
			}
		});
	}
}
