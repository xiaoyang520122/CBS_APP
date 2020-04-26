package com.cninsure.cp.entity.yjx;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;

import com.cninsure.cp.entity.PublicOrderEntity;

public class YjxOrderListEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	 	public int endRow;//":3,
	 	public int firstPage;//":1,
	 	public boolean hasNextPage;//":true,
	 	public boolean hasPreviousPage;//":false,
	 	public boolean isFirstPage;//":false,
	 	public boolean isLastPage;//":false,
	 	public int lastPage;//":1,
	 	public List<YjxCaseDispatchTable> list;
	 	public int navigatePages;//":8,
	 	public int[] navigatepageNums;//":[1],
	 	public int nextPage;//":1,
	 	public int pageNum;//":0,
	 	public int pageSize;//":20,
	 	public int pages;//":1,
	 	public int prePage;//":0,
	 	public int size;//":3,
	 	public int startRow;//":1,
	 	public int total;//":3
	 	
	 	
	 	@SuppressLint("SimpleDateFormat")
		private SimpleDateFormat simpSF=new SimpleDateFormat("yyyy-MM-dd");
		@SuppressLint("SimpleDateFormat")
		private SimpleDateFormat SF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	/**将医健险返回数据封装到PublicOrderEntity中，以便通过统一的adapter进行加载*/ 
		public List<PublicOrderEntity> getYjxPublicOrderEntity() {
			List<PublicOrderEntity> dataTemp = new ArrayList<PublicOrderEntity>();
			for (YjxCaseDispatchTable dispatchTable : list) {
				PublicOrderEntity entity = new PublicOrderEntity();
				
				try {
					entity.createDate = SF.format(dispatchTable.createDate); //创建时间
					entity.dispatchDate = SF.format(dispatchTable.createDate); //医健险调度时间用创建时间
				} catch (Exception e) { e.printStackTrace(); }
				
				try {
					entity.caseTypeId = Integer.parseInt(dispatchTable.insuranceBigTypeId);
				} catch (Exception e) {
					entity.caseTypeId = 200; //如果出错医健险产品ID默认为200
							e.printStackTrace(); }

				entity.id = dispatchTable.id;
				entity.caseTypeName = dispatchTable.insuranceBigType; //险种名称
				entity.baoanNo = dispatchTable.caseBaoanNo;  //报案号
				entity.bussTypeId = dispatchTable.bussTypeId; //业务品种id
				entity.bussTypeName = dispatchTable.bussType; //业务品种
				entity.caseLocation = dispatchTable.taskAddress; //出现地点
				entity.feicheBaoxianType = dispatchTable.product; //产品细类
				entity.status = dispatchTable.status;
				entity.caseTypeAPP = "YJX";
				entity.timeOutHours = dispatchTable.aging;
				entity.uid = dispatchTable.uid;
				entity.caseBaoanUid = dispatchTable.caseBaoanUid;
//				entity.dispatcherTel = dispatchTable.user.mobile;
				
				dataTemp.add(entity);
			}
			return dataTemp;
		}

}
