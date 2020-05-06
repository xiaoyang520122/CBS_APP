package com.cninsure.cp.activty;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.UserInfo;
import com.cninsure.cp.utils.BankLogoManage;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.view.LoadingDialog;

public class BankCardActivity extends BaseActivity {

	private UserInfo userInfo;
	private TextView banckNameTv,banckPartNameTv,banckNumberTv,carkUserNameTv,addOrChangeTv,cancelTv;
	private ImageView banckLogoImg;
	private LoadingDialog loadDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_back_card_layout);
		EventBus.getDefault().register(this);
		initView();
	}

	/**初始化组件*/
	private void initView() {
		loadDialog = new LoadingDialog(this);
		banckNameTv = (TextView) findViewById(R.id.mybackcard_Cardname);
		banckPartNameTv = (TextView) findViewById(R.id.mybackcard_CardPartName); 
		banckNumberTv  = (TextView) findViewById(R.id.mybackcard_CardNumber);
		carkUserNameTv = (TextView) findViewById(R.id.mybackcard_CardUserName);
		addOrChangeTv = (TextView) findViewById(R.id.mybackcard_addCardOrChange);
		banckLogoImg = (ImageView) findViewById(R.id.mybackcard_Cardlogo);
		cancelTv = (TextView) findViewById(R.id.mybackcard_cancleTv);
		
		cancelTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				BankCardActivity.this.finish();
			}
		});
	}
	
	/**得到数据和设置事件**/
	private void getdata() {
//		userInfo=(UserInfo) getIntent().getSerializableExtra("UserInfo");
		if (TextUtils.isEmpty(userInfo.data.payeeUserBankName)) {//银行名称为空
			toAddCardActivity();
		}else {
			
		}
	}
	
	private void downLoadUserInfo() {
		List<String> paramsList = new ArrayList<String>();
		paramsList.add("userId");
		paramsList.add(AppApplication.getUSER().data.userId);
		paramsList.add("targetUserId");
		paramsList.add(AppApplication.getUSER().data.userId);
		HttpUtils.requestGet(URLs.GetUserInfo(), paramsList, HttpRequestTool.GET_USER_INFO);
		loadDialog.setMessage("数据加载中……").show();
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventdata(List<NameValuePair> value) {
		int typecode = Integer.parseInt(value.get(0).getName());
		if (typecode == HttpRequestTool.GET_USER_INFO ) {
			loadDialog.dismiss();
		}
		switch (CheckHttpResult.checkList(value, BankCardActivity.this)) {
		case HttpRequestTool.GET_USER_INFO:
			userInfo = JSON.parseObject(value.get(0).getValue(), UserInfo.class);
			showBankInfo();
			getdata();
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		downLoadUserInfo();
	}
	
	protected void showBankInfo() {
		if (!TextUtils.isEmpty(userInfo.data.payeeUserBankNumber)) {
			banckNameTv .setText(userInfo.data.payeeUserBankName);
			banckPartNameTv .setText(userInfo.data.payeeUserBankBranch);
			String bnumber=userInfo.data.payeeUserBankNumber;
			String bn= "**** **** **** "+bnumber.substring(bnumber.length()-4, bnumber.length());
			banckNumberTv  .setText(bn);
			carkUserNameTv .setText(userInfo.data.payeeUserName);
			
			addOrChangeTv .setText("修改");
			toAddCardActivity();
			
			List<NameValuePair> bankLitleLogos=BankLogoManage.getbanklitleLogo();
			for (int i = 0; i < bankLitleLogos.size(); i++) {
				if (!TextUtils.isEmpty(userInfo.data.payeeUserBankName) 
						&& userInfo.data.payeeUserBankName.equals(bankLitleLogos.get(i).getValue())) {
					int resid=Integer.valueOf(bankLitleLogos.get(i).getName());
					banckLogoImg.setImageResource(resid);
					return;
				}
			}
		}
	}
	
	/**跳转到添加银行卡的界面**/
	private void toAddCardActivity(){
		addOrChangeTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent =new Intent(BankCardActivity.this, AddBanckCardActivity.class);
				UserInfo ui=userInfo;
				intent.putExtra("UserInfo", ui);
				BankCardActivity.this.startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
