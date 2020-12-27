package com.cninsure.cp.entity.cx.injurysurvey;

import java.io.Serializable;

/**
 * @author :xy-wm
 * date:2020/12/18 11:26
 * usefuLness: CBS_APP
 */
public class InSurveyAskList implements Serializable {
    public String 	askObj	;//	询问对象(调阅机构/护理人姓名)
    public String 	askTel	;//	对象电话(机构电话/护理人电话)
    public String 	workOrg	;//	工作单位
    public String 	workTimeStart	;//	护理时段始
    public String 	workTimeEnd	;//	护理时段止
}
