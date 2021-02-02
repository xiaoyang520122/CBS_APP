package com.cninsure.cp.cx.publicmatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.R;
import com.cninsure.cp.cargo.CargoWorkActivity;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.cx.util.CxWorkImgUploadUtil;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxImagEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicImagFragment extends BaseFragment {


    private LayoutInflater inflater;
    private Activity activity;
    private View contentView;
    public CxDictEntity cxImgDict = new CxDictEntity(); //拍照类型字典数据
    private String orderUid,caseBaoanUid;
    private PublicOrderEntity orderInfoEn;

    @ViewInject(R.id.newCx_expandablelistview) private ExpandableListView photoListView;
    @ViewInject(R.id.newCx_ImgFragment_button) private Button submitImgBut;
    /**图片合集*/
    private List<CxImagEntity> imgEnList;
    /**图片分类合集*/
    private Map<String,List<CxImagEntity>> classImgMap;
    public static PublicExpandablelistFAdapter adapter;
    private List<CxImagEntity> submitImgEnList;  //上传图片合集
    private List<NameValuePair> params;
    /**大标题分类*/
    private List<DictData> parentPtoTypeDict;
    /**拍照分类小类*/
    private Map<String,List<DictData>> childPtoTypeDicts;
    private int dictStart,dictEnd;

    private PublicImagFragment(){}

    /**
     *
     * @param orderUid 订单号
     * @param orderInfoEn 订单信息
     * @param dictStart 拍照字典（value）值最小值-1
     * @param dictEnd 拍照字典值最大值+1
     */
    public PublicImagFragment(String orderUid, PublicOrderEntity orderInfoEn,int dictStart,int dictEnd){
        this.orderUid = orderUid;
        this.orderInfoEn = orderInfoEn;
        this.dictStart = dictStart;
        this.dictEnd = dictEnd;
        this.caseBaoanUid = orderInfoEn.caseBaoanUid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_expandablelistview,null);
        activity =  getActivity();
        ViewUtils.inject(this,contentView);
        dowloadDictType();
        setSubmitOnclick();
        return contentView;
    }


    private void setSubmitOnclick() {
        submitImgBut.setOnClickListener(v -> {
            uploadImg();
        });
    }
    /**上传作业图片到服务器，并记录文件名称**/
    private void uploadImg() {
        params = new ArrayList<>();
        submitImgEnList = new ArrayList<>();  // 待上传图片类集合
        for (String keyValue:classImgMap.keySet()){
            List<CxImagEntity> tempImgList = classImgMap.get(keyValue);
            if (tempImgList!=null){
                for (CxImagEntity tempImglist : tempImgList) {
                    if (tempImglist != null && tempImglist.getImageUrl() != null && tempImglist.getImageUrl().indexOf("://") == -1) {
                        params.add(new BasicNameValuePair(tempImglist.type + "", tempImglist.getImageUrl()));
                        submitImgEnList.add(tempImglist);
                    }
                }}
        }

        if (submitImgEnList.size() > 0) {
            CxWorkImgUploadUtil.uploadCxImg(activity, submitImgEnList, URLs.UPLOAD_FILE_PHOTO);
        } else {
            DialogUtil.getAlertOneButton(activity, "没有需要上传的图片！", null).show();
        }
    }
    /**保存作业图片路径**/
    private void saveImg() {
        List<NameValuePair> httpParams = new ArrayList<>();
//        httpParams.add(new BasicNameValuePair( "id", tempImgData.id==0?"":tempImgData.id+""));  //替换图片时使用
        httpParams.add(new BasicNameValuePair("orderUid", orderUid));
        httpParams.add(new BasicNameValuePair("baoanUid", caseBaoanUid));

        if (submitImgEnList.size() > 0) {
            PhotoUploadUtil.newCxImgSave(activity, submitImgEnList, URLs.CX_UP_WORK_IMG, httpParams);
        } else {
            DialogUtil.getAlertOneButton(activity, "没有需要上传的图片！", null).show();
        }
    }



    private void dowloadDictType() {
        if (cxImgDict!=null && cxImgDict.list!=null && cxImgDict.list.size()>0) {
            dowloadWorkImg(); return;
        }
        LoadDialogUtil.setMessageAndShow(activity,"载入中……");
        List<String> params = new ArrayList<>(2);
        params.add("type");
//        params.add("medicalFee,casualtiesFee,poNature,deliveryCompany"); // 医疗费用赔偿MedicalFee ,死亡伤残赔偿casualtiesFee ,户籍性质poNature
        params.add("cxOrderWorkMediaType");
        HttpUtils.requestGet(URLs.CX_NEW_GET_IMG_TYPE_DICT, params, HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT_IF);
    }

    /**
     * 下载接报案对应作业图片
     */
    private void dowloadWorkImg() {
        if (imgEnList!=null && imgEnList.size()>0) {
            setAdapter();
        }else dowloadImg();
    }
    /**
     * 下载接报案对应作业图片
     */
    private void dowloadImg() {
        LoadDialogUtil.setMessageAndShow(activity,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("baoanUid");
        params.add(caseBaoanUid);
        params.add("isDelete");
        params.add("0");
        HttpUtils.requestGet(URLs.CX_GET_WORK_IMG, params, HttpRequestTool.CX_GET_WORK_IMG);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnetResult(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_GET_WORK_IMG: //获取案件图片列表
                LoadDialogUtil.dismissDialog();
                displayImg(values.get(0).getValue());
                break;
            case HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT_IF:
                dowloadWorkImg();
                LoadDialogUtil.dismissDialog();
                cxImgDict.list = JSON.parseArray(values.get(0).getValue(), DictData.class);
                getCxJJDict();
                break;
            case HttpRequestTool.UPLOAD_FILE_PHOTO:
                LoadDialogUtil.dismissDialog();
                saveImg();
                break;
            case HttpRequestTool.UPLOAD_WORK_PHOTO:
                LoadDialogUtil.dismissDialog();
                dowloadImg();
                break;
            default:
                break;
        }
    }

    /**上传影像资料成功后刷新界面**/
    @Subscribe(threadMode=ThreadMode.MAIN)
    public void eventmeth(String successCode) {
        if ("UPLOAD_SUCCESS".equals(successCode)) {  //上传图片成功后重新加载Activity
           notifyData((int) photoListView.getSelectedPosition());
        }
    }

    private void displayImg(String value) {
        imgEnList = JSON.parseArray(value,CxImagEntity.class);
        for (CxImagEntity tempCimgEn:imgEnList){
            if (classImgMap.get(tempCimgEn.type)==null){
                classImgMap.put(tempCimgEn.type,new ArrayList<>());
            }
            classImgMap.get(tempCimgEn.type).add(tempCimgEn);
        }
        setAdapter();
    }

    private void setAdapter(){
        PublicPhotoChoiceActivity.callBack = new NotifyCallBack() {
            @Override
            public void notifyDo(int groupPoint,CxImagEntity imgEn) {
                putImgEn(imgEn);
                notifyData(groupPoint);
            }

            @Override
            public void notifydelete(int groupPoint) {
                notifyData(groupPoint);
            }
        };
        adapter = new PublicExpandablelistFAdapter(activity, parentPtoTypeDict, childPtoTypeDicts, classImgMap, orderInfoEn, PublicPhotoChoiceActivity.callBack);
        photoListView.setAdapter(adapter);
    }

    private void notifyData(int groupPoint){
        adapter.notifyDataSetChanged();
        photoListView.collapseGroup(groupPoint); //收起
        photoListView.expandGroup(groupPoint); //展开
    }
    /**
     * 添加选择的图片到Map集合中
     * @param imgEn
     */
    private void putImgEn(CxImagEntity imgEn){
        if (imgEn==null) return;
        if (classImgMap.get(imgEn.type)==null) classImgMap.put(imgEn.type,new ArrayList<>());
            classImgMap.get(imgEn.type).add(imgEn);
    }

    public void  getCxJJDict(){
        parentPtoTypeDict = new ArrayList<>(8);
        childPtoTypeDicts = new HashMap<>(8);
        classImgMap = new HashMap<>(8);
        for (DictData cddd:cxImgDict.list){
            if ("cxOrderWorkMediaType".equals(cddd.type) && Integer.parseInt(cddd.value)>dictStart && Integer.parseInt(cddd.value)<dictEnd)
                parentPtoTypeDict.add(cddd);
        }
        for (DictData cddd:cxImgDict.list) {
            for (DictData pdd : parentPtoTypeDict) {
                if ((cddd.parentId + "").equals(pdd.value)) {
                    if (childPtoTypeDicts.get(pdd.value) == null) {
                        childPtoTypeDicts.put(pdd.value, new ArrayList<>());
                    }
                    childPtoTypeDicts.get(pdd.value).add(cddd);
                    break;
                }
            }
            if (classImgMap.get(cddd.value) == null) classImgMap.put(cddd.value,new ArrayList<>());
        }
    }

    @Override
    public void SaveDataToEntity() {
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();  }
    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }  }
    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
