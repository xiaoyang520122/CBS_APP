package com.cninsure.cp.cx.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.ocr.LinePathActivity;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.FileChooseUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.cninsure.cp.utils.regex.RegexUtils;
import com.cninsure.cp.utils.widget.EditTextTool;
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


    private CheckBox lossType1,lossType2,lossType3,lossType4;  //损失类型
    @ViewInject(R.id.csu_signLicense_img)  private ImageView signLicenseImg;  //签字图片
    @ViewInject(R.id.csu_ckDate)  private TextView ckDateTv;  //查勘时间 此字段取案件第一张照片上传系统的时间，不管人工调整到什么时间，在案件提交时系统自动按照第一张照片上传时间写入查勘时间。
    @ViewInject(R.id.csu_ckAccidentType)  private TextView ckAccidentTypeTv; //事故类型
    @ViewInject(R.id.csu_ckAccidentSmallType)  private TextView ckAccidentSmallTypeTv; //事故详细类型
    @ViewInject(R.id.csu_ckAccidentReason)  private TextView ckAccidentReasonTv; //出险原因
    @ViewInject(R.id.csu_ckAccidentSmallReason)  private TextView ckAccidentSmallReasonTv; //出险详细原因
    @ViewInject(R.id.csu_surveyType)  private TextView surveyTypeTv;//查勘类型
    @ViewInject(R.id.csu_comfirmLiabilityType)  private TextView comfirmLiabilityTypeTv;//责任认定类型
    @ViewInject(R.id.csu_ckAccidentLiability)  private TextView ckAccidentLiabilityTv;//事故责任
    @ViewInject(R.id.csu_liabilityRatio)  private EditText liabilityRatioEdt;//责任比例
    @ViewInject(R.id.csu_lossObjectType)  private TextView lossObjectTypeTv;//损失详情
    @ViewInject(R.id.csu_baoanDriverName)  private EditText baoanDriverNameEdit;//报案驾驶员
    @ViewInject(R.id.csu_canDriveNormally_RG)  private RadioGroup canDriveNormallyRg;//能否正常行驶
    @ViewInject(R.id.csu_isWater)  private TextView isWaterTv; //是否水淹
    @ViewInject(R.id.csu_waterLevel)  private TextView waterLevelTv; //水淹类型
    @ViewInject(R.id.csu_fraudTag)  private TextView fraudTagTv; //欺诈标识
    @ViewInject(R.id.csu_fraudTypeLayout)  private LinearLayout fraudTypeLayout; //欺诈类型布局
    @ViewInject(R.id.csu_fraudType)  private TextView fraudTypeTv; //欺诈类型
    @ViewInject(R.id.csu_CompensationMethod)  private TextView compensationMethodTv;//赔付方式
//    @ViewInject(R.id.csu_ckIsInsuranceLiability_RG)  private RadioGroup ckIsInsuranceLiabilityRg;//是否属于保险责任
    @ViewInject(R.id.csu_isDaiwei_RG)  private RadioGroup isDaiweiRg;//是否代位
    @ViewInject(R.id.csu_lossAmount)  private EditText lossAmountEdt;//估损金额
    @ViewInject(R.id.csu_surveySummary)  private EditText surveySummaryEdt;//查勘概述
    @ViewInject(R.id.csu_isScene_RG)  private RadioGroup isSceneRg;//是否现场案件
    @ViewInject(R.id.csu_isHsLoad_RG)  private RadioGroup isHsLoadRg;//是否高速公路
    @ViewInject(R.id.csu_isInsuredCase)  private TextView isInsuredCaseTv;//是否人伤案件

    @ViewInject(R.id.csu_surveyAddress_equal)  private TextView surveyAddressEqualTv;//同派单地点
    @ViewInject(R.id.csu_surveyAddress_local)  private TextView surveyAddressLocalTv;//定位当前地点
    @ViewInject(R.id.csu_surveyAddress)  private EditText surveyAddressEdt;//查勘地点
    @ViewInject(R.id.csu_surveyConclusion)  private EditText surveyConclusionEdt;//查勘结论
    @ViewInject(R.id.csu_surveyConclusionChoice)  private TextView surveyConclusionChoice;//查勘结论选择器
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
        lossType4 = contentView.findViewById(R.id.cs_lossType4);
        setonclickLinsen();
        setCkDate();
        setTypePickeOclick() ;// 绑定类型选择
        getLocalInfoOncilck(); //获取查勘地点的监听事件
        setSignOnclick();  //签字
        setEnclosureOnclick(); //上传附件
        setlossAmountEdtChanged();
    }

    private void setlossAmountEdtChanged(){

        lossAmountEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(lossAmountEdt.getText().toString())){
                    try {
//                        Float lossAmountNumber = Float.parseFloat(lossAmountEdt.getText().toString());
                        Double lossAmountNumber = !RegexUtils.checkDecimals(lossAmountEdt.getText().toString()) ?null: Double.parseDouble(lossAmountEdt.getText().toString()); //估损金额
                        if (lossAmountNumber != null && lossAmountNumber > 10000f && ckIsMajorCaseRg != null) {
                            ckIsMajorCaseRg.check(R.id.csu_ckIsMajorCase_RBT);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

//        lossAmountEdt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) { }
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!TextUtils.isEmpty(s.toString())){
//                    Float lossAmountNumber = Float.parseFloat(s.toString());
//                    if (lossAmountNumber!=null && lossAmountNumber>10000f && ckIsMajorCaseRg!=null){
//                        ckIsMajorCaseRg.check(R.id.csu_ckIsMajorCase_RBT);
//                    }
//                }
//            }
//        });
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
        if (data==null) return;
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
        setAccidentType();
        setAccidentResonType();
        setIsWater();
        setfraudTag();
        setAccidentLiabilityType();
        TypePickeUtil.setTypePickerDialog(activity,surveyTypeTv,activity.cxSurveyDict,"survey_type");
        TypePickeUtil.setTypePickerDialog(activity,comfirmLiabilityTypeTv,activity.cxSurveyDict,"comfirmLiabilityType"); //责任认定类型
        TypePickeUtil.setTypePickerDialogByOther(activity, surveyConclusionEdt,activity.cxSurveyDict,"survey_conclusion",surveyConclusionChoice);
        TypePickeUtil.setTypePickerDialog(activity,compensationMethodTv,activity.cxSurveyDict,"compensation_method");
        setLossObjectTypeCheck(); //损失详情
    }

    /**
     * 责任比例：必填，当“事故责任”选择以后，直接带入对应责任比例，若“事故责任”选择“无法界定责任”，此处可填写任意责任，数字范围0<X<100。此处与“事故责任”的责任对应关系为：
     */
    private void setAccidentLiabilityType() {
        TypePickeUtil.setTypePickerDialog(activity,ckAccidentLiabilityTv,activity.cxSurveyDict,"accident_liability"); //事故责任
        ckAccidentLiabilityTv.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = activity.cxSurveyDict.getValueByLabel("accident_liability",s.toString());
                EditTextTool.noEidt(liabilityRatioEdt);
                liabilityRatioEdt.setTextColor(activity.getResources().getColor(R.color.hui_text_xxh));
                if ("1".equals(value)){
                    SetTextUtil.setEditText(liabilityRatioEdt,"100");
                }else if ("2".equals(value)){
                    SetTextUtil.setEditText(liabilityRatioEdt,"70");
                }else if ("3".equals(value)){
                    SetTextUtil.setEditText(liabilityRatioEdt,"50");
                }else if ("4".equals(value)){
                    SetTextUtil.setEditText(liabilityRatioEdt,"30");
                }else if ("5".equals(value)){
                    SetTextUtil.setEditText(liabilityRatioEdt,"0");
                }else{
                    SetTextUtil.setEditText(liabilityRatioEdt,"");
                    EditTextTool.okEidt(liabilityRatioEdt);
                    liabilityRatioEdt.setTextColor(activity.getResources().getColor(R.color.hui_text_xxxxh));
                }
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        liabilityRatioEdt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String liability = liabilityRatioEdt.getText().toString();
                if (!TextUtils.isEmpty(liability) && RegexUtils.checkDecimals(liability) &&(Float.parseFloat(liability)<0 || Float.parseFloat(liability)>100)){
                    liabilityRatioEdt.setError("比例需要为数字且大于等于0，小于等于100！");
                }
            }
            @Override public void afterTextChanged(Editable s) {  }
        });
    }

    /**出险原因
     * 出险原因 根据值类型 “accident_reason” 从字典库获取，筛选时 parentId 为0时是 “出险原因”一级集合，然后根据选中的 “险原因”的值作为parentId筛选出“险原因”二级的集合*/
    private void setAccidentResonType() { //accident_reason
        TypePickeUtil.setTypePickerDialogByParentId(activity,ckAccidentReasonTv,activity.cxSurveyDict,"accident_reason","0");
        ckAccidentReasonTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SetTextUtil.setTextViewText(ckAccidentSmallReasonTv,"");
                for (DictData dictData:activity.cxSurveyDict.getDictByTypeAndParentId("accident_reason","0")){
                    if (s.toString().equals(dictData.label)){
                        activity.cxWorkEntity.surveyInfo.ckAccidentReason = dictData.value;
                        if (TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByTypeAndParentId("accident_reason",dictData.value)).length>0){
                            ckAccidentSmallReasonTv.setVisibility(View.VISIBLE);
                            TypePickeUtil.setTypePickerDialogByParentId(activity,ckAccidentSmallReasonTv,activity.cxSurveyDict,"accident_reason",dictData.value);
                        }else{
                            ckAccidentSmallReasonTv.setVisibility(View.GONE);
                        }
                        break;
                    }
                }
            }
            @Override  public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * 1、是否水淹：新增项，必填，点选项“是”“否”，点选“是”以后，必须点选水淹等级。
     * 2、水淹等级：新增项，当“是否水淹”选择为“是”时必填
     */
    private void setIsWater() { //accident_reason
        TypePickeUtil.showTypePickerDialogByOnclick(activity,isInsuredCaseTv,new String[]{"是","否"}); //是否人伤
        TypePickeUtil.showTypePickerDialogByOnclick(activity,isWaterTv,new String[]{"是","否"}); //是否水淹
        isWaterTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SetTextUtil.setTextViewText(waterLevelTv, "");
                if ("是".equals(s.toString())){
                    activity.cxWorkEntity.surveyInfo.isWater = "1";
                    waterLevelTv.setVisibility(View.VISIBLE);
                    TypePickeUtil.setTypePickerDialog(activity, waterLevelTv, activity.cxSurveyDict, "waterLevel");
                }else {
                    activity.cxWorkEntity.surveyInfo.isWater = "0";
                    waterLevelTv.setVisibility(View.GONE);
                }
            }
            @Override  public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * 欺诈标识：新增字段，原“是否属于保险责任”修改为“ 欺诈标识”，必填，点选项如下，
     * 若点选“欺诈放弃索赔”“欺诈拒绝赔付”“疑似欺诈”则出现二级点选项目“欺诈类型。
     * 0	非欺诈案件
     * 1	欺诈放弃索赔
     * 2	欺诈拒绝赔付
     * 3	疑似欺诈
     */
    private void setfraudTag() { //accident_reason
        TypePickeUtil.setTypePickerDialog(activity,fraudTagTv,activity.cxSurveyDict,"fraudTag");//欺诈标识
        List<DictData> fraudList = activity.cxSurveyDict.getDictByType("fraudTag");
        fraudTagTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SetTextUtil.setTextViewText(fraudTypeTv, "");
                fraudTypeLayout.setVisibility(View.GONE);
                for (DictData dictTem:fraudList){
                    if (s.toString().equals(dictTem.label) && "1,2,3".contains(dictTem.value)){ //若点选“欺诈放弃索赔”“欺诈拒绝赔付”“疑似欺诈”则出现二级点选项目“欺诈类型。
                        fraudTypeLayout.setVisibility(View.VISIBLE);
                        setFraudTypeCheck();//欺诈类型
                        break;
                    }
                }
            }
            @Override  public void afterTextChanged(Editable s) { }
        });
    }

    /**
     * 事故类型 根据值类型 “accident_small_type” 从字典库获取，筛选时 parentId 为0时是 “事故类型”，然后根据选中的 “事故类型”的值作为parentId筛选出“事故详细类型”的集合
     */
    private void setAccidentType() {
        TypePickeUtil.setTypePickerDialogByParentId(activity,ckAccidentTypeTv,activity.cxSurveyDict,"accident_small_type","0");
        ckAccidentTypeTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SetTextUtil.setTextViewText(ckAccidentSmallTypeTv,"");
                for (DictData dictData:activity.cxSurveyDict.getDictByTypeAndParentId("accident_small_type","0")){
                    if (s.toString().equals(dictData.label)){
                        TypePickeUtil.setTypePickerDialogByParentId(activity,ckAccidentSmallTypeTv,activity.cxSurveyDict,"accident_small_type",dictData.value);
                        break;
                    }
                }
            }
            @Override  public void afterTextChanged(Editable s) { }
        });
    }

    private void setLossObjectTypeCheck(){
        lossObjectTypeTv.setOnClickListener(v -> {
            String tempArr[] = TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("loss_object_type"));
            List<DictData> dictList = activity.cxSurveyDict.getDictByType("loss_object_type");
            boolean isChoice[] = new boolean[tempArr.length];
            for (int i = 0;i<tempArr.length;i++){
                isChoice[i] = false;
            }
            new AlertDialog.Builder(activity).setTitle("请选择")
                    .setMultiChoiceItems(tempArr, isChoice, (dialog, which, isChecked) -> isChoice[which] = isChecked)
                    .setNegativeButton("确定", (dialog, which) -> {
                        clearLossTypeChecked();
                        String valueStr = "";
                        List<String> lossObjectTypeList = new ArrayList<>();
                        for (int i = 0;i<isChoice.length;i++){
                            if (isChoice[i]) {
                                lossObjectTypeList.add(dictList.get(i).value);
                                setLossObjectChecked(dictList.get(i).value);
                                valueStr += ","+dictList.get(i).value;
                            }
                        }
                        setisInsuredCaseValue(valueStr);
                        activity.cxWorkEntity.surveyInfo.lossObjectType = lossObjectTypeList.toArray( new String[lossObjectTypeList.size()]); //损失类型
                        displayLossTypeText();
                    }).create().show();
        });
    }

    /**
     * 欺诈类型：新增字段，“欺诈标识”点选为“欺诈放弃索赔”“欺诈拒绝赔付”“疑似欺诈”以后必填，点选项，可多选
     */
    private void setFraudTypeCheck(){
        fraudTypeTv.setOnClickListener(v -> {
            List<DictData> dictList = activity.cxSurveyDict.getDictByType("fraudType");
            String tempArr[] = TypePickeUtil.getDictLabelArr(dictList);
            boolean isChoice[] = new boolean[tempArr.length];
            for (int i = 0;i<tempArr.length;i++){
                isChoice[i] = false;
            }
            new AlertDialog.Builder(activity).setTitle("请选择")
                    .setMultiChoiceItems(tempArr, isChoice, (dialog, which, isChecked) -> isChoice[which] = isChecked)
                    .setNegativeButton("确定", (dialog, which) -> {
//                        clearLossTypeChecked();
                        String lableStr = "";
                        List<String> lossObjectTypeList = new ArrayList<>();
                        for (int i = 0;i<isChoice.length;i++){
                            if (isChoice[i]) {
                                lossObjectTypeList.add(dictList.get(i).value);
                                lableStr += (TextUtils.isEmpty(lableStr)?"":",")+dictList.get(i).label;
                            }
                        }
                        activity.cxWorkEntity.surveyInfo.fraudType = lossObjectTypeList.toArray( new String[lossObjectTypeList.size()]); //损失类型
                        SetTextUtil.setTextViewText(fraudTypeTv,lableStr);
                    }).create().show();
        });
    }

    /**
     * 如果“损失详情”字段中点选项目含“第三者人员伤亡”、“本车车上乘客伤亡”“本车车上司机伤亡”时，本选项自动锁定为“是”。
     * @param value
     */
    private void setisInsuredCaseValue(String value) {
        isInsuredCaseTv.setOnClickListener(null);
        if (value.contains("020")){ //“第三者人员伤亡”
            SetTextUtil.setTextViewText(isInsuredCaseTv,"是");
        }else if (value.contains("060")){ //“本车车上乘客伤亡”
            SetTextUtil.setTextViewText(isInsuredCaseTv,"是");
        }else if (value.contains("070")){ //“本车车上司机伤亡”
            SetTextUtil.setTextViewText(isInsuredCaseTv,"是");
        }else{
            TypePickeUtil.showTypePickerDialogByOnclick(activity,isInsuredCaseTv,new String[]{"是","否"}); //是否人伤
        }
    }

    private void clearLossTypeChecked(){
        //先清空之前的选择
        lossType1.setChecked(false);
        lossType2.setChecked(false);
        lossType3.setChecked(false);
        lossType4.setChecked(false);
    }
    private void setLossObjectChecked(String value){
        if (!TextUtils.isEmpty(value)){
            int code = Integer.parseInt(value);
        switch (code){
            case 10:lossType1.setChecked(true); break;
            case 20:lossType2.setChecked(true); break;
            case 30:lossType3.setChecked(true); break;
            case 40:lossType3.setChecked(true); break;
            case 50:lossType4.setChecked(true); break;
            case 60:lossType2.setChecked(true); break;
            case 70:lossType2.setChecked(true); break;
            case 80:lossType3.setChecked(true); break;
        }}
    }

    private void displayLossTypeText() {
        if (activity.cxWorkEntity.surveyInfo.lossObjectType==null || activity.cxWorkEntity.surveyInfo.lossObjectType.length==0)
            return;
        String labelList = "";
        String tempArr[] = TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("loss_object_type"));
            for (int j = 0; j < activity.cxWorkEntity.surveyInfo.lossObjectType.length; j++) {
                labelList = labelList + (TextUtils.isEmpty(labelList)?"":",") + activity.cxSurveyDict.getLabelByValue("loss_object_type",activity.cxWorkEntity.surveyInfo.lossObjectType[j]);
            }
        lossObjectTypeTv.setText(labelList);
    }

    private void displayfraudTypeText() {
        if (activity.cxWorkEntity.surveyInfo.fraudType==null || activity.cxWorkEntity.surveyInfo.fraudType.length==0)
            return;
        String labelList = "";
            for (int j = 0; j < activity.cxWorkEntity.surveyInfo.fraudType.length; j++) {
                labelList += (TextUtils.isEmpty(labelList)?"":",") + activity.cxSurveyDict.getLabelByValue("fraudType",activity.cxWorkEntity.surveyInfo.fraudType[j]);
            }
        fraudTypeTv.setText(labelList);
    }
    private void setonclickLinsen() {
        lossTypeSetOnclick(lossType1,2);//显示或隐藏三者
        lossTypeSetOnclick(lossType2,3); //显示或隐藏人伤
        lossTypeSetOnclick(lossType3,4);//显示或隐藏物损
        lossTypeSetOnclick(lossType4,5);//显示或隐藏标的车

    }

    private void lossTypeSetOnclick( CheckBox lossType,int position){
        if (position == 5) return; //标的车不用切换Fragment
        lossType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //显示对应类型的Fragment
                if (activity.fragmentMap.get(position)!=null && isChecked)
                    return;
                //这里发送Handler是为了让Activity中加载对应的Fragment和显示对应的TableLayout
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
        if (lossType4.isChecked())
            lossTypeList.add(3);
        CxSurveyWorkEntity.SurveyInfoEntity surveyInfo = activity.cxWorkEntity.surveyInfo;
        if (surveyInfo!=null) {
            surveyInfo.lossType = lossTypeList.toArray(new Integer[lossTypeList.size()]);  //损失类型

//        @ViewInject(R.id.csu_signLicense_img)  private ImageView signLicenseImg;  //签字图片
            surveyInfo.ckDate = ckDateTv.getText().toString(); //查勘时间
            surveyInfo.ckAccidentType = activity.cxSurveyDict.getValueByLabel("accident_small_type", ckAccidentTypeTv.getText().toString());//; //事故类型
            surveyInfo.ckAccidentSmallType = activity.cxSurveyDict.getValueByLabel("accident_small_type", ckAccidentSmallTypeTv.getText().toString());//; //事故详细类型
//        surveyInfo.ckAccidentReason = activity.cxSurveyDict.getValueByLabel("accident_reason",ckAccidentReasonTv.getText().toString()); //出险原因
            surveyInfo.ckAccidentSmallReason = activity.cxSurveyDict.getValueByLabel("accident_reason", ckAccidentSmallReasonTv.getText().toString()); //出险详细原因
            surveyInfo.waterLevel = activity.cxSurveyDict.getValueByLabel("waterLevel", waterLevelTv.getText().toString()); //水淹类型
            surveyInfo.surveyType = TypePickeUtil.getValue(surveyTypeTv.getText().toString(), activity.cxSurveyDict, "survey_type");//; //查勘类型
            surveyInfo.comfirmLiabilityType = activity.cxSurveyDict.getValueByLabel("comfirmLiabilityType", comfirmLiabilityTypeTv.getText().toString());//; //责任认定类型
            surveyInfo.fraudTag = activity.cxSurveyDict.getValueByLabel("fraudTag", fraudTagTv.getText().toString());//; //欺诈标识

            surveyInfo.ckAccidentLiability = TypePickeUtil.getValue(ckAccidentLiabilityTv.getText().toString(), activity.cxSurveyDict, "accident_liability"); //事故责任
            String temstr = liabilityRatioEdt.getText().toString();
            surveyInfo.liabilityRatio = !RegexUtils.checkDecimals(liabilityRatioEdt.getText().toString()) ? null : Double.parseDouble(liabilityRatioEdt.getText().toString()); //责任比例
//        @ViewInject(R.id.csu_lossObjectType)  private TextView lossObjectTypeTv;//损失详情

            surveyInfo.baoanDriverName = baoanDriverNameEdit.getText().toString(); //报案驾驶员
            String lossAmountStr = lossAmountEdt.getText().toString();
            surveyInfo.lossAmount = (RegexUtils.checkDecimals(lossAmountStr)) ?Float.valueOf(lossAmountStr) : null; //估损金额
            surveyInfo.surveySummary = surveySummaryEdt.getText().toString(); //查勘概述
            //能否正常行驶
            switch (canDriveNormallyRg.getCheckedRadioButtonId()) {
                case R.id.csu_canDriveNormally_RBT:
                    surveyInfo.canDriveNormally = 1;
                    break;
                case R.id.csu_canDriveNormally_RBF:
                    surveyInfo.canDriveNormally = 0;
            }
            surveyInfo.compensationMethod = TypePickeUtil.getValue(compensationMethodTv.getText().toString(), activity.cxSurveyDict, "compensation_method"); //赔付方式
            //是否属于保险责任
//        switch (ckIsInsuranceLiabilityRg.getCheckedRadioButtonId()){
//            case R.id.csu_ckIsInsuranceLiability_RBT: surveyInfo.ckIsInsuranceLiability = 1;break;
//            case R.id.csu_ckIsInsuranceLiability_RBF: surveyInfo.ckIsInsuranceLiability = 0;
//        }
            //是否代位
            switch (isDaiweiRg.getCheckedRadioButtonId()) {
                case R.id.csu_isDaiwei_RBT:
                    surveyInfo.isDaiwei = 1;
                    break;
                case R.id.csu_isDaiwei_RBF:
                    surveyInfo.isDaiwei = 0;
            }
            //是否现场案件
            switch (isSceneRg.getCheckedRadioButtonId()) {
                case R.id.csu_isScene_RBT:
                    surveyInfo.isScene = 1;
                    break;
                case R.id.csu_isScene_RBF:
                    surveyInfo.isScene = 0;
            }
            //是否高速公路
            switch (isHsLoadRg.getCheckedRadioButtonId()) {
                case R.id.csu_isHsLoad_RBT:
                    surveyInfo.isHsLoad = 1;
                    break;
                case R.id.csu_isHsLoad_RBF:
                    surveyInfo.isHsLoad = 0;
            }
            //是否人伤案件
            if (isInsuredCaseTv == null && TextUtils.isEmpty(isInsuredCaseTv.getText().toString())) {
                surveyInfo.isInsuredCase = null;
            } else if ("是".equals(isInsuredCaseTv.getText().toString())) {
                surveyInfo.isInsuredCase = 1; //是-是否人伤案件
            } else if ("否".equals(isInsuredCaseTv.getText().toString())) {
                surveyInfo.isInsuredCase = 0;//否-是否人伤案件
            }
            surveyInfo.surveyAddress = surveyAddressEdt.getText().toString(); //查勘地点
            surveyInfo.surveyConclusion = surveyConclusionEdt.getText().toString(); //查勘结论

            //是否重大案件
            switch (ckIsMajorCaseRg.getCheckedRadioButtonId()) {
                case R.id.csu_ckIsMajorCase_RBT:
                    surveyInfo.ckIsMajorCase = 1;
                    break;
                case R.id.csu_ckIsMajorCase_RBF:
                    surveyInfo.ckIsMajorCase = 0;
            }
        }
    }

    private void displaySurveyData() {
        CxSurveyWorkEntity.SurveyInfoEntity surveyInfo = activity.cxWorkEntity.surveyInfo;
        //损失类型
        if (null==surveyInfo.lossType) {
            return;
        }
        for (int i=0 ;i<surveyInfo.lossType.length;i++){
            switch (surveyInfo.lossType[i]){
            case 0:lossType1.setChecked(true); break;
            case 1:lossType2.setChecked(true); break;
            case 2:lossType3.setChecked(true); break;
            case 3:lossType4.setChecked(true); break;
            }
        }
        disPlaySign();  //签字图片
        SetTextUtil.setTextViewText(ckDateTv,surveyInfo.ckDate);//查勘时间
        SetTextUtil.setTextViewText(ckAccidentTypeTv,activity.cxSurveyDict.getLabelByValue("accident_small_type",surveyInfo.ckAccidentType));  //事故类型--需要放到事故详细类型前面赋值
        SetTextUtil.setTextViewText(ckAccidentSmallTypeTv,activity.cxSurveyDict.getLabelByValue("accident_small_type", surveyInfo.ckAccidentSmallType));//事故详细类型
        SetTextUtil.setTextViewText(ckAccidentReasonTv,activity.cxSurveyDict.getLabelByValue("accident_reason",surveyInfo.ckAccidentReason));//出险原因
        SetTextUtil.setTvTextForArr(surveyTypeTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("survey_type")), surveyInfo.surveyType);//查勘类型
        SetTextUtil.setTextViewText(isWaterTv,"1".equals(surveyInfo.isWater)?"是":"否");//是否水淹
        SetTextUtil.setTextViewText(waterLevelTv,activity.cxSurveyDict.getLabelByValue("waterLevel", surveyInfo.waterLevel));//水淹类型
        SetTextUtil.setTextViewText(ckAccidentSmallReasonTv,activity.cxSurveyDict.getLabelByValue("accident_reason", surveyInfo.ckAccidentSmallReason));//出险详细原因
        SetTextUtil.setTextViewText(comfirmLiabilityTypeTv,activity.cxSurveyDict.getLabelByValue("comfirmLiabilityType", surveyInfo.comfirmLiabilityType));//责任认定类型
        SetTextUtil.setTextViewText(fraudTagTv,activity.cxSurveyDict.getLabelByValue("fraudTag", surveyInfo.fraudTag));//欺诈标识
        displayfraudTypeText(); //欺诈类型
        SetTextUtil.setTvTextForArr(ckAccidentLiabilityTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("accident_liability")), surveyInfo.ckAccidentLiability);//事故责任
        SetTextUtil.setEditText(liabilityRatioEdt,surveyInfo.liabilityRatio!=null?(surveyInfo.liabilityRatio+""):"");//责任比例
        displayLossTypeText();//损失详情
        SetTextUtil.setEditText(baoanDriverNameEdit,surveyInfo.baoanDriverName);//报案驾驶员
        SetTextUtil.setEditText(lossAmountEdt,surveyInfo.lossAmount!=null?(surveyInfo.lossAmount+""):"");//估损金额
        SetTextUtil.setEditText(surveySummaryEdt,surveyInfo.surveySummary);//查勘概述
        //能否正常行驶
        if (surveyInfo.canDriveNormally==1) canDriveNormallyRg.check(R.id.csu_canDriveNormally_RBT);
        if (surveyInfo.canDriveNormally==0) canDriveNormallyRg.check(R.id.csu_canDriveNormally_RBF);
        SetTextUtil.setTvTextForArr(compensationMethodTv,TypePickeUtil.getDictLabelArr(activity.cxSurveyDict.getDictByType("compensation_method")),surveyInfo.compensationMethod);//赔付方式
        //是否属于保险责任
//        if (surveyInfo.ckIsInsuranceLiability==1) ckIsInsuranceLiabilityRg.check(R.id.csu_ckIsInsuranceLiability_RBT);
//        if (surveyInfo.ckIsInsuranceLiability==0) ckIsInsuranceLiabilityRg.check(R.id.csu_ckIsInsuranceLiability_RBF);
        //是否代位
        if (surveyInfo.isDaiwei==1) isDaiweiRg.check(R.id.csu_isDaiwei_RBT);
        if (surveyInfo.isDaiwei==0) isDaiweiRg.check(R.id.csu_isDaiwei_RBF);
        //是否现场案件
        if (surveyInfo.isScene==1) isSceneRg.check(R.id.csu_isScene_RBT);
        if (surveyInfo.isScene==0) isSceneRg.check(R.id.csu_isScene_RBF);
        //是否高速公路
        if (surveyInfo.isHsLoad==1) isHsLoadRg.check(R.id.csu_isHsLoad_RBT);
        if (surveyInfo.isHsLoad==0) isHsLoadRg.check(R.id.csu_isHsLoad_RBF);
        if (surveyInfo.isInsuredCase!=null && surveyInfo.isInsuredCase==1) SetTextUtil.setTextViewText(isInsuredCaseTv,"是"); //是否人伤案件
        if (surveyInfo.isInsuredCase!=null && surveyInfo.isInsuredCase==0) SetTextUtil.setTextViewText(isInsuredCaseTv,"否"); //是否人伤案件
        SetTextUtil.setEditText(surveyAddressEdt,surveyInfo.surveyAddress); //查勘地点
        SetTextUtil.setEditText(surveyConclusionEdt,surveyInfo.surveyConclusion);//查勘结论

        //是否重大案件
        if (surveyInfo.ckIsMajorCase==1) ckIsMajorCaseRg.check(R.id.csu_ckIsMajorCase_RBT);
        if (surveyInfo.ckIsMajorCase==0) ckIsMajorCaseRg.check(R.id.csu_ckIsMajorCase_RBF);
        displayFileToList(); //附件列表
    }


}
