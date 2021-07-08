package com.cninsure.cp.fragment.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.fragment.CxChoiceGGSTool;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.ToastUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :xy-wm
 * date:2021/6/30 10:33
 * usefuLness: CBS_APP
 */
public class CxOrderListMoreOperationTool {
    private Activity context;
    private Dialog dialog;  //显示操作类型的Dialog
    private CxChoiceGGSTool cxChoiceGGSTool; //转派时，选择转派公估师的工具类。
    private TextView refuseTv,transferTv,backTv,revokeTv; //拒绝，转派，退单，撤单。

    private CxOrderListMoreOperationTool(){}

    public CxOrderListMoreOperationTool(Activity context){
        this.context = context;
    }

    public void showOperationDialog(PublicOrderEntity orderEn,CxChoiceGGSTool cxChoiceGGSTool){
        this.cxChoiceGGSTool = cxChoiceGGSTool;
        dialog = DialogUtil.getDialogByViewOneButton(context,getCheckView(orderEn),"选择操作类型",null);
        dialog.show();
    }


   private View getCheckView(PublicOrderEntity orderEn){
       LinearLayout groupView = new LinearLayout(context);
       groupView.setOrientation(LinearLayout.VERTICAL);
       refuseTv = (TextView) context.getLayoutInflater().inflate(R.layout.simple_list_item_1,null);
       transferTv = (TextView) context.getLayoutInflater().inflate(R.layout.simple_list_item_1,null);
       backTv = (TextView) context.getLayoutInflater().inflate(R.layout.simple_list_item_1,null);
       revokeTv = (TextView) context.getLayoutInflater().inflate(R.layout.simple_list_item_1,null);

       refuseTv.setText("拒绝");
       transferTv.setText("转派");
       backTv.setText("退单");
       revokeTv.setText("撤单");

       refuseTv.setGravity(Gravity.CENTER);
       transferTv.setGravity(Gravity.CENTER);
       backTv.setGravity(Gravity.CENTER);
       revokeTv.setGravity(Gravity.CENTER);

       groupView.addView(refuseTv);
       groupView.addView(transferTv);
       groupView.addView(backTv);
       groupView.addView(revokeTv);

       //拒绝
       refuseTv.setOnClickListener(v -> cancelOrder(orderEn));  //拒绝操作
       transferTv.setOnClickListener(v -> cxChoiceGGSTool.showChoiceDialog(orderEn.id + "")); //转派操作
       backTv.setOnClickListener(v -> showBackAlert(orderEn)); //退单操作
       revokeTv.setOnClickListener(v -> showRevokeAlert(orderEn)); //撤单操作
       setVisiblity(orderEn);
       return groupView;
   }

    /**
     * 根据状态显示或者隐藏
     * @param orderEn
     */
    private void setVisiblity(PublicOrderEntity orderEn) {
        switch (orderEn.status){
            case 2: //待调度，只能拒绝、转派、撤单
                backTv.setVisibility(View.GONE); //隐藏退单选项
                revokeTv.setVisibility(View.GONE); //隐藏撤单选项
                break;

            case 4: //已接单，只能退单、转派、撤单
                refuseTv.setVisibility(View.GONE); //隐藏拒绝选项
                break;

            case 6: //作业中，只能转派、撤单
                refuseTv.setVisibility(View.GONE); //隐藏拒绝选项
                backTv.setVisibility(View.GONE); //隐藏退单选项
                break;

            case 10: //作业审核退回，只能转派、撤单
                refuseTv.setVisibility(View.GONE); //隐藏拒绝选项
                backTv.setVisibility(View.GONE); //隐藏退单选项
                break;
        }
    }


    Integer revokeTypeId = null;
    String revokeType = null;
    String revokeDesc = null;
    private  EditText revokeDescEdit;
    /**
     * 弹框选择撤单理由
     * @param orderEn
     *
     *   { value: '0', label: '未勘现场、私了销案' },
     *     { value: '1', label: '交警扣车' },
     *     { value: '2', label: '交警处理、现场未勘' },
     *     { value: '3', label: '无需修复' },
     *     { value: '4', label: '无损失' },
     *     { value: '5', label: '其他' }
     */
    private void showRevokeAlert(PublicOrderEntity orderEn) {
        revokeTypeId = null;
        revokeType = null;
        Dialog dialog = new AlertDialog.Builder(context).setTitle("请填写撤单理由")
                .setView(getSubmitRevokeView())
                .setNegativeButton("取消",null)
                .setNeutralButton("确定", (dialog12, which) -> submitRevoke(orderEn)).create();
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 获取撤单填写内容界面
     * @return
     */
    private View getSubmitRevokeView() {
        View view  = context.getLayoutInflater().inflate(R.layout.submit_revoke_dialog_layout,null);
        RadioGroup group = view.findViewById(R.id.revoke_radioGroup);
        revokeDescEdit = view.findViewById(R.id.revoke_revokeDesc);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                revokeDescEdit.setVisibility(View.GONE);
                revokeDescEdit.setText("");
                switch (checkedId){
                    case R.id.revoke_radiobutton_id0 : revokeTypeId = 0; revokeType="未勘现场、私了销案"; break;
                    case R.id.revoke_radiobutton_id1 : revokeTypeId = 1; revokeType="交警扣车"; break;
                    case R.id.revoke_radiobutton_id2 : revokeTypeId = 2; revokeType="交警处理、现场未勘"; break;
                    case R.id.revoke_radiobutton_id3 : revokeTypeId = 3; revokeType="无需修复"; break;
                    case R.id.revoke_radiobutton_id4 : revokeTypeId = 4; revokeType="无损失"; break;
                    case R.id.revoke_radiobutton_id5 : revokeTypeId = 5; revokeType="其他";
                        revokeDescEdit.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });
        return view;
    }

    /**
     * 撤单
     */
    private void submitRevoke(PublicOrderEntity orderEn) {
        if (dialog!=null) dialog.dismiss();
        revokeDesc = revokeDescEdit.getText().toString();
        if (revokeTypeId == 5 && TextUtils.isEmpty(revokeDesc)) {
            DialogUtil.getErrDialog(context,"未填写其他原因！").show();
            return;
        }
        if (revokeTypeId==null || TextUtils.isEmpty(revokeType)){
            DialogUtil.getErrDialog(context,"请先选择退回原因").show();
            return;
        }
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        params.add(new BasicNameValuePair("id", orderEn.id + ""));
        params.add(new BasicNameValuePair("revokeTypeId", revokeTypeId + ""));
        params.add(new BasicNameValuePair("revokeType", revokeType));
        if (revokeTypeId == 5) params.add(new BasicNameValuePair("revokeDesc", revokeDesc));
        HttpUtils.requestPost(URLs.CX_POST_REVOKE, params, HttpRequestTool.CX_POST_REVOKE);
        LoadDialogUtil.setMessageAndShow(context,"取消中……");
    }


    private void showBackAlert(PublicOrderEntity orderEn){
       DialogUtil.getAlertOnelistener(context, "确认退单吗？",
               (dialog, which) -> {
                   chargeBackOrder(orderEn);
               }).show();
   }
    /**
     * 公估师退单
     * @param orderEn
     */
    private void chargeBackOrder(PublicOrderEntity orderEn) {
        if (dialog!=null) dialog.dismiss();
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        params.add(new BasicNameValuePair("id", orderEn.id + ""));
        HttpUtils.requestPost(URLs.CX_POST_CHARGE_BACK, params, HttpRequestTool.CX_POST_CHARGE_BACK);
        LoadDialogUtil.setMessageAndShow(context,"取消中……");
    }

    /**
     * 拒绝订单
     * @param orderEn
     */
    private void cancelOrder(PublicOrderEntity orderEn) {
        if (dialog!=null) dialog.dismiss();
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        params.add(new BasicNameValuePair("id", orderEn.id + ""));
        HttpUtils.requestPost(URLs.CancelOrder(), params, HttpRequestTool.CANCEL_ORDER);
        LoadDialogUtil.setMessageAndShow(context,"取消中……");
    }

    /**
     * 提交转派
     */
    private void submitTransfer() {
        if (dialog!=null) dialog.dismiss();
        List<NameValuePair> paramsList = new ArrayList<>(6);
        paramsList.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        paramsList.add(new BasicNameValuePair("id", cxChoiceGGSTool.orderId));
        paramsList.add(new BasicNameValuePair("ggsUid", cxChoiceGGSTool.choiceGGS.userId));
        HttpUtils.requestPost(URLs.CX_ORDER_TRANSFER, paramsList, HttpRequestTool.CX_ORDER_TRANSFER);
        LoadDialogUtil.setMessageAndShow(context, "加载中……");
    }
}
