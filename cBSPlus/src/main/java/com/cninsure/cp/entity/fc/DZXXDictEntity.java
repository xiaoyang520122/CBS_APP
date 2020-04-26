package com.cninsure.cp.entity.fc;

import java.util.List;

import com.cninsure.cp.entity.FCBasicEntity;

public class DZXXDictEntity extends FCBasicEntity {

	private static final long serialVersionUID = 1L;

	public List<DZXXData> data;
	
	public static class DZXXData{
		public String id;	//":7,
		public String projectNo;	//":"LX20162000100001",
		public String projectName;	//":"“莫兰蒂”台风",
		public String caseNum;	//":550,
		public String gsOrg;	//":3,
		public String projectDesc;	//":"福建",
		public String remarks;	//":"",
		public String createBy;	//":1,
		public String createDate;	//":"2016-10-04",
		public String filed1;	//;	//":"",
		public String filed2;	//":"",
		public String filed3;	//":"",
		public String filed4;	//":"",
		public String filed5;	//":"",
		public String filed6;	//":"",
		public String filed7;	//":""
	}
}
