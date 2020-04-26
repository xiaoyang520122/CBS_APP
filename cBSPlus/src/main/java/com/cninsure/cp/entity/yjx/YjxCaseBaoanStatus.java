package com.cninsure.cp.entity.yjx;

public class YjxCaseBaoanStatus {
	
	public static final int PREPARED = 0;//暂存
	public static final int DISPATCH = 1;//待调度
	public static final int WORK = 2;//作业
	public static final int WORKFINISH = 3;//作业完成
	public static final int FINISH = 4;//结案
//	public static final int REPORT_GENERATED = 5;//生成报告
	
	public static String getStausString(int i){
		switch (i) {
		case 0:
			return "暂存";
		case 1:
			return "待调度";
		case 2:
			return "已调度";
		case 3:
			return "作业完成";
		case 4:
			return "结案";
		default:
			return "状态未知";
		}
	}

}
