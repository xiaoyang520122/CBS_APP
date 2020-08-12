package com.cninsure.cp.entity.cx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CxInjuryTrackWorkEntity implements Serializable {
    public String injuredName;    //伤者姓名
    public String injuredTel;    //伤者电话
    public String workAddress;    //作业地点
    public Integer newInjuries = -1;    //新增伤情  1有、0无
    public String recovery;    //恢复情况
    public ArrayList<InjuryTrackAskObject> askList;
    public String otherExplain;    //其他说明
    public Integer deliveryMode = -1;    //快递方式  0自行送达、1到付
    public Integer deliveryCompany = -1;    //快递公司  0、韵达	1、中通快递	2、宅急送	3、EMS	4、圆通快递	5、顺丰快递	6、申通快递
    public String deliveryNo;    //快递单号
    public String consignee;    //收货人
    public String shippingAddress;    //收货地址
    public String deliveryBill;    //快递单
    public List<String> enclosureList;    //附件信息列表

    public static class InjuryTrackAskObject implements Serializable {
        public String askObject;    //询问对象
        public String askObjectTel;    //对象电话
    }
}
