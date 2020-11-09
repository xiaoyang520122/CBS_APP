package com.cninsure.cp.activty;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.adapter.ExtractHistoryAdapter;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.extract.CxExtApplyListEntity;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ExtractHistryActivity extends BaseActivity {

    @ViewInject(R.id.extract_history_Back) private TextView backTv;  //返回按钮
    @ViewInject(R.id.extract_history_ListView)private ListView listView; //申请列表

    private CxExtApplyListEntity historyList;  //申请记录
    private ExtractHistoryAdapter adapter; //适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extract_history_activity);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        initView();
        getOrderList();
    }

    private void initView() {
        backTv.setOnClickListener(v -> {
         this.finish();
        });
    }

    private void getOrderList(){
        List<String> paramsList = new ArrayList<>(6);
        paramsList.add("userId");
        paramsList.add(AppApplication.getUSER().data.userId);
        paramsList.add("start");
        paramsList.add("0");
        paramsList.add("size");
        paramsList.add("10000");
//        paramsList.add("m");
//        paramsList.add("2020-10");
        HttpUtils.requestGet(URLs.POS_APPLY_HISTORY, paramsList, HttpRequestTool.POS_APPLY_HISTORY);
        LoadDialogUtil.setMessageAndShow(this,"加载中……");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventFun(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.POS_APPLY_HISTORY: //
                LoadDialogUtil.dismissDialog();
                analysisHistoryInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    /**
     * 解析并显示用户信息
     * @param value
     */
    private void analysisHistoryInfo(String value) {
        historyList = JSON.parseObject(value,CxExtApplyListEntity.class);
        if (historyList!=null && historyList.list!=null ){
            adapter = new ExtractHistoryAdapter(historyList.list,this);
            listView.setAdapter(adapter);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
