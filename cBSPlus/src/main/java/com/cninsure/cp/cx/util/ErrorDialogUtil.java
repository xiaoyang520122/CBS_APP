package com.cninsure.cp.cx.util;

import android.app.Activity;
import android.content.DialogInterface;

import com.cninsure.cp.utils.DialogUtil;

public class ErrorDialogUtil {

    /**提示错误后，并在关闭dialog的时候结束*/
    public static void  showErrorAndFinish(Activity context, String hintMsg) {
        DialogUtil.getErrDialogAndFinish(context, hintMsg, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                context.finish();
            }
        }).show();
    }
}
