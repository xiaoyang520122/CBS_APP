package com.cninsure.cp.cx.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxSurveyWorkActivity;
import com.cninsure.cp.entity.cx.CxSurveyWorkEntity;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zcw.togglebutton.ToggleButton;

public class CxSubjectFragment extends BaseFragment {

    private View contentView;
    public CxSurveyWorkEntity.SubjectInfoEntity subjectInfo; //定损信息
    private CxSurveyWorkActivity activity;

    @ViewInject(R.id.cs_isLicenseKou)  private ToggleButton isLicenseKou;

    @ViewInject(R.id.jxzheng_cameraLin)  private LinearLayout jxzLineLayout;
    @ViewInject(R.id.cs_pathMoveLicense)  private TextView pathMoveLicenseTv;  //行驶证拍照
    @ViewInject(R.id.cs_pathDriverLicense)  private TextView pathDriverLicenseTv;  //驾驶证拍照

    //银行卡信息
    @ViewInject(R.id.cs_insuredPersonName)  private EditText insuredPersonNameEdt; //持卡人
    @ViewInject(R.id.cs_insuredBankDeposit)  private EditText insuredBankDepositEdt;  //开户行
    @ViewInject(R.id.cs_insuredBankNo)  private EditText insuredBankNoEdt; //开户行
    @ViewInject(R.id.cs_bankCarLicense)  private ImageView bankCarLicenseImg; //银行卡图片

    //行驶证
    @ViewInject(R.id.cs_bdCarNumber)  private EditText bdCarNumberEdt; //车牌号
    @ViewInject(R.id.cs_bdCarVin)  private EditText bdCarVinEdt; //车架号
    @ViewInject(R.id.cs_bdEngineNo)  private EditText bdEngineNoEdt; //发动机号
    @ViewInject(R.id.cs_bdCarRegisterDate)  private TextView bdCarRegisterDateTv; //初登日期
    @ViewInject(R.id.cs_bdCarEffectiveDate)  private TextView bdCarEffectiveDateTv; //行驶证有效期至
    @ViewInject(R.id.cs_bdDrivingType)  private EditText bdDrivingTypeEdt; //准驾车型
    @ViewInject(R.id.cs_bdCarNumberType)  private TextView bdCarNumberTypeTv; //号牌种类
    @ViewInject(R.id.cs_bdCarUseType)  private TextView bdCarUseTypeTv; //使用性质

    //驾驶证
    @ViewInject(R.id.cs_bdDriverName)  private EditText bdDriverNameEdt; //驾驶员姓名
    @ViewInject(R.id.cs_bdDriverPhone)  private EditText bdDriverPhoneEdt; //驾驶员电话
    @ViewInject(R.id.cs_bdDriverNo)  private EditText bdDriverNoEdt; //驾驶证
    @ViewInject(R.id.cs_bdDriverRegisterDate)  private TextView bdDriverRegisterDateTv; //初次领证日期
    @ViewInject(R.id.cs_bdDriverEffectiveStar)  private TextView bdDriverEffectiveStarTv; //有效起始日期
    @ViewInject(R.id.cs_bdDriverEffectiveEnd)  private TextView bdDriverEffectiveEndTv; //驾驶证有效期至
    @ViewInject(R.id.cs_bdCarVinIsAgreement_RG)  private RadioGroup bdCarVinIsAgreementRG; //车架号是否相符
    @ViewInject(R.id.cs_bdCardIsEffective_RG)  private RadioGroup bdCardIsEffectiveRG; //行驶证是否相符
    @ViewInject(R.id.cs_bdDriverIsEffective_RG)  private RadioGroup bdDriverIsEffectiveRG; //驾驶证是否相符
    @ViewInject(R.id.cs_bdDrivingIsAgreement_RG)  private RadioGroup bdDrivingIsAgreementRG; //准驾车型是否相符



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.cx_subject_fragment, null);
        activity = (CxSurveyWorkActivity) getActivity();
        ViewUtils.inject(this, contentView); //注入view和事件
        initView();
        return contentView;
    }

    private void initView() {
        if ( activity.cxWorkEntity.subjectInfo == null)
            activity.cxWorkEntity.subjectInfo = new CxSurveyWorkEntity.SubjectInfoEntity();
        setLicenseKouOnclick();  //设置双证被扣单击事件
        setMoveLicenseOnclick(); //行驶证点击事件
        setShortDatePick(); //选择时间单击事件绑定
        setTypePickeOclick() ;// 绑定类型选择

        displaySubjectData();
    }

    /** 绑定类型选择*/
    private void setTypePickeOclick() {
        TypePickeUtil.setTypePickerDialog(activity,bdCarNumberTypeTv,activity.cxSurveyDict,"carno_type");
        TypePickeUtil.setTypePickerDialog(activity,bdCarUseTypeTv,activity.cxSurveyDict,"car_usetype");
    }

    private void setShortDatePick() {
        DateChoiceUtil.setShortDatePickerDialog(activity,bdCarRegisterDateTv);
        DateChoiceUtil.setShortDatePickerDialog(activity,bdCarEffectiveDateTv);
        DateChoiceUtil.setShortDatePickerDialog(activity,bdDriverRegisterDateTv);
        DateChoiceUtil.setShortDatePickerDialog(activity,bdDriverEffectiveStarTv);
        DateChoiceUtil.setShortDatePickerDialog(activity,bdDriverEffectiveEndTv);
    }

    /**行驶证点击事件
     * /**调用摄像头识别身份证行驶证等信息
     * 1 身份证识别 * 2 银行卡识别* 3 驾驶证识别* 4 行驶证识别 * **/
    private void setMoveLicenseOnclick() {

        //银行卡
        bankCarLicenseImg.setOnClickListener(v -> activity.cameraHelp.startCamera(2));

        // 行驶证识别或展示
        pathMoveLicenseTv.setOnClickListener(v -> {
//       showMoveLDialog(); //相册选择或拍摄，未完待续。
            activity.cameraHelp.startCamera(4);
        });

        // 驾驶证识别或展示
        pathDriverLicenseTv.setOnClickListener(v -> {
//       showMoveLDialog(); //相册选择或拍摄，未完待续。
            activity.cameraHelp.startCamera(3);
        });
    }

    @Override
    public void onPause() {
        LoadDialogUtil.dismissDialog();
        SaveDataToEntity();
        super.onPause();
    }

    private void showMoveLDialog() {
       new AlertDialog.Builder(activity).setMessage("选择获取类型").setItems(new String[]{"拍照", "相册选择"}, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

           }
       }).create().show();

    }

    /**
     * 设置双证被扣单击事件
     */
    private void setLicenseKouOnclick() {
        isLicenseKou.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on){
                    activity.cxWorkEntity.subjectInfo.isLicenseKou = 1;
                    jxzLineLayout.setVisibility(View.GONE);
                } else {
                    activity.cxWorkEntity.subjectInfo.isLicenseKou = 0;
                    jxzLineLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**显示OCR识别的 银行卡信息*/
    public void disBankCardInfo(String imgName) {
        SetTextUtil.setEditText(insuredBankNoEdt,activity.cxWorkEntity.subjectInfo.insuredBankNo,SetTextUtil.BANK_CARD); //账号
        activity.cxWorkEntity.subjectInfo.bankCarLicense = imgName;  //照片名称
    }

    public void disPlayDriverLicense(String imgName) {
        //驾驶证
        bdDriverNameEdt.setText(activity.cxWorkEntity.subjectInfo.bdDriverName); //驾驶员姓名
        bdDriverNoEdt.setText(activity.cxWorkEntity.subjectInfo.bdDriverNo); //驾驶证
        bdDriverRegisterDateTv.setText(activity.cxWorkEntity.subjectInfo.bdCarRegisterDate); //初次领证日期
        bdDriverEffectiveStarTv.setText(activity.cxWorkEntity.subjectInfo.bdDriverEffectiveStar); //有效起始日期
        bdDriverEffectiveEndTv.setText(activity.cxWorkEntity.subjectInfo.bdDriverEffectiveEnd); //驾驶证有效期至
        activity.cxWorkEntity.subjectInfo.pathDriverLicense = imgName;  //照片名称
    }

    public void disPlayMoveLicense(String imgName) {
        //行驶证
        SetTextUtil.setEditText(bdCarNumberEdt,activity.cxWorkEntity.subjectInfo.bdCarNumber,SetTextUtil.CAR_NO);//车牌号
        SetTextUtil.setEditText(bdCarVinEdt ,activity.cxWorkEntity.subjectInfo.bdCarVin,SetTextUtil.VIN); //车架号
        SetTextUtil.setEditText(bdEngineNoEdt,activity.cxWorkEntity.subjectInfo.bdEngineNo,SetTextUtil.ENGIN); //发动机号
        bdCarRegisterDateTv.setText(activity.cxWorkEntity.subjectInfo.bdCarRegisterDate); //初登日期
        bdDrivingTypeEdt.setText(activity.cxWorkEntity.subjectInfo.bdDrivingType); //准驾车型
        SetTextUtil.setTvTextForArr(bdCarUseTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("car_usetype")),activity.cxWorkEntity.subjectInfo.bdCarUseType);  //使用性质
        activity.cxWorkEntity.subjectInfo.pathMoveLicense = imgName;  //照片名称
    }

    @Override
    public void SaveDataToEntity() {
        //银行卡信息
        activity.cxWorkEntity.subjectInfo.insuredPersonName = insuredPersonNameEdt.getText().toString();  //持卡人
        activity.cxWorkEntity.subjectInfo.insuredBankDeposit = insuredBankDepositEdt.getText().toString();  //开户行
        activity.cxWorkEntity.subjectInfo.insuredBankNo = insuredBankNoEdt.getText().toString(); //账号
        //行驶证
        activity.cxWorkEntity.subjectInfo.bdCarNumber = bdCarNumberEdt.getText().toString();//车牌号
        activity.cxWorkEntity.subjectInfo.bdCarVin = bdCarVinEdt .getText().toString(); //车架号
        activity.cxWorkEntity.subjectInfo.bdEngineNo = bdEngineNoEdt.getText().toString(); //发动机号
        activity.cxWorkEntity.subjectInfo.bdCarRegisterDate = bdCarRegisterDateTv.getText().toString();//初登日期
        activity.cxWorkEntity.subjectInfo.bdCarEffectiveDate = bdCarEffectiveDateTv.getText().toString(); //行驶证有效期至
        activity.cxWorkEntity.subjectInfo.bdDrivingType = bdDrivingTypeEdt.getText().toString(); //准驾车型
        activity.cxWorkEntity.subjectInfo.bdCarNumberType =  TypePickeUtil.getValue(bdCarNumberTypeTv.getText().toString(),activity.cxSurveyDict,"carno_type");   //号牌种类
        activity.cxWorkEntity.subjectInfo.bdCarUseType =  TypePickeUtil.getValue(bdCarUseTypeTv.getText().toString(),activity.cxSurveyDict,"car_usetype");   //使用性质
        //驾驶证
        activity.cxWorkEntity.subjectInfo.bdDriverName = bdDriverNameEdt.getText().toString();//驾驶员姓名
        activity.cxWorkEntity.subjectInfo.bdDriverPhone = bdDriverPhoneEdt.getText().toString();//驾驶员电话
        activity.cxWorkEntity.subjectInfo.bdDriverNo = bdDriverNoEdt.getText().toString();//驾驶证
        activity.cxWorkEntity.subjectInfo.bdDriverRegisterDate = bdDriverRegisterDateTv.getText().toString();//初次领证日期
        activity.cxWorkEntity.subjectInfo.bdDriverEffectiveStar = bdDriverEffectiveStarTv.getText().toString();//有效起始日期
        activity.cxWorkEntity.subjectInfo.bdDriverEffectiveEnd = bdDriverEffectiveEndTv.getText().toString();//驾驶证有效期至
        //车架号是否相符
        int CarVinIsRGCheckId = bdCarVinIsAgreementRG.getCheckedRadioButtonId();
        if (CarVinIsRGCheckId>0) //车架号是否相符
            switch (CarVinIsRGCheckId){
                case R.id.cs_bdCarVinIsAgreement_RB1:activity.cxWorkEntity.subjectInfo.bdCarVinIsAgreement = 0;break;//未验
                case R.id.cs_bdCarVinIsAgreement_RB2:activity.cxWorkEntity.subjectInfo.bdCarVinIsAgreement = 1;break;//相符
                case R.id.cs_bdCarVinIsAgreement_RB3:activity.cxWorkEntity.subjectInfo.bdCarVinIsAgreement = 2;break;//不符
            }
        //行驶证是否有效
        int CardIsRGCheckId = bdCardIsEffectiveRG.getCheckedRadioButtonId();
        if (CardIsRGCheckId>0) //车架号是否相符
            switch (CardIsRGCheckId){
                case R.id.cs_bdCardIsEffective_RB1:activity.cxWorkEntity.subjectInfo.bdCardIsEffective = 0;break;//未验
                case R.id.cs_bdCardIsEffective_RB2:activity.cxWorkEntity.subjectInfo.bdCardIsEffective = 1;break;//有效
                case R.id.cs_bdCardIsEffective_RB3:activity.cxWorkEntity.subjectInfo.bdCardIsEffective = 2;break;//无效
            }
        //驾驶证是否有效
        int DriverRGCheckId = bdDriverIsEffectiveRG.getCheckedRadioButtonId(); //bdDriverIsEffectiveRG
        if (DriverRGCheckId>0) //车架号是否相符
            switch (DriverRGCheckId){
                case R.id.cs_bdDriverIsEffective_RB1:activity.cxWorkEntity.subjectInfo.bdDriverIsEffective = 0;break;//未验
                case R.id.cs_bdDriverIsEffective_RB2:activity.cxWorkEntity.subjectInfo.bdDriverIsEffective = 1;break;//有效
                case R.id.cs_bdDriverIsEffective_RB3:activity.cxWorkEntity.subjectInfo.bdDriverIsEffective = 2;break;//无效
            }
        //驾驶证是否相符
        int bdDrivingRGCheckId = bdDrivingIsAgreementRG.getCheckedRadioButtonId();
        if (bdDrivingRGCheckId>0) //车架号是否相符
            switch (bdDrivingRGCheckId){
                case R.id.cs_bdDrivingIsAgreement_RB1:activity.cxWorkEntity.subjectInfo.bdDrivingIsAgreement = 0;break;//未验
                case R.id.cs_bdDrivingIsAgreement_RB2:activity.cxWorkEntity.subjectInfo.bdDrivingIsAgreement = 1;break;//相符
                case R.id.cs_bdDrivingIsAgreement_RB3:activity.cxWorkEntity.subjectInfo.bdDrivingIsAgreement = 2;break;//不符
            }
    }

    /**
     * 显示实体类中的所有数据
     */
    private void displaySubjectData() {
        CxSurveyWorkEntity.SubjectInfoEntity subjectInfoEnt = activity.cxWorkEntity.subjectInfo;
        if (activity.cxWorkEntity.subjectInfo.isLicenseKou==1){
            isLicenseKou.setToggleOn(true); //双证被扣
            jxzLineLayout.setVisibility(View.GONE);
        }else{jxzLineLayout.setVisibility(View.VISIBLE);}
        //银行卡信息
        SetTextUtil.setEditText(insuredPersonNameEdt,subjectInfoEnt.insuredPersonName);  //持卡人
        SetTextUtil.setEditText(insuredBankDepositEdt,subjectInfoEnt.insuredBankDeposit);  //开户行
        SetTextUtil.setEditText(insuredBankNoEdt,subjectInfoEnt.insuredBankNo,SetTextUtil.BANK_CARD); //账号
        //行驶证
        SetTextUtil.setEditText(bdCarNumberEdt,subjectInfoEnt.bdCarNumber,SetTextUtil.CAR_NO);//车牌号
        SetTextUtil.setEditText(bdCarVinEdt ,subjectInfoEnt.bdCarVin,SetTextUtil.VIN); //车架号
        SetTextUtil.setEditText(bdEngineNoEdt,subjectInfoEnt.bdEngineNo,SetTextUtil.ENGIN); //发动机号
        SetTextUtil.setTextViewText(bdCarRegisterDateTv,subjectInfoEnt.bdCarRegisterDate);//初登日期
        SetTextUtil.setTextViewText(bdCarEffectiveDateTv,subjectInfoEnt.bdCarEffectiveDate); //行驶证有效期至
        SetTextUtil.setEditText(bdDrivingTypeEdt,subjectInfoEnt.bdDrivingType); //准驾车型
        SetTextUtil.setTvTextForArr(bdCarNumberTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("carno_type")),subjectInfoEnt.bdCarNumberType);  //号牌种类
        SetTextUtil.setTvTextForArr(bdCarUseTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("car_usetype")),subjectInfoEnt.bdCarUseType);  //使用性质
        //驾驶证
        SetTextUtil.setEditText(bdDriverNameEdt,subjectInfoEnt.bdDriverName);//驾驶员姓名
        SetTextUtil.setEditText(bdDriverPhoneEdt,subjectInfoEnt.bdDriverPhone,SetTextUtil.MOBILE);//驾驶员电话
        SetTextUtil.setEditText(bdDriverNoEdt,subjectInfoEnt.bdDriverNo);//驾驶证
        SetTextUtil.setTextViewText(bdDriverRegisterDateTv,subjectInfoEnt.bdDriverRegisterDate);//初次领证日期
        SetTextUtil.setTextViewText(bdDriverEffectiveStarTv,subjectInfoEnt.bdDriverEffectiveStar);//有效起始日期
        SetTextUtil.setTextViewText(bdDriverEffectiveEndTv,subjectInfoEnt.bdDriverEffectiveEnd);//驾驶证有效期至
        //车架号是否相符
        if (subjectInfoEnt.bdCarVinIsAgreement==0) bdCarVinIsAgreementRG.check(R.id.cs_bdCarVinIsAgreement_RB1);//未验
        if (subjectInfoEnt.bdCarVinIsAgreement==1) bdCarVinIsAgreementRG.check(R.id.cs_bdCarVinIsAgreement_RB2);//相符
        if (subjectInfoEnt.bdCarVinIsAgreement==2) bdCarVinIsAgreementRG.check(R.id.cs_bdCarVinIsAgreement_RB3);//不符
        //行驶证是否有效
        if (subjectInfoEnt.bdCardIsEffective==0) bdCardIsEffectiveRG.check(R.id.cs_bdCardIsEffective_RB1);//未验
        if (subjectInfoEnt.bdCardIsEffective==1) bdCardIsEffectiveRG.check(R.id.cs_bdCardIsEffective_RB2);//有效
        if (subjectInfoEnt.bdCardIsEffective==2) bdCardIsEffectiveRG.check(R.id.cs_bdCardIsEffective_RB3);//无效
        //驾驶证是否有效
        if (subjectInfoEnt.bdDriverIsEffective==0) bdDriverIsEffectiveRG.check(R.id.cs_bdDriverIsEffective_RB1);//未验
        if (subjectInfoEnt.bdDriverIsEffective==1) bdDriverIsEffectiveRG.check(R.id.cs_bdDriverIsEffective_RB2);//有效
        if (subjectInfoEnt.bdDriverIsEffective==2) bdDriverIsEffectiveRG.check(R.id.cs_bdDriverIsEffective_RB3);//无效
        //准驾车型是否相符
        if (subjectInfoEnt.bdDrivingIsAgreement==0)bdDrivingIsAgreementRG.check(R.id.cs_bdDrivingIsAgreement_RB1);//未验
        if (subjectInfoEnt.bdDrivingIsAgreement==1)bdDrivingIsAgreementRG.check(R.id.cs_bdDrivingIsAgreement_RB2);//相符
        if (subjectInfoEnt.bdDrivingIsAgreement==2)bdDrivingIsAgreementRG.check(R.id.cs_bdDrivingIsAgreement_RB3);//不符
    }

}
