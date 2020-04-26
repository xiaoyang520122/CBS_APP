package com.cninsure.cp.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cninsure.cp.entity.WorkPhotos.TableData.WorkPhotoEntitiy;

public class WorkPhotos  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public TableData tableData;
	
	public static class TableData{
		public List<WorkPhotoEntitiy> data;
		
		public static class WorkPhotoEntitiy{
			public String id;
			/**图片路径**/
            public String location;
            /**图片分类id**/
            public String typeId;
            public String uid;
		}
	}
	
	public List<WorkPhotoEntitiy> getByTypeId(String typeid){
		List<WorkPhotoEntitiy> typeList=new ArrayList<WorkPhotoEntitiy>();
		for (WorkPhotoEntitiy dataen:tableData.data) {
			if (dataen.typeId.equals(typeid)) {
				typeList.add(dataen);
			}
		}
		return typeList;
	}

}
