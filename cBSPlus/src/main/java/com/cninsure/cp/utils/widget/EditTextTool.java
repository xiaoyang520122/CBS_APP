package com.cninsure.cp.utils.widget;

import android.widget.EditText;
import android.widget.TextView;

/**
 * @author :xy-wm
 * date:2021/5/16 13:15
 * usefuLness: CBS_APP
 */
public class EditTextTool {
    //不可编辑状态
    public static void noEidt(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }
    //可编辑状态
    public static void okEidt(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        editText.requestFocus();
    }
}
