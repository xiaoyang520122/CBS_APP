package com.cninsure.cp.entity;

import java.io.Serializable;

public class BaseEntity implements Serializable {

    public String msg; //是否成功对应的提示消息
    public boolean success; //是否成功 true成功  false失败
}
