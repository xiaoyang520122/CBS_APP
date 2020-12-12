package com.cninsure.cp.entity.toolclass;

/**
 * @author :xy-wm
 * date:2020/12/11 12:23
 * usefuLness: CBS_APP
 */

import com.cninsure.cp.entity.cargo.CargoCaseWorkImagesTable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *序列化map供Bundle传递map使用
 */
@SuppressWarnings("serial")
public class SerializableMap implements Serializable {
    private Map<Long , List<CargoCaseWorkImagesTable>> map;

    public Map<Long , List<CargoCaseWorkImagesTable>> getMap() {
        return map;
    }

    public void setMap(Map<Long , List<CargoCaseWorkImagesTable>> map) {
        this.map = map;
    }
}
