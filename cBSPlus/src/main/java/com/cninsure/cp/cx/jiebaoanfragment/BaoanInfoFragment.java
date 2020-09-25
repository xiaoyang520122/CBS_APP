package com.cninsure.cp.cx.jiebaoanfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.cx.util.ErrorDialogUtil;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxOrderEntity;
import com.cninsure.cp.entity.cx.EntrusterShortNameCxTable;
import com.cninsure.cp.entity.cx.JieBaoanEntity;
import com.cninsure.cp.utils.CallUtils;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.GetOrederStatus;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PopupWindowUtils;
import com.cninsure.cp.utils.SetTextUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class BaoanInfoFragment extends BaseFragment {

    private View contentView ,footerView;
    private LayoutInflater inflater;
//    private MyAdapter myAdapter;
    private CxJieBaoanInfoActivity activity;
    private MyOrderListAdapter adapter;

    private JieBaoanEntity baoanInfo; //接报案信息
    private EntrusterShortNameCxTable wtShotEntity; //接报案信息
    private List<CxOrderEntity.CxOrderTable> orderList; //接报案对应任务列表

    @ViewInject(R.id.CxBaFgmt_workRequirements)  TextView workRequirements;  //作业要求
    @ViewInject(R.id.CxBaFgmt_caseBaoanNo)  TextView caseBaoanNo;  //报案号
    @ViewInject(R.id.CxBaFgmt_caseBaoanNo_copy)  TextView caseBaoanNoCopy;  //复制报案号
    @ViewInject(R.id.CxBaFgmt_wtDate)  TextView wtDate;  //委托时间
    @ViewInject(R.id.CxBaFgmt_caseLifecycle)  TextView caseLifecycle;  //委托信息
    @ViewInject(R.id.CxBaFgmt_listTitle)  TextView orderListTitle;  //任务列表ListView标题
    @ViewInject(R.id.CxBaFgmt_list)  ListView orderListView;  //任务列表ListView


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_baoan_info_fragment,null);
        activity = (CxJieBaoanInfoActivity) getActivity();
        ViewUtils.inject(this,contentView);
        downloadBaoanInfo();
        initView();

        return contentView;
    }

    private void initView() {
        workRequirements.setOnClickListener(new View.OnClickListener() {  //查看作业要求
            @Override
            public void onClick(View arg0) {
                PopupWindowUtils.showPopupWindow(getpopView(wtShotEntity.workRequirements), workRequirements, activity);
            }
        });
    }

    private View getpopView(final String caseLifecycle) {
        LinearLayout addView=(LinearLayout) LayoutInflater.from(activity) .inflate(R.layout.popupwindow_text, null);
        TextView view= addView.findViewById(R.id.popupwindow_text);
        view.setText(caseLifecycle);
        view.setHint("无信息！");
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                CopyUtils.copy(activity, caseLifecycle);
                return false;
            }
        });
        return addView;
    }

    /**下载报案信息*/
    private void downloadBaoanInfo() {
        LoadDialogUtil.setMessageAndShow(getActivity(),"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("userId");
        params.add(AppApplication.getUSER().data.userId);
        params.add("uid");
        params.add(activity.orderInfoEn.caseBaoanUid);
        HttpUtils.requestGet(URLs.CX_JIE_BAOAN_INFO, params, HttpRequestTool.CX_JIE_BAOAN_INFO);
    }
    /**下载委托人简称信息，获取作业要求*/
    private void downloadShortWtNameInfo() {
        LoadDialogUtil.setMessageAndShow(getActivity(),"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("uid");
        params.add(baoanInfo.data.wtShortNameId);
        HttpUtils.requestGet(URLs.CX_GET_WT_SHORT_INFO, params, HttpRequestTool.CX_GET_WT_SHORT_INFO);
    }
    /**根据报案编号查询订单信息*/
    private void downloadOrderList() {
        LoadDialogUtil.setMessageAndShow(getActivity(),"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("baoanUid");
        params.add(baoanInfo.data.uid);
        params.add("userId");
        params.add(AppApplication.getUSER().data.userId);
        HttpUtils.requestGet(URLs.CX_GET_ORDER_LIST_BY_BAOAN_UID, params, HttpRequestTool.CX_GET_ORDER_LIST_BY_BAOAN_UID);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnet(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_JIE_BAOAN_INFO: //获取订单信息
                LoadDialogUtil.dismissDialog();
                getJieBaoanInfo(values.get(0).getValue());
                break;
            case HttpRequestTool.CX_GET_WT_SHORT_INFO: //获取委托人简称信息
                LoadDialogUtil.dismissDialog();
                getShortNameInfo(values.get(0).getValue());
                break;
            case HttpRequestTool.CX_GET_ORDER_LIST_BY_BAOAN_UID: //获取接报案下面的调度任务信息
                LoadDialogUtil.dismissDialog();
                getOrderListInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    /**
     * 获取接报案下面的调度任务信息 并显示
     * @param value
     */
    private void getOrderListInfo(String value) {
        try {
            orderList = JSON.parseArray(value,CxOrderEntity.CxOrderTable.class);
        }catch (Exception e){
            orderList = new ArrayList<>();
            e.printStackTrace();
        }
        adapter = new MyOrderListAdapter();
        orderListView.setAdapter(adapter);
    }

    /**
     * 解析委托人简称
     * @param value
     */
    private void getShortNameInfo(String value) {
        wtShotEntity = JSON.parseObject(value,EntrusterShortNameCxTable.class);
    }

    private void getJieBaoanInfo(String value) {
        try {
            baoanInfo = JSON.parseObject(value,JieBaoanEntity.class);
        }catch (Exception e){
            ErrorDialogUtil.showErrorAndFinish(activity,"获取任务信息失败，请联系管理员！");
            e.printStackTrace();
        }
        if (baoanInfo!=null){
            downloadShortWtNameInfo();
            downloadOrderList();
            displayBaoanInfo();
        }
    }

    /**显示头部接报案信息*/
    private void displayBaoanInfo() {
        if (baoanInfo!=null && baoanInfo.data!=null){
            SetTextUtil.setTextViewText(caseBaoanNo,baoanInfo.data.caseBaoanNo);  //报案号
            SetTextUtil.setTextViewText(wtDate,baoanInfo.data.wtDate);  //委托时间
            SetTextUtil.setTextViewText(caseLifecycle,activity.orderInfoEn.caseLifecycle);  //委托信息
            CopyUtils.setCopyOnclickListener(activity,caseBaoanNo,baoanInfo.data.caseBaoanNo);  //复制报案号
        }
    }

    @Override
    public void SaveDataToEntity() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public class MyOrderListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (orderList!=null) return orderList.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (orderList!=null) return orderList.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.cx_baoan_info_fragment_item,null);
            ViewHolder vHolder=null;
            vHolder=new ViewHolder();
            ViewUtils.inject(vHolder, convertView);
            CxOrderEntity.CxOrderTable itemData = orderList.get(position);

            SetTextUtil.setTextViewText(vHolder.bussType,itemData.bussType);  //作业类型
            String statuss= GetOrederStatus.fromStatuId(Integer.valueOf(itemData.status));
            SetTextUtil.setTextViewText(vHolder.status,statuss);  // 状态
            SetTextUtil.setTextViewText(vHolder.caseBaoanNo,itemData.caseBaoanNo);  // 报案号
           CopyUtils.setCopyOnclickListener(activity,vHolder.caseBaoanNoCopy,itemData.caseBaoanNo);  // 报案号复制
            SetTextUtil.setTextViewText(vHolder.wtDate,itemData.wtDate);  //委托时间
            SetTextUtil.setTextViewText(vHolder. biaodiCarNo,itemData.biaodiCarNo);  // 标的车牌号
            SetTextUtil.setTextViewText(vHolder.baoanPerson,itemData.baoanPerson);  // 报案人
            SetTextUtil.setTextViewText(vHolder.baoanPersonMobile,itemData.baoanPersonMobile);  //报案人电话
            vHolder. baoanPersonMobileCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallUtils.call(activity,itemData.baoanPersonMobile);
                }
            });  // 拨打报案人电话
            SetTextUtil.setTextViewText(vHolder.caseAddress,itemData.caseAddress);  // 出险地址
            return convertView;
        }
    }

    public class ViewHolder{
        @ViewInject(R.id.CxBaFgmtItm_bussType) TextView bussType;  //作业类型
        @ViewInject(R.id.CxBaFgmtItm_status) TextView status;  // 状态
        @ViewInject(R.id.CxBaFgmtItm_caseBaoanNo) TextView caseBaoanNo;  // 报案号
        @ViewInject(R.id.CxBaFgmtItm_caseBaoanNo_copy) TextView caseBaoanNoCopy;  // 报案号
        @ViewInject(R.id.CxBaFgmtItm_wtDate) TextView wtDate;  //委托时间
        @ViewInject(R.id.CxBaFgmtItm_biaodiCarNo) TextView biaodiCarNo;  // 标的车牌号
        @ViewInject(R.id.CxBaFgmtItm_baoanPerson) TextView baoanPerson;  // 报案人
        @ViewInject(R.id.CxBaFgmtItm_baoanPersonMobile) TextView baoanPersonMobile;  //报案人电话
        @ViewInject(R.id.CxBaFgmtItm_baoanPersonMobile_call) TextView baoanPersonMobileCall;  // 拨打报案人电话
        @ViewInject(R.id.CxBaFgmtItm_caseAddress) TextView caseAddress;  // 出险地址
    }
}
