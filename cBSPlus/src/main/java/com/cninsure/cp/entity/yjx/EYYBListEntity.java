package com.cninsure.cp.entity.yjx;

import java.io.Serializable;
import java.util.List;

public class EYYBListEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	public TableData tableData;
	
	public static class TableData{
		public List<EYYBDataEntity> data;
		
		public int length; //":10000,
		public int recordsFiltered; //":1,
		public int recordsTotal; //":8920,
		public int start; //":0
		
		public static class EYYBDataEntity{
			public long id; //":"75",
//			public long isLeaf; //":"1",
//			public long level; //":"1",
			/**营业部名称**/
			public String name; //":"车险业务管理处",
            public long parentId; //":"73"
		}
	}

	
}
