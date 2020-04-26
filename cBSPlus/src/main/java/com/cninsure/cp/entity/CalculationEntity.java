package com.cninsure.cp.entity;

import java.util.List;

public class CalculationEntity extends FCBasicEntity {
	private static final long serialVersionUID = 1L;
	public List<CalculationData> data;
	
	public static class CalculationData{
		 public String remarks;//":null,
		 public String createDate;//":"2018-01-04 03:02:59",
		 public String updateDate;//":"2018-01-04 03:02:59",
		 public String delFlag;//":"0",
		 public int id;//":165595,
		 public long caseId;//":63199,
		 public String name;//":"童加松",
		 public String account;//":"tongjs",
//		 public int isZhuBan;//":100,
//		 public String isZhuBanP;//":null,
//		 public String onSite;//":1,
//		 public String onSiteP;//":null,
//		 public String dataCollection;//":100,
//		 public String dataCollectionP;//":null,
//		 public String aggregate;//":100,
//		 public String aggregateP;//":null,
//		 public String xunJiaDingSun;//":100,
//		 public String xunJiaDingSunP;//":null,
//		 public String communicateWithDeputer;//":1,
//		 public String communicateWithDeputerP;//":null,
//		 public String negotiationWithCustomer;//":1,
//		 public String negotiationWithCustomerP;//":null,
//		 public String writeReport;//":100,
//		 public String writeReportP;//":null,
//		 public String modifyRport;//":100,
//		 public String modifyRportP;//":null,
		 public float total;//":100,
//		 public String kaipiaoDate;//":"",
//		 public float money;//":0,
//		 public float daoZhangAmount;//":0,
//		 public String planYwl;//":null,
//		 public String realYwl;//":null,
//		 public String daoZhangDate;//":"",
	}
}
