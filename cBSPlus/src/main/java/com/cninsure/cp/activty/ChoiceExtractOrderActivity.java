package com.cninsure.cp.activty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.adapter.ChoiceExtrOrderAdapter;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxOrderEntity;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.cx.EmptyViewUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zcw.togglebutton.ToggleButton;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ChoiceExtractOrderActivity extends BaseActivity {
    @ViewInject(R.id.extract_chioce_ListView) private ListView listView; //可提现列表
    @ViewInject(R.id.extract_chioce_all_button) private ToggleButton cAllToggleBut; //全选
    @ViewInject(R.id.extract_chioce_amount) private TextView choiceAmountTv; //已选金额
    @ViewInject(R.id.extract_chioce_submit) private TextView submitBut; //确定按钮
    @ViewInject(R.id.extract_chioce_Back) private TextView backTv; //退回按钮

    private CxOrderEntity orderList; //可提现订单列表
    private ChoiceExtrOrderAdapter adapter; //提现清单适配器
    private double amount = 0; //累计选择提现金额



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_extract_order_activity);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        getOrderList();
        new EmptyViewUtil().SetEmptyView(this,listView);
        setSubmitOnclick();
    }

    /**
     * 确认选择提现
     */
    private void setSubmitOnclick() {
        submitBut.setOnClickListener(v -> {
            String orderIds = "";
            if (adapter!=null && adapter.checkMap!=null)
            for(Long key : adapter.checkMap.keySet()){
                if (!TextUtils.isEmpty(orderIds)) orderIds += (","+key);  //拼接id字符串
                else orderIds += (""+key);
            }
            Intent intent = new Intent();
            // 获取用户计算后的结果
            intent.putExtra("orderIds", orderIds); //申请的任务清单id
            intent.putExtra("choiceExtractAmount", amount); //选择的清单提现金额
            setResult(2, intent);
            finish(); //结束当前的activity的生命周期
        });
        backTv.setOnClickListener(v -> { //返回
            this.finish();
        });
    }

    /**
     * posStatusArr 提现状态；
     * public static final int PREPARE = 1;//可提现
     * public static final int APPLY = 2;//发起提现
     * public static final int PASS = 3;//审核通过
     * public static final int ADOPT = 4;//审核退回
     * public static final int FAIL = 5;//支付失败
     * public static final int SUCCESS = 6;//支付成功
     * public static final int CANT = 9;//不能提现(自有公估师不能提现)
     */
    private void getOrderList(){
        List<String> paramsList = new ArrayList<>(6);
        paramsList.add("userId");
        paramsList.add(AppApplication.getUSER().data.userId);
        paramsList.add("ggsUid");
        paramsList.add(AppApplication.getUSER().data.userId);
        paramsList.add("start");
        paramsList.add("0");
        paramsList.add("size");
        paramsList.add("10000");
        paramsList.add("statusArr");
        paramsList.add("9");
        paramsList.add("posStatusArr");
        paramsList.add("1,5");
        HttpUtils.requestGet(URLs.CX_NEW_GET_GGS_ORDER, paramsList, HttpRequestTool.CX_NEW_GET_GGS_ORDER);
        LoadDialogUtil.setMessageAndShow(this,"加载中……");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(List<NameValuePair> values){
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_NEW_GET_GGS_ORDER:
                LoadDialogUtil.dismissDialog();
                jiexiDate(values.get(0).getValue());
                break;

            default:
                break;
        }
    }

    private void jiexiDate(String value) {
        orderList = JSON.parseObject(value, CxOrderEntity.class);
        if (orderList!=null && orderList.list!=null){
            adapter = new ChoiceExtrOrderAdapter(orderList.list,this, () -> {
                setDataChange();
            });
            listView.setAdapter(adapter);
            //设置全选
            setChoiceAllOnclick();
        }
    }

    /**
     * 更新选择变化
     */
    private void setDataChange() {
        amount = 0;
        for (CxOrderEntity.CxOrderTable tempTable:orderList.list){
           if (adapter.checkMap.get(Long.valueOf(tempTable.id))!=null && tempTable.canPosAmount!=null){
               amount += tempTable.canPosAmount;
           }
        }
        choiceAmountTv.setText(amount+"");
    }

    private void setChoiceAllOnclick() {
        cAllToggleBut.setOnToggleChanged(on -> {
            adapter.choiceAll(on);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
