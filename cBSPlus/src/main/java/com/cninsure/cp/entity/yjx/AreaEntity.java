package com.cninsure.cp.entity.yjx;

import java.util.List;

public class AreaEntity {
 public List<AreaTableData> tableData;
 
 public static class AreaTableData{
	 public Integer id;
	 public String name="";
 }
 
 
 /**根据传递的AreaEntity对象和int值获取在areaEn.tableData指定位置的AreaTableData对象*/
 public static AreaTableData getAreaTableByPosition(AreaEntity areaEn,int point){
	 if (areaEn!=null && areaEn.tableData!=null && areaEn.tableData.size()>point && point>=0) {
		 return areaEn.tableData.get(point);
	}else {
		return null;
	}
 }
}
