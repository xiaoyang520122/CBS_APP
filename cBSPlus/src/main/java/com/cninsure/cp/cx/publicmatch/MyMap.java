package com.cninsure.cp.cx.publicmatch;

import com.cninsure.cp.entity.cx.CxImagEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author :xy-wm
 * date:2021/1/18 17:38
 * usefuLness: CBS_APP
 */
public class MyMap implements Serializable {
    public Map<String, List<CxImagEntity>> myMap;

    public MyMap(Map<String, List<CxImagEntity>> cMap){
        myMap= cMap;
    }
}
