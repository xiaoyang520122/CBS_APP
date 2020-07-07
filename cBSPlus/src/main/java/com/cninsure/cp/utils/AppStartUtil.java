package com.cninsure.cp.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AppStartUtil {

    /**
     * 启动一个app
     */
    public void startAPP(Context context){
        try{
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.cninsure.cp");
            context.startActivity(intent);
        }catch(Exception e){
            Toast.makeText(context, "没有安装", Toast.LENGTH_LONG).show();
        }
    }
}
