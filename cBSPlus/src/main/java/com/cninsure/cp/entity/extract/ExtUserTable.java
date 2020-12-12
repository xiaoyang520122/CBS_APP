package com.cninsure.cp.entity.extract;

import java.io.Serializable;
import java.util.Date;

public class ExtUserTable implements Serializable {
    public Long id;
    public String uid;
    public Date createTime;
    public Date updateTime;
    public String userId;
    public String name;//公估师姓名
    public String idCard;//身份证号
    public String mobile;//电话号码
    public String deptOaId;//归属机构OA ID
    public Integer deptId;//归属营业部ID
    public String deptName;//归属营业部
    public String bankNo;//银行卡号
    public String bankName;//开户行
    public String bankBranchName;//开户支行
    public String idCardPhotoFront;//身份证照片-正面
    public String idCardPhotoBack;//身份证照片-反面
    public String bankCardPhoto;//银行卡照片
    public String agreementUrl;//协议PDF路径
    public Integer status;//签约状态 0未签约，1已签约
    public Integer isDelete;//是否注销
    public Double canPosAmount;//可提现金额
    public Double bondAmount;//保证金
    public Double accountTotalAmount;//账户总额
    /**保证金可提现状态
     *  public static final int CANT_POS = 0;//保证金与订单提成不可提现
     *   public static final int CAN_POS = 1;//提成可提现
     *   public static final int CAN_POS_BOND = 2;//可提现 提成与保证金*/
    public Integer posStatus;//提现状态
    public Double posAmount;//已提现金额
    public Double posBondAmount;//已提现保证金
    public String address;//通信地址
}
