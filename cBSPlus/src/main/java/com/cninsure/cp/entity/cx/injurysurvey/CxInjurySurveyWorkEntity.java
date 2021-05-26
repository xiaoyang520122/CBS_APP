package com.cninsure.cp.entity.cx.injurysurvey;

import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.entity.BaseEntity;

import java.util.List;

/**
 * @author :xy-wm
 * date:2021/2/4 11:17
 * usefuLness: CBS_APP
 */
public class CxInjurySurveyWorkEntity extends BaseEntity {

//个人信息
    public String	injuredType	;//	伤者类型
    public String	injuredName	;//	伤者姓名
    public String	injuredSex	;//	伤者性别
    public String	injuredAge	;//	伤者年龄
    public String	injuredTel	;//	伤者电话
    public String	idcardType	;//	证件类型
    public String	idcardNo	;//	证件号码
    public String	birthDate	;//	出生年月
    public String	householdNature	;//	户籍性质
    public String	contactName	;//	联系人姓名
    public String	contactTel	;//	联系人电话
    public String	contactRelationship	;//	与伤者关系
//居住信息
    public String	liveTimeStart	;//	居住时间从
    public String	liveTimeEnd	;//	居住时间止
    public String	liveAddress	;//	居住地点
    public String	provePerson	;//	证明人

    public List<CxInjurySurveyHospitalInfos> hospitalInfos;  //医院信息
//工作信息
    public String	isHaveJob	;//	有无工作
    public String	noJobRemark	;//	无工作备注
    public String	companyName	;//	单位名称
    public String	companyAddress	;//	单位地址
    public String	income	;//	收入情况
    public String	isPaySocialSecurity	;//	缴纳社保
    public String	jobTimeStart	;//	工作时间始
    public String	jobTimeEnd	;//	工作时间止
    public String	salaryType	;//	工资发放
    public String	jobProvePerson	;//	证明人
    public List<CxInjurySurveyFamilyMembers> familyMembers; //家庭成员
//事故信息
    public String	accidentTime	;//	事故时间
    public String	accidentProvince	;//	事故地点省
    public String	accidentProvinceNo	;//	事故地点省编号
    public String	accidentCity	;//	事故地点市
    public String	accidentCityNo	;//	事故地点市编号
    public String	accidentAddress	;//	事故详细地址
    public String	policeCase	;//	出警情况
    public String	specialCaseRemark	;//	特殊情况备注
    public String	carColor	;//	标的车颜色
    public String	driverSex	;//	标的司机特征
    public String	traitDescribe	;//	特征描述
    public String	bdCarNo	;//	标的车牌号
    public String	bdCarModels	;//	车型
    public String	woundedIdentity	;//	伤者身份
    public String	trafficState	;//	交通状态
//需求信息
    public String	isNeedAdvanceMedicalFee	;//	垫付医疗费
    public String	isSatisfiedCurrentHospital	;//	当前就诊医院
    public String	isNeedTransferService	;//	转院服务
    public String	nursingWorkerType	;//	护工类型
    public String	dietaryDemand	;//	饮食需求
    public String	hospitalNecessities	;//	住院必需品
    public String	isNeedAfterLiveSouce	;//	伤后生活来源
    public String	isNeedAdvanceLiveFee	;//	垫付生活费
    public String	otherDemand	;//	其他需求
    // 损失信息
    public String	treatCase	;//	治疗情况
    public String	treatWay	;//	治疗方式
    public String	surgeryName	;//	手术名称
    public String	internalFixation	;//	内固定
    public String	jointReplacement	;//	关节置换
    public String	visceralInjury	;//	脏器损伤
    public String	forecastMedicalFee	;//	预计医疗费
    public String	isNeedSecondSurgery	;//	二次手术
    public String	injuryDagnosis	;//	伤情诊断
    public String	sendDiagnosisWay	;//	送诊方式
    public String	traumaHistory	;//	既往外伤史
    public String	injuredBodyTypeIds	;//	损伤类型id
    public List<CxInjuredBodyTypes> injuredBodyTypes; //损伤类型
    public String	titleId	;//	损伤类型标题Id
    public String	title	;//	损伤类型标题
    public String	injuredCase	;//	损伤情况

    public String	ggsSignatureUrl	;//	查勘员签名
    public String	injuredSignatureUrl	;//	伤者或家属签名
    public String	injuredSignatureIDCard	;//	伤者签名方身份证号

    public String	riskInfoAttr1	;//	用户对于慰问过程是否人为抗拒抵触
    public String	riskInfoAttr2	;//	是否流露出已经有人承诺费用及其他
    public String	riskInfoAttr3	;//	用户是否已经暗示不希望保险公司和自己接触
    public String	riskInfoAttr4	;//	床位旁边是否有某某律师（法律）事务所宣传手册
    public String	riskInfoAttr5	;//	探视期间是否有号称某某律师的出现

    public String	expressWay	;//	快递方式
    public String	expressCompany	;//	快递公司
    public String	expressNo	;//	快递单号
    public String	expressReceiver	;//	收货人
    public String	expressAddress	;//	收货地址
    public String	expressProvince	;//	省
    public String	expressProvinceNo	;//	省编码
    public String	expressCity	;//	市
    public String	expressCityNo	;//	市编码
    public String	expressArea	;//	区
    public String	expressAreaNo	;//	区编码
    public String	expressNoUrl	;//	快递单

    public String	enclosureList	;//	附件信息
    public String	voiceNoteList	;//	语音
    public String	surveyAddress	;//	人伤查勘地点

    public String	deliveryMode	;//	快递方式
    public String	deliveryCompany	;//	快递公司
    public String	deliveryNo	;//	快递单号
    public String	consignee	;//	收货人
    public String	shippingAddress	;//	收货地址
    public String	deliveryBill	;//	快递单
//    public String	enclosureList	;//	附件信息列表

    public String areaNo;
    public String area;
    public String province;
    public String caseProvince;
    public String city;

}
