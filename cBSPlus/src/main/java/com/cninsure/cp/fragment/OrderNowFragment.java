package com.cninsure.cp.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.IndexActivity;
import com.cninsure.cp.LoadingActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.activity.yjx.YjxSurveyActivity;
import com.cninsure.cp.cx.CxDsBaoanInfoActivity;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.FCOrderEntity;
import com.cninsure.cp.entity.PagedRequest;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.YjxStatus;
import com.cninsure.cp.entity.cx.CxNewAuditEntity;
import com.cninsure.cp.entity.cx.CxOrderEntity;
import com.cninsure.cp.entity.cx.CxOrderStatus;
import com.cninsure.cp.entity.fc.APPRequestModel;
import com.cninsure.cp.entity.fc.ShenheMsgEntity;
import com.cninsure.cp.entity.yjx.YjxCaseDispatchTable;
import com.cninsure.cp.entity.yjx.YjxOrderListEntity;
import com.cninsure.cp.fc.activity.SurveyActivity;
import com.cninsure.cp.fragment.utils.CxOrderListMoreOperationTool;
import com.cninsure.cp.navi.NaviHelper;
import com.cninsure.cp.utils.APPDownloadUtils;
import com.cninsure.cp.utils.CallUtils;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.GetFcStatusUtil;
import com.cninsure.cp.utils.GetOrederStatus;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PopupWindowUtils;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.extact.ExtactUserUtil;
import com.cninsure.cp.view.LoadingDialog;
import com.cninsure.cp.view.MarqueeTextView;

public class OrderNowFragment extends Fragment implements OnCheckedChangeListener, OnItemClickListener, OnClickListener {

	private View fragmentView;
	private TextView listTitleTv, CXOrderTv;
	private MarqueeTextView FCOrderTv;
	private ListView listView;
	private List<String> paramsList;
	// private List<PublicOrderEntity> dataAll;
	private List<PublicOrderEntity> data;
	private MyOrderAdapter adapter;
	private LayoutInflater inflater;
	private RadioGroup radgrup;
	private LoadingDialog loadDialog;
	private List<NameValuePair> params;
	private boolean isJumpToWork = false;
	private String jumpOrderUid;
	private String jumpstatu;
	private String jumpType;
	/** 0显示未结案，作业中和被驳回；1显示为接单；2显示作业中；3显示被驳回 **/
	private int showOrderType = 0;
	/** 请求车险任务列表还是非车任务列表：1为非车，2为车险 **/
	private int FCorCX = 1;
	/** 非车任务列表 **/
	private FCOrderEntity FcOrders;
	private List<PublicOrderEntity> DataALL;
	/**医健险任务列表*/
	private List<YjxCaseDispatchTable> yjxGgsOrderList;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat simpSF=new SimpleDateFormat("HH:mm:ss");
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat SF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static ExtactUserUtil extactUserUtil;
	private CxChoiceGGSTool cxChoiceGGSTool;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.ordernow_fragment, null);
		EventBus.getDefault().register(this);
		initView();
		downloadOrderData(1);
		new ExtactUserUtil().isExtactGetInfo(getActivity(),false); //如果是外部车童，就获取车童信息
		return fragmentView;
	}

	private void initView() {
		loadDialog = new LoadingDialog(getActivity());
		listTitleTv =  fragmentView.findViewById(R.id.orderNF_listTitle);
		listView = (ListView) fragmentView.findViewById(R.id.orderNF_list);
		listView.setEmptyView(fragmentView.findViewById(R.id.orderNF_emptyText));
		radgrup = (RadioGroup) fragmentView.findViewById(R.id.OTCI_btnG);

		listTitleTv.setText(String.format(getResources().getString(R.string.order_listTitle), "全部"));
		radgrup.setOnCheckedChangeListener(this);
		data = new ArrayList<PublicOrderEntity>();

		cxChoiceGGSTool = new CxChoiceGGSTool(getActivity(), (dialog, which) -> {
			showTransferDialog();
		});
	}

	/**
	 * 如果选着转派公估师，提示用户是否确认转派，如果没选择转派公估师，提示用户。
	 */
	private void showTransferDialog() { //transfer
		if (cxChoiceGGSTool.choiceGGS!=null && cxChoiceGGSTool.choiceGGS.userId!=null){
			DialogUtil.getAlertOnelistener(getActivity(), "确认转派给公估师" + cxChoiceGGSTool.choiceGGS.name + "吗？",
					(dialog, which) -> {
						submitTransfer();
					}).show();

		}else{
			ToastUtil.showToastShort(getActivity(),"未选择公估师");
		}
	}

	/**
	 * 提交转派
	 */
	private void submitTransfer() {
		List<NameValuePair> paramsList = new ArrayList<>(6);
		paramsList.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		paramsList.add(new BasicNameValuePair("id", cxChoiceGGSTool.orderId));
		paramsList.add(new BasicNameValuePair("ggsUid", cxChoiceGGSTool.choiceGGS.userId));
		HttpUtils.requestPost(URLs.CX_ORDER_TRANSFER, paramsList, HttpRequestTool.CX_ORDER_TRANSFER);
		LoadDialogUtil.setMessageAndShow(getActivity(), "加载中……");
	}


	@Override
	public void onResume() {
		super.onResume();
		if (isJumpToWork) {
			isJumpToWork = false;
//			downloadOrderData(FCorCX);
			downloadOrderData(2);
		}
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isJumpToWork = true;
		EventBus.getDefault().unregister(this);
		if (loadDialog!=null && loadDialog.isShowing()) loadDialog.dismiss();
	}

	/**
	 *
	 * @param downType 1非车；2车险；3医健险
	 */
	public void downloadOrderData(int downType) {
		if (downType == 1) {  //1非车
			params = new ArrayList<NameValuePair>();
			@SuppressWarnings("rawtypes")
			APPRequestModel<PagedRequest> appre = new APPRequestModel<PagedRequest>();
			try {
				appre.userToken = AppApplication.getUSER().data.targetOid;
			} catch (Exception e) {
				startActivity(new Intent(AppApplication.mInstance, LoadingActivity.class)); //获取信息为空时就重新登录
				e.printStackTrace();
			}
			PagedRequest<String> requestData = new PagedRequest<String>();
			requestData.pageNo = 1;
			requestData.pageSize = 999999;
			requestData.data = null;
			appre.requestData = requestData;
			params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre)));
			HttpUtils.requestPost(URLs.FC_GET_WORK_CASE_LIST, params, HttpRequestTool.FC_GET_WORK_CASE_LIST);
		} else if (downType == 2) {  //2车险
			paramsList = new ArrayList<String>(2);
			paramsList.add("userId");
			paramsList.add(AppApplication.getUSER().data.userId);
			paramsList.add("ggsUid");
			paramsList.add(AppApplication.getUSER().data.userId);
			paramsList.add("start");
			paramsList.add("0");
			paramsList.add("size");
			paramsList.add("100");
			paramsList.add("statusArr");
			paramsList.add(",4,2,6,10");
			HttpUtils.requestGet(URLs.CX_NEW_GET_GGS_ORDER, paramsList, HttpRequestTool.CX_NEW_GET_GGS_ORDER);
		} else if (downType == 3) {
			paramsList = new ArrayList<String>(2);
			paramsList.add("userId");
			paramsList.add(AppApplication.getUSER().data.userId);
			paramsList.add("ggsId");
			paramsList.add(AppApplication.getUSER().data.userId);
			paramsList.add("size");
			paramsList.add("20");
			paramsList.add("start");
			paramsList.add("0");
			paramsList.add("statusArr");
			paramsList.add("1,2,88");
			HttpUtils.requestGet(URLs.YJX_GGS_ORDER_LIST, paramsList, HttpRequestTool.YJX_GGS_ORDER_LIST);
		}
		if (adapter == null) {
			loadDialog.setMessage("信息读取中……").show();
		}
	}
	
	
	/**新订单透传时刷新界面**/
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventReflash(String code){
		if ("NEW_ORDER".equals(code)) {
			downloadOrderData(1);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int bid) {
		switch (bid) {
		case R.id.OTCI_btn_0:
			showOrderType = 0;
			checkOrderList(0,1, 2, 3, 4, 5 ,6, 7,10);
			listTitleTv.setText(String.format(getResources().getString(R.string.order_listTitle), "全部"));
			break;

		case R.id.OTCI_btn_1:
			showOrderType = 1;
			checkOrderList(0,1,2,3);
			listTitleTv.setText(String.format(getResources().getString(R.string.order_listTitle), "未接单"));
			break;

		case R.id.OTCI_btn_2:
			showOrderType = 2;
			checkOrderList(4, 5);
			listTitleTv.setText(String.format(getResources().getString(R.string.order_listTitle), "作业中"));
			break;

		case R.id.OTCI_btn_3:
			showOrderType = 3;
			checkOrderList(7,88);
			listTitleTv.setText(String.format(getResources().getString(R.string.order_listTitle), "被驳回"));
			break;

		default:
			break;
		}
	}
	
	private void checkOrderList(int... typeId) {
		data.clear();
		for (int i : typeId) {
			switch (i) {
			case 0:
				checkData(0);
				break;
			case 1:
				checkData(1);
				break;

			case 5:
				checkData(5);
				break;

			case 2:
				checkData(2);
				break;

			case 3:
				checkData(3);
				break;

			case 4:
				checkData(4);
				break;

			case 6:
				checkData(6);
				break;

			case 7:
				checkData(7);
				break;

			case 10:
				checkData(10);
				break;

			default:
				break;
			}
		}
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}else {
			Displayorder();
		}
	}

	private void checkData(int typeid) {
		if (DataALL!=null) {
			for (int j = 0; j < DataALL.size(); j++) {
				if (DataALL.get(j).status == typeid) {
					data.add(DataALL.get(j));
				}
			}
		}else {
			data=new ArrayList<PublicOrderEntity>();
		}
		
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventSelfdata(List<NameValuePair> value) {
		int rcode = Integer.valueOf(value.get(0).getName());
		if (rcode == HttpRequestTool.RECEIVE_ORDER || rcode == HttpRequestTool.CANCEL_ORDER || rcode == HttpRequestTool.FC_GET_WORK_CASE_LIST
				|| rcode == HttpRequestTool.CX_NEW_GET_GGS_ORDER || rcode == HttpRequestTool.SUBMIT_WORK || rcode == HttpRequestTool.GET_VERSION_INFO
				|| rcode == HttpRequestTool.GET_ORDER_AUDIT_LIST || rcode == HttpRequestTool.YJX_GGS_ORDER_LIST) {
			loadDialog.dismiss();
		}
		if (rcode == HttpRequestTool.FC_GET_WORK_CASE_LIST) {
			downloadOrderData(2);
			// int httpRcode=Integer.valueOf(value.get(1).getValue());
			// if (httpRcode!=200) { //非车接口出现问题时要保证能正常请求车险数据
			//
			// }
		}
		if (rcode == HttpRequestTool.CLEAN_CID && CheckHttpResult.checkList(value, getActivity()) != HttpRequestTool.CLEAN_CID) {
			ToastUtil.showToastLong(getActivity(), "退出用户失败!");
			((IndexActivity) getActivity()).loadDialog.dismiss();
		}

		switch (CheckHttpResult.checkList(value, getActivity())) {
			case HttpRequestTool.CX_NEW_GET_GGS_ORDER:
				downloadOrderData(3);
				jiexiDate(value.get(0).getValue());
				break;
			case HttpRequestTool.YJX_GGS_ORDER_LIST:
				jiexiYjxDate(value.get(0).getValue());
				// Displayorder();
				break;

			case HttpRequestTool.RECEIVE_ORDER:
				ToastUtil.showToastLong(getActivity(), value.get(0).getValue());
				if (isJumpToWork) {
					jumpToWorkActivity(isJumpToWork, jumpOrderUid, Integer.parseInt(jumpType), jumpstatu, null);
				}
				downloadOrderData(1);
				break;
			case HttpRequestTool.CANCEL_ORDER:
				LoadDialogUtil.dismissDialog();
				ToastUtil.showToastLong(getActivity(), value.get(0).getValue());
				((IndexActivity)getActivity()).displayFragment(2);//重新加载Fragment以刷新界面
				break;
			case HttpRequestTool.SUBMIT_WORK:// 提交审核成功！
				showSubmitSuccessAlert(value);
				break;
			case HttpRequestTool.FC_GET_WORK_CASE_LIST:// 获取非车任务列表！
				jiexiFCdata(value.get(0).getValue());
				// downloadOrderData(2);
				break;
			case HttpRequestTool.CLEAN_CID:
				((IndexActivity) getActivity()).excetUser();// indexActivity中请求清空服务器端CID成功后在这里调用indexActivity中方法退出用户
				break;
			case HttpRequestTool.GET_VERSION_INFO:// 版本信息
				handleVersion(value.get(0).getValue());
				break;
			case HttpRequestTool.GET_ORDER_AUDIT_LIST: // 审核信息
				showSHHMessage(value.get(0).getValue());
				break;
			case HttpRequestTool.YJX_ORDER_BACK: // 医健险调度退回
				showYjxHintMsg(HttpRequestTool.YJX_ORDER_BACK, value.get(0).getValue());
				break;
			case HttpRequestTool.YJX_ORDER_ACCEPT: // 接受医健险调度
				showYjxHintMsg(HttpRequestTool.YJX_ORDER_ACCEPT, value.get(0).getValue());
				break;
			case HttpRequestTool.CX_EXT_USER: //外部车童信息
				LoadDialogUtil.dismissDialog();
				extactUserUtil = new ExtactUserUtil();
				extactUserUtil.isSignble(getActivity(), value.get(0).getValue());
				break;
			case HttpRequestTool.CX_ORDER_TRANSFER: //提交转派
				LoadDialogUtil.dismissDialog();
				((IndexActivity)getActivity()).displayFragment(2);//重新加载Fragment以刷新界面
				getTransferSubmitInfo(value.get(0).getValue());
				break;
			case HttpRequestTool.CX_GET_USER_BY_ORGID: //转派查询车童清单
				cxChoiceGGSTool.setGGSList(value.get(0).getValue());
				break;
			case HttpRequestTool.CX_POST_CHARGE_BACK: //公估师退单
				showRequestmsg(value.get(0).getValue());
				((IndexActivity)getActivity()).displayFragment(2);//重新加载Fragment以刷新界面
				break;
			case HttpRequestTool.CX_POST_REVOKE: //公估师撤单
				showRequestmsg(value.get(0).getValue());
				((IndexActivity)getActivity()).displayFragment(2);//重新加载Fragment以刷新界面
				break;
			case HttpRequestTool.CX_ACCEPT_OR_REFUSE_ZP_ORDER: //接受或者拒绝转派
				showRequestmsg(value.get(0).getValue());
				((IndexActivity)getActivity()).displayFragment(2); //重新加载Fragment以刷新界面
				break;

			default:
				break;
		}
	}

	private void showRequestmsg(String json){
		LoadDialogUtil.dismissDialog();
		try {
			BaseEntity requestMsg = JSON.parseObject(json,BaseEntity.class);
			DialogUtil.getAlertDialog(getActivity(),requestMsg.msg,"提示").show();
		} catch (Exception e) {
			ToastUtil.showToastLong(getActivity(), json);
			e.printStackTrace();
		}
	}

	/**
	 * 车险任务转派提交返回信息
	 * @param value
	 */
	private void getTransferSubmitInfo(String value) {
		if (value != null){
			BaseEntity basEn = JSON.parseObject(value,BaseEntity.class);
			if (basEn!=null && basEn.success){
				ToastUtil.showToastLong(getActivity(),"转派成功！");
			}else{
				DialogUtil.getErrDialog(getActivity(),"转派失败："+(basEn.msg==null?"":basEn.msg)).show();
				ToastUtil.showToastLong(getActivity(),"转派失败："+(basEn.msg==null?"":basEn.msg));
			}
		}else{
			ToastUtil.showToastLong(getActivity(),"转派失败！");}
	}

	private void showYjxHintMsg(int code, String value) {
		switch (code) {
		case HttpRequestTool.YJX_ORDER_BACK:// 医健险调度退回
			if (value.indexOf("修改成功")>-1) {//退回成功,刷新界面
				downloadOrderData(1);//重新加载数据
			}
			DialogUtil.getAlertOneButton(getActivity(),  "退回操作："+value, null).show();
			break;
		case HttpRequestTool.YJX_ORDER_ACCEPT:// 接受医健险调度
			if (value.indexOf("修改成功")>-1) {//修改成功,刷新界面
				downloadOrderData(1);//重新加载数据
			}
			DialogUtil.getAlertOneButton(getActivity(), "接受调度操作："+value, null).show();
			break;

		default:
			break;
		}
		
	}

	private void jiexiYjxDate(String value) {
		//DataALL = new ArrayList<PublicOrderEntity>();
		try {
			YjxOrderListEntity yjxOrderListEntity = JSON.parseObject(value, YjxOrderListEntity.class);
			if (yjxOrderListEntity!=null && yjxOrderListEntity.list!=null) {
				List<PublicOrderEntity> dataTemp = yjxOrderListEntity.getYjxPublicOrderEntity();
					data.addAll(dataTemp);
					DataALL.addAll(dataTemp);  //添加医健险信息到所有订单列表中
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		switch (showOrderType) {//加载选中的类别
		case 0:
			checkOrderList(0,1, 2, 3, 4, 5, 6, 7,10);
			break;

		case 1:
			checkOrderList(0,1, 3);
			break;

		case 2:
			checkOrderList(2, 4, 5);
			break;

		case 3:
			checkOrderList(7);
			break;

		default:
			break;
		}
	}

	private void handleVersion(String value) {
		try {
			final JSONObject object = new JSONObject(value).getJSONObject("data");
			int versioncose=Integer.valueOf(object.optString("versionCode"));
			final String DownloadUrl=object.getString("clientUrl");
			int sysVersonCode=Integer.valueOf((IndexActivity.instance.getAppVersion(2)));
			if (versioncose <= sysVersonCode) {
			} else {
				Dialog dialog=DialogUtil.getAlertOneButton(getActivity(), "有新的版本可以更新！\n最新版本号：" + object.optString("versionName") + "\n更新信息：" + object.optString("message"),null);
				dialog.show();
				dialog.setOnDismissListener(new DialogInterface. OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface arg0) {
						new APPDownloadUtils((IndexActivity) getActivity()).downloadAPK(DownloadUrl, "CBSPlus.apk");
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 解析非车任务列表数据 **/
	private void jiexiFCdata(String value) {
		FcOrders = JSON.parseObject(value, FCOrderEntity.class);
		DataALL = new ArrayList<PublicOrderEntity>();
		if (FcOrders.data != null && FcOrders.data.list != null) {
			for (int i = 0; i < FcOrders.data.list.size(); i++) {
				data.add(FcOrders.data.list.get(i));
				DataALL.add(FcOrders.data.list.get(i));// 添加非车信息到所有订单列表中
			}
		} else {
			data = new ArrayList<PublicOrderEntity>();
		}
	}

	private void showSubmitSuccessAlert(List<NameValuePair> value) {
		DialogUtil.getAlertOneButton(getActivity(), value.get(0).getValue(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				downloadOrderData(FCorCX);
			}
		}).show();
	}

	private void jiexiDate(String value) {
		CxOrderEntity tempdata1=JSON.parseObject(value, CxOrderEntity.class);
		if (DataALL == null) {//当非车数据请求失败的时候为DataALL空，需要初始化
			DataALL = new ArrayList<>();
		}
		for (int i = 0; i < tempdata1.list.size(); i++) {
			if (tempdata1.list.get(i).status == 2 || tempdata1.list.get(i).status == 4 || tempdata1.list.get(i).status == 6 || tempdata1.list.get(i).status == 10) {
				for (PublicOrderEntity tempPoe:DataALL){ //避免重复，有重复就剔除掉。
					boolean idB = tempPoe.id.longValue()==(tempdata1.list.get(i).getStandardOrderEnt().id.longValue());
					if ("CX".equals(tempPoe.caseTypeAPP) && idB){
						DataALL.remove(tempPoe); break;
					}
				}
				data.add(tempdata1.list.get(i).getStandardOrderEnt());
				DataALL.add(tempdata1.list.get(i).getStandardOrderEnt());// 添加车险信息到所有订单列表中
			}
		}
	}

	private void Displayorder() {
		adapter = new MyOrderAdapter();
		listView.setAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private class MyOrderAdapter extends BaseAdapter {

		@SuppressLint("SimpleDateFormat")
		public MyOrderAdapter() {
			inflater = LayoutInflater.from(OrderNowFragment.this.getActivity());
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressWarnings("deprecation")
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
				vh.wtren = (MarqueeTextView) conview.findViewById(R.id.ONLI_wtren);
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
				setOnButtonTvOnclick(data.get(item),item, vh.firstButTv, vh.secendButTv,vh);
//			} else {
//				vh = (ViewHoder) conview.getTag();
//			}
			final PublicOrderEntity idata = data.get(item);
			if ("FC".equals(idata.caseTypeAPP)) {
				vh.NameOrCar.setText("案件名称");
				vh.time.setText(idata.createDate);
				switch (idata.riskType) {
				case 1:
					vh.casetype.setText("财险");
					break;
				case 2:
					vh.casetype.setText("水险");
					break;
				case 3:
					vh.casetype.setText("创新·分散型");
					break;
				default:
					break;
				}
				vh.casetype.setText((idata.riskType==1?"财险":"水险"));
				vh.bussType.setText(idata.feicheBaoxianType);
				vh.baoanNo.setText(idata.insurerCaseNo);
				vh.caseName.setText(idata.caseName);
				vh.status.setText(GetFcStatusUtil.getstatus(idata.status));
				vh.wtren.setText(idata.deputePer);
				vh.cxUintLink.setText(idata.cxUintLink);
				vh.lxPhone.setText(idata.lxPhone);
				vh.naviTv.setVisibility(View.GONE);
				vh.addressTv.setText(idata.caseLocation);
				if (TextUtils.isEmpty(idata.lxPhone)) {
					vh.callPhoneTv.setVisibility(View.INVISIBLE);
				}else {
					vh.callPhoneTv.setVisibility(View.VISIBLE);
				}
				vh.callPhoneTv.setOnClickListener(arg0 -> CallUtils.call(OrderNowFragment.this.getActivity(), idata.lxPhone));
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
				//超时显示
				if (idata.status==1 || idata.status==2 || idata.status==88 ) { //
					vh.timeOutTv.setVisibility(View.VISIBLE);
					data.get(item).outTimView=vh.timeOutTv;
					setTimeOut(item);
				}else {
					vh.timeOutTv.setVisibility(View.GONE);
				}

				((TextView)conview.findViewById(R.id.ONLI_mcWtren)).setText("任务编号");
				vh.wtren.setText(idata.uid);
				//设置复制的提示图标
				vh.wtren.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.copy_somshing_yellow35), null);
				CopyUtils.setCopyOnclickListener(getActivity(), vh.wtren, idata.uid); //点击复制任务编号
				conview.findViewById(R.id.ONLI_mcContect).setVisibility(View.GONE);
				conview.findViewById(R.id.ONLI_mcContectLO).setVisibility(View.GONE);
				vh.WTinfoTv.setVisibility(View.GONE);
				
			} else {
				vh.NameOrCar.setText("车牌号");
				vh.time.setText(idata.createDate);
				vh.casetype.setText(idata.caseTypeName);
				if(idata.bussTypeId == 395){ //人伤查勘分单项和全案
					if (idata.investigationType!=null && idata.investigationType==1)  vh.bussType.setText(idata.bussTypeName+"(全案)");   //全案
					if (idata.investigationType!=null && idata.investigationType==0)  vh.bussType.setText(idata.bussTypeName+"(单项)");   //单项
				}else{
					vh.bussType.setText(idata.bussTypeName);
				}
				vh.baoanNo.setText(idata.baoanNo);
				vh.caseName.setText(idata.licensePlateBiaoDi);
				String statuss=GetOrederStatus.fromStatuId(Integer.valueOf(idata.status));
				vh.status.setText(statuss+getZPStatus(idata));
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
						CallUtils.call(OrderNowFragment.this.getActivity(), idata.baoanPersonPhone);
					}
				});
				
				if (idata.status==7) {//审核驳回
					vh.timeOutTv.setVisibility(View.GONE);
				}else if (idata.status!=2) { //非作业中的案件不显示超时信息
					vh.timeOutTv.setVisibility(View.GONE);
				}else { //
					vh.timeOutTv.setVisibility(View.VISIBLE);
					data.get(item).outTimView=vh.timeOutTv;
					setTimeOut(item);
				}

				vh.WTinfoTv.setVisibility(View.VISIBLE);
				vh.WTinfoTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						PopupWindowUtils.showPopupWindow(getpopView(idata.caseLifecycle), vh.WTinfoTv, OrderNowFragment.this.getActivity());
					}

					private View getpopView(final String caseLifecycle) {
						LinearLayout addView=(LinearLayout) LayoutInflater.from(OrderNowFragment.this.getActivity())
								.inflate(R.layout.popupwindow_linearlayout, null);
						TextView view=new TextView(OrderNowFragment.this.getActivity());
						view.setText(caseLifecycle);
						view.setHint("无委托信息！");
						view.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View arg0) {
								CopyUtils.copy(OrderNowFragment.this.getActivity(), caseLifecycle);
								return false;
							}
						});
						addView.addView(view);
						return addView;
					}
				});
				
				//设置线路规划
				vh.naviTv.setOnClickListener(arg0 -> NaviHelper.startNavi(getActivity(), idata.caseLocationLatitude, idata.caseLocationLongitude,
						idata.caseLocation, idata.baoanPersonPhone));
				
				/**查看审核报告*/
				if (idata.status==7) {
					vh.rejectTv.setVisibility(View.VISIBLE);
					downloadBohuiInfo(vh.rejectTv,idata.uid);
				}
			}
			/**公共部分**/
			vh.copyTv.setOnClickListener(arg0 -> CopyUtils.copy(OrderNowFragment.this.getActivity(), vh.baoanNo.getText().toString()));
			return conview;
		}

		/**设置item下面两个按钮对应的文本和事件=====================================================================**/
		private void setOnButtonTvOnclick(final PublicOrderEntity idata,final int itemPostion,TextView firstTv,TextView secendTv,ViewHoder vh){
			// TODO Auto-generated method stub
			if ("FC".equals(data.get(itemPostion).caseTypeAPP)) { //如果是非车业务只显示蓝色按钮，修改文本并设置点击事件（直接跳转到作业界面）
//				firstTv.setVisibility(View.GONE);
				firstTv.setText("拨号联系人");
				secendTv.setText("去作业");
				firstTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						CallUtils.call(OrderNowFragment.this.getActivity(), idata.lxPhone);
					}
				});
				secendTv.setOnClickListener(arg0 -> {
					Intent intent = new Intent(getActivity(), SurveyActivity.class);
					int id = Integer.parseInt(data.get(itemPostion).id + "");
					intent.putExtra("id", id);
					intent.putExtra("caseNo", data.get(itemPostion).caseNo);
					startActivity(intent);
				});
				return;
			}
			
			if ("YJX".equals(data.get(itemPostion).caseTypeAPP)) { 
				setYjxButtonTvOnclick(idata,firstTv,secendTv,data.get(itemPostion),itemPostion);
				return;
			}
			
			/***到了这里就是车险的案件了***/
			if (data.get(itemPostion).status == 2) {  //未接单状态 可以选择取消订单或接受
				setTVByStatus(firstTv,idata,itemPostion);
				if (!getZPWorkble(idata)) {secendTv.setTextColor(getActivity().getResources().getColor(R.color.hui_text_xh)); return;} //根据转派情况确定是否能操作。
				secendTv.setTextColor(getActivity().getResources().getColor(R.color.bulue_main));
				secendTv.setOnClickListener(arg0 -> {
					reciveOrder(itemPostion);//接受订单
				});
				return;
			} else if (data.get(itemPostion).status == 4) {//案件也接单，只显示蓝色按钮，修改文本并设置点击事件（直接跳转到作业界面）
				setTVByStatus(firstTv,idata,itemPostion);
				secendTv.setText("去作业");
				if (!getZPWorkble(idata)) {secendTv.setTextColor(getActivity().getResources().getColor(R.color.hui_text_xh)); return;}//根据转派情况确定是否能操作。
				secendTv.setTextColor(getActivity().getResources().getColor(R.color.bulue_main));
				secendTv.setOnClickListener(arg0 -> jumpToWorkActivity(true, data.get(itemPostion).uid,
						data.get(itemPostion).bussTypeId , data.get(itemPostion).status + "",data.get(itemPostion)));
			}  else if (data.get(itemPostion).status == 6) { /**案件作业中*/
//				firstTv.setText("提交审核");
				setTVByStatus(firstTv,idata,itemPostion);
				secendTv.setText("去作业");
				if (!getZPWorkble(idata)) {secendTv.setTextColor(getActivity().getResources().getColor(R.color.hui_text_xh)); return;}//根据转派情况确定是否能操作。
				secendTv.setTextColor(getActivity().getResources().getColor(R.color.bulue_main));
				secendTv.setOnClickListener(arg0 -> jumpToWorkActivity(true, data.get(itemPostion).uid,
						data.get(itemPostion).bussTypeId , data.get(itemPostion).status + "",data.get(itemPostion)));
			} else if (data.get(itemPostion).status == 10) { /**审核退回*/
//				firstTv.setText("提交审核");
				setTVByStatus(firstTv,idata,itemPostion);
				secendTv.setText("去作业");
				if (!getZPWorkble(idata)) {secendTv.setTextColor(getActivity().getResources().getColor(R.color.hui_text_xh)); return;}//根据转派情况确定是否能操作。
				secendTv.setTextColor(getActivity().getResources().getColor(R.color.bulue_main));
				secendTv.setOnClickListener(arg0 -> jumpToWorkActivity(true, data.get(itemPostion).uid,
						data.get(itemPostion).bussTypeId , data.get(itemPostion).status + "",data.get(itemPostion)));
				vh.rejectTv.setText("审核信息！");
				vh.rejectTv.setVisibility(View.VISIBLE);
				downloadBohuiInfo(vh.rejectTv,idata.uid);
			}
		}

		/**
		 * 车险新任务，选择拒绝或者是转派
		 * @param itemPostion
		 */
		private void showUTChoiceDialog(int itemPostion) {
//			new CxOrderListMoreOperationTool(getActivity()).showOperationDialog(data.get(itemPostion),cxChoiceGGSTool);
//			new AlertDialog.Builder(getActivity()).setTitle("选择！")
//					.setItems(new String[]{"拒绝", "转派"}, (dialog, which) -> {
//						if (which==0) cancelOrder(itemPostion);//拒绝订单
//						else {
//							cxChoiceGGSTool.showChoiceDialog(data.get(itemPostion).id + "");
//						}
//					}).create().show();
		}

		/**
		 * 根据任务状态设置文本提示和点击事件
		 * @param firstTv
		 * @param idata
		 */
		private void setTVByStatus(TextView firstTv, PublicOrderEntity idata,int itemPostion) {
			if (idata.lastGgsUid!=null && idata.lastGgsUid.equals(AppApplication.getUSER().data.userId)) { //上一个转派人员UID与登录人员UId一致，订单为登录人员转派出去的，只能进行作业
				//根据转派状态显示对应的文本
				if (idata.transferStatus == null) { //没有转派记录 可以转派
					SetTextUtil.setTextViewText(firstTv, "更多");
					firstTv.setTextColor(getActivity().getResources().getColor(R.color.bulue_main));
					firstTv.setOnClickListener(v -> {
						new CxOrderListMoreOperationTool(getActivity()).showOperationDialog(data.get(itemPostion), cxChoiceGGSTool);
					});
				} else if (idata.transferStatus == 0) { //转派中
					SetTextUtil.setTextViewText(firstTv, "转派中");
					firstTv.setOnClickListener(null);
				} else if (idata.transferStatus == 2) { //转派被拒绝-可转派
					SetTextUtil.setTextViewText(firstTv, "更多");
					firstTv.setTextColor(getActivity().getResources().getColor(R.color.bulue_main));
					firstTv.setOnClickListener(v -> {
						new CxOrderListMoreOperationTool(getActivity()).showOperationDialog(data.get(itemPostion), cxChoiceGGSTool);
					});
				} else if (idata.transferStatus == 1) { //已转派
					SetTextUtil.setTextViewText(firstTv, "已转派");
					firstTv.setOnClickListener(null);
				}
				//上一个转派人员UID与登录人员UId不一致，代表该订单是别人转派给自己的。
			}else {
				if (idata.transferStatus == null || idata.transferStatus != 0 ) { //没有转派记录 可以转派
					SetTextUtil.setTextViewText(firstTv, "更多");
					firstTv.setTextColor(getActivity().getResources().getColor(R.color.bulue_main));
					firstTv.setOnClickListener(v -> {
						new CxOrderListMoreOperationTool(getActivity()).showOperationDialog(data.get(itemPostion), cxChoiceGGSTool);
					});
				} else if (idata.transferStatus == 0) { //转派中
					SetTextUtil.setTextViewText(firstTv, "接受/拒绝转派");
					firstTv.setTextColor(getActivity().getResources().getColor(R.color.bulue_main));
					firstTv.setOnClickListener(v -> showAcceptZPOrderAlert(idata));
				}
			}
		}

		/**
		 * 弹框选择接受或者拒绝转派
		 * @param idata
		 */
		private void showAcceptZPOrderAlert(PublicOrderEntity idata) {
			new AlertDialog.Builder(getActivity()).setTitle("选择操作类型")
					.setItems(new String[]{"接受转派", "拒绝转派"}, (dialog, which) -> {
						if (which==0) acceptZPOrder(idata,URLs.CX_ACCEPT_ZP_ORDER); //接受转派
						else if (which==1) acceptZPOrder(idata,URLs.CX_REFUSE_ZP_ORDER); //拒绝转派
					}).setNegativeButton("取消",null)
					.create().show();
		}

		/**
		 * 接受转派订单
		 * @param idata
		 */
		private void acceptZPOrder(PublicOrderEntity idata,String url) {
			List<NameValuePair> paramsList = new ArrayList<>(6);
			paramsList.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
			paramsList.add(new BasicNameValuePair("id", idata.id+""));
			HttpUtils.requestPost(url, paramsList, HttpRequestTool.CX_ACCEPT_OR_REFUSE_ZP_ORDER);
		}

		/***医健险案件item底部按钮点击事件设置***/
		private void setYjxButtonTvOnclick(final PublicOrderEntity idata, TextView firstTv, TextView secendTv, final PublicOrderEntity dataItem, final int itemPostion) {
			if (dataItem.status == 1) {
				firstTv.setOnClickListener(arg0 -> {
//						//取消订单
					paramsList = new ArrayList<String>(2);
					paramsList.add("id");
					paramsList.add(dataItem.id+"");
					paramsList.add("status");
					paramsList.add(YjxStatus.REFUSE+"");
					HttpUtils.requestGet(URLs.YJX_CHANGE_STATUS, paramsList, HttpRequestTool.YJX_ORDER_BACK);
				});
				secendTv.setOnClickListener(arg0 -> {
//						//接受订单
					paramsList = new ArrayList<String>(2);
					paramsList.add("id");
					paramsList.add(dataItem.id+"");
					paramsList.add("status");
					paramsList.add(YjxStatus.RECEIVE+"");
					HttpUtils.requestGet(URLs.YJX_CHANGE_STATUS, paramsList, HttpRequestTool.YJX_ORDER_ACCEPT);
				});
				return;
			} else if (data.get(itemPostion).status == 2) {//案件也接单，只显示蓝色按钮，修改文本并设置点击事件（直接跳转到作业界面）
				firstTv.setText("线路规划");
				secendTv.setText("去作业");
				firstTv.setOnClickListener(arg0 -> NaviHelper.startNavi(getActivity(), idata.caseLocationLatitude, idata.caseLocationLongitude,
						idata.caseLocation, idata.baoanPersonPhone));
				secendTv.setOnClickListener(arg0 -> jumpToYJXWorkActivity(true, data.get(itemPostion)));
			}  else if (data.get(itemPostion).status == 88) {
				firstTv.setText("提交审核");
				secendTv.setText("去作业");
				
				firstTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {//提交审核
						DialogUtil.getErrDialog(getActivity(), "开发中！").show();
						return;
//						List<NameValuePair> params = new ArrayList<NameValuePair>();
//						params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
//						params.add(new BasicNameValuePair("orderUid", data.get(itemPostion).uid));
//						HttpUtils.requestPost(URLs.SubmitWork(), params, HttpRequestTool.SUBMIT_WORK);
//						loadDialog.setMessage("操作中……").show();
					}
				});
				secendTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						jumpToYJXWorkActivity(true, data.get(itemPostion));
					}
				});
			}
		}

		/**倒计时设置**/
		private void setTimeOut(final int item){
			if (data.get(item).timeOutHours>0 && !TextUtils.isEmpty(data.get(item).dispatchDate)) {
				if (data.get(item).timer!=null) {
					data.get(item).timer.cancel();
				}
				data.get(item).timer=new Timer();
				data.get(item).timer.schedule(new TimerTask() {
					@Override
					public void run() {
						Message message=new Message();
						if (item<data.size()) {
							message.obj=data.get(item);
							message.what=item;
							handler.sendMessage(message);
						}
					}
				}, 0, 1000);
			}
		}
		
		@SuppressLint("HandlerLeak")
		private Handler handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				setTime(msg.what);
			}
		};

		/**计算并显示超时信息**/
		@SuppressWarnings("deprecation")
		private void setTime(int item) {
			try {
				Date nowdate = new Date();
				PublicOrderEntity tempOrder=data.get(item);
				Date creatDate = SF.parse(tempOrder.dispatchDate.toString());

				long nowtime = nowdate.getTime();
				long creatTime = creatDate.getTime();

				long outtime = data.get(item).timeOutHours * 60 * 60 * 1000;
				long ctime = nowtime - creatTime;

				long time = outtime - ctime;

				if (time >= 0) {
					data.get(item).outTimView.setTextColor(Color.parseColor("#ff1296db"));
					data.get(item).outTimView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.corners_bule_30dp));
					data.get(item).outTimView.setText("剩余时间：" + formatDuring(time));
				} else {
					data.get(item).outTimView.setTextColor(Color.parseColor("#ffff0000"));
					data.get(item).outTimView.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.corners_red_30dp));
					data.get(item).outTimView.setText("超出时间：" + formatDuring(time * -1));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**毫秒转化为时分秒*/
		 public String formatDuring(long mss) {  
		        long days = mss / (1000 * 60 * 60 * 24);  
		        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);  
		        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);  
		        long seconds = (mss % (1000 * 60)) / 1000;  
		        String hourstr=hours>=10?(hours+""):("0"+hours); 
		        String minutessr=minutes>=10?(minutes+""):("0"+minutes); 
		        String secondsstr=seconds>=10?(seconds+""):("0"+seconds);
		        
		        if (days>0) {
			        return days+"天"+hourstr + ":" + minutessr + ":" + secondsstr;  
				}else {
			        return hourstr + ":" + minutessr + ":" + secondsstr;  
				}
		    } 

		public class ViewHoder {
			TextView time, casetype,bussType, baoanNo, caseName, status,
			cxUintLink, lxPhone,NameOrCar,callPhoneTv,copyTv,WTinfoTv,timeOutTv;
			MarqueeTextView wtren;
			/**驳回信息*/
			TextView rejectTv,addressTv,naviTv;
			/**底部操作按钮*/
			TextView firstButTv,secendButTv;
		}
	}

	/**
	 * 根据转派信息判断转派状态，分是否由登录用户转出或别人转入。
	 * @param idata
	 * @return
	 */
	private String getZPStatus(PublicOrderEntity idata) {
		if (idata.lastGgsUid==null || idata.transferStatus == null) return ""; //没有转派人员和转派状态信息，就返回空字符串。
		if (idata.lastGgsUid.equals(AppApplication.getUSER().data.userId)) { //上一个转派人员UID与登录人员UId一致，订单为登录人员转派出去的
			//根据转派状态显示对应的文本
			if (idata.transferStatus == 0) {
				return "-转派中";
			} else if (idata.transferStatus == 2) {
				return "-转派被拒绝";
			} else if (idata.transferStatus == 1) {
				return "-已转派";
			}
		}else { //上一个转派人员UID与登录人员UId不一致，代表该订单是别人转派给自己的。
			//根据转派状态显示对应的文本
			if (idata.transferStatus == 0) {
				return "-转派待确认";
			} else if (idata.transferStatus == 2) {
				return "-您已拒绝转派";
			} else if (idata.transferStatus == 1) {
				return "-接受的转派";
			}
		}
		return "";
	}

	/**
	 * 根据转派信息判断当前用户是否能进行作业操作，分是否由登录用户转出或别人转入。
	 * 传true代表可以作业，传false代表不能作业。
	 * @param idata
	 * @return
	 */
	private boolean getZPWorkble(PublicOrderEntity idata) {
		if (idata.lastGgsUid==null || idata.transferStatus == null) return true; //没有转派人员和转派状态信息，就返回空字符串。
		if (idata.lastGgsUid.equals(AppApplication.getUSER().data.userId)) { //上一个转派人员UID与登录人员UId一致，订单为登录人员转派出去的
			//根据转派状态显示对应的文本
			if (idata.transferStatus == 0) {
				return false; //转派中不能作业
			} else if (idata.transferStatus == 2) {
				return true; //对方拒绝转派，能作业。
			} else if (idata.transferStatus == 1) {
				return false; //对方接受转派，自己不能作业
			}
		}else { //上一个转派人员UID与登录人员UId不一致，代表该订单是别人转派给自己的。
			//根据转派状态显示对应的文本
			if (idata.transferStatus == 0) {
				return false; //自己未接受该转派，不能作业
			} else if (idata.transferStatus == 2) {
				return false; //自己拒绝转派，不能作业
			} else if (idata.transferStatus == 1) {
				return true; //已接收的转派，可以作业
			}
		}
		return true;
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
		HttpUtils.requestGet(URLs.GET_ORDER_AUDIT_LIST, params2, HttpRequestTool.GET_ORDER_AUDIT_LIST);
	}
	
	/**解析审核信息**/
	private void showSHHMessage(String value) {
		List<CxNewAuditEntity> SHHMsg= JSON.parseArray(value, CxNewAuditEntity.class);
		List<Map<String, String>> params=new ArrayList<Map<String, String>>();
		Map<String, String> map;

		if (SHHMsg!=null && SHHMsg.size()>0) {
			for (int i = 0; i < SHHMsg.size(); i++) {
					map=new HashMap<String, String>();
					map.put("data", SF.format(SHHMsg.get(i).auditTime));
					boolean ispasss=SHHMsg.get(i).status==1;
					String ispass=(ispasss)?"通过":"不通过";
					map.put("msg", "审核结果："+ispass+"\n审核意见："+ SHHMsg.get(i).auditMsg);
					if (SHHMsg.get(i).status== 0) //审核不通过
						map.put("msg", "审核结果："+ispass+"\n审核意见："+ SHHMsg.get(i).auditMsg+"\n退回概述："+ SHHMsg.get(i).backSummary
								+"\n退回原因："+ SHHMsg.get(i).backReasons);
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
		DialogUtil.getDialogByViewTwoButton(getActivity(), listView, "订单审核信息！", null).show();
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int itemId, long arg3) {
		// TODO Auto-generated method stub
		if ("FC".equals(data.get(itemId).caseTypeAPP)) {
			Intent intent = new Intent(getActivity(), SurveyActivity.class);
			int id = Integer.parseInt(data.get(itemId).id + "");
			intent.putExtra("id", id);
			intent.putExtra("caseNo", data.get(itemId).caseNo);
			startActivity(intent);
			return;
		}
		if (data.get(itemId).status == 0) {
			DialogUtil.getItemDialog(getActivity(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int clickid) {
					switch (clickid) {
					case 0:
						reciveOrder(itemId);
						break;
					case 1:
						cancelOrder(itemId);
						break;
					default:
						break;
					}
				}
			}, "接单", "取消订单").show();//, "接单并开始作业", "查看案件详情"
			return;
		} else if (data.get(itemId).status == 2) {
						jumpToWorkActivity(true, data.get(itemId).uid, data.get(itemId).bussTypeId, data.get(itemId).status + "",data.get(itemId));
		}  else if (data.get(itemId).status == 7) {
			DialogUtil.getItemDialog(getActivity(), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int lspoint) {
					if (lspoint == 0) {
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
						params.add(new BasicNameValuePair("orderUid", data.get(itemId).uid));
						HttpUtils.requestPost(URLs.SubmitWork(), params, HttpRequestTool.SUBMIT_WORK);
						loadDialog.setMessage("操作中……").show();
					} else if (lspoint == 1) {
						jumpToWorkActivity(true, data.get(itemId).uid, data.get(itemId).bussTypeId, data.get(itemId).status + "",data.get(itemId));
					} 
				}
			}, "提交审核", "填写作业信息").show();//, "查看案件详情"
		} else if (data.get(itemId).status == 5) {
			DialogUtil.getItemDialog(getActivity(), (arg01, lspoint) -> {
				if (lspoint == 0) {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
					params.add(new BasicNameValuePair("orderUid", data.get(itemId).uid));
					HttpUtils.requestPost(URLs.SubmitWork(), params, HttpRequestTool.SUBMIT_WORK);
					loadDialog.setMessage("操作中……").show();
				} else if (lspoint == 1) {
					jumpToWorkActivity(true, data.get(itemId).uid, data.get(itemId).bussTypeId, data.get(itemId).status + "",data.get(itemId));
				}
			}, "提交审核", "填写作业信息").show();//, "查看案件详情"
		}
	}

	/**
	 * 
	 */
	public void reciveOrder(int itemId) {
		params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		params.add(new BasicNameValuePair("id", data.get(itemId).id + ""));
		HttpUtils.requestPost(URLs.ReceiveOrder(), params, HttpRequestTool.RECEIVE_ORDER);
		loadDialog.setMessage("确定中……").show();
	}

	public void cancelOrder(int itemId) {
		params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		params.add(new BasicNameValuePair("id", data.get(itemId).id + ""));
		HttpUtils.requestPost(URLs.CancelOrder(), params, HttpRequestTool.CANCEL_ORDER);
		loadDialog.setMessage("取消中……").show();
	}

	public void jumpToWorkActivity(boolean jumpflag, String uid, Integer type, String statu,PublicOrderEntity dataEn) {
		if (jumpflag) {
//			Intent intent = new Intent(getActivity(), WorkOrderActivty.class);
			Intent intent = new Intent();
			switch (type){
//				case 2 :  intent.setClass(getActivity(), CxSurveyWorkActivity.class);break;  //现场查勘
				case 2 :  intent.setClass(getActivity(), CxJieBaoanInfoActivity.class);break;  //现场查勘新
////				case 39 :  intent.setClass(getActivity(), CxInjurySurveyActivity.class);break;  //人伤查勘
//				case 40 :  intent.setClass(getActivity(), CxDsBaoanInfoActivity.class);break;  //标的定损 //CxDsWorkActivity
//				case 41 :  intent.setClass(getActivity(), CxDsWorkActivity.class);break;  //三者定损 - 界面同“标的定损” //CxDsWorkActivity
//				case 42 :  intent.setClass(getActivity(), CxDamageActivity.class);break;  //物损定损
//				case 392 :  intent.setClass(getActivity(), CxInjuryTrackActivity.class);break;  //人伤跟踪
//				case 394 :  intent.setClass(getActivity(), CxDisabyIdentifyActivity.class);break; //陪同残定
//				case 393 :  intent.setClass(getActivity(), CxInjuryMediateActivity.class);break; //人伤调解
//				case 395 :   //人伤调查 investigationType
//					if (dataEn.investigationType!=null && dataEn.investigationType==1){ //全案
//						intent.setClass(getActivity(), CxInjuryExamineActivity.class);
//					}else{intent.setClass(getActivity(), CxInjuryExamineOnlyActivity.class);}
//					break;
//				//人伤任务、人伤初勘（同人伤查勘<人伤任务>），人伤定损（接口不通，做不了）
//				default: DialogUtil.getAlertOneButton(getActivity(),"功能开发中！",null).show();return;  //默认现场查勘
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
	
	/**跳转到医健险作业界面**/
	public void jumpToYJXWorkActivity(boolean jumpflag, PublicOrderEntity poEn) {
		if (jumpflag) {
			Intent intent = new Intent(getActivity(), YjxSurveyActivity.class);
			intent.putExtra("dispatchUid", poEn.uid);
			intent.putExtra("uid", poEn.caseBaoanUid);
			intent.putExtra("id", poEn.id+"");
			getActivity().startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.orderNF_FC:
//			changCheckWorkType(1);
//			break;
//
//		case R.id.orderNF_CX:
//			changCheckWorkType(2);
//			break;
//
//		default:
//			break;
//		}
	}

	public void changCheckWorkType(int type) {
		if (type == 1) {
			FCorCX = 1;
			checkOrderList(3, 4);
			changView();
			recoverBg(FCOrderTv, R.drawable.corners_white_left_30dp);
		} else {
			FCorCX = 2;
			checkOrderList(0, 2, 5, 7);
			changView();
			recoverBg(CXOrderTv, R.drawable.corners_white_right_30dp);
		}
	}

	private void changView() {

		RadioButton rd1, rd2, rd3;
		rd1 = (RadioButton) fragmentView.findViewById(R.id.OTCI_btn_1);
		rd2 = (RadioButton) fragmentView.findViewById(R.id.OTCI_btn_2);
		rd3 = (RadioButton) fragmentView.findViewById(R.id.OTCI_btn_3);
		radgrup.check(R.id.OTCI_btn_0);
	}

	private void recoverBg(TextView tv, int reid) {
		FCOrderTv.setBackgroundResource(R.drawable.corners_alpha_left_30dp);
		CXOrderTv.setBackgroundResource(R.drawable.corners_alpha_right_30dp);

		FCOrderTv.setTextColor(getResources().getColor(R.color.hui_text_h));
		CXOrderTv.setTextColor(getResources().getColor(R.color.hui_text_h));
		setTvBg(tv, reid);
	}

	private void setTvBg(TextView tv, int reid) {
		tv.setBackgroundResource(reid);
		tv.setTextColor(getResources().getColor(R.color.bulue_main));
	}
}
