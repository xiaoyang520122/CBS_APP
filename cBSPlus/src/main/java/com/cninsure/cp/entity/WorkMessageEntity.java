package com.cninsure.cp.entity;

import java.io.Serializable;

public class WorkMessageEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public WorkEnData data;
	
	public static class WorkEnData{
		public String bdCarNumber;//":"晋M74721",
		public String bdCarVin;//":"LFWRRXPJ7B1E507542",
		public String bdDriverLincense;//":"440782198905114717",
		public String bdDriverName;//":"郑嘉荣",
		public String bdDriverPhone;//":null,
		public String bdDrivingType;//":"C1",
		public String bdEngineNo;//":"51969591",
		public String bdSameDriverLincense;//":1,
		public String bdSameDrivingType;//":1,
		public String bdSameMoveLincense;//":1,
		public String bdSameVarVin;//":0,
		public String ckAccidentLiability;//":"2",
		public String ckAccidentType;//":"3",
		public String ckIsCasualties;//":null,
		public String ckIsCasualtiesValue;//":null,
		public String ckIsDestroy;//":null,
		public String ckIsDestroyValue;//":null,
		public String ckIsTouchCompensate;//":null,
		public String ckIsTouchCompensateValue;//":null,
		public String ckLocation;//":null,
		public String ckResultMessage;//":null,
		public String ckResultType;//":null,
		public String ckYuguAmount;//":null,
		public String createDate;//":null,
		public String id;//":null,
		public String injuredCommands;//":null,
		public String insuredBankDeposit;//":null,
		public String insuredBankNo;//":null,
		public String insuredPersonName;//":null,
		public String isLicenseKou;//":0,
		public String orderUid;//":"B-20180206140433-988D2-009",
		public String pathBank;//":null,
		public String pathDigest;//":null,
		public String pathDriverLicense;//":"picture-20180310172433-28181-F4783.jpg",
		public String pathMoveLicense;//":"picture-20180310172407-37913-16171.jpg",
		public String tenantId;//":null,
		public String thirdCommands;//":null,
		public String uid;//":null,
		public String updateDate;//":null
	}

}
