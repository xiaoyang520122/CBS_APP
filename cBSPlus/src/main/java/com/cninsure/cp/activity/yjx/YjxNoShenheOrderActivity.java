package com.cninsure.cp.activity.yjx;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.yjx.YjxCaseDispatchTable;
import com.cninsure.cp.entity.yjx.YjxOrderListEntity;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class YjxNoShenheOrderActivity extends BaseActivity {
	
	private TextView actionTV1, actionTV2, actionTV3; // 顶部返回按钮，标题和暂存按钮。
	private LayoutInflater inflater;
	private PullToRefreshListView listView;
	/**待审核列表*/
	private List<YjxCaseDispatchTable> noShenheList;
	/**每次请求的待审核订单存到这个对象里面*/
	private YjxOrderListEntity tempOrderEn;
	/**适配器*/
	private YjxNoShenheListAdapter disAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yjx_no_sh_order_activity);
		EventBus.getDefault().register(this);
		initaction();
		initView();
		downOrder(0);
	}

	private void downOrder(int start) {
		LoadDialogUtil.setMessageAndShow(YjxNoShenheOrderActivity.this, "加载中……");
		List<String> params = new ArrayList<String>();
		params.add("size");
		params.add("10");

		params.add("start");
		params.add(start + "");

		params.add("userId");
		params.add(AppApplication.USER.data.userId);
		HttpUtils.requestGet(URLs.YJX_SHENHE_ORDER_LIST, params, HttpRequestTool.YJX_SHENHE_ORDER_LIST);
	}

	private void initaction() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();
	}

	private void setAction() {
		actionTV2.setText("医健险待审核列表");
		actionTV3.setText("");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				YjxNoShenheOrderActivity.this.finish(); //
			}
		});
	}
	
	private void initView() {
		inflater = LayoutInflater.from(this);
		listView = (PullToRefreshListView) findViewById(R.id.yjx_noShOr_pull_refresh_list);
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
		setOnclickPull();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setOnclickPull() {
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { //下拉刷新
				downOrder(0);
			}
			
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { //加载更多
				if (tempOrderEn != null && tempOrderEn.total > noShenheList.size()) {
					int tempPageNum = noShenheList.size() / tempOrderEn.pageSize; // 设置当前页页码
					downOrder(tempPageNum);
				} else {
					hintNoMoreDate(); // 提示没有更多信息可以加载
				}
			}
		});
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
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventBusR(List<NameValuePair> values) {
		int rcode = Integer.valueOf(values.get(0).getName());
		switch (rcode) {
		case HttpRequestTool.YJX_SHENHE_ORDER_LIST:
			LoadDialogUtil.dismissDialog();
			getDisPatchData(values.get(0).getValue());
			break;

		default:
			break;
		}
	}
	
	/**获取接报案信息*/
	private void getDisPatchData(String value) {
		tempOrderEn = JSON.parseObject(value, YjxOrderListEntity.class);
		if (tempOrderEn==null || tempOrderEn.list==null ) {
			
		}else if ((disAdapter == null && noShenheList != null) || disAdapter == null) { // 第一处加载数据（fatherDate==null是第一次有数据加载）不判断是刷新还是加载更多
			noShenheList = new ArrayList<YjxCaseDispatchTable>();
			for (int i = 0; i < tempOrderEn.list.size(); i++) {
				noShenheList.add(tempOrderEn.list.get(i));
			}
			disAdapter = new YjxNoShenheListAdapter(this, noShenheList);
			listView.setAdapter(disAdapter);
		} else if (tempOrderEn != null && tempOrderEn.pageNum == 0) { // myPageNum==0是刷新，清空老数据，添加新数据
			noShenheList = new ArrayList<YjxCaseDispatchTable>();
			for (int i = 0; i < tempOrderEn.list.size(); i++) {
				noShenheList.add(tempOrderEn.list.get(i));
			}
			disAdapter = new YjxNoShenheListAdapter(this, noShenheList);
			listView.setAdapter(disAdapter);
		}else {
			for (int i = 0; i < tempOrderEn.list.size(); i++) {
				noShenheList.add(tempOrderEn.list.get(i));
			}
		}
		disAdapter.notifyDataSetChanged();
		listView.onRefreshComplete();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
