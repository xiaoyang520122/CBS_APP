package com.cninsure.cp.entity.fc;

import java.io.Serializable;

public class APPResponseModel<T> implements Serializable {
	
	private static final long serialVersionUID = 839035436643717833L;
	
	public String code;/* 返回码 */
	public String message;/* 返回信息 */
	public String exception;/* 返回异常信息 */
	public T data;/* 返回数据列表 */
	
}
