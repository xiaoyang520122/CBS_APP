package com.cninsure.cp.entity.cx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CxSurveyWorkEntity implements Serializable {


    public String creatDate; //添加时间
    public String updateDate;//更新时间
    public String orderUid;  //作业标识(任务订单号码)

    public String areaNo;
    public String area;
    public String province;
    public String caseProvince;
    public String city;

    public SurveyInfoEntity surveyInfo; //查勘信息
    public SubjectInfoEntity subjectInfo; //定损信息
    public List<ThirdPartyEntity> thirdPartys; //三者信息
    public List<InjuredInfosEntity> injuredInfos; //人伤信息
    public List<DamageInfosEntity> damageInfos; //物损信息

    /**
     * 查勘信息
     */
    public static class SurveyInfoEntity {
        public String orderUid; //	作业标识(任务订单号码)
        public String ckDate; //	查勘时间	yyyy-mm-dd HH:mm:ss）
        public Integer ckAccidentType=-1; //	事故类型	0、单方；1、injuredInfos
        public Integer ckAccidentSmallType=-1; //	事故详细类型	0碰撞、1火烧、2自然灾害
        public String ckAccidentReason; //	出险原因	行使受损、停放受损、水淹、火灾、车身人为划痕、玻璃单独损坏、车辆盗抢、重大自然灾害、其他
        public String ckAccidentSmallReason; //	出险详细原因
        public Integer surveyType=-1; //	查勘类型	0现场查勘、1非现场查勘、2补勘现场
        public Integer ckAccidentLiability=-1; //	事故责任	0全责（固定100%），1主责（默认70%，准许录入在5-100）、2同责（固定50%）、3次责（30%，0-50）、4无责（固定0%）
        public String liabilityRatio; //	事故责任比例	比例是数值（0 ~ 100）【 0全责（固定100%），1主责（默认70%，准许录入在5-100）、2同责（固定50%）、3次责（30%，0-50）、4无责（固定0%）】
        public Integer [] lossType; //	损失类型	0三者、1物损、2人伤
        public Integer []lossObjectType; //	损失情况	三者：0三者车损；物损：1标的车物品、2三者车内物、3三者车外物；人伤：4本车司机、5本车乘客、6三者车内人，7其他三者人伤
        public String baoanDriverName; //	报案驾驶员
        public Integer canDriveNormally=-1; //	车辆能否正常行驶	1是，0否
        public Integer compensationMethod=-1; //	赔付方式	0按责赔付、1互碰自赔
        public Integer ckIsInsuranceLiability=-1; //	是否属于保险责任	1是，0否
        public Integer isDaiwei=-1; //	是否代位	1是，0否
        public String lossAmount; //	估损金额
        public Integer ckIsMajorCase=-1; //	是否重大案件	1是，0否
        public Integer isScene=-1; //	是否现场报案	1是，0否
        public Integer isHsLoad=-1; //	是否在高速公路	1是，0否
        public Integer surveyConclusion=-1; //	查勘结论	……
        public String surveyAddress; //	查勘地点
        public String surveySummary; //	查勘概述
        public String signLicense; //	签字照片链接	保存作业图片接口返回字段fileUrl
        public String signPhotoId; //	签字照片id	保存作业图片接口返回字段id
        public List<String> enclosureList = new ArrayList<>(); //附件集合
        public boolean customerNotice; //客户告知书阅读状态

        /**因后台需要字典值value传String类型，这里做转化*/
        public String getCkAccidentLiability() {
            return ckAccidentLiability==null?"":ckAccidentLiability+"";
        }/**因后台需要字典值value传String类型，这里做转化*/
        public String getCkAccidentSmallType() {
            return ckAccidentSmallType==null?"":ckAccidentSmallType+"";
        } /**因后台需要字典值value传String类型，这里做转化*/
        public String getCkAccidentType() {
            return ckAccidentType==null?"":ckAccidentType+"";
        } /**因后台需要字典值value传String类型，这里做转化*/
        public String getSurveyType() {
            return surveyType==null?"":surveyType+"";
        }/**因后台需要字典值value传String类型，这里做转化*/
        public String[] getLossType() {
            if (lossType==null) return new String[]{};
            String[] lossTypeStrArr = new String[lossType.length];
            for (int i=0;i<lossType.length;i++){lossTypeStrArr[i]=lossType[i]+"";}
            return lossTypeStrArr;
        }/**因后台需要字典值value传String类型，这里做转化*/
        public String[] getLossObjectType() {
            if (lossObjectType==null) return new String[]{};
            String[] lossObjectStrArr = new String[lossObjectType.length];
            for (int i=0;i<lossObjectType.length;i++){lossObjectStrArr[i]=lossObjectType[i]+"";}
            return lossObjectStrArr;
        }/**因后台需要字典值value传String类型，这里做转化*/
        public String getCompensationMethod() {
            return compensationMethod+"";
        }
    }

    /***标的信息*/
    public static class SubjectInfoEntity {
        public Integer isLicenseKou=0; //	双证被扣	1是，0否
        public String bdCarNumber; //	车牌号
        public String bdCarVin; //	车架号
        public String bdEngineNo; //	发动机号
        public String bdCarRegisterDate; //	初登日期	yyyy-mm-dd
        public String bdCarEffectiveDate; //	行驶证有效期至	yyyy-mm-dd
        public String bdDriverNo; //	驾驶证号
        public String bdDriverName; //	驾驶员姓名
        public String bdDriverPhone; //	驾驶员电话
        public String bdDrivingType; //	准驾车型
        public String bdDriverRegisterDate; //	初次领证日期	yyyy-mm-dd
        public String bdDriverEffectiveStar; //	有效起始日期	yyyy-mm-dd
        public String bdDriverEffectiveEnd; //	驾驶证有效期至	yyyy-mm-dd
        public String insuredPersonName; //	持卡人姓名
        public String insuredBankDeposit; //	开户行
        public String insuredBankNo; //	银行卡号
        public Integer bdCarNumberType=-1; //	号牌种类	0小型家用车、1客车、2货车、3特种车、4其他
        public Integer bdCarUseType=-1; //	使用性质	0运营、1非运营
        public Integer bdCarVinIsAgreement=0; //	车架号是否相符	0未验、1相符、2不符
        public Integer bdCardIsEffective=0; //	行驶证是否有效	0未验、1有效、2无效
        public Integer bdDrivingIsAgreement=0; //	准驾车型是否相符	0未验、1相符、2不符
        public Integer bdDriverIsEffective=0; //	驾驶证是否有效	0未验、1有效、2无效
        public String pathDriverLicense; //	驾驶证链接	保存作业图片接口返回字段fileUrl
        public String pathDriverPhotoId; //	驾驶证图像Id	保存作业图片接口返回字段id
        public String pathMoveLicense; //	行驶证链接	保存作业图片接口返回字段fileUrl
        public String pathMovePhotoId; //	行驶证图像Id	保存作业图片接口返回字段id
        public String bankCarLicense; //	银行卡链接	保存作业图片接口返回字段fileUrl
        public String bankCarPhotoId; //	银行卡图像Id	保存作业图片接口返回字段id

        /**因后台需要字典值value传String类型，这里做转化*/
        public String getBdCarUseType() {
            return bdCarUseType==null?"":bdCarUseType+"";
        } /**因后台需要字典值value传String类型，这里做转化*/
        public String getBdCarNumberType() {
            return bdCarNumberType==null?"":bdCarNumberType+"";
        }
    }


    /**
     * 三者信息 thirdPartys（集合）
     */
    public static class ThirdPartyEntity {
        public Integer thirdPartysNo; //	序号	从1开始
        public String carNumber; //	车牌号
        public String frameNumber; //	车架号
        public String engineNumber; //	发动机号
        public String szCarRegisterDate; //	初登日期	yyyy-mm-dd
        public String szCarEffectiveDate; //	行驶证有效期至	yyyy-mm-dd
        public String driverLicense; //	驾驶证号
        public String carPerson; //	驾驶员姓名
        public String carPersonPhone; //	驾驶员电话
        public String drivingMode; //	准驾车型
        public Integer szCarNumberType=-1; //	号牌种类	0小型家用车、1客车、2货车、3特种车、4其他
        public Integer szCarUseType=-1; //	使用性质	0运营、1非运营
        public String szDriverRegisterDate; //	初次领证日期	yyyy-mm-dd
        public String szDriverEffectiveStar; //	有效起始日期	yyyy-mm-dd
        public String szDriverEffectiveEnd; //	驾驶证有效期至	yyyy-mm-dd
        public String pathDriverLicense; //	驾驶证链接	保存作业图片接口返回字段fileUrl
        public String pathDriverId; //	驾驶证图像ID	保存作业图片接口返回字段id
        public String pathMoveLicense; //	行驶证链接	保存作业图片接口返回字段fileUrl
        public String pathMoverId; //	行驶证图像ID	保存作业图片接口返回字段id
        public Integer szisLicenseKou=0; //	是否双证被扣	1是，0否
        /**因后台需要字典值value传String类型，这里做转化*/
        public String getSzCarUseType() {
            return szCarUseType==null?"":szCarUseType+"";
        }
        public String getSzCarNumberType() {
            return szCarNumberType==null?"":szCarNumberType+"";
        }

    }

    /**
     * 人伤信息
     */

    public static class InjuredInfosEntity{
        public	Integer	injuredInfoNo	; //	序号	从1开始
        public	String	injuredName	; //	姓名
        public	String	injuredCarNo	; //	身份证号
        public	Integer	injuredType=-1	; //	伤者类型	0本车司机、1本车乘客、2三者车内人伤、3其他三者人员
        public	String	injuredPhone	; //	伤者电话
        public	Integer	isQuickPaid=-1	; //	是否选择快赔	1是，0否
        public	String	quickPaidResult	; //	快赔结果

        public String getInjuredType() {
            return injuredType==null?"":injuredType+"";
        }
    }

    /**
     * 物损信息
     */
    public static class DamageInfosEntity{
        public	Integer	damageNo	; //	序号	从1开始
        public	String	damageOwner	; //	归属人
        public	String	damageObjectName	; //	物损名称
        public	Integer	damageType	=-1; //	损失类型	0标的车物品、1三者车内物、2三者车外物
        public	String	damageOwnerPhone	; //	物主电话

    }

}
