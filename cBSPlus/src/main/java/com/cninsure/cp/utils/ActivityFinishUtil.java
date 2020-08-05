package com.cninsure.cp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;


public class ActivityFinishUtil {

    /** 提示用户是否真的要退出该界面，避免勿退出！ **/
    public static void showFinishAlert(Activity context) {
        Dialog dialog = DialogUtil.getAlertOnelistener(context, "确定要退出该页面吗！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                context.finish();
            }
        });
        dialog.show();
    }
}
