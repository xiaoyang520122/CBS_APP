package com.cninsure.cp.cx.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxWorkActivity;
import com.cninsure.cp.entity.cx.CxWorkEntity;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.List;

public class CxSurveyFragment extends Fragment {

    private View contentView;
    public CxWorkEntity.SurveyInfoEntity surveyInfo;
    private CxWorkActivity activity;


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
    @ViewInject(R.id.csu_isScene_RG)  private RadioGroup isSceneRg;//是否现场案件
    @ViewInject(R.id.csu_isHsLoad_RG)  private RadioGroup isHsLoadRg;//是否告诉公路

    @ViewInject(R.id.csu_surveyAddress_equal)  private TextView surveyAddressEqualTv;//同派单地点
    @ViewInject(R.id.csu_surveyAddress_local)  private TextView surveyAddressLocalTv;//定位当前地点
    @ViewInject(R.id.csu_surveyAddress)  private EditText surveyAddressEdt;//查勘地点
    @ViewInject(R.id.csu_surveyConclusion)  private TextView surveyConclusionTv;//查勘结论
    @ViewInject(R.id.csu_ckIsMajorCase_RG)  private RadioGroup ckIsMajorCaseRg;//是否重大案件
    @ViewInject(R.id.csu_signLicense)  private TextView signLicenseTv;//签字按钮

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.cx_survey_fragment,null);
        activity = (CxWorkActivity) getActivity();
        ViewUtils.inject(this, contentView); //注入view和事件
        initView();
        return contentView;
    }

    private void initView() {
        if (activity.cxWorkEntity.surveyInfo==null)
            activity.cxWorkEntity.surveyInfo = surveyInfo = new CxWorkEntity.SurveyInfoEntity();
        lossType1 = contentView.findViewById(R.id.cs_lossType1);
        lossType2 = contentView.findViewById(R.id.cs_lossType2);
        lossType3 = contentView.findViewById(R.id.cs_lossType3);
        setonclickLinsen();
        setCkDate();
        setTypePickeOclick() ;// 绑定类型选择
        getLocalInfoOncilck(); //获取查勘地点的监听事件
    }

    /**如果查勘时间为空，可以选择时间，如果不为空就回显**/
    private void setCkDate() {
        String cxDate = "";
        if (activity.cxWorkEntity != null && activity.cxWorkEntity.surveyInfo != null)
            cxDate = activity.cxWorkEntity.surveyInfo.ckDate;
        if (cxDate.isEmpty()){
            DateChoiceUtil.setLongDatePickerDialogOnClick(activity,ckDateTv);
        }else
            ckDateTv.setText(cxDate);
    }

    /**获取查勘地点*/
    private void getLocalInfoOncilck() {
        surveyAddressEqualTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surveyAddressEdt.setText(activity.orderInfoEn.caseLocation);
            }
        });
        surveyAddressLocalTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = AppApplication.LOCATION.getAddrStr();
                surveyAddressEdt.setText(address);
            }
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
        TypePickeUtil.setTypePickerDialog(activity,lossObjectTypeTv,activity.cxSurveyDict,"loss_object_type");
    }

    private void setLossObjectTypeCheck(){
        lossObjectTypeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempArr[] = TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("loss_object_type"));
                boolean isChoice[] = new boolean[tempArr.length];
                for (int i = 0;i<tempArr.length;i++){
                    isChoice[i] = false;
                }
                new AlertDialog.Builder(activity).setTitle("请选择")
                        .setMultiChoiceItems(tempArr, isChoice, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                isChoice[which] = isChecked;
                            }
                        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer lossTmp[];
                        List<Integer> lossList = new ArrayList<>();
                        for (int i = 0;i<isChoice.length;i++){
                            if (isChoice[i])
                                lossList.add(i);
                        }
                        lossTmp = (Integer[]) lossList.toArray();
                        surveyInfo.lossType = lossTmp; //损失类型
                        lossObjectTypeTv.setText(lossTmp.toString());
                    }
                })
                        .create().show();
            }
        });
    }

    private void setonclickLinsen() {
        lossType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.refreshContent(2,lossType1.isChecked()); //显示或隐藏三者
            }
        });
        lossType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.refreshContent(3,lossType2.isChecked()); //显示或隐藏人伤
            }
        });
        lossType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.refreshContent(4,lossType3.isChecked()); //显示或隐藏物损
            }
        });

    }

    /***显示签字图片*/
    public void disPlaySign() {
        if (surveyInfo!=null && !surveyInfo.signLicense.isEmpty()){
            Glide.with(getActivity()).load( surveyInfo.signLicense).into( signLicenseImg);
            signLicenseImg.setVisibility(View.VISIBLE);
        }else{
            signLicenseImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        LoadDialogUtil.dismissDialog();
        saveDataToEntity();
        super.onPause();
    }

    private void saveDataToEntity() {
        //损失类型
        List<Integer> lossTypeList = new ArrayList<>();
        if (lossType1.isChecked())
            lossTypeList.add(0);
        if (lossType2.isChecked())
            lossTypeList.add(1);
        if (lossType3.isChecked())
            lossTypeList.add(2);
        lossTypeList.toArray(surveyInfo.lossType);  //损失类型

//        @ViewInject(R.id.csu_signLicense_img)  private ImageView signLicenseImg;  //签字图片
        surveyInfo.ckDate = ckDateTv.getText().toString(); //查勘时间
        surveyInfo.ckAccidentType = TypePickeUtil.getValue(ckAccidentTypeTv.getText().toString(),activity.cxSurveyDict,"accident_type");//; //事故类型
        surveyInfo.ckAccidentSmallType = TypePickeUtil.getValue(ckAccidentSmallTypeTv.getText().toString(),activity.cxSurveyDict,"accident_small_type");//; //事故详细类型
        surveyInfo.ckAccidentReason = ckAccidentReasonTv.getText().toString(); //出险原因
        surveyInfo.surveyType = TypePickeUtil.getValue(surveyTypeTv.getText().toString(),activity.cxSurveyDict,"survey_type");//; //查勘类型

        surveyInfo.ckAccidentLiability = TypePickeUtil.getValue(ckAccidentLiabilityTv.getText().toString(),activity.cxSurveyDict,"accident_liability"); //事故责任
        surveyInfo.liabilityRatio = Float.parseFloat(liabilityRatioEdt.getText().toString()); //责任比例
//        @ViewInject(R.id.csu_lossObjectType)  private TextView lossObjectTypeTv;//损失情况

        surveyInfo.baoanDriverName = baoanDriverNameEdit.getText().toString(); //报案驾驶员
        //能否正常行驶
        switch (canDriveNormallyRg.getCheckedRadioButtonId()){
            case R.id.csu_canDriveNormally_RBT: surveyInfo.canDriveNormally = 1;
            case R.id.csu_canDriveNormally_RBF: surveyInfo.canDriveNormally = 0;
        }
        surveyInfo.compensationMethod = TypePickeUtil.getValue(compensationMethodTv.getText().toString(),activity.cxSurveyDict,"compensation_method"); //赔付方式
        //是否属于保险责任
        switch (ckIsInsuranceLiabilityRg.getCheckedRadioButtonId()){
            case R.id.csu_ckIsInsuranceLiability_RBT: surveyInfo.ckIsInsuranceLiability = 1;
            case R.id.csu_ckIsInsuranceLiability_RBF: surveyInfo.ckIsInsuranceLiability = 0;
        }
        //是否代位
        switch (isDaiweiRg.getCheckedRadioButtonId()){
            case R.id.csu_isDaiwei_RBT: surveyInfo.isDaiwei = 1;
            case R.id.csu_isDaiwei_RBF: surveyInfo.isDaiwei = 0;
        }
        //是否现场案件
        switch (isSceneRg.getCheckedRadioButtonId()){
            case R.id.csu_isScene_RBT: surveyInfo.isScene = 1;
            case R.id.csu_isScene_RBF: surveyInfo.isScene = 0;
        }
        //是否告诉公路
        switch (isHsLoadRg.getCheckedRadioButtonId()){
            case R.id.csu_isHsLoad_RBT: surveyInfo.isHsLoad = 1;
            case R.id.csu_isHsLoad_RBF: surveyInfo.isHsLoad = 0;
        }
        surveyInfo.surveyAddress = surveyAddressEdt.getText().toString(); //查勘地点
        surveyInfo.surveyConclusion = TypePickeUtil.getValue(surveyConclusionTv.getText().toString(),activity.cxSurveyDict,"survey_conclusion"); //查勘结论

        //是否重大案件
        switch (ckIsMajorCaseRg.getCheckedRadioButtonId()){
            case R.id.csu_ckIsMajorCase_RBT: surveyInfo.ckIsMajorCase = 1;
            case R.id.csu_ckIsMajorCase_RBF: surveyInfo.ckIsMajorCase = 0;
        }
//        @ViewInject(R.id.csu_signLicense)  private TextView signLicenseTv;//签字按钮
    }
}
