package com.cninsure.cp.utils.permission_util;


import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Binder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;

import java.lang.reflect.Method;

/**
 *悬浮窗口权限判断，没有就提示用户赋权，有就不提示。
 * 个别手机已有权限依然提示没有的可以点击已设置，后续不在提示。
 */
public class FloatingWindowPermissionUtil {

    private static PermissionPageUtils permissionUtil;
    private static AlertDialog.Builder builder ;

    /**
     * 判断 悬浮窗口权限是否打开 */
    public static void isAppOps(Context context) {
        permissionUtil=new PermissionPageUtils(context);
        // 但这个方法也不能保证正确，一些机型上会返回错误即MODE_ERRORED，就是获取不到权限值，这个方法就返回了false，但实际上悬浮窗是可以使用的。
        boolean isedit= AppApplication.sp.getBoolean("MODE_ERRORED_edit", true);
        if (!getAppOps(context) && isedit) {
            builder = new AlertDialog.Builder(context);
            builder.setView(getShowView(context))
                    .setNeutralButton("前往配置", null);
            setDissmis(builder).show();
        }
    }

    private static View getShowView(Context context) {
        View view= LayoutInflater.from(context).inflate(R.layout.permission_setting_alert_view, null);
        ((CheckBox)view.findViewById(R.id.permission_alert_view_checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                SharedPreferences.Editor editor=AppApplication.sp.edit();
                if (isChecked) {
                    editor.putBoolean("MODE_ERRORED_edit", false);
                }else {
                    editor.putBoolean("MODE_ERRORED_edit", true);
                }
                editor.commit();
                editor.clear();
            }
        });
        return view;
    }

    private static AlertDialog setDissmis(AlertDialog.Builder builder) {
        AlertDialog alertDialog=builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                try {
                    permissionUtil.jumpPermissionPage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return alertDialog;
    }

    /**
     * 判断 悬浮窗口权限是否打开  但这个方法也不能保证正确，一些机型上会返回错误即MODE_ERRORED，就是获取不到权限值，这个方法就返回了false，但实际上悬浮窗是可以使用的。
     * @param context
     * @return true 允许  false禁止
     */
    public static boolean getAppOps(Context context) {
        try {
            Object object = context.getSystemService("appops");
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = context.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {

        }
        return false;
    }

}
