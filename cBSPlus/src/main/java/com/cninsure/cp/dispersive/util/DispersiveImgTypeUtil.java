package com.cninsure.cp.dispersive.util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/***
 * 获取分散型作业拍照类型数组
 */
public class DispersiveImgTypeUtil  {

    /**现场环境照片分类*/
    private List<NameValuePair> sceneImgTypes;
    /**现场状况照片分类*/
    private List<NameValuePair> documentsImgTypes;

    /**获取现场环境照片分类数组
     * /***
     *  * 现场标识牌0,
     *  * 查勘员与现场合影1,
     *  * 施救现场2,
     *  * /*/
    public List<NameValuePair> getSceneImgTypes(){
        sceneImgTypes = new ArrayList<NameValuePair>(3);
        sceneImgTypes.add(new BasicNameValuePair("现场标识牌","0"));
        sceneImgTypes.add(new BasicNameValuePair("查勘员与现场合影","1"));
        sceneImgTypes.add(new BasicNameValuePair("施救现场","2"));
        return sceneImgTypes;
    }
    /**获取现场状况照片分类数组
     * * 现场方位标示3,
     *  * 人与标的合影4,
     *  * 标的全景5,
     *  * 标的细节6,
     *  * 出险部位7,
     *  * 出险原因8,
     *  * 受损标的布置全景9,
     *  * 标的受损基本情况10,
     *  * 受损标的数量11,
     *  * 受损标的规格铭牌12,
     *  * 发票或价值证明13,
     *  * 购买合同14,
     *  * 维修协议15,
     *  * 其他16,
     *  * 调查问卷17,
     *  * 现场货物清单18;
     *  * （17、18类型经确认需求，说归到其他 16）*/
    public List<NameValuePair> getDocumentsImgTypes(){
        documentsImgTypes = new ArrayList<NameValuePair>(15);
        documentsImgTypes.add(new BasicNameValuePair("现场方位标示","3"));
        documentsImgTypes.add(new BasicNameValuePair("人与标的合影","4"));
        documentsImgTypes.add(new BasicNameValuePair("标的全景","5"));
        documentsImgTypes.add(new BasicNameValuePair("标的细节","6"));
        documentsImgTypes.add(new BasicNameValuePair("出险部位","7"));
        documentsImgTypes.add(new BasicNameValuePair("出险原因","8"));
        documentsImgTypes.add(new BasicNameValuePair("受损标的布置全景","9"));
        documentsImgTypes.add(new BasicNameValuePair("标的受损基本情况","10"));
        documentsImgTypes.add(new BasicNameValuePair("受损标的数量","11"));
        documentsImgTypes.add(new BasicNameValuePair("受损标的规格、铭牌","12"));
        documentsImgTypes.add(new BasicNameValuePair("发票（价值证明）","13"));
        documentsImgTypes.add(new BasicNameValuePair("购买合同","14"));
        documentsImgTypes.add(new BasicNameValuePair("维修协议","15"));
        documentsImgTypes.add(new BasicNameValuePair("其他","16"));
//        documentsImgTypes.add(new BasicNameValuePair("调查问卷","17"));
//        documentsImgTypes.add(new BasicNameValuePair("现场货物清单","18"));
        return documentsImgTypes;
    }

}
