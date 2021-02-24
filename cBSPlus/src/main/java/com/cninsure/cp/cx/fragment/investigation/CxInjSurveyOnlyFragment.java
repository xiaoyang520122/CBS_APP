package com.cninsure.cp.cx.fragment.investigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxInjuryExamineOnlyActivity;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.entity.cx.injuryexamine.DoctorEntity;
import com.cninsure.cp.entity.cx.injuryexamine.InjuryExamineOnlyWorkEntity;
import com.cninsure.cp.utils.SetTextUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author :xy-wm
 * date:2020/12/18
 * usefuLness: CBS_APP
 */
public class CxInjSurveyOnlyFragment extends BaseFragment implements View.OnClickListener {

    private View contentView;


    private List<View> doctorViewList;
    private LayoutInflater inflater;
    private CxInjuryExamineOnlyActivity activity;

    @ViewInject(R.id.cxInInOn_injuredName) private EditText injuredNameEdit; //伤者姓名
    @ViewInject(R.id.cxInInOn_injuredTel) private EditText injuredTelEdit;    //伤者电话
    @ViewInject(R.id.cxInInOn_local) private TextView localAddress;    //定位当前地址
    @ViewInject(R.id.cxInInOn_surveyAddress) private EditText surveyAddress;    //作业地址
    @ViewInject(R.id.cxInInOn_surveyTime) private TextView surveyTime;    //调查时间
    @ViewInject(R.id.cxInInOn_hospitalName) private EditText hospitalName;    //医院名称
    @ViewInject(R.id.cxInInOn_doctorList) private LinearLayout surveySmallTypeGroup;//询问代付列表父级布局
    @ViewInject(R.id.cxInInOn_surveyResult_RG) private RadioGroup surveyResultRG; //调查结果
    @ViewInject(R.id.cxInInOn_surveyReport) private EditText surveyReportEdit; //调查报告

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cxrs_injury_investigation_only_fragment, null);
        activity = (CxInjuryExamineOnlyActivity) getActivity();
        ViewUtils.inject(this,contentView);
        initView();
        return contentView;
    }

    //初始化控件
    private void initView() {
        displaBasicInfo();
        initDoctorList();
    }

    private void displaBasicInfo() {
        SetTextUtil.setTextViewText(injuredNameEdit,activity.taskEntity.data.contentJson.injuredName); //伤者姓名
        SetTextUtil.setTextViewText(injuredTelEdit,activity.taskEntity.data.contentJson.injuredTel); //伤者电话
        SetTextUtil.setTextViewText(surveyReportEdit,activity.taskEntity.data.contentJson.surveyReport); //调查报告

        SetTextUtil.setTextViewText(surveyAddress,activity.taskEntity.data.contentJson.surveyAddress); //作业地址
        SetTextUtil.setTextViewText(surveyTime,activity.taskEntity.data.contentJson.surveyTime); //调查时间
        SetTextUtil.setTextViewText(hospitalName,activity.taskEntity.data.contentJson.hospitalName); //医院名称
        //调查结果  1有、0无
        if ("1".equals(activity.taskEntity.data.contentJson.surveyResult)) surveyResultRG.check(R.id.cxInInOn_surveyResult_RBB); //阴性
        if ("0".equals(activity.taskEntity.data.contentJson.surveyResult)) surveyResultRG.check(R.id.cxInInOn_surveyResult_RBY); //阳性

        localAddress.setOnClickListener(v -> {
            String address = AppApplication.LOCATION.getAddrStr();
            if (AppApplication.LOCATION!=null) SetTextUtil.setEditText(surveyAddress,AppApplication.LOCATION.getAddrStr());
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    /**显示询问医生列表*/
    private void initDoctorList() {
        /**清空数据*/
        surveySmallTypeGroup.removeAllViews();
        if (doctorViewList==null) doctorViewList = new ArrayList<>();
        doctorViewList.clear();
        /**加载数据*/
        if (activity.taskEntity != null && activity.taskEntity.data != null && activity.taskEntity.data.contentJson != null
         && activity.taskEntity.data.contentJson.doctorList != null) {
            InjuryExamineOnlyWorkEntity tempImw = activity.taskEntity.data.contentJson;
            for (int i=0;i<tempImw.doctorList.size();i++) {
                DoctorEntity doctorTemp = tempImw.doctorList.get(i);
                View view  = getInSurveyAskItem(doctorTemp);
                doctorViewList.add(view);
                surveySmallTypeGroup.addView(view);
                setAskOnclick(surveySmallTypeGroup,doctorViewList,view);
            }
        }else{ //没有数据默认加载一个空数据
            DoctorEntity doctorTemp = new DoctorEntity();
            View view  = getInSurveyAskItem(doctorTemp);
            doctorViewList.add(view);
            surveySmallTypeGroup.addView(view);
            setAskOnclick(surveySmallTypeGroup,doctorViewList,view);
        }
    }


    /**
     * 添加 或 删除 询问对象
     */
    private void setAskOnclick(LinearLayout viewConte, List<View> personList, View pointView) {
        pointView.findViewById(R.id.cargo_ii_askTel_delete).setOnClickListener(v -> {
            viewConte.removeView(pointView);
            personList.remove(pointView);
            disPlayAskButton(personList);
        });
        pointView.findViewById(R.id.cargo_ii_askTel_add).setOnClickListener(v -> {
            View newView = getInSurveyAskItem(null);
            personList.add(newView);
            viewConte.addView(newView);
            setAskOnclick(viewConte,personList,newView);
            disPlayAskButton(personList);
        });
        disPlayAskButton(personList);
    }

    private void disPlayAskButton(List<View> personList){
        for (int j = 0; j < personList.size(); j++) {
            if (personList.size()==1){
                personList.get(j).findViewById(R.id.cargo_ii_askTel_delete).setVisibility(View.GONE); //只有一个调查对象时，只能添加不能删除。
                personList.get(j).findViewById(R.id.cargo_ii_askTel_add).setVisibility(View.VISIBLE); //只有一个调查对象时，只能添加不能删除。
            }else if (j == (personList.size() - 1)) {
                personList.get(j).findViewById(R.id.cargo_ii_askTel_delete).setVisibility(View.VISIBLE); //只有一个调查对象时，只能添加不能删除。
                personList.get(j).findViewById(R.id.cargo_ii_askTel_add).setVisibility(View.VISIBLE); //只有一个调查对象时，只能添加不能删除。
            } else {
                personList.get(j).findViewById(R.id.cargo_ii_askTel_delete).setVisibility(View.VISIBLE); //最后一个询问对象既可以删除，也可以增加。
                personList.get(j).findViewById(R.id.cargo_ii_askTel_add).setVisibility(View.GONE); //最后一个询问对象既可以删除，也可以增加。
            }
        }
    }

    /**
     * 删除作业地址
     * @param itemMap
     * @param fViewConte
     */
    private void setAddressDelete(Map<View, List<View>> itemMap, LinearLayout fViewConte) {
        for (View keyView:itemMap.keySet()){
            if (itemMap==null || itemMap.size()==1 ){
                keyView.findViewById(R.id.cxrsdcqa_surveyDelete).setVisibility(View.GONE);
            }else{
                keyView.findViewById(R.id.cxrsdcqa_surveyDelete).setVisibility(View.VISIBLE);
                keyView.findViewById(R.id.cxrsdcqa_surveyDelete).setOnClickListener(v -> {
                    fViewConte.removeView(keyView);
                    itemMap.remove(keyView);
                    setAddressDelete(itemMap, fViewConte);
                });
            }
        }
    }


    /**
     * 显示调查对象item
     * @param isaskTemp
     * @return
     */
    private View getInSurveyAskItem(DoctorEntity isaskTemp) {
        View view = inflater.inflate(R.layout.cxrsdc_injury_survey_list_item_item,null);
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askObjTiele),"医生姓名：");
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askTelTitle),"医生电话：");
            if (isaskTemp!=null){
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askObj),isaskTemp.doctorName);
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askTel),isaskTemp.doctorTel);
            }
        return view;
    }

    @Override
    public void SaveDataToEntity() {
        if (doctorViewList == null) return;
        for (int i = 0; i < doctorViewList.size(); i++) {  //询问对象信息
            View doctorView = doctorViewList.get(i);
            if (activity.taskEntity.data.contentJson.doctorList == null)
                activity.taskEntity.data.contentJson.doctorList = new ArrayList<>();
            if (activity.taskEntity.data.contentJson.doctorList.get(i) == null)
                activity.taskEntity.data.contentJson.doctorList.set(i, new DoctorEntity());
            DoctorEntity doctorEn = activity.taskEntity.data.contentJson.doctorList.get(i);
            doctorEn.doctorName = ((EditText) doctorView.findViewById(R.id.cargo_ii_askObj)).getText().toString();  //询问对象
            doctorEn.doctorTel = ((EditText) doctorView.findViewById(R.id.cargo_ii_askTel)).getText().toString();  //对象电话
        }
        activity.taskEntity.data.contentJson.injuredName = injuredNameEdit.getText().toString(); //伤者姓名
        activity.taskEntity.data.contentJson.injuredTel = injuredTelEdit.getText().toString(); //伤者电话
        activity.taskEntity.data.contentJson.surveyTime = surveyTime.getText().toString(); //查勘地点
        activity.taskEntity.data.contentJson.surveyReport = surveyReportEdit.getText().toString(); //调查报告
        activity.taskEntity.data.contentJson.surveyAddress = surveyAddress.getText().toString(); //查勘地点
        //调查结果
        switch (surveyResultRG.getCheckedRadioButtonId()) {
            case R.id.cxInInOn_surveyResult_RBY:
                activity.taskEntity.data.contentJson.surveyResult = activity.cxDict.getValueByLabel("survey_result", "阳性");
                break;
            case R.id.cxInInOn_surveyResult_RBB:
                activity.taskEntity.data.contentJson.surveyResult = activity.cxDict.getValueByLabel("survey_result", "阴性");
                break;
        }
    }
}

