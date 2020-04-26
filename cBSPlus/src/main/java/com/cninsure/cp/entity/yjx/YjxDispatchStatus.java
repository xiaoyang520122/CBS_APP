package com.cninsure.cp.entity.yjx;

public class YjxDispatchStatus {
	
	public static final int PREPARED = 0;//调度暂存
	public static final int INITIAL = 1;//调度发起（待处理）
	public static final int RECEIVE = 2;//接收（处理中）
	public static final int AUDITING = 3;//作业提交，审核中
	public static final int AUDITED = 4;//审核完成
	public static final int FIRST_AUDITED = 5;//一审完成
	public static final int ADOPT = 88;//审核驳回
	public static final int REFUSE = 99;//拒绝
	public static final int REDISPATCH = 999;//改派
	
	
	public static String getStatuString(int status){
		switch (status) {
		case 0:
			return "调度暂存";

		case 1:
			return "待处理";

		case 2:
			return "作业中";

		case 3:
			return "等待审核";

		case 4:
			return "审核完成";

		case 5:
			return "一审完成";

		case 88:
			return "审核驳回";

		case 99:
			return "退回";

		case 999:
			return "改派";

		default:
			return "状态未知";
		}
		
	}

}
