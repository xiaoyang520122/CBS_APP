package com.cninsure.cp.entity;

import java.io.Serializable;

public class Case implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public DataCase data;
	
	public static class DataCase{
		/**委托协议ID（合同）*/
		public String agreementId="";
		/**委托协议名称（合同）*/
		public String agreementName="";
		/**报案时间*/
		public String baoanDate="";
		/**报案号*/
		public String baoanNo="";
		/**标的车型*/
		public String biaoDiCarType="";
		/**商业赔案号*/
		public String businessLossNumber="";
		/**商业保单号*/
		public String businessPolicyNumber="";
		/**业务类型ID*/
		public int bussTypeId;
		/**业务名称*/
		public String bussTypeName="";
		/**出险时间*/
		public String caseDate="";
		/**出险经过*/
		public String caseLifecycle="";
		/**出险地点*/
		public String caseLocation="";
		/**出险经地点全称*/
		public String caseLocationFull="";
		/**地点纬度*/
		public String caseLocationLatitude="";
		/**地点经度*/
		public String caseLocationLongitude="";
		/**案件备注*/
		public String caseRemarks="";
		/**险种类型ID*/
		public int caseTypeId;
		/**险种类型名称*/
		public String caseTypeName="";
		/**确认公估费*/
		public float confirmGgFee;
		/**联系人名称*/
		public String contactsName="";
		/**联系人电话*/
		public String contactsPhone="";
		/**创建时间*/
		public String createDate="";
		/**委托时间*/
		public String entrusterDate="";
		/**委托人ID*/
		public int entrusterId;
		/**委托人名称*/
		public String entrusterName="";
		/**个案委托人名称*/
		public String entrusterNamePersonage="";
		/**保险公司坐席号码*/
		public String entrusterPhone="";
		/**产品ID*/
		public int ggProductId;
		/**产品名称*/
		public String ggProductName="";
		/**流水号*/
		public long id;
		/**承保地点*/
		public String insuredLocation="";
		/**被保险人*/
		public String insuredPerson="";
		/**是否删除*/
		public int isDelete;
		/**是否二次委托(1是，0否)*/
		public int isRepeatEntrust;
		/**是否现场(1是，0否)*/
		public int isScene;
		/**标地车牌*/
		public String licensePlateBiaoDi="";
		/**三者车牌*/
		public String licensePlateSanZhe="";
		/**归属营业部ID*/
		public int organizationId;
		/**营业部名称*/
		public String organizationName="";
		/**交强赔案号*/
		public String saliLossNumber="";
		/**交强保单号*/
		public String saliPolicyNumber="";
		/**服务类型（默认1车险业务2非车财险业务3非车水险业务）*/
		public int agreementType;
		/**标准公估费*/
		public String standardGgFee="";
		/**案件状态*/
		public int status;
		/**主体标识*/
		public String tenantId="";
		/**案件标识(案件编号）*/
		public String uid="";
		/**更新时间*/
		public String updateDate="";
		/**预警级别(默认为0，5黄色预警，10红色预警）*/
		public int warningLevel;
		/**结案时间*/
		public String caseFinishTime="";

	}

}
