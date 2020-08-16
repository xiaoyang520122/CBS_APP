package com.cninsure.cp.entity.cx;

import com.cninsure.cp.entity.BaseEntity;

public class CxDsTaskEntity extends BaseEntity {
    public CxDsTaskDataEntity data ;

    public static class CxDsTaskDataEntity extends CxTaskBaseEntity{
        public CxDsWorkEntity contentJson;

    }
}
