package com.cninsure.cp.entity.cx;

import android.content.Intent;

import java.io.Serializable;
import java.util.List;

public class CxDsWorkEntity extends CxWorkAddressBaseEntity implements Serializable {
    public String dsCarNumber;//车牌号
    public String carType;//车型
    public String carTypeId;
    public String dsServiceDepot;//维修厂
    public String dsAptitudes;//资质
    public String carSeries;//车系
    public String carTypeName;//车品牌
    public String carStructure;//车辆结构
    public Integer claimLevel;//索赔险别
    public String dsLocation;//定损地点
    public String dsRescueAmount;//定损施救费
    public float hsRescueAmount;//核损施救费
    public float dsAllTotalAmount;//定损总金额

    public String insuredPerson; //  被保险人
    public String riskDate; // 出险时间
    public String carnoType; // 车类型

    public String carFacturerId; // 厂商id
    public String carFacturer; // 厂商
    public String carBrandId; // 车品牌id
    public String carBrand; //车品牌
    public String  carSeriesId; // 车系


    public String dsInstructions;//定损说明
    public List<String> enclosureList;//附件信息List
    public List<CxDsReplaceInfos> replaceInfos;//（数组）换件信息（换件信息金额 = 总计(原)+管理费-残值）
    public CxDsReplaceInfosTotal replaceInfosTotal;//残值（数组）
    public List<CxDsTimeInfos> timeInfos;//工时信息（数组）
    public List<CxDsRepairInfos> repairInfos;//外修配件（数组）
    public CxDsRepairInfosTotal repairInfosTotal;//外修小计（数组）
    /**因后台需要字典值value传String类型，这里做转化*/
    public String getClaimLevel() {
        return claimLevel==null?"":claimLevel+"";
    }

    //获取换件项目总计金额
    public String getHjTotal(){
        if (replaceInfos==null) return "0";
        float total = 0f;
        for (CxDsReplaceInfos items : replaceInfos){
            total += items.unitTotalPrice;
        }
        return total+"";
    }

    public static class CxDsReplaceInfos {
        public float unitPrice;//定损单价
        public int unitCount;//数量
        public float unitTotalPrice;//定损小计
        public String remark;//定损备注
        public float hsUnitPrice;//核价单价
        public int hsUnitCount;//数量
        public float hsUnitTotalPrice;//核损小计
        public String hsRemark;//核损备注

        public String  partId;// 配件id
        public String partPosId;// 配件所属部位id
        public String partName; // 配件名称 -换件项目
        public String  partCode; // 配件编码
        public String localPrice; // 本地价格

    }
    public static class CxDsReplaceInfosTotal {
        public String dsFeeResidual;//定损残值
        public float dsFeeManagement;//定损管理费
        public float dsTotalFee;//定损总计 =换件信息-合计 =换件信息金额 =总计(原)+管理费-残值
        public float hsFeeResidual;//核损残值
        public float hsFeeManagement;//核损管理费
        public float hsTotalFee;//核损总计
    }

    /**
     * 获取工时信息合计金额
     */
    public String getGsTotal(){
        if (timeInfos==null) return "0";
        float total = 0f;
        for (CxDsTimeInfos items:timeInfos){
            total+= items.dsAmount;
        }
        return total+"";
    }
    public static class CxDsTimeInfos {
        public int  timeIndex;//序号
        public String timeType;//类型
        public String timeProject;//项目
        public float dsAmount;//定损金额
        public String dsRemark;//定损备注
        public float hsAmount;//核价金额
        public String  hsRemark;//核损备注

        public String   timeTypeId; // 工时类型
        public String   timeTypeCode; // 工时类型编码
        public String   timeTypeName; // 工时类型名称
        public String  time; // 工时时间

    }
    public static class CxDsRepairInfos {
        public String repairIndex;//序号
        public float localPrice;//本地价格
        public float dsAmount;//定损金额
        public String dsRemark;//定损备注
        public float hsAmount;//核价金额
        public String hsRemark;//核损备注

        public String partId; // 配件id
        public String partPosId; // 配件所属部位id
        public String partName; // 配件名称
        public String partCode; // 配件编码

    }
    public static class CxDsRepairInfosTotal {
        public float dsTotalAmount;//外修小计
        public float hsTotalAmount;//核损外修小计
    }

}
