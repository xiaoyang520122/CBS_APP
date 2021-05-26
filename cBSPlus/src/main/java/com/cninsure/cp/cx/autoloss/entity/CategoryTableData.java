package com.cninsure.cp.cx.autoloss.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author :xy-wm
 * date:2021/5/24 22:01
 * usefuLness: CBS_APP
 */
public class CategoryTableData implements Serializable {
    public List<CategoryTable> data;
    public int length;  //40,
    public int recordsFiltered;  //3,
    public int recordsTotal;  //1668,
    public int start;  //0

    public String[] getArrsCateName(){
        if (data!=null && data.size()>0){
            String []nameArr = new String[data.size()];
            for (int i = 0;i<data.size();i++){
                nameArr[i] = data.get(i).cateName;
            }
            return nameArr;
        }else{
            return new String[0];
        }
    }
}
