package com.cninsure.cp.activity.yjx;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.yjx.YjxBaoanListEntity;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class YjxTempStorageActivity extends BaseActivity implements OnCheckedChangeListener {

	private TextView actionTV1, actionTV2, actionTV3; // 顶部返回按钮，标题和暂存按钮。
	private PullToRefreshListView listView;
	private YjxBaoanListEntity tempEntity, noDispatchEntity, DispatchEntity, finishEntity;
	private RadioGroup radgrup; // 切换选项卡的RadioGroup
	private LayoutInflater inflater;
	private int STORAGE = 1; // 暂存接报案信息
	private int SUBMIT = 2; // 暂存接报案信息
	private int FINISH = 3; // 暂存接报案信息
	/** 请求类型：1暂存，2未调度，3已调度，4已结案 */
	private int status = 1;
	private YjxCaseBaoanAdapter adapter;
	private List<YjxCaseBaoanEntity> tempDate, noDispatchDate, DispatchDate, finishDate;
	/** 每页数据条数 */
	private int pageSize = 10;
	/** 当前请求页页码 */
	private int tempPageNum, noDispatchPageNum, DispatchPageNum, finishPageNum;
	public static YjxTempStorageActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yjx_temp_storage_linear);
		EventBus.getDefault().register(this);
		initaction();
		initView();
		downLoadData(0, "0"); // "0,1,2"
		instance = this;
	}

	/**
	 * 分页查询接报案列表
	 * 
	 * @param size
	 *            每一页显示条数
	 * @param start
	 *            起始条数
	 * @param status
	 *            需要查询的暂存接报案状态，多个状态中间用逗号隔开
	 */
	private void downLoadData(int start, String status) {
		LoadDialogUtil.setMessageAndShow(YjxTempStorageActivity.this, "加载中……");
		List<String> params = new ArrayList<String>();
		params.add("size");
		params.add(pageSize + "");

		params.add("start");
		params.add(start + "");

		params.add("statusArr"); // 多个状态中间用逗号隔开
		params.add(status);

		params.add("userId");
		params.add(AppApplication.getUSER().data.userId);
		HttpUtils.requestGet(URLs.GET_YJX_BAOAN_LIST, params, HttpRequestTool.GET_YJX_BAOAN_LIST);
	}

	/**
	 * 通过接报案id删除接报案信息
	 * 
	 * @param id
	 */
	public void deleteCaseBaoan(long id) {
		LoadDialogUtil.setMessageAndShow(YjxTempStorageActivity.this, "操作中……");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id + ""));
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		HttpUtils.requestPost(URLs.DELETE_YJX_BAOAN_CASE, params, HttpRequestTool.DELETE_YJX_BAOAN_CASE);
	}

	/**
	 * 通过接报案id退回接报案
	 * 
	 * @param id
	 */
	public void backCaseBaoan(long id) {
		LoadDialogUtil.setMessageAndShow(YjxTempStorageActivity.this, "退回中……");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id + ""));
		params.add(new BasicNameValuePair("status", "0"));
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		HttpUtils.requestPost(URLs.BACK_YJX_BAOAN_CASE, params, HttpRequestTool.BACK_YJX_BAOAN_CASE);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventBusR(List<NameValuePair> values) {
		int rcode = Integer.valueOf(values.get(0).getName());
		switch (rcode) {
		case HttpRequestTool.GET_YJX_BAOAN_LIST:
			LoadDialogUtil.dismissDialog();
			getBaoanInfo(values.get(0).getValue());
			break;
		case HttpRequestTool.DELETE_YJX_BAOAN_CASE:
			LoadDialogUtil.dismissDialog();
			getdeleteJieBaoanInfo(values.get(0).getValue());
			break;
		case HttpRequestTool.BACK_YJX_BAOAN_CASE:
			LoadDialogUtil.dismissDialog();
			getBackJieBaoanInfo(values.get(0).getValue());
			break;

		default:
			break;
		}
	}

	/** 是否退回成功，成功就刷新界面数据 */
	private void getBackJieBaoanInfo(String valueInfo) { // 返回数据信息——"案件状态修改成功"
		if (valueInfo != null && valueInfo.indexOf("成功") > -1) {
			Dialog dialog = DialogUtil.getAlertOneButton(this, "退回接报案成功！", null);
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {// 刷新界面
					getBuyPullDown();// 重新请求第一页数据，以刷新界面。
				}
			});
			dialog.show();
		} else {
			DialogUtil.getAlertOneButton(this, "退回接报案失败！", null).show();
		}
	}

	/** 是否删除成功，成功就刷新界面数据 */
	private void getdeleteJieBaoanInfo(String valueInfo) {
		if (valueInfo != null && valueInfo.indexOf("成功") > -1) { // 删除接报案成功!
			Dialog dialog = DialogUtil.getAlertOneButton(this, valueInfo, null);
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {// 刷新界面
					getBuyPullDown();// 重新请求第一页数据，以刷新界面。
				}
			});
			dialog.show();
		} else {
			DialogUtil.getErrDialog(this, "删除操作失败！" + valueInfo).show();
		}
	}

	/** 将新数据添加到集合中以便显示，老的数据和新的数据一起显示，实现上拉加载，下拉刷新 */
	public void addDate(YjxBaoanListEntity dateEn, int myPageNum) {
		if ((adapter == null && dateEn != null && dateEn.list != null) || adapter == null) { // 第一处加载数据（fatherDate==null是第一次有数据加载）不判断是刷新还是加载更多
			switch (status) {
			case 1:
				tempDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, tempDate);
				break;
			case 2:
				noDispatchDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, noDispatchDate);
				break;
			case 3:
				DispatchDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, DispatchDate);
				break;
			case 4:
				finishDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, finishDate);
				break;
			}
			listView.setAdapter(adapter);
			listView.onRefreshComplete();
		} else if (dateEn != null && myPageNum == 0) { // myPageNum==0是刷新，清空老数据，添加新数据
			switch (status) {
			case 1:
				tempDate.clear();
				tempDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, tempDate);
				listView.setAdapter(adapter);
				break;
			case 2:
				noDispatchDate.clear();
				noDispatchDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, noDispatchDate);
				listView.setAdapter(adapter);
				break;
			case 3:
				DispatchDate.clear();
				DispatchDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, DispatchDate);
				listView.setAdapter(adapter);
				break;
			case 4:
				finishDate.clear();
				finishDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, finishDate);
				listView.setAdapter(adapter);
				break;
			}
			// if (adapter.listDate!=null && adapter.listDate.size()>0) {
			// if (status==1 && adapter.listDate.get(0).status!=0 ) {
			// //刚从其他状态跳转到暂存状态选项卡，new 一下Adapter
			// }else if (status==2 && adapter.listDate.get(0).status!=1 )
			// {//刚从其他状态跳转到待调度状态选项卡，new 一下Adapter
			// }else if (status==3 && adapter.listDate.get(0).status!=2 &&
			// adapter.listDate.get(0).status!=3 ) {
			// }else if (status==4 && adapter.listDate.get(0).status!=4 ) {
			// }
			// }
			// adapter.notifyDataSetChanged();
			listView.onRefreshComplete();
		} else if (dateEn != null && dateEn.list != null) {
			switch (status) {
			case 1:
				tempDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, tempDate);
				break;
			case 2:
				noDispatchDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, noDispatchDate);
				break;
			case 3:
				DispatchDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, DispatchDate);
				break;
			case 4:
				finishDate.addAll(dateEn.list);
				adapter = new YjxCaseBaoanAdapter(this, finishDate);
				break;
			}
			adapter.notifyDataSetChanged();
			listView.onRefreshComplete();
		}
	}

	/** 解析接报案列表信息 */
	// tempDate, noDispatchDate , DispatchDate, finishDate
	private void getBaoanInfo(String value) {
		switch (status) {
		case 1:
			tempEntity = JSON.parseObject(value, YjxBaoanListEntity.class);// "暂存";
			if (tempEntity != null && tempEntity.list != null)
				if (tempDate == null)
					tempDate = new ArrayList<YjxCaseBaoanEntity>();
			addDate(tempEntity, tempPageNum);
			break;
		case 2:
			noDispatchEntity = JSON.parseObject(value, YjxBaoanListEntity.class);// "待调度";
			if (noDispatchEntity != null && noDispatchEntity.list != null)
				if (noDispatchDate == null)
					noDispatchDate = new ArrayList<YjxCaseBaoanEntity>();
			addDate(noDispatchEntity, noDispatchPageNum);
			break;
		case 3:
			DispatchEntity = JSON.parseObject(value, YjxBaoanListEntity.class);// "已调度";
			if (DispatchEntity != null && DispatchEntity.list != null)
				if (DispatchDate == null)
					DispatchDate = new ArrayList<YjxCaseBaoanEntity>();
			addDate(DispatchEntity, DispatchPageNum);
			break;
		case 4:
			finishEntity = JSON.parseObject(value, YjxBaoanListEntity.class);// "结案";
			if (finishEntity != null && finishEntity.list != null)
				if (finishDate == null)
					finishDate = new ArrayList<YjxCaseBaoanEntity>();
			addDate(finishEntity, finishPageNum);
			break;

		default:
			break;
		}
	}

	private void initView() {
		inflater = LayoutInflater.from(this);
		listView = (PullToRefreshListView) findViewById(R.id.yjx_diaodu_pull_refresh_list);
		listView.setMode(PullToRefreshBase.Mode.BOTH);
		listView.setEmptyView(inflater.inflate(R.layout.empty_view, null));

		ILoadingLayout startLabels = listView.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("正在载入...");// 刷新时
		startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
		// startLabels.setLastUpdatedLabel("上次刷新时间：刚刚");

		ILoadingLayout endLabels = listView.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("上拉加载...");// 刚上拉时，显示的提示
		endLabels.setRefreshingLabel("正在加载...");// 加载更多时
		endLabels.setReleaseLabel("加载更多...");// 上拉达到一定距离时，显示的提示
		// endLabels.setLastUpdatedLabel("上次刷新时间：刚刚");

		radgrup = (RadioGroup) findViewById(R.id.YJXBALA_BtnG);
		radgrup.setOnCheckedChangeListener(this);
		setOnclickPull();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setOnclickPull() {
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getBuyPullDown();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getBuyPullUp();
			}
		});
	}

	/** 上啦加载更多 */
	private void getBuyPullUp() {

		switch (status) { // tempEntity , noDispatchEntity , DispatchEntity,
							// finishEntity;
		// tempDate, noDispatchDate , DispatchDate, finishDate
		case 1:
			if (tempEntity != null && tempEntity.total > tempDate.size()) {
				tempPageNum = tempDate.size() / pageSize; // 设置当前页页码
				downLoadData(tempDate.size() / pageSize, "0");
			} else {
				hintNoMoreDate(); // 提示没有更多信息可以加载
			}
			break;

		case 2:
			if (noDispatchEntity.total > noDispatchDate.size()) {
				noDispatchPageNum = noDispatchDate.size() / pageSize; // 设置当前页页码
				downLoadData(noDispatchDate.size() / pageSize, "1");
			} else {
				hintNoMoreDate();// 提示没有更多信息可以加载
			}
			break;

		case 3:
			if (DispatchEntity.total > DispatchDate.size()) {
				DispatchPageNum = DispatchDate.size() / pageSize; // 设置当前页页码
				downLoadData(DispatchDate.size() / pageSize, "2,3");
			} else {
				hintNoMoreDate();// 提示没有更多信息可以加载
			}
			break;

		case 4:
			if (finishEntity.total > finishDate.size()) {
				finishPageNum = finishDate.size() / pageSize; // 设置当前页页码
				downLoadData(finishDate.size() / pageSize, "4");
			} else {
				hintNoMoreDate();// 提示没有更多信息可以加载
			}
			break;

		default:
			break;
		}
	}

	/** 在上拉提示中显示无更多信息的提示，并睡眠两秒后关闭提示 */
	public void hintNoMoreDate() {
		ILoadingLayout endLabels = listView.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("上拉加载...");// 刚上拉时，显示的提示
		endLabels.setRefreshingLabel("----我也是有底线的----");// 加载更多时
		endLabels.setReleaseLabel("没有更多...");// 上拉达到一定距离时，显示的提示
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ListHandler.sendEmptyMessage(0);
			}
		}, 2 * 1000);
	}

	@SuppressLint("HandlerLeak")
	private Handler ListHandler = new Handler() { // 关闭ListView刷新、加载提示信息！
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			listView.onRefreshComplete();
			ILoadingLayout endLabels = listView.getLoadingLayoutProxy(false, true);
			endLabels.setPullLabel("上拉加载...");// 刚上拉时，显示的提示
			endLabels.setRefreshingLabel("正在加载...");// 加载更多时
			endLabels.setReleaseLabel("加载更多...");// 上拉达到一定距离时，显示的提示
		}
	};

	/** 重新请求第一页数据，以刷新界面。-下拉刷新 */
	public void getBuyPullDown() { // tempEntity , noDispatchEntity ,
									// DispatchEntity, finishEntity;
		switch (status) {
		case 1:
			downLoadData(0, "0");
			if (tempEntity == null)
				tempEntity = new YjxBaoanListEntity();
			tempPageNum = 0; // 设置当前页页码 ，刷新后默认第一页
			break;

		case 2:
			downLoadData(0, "1");
			if (noDispatchEntity == null)
				noDispatchEntity = new YjxBaoanListEntity();
			noDispatchPageNum = 0; // 设置当前页页码 ，刷新后默认第一页
			break;

		case 3:
			downLoadData(0, "2,3");
			if (DispatchEntity == null)
				DispatchEntity = new YjxBaoanListEntity();
			DispatchPageNum = 0; // 设置当前页页码 ，刷新后默认第一页
			break;

		case 4:
			downLoadData(0, "4");
			if (finishEntity == null)
				finishEntity = new YjxBaoanListEntity();
			finishPageNum = 0; // 设置当前页页码 ，刷新后默认第一页
			break;

		default:
			break;
		}
	}

	private void initaction() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();
	}

	private void setAction() {
		actionTV2.setText("医健险接报案列表");
		actionTV3.setText("+新增");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				YjxTempStorageActivity.this.finish(); //
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String roleIds = AppApplication.getUSER().data.roleIds;
				if (roleIds.indexOf(URLs.getRoleId()+"")>-1) { 	//有接报案录入权限才能录入接报案
					startActivity(new Intent(YjxTempStorageActivity.this, YjxBaoanInputActivity.class));
				}else {
					DialogUtil.getErrDialog(YjxTempStorageActivity.this, "暂时没有医健险接报案录入权限！").show();
				}
			}
		});
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int buttonId) {
		// TODO Auto-generated method stub
		switch (buttonId) {
		case R.id.YJXBALA_btn_0: // 1暂存
			if (tempDate == null) { // 第一次进或没有数据，下载
				tempPageNum = 0; // 设置当前页页码 ，默认第一页
				downLoadData(0, "0");
			} else if (status != 1) { // 第二次进先判断是不是跳转到当前页
				adapter = new YjxCaseBaoanAdapter(this, tempDate);
				listView.setAdapter(adapter);
				listView.onRefreshComplete();
			}
			status = 1;
			break;

		case R.id.YJXBALA_btn_1: // ，2未调度
			if (noDispatchDate == null) { // 第一次进或没有数据，下载
				tempPageNum = 0; // 设置当前页页码 ，默认第一页
				downLoadData(0, "1");
			} else if (status != 2) { // 第二次进先判断是不是跳转到当前页
				adapter = new YjxCaseBaoanAdapter(this, noDispatchDate);
				listView.setAdapter(adapter);
				listView.onRefreshComplete();
			}
			status = 2;
			break;

		case R.id.YJXBALA_btn_2: // ，3已调度，
			if (DispatchDate == null) { // 第一次进或没有数据，下载
				tempPageNum = 0; // 设置当前页页码 ，默认第一页
				downLoadData(0, "2,3");
			} else if (status != 3) { // 第二次进先判断是不是跳转到当前页
				adapter = new YjxCaseBaoanAdapter(this, DispatchDate);
				listView.setAdapter(adapter);
				listView.onRefreshComplete();
			}
			status = 3;
			break;

		case R.id.YJXBALA_btn_3: // 4已结案
			if (finishDate == null) { // 第一次进或没有数据，下载
				tempPageNum = 0; // 设置当前页页码 ，默认第一页
				downLoadData(0, "4");
			} else if (status != 4) { // 第二次进先判断是不是跳转到当前页
				adapter = new YjxCaseBaoanAdapter(this, finishDate);
				listView.setAdapter(adapter);
				listView.onRefreshComplete();
			}
			status = 4;
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
