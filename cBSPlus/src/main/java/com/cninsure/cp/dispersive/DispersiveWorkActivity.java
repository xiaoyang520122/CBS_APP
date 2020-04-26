package com.cninsure.cp.dispersive;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.LoadingActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.dispersive.util.DispersiveImgTypeUtil;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.WorkPhotos;
import com.cninsure.cp.entity.dispersive.DisWorkImageEntity;
import com.cninsure.cp.entity.dispersive.DisWorkInfoEntity;
import com.cninsure.cp.entity.dispersive.DispersiveDispatchEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispersiveWorkActivity extends BaseActivity {
    public static DispersiveWorkActivity instence;
    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private View msgView,examineView;
    private ExpandableListView photoListView1,photoListView2;
    private MyDisExpandablelistAdapter adapter1,adapter2;
    private Map<String ,DispersiveGalleryAdapter> gridAdapterArr;
    private List<View> viewlist;
    public DispersiveDispatchEntity.DispersiveDispatchItem caseDisTemp;
    /***照片接口返回数据*/
    private DisWorkImageEntity workEnty;
    /**作业数据实体类**/
    private DisWorkInfoEntity disWorkInfo;
    /**现场环境图片结合*/
    private List<List<DisWorkImageEntity.DisWorkImgData>> siteImgEnList;
    /**运输单证图片结合*/
    private List<List<DisWorkImageEntity.DisWorkImgData>> documentImgEnList;
    /**选着的照片大类 0现场环境，1运输单证*/
    private int choicePhotoLargeType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispersive_work_main_activity);
        ((TextView)findViewById(R.id.ACTION_V_CTV)).setText("查勘作业");
        EventBus.getDefault().register(this);
        initview();
        setRadioOnclick();
        setAction();
        disPlayDispatchInfo();
        dowloadWorkInfo();
        initImgEnList();
        instence = this;
    }

    private void initview() {
        gridAdapterArr = new HashMap<>(17);
        viewPager=(ViewPager) findViewById(R.id.parkPhotoUpload_ViewPager);
        radioGroup=(RadioGroup) findViewById(R.id.parkPhotoUpload_RadioGroup);
        caseDisTemp = (DispersiveDispatchEntity.DispersiveDispatchItem) getIntent().getSerializableExtra("caseDisTemp");
        initPagerView();
    }

    /**显示调度信息*/
    private void disPlayDispatchInfo(){
        if (caseDisTemp==null || caseDisTemp.uid==null){  //调度信息缺失影响保存的直接报错后关闭界面。
           Dialog dialog =  DialogUtil.getAlertOneButton(this,"案件信息获取失败！",null);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    DispersiveWorkActivity.this.finish();
                }
            });
        }else{
            disPlay();
        }
    }

    /**组装调度信息并显示*/
    private void disPlay(){
        ListView listView = msgView.findViewById(R.id.dispersive_work_Info_listView);
        List<String> infoData= new ArrayList<String>();
        infoData.add("出险险种：\t"+caseDisTemp.insuranceBigType +"/"+caseDisTemp.insuranceSmallType);
        infoData.add("案件对接人：\t"+caseDisTemp.takerName +"/"+caseDisTemp.takerTel);
        infoData.add("保险公司：\t"+caseDisTemp.wtName);
        infoData.add("报案号：\t"+caseDisTemp.baoanNo);
        infoData.add("现场联系人：\t"+caseDisTemp.sceneName+"/"+caseDisTemp.sceneTel);
        infoData.add("查勘地点：\t"+caseDisTemp.surveyAddr);
        infoData.add("受损基本情况：\t"+caseDisTemp.damagedState);
        infoData.add("查勘重点要求：\t"+caseDisTemp.keyPoint);
        ArrayAdapter<String> infoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,infoData);
        listView.setAdapter(infoAdapter);
    }

    private void setAction(){
        findViewById(R.id.ACTION_V_LTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DispersiveWorkActivity.this.finish();
            }
        });
        if (caseDisTemp!=null && (caseDisTemp.status==11 || caseDisTemp.status==5)) { //驳回和已到达现场的才能提交审核
        ((TextView)findViewById(R.id.ACTION_V_RTV)).setText("提交 >");
        ((TextView)findViewById(R.id.ACTION_V_RTV)).setCompoundDrawables(null, null, null, null);
        setSubmitOclick();
        } else{
            findViewById(R.id.ACTION_V_RTV).setVisibility(View.GONE);
        }
    }

    /**保存作业单击事件设置*/
    private void setSubmitOclick() {
        findViewById(R.id.ACTION_V_RTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoiceDialog();
            }
        });
    }

    /**弹框选择保存作业或者是提交审核**/
    private void showChoiceDialog(){
        new AlertDialog.Builder(DispersiveWorkActivity.this).setTitle("选择操作！\n提交审核前请保证作业信息已保存！")
                .setItems(new String[]{"保存作业信息", "提交审核"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){ //选择保存作业信息
                            submitWorkInfo(); //提交作业信息
                        }else if (which==1){  //选择提交审核
                            submitForReview(); //提交审核
                        }
                    }
                }).create().show();
    }

    /**作业提交审核*/
    private void submitForReview(){
        if (photoIsfull()) {
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            paramsList.add(new BasicNameValuePair("userId", AppApplication.USER.data.userId));
            paramsList.add(new BasicNameValuePair("dispatchUid", caseDisTemp.uid));  //作业uid
            HttpUtils.requestPost(URLs.FSX_WORK_SUBMIT_RIVIEW, paramsList, HttpRequestTool.FSX_WORK_SUBMIT_RIVIEW);
            LoadDialogUtil.setMessageAndShow(this, "处理中……");
        }
    }

    /**判断并提示照片类型是否拍摄齐全*/
    private boolean photoIsfull() {
        //事故经过和后续意见是否以保存
        if (TextUtils.isEmpty(disWorkInfo.data.story) && !TextUtils.isEmpty(((EditText)examineView.findViewById(R.id.dispersive_work_examine_jg)).getText().toString()) ){
            DialogUtil.getAlertOneButton(this,"事故经过未保存！",null).show();
            return false;
        }
        if (TextUtils.isEmpty(disWorkInfo.data.advice) && !TextUtils.isEmpty(((EditText)examineView.findViewById(R.id.dispersive_work_examine_yj)).getText().toString()) ){
            DialogUtil.getAlertOneButton(this,"后续意见未保存！",null).show();
            return false;
        }
        //现场环境
        for (List<DisWorkImageEntity.DisWorkImgData> tempArr: siteImgEnList){
            if (getUpPhotoSize(tempArr)<tempArr.size()) {  //如果有照片未上传，让客户先上传
                DialogUtil.getAlertOneButton(this,"现场环境中有照片未上传，请先上传！",null).show();
                return false;
            }
            if (tempArr.size()<1) {  //现场环境信息中，每个类型拍照不能少于1张！
                DialogUtil.getAlertOneButton(this,"现场环境信息中，每个类型拍照不能少于1张！",null).show();
                return false;
            }
        }
        //运输单  要求左右小类之和不少于5张即可
        int documentSize = 0;
        for (List<DisWorkImageEntity.DisWorkImgData> tempArr: documentImgEnList){
            if (getUpPhotoSize(tempArr)<tempArr.size()) {  //如果有照片未上传，让客户先上传
                DialogUtil.getAlertOneButton(this,"运输单证中有照片未上传，请先上传！",null).show();
                return false;
            }
            documentSize = documentSize + tempArr.size();
        }
        if (documentSize<5) {  //运输单证  要求左右小类之和不少于5张即可
            DialogUtil.getAlertOneButton(this,"运输单证信息中，拍照总和不能少于5张！",null).show();
            return false;
        }
        return true;
    }

    /**获取集合中已上传图片拍照数量*/
    public int getUpPhotoSize(List<DisWorkImageEntity.DisWorkImgData> tempArr){
        int countSize = 0;
        for (DisWorkImageEntity.DisWorkImgData tempData:tempArr){
            if (tempData.getImageUrl().indexOf("://")>-1){  //已上传图片
                countSize++;
            }
        }
        return countSize;
    }

    /**提交事故经过概述和处理意见*/
    private void submitWorkInfo() {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("userId",AppApplication.USER.data.userId));
        paramsList.add(new BasicNameValuePair("uid",disWorkInfo.data.uid));  //作业uid
        paramsList.add(new BasicNameValuePair("id",disWorkInfo.data.id+""));  //作业uid

        paramsList.add(new BasicNameValuePair("dispatchUid",caseDisTemp.uid));
        paramsList.add(new BasicNameValuePair("province",caseDisTemp.province));
        paramsList.add(new BasicNameValuePair("provinceCode",caseDisTemp.provinceCode));
        paramsList.add(new BasicNameValuePair("city",caseDisTemp.city));
        paramsList.add(new BasicNameValuePair("cityCode",caseDisTemp.cityCode));
        paramsList.add(new BasicNameValuePair("district",caseDisTemp.district));
        paramsList.add(new BasicNameValuePair("districtCode",caseDisTemp.districtCode));
        paramsList.add(new BasicNameValuePair("longitude",caseDisTemp.longitude+""));
        paramsList.add(new BasicNameValuePair("latitude",caseDisTemp.latitude+""));
        paramsList.add(new BasicNameValuePair("signInAddr",disWorkInfo.data.signInAddr));  //

        paramsList.add(new BasicNameValuePair("story",((EditText)examineView.findViewById(R.id.dispersive_work_examine_jg)).getText().toString())); //事故经过概述
        paramsList.add(new BasicNameValuePair("advice",((EditText)examineView.findViewById(R.id.dispersive_work_examine_yj)).getText().toString()));  //后续处理意见
        HttpUtils.requestPost(URLs.FSX_WORK_INFO_SAVE, paramsList, HttpRequestTool.FSX_WORK_INFO_SAVE);
        LoadDialogUtil.setMessageAndShow(this,"提交保存……");
    }

    /**加载Viewpager**/
    private void initPagerView() {
        viewlist = new ArrayList<View>(4);
        msgView = LayoutInflater.from(this).inflate(R.layout.dispersive_work_info_view, null);
        photoListView1 = (ExpandableListView) LayoutInflater.from(this).inflate(R.layout.dispersive_work_expandablelistview, null);
        photoListView2 = (ExpandableListView) LayoutInflater.from(this).inflate(R.layout.dispersive_work_expandablelistview, null);
        examineView =  LayoutInflater.from(this).inflate(R.layout.dispersive_work_examine_view, null);

//        uploadActivityHelp=new ParkPhotoUploadActivityHelp3(this);
//        uploadActivityHelp.setview(examineView);
        viewlist=new ArrayList<View>();
        viewlist.add(msgView);
        viewlist.add(photoListView1);
        viewlist.add(photoListView2);
        viewlist.add(examineView);
        viewPager.setAdapter(new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return viewlist.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewlist.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewlist.get(position));
                return viewlist.get(position);
            }
        });
    }

    /**刷新大标题（现场环境，运输单证）中已拍照数量*/
    public void changeCountNum() {
            int countSum1 = 0;
            for (List<DisWorkImageEntity.DisWorkImgData> tempList:siteImgEnList){
                countSum1 = countSum1 + tempList.size();
            }
            ((RadioButton)findViewById(R.id.dispersive_photo1_RB)).setText("现场环境\n(" + countSum1 + "/3)");
            int countSum2 = 0;
            for (List<DisWorkImageEntity.DisWorkImgData> tempList:documentImgEnList){
                countSum2 = countSum2 + tempList.size();
            }
            ((RadioButton)findViewById(R.id.dispersive_photo2_RB)).setText("运输单证\n(" + countSum2 + "/5)");
    }
    /**设置Radiobutton点击时ViewPager的切换**/
    @SuppressWarnings("deprecation")
    private void setRadioOnclick() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                if (arg1==R.id.dispersive_disInfo_RB) {
                    viewPager.setCurrentItem(0);
                }else if (arg1==R.id.dispersive_photo1_RB) {
                    viewPager.setCurrentItem(1);
                    choicePhotoLargeType = 0;
                    changeCountNum(); //刷新照片小类对应数量信息
                }else if (arg1==R.id.dispersive_photo2_RB) {
                    viewPager.setCurrentItem(2);
                    choicePhotoLargeType = 1;
                    changeCountNum(); //刷新照片小类对应数量信息
                }else if (arg1==R.id.dispersive_reson_RB) {
                    viewPager.setCurrentItem(3);
                }
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (arg0==0) {
                    radioGroup.check(R.id.dispersive_disInfo_RB);
                }else if (arg0==1) {
                    radioGroup.check(R.id.dispersive_photo1_RB);
                }else if (arg0==2) {
                    radioGroup.check(R.id.dispersive_photo2_RB);
                }else if (arg0==3) {
                    radioGroup.check(R.id.dispersive_reson_RB);
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) { }
            @Override
            public void onPageScrollStateChanged(int arg0) { }
        });
    }

    /**下载作业信息*/
    private void dowloadWorkInfo(){
        if (caseDisTemp==null || caseDisTemp.uid==null){
           Dialog dialog = DialogUtil.getAlertOneButton(this, "调度信息为空！！请联系管理员。", null);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    DispersiveWorkActivity.this.finish();  //调度信息或者调度uid为空提示用户。
                }
            });
            dialog.show();
        }
        List<String> paramsList = new ArrayList<>();
        if (AppApplication.USER==null || AppApplication.USER.data==null){  //用户信息为空重新登录。
            startActivity(new Intent(this, LoadingActivity.class));
            this.finish();
        }
        paramsList.add("userId");
        paramsList.add(AppApplication.USER.data.userId);
        paramsList.add("dispatchUid");
        paramsList.add(caseDisTemp.uid+"");
        HttpUtils.requestGet(URLs.FSX_WORK_INFO, paramsList, HttpRequestTool.FSX_WORK_INFO);
        LoadDialogUtil.setMessageAndShow(this,"数据加载中……");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventuploadSuccess(List<NameValuePair> value){
        int typecode = Integer.parseInt(value.get(0).getName());
        if (typecode == HttpRequestTool.FSX_WORK_INFO || typecode == HttpRequestTool.FSX_WORK_IMG_DOWLOAD
        || typecode == HttpRequestTool.FSX_WORK_INFO_SAVE || typecode == HttpRequestTool.FSX_WORK_SUBMIT_RIVIEW) {
            LoadDialogUtil.dismissDialog();
        }
        switch (typecode){
            case HttpRequestTool.FSX_WORK_INFO:
//                    getWorkInfo(value.get(0).getValue());
                getWorkInfo(value.get(0).getValue());
                break;
            case HttpRequestTool.FSX_WORK_IMG_DOWLOAD:
                    getImageInfo(value.get(0).getValue());
                break;
            case HttpRequestTool.FSX_WORK_INFO_SAVE:  //概述和处理意见保存返回消息。
                getWorkSaveResponseMsg(value.get(0).getValue());
                break;
            case HttpRequestTool.FSX_WORK_SUBMIT_RIVIEW:  //提交审核返回信息。
                getSubmitReviewResponseMsg(value.get(0).getValue());
                break;
        }
    }

    /**作业提交审核接口相应数据*/
    private void getSubmitReviewResponseMsg(String value) {
        BaseEntity bEn = JSON.parseObject(value, BaseEntity.class);
        if (bEn.success){  //提交审核成功
            Dialog dialog = DialogUtil.getAlertOneButton(this,bEn.msg,null);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    DispersiveWorkActivity.this.finish();  //弹框提示用户提交审核成功，关闭弹框后退出作业界面。
                }
            });
            dialog.show();
        }else{  //保存失败提示用户。
            DialogUtil.getAlertOneButton(this,"失败，"+bEn.msg,null).show();
        }
    }

    /**保存成功概述和建议后保存图片*/
    private void getWorkSaveResponseMsg(String value) {
        BaseEntity bEn = JSON.parseObject(value, BaseEntity.class);
        if (bEn.success){  //保存概要和建议成功后上传图片
            submitWorkImage(); //上传图片
        }else{  //保存失败提示用户。
            DialogUtil.getAlertOneButton(this,"失败，"+bEn.msg,null).show();
        }
    }

    /**上传做也图片**/
    private void submitWorkImage(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        List<DisWorkImageEntity.DisWorkImgData> submitImgEnList = new ArrayList<>();  // 待上传图片类集合

//        httpParams.add(new BasicNameValuePair( "id", tempImgData.id==0?"":tempImgData.id+""));  //替换图片时使用
        httpParams.add(new BasicNameValuePair( "workUid", disWorkInfo.data.uid));
//        httpParams.add(new BasicNameValuePair( "dispatchUid", caseDisTemp.uid));
        httpParams.add(new BasicNameValuePair( "FSX_UP_WORK_IMG", ""));

        for (List<DisWorkImageEntity.DisWorkImgData> tempImglist:siteImgEnList){  //现场环境照片
            for (DisWorkImageEntity.DisWorkImgData tempImgData:tempImglist){
                if (tempImgData!=null && tempImgData.getImageUrl()!=null && tempImgData.getImageUrl().indexOf("://")==-1){
                    submitImgEnList.add(tempImgData);
                    params.add(new BasicNameValuePair( tempImgData.imageType+"", tempImgData.getImageUrl()));
                }
            }
        }

        for (List<DisWorkImageEntity.DisWorkImgData> tempImglist:documentImgEnList){ //运输单证照片
            for (DisWorkImageEntity.DisWorkImgData tempImgData:tempImglist){
                if (tempImgData!=null && tempImgData.getImageUrl()!=null && tempImgData.getImageUrl().indexOf("://")==-1){
                    submitImgEnList.add(tempImgData);
                    params.add(new BasicNameValuePair( tempImgData.imageType+"", tempImgData.getImageUrl()));
                }
            }
        }
        if (submitImgEnList.size()>0){
            PhotoUploadUtil.upload(this,params,URLs.FSX_WORK_IMG_SAVE,httpParams);
        }else{
            DialogUtil.getAlertOneButton(this,"没有需要上传的图片！",null).show();
        }
    }

    /**上传影像资料成功后刷新界面**/
    @Subscribe(threadMode=ThreadMode.MAIN)
    public void eventmeth(String successCode){
        if ("UPLOAD_SUCCESS".equals(successCode)) {  //上传图片成功后重新加载Activity
            Intent intent = new Intent();
            intent.setClass(this,DispersiveWorkActivity.class);
            intent.putExtra("caseDisTemp",caseDisTemp);
            this.startActivity(intent );  //开始作业
            this.finish();
        }
    }

    /***j解析图片信息*/
    private void getImageInfo(String value) {
        workEnty = JSON.parseObject(value,DisWorkImageEntity.class);
        getImgEnList();
    }

    /**获取分类图片集合*/
    private void getImgEnList(){
        initImgEnList(); //初始化图片合集
        if (workEnty!=null && workEnty.data!=null && workEnty.data.size()>0){ //获取图片信息成功则整理显示
           for (DisWorkImageEntity.DisWorkImgData tempImgdata:workEnty.data){
               if (tempImgdata!=null && tempImgdata.imageType<3){
                   siteImgEnList.get(tempImgdata.imageType).add(tempImgdata);
               }else if(tempImgdata!=null && tempImgdata.imageType<17){
                   documentImgEnList.get(tempImgdata.imageType-3).add(tempImgdata);
               }
           }
            adapter1.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
            changeCountNum(); //刷新照片小类对应数量信息
        }
    }

    public void notifyExAdapter(){
        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
    }

    /***初始化图片集合*/
    private void initImgEnList(){
        siteImgEnList = new ArrayList<>(3);  //现场环境默认三个类别
        for (int i=0;i<3;i++){
            siteImgEnList.add(new ArrayList<>());
        }
        documentImgEnList = new ArrayList<>(14);  //事故单证默认类别14
        for (int i=0;i<14;i++){
            documentImgEnList.add(new ArrayList<>());
        }
        adapter1 = new MyDisExpandablelistAdapter(0);
        adapter2 = new MyDisExpandablelistAdapter(1);
        photoListView1.setAdapter(adapter1);
        photoListView2.setAdapter(adapter2);
    }

    /**解析作业信息，并显示、下载图片信息*/
    private void getWorkInfo(String value) {
        disWorkInfo = JSON.parseObject(value,DisWorkInfoEntity.class);
        if (disWorkInfo==null){
            DialogUtil.getAlertOneButton(this,"获取作业信息失败！请联系管理员。",null).show();
        }else{
           dowloadImgInfo();
        }
    }

    /**下载任务对应照片信息*/
    private void dowloadImgInfo(){
       ((EditText)examineView.findViewById(R.id.dispersive_work_examine_jg)).setText(disWorkInfo.data.story);
       ((EditText)examineView.findViewById(R.id.dispersive_work_examine_yj)).setText(disWorkInfo.data.advice);

        List<String> paramsList = new ArrayList<String>();
        paramsList.add("userId");
        paramsList.add(AppApplication.USER.data.userId);
        paramsList.add("workUid");
        paramsList.add(disWorkInfo.data.uid);
        HttpUtils.requestGet(URLs.FSX_WORK_IMG_DOWLOAD, paramsList, HttpRequestTool.FSX_WORK_IMG_DOWLOAD);
        LoadDialogUtil.setMessageAndShow(this,"数据加载中……");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class MyDisExpandablelistAdapter extends BaseExpandableListAdapter {

        private int checkImgGroupPstion;
        private List<NameValuePair> titlesArr;
        /**图片合集*/
        private List<List<DisWorkImageEntity.DisWorkImgData>> imgEnList;

        private MyDisExpandablelistAdapter(){}
        public MyDisExpandablelistAdapter(int checkImgGroupPstion){
            this.checkImgGroupPstion = checkImgGroupPstion;
            if (checkImgGroupPstion == 0){
                this.titlesArr = new DispersiveImgTypeUtil().getSceneImgTypes(); //现场照片
                imgEnList = siteImgEnList;
            }else {
                this.titlesArr = new DispersiveImgTypeUtil().getDocumentsImgTypes();//运输单证
                imgEnList = documentImgEnList;
            }
        }

        private LayoutInflater inflater = LayoutInflater.from(DispersiveWorkActivity.this);

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
        public View getChildView(int arg0, int arg1, boolean arg2, View contentview, ViewGroup arg4) {
            contentview=inflater.inflate(R.layout.dispersive_gridview_layout, null);

            GridView gridView=(GridView) contentview.findViewById(R.id.item_for_exlist_photoup_gridView1);
            DispersiveGalleryAdapter gAdapter;
            String key = titlesArr.get(arg0).getValue();
            gAdapter = new DispersiveGalleryAdapter(DispersiveWorkActivity.this, imgEnList.get(arg0), Integer.parseInt(titlesArr.get(arg0).getValue()));
            gridAdapterArr.put(key, gAdapter);
            gridView.setAdapter(gAdapter);
            return contentview;
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
            return titlesArr.get(arg0);
        }

        @Override
        public int getGroupCount() {
            return titlesArr.size();
        }

        @Override
        public long getGroupId(int arg0) {
            return arg0;
        }

        @Override
        public View getGroupView(int groupPostion, boolean arg1, View contentview, ViewGroup arg3) {
            contentview=inflater.inflate(R.layout.item_expandablelistview_photo_upload, null);
            ((TextView) contentview.findViewById(R.id.item_for_exlist_photoup_title)).setText(titlesArr.get(groupPostion).getName()); //分组标题
            TextView photoSum=(TextView) contentview.findViewById(R.id.item_for_exlist_photoup_sum);
            TextView photoDemand=(TextView) contentview.findViewById(R.id.item_for_exlist_photoup_demand);
            int groupPhotoSize = imgEnList.get(groupPostion).size();
            photoSum.setText(groupPhotoSize+"");
            if (checkImgGroupPstion==0) {
                ((TextView) contentview.findViewById(R.id.item_for_exlist_photoup_demand)).setText("1"); //分组标题必拍数量
            }
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

    /**相册选择照片后调用方法加载图片*/
    public void addPhoto(List<WorkPhotos.TableData.WorkPhotoEntitiy> temPhotoEntitiys,int imgType){
        if (imgType<3){
            for (WorkPhotos.TableData.WorkPhotoEntitiy woekEnty:temPhotoEntitiys){  //现场照片
               DisWorkImageEntity.DisWorkImgData disWorkEnty = new DisWorkImageEntity.DisWorkImgData();
                disWorkEnty.imageType = imgType;
                disWorkEnty.setImageUrl(woekEnty.location);
                siteImgEnList.get(imgType).add(disWorkEnty);
            }
            photoListView1.collapseGroup(imgType);
            photoListView1.expandGroup(imgType);
            changeCountNum(); //刷新照片小类对应数量信息
        }else if (imgType<17){//运输单证图片结合*/
            for (WorkPhotos.TableData.WorkPhotoEntitiy woekEnty:temPhotoEntitiys){  //事故单证
                DisWorkImageEntity.DisWorkImgData disWorkEnty = new DisWorkImageEntity.DisWorkImgData();
                disWorkEnty.imageType = imgType;
                disWorkEnty.setImageUrl(woekEnty.location);
                documentImgEnList.get(imgType-3).add(disWorkEnty);
            }
            photoListView2.collapseGroup(imgType);
            photoListView2.expandGroup(imgType);
            changeCountNum(); //刷新照片小类对应数量信息
        }
        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
        for (String key:gridAdapterArr.keySet()){
            gridAdapterArr.get(key).notifyDataSetChanged();
        }
    }

}
