package com.cninsure.cp.entity.cx;

import java.io.Serializable;

/**
 * @author :xy-wm
 * date:2020/12/8 17:39
 * usefuLness: CBS_APP
 */
public class DictData implements Serializable {
//      public String createBy;  //"User-20180103101603-687B671A", 创建人
//      public String createDate;  //"2020-07-09 11:23:45", 创建日期
    public String delFlag;  //"0",  //删除标志
    public String description;  //"车险订单作业资料类型",
    public long id;  //5,
    public String label;  //"人车合影",
    public long parentId;  //null,
    public String remarks;  //null,
    public int sort;  //1,
    public String type;  //"cxOrderWorkImageType",
//      public String updateBy;  //"User-20180103101603-687B671A",
//      public String updateDate;  //"2020-07-09 11:23:45",
    public String value;  //"1" 字典值，字符串


}
