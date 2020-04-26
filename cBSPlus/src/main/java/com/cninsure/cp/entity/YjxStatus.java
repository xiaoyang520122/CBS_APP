package com.cninsure.cp.entity;

public class YjxStatus {

	/**调度暂存*/
	public static final int PREPARED = 0;//调度暂存
	/**待接收*/
	public static final int INITIAL = 1;//调度发起（待处理）
	/**作业中*/
	public static final int RECEIVE = 2;//接收（处理中）
	/**待审核*/
	public static final int AUDITING = 3;//作业提交，审核中
	/**审核完成*/
	public static final int AUDITED = 4;//审核完成
	/**一审完成*/
	public static final int FIRST_AUDITED = 5;//一审完成
	/**审核驳回*/
	public static final int ADOPT = 88;//审核驳回
	/**拒绝*/
	public static final int REFUSE = 99;//拒绝
	/**改派*/
	public static final int REDISPATCH = 999;//改派

}
