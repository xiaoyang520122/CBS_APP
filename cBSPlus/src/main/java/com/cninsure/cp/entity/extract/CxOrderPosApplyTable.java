package com.cninsure.cp.entity.extract;

import java.util.Date;

/**
 * 订单提现记录
 *
 * @Report(title=public String 公估师提现清单 sheetName=public String 公估师提现清单public String )
 * @Table(name=public String cx_order_pos_applypublic String )
 */
public class CxOrderPosApplyTable {

    public Long id;
    public String uid;
    public String applyTime;//申请提现时间
    public String auditTime;//审核时间
    public String payTime;//审核时间
    public Integer orgId;//公估师归属营业部ID
    public String orgName;//公估师归属营业部名称
    public String ggsUid;//公估师ID
    public String ggsName;//公估师名称
    public Double posAmount;//提现金额
    public Double passAmount;//审核通过金额
    public Double adoptAmount;//审核不通过金额
    public Integer status;//状态
    public String payFailReason;//支付失败原因
    public String idCard;//身份证号
    public String mobile;//电话号码
    public String bankNo;//银行卡号
    public String bankName;//开户行
    public String bankBranceName;//开户支行
    public Integer type;//类型；0：正常审核；1：系统自动审核

    public Integer posBondType;//0绩效提现：1、保证金提到总公司账号；2、保证金提到个人账户

    public String getPosBondType() {
        if (posBondType != null) {
            switch (posBondType) {
                case 0: return "绩效提取";
                case 1: return "保证金提到公司";
                case 2: return "保证金提取";
                default:  return "未知申请！";
            }
        } else {
            return "未知申请！";
        }
    }
}
