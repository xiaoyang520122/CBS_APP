package com.cninsure.cp.dispersive;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.LoadingActivity;
import com.cninsure.cp.LoginActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.dispersive.DispersiveDispatchEntity;
import com.cninsure.cp.entity.dispersive.ResPonseEntity;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.UserInfoUtil;
import com.cninsure.cp.utils.permission_util.PermissionApplicationUtil;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DispersiveUserActivity extends BaseActivity implements View.OnClickListener {

    private TextView ordersearchTV, ordernowTV, WaitTv;
    private String oderStatus = "2,11";  //请求案件状态
    private int choiceMenu = 1;
    private List<String> paramsList; //请求参数
    private DispersiveDispatchEntity ddtEn; //案件返回数据
    private List<List<DispersiveDispatchEntity.DispersiveDispatchItem>> disDisData;  //掉地信息列表
    private ListView dispatchListView;
    private DispersiveOrderAdapter dDadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispersive_user_activity_view);
        EventBus.getDefault().register(this);
        initView();
        setUserCenterTvOnclick();
        getDefaulList(); //加载空数据
        getDefaulData(0); //默认重第一条开始查询
        new PermissionApplicationUtil(this); //申请读写权限和拍照权限
    }

    private void getDefaulList(){
        disDisData = new ArrayList<>(3);
        for (int i=0;i<3;i++){
            disDisData.add(null);
        }
    }

    private void setUserCenterTvOnclick(){
        findViewById(R.id.dispersive_activity_V_LTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //外部车童登录，跳转到外部车童界面
                Intent intent=new Intent(DispersiveUserActivity.this, CtCenterActivity.class);
                DispersiveUserActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserInfoUtil.USERIsNull(this);  //判断用户信息是否为空
    }

    /**获取分散型调度列表*/
    private void getDefaulData(int startNum) {
        paramsList = new ArrayList<String>(5);
        paramsList.add("userId");
        if (AppApplication.USER==null || AppApplication.USER.data==null){  //用户信息为空重新登录。
            startActivity(new Intent(this, LoadingActivity.class));
            this.finish();
        }
        paramsList.add(AppApplication.USER.data.userId);
        paramsList.add("size");
        paramsList.add("10");
        paramsList.add("start");
        paramsList.add(startNum+"");
        paramsList.add("statusArr");
        paramsList.add(oderStatus);
        paramsList.add("ggsId");
        paramsList.add(AppApplication.USER.data.userId);
        HttpUtils.requestGet(URLs.FSX_GGS_ORDER_LIST, paramsList, HttpRequestTool.FSX_GGS_ORDER_LIST);
        LoadDialogUtil.setMessageAndShow(this, "请稍后……");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dispersiveEvent(List<NameValuePair> value) {
        int rcode = Integer.valueOf(value.get(0).getName());
        if (rcode == HttpRequestTool.FSX_GGS_ORDER_LIST  || rcode == HttpRequestTool.FSX_GGS_ACCEPT_ORDER
                || rcode == HttpRequestTool.FSX_GGS_CANCEL_ORDER  || rcode == HttpRequestTool.FSX_GGS_SAVE_ORDER ) {
            LoadDialogUtil.dismissDialog();
        }
        switch (CheckHttpResult.checkList(value, this)) {
            case HttpRequestTool.FSX_GGS_ORDER_LIST:  //获取订单
                ddtEn = JSON.parseObject(value.get(0).getValue(), DispersiveDispatchEntity.class);
                disPlayData();
                break;
            case HttpRequestTool.FSX_GGS_ACCEPT_ORDER:  //退回或者接受订单
                responseMsg(value.get(0).getValue());
                break;
            case HttpRequestTool.FSX_GGS_CANCEL_ORDER:  //取消任务
                responseMsg(value.get(0).getValue());
                break;
            case HttpRequestTool.FSX_GGS_SAVE_ORDER:  //到达现场
                responseMsg(value.get(0).getValue());
                break;
            default:
                break;
        }
    }

    /**退回或者接受订单请求返回数据解析和提示用户*/
    private void responseMsg(String value) {  //{"data":null,"msg":"任务已接受","success":true}
        try {
            ResPonseEntity acceptRespon = JSON.parseObject(value,ResPonseEntity.class);
            if (acceptRespon!=null && !TextUtils.isEmpty(acceptRespon.msg)){
                getAcceptResponse(acceptRespon.msg).show();
            }else{
                getAcceptResponse("请求响应信息解析失败！").show();
            }
        }catch (Exception e){  //解些错误，提示用户请求失败
            getAcceptResponse("请求响应信息解析失败！").show();
        }
    }

    private Dialog getAcceptResponse(String msg){
        Dialog dialog = DialogUtil.getAlertOneButton(this, msg,null);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getDefaulData(0);
            }
        });
        return dialog;
    }

    /**
     * 显示调度任务列表信息
     */
    private void disPlayData() {
        //disDisData
        if(ddtEn!=null && ddtEn.success==true && ddtEn.data!=null && ddtEn.data.list!=null){
            disDisData.set(choiceMenu,ddtEn.data.list);
            dispatchListView.setAdapter(new DispersiveOrderAdapter(this,disDisData.get(choiceMenu)));
        }else{
            ToastUtil.showToastLong(this,"为获取到调度任务信息！");
        }
    }

    @SuppressLint("ResourceType")
    private void initView() {
        ordersearchTV =  findViewById(R.id.dispersive_orderList_tv);
        ordernowTV = findViewById(R.id.dispersive_orderNow_tv);
        WaitTv = findViewById(R.id.dispersive_localSave_tv);
        dispatchListView = findViewById(R.id.dispersive_Dispatch_order_listView);
        dispatchListView.setEmptyView(findViewById(R.layout.empty_dispatch_view));

        ordersearchTV.setOnClickListener(this);
        ordernowTV.setOnClickListener(this);
        WaitTv.setOnClickListener(this);
    }


    private void recoverView(TextView tv, int drawadbleId) {
        ordersearchTV.setTextColor(this.getResources().getColor(R.color.hui_text_h));
        ordernowTV.setTextColor(this.getResources().getColor(R.color.hui_text_h));
        WaitTv.setTextColor(this.getResources().getColor(R.color.hui_text_h));

        ordersearchTV.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.searchorder_hui), null, null);
        ordernowTV.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.ordernow_hui), null, null);
        WaitTv.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.waitcase_hui), null, null);

        tv.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(drawadbleId), null, null);
        tv.setTextColor(this.getResources().getColor(R.color.bule_text_h));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dispersive_localSave_tv:
                recoverView(WaitTv, R.drawable.waitcase);
                oderStatus = "1";
                choiceMenu = 0;
                showData();
                break;
            case R.id.dispersive_orderNow_tv:
                recoverView(ordernowTV, R.drawable.ordernow);
                oderStatus = "2,5,11";
                choiceMenu = 1;
                showData();
                break;
            case R.id.dispersive_orderList_tv:
                recoverView(ordersearchTV, R.drawable.searchorder);
                oderStatus = "3,4,6,9";
                choiceMenu = 2;
                showData();
                break;

            default:
                break;
        }
    }

    /**显示选中状态的调度任务，如果没有尝试下载*/
    private void showData() {
       if (disDisData.get(choiceMenu)==null || disDisData.get(choiceMenu).size()==0){
           dispatchListView.setAdapter(new DispersiveOrderAdapter(this, new ArrayList<DispersiveDispatchEntity.DispersiveDispatchItem>()));
           getDefaulData(0); //没有数据尝试现在新数据
       }else{
           dispatchListView.setAdapter(new DispersiveOrderAdapter(this,disDisData.get(choiceMenu)));
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
