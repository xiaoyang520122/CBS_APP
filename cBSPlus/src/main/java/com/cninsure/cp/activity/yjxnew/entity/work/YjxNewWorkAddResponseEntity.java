package com.cninsure.cp.activity.yjxnew.entity.work;

import com.cninsure.cp.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @author :xy-wm
 * date:2021/5/6 17:35
 * usefuLness: CBS_APP
 */
public class YjxNewWorkAddResponseEntity extends BaseEntity implements Serializable {
    public List<YjxNewCaseWorkTable> data;
}
