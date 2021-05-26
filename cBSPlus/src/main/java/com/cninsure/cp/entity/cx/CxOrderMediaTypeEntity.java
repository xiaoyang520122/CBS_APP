package com.cninsure.cp.entity.cx;

import android.text.TextUtils;

import com.cninsure.cp.entity.BaseEntity;

import java.util.List;

/**
 * @author :xy-wm
 * date:2021/5/18 11:29
 * usefuLness: CBS_APP
 */
public class CxOrderMediaTypeEntity extends BaseEntity {
    public List<CxOrderWorkMediaTypeTable> data;

    /**
     * 这里获取的是保存图片路径需要的参数fullPath值，如“基础资料;驾驶证正面”，图片类型全路径，中间用分号隔开
     * 这个值取当前分类parentPathNames+lable，中间用分号隔开。 parentPathNames 如：",车险,基本资料,查勘照片,"
     * @param type
     * @return
     */
    public String getFullPathByValue(String type){
        for (CxOrderWorkMediaTypeTable mediaTem:data){
            if(type.equals(mediaTem.value)){
                if (!TextUtils.isEmpty(mediaTem.parentPathNames)){
                    String []pNams = mediaTem.parentPathNames.split(",");
                    String fullPath = "";
                    for (String name:pNams){
                        if (!TextUtils.isEmpty(name)){
                            fullPath += (TextUtils.isEmpty(fullPath)?"":";") + name;
                        }
                    }
                    fullPath += (TextUtils.isEmpty(fullPath)?"":";") + mediaTem.label;
                    return TextUtils.isEmpty(fullPath)?"默认分类":fullPath;
                }else {
                    return "默认分类";
                }
            }
        }
        return "默认分类";
    }
}
