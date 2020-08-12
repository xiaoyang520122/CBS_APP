package com.cninsure.cp.entity.cx;

import java.io.Serializable;
import java.util.List;

public class CxDisabyIdentifyEntity implements Serializable {

    public String injuredName;//	伤者姓名
    public String injuredTel;//	伤者电话
    public String appraisalTime;//	残定时间
    public String appraisalOffice;//	残定机构
    public String appraisalAddress;//	残定地点
    public String appraisalPerson;//	鉴定人
    public String appraisalTel;//	鉴定电话
    public String appraisalResult;//	鉴定结果
    public String appraisalRemarks;//	备注

    public Integer deliveryMode;//	快递方式 0自行送达、1到付
    public Integer deliveryCompany=-1;//	快递公司 "0、韵达	1、中通快递	2、宅急送	3、EMS	4、圆通快递	5、顺丰快递	6、申通快递"
    public String deliveryNo;//	快递单号
    public String consignee;//收货人
    public String shippingAddress;//	收货地址
    public String deliveryBill;//	快递单

    public List<String> enclosureList;//	附件信息

}
