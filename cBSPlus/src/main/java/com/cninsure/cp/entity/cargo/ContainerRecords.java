package com.cninsure.cp.entity.cargo;

import android.text.TextUtils;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :xy-wm
 * date:2020/12/10 8:33
 * usefuLness: CBS_APP
 */
public class ContainerRecords implements Serializable {

    public String caseNo	; //案件编号
    public String insured	; //被保险人
    public String consigneed	; //现场人姓名
    public String consigneedPhone	; //电话号码
    public String carrierNo	; //承运车辆车号
    public String containerNo	; //集装箱号
    public String sealNo	; //封条号
    public String pickUpPort	; //提货港
    public String pickUpTime	; //提货日期
    public String riskTime	; //出险日期
    public String riskAddress	; //出险地点
    public String freightName	; //货物名称
    public String freightPrice	; //货物单价
    public String packaging	; //包装规格
    public String transportCount	; //运输总件数
    public String damageCount	; //受损数量
    public String forearm	; //前板 保存格式“0,1,2"  [{ value: '0', label: '破洞' },{ value: '1', label: '外鼓' },{ value: '2', label: '内凹' },{ value: '3', label: '裂缝' },{ value: '4', label: '完好' }]
    public String forearmRemark	; //前板备注
    public String leftPlate	; //左侧板   [{ value: '0', label: '破洞' },{ value: '1', label: '外鼓' },{ value: '2', label: '内凹' },{ value: '3', label: '裂缝' },{ value: '4', label: '完好' }]
    public String leftPlateRemark	; //左侧板备注
    public String rightPlate	; //右侧板  [{ value: '0', label: '破洞' },{ value: '1', label: '外鼓' },{ value: '2', label: '内凹' },{ value: '3', label: '裂缝' },{ value: '4', label: '完好' }]
    public String rightPlateRemark	; //右侧板备注
    public String topPlate	; //箱顶板  [{ value: '0', label: '破洞' },{ value: '1', label: '外鼓' },{ value: '2', label: '内凹' },{ value: '3', label: '裂缝' },{ value: '4', label: '完好' }]
    public String topPlateRemark	; //箱顶板备注
    public String bottomPlate	; //箱内地板  [{ value: '0', label: '破洞' },{ value: '1', label: '裂缝' },{ value: '2', label: '潮湿' },{ value: '3', label: '完好' }]
    public String bottomPlateRemark	; //箱内地板备注
    public String doorAndSeal	; //箱门及密封胶条  [{ value: '0', label: '老化' },{ value: '1', label: '透光' },{ value: '2', label: '完好' }]
    public String doorAndSealRemark	; //箱门及密封胶条备注
    public String causeAndCourse	; //事故发生原因及经过
    public String comfirmFreightName	; //货物名称
    public String comfirmFreightPrice	; //货主申报单价
    public String comfirmPackage	; //包装情况
    public String comfirmSpecification	; //货物规格
    public String comfirmTransportCount	; //运载总数量
    public String comfirmDamageCount	; //受损数量
    public Integer silverNitrateDetection	; //硝酸银检测  [{ value: 0, label: '海水' },{ value: 1, label: '淡水' }]
    public Integer isBrokenPackge	; //包装是否破损  [{ value: 0, label: '是' },{ value: 1, label: '否' }]
    public String ckGgsUrl	; //现场查勘人
    public String signatureUrl	; //现场负责人/代理人
    public String addedExplain;//补充说明


    public String caseName;  //案件名称
//    public String insured;  //被保险人
//    public String riskTime;  //出险时间
    public String surveyAddress;  //查勘地址
    public String ckTime;  //查勘时间
    public String insurerContact;  //保险人代表
    public String insurerContactPhone;  //保险人代表电话
    public String insuredContact;  //被保险人代表
    public String insuredContactPhone;  //被保险人代表电话
    public String ggsContact;  //公估方代表
    public String ggsContactPhone;  //公估方代表电话
//    public String causeAndCourse;  //查勘内容
//    public String signatureUrl;  //签名图片

    /**检查对象可选择值*/
    private Map<String, String[]> damageValues;
    public Map<String, String[]> getDamageMap(){
        damageValues = new HashMap<>(8);
        String[] forearmArr = new String[]{"破洞","外鼓","内凹","裂缝","完好"};//前板
        String[] leftPlateArr = new String[]{"破洞","外鼓","内凹","裂缝","完好"};//左侧板
        String[] rightPlateArr = new String[]{"破洞","外鼓","内凹","裂缝","完好"};//右侧板
        String[] topPlateArr = new String[]{"破洞","外鼓","内凹","裂缝","完好"};//箱顶板
        String[] bottomPlateArr = new String[]{"破洞","裂缝","潮湿","完好"};//箱内地板
        String[] doorAndSealArr = new String[]{"老化","透光","完好"};//箱门及密封胶条
//        String[] silverNitrateDetectionArr = new String[]{"海水","淡水"};//箱门及密封胶条
//        String[] isBrokenPackgeArr = new String[]{"是","否"};//包装是否破损
        damageValues.put("前板",forearmArr);
        damageValues.put("左侧板",leftPlateArr);
        damageValues.put("右侧板",rightPlateArr);
        damageValues.put("箱顶板",topPlateArr);
        damageValues.put("箱内地板",bottomPlateArr);
        damageValues.put("箱门及密封胶条",doorAndSealArr);
//        damageValues.put("硝酸银检测",silverNitrateDetectionArr);
//        damageValues.put("包装是否破损",isBrokenPackgeArr);
        return damageValues;
    }

    /**根据检测部位名称，获取指定检测部位的可选值数组*/
    public String[] getDamageValue(String damageName){
        getDamageMap();
        String[] valueArr = damageValues.get(damageName);
        if (valueArr==null) return new String[]{};
        else return valueArr;
    }

    /**
     * 根据检测部位名称和编号值（多个组成的字符串）获取对应的数组数据组成的字符串。
     * @param damageName
     * @param value
     * @return
     */
    public String getDamageLabelStr(String damageName,String value){
        getDamageMap();
        String tempStr = "";
        String[] valueArr = getDamageValue(damageName);
        if (!TextUtils.isEmpty(value)){ //value为空，则返回空字符串
            String[] positionArr = value.split(",");
            for (String tempPosition: positionArr) {
                if (TextUtils.isEmpty(tempStr)){
                    tempStr = tempStr + valueArr[Integer.parseInt(tempPosition)];
                }else{
                    tempStr = tempStr+"," + valueArr[Integer.parseInt(tempPosition)];
                }
            }
        }
        return tempStr;
    }

    public void setDamageValues(String damageName,String values){
        if (!TextUtils.isEmpty(damageName) && damageName.equals("前板"))forearm=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("左侧板"))leftPlate=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("右侧板"))rightPlate=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱顶板"))topPlate=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱内地板"))bottomPlate=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱门及密封胶条"))doorAndSeal=values;
//        if (!TextUtils.isEmpty(damageName) && damageName.equals("硝酸银检测"))silverNitrateDetection=values;
//        if (!TextUtils.isEmpty(damageName) && damageName.equals("包装是否破损"))isBrokenPackge=values;
    }

    public void setDamageRemarkValue(String damageName,String values){
        if (!TextUtils.isEmpty(damageName) && damageName.equals("前板"))forearmRemark=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("左侧板"))leftPlateRemark=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("右侧板"))rightPlateRemark=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱顶板"))topPlateRemark=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱内地板"))bottomPlateRemark=values;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱门及密封胶条"))doorAndSealRemark=values;
    }

    public String getDamageRemarkValue(String damageName){
        String ramakTemp = "";
        if (!TextUtils.isEmpty(damageName) && damageName.equals("前板"))ramakTemp=forearmRemark;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("左侧板"))ramakTemp=leftPlateRemark;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("右侧板"))ramakTemp=rightPlateRemark;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱顶板"))ramakTemp=topPlateRemark;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱内地板"))ramakTemp=bottomPlateRemark;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱门及密封胶条"))ramakTemp=doorAndSealRemark;
        if (ramakTemp==null) return "";
        return ramakTemp;
    }

    /**
     * 获取检查部位对象保存值
     * @param damageName
     */
    public String getDamageValueByName(String damageName){
        String valueTemp = "";
        if (!TextUtils.isEmpty(damageName) && damageName.equals("前板")) valueTemp = forearm;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("左侧板")) valueTemp = leftPlate;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("右侧板")) valueTemp = rightPlate;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱顶板")) valueTemp = topPlate;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱内地板")) valueTemp = bottomPlate;
        if (!TextUtils.isEmpty(damageName) && damageName.equals("箱门及密封胶条")) valueTemp = doorAndSeal;
        if (TextUtils.isEmpty(valueTemp)) return "";
        else return valueTemp;
    }


}
