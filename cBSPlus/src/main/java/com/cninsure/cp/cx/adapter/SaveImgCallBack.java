package com.cninsure.cp.cx.adapter;

import com.cninsure.cp.entity.cx.CxImagEntity;

import java.io.Serializable;
import java.util.List;

public interface SaveImgCallBack extends Serializable {
    public void addImg(List<CxImagEntity> imgList,int position);
    public void deleteImg(CxImagEntity deleteImgEn,int position);
}
