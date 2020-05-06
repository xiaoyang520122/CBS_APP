package com.cninsure.cp.activty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration.Status;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.Case;
import com.cninsure.cp.entity.Case.DataCase;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.utils.ActivityManagerUtil;
import com.cninsure.cp.utils.CallUtils;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.view.LoadingDialog;

public class CaseInfoActivty extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private TextView pusstypeTv, conectMTv, conectPhoneTv, wtNameTv, wtTimeTv,actionTV1,actionTV2,actionTV3;
	/**列表信息集合*/
	private List<List<NameValuePair>> caseInfoList;
	private List<String> listValues;
	private ListView listView;
	private LoadingDialog loaddialog;
	private Case caseinfo;
	private SimpleAdapter adapter;
	private RadioGroup radioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_case_info);
		ActivityManagerUtil.getInstance().addToList(this);
		EventBus.getDefault().register(this);
		initView();
	}

	private void initView() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		pusstypeTv = (TextView) findViewById(R.id.CASEINFO_Pusstype);
		conectMTv = (TextView) findViewById(R.id.CASEINFO_conectName);
		conectPhoneTv = (TextView) findViewById(R.id.CASEINFO_conectPhone);
		wtNameTv = (TextView) findViewById(R.id.CASEINFO_WTName);
		wtTimeTv = (TextView) findViewById(R.id.CASEINFO_WTTime);
		listView = (ListView) findViewById(R.id.CASEINFO_ListView);
		radioGroup = (RadioGroup) findViewById(R.id.CASEINFO_btnG);
		listView.setEmptyView(findViewById(R.id.CASEINFO_empty));
		radioGroup.setOnCheckedChangeListener(this);
		loaddialog = new LoadingDialog(this);
		setAction();
		loadCaseInfo();
	}

	private void setAction() {
		actionTV2.setText("案件详情");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV3.setText("");
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CaseInfoActivty.this.finish();
			}
		});
//		actionTV3.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				Intent intent=new Intent(CaseInfoActivty.this, WorkOrderActivty.class);
//				intent.putExtra("orderUid", getIntent().getStringExtra("orderUid"));
//				intent.putExtra("taskType", getIntent().getStringExtra("taskType"));
//				CaseInfoActivty.this.startActivity(intent);
//				CaseInfoActivty.this.finish();
//			}
//		});
	}

	private void loadCaseInfo() {
		List<String> params = new ArrayList<String>(2);
		params.add("userId");
		params.add(AppApplication.getUSER().data.userId);
		params.add("uid");
		params.add(getIntent().getStringExtra("caseBaoanUid"));
		HttpUtils.requestGet(URLs.GetOrderInfo(), params, HttpRequestTool.GET_ORDER_INFO);
		loaddialog.setMessage("数据加载中……").show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventCaseInfo(List<NameValuePair> values) {
		int rcode = Integer.valueOf(values.get(0).getName());
		if (rcode == HttpRequestTool.GET_ORDER_INFO) {
			loaddialog.dismiss();
		}
		switch (CheckHttpResult.checkList(values, this,HttpRequestTool.GET_ORDER_INFO)) {
		case HttpRequestTool.GET_ORDER_INFO:
			caseinfo = JSON.parseObject(values.get(0).getValue(), Case.class);
			getCaseInfoFormObj();
			break;

		default:
			break;
		}
	}

	private void getCaseInfoFormObj() {
		if (caseinfo == null || caseinfo.data == null) {
			this.finish();
		} else {
			DataCase data = caseinfo.data;
			pusstypeTv.setText(data.bussTypeName);
			conectMTv.setText(data.contactsName);
			conectPhoneTv.setText(data.contactsPhone);
			wtNameTv.setText(data.entrusterName);
			wtTimeTv.setText(data.entrusterDate);
			conectPhoneTv.setOnClickListener(this);
			getListInfo(data);
		}
	}

	private void getListInfo(DataCase data) {
		caseInfoList=new ArrayList<List<NameValuePair>>(3);
		
		/**案件基本信息*/
		List<NameValuePair> baseList=new ArrayList<NameValuePair>();
		baseList.add(new BasicNameValuePair("险种", caseinfo.data.caseTypeName));
		baseList.add(new BasicNameValuePair("公估产品", caseinfo.data.bussTypeName));
		baseList.add(new BasicNameValuePair("归属机构", caseinfo.data.organizationName));
//		baseList.add(new BasicNameValuePair("作业机构", caseinfo.data.));
		baseList.add(new BasicNameValuePair("报案号", caseinfo.data.baoanNo));
		baseList.add(new BasicNameValuePair("业务范围",  getserviceType(caseinfo.data.agreementType)));
		baseList.add(new BasicNameValuePair("个案委托人名称", caseinfo.data.entrusterNamePersonage));
		baseList.add(new BasicNameValuePair("委托协议", caseinfo.data.agreementName));
		baseList.add(new BasicNameValuePair("报案时间", caseinfo.data.baoanDate));
		baseList.add(new BasicNameValuePair("是否二次委托", caseinfo.data.isRepeatEntrust==1?"是":"否"));
		baseList.add(new BasicNameValuePair("保险公司坐席号", caseinfo.data.entrusterPhone));
		caseInfoList.add(baseList);
		/**案件出险信息 */
		List<NameValuePair> cxList=new ArrayList<NameValuePair>();
		cxList.add(new BasicNameValuePair("出险地点", caseinfo.data.caseLocation));
		cxList.add(new BasicNameValuePair("被保险人", caseinfo.data.insuredPerson));
		cxList.add(new BasicNameValuePair("标的车牌", caseinfo.data.licensePlateBiaoDi));
		cxList.add(new BasicNameValuePair("三者车牌", caseinfo.data.licensePlateSanZhe));
		cxList.add(new BasicNameValuePair("交强赔案号", caseinfo.data.saliLossNumber));
		cxList.add(new BasicNameValuePair("交强保单号", caseinfo.data.saliPolicyNumber));
		cxList.add(new BasicNameValuePair("出险时间", caseinfo.data.caseDate));
		cxList.add(new BasicNameValuePair("是否现场", caseinfo.data.isScene==1?"是":"否"));
		cxList.add(new BasicNameValuePair("商业赔案号", caseinfo.data.businessLossNumber));
		cxList.add(new BasicNameValuePair("商业保单号", caseinfo.data.businessPolicyNumber));
		cxList.add(new BasicNameValuePair("承保地点", caseinfo.data.insuredLocation));
		cxList.add(new BasicNameValuePair("出险经过", caseinfo.data.caseLifecycle));
		caseInfoList.add(cxList);
		
		/**结算信息 */
		List<NameValuePair> jsList=new ArrayList<NameValuePair>();
//		jsList.add(new BasicNameValuePair("标准公估费", caseinfo.data.standardGgFee));
		jsList.add(new BasicNameValuePair("结案时间", caseinfo.data.caseFinishTime));
		caseInfoList.add(jsList);
		
		displayCaseInfo(0);
	}
	
	private void displayCaseInfo(int position){
		List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < caseInfoList.get(position).size(); i++) {
			Map<String, Object> listem = new HashMap<String, Object>();
			listem.put("title", caseInfoList.get(position).get(i).getName());
			listem.put("value", caseInfoList.get(position).get(i).getValue());
			listems.add(listem);
		}
		adapter = new SimpleAdapter(this, listems, R.layout.caseinfo_listview_item, new String[] { "title", "value" }, new int[] { R.id.CASEINFOitem_title, R.id.CASEINFOitem_value });
//		setHeight();
		listView.setAdapter(adapter);
	}

	public String getserviceType(int i) {
		switch (i) {
		case 1:
			return "车险业务";
		case 2:
			return "非车财险业务";
		case 3:
			return "非车水险业务";
		default:
			break;
		}
		return "无";
	}
	
//	 public void setHeight(){ 
//		 int height = 0; 
//		 int count = adapter.getCount(); 
//		 for(int i=0;i<count;i++){ 
//			 View temp = adapter.getView(i,null,listView);
//			 temp.measure(0,0); 
//			 height += temp.getMeasuredHeight();
//			 } 
//		 LayoutParams params = this.listView.getLayoutParams(); 
//		 params.width = LayoutParams.FILL_PARENT;
//		 params.height = height; 
//		 listView.setLayoutParams(params);
//		 }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.CASEINFO_conectPhone:
			CallUtils.call(CaseInfoActivty.this, caseinfo.data.contactsPhone);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		// TODO Auto-generated method stub
		listView.removeHeaderView(textView);
		switch (id) {
		case R.id.CASEINFO_btn_0:
			displayCaseInfo(0);
			break;
		case R.id.CASEINFO_btn_1:
			displayCaseInfo(1);
			break;
		case R.id.CASEINFO_btn_2:
			displayCaseInfo(2);
			break;
		case R.id.CASEINFO_btn_3:
			listView.addHeaderView(getHeadTv());
			listView.setAdapter(new JDAdapter());
			listView.smoothScrollToPosition(status);
			break;

		default:
			break;
		}
	}
	private TextView textView;
	private TextView getHeadTv(){
		textView=new TextView(this);
		textView.setText("订单："+getIntent().getStringExtra("orderUid")+" 的进度情况：\n");
		return textView;
	}
	
	/**案件状态，也是listView需要高亮和显示的行数*/
	private int status;
	private class JDAdapter extends BaseAdapter{
		
		private List<String> valuelist;
		private LayoutInflater inflater;
		
		public JDAdapter(){
			status=CaseInfoActivty.this.getIntent().getIntExtra("status", 0);
			inflater=LayoutInflater.from(CaseInfoActivty.this);
			valuelist=new ArrayList<String>();
			valuelist.add("订单审核通过");//8
			valuelist.add("订单审核驳回");//7
			valuelist.add("订单提交审核中");//6
			valuelist.add("订单作业完成");//5
			valuelist.add("系统取消订单");//4
			valuelist.add("公估师取消订单");//3
			valuelist.add("公估师已接单");//2
			valuelist.add("已结调度");//1
			valuelist.add("订单调度成功");//0
		}

		@Override
		public int getCount() {
			return valuelist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return valuelist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View conView, ViewGroup arg2) {
			TextView valueTv;
			conView=inflater.inflate(R.layout.caseinfo_listview_jindu_item, null);
			valueTv=(TextView) conView.findViewById(R.id.CASEINFOitemjd_value);
			valueTv.setText(valuelist.get(arg0));
			if (8-arg0==status) {
				ImageView img=(ImageView) conView.findViewById(R.id.CASEINFOitemjd_img);
				img.setImageResource(R.drawable.shijian_bule32);
				valueTv.setTextColor(CaseInfoActivty.this.getResources().getColor(R.color.bule_text_h));
			}
			return conView;
		}
		
	}
}
/**
 * 　方式三：代码中动态设置高度（让ListView高度最大 显示完全所有数据） 复制代码 public void setHeight(){ int
 * height = 0; int count = adapter.getCount(); for(int i=0;i<count;i++){ View
 * temp = adapter.getView(i,null,listview); temp.measure(0,0); height +=
 * temp.getMeasuredHeight(); } LayoutParams params =
 * this.listview.getLayoutParams(); params.width = LayoutParams.FILL_PARENT;
 * params.height = height; listview.setLayoutParams(layoutParams); } 复制代码
 **/
