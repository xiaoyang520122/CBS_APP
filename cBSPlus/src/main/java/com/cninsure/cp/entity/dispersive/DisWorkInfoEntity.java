package com.cninsure.cp.entity.dispersive;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DisWorkInfoEntity implements Serializable {


    public String msg; //":"",
    public String success; //":true
    public DispersiveWorkInfo data;

    public static class DispersiveWorkInfo implements Serializable {
        public Long id;
        public String uid;
        public String tenantId;
        public Date createDate;
        public String createBy;
        public Date updateDate;
        public String updateBy;

        /**调度任务编号不能为空*/
        public String dispatchUid;

        public String ggsId;
        public String ggsName; // 公估师
        public String ggsTel; // 公估师电话
        /*
         * 签到信息
         */
        public String province; // 省
        public String provinceCode;
        public String city; //市
        public String cityCode;
        public String district; //区
        public String districtCode;
        public Float longitude; // 签到经度
        public Float latitude; // 签到纬度
        /**签到时间yyyy-MM-dd HH:mm:ss")*/
        public Date signInTime; // 签到时间
       /**签到地址不能为空*/
        public String signInAddr;
        public String surveyAddr;
        public Integer status;
        public Integer delFlag = 0;

        /** 事故经过概述 */
        public String story;
        /** 后续处理意见 */
        public String advice;
    }

}
