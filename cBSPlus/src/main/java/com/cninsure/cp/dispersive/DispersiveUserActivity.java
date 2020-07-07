package com.cninsure.cp.dispersive;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cninsure.cp.entity.PushType;
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
import com.cninsure.cp.utils.permission_util.FloatingWindowPermissionUtil;
import com.cninsure.cp.utils.permission_util.PermissionApplicationUtil;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DispersiveUserActivity extends BaseActivity implements View.OnClickListener {

    private TextView ordersearchTV, ordernowTV, WaitTv;
    private String oderStatus = "2,11";  //请求案件状态
    private int choiceMenu = 1;
    private List<String> paramsList; //请求参数
    private DispersiveDispatchEntity ddtEn; //案件返回数据
    private List<DispersiveDispatchEntity> newDisptchLists;//每个菜单对应的最后一次通过接口获取的数据
    private List<DispersiveDispatchEntity.DispersiveDispatchItem> DisptchListsNew,DisptchListsWork,DisptchListsEnd;//每个菜单对应的最后一次通过接口获取的数据
    private List<List<DispersiveDispatchEntity.DispersiveDispatchItem>> disDisData;  //调度信息列表
    private PullToRefreshListView dispatchListView;
    private List<DispersiveOrderAdapter> dDadapter;
    public static DispersiveUserActivity instence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispersive_user_activity_view);
        instence = this;
        EventBus.getDefault().register(this);
        initView();
        setUserCenterTvOnclick();
        getDefaulList(); //加载空数据
        getDefaulData(0); //默认重第一条开始查询
        new PermissionApplicationUtil(this); //申请读写权限和拍照权限
        FloatingWindowPermissionUtil.isAppOps(this);  //悬浮弹框权限检查
    }

    private void getDefaulList(){
        dDadapter = new ArrayList<>();
        disDisData = new ArrayList<>(3);
        newDisptchLists = new ArrayList<>(3);
        DisptchListsNew = new ArrayList<>(10);
        DisptchListsWork = new ArrayList<>(10);
        DisptchListsEnd = new ArrayList<>(10);

        newDisptchLists.add(null);
        newDisptchLists.add(null);
        newDisptchLists.add(null);
        disDisData.add(DisptchListsNew);
        disDisData.add(DisptchListsWork);
        disDisData.add(DisptchListsEnd);
        dDadapter.add(new DispersiveOrderAdapter(this,DisptchListsNew));
        dDadapter.add(new DispersiveOrderAdapter(this,DisptchListsWork));
        dDadapter.add(new DispersiveOrderAdapter(this,DisptchListsEnd));

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
        if (AppApplication.getUSER()==null || AppApplication.getUSER().data==null){  //用户信息为空重新登录。
            startActivity(new Intent(this, LoadingActivity.class));
            this.finish();
        }
        UserInfoUtil.USERIsNull(this);
        paramsList.add(AppApplication.getUSER().data.userId);
        paramsList.add("size");
        paramsList.add("5");
        paramsList.add("start");
        paramsList.add(startNum+"");
        paramsList.add("statusArr");
        paramsList.add(oderStatus);
        paramsList.add("ggsId");
        paramsList.add(AppApplication.getUSER().data.userId);
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
                dispatchListView.onRefreshComplete();
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

    /**分散型新订单透传悬浮弹框提示用户*/
    @Subscribe(threadMode=ThreadMode.MAIN)
    public void eventThing(NameValuePair value){
        String type=value.getName();
        if (type.equals(PushType.FSX_NEW_ORDER)) {
            onClickDo(WaitTv,R.drawable.waitcase,"1",0);
            getDefaulData(0); //默认重第一条开始查询
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
        if(ddtEn!=null && ddtEn.success==true && ddtEn.data!=null && ddtEn.data.list!=null){ //获取调度列表信息成功后，将任务放到对应集合中
            if (ddtEn.data.startRow==1){ //如果请求的是第一页的数据，直接替换之前的内容
                disDisData.get(choiceMenu).clear();
                disDisData.get(choiceMenu).addAll(ddtEn.data.list);
                dispatchListView.setAdapter(dDadapter.get(choiceMenu));
            }else{  //如果不是第一页数据就累加到集合中
                disDisData.get(choiceMenu).addAll(ddtEn.data.list);
                dDadapter.get(choiceMenu).notifyDataSetChanged();
            }
            newDisptchLists.set(choiceMenu,ddtEn); //保存对应最后一次请求数据
//            dispatchListView.setAdapter(new DispersiveOrderAdapter(this,disDisData.get(choiceMenu)));
        }else{
            ToastUtil.showToastLong(this,"未获取到调度任务信息！");
        }
    }

    private void initView() {
        ordersearchTV =  findViewById(R.id.dispersive_orderList_tv);
        ordernowTV = findViewById(R.id.dispersive_orderNow_tv);
        WaitTv = findViewById(R.id.dispersive_localSave_tv);
        initListView();
        ordersearchTV.setOnClickListener(this);
        ordernowTV.setOnClickListener(this);
        WaitTv.setOnClickListener(this);
    }

    @SuppressLint("ResourceType")
    private void initListView(){
        dispatchListView = findViewById(R.id.dispersive_Dispatch_order_listView);
        dispatchListView.setEmptyView(findViewById(R.layout.empty_dispatch_view));
        dispatchListView.setMode(PullToRefreshBase.Mode.BOTH);
        ILoadingLayout startLabels = dispatchListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示
        // startLabels.setLastUpdatedLabel("上次刷新时间：刚刚");

        ILoadingLayout endLabels = dispatchListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载...");// 刚上拉时，显示的提示
        endLabels.setRefreshingLabel("正在加载...");// 加载更多时
        endLabels.setReleaseLabel("加载更多...");// 上拉达到一定距离时，显示的提示
        setOnRefreshOnclick(); //设置下拉和上拉监听
    }

    /**设置下拉刷新，上哪加载更多*/
    private void setOnRefreshOnclick() {
        dispatchListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) { //下拉刷新
                getDefaulData(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) { //上拉加载更多
                DispersiveDispatchEntity tempData = newDisptchLists.get(choiceMenu);
                if (tempData!=null && tempData.data.total>disDisData.get(choiceMenu).size()){
                    int tempPageNum = disDisData.get(choiceMenu).size() / tempData.data.pageSize; // 设置当前页页码
                    getDefaulData(tempPageNum);
                }else{
                    hintNoMoreDate(); // 提示没有更多信息可以加载
                }
            }
        });
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

    /**
     * 点击待接单时触发的事件
     * @param tv
     * @param resousId
     * @param Status 状态
     * @param chMenu 选择菜单位置编码
     */
    private void onClickDo(TextView tv,int resousId,String Status,int chMenu){
        recoverView(tv, resousId);
        oderStatus = Status;
        choiceMenu = chMenu;
        showData();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dispersive_localSave_tv:
                onClickDo(WaitTv,R.drawable.waitcase,"1",0);
                break;
            case R.id.dispersive_orderNow_tv:
                onClickDo(ordernowTV,R.drawable.ordernow,"2,5,11",1);
                break;
            case R.id.dispersive_orderList_tv:
                onClickDo(ordersearchTV,R.drawable.searchorder,"3,4,6,9",2);
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

    /** 在上拉提示中显示无更多信息的提示，并睡眠两秒后关闭提示 */
    public void hintNoMoreDate() {
        ILoadingLayout endLabels = dispatchListView.getLoadingLayoutProxy(false, true);
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
            dispatchListView.onRefreshComplete();
            ILoadingLayout endLabels = dispatchListView.getLoadingLayoutProxy(false, true);
            endLabels.setPullLabel("上拉加载...");// 刚上拉时，显示的提示
            endLabels.setRefreshingLabel("正在加载...");// 加载更多时
            endLabels.setReleaseLabel("加载更多...");// 上拉达到一定距离时，显示的提示
        }
    };
}
