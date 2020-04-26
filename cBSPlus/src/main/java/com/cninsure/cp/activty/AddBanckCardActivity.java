package com.cninsure.cp.activty;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.UserInfo;
import com.cninsure.cp.utils.BankLogoManage;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.GridViewForScrollView;
import com.cninsure.cp.view.LoadingDialog;

public class AddBanckCardActivity extends BaseActivity {
	
	private UserInfo userInfo;
	private GridViewForScrollView banckGridView;//选择银行列表
	private TextView cardUserTv,cancelTv,submitTv;//持卡人名称，取消，确定提交
	private EditText cardNumberEdit,banckPartNameEdit;//银行卡卡号，支行名称
	private MyBankAdapter bankAdapter;
	private int choiceBank=-1;//选中的银行在GridView中的位置
	private List<NameValuePair> logoList;//银行logo集合
	private String name,bankNumber,partName,bankName;
	private LoadingDialog loadDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_backcard_view_layout);
		EventBus.getDefault().register(this);
		initView();
	}

	private void initView() {
		banckGridView=(GridViewForScrollView) findViewById(R.id.addBC_GridView);
		cardUserTv=(TextView) findViewById(R.id.addBC_CardUserName);
		cancelTv=(TextView) findViewById(R.id.addBC_cancel);
		submitTv=(TextView) findViewById(R.id.addBC_submit);
		cardNumberEdit=(EditText) findViewById(R.id.addBC_CardNumber);
		banckPartNameEdit=(EditText) findViewById(R.id.addBC_CardpartName);
		userInfo=(UserInfo) getIntent().getSerializableExtra("UserInfo");
		isIdCardEmpty();
		loadDialog = new LoadingDialog(AddBanckCardActivity.this);
		setValue();
		bankAdapter=new MyBankAdapter();
		banckGridView.setAdapter(bankAdapter);
		setAction();
	}
	
	private void setValue() {
		logoList=BankLogoManage.getbankLogo();
		cardUserTv.setText(userInfo.data.name);
		if (!TextUtils.isEmpty(userInfo.data.payeeUserBankNumber)) {
			cardNumberEdit.setText(userInfo.data.payeeUserBankNumber);
		}
		if (!TextUtils.isEmpty(userInfo.data.payeeUserBankBranch)) {
			banckPartNameEdit.setText(userInfo.data.payeeUserBankBranch);
		}
		if (!TextUtils.isEmpty(userInfo.data.payeeUserBankName)) {
			for (int i = 0; i < logoList.size(); i++) {
				if (logoList.get(i).getValue().equals(userInfo.data.payeeUserBankName)) {
					choiceBank=i;
					return;
				}
			}
		}
	}

	private void isIdCardEmpty() {
		if (TextUtils.isEmpty(userInfo.data.idCard)) {
			Dialog dialog=DialogUtil.getAlertOneButton(AddBanckCardActivity.this, "请后台补全身份证号码信息后再添加银行卡信息！",null);
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					AddBanckCardActivity.this.finish();
				}
			});
			dialog.show();
		} 
		
	}

	/**设置标题栏点击事件*/
	private void setAction() {
		/**确认提交*/
		submitTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getvalue();
			}
		});
		
		cancelTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AddBanckCardActivity.this.finish();
			}
		});
	}
	
	/**获取控件上的数据**/
	private void getvalue() {
		name = cardUserTv.getText().toString();
		bankNumber = cardNumberEdit.getText().toString();
		partName = banckPartNameEdit.getText().toString();
		if (choiceBank!=-1) {
			bankName = logoList.get(choiceBank).getValue();
		}
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(bankNumber) 
				|| TextUtils.isEmpty(partName) || TextUtils.isEmpty(bankName) ) {
			DialogUtil.getAlertOneButton(AddBanckCardActivity.this, "请填写和选择全部信息后提交！", null).show();
		}else {
			String paramString = "请再次确定银行卡信息!\n银行名称："+bankName
					+"\n银行卡号："+bankNumber;
			DialogUtil.getAlertOnelistener(AddBanckCardActivity.this, paramString, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					List<NameValuePair> NVparames = new ArrayList<NameValuePair>(1);
					NVparames.add(new BasicNameValuePair("userId", AppApplication.USER.data.userId));
					NVparames.add(new BasicNameValuePair("payeeUserName", name));
					NVparames.add(new BasicNameValuePair("payeeUserIdcard", userInfo.data.idCard));
					NVparames.add(new BasicNameValuePair("payeeUserBankName", bankName));
					NVparames.add(new BasicNameValuePair("payeeUserBankBranch", partName));
					NVparames.add(new BasicNameValuePair("payeeUserBankNumber", bankNumber));
					HttpUtils.requestPost(URLs.SAVE_BANK_CARD,NVparames, HttpRequestTool.SAVE_BANK_INFO);
					loadDialog.setMessage("数据请求中……").show();
				}
			}).show();
		}
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventdata(List<NameValuePair> value) {
		int typecode = Integer.parseInt(value.get(0).getName());
		if (typecode == HttpRequestTool.SAVE_BANK_INFO ) {
			loadDialog.dismiss();
		}
		switch (CheckHttpResult.checkList(value, AddBanckCardActivity.this)) {
		case HttpRequestTool.SAVE_BANK_INFO:
			ToastUtil.showToastLong(AddBanckCardActivity.this, "操作成功！");
			AddBanckCardActivity.this.finish();
			
			userInfo.data.payeeUserBankBranch=partName;//":null,
			/** 银行名称 */
			userInfo.data.payeeUserBankName=bankName;//":null,
			/** 银行卡号 */
			userInfo.data.payeeUserBankNumber=bankNumber;//":null,
			/** 持卡人身份证号 */
			userInfo.data.payeeUserIdcard=userInfo.data.idCard;//":null,
			/** 持卡人姓名 */
			userInfo.data.payeeUserName=name;//"
			break;

		default:
			break;
		}
		
	}

	private class MyBankAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return logoList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return logoList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int itemPoint, View conView, ViewGroup arg2) {
			conView=LayoutInflater.from(AddBanckCardActivity.this).inflate(R.layout.add_bank_img_item, null);
			ImageView logoImg=(ImageView) conView.findViewById(R.id.addBII_logo);
			ImageView choiceImg=(ImageView) conView.findViewById(R.id.addBII_choiceTage);
			int res=Integer.valueOf(logoList.get(itemPoint).getName());
			logoImg.setImageResource(res);
			if (choiceBank==itemPoint) {
				choiceImg.setVisibility(View.VISIBLE);
			}else {
				choiceImg.setVisibility(View.GONE);
			}
			conView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					choiceBank = itemPoint;
					bankAdapter.notifyDataSetChanged();
				}
			});
			return conView;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
