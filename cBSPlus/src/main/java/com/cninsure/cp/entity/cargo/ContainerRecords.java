package com.cninsure.cp.entity.cargo;

import java.io.Serializable;

/**
 * @author :xy-wm
 * date:2020/12/10 8:33
 * usefuLness: CBS_APP
 */
public class ContainerRecords implements Serializable {
    public String caseNo;  //	案件编号
    public String insured;  //	被保险人
    public String consigneed;  //	收货人
    public String consigneedPhone;  //	收货人联系方式
    public String freightStartDate;  //	货物起运日期
    public String freightStartAddress;  //	货物起运地址
    public String freightEndDate;  //	货物到达日期
    public String freightEndAddress;  //	货物到达地址
    public String freightName;  //	货物名称
    public String carNumber;  //	集装箱编号或车牌号
    public String ckTime;  //	查勘时间
    public String surveyAddress;  //	查勘地址
    public String causeAndCourse;  //	原因和经过
    public String situation;  //	现场情况
    public String description;  //	描述和损失情况
    public String signatureUrl;  //	签名图片

    public String caseName;  //	案件名称
//    public String insured;  //	被保险人
    public String riskTime;  //	出险时间
//    public String surveyAddress;  //	查勘地址
//    public String ckTime;  //	查勘时间
    public String insurerContact;  //	保险人代表
    public String insurerContactPhone;  //	保险人代表电话
    public String insuredContact;  //	被保险人代表
    public String insuredContactPhone;  //	被保险人代表电话
    public String ggsContact;  //	公估方代表
    public String ggsContactPhone;  //	公估方代表电话
//    public String causeAndCourse;  //	查勘内容
//    public String signatureUrl;  //	签名图片


}
