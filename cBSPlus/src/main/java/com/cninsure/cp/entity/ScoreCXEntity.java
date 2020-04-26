package com.cninsure.cp.entity;

import java.io.Serializable;

public class ScoreCXEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public ScoreCXData data;

	
	public static class ScoreCXData{
		  	public String billUid;
		  	public String caseFinishTime;
		  	public String caseNo;
		  	public String createDate;
		  	public String ggsIdCard;
		  	public String ggsName;
		  	public String ggsUid;
		  	public String id;
		  	public String type;
		  	public String uid;
		  	public String updateDate;
		  	public String wtId;
		  	public String wtName;
		  	public String wtSalesCarFee;
		  	public String wtSalesGasolineFee;
		  	public String wtSalesGgsSalary;
		  	/**创收**/
		  	public String yuguGgAmount="0.0";
		  	/**车**/
		  	public String zySalesCarFee="0.0";
		  	/**油**/
		  	public String zySalesGasolineFee="0.0";
		  	/**公估师薪酬**/
		  	public String zySalesGgsSalary="0.0";
	}
}
