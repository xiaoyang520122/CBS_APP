package com.cninsure.cp.entity.cx;

import java.io.Serializable;
import java.util.List;

public class CxDamageWorkEntity extends CxWorkAddressBaseEntity implements Serializable {

    public String belongPerson; // 归属人
    public String damageName; // 物损名称
    public String damageType; // 损失类型-编码value
    public String damageTypeName; // 损失类型-名称label

    public Float dsRescueAmount; // 定损施救费
    public Float hsRescueAmount; // 核损施救费
    public Float dsTotalAmount; // 定损总计
    public String dsInstructions; // 定损说明
    public String surveyAddress; // 作业地点
    public List<MaterialsEntity> smallMaterials;

    /**因后台需要字典值value传String类型，这里做转化*/
    public String getDamageType() {
        return damageType==null?"":damageType+"";
    }

    public static class MaterialsEntity  {
        public Integer damageIndex; //序号
        public Integer smallType;//物损类别
        public String name;//项目名称
        public Float dsUnitPrice;//单价
        public Integer dsUnitCount;//数量
        public Float dsSalvageValue;//残值
        public String dsRemark;//备注
        public Float hsUnitPrice;//核损单价
        public Integer hsUnitCount;//核损数量
        public Float hsSalvageValue;//核损残值
        public String hsRemark;//核损备注

        /**因后台需要字典值value传String类型，这里做转化*/
        public String getSmallType() {
            return smallType==null?"":smallType+"";
        }
    }
}
