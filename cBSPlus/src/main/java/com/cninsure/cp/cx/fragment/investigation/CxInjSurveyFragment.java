package com.cninsure.cp.cx.fragment.investigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxInjuryExamineActivity;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.entity.cx.injurysurvey.InExamineTypeList;
import com.cninsure.cp.entity.cx.injurysurvey.InSurveyAskList;
import com.cninsure.cp.entity.cx.injurysurvey.InSurveyItems;
import com.cninsure.cp.entity.cx.injurysurvey.InjuryExamineWorkEntity;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.view.AntoLineUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author :xy-wm
 * date:2020/12/18
 * usefuLness: CBS_APP
 */
public class CxInjSurveyFragment extends BaseFragment implements View.OnClickListener {

    private View contentView;

    //死亡伤残赔偿复选框id合集
    private Map<String,CheckBox> typeViewList;

    private Map<String,View> surveyMapView;
    private Map<String,Map<View,List<View>>> typeViewMap; //调查类型View对应Map对象
    private LayoutInflater inflater;
    private CxInjuryExamineActivity activity;

    @ViewInject(R.id.cxInIn_medicalPaid_line) LinearLayout surveyLine; //调查记录输入框

    @ViewInject(R.id.cxInIn_injuredName) private EditText injuredNameEdit; //伤者姓名
    @ViewInject(R.id.cxInIn_injuredTel) private EditText injuredTelEdit;    //伤者电话
    @ViewInject(R.id.cxInIn_CheckBoxGroup) private AntoLineUtil surveySmallTypeGroup;//调查类型View组
    @ViewInject(R.id.cxInIn_surveyResult_RG) private RadioGroup surveyResultRG; //调查结果
    @ViewInject(R.id.cxInIn_surveyReport) private EditText surveyReportEdit; //调查报告

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cxrs_injury_investigation_list_fragment, null);
        activity = (CxInjuryExamineActivity) getActivity();
        ViewUtils.inject(this,contentView);
        initView();
        return contentView;
    }

    //初始化控件
    private void initView() {
        displaBasicInfo();
        displayCheckBox();
        initMap();
    }

    private void displaBasicInfo() {
        SetTextUtil.setTextViewText(injuredNameEdit,activity.taskEntity.data.contentJson.injuredName); //伤者姓名
        SetTextUtil.setTextViewText(injuredTelEdit,activity.taskEntity.data.contentJson.injuredTel); //伤者电话
        SetTextUtil.setTextViewText(surveyReportEdit,activity.taskEntity.data.contentJson.surveyReport); //调查报告
        //调查结果  1有、0无
        if ("1".equals(activity.taskEntity.data.contentJson.surveyResult)) surveyResultRG.check(R.id.cxInIn_surveyResult_RBB); //阴性
        if ("0".equals(activity.taskEntity.data.contentJson.surveyResult)) surveyResultRG.check(R.id.cxInIn_surveyResult_RBY); //阳性
    }

    private void displayCheckBox() {
        List<DictData> inSmallType = activity.cxDict.getDictByType("investigation_small_type");
        typeViewList = new HashMap<>(7);
        for (int i=0;i<inSmallType.size();i++){
            DictData dda = inSmallType.get(i);
            // 用以下方法将layout布局文件换成view
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.check_item,null);
            CheckBox cbox = view.findViewById(R.id.video_previ);
            cbox.setText(dda.label);
            typeViewList.put(dda.value,view.findViewById(R.id.video_previ));
            surveySmallTypeGroup.addView(view);
            setCheckBoxOnclick(dda.value);
        }
    }

    /**
     * 选择调查内容并显示对应的作业界面
     * @param value
     */
    private void setCheckBoxOnclick(String value) {
        typeViewList.get(value).setOnCheckedChangeListener((buttonView, isChecked) -> {
            addMFee(value,isChecked);
        });
    }

    /**添加或删除*/
    private void addMFee(String key,boolean isChecked){
        DictData dictData = activity.cxDict.getDictDataByValue("investigation_small_type",key);
        if (isChecked && activity.taskEntity.data.contentJson.InSurveyTypeIsNull(dictData.value)){
            InExamineTypeList tempMp = new InExamineTypeList();
            tempMp.itemValue = dictData.value;
            if (!dictData.value.equals("6"))  tempMp.itemTitle = dictData.label; //如果是其他作业类型，不需要默认标题值。
            activity.taskEntity.data.contentJson.surveyTypeList.add(tempMp);
            disPlayMedicalPaid(activity.taskEntity.data.contentJson.surveyTypeList); //非if else判断，需要放到判断中执行
        }else if(!isChecked){
            for ( InExamineTypeList obj:activity.taskEntity.data.contentJson.surveyTypeList){
                if (obj.itemValue.equals(dictData.value)) {
                    activity.taskEntity.data.contentJson.surveyTypeList.remove(obj);
                    break;
                }
            }
            surveyMapView.put(dictData.value,null);
            typeViewMap.remove(dictData.value);
            disPlayMedicalPaid(activity.taskEntity.data.contentJson.surveyTypeList); //非if else判断，需要放到判断中执行
        }
//        savemFeeList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    /**装入空的View*/
    private void initMap() {
        if (activity.taskEntity != null && activity.taskEntity.data != null && activity.taskEntity.data.contentJson != null) {
            InjuryExamineWorkEntity tempImw = activity.taskEntity.data.contentJson;
            if (surveyMapView == null) surveyMapView = new TreeMap<>();
            if (tempImw.surveyTypeList == null) tempImw.surveyTypeList = new ArrayList<>();
            disPlayMedicalPaid(activity.taskEntity.data.contentJson.surveyTypeList);
        }
    }

    /**显示探访内容*/
    private void disPlayMedicalPaid(List<InExamineTypeList> medicalPaid){
        SaveDataToEntity();  //保存一下现有控件上数据
        if (medicalPaid!=null){
            surveyLine.removeAllViews();
            surveyMapView = new TreeMap<>();
            typeViewMap = new TreeMap<>();
            for (InExamineTypeList temp:medicalPaid){
                if (temp!=null){
                    surveyMapView.put(temp.itemValue,getItemView(temp));
                    surveyLine.addView(surveyMapView.get(temp.itemValue));
                    typeViewList.get(temp.itemValue).setChecked(true); //设置选中
                }
            }
        }
    }

    /**获取探访内容view*/
    private View getItemView(InExamineTypeList temp) {
        Map<View, List<View>> itemMap = new HashMap<>();

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.cxrs_injury_investigation_list_item, null);
        LinearLayout viewConte = view.findViewById(R.id.cxrsdc_qa_item_parent);
        SetTextUtil.setTextViewText(view.findViewById(R.id.cxrsdc_qa_item_parent_title), temp.itemTitle); //设置调查项目标题
        if (temp.itemValue.equals("6")){ //其他调查类型，需要录入名称和回显。
            SetTextUtil.setTextViewText(view.findViewById(R.id.cxrsdc_qa_item_parent_title), "其他："); //设置调查项目标题
            view.findViewById(R.id.cxrsdc_qa_item_parent_title_value).setVisibility(View.VISIBLE);  //显示其他调查类型名称输入框
            SetTextUtil.setEditText(view.findViewById(R.id.cxrsdc_qa_item_parent_title_value),temp.itemTitle);
        }
        setAddWorkSddressOnclick(view.findViewById(R.id.cxrsdc_qa_item_parent_add), temp,viewConte,itemMap);

        if (temp != null && temp.items != null && temp.items.size() > 0) {
            for (int i = 0; i < temp.items.size(); i++) { //遍历显示作业内容
                InSurveyItems isiTemp = temp.items.get(i);
                Map<View, List<View>> tempViewMap = getInSurveyItem(isiTemp, temp.itemValue, temp,viewConte);
                itemMap.putAll(tempViewMap); //装入作业地点及对应的调查对象View集合
                    for (View keyView : tempViewMap.keySet()) {
                    viewConte.addView(keyView);
                    setAddressDelete(itemMap,viewConte);
                }
            }
        } else {
            temp.items = new ArrayList<>();
            temp.items.add(new InSurveyItems());
            Map<View, List<View>> tempViewMap = getInSurveyItem(temp.items.get(0), temp.itemValue, temp,viewConte);
            itemMap.putAll(tempViewMap); //装入作业地点及对应的调查对象View集合
            for (View keyView : tempViewMap.keySet()) {
                viewConte.addView(keyView); //这里Map只有一个可以，直接拿出来显示
                setAddressDelete(itemMap,viewConte);
            }
        }
        typeViewMap.put(temp.itemValue,itemMap);
        return view;
    }

    /**
     * 点击添加作业地点事件
     * @param viewById
     * @param temp
     * @param viewConte
     * @param itemMap
     */
    private void setAddWorkSddressOnclick(View viewById, InExamineTypeList temp, LinearLayout viewConte, Map<View, List<View>> itemMap) {
        viewById.setOnClickListener(v -> {
            //通过加控件的形式达到加项目目的。
            temp.items = new ArrayList<>();
            temp.items.add(new InSurveyItems());
            Map<View, List<View>> tempViewMap = getInSurveyItem(temp.items.get(0), temp.itemValue, temp,viewConte);
            itemMap.putAll(tempViewMap); //装入作业地点及对应的调查对象View集合
            for (View keyView : tempViewMap.keySet()) {
                viewConte.addView(keyView); //这里Map只有一个可以，直接拿出来显示
                setAddressDelete(itemMap,viewConte);
            }
        });
    }

    /**显示作业地点信息*/
    private Map<View,List<View>> getInSurveyItem(InSurveyItems isiTemp, String itemValue, InExamineTypeList surveyTypeList, LinearLayout fViewConte) {
        Map<View,List<View>> itemMap = new HashMap<>();
        List<View> personList = new ArrayList<>();

        LinearLayout view  = (LinearLayout) inflater.inflate(R.layout.cxrsdc_injury_survey_list_item,null);
        SetTextUtil.setTextViewText(view.findViewById(R.id.cxrsdcqa_surveyTime),isiTemp.workTime); //作业时间
        DateChoiceUtil.setLongDatePickerDialogOnClick(activity,view.findViewById(R.id.cxrsdcqa_surveyTime));
        SetTextUtil.setEditText(view.findViewById(R.id.cxrsdcqa_surveyAddress),isiTemp.workAddress); //作业地点
        LinearLayout viewConte  = view.findViewById(R.id.cxrsdc_qa_item_item_parent);
        if (isiTemp!=null && isiTemp.askList !=null && isiTemp.askList.size()>0){
            for (int i = 0; i<isiTemp.askList.size(); i++){
                InSurveyAskList isaskTemp = isiTemp.askList.get(i);
                personList.add(getInSurveyAskItem(isaskTemp,itemValue));
                viewConte.addView(personList.get(i));
                setAskOnclick(viewConte,personList,personList.get(i),itemValue);
            }
        }else{
            isiTemp.askList = new ArrayList<>();
            isiTemp.askList.add(new InSurveyAskList());
            personList.add(getInSurveyAskItem(null,itemValue));
            viewConte.addView(personList.get(0));
            setAskOnclick(viewConte,personList,personList.get(0),itemValue);
        }
        itemMap.put(view,personList);
//        setAddressDelete(view.findViewById(R.id.cxrsdcqa_surveyDelete),itemMap,view,fViewConte,itemValue);
        return itemMap;
    }

    /**
     * 添加 或 删除 询问对象
     */
    private void setAskOnclick(LinearLayout viewConte, List<View> personList, View pointView, String itemValue) {
        if ("2".equals(itemValue) || "3".equals(itemValue) ||"5".equals(itemValue)) {
            if ( "3".equals(itemValue) ||"5".equals(itemValue)) { //个别地址不用添加多个询问对象
                pointView.findViewById(R.id.cargo_ii_askTel_delete).setVisibility(View.GONE); //只有一个调查对象时，只能添加不能删除。
                pointView.findViewById(R.id.cargo_ii_askTel_add).setVisibility(View.GONE); //只有一个调查对象时，只能添加不能删除。
            }
            return;
        } //个别地址不用添加多个询问对象
        pointView.findViewById(R.id.cargo_ii_askTel_delete).setOnClickListener(v -> {
            viewConte.removeView(pointView);
            personList.remove(pointView);
            disPlayAskButton(personList,itemValue);
        });
        pointView.findViewById(R.id.cargo_ii_askTel_add).setOnClickListener(v -> {
            View newView = getInSurveyAskItem(null, itemValue);
            personList.add(newView);
            viewConte.addView(newView);
            setAskOnclick(viewConte,personList,newView,itemValue);
            disPlayAskButton(personList,itemValue);
        });
        disPlayAskButton(personList,itemValue);
    }

    private void disPlayAskButton(List<View> personList, String itemValue){
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
    private View getInSurveyAskItem(InSurveyAskList isaskTemp,String itemValue) {
        View view = null;
        if (itemValue.equals("2") ){ //护理人员信息调查
            view = inflater.inflate(R.layout.cxrsdc_injury_survey_list_nursing_item,null);
            if (isaskTemp!=null){
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ni_askObj),isaskTemp.askObj); //护理人姓名
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ni_askTel),isaskTemp.askTel); //护理人电话
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ni_workOrg),isaskTemp.workOrg); //工作单位
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ni_workTimeStart),isaskTemp.workTimeStart); //开始时间
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ni_workTimeEnd),isaskTemp.workTimeEnd); //结束时间
            }
            DateChoiceUtil.setShortDatePickerDialog(activity,view.findViewById(R.id.cargo_ni_workTimeStart));
            DateChoiceUtil.setShortDatePickerDialog(activity,view.findViewById(R.id.cargo_ni_workTimeEnd));
        }else {
            view = inflater.inflate(R.layout.cxrsdc_injury_survey_list_item_item,null);
            if(itemValue.equals("3") ){  //医疗票据线下调阅
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askObjTiele),"调阅机构：");
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askTelTitle),"机构电话：");
            }else{
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askObjTiele),"询问对象：");
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askTelTitle),"对象电话：");
            }
            if (isaskTemp!=null){
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askObj),isaskTemp.askObj);
                SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askTel),isaskTemp.askTel);
            }
        }
        return view;
    }

    @Override
    public void SaveDataToEntity() {
        if (typeViewMap == null) return;
        for (String typeVk : typeViewMap.keySet()) {  //调查分类
            DictData dictData = activity.cxDict.getDictDataByValue("investigation_small_type", typeVk);
            InExamineTypeList iSuTyLTemp = activity.taskEntity.data.contentJson.getInSuTypeByValue(typeVk);

            iSuTyLTemp.itemValue = typeVk; //作业分类名称
            if (dictData.label.equals("6")) { //其他作业类型，名称需要
                iSuTyLTemp.itemTitle = ((EditText) surveyMapView.get(typeVk).findViewById(R.id.cxrsdc_qa_item_parent_title_value)).getText().toString();
            }else{
                iSuTyLTemp.itemTitle = dictData.label; //作业分类value
            }

            Map<View, List<View>> tempMap = typeViewMap.get(typeVk);
            int addressCount = 0;
            for (View view : tempMap.keySet()) {  //调查地址信息
                InSurveyItems iSuItemTemp = iSuTyLTemp.getInSuItem(addressCount);
                iSuItemTemp.workTime = ((TextView) view.findViewById(R.id.cxrsdcqa_surveyTime)).getText().toString(); //作业时间
                iSuItemTemp.workAddress = ((EditText) view.findViewById(R.id.cxrsdcqa_surveyAddress)).getText().toString();  //作业地点

                List<View> viewTemp = tempMap.get(view);
                int askCount = 0;
                for (View askView : viewTemp) {  //询问对象信息
                    InSurveyAskList inSurveyAskListTemp = iSuItemTemp.getInSuAsk(askCount);
                    if (typeVk.equals("2") ) { //护理人员信息调查
                        inSurveyAskListTemp.askObj = ((EditText) askView.findViewById(R.id.cargo_ni_askObj)).getText().toString();  //护理人姓名
                        inSurveyAskListTemp.askTel = ((EditText) askView.findViewById(R.id.cargo_ni_askTel)).getText().toString();  //护理人电话
                        inSurveyAskListTemp.workOrg = ((EditText) askView.findViewById(R.id.cargo_ni_workOrg)).getText().toString();  //工作单位
                        inSurveyAskListTemp.workTimeStart = ((TextView) askView.findViewById(R.id.cargo_ni_workTimeStart)).getText().toString();  //开始时间
                        inSurveyAskListTemp.workTimeEnd = ((TextView) askView.findViewById(R.id.cargo_ni_workTimeEnd)).getText().toString();  //结束时间
                    }else{
                        inSurveyAskListTemp.askObj = ((EditText) askView.findViewById(R.id.cargo_ii_askObj)).getText().toString();  //询问对象
                        inSurveyAskListTemp.askTel = ((EditText) askView.findViewById(R.id.cargo_ii_askTel)).getText().toString();  //对象电话
                    }
                    askCount++;
                }
                addressCount++;
            }
        }
        activity.taskEntity.data.contentJson.getInvestigationSmallTypes(); //保存选择的调查类型value到列表，web端要用。
        activity.taskEntity.data.contentJson.injuredName = injuredNameEdit.getText().toString(); //伤者姓名
        activity.taskEntity.data.contentJson.injuredTel = injuredTelEdit.getText().toString(); //伤者电话
        activity.taskEntity.data.contentJson.surveyReport = surveyReportEdit.getText().toString(); //调查报告
        //调查结果
        switch (surveyResultRG.getCheckedRadioButtonId()){
            case R.id.cxInIn_surveyResult_RBY:
                activity.taskEntity.data.contentJson.surveyResult=activity.cxDict.getValueByLabel("survey_result","阳性");break;
            case R.id.cxInIn_surveyResult_RBB:
                activity.taskEntity.data.contentJson.surveyResult=activity.cxDict.getValueByLabel("survey_result","阴性");break;
        }
    }
}

