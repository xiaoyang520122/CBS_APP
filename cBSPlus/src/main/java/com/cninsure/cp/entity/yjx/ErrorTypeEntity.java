package com.cninsure.cp.entity.yjx;

import java.io.Serializable;
import java.util.List;

public class ErrorTypeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public ErrortableData tableData;
	
	public static class ErrortableData{
		public List<ErrorType> data;
		
		public static class ErrorType{
			 public String description;  //":"差错类型",
			 public String id;  //":"38",
			 public String label;  //":"A类: 照片审核类",
			 public String parentId;  //":"null",
			 public String type;  //":"errorType",
			 public String value;  //":"1"
		}
//		"length":50,
//        "recordsFiltered":5,
//        "recordsTotal":144,
//        "start":0
	}
	
}
