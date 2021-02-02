package com.cninsure.cp.cx;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.adapter.MyFragmentPagerAdapter;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.cx.fragment.investigation.CxInjSurveyOnlyFragment;
import com.cninsure.cp.cx.fragment.investigation.CxSuDeliveryOnlyFragment;
import com.cninsure.cp.cx.publicmatch.PublicImagFragment;
import com.cninsure.cp.cx.util.CxWorkSubmitUtil;
import com.cninsure.cp.cx.util.ErrorDialogUtil;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.entity.cx.injurysurvey.CxInjuryExamineOnlyTaskEntity;
import com.cninsure.cp.entity.cx.injurysurvey.InjuryExamineOnlyTaskData;
import com.cninsure.cp.entity.cx.injurysurvey.InjuryExamineOnlyWorkEntity;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.ActivityFinishUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.google.android.material.tabs.TabLayout;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author :xy-wm
 * date:2020/12/17 17:56
 * usefuLness: CBS_APP
 */
public class CxInjuryExamineOnlyActivity extends BaseActivity implements View.OnClickListener {


    public CxInjuryExamineOnlyTaskEntity taskEntity; //物损任务信息
    //    @ViewInject(R.id.cxInMe_tablayout)
    private TabLayout mTabLayout;
    //    @ViewInject(R.id.cxInMe_viewpager)
    public ViewPager mViewPager;
    private String[] mainTitlesArray = { "探访内容" ,"影像资料","快递附件","留言"};
    public Map<Integer, BaseFragment> fragmentMap;
    private MyFragmentPagerAdapter adapter;
    private FragmentManager fm;
    public String QorderUid;
    public PublicOrderEntity dataEn;
    public CxDictEntity cxDict = new CxDictEntity(); //拍照类型字典数据

    private CxInjSurveyOnlyFragment fg0;
    private PublicImagFragment fg1;
    private CxSuDeliveryOnlyFragment fg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cxrs_injury_mediate_activity);
        QorderUid = getIntent().getStringExtra("orderUid");
        dataEn = (PublicOrderEntity) getIntent().getSerializableExtra("PublicOrderEntity");
//        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        dowloadDictType();
    }

    private void dowloadDictType() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("type");
        params.add("investigation_small_type,survey_result,deliveryCompany");

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

    private void initView() {
        fm = getSupportFragmentManager();
        mViewPager = findViewById(R.id.cxInMe_viewpager);
        mTabLayout = findViewById(R.id.cxInMe_tablayout);
//        if (cxWorkEntity==null)
//            cxWorkEntity = new CxSurveyWorkEntity();
        initFragment();
        initViewPager();
        initTab();
        setBackOnclick();
    }

    private void initFragment() {
        fg0 = new CxInjSurveyOnlyFragment();
        fg1 = new PublicImagFragment(QorderUid,dataEn,17,19);
        fg2 = new CxSuDeliveryOnlyFragment();

        fragmentMap = new TreeMap<>();
        fragmentMap.put(0, fg0);
        fragmentMap.put(1, fg1);
        fragmentMap.put(2, fg2);
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

    /**刷新显示TabLayout菜单*/
    private void displayTabText() {
        int i = 0;
        for (Map.Entry<Integer, BaseFragment> mapEn: fragmentMap.entrySet()) {
            mTabLayout.getTabAt(i).setText(mainTitlesArray[mapEn.getKey()]);
            i++;
        }
    }

    private void setBackOnclick() {
        findViewById(R.id.CX_Act_Back_Tv).setOnClickListener(this);
        findViewById(R.id.CX_Act_More_Tv).setOnClickListener(this);
        ((TextView)findViewById(R.id.CX_Act_Title_Tv)).setText("人伤调查（单项）");
        ((TextView)findViewById(R.id.CX_Act_More_Tv)).setText("保存/提交");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.CX_Act_Back_Tv: ActivityFinishUtil.showFinishAlert(this); break; //点击返回键
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
        PublicOrderEntity orderInfoEn = (PublicOrderEntity) getIntent().getSerializableExtra("PublicOrderEntity");
        InjuryExamineOnlyWorkEntity damageEnt = taskEntity.data.contentJson;
        damageEnt.areaNo = orderInfoEn.areaNo;
        damageEnt.area = orderInfoEn.area;
        damageEnt.province = orderInfoEn.province;
        damageEnt.caseProvince = orderInfoEn.caseProvince;
        damageEnt.city = orderInfoEn.city;

        for (int i=0;i<fragmentMap.size();i++){
            fragmentMap.get(i).SaveDataToEntity();
        }
        CxWorkSubmitUtil.submit(this,status,QorderUid, JSON.toJSONString(taskEntity.data.contentJson),taskEntity.data.id); //提交
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnetH5(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT:
                dowloadOderView();
                LoadDialogUtil.dismissDialog();
                cxDict.list = JSON.parseArray(values.get(0).getValue(), DictData.class);
//                workhelp = new CxWorkhelp(this, uploadView, cxSurveyDict.getDictByType("cxOrderWorkImageType"), null);
                break;
            case HttpRequestTool.UPLOAD_FILE_PHOTO: //上传附件成功
                fg2.getUploadFileInfo(values);
                break;
            case HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID: //获取订单信息
                LoadDialogUtil.dismissDialog();
                getTaskInfo(values.get(0).getValue());
                break;
            case HttpRequestTool.CX_NEW_WORK_SAVE: // 保存或提交审核返回数据
                LoadDialogUtil.dismissDialog();
                getTaskWorkSavaInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    /**解析获取的到的任务作业信息
     * @param value*/
    private void getTaskInfo(String value) {
        try {
            taskEntity = JSON.parseObject(value, CxInjuryExamineOnlyTaskEntity.class);
        } catch (Exception e) {  //解析失败，关闭界面
            ErrorDialogUtil.showErrorAndFinish(this,"获取任务信息失败，请联系管理员！");
            e.printStackTrace();
        }
        if (taskEntity == null )  taskEntity = new CxInjuryExamineOnlyTaskEntity();
        if (taskEntity.data == null  ) taskEntity.data = new InjuryExamineOnlyTaskData();
        if (taskEntity.data.contentJson == null) taskEntity.data.contentJson = new InjuryExamineOnlyWorkEntity();
        initView();
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
                        CxInjuryExamineOnlyActivity.this.finish();
                    else  dowloadOderView();
                }
            });
            dialog.show();
        }else DialogUtil.getErrDialog(this,"操作失败："+baseEntity.msg).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== HttpRequestTool.LINEPATH) { //签字返回图片
//            upSignPhoto(data,5);
        }else if (requestCode == PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE){  //上传附件或者单据选择的文件。
            fg2.inspectFileSize(data); //判断文件大小是否小于20M
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}