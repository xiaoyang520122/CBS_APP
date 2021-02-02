package com.cninsure.cp.entity.cargo;

import java.io.Serializable;
import java.util.List;

/**
 * 货运险询问笔录
 * @author :xy-wm
 * date:2021/1/27 14:58
 * usefuLness: CBS_APP
 */
public class SurveyAskRecordsEntity implements Serializable {

    public String askDate; // 询问日期
    public List<String> askTimeRange; // 询问时间段
    public String address; // 地址
    public String askPeople; // 询问人
    public String recordPeople; // 记录人
    public String askedPeople; // 被询问人
    public String askedSex; // 被询问人性别
    public String idCardType; // 被询问人证件类型
    public String idCardNo; // 被询问人证件号码
    public String householdAddress; // 户籍所在地
    public String currentAddress; // 现住址
    public String company; // 工作单位
    public String askedPhone; // 联系方式
    public String caseName; // 案件名称
    public List<QandAEntity> questionsAndAnswers;// 问答集合
    public String signatureUrl;// 签名图片
    public String recordDocUrl; // 文档路径
}
