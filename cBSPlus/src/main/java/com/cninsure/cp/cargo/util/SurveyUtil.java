package com.cninsure.cp.cargo.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.cargo.ContainerRecords;
import com.cninsure.cp.entity.cargo.SurveyRecordsEntity;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.SetTextUtil;

/**
 * @author :xy-wm
 * date:2020/12/10 12:02
 * usefuLness: CBS_APP
 */
public class SurveyUtil {
    private Context context;
    private View surveyView,surveyNotView;
    private SurveyRecordsEntity sREn;

    private SurveyUtil (){}
    public SurveyUtil (Context context,View surveyView,View surveyNotView,SurveyRecordsEntity sREn){
        this.context = context;
        this.sREn = sREn;
        this. surveyNotView = surveyNotView;
        this. surveyView = surveyView;
    }


    /**显示集装箱信息*/
    public void disPlayContainerInfo(String caseNo,String insured) {
        SetTextUtil.setTextViewText(surveyView.findViewById(R.id.CargoSR_caseNo), caseNo); //案件号
        SetTextUtil.setTextViewText(surveyView.findViewById(R.id.CargoSR_insured), insured); //被保险人
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_consigneed), sREn.records.consigneed); //收货人
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_consigneedPhone), sREn.records.consigneedPhone); //收货人联系方式
        SetTextUtil.setTextViewText(surveyView.findViewById(R.id.CargoSR_freightStartDate), sREn.records.freightStartDate); //起运日期
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_freightStartAddress), sREn.records.freightStartAddress); //起运地点
        SetTextUtil.setTextViewText(surveyView.findViewById(R.id.CargoSR_freightEndDate), sREn.records.freightEndDate); //到达日期
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_freightEndAddress), sREn.records.freightEndAddress); //到达地点
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_freightName), sREn.records.freightName); //货物名称
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_carNumber), sREn.records.carNumber); //集装箱号/车牌号
        SetTextUtil.setTextViewText(surveyView.findViewById(R.id.CargoSR_ckTime), sREn.records.ckTime); //到达日期
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_surveyAddress), sREn.records.surveyAddress); //查勘地点
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_causeAndCourse), sREn.records.causeAndCourse); //出险原因及经过
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_situation), sREn.records.situation); //集装箱验箱情况
        SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_description), sREn.records.description); //货物描述、受损程度及损失情况
        Glide.with(context).load(sREn.records.signatureUrl).into((ImageView) surveyView.findViewById(R.id.CargoSR_signatureUrl)); //签字图片
        disPlaySign();
        DateChoiceUtil.setLongDatePickerDialogOnClick(context,surveyView.findViewById(R.id.CargoSR_freightStartDate));
        DateChoiceUtil.setLongDatePickerDialogOnClick(context,surveyView.findViewById(R.id.CargoSR_freightEndDate));
        DateChoiceUtil.setLongDatePickerDialogOnClick(context,surveyView.findViewById(R.id.CargoSR_ckTime));
    }

    public void disPlayNotContainerInfo(String insured,String riskTime) {
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.CargoSRN_caseName), sREn.records.caseName); //案件名称
        SetTextUtil.setTextViewText(surveyNotView.findViewById(R.id.CargoSRN_insured), insured); //被保险人
        SetTextUtil.setTextViewText(surveyNotView.findViewById(R.id.CargoSRN_riskTime), riskTime); //出险时间
        SetTextUtil.setTextViewText(surveyNotView.findViewById(R.id.CargoSRN_ckTime), sREn.records.ckTime); //查勘日期
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.CargoSRN_surveyAddress), sREn.records.surveyAddress); //收货人
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_insurerContact), sREn.records.insurerContact); //保险人代表
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_insurerContactPhone), sREn.records.insurerContactPhone); //保险人代表电话
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_insuredContact), sREn.records.insuredContact); //被保险人代表
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_insuredContactPhone), sREn.records.insuredContactPhone); //被保险人代表电话
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_ggsContact), sREn.records.ggsContact); //公估方代表
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_ggsContactPhone), sREn.records.ggsContactPhone); //公估方代表电话
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.CargoSRN_causeAndCourse), sREn.records.causeAndCourse); //查勘内容
        Glide.with(context).load(sREn.records.signatureUrl).into((ImageView) surveyNotView.findViewById(R.id.CargoSRN_signatureUrl)); //签字图片
        disPlaySign();
        DateChoiceUtil.setLongDatePickerDialogOnClick(context,surveyNotView.findViewById(R.id.CargoSRN_ckTime));
    }

    public SurveyRecordsEntity reflashData(){
        if (sREn==null) return null;
        if(sREn.records ==null) sREn.records = new ContainerRecords();
        if (sREn.ckDocType == 0) getContainerInfo();  //刷新界面集装箱信息到实体类
        if (sREn.ckDocType == 1) getNotContainerInfo();  //刷新界面集装箱信息到实体类
        return sREn;
    }

    /**获取集装箱数据*/
    private void getContainerInfo() {
        sREn.records.caseNo = ((TextView)(surveyView.findViewById(R.id.CargoSR_caseNo))).getText().toString(); //案件号
        sREn.records.insured = ((TextView)(surveyView.findViewById(R.id.CargoSR_insured))).getText().toString(); //被保险人
        sREn.records.consigneed = ((EditText)(surveyView.findViewById(R.id.CargoSR_consigneed))).getText().toString(); //收货人
        sREn.records.consigneedPhone = ((EditText)(surveyView.findViewById(R.id.CargoSR_consigneedPhone))).getText().toString(); //收货人联系方式
        sREn.records.freightStartDate = ((TextView)(surveyView.findViewById(R.id.CargoSR_freightStartDate))).getText().toString(); //起运日期
        sREn.records.freightStartAddress = ((EditText)(surveyView.findViewById(R.id.CargoSR_freightStartAddress))).getText().toString(); //起运地点
        sREn.records.freightEndDate = ((TextView)(surveyView.findViewById(R.id.CargoSR_freightEndDate))).getText().toString(); //到达日期
        sREn.records.freightEndAddress = ((EditText)(surveyView.findViewById(R.id.CargoSR_freightEndAddress))).getText().toString(); //到达地点
        sREn.records.freightName = ((EditText)(surveyView.findViewById(R.id.CargoSR_freightName))).getText().toString(); //货物名称
        sREn.records.carNumber = ((EditText)(surveyView.findViewById(R.id.CargoSR_carNumber))).getText().toString(); //集装箱号/车牌号
        sREn.records.ckTime = ((TextView)(surveyView.findViewById(R.id.CargoSR_ckTime))).getText().toString(); //到达日期
        sREn.records.surveyAddress = ((EditText)(surveyView.findViewById(R.id.CargoSR_surveyAddress))).getText().toString(); //查勘地点
        sREn.records.causeAndCourse = ((EditText)(surveyView.findViewById(R.id.CargoSR_causeAndCourse))).getText().toString(); //出险原因及经过
        sREn.records.situation = ((EditText)(surveyView.findViewById(R.id.CargoSR_situation))).getText().toString(); //集装箱验箱情况
        sREn.records.description = ((EditText)(surveyView.findViewById(R.id.CargoSR_description))).getText().toString(); //货物描述、受损程度及损失情况
//        Glide.with(context).load(sREn.records.signatureUrl).into((ImageView) surveyView.findViewById(R.id.CargoSR_signatureUrl)); //签字图片
    }

    /**获取非集装箱数据*/
    private void getNotContainerInfo() {
        sREn.records.caseName = ((EditText)(surveyNotView.findViewById(R.id.CargoSRN_caseName))).getText().toString(); //案件名称
        sREn.records.insured = ((TextView)(surveyNotView.findViewById(R.id.CargoSRN_insured))).getText().toString(); //被保险人
        sREn.records.riskTime = ((TextView)(surveyNotView.findViewById(R.id.CargoSRN_riskTime))).getText().toString(); //出险时间
        sREn.records.ckTime = ((TextView)(surveyNotView.findViewById(R.id.CargoSRN_ckTime))).getText().toString(); //查勘日期
        sREn.records.surveyAddress = ((EditText)(surveyNotView.findViewById(R.id.CargoSRN_surveyAddress))).getText().toString(); //收货人
        sREn.records.insurerContact = ((EditText)(surveyNotView.findViewById(R.id.cti_insurerContact))).getText().toString(); //保险人代表
        sREn.records.insurerContactPhone = ((EditText)(surveyNotView.findViewById(R.id.cti_insurerContactPhone))).getText().toString(); //保险人代表电话
        sREn.records.insuredContact = ((EditText)(surveyNotView.findViewById(R.id.cti_insuredContact))).getText().toString(); //被保险人代表
        sREn.records.insuredContactPhone = ((EditText)(surveyNotView.findViewById(R.id.cti_insuredContactPhone))).getText().toString(); //被保险人代表电话
        sREn.records.ggsContact = ((EditText)(surveyNotView.findViewById(R.id.cti_ggsContact))).getText().toString(); //公估方代表
        sREn.records.ggsContactPhone = ((EditText)(surveyNotView.findViewById(R.id.cti_ggsContactPhone))).getText().toString(); //公估方代表电话
        sREn.records.causeAndCourse = ((EditText)(surveyNotView.findViewById(R.id.CargoSRN_causeAndCourse))).getText().toString(); //查勘内容
//        Glide.with(context).load(sREn.records.signatureUrl).into((ImageView) surveyNotView.findViewById(R.id.CargoSRN_signatureUrl)); //签字图片
    }

    public void disPlaySign() {
        if(sREn.records ==null) sREn.records = new ContainerRecords();
        String signPath= AppApplication.getUSER().data.qiniuUrl+sREn.records.signatureUrl;
        if (!TextUtils.isEmpty(signPath) && sREn.ckDocType == 0){
            Glide.with(context).load(signPath).into(((ImageView)(surveyView.findViewById(R.id.CargoSR_signatureUrl))));  //刷新界面集装箱信息到实体类
            (surveyView.findViewById(R.id.CargoSR_signatureUrl)).setVisibility(View.VISIBLE);
        }else if (!TextUtils.isEmpty(signPath) && sREn.ckDocType == 1)
            Glide.with(context).load(signPath).into(((ImageView)(surveyNotView.findViewById(R.id.CargoSRN_signatureUrl)))); ;  //刷新界面集装箱信息到实体类
        (surveyNotView.findViewById(R.id.CargoSRN_signatureUrl)).setVisibility(View.VISIBLE);
    }
}
