package com.cninsure.cp.cx.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxSurveyWorkActivity;
import com.cninsure.cp.cx.util.CxFileUploadUtil;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxSurveyWorkEntity;
import com.cninsure.cp.ocr.LinePathActivity;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.FileChooseUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CxSurveyFragment extends BaseFragment {

    private View contentView;
//    public CxSurveyWorkEntity.SurveyInfoEntity surveyInfo;
    private CxSurveyWorkActivity activity;
    private LayoutInflater inflater;


    private CheckBox lossType1,lossType2,lossType3;  //损失类型
    @ViewInject(R.id.csu_signLicense_img)  private ImageView signLicenseImg;  //签字图片
    @ViewInject(R.id.csu_ckDate)  private TextView ckDateTv;  //查勘时间
    @ViewInject(R.id.csu_ckAccidentType)  private TextView ckAccidentTypeTv; //事故类型
    @ViewInject(R.id.csu_ckAccidentSmallType)  private TextView ckAccidentSmallTypeTv; //事故详细类型
    @ViewInject(R.id.csu_ckAccidentReason)  private TextView ckAccidentReasonTv; //出险原因
    @ViewInject(R.id.csu_surveyType)  private TextView surveyTypeTv;//查勘类型
    @ViewInject(R.id.csu_ckAccidentLiability)  private TextView ckAccidentLiabilityTv;//事故责任
    @ViewInject(R.id.csu_liabilityRatio)  private EditText liabilityRatioEdt;//责任比例
    @ViewInject(R.id.csu_lossObjectType)  private TextView lossObjectTypeTv;//损失情况
    @ViewInject(R.id.csu_baoanDriverName)  private EditText baoanDriverNameEdit;//报案驾驶员
    @ViewInject(R.id.csu_canDriveNormally_RG)  private RadioGroup canDriveNormallyRg;//能否正常行驶
    @ViewInject(R.id.csu_CompensationMethod)  private TextView compensationMethodTv;//赔付方式
    @ViewInject(R.id.csu_ckIsInsuranceLiability_RG)  private RadioGroup ckIsInsuranceLiabilityRg;//是否属于保险责任
    @ViewInject(R.id.csu_isDaiwei_RG)  private RadioGroup isDaiweiRg;//是否代位
    @ViewInject(R.id.csu_lossAmount)  private EditText lossAmountEdt;//估损金额
    @ViewInject(R.id.csu_surveySummary)  private EditText surveySummaryEdt;//查勘概述
    @ViewInject(R.id.csu_isScene_RG)  private RadioGroup isSceneRg;//是否现场案件
    @ViewInject(R.id.csu_isHsLoad_RG)  private RadioGroup isHsLoadRg;//是否告诉公路

    @ViewInject(R.id.csu_surveyAddress_equal)  private TextView surveyAddressEqualTv;//同派单地点
    @ViewInject(R.id.csu_surveyAddress_local)  private TextView surveyAddressLocalTv;//定位当前地点
    @ViewInject(R.id.csu_surveyAddress)  private EditText surveyAddressEdt;//查勘地点
    @ViewInject(R.id.csu_surveyConclusion)  private TextView surveyConclusionTv;//查勘结论
    @ViewInject(R.id.csu_ckIsMajorCase_RG)  private RadioGroup ckIsMajorCaseRg;//是否重大案件
    @ViewInject(R.id.csu_signLicense)  private TextView signLicenseTv;//签字按钮
    @ViewInject(R.id.csu_enclosureList_add)  private TextView enclosureListTv;//上传附件
    @ViewInject(R.id.csu_enclosureList_line)  private LinearLayout enclosureListLine;//附件列表

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_survey_fragment,null);
        activity = (CxSurveyWorkActivity) getActivity();
        ViewUtils.inject(this, contentView); //注入view和事件
        initView();
        displaySurveyData();
        return contentView;
    }

    private void initView() {
        if (activity.cxWorkEntity.surveyInfo==null)
            activity.cxWorkEntity.surveyInfo = new CxSurveyWorkEntity.SurveyInfoEntity();
        else
            activity.cxWorkEntity.surveyInfo = activity.cxWorkEntity.surveyInfo;
        lossType1 = contentView.findViewById(R.id.cs_lossType1);
        lossType2 = contentView.findViewById(R.id.cs_lossType2);
        lossType3 = contentView.findViewById(R.id.cs_lossType3);
        setonclickLinsen();
        setCkDate();
        setTypePickeOclick() ;// 绑定类型选择
        getLocalInfoOncilck(); //获取查勘地点的监听事件
        setSignOnclick();  //签字
        setEnclosureOnclick(); //上传附件
    }

    /**上传附件按钮点击，选择文件*/
    private void setEnclosureOnclick() {
        enclosureListTv.setOnClickListener(v -> PickPhotoUtil.albumPhoto(activity, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE));
    }

    /**
     * 判断文件大小是否小于20M,小于就上传。
     * @param data
     */
    public void inspectFileSize(Intent data) {
        String FilePath = FileChooseUtil.getInstance(activity).getChooseFileResultPath(data.getData());
        File fileTemp = new File(FilePath);
        if (fileTemp!=null && fileTemp.length()>0 && (fileTemp.length() < 20971520)) { //必须小于20M（20971520 byte）
            List<NameValuePair> fileUrls = new ArrayList<NameValuePair>();
            fileUrls.add(new BasicNameValuePair("0", FilePath));
            CxFileUploadUtil.uploadCxFile(activity, fileUrls, URLs.UPLOAD_FILE_PHOTO,null); //上传
        }
    }

    /**显示上传成功的附件*/
    public void getUploadFileInfo(List<NameValuePair> values) {
        String UpedFileName = values.get(0).getValue();
        String oldFileName = values.get(1).getValue();
        activity.cxWorkEntity.surveyInfo.enclosureList.add(UpedFileName);
        displayFileToList();
    }

    private void displayFileToList() {
        enclosureListLine.removeAllViews();  //添加前清空，避免重复加载
        for (int i = 0; i < activity.cxWorkEntity.surveyInfo.enclosureList.size(); i++) {
            View view = inflater.inflate(R.layout.expandable_child_item, null);
            SetTextUtil.setTextViewText(view.findViewById(R.id.UPPHOTO_LI_name),activity.cxWorkEntity.surveyInfo.enclosureList.get(i)); //文件名称
            enclosureListLine.addView(view);  //添加到LineLayout
            int finalI = i;
            view.findViewById(R.id.UPPHOTO_LI_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.cxWorkEntity.surveyInfo.enclosureList.remove(finalI); //移除名称集合
                    enclosureListLine.removeView(view);  //移除LineLayout，不在显示
                }
            });
        }
    }

    /**签字监听事件*/
    private void setSignOnclick() {
        signLicenseTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSign();
            }
        });
    }
    /**启动签字**/
    private void startSign(){
        Intent intent=new Intent(activity, LinePathActivity.class);
        intent.putExtra("orderUid", activity.getIntent().getStringExtra("orderUid"));
        startActivityForResult(intent, HttpRequestTool.LINEPATH);
    }

    /**如果查勘时间为空，可以选择时间，如果不为空就回显**/
    private void setCkDate() {
        String cxDate = "";
        if (activity.cxWorkEntity != null && activity.cxWorkEntity.surveyInfo != null)
            cxDate = activity.cxWorkEntity.surveyInfo.ckDate;
        if (TextUtils.isEmpty(cxDate)){
            DateChoiceUtil.setLongDatePickerDialogOnClick(activity,ckDateTv);
        }else
            ckDateTv.setText(cxDate);
    }

    /**获取查勘地点*/
    private void getLocalInfoOncilck() {
        surveyAddressEqualTv.setOnClickListener(v -> surveyAddressEdt.setText(activity.orderInfoEn.caseLocation));
        surveyAddressLocalTv.setOnClickListener(v -> {
            String address = AppApplication.LOCATION.getAddrStr();
            surveyAddressEdt.setText(address);
        });
    }

    /** 绑定类型选择*/
    private void setTypePickeOclick() {

        TypePickeUtil.setTypePickerDialog(activity,ckAccidentTypeTv,activity.cxSurveyDict,"accident_type");
        TypePickeUtil.setTypePickerDialog(activity,ckAccidentSmallTypeTv,activity.cxSurveyDict,"accident_small_type");
        TypePickeUtil.setTypePickerDialog(activity,ckAccidentReasonTv,activity.cxSurveyDict,"accident_reason");
        TypePickeUtil.setTypePickerDialog(activity,surveyTypeTv,activity.cxSurveyDict,"survey_type");
        TypePickeUtil.setTypePickerDialog(activity,ckAccidentLiabilityTv,activity.cxSurveyDict,"accident_liability");
        TypePickeUtil.setTypePickerDialog(activity,surveyConclusionTv,activity.cxSurveyDict,"survey_conclusion");
        TypePickeUtil.setTypePickerDialog(activity,compensationMethodTv,activity.cxSurveyDict,"compensation_method");
        setLossObjectTypeCheck(); //损失情况
    }

    private void setLossObjectTypeCheck(){
        lossObjectTypeTv.setOnClickListener(v -> {
            String tempArr[] = TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("loss_object_type"));
            boolean isChoice[] = new boolean[tempArr.length];
            for (int i = 0;i<tempArr.length;i++){
                isChoice[i] = false;
            }
            new AlertDialog.Builder(activity).setTitle("请选择")
                    .setMultiChoiceItems(tempArr, isChoice, (dialog, which, isChecked) -> isChoice[which] = isChecked)
                    .setNegativeButton("确定", (dialog, which) -> {
                        List<Integer> lossList = new ArrayList<>();
                        for (int i = 0;i<isChoice.length;i++){
                            if (isChoice[i])
                                lossList.add(i);
                        }
                        Integer lossTmp[] = lossList.toArray( new Integer[lossList.size()]);
                        activity.cxWorkEntity.surveyInfo.lossObjectType = lossTmp; //损失类型
                        displayLossTypeText();
                    }).create().show();
        });
    }

    private void displayLossTypeText() {
        if (activity.cxWorkEntity.surveyInfo.lossObjectType==null || activity.cxWorkEntity.surveyInfo.lossObjectType.length==0)
            return;
        String labelList = "";
        String tempArr[] = TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("loss_object_type"));
        for (int i = 0; i < tempArr.length; i++) {
            for (int j = 0; j < activity.cxWorkEntity.surveyInfo.lossObjectType.length; j++) {
                if (activity.cxWorkEntity.surveyInfo.lossObjectType[j] == i) {
                    labelList = labelList + "," + tempArr[i];
                }
            }
        }
        lossObjectTypeTv.setText(labelList);
    }
    private void setonclickLinsen() {
//        lossType1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activity.refreshContent(2,lossType1.isChecked()); //显示或隐藏三者
//            }
//        });
//        lossType2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activity.refreshContent(3,lossType2.isChecked()); //显示或隐藏人伤
//            }
//        });
//        lossType3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activity.refreshContent(4,lossType3.isChecked()); //显示或隐藏物损
//            }
//        });
        lossTypeSetOnclick(lossType1,2);//显示或隐藏三者
        lossTypeSetOnclick(lossType2,3); //显示或隐藏人伤
        lossTypeSetOnclick(lossType3,4);//显示或隐藏物损

    }

    private void lossTypeSetOnclick( CheckBox lossType,int position){
        lossType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //显示或隐藏物损
                if (activity.fragmentMap.get(position)!=null && isChecked)
                    return;
                Message msg = new Message();
                msg.what = position;
                msg.obj = isChecked;
                activity.refreshHandler.sendMessage(msg);
            }
        });
    }

    /***显示签字图片*/
    public void disPlaySign() {
        if (activity.cxWorkEntity.surveyInfo!=null && !TextUtils.isEmpty(activity.cxWorkEntity.surveyInfo.signLicense)){
            //车险 上传OCR图片路径,上传成功后返回图片名称及后缀 例如："picture-20180310151556-69210-E2638.jpg"，访问是需要加上登录时获取的头部分
            String imgPath=AppApplication.getUSER().data.qiniuUrl+activity.cxWorkEntity.surveyInfo.signLicense;
            Glide.with(getActivity()).load( imgPath).into( signLicenseImg);
            signLicenseImg.setVisibility(View.VISIBLE);
//            showQRcode();  //展示报告二维码-待开发
        }else{
            signLicenseImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        LoadDialogUtil.dismissDialog();
        SaveDataToEntity();
        super.onPause();
    }


    @Override
    public void SaveDataToEntity() {
        //损失类型
        List<Integer> lossTypeList = new ArrayList<>();
        if (lossType1.isChecked())
            lossTypeList.add(0);
        if (lossType2.isChecked())
            lossTypeList.add(1);
        if (lossType3.isChecked())
            lossTypeList.add(2);
        CxSurveyWorkEntity.SurveyInfoEntity surveyInfo = activity.cxWorkEntity.surveyInfo;
        surveyInfo.lossType = lossTypeList.toArray(new Integer[lossTypeList.size()]);  //损失类型

//        @ViewInject(R.id.csu_signLicense_img)  private ImageView signLicenseImg;  //签字图片
        surveyInfo.ckDate = ckDateTv.getText().toString(); //查勘时间
        surveyInfo.ckAccidentType = TypePickeUtil.getValue(ckAccidentTypeTv.getText().toString(),activity.cxSurveyDict,"accident_type");//; //事故类型
        surveyInfo.ckAccidentSmallType = TypePickeUtil.getValue(ckAccidentSmallTypeTv.getText().toString(),activity.cxSurveyDict,"accident_small_type");//; //事故详细类型
        surveyInfo.ckAccidentReason = ckAccidentReasonTv.getText().toString(); //出险原因
        surveyInfo.surveyType = TypePickeUtil.getValue(surveyTypeTv.getText().toString(),activity.cxSurveyDict,"survey_type");//; //查勘类型

        surveyInfo.ckAccidentLiability = TypePickeUtil.getValue(ckAccidentLiabilityTv.getText().toString(),activity.cxSurveyDict,"accident_liability"); //事故责任
        surveyInfo.liabilityRatio = liabilityRatioEdt.getText().toString(); //责任比例
//        @ViewInject(R.id.csu_lossObjectType)  private TextView lossObjectTypeTv;//损失情况

        surveyInfo.baoanDriverName = baoanDriverNameEdit.getText().toString(); //报案驾驶员
        surveyInfo.lossAmount = lossAmountEdt.getText().toString(); //估损金额
        surveyInfo.surveySummary = surveySummaryEdt.getText().toString(); //查勘概述
        //能否正常行驶
        switch (canDriveNormallyRg.getCheckedRadioButtonId()){
            case R.id.csu_canDriveNormally_RBT: surveyInfo.canDriveNormally = 1;break;
            case R.id.csu_canDriveNormally_RBF: surveyInfo.canDriveNormally = 0;
        }
        surveyInfo.compensationMethod = TypePickeUtil.getValue(compensationMethodTv.getText().toString(),activity.cxSurveyDict,"compensation_method"); //赔付方式
        //是否属于保险责任
        switch (ckIsInsuranceLiabilityRg.getCheckedRadioButtonId()){
            case R.id.csu_ckIsInsuranceLiability_RBT: surveyInfo.ckIsInsuranceLiability = 1;break;
            case R.id.csu_ckIsInsuranceLiability_RBF: surveyInfo.ckIsInsuranceLiability = 0;
        }
        //是否代位
        switch (isDaiweiRg.getCheckedRadioButtonId()){
            case R.id.csu_isDaiwei_RBT: surveyInfo.isDaiwei = 1;break;
            case R.id.csu_isDaiwei_RBF: surveyInfo.isDaiwei = 0;
        }
        //是否现场案件
        switch (isSceneRg.getCheckedRadioButtonId()){
            case R.id.csu_isScene_RBT: surveyInfo.isScene = 1;break;
            case R.id.csu_isScene_RBF: surveyInfo.isScene = 0;
        }
        //是否高速公路
        switch (isHsLoadRg.getCheckedRadioButtonId()){
            case R.id.csu_isHsLoad_RBT: surveyInfo.isHsLoad = 1;break;
            case R.id.csu_isHsLoad_RBF: surveyInfo.isHsLoad = 0;
        }
        surveyInfo.surveyAddress = surveyAddressEdt.getText().toString(); //查勘地点
        surveyInfo.surveyConclusion = TypePickeUtil.getValue(surveyConclusionTv.getText().toString(),activity.cxSurveyDict,"survey_conclusion"); //查勘结论

        //是否重大案件
        switch (ckIsMajorCaseRg.getCheckedRadioButtonId()){
            case R.id.csu_ckIsMajorCase_RBT: surveyInfo.ckIsMajorCase = 1;break;
            case R.id.csu_ckIsMajorCase_RBF: surveyInfo.ckIsMajorCase = 0;
        }
    }

    private void displaySurveyData() {
        CxSurveyWorkEntity.SurveyInfoEntity surveyInfo = activity.cxWorkEntity.surveyInfo;
        //损失类型
        if (null!=surveyInfo.lossType)
        for (int i=0 ;i<surveyInfo.lossType.length;i++){
            switch (surveyInfo.lossType[i]){
            case 0:lossType1.setChecked(true); break;
            case 1:lossType2.setChecked(true); break;
            case 2:lossType3.setChecked(true); break;
            }
        }
        disPlaySign();  //签字图片
        SetTextUtil.setTextViewText(ckDateTv,surveyInfo.ckDate);//查勘时间
        SetTextUtil.setTvTextForArr(ckAccidentTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("accident_type")),surveyInfo.ckAccidentType);  //事故类型
        SetTextUtil.setTvTextForArr(ckAccidentSmallTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("accident_small_type")), surveyInfo.ckAccidentSmallType);//事故详细类型
        SetTextUtil.setTextViewText(ckAccidentReasonTv,surveyInfo.ckAccidentReason);//出险原因
        SetTextUtil.setTvTextForArr(surveyTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("survey_type")), surveyInfo.surveyType);//查勘类型
        SetTextUtil.setTvTextForArr(ckAccidentLiabilityTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("accident_liability")), surveyInfo.ckAccidentLiability);//事故责任
        SetTextUtil.setEditText(liabilityRatioEdt,surveyInfo.liabilityRatio);//责任比例
        displayLossTypeText();//损失情况
        SetTextUtil.setEditText(baoanDriverNameEdit,surveyInfo.baoanDriverName);//报案驾驶员
        SetTextUtil.setEditText(lossAmountEdt,surveyInfo.lossAmount);//估损金额
        SetTextUtil.setEditText(surveySummaryEdt,surveyInfo.surveySummary);//查勘概述
        //能否正常行驶
        if (surveyInfo.canDriveNormally==1) canDriveNormallyRg.check(R.id.csu_canDriveNormally_RBT);
        if (surveyInfo.canDriveNormally==0) canDriveNormallyRg.check(R.id.csu_canDriveNormally_RBF);
        SetTextUtil.setTvTextForArr(compensationMethodTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("compensation_method")),surveyInfo.compensationMethod);//赔付方式
        //是否属于保险责任
        if (surveyInfo.ckIsInsuranceLiability==1) ckIsInsuranceLiabilityRg.check(R.id.csu_ckIsInsuranceLiability_RBT);
        if (surveyInfo.ckIsInsuranceLiability==0) ckIsInsuranceLiabilityRg.check(R.id.csu_ckIsInsuranceLiability_RBF);
        //是否代位
        if (surveyInfo.isDaiwei==1) isDaiweiRg.check(R.id.csu_isDaiwei_RBT);
        if (surveyInfo.isDaiwei==0) isDaiweiRg.check(R.id.csu_isDaiwei_RBF);
        //是否现场案件
        if (surveyInfo.isScene==1) isSceneRg.check(R.id.csu_isScene_RBT);
        if (surveyInfo.isScene==0) isSceneRg.check(R.id.csu_isScene_RBF);
        //是否告诉公路
        if (surveyInfo.isHsLoad==1) isHsLoadRg.check(R.id.csu_isHsLoad_RBT);
        if (surveyInfo.isHsLoad==0) isHsLoadRg.check(R.id.csu_isHsLoad_RBF);
        SetTextUtil.setEditText(surveyAddressEdt,surveyInfo.surveyAddress); //查勘地点
        SetTextUtil.setTvTextForArr(surveyConclusionTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("survey_conclusion")),surveyInfo.surveyConclusion);//查勘结论

        //是否重大案件
        if (surveyInfo.ckIsMajorCase==1) ckIsMajorCaseRg.check(R.id.csu_ckIsMajorCase_RBT);
        if (surveyInfo.ckIsMajorCase==0) ckIsMajorCaseRg.check(R.id.csu_ckIsMajorCase_RBF);
        displayFileToList(); //附件列表
    }


}
