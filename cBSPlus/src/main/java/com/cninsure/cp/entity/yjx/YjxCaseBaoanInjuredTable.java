package com.cninsure.cp.entity.yjx;

import com.lidroid.xutils.db.annotation.Table;

@Table(name="yjx_case_baoan_injured")
public class YjxCaseBaoanInjuredTable {
	
	public Integer id;
	/**
	 * 归属接报案信息
	 */
	public Integer caseId;
	/**
	 * 伤者姓名(errorMessage="伤者姓名不能为空")
	 */
	public String name;
	/**
	 * 伤者身份证号(errorMessage="伤者身份证号不能为空")
	 */
	public String idCard;
	/**
	 * 伤者性别
	 */
	public String sex;
	/**
	 * 诊断(errorMessage="诊断信息不能为空")
	 */
	public String diagnostic;
	/**
	 * 就医医院(errorMessage="就医医院不能为空")
	 */
	public String hospital;
	
}
