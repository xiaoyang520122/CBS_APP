package com.cninsure.cp.entity.yjx;

import com.lidroid.xutils.db.annotation.Id;

//@Table(name="yjx_case_work_injured")
public class YjxWorkInjuredEntity {
	
	@Id
	public Integer id;
	/**
	 * 归属接报案信息
	 */
	public Integer caseId;
	/**
	 * 归属作业信息
	 */
	public Integer workId;
	/**
	 * 伤者姓名
	 */
//	@NotNullValidator(errorMessage="伤者姓名不能为空")
	public String name;
	/**
	 * 伤者身份证号
	 */
//	@NotNullValidator(errorMessage="伤者身份证号不能为空")
	public String idCard;
	/**
	 * 伤者性别
	 */
	public String sex;
	/**
	 * 诊断
	 */
//	@NotNullValidator(errorMessage="诊断信息不能为空")
	public String diagnostic;
	/**
	 * 就医医院
	 */
//	@NotNullValidator(errorMessage="就医医院不能为空")
	public String hospital;
	
}
