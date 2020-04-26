package com.cninsure.cp.entity.fc;

public class CaseRelateDevote extends DataEntity {
	public static final long serialVersionUID = 1L;
	public Long id;
	public Long caseId;
	/** 公估师名称 */
	public String name="";
	/**登录名**/
	public String account="";
	/** 主办人 */
	public Double isZhuBan=0.0;
	public Double isZhuBanP;
	/** 现场处理 */
	public Integer onSite=0;
	public Double onSiteP;
	/** 资料收集 */
	public Double dataCollection=0.0;
	public Double dataCollectionP;
	/** 汇总 */
	public Double aggregate=0.0;
	public Double aggregateP;
	/** 询价定损 */
	public Double xunJiaDingSun=0.0;
	public Double xunJiaDingSunP;
	/** 保险人沟通 */
	public Integer communicateWithDeputer=0;
	public Double communicateWithDeputerP;
	/** 客户谈判确认 */
	public Integer negotiationWithCustomer=0;
	public Double negotiationWithCustomerP;
	/** 报告撰写装订 */
	public Double writeReport=0.0;
	public Double writeReportP;
	/** 报告校改 */
	public Double modifyRport=0.0;
	public Double modifyRportP;
	/** 汇总 */
	public Double total=0.0;
	public String kaipiaoDate="";
	public Double money;
	public Double daoZhangAmount;
	/** 业务量 */
	public Double planYwl;
	public Double realYwl;
	public String daoZhangDate="";
	public String filed1="";
	public String filed2="";
	public String filed3="";
	public String filed4="";
	/** 确认1待确认，2同意，3不同意 */
	public String insure="";

}