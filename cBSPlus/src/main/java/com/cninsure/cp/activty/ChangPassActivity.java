package com.cninsure.cp.activty;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.LoginActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.utils.ActivityManagerUtil;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.view.LoadingDialog;

public class ChangPassActivity extends BaseActivity implements OnClickListener {

	private TextView actionTV1, actionTV2, actionTV3;
	private EditText oldpassTv, newPassTv1, newPassTv2;
	private LoadingDialog loadingDialog;
	private ImageView cImageView;
	private boolean isChangPass=true;
	private LinearLayout linePass,linePhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.activity_changpass);
		ActivityManagerUtil.getInstance().addToList(this);
		initactionView();
	}

	private void initactionView() {
		loadingDialog=new LoadingDialog(this);
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();
		initView();
	}

	private void initView() {
		oldpassTv = (EditText) findViewById(R.id.changpass_yspass);
		newPassTv1 = (EditText) findViewById(R.id.changpass_newpass1);
		newPassTv2 = (EditText) findViewById(R.id.changpass_newpass2);
		cImageView= (ImageView) findViewById(R.id.changpass_changImage);
		linePass = (LinearLayout) findViewById(R.id.changpass_changpass_Linea);
		linePhone= (LinearLayout) findViewById(R.id.changpass_changphone_Linea);
		changDisplayViews();
		findViewById(R.id.changpass_submit_button).setOnClickListener(this);
	}

	private void changDisplayViews() {
		isChangPass=getIntent().getBooleanExtra("isChangPass", true);
		if (isChangPass) {
			cImageView.setImageResource(R.drawable.chang_pass_white200);
			linePass.setVisibility(View.VISIBLE);
			linePhone.setVisibility(View.GONE);
		}else {
			cImageView.setImageResource(R.drawable.chang_phone_white190);
			linePass.setVisibility(View.GONE);
			linePhone.setVisibility(View.VISIBLE);
		}
	}

	private void setAction() {
		actionTV2.setText("修改登录密码");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV3.setText("返回");
		actionTV1.setOnClickListener(this);
		actionTV3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ACTION_V_LTV:
			ChangPassActivity.this.finish();
			break;
		case R.id.ACTION_V_RTV:
			ChangPassActivity.this.finish();
			break;
		case R.id.changpass_submit_button:// 提交修改
			isChangPass();
			break;

		default:
			break;
		}
	}
	
	private void isChangPass(){
		if (isChangPass) {
			getpassinfo();
		}else {
			changPhone();
		}
		
	}
	
	private void changPhone(){
		String phonenum = ((EditText)findViewById(R.id.changpass_newphone)).getText().toString();
		String password =  ((EditText)findViewById(R.id.changpass_changPhone_pass)).getText().toString();
		if (TextUtils.isEmpty(phonenum) || TextUtils.isEmpty(password)) {
			DialogUtil.getErrDialog(this, "手机号或密码不能为空！").show();
		}else {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userId", AppApplication.USER.data.userId));
			params.add(new BasicNameValuePair("type", "2"));
			params.add(new BasicNameValuePair("account", AppApplication.USER.data.loginName));
			params.add(new BasicNameValuePair("mobile", phonenum));
			params.add(new BasicNameValuePair("oldPassword", password));
			HttpUtils.requestPost(URLs.ChangPhone(), params, HttpRequestTool.CHANG_PASS);
			loadingDialog.setMessage("修改中……").show();
		}
	}

	private void getpassinfo() {
		String oldpassstr = oldpassTv.getText().toString();
		String newpassstr1 = newPassTv1.getText().toString();
		String newpassstr2 = newPassTv2.getText().toString();
		if (oldpassstr.isEmpty() || newpassstr1.isEmpty() || newpassstr2.isEmpty()) {
			DialogUtil.getErrDialog(this, "不能留空，请录入全部信息后提交！").show();
			return;
		}
		if (!newpassstr1.equals(newpassstr2)) {
			DialogUtil.getErrDialog(this, "两次录入的新密码不同，请重新录入！").show();
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", AppApplication.USER.data.userId));
//		params.add(new BasicNameValuePair("type", "1"));
//		params.add(new BasicNameValuePair("account", AppApplication.USER.data.loginName));
//		params.add(new BasicNameValuePair("mobile", ""));
		params.add(new BasicNameValuePair("oldPassword", oldpassstr));
		params.add(new BasicNameValuePair("newPassword", newpassstr1));
		HttpUtils.requestPost(URLs.ChangPass(), params, HttpRequestTool.CHANG_PASS);
		loadingDialog.setMessage("修改中……").show();
	}

	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventdata(List<NameValuePair> values){
		int reqCode=Integer.parseInt(values.get(0).getName());
		if (reqCode==HttpRequestTool.CHANG_PASS) {
			loadingDialog.dismiss();
		}
		switch (CheckHttpResult.checkList(values, this)) {
		case HttpRequestTool.CHANG_PASS:
			DialogUtil.getAlertOneButton(this, values.get(0).getValue(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					startActivity(new Intent(ChangPassActivity.this, LoginActivity.class));
					ChangPassActivity.this.finish();
				}
			}).show();
			break;

		default:
			break;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

}
