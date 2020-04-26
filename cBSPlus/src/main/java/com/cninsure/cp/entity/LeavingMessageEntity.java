package com.cninsure.cp.entity;

import java.io.Serializable;
import java.util.List;

public class LeavingMessageEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public LeavingmessageTableData tableData;

	public static class LeavingmessageTableData {

		public List<LeavingMessageData> data;

		public static class LeavingMessageData {
			public String createDate;
			public String id;
			public String message;
			public String tenantId;
			public String uid;
			public String updateDate;
			public String userName;
			public String workUid;
		}
	}
}
