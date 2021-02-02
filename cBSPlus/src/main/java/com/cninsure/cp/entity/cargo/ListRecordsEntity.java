package com.cninsure.cp.entity.cargo;

import java.io.Serializable;

/**
 * @author :xy-wm
 * date:2021/1/27 15:13
 * usefuLness: CBS_APP
 */
public class ListRecordsEntity implements Serializable {
    public String name;// 受损项目名称
    public String type;// 规格型号
    public String unit;// 单位
    public String count;// 数量
    public String description;// 受损情况
    public String remark;// 备注
}
