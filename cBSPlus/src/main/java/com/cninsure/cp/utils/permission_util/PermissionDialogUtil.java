package com.cninsure.cp.utils.permission_util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by YuShuangPing on 2018/11/5.
 */
public class PermissionDialogUtil {
    /**
     * 创建一个选择对话框
     *
     * @param context
     * @param pContent            提示消息
     * @param dialogClickListener 点击监听
     * @return
     */
    public static Dialog showSelectDialog(Context context, String title, String pContent, String pLeftBtnStr,
                                          String pRightBtnStr,
                                          final DialogClickListener dialogClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setTitle(title)
                .setMessage(pContent)
                .setPositiveButton(pRightBtnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogClickListener.confirm();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(pLeftBtnStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogClickListener.cancel();
                        dialog.dismiss();
//                        return;
                    }
                })
                .create();
        return dialog;
    }


    public interface DialogClickListener {

        public abstract void confirm();

        public abstract void cancel();

    }
}
