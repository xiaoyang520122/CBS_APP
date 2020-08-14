package com.cninsure.cp.entity.cx;

import com.cninsure.cp.entity.BaseEntity;

public class CxInjuryMediateTaskEntity extends BaseEntity {

    public InjuryMediateTaskData data;

    public static class InjuryMediateTaskData extends CxTaskBaseEntity{

        public InjuryMediateWorkEntity contentJson;

    }

}
