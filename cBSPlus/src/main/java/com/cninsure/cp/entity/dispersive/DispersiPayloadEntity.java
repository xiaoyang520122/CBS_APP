package com.cninsure.cp.entity.dispersive;

import java.io.Serializable;
import java.util.Date;

public class DispersiPayloadEntity implements Serializable {

//    public DispersiveDispatchEntity.DispersiveDispatchItem data;
//    public String msg;  //
//    public boolean success;  //true

    public long id;
    /**
     * 案件标识(案件编号）
     */
    public String uid;

    /**
     * 主体标识
     */
    public String tenantId;

    /**
     * 创建时间
     */
    public Date createDate;

    /**
     * 创建人
     */
    public String createBy;

    /**
     * 更新时间
     */
    public Date updateDate;

    /**
     * 更新人
     */
    public String updateBy;

    /**
     * 接报案编号
     */
    public String baoanUid;

    /**
     * 报案号
     */
    public String baoanNo;

    /**
     * 归属机构ID
     */
    public Integer gsOrgId;

    /**
     * 归属机构
     */
    public String gsOrg;

    /**
     * 产品大类ID：3
     */
    public Integer caseTypeId;

    /**
     * 产品大类: 创新及分散型业务
     */
    public String caseType;

    /**
     * 险种大类ID
     */
    public Integer insuranceBigTypeId;

    /**
     * 险种大类
     */
    public String insuranceBigType;

    /**
     * 险种细类ID
     */
    public Integer insuranceSmallTypeId;

    /**
     * 险种细类
     */
    public String insuranceSmallType;

    /**
     * 查勘员ID
     */
    public String ggsId;

    /**
     * 查勘员名称 相当于公估师
     */
    public String ggsName;

    /**
     * 查勘员联系电话
     */
    public String ggsTel;

    /**
     * 委托人ID
     */
    public Integer wtId;

    /**
     * 委托人名称 即保险公司
     */
    public String wtName;

    /**
     * 委托联系人 即保险公司联系人
     */
    public String wtContact;

    /**
     * 委托联系人电话
     */
    public String wtContactTel;

    /**
     * 现场联系人
     */
    public String sceneId;

    /**
     * 现场联系人名称
     */
    public String sceneName;

    /**
     * 现场联系人电话
     */
    public String sceneTel;

    /**
     * 改派ID
     */
    public Integer reDispatchId;
    /**
     * 查勘员佣金
     */
//    public Double commissionFee;

    /**
     0暂存  0;
     1 已调度（待接受）;
     2 公估师接受（作业中） ;
     3 作业提交，审核中  ;
     4 审核完成  ;
     5 审核驳回  ;
     6 公估师拒绝  ;
     7 拒绝再改派  ;
     8 公估师取消  ;
     9 撤单（取消）再改派  ;
     10 公估师超时  ;
     11 公估师到达现场  ;
     12 超时再改派  ;
     */
    public Integer status;

    /**
     * 是否删除
     */
    public Integer delFlag;

    /**
     * 查勘地点 默认与出险地点一致，可视为出险地点
     */
//    public String exploreAddress;

    /**
     * 受损基本情况
     */
    public String damagedState;

    /**
     * 查勘重点要求
     */
    public String keyPoint;

    /**
     * 若查勘员点击取消任务，此字段用来记录拒绝原因
     */
    public String remark;

    /** 查勘地点*/
    public String surveyAddr;
    /**查勘难度：简单0，一般1，特殊2*/
    public Integer difficultyLevel;
    /**查勘员佣金*/
    public Double surveyorFee;
    /**查勘重点要求*/
    public String majorClaims;
    /** 是否本地*/
    public Integer local;

    /**
     * 重新调度uid
     */
    public String redispatchUid;
    public String takerId;
    /**
     *  案件对接人、后台负责人
     */
    public String takerName;
    /**
     * 案件对接人电话
     */
    public String takerTel;
    /**
     * 省
     */
    public String province;
    /**
     * 省编码
     */
    public String provinceCode;
    /**
     * 市
     */
    public String city;
    /**
     * 市编码
     */
    public String cityCode;
    /**
     * 区
     */
    public String district;
    /**
     * 区编码
     */
    public String districtCode;

/**经度*/
    public Float longitude;
    /**纬度*/
    public Float latitude;




    /**机构间结算价*/
    public Double balancePrice; // 机构间结算价
    /**奖励/扣款*/
    public Double reward; // 奖励/扣款
    /***超时*/
    public Date timeout;

}
