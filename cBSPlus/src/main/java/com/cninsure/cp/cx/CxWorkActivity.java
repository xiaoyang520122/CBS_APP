package com.cninsure.cp.cx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.adapter.MyFragmentPagerAdapter;
import com.cninsure.cp.cx.fragment.CxDamageFragment;
import com.cninsure.cp.cx.fragment.CxInjuredFragment;
import com.cninsure.cp.cx.fragment.CxSubjectFragment;
import com.cninsure.cp.cx.fragment.CxSurveyFragment;
import com.cninsure.cp.cx.fragment.CxThirdFragment;
import com.cninsure.cp.entity.OCREntity;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxWorkEntity;
import com.cninsure.cp.ocr.CxWorkPhotoHelp;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.google.android.material.tabs.TabLayout;

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
import java.util.TreeMap;

public class CxWorkActivity extends BaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] mainTitlesArray = {"定损信息", "查勘信息" ,"三者信息","人伤信息","物损信息"};
    private MyFragmentPagerAdapter adapter;
    private CxSubjectFragment fg0;
    private CxSurveyFragment fg1;
    private Map<Integer, Fragment> fragmentMap;
    private FragmentManager fm;
    public CxWorkPhotoHelp cameraHelp; //调用摄像头拍照的帮助类**/
    /**OCR解析信息及图片路径1,身份证，2银行卡，3驾驶证，4行驶证，5签名**/
    public OCREntity ocrEntity1,ocrEntity2,ocrEntity3,ocrEntity4,ocrEntity5;
    public CxWorkEntity cxWorkEntity;
    /**拍摄照片路径**/
    private File file;
    public String QorderUid;
    private CxWorkhelp workhelp;
    private View uploadView;
    public PublicOrderEntity orderInfoEn; //任务信息
    public CxDictEntity cxSurveyDict = new CxDictEntity(); //拍照类型字典数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cx_work_activity);
        EventBus.getDefault().register(this);
        dowloadDictType();
        initView();
    }

    private void initView() {
        uploadView = LayoutInflater.from(this).inflate(R.layout.imageupload_view, null);
        QorderUid = getIntent().getStringExtra("orderUid");
        orderInfoEn = (PublicOrderEntity) getIntent().getSerializableExtra("PublicOrderEntity");
        fm = getSupportFragmentManager();
        fragmentMap = new TreeMap<>();
        mViewPager = findViewById(R.id.cxworkA_viewpager);
        mTabLayout = findViewById(R.id.cxworkA_tablayout);
        cxWorkEntity = new CxWorkEntity();
        initFragment();
        initViewPager();
        initTab();
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
                case 2: fragmentMap.remove(2); break;
                case 3: fragmentMap.remove(3); break;
                case 4: fragmentMap.remove(4); break;
            }
        }
        notifyChange();
    }

    private void notifyChange(){
        adapter.notifyDataSetChanged();
        displayTabText();
    }


    /**刷新显示TabLayout菜单*/
    private void displayTabText() {
        int i = 0;
        for (Map.Entry<Integer, Fragment> mapEn: fragmentMap.entrySet()) {
            mTabLayout.getTabAt(i).setText(mainTitlesArray[mapEn.getKey()]);
            i++;
        }
    }

    private void dowloadDictType() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("type");
        params.add("cxOrderWorkImageType,accident_type,accident_small_type,accident_reason,accident_small_reason,survey_type," +
                "accident_liability,loss_type,loss_object_type,compensation_method,survey_conclusion,carno_type,car_usetype");
        HttpUtils.requestGet(URLs.CX_NEW_GET_IMG_TYPE_DICT, params, HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT);
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

        if (requestCode==1 || requestCode==2 || requestCode==3 || requestCode==4) {
            if (data!=null) {
                file=(File)data.getSerializableExtra("FilePath");
                cameraHelp.forString(requestCode,file);
            }
        }else if (requestCode== HttpRequestTool.LINEPATH) { //签字返回图片
            upSignPhoto(data,5);
        }else if (resultCode != RESULT_OK) { // 此处的 RESULT_OK 是系统自定义得一个常量
            Log.e("getphotos", "ActivityResult resultCode error");
//			ToastUtil.showToastLong(this, "操作失败！");
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
        switch (CheckHttpResult.checkList(values, this,HttpRequestTool.GET_WORK_MESSAGES)) {
            case HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT:
                LoadDialogUtil.dismissDialog();
                cxSurveyDict.list = JSON.parseArray(values.get(0).getValue(), CxDictEntity.DictData.class);
                workhelp = new CxWorkhelp(this, uploadView, cxSurveyDict.getDictByType("cxOrderWorkImageType"), null);
                break;
            default:
                break;
        }
    }

    /**上传OCR图片和签字后返回成功、图片名称及后缀 * 1 身份证识别 * 2 银行卡识别* 3 驾驶证识别* 4 行驶证识别 * **/
    @Subscribe(threadMode=ThreadMode.MAIN)
    public void eventmeth(NameValuePair valuePair){
        if ("UPLOAD_SUCCESS".equals(valuePair.getName())) {
            cameraHelp.sendMsgToBack(valuePair.getValue());
        }
    }
    /**上传签字后返回成功与图片名称及后缀 * 1 身份证识别 * 2 银行卡识别* 3 驾驶证识别* 4 行驶证识别 * **/
    @Subscribe(threadMode=ThreadMode.MAIN)
    public void eventmeth(List<NameValuePair> responseValue){
        int type=Integer.parseInt(responseValue.get(0).getValue());
        if (type==5) {
            if ("UPLOAD_SUCCESS".equals(responseValue.get(1).getName())) {
                signMeath(responseValue.get(1).getValue());
            }
        }else  if (type==4||type==3||type==2||type==1) {
            disPlayOcrInfo(type,responseValue.get(1).getValue());
            cameraHelp.sendMsgToBack(responseValue.get(1).getValue());
        }
    }

    /**处理签字图片*/
    private void signMeath(String url){
        if (url!=null) {
            ocrEntity5=new OCREntity();
            fg1.surveyInfo.signLicense = url;
            ocrEntity5.url=url;//签字图片名称，不包含完整路径
            fg1.disPlaySign(); //显示签名
        }
    }

    /**显示OCR信息  1 身份证识别 * 2 银行卡识别* 3 驾驶证识别* 4 行驶证识别*/
    private void disPlayOcrInfo(int type, String value) {
        switch (type){
            case 1:  //disPlayIdCard();
            case 2:  fg0.disBankCardInfo(ocrEntity2,value); //银行卡识别
            case 3:  fg0.disPlayDriverLicense(value); //驾驶证识别
            case 4:  fg0.disPlayMoveLicense(value); //行驶证识别

        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
