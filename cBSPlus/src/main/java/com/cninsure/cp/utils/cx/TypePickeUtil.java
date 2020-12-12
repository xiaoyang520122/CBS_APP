package com.cninsure.cp.utils.cx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.DictData;

import java.util.List;

public class TypePickeUtil {

    /**获取显示可选择内容供选择，并在在选择后赋值到对应的TextView**/
    public static void setTypePickerDialog(final Context context, final TextView textTv, CxDictEntity dictData,String type) {
        textTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View arg0) {
                String tempArr[] = getDictLabelArr(dictData.getDictByType(type));
                showTypePickerDialog(context,textTv,tempArr);
            }
        });
    }
    /**获取显示可选择内容供选择，并在在选择后赋值到对应的TextView**/
    public static void setTypePickerDialog(final Context context, final TextView textTv, String[] tempArr) {
        textTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View arg0) {
                showTypePickerDialog(context,textTv,tempArr);
            }
        });
    }

    public static void showTypePickerDialog(final Context context, final TextView textTv, String[] tempArr){
        new AlertDialog.Builder(context).setTitle("请选择：")
                .setItems(tempArr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textTv.setText(tempArr[which]);
                    }
                }).create().show();
    }

    public static String[] getDictLabelArr(List<DictData> dictData){
        String[] tempArr = new String[dictData.size()];
        for (int i=0;i<dictData.size();i++){
            tempArr[i] = dictData.get(i).label;
        }
        return tempArr;
    }

    /**
     * 通过 label 名称获取字典库中，该类型对应的 value
     * @param labelStr
     * @param dictData
     * @param type
     * @return
     */
    public static Integer getValue(String labelStr, CxDictEntity dictData,String type){
        String tempArr[] = getDictLabelArr(dictData.getDictByType(type));
        for (int i= 0;i<tempArr.length;i++){
            if (!TextUtils.isEmpty(labelStr) && labelStr.equals(tempArr[i]))
            return i;
        }
            return -1;
    }
}

