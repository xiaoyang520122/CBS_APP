package com.cninsure.cp.entity.cargo;

import android.content.Intent;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 派工事项
 * @author :xy-wm
 * date:2020/12/8 14:08
 * usefuLness: CBS_APP
 */
public class DispatchMatter {

    private Map<Integer,String> matterMap;

    public DispatchMatter(){
        getMatter();
    }

    public Map<Integer,String> getMatter(){
        matterMap = new HashMap<>(6);
        matterMap.put(0,"询问出险时间");
        matterMap.put(1,"确定出险地点");
        matterMap.put(2,"出险经过");
        matterMap.put(3,"确定运输工具");
        matterMap.put(4,"施救措施");
        matterMap.put(5,"损失数量核定");
        matterMap.put(6,"了解运输总数量");
        return matterMap;
    }

    public String getMatterForString(String matterStrArr){
        if (TextUtils.isEmpty(matterStrArr)) return "";
        String matterStr = "" ;
        String[] matterArr = matterStrArr.split(",");
        if (matterArr!=null){
            for (String code:matterArr)
            matterStr += (matterMap.get(Integer.valueOf(code))+",");
        }
        return matterStr;
    }

    /**
     * 返回样式举例
     *询问出险时间
     * 确定出险地点
     * 出险经过
     * 确定运输工具
     * 施救措施
     * 损失数量核定
     * 了解运输总数量
     * */
    public String getMatterlistString(String matterStrArr){
        String matterStr = getMatterForString(matterStrArr) ;
        matterStr = matterStr.replace(",","\n");
        return matterStr;
    }
}
