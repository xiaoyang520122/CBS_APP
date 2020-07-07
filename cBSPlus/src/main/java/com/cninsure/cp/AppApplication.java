package com.cninsure.cp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.cninsure.cp.entity.PersistentCookieStore;
import com.cninsure.cp.entity.PushType;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.User;
import com.cninsure.cp.service.LocationService;
import com.cninsure.cp.service.Music;
import com.cninsure.cp.utils.CrashHandler;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LogcatHelper;
import com.cninsure.cp.utils.UserInfoUtil;
import com.igexin.sdk.IUserLoggerInterface;

public class AppApplication extends Application {

	public static boolean MARKER_TYPE = true;

	private static List<NameValuePair> NVparames;
	public static AppApplication mInstance = null;
	public boolean m_bKeyRight = true;
	public static String CID = "";
	public static User USER = new User();
	public static SharedPreferences sp;
	public static BDLocation LOCATION;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		SDKInitializer.initialize(getApplicationContext());
		sp = getSharedPreferences("CBSPlus_sp", Context.MODE_PRIVATE);
		EventBus.getDefault().register(this);
		LogcatHelper.getInstance(this).start(); //打印日志到本地手机内
//		startService(new Intent(this, LocationService.class));//开启百度定位服务
		getCID();
		startService(new Intent(this, LocationService.class));
		CrashHandler.getInstance().init(this); //全局捕获崩溃异常记录日志保存至本地
		com.igexin.sdk.PushManager.getInstance().initialize(this); //个推进行 SDK 的初始化
	}

	private void getCID() {
		CID = sp.getString("clientID", "nomsg");
		uploadCid();
	}

	public static User getUSER(){
//		if (USER==null || USER.data==null || USER.data.userId==null){
		if (UserInfoUtil.USERIsNull(mInstance)){
//			UserInfoUtil.USERIsNull(mInstance);
//			try {
//				Thread.sleep(6*1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			return USER;
		}else{
			return USER;
		}
	}

	public static void emptyUSER(){
		USER = new User();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		// 整体摧毁的时候调用这个方法
	}

	public static AppApplication getInstance() {
		return mInstance;
	}

	public PersistentCookieStore getPersistentCookieStore() {
		PersistentCookieStore cookieStore = new PersistentCookieStore(mInstance);
		return cookieStore;
	}

	public String getusername() {
		if (sp == null) {
			sp = getSharedPreferences("register_info", Context.MODE_PRIVATE);
		}
		return sp.getString("name", "无信息");
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void eventCID(NameValuePair value) {
		int code = Integer.valueOf(value.getName());
		switch (code) {
		case HttpRequestTool.RECIVE_CID:
			Editor editor = sp.edit();
			editor.putString("clientID", value.getValue());
			editor.commit();
			editor.clear();
			CID = value.getValue();
			uploadCid();
			break;
		default:
			break;
		}
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void eventCID(List<NameValuePair> values) {
		int code = Integer.valueOf(values.get(0).getName());//CheckHttpResult.checkList(values, AppApplication.getInstance());
		switch (code) {
		case HttpRequestTool.UPLOAD_CID:
			// CID上传成功
			break;

		default:
			break;
		}
	}


	/**分散型新订单透传悬浮弹框提示用户*/
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventThing(NameValuePair value){
		String type=value.getName();
		if (type.equals(PushType.FSX_NEW_ORDER)) {
			BaseActivity.disPlayAlertMessage(value.getValue());
		}
	}

	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void eventlocation(BDLocation location) {
		if (location != null) {
			LOCATION = location;
			uploadLocation(location);
		}
	}

	public static void uploadCid() {
		if (CID != null && CID.length() !=0  && USER.data!=null  && USER.data.userId!=null ) {
			NVparames = new ArrayList<NameValuePair>(1);
			NVparames.add(new BasicNameValuePair("clientId", CID));
			NVparames.add(new BasicNameValuePair("userId", USER.data.userId));
			HttpUtils.requestPost(URLs.UpCid(), NVparames, HttpRequestTool.UPLOAD_CID);
		}
	}

	public void uploadLocation(BDLocation location) {
		if (USER.data!=null  && USER.data.userId!=null) {
			NVparames = new ArrayList<NameValuePair>(1);
			NVparames.add(new BasicNameValuePair("locationLongitude", location.getLongitude() + ""));
			NVparames.add(new BasicNameValuePair("locationLatitude", location.getLatitude() + ""));
			NVparames.add(new BasicNameValuePair("userId", USER.data.userId));
			NVparames.add(new BasicNameValuePair("loginName", USER.data.loginName));
			HttpUtils.requestPost(URLs.UpLocation()+"?locationLongitude="+location.getLongitude()+
					"&locationLatitude="+location.getLatitude()+"&loginName="+USER.data.loginName
			,NVparames, HttpRequestTool.UPLOAD_LOCATION);
//			ToastUtil.showToastLong(getApplicationContext(), "已上传gps信息"+"locationLongitude="+location.getLongitude()+
//					"&locationLatitude="+location.getLatitude());
		}
	}

	public static void saveUser(String value,String password) {
		USER=JSON.parseObject(value, User.class);
		CrashHandler.userId=USER.data.id+"";
//		CrashHandler.userName=USER.data.name;
		Editor editor=sp.edit();
		editor.putString("tenantPinyinInitials", USER.data.tenantPinyinInitials);
		editor.putString("loginName", USER.data.loginName);
		editor.putString("password", password);
		editor.putString("CrashUserName", USER.data.name);
		editor.commit();
		editor.clear();
	}

}
