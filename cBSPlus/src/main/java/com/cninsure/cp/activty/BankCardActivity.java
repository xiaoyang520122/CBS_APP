package com.cninsure.cp.activty;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.Dialog;
import android.content.DialogInterface;
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
import com.cninsure.cp.entity.extract.ExtUserEtity;
import com.cninsure.cp.utils.BankLogoManage;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.view.LoadingDialog;

public class BankCardActivity extends BaseActivity {
	/**公估师账户信息**/
	private ExtUserEtity extUserEtity;
//	private UserInfo userInfo;
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
		banckNameTv =  findViewById(R.id.mybackcard_Cardname);
		banckPartNameTv =  findViewById(R.id.mybackcard_CardPartName); 
		banckNumberTv  =  findViewById(R.id.mybackcard_CardNumber);
		carkUserNameTv =  findViewById(R.id.mybackcard_CardUserName);
		addOrChangeTv =  findViewById(R.id.mybackcard_addCardOrChange);
		banckLogoImg =  findViewById(R.id.mybackcard_Cardlogo);
		cancelTv =  findViewById(R.id.mybackcard_cancleTv);
		
		cancelTv.setOnClickListener(arg0 -> BankCardActivity.this.finish());
	}
	
	/**得到数据和设置事件**/
	private void getdata() {
		if (TextUtils.isEmpty(extUserEtity.data.bankName)) {//银行名称为空
			toAddCardActivity();
		}else {
			
		}
	}

	/**
	 * 获取公估师信息
	 */
	private void downLoadUserInfo() {
		List<String> httpParams = new ArrayList<>();
		httpParams.add("userId");
		httpParams.add(AppApplication.USER.data.userId);
		HttpUtils.requestGet(URLs.CX_EXT_USER, httpParams, HttpRequestTool.CX_EXT_USER);
		LoadDialogUtil.setMessageAndShow(this,"努力加载中……");
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventFun(List<NameValuePair> values) {
		int responsecode = Integer.parseInt(values.get(0).getName());
		switch (responsecode) {
			case HttpRequestTool.CX_EXT_USER: //
				LoadDialogUtil.dismissDialog();
				analysisExtUserInfo(values.get(0).getValue());
				break;
			default:
				break;
		}
	}
	private void analysisExtUserInfo(String value) {
		extUserEtity = JSON.parseObject(value,ExtUserEtity.class);
		if (extUserEtity!=null && extUserEtity.data!=null ){
			showBankInfo();
			getdata();
		}else{
			Dialog dialog = DialogUtil.getAlertOnelistener(this,"获取信息失败！",null);
			dialog.setOnDismissListener(dialog1 -> {
				BankCardActivity.this.finish();
			});
			dialog.show();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		downLoadUserInfo();
	}
	
	protected void showBankInfo() {
		if (!TextUtils.isEmpty(extUserEtity.data.bankNo)) {
			banckNameTv .setText(extUserEtity.data.bankName);
			banckPartNameTv .setText(extUserEtity.data.bankBranchName);
			String bnumber=extUserEtity.data.bankNo;
			String bn= "**** **** **** "+bnumber.substring(bnumber.length()-4);
			banckNumberTv  .setText(bn);
			carkUserNameTv .setText(extUserEtity.data.name);
			
			addOrChangeTv .setText("修改");
			toAddCardActivity();
			
			List<NameValuePair> bankLitleLogos=BankLogoManage.getbanklitleLogo();
			for (int i = 0; i < bankLitleLogos.size(); i++) {
				if (!TextUtils.isEmpty(extUserEtity.data.bankName)
						&& extUserEtity.data.bankName.equals(bankLitleLogos.get(i).getValue())) {
					int resid=Integer.valueOf(bankLitleLogos.get(i).getName());
					banckLogoImg.setImageResource(resid);
					return;
				}
			}
		}
	}
	
	/**跳转到添加银行卡的界面**/
	private void toAddCardActivity(){
		addOrChangeTv.setOnClickListener(arg0 -> {
			Intent intent =new Intent(BankCardActivity.this, AddBanckCardActivity.class);
			intent.putExtra("extUserEtity", extUserEtity);
			BankCardActivity.this.startActivity(intent);
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
