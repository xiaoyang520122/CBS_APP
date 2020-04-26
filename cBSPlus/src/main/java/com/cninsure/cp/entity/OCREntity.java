package com.cninsure.cp.entity;

import java.io.Serializable;

public class OCREntity implements Serializable {

	private static final long serialVersionUID = 1L;

	   //行驶证信息	
	/**"车牌号码"**/
  public String bdCarNumber;//:"车牌号码",
	/**"车架号"**/
  public String  bdCarVin;//: "车架号",
	/**"发动机号码"**/
  public String  bdEngineNo;//: "发动机号码",

    //驾驶证信息
	/**"驾驶证号码"**/
  public String  bdDriverLincense;//:"驾驶证号码",
	/**"准驾车型"**/
  public String  bdDrivingType;//:"准驾车型",
	/**"驾驶员"**/
  public String bdDriverName;//:"驾驶员",
    
    // 银行卡信息
	/**"银行卡号"**/
  public String  insuredBankNo;//:"银行卡号"
  
  /**"图片路径，包括驾驶证、行驶证、银行卡、身份证和签字图片"**/
  public String  url;

}
