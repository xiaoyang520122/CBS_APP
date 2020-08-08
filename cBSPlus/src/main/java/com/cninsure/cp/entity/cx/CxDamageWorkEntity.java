package com.cninsure.cp.entity.cx;

import java.io.Serializable;
import java.util.List;

public class CxDamageWorkEntity implements Serializable {

    public String belongPerson; // 归属人
    public String damageName; // 物损名称
    public Integer damageType; // 损失类型
    public Float dsRescueAmount; // 定损施救费
    public Float hsRescueAmount; // 核损施救费
    public Float dsTotalAmount; // 定损总计
    public String dsInstructions; // 定损说明
    public List<MaterialsEntity> smallMaterials;

    public static class MaterialsEntity  {
        public Integer damageIndex; //序号
        public Integer smallType;//物损类别
        public String name;//项目名称
        public Float dsUnitPrice;//单价
        public Integer dsUnitCount;//数量
        public Float dsSalvageValue;//残值
        public String dsRemark;//备注
        public Float hsUnitPrice;//单价
        public Integer hsUnitCount;//数量
        public Float hsSalvageValue;//残值
        public String hsRemark;//备注
    }
}
