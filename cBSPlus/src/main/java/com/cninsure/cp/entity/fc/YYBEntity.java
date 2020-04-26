package com.cninsure.cp.entity.fc;

import java.io.Serializable;
import java.util.List;

public class YYBEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public YYBtableData tableData;
	
	public static class YYBtableData{
		public List<YYBDataEntity> data;
		
		public static  class YYBDataEntity{
			 public long id;
             public String isLeaf;
             public String level;
             /**营业部名称**/
             public String name;
             public String parentId;
		}
	}

}
