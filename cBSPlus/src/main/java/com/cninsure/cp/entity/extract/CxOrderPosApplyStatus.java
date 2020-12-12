package com.cninsure.cp.entity.extract;

/**
 * 提现申请状态
* <br/>
* Copyright: Copyright (c) 2020 fanhuaholding.com
* @author: zoutaodong
* @date: 2020年10月30日 下午4:57:11
 */
public class CxOrderPosApplyStatus {
	
	public static final int APPLY = 0;//申请
	public static final int AUDIT_COMPLETE = 1;//完成审核
	public static final int PAY_FAIL = 5;//支付失败
	public static final int PAY_SUCCESS = 6;//支付成功

			public static String getExtractApplyStatus(int code){
				switch (code){
					case 0: return "待审核";
					case 1: return "待支付";
					case 2: return "审核驳回";
					case 5: return "支付失败";
					case 6: return "已支付";
			default: return"未知！";
		}
	}

}
