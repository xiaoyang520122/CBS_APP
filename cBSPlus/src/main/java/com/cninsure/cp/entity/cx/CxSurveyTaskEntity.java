package com.cninsure.cp.entity.cx;

import com.cninsure.cp.entity.BaseEntity;

public class CxSurveyTaskEntity extends BaseEntity {

       public CxTaskSurveyEntity data;

    public static class CxTaskSurveyEntity extends CxTaskBaseEntity  {
        public CxSurveyWorkEntity contentJson; //作业信息
    }
}
