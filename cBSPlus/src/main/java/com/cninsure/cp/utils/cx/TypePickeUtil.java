package com.cninsure.cp.utils.cx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cninsure.cp.cx.CxSurveyWorkActivity;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.fragment.OrderNowFragment;

import java.util.ArrayList;
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
    public static void setTypePickerDialogByValus(final Context context, final TextView textTv, CxDictEntity dictData,String type,String values) {
        textTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View arg0) {
                String tempArr[] = getDictLabelArrByValues(dictData.getDictByType(type),values);
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

    public static void showTypePickerDialogByOnclick(final Context context, final TextView textTv, String[] tempArr){
       textTv.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               showTypePickerDialog(context,textTv, tempArr);
           }
       });
    }

    public static String[] getDictLabelArr(List<DictData> dictData){
        String[] tempArr = new String[dictData.size()];
        for (int i=0;i<dictData.size();i++){
            tempArr[i] = dictData.get(i).label;
        }
        return tempArr;
    }

    public static String[] getDictLabelArrByValues(List<DictData> dictData,String values){
        List<String> temLpist = new ArrayList<>();
        for (int i=0;i<dictData.size();i++){
           if(values.contains(dictData.get(i).value)){
               temLpist.add(dictData.get(i).label);
           }
        }
        String[] tempArr = temLpist.toArray(new String[temLpist.size()]);
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

    /**
     * 通过 label 名称获取字典库中，该类型对应的 value
     * @param labelStr
     * @param dictData
     * @param type
     * @return
     */
    public static String getValueByLabel(String labelStr, CxDictEntity dictData,String type){
        for (DictData dict:dictData.list){
            if (!TextUtils.isEmpty(labelStr) && labelStr.equals(dict.label))
            return dict.value;
        }
            return null;
    }

    /**
     * 通过 value 获取字典库中，该类型对应的 lable
     * @param value
     * @param dictData
     * @param type
     * @return
     */
    public static String getLable(String value, CxDictEntity dictData,String type){
        if (TextUtils.isEmpty(value)) return "";
        List<DictData> dictDatas = dictData.getDictByType(type);
        String tempArr[] = getDictLabelArr(dictData.getDictByType(type));
        for (DictData tempDD:dictDatas){
            if (value.equals(tempDD.value))
            return tempDD.label;
        }
            return "";
    }

    public static void setTypePickerDialogByParentId(CxSurveyWorkActivity context, TextView textTv, CxDictEntity dictData, String type, String parentId) {
        textTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View arg0) {
                String tempArr[] = getDictLabelArr(dictData.getDictByTypeAndParentId(type,parentId));
                showTypePickerDialog(context,textTv,tempArr);
            }
        });
    }
}

