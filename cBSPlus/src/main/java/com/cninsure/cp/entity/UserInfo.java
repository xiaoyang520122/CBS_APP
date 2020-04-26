package com.cninsure.cp.entity;

import java.io.Serializable;

public class UserInfo implements Serializable {

	public static final long serialVersionUID = 1L;
	
	public Data data;

	public static class Data  implements Serializable  {

		private static final long serialVersionUID = 1L;
		/** 流水号 */
		public String id;//
		/** 用户标识 */
		public String userId;
		/** 租户ID */
		public String tenantId;
		/** 归属机构 */
		public String organizationSelfId;//
		/** 归属机构名称 */
		public String organizationSelfName;//
		/** 登录机构（权限机构） */
		public String organizationLoginId;//
		/** 登录机构名称 */
		public String organizationLoginName;//
		/** 创建时间 */
		public String createDate;//
		/** 修改时间 */
		public String upDateDate;
		/** 登录名 */
		public String loginName;//
		/** 密码 */
		public String password;
		/** 用户名称 */
		public String name="";//
		/** 邮件地址 */
		public String email;//
		/** 电话 */
		public String mobile;//
		/** 类型(1超级管理员账号，2普通管理员账号，3普通用户账号) */
		public String type;
		/** 身份证号码 */
		public String idCard="";
		/** 是否删除 */
		public String isDelete;
		/** 备注 */
		public String remarks;//
		/** 角色(,1,2,3,4,) */
		public String roleIds;//
		/** CBSPlus角色名称 */
		public String rolesName;//
		/** CBS角色名称 */
		public String allRoleNames;//
		/** （最后）地点经度 */
		public Double locationLongitude;
		/** （最后）地点纬度 */
		public Double locationLatitude;
		/** 区域ID（三级联动） */
		public String regionId;//
		/** 区域名称（三级联动） */
		public String regionName;//
		/** 区域ID（字典表中获取） */
		public String quyuId;//
		/** 区域名称（字典表中获取） */
		public String quyuName;//
		/** 客户端_编码 */
		public String clientId;
		/** 支行名称 */
		public String payeeUserBankBranch;//":null,
		/** 银行名称 */
		public String payeeUserBankName;//":null,
		/** 银行卡号 */
		public String payeeUserBankNumber;//":null,
		/** 持卡人身份证号 */
		public String payeeUserIdcard;//":null,
		/** 持卡人姓名 */
		public String payeeUserName;//":null,

	}
}
