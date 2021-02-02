package com.cninsure.cp.cx.publicmatch;

import com.cninsure.cp.entity.cx.CxImagEntity;

import java.io.Serializable;

/**
 * @author :xy-wm
 * date:2021/1/19 9:52
 * usefuLness: CBS_APP
 */
public interface NotifyCallBack  extends Serializable {
    void notifyDo(int groupPoint, CxImagEntity imgEn);
    void notifydelete(int groupPoint);
}
