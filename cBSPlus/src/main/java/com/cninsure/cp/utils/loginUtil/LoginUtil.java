package com.cninsure.cp.utils.loginUtil;

import android.app.Activity;
import android.content.Intent;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BuildConfig;
import com.cninsure.cp.IndexActivity;
import com.cninsure.cp.LoginActivity;
import com.cninsure.cp.dispersive.DispersiveUserActivity;

/**
 * @author :xy-wm
 * date:2020/11/24 9:55
 * usefuLness: CBS_APP
 */
public class LoginUtil {

    private Activity activity;

    private LoginUtil(){}
    public LoginUtil(Activity activity){
        this.activity = activity;
    }

    public void jumpActivity(String value) {
        if (isDispersiveUser()){//分散型外部车童登录，跳转到外部车童界面
            Intent intent=new Intent(activity, DispersiveUserActivity.class);
            activity.startActivity(intent);
        }else{  //非分散型外部车童
            Intent intent=new Intent(activity, IndexActivity.class);
            intent.putExtra("loginvalue", value);
            activity.startActivity(intent);
        }
    }

    /**
     * 如果是分散型公估师，跳转到分散型
     * @return
     */
    private boolean isDispersiveUser(){
        if (AppApplication.getUSER().data!=null && AppApplication.getUSER().data.roleIds!=null
                && AppApplication.getUSER().data.roleIds.indexOf(BuildConfig.FSX_USER_TYPE)>-1){
            return true;
        }else{
            return false;
        }
    }
}
