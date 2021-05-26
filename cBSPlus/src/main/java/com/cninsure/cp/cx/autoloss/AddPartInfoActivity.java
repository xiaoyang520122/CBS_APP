package com.cninsure.cp.cx.autoloss;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDsWorkEntity;
import com.cninsure.cp.utils.DialogUtil;
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

/**
 * @author :xy-wm
 * date:2021/5/25 20:06
 * usefuLness: CBS_APP
 */
public class AddPartInfoActivity extends BaseActivity {

    @ViewInject(R.id.cxdsAddPart_listView) private ListView listView;

    private View footerView;
    private Button addButton;

    private CxDsWorkEntity workEntity; //智能定损信息。
    private List<CxDsWorkEntity.CxDsReplaceInfos> replaceInfoList; //换件项目


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_ds_add_parts_info);
        EventBus.getDefault().register(this);
        ViewUtils.inject(this);
        workEntity = (CxDsWorkEntity) getIntent().getSerializableExtra("CxDsWorkEntity");
        replaceInfoList = new ArrayList<>();
        initFooterView();
    }

    private void initFooterView() {
        footerView = LayoutInflater.from(this).inflate(R.layout.add_layout,null);
        addButton = footerView.findViewById(R.id.add_Layout_button);
        listView.addFooterView(footerView);
        if (workEntity!=null && workEntity.replaceInfos!=null){
            replaceInfoList.addAll(workEntity.replaceInfos);
        };
    }

    /**获取厂家信息*/
    private void downloadInfo(String cateName) {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> paramlist = new ArrayList<String>();
        HttpUtils.requestGet(URLs.CX_GET_CAR_PEIJIAN_LIST, paramlist, HttpRequestTool.CX_GET_CAR_PEIJIAN_LIST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnet(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_GET_CAR_FACTORY_LIST:
                LoadDialogUtil.dismissDialog();
                getPeijianInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    private void getPeijianInfo(String value) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
