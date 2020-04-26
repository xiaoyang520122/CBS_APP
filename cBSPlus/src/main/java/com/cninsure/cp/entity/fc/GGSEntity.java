package com.cninsure.cp.entity.fc;

import java.io.Serializable;
import java.util.List;

public class GGSEntity implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	public GGSTableData tableData;
	
	public static class GGSTableData{
		public List<GGSData> data;
		
		public static class GGSData{
			public String createDate;
			public long id;
			public String loginName;
            /**公估师姓名**/
			public String name;
			public String organizationLoginId;
			public String organizationLoginName;
			/**公估师归属营业部**/
			public String organizationSelfName;
			public String productTypes;
			public String quyuId;
			public String quyuName;
			public String regionId;
			public String regionName;
			public String serviceCategoryIds;
			public String targetRoles;
			public String userId;//User-20180103101825-2D38079B"
		}
	}

}
