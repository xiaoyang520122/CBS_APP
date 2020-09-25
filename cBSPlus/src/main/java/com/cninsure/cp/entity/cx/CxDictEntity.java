package com.cninsure.cp.entity.cx;


import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 车险查勘任务Type类型如下
 * accident_type	//	事故类型	0、单方；1、多方
 * accident_small_type	//	事故详细类型	0碰撞、1火烧、2自然灾害
 * accident_reason	//	出险原因	行使受损、停放受损、水淹、火灾、车身人为划痕、玻璃单独损坏、车辆盗抢、重大自然灾害、其他
 * accident_small_reason	//	出险详细原因
 * survey_type	//	查勘类型	0现场查勘、1非现场查勘、2补勘现场
 * accident_liability	//	事故责任	0全责（固定100%），1主责（默认70%，准许录入在5-100）、2同责（固定50%）、3次责（30%，0-50）、4无责（固定0%）
 * loss_type	//	损失类型	0三者、1物损、2人伤
 * loss_object_type	//	损失情况	三者：0三者车损；物损：1标的车物品、2三者车内物、3三者车外物；人伤：4本车司机、5本车乘客、6三者车内人，7其他三者人伤
 * compensation_method	//	赔付方式	0按责赔付、1互碰自赔
 * survey_conclusion	//	查勘结论	……
 * carno_type	//	号牌种类	0小型家用车、1客车、2货车、3特种车、4其他
 * car_usetype	//	使用性质	0运营、1非运营
 * injured_type     //伤者类型 0本车司机、1本车乘客、2三者车内人伤、3其他三者人员
 *  damage_loss_type    //损失类型 0标的车物品、1三者车内物、2三者车外物
 *
 *  物损定损任务：
 *  damage_loss_type ：损失类型
 *  damage_type ：物损类别
 */

public class CxDictEntity implements Serializable {

    public List<DictData> list;

    public static class DictData implements Serializable {
//      public String createBy;  //"User-20180103101603-687B671A", 创建人
//      public String createDate;  //"2020-07-09 11:23:45", 创建日期
        public String delFlag;  //"0",  //删除标志
        public String description;  //"车险订单作业资料类型",
        public long id;  //5,
        public String label;  //"人车合影",
//      public long parentId;  //null,
//      public String remarks;  //null,
        public int sort;  //1,
        public String type;  //"cxOrderWorkImageType",
//      public String updateBy;  //"User-20180103101603-687B671A",
//      public String updateDate;  //"2020-07-09 11:23:45",
        public String value;  //"1" 字典值，字符串
    }

    public List<DictData> getDictByType(String type) {
        List<DictData> tempList = new ArrayList<>();
        if (list != null) {
            for (DictData dEn : list) {
                if (type.equals(dEn.type) && "0".equals(dEn.delFlag))
                    tempList.add(dEn);
            }
        }
        return tempList;
    }

    public String getLabelByValue(String type,String value){
        if (TextUtils.isEmpty(value)) return "";
        if (list != null) {
            for (DictData dEn : list) {
                if (type.equals(dEn.type) && value.equals(dEn.value) && "0".equals(dEn.delFlag))
                   return dEn.label;
            }
        }
        return "";
    }
}
