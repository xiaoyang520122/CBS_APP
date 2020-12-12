package com.cninsure.cp.entity.cargo;

import java.io.Serializable;
import java.util.Date;


public class CargoCaseWorkSurveyTable implements Serializable {
	
	public Long id;
	public String createBy;
	public Date createTime;
	public String updateBy;
	public Date updateTime;
	public Long caseId;
	public String caseNo;//案件编号
	public String surveyRecords;//查勘记录
	public String listRecords;//清点记录
	public String askRecords;//问询记录
	public String lossRecords;//损失清单
	public String materialList;//理赔材料清单
	public Integer status;//状态
	
}
