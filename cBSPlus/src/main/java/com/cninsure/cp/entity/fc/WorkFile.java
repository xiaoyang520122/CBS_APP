package com.cninsure.cp.entity.fc;

import java.util.Date;
import java.util.List;

import com.cninsure.cp.entity.FCBasicEntity;

public class WorkFile extends FCBasicEntity {

	public static final long serialVersionUID = 1L;

	public List<FCFileEntity> data;

	public static class FCFileEntity {

		/***/
		public Long id;
		public Long workId;
		public String caseNo;
		public String fileName;
		public String filePath;
		/**图片类型 1、图片，其他非图片文件 **/
		public String fileType;
		/**图片分类 0,1,2,3,……10*/
		public String fileTypeId;
		public Date createDate;
	}

}
