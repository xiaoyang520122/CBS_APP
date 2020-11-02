package com.cninsure.cp.activty;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.ExtUserEtity;
import com.cninsure.cp.utils.BankLogoManage;
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

public class AccountActivity extends BaseActivity implements View.OnClickListener {

    /**公估师账户信息**/
    private ExtUserEtity extUserEtity;
    @ViewInject(R.id.account_MSGCENTER_Back) private TextView backTv;  //返回按钮
    @ViewInject(R.id.account_Cardlogo) private ImageView CardLogo; //银行卡图片
    @ViewInject(R.id.account_car_name) private TextView CardName; //银行名称
    @ViewInject(R.id.account_banc_card_no) private TextView bankCardNo; //银行卡号
    @ViewInject(R.id.account_bancCard_info_layout) private LinearLayout CardLayout; //银行卡布局
    @ViewInject(R.id.account_Amount) private TextView totalAmount; //资产总额
    @ViewInject(R.id.account_can_extract_amount) private TextView canExtAmount; //可提现额度
    @ViewInject(R.id.account_baozheng_amount) private TextView baozhengAmount; //保证金
    @ViewInject(R.id.account_Extract_lineLayout) private LinearLayout extractLayout; //提现操作布局
    @ViewInject(R.id.account_extract_history_lineLayout) private LinearLayout extractHistryLayout; //提现历史布局


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        extUser();
    }

    /**
     * 获取公估师信息
     */
    private void extUser() {
        List<String> httpParams = new ArrayList<>();
        httpParams.add("userId");
        httpParams.add(AppApplication.USER.data.userId);
        HttpUtils.requestGet(URLs.CX_EXT_USER, httpParams, HttpRequestTool.CX_EXT_USER);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventFun(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_EXT_USER: //
                LoadDialogUtil.dismissDialog();
                analysisExtUserInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    private void analysisExtUserInfo(String value) {
        extUserEtity = JSON.parseObject(value,ExtUserEtity.class);
        disPlayViewInfo();
    }

    /**显示信息**/
    private void disPlayViewInfo() {
        backTv.setOnClickListener(this);  //返回按钮
        extractLayout.setOnClickListener(this); //提现操作布局
        extractHistryLayout.setOnClickListener(this); //提现历史布局
        CardLayout.setOnClickListener(this); //银行卡布局
        if (extUserEtity==null || extUserEtity.data==null) return;
        disPlayCardLogo(); //银行卡图片,银行名称
        if (extUserEtity.data.accountTotalAmount!=null) totalAmount.setText(extUserEtity.data.accountTotalAmount.toString());//资产总额
        if (extUserEtity.data.canPosAmount!=null) canExtAmount.setText(extUserEtity.data.canPosAmount.toString());//可提现额度
        if (extUserEtity.data.bondAmount!=null) baozhengAmount.setText(extUserEtity.data.bondAmount.toString());//保证金
    }

    /**
     * 显示银行卡图片,银行名称
     */
    private void disPlayCardLogo() {
        if (!TextUtils.isEmpty(extUserEtity.data.bankName)){
            CardName.setText(extUserEtity.data.bankName); //银行名称
            List<NameValuePair> bankLitleLogos= BankLogoManage.getbanklitleLogo();
            for (int i = 0; i < bankLitleLogos.size(); i++) {
                if (extUserEtity.data.bankName.indexOf(bankLitleLogos.get(i).getValue())>-1) {
                    int resid=Integer.valueOf(bankLitleLogos.get(i).getName());
                    CardLogo.setImageResource(resid); //银行卡图片
                    return;
                }
            }
        }
        displayBankCarNo(); //显示银行卡号
    }

    /**
     * 显示银行卡号
     */
    private void displayBankCarNo(){
        String bnumber=extUserEtity.data.bankNo;
        if (!TextUtils.isEmpty(bnumber)){
            String bn= "**** **** **** "+bnumber.substring(bnumber.length()-4);
            bankCardNo.setText(bn);  //银行卡号
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.account_MSGCENTER_Back: this.finish();break; //退出
            case R.id.account_bancCard_info_layout: startActivity(new Intent(this,BankCardActivity.class));break; //银行卡详情
            case R.id.account_Extract_lineLayout: startActivity(new Intent(this,BankCardActivity.class));break; //提现操作

            default:
        }
    }
}
