package com.cninsure.cp.entity.fc;


import java.io.Serializable;
import java.util.Date;

/**
 * 数据Entity类
 * @author ThinkGem
 * @version 2013-05-28
 */
public abstract class DataEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String remarks;	// 备注
	protected Date createDate;// 创建日期
	protected Date updateDate;// 更新日期
	protected String delFlag; // 删除标记（0：正常；1：删除；2：审核）

}
