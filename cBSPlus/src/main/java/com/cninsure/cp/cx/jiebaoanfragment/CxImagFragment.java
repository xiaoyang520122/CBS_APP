package com.cninsure.cp.cx.jiebaoanfragment;

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
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.cx.adapter.CxImagAdapter;
import com.cninsure.cp.cx.adapter.SaveImgCallBack;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.cx.util.CxWorkImgUploadUtil;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxImagEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.entity.dispersive.DisWorkImageEntity;
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
import java.util.List;

public class CxImagFragment extends BaseFragment {


    private LayoutInflater inflater;
    private CxJieBaoanInfoActivity activity;
    private View contentView;
    public CxDictEntity cxImgDict = new CxDictEntity(); //拍照类型字典数据

    @ViewInject(R.id.newCx_expandablelistview) private ExpandableListView photoListView;
    @ViewInject(R.id.newCx_ImgFragment_button) private Button submitImgBut;
    /**图片合集*/
    private List<CxImagEntity> imgEnList;
    public static CxImagAdapter adapter;
    private List<CxImagEntity> submitImgEnList;  //上传图片合集
    private List<NameValuePair> params;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_expandablelistview,null);
        activity = (CxJieBaoanInfoActivity) getActivity();
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
    /**上传作业图片**/
    private void uploadImg() {
        params = new ArrayList<>();
        submitImgEnList = new ArrayList<>();  // 待上传图片类集合
        for (CxImagEntity tempImglist : imgEnList) {
            if (tempImglist != null && tempImglist.getImageUrl() != null && tempImglist.getImageUrl().indexOf("://") == -1) {
                params.add(new BasicNameValuePair(tempImglist.type + "", tempImglist.getImageUrl()));
                submitImgEnList.add(tempImglist);
            }
        }
        if (submitImgEnList.size() > 0) {
            CxWorkImgUploadUtil.uploadCxImg(activity, submitImgEnList, URLs.UPLOAD_FILE_PHOTO);
        } else {
            DialogUtil.getAlertOneButton(activity, "没有需要上传的图片！", null).show();
        }
    }
    /**上传作业图片**/
    private void saveImg() {
        List<NameValuePair> httpParams = new ArrayList<>();
//        httpParams.add(new BasicNameValuePair( "id", tempImgData.id==0?"":tempImgData.id+""));  //替换图片时使用
        httpParams.add(new BasicNameValuePair("orderUid", activity.orderInfoEn.uid));
        httpParams.add(new BasicNameValuePair("baoanUid", activity.orderInfoEn.caseBaoanUid));

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
        params.add(activity.orderInfoEn.caseBaoanUid);
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

    private void displayImg(String value) {
        imgEnList = JSON.parseArray(value,CxImagEntity.class);
        setAdapter();
    }

    private void setAdapter(){
        adapter = new CxImagAdapter(activity, imgEnList, getCxJJDict(), new SaveImgCallBack() {
            @Override
            public void addImg(List<CxImagEntity> imgList,int position) {
                imgEnList.addAll(imgList);
                adapter.notifyDataSetChanged();
                photoListView.collapseGroup(position); //收起
                photoListView.expandGroup(position); //展开
            }
            @Override
            public void deleteImg(CxImagEntity deleteImgEn,int position) {
                imgEnList.remove(deleteImgEn);
                adapter.notifyDataSetChanged();
                photoListView.collapseGroup(position); //收起
                photoListView.expandGroup(position); //展开
            }
        });
        photoListView.setAdapter(adapter);
    }

    public List<DictData>  getCxJJDict(){
        List<DictData> list = new ArrayList<>(10);
        for (DictData cddd:cxImgDict.list){
            if ("cxOrderWorkMediaType".equals(cddd.type) && Integer.parseInt(cddd.value)<11)
                list.add(cddd);
        }
        return list;
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
