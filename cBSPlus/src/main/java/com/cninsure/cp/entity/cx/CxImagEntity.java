package com.cninsure.cp.entity.cx;

import com.cninsure.cp.entity.yjx.ImagePathUtil;

import java.io.Serializable;

public class CxImagEntity implements Serializable {
    public String source; //图片来源  //来自电脑 1,来自手机相册(安卓) 2,    来自手机拍照(安卓) 3,    来自手机相册(iOS) 4,    来自手机拍照(iOS) 5
    public String type; //图片类型
    public String fileUrl;// 6167201900000288130033.jpg_172459_file-20190506144338-52724-49A9C.jpg
    public String fileName;  // test32
    public String fileSuffix="jpg";  //jpg

    public String baoanUid;  //CX-BA-20200920174018-956DB
    public String createName;  //公估师001
    public String createUid;  //User-20180112103259-7F7C96F2
    public String direction;  //0,
    public Long id;  //39,
    public Integer isDelete;  //0,
    public String modifyName;  //null,
    public String modifyTime;  //null,
    public String modifyUid;  //null,
    public String orderUid;  //ORDER-20200920174018-BCD5F
    public Integer sort;  //0,
    public String uploadTime;  //2020-09-27 12:08:04"



    public String getImageUrl() {
        if (fileUrl.indexOf("/CBSPlus/")!=-1){
            return fileUrl;
        }else{
            return ImagePathUtil.BaseUrl+fileUrl;
        }
    }
}
