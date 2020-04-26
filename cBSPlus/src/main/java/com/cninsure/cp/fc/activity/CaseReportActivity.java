package com.cninsure.cp.fc.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.FCOrderEntity;
import com.cninsure.cp.entity.PagedRequest;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.APPRequestModel;
import com.cninsure.cp.entity.fc.CaseBean;
import com.cninsure.cp.entity.fc.CaseManage;
import com.cninsure.cp.utils.CallUtils;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.GetFcStatusUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.view.LoadingDialog;
import com.cninsure.cp.view.MarqueeTextView;

public class CaseReportActivity extends BaseActivity {

//	private TextView actionTV1, actionTV2, actionTV3;
//	private ListView mPullRefreshListView;
//	private LoadingDialog loadDialog; // 遮罩
//	private List<NameValuePair> params;
//	private FCOrderEntity fcCaseorders;
//	private List<PublicOrderEntity> data;
//	/** listView数据为空时显示的提示 */
//	private TextView emptyTv;
//	private MyOrderAdapter adapter;
//	private LayoutInflater inflater;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.case_report_order_activity);
//		EventBus.getDefault().register(this);
//		initaction();
//		initView();
//	}
//
//	private void initView() {
//		loadDialog = new LoadingDialog(this);
//		mPullRefreshListView = (ListView) findViewById(R.id.casereport_pull_refresh_list);
//		emptyTv = (TextView) findViewById(R.id.casereport_emptyText);
//
//	}
//
//	private void initaction() {
//		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
//		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
//		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
//		setAction();
//	}
//
//	private void setAction() {
//		actionTV2.setText("非车接报案列表");
//		actionTV3.setText("新增+");
//		actionTV3.setCompoundDrawables(null, null, null, null);
//		actionTV1.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				CaseReportActivity.this.finish();
//			}
//		});
//		actionTV3.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) { //跳转到飞车接报案录入界面
//				CaseReportActivity.this.startActivity(new Intent(CaseReportActivity.this, CaseInputActivity.class));
//			}
//		});
//	}
//	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		dowloadingFc();
//	}
//
//	/** 自定义的EventBus方法接受 **/
//	@Subscribe(threadMode = ThreadMode.MAIN)
//	public void eventData(List<NameValuePair> value) {
//		int code = Integer.parseInt(value.get(0).getName());
//		if (code == HttpRequestTool.GET_FC_SC_STATUS_LIST) {
//			loadDialog.dismiss();
//		}
//
//		switch (CheckHttpResult.checkList(value, this)) {
//		case HttpRequestTool.GET_FC_SC_STATUS_LIST:
//			getFCorderInfo(value.get(0).getValue());
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	/** 获取非车已提交审核的订单 */
//	private void dowloadingFc() {
//		loadDialog.setMessage("努力加载中……").show();
//		params = new ArrayList<NameValuePair>();
//		@SuppressWarnings("rawtypes")
//		APPRequestModel<PagedRequest> appre = new APPRequestModel<PagedRequest>();
//		appre.userToken = AppApplication.USER.data.targetOid;
//		@SuppressWarnings("rawtypes")
//		PagedRequest<Map> requestData = new PagedRequest<Map>();
//		requestData.pageSize = 500;
//		requestData.pageNo = 1;
//		Map<String, String> map = new HashMap<String, String>(1);
//		map.put("filed3", "1");
//		requestData.data = map;
//		appre.requestData = requestData;
//		params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre)));
//		HttpUtils.requestPost(URLs.GET_FC_STATUS_LIST, params, HttpRequestTool.GET_FC_SC_STATUS_LIST);
//	}
//
//	/** 如果是第一页，可能是刷新列表了，需要清空掉之前分类中的历史数据，然后再添加，否则可能是下拉加载，直接追加进对应的数据中 **/
//	private void getFCorderInfo(String value) {
//		fcCaseorders = JSON.parseObject(value, FCOrderEntity.class);
//		List<PublicOrderEntity> orderArr = fcCaseorders.data.list;
//		data = new ArrayList<PublicOrderEntity>();
//		for (int i = 0; i < orderArr.size(); i++) {
//			data.add(orderArr.get(i));
//		}
//		getLocalSave();
//		showDataList();
//	}
//
//	/**获取本地暂存接报案信息**/
//	private void getLocalSave() {
//		int requestCode=getIntent().getIntExtra("caseInputRequestCode", -1);
//		if (requestCode==-1) { //编辑类型的请求
//			
//		}else if(requestCode==1) { //暂存回显
//			String tempCase=AppApplication.sp.getString("caseInputRequestCodeLocal", "");
//			if (!TextUtils.isEmpty(tempCase)) {
//				APPRequestModel<CaseBean>  appRequestData=JSON.parseObject(tempCase, APPRequestModel.class);
//				if (appRequestData!=null && appRequestData.requestData.casem!=null) {
//					CaseManage casem=appRequestData.requestData.casem;
//					PublicOrderEntity pentity=new PublicOrderEntity();
//					pentity.caseTypeAPP="FC";
//					pentity.createDate="未保存！";
//					pentity.riskType = casem.riskType;
//					pentity.feicheBaoxianType=casem.feicheBaoxianType;
//					pentity.insurerCaseNo=casem.insurerCaseNo;
//					pentity.caseName=casem.caseName;
//					pentity.status=casem.status;
//					pentity.deputePer=casem.deputePer;
//					pentity.cxUintLink=casem.cxUintLink;
//					pentity.lxPhone=casem.lxPhone;
//					pentity.caseLocation=casem.dangerAdd;
//					data.add(pentity);
//				}
//			}
//		}
//	}
//
//	private void showDataList() {
//		if (adapter == null) {
//			adapter = new MyOrderAdapter();
//			mPullRefreshListView.setAdapter(adapter);
//		} else {
//			adapter.notifyDataSetChanged();
//		}
//		sheEmptyView();
//	}
//
//	private void sheEmptyView() {
//		if (data.size() == 0) {
//			emptyTv.setVisibility(View.VISIBLE);
//		} else {
//			emptyTv.setVisibility(View.GONE);
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		EventBus.getDefault().unregister(this);
//	}
//
//	public class MyOrderAdapter extends BaseAdapter {
//
//		public MyOrderAdapter() {
//			inflater = LayoutInflater.from(CaseReportActivity.this);
//		}
//
//		@Override
//		public int getCount() {
//			return data.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			return data.get(arg0);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return arg0;
//		}
//
//		@Override
//		public View getView(int item, View conview, ViewGroup arg2) {
//
//			final ViewHoder vh;
//			// if (conview == null) {
//			conview = inflater.inflate(R.layout.ordernow_list_item, null);
//			vh = new ViewHoder();
//			vh.time = (TextView) conview.findViewById(R.id.ONLI_time);
//			vh.casetype = (TextView) conview.findViewById(R.id.ONLI_CaseType);
//			vh.baoanNo = (TextView) conview.findViewById(R.id.ONLI_baoanno);
//			vh.caseName = (TextView) conview.findViewById(R.id.ONLI_mcORcNoTv);
//			vh.status = (TextView) conview.findViewById(R.id.ONLI_status);
//			vh.wtren = (MarqueeTextView) conview.findViewById(R.id.ONLI_wtren);
//			vh.bussType = (TextView) conview.findViewById(R.id.ONLI_bussType);
//			vh.NameOrCar = (TextView) conview.findViewById(R.id.ONLI_mcORcNo);
//			vh.cxUintLink = (TextView) conview.findViewById(R.id.ONLI_cxUintLink);
//			vh.lxPhone = (TextView) conview.findViewById(R.id.ONLI_lxPhone);
//			vh.callPhoneTv = (TextView) conview.findViewById(R.id.ONLI_lxPhone_call);
//			vh.copyTv = (TextView) conview.findViewById(R.id.ONLI_copy_baoan_no);
//			vh.WTinfoTv = (TextView) conview.findViewById(R.id.ONLI_WT_info);
//			vh.timeOutTv = (TextView) conview.findViewById(R.id.ONLI_timeOut);
//			vh.rejectTv = (TextView) conview.findViewById(R.id.ONLI_bohuiInfo);
//			vh.addressTv = (TextView) conview.findViewById(R.id.ONLI_address);
//			vh.naviTv = (TextView) conview.findViewById(R.id.ONLI_naviGation);
//			vh.firstButTv = (TextView) conview.findViewById(R.id.ONLI_cancel_order);
//			vh.secendButTv = (TextView) conview.findViewById(R.id.ONLI_accept_order);
//			conview.setTag(vh);
//			vh.firstButTv.setText("删除");
//			vh.secendButTv.setText("编辑");
////			setOnButtonTvOnclick(data.get(item), item, vh.firstButTv, vh.secendButTv);
//			// } else {
//			// vh = (ViewHoder) conview.getTag();
//			// }
//			final PublicOrderEntity idata = data.get(item);
//			if ("FC".equals(idata.caseTypeAPP)) {
//				vh.NameOrCar.setText("案件名称");
//				vh.time.setText(idata.createDate);
//				vh.casetype.setText((idata.riskType == 1 ? "财险" : "水险"));
//				vh.bussType.setText(idata.feicheBaoxianType);
//				vh.baoanNo.setText(idata.insurerCaseNo);
//				vh.caseName.setText(idata.caseName);
//				vh.status.setText(GetFcStatusUtil.getstatus(idata.status));
//				vh.wtren.setText(idata.deputePer);
//				vh.cxUintLink.setText(idata.cxUintLink);
//				vh.lxPhone.setText(idata.lxPhone);
//				vh.naviTv.setVisibility(View.GONE);
//				vh.addressTv.setText(idata.caseLocation);
//				if (TextUtils.isEmpty(idata.lxPhone)) {
//					vh.callPhoneTv.setVisibility(View.INVISIBLE);
//				} else {
//					vh.callPhoneTv.setVisibility(View.VISIBLE);
//				}
//				vh.callPhoneTv.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						CallUtils.call(CaseReportActivity.this, idata.lxPhone);
//					}
//				});
//				vh.WTinfoTv.setVisibility(View.GONE);
//				vh.timeOutTv.setVisibility(View.GONE);
//				vh.rejectTv.setVisibility(View.GONE);
//
//				/** 公共部分 **/
//				vh.copyTv.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						CopyUtils.copy(CaseReportActivity.this, vh.baoanNo.getText().toString());
//					}
//				});
//			}
//			setOnclikToEdit(vh.secendButTv,idata);
//			return conview;
//		}
//		
//		public class ViewHoder {
//			TextView time, casetype,bussType, baoanNo, caseName, status,
//			cxUintLink, lxPhone,NameOrCar,callPhoneTv,copyTv,WTinfoTv,timeOutTv;
//			MarqueeTextView wtren;
//			/**驳回信息*/
//			TextView rejectTv,addressTv,naviTv;
//			/**底部操作按钮*/
//			TextView firstButTv,secendButTv;
//		}
//	}
//	
//	/**跳转到编辑界面**/
//	private void setOnclikToEdit(TextView secendButTv, final PublicOrderEntity idata) {
//		secendButTv.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) { 
//				Intent intent;
//				if (TextUtils.isEmpty(idata.feicheBaoxianType) && idata.feicheBaoxianType.equals("1")) {
//					intent=new Intent(CaseReportActivity.this, CaseInputActivity.class);
//				}else if(TextUtils.isEmpty(idata.feicheBaoxianType) && idata.feicheBaoxianType.equals("2")) {
//					intent=new Intent(CaseReportActivity.this, WaterCaseInputActivity.class);
//				}else {
//					intent=new Intent(CaseReportActivity.this, CaseInputActivity.class);
//					intent.putExtra("caseInputRequestCode", 1);
//				}
//				if (idata.caseNo!=null) {
//					intent.putExtra("CaseNo", idata.caseNo);
//				}
//				CaseReportActivity.this.startActivity(intent);
//			}
//		});
//	}

}
