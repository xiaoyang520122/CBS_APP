package com.cninsure.cp.cx.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.jiebaoanfragment.CxImagFragment2;
import com.cninsure.cp.entity.cx.CxDsTaskEntity;
import com.cninsure.cp.entity.cx.CxImagEntity;
import com.cninsure.cp.entity.cx.CxOrderEntity;
import com.cninsure.cp.entity.cx.CxOrderMediaTypeEntity;
import com.cninsure.cp.entity.cx.CxOrderWorkMediaTypeTable;
import com.cninsure.cp.entity.cx.CxSurveyTaskEntity;
import com.cninsure.cp.utils.SetTextUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CxImagAdapter2 extends BaseExpandableListAdapter {


    /**图片合集*/
    private List<CxImagEntity> imgEnList;
    private Handler handler;
    private LayoutInflater inflater;
    private Activity context;
    public CxOrderMediaTypeEntity cxMediaTypes;
    public SaveImgCallBack addScb; //通过该接口的回调将数据写入到activity的图片实体类中，并刷新adapter
    private String parentId; //第一级标题的父级id
    public List<CxOrderWorkMediaTypeTable> groupList;
    private Map<String ,List<CxOrderWorkMediaTypeTable>> childMap;
    private String orderUid; //订单编号，用来生成本地文件夹存拍照和选择的照片。
//    public List<CxOrderEntity.CxOrderTable> orderList; //接报案对应任务列表
    private CxImagFragment2 if2;

    private CxImagAdapter2(){}
    public CxImagAdapter2(Activity context, List<CxImagEntity> documentImgEnList, CxOrderMediaTypeEntity cxMediaTypes,
                          SaveImgCallBack scb, String parentId,String orderUid,CxImagFragment2 if2){
        imgEnList = documentImgEnList;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.cxMediaTypes = cxMediaTypes;
        this.addScb = scb;
        this.parentId = parentId;
        this.orderUid = orderUid;
        this.if2 = if2;
        getGroupList();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                notifyDataSetChanged();
                super.handleMessage(msg);
            }
        };
    }

    /**
     * 获取二级标题中自增的标题分类
     */
//    private List<CxOrderWorkMediaTypeTable> getGroupListByBussTypeId(CxOrderWorkMediaTypeTable mediaType,Integer bussTypeId){
//        List<CxOrderWorkMediaTypeTable> groupList = new ArrayList<>();
//        if(40 == bussTypeId && if2.bdDsEn!=null && if2.bdDsEn.data!=null && if2.bdDsEn.data.contentJson!=null
//            && !TextUtils.isEmpty(if2.bdDsEn.data.contentJson.dsCarNumber)){
//            groupList.add(mediaType.copyNewMediaType(mediaType.value+"_"+(groupList.size()+1),"主车："+if2.bdDsEn.data.contentJson.dsCarNumber));
//
//        }
//        if (40 == bussTypeId && if2.thDsEn!=null){
//            for (CxDsTaskEntity dsEn:if2.thDsEn){
//                if(dsEn!=null && dsEn.data!=null && dsEn.data.contentJson!=null
//                        && !TextUtils.isEmpty(dsEn.data.contentJson.dsCarNumber)){
//                    groupList.add(mediaType.copyNewMediaType(mediaType.value+"_"+(groupList.size()+1),"三者车："+dsEn.data.contentJson.dsCarNumber));
//
//                }
//            }
//        }
//        return groupList;
//    }
    /**
     * 根据parentId获取第一级父级对象集合
     */
    private void getGroupList() {
        groupList = new ArrayList<>();
        if (cxMediaTypes!=null && cxMediaTypes.data!=null && !TextUtils.isEmpty(parentId)){
            for (CxOrderWorkMediaTypeTable typeTemp:cxMediaTypes.data){
                if (parentId.equals(typeTemp.parentId)){
//                    if ("损失标的性质（存在多个情况）".equals(typeTemp.label)){
//                        if ("11".equals(parentId)){
//                            groupList.addAll(getGroupListByBussTypeId(typeTemp,40));
//                        }
//                    }else{
                        groupList.add(typeTemp);
//                    }
                }
            }

        //获取子类map集合
            childMap = new HashMap<>();
            for (CxOrderWorkMediaTypeTable parentEn:groupList){
                List<CxOrderWorkMediaTypeTable> childListTem = new ArrayList<>();
                for (CxOrderWorkMediaTypeTable typeTem:cxMediaTypes.data){
                    if (parentEn.value.equals(typeTem.parentId)){
                        childListTem.add(typeTem);
                    }
                }
                childMap.put(parentEn.value,childListTem);
            }
        }
    }

    public void refresh(int groupPosition) {
        handler.sendMessage(new Message());
    }

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sFormat=new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public Object getChild(int arg0, int arg1) {
        if (childMap.get(groupList.get(arg0).value)==null || childMap.get(groupList.get(arg0).value).size()==0){ //如果父级没有子级的，就用父级。
            return groupList.get(arg0);
        }else{
            return childMap.get(groupList.get(arg0).value).get(arg1) ;
        }
    }

    @Override
    public long getChildId(int arg0, int arg1) {
        return arg1;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean arg2, View contentview, ViewGroup arg4) {
        CxOrderWorkMediaTypeTable childTypeEn;
        contentview = inflater.inflate(R.layout.cx_img_gridview_layout, null);
        if (childMap.get(groupList.get(groupPosition).value)==null || childMap.get(groupList.get(groupPosition).value).size()==0){  //如果父级没有子级的，就用父级。
            childTypeEn = groupList.get(groupPosition);
            contentview.findViewById(R.id.item_for_exlist_photoup_wsline).setVisibility(View.GONE);
        }else{ //有子集就显示标题栏信息
            childTypeEn = childMap.get(groupList.get(groupPosition).value).get(childPosition) ;
            contentview.findViewById(R.id.item_for_exlist_photoup_wsline).setVisibility(View.VISIBLE);
            SetTextUtil.setTextViewText(contentview.findViewById(R.id.item_for_exlist_photoup_wstitle),childTypeEn.label);
        }

        GridView gridView = contentview.findViewById(R.id.item_for_exlist_photoup_gridView1);
        CxGalleryAdapter gAdapter;
        String type = childTypeEn.value;
        gAdapter = new CxGalleryAdapter(context,getImgList(type),type,groupPosition,addScb,orderUid);
        gridView.setAdapter(gAdapter);
        return contentview;
    }



    public List<CxImagEntity> getImgList(String label){
        List<CxImagEntity> imgsTemp = new ArrayList<>();
        if (childMap.get(label)==null || childMap.get(label).size()==0){  //没有
            for (CxImagEntity cie: imgEnList){
                if (label.equals(cie.type)) imgsTemp.add(cie);
            }
        }else{
            for (CxOrderWorkMediaTypeTable temp:childMap.get(label)){
                if (imgEnList!=null)
                for (CxImagEntity cie: imgEnList){
                    if (temp.value.equals(cie.type)) imgsTemp.add(cie);
                }
            }
        }
        return imgsTemp;
    }


    @Override
    public int getChildrenCount(int arg0) {
        if (childMap.get(groupList.get(arg0).value)==null || childMap.get(groupList.get(arg0).value).size()==0){//如果父级没有子级的，就传1。
        return 1;
    }else{
        return childMap.get(groupList.get(arg0).value).size() ;
    }
    }

    @Override
    public long getCombinedChildId(long arg0, long arg1) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long arg0) {
        return 0;
    }

    @Override
    public Object getGroup(int arg0) {
        return groupList.get(arg0);
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public long getGroupId(int arg0) {
        return arg0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean arg1, View contentview, ViewGroup arg3) {
        contentview = inflater.inflate(R.layout.item_expandablelistview_photo_upload, null);
        ((TextView) contentview.findViewById(R.id.item_for_exlist_photoup_title)).setText(groupList.get(groupPosition).label); //分组标题
        TextView photoSum =  contentview.findViewById(R.id.item_for_exlist_photoup_sum);
        TextView photoDemand =  contentview.findViewById(R.id.item_for_exlist_photoup_demand);
        int groupPhotoSize = getImgList(groupList.get(groupPosition).value).size();
        setSubmitSum(photoSum,photoDemand,getImgList(groupList.get(groupPosition).value));
        return contentview;
    }

    public void setSubmitSum(TextView photoSum, TextView photoDemand, List<CxImagEntity> imgList) {
        int submitSum = 0;
        for (CxImagEntity tempCie : imgList) {
            if (tempCie.id != null) {
                submitSum++;
            }
        }
        photoSum.setText("上传" + submitSum);
        if (submitSum != imgList.size()) {
            photoDemand.setTextColor(context.getResources().getColor(R.color.yellow_baner));  //有未上传的图片就总数标黄色
            photoDemand.setText("共" + imgList.size() + "/" + (imgList.size() - submitSum) + "未传");
        } else photoDemand.setText("共" + imgList.size());
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupCollapsed(int arg0) {
    }

    @Override
    public void onGroupExpanded(int arg0) {
    }

    @Override
    public void registerDataSetObserver(DataSetObserver arg0) {
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver arg0) {
    }
}
