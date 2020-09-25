package com.cninsure.cp.entity.dispersive;

import com.cninsure.cp.entity.yjx.ImagePathUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DisWorkImageEntity  implements Serializable {


    public String msg; //":"",
    public String success; //":true
    public List<DisWorkImgData> data;

    public static class DisWorkImgData implements Serializable {
        public Date createDate; //":"2020-04-03 16:40:27",
        public int delFlag; //":0,
        /**调度任务编号**/
        public String dispatchUid; //":null,
        public long id; //":12,
        /**图片大类*/
        public int imageType; //10,
        /**图片小类*/
        public Integer imageSubType; //"**标的受损基本情况（10）类别下面添加子类别（新字段：imageSubType）：损失物品类1（101），可继续添加，按照顺序编号即可，如：损失物品类2（102），损失物品类3（103），等等**
        /**图片七牛云名称  http://qiniu.cnsurvey.cn/+*/
        private String imageUrl; //":"IMG-20200403164027-6797E.png",
        public String tenantId; //":null,
        public String uid; //":"IMG-20200403164027-6797E",
        public Date updateDate; //":null,
        public String workUid; //":"OTHER-W-20200403113559-E4191"


        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageUrl() {
            if (imageUrl.indexOf("/CBSPlus/")!=-1){
                return imageUrl;
            }else{
                return ImagePathUtil.BaseUrl+imageUrl;
            }
        }
    }

}
