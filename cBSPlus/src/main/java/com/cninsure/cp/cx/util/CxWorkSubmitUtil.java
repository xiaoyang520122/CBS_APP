package com.cninsure.cp.cx.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CxWorkSubmitUtil {

    /**
     * 弹框选择是1保存还是0暂存
     * @param activity
     * @param onClickListener
     */
    public static void showSaveDialog(Activity activity, DialogInterface.OnClickListener onClickListener) {

        new AlertDialog.Builder(activity).setTitle("请选择")
                .setItems(new String[]{"保存", "提交审核"}, onClickListener)
                .setNeutralButton("取消", null).create().show();
    }

    /**作业暂存或提交审核*/
    public static void submit(Activity activity,int status, String QorderUid, String contentJson, Long id) {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        paramsList.add(new BasicNameValuePair("orderUid", QorderUid));  //订单uid
        paramsList.add(new BasicNameValuePair("content", contentJson));  //作业内容，保存为JSON对象
        paramsList.add(new BasicNameValuePair("status", status + ""));  //0：暂存；1：提交（送审）
        if (id != null && id > 0)
            paramsList.add(new BasicNameValuePair("id", id+""));  //作业id
        HttpUtils.requestPost(URLs.CX_NEW_WORK_SAVE, paramsList, HttpRequestTool.CX_NEW_WORK_SAVE);
        LoadDialogUtil.setMessageAndShow(activity, "处理中……");
    }
}
