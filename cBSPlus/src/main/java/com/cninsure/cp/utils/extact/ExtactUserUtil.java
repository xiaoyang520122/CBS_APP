package com.cninsure.cp.utils.extact;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.activty.register.SignAgmentActivity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.extract.ExtUserEtity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :xy-wm
 * date:2020/11/24 10:50
 * usefuLness: CBS_APP
 */
public class ExtactUserUtil {

    public ExtUserEtity extUserEtity;

    /**
     * 获取外部车童信息
     * @param context
     * @param isShowDialog  是否显示加载中的提示框。
     * HttpRequestTool.CX_EXT_USER
     */
    public void getUserInfo(Context context,boolean isShowDialog) {
        List<String> httpParams = new ArrayList<>();
        httpParams.add("userId");
        httpParams.add(AppApplication.USER.data.userId);
        HttpUtils.requestGet(URLs.CX_EXT_USER, httpParams, HttpRequestTool.CX_EXT_USER);
       if (isShowDialog) LoadDialogUtil.setMessageAndShow(context,"努力加载中……");
    }

    /**
     * 如果是外部车童就下载车童信息
     * @param context
     * @param isShowDialog
     */
    public void isExtactGetInfo(Context context,boolean isShowDialog){
        if ("99".equals(AppApplication.getUSER().data.userType)){  //"99"是外部车童
            getUserInfo(context,isShowDialog);
        }
    }

    /**
     * 如果外部车童没有签署协议，则提示，并强制跳转。
     * @param value
     */
    public void isSignble(Activity activity,String value) {
        extUserEtity = JSON.parseObject(value, ExtUserEtity.class);
       if (extUserEtity!=null && extUserEtity.data!=null && extUserEtity.data.status!=null && extUserEtity.data.status==0){ //签约状态不等于1或者没有签约状态
        showSignAlert(activity,extUserEtity);
       }else{

       }
    }

    /**
     * 提示用户签约，否则无法继续使用
     * @param activity
     * @param extUserEtity
     */
    private void showSignAlert(Activity activity, ExtUserEtity extUserEtity) {
        DialogUtil.getErrDialogAndFinish(activity, "查勘员未签约，需要先完善资料并签约才能继续操作！", dialog -> {
//            Intent intent = new Intent();
//            intent.setClass(activity, SignAgmentActivity.class);
//            intent.putExtra("extUserEtity",extUserEtity);
//            activity.startActivity(intent);
            jumpToSignView(activity);
        }).show();
    }

    public void jumpToSignView(Activity activity){
        Intent intent = new Intent();
        intent.setClass(activity, SignAgmentActivity.class);
        intent.putExtra("extUserEtity",extUserEtity);
        activity.startActivity(intent);
    }

}
