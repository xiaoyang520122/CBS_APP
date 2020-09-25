package com.cninsure.cp.entity.cx;

import android.content.Intent;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CxDsWorkEntity extends CxWorkAddressBaseEntity implements Serializable {
    public String dsCarNumber;//车牌号
    public String carType;//车型
    public String carTypeId;
    public String carTypeCountry;  //车型生产类型
    public String dsServiceDepot;//维修厂
    public String dsAptitudes;//资质
    public String carSeries;//车系
    public String carStructure;//车辆结构
    public Integer claimLevel=-1;//索赔险别
    public String dsLocation;//定损地点
    public String dsRescueAmount;//定损施救费
    public Float hsRescueAmount;//核损施救费
    private Float dsAllTotalAmount;//定损总金额

    public Float getDsAllTotalAmount() {
        float DsAllTotalAmountTemp = Float.parseFloat(getHjTotal())+Float.parseFloat(getGsTotal())+Float.parseFloat(getWxTotal());
        return DsAllTotalAmountTemp;
    }

    public String insuredPerson; //  被保险人
    public String riskDate; // 出险时间
    public String carnoType; // 车类型

    public String carFacturerId; // 厂商id
    public String carFacturer; // 厂商
    public String carFacturerCountry; //厂商生产类型
    public String carBrandId; // 车品牌id
    public String carBrand; //车品牌
    public String carBrandCountry;//车品牌生产类型
    public String  carSeriesId; // 车系
    public String carSeriesCountry; //车系生产类型


    public String dsInstructions;//定损说明
    public List<String> enclosureList=new ArrayList<>();//附件信息List
    public List<CxDsReplaceInfos> replaceInfos  =new ArrayList<>();//（数组）换件信息（换件信息金额 = 总计(原)+管理费-残值）
    public CxDsReplaceInfosTotal replaceInfosTotal;//残值（数组）
    public List<CxDsTimeInfos> timeInfos =new ArrayList<>();//工时信息（数组）
    public List<CxDsRepairInfos> repairInfos =new ArrayList<>();//外修配件（数组）
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

    public static class CxDsReplaceInfos  implements Serializable {
        public Float unitPrice;//定损单价
        public Integer unitCount;//数量
        public Float unitTotalPrice;//定损小计
        public String remark;//定损备注
        public Float hsUnitPrice;//核价单价
        public Integer hsUnitCount;//数量
        public Float hsUnitTotalPrice;//核损小计
        public String hsRemark;//核损备注

        public String  partId;// 配件id
        public String partPosId;// 配件所属部位id
        public String partName; // 配件名称 -换件项目
        public String  partCode; // 配件编码
        public String localPrice; // 本地价格

    }
    public static class CxDsReplaceInfosTotal  implements Serializable {
        public String dsFeeResidual;//定损残值
        public String dsFeeManagement;//定损管理费
        public Float dsTotalFee;//定损总计 =换件信息-合计 =换件信息金额 =总计(原)+管理费-残值
        public Float hsFeeResidual;//核损残值
        public Float hsFeeManagement;//核损管理费
        public Float hsTotalFee;//核损总计
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


    public static class CxDsTimeInfos  implements Serializable {
        public Integer  timeIndex;//序号
        public String timeType;//类型
        public String timeProject;//项目
        public Float dsAmount;//定损金额
        public String dsRemark;//定损备注
        public Float hsAmount;//核价金额
        public String  hsRemark;//核损备注

        public String   timeTypeId; // 工时类型
        public String   timeTypeCode; // 工时类型编码
        public String   timeTypeName; // 工时类型名称
        public String  time; // 工时时间

    }

    /**
     * 外修配件
     */
    public static class CxDsRepairInfos  implements Serializable {
        public String repairIndex;//序号
        public Float localPrice;//本地价格
        public Float dsAmount;//定损金额
        public String dsRemark;//定损备注
        public Float hsAmount;//核价金额
        public String hsRemark;//核损备注

        public String partId; // 配件id
        public String partPosId; // 配件所属部位id
        public String partName; // 配件名称
        public String partCode; // 配件编码

    }

    /**
     * 获取工时信息合计金额
     */
    public String getWxTotal(){
        if (repairInfos==null) return "0";
        float total = 0f;
        for (CxDsRepairInfos items:repairInfos){
            total+= items.dsAmount;
        }
        return total+"";
    }
    public static class CxDsRepairInfosTotal  implements Serializable {
        private Float dsTotalAmount;//外修小计
        private Float hsTotalAmount;//核损外修小计

    }

}
