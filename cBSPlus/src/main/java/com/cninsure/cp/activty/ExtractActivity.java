package com.cninsure.cp.activty;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.ExtUserEtity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ExtractActivity extends BaseActivity implements View.OnClickListener {
    /**公估师账户信息**/
    private ExtUserEtity extUserEtity;
    private Double submitExtAmount;  //选择订单后的累计提现额度
    private String orderIds; //申请提现的任务id集合
    @ViewInject(R.id.extract_act_Back) private TextView backTv; //退出
    @ViewInject(R.id.extract_Amount) private TextView canAmountTv; //可提现金额
    @ViewInject(R.id.extract_yugu_Amount) private TextView yuguAmountTv;  //预估金额
    @ViewInject(R.id.extract_can_layout) private LinearLayout extractLayout;  //提现布局
    @ViewInject(R.id.extract_submit_btn) private Button extractSubmitButton;  //提交提现按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extract_activity);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        getData();
    }

    /**获取公估师账户信息*/
    private void getData() {
        extUserEtity = (ExtUserEtity) getIntent().getSerializableExtra("extUserEtity");
        intitView();
    }

    private void intitView() {
        if (extUserEtity!=null && extUserEtity.data!=null && extUserEtity.data.canPosAmount!=null)
            canAmountTv.setText("￥"+extUserEtity.data.canPosAmount.toString());//可提现额度
        backTv.setOnClickListener(this);
        extractLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.extract_can_layout: startActivityForResult(new Intent(this,ChoiceExtractOrderActivity.class), HttpRequestTool.CHOICE_EXTRACT_ORDER_REQUEST);break;
            case R.id.extract_submit_btn: submitApply();break;//提交申请
            case R.id.extract_act_Back: this.finish();break;//退出

            default:
        }
    }

    /**
     * 提交申请
     */
    private void submitApply() {
        if (submitExtAmount!=null && submitExtAmount>0 && !TextUtils.isEmpty(orderIds)){
            if (!(submitExtAmount>100) || !(submitExtAmount<10000)) {
                DialogUtil.getAlertOneButton(this,"提现金额需要大于100，小于10000！",null).show();
            }else if (false){ //本周有提现，不能再提现

            }else{
                posSubmit();
            }
        }else{
            DialogUtil.getAlertOneButton(this,"未选择订单！",null).show();
        }
    }

    /**调用提现申请提交提现任务*/
    private void posSubmit(){
        List<NameValuePair> paramsList = new ArrayList<>(6);
        paramsList.add(new BasicNameValuePair("userId",AppApplication.getUSER().data.userId));
        paramsList.add(new BasicNameValuePair("orderIds",orderIds));
        HttpUtils.requestPost(URLs.POS_APPLY_SUBMIT, paramsList, HttpRequestTool.POS_APPLY_SUBMIT);
        LoadDialogUtil.setMessageAndShow(this,"加载中……");
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventFun(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.POS_APPLY_SUBMIT:
                LoadDialogUtil.dismissDialog();
                analysisInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    private void analysisInfo(String value) {
        BaseEntity ben = JSON.parseObject(value,BaseEntity.class);
        if (ben!=null){
            if (ben.success){
                DialogUtil.getAlertOneButton(this,"提交成功-"+ben.msg,null).show();
                this.finish();
            }else{
                DialogUtil.getAlertOneButton(this,"提交失败-"+ben.msg,null).show();
            }
        }else{
            DialogUtil.getAlertOneButton(this,"提交失败！！"+value,null).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) return;
        submitExtAmount = data.getDoubleExtra("choiceExtractAmount",-1);
        orderIds = data.getStringExtra("orderIds");
        if (HttpRequestTool.CHOICE_EXTRACT_ORDER_REQUEST == requestCode && submitExtAmount>-1 && !TextUtils.isEmpty(orderIds)){
            yuguAmountTv.setText("￥"+submitExtAmount.toString());
            setSubmitOnclickL();
        }
    }

    /**
     *
     */
    private void setSubmitOnclickL() {
        if (submitExtAmount>0) {  //有提现额度，点亮按钮，可提交申请
            extractSubmitButton.setBackgroundResource(R.drawable.corners_bule_4_30dp);
            extractSubmitButton.setOnClickListener(this);
        }else{ //无提现额度，置灰按钮，不能提交。
            extractSubmitButton.setBackgroundResource(R.drawable.corners_hui_4_30dp);
            extractSubmitButton.setOnClickListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
