package com.cninsure.cp.entity.cx;

import com.cninsure.cp.entity.BaseEntity;

public class CxDisabyIdentTaskEntity extends BaseEntity {
    public CxDisabyIdentTaskDataEntity data;

    public static class CxDisabyIdentTaskDataEntity extends CxTaskBaseEntity{
        public CxDisabyIdentifyEntity contentJson; //作业信息
    }

}
