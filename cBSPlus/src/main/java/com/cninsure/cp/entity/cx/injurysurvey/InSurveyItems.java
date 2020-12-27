package com.cninsure.cp.entity.cx.injurysurvey;

import java.io.Serializable;
import java.util.List;

/**
 * @author :xy-wm
 * date:2020/12/18 11:24
 * usefuLness: CBS_APP
 */
public class InSurveyItems implements Serializable {
    public String 	workTime	;//	作业时间
    public String 	workAddress	;//	作业地点
    public List<InSurveyAskList> askLists;

}
