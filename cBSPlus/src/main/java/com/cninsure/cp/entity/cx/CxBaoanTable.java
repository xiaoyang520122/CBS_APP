package com.cninsure.cp.entity.cx;

import java.io.Serializable;
import java.util.Date;

/**
 * 接报案信息
* <br/>
* Copyright: Copyright (c) 2020 fanhuaholding.com
* @author: zoutaodong
* @date: 2020年6月30日 下午2:46:13
 */
//@Table(name="cx_baoan")
public class CxBaoanTable implements Serializable {
	
	public Integer id;
	public String uid;
	public String tenantId;
	public String createBy;
	public Date createTime;
	public String updateBy;
	public Date updateTime;
	public Integer status;//状态
	public Integer productId;//产品细类ID，根据委托人属性（是否作业地结算、作业及承保是否同以机构）由系统自动判断
	public String productType;//产品细类，根据委托人属性（是否作业地结算、作业及承保是否同以机构）由系统自动判断
	public String bussTypeIds;//任务类型ID（原业务品种）任务类型限制为以下几种：现场查勘、标的定损、三者定损、物损、人伤查勘。一个案件现场查勘、标的定损任务只有一个（其他可多个）
	public String bussTypes;//任务类型（原业务品种）任务类型限制为以下几种：现场查勘、标的定损、三者定损、物损、人伤查勘。一个案件现场查勘、标的定损任务只有一个（其他可多个）
	public String acceptInsuranceUid;//承保机构UID
	public String acceptInsurance;//承保机构
	public Integer wtId;//委托人ID
	public String wtName;//委托人名称
	public String wtShortNameId;//委托人简称
	public String wtShortName;//委托人简称
	public Integer local;//是否本地，根据委托人属性（是否作业地结算、作业及承保是否同以机构）由系统自动判断
	public String caseBaoanNo;//报案号
	public Integer deptId;//归属营业部ID 	归属营业部根据产品细类、作业范围自动判断
	public String deptName;//归属营业部名称	归属营业部根据产品细类、作业范围自动判断
	public Integer orgId;//作业营业部ID 	
	public String orgName;//作业营业部名称
	public String wtDate;//委托时间
	public String baoanPerson;//报案人（联系人）
	public String baoanPersonMobile;//报案人电话（联系人电话）
	public String drivePerson;//驾驶人（伤者姓名）
	public String drivePersonMobile;//驾驶人电话（伤者电话）
	public String dockingPerson;//对接
	public String dockingPersonMobile;//对接人电话
	public Date riskDate;//出险时间
	public String biaodiCarNo;//标的车牌
	public String caseAddress;//出险地点
	public String caseProvince;//所在省
	public String caseCity;//所在市
	public String caseArea;//出险区域
	public String areaNo;//出险区域编码
	public String districtType;//作业范围
	public String districtTypeName;//作业范围名称
	public String caseDetail;//出险经过
	public Integer autoDispatch;//是否自动调度
	public Double longtitude;
	public Double latitude;
	public String investigationType;//调查类型
	public String investigations;//调查内容
	public String hospital;//医院名称
	public String admissionNumber;//住院号
	public String bedNumber;//床位号
	public String department;//科室
	public String expressProvince;//快递信息-省
	public String expressCity;//快递信息-市
	public String expressArea;//快递信息-区
	public String expressAreaNo;//快递信息-区编码
	public String expressAddress;//快递信息-详细地址
	public String expressReceiver;//快递信息-收件人
	public String expressMobile;//快递信息-联系电话
	public Integer visitCount;//探视次数
	
}
