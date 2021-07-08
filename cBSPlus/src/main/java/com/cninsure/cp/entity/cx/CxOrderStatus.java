package com.cninsure.cp.entity.cx;

/**
 * 订单状态
* <br/>
* Copyright: Copyright (c) 2020 fanhuaholding.com
* @author: zoutaodong
* @date: 2020年7月8日 下午5:09:01
 */
public class CxOrderStatus {
	public static final int WAIT_DISPATCH = 1;//待调度
	public static final int DISPATCHED = 2;//已调度
	public static final int DISPATCH_FAIL = 3;//调度受阻
	public static final int ACCEPT = 4;//已接单
	public static final int REFUSE = 5;//拒绝
	public static final int WORK = 6;//作业中
	public static final int WAIT_AUDIT = 7;//作业待审核
	public static final int NO_AUDITER = 8;//作业待审核-未找到审核人
	public static final int PASS = 9;//作业审核通过
	public static final int ADOPT = 10;//作业审核退回
	public static final int REMOVE = 99;//移除
	//86撤单审核未找到审核人
	// 88撤单审核通过
}
