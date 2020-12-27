package com.cninsure.cp.entity.cx.injurysurvey;

import java.io.Serializable;
import java.util.List;

/**
 * @author :xy-wm
 * date:2020/12/18 11:21
 * usefuLness: CBS_APP
 */
public class InSurveyTypeList implements Serializable {
    public String 	itemTitle	;//	调查类型标题
    public String 	itemValue	;//	调查类型值
    public List<InSurveyItems> items;
}
