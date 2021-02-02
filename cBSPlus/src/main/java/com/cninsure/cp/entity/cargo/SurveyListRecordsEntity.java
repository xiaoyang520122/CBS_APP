package com.cninsure.cp.entity.cargo;

import java.io.Serializable;
import java.util.List;

/**
 * @author :xy-wm
 * date:2021/1/27 15:10
 * usefuLness: CBS_APP
 */
public class SurveyListRecordsEntity implements Serializable {

    public String insured; // 被保险人
    public String address; // 清点地址
    public String projectType; // 项目类别
    public List<ListRecordsEntity> listRecords;//清单记录
    public String signatureUrl; // 签名图片
    public String inventoryDate;
    public String recordDocUrl; // 文档路径
}
