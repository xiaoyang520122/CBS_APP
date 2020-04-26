package com.cninsure.cp.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MyPullRefreshListViewAlertUtil {

	private static PullToRefreshListView mPullRefreshListView;
	
	/**
	 *  在上拉提示中显示无更多信息的提示，并睡眠两秒后关闭提示 
	 * @param mPullRefreshListView 
	 * @param AlertMsg //没有更多数据时的提示信息
	 * @param alertTime 提示时间（毫秒为单位）
	 */
	public static void setAlertInfo(PullToRefreshListView PRfListView,String AlertMsg,int alertTime) {
		mPullRefreshListView = PRfListView;
		ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("上拉加载...");// 刚上拉时，显示的提示
		endLabels.setRefreshingLabel(AlertMsg);// 加载更多时
		endLabels.setReleaseLabel("加载更多...");// 上拉达到一定距离时，显示的提示
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ListHandler.sendEmptyMessage(0);
			}
		}, alertTime);
	}
	
	@SuppressLint("HandlerLeak")
	private static Handler ListHandler = new Handler() { // 关闭ListView刷新、加载提示信息！
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mPullRefreshListView.onRefreshComplete();
			ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
			endLabels.setPullLabel("上拉加载...");// 刚上拉时，显示的提示
			endLabels.setRefreshingLabel("正在加载...");// 加载更多时
			endLabels.setReleaseLabel("加载更多...");// 上拉达到一定距离时，显示的提示
		}
	};
	
	
}
