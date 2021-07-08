package com.cninsure.cp.entity.cx;

import java.io.Serializable;
import java.util.Date;

/**
 * @author :xy-wm
 * date:2021/7/5 9:59
 * usefuLness: CBS_APP
 */
public class CxNewAuditEntity implements Serializable {
    public Integer id;
    public String orderUid;//订单编号
    public String auditerUid;//审核人UID
    public String auditerName;//审核人姓名
    public String auditerMobile;//审核人电话
    public Date auditTime;//审核时间
    public Integer status;//审核状态
    public Integer type;//审核类型；0：人员手工审核；1：系统自动审核
    public String auditMsg;//审核建议
    public String backSummary;//退回概述
    public String backReasonIds;//退回原因IDS
    public String backReasons;//退回原因
    public String firstLevelReason;//一级退回原因
    public Double rewards;//奖惩

    public String isAddTrackOrder;  ////是否添加跟踪流(0:否 ; 1:是)
    public String trackOrder;  //



}
