package com.cninsure.cp.cx.jiebaoanfragment;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.entity.cx.CxBaoanTaskEntity;
import com.cninsure.cp.entity.cx.CxDamageWorkEntity;
import com.cninsure.cp.entity.cx.CxDsWorkEntity;
import com.cninsure.cp.entity.cx.CxImagEntity;
import com.cninsure.cp.entity.cx.CxOrderMediaTypeEntity;
import com.cninsure.cp.entity.cx.CxSurveyTaskEntity;
import com.cninsure.cp.entity.cx.CxSurveyWorkEntity;
import com.cninsure.cp.entity.cx.CxTaskModelEntity;
import com.cninsure.cp.utils.DialogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 判断拍照是否满足要求
 * @author :xy-wm
 * date:2021/5/27 13:04
 * usefuLness: CBS_APP
 */
public class DoesPhotosMustPassTool {

//    public CxOrderMediaTypeEntity cxMediaTypes ; //拍照类型字典数据
    private List<CxImagEntity> imgEnList; //影像采集信息合集，
    private Integer bussTypeId; //任务类型
    private String orderUid; //任务编号
//    public CxBaoanTaskEntity baoanTaskEntity; //接报案下面所有任务的作业数据
    private Context context;


    private DoesPhotosMustPassTool(){}
    public DoesPhotosMustPassTool(Context context, List<CxImagEntity> imgEnList, String orderUid){
        this.imgEnList = imgEnList;
        this.context = context;
        this.orderUid = orderUid;
    }



    /**
     * 查勘任务拍照是否满足
     * @return
     */
    public boolean isSurveyPass(CxSurveyWorkEntity surveyWorkEn) {
        Map<String,String> tyeCount = new HashMap<>();
        Map<String,String> jsz = new HashMap<>();
        Map<String,String> xsz = new HashMap<>();
        Map<String,String> sgzm = new HashMap<>();
        for (CxImagEntity imgItem:imgEnList){
            if ("100101".equals(imgItem.type)) tyeCount.put("100101","100101"); //人车合影
            else if ("100102".equals(imgItem.type)) tyeCount.put("100102","100102"); //验标照片
            else if ("100103".equals(imgItem.type)) tyeCount.put("100103","100103"); //环境照片
            else if ("100104".equals(imgItem.type)) tyeCount.put("100104","100104"); //痕迹照片
            else if ("1002".equals(imgItem.type)) jsz.put("1002","1002"); //驾驶证（正、副本）
            else if ("1003".equals(imgItem.type)) xsz.put("1003","1003"); //行驶证（正、副本）
            else if ("1004".equals(imgItem.type)) sgzm.put("1004","1004"); //事故证明/事故原因分析
        }
        if (tyeCount.size()<4){ //查勘照片不齐全
            DialogUtil.getErrDialog(context,"查勘照片未拍摄齐全，请保存作业后拍摄照片再提交！").show();
            return false ;
        }
        if (surveyWorkEn==null){
            DialogUtil.getErrDialog(context,"无作业信息！").show();
            return false;
        }
                //1、	行驶证 必拍判断  //“证件查验”处点选“缺少行驶证”“缺少行驶证和驾驶证”，且缺失原因为“交警暂扣” 不限制拍照
                if (surveyWorkEn!=null && surveyWorkEn.subjectInfo!=null && surveyWorkEn.subjectInfo.isLicenseKou!=null) {
                    if ("03".equals(surveyWorkEn.subjectInfo.isLicenseKou) || "04".equals(surveyWorkEn.subjectInfo.isLicenseKou)) {
                        if (surveyWorkEn.subjectInfo.licenseMissingResult == null) {
                            DialogUtil.getErrDialog(context, "缺少证件缺失原因，请选择！").show();
                            return false;
                        } else if ("02".equals(surveyWorkEn.subjectInfo.licenseMissingResult)) { //驾驶员遗忘需要拍谁行驶证照片
                            if (xsz.size() < 1) {
                                DialogUtil.getErrDialog(context, "行驶证未拍摄，请保存作业后拍摄照片再提交！").show();
                                return false;
                            }
                        }
                    }else if ("01".equals(surveyWorkEn.subjectInfo.isLicenseKou)){ //两证齐全必拍行驶证
                        if (xsz.size() < 1) {
                            DialogUtil.getErrDialog(context, "两证齐全必拍行驶证，请保存作业后拍摄照片再提交！").show();
                            return false;
                        }
                    }
                }else{
                    DialogUtil.getErrDialog(context,"缺少证件查验结果，请选择！").show();
                    return false;
                }

                //2、	驾驶证 必拍判断 - “证件查验”处点选“缺少驾驶证”“缺少行驶证和驾驶证”，且缺失原因为“交警暂扣” 不限制拍照
                if (surveyWorkEn!=null && surveyWorkEn.subjectInfo!=null && surveyWorkEn.subjectInfo.isLicenseKou!=null ){
                    if ("02".equals(surveyWorkEn.subjectInfo.isLicenseKou) || "04".equals(surveyWorkEn.subjectInfo.isLicenseKou)){
                        if (surveyWorkEn.subjectInfo.licenseMissingResult == null){
                            DialogUtil.getErrDialog(context, "缺少证件缺失原因，请选择！").show();
                            return false;
                        }else if ("02".equals(surveyWorkEn.subjectInfo.licenseMissingResult)) { //驾驶员遗忘需要拍摄驾驶证照片
                            if (jsz.size() < 1) {
                                DialogUtil.getErrDialog(context, "驾驶证未拍摄，请保存作业后拍摄照片再提交！").show();
                                return false;
                            }
                        }
                    }else if ("01".equals(surveyWorkEn.subjectInfo.isLicenseKou)){ //两证齐全必拍驾驶证
                        if (jsz.size() < 1) {
                            DialogUtil.getErrDialog(context, "两证齐全必拍驾驶证，请保存作业后拍摄照片再提交！").show();
                            return false;
                        }
                    }
                }else{
                    DialogUtil.getErrDialog(context,"缺少证件查验结果，请选择！").show(); //或证件缺失原因
                    return false;
                }
            //事故证明 必拍判断 - “损失类型”字段点选只有“标的车损”(value=3)一项，且“估损金额”≤2000元时（此处的损失类型、估损金额限制条件可灵活配置调整），事故证明无照片可提交
                if (surveyWorkEn!=null && surveyWorkEn.surveyInfo!=null && surveyWorkEn.surveyInfo.lossType!=null  && surveyWorkEn.surveyInfo.lossType.length>0 && surveyWorkEn.surveyInfo.lossAmount != null) {
                    if (surveyWorkEn.surveyInfo.lossType.length == 1 && surveyWorkEn.surveyInfo.lossType[0] == 3 && surveyWorkEn.surveyInfo.lossAmount > 2000){
                        //“损失类型”字段点选只有“标的车损”(value=3)一项，且“估损金额”≤2000元时（此处的损失类型、估损金额限制条件可灵活配置调整），事故证明无照片可提交
                        if (sgzm.size()<1){
                            DialogUtil.getErrDialog(context,"事故证明未拍摄，请保存作业后拍摄照片再提交！").show();
                            return false;
                        }
                    }else if ( surveyWorkEn.surveyInfo.lossType.length > 0){
                        //当“损失类型”字段点选除“标的车损”外任意一项或多项时，事故证明影像目录必须上传照片，查勘任务才可以提交。
                        for (Integer lossItem:surveyWorkEn.surveyInfo.lossType){
                            if (lossItem != 3){
                                if (sgzm.size()<1){
                                    DialogUtil.getErrDialog(context,"事故证明未拍摄，请保存作业后拍摄照片再提交！").show();
                                    return false;
                                }
                            }
                        }
                    }
                }else{
                    DialogUtil.getErrDialog(context,"损失类型 或 估损金额 未填写！").show();
                    return false;
                }
        return true;
    }


    /**
     * 判断定损任务拍照数量是否满足要求。
     * @param cxDsEn
     * @return
     */
    public boolean isDsPass(CxDsWorkEntity cxDsEn){
        Map<String,String> csCount = new HashMap<>(); //车损照片
        Map<String,String> cjhCount = new HashMap<>(); //车架号
        Map<String,String> sjfCount = new HashMap<>(); //施救费发票 施救费>0必传
        for (CxImagEntity imgItem:imgEnList){
            if (("110101_"+orderUid).equals(imgItem.type)) csCount.put(imgItem.id+"","110101"); //车损照片
            else if (("110102_"+orderUid).equals(imgItem.type)) cjhCount.put(imgItem.id+"","110102"); //车架号
            else if (("110104_"+orderUid).equals(imgItem.type)) sjfCount.put(imgItem.id+"","110104"); //施救费发票 施救费>0必传
        }
        if (csCount.size()<1){
            DialogUtil.getErrDialog(context,"车损照片未拍照，请保存作业后拍摄照片再提交！").show();
            return false;
        }if (cjhCount.size()<1){
            DialogUtil.getErrDialog(context,"车架号未拍照，请保存作业后拍摄照片再提交！").show();
            return false;
        }

        if (cxDsEn.dsRescueAmount==null){
            DialogUtil.getErrDialog(context,"施救费未填写！").show();
            return false;
        }else if ((cxDsEn.dsRescueAmount==null?0:cxDsEn.dsRescueAmount)>0 && sjfCount.size()<1){
            DialogUtil.getErrDialog(context,"施救费大于0时，施救费发票需要拍照，，请保存作业后拍摄照片再提交！").show();
            return false;
        }
        return true;
    }


    /**
     * 判断物损任务拍照数量是否满足要求。
     * @param cxDamgEn 物损任务作业信息
     * @return
     */
    public boolean isDamagePass(CxDamageWorkEntity cxDamgEn){
        Map<String,String> wsCount = new HashMap<>(); //物损照片
        Map<String,String> sjfCount = new HashMap<>(); //施救费发票 施救费>0必传
        for (CxImagEntity imgItem:imgEnList){
            if (("120101_"+orderUid).equals(imgItem.type)) wsCount.put(imgItem.id+"","120101"); //物损照片
            else if ( ("120103_"+orderUid).equals(imgItem.type)) sjfCount.put(imgItem.id+"","120103"); //施救费发票
        }
        if (wsCount.size()<1){
            DialogUtil.getErrDialog(context,"物损照片未拍照，请保存作业后拍摄照片再提交！").show();
            return false;
        }
        if (cxDamgEn.dsRescueAmount==null){
            DialogUtil.getErrDialog(context,"施救费未填写！").show();
            return false;
        }else if ((cxDamgEn.dsRescueAmount==null?0:cxDamgEn.dsRescueAmount)>0 &&sjfCount.size()<1){
            DialogUtil.getErrDialog(context,"施救费大于0时，施救费发票需要拍照，请保存作业后拍摄照片再提交！").show();
            return false;
        }
        return true;
    }
}
