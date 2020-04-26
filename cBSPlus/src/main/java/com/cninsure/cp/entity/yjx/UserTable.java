package com.cninsure.cp.entity.yjx;

import java.util.Date;

public class UserTable {
    /**
    * 流水号
    */
    public Integer id;

    /**
    * 用户标识
    */
    public String userId;

    /**
    * 租户ID
    */
    public String tenantId;

    /**
    * 归属机构
    */
    public Integer organizationSelfId;

    /**
    * 归属机构名称
    */
    public String organizationSelfName;

    /**
    * 登录机构（权限机构）
    */
    public String organizationLoginId;

    /**
    * 登录机构名称
    */
    public String organizationLoginName;

    /**
    * 创建时间
    */
    public Date createDate;

    /**
    * 修改时间
    */
    public Date updateDate;

    /**
    * 登录名
    */
    public String loginName;

    /**
    * 密码
    */
    public String password;

    /**
    * 用户名称
    */
    public String name;

    /**
    * 邮件地址
    */
    public String email;

    /**
    * 电话
    */
    public String mobile;

    /**
    * 类型(1超级管理员账号，2普通管理员账号，3普通用户账号，4千县万店用户)
    */
    public Integer type;

    /**
    * 身份证号码
    */
    public String idCard;

    /**
    * 是否删除
    */
    public Integer isDelete;

    /**
    * 备注
    */
    public String remarks;

    /**
    * 角色(,1,2,3,4,)
    */
    public String roles;

    /**
    * 角色名称
    */
    public String rolesName;

    /**
    * （最后）地点经度
    */
    public Double locationLongitude;

    /**
    * （最后）地点纬度
    */
    public Double locationLatitude;

    /**
    * 区域ID（三级联动）
    */
    public Integer regionId;

    /**
    * 区域名称（三级联动）
    */
    public String regionName;

    /**
    * 区域ID（字典表中获取）
    */
    public Integer quyuId;

    /**
    * 区域名称 （字典表中获取）
    */
    public String quyuName;

    /**
    * 客户端_编码
    */
    public String clientId;

    /**
    * 目标Oid
    */
    public Integer targetOid;

    /**
    * 【0:平台车险】【1:CBS】
    */
    public Integer targetType;

    /**
    * 产品类型（,1,2,3,）
    */
    public String productTypes;

    /**
    * 目标角色（,1,2,3,）
    */
    public String targetRoles;

    /**
    * 全部角色名称(不参与业务逻辑）
    */
    public String allRoleNames;

    /**
    * 服务大类（,1,2,3,）
    */
    public String serviceCategoryIds;

    /**
    * 有效案件量
    */
    public Integer passOrderQuantity;

    /**
    * 评论总分
    */
    public Double starScoreSum;

    /**
    * 评论次数
    */
    public Integer starScoreCount;

    /**
    * 评论平均分
    */
    public Double starScoreAverage;

    /**
    * 用户OAID
    */
    public String oaUserId;

    /**
    * 用户状态
    */
    public Integer status;

    /**
    * 收款人姓名
    */
    public String payeeUserName;

    /**
    * 收款人身份证号码
    */
    public String payeeUserIdcard;

    /**
    * 收款人（开户银行）
    */
    public String payeeUserBankName;

    /**
    * 收款人（开户支行）
    */
    public String payeeUserBankBranch;

    /**
    * 收款人（银行账号）
    */
    public String payeeUserBankNumber;

    /**
    * 外部车童99，系统管理1，部门经理2，普通用户3，自有公估师4
    */
    public Integer userType;
}