package com.cninsure.cp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.LoadingDialog;

public class ScannerLoginActivity extends BaseActivity {
	
	LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner_login);
		EventBus.getDefault().register(this);
		dialog=new LoadingDialog(this).setMessage("登录中……");
//		ToastUtil.showToastLong(this, getIntent().getStringExtra("uid"));
	}

	private void login() {
		dialog.show();
		
		List<NameValuePair> params=new ArrayList<NameValuePair>();
		String uid=getIntent().getStringExtra("uid");
		Log.i("JsonHttpUtils", "扫码传递uid：" + uid);
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		params.add(new BasicNameValuePair("status", "3"));
		HttpUtils.requestPost(URLs.SCANNER_LOGIN, params, HttpRequestTool.SCANNER_LOGIN);
//		ToastUtil.showToastLong(this, "登录请求发送成功！");
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventbus(List<NameValuePair> value){
		int code=Integer.parseInt(value.get(1).getValue());
		dialog.dismiss();
		if (code!=200) {
			DialogUtil.getAlertOneButton(this, "登录失败！请求参数："+getIntent().getStringExtra("uid")+"===="+
					value.get(0).getValue(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					ScannerLoginActivity.this.finish();
				}
			}).show();
		}else {
			switch (CheckHttpResult.checkList(value, this)) {
			case HttpRequestTool.SCANNER_LOGIN:
				ToastUtil.showToastLong(this, value.get(0).getValue());
				this.finish();
				break;

			default:
				break;
			}
		}
	}

	public void submitOrPass(View view){
		switch (view.getId()) {
		case R.id.scanner_login_sure_button://确认登录
			login();
			break;
			
		case R.id.scanner_login_pass_text://取消登录
			this.finish();
			break;

		default:
			break;
		}
	}
	
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
