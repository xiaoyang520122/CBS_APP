package com.cninsure.cp.entity.cargo;

import java.io.Serializable;

/**
 * @author :xy-wm
 * date:2020/12/10 11:10
 * usefuLness: CBS_APP
 */
public class SurveyRecordsEntity implements Serializable {
    /**0集装箱，1非集装箱*/
    public String ckDocType; //是否集装箱
    public ContainerRecords records; // 集装箱
    public String recordDocUrl=""; // 集装箱文档路径
}
