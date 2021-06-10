package com.cninsure.cp.entity.cx;

import java.io.Serializable;

/**
 * @author :xy-wm
 * date:2021/5/26 20:44
 * usefuLness: CBS_APP
 */
public class CxTaskModelEntity implements Serializable {
    public String bussType;  //现场查勘",
    public Integer bussTypeId;  //2,
    public String content;  //作业内容数据JSON字符串
    public String orderUid;  //任务UID
    public Object contentJson; //作业内容储存对象
}
