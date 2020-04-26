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

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.CaseOrder;
import com.cninsure.cp.entity.FCOrderEntity;
import com.cninsure.cp.entity.PagedRequest;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.APPRequestModel;
import com.cninsure.cp.utils.CallUtils;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.GetFcStatusUtil;
import com.cninsure.cp.utils.GetOrederStatus;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.view.LoadingDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class CancelAndSubmitActivity extends BaseActivity implements OnCheckedChangeListener {
	

	private TextView actionTV1, actionTV2, actionTV3;
	/**0"已提交审核订单",1"已取消订单"*/
	private int actionType;
	private PullToRefreshListView mPullRefreshListView;
	private List<NameValuePair> params;
	private LoadingDialog loadDialog;
	private List<List<PublicOrderEntity>> data;
	private CaseOrder caseorders;
	private MyOrderAdapter adapter;
	private FCOrderEntity fcCaseorders;
	/**选择的显示类型，0非车，1车险，2货运险，3医健险**/
	private int checkType=0;
	private RadioGroup radgrup;
	/**listView数据为空时显示的提示*/
	private TextView emptyTv;
	private LayoutInflater inflater;
	/**get请求的参数*/
	private List<String> paramsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cancel_submit_order_activity);
		EventBus.getDefault().register(this);
		initaction();
		initView();
		downloadData();
	}
	
	private void initView(){
		data = new ArrayList<List<PublicOrderEntity>>(4);
		for (int i = 0; i < 4; i++) {
			data.add(new ArrayList<PublicOrderEntity>());
		}
		radgrup = (RadioGroup) findViewById(R.id.CANCELSUB_btnG);
		loadDialog=new LoadingDialog(this);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.CANCELSUB_pull_refresh_list);
		emptyTv = (TextView) findViewById(R.id.CANCELSUB_emptyText);
		
		radgrup.setOnCheckedChangeListener(this);
	}
	
	
	private void initaction() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();
	}

	private void setAction() {
		actionType=getIntent().getIntExtra("actionType", 0);
		settitleTv();
		actionTV3.setText("");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CancelAndSubmitActivity.this.finish();
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				workhelp.upload();
			}
		});
	}

/**根据不同的要求显示不同的标题**/
	private void settitleTv() {
		if (actionType == 0) {
			actionTV2.setText("已提交审核订单");
		} else if (actionType == 1) {
			actionTV2.setText("已取消订单");
		} else {
			Dialog dialog = DialogUtil.getAlertOneButton(this, "未知的操作类型，请联系管理员！", null);
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					CancelAndSubmitActivity.this.finish();
				}
			});
			dialog.show();
		}
	}
	/**根据传递的actionType选择下载显示的数据**/
	private void downloadData() {
		loadDialog.setMessage("努力加载中……").show();
//		if (actionType == 0) {
			dowloadingFc(1);
//		} else if (actionType == 1) {
//			
//		}
		
	}
	
	
	private void dowLoadingDataCX(int startPoint) {
		paramsList = new ArrayList<String>(4);
		paramsList.add("userId");
		paramsList.add(AppApplication.USER.data.userId);
		paramsList.add("size");	
		paramsList.add(10 + "");
		paramsList.add("orderStatus");
		if (actionType==0) {
			paramsList.add(",6,"); 
		}else {
			paramsList.add(",2,3,"); 
		}
		paramsList.add("start");
		if (caseorders==null) {
			paramsList.add("0");
		}else {
			paramsList.add(startPoint+"");
		}
		paramsList.add("caseTypeId");
		if (checkType==1) {
			paramsList.add("100");
		}else if(checkType==2) {
			paramsList.add("400");
		}else if(checkType==3){
			paramsList.add("200");
		}
		HttpUtils.requestGet(URLs.GetStatuSelforder(), paramsList, HttpRequestTool.GET_CANCEL_SUBMIT);
		if (adapter==null) {
			loadDialog.setMessage("信息读取中……").show();
		}
	}
	
	/**获取非车已提交审核的订单*/
	private void dowloadingFc(int pagePoint) {
		params = new ArrayList<NameValuePair>();
		@SuppressWarnings("rawtypes")
		APPRequestModel<PagedRequest> appre = new APPRequestModel<PagedRequest>();
		appre.userToken = AppApplication.USER.data.targetOid;
		@SuppressWarnings("rawtypes")
		PagedRequest<Map> requestData = new PagedRequest<Map>();
		requestData.pageNo = pagePoint;
		requestData.pageSize = 10;
		Map<String, String> map=new HashMap<String, String>(1);
		if (actionType==0) {
			map.put("filed3", "5");
		}else {
			map.put("filed3", "-6");
		}
		requestData.data = map;
		appre.requestData = requestData;
		params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre)));
		HttpUtils.requestPost(URLs.GET_FC_STATUS_LIST, params, HttpRequestTool.GET_FC_SC_STATUS_LIST);
	}
	
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventData(List<NameValuePair> value) {
		int code = Integer.parseInt(value.get(0).getName());
		if (code == HttpRequestTool.GET_FC_SC_STATUS_LIST || code == HttpRequestTool.GET_CANCEL_SUBMIT) {
			loadDialog.dismiss();
		}

		switch (CheckHttpResult.checkList(value, this)) {
		case HttpRequestTool.GET_FC_SC_STATUS_LIST:
			getFCorderInfo(value.get(0).getValue());
			break;
		case HttpRequestTool.GET_CANCEL_SUBMIT:
			getCXorderInfo(value.get(0).getValue());
			break;

		default:
			break;
		}
	}
	
	/**如果是第一页，可能是刷新列表了，需要清空掉之前分类中的历史数据，然后再添加，否则可能是下拉加载，直接追加进对应的数据中**/
	private void getCXorderInfo(String value) {
		caseorders = JSON.parseObject(value, CaseOrder.class);
		if (caseorders != null && caseorders.tableData.start==0) {
			data.add(checkType, caseorders.tableData.data) ;
		} else if (caseorders != null && caseorders.tableData.start!=0 && data!=null) {
			data.get(checkType).addAll(caseorders.tableData.data);
		}
		showDataList();
	}

	/**如果是第一页，可能是刷新列表了，需要清空掉之前分类中的历史数据，然后再添加，否则可能是下拉加载，直接追加进对应的数据中**/
	private void getFCorderInfo(String value) {
		fcCaseorders=JSON.parseObject(value, FCOrderEntity.class);
		List<PublicOrderEntity> orderArr=fcCaseorders.data.list;
		if (fcCaseorders.data.pageNo==1) {
			data.get(0).clear();
		}
		for (int i = 0; i < orderArr.size(); i++) {
			data.get(0).add(orderArr.get(i));
		}
		showDataList();
	}
	
	private void showDataList() {
		if (adapter == null) {
			adapter = new MyOrderAdapter();
			mPullRefreshListView.setAdapter(adapter);
			setOnLisner();
		} else {
			adapter.notifyDataSetChanged();
			mPullRefreshListView.onRefreshComplete();
		}
		sheEmptyView();
	}
	
	private void sheEmptyView(){
		if (data.get(checkType).size()==0) {
			emptyTv.setVisibility(View.VISIBLE);
		}else {
			emptyTv.setVisibility(View.GONE);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setOnLisner() {
		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				//重新定义为第一页
            	if (checkType==0) {
					dowloadingFc(1);
				}else {
					dowLoadingDataCX(0);
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				
			}
		});
	}
	
	public class MyOrderAdapter extends BaseAdapter {

		public MyOrderAdapter() {
			inflater = LayoutInflater.from(CancelAndSubmitActivity.this);
		}

		@Override
		public int getCount() {
			return data.get(checkType).size();
		}

		@Override
		public Object getItem(int arg0) {
			return data.get(checkType).get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int item, View conview, ViewGroup arg2) {
			ViewHoder vh;
			if (conview == null) {
				conview = inflater.inflate(R.layout.ordernow_list_item, null);
				vh = new ViewHoder();
				vh.time = (TextView) conview.findViewById(R.id.ONLI_time);
				vh.casetype = (TextView) conview.findViewById(R.id.ONLI_CaseType);
				vh.baoanNo = (TextView) conview.findViewById(R.id.ONLI_baoanno);
				vh.caseName = (TextView) conview.findViewById(R.id.ONLI_mcORcNoTv);
				vh.status = (TextView) conview.findViewById(R.id.ONLI_status);
				vh.wtren = (TextView) conview.findViewById(R.id.ONLI_wtren);
				vh.bussType = (TextView) conview.findViewById(R.id.ONLI_bussType);
				vh.NameOrCar = (TextView) conview.findViewById(R.id.ONLI_mcORcNo);
				vh.cxUintLink = (TextView) conview.findViewById(R.id.ONLI_cxUintLink);
				vh.lxPhone = (TextView) conview.findViewById(R.id.ONLI_lxPhone);

				vh.firstButTv =(TextView) conview.findViewById(R.id.ONLI_cancel_order);
				vh.secendButTv=(TextView) conview.findViewById(R.id.ONLI_accept_order);
				conview.setTag(vh);
				setOnButtonTvOnclick(item, vh.firstButTv, vh.secendButTv);
			} else {
				vh = (ViewHoder) conview.getTag();
			}
			final PublicOrderEntity idata = data.get(checkType).get(item);
			if ("FC".equals(idata.caseTypeAPP)) {
				vh.NameOrCar.setText("案件名称：");
				vh.time.setText(idata.createDate);
				vh.casetype.setText((idata.status==1?"财险":"水险"));
				vh.bussType.setText(idata.feicheBaoxianType);
				vh.baoanNo.setText(idata.insurerCaseNo);
				vh.caseName.setText(idata.caseName);
				vh.status.setText(GetFcStatusUtil.getstatus(idata.status));
				vh.wtren.setText(idata.deputePer);
				vh.cxUintLink.setText(idata.cxUintLink);
				vh.lxPhone.setText(idata.lxPhone);
				if (TextUtils.isEmpty(idata.lxPhone)) {
					vh.lxPhone.setVisibility(View.INVISIBLE);
				}else {
					vh.lxPhone.setVisibility(View.VISIBLE);
				}
				vh.lxPhone.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						CallUtils.call(CancelAndSubmitActivity.this, idata.lxPhone);
					}
				});
				
			} else {
				vh.NameOrCar.setText("车牌号：");
				vh.time.setText(idata.createDate);
				vh.casetype.setText(idata.caseTypeName);
				vh.bussType.setText(idata.bussTypeName);
				vh.baoanNo.setText(idata.baoanNo);
				vh.caseName.setText(idata.licensePlateBiaoDi);
				String statuss=GetOrederStatus.fromStatuId(Integer.valueOf(idata.status));
				vh.status.setText(statuss);
				vh.wtren.setText(idata.entrusterName);
				vh.cxUintLink.setText(idata.baoanPersonName);
				vh.lxPhone.setText(idata.baoanPersonPhone);
				if (TextUtils.isEmpty(idata.baoanPersonPhone)) {
					vh.lxPhone.setVisibility(View.INVISIBLE);
				}else {
					vh.lxPhone.setVisibility(View.VISIBLE);
				}
				vh.lxPhone.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						CallUtils.call(CancelAndSubmitActivity.this, idata.baoanPersonPhone);
					}
				});
			}

			return conview;
		}

		public class ViewHoder {
			TextView time, casetype,bussType, baoanNo, caseName, status, wtren, cxUintLink, lxPhone,NameOrCar;

			TextView rejectTv,addressTv,naviTv;
			/**底部操作按钮*/
			TextView firstButTv,secendButTv;
		}

	}
	
	private void setOnButtonTvOnclick(final int itemPostion,TextView firstTv,TextView secendTv) {
		firstTv.setText("查看接报案信息");
		secendTv.setText("查看订单作业信息");
		firstTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {//提交审核
				jumpToBaoanInfo(itemPostion);
			}
		});
		secendTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				jumpToWorkActivity(true, data.get(checkType).get(itemPostion).uid, 
				data.get(checkType).get(itemPostion).bussTypeId + "", data.get(checkType).get(itemPostion).status + "");
			}
		});
	}
	
	/**跳转到接报案信息界面*/
	private void jumpToBaoanInfo(int itemId){
		Intent intent=new Intent(this, CaseInfoActivty.class);
		intent.putExtra("caseBaoanUid", data.get(checkType).get((int)itemId).caseBaoanUid);
		intent.putExtra("status", data.get(checkType).get((int)itemId).status);
		intent.putExtra("orderUid", data.get(checkType).get((int)itemId).uid);
		intent.putExtra("taskType", data.get(checkType).get((int)itemId).bussTypeId+"");
		this.startActivity(intent);
	}
	
	/**跳转到车险作业查看界面*/
	public void jumpToWorkActivity(boolean jumpflag, String uid, String type, String statu) {
		if (jumpflag) {
			Intent intent = new Intent(this, DisplayOrderActivity.class);
			intent.putExtra("orderUid", uid);
			intent.putExtra("taskType", type);
			intent.putExtra("status", statu);
			this.startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.CANCELSUB_btn_0:
			checkType=0;
			break;
		case R.id.CANCELSUB_btn_1:
			checkType=1;
			break;
		case R.id.CANCELSUB_btn_2:
			checkType=2;
			break;
		case R.id.CANCELSUB_btn_3:
			checkType=3;
			break;

		default:
			break;
		}
		downloadCheckDate();
	}

	private void downloadCheckDate() {
		if (checkType==0) {
			if (data.get(checkType).size()==0) {
				loadDialog.setMessage("努力加载中……").show();
				dowloadingFc(1);
				adapter = new MyOrderAdapter();
				mPullRefreshListView.setAdapter(adapter);
			}else {
				checkShowData();
			}
			
		}else {
			if (data.get(checkType).size()==0) {
				loadDialog.setMessage("努力加载中……").show();
				dowLoadingDataCX(0);
				adapter = new MyOrderAdapter();
				mPullRefreshListView.setAdapter(adapter);
			}else {
				checkShowData();
			}
		}
		
	}
	
	private void checkShowData() {
		if (adapter == null) {
			adapter = new MyOrderAdapter();
		}
		mPullRefreshListView.setAdapter(adapter);
		setOnLisner();
		sheEmptyView();
	}

}
