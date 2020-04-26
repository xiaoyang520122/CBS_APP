package com.cninsure.cp.entity.fc;


import java.io.Serializable;

public class APPRequestModel<T> implements Serializable {
	
	private static final long serialVersionUID = -8069135583303639649L;
	
	public String userToken;
	public T requestData;

}
