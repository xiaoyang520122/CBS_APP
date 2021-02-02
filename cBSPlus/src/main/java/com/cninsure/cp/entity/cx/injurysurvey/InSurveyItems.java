package com.cninsure.cp.entity.cx.injurysurvey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :xy-wm
 * date:2020/12/18 11:24
 * usefuLness: CBS_APP
 */
public class InSurveyItems implements Serializable {
    public String 	workTime	;//	作业时间
    public String 	workAddress	;//	作业地点
    public List<InSurveyAskList> askList;

    /**
     * 根据List位置，获取询问对象信息
     * @param askCount
     * @return
     */
    public InSurveyAskList getInSuAsk(int askCount) {
        if (askList ==null) askList = new ArrayList<>();
        if (askList.size()==0 || askCount<0){
            InSurveyAskList isaTemp = new InSurveyAskList();
            askList.add(isaTemp);
            return isaTemp;
        }else if (!(askCount< askList.size())){
            InSurveyAskList isaTemp = new InSurveyAskList();
            askList.add(isaTemp);
            return isaTemp;
        }else{
            return askList.get(askCount);
        }
    }
}
