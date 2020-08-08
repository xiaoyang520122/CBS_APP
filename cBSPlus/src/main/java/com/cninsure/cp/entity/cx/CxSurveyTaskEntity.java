package com.cninsure.cp.entity.cx;

import com.cninsure.cp.entity.BaseEntity;

public class CxSurveyTaskEntity extends BaseEntity {

       public CxTaskEntity data;

    public static class CxTaskEntity extends CxTaskBaseEntity  {
        public CxSurveyWorkEntity contentJson; //作业信息
    }
}
