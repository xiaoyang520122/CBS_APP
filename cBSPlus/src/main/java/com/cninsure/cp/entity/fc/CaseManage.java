package com.cninsure.cp.entity.fc;



public class CaseManage extends DataEntity{

	public static final long serialVersionUID = 1L;
	public String casePick="";//报案摘要
	public String deputeItem="";//委托事项
	public String urgencyLevel="";//紧急程度
	public String deputeLinkEmail="";//委托人联系人EMAIL
	public String remark="";
	public String remarks="";
	public Integer shipType;//船舶类型
	public String dangerPeriod;//出险日期
	public String caseNo="";//案件编号
	public String filed6="";
	public String preLossAmount="";//预计损失金额
	public Integer goodsUnit;//货物单位
	public String revCaseType="";//接案方式
	public String filed4="";
	public String filed5="";
	public String preCheckDate="";//预计查勘日期
	public Integer filed2;
	public String filed3="";
	public Integer filed1;
	public Long id;
	public String shipDw="";//船舶吨位
	public String deputeDate="";//委托日期
	public String payerLink="";//付款方联系人
	public String deputeShipname="";//委托方船名
	public String lossAmout="";//报损金额
	public String policyNo="";//保单号
	public Integer goodsNum;//货物数量
	public String preDangerDate="";//水险出险日期
	public Integer businessType;//业务类型
	public Integer preLossBz;//预计损失币种
	public String caseName="";//案件名称
	public Integer mainLossType;//主要受损类型
	public String insurerCaseNo="";//保险公司报案号
	public String payer="";//付款方
	public Integer riskType;//险种大类
	public Integer status;//状态
	public String deputeLinkPhone="";//委托人联系人手机
	public String mainGoodsType="";//主要货物类型
	public String lossCurrency="";//报损币种
	public String checkAdd="";//查勘地点
	public String dangerAdd="";//出险地点
	public String deputeCaseNo="";//委托人案件编号
	public String revCaseDate="";//接案日期
	public String dangerRes="";//出险原因
	public Integer caseType=0;//案件类型
	public String immenseOption="";//大灾选项
	public String deputeSf="";//委托人身份
	public String deputeLinkTel="";//委托人联系人电话
	public String deputePer="";//委托人
	public String deputeLnikZz="";//委托人联系人传真 
	public String deputeLinkPer="";//委托人联系人
	public String gsOrg ="";//归属机构
	public String insurerCaseLno="";//保险公司立案号
	public String recCasePer="";//接案人
	public String fgLinker="";//付款方联系人
	public String fgLinkTel="";//付款方联系人电话

	public String endMoney="";//存放审批额度
	public String outType="";//出单类型
	
	public String feicheCaseAccount="";//估损范围
	public String  caseIndustry="";//所属行业
	public String  feicheBaoxianType="";//险种
	
	public String othCompany ="";//经济公司或企业集团
	public Long caseId ;
	public String picino =""; //批次号
	
	public String deputeId="";//委托人ID
//	public String file="";//开票回执附近
//	
//	
//	
//	
//	public String getFile() {
//		return file="";
//	}
//
//	public void setFile(String file) {
//		this.file = file="";
//	}
	
	public String reciverPhone ="";//接案人电话
	public String wtfYq ="";//委托方要求
	public String carNo ="";//车牌号
	public String cxUintLink ="";//出险单位联系人
	public String lxPhone ="";//联系人电话
	public String emial ="";//邮箱
	public String cxJg ="";//出险经过
	
	public String isYd ="";//是否异地1本地，2异地
	
	public String vatCompany="";//增值税开票的公司名称
	
	public int caseLibType;//案件库类型 0 不在案件库里 1 学习类案件资料库 2 经典案例
	public String securityDate="";//学习类案件的保密时间
	public String introduction="";//经典案例简介
	public String description="";//经典案例描述
	public int isCollection;//是否收藏 0 没收藏 1 收藏
	

}