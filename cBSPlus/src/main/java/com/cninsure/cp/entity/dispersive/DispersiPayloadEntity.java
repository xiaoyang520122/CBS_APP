package com.cninsure.cp.entity.dispersive;

import java.io.Serializable;

public class DispersiPayloadEntity implements Serializable {

    public DispersiveDispatchEntity.DispersiveDispatchItem data;
    public String msg;  //
    public boolean success;  //true
}
