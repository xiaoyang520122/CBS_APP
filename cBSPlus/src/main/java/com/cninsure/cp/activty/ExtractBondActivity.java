package com.cninsure.cp.activty;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.extract.ExtUserEtity;
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

public class ExtractBondActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 公估师账户信息
     **/
    private ExtUserEtity extUserEtity;
    private Double submitExtAmount;  //选择订单后的累计提现额度
    private String orderIds; //申请提现的任务id集合
    @ViewInject(R.id.extract_bond_Back)
    private TextView backTv; //退出
    @ViewInject(R.id.extract_bondAmount)
    private TextView canAmountTv; //可提现金额
    @ViewInject(R.id.extract_bondyugu_Amount)
    private TextView yuguAmountTv;  //预估金额
    @ViewInject(R.id.extract_bondcan_history_Tv)
    private TextView extractTv;  //提现
    @ViewInject(R.id.extract_bond_submit_btn)
    private Button extractSubmitButton;  //提交提现按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extract_bond_activity);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        getData();
    }

    /**
     * 获取公估师账户信息
     */
    private void getData() {
        extUserEtity = (ExtUserEtity) getIntent().getSerializableExtra("extUserEtity");
        intitView();
        setSubmitOnclickL();
    }

    private void intitView() {
        if (extUserEtity != null && extUserEtity.data != null && extUserEtity.data.bondAmount != null) {
            canAmountTv.setText("￥" + extUserEtity.data.bondAmount.toString());//可提现保证金额度
            yuguAmountTv.setText("￥" + extUserEtity.data.bondAmount.toString());//可提现保证金额度
        }
        backTv.setOnClickListener(this);
        extractTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.extract_bondcan_history_Tv:
                startActivity(new Intent(this, ExtBondHistoryActivity.class));
                break;
            case R.id.extract_bond_submit_btn:
                submitApply();
                break;//提交申请
            case R.id.extract_bond_Back:
                this.finish();
                break;//退出

            default:
        }
    }

    /**
     * 提交申请
     */
    private void submitApply() {
        if (extUserEtity.data.posStatus == null || extUserEtity.data.posStatus != 2) {
            DialogUtil.getAlertOneButton(this, "不可提取！", null).show();
            return;
        }
        if (extUserEtity != null && extUserEtity.data != null && extUserEtity.data.bondAmount == 0){
            DialogUtil.getAlertOneButton(this, "无可提现额度！", null).show();
            return;
        }
        if (extUserEtity != null && extUserEtity.data != null && extUserEtity.data.bondAmount != null &&
            extUserEtity.data.posStatus != null && extUserEtity.data.posStatus == 2) {
             submitHint();
        } else {
            DialogUtil.getAlertOneButton(this, "不能提现！", null).show();
        }
    }

    /**提示用户是否确认提现！*/
    private void submitHint() {
        DialogUtil.getAlertOnelistener(this, "确认要提取保证金吗？", (dialog, which) -> {
            posSubmit();
        }).show();
    }

    /**
     * 调用提现申请提交提现任务
     */
    private void posSubmit() {
        List<NameValuePair> paramsList = new ArrayList<>(6);
        paramsList.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        paramsList.add(new BasicNameValuePair("posBondType", "2"));
        paramsList.add(new BasicNameValuePair("posAmount", extUserEtity.data.bondAmount+"")); //提取金额
        paramsList.add(new BasicNameValuePair("ggsUid", AppApplication.getUSER().data.userId));
        HttpUtils.requestPost(URLs.POS_BOND_HISTORY, paramsList, HttpRequestTool.POS_BOND_HISTORY);
        LoadDialogUtil.setMessageAndShow(this, "加载中……");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventFun(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.POS_BOND_HISTORY:
                LoadDialogUtil.dismissDialog();
                analysisInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    private void analysisInfo(String value) {
        BaseEntity ben = JSON.parseObject(value, BaseEntity.class);
        Dialog dialog = null;
        if (ben != null) {
            if (ben.success) {
                dialog = DialogUtil.getAlertOneButton(this, "提交成功-" + ben.msg, null);
            } else {
                DialogUtil.getAlertOneButton(this, "提交失败-" + ben.msg, null).show();
            }
        } else {
            DialogUtil.getAlertOneButton(this, "提交失败！！" + value, null).show();
        }
//提交成功就关闭当前界面。
        if (dialog != null) {
            dialog.setOnDismissListener(dialog1 -> {
                ExtractBondActivity.this.finish();
            });
        }
        dialog.show();
    }


    /**
     *
     */
    private void setSubmitOnclickL() {
        if (extUserEtity != null && extUserEtity.data != null && extUserEtity.data.bondAmount!=null && extUserEtity.data.bondAmount > 0) {  //有提现额度，点亮按钮，可提交申请
            extractSubmitButton.setBackgroundResource(R.drawable.corners_bule_4_30dp);
            extractSubmitButton.setOnClickListener(this);
        } else { //无提现额度，置灰按钮，不能提交。
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
