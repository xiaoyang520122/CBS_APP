package com.cninsure.cp.cx;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.adapter.MyFragmentPagerAdapter;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.cx.fragment.CxDamageFragment;
import com.cninsure.cp.cx.fragment.CxInjuredFragment;
import com.cninsure.cp.cx.fragment.CxSubjectFragment;
import com.cninsure.cp.cx.fragment.CxSurveyFragment;
import com.cninsure.cp.cx.fragment.CxThirdFragment;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxSurveyTaskEntity;
import com.cninsure.cp.entity.cx.CxSurveyWorkEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.ocr.CxWorkPhotoHelp;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.ActivityFinishUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.google.android.material.tabs.TabLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CxSurveyWorkActivity extends BaseActivity implements View.OnClickListener {

    private TabLayout mTabLayout;
    public ViewPager mViewPager;
    private String[] mainTitlesArray = {"标的信息", "查勘信息" ,"三者信息","人伤信息","物损信息"};
    private MyFragmentPagerAdapter adapter;
    private CxSubjectFragment fg0;
    private CxSurveyFragment fg1;
    public Map<Integer, BaseFragment> fragmentMap;
    private FragmentManager fm;
    public CxWorkPhotoHelp cameraHelp; //调用摄像头拍照的帮助类**/
    /**OCR解析信息及图片路径1,身份证，2银行卡，3驾驶证，4行驶证，5签名**/
//    public OCREntity ocrEntity1,ocrEntity2,ocrEntity3,ocrEntity4,ocrEntity5;
    public CxSurveyWorkEntity cxWorkEntity;
    public CxSurveyTaskEntity cxTaskWorkEntity; //包含作业信息的任务信息
    /**拍摄照片路径**/
    private File file;
    public String QorderUid;
    private CxWorkhelp workhelp;
    private View uploadView;
    public PublicOrderEntity orderInfoEn; //任务信息
    public CxDictEntity cxSurveyDict = new CxDictEntity(); //拍照类型字典数据

    public static final int THIRD_SZ_XSZ_OCR = 8,THIRD_SZ_JSZ_OCR = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cx_work_activity);
        EventBus.getDefault().register(this);
        QorderUid = getIntent().getStringExtra("orderUid");
        orderInfoEn = (PublicOrderEntity) getIntent().getSerializableExtra("PublicOrderEntity");
        dowloadDictType();
    }

    private void initView() {
        uploadView = LayoutInflater.from(this).inflate(R.layout.imageupload_view, null);
        fm = getSupportFragmentManager();
        fragmentMap = new TreeMap<>();
        mViewPager = findViewById(R.id.cxworkA_viewpager);
        mTabLayout = findViewById(R.id.cxworkA_tablayout);
        if (cxWorkEntity==null)
        cxWorkEntity = new CxSurveyWorkEntity();
        initFragment();
        initViewPager();
        initTab();
        setBackOnclick();
    }

    private void setBackOnclick() {
        findViewById(R.id.CX_Act_Back_Tv).setOnClickListener(this);
        findViewById(R.id.CX_Act_More_Tv).setOnClickListener(this);
        ((TextView)findViewById(R.id.CX_Act_Title_Tv)).setText("现场查勘");
        ((TextView)findViewById(R.id.CX_Act_More_Tv)).setText("保存/提交");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.CX_Act_Back_Tv: ActivityFinishUtil.showFinishAlert(CxSurveyWorkActivity.this); break; //点击返回键
            case R.id.CX_Act_More_Tv: showSaveDialog(); break; //点击保存或暂存键
        }
    }

    /**弹框选择是1保存还是0暂存*/
    private void showSaveDialog() {
        new AlertDialog.Builder(this).setTitle("请选择")
                .setItems(new String[]{"保存", "提交"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isSubmit = which;
                        SaveWorkInfo(which);
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    /**保存作业信息*/
    private void SaveWorkInfo(int status) {
        cxWorkEntity.areaNo = orderInfoEn.areaNo;
        cxWorkEntity.area = orderInfoEn.area;
        cxWorkEntity.province = orderInfoEn.province;
        cxWorkEntity.caseProvince = orderInfoEn.caseProvince;
        cxWorkEntity.city = orderInfoEn.city;
       for (Integer key:fragmentMap.keySet()){
           fragmentMap.get(key).SaveDataToEntity();
       }
        submitWorkInfo(status);
    }

    /**作业暂存或提交审核*/
    private void submitWorkInfo(int status) {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        paramsList.add(new BasicNameValuePair("orderUid", QorderUid));  //订单uid
        paramsList.add(new BasicNameValuePair("content", JSON.toJSONString(cxWorkEntity)));  //作业内容，保存为JSON对象
        paramsList.add(new BasicNameValuePair("status", status + ""));  //0：暂存；1：提交（送审）
        if (cxTaskWorkEntity.data.id != null && cxTaskWorkEntity.data.id > 0)
            paramsList.add(new BasicNameValuePair("id", cxTaskWorkEntity.data.id+""));  //作业id
        HttpUtils.requestPost(URLs.CX_NEW_WORK_SAVE, paramsList, HttpRequestTool.CX_NEW_WORK_SAVE);
        LoadDialogUtil.setMessageAndShow(this, "处理中……");
    }

    private void initFragment() {
        fg0 = new CxSubjectFragment();
        fg1 = new CxSurveyFragment();

        fragmentMap.put(0, fg0);
        fragmentMap.put(1, fg1);
    }

    private void initViewPager() {
        adapter = new MyFragmentPagerAdapter(fm, fragmentMap);
        mViewPager.setAdapter(adapter);
    }

    private void initTab() {
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.bulue_main));
        mTabLayout.setupWithViewPager(mViewPager);
        displayTabText();  //TabLayout关联ViewPager后才设置标题文字，标题个数是根据Fragment个数来确定的
    }

    public Handler refreshHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            refreshContent(msg.what,(Boolean)msg.obj);
        }
    };

    /**
     * 如果添加了“三者、物损、人伤”就刷新界面
     * @param tabCode 添加或者移除的 内容代码，2三者，3人伤，4物损
     * @param addBle  添加或者移除，true添加，false移除
     */
    public void refreshContent(Integer tabCode, boolean addBle) {

        if (addBle) {  //添加
            switch (tabCode) {
                case 2: fragmentMap.put(2,new CxThirdFragment()); break;
                case 3: fragmentMap.put(3,new CxInjuredFragment()); break;
                case 4: fragmentMap.put(4,new CxDamageFragment()); break;
            }
        }else{  //移除
            switch (tabCode) {
                case 2:
                    fragmentMap.remove(2);
                    cxWorkEntity.thirdPartys = new ArrayList<>(); //移除对应的数据
                    break;
                case 3:
                    fragmentMap.remove(3);
                    cxWorkEntity.injuredInfos = new ArrayList<>();//移除对应的数据
                    break;
                case 4:
                    fragmentMap.remove(4);
                    cxWorkEntity.damageInfos = new ArrayList<>();//移除对应的数据
                    break;
            }
        }
        notifyChange(tabCode,addBle);
    }

    private void notifyChange(Integer tabCode, boolean addBle){
        adapter.notifyDataSetChanged();
        displayTabText();
//        if (mViewPager.getCurrentItem() ==1) //只有在第二个界面的时候，选中才会跳转到选中页面
//        if (addBle) mViewPager.setCurrentItem(tabCode);
    }


    /**刷新显示TabLayout菜单*/
    private void displayTabText() {
        int i = 0;
        for (Map.Entry<Integer, BaseFragment> mapEn: fragmentMap.entrySet()) {
            mTabLayout.getTabAt(i).setText(mainTitlesArray[mapEn.getKey()]);
            i++;
        }
    }

    private void dowloadDictType() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("type");
        params.add("cxOrderWorkImageType,accident_type,accident_small_type,accident_reason,accident_small_reason,survey_type,damage_loss_type," +
                "accident_liability,loss_type,loss_object_type,compensation_method,survey_conclusion,carno_type,car_usetype,injured_type" +
                ",isLicenseKou,licenseMissingResult,quasiDrivingType,comfirmLiabilityType,waterLevel,fraudTag,fraudType");
        HttpUtils.requestGet(URLs.CX_NEW_GET_IMG_TYPE_DICT, params, HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT);
    }

    private void dowloadOderView() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("userId");
        params.add(AppApplication.getUSER().data.userId);
        params.add("orderUid");
        params.add(QorderUid);
        HttpUtils.requestGet(URLs.CX_NEW_GET_ORDER_VIEW_BY_UID, params, HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID);
    }

    /**初始化**/
    @Override
    protected void onResume() {
        super.onResume();
        if (cameraHelp==null) {
            cameraHelp=new CxWorkPhotoHelp(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==1 || requestCode==2 || requestCode==3 || requestCode==4
                || requestCode==THIRD_SZ_XSZ_OCR || requestCode==THIRD_SZ_JSZ_OCR) {
            if (data!=null) {
                file=(File)data.getSerializableExtra("FilePath");
                cameraHelp.forString(requestCode,file);
            }
        }else if (resultCode== HttpRequestTool.LINEPATH) { //签字返回图片
            upSignPhoto(data,5);
        }else if (requestCode == PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE){  //上传附件选择的文件。
            fg1.inspectFileSize(data); //判断文件大小是否小于20M
        }else if (resultCode != RESULT_OK) { // 此处的 RESULT_OK 是系统自定义得一个常量
            Log.e("getphotos", "ActivityResult resultCode error");
			ToastUtil.showToastLong(this, "操作失败！");
            return;
        }else {
            workhelp.eventresultcode(requestCode, resultCode, data);
            EventBus.getDefault().post(resultCode);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnetH5(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT:
                dowloadOderView();
                LoadDialogUtil.dismissDialog();
                cxSurveyDict.list = JSON.parseArray(values.get(0).getValue(), DictData.class);
                workhelp = new CxWorkhelp(this, uploadView, cxSurveyDict.getDictByType("cxOrderWorkImageType"), null);
                break;
            case HttpRequestTool.UPLOAD_FILE_PHOTO: //上传附件成功
                fg1.getUploadFileInfo(values);
                break;
            case HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID: //获取订单信息
                LoadDialogUtil.dismissDialog();
                getTaskWorkInfo(values.get(0).getValue());
                break;
            case HttpRequestTool.CX_NEW_WORK_SAVE: // 保存或提交审核返回数据
                LoadDialogUtil.dismissDialog();
                getTaskWorkSavaInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    private int isSubmit;
    /**保存或提交审核返回数据*/
    private void getTaskWorkSavaInfo(String value) {
        BaseEntity baseEntity = JSON.parseObject(value,BaseEntity.class);
        if (baseEntity.success) {
            Dialog dialog = DialogUtil.getAlertDialog(this,baseEntity.msg,"提示！");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (isSubmit==1)
                        CxSurveyWorkActivity.this.finish();
                    else dowloadOderView();
                }
            });
            dialog.show();
        }else DialogUtil.getErrDialog(this,"操作失败："+baseEntity.msg).show();
    }

    /**解析获取的到的任务作业信息
     * @param value*/
    private void getTaskWorkInfo(String value) {
        try {
            cxTaskWorkEntity = JSON.parseObject(value, CxSurveyTaskEntity.class);
        } catch (Exception e) {  //解析失败，关闭界面
           disPlayErrorDialog();
            e.printStackTrace();
        }

        if (cxTaskWorkEntity == null )  cxTaskWorkEntity = new CxSurveyTaskEntity();
        if (cxTaskWorkEntity.data == null  ) cxTaskWorkEntity.data = new CxSurveyTaskEntity.CxTaskSurveyEntity();
        if (cxTaskWorkEntity.data.contentJson == null) cxTaskWorkEntity.data.contentJson = new CxSurveyWorkEntity();
        cxWorkEntity = cxTaskWorkEntity.data.contentJson;
        initView();
//        if (cxTaskWorkEntity != null && cxTaskWorkEntity.data != null && cxTaskWorkEntity.data.contentJson != null) {  //作业信息不为空就赋值，并显示
//            cxWorkEntity = cxTaskWorkEntity.data.contentJson;
//            handler.sendMessage(new Message());
//        }
//        else{  //返回信息解析失败，不能继续作业
//            disPlayErrorDialog();
//        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            initView();
        }
    };

    /**提示错误后，并在关闭dialog的时候结束*/
    private void disPlayErrorDialog() {
        DialogUtil.getErrDialogAndFinish(this, "获取任务信息失败，请联系管理员！", new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CxSurveyWorkActivity.this.finish();
            }
        }).show();
    }

    /**上传OCR图片和签字后返回成功、图片名称及后缀 * 1 身份证识别 * 2 银行卡识别* 3 驾驶证识别* 4 行驶证识别 * **/
//    @Subscribe(threadMode=ThreadMode.MAIN)
//    public void eventmeth(NameValuePair valuePair){
//        if ("UPLOAD_SUCCESS".equals(valuePair.getName())) {
////            cameraHelp.sendMsgToBack(valuePair.getValue());
//        }
//    }
    /**上传签字后返回成功与图片名称及后缀 * 1 身份证识别 * 2 银行卡识别* 3 驾驶证识别* 4 行驶证识别 * **/
    @Subscribe(threadMode=ThreadMode.MAIN)
    public void eventmeth(List<NameValuePair> responseValue){
        int type=Integer.parseInt(responseValue.get(0).getValue());
        if (type==5) {  //签字返回信息
            if ("UPLOAD_SUCCESS".equals(responseValue.get(1).getName())) {
                signMeath(responseValue.get(1).getValue());
            }
        }else  if (type==4||type==3||type==2||type==1) {
            disPlayOcrInfo(type,responseValue.get(1).getValue());
//            cameraHelp.sendMsgToBack(responseValue.get(1).getValue());
        }else if ( type==THIRD_SZ_XSZ_OCR || type==THIRD_SZ_JSZ_OCR){  //上传成功以后，要将图片名称保存到三者对应的集合中
            ((CxThirdFragment)fragmentMap.get(2)).disPlayOcrInfo(type,responseValue.get(1).getValue());
        }
    }

    /**处理签字图片*/
    private void signMeath(String url){
        if (url!=null) {
            cxWorkEntity.surveyInfo.signLicense = url;//签字图片名称，不包含完整路径
            fg1.disPlaySign(); //显示签名
        }
    }

    /**显示OCR信息  1 身份证识别 * 2 银行卡识别* 3 驾驶证识别* 4 行驶证识别*/
    private void disPlayOcrInfo(int type, String value) {
        switch (type){
            case 1:  //disPlayIdCard();
            case 2:  fg0.disBankCardInfo(value); //银行卡识别
            case 3:  fg0.disPlayDriverLicense(value); //驾驶证识别
            case 4:  fg0.disPlayMoveLicense(value); //行驶证识别

        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**返回键提示是否退出*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            ActivityFinishUtil.showFinishAlert(this);
            return false;//拦截事件
        }
        return super.onKeyDown(keyCode, event);
    }
}
