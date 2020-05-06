package com.cninsure.cp;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.dispersive.DispersiveUserActivity;
import com.cninsure.cp.utils.ActivityManagerUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.igexin.sdk.PushManager;

public class LoadingActivity extends BaseActivity {
	

	private List<NameValuePair> paramlist;
	private String namestr,platstr,passstr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_page_activity);
		// com.getui.demo.DemoPushService 为第三方自定义推送服务
//		PushManager.getInstance().initialize(this.getApplicationContext(), com.cninsure.cp.service.CbsDemoService.class);
		// com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
//		PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), com.cninsure.cp.service.CbsIntentService.class);
		EventBus.getDefault().register(this);
//		startService(new Intent(this, LocationService.class));
		sleepTime();
	}
	
	/**睡眠一会后登录*/
	private void sleepTime() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				loginUser();
			}
		}, 3000);
	}

	private void loginUser(){
		ActivityManagerUtil.getInstance().finishAllActivity();
		
		platstr=AppApplication.sp.getString("tenantPinyinInitials", "");
		namestr=AppApplication.sp.getString("loginName", "");
		passstr=AppApplication.sp.getString("password", "");
		
		paramlist=new ArrayList<NameValuePair>(6);
		if (namestr!=null && !TextUtils.isEmpty(namestr)) {
			paramlist.add(new BasicNameValuePair("plat", platstr));
			paramlist.add(new BasicNameValuePair("account", namestr));
			paramlist.add(new BasicNameValuePair("password", passstr));
			HttpUtils.requestPost(URLs.LoginByPass(), paramlist, HttpRequestTool.LOGIN_BY_PASS);
		}else {
			startActivity(new Intent(this, LoginActivity.class));
			this.finish();
		}
	}
	
	private void jumpToLoging(String value){
//		Intent intent=new Intent(this, IndexActivity.class);
//		intent.putExtra("loginvalue", value);//value.get(0).getValue()
//		Log.i("JsonHttpUtils", "B200-LoginActivity请求返回数据为：" + value.get(0).getValue());
//		startActivity(intent);
		AppApplication.saveUser(value,passstr);
		saveLoginInfo();
		jumpActivity(value);
		finish();
	}

	/***更具用户类型判断跳转的Activity*/
	private void jumpActivity(String value) {
		if ("99".equals(AppApplication.getUSER().data.userType)){//外部车童登录，跳转到外部车童界面
			Intent intent=new Intent(this, DispersiveUserActivity.class);
			LoadingActivity.this.startActivity(intent);
		}else{  //非外部车童
			Intent intent=new Intent(this, IndexActivity.class);
			intent.putExtra("loginvalue", value);
			LoadingActivity.this.startActivity(intent);
		}
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventconn(List<NameValuePair> value) {
//		Log.i("JsonHttpUtils", "A-LoginActivity请求返回数据为：" + value.get(0).getValue());
		int conncode = Integer.parseInt(value.get(1).getValue());
		int conntype = Integer.parseInt(value.get(0).getName());
		if (conntype == HttpRequestTool.LOGIN_BY_PASS) {
			switch (conncode) {
			case 200:
				jumpToLoging(value.get(0).getValue());
				break;

			default:
				startActivity(new Intent(this, LoginActivity.class));
				break;
			}
		}
	}
	
	public void saveLoginInfo(){
		Editor editor=AppApplication.sp.edit();
		editor.putString("LoginPlat", platstr);
		editor.putString("LoginName", namestr);
		editor.putString("LoginPass", passstr);
		editor.commit();
		editor.clear();
		AppApplication.uploadCid();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

}
