package com.cninsure.cp.entity.cx;

import com.cninsure.cp.entity.BaseEntity;

public class CxInjuryTrackTaskEntity extends BaseEntity {

    public InjuryTrackWorkEntity data;

    public static class InjuryTrackWorkEntity extends CxTaskBaseEntity {

        public CxInjuryTrackWorkEntity contentJson;  //作业内容

    }
}
