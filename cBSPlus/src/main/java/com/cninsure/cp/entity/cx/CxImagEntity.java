package com.cninsure.cp.entity.cx;

import com.cninsure.cp.entity.yjx.ImagePathUtil;

import java.io.Serializable;

public class CxImagEntity implements Serializable {
    public int source; //图片来源
    public String type; //图片类型
    public String fileUrl;// 6167201900000288130033.jpg_172459_file-20190506144338-52724-49A9C.jpg
    public String fileName;  // test32
    public String fileSuffix="jpg";  //jpg

    public String getImageUrl() {
        if (fileUrl.indexOf("/CBSPlus/")!=-1){
            return fileUrl;
        }else{
            return ImagePathUtil.BaseUrl+fileUrl;
        }
    }
}
