package com.cninsure.cp.entity.fc;



public class CaseOperation extends DataEntity{


	public static final long serialVersionUID = 1L;
	public Long id;
	public Long caseId;
	public String caseNo="";
	public String belongToOrg ="";
	public String agencyNo ="";
	
	public String hostChecker ="";
	public Integer insuranceTypeId ;
	
	public String inflowDate="";
	public String reportStatus ="";
	public String agency ="";
	public String caseName ="";
	public String policyNumber ="";
	
	public String applicantName="";
	public String applicantLinkTel="";
	public String insuredName="";
	public String insuredLinkTel="";
	
	public String insuredAdd="";
	public String insuredBussiness="";
	public String freeOdds="";
	
	public String absoluteDeductible="";
	
	public String insuranceDate;
	public String protectionDate ;
	public String endDate;
	
	public String insuranceAmount="";
	public String premium ="";
	public String specialAgreement ="";
	public String status="";
	public String mainBill="";
	public String mainKey="";
	
	public String inWay="";
	public String outWay="";
	public String operation ="";
	public String immenseOption="";
	public String opDate="";//立案时间
	public String casenycd="";
	
}