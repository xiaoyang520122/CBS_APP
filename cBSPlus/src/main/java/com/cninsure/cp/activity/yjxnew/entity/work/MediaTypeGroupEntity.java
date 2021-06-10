package com.cninsure.cp.activity.yjxnew.entity.work;

import com.cninsure.cp.entity.BasePageEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @author :xy-wm
 * date:2021/6/10 9:16
 * usefuLness: CBS_APP
 */
public class MediaTypeGroupEntity implements Serializable{

    public MediaTypeGroupData data;
    public String msg;
    public Boolean success;


    public class MediaTypeGroupData extends BasePageEntity {
        public List<MediaTypeGroupTable> list;
    }
}
