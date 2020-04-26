package com.cninsure.cp.activty;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.utils.ActivityManagerUtil;


public class SecurityCenterActivity extends BaseActivity implements OnClickListener {

	private TextView  actionTV1,actionTV2,actionTV3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_center);
		ActivityManagerUtil.getInstance().addToList(this);
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
		findViewById(R.id.securitycenter_cpass).setOnClickListener(this);
		findViewById(R.id.securitycenter_cphone).setOnClickListener(this);
	}

	private void setAction() {
		actionTV2.setText("安全中心");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV3.setText("返回");
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SecurityCenterActivity.this.finish();
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SecurityCenterActivity.this.finish();
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent=new Intent(this, ChangPassActivity.class);
		switch (v.getId()) {
		case R.id.securitycenter_cpass:
			intent.putExtra("isChangPass", true);
			break;
			
		case R.id.securitycenter_cphone:
			intent.putExtra("isChangPass", false);
			break;

		default:
			break;
		}
		startActivity(intent);
	}
	
}
