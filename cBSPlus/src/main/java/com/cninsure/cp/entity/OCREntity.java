package com.cninsure.cp.entity;


import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class OCREntity implements Serializable {

    private SimpleDateFormat sfSimple = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat sfStandard = new SimpleDateFormat("yyyy-MM-dd");

    public String getStandardBySimple(String SimpleDateString){
        try {
            return sfStandard.format(sfSimple.parse(SimpleDateString));
        } catch (ParseException e) {
            e.printStackTrace();
            return SimpleDateString;  //错误返回原字符串
        }
    }

	private static final long serialVersionUID = 1L;

	   //行驶证信息
    /**车牌号*/
    public String bdCarNumber; //车牌号
    /**车架号*/
    public String bdCarVin; //车架号
    /**发动机号*/
    public String bdEngineNo; //发动机号
    /**初登日期*/
    private String bdCarRegisterDate; //初登日期
//    /**行驶证有效期至*/
//    public String bdCarEffectiveDate; //行驶证有效期至
    /**准驾车型*/
    public String bdDrivingType; //准驾车型
    /**号牌种类*/
//    public String bdCarNumberType; //号牌种类
    /**使用性质 0运营、1非运营*/
    private Integer bdCarUseType; //使用性质 0运营、1非运营


    //驾驶证
    /**驾驶员姓名*/
    public String bdDriverName; //驾驶员姓名
    /**驾驶员电话*/
    public String  bdDriverPhone; //驾驶员电话
    /**驾驶证*/
    public String bdDriverNo; //驾驶证
    /**初次领证日期*/
    private String bdDriverRegisterDate; //初次领证日期
    /**有效起始日期*/
    private String bdDriverEffectiveStar; //有效起始日期
    /**驾驶证有效期至*/
    private String bdDriverEffectiveEnd; //驾驶证有效期至
//    /**车架号是否相符*/
//    public String bdCarVinIsAgreement; //车架号是否相符
//    /**行驶证是否相符*/
//    public String bdCardIsEffective; //行驶证是否相符
//    /**驾驶证是否相符*/
//    public String bdDriverIsEffective; //驾驶证是否相符
//    /**驾驶证是否相符*/
//    public String bdDrivingIsAgreement; //驾驶证是否相符

    // 银行卡信息
	/**"银行卡号"**/
  public String  insuredBankNo;//:"银行卡号"
  
  /**"图片路径，包括驾驶证、行驶证、银行卡、身份证和签字图片"**/
  public String  url;

    public void setBdCarUseType(String TypeStr) {
        if ("营运".equals(TypeStr)){
            bdCarUseType = 0;
        }else if("非营运".equals(TypeStr)){
            bdCarUseType = 1;
        }else{
            bdCarUseType = -1;
        }
    }

    public String getBdCarUseType() {
        switch (bdCarUseType){
            case 0:  return "营运";
            case 1:  return "非营运";

            default: return "未知";
        }
    }
    public Integer getBdCarUseTypeValue() {
        return bdCarUseType;
    }

    /**设置初登日期*/
    public void setBdCarRegisterDate(String bdCarRegisterDate) {
        this.bdCarRegisterDate = getStandardBySimple(bdCarRegisterDate);
    }
    /**获取初登日期*/
    public String getBdCarRegisterDate() {
        return bdCarRegisterDate;
    }

    /**初次领证日期*/
    public void setBdDriverRegisterDate(String bdDriverRegisterDate) {
        this.bdDriverRegisterDate = getStandardBySimple(bdDriverRegisterDate);
    }
    /**有效起始日期*/
    public void setBdDriverEffectiveStar(String bdDriverEffectiveStar) {
        this.bdDriverEffectiveStar = getStandardBySimple(bdDriverEffectiveStar);
    }
    /**驾驶证有效期至*/
    public void setBdDriverEffectiveEnd(String bdDriverEffectiveEnd) {
        this.bdDriverEffectiveEnd = getStandardBySimple(bdDriverEffectiveEnd);
    }
    /**初次领证日期*/
    public String getBdDriverRegisterDate() {
        return bdDriverRegisterDate;
    }
    /**有效起始日期*/
    public String getBdDriverEffectiveStar() {
        return bdDriverEffectiveStar;
    }
    /**驾驶证有效期至*/
    public String getBdDriverEffectiveEnd() {
        return bdDriverEffectiveEnd;
    }
}
