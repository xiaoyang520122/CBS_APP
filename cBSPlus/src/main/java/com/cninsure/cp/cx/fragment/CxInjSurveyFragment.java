package com.cninsure.cp.cx.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxInjurySurveyActivity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.entity.cx.MedicalPaid;
import com.cninsure.cp.entity.cx.injurysurvey.CxInjurySurveyTaskEntity;
import com.cninsure.cp.entity.cx.injurysurvey.InSurveyAskList;
import com.cninsure.cp.entity.cx.injurysurvey.InSurveyItems;
import com.cninsure.cp.entity.cx.injurysurvey.InSurveyTypeList;
import com.cninsure.cp.entity.cx.injurysurvey.InjurySurveyWorkEntity;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.cninsure.cp.view.AntoLineUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


/**
 * @author :xy-wm
 * date:2020/12/18
 * usefuLness: CBS_APP
 */
public class CxInjSurveyFragment extends BaseFragment {//implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

//    private View contentView;
//
//    //死亡伤残赔偿复选框id合集
//    private Map<String,CheckBox> typeViewList;
//
//    private Map<String,View> surveyMapView;
//    private Map<String,List<>>
//    private LayoutInflater inflater;
//    private CxInjurySurveyActivity activity;
//
//    @ViewInject(R.id.cxInIn_medicalPaid_line) LinearLayout surveyLine; //调查记录输入框
//
//    @ViewInject(R.id.cxInIn_injuredName) private EditText injuredNameEdit; //伤者姓名
//    @ViewInject(R.id.cxInIn_injuredTel) private EditText injuredTelEdit;    //伤者电话
//    @ViewInject(R.id.cxInIn_CheckBoxGroup) private AntoLineUtil surveySmallTypeGroup;//调查类型View组
//    @ViewInject(R.id.cxInIn_surveyResult_RG) private RadioGroup surveyResultRG; //调查结果
//    @ViewInject(R.id.cxInIn_surveyReport) private EditText surveyReportEdit; //调查报告
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        this.inflater = inflater;
//        contentView = inflater.inflate(R.layout.cxrs_injury_investigation_list_fragment, null);
//        activity = (CxInjurySurveyActivity) getActivity();
//        ViewUtils.inject(this,contentView);
//        initView();
//        return contentView;
//    }
//
//    //初始化控件
//    private void initView() {
//        setCheckBoxOnclick();
//        initMap();
//    }
//
//    private void setCheckBoxOnclick() {
//        List<DictData> inSmallType = activity.cxDict.getDictByType("investigation_small_type");
//        typeViewList = new HashMap<>(7);
//        for (int i=0;i<inSmallType.size();i++){
//            DictData dda = inSmallType.get(i);
//            // 用以下方法将layout布局文件换成view
//            LayoutInflater inflater = getLayoutInflater();
//            View view = inflater.inflate(R.layout.check_item,null);
//            TextView textView = view.findViewById(R.id.video_preview_item_tv);
//            textView.setText(dda.label);
//            typeViewList.put(dda.value,view.findViewById(R.id.video_previ));
//            surveySmallTypeGroup.addView(view);
//            typeViewList.get(dda.value).setOnCheckedChangeListener(this);
//        }
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        for (String key:typeViewList.keySet()){
//            if (buttonView.getId() == typeViewList.get(key).getId()) {
//                addMFee(key,isChecked);
//                break;
//            }
//        }
//        countFee();
//    }
//
//    /**添加或删除*/
//    private void addMFee(String key,boolean isChecked){
//        DictData dictData = activity.cxDict.getDictDataByValue("investigation_small_type",key);
//        if (isChecked){
//            InSurveyTypeList tempMp = new InSurveyTypeList();
//            tempMp.itemValue = dictData.value;
//            tempMp.itemTitle = dictData.label;
//            activity.taskEntity.data.contentJson.surveyTypeList.add(tempMp);
//        }else{
//            for ( InSurveyTypeList obj:activity.taskEntity.data.contentJson.surveyTypeList){
//                if (obj.itemValue.equals(dictData.value)) {
//                    activity.taskEntity.data.contentJson.surveyTypeList.remove(obj);
//                    break;
//                }
//            }
//            surveyMapView.put(dictData.value,null);
//        }
//        disPlayMedicalPaid(activity.taskEntity.data.contentJson.surveyTypeList);
//        savemFeeList();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//
//        }
//    }
//
//    /**装入空的View*/
//    private void initMap() {
//        if (activity.taskEntity != null && activity.taskEntity.data != null && activity.taskEntity.data.contentJson != null) {
//            InjurySurveyWorkEntity tempImw = activity.taskEntity.data.contentJson;
//            if (surveyMapView == null) surveyMapView = new TreeMap<>();
//            if (tempImw.surveyTypeList == null) tempImw.surveyTypeList = new ArrayList<>();
//            disPlayMedicalPaid(activity.taskEntity.data.contentJson.surveyTypeList);
//        }
//    }
//
//    /**显示探访内容*/
//    private void disPlayMedicalPaid(List<InSurveyTypeList> medicalPaid){
//        if (medicalPaid!=null){
//            surveyLine.removeAllViews();
//            for (InSurveyTypeList temp:medicalPaid){
//                if (temp!=null){
//                    surveyMapView.put(temp.itemValue,getItemView(temp));
//                    surveyLine.addView(surveyMapView.get(temp.itemValue));
//                    typeViewList.get(temp.itemValue).setChecked(true); //设置选中
//                }
//            }
//        }
//    }
//
//    /**添加探访内容*/
//    private View getItemView(InSurveyTypeList temp) {
//        LinearLayout view  = (LinearLayout) inflater.inflate(R.layout.cxrs_injury_investigation_list_item,null);
//        SetTextUtil.setTextViewText(view.findViewById(R.id.cxrsdc_qa_item_parent),temp.itemTitle); //设置调查项目标题
//        if (temp!=null && temp.items!=null && temp.items.size()>0){
//            for (InSurveyItems isiTemp:temp.items){ //遍历显示作业内容
//                view.addView(getInSurveyItem(isiTemp));
//            }
//        }else{
//            view.addView(getInSurveyItem(new InSurveyItems()));
//        }
//        return view;
//    }
//
//    /**显示作业*/
//    private View getInSurveyItem(InSurveyItems isiTemp) {
//        LinearLayout view  = (LinearLayout) inflater.inflate(R.layout.cxrsdc_injury_survey_list_item,null);
//        SetTextUtil.setTextViewText(view.findViewById(R.id.cxrsdcqa_surveyTime),isiTemp.workTime); //作业时间
//        SetTextUtil.setEditText(view.findViewById(R.id.cxrsdcqa_surveyAddress),isiTemp.workTime); //作业地点
//        if (isiTemp!=null && isiTemp.askLists!=null && isiTemp.askLists.size()>0){
//            for (InSurveyAskList isaskTemp:isiTemp.askLists){
//                view.addView(getInSurveyAskItem(isaskTemp));
//            }
//        }else{
//
//        }
//        return view;
//    }
//
//    /**
//     * 显示调查内容item
//     * @param isaskTemp
//     * @return
//     */
//    private View getInSurveyAskItem(InSurveyAskList isaskTemp) {
//        View view = inflater.inflate(R.layout.cxrsdc_injury_survey_list_item_item,null);
//        if (isaskTemp!=null){
//
//        }
//        return view;
//    }
//
//    public void countFee() {
//        float mFeeCount = 0;
//        float cFeeCount = 0;
//        float allCount = 0;
//        CxInjurySurveyTaskEntity tempImw = activity.taskEntity.data.contentJson;
//        for (Integer key : surveyMapView.keySet()) {  //huqu
//            View view = surveyMapView.get(key);
//            if (view!=null){
//                String itemFeeStr = ((EditText)view.findViewById(R.id.cxInIn_itemFee)).getText().toString();
//                if (!TextUtils.isEmpty(itemFeeStr)) mFeeCount +=Float.parseFloat(itemFeeStr);
//            }
//        }
//        for (Integer key : cFeeMapView.keySet()) {
//            View view = cFeeMapView.get(key);
//            if (view != null) {
//                String itemFeeStr = ((EditText) view.findViewById(R.id.cxInIn_itemFee)).getText().toString();
//                if (!TextUtils.isEmpty(itemFeeStr)) cFeeCount += Float.parseFloat(itemFeeStr);
//            }
//        }
//        allCount = mFeeCount+cFeeCount;
//        medicalSubtotal.setText(mFeeCount+"");
//        casualtiesSubtotal.setText(cFeeCount+"");
//        total.setText(allCount+"");
//
//    }
//
//    /***保存数据到实体类*/
    @Override
    public void SaveDataToEntity() {
//        if (activity==null) return;  //activity说明没有初始化这个Fragment，也就没有任何操作，没有不要保存了，
//        CxInjurySurveyTaskEntity tempImw = activity.taskEntity.data.contentJson;
//        String mstStr = medicalSubtotal.getText().toString();
//        tempImw.medicalSubtotal = TextUtils.isEmpty(mstStr)?null:Float.parseFloat(mstStr);//	医疗费用小计
//
//        String csStr = casualtiesSubtotal.getText().toString();
//        tempImw.casualtiesSubtotal = TextUtils.isEmpty(csStr)?null:Float.parseFloat(csStr);//	死亡伤残赔偿小计
//
//        String totalStr = total.getText().toString();
//        tempImw.total = TextUtils.isEmpty(totalStr)?null:Float.parseFloat(totalStr);//	总计
//        tempImw.DeductibleExplain = DeductibleExplain.getText().toString();//	免赔说明
//
//        savemFeeList();
    }
//
//    private void savemFeeList() {
//        InjurySurveyWorkEntity tempImw = activity.taskEntity.data.contentJson;
//        for (String key: surveyMapView.keySet()){
//            View view = surveyMapView.get(key);
//           if (view!=null){
//               for (MedicalPaid tMp:tempImw.medicalPaid){
//                   if (tMp.itemValue == key){
//                       MedicalPaid tempMp = tMp;
//                       String itemFeeStr = ((EditText)view.findViewById(R.id.cxInIn_itemFee)).getText().toString();
//                       tempMp.itemFee = TextUtils.isEmpty(itemFeeStr)?null:Float.valueOf(itemFeeStr);
//                       tempMp.explain = ((EditText)view.findViewById(R.id.cxInIn_explain)).getText().toString();
//                       tempMp.basis = ((EditText)view.findViewById(R.id.cxInIn_basis)).getText().toString();
//                   }
//               }
//           }
//        }
//    }
}

