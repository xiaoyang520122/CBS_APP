package com.cninsure.cp.entity.cx;

import com.cninsure.cp.entity.BaseEntity;


public class CxDamageTaskEntity extends BaseEntity {

    public DamageWorkEntity data;

    public static class DamageWorkEntity extends CxTaskBaseEntity{

        public CxDamageWorkEntity contentJson;

    }
}
