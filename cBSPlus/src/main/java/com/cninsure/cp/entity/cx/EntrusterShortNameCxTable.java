package com.cninsure.cp.entity.cx;

import java.io.Serializable;
import java.util.Date;

/**
 * 委托人简称-车险
* <br/>
* Copyright: Copyright (c) 2020 fanhuaholding.com
* @author: zoutaodong
* @date: 2020年6月22日 下午4:01:50
 */
//@Table(name="tb_common_entruster_short_name_cx")
public class EntrusterShortNameCxTable implements Serializable {
	
    public Integer id;
	public String uid;
	public Date createTime;
	public String createBy;
	public Date updateTime;
	public String updateBy;
    public String tenantId;
    public String name;//名称
    public String code;//编码
    public Integer settleType;//结算类型
    public String workRequirements;//作业要求
    public String dockingPersonId;//对接人ID
    public String dockingPerson;//对接人
    public String dockingPersonMobile;//对接人电话
    public Integer isDelete;//是否删除
    public Integer fgsId;//归属分公司ID
    public String fgsName;//归属分公司
    
}