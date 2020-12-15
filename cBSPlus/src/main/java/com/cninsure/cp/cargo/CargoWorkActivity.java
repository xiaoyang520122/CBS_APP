package com.cninsure.cp.cargo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cargo.adapter.CargoExpandablelistAdapter;
import com.cninsure.cp.cargo.util.CargoPhotoUploadUtil;
import com.cninsure.cp.cargo.util.SurveyUtil;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cargo.CargoCaseBaoanTable;
import com.cninsure.cp.entity.cargo.CargoCaseWorkImagesTable;
import com.cninsure.cp.entity.cargo.CargoCaseWorkSurveyTable;
import com.cninsure.cp.entity.cargo.CargoSurveyEntity;
import com.cninsure.cp.entity.cargo.ContainerRecords;
import com.cninsure.cp.entity.cargo.DispatchMatter;
import com.cninsure.cp.entity.cargo.SurveyRecordsEntity;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.fc.activity.CaseInputActivity;
import com.cninsure.cp.ocr.LinePathActivity;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.zcw.togglebutton.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CargoWorkActivity extends BaseActivity {
    public static CargoWorkActivity instence;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private RadioGroup radioGroup;
    private View msgView,surveyView,surveyNotView,examineView;
    private ExpandableListView photoListView;
    public static CargoExpandablelistAdapter adapter2;//adapter1;
    private Map<String , CargoExpandablelistAdapter.CargoGalleryAdapter> gridAdapterArr;
    private List<View> viewlist;
    public CargoCaseBaoanTable caseBaoanTable;
    /***照片接口返回数据*/
    private List<CargoCaseWorkImagesTable> workImgEnty;
    /**分类后的照片*/
    public static Map<String, List<CargoCaseWorkImagesTable>> classImgMap;
    /**作业数据实体类**/
    private CargoSurveyEntity surveyEn;
    /**查勘记录*/
    public SurveyRecordsEntity sREn;
    /**现场环境图片结合*/
//    private List<List<CargoCaseWorkImagesTable>> siteImgEnList;
    /**拍照类型字典数据*/
    private CxDictEntity photoTypeDict; //拍照类型字典数据
    /**大标题分类*/
    private List<DictData> parentPtoTypeDict;
    /**拍照分类小类*/
    private Map<String,List<DictData>> childPtoTypeDicts;
    /**View数据加载获取帮助类*/
    private SurveyUtil surveyUtil;
//    public static Map<Long, List<CargoCaseWorkImagesTable>> imgMap;
    private boolean submitBle=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cargo_work_main_activity);
        ((TextView)findViewById(R.id.ACTION_V_CTV)).setText("查勘作业");
        EventBus.getDefault().register(this);
        initview();
        setRadioOnclick();
        setAction();
        disPlayDispatchInfo();
        photoTypeDict = new CxDictEntity();
        dowloadImgType();
        instence = this;
    }

    /**获取拍照分类*/
    private void dowloadImgType() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("type");
        params.add("hyxCaseWorkMediaType");
        HttpUtils.requestGet(URLs.CX_NEW_GET_IMG_TYPE_DICT, params, HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT);
    }

    private void initview() {
        classImgMap = new HashMap<>();
        gridAdapterArr = new HashMap<>(17);
        viewPager= findViewById(R.id.CargoWM_ViewPager);
        radioGroup= findViewById(R.id.CargoWM_RadioGroup);
        caseBaoanTable = (CargoCaseBaoanTable) getIntent().getSerializableExtra("caseBaoanTable");
        initPagerView();
    }

    /**显示报案信息*/
    private void disPlayDispatchInfo(){
        if (caseBaoanTable == null || caseBaoanTable.id == null){  //报案信息缺失影响保存的直接报错后关闭界面。
           Dialog dialog =  DialogUtil.getAlertOneButton(this,"案件信息获取失败！",null);
            dialog.setOnDismissListener(dialog1 -> CargoWorkActivity.this.finish());
        }else{
            disPlay();
        }
    }

    /**组装报案信息并显示*/
    private void disPlay(){
        ListView listView = msgView.findViewById(R.id.dispersive_work_Info_listView);
        List<String> infoData= new ArrayList<String>();
        infoData.add("出险险种：\t"+ caseBaoanTable.insuranceTypeName);
        infoData.add("案件对接人：\t"+ caseBaoanTable.ggsName +"/"+ caseBaoanTable.ggsMobile);
        infoData.add("保险公司：\t"+ caseBaoanTable.wtName);
        infoData.add("报案号：\t"+ caseBaoanTable.reportNo);
        infoData.add("被保险人联系人：\t"+ caseBaoanTable.insuredContract+"/"+ caseBaoanTable.insuredContractMobile);
        infoData.add("查勘地点：\t"+ caseBaoanTable.surveyAddress);
        infoData.add("派工事项：\t"+new DispatchMatter().getMatterForString(caseBaoanTable.dispatchMatter));
        infoData.add("派工事项补充：\t"+ caseBaoanTable.dispatchMatterAdd);
        ArrayAdapter<String> infoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,infoData);
        listView.setAdapter(infoAdapter);
    }

    private void setAction(){
        findViewById(R.id.ACTION_V_LTV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargoWorkActivity.this.finish();
            }
        });
        if (caseBaoanTable !=null && (caseBaoanTable.status==11 || caseBaoanTable.status==5)) { //驳回和已到达现场的才能提交审核
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
        new AlertDialog.Builder(CargoWorkActivity.this).setTitle("选择操作！\n提交审核前，请先保存作业信息！")
                .setItems(new String[]{"保存作业信息", "提交审核"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0){ //选择保存作业信息
                            submitWorkInfo(0); //保存作业信息
                        }else if (which==1){  //选择提交审核
//                            submitForReview(); //提交审核
                            submitWorkInfo(1); //提交审核
                        }
                    }
                }).create().show();
    }

    /**作业提交审核*/
    private void submitForReview() {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        HttpUtils.requestPost(URLs.FSX_WORK_SUBMIT_RIVIEW, paramsList, HttpRequestTool.FSX_WORK_SUBMIT_RIVIEW);
        LoadDialogUtil.setMessageAndShow(this, "处理中……");
    }

    /**获取集合中已上传图片拍照数量*/
    public int getUpPhotoSize(List<CargoCaseWorkImagesTable> tempArr){
        int countSize = 0;
        for (CargoCaseWorkImagesTable tempData:tempArr){
            if (tempData.fileUrl.indexOf("://")>-1){  //已上传图片
                countSize++;
            }
        }
        return countSize;
    }

    /**
     * 提交事故经过概述和处理意见 0暂存（保存），1提交审核
     * @param status
     */
    private void submitWorkInfo(int status) {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        if (surveyEn!=null && surveyEn.data!=null && surveyEn.data.id!=null){
            paramsList.add(new BasicNameValuePair("id",surveyEn.data.id+""));
        }
        paramsList.add(new BasicNameValuePair("caseId",caseBaoanTable.id+""));
        paramsList.add(new BasicNameValuePair("status",status+""));

        paramsList.add(new BasicNameValuePair("listRecords",surveyEn.data.listRecords));
        paramsList.add(new BasicNameValuePair("askRecords",surveyEn.data.askRecords));
        paramsList.add(new BasicNameValuePair("lossRecords",surveyEn.data.lossRecords));
        paramsList.add(new BasicNameValuePair("materialList",surveyEn.data.materialList));

        surveyUtil.reflashData();
        paramsList.add(new BasicNameValuePair("surveyRecords",JSON.toJSONString(sREn)));  //参看信息
        if (status==0){ //保存
            HttpUtils.requestPost(URLs.CARGO_SURVEY_SAVE, paramsList, HttpRequestTool.CARGO_SURVEY_SAVE);
        }else if (status==1){  //提交
            HttpUtils.requestPost(URLs.CARGO_SURVEY_SAVE, paramsList, HttpRequestTool.CARGO_SURVEY_SUBMIT);
        }
        LoadDialogUtil.setMessageAndShow(this,"提交保存……");
    }

    /**加载Viewpager**/
    private void initPagerView() {
        viewlist = new ArrayList<View>(4);
        msgView = LayoutInflater.from(this).inflate(R.layout.dispersive_work_info_view, null);
        surveyView = LayoutInflater.from(this).inflate(R.layout.cargo_survey_records_iscontainer, null);
        surveyNotView = LayoutInflater.from(this).inflate(R.layout.cargo_survey_records_notcontainer, null);
        photoListView = (ExpandableListView) LayoutInflater.from(this).inflate(R.layout.public_expandablelistview, null);
        examineView =  LayoutInflater.from(this).inflate(R.layout.dispersive_work_examine_view, null);

//      uploadActivityHelp=new ParkPhotoUploadActivityHelp3(this);
//      uploadActivityHelp.setview(examineView);
        viewlist=new ArrayList<View>();
        viewlist.add(msgView);
        viewlist.add(surveyView);
//        viewlist.add(examineView);
        viewlist.add(photoListView);
        viewPager.setAdapter(getadapter());
        setSignOnclick(surveyView.findViewById(R.id.CargoSR__signButton));
        setSignOnclick(surveyNotView.findViewById(R.id.CargoSRN__signButton));
        setContainerbleChangLesenler(surveyView.findViewById(R.id.CargoSR_ckDocType));
        setContainerbleChangLesenler(surveyNotView.findViewById(R.id.CargoSRN_ckDocTypeNotC));
    }

    /**签字监听事件*/
    private void setSignOnclick(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSign();
            }
        });
    }
    /**启动签字**/
    private void startSign(){
        Intent intent=new Intent(this, LinePathActivity.class);
        intent.putExtra("orderUid", this.getIntent().getStringExtra("orderUid"));
        startActivityForResult(intent, HttpRequestTool.LINEPATH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== HttpRequestTool.LINEPATH) { //签字返回图片
            upSignPhoto(data,5);
        }
    }
    /**上传签字图片**/
    private void upSignPhoto(Intent data,int type) {//(String)data.getStringExtra("LinePathFilePath");
        if (null!=data&&null!=data.getStringExtra("LinePathFilePath")) {
            List<NameValuePair> fileUrls=new ArrayList<NameValuePair>();
            fileUrls.add(new BasicNameValuePair("0", (String)data.getStringExtra("LinePathFilePath")));
            PhotoUploadUtil.uploadOCR(this, fileUrls, URLs.UP_OCR_PHOTO, type);
        }
    }

    private PagerAdapter getadapter(){
        pagerAdapter = new PagerAdapter() {

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
            @Override
            public int getItemPosition(Object object) {
                // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
                return POSITION_NONE;
            }
        };
        return pagerAdapter;
    }

    private void setContainerbleChangLesenler(ToggleButton tgb) {
        tgb.setToggleOn(true); //默认选中
        tgb.setOnToggleChanged(on -> {
            if (on) {  //集装箱
                viewlist.set(1,surveyView);
                sREn.ckDocType="0";
                ((ToggleButton)surveyView.findViewById(R.id.CargoSR_ckDocType)).setToggleOn(true);
                surveyUtil.disPlayContainerInfo(caseBaoanTable.caseNo,caseBaoanTable.insured);
            }else{  //非集装箱
                viewlist.set(1,surveyNotView);
                sREn.ckDocType="1";
                ((ToggleButton)surveyNotView.findViewById(R.id.CargoSRN_ckDocTypeNotC)).setToggleOff(true);
                surveyUtil.disPlayNotContainerInfo(caseBaoanTable.insured,caseBaoanTable.riskTime);
            }
            pagerAdapter.notifyDataSetChanged();
        });
    }

    /**设置Radiobutton点击时ViewPager的切换**/
    @SuppressWarnings("deprecation")
    private void setRadioOnclick() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                if (arg1==R.id.CargoWM_disInfo_RB) {
                    viewPager.setCurrentItem(0);
                }else if (arg1==R.id.CargoWM_1_RB) {
                    viewPager.setCurrentItem(1);
//                }else if (arg1==R.id.CargoWM_2_RB) {
//                    viewPager.setCurrentItem(2);
//                    choicePhotoLargeType = 1;
//                    changeCountNum(); //刷新照片小类对应数量信息
//                }else if (arg1==R.id.CargoWM_3_RB) {
//                    viewPager.setCurrentItem(2);
                }else if (arg1==R.id.CargoWM_4_RB) {
                    viewPager.setCurrentItem(3);
                }
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (arg0==0) {
                    radioGroup.check(R.id.CargoWM_disInfo_RB);
//                }else if (arg0==1) {
//                    radioGroup.check(R.id.CargoWM_photo1_RB);
                }else if (arg0==1) {
                    radioGroup.check(R.id.CargoWM_1_RB);
                }else if (arg0==2) {
                    radioGroup.check(R.id.CargoWM_4_RB);
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
        if (caseBaoanTable ==null || caseBaoanTable.id==null){
           Dialog dialog = DialogUtil.getAlertOneButton(this, "报案信息为空！！请联系管理员。", null);
            dialog.setOnDismissListener(dialog1 -> {
                CargoWorkActivity.this.finish();  //报案信息或者报案uid为空提示用户。
            });
            dialog.show();
        }
        List<String> paramsList = new ArrayList<>();
        paramsList.add("caseId");
        paramsList.add(caseBaoanTable.id+"");
        HttpUtils.requestGet(URLs.CARGO_SURVEY_VIEW, paramsList, HttpRequestTool.CARGO_SURVEY_VIEW);
        LoadDialogUtil.setMessageAndShow(this,"数据加载中……");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventuploadSuccess(List<NameValuePair> value){

        try {
            int typecode = Integer.parseInt(value.get(0).getName());
            if (typecode == HttpRequestTool.CARGO_SURVEY_VIEW || typecode == HttpRequestTool.FSX_WORK_IMG_DOWLOAD
            || typecode == HttpRequestTool.CARGO_SURVEY_SAVE || typecode == HttpRequestTool.FSX_WORK_SUBMIT_RIVIEW) {
                LoadDialogUtil.dismissDialog();
            }
            switch (typecode){
                case HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT:
                    LoadDialogUtil.dismissDialog();
                    getPhotoTypeDict(value.get(0).getValue());
                    break;
                case HttpRequestTool.CARGO_SURVEY_VIEW:
                    getWorkInfo(value.get(0).getValue());
                    break;
                case HttpRequestTool.CARGO_WORK_IMG:  //作业图片
                        getImageInfo(value.get(0).getValue());
                    break;
                case HttpRequestTool.CARGO_SURVEY_SAVE:  //保存作业信息。
                    getWorkSaveResponseMsg(value.get(0).getValue());
                    break;
                case HttpRequestTool.CARGO_SURVEY_SUBMIT:  //提交审核。
                    getWorkSubmitResponseMsg(value.get(0).getValue());
                    break;
                case HttpRequestTool.FSX_WORK_SUBMIT_RIVIEW:  //提交审核返回信息。
                    getSubmitReviewResponseMsg(value.get(0).getValue());
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            int type=Integer.parseInt(value.get(0).getValue());
            if (type==5) {  //签字返回信息
                if ("UPLOAD_SUCCESS".equals(value.get(1).getName())) {
                    signMeath(value.get(1).getValue());
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void getWorkSubmitResponseMsg(String value) {
        BaseEntity bEn = JSON.parseObject(value, BaseEntity.class);
        if (bEn.success){  //
            submitBle = true;
            submitWorkImage(); //上传图片
        }else{  //保存失败提示用户。
            DialogUtil.getAlertOneButton(this,"失败，"+bEn.msg,null).show();
        }
    }

    /**处理签字图片*/
    private void signMeath(String url){
        if (url!=null) {
            sREn.records.signatureUrl = url;//签字图片名称，不包含完整路径
            surveyUtil.disPlaySign();//显示签名
        }
    }


    /***
     * 获取拍照分类字典值
     * @param value
     */
    private void getPhotoTypeDict(String value) {
        photoTypeDict.list = JSON.parseArray(value, DictData.class);
        if (photoTypeDict!=null){
            dowloadWorkInfo(); //下载作业信息
            screenPtoType();
        }else{
            ToastUtil.showToastLong(this,"无法获取拍照分类！"); //无法获取分类，结束进程
            this.finish();
        }
    }

    /**筛选拍照分类，按照父类，子类进行分组*/
    private void screenPtoType() {
        parentPtoTypeDict = new ArrayList<>(7);
        childPtoTypeDicts = new HashMap<>(7);
        for (DictData dd:photoTypeDict.list){
            if (dd.parentId == 300){  //parentId=300的是父类拍照类型
                parentPtoTypeDict.add(dd);
            }else{
                addChild(dd);
            }
        }
    }


    private void addChild(DictData dd) {
        if (childPtoTypeDicts.get(dd.parentId+"")==null){
            childPtoTypeDicts.put(dd.parentId+"",new ArrayList<>());
        }
        childPtoTypeDicts.get(dd.parentId+"").add(dd);
    }

    /**作业提交审核接口相应数据*/
    private void getSubmitReviewResponseMsg(String value) {
        BaseEntity bEn = JSON.parseObject(value, BaseEntity.class);
        if (bEn.success){  //提交审核成功
            Dialog dialog = DialogUtil.getAlertOneButton(this,bEn.msg,null);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    CargoWorkActivity.this.finish();  //弹框提示用户提交审核成功，关闭弹框后退出作业界面。
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
        if (bEn.success){  //
            submitWorkImage(); //上传图片
        }else{  //保存失败提示用户。
            DialogUtil.getAlertOneButton(this,"失败，"+bEn.msg,null).show();
        }
    }

    /**上传作业图片**/
    private void submitWorkImage(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        List<NameValuePair> httpParams = new ArrayList<NameValuePair>();
        List<CargoCaseWorkImagesTable> submitImgEnList = new ArrayList<>();  // 待上传图片类集合

        for (String keyStr:classImgMap.keySet()){ //现场状况照片
            for (CargoCaseWorkImagesTable tempImgData:classImgMap.get(keyStr)){
                if (tempImgData!=null && tempImgData.fileUrl!=null && tempImgData.fileUrl.indexOf("/")!=-1){ //需要未上传
                    submitImgEnList.add(tempImgData);
                    params.add(new BasicNameValuePair( tempImgData.type, tempImgData.fileName));
                }
            }
        }
        if (submitImgEnList.size()>0){
            CargoPhotoUploadUtil.imgUpload(this,submitImgEnList,URLs.UP_OCR_PHOTO);
        }else{
            DialogUtil.getAlertOneButton(this,"没有需要上传的图片！",null).show();
            submitFinish();
        }
    }

    /**上传影像资料成功后刷新界面**/
    @Subscribe(threadMode=ThreadMode.MAIN)
    public void eventmeth(String successCode) {
        if ("UPLOAD_SUCCESS".equals(successCode)) {  //上传图片成功后重新加载Activity
            if (!submitBle) {
                Intent intent = new Intent();
                intent.setClass(this, CargoWorkActivity.class);
                intent.putExtra("caseBaoanTable", caseBaoanTable);
                this.startActivity(intent);  //开始作业
            }
            this.finish();
        }
    }

    public void submitFinish(){
        if (submitBle)
        DialogUtil.getAlertOneButton(this, "提交成功!", (arg0, arg1) -> {
           CargoWorkActivity.this.finish();
        }).show();
    }

        /***j解析图片信息*/
        private void getImageInfo (String value){
            workImgEnty = JSON.parseArray(value, CargoCaseWorkImagesTable.class);
            screenImgEnList();
        }

        /**获取分类图片集合*/
        private void screenImgEnList () {
            classImgMap = new HashMap<>();
            for (DictData dda : parentPtoTypeDict) {
                for (DictData cdda : childPtoTypeDicts.get(dda.value))
                    classImgMap.put(cdda.value, new ArrayList<>());
            }  //默认初始化
            if (workImgEnty != null && workImgEnty.size() > 0) { //获取图片信息成功则整理显示
                for (CargoCaseWorkImagesTable tempImg : workImgEnty) {
                    addImgToDictType(tempImg);
                }
            }
            adapter2 = new CargoExpandablelistAdapter(this, parentPtoTypeDict, childPtoTypeDicts, classImgMap, caseBaoanTable);
            photoListView.setAdapter(adapter2);
        }

        @Override
        protected void onResume () {
            super.onResume();
            if (adapter2 != null) adapter2.notifyDataSetChanged();
        }

    @Override
    protected void onStop() {
        super.onStop();
        LoadDialogUtil.dismissDialog();
    }

    /**将图片归类到字典map中去*/
        private void addImgToDictType (CargoCaseWorkImagesTable tempImg){
            if (classImgMap.get(tempImg.type) == null)
                classImgMap.put(tempImg.type, new ArrayList<>());
            classImgMap.get(tempImg.type).add(tempImg);
        }

        /**解析作业信息，并显示、下载图片信息*/
        private void getWorkInfo (String value){
            surveyEn = JSON.parseObject(value, CargoSurveyEntity.class);
            if (surveyEn == null) {
                DialogUtil.getAlertOneButton(this, "获取作业信息失败！请联系管理员。", null).show();
            } else {
                displaySurveyInfo();
            }
            dowloadImgInfo();
        }

        /**
         * 显示作业信息
         */
        private void displaySurveyInfo () {
            if (surveyEn.data != null && !TextUtils.isEmpty(surveyEn.data.surveyRecords)) {  //surveyView
                sREn = JSON.parseObject(surveyEn.data.surveyRecords, SurveyRecordsEntity.class);
                if (sREn == null) {
                    initSren();
                } else {
                    surveyUtil = new SurveyUtil(this, surveyView, surveyNotView, sREn);
                }
                if (!TextUtils.isEmpty(sREn.ckDocType) && sREn.ckDocType.equals("0")) {
                    viewlist.set(1, surveyView); //显示集装箱View 及对应数据
                    surveyUtil.disPlayContainerInfo(caseBaoanTable.caseNo, caseBaoanTable.insured);
                    ((ToggleButton)surveyView.findViewById(R.id.CargoSR_ckDocType)).setToggleOn(true);
                    ((ToggleButton)surveyNotView.findViewById(R.id.CargoSRN_ckDocTypeNotC)).setToggleOn(true);
                } else {
                    viewlist.set(1, surveyNotView);  //显示非集装箱View 及对应数据
                    surveyUtil.disPlayNotContainerInfo(caseBaoanTable.insured, caseBaoanTable.riskTime);
                    ((ToggleButton)surveyView.findViewById(R.id.CargoSR_ckDocType)).setToggleOff(true);
                    ((ToggleButton)surveyNotView.findViewById(R.id.CargoSRN_ckDocTypeNotC)).setToggleOff(true);
                }
                pagerAdapter.notifyDataSetChanged();
            } else {
                surveyEn = new CargoSurveyEntity();
                surveyEn.data = new CargoCaseWorkSurveyTable();
                initSren();
            }
        }

        private void initSren () {
            sREn = new SurveyRecordsEntity();
            sREn.ckDocType = "0";
            sREn.records = new ContainerRecords();
            sREn.records.caseNo = caseBaoanTable.caseNo;
            sREn.records.insured = caseBaoanTable.insured;
            sREn.records.riskTime = caseBaoanTable.riskTime;
            surveyUtil = new SurveyUtil(this, surveyView, surveyNotView, sREn);
            surveyUtil.disPlayContainerInfo(caseBaoanTable.caseNo, caseBaoanTable.insured);
        }

        /**下载任务对应照片信息*/
        private void dowloadImgInfo () {
            List<String> paramsList = new ArrayList<>();
            paramsList.add("baoanUid");
            paramsList.add(caseBaoanTable.caseNo);
            paramsList.add("isDelete");
            paramsList.add("0");
            HttpUtils.requestGet(URLs.CARGO_WORK_IMG, paramsList, HttpRequestTool.CARGO_WORK_IMG);
            LoadDialogUtil.setMessageAndShow(this, "数据加载中……");
        }

        @Override
        protected void onDestroy () {
            super.onDestroy();
            EventBus.getDefault().unregister(this);
        }

    /**监听返回键，并调用退出提示方法**/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            HintOut();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**提示用户是否真的要退出该界面，避免勿退出！**/
    private void HintOut(){
        Dialog  dialog=DialogUtil.getAlertOnelistener(this, "确定要退出该页面吗！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                CargoWorkActivity.this.finish();
            }
        });
        dialog.show();
    }
    }
