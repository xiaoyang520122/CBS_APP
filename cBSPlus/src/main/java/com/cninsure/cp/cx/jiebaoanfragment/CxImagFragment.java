package com.cninsure.cp.cx.jiebaoanfragment;

import android.os.Bundle;
import android.service.autofill.SaveCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.cx.CxWorkhelp;
import com.cninsure.cp.cx.adapter.CxImagAdapter;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.dispersive.DispersiveWorkActivity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxImagEntity;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CxImagFragment extends BaseFragment {


    private LayoutInflater inflater;
    private CxJieBaoanInfoActivity activity;
    private View contentView;
    public CxDictEntity cxSurveyDict = new CxDictEntity(); //拍照类型字典数据

    @ViewInject(R.id.parkPhotoUpload_expandablelistview3) private ExpandableListView photoListView;
    /**图片合集*/
    private List<CxImagEntity> imgEnList;
    private CxImagAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.public_expandablelistview,null);
        activity = (CxJieBaoanInfoActivity) getActivity();
        ViewUtils.inject(this,contentView);
        dowloadWorkImg();
        return contentView;
    }

    /**
     * 下载接报案对应作业图片
     */
    private void dowloadWorkImg() {
        LoadDialogUtil.setMessageAndShow(activity,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("baoanUid");
        params.add(activity.orderInfoEn.uid);
        params.add("isDelete");
        params.add("0");
        HttpUtils.requestGet(URLs.CX_GET_WORK_IMG, params, HttpRequestTool.CX_GET_WORK_IMG);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnetResult(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_GET_WORK_IMG: //获取案件图片列表
                displayImg(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    private void displayImg(String value) {
        imgEnList = JSON.parseArray(value,CxImagEntity.class);
        adapter = new CxImagAdapter(activity, imgEnList, getCxJJDict(), new CxImagAdapter.SaveImgCallBack() {
            @Override
            public void addImg(List<CxImagEntity> imgList) {
                imgEnList.addAll(imgList);
                adapter.notifyDataSetChanged();
                adapter.refresh();
            }
            @Override
            public void deleteImg(CxImagEntity deleteImgEn) {
                imgEnList.remove(deleteImgEn);
                adapter.notifyDataSetChanged();
                adapter.refresh();
            }
        });
        photoListView.setAdapter(adapter);
    }

    public List<CxDictEntity.DictData>  getCxJJDict(){
        List<CxDictEntity.DictData> list = new ArrayList<>(10);
        for (CxDictEntity.DictData cddd:activity.cxDict.list){
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
