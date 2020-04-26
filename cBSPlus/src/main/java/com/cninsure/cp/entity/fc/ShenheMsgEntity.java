package com.cninsure.cp.entity.fc;

import java.io.Serializable;
import java.util.List;

public class ShenheMsgEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public SHHTableData tableData;
	
	public static class SHHTableData{
		public List<SHHData> data;
		
		public static class SHHData{
			
			public List<SHHListEntity> auditEvaluateTables;
			public static class SHHListEntity	{
				public String athUid;	//":"tah-20180206090730-4FB9D282",
				public String auditPersonId;	//":"ap-20180125162616-9279FC75",
				public String auditUserId;	//":"User-20180112103915-A5A657C9",
				public String auditUserName;	//":"审核员001",
				public String caseUid;	//":"B-20180203111335-0E103",
				public String createDate;	//":1517879250000,
				public String errorMessage;	//":"照片不清",
				public String errorPoints;	//":10,
				public String errorType;	//":38,
				public String id;	//":27,
				public String orderUid;	//":"B-20180203111335-0E103-016",
				public String tenantId;	//":"Tenant-20170909110851-83C95DBF",
				public String uid;	//":"ev-20180206090730-3E9D011C",
				public String updateDate;	//":1517879250000
			}
			/**审核信息**/
			public String auditMessage;
            public String auditPerosnId;	//":"ap-20180126150827-DD53B08D",
            public String auditUserName;	//":"审核员001",
            public String auditUserUid;	//":"User-20180112103915-A5A657C9",
            public String createDate;	//":"2018-01-28 17:58:38",
            public String id;	//":"58",
            public String isPass;	//":"0",
            public String orderUid;	//":"B-20180128172702-566EA-006",
            public String updateDate;	//":"2018-01-28 17:58:38"
		}
	}

}
