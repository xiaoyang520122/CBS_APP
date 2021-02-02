package com.cninsure.cp.entity.cx.injurysurvey;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :xy-wm
 * date:2020/12/18 10:52
 * usefuLness: CBS_APP
 */
public class InjuryExamineWorkEntity implements Serializable {

    public String 	injuredName	;//	伤者姓名
    public String 	injuredTel	;//	伤者电话
    public List<String> 	investigationSmallTypes	;//	调查类型
    public String 	otherResetTitle	;//	其他类型重命名

    public String 	surveyResult	;//	调查结果
    public String 	surveyReport	;//	调查报告

    public String 	deliveryMode	;//	快递方式
    public String 	deliveryCompany	;//	快递公司
    public String 	deliveryNo	;//	快递单号
    public String 	consignee	;//	收货人
    public String 	shippingAddress	;//	收货地址
    public String 	deliveryBill	;//	快递单
    public String 	expressProvince	;//	省
    public String 	expressProvinceNo	;//	省编码
    public String 	expressCity	;//	市
    public String 	expressCityNo	;//	市编码
    public String 	expressArea	;//	区
    public String 	expressAreaNo	;//	区编码

    public List<String> 	enclosureList	;//	附件信息
    public List<String> 	voiceNoteList	;//	语音




     public List<InExamineTypeList> surveyTypeList;      //探访内容
//            "doctorList;  //Array[2],
    public String id;  //57,
    public String surveyAddress;  //"2121",
    public String hospitalName;  //"21",

    public String areaNo; //作业区域编码
    public String area;  //作业区域名称
    public String province;  //作业区域省份
    public String caseProvince;  //出险省份
    public String city;  //城市

    public InExamineTypeList getInSuTypeByValue(String itValue){
        if (surveyTypeList==null)  surveyTypeList = new ArrayList<>();
        if (surveyTypeList.size()==0 || TextUtils.isEmpty(itValue)){
            InExamineTypeList istTemp = new InExamineTypeList();
            surveyTypeList.add(istTemp);
            return istTemp;
        }
        for (InExamineTypeList tempSTL:surveyTypeList){
            if (itValue.equals(tempSTL.itemValue)) return tempSTL;
        }
        InExamineTypeList istTemp = new InExamineTypeList();
        surveyTypeList.add(istTemp);
        return istTemp;
    }

    public boolean InSurveyTypeIsNull(String itValue){
        if (surveyTypeList==null)  return true;
        for (InExamineTypeList tempSTL:surveyTypeList){
            if (itValue.equals(tempSTL.itemValue)) return false;
        }
        return true;
    }

    /**获取作业类型集合*/
    public void getInvestigationSmallTypes(){
        if (surveyTypeList==null) return;
        investigationSmallTypes = new ArrayList<>(7);
        for (InExamineTypeList tempSTL:surveyTypeList){
            investigationSmallTypes.add(tempSTL.itemValue);
        }
    }

}
