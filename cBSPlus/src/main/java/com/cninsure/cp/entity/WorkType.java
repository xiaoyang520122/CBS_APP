package com.cninsure.cp.entity;

import java.io.Serializable;
import java.util.List;

public class WorkType implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public TableData tableData;
	
	public static class TableData implements Serializable{
		private static final long serialVersionUID = 1L;
		public List<DataEntitiy> data;
		
		public static class DataEntitiy implements Serializable{
			private static final long serialVersionUID = 1L;
			/**照片类型*/
			public String description;
			/**照片类型id*/
            public int id;
            public String label;
            public int parentId;
            public String type;
            public String value;
		}
	}

}
