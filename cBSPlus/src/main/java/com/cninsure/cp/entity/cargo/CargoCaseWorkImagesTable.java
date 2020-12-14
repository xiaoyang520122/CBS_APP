package com.cninsure.cp.entity.cargo;

import java.io.Serializable;
import java.util.Date;


public class CargoCaseWorkImagesTable implements Serializable {
	
	public Integer id;
	public String baoanUid;//报案编号
	public String orderUid;//订单编号
	public String type;
	public String createUid;//图片上传人UID
	public String createName;//图片上传人名称
	public String modifyUid;//图片更新人UID
	public String modifyName;//图片更新人名称
	public String uploadTime;//图片上传时间
	public String modifyTime;//图片修改时间
	public String fileUrl;//图片路径
	public String fileName;//图片名称
	public String fileSuffix;//图片后缀
	public Integer direction;//方向
	public Integer sort;//排序号
	public Integer source;//来源
	public Integer isDelete;//是否删除
	

}
