package com.cninsure.cp.activty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.utils.ActivityManagerUtil;
import com.cninsure.cp.utils.CallUtils;

public class AboutUsAvtivity extends BaseActivity {

	private TextView actionTV1, actionTV2, actionTV3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		ActivityManagerUtil.getInstance().addToList(this);
		initView();
	}

	private void initView() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();

		findViewById(R.id.aboutus_link).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Uri uri = Uri.parse("http://www.cnsurvey.cn/");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
			}
		});
		findViewById(R.id.aboutus_phonenumber).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CallUtils.call(AboutUsAvtivity.this, "075523963333");
			}
		});
	}

	private void setAction() {
		actionTV2.setText("关于我们");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV3.setText("返回");
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AboutUsAvtivity.this.finish();
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AboutUsAvtivity.this.finish();
			}
		});
	}

}
