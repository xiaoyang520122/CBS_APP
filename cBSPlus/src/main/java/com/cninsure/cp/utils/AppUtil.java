package com.cninsure.cp.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class AppUtil {


    /**
     * 应用是否在前台运行
     * @return true：在前台运行；false：已经被切到后台了
     */

    //在进程中去寻找当前APP的信息，判断是否在前台运行
    public boolean isAppOnForeground(Context context) {
        ActivityManager activityManager =(ActivityManager) context.getApplicationContext().getSystemService(
                Context.ACTIVITY_SERVICE);
        String packageName =context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

}
