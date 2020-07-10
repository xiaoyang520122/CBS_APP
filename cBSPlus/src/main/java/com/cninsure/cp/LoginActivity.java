package com.cninsure.cp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.dispersive.DispersiveUserActivity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.permission_util.PermissionApplicationUtil;
import com.cninsure.cp.utils.permission_util.PermissionsUtilX;
import com.cninsure.cp.view.LoadingDialog;
import com.igexin.sdk.PushManager;

public class LoginActivity extends BaseActivity implements OnClickListener, PermissionsUtilX.IPermissionsCallback {

	private EditText plat, name, pass;
	private Button submitBt;
	private List<NameValuePair> paramlist;
	private String namestr,platstr,passstr;
	private ImageView bgImg;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		// com.getui.demo.DemoPushService 为第三方自定义推送服务
//		PushManager.getInstance().initialize(this.getApplicationContext(), com.cninsure.cp.service.CbsDemoService.class);
		// com.getui.demo.DemoIntentService 为第三方自定义的推送服务事件接收类
//		PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), com.cninsure.cp.service.CbsIntentService.class);
		EventBus.getDefault().register(this);
		initView();
		new PermissionApplicationUtil(this); //申请读写权限和拍照权限
//		getPermission();
	}

	private void initView() {
		dialog=new LoadingDialog(this);
		bgImg=(ImageView) findViewById(R.id.login_bgImg);
		Glide.with(this).load(R.drawable.loginmovie).into(bgImg);
		plat = (EditText) findViewById(R.id.login_plat);
		name = (EditText) findViewById(R.id.login_name);
		pass = (EditText) findViewById(R.id.login_pass);
		submitBt = (Button) findViewById(R.id.login_button);
		submitBt.setOnClickListener(this);
		name.setError("请手动输入用户名密码，勿使用系统自带回填功能！",getResources().getDrawable(R.drawable.c));
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventconn(List<NameValuePair> value) {
		int conncode = Integer.parseInt(value.get(1).getValue());
		int conntype = Integer.parseInt(value.get(0).getName());
		if (conntype == HttpRequestTool.LOGIN_BY_PASS) {
			switch (conncode) {
			case 200:
				AppApplication.saveUser(value.get(0).getValue(),passstr);
				saveLoginInfo();
				jumpActivity(value.get(0).getValue());
				break;
			case 400:
				String msg = value.get(0).getValue();
				alertDialog = DialogUtil.getAlertDialog(this,value.get(0).getValue());
				alertDialog.show();
				ToastUtil.showToastLong(this, value.get(0).getValue());
				Log.i("JsonHttpUtils", "B400-LoginActivity请求返回数据为：" + value.get(0).getValue());
				break;

			default:
				break;
			}
		}
		dialog.dismiss();
	}

	private Dialog alertDialog;

	private void jumpActivity(String value) {
		if ("99".equals(AppApplication.getUSER().data.userType)){//外部车童登录，跳转到外部车童界面
			Intent intent=new Intent(this, DispersiveUserActivity.class);
			LoginActivity.this.startActivity(intent);
		}else{  //非外部车童
			Intent intent=new Intent(this, IndexActivity.class);
			intent.putExtra("loginvalue", value);
			LoginActivity.this.startActivity(intent);
		}
	}

	@Override
	public void onClick(View arg0) {
		namestr=name.getText().toString();
		platstr=plat.getText().toString();
		passstr=pass.getText().toString();
		paramlist = new ArrayList<NameValuePair>(6);
		paramlist.add(new BasicNameValuePair("plat", platstr));//tenantPinyinInitials
		paramlist.add(new BasicNameValuePair("account", namestr));//loginName
		paramlist.add(new BasicNameValuePair("password", passstr));
		dialog.setMessage("登录中……").show();
		HttpUtils.requestPost(URLs.LoginByPass(), paramlist, HttpRequestTool.LOGIN_BY_PASS);
		ToastUtil.showToastLong(this, "登录请求已发送！");
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
		if (alertDialog!=null && alertDialog.isShowing())
		alertDialog.dismiss();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.finish();
	}

	private PermissionsUtilX permissionsUtil;
	private void getPermission() {
              permissionsUtil = PermissionsUtilX.with(this)
				                 .requestCode(0)
				                 .isDebug(true)
				                 .permissions(PermissionsUtilX.Permission.Storage.WRITE_EXTERNAL_STORAGE)
				                 .request();

          }

//			 当前Activity需要实现PermissionsUtilX.IPermissionsCallback接口
      @Override
          public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
              //需要调用onRequestPermissionsResult
              permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
              super.onRequestPermissionsResult(requestCode, permissions, grantResults);
          }

			      @Override
          public void onPermissionsGranted(int requestCode, String... permission) {
              //权限获取回调
              Log.e("555", "456" );
          }

			      @Override
          public void onPermissionsDenied(int requestCode, String... permission) {
              //权限被拒绝回调
          }
}
