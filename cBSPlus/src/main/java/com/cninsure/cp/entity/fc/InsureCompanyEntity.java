package com.cninsure.cp.entity.fc;

import java.util.List;

import com.cninsure.cp.entity.FCBasicEntity;

/**保险公司实体类**/
public class InsureCompanyEntity extends FCBasicEntity {

	private static final long serialVersionUID = 1L;
	
	public WTRenData data;
	
	public static class WTRenData{
		public List<WTRenDataEntity> list;
		
		public static class WTRenDataEntity{
			public long id;
			/**保险公司名称**/
			public String name;
			/**删除标记 */
			public String delFlag="";//":"0",
			/**委托人联系人名称*/
			public String master="";//":"",
			/**委托人联系人电话*/
			public String phone="";//":"",
			/**委托人联系人传真*/
			public String fax="";//":"",
			/**委托人联系人email*/
			public String email="";//":"",
			/**委托人简称*/
			public String shortName="";//":"其他",
			
		}
	}
	
}
