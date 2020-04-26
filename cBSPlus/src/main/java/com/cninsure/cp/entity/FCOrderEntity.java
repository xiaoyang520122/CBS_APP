package com.cninsure.cp.entity;

import java.util.List;

public class FCOrderEntity extends FCBasicEntity {

	private static final long serialVersionUID = 1L;

	public OrderDateEntity data;

	public static class OrderDateEntity {

		/** 当前页码 */
		public int pageNo;
		/** 单页长度 */
		public int pageSize;
		/** 总数 */
		public long count;
		/** 开始 */
		public long first;
		/** 结束 */
		public long last;
		/** 下一页 */
		public long next;
		/** 是否第一页 */
		public boolean firstPage;
		/** 是否最后一页 */
		public boolean lastPage;
		public List<PublicOrderEntity> list;

//		public static class DataList {
//			public String createDate;
//			public Long id;
//			/** 案件编号 */
//			public String caseNo;
//			/** 案件名称 */
//			public String caseName;
//			/** 委托人名称 */
//			public String deputePer;
//			/** 案件状态3 作业待处理 4 作业处理中 */
//			public int status;
//			/** 险种大类1 财险。2 水险 */
//			public int riskType;
//			/** 报案号 */
//			public String insurerCaseNo;
//			/** 归属机构 */
//			public String gsOrg;
//		}

	}
}
