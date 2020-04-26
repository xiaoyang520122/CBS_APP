package com.cninsure.cp.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	public Data data;

	public static class Data {
		/** 流水号 */
		public Integer id;
		/** CBS用户Oid */
		public String targetOid;
		/** 用户标识 */
		public String userId;
		/** 租户ID */
		public String tenantId;
		/** 主体名称 **/
		public String tenantName;
		/** 主体编号 **/
		public String tenantPinyinInitials;
		/** 主体简称 **/
		public String tenantShortName;
		/** 归属机构 */
		public Integer organizationId;
		/** 归属机构名称 */
		public String organizationSelfName;
		/** 登录机构（权限机构） */
		public String organizationLoginId;//改为了String-20180119
		/** 登录机构名称 */
		public String organizationLoginName;
		/** 创建时间 */
		public Date createDate;
		/** 修改时间 */
		public Date updateDate;
		/** 登录名 */
		public String loginName;
		/** 密码 */
		public String password;
		/** 用户名称 */
		public String name;
		/** 邮件地址 */
		public String email;
		/** 电话 */
		public String mobile;
		/** 类型(1超级管理员账号，2普通管理员账号，3普通用户账号) */
		public Integer type;
		/** 身份证号码 */
		public String idCard;
		/** 是否删除 */
		public Integer isDelete;
		/** 备注 */
		public String remarks;
		/** 角色(,1,2,3,4,) */
		public String roleIds;
		/** （最后）地点经度 */
		public Double locationLongitude;
		/** （最后）地点纬度 */
		public Double locationLatitude;
		/** 区域ID（三级联动） */
		public Integer regionId;
		/** 区域名称（三级联动） */
		public String regionName;
		/** 区域ID（字典表中获取） */
		public Integer quyuId;
		/** 区域名称 （字典表中获取） */
		public String quyuName;
		/**APP中部分接口获取到的图片地址需要加上次前缀方可访问**/
		public String qiniuUrl;
		
		
		public String  productTypes; //; //,1,2,3,",
		public String  targetType; //1",
		/**"99=外部车童"1=系统管理"2=部门经理"3=普通用户"4=自有公估师*/
		public String  userType;

	}

}