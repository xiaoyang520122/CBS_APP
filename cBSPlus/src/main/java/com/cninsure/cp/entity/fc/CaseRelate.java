package com.cninsure.cp.entity.fc;



/**
 * 检验师
 *
 */
public class CaseRelate extends DataEntity{


	public static final long serialVersionUID = 1L;
	public Long id;
	public String accounts ="";//登录名
	public String userName ="";//用户名称
	public String homeInstitution="";//归属机构
	public String linkTel =""; //联系电话
	public String dispatchStatus=""; //调度状态
	public String dispatchDate =""; //调度时间
	public String relType=""; //公估师类型
	
	public String workContent="";	//工作内容
	public String contributionRatio="";//贡献比率
	
}