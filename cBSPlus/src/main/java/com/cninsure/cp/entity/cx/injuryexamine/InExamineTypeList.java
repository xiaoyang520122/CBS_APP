package com.cninsure.cp.entity.cx.injuryexamine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :xy-wm
 * date:2020/12/18 11:21
 * usefuLness: CBS_APP
 */
public class InExamineTypeList implements Serializable {
    public String 	itemTitle	;//	调查类型标题
    public String 	itemValue	;//	调查类型值
    public List<InSurveyItems> items;

    /**
     * 根据List位置，获取调查地址信息
     * @param addressCount
     * @return
     */
    public InSurveyItems getInSuItem(int addressCount) {
        if (items==null) items = new ArrayList<>();
        if (items==null || items.size()==0 || addressCount<0){
            InSurveyItems isiTemp = new InSurveyItems();
            items.add(isiTemp);
            return isiTemp;
        }else if (!(addressCount<items.size())){
            InSurveyItems isiTemp = new InSurveyItems();
            items.add(isiTemp);
            return isiTemp;
        }else{
            return items.get(addressCount);
        }
    }
}
