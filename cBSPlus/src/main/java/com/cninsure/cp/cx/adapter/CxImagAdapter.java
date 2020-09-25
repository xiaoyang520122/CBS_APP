package com.cninsure.cp.cx.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxImagEntity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

    public class CxImagAdapter extends BaseExpandableListAdapter {


        public interface SaveImgCallBack extends Serializable {
            public void addImg(List<CxImagEntity> imgList);
            public void deleteImg(CxImagEntity deleteImgEn);
        }

        /**图片合集*/
        private List<CxImagEntity> imgEnList;
        private Handler handler;
        private LayoutInflater inflater;
        private Context context;
        private List<CxDictEntity.DictData> dictList;
        private SaveImgCallBack addScb; //通过该接口的回调将数据写入到activity的图片实体类中，并刷新adapter

        private CxImagAdapter(){}
        public CxImagAdapter(Context context, List<CxImagEntity> documentImgEnList, List<CxDictEntity.DictData> list, SaveImgCallBack scb){
            imgEnList = documentImgEnList;
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.dictList = list;
            this.addScb = scb;
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    notifyDataSetChanged();
                    super.handleMessage(msg);
                }
            };
        }

        public void refresh() {
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
            return imgEnList.get(arg0) ;
        }

        @Override
        public long getChildId(int arg0, int arg1) {
            return arg1;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean arg2, View contentview, ViewGroup arg4) {
            contentview = inflater.inflate(R.layout.dispersive_gridview_layout, null);
            GridView gridView = contentview.findViewById(R.id.item_for_exlist_photoup_gridView1);
            CxGalleryAdapter gAdapter;
            String type = dictList.get(groupPosition).label;
            gAdapter = new CxGalleryAdapter(context,getImgList(type),type,addScb);
            gridView.setAdapter(gAdapter);
            return contentview;
        }



        public List<CxImagEntity> getImgList(String label){
            List<CxImagEntity> imgsTemp = new ArrayList<>();
            for (CxImagEntity cie: imgEnList){
                if (label.equals(cie.type)) imgsTemp.add(cie);
            }
            return imgsTemp;
        }


        @Override
        public int getChildrenCount(int arg0) {
            return 1;
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
            return dictList.get(arg0);
        }

        @Override
        public int getGroupCount() {
            return dictList.size();
        }

        @Override
        public long getGroupId(int arg0) {
            return arg0;
        }

        @Override
        public View getGroupView(int groupPostion, boolean arg1, View contentview, ViewGroup arg3) {
            contentview = inflater.inflate(R.layout.item_expandablelistview_photo_upload, null);
            ((TextView) contentview.findViewById(R.id.item_for_exlist_photoup_title)).setText(dictList.get(groupPostion).label); //分组标题
            TextView photoSum =  contentview.findViewById(R.id.item_for_exlist_photoup_sum);
            TextView photoDemand =  contentview.findViewById(R.id.item_for_exlist_photoup_demand);
            int groupPhotoSize = getImgList(dictList.get(groupPostion).label).size();
            photoSum.setText(groupPhotoSize + "");
            return contentview;
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