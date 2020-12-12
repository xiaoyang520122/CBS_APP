package com.cninsure.cp.entity.cargo;

public class CargoCaseBaoanStatus {
	
	public static final int ENTRUST = 0;//委托
	public static final int SUBMIT = 1;//提交
	public static final int GGS_ACCEPT = 2;//公估师接收
	public static final int GGS_REFUSE = 3;//公估师拒绝
	public static final int DISPATCH = 4;//调度查勘员
	public static final int SURVEY_ACCEPT = 5;//查勘员接收
	public static final int SURVEY_REFUSE = 6;//查勘员拒绝
	public static final int WORK_SUBMIT = 7;//作业提交
	public static final int AUDIT_CLAIMED = 8;//审核认领
	public static final int AUDIT_PASS = 9;//审核通过
	public static final int AUDIT_ADOPT = 10;//审核退回
	public static final int FIX_DUTY = 11;//定责
	public static final int LOSS = 12;//定损
	public static final int ADJUSTMENT = 13;//理算
	public static final int WAIT_FINISH = 14;//待结案（理算完成）
	public static final int FINISH = 15;//结案

}
