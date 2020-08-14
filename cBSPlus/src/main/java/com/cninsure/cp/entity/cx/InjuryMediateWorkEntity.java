package com.cninsure.cp.entity.cx;

import java.util.List;

public class InjuryMediateWorkEntity {

    public String accidentTime;//	事故时间
    public String accidentAddress;//	事故地点
    public String partyOneName;//	甲(受害方)
    public Integer poSex;//	甲(受害方)性别
    public String poCardNo;//	身份证号
    public String poTel;//	电话
    public String poDuty;//	事故责任
    public String pooOccupation;//	职业
    public String poIncome;//	收入(月薪)
    public String poWorkCompany;//	工作单位
    public String poLiveAddress;//	现居地址
    public String poLiveStart;//	居住时间开始
    public String poLiveEnd;//	居住时间结束
    public String poHometown;//	户籍地
    public Integer poNature;//	户籍性质  0农业‘1非农业’2其他

    public String partyBName;//	乙(标的方)
    public Integer pbSex;//	乙(标的方)性别
    public String pbCardNo;//	身份证号
    public String pbTel;//	电话
    public String pbDuty;//	事故责任
    public String pbLetterNo;//	事故认定书编号
    public String pbSignDate;//	签署日期
    public String poSign;//	甲方签字
    public String pbSign;//	乙方签字

    public List<Integer> medicalFee;//	医疗费用赔偿 0、医疗费 1、诊疗费 2、住院费 3、整容费 4、住院伙食补助费 5、后续治疗费 6、营养费
    public List<Integer> casualtiesFee;//	死亡伤残赔偿  0、丧葬费 1、死亡补偿费 2、残疾赔偿金 3、交通费 4、护理费 5、康复费 6、残疾辅助器具费 7、住宿费 8、误工费 9、被抚养人生活费 10、受害人亲属办理丧葬事宜支出的交通费用

    public List<MedicalPaid> medicalPaid;
    public List<MedicalPaid> casualtiesPaid;

    public Float medicalSubtotal;//	医疗费用小计
    public Float casualtiesSubtotal;//	死亡伤残赔偿小计
    public Float total;//	总计
    public String DeductibleExplain;//	免赔说明

    public String agreementA;//	按事故责任比例计算后合计金额
    public String agreementB;//	甲方应赔偿乙方财产损失
    public String agreementC;//	乙方应赔偿甲方财产损失
    public String agreementD;//	乙方已经支付给甲方赔款
    public String agreementE;//	乙方支付时限日期
    public String agreementF;//	一次性赔偿甲方人身损失共计
    public String agreementG;//	开户行户名
    public String agreementH;//	开户行名称
    public String agreementI;//	开户行账号

    public Integer deliveryMode;//	快递方式
    public Integer deliveryCompany;//	快递公司
    public String deliveryNo;//	快递单号
    public String consignee;//	收货人
    public String shippingAddress;//	收货地址
    public String deliveryBill;//	快递单

    public List<String> enclosureList;//	附件信息

}
