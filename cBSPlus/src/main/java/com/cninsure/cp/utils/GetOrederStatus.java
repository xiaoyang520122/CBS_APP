package com.cninsure.cp.utils;

public class GetOrederStatus {
	
	public static String fromStatuId (int i){
		String results="未知状态";
		switch (i) {
		case 0:
			results= "待接单";
			break;
			
		case 1:
			results= "已结调度";
			break;
			
		case 2:
			results= "作业中";
			break;
			
		case 3:
			results= "公估师取消";
			break;
			
		case 4:
			results= "系统取消";
			break;
			
			
		case 5:
			results= "作业完成";
			break;
			
		case 6:
			results= "提交审核";
			break;
			
		case 7:
			results= "审核驳回";
			break;
			
		case 8:
			results= "审核通过";
			break;

		default:
			break;
		}
		return results;
	}
	public static String getFCStatus (int i){
		String results="未知状态";
		switch (i) {
		case 3:
			results= "作业待处理";
			break;
			
		case 4:
			results= "作业处理中";
			break;
			
		default:
			results= "未知状态";
			break;
		}
		return results;
	}
	
	/**获取医健险案件状态
	 * public static final int PREPARED = 0;//调度暂存
	public static final int INITIAL = 1;//调度发起（待处理）
	public static final int RECEIVE = 2;//接收（处理中）
	public static final int AUDITING = 3;//作业提交，审核中
	public static final int AUDITED = 4;//审核完成
	public static final int FIRST_AUDITED = 5;//一审完成
	public static final int ADOPT = 88;//审核驳回
	public static final int REFUSE = 99;//拒绝
	public static final int REDISPATCH = 999;//改派**/
	public static String getYjxStatus (int i){
		String results="未知状态";
		switch (i) {
		case 0:
			results= "调度暂存";
			break;
		case 1:
			results= "待接收";
			break;
		case 2:
			results= "作业中";
			break;
		case 3:
			results= "待审核";
			break;
		case 4:
			results= "审核完成";
			break;
		case 5:
			results= "一审完成";
			break;
		case 88:
			results= "审核驳回";
			break;
		case 99:
			results= "拒绝";
			break;
		case 999:
			results= "改派";
			break;
			
			
		default:
			results= "未知状态";
			break;
		}
		return results;
	}

}
