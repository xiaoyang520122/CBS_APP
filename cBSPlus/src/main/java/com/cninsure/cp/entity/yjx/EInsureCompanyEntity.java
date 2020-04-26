package com.cninsure.cp.entity.yjx;

import java.io.Serializable;
import java.util.List;

public class EInsureCompanyEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	public TableData tableData;
	
	public static class TableData{
		public List<EWTRenDataEntity> data;
		public int length; //":10000,
		public int recordsFiltered; //":1,
		public int recordsTotal; //":8920,
		public int start; //":0
		
		public static class EWTRenDataEntity{
			public String createDate; //":"2018-09-04 12:00:00",
			
			public long id; //":"7748",
//			public int isLeaf; //":"1",
//			public int level; //":"1",
			/**保险公司名称**/
			public String name; //":"中国人寿保险股份有限公司深圳分公司",
			/**委托人简称*/
			public String shortName; //":"国寿财",
			/**归属主体（3为泛华公估）**/
			public String type; //":"0"
		}
	}

	
}
