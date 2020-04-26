package com.cninsure.cp.entity;

import java.io.Serializable;
import java.util.List;

public class CaseOrder implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public TableDataEntity tableData;
	
	public static class TableDataEntity{
		public List<PublicOrderEntity> data;
		
		public int length;
        public int recordsFiltered;
        public int recordsTotal;
        public int start;
		
//		public static class DataEntity{
//			/**报案号**/
//			public String baoanNo;
//			/**业务品种id**/
//            public long bussTypeId;
//			/**业务品种名称**/
//            public String bussTypeName;
//            public String caseUid;
//            /**创建时间*/
//            public String createDate;
//            /**调入人名称**/
//            public String dispatcherName;
//            /**调度人Uid**/
//            public String dispatcherUid;
//            /**委托人id**/
//            public long entrusterId;
//            /**委托人名称**/
//            public String entrusterName;
//            /**公估师名称**/
//            public String ggsName;
//            /**公估师UID**/
//            public String ggsUid;
//            /**编号**/
//            @SuppressWarnings("unused")
//			public long id;
//            /**标地车牌**/
//            public String licensePlateBiaoDi;
//            /**订单状态**/
//            public int status;
//            /**订单标识UID**/
//            public String uid;
//            /**跟新时间**/
//            public String updateDate;
//
//		}
	}

}
