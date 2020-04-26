package com.cninsure.cp.activty;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.utils.ActivityManagerUtil;
import com.cninsure.cp.utils.CallUtils;
import com.cninsure.cp.utils.ToastUtil;

public class HelpCenterActivity extends BaseActivity implements OnClickListener {

	private TextView  actionTV1,actionTV2,actionTV3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		ActivityManagerUtil.getInstance().addToList(this);
		setContentView(R.layout.activity_helpcenter);
		initactionView();
	}

	private void initactionView() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction() ;
		initView();
	}

	private void initView() {
		findViewById(R.id.HELPCENTER_call).setOnClickListener(this);
		findViewById(R.id.HELPCENTER_msg).setOnClickListener(this);
	}

	private void setAction() {
		actionTV2.setText("帮助中心");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV3.setText("返回");
		
		actionTV1.setOnClickListener(this);
		actionTV3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.HELPCENTER_call:
			CallUtils.call(this, "0755-23963333");
			break;
		case R.id.HELPCENTER_msg:
			ToastUtil.showToastLong(this, "功能接口开发中……");
			break;
		case R.id.ACTION_V_LTV:
			this.finish();
			break;
		case R.id.ACTION_V_RTV:
			this.finish();
			break;

		default:
			break;
		}
		
	}
	
	
}
