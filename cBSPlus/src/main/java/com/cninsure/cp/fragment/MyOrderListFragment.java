package com.cninsure.cp.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.activity.yjx.YjxBaoanInputActivity;
import com.cninsure.cp.activity.yjx.YjxDispatchShenheActivity;
import com.cninsure.cp.activty.CaseInfoActivty;
import com.cninsure.cp.activty.DisplayOrderActivity;
import com.cninsure.cp.cx.CxDsBaoanInfoActivity;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.entity.CaseOrder;
import com.cninsure.cp.entity.FCOrderEntity;
import com.cninsure.cp.entity.PagedRequest;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxOrderEntity;
import com.cninsure.cp.entity.fc.APPRequestModel;
import com.cninsure.cp.entity.fc.ShenheMsgEntity;
import com.cninsure.cp.entity.yjx.YjxOrderListEntity;
import com.cninsure.cp.navi.NaviHelper;
import com.cninsure.cp.utils.CallUtils;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.GetFcStatusUtil;
import com.cninsure.cp.utils.GetOrederStatus;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.MyPullRefreshListViewAlertUtil;
import com.cninsure.cp.utils.PopupWindowUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.cx.EmptyViewUtil;
import com.cninsure.cp.view.LoadingDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MyOrderListFragment extends Fragment implements OnItemClickListener, OnCheckedChangeListener {

	private View contentView;
	private PullToRefreshListView mPullRefreshListView;
	private LayoutInflater inflater;
	private List<PublicOrderEntity> adapterDate , cxDate , fcDate , yjxDate;
	private List<String> paramsList;
	private List<NameValuePair> params;
	private LoadingDialog loadDialog;
	private MyOrderAdapter adapter;
	/**车险最后下载数据数据*/
	private CaseOrder caseorders;
	/**医键险最后下载数据*/
	private YjxOrderListEntity yjxOrderListEntity;
	/**非车险最后下载数据*/
	private FCOrderEntity fcCaseorders;
	/**选择的显示类型，0非车，1车险，2货运险，3医健险**/
	private int checkType=0;
	private RadioGroup radgrup;
	/**listView数据为空时显示的提示*/
//	private TextView emptyTv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.myorderlist_fragment, null);
		EventBus.getDefault().register(this);
		initView();
		dowloadingFc(1);
		return contentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
		dowLoadingDataCX(0);
	}

	@Override
	public void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

	private void initView() {
		mPullRefreshListView = (PullToRefreshListView) contentView.findViewById(R.id.MOLF_pull_refresh_list);
		new EmptyViewUtil().SetEmptyView(getActivity(),mPullRefreshListView);
		radgrup = (RadioGroup) contentView.findViewById(R.id.ENDORDER_btnG);

		mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
		radgrup.setOnCheckedChangeListener(this);
		loadDialog = new LoadingDialog(MyOrderListFragment.this.getActivity());
	}

	private void dowLoadingDataCX(int startPoint) {
		paramsList = new ArrayList<String>(4);
//		paramsList.add("userId");
//		paramsList.add(AppApplication.getUSER().data.userId);
//		paramsList.add("size");
//		paramsList.add(10 + "");
//		paramsList.add("orderStatus");
//		paramsList.add(",3,8,"); //审核通过
//		paramsList.add("start");
//		if (caseorders==null) {
//			paramsList.add("0");
//		}else {
//			paramsList.add(startPoint+"");
//		}
//		paramsList.add("caseTypeId");
//		if (checkType==1) {
//			paramsList.add("100");
//		}else if(checkType==2) {
//			paramsList.add("400");
//		}else if(checkType==3){
//			paramsList.add("200");
//		}
////		HttpUtils.requestGet(URLs.GetSelforder(), paramsList, HttpRequestTool.GET_SELFORDER_END);
//		loadDialog.setMessage("加载中……").show();
//		HttpUtils.requestGet(URLs.GetStatuSelforder(), paramsList, HttpRequestTool.GET_SELFORDER_END);

		paramsList = new ArrayList<String>(2);
		paramsList.add("userId");
		paramsList.add(AppApplication.getUSER().data.userId);
		paramsList.add("ggsUid");
		paramsList.add(AppApplication.getUSER().data.userId);
		paramsList.add("start");
		paramsList.add("0");
		paramsList.add("size");
		paramsList.add("50");
		paramsList.add("statusArr");
		paramsList.add(",7,8,9");
		HttpUtils.requestGet(URLs.CX_NEW_GET_GGS_ORDER, paramsList, HttpRequestTool.CX_NEW_GET_GGS_ORDER_COMPLATED);

		if (adapter==null) {
			loadDialog.setMessage("信息读取中……").show();
		}
	}
	
	/**获取医键险已完成任务列表*/
	private void getYjxFinishOrder(int startPoint){
		paramsList = new ArrayList<String>(2);
		paramsList.add("userId");
		paramsList.add(AppApplication.getUSER().data.userId);
		paramsList.add("ggsId");
		paramsList.add(AppApplication.getUSER().data.userId);
		paramsList.add("size");
		paramsList.add("20");
		paramsList.add("start");
		paramsList.add(startPoint+"");
		paramsList.add("statusArr");
		paramsList.add("4,5");
		loadDialog.setMessage("加载中……").show();
		HttpUtils.requestGet(URLs.YJX_GGS_ORDER_LIST, paramsList, HttpRequestTool.YJX_GGS_ORDER_LIST);
	}
	
	private void dowloadingFc(int pagePoint) {
		params = new ArrayList<NameValuePair>();
		@SuppressWarnings("rawtypes")
		APPRequestModel<PagedRequest> appre = new APPRequestModel<PagedRequest>();
		appre.userToken = AppApplication.getUSER().data.targetOid;
		PagedRequest<Map> requestData = new PagedRequest<Map>();
		requestData.pageNo = pagePoint;
		requestData.pageSize = 10;
		Map<String, String> map=new HashMap<String, String>(1);
		map.put("filed3", "6,7,8,9,10");
		requestData.data = map;
		appre.requestData = requestData;
		params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre)));
		loadDialog.setMessage("加载中……").show();
		HttpUtils.requestPost(URLs.GET_FC_STATUS_LIST, params, HttpRequestTool.GET_FC_WORKED_LIST);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventDownLoadMyorderList(List<NameValuePair> value) {

		int requestType = Integer.parseInt(value.get(0).getName());
		if (requestType == HttpRequestTool.CX_NEW_GET_GGS_ORDER_COMPLATED || requestType ==  HttpRequestTool.GET_FC_WORKED_LIST
				|| requestType == HttpRequestTool.GET_ORDER_STATUS|| requestType == HttpRequestTool.YJX_GGS_ORDER_LIST) { 
			loadDialog.dismiss();
		}
		switch (CheckHttpResult.checkList(value, MyOrderListFragment.this.getActivity())) {
		case HttpRequestTool.CX_NEW_GET_GGS_ORDER_COMPLATED:
			getOrderDataCX(value.get(0).getValue());
			break;

		case HttpRequestTool.GET_FC_WORKED_LIST:
			getOrderDataFC(value.get(0).getValue());
			break;
		case HttpRequestTool.GET_ORDER_STATUS: //审核信息
			showSHHMessage(value.get(0).getValue());
			break; 
		case HttpRequestTool.YJX_GGS_ORDER_LIST: //医键险完成订单信息
			getYjxFinishOrders(value.get(0).getValue());
			break; 

		default:
			break;
		}
	}

	/**获取医键险已完成订单信息*/
	private void getYjxFinishOrders(String value) {
		try {
			yjxOrderListEntity = JSON.parseObject(value, YjxOrderListEntity.class);
			if (yjxOrderListEntity!=null && yjxOrderListEntity.list!=null) {
					yjxDate = yjxOrderListEntity.getYjxPublicOrderEntity();  //添加医健险信息到所有订单列表中
					if (checkType==3) {
						adapterDate = yjxDate;
						checkShowData();
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getOrderDataCX(String value) {
		CxOrderEntity tempdata1 = JSON.parseObject(value, CxOrderEntity.class);
		List<PublicOrderEntity> publicOrderEn = new ArrayList<>();
		if (tempdata1 !=null && tempdata1.list!=null ){
			for (int i = 0; i < tempdata1.list.size(); i++) {
				publicOrderEn.add(tempdata1.list.get(i).getStandardOrderEnt());
			}
//		caseorders = JSON.parseObject(value, CaseOrder.class);
			if (tempdata1.startRow == 1) {
				cxDate.clear();
			}
				cxDate.addAll(publicOrderEn);
		}
		showDataList();
	}
	private void getOrderDataFC(String value) {
		fcCaseorders = JSON.parseObject(value, FCOrderEntity.class);
		if (fcCaseorders.code==0) {
			if (fcCaseorders != null && fcCaseorders.data.pageNo==1) {
				fcDate = fcCaseorders.data.list;
			} else if (fcCaseorders != null && fcCaseorders.data.pageNo!=0 && fcDate!=null) {
				fcDate.addAll(fcCaseorders.data.list);
			}
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
	}
	
	private void checkShowData() {
		if (adapter == null) {
			adapter = new MyOrderAdapter();
		}
		mPullRefreshListView.setAdapter(adapter);
		setOnLisner();
	}
	

	private void setOnLisner() {
		
		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override//下拉刷新
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //重新定义为第一页
            	if (checkType==0) { //非车
					dowloadingFc(1);
				}else if (checkType==1) { //车险
					dowLoadingDataCX(0);
				}else if (checkType==2) { //货运险
					dowLoadingDataCX(0);
				}else if (checkType==3) { //医健险
					getYjxFinishOrder(0);
				}
            }

            @Override//上拉加载更多
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
             //开始数加10
            	
            	if (checkType==0) { //非车
            		if (null!=fcCaseorders && null!=fcCaseorders.data){
            			if ( !fcCaseorders.data.lastPage) {
            				dowloadingFc(fcCaseorders.data.pageNo+1);
						}else {
							ToastUtil.showToastShort(getActivity(), "没有更多数据了！");
							MyPullRefreshListViewAlertUtil.setAlertInfo(mPullRefreshListView, "----我也是有底线的----", 2*1000); //提示没有更多信息可以加载;
						}
            		}else {
            			dowloadingFc(1); //为空重新下载
					}
            		
				}else if (checkType==1) { //车险
					if (caseorders!=null) {
						if ((caseorders.tableData.start+10)>caseorders.tableData.recordsFiltered) { // TODO : 这个不知道有没有问题!!!!!??????
							ToastUtil.showToastShort(getActivity(), "没有更多数据了！");
							MyPullRefreshListViewAlertUtil.setAlertInfo(mPullRefreshListView, "----我也是有底线的----", 2*1000); //提示没有更多信息可以加载
						}else {
							dowLoadingDataCX(caseorders.tableData.start+10);
						}
					}else {
						dowLoadingDataCX(0); //为空重新下载
					}
					
				}else if (checkType==2) { //货运险
//					dowLoadingDataCX(0);
				}else if (checkType==3) { //医健险
					if (yjxOrderListEntity != null && yjxOrderListEntity.total > yjxDate.size()) {
						getYjxFinishOrder(yjxDate.size() / yjxOrderListEntity.pageSize);
					} else {
						MyPullRefreshListViewAlertUtil.setAlertInfo(mPullRefreshListView, "----我也是有底线的----", 2*1000); //提示没有更多信息可以加载
					}
					getYjxFinishOrder(0);
				}
            	
            	if (fcCaseorders!=null && fcCaseorders.data!=null && checkType==0) {//
					dowloadingFc(fcCaseorders.data.pageNo+1);
				}else {
					if (caseorders!=null && caseorders.tableData!=null && (caseorders.tableData.start+10)>caseorders.tableData.recordsFiltered) {
	            		ToastUtil.showToastShort(getActivity(), "没有更多数据了！");
	            		MyPullRefreshListViewAlertUtil.setAlertInfo(mPullRefreshListView, "----我也是有底线的----", 2*1000); //提示没有更多信息可以加载
					}
					if (caseorders!=null && caseorders.tableData!=null){
						dowLoadingDataCX(caseorders.tableData.start+10);
					}else{
						dowLoadingDataCX(0); //如果为空就重新加载
					}
				}
            }
		});
	}
	
	public class MyOrderAdapter extends BaseAdapter {

		public MyOrderAdapter() {
			inflater = LayoutInflater.from(MyOrderListFragment.this.getActivity());
		}

		@Override
		public int getCount() {
			if (adapterDate==null) {
				return 0;
			}
			return adapterDate.size();
		}

		@Override
		public Object getItem(int arg0) {
			if (adapterDate==null) {
				return null;
			}
			return adapterDate.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		//final PublicOrderEntity idata = data.get(checkType).get(item);
		@Override
		public View getView(int item, View conview, ViewGroup arg2) {
			
			final ViewHoder vh;
//			if (conview == null) {
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
				vh.callPhoneTv=(TextView) conview.findViewById(R.id.ONLI_lxPhone_call);
				vh.copyTv=(TextView) conview.findViewById(R.id.ONLI_copy_baoan_no);
				vh.WTinfoTv=(TextView) conview.findViewById(R.id.ONLI_WT_info);
				vh.timeOutTv=(TextView) conview.findViewById(R.id.ONLI_timeOut);
				vh.rejectTv=(TextView) conview.findViewById(R.id.ONLI_bohuiInfo);
				vh.addressTv =(TextView) conview.findViewById(R.id.ONLI_address);
				vh.naviTv=(TextView) conview.findViewById(R.id.ONLI_naviGation);
				vh.firstButTv =(TextView) conview.findViewById(R.id.ONLI_cancel_order);
				vh.secendButTv=(TextView) conview.findViewById(R.id.ONLI_accept_order);
				conview.setTag(vh);
				setOnButtonTvOnclick(item, vh.firstButTv, vh.secendButTv);
//			} else {
//				vh = (ViewHoder) conview.getTag();
//			}
				final PublicOrderEntity idata = adapterDate.get(item);
			if ("FC".equals(idata.caseTypeAPP)) {
				vh.NameOrCar.setText("案件名称：");
				vh.time.setText(idata.createDate);
				vh.casetype.setText((idata.riskType==1?"财险":"水险"));
				vh.bussType.setText(idata.feicheBaoxianType);
				vh.baoanNo.setText(idata.insurerCaseNo);
				vh.caseName.setText(idata.caseName);
				vh.status.setText(GetFcStatusUtil.getstatus(idata.status));
				vh.wtren.setText(idata.deputePer);
				vh.cxUintLink.setText(idata.cxUintLink);
				vh.lxPhone.setText(idata.lxPhone);
				if (TextUtils.isEmpty(idata.lxPhone)) {
					vh.callPhoneTv.setVisibility(View.INVISIBLE);
				}else {
					vh.callPhoneTv.setVisibility(View.VISIBLE);
				}
				vh.callPhoneTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						CallUtils.call(MyOrderListFragment.this.getActivity(), idata.lxPhone);
					}
				});
				vh.WTinfoTv.setVisibility(View.GONE);
				vh.timeOutTv.setVisibility(View.GONE);
				vh.rejectTv.setVisibility(View.GONE);
			} else if ("YJX".equals(idata.caseTypeAPP)) { //医健险
				vh.NameOrCar.setText("作业类型");
				vh.caseName.setText(idata.bussTypeName);
				vh.time.setText(idata.createDate);
				vh.casetype.setText(idata.caseTypeName);
				vh.bussType.setText(idata.feicheBaoxianType);
				vh.baoanNo.setText(idata.baoanNo);
				String statuss=GetOrederStatus.getYjxStatus(Integer.valueOf(idata.status));
				vh.status.setText(statuss);
				vh.addressTv.setText(idata.caseLocation);
				((TextView)conview.findViewById(R.id.ONLI_mcWtren)).setText("任务编号");
				vh.wtren.setText(idata.uid);
				//设置复制的提示图标
				vh.wtren.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.copy_somshing_yellow35), null);
				CopyUtils.setCopyOnclickListener(getActivity(), vh.wtren, idata.uid); //点击复制任务编号
//				conview.findViewById(R.id.ONLI_mcWtrenLO).setVisibility(View.GONE);
				conview.findViewById(R.id.ONLI_mcContect).setVisibility(View.GONE);
				conview.findViewById(R.id.ONLI_mcContectLO).setVisibility(View.GONE);
				vh.WTinfoTv.setVisibility(View.GONE);
				
			}  else {
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
				vh.addressTv.setText(idata.caseLocation);
				if (TextUtils.isEmpty(idata.baoanPersonPhone)) {
					vh.callPhoneTv.setVisibility(View.INVISIBLE);
				}else {
					vh.callPhoneTv.setVisibility(View.VISIBLE);
				}
				vh.callPhoneTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						CallUtils.call(MyOrderListFragment.this.getActivity(), idata.baoanPersonPhone);
					}
				});
				
				if (idata.status==7) {//审核驳回
					vh.timeOutTv.setVisibility(View.GONE);
				}else if (idata.status!=2) { //非作业中的案件不显示超时信息
					vh.timeOutTv.setVisibility(View.GONE);
				}

				vh.WTinfoTv.setVisibility(View.VISIBLE);
				vh.WTinfoTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						PopupWindowUtils.showPopupWindow(getpopView(idata.caseLifecycle), vh.WTinfoTv, MyOrderListFragment.this.getActivity());
					}

					private View getpopView(final String caseLifecycle) {
						LinearLayout addView=(LinearLayout) LayoutInflater.from(MyOrderListFragment.this.getActivity())
								.inflate(R.layout.popupwindow_linearlayout, null);
						TextView view=new TextView(MyOrderListFragment.this.getActivity());
						view.setText(caseLifecycle);
						view.setHint("无委托信息！");
						view.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View arg0) {
								CopyUtils.copy(MyOrderListFragment.this.getActivity(), caseLifecycle);
								return false;
							}
						});
						addView.addView(view);
						return addView;
					}
				});
				
				//设置线路规划
				vh.naviTv.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						NaviHelper.startNavi(getActivity(), idata.caseLocationLatitude, idata.caseLocationLongitude,
								idata.caseLocation, idata.baoanPersonPhone);
					}
				});
				
				/**查看审核报告*/
//				if (idata.status==7) {
				vh.rejectTv.setText("审核信息！");
				vh.rejectTv.setVisibility(View.VISIBLE);
				downloadBohuiInfo(vh.rejectTv,idata.uid);
//				}
			}
			
			
			/**公共部分**/
			vh.copyTv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					CopyUtils.copy(MyOrderListFragment.this.getActivity(), vh.baoanNo.getText().toString());
				}
			});
			
			return conview;
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
					jumpToWorkActivity(true, adapterDate.get(itemPostion).uid,
							adapterDate.get(itemPostion).bussTypeId , adapterDate.get(itemPostion).status + "",adapterDate.get(itemPostion));
				}
			});
			
			if ("YJX".equals(adapterDate.get(itemPostion).caseTypeAPP)) { 
				setFinishOclick(itemPostion, firstTv, secendTv);
//				setYjxButtonTvOnclick(idata,firstTv,secendTv,data.get(itemPostion),itemPostion);
				return;
			}
			
		}
		
		/**结案及其他位置状态操作*/
		private void setFinishOclick(final int point, TextView button1, TextView button2) {
			button1.setText("接报案详情");
			button2.setText("作业详情");
			button1.setOnClickListener(new OnClickListener() { //退回接报案
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(), YjxBaoanInputActivity.class);
					intent.putExtra("uid", adapterDate.get(point).caseBaoanUid); //这里要传接报案的UID，而不是任务的UID
					intent.putExtra("requestType", "seeBaoanInfo");
					getActivity().startActivity(intent);
				}
			});
			//作业信息（进入后不可编辑）
			button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) { //跳转到调度界面，因为已结案接报案不用调度，所以不通过jumpTDispatchActivtity(point);方法跳转
					
					Intent intent = new Intent(getActivity(), YjxDispatchShenheActivity.class);
					intent.putExtra("dispatchUid", adapterDate.get(point).uid);
					intent.putExtra("uid", adapterDate.get(point).caseBaoanUid);
					intent.putExtra("id", adapterDate.get(point).id+"");
					intent.putExtra("requestType", "seeWorkInfo");
					getActivity().startActivity(intent);
				}
			});
		}

		public class ViewHoder {
			TextView time, casetype,bussType, baoanNo, caseName, status, wtren,
			cxUintLink, lxPhone,NameOrCar,callPhoneTv,copyTv,WTinfoTv,timeOutTv;
			/**驳回信息*/
			TextView rejectTv,addressTv,naviTv;
			/**底部操作按钮*/
			TextView firstButTv,secendButTv;
		}

	}
	
	/**下载驳回信息
	 * @param uid */
	private void downloadBohuiInfo(TextView rejectTv, final String uid) {
		rejectTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				downloadSHHMsg(uid);
			}
		});
	}
	
	/**获取审核信息 
	 * @param uid **/
	private void downloadSHHMsg(String uid){
		/**获取审核信息*/
		loadDialog.setMessage("信息读取中……").show();
		List<String> params2 = new ArrayList<String>();
		params2.add("userId");
		params2.add(AppApplication.getUSER().data.userId);
		params2.add("orderUid");
		params2.add(uid);
		HttpUtils.requestGet(URLs.GET_ORDER_STATUS, params2, HttpRequestTool.GET_ORDER_STATUS);
	}
	
	/**解析审核信息**/
	private void showSHHMessage(String value) {
		ShenheMsgEntity SHHMsg= JSON.parseObject(value, ShenheMsgEntity.class);
		List<Map<String, String>> params=new ArrayList<Map<String, String>>();
		Map<String, String> map;
		map=new HashMap<String, String>();
//		map.put("data", "");
//		map.put("msg", "审核列表");
//		params.add(map);
		
		if (SHHMsg!=null && SHHMsg.tableData != null && SHHMsg.tableData.data != null && SHHMsg.tableData.data.get(0).createDate!=null) {
			for (int i = 0; i < SHHMsg.tableData.data.size(); i++) {
				String cctype="";
				for (int j = 0; j < SHHMsg.tableData.data.get(i).auditEvaluateTables.size(); j++) {
					cctype+="\n"+"差错原因"+(j+1)+"："+SHHMsg.tableData.data.get(i).auditEvaluateTables.get(j).errorMessage+
							"\n差错扣分："+SHHMsg.tableData.data.get(i).auditEvaluateTables.get(j).errorPoints;
				}
					map=new HashMap<String, String>();
					map.put("data", SHHMsg.tableData.data.get(i).createDate);
					boolean ispasss=SHHMsg.tableData.data.get(i).isPass.equals("1");
					String ispass=(ispasss)?"通过":"不通过";
					map.put("msg", "审核结果："+ispass+"\n审核意见："+
							SHHMsg.tableData.data.get(i).auditMessage+cctype);
					params.add(map);
			}
		}else {
			map=new HashMap<String, String>();
			map.put("data", "");
			map.put("msg", "无");
			params.add(map);
		}
		int [] into=new int[]{R.id.LEAVINGitem_time,R.id.LEAVINGitem_message};
		SimpleAdapter simpleAdapter=new SimpleAdapter(this.getActivity(), params, R.layout.leaving_message_item, 
				new String[]{"data","msg"},into);
		ListView listView=new ListView(getActivity());
		listView.setAdapter(simpleAdapter);
		DialogUtil.getDialogByViewOnlistener(getActivity(), listView, "订单审核信息！", null).show();
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long itemId) {
		switch (checkType) {
		case 0://非车
			
			break;
			
		case 1://1车险
			choiceJumpType((int)itemId);
			break;
			
		case 2://2货运险
			ToastUtil.showToastShort(getActivity(), "暂未开发");
			break;
			
		case 3://3医健险
			ToastUtil.showToastShort(getActivity(), "暂未开发");
			break;

		default:
			break;
		}
	}
	
	/**车险查看案件或者订单信息跳转选择**/
	private void choiceJumpType(final int itemId){
		DialogUtil.getItemDialog(getActivity(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int choicePoint) {
//				if (choicePoint==0) {
//					jumpToBaoanInfo(itemId);
//				}else {
//					jumpToWorkActivity(true, adapterDate.get(itemId).uid, adapterDate.get(itemId).bussTypeId + "",adapterDate.get(itemId).status + "");
//				}
				jumpToWorkActivity(true, adapterDate.get(itemId).uid,
						adapterDate.get(itemId).bussTypeId , adapterDate.get(itemId).status + "",adapterDate.get(itemId));
			}
		}, new String[]{"查看接报案信息","查看订单作业信息"}).show();
	}
	
	/**跳转到接报案信息界面*/
	private void jumpToBaoanInfo(int itemId){
		Intent intent=new Intent(getActivity(), CaseInfoActivty.class);
		intent.putExtra("caseBaoanUid", adapterDate.get((int)itemId).caseBaoanUid);
		intent.putExtra("status", adapterDate.get((int)itemId).status);
		intent.putExtra("orderUid", adapterDate.get((int)itemId).uid);
		intent.putExtra("taskType", adapterDate.get((int)itemId).bussTypeId+"");
		getActivity().startActivity(intent);
	}
	
	/**跳转到车险作业查看界面*/
	public void jumpToWorkActivity(boolean jumpflag, String uid, Integer type, String statu,PublicOrderEntity dataEn) {
//		if (jumpflag) {
//			Intent intent = new Intent(getActivity(), DisplayOrderActivity.class);
//			intent.putExtra("orderUid", uid);
//			intent.putExtra("taskType", type);
//			intent.putExtra("status", statu);
//			getActivity().startActivity(intent);
//		}
		if (jumpflag) {
			Intent intent = new Intent();
			switch (type){
				case 2 :  intent.setClass(getActivity(), CxJieBaoanInfoActivity.class);break;  //现场查勘新
				default: intent.setClass(getActivity(), CxDsBaoanInfoActivity.class);break;  //默认现场查勘
			}
			intent.putExtra("bussTypeId", type);
			intent.putExtra("orderUid", uid);
			intent.putExtra("taskType", type);
			intent.putExtra("status", statu);
			intent.putExtra("PublicOrderEntity", dataEn);
			Log.i("JsonHttpUtils", "调用JS传递字符串status=" + statu);
			getActivity().startActivity(intent);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int bid) {
		switch (bid) {
		case R.id.ENDORDER_btn_0:
			isChecked(0);
			break;
		case R.id.ENDORDER_btn_1:
			isChecked(1);
			break;
		case R.id.ENDORDER_btn_2:
			isChecked(2);
			break;
		case R.id.ENDORDER_btn_3:
			isChecked(3);
			break;

		default:
			break;
		}
		
	}
	
	private void isChecked(int position){
		if (position!=checkType) {
			checkType=position;
			checkDownload();
		}
	}
	
	private void checkDownload() {
		if (checkType==0) {
			if (null==fcDate || fcDate.size()==0) {
				adapterDate = new ArrayList<PublicOrderEntity>() ;
				dowloadingFc(1);
			}else {
				adapterDate = fcDate;
//				checkShowData();
			}
		}else if (checkType==3) { //医键险已完成任务列表
			if (null==yjxDate || yjxDate.size()==0) {
				adapterDate = new ArrayList<PublicOrderEntity>() ;
				getYjxFinishOrder(0);
			}else {
				adapterDate = yjxDate;
//				checkShowData();
			}
		} else if (checkType==1){
			 if(null==cxDate || cxDate.size()==0){
				 adapterDate = cxDate = new ArrayList<PublicOrderEntity>() ;
				 dowLoadingDataCX(0);
			 }else {
				 adapterDate = cxDate;
//				 checkShowData();
			}
		} else{
				 adapterDate = new ArrayList<PublicOrderEntity>() ;
		}
		checkShowData();
	}

}
