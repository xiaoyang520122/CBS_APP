package com.cninsure.cp.entity.cx;

import java.io.Serializable;

public class CxTaskBaseEntity implements Serializable {
    public String bussType;  //"现场查勘", 业务类型
    public Integer bussTypeId;  // 业务类型id
    public String content;  //保存的作业json信息
    public String createTime;  //"2020-08-04 11:26:58",
    public Float dsAmount;  //0
    public String ggsName;  //"公估师001"
    public String ggsUid;  //"User-20180112103259-7F7C96F2"
    public Long id;  // id
    public Float lossAmount;  //0
    public String orderUid;  //"ORDER-20200730100722-77942"
    public Integer status;  //状态
    public String updateTime;  //"2020-08-04 11:26:58"
    public String workPlatform;  //

}
