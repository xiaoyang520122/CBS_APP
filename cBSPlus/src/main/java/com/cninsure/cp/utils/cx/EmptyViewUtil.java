package com.cninsure.cp.utils.cx;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * @author :xy-wm
 * date:2020/11/12 15:14
 * usefuLness: CBS_APP
 */
public class EmptyViewUtil {

    /**
     * 盲僧版本空提示
     * @param context
     * @param listView
     */
    public void SetEmptyView(Context context, ListView listView){
        View emptyView = LayoutInflater.from(context).inflate(R.layout.empty_view,null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }

    /**
     *
     * @param context
     * @param listView
     */
    public void SetDispatchEmptyView(Context context, ListView listView){
        View emptyView = LayoutInflater.from(context).inflate(R.layout.empty_dispatch_view,null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }

    /**
     *
     * @param context
     * @param listView
     */
    public void SetDispatchEmptyView(Context context, PullToRefreshListView listView){
        View emptyView = LayoutInflater.from(context).inflate(R.layout.empty_dispatch_view,null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }

    public void SetInjuredEmptyView(Context context, ListView listView){
        View emptyView = LayoutInflater.from(context).inflate(R.layout.yjx_injured_empty,null);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        emptyView.setVisibility(View.GONE);
        ((ViewGroup)listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
    }
}
