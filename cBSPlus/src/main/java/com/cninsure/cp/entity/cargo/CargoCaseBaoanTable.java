package com.cninsure.cp.entity.cargo;

import java.io.Serializable;
import java.math.BigDecimal;


public class CargoCaseBaoanTable implements Serializable {
	
	public Long id;
	public String createBy;//创建人
	public String createTime;//创建时间
	public String updateBy;//更新人
	public String updateTime;//更新时间
	public String caseNo;//案件编号
	public String caseName;//案件名称
	public Integer gsOrgId;//归属机构ID
	public String gsOrgName;//归属机构名称
	public Long wtId;//委托人ID
	public String wtName;//委托人
	public String wtShortName;//委托人简称
	public String wtContractName;//委托联系人
	public String wtContractMobile;//委托联系人手机
	public String wtContractEmail;//委托联系人邮箱
	public String wtContractWechat;//委托联系人微信
	public String wtContractAddress;//委托联系人地址
	public Integer wtTypeId;//委托人属性ID
	public String wtTypeName;//委托人属性
	public String payer;//付款人
	public Integer insuranceTypeId;//险种ID
	public String insuranceTypeName;//险种
	public Integer riskReasonId;//事故原因ID
	public String riskReasonName;//事故原因
	public String policyNo;//保单号
	public String reportNo;//报案号
	public String riskTime;//出险时间
	public String riskAddress;//出险地点
	public String province;//省
	public String city;//市
	public String areaCode;//区编码
	public String areaName;//区
	public String insured;//被保险人
	public String insuredContract;//被保险人联系人
	public String insuredContractMobile;//被保险人联系人电话
	public String wtItems;//委托人事项
	public String wtNote;//委托注意事项
	public Integer isAssignGgs;//是否指派公估师
	public String ggsUid;//主办公估师UID
	public String ggsName;//主办公估师名称
	public String ggsMobile;//主办公估师电话
	public String ggsEmail;//主办公估师邮箱
	public Long damageSubjectId;//受损标的ID
	public String damageSubject;//受损标的
	public String wtTime;//委托时间
	public Integer acceptType;//接收选项；0：平台受理派工；1：公估师全流程受理；9：拒接案件
	public String refuseReason;//拒绝原因
	public String surveyTime;//查勘时间
	public String surveyAddress;//查勘地点
	public String dispatchMatter;//派工事项
	public String dispatchMatterAdd;//派工事项补充
	public BigDecimal surveyPrice;//查勘费用
	public String surveyPriceOverTimeReason;//查勘费用超过原因
	public String surveyUid;//选派车童UID
	public String surveyName;//选派车童
	public String auditerUid;//审核人UID
	public String auditerName;//审核人
	public String auditClaimTime;//审核认领时间
	public String lossSituation;//损失情况
	public BigDecimal claimAmount;//索赔金额
	public BigDecimal gsAmount;//估损金额
	public Integer delFlag;//是否删除
	public Integer status;//状态
	
}
