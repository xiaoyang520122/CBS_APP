package com.cninsure.cp.entity;

public class PushType {

	/**
	 * 调度调度推送
	 */
	public static String NEW_ORDER 	= "NEW_ORDER";
	
	/**
	 * 后台主动调度取消推送
	 */
	public static String CANCEL_ORDER 	= "CANCEL_ORDER";
	
	/**
	 * 审核通过推送
	 */
	public static String AUDIT_PASS 	= "AUDIT_PASS";
	
	/**
	 * 审核驳回推送
	 */
	public static String AUDIT_REJECT 	= "AUDIT_REJECT";
	
	/**
	 * 公估师接单
	 */
	public static String GGS_RECEIVE 	= "GGS_RECEIVE";
	
	/**
	 * 公估师取消
	 */
	public static String GGS_CANCEL 	= "GGS_CANCEL";
	
	/**
	 * 公估师作业完成提交
	 */
	public static String GGS_WORK_SUBMIT 	= "GGS_WORK_SUBMIT";
	
	/**
	 * 公估师作业提价审核
	 */
	public static String GGS_WORK_AUDIT 	= "GGS_WORK_AUDIT";
}
