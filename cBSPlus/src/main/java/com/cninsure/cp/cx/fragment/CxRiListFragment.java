package com.cninsure.cp.cx.fragment;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxInjuryMediateActivity;
import com.cninsure.cp.entity.cx.InjuryMediateWorkEntity;
import com.cninsure.cp.entity.cx.MedicalPaid;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CxRiListFragment  extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private View contentView;


    //医疗费用赔偿复选框id合集
    private int[] mFeeIdArr = new int[]{R.id.cxInMe_mFee0,R.id.cxInMe_mFee1,R.id.cxInMe_mFee2,R.id.cxInMe_mFee3
    ,R.id.cxInMe_mFee4,R.id.cxInMe_mFee5,R.id.cxInMe_mFee6};
    //死亡伤残赔偿复选框id合集
    private int[] cFeeIdArr = new int[]{R.id.cxInMe_cFee0,R.id.cxInMe_cFee1,R.id.cxInMe_cFee2,R.id.cxInMe_cFee3,R.id.cxInMe_cFee4
    ,R.id.cxInMe_cFee5,R.id.cxInMe_cFee6,R.id.cxInMe_cFee7,R.id.cxInMe_cFee8,R.id.cxInMe_cFee9,R.id.cxInMe_cFee10};

    private Map<Integer,View> mFeeMapView,cFeeMapView;
    private LayoutInflater inflater;
    private CxInjuryMediateActivity activity;

    @ViewInject(R.id.cxInMe_medicalPaid_line)
    LinearLayout medicalPaidLine;
    @ViewInject(R.id.cxInMe_casualtiesPaid_line)
    LinearLayout casualtiesPaidLine;

    @ViewInject(R.id.cxInMe_medicalSubtotal) private TextView medicalSubtotal;//	医疗费用小计
    @ViewInject(R.id.cxInMe_casualtiesSubtotal) private TextView casualtiesSubtotal;//	死亡伤残赔偿小计
    @ViewInject(R.id.cxInMe_total) private TextView total;//	总计
    @ViewInject(R.id.cxInMe_DeductibleExplain) private EditText DeductibleExplain;//	免赔说明

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cxrs_injury_mediate_list_fragment, null);
        activity = (CxInjuryMediateActivity) getActivity();
        ViewUtils.inject(this,contentView);
        initView();
        return contentView;
    }

    //初始化控件
    private void initView() {
        initMap();
        setCheckBoxOnclick();
        displayOtherData();
    }

    /**显示列表意外的其他数据*/
    private void displayOtherData() {
        InjuryMediateWorkEntity tempImw = activity.taskEntity.data.contentJson;
        SetTextUtil.setTextViewText(medicalSubtotal,tempImw.medicalSubtotal==null?"":tempImw.medicalSubtotal+"");//	医疗费用小计
        SetTextUtil.setTextViewText(casualtiesSubtotal,tempImw.casualtiesSubtotal==null?"":tempImw.casualtiesSubtotal+"");//	死亡伤残赔偿小计
        SetTextUtil.setTextViewText(total,tempImw.total==null?"":tempImw.total+"");//	总计
        SetTextUtil.setEditText(DeductibleExplain,tempImw.DeductibleExplain);//	免赔说明
    }

    private void setCheckBoxOnclick() {
        for (int id:mFeeIdArr){
            ((CheckBox)contentView.findViewById(id)).setOnCheckedChangeListener(this);
        }
        for (int id:cFeeIdArr){
            ((CheckBox)contentView.findViewById(id)).setOnCheckedChangeListener(this);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cxInMe_mFee0:addMFee(0,isChecked);break;
            case R.id.cxInMe_mFee1:addMFee(1,isChecked);break;
            case R.id.cxInMe_mFee2:addMFee(2,isChecked);break;
            case R.id.cxInMe_mFee3:addMFee(3,isChecked);break;
            case R.id.cxInMe_mFee4:addMFee(4,isChecked);break;
            case R.id.cxInMe_mFee5:addMFee(5,isChecked);break;
            case R.id.cxInMe_mFee6:addMFee(6,isChecked);break;

            case R.id.cxInMe_cFee0:addCFee(0,isChecked);break;
            case R.id.cxInMe_cFee1:addCFee(1,isChecked);break;
            case R.id.cxInMe_cFee2:addCFee(2,isChecked);break;
            case R.id.cxInMe_cFee3:addCFee(3,isChecked);break;
            case R.id.cxInMe_cFee4:addCFee(4,isChecked);break;
            case R.id.cxInMe_cFee5:addCFee(5,isChecked);break;
            case R.id.cxInMe_cFee6:addCFee(6,isChecked);break;
            case R.id.cxInMe_cFee7:addCFee(7,isChecked);break;
            case R.id.cxInMe_cFee8:addCFee(8,isChecked);break;
            case R.id.cxInMe_cFee9:addCFee(9,isChecked);break;
            case R.id.cxInMe_cFee10:addCFee(10,isChecked);break;
        }
        countFee();
    }

//添加或删除 医疗费用赔偿明细
    private void addMFee(int position,boolean isChecked){
        if (isChecked){
            activity.taskEntity.data.contentJson.medicalFee.add(position);
            MedicalPaid tempMp = new MedicalPaid();
            tempMp.itemValue = position;
            tempMp.itemLabel = TypePickeUtil.getDictLabelArr(activity.cxDict.getDictByType("medicalFee"))[position];
            activity.taskEntity.data.contentJson.medicalPaid.add(tempMp);
        }else{
            activity.taskEntity.data.contentJson.medicalFee.remove(Integer.valueOf(position));
            for (MedicalPaid obj:activity.taskEntity.data.contentJson.medicalPaid){
                if (obj.itemValue == position) activity.taskEntity.data.contentJson.medicalPaid.remove(obj);
            }
            mFeeMapView.put(position,null);
        }
        savemFeeList();
        savecFeeList();
        dipPlayFeeList();
    }
    //添加或删除 死亡伤残赔偿明细
    private void addCFee(int position, boolean isChecked) {
        if (isChecked){
            activity.taskEntity.data.contentJson.casualtiesFee.add(position);
            MedicalPaid tempMp = new MedicalPaid();
            tempMp.itemValue = position;
            tempMp.itemLabel = TypePickeUtil.getDictLabelArr(activity.cxDict.getDictByType("casualtiesFee"))[position];
            activity.taskEntity.data.contentJson.casualtiesPaid.add(tempMp);
        }else{
            activity.taskEntity.data.contentJson.casualtiesFee.add(position);
            for (MedicalPaid obj:activity.taskEntity.data.contentJson.casualtiesPaid){
                if (obj.itemValue == position) activity.taskEntity.data.contentJson.casualtiesPaid.remove(obj);
            }
            cFeeMapView.put(position,null);
        }
        savemFeeList();
        savecFeeList();
        dipPlayFeeList();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    /**装如空的View*/
    private void initMap() {
        InjuryMediateWorkEntity tempImw = activity.taskEntity.data.contentJson;
        if (mFeeMapView == null) mFeeMapView = new TreeMap<>();
        if (cFeeMapView == null) cFeeMapView = new TreeMap<>();
        if (tempImw.medicalPaid == null) tempImw.medicalPaid = new HashSet<>();
        if (tempImw.casualtiesPaid == null) tempImw.casualtiesPaid = new HashSet<>();
        if (tempImw.medicalFee == null) tempImw.medicalFee = new TreeSet<>();
        if (tempImw.casualtiesFee == null) tempImw.casualtiesFee = new TreeSet<>();
        dipPlayFeeList();
    }

    /***/
    private void dipPlayFeeList() {
        if (activity.taskEntity!=null && activity.taskEntity.data!=null && activity.taskEntity.data.contentJson!=null){
            disPlayMedicalPaid(activity.taskEntity.data.contentJson.medicalPaid); //显示 医疗费用赔偿 明细
            disPlayCasualtiesPaid(activity.taskEntity.data.contentJson.casualtiesPaid); //显示 死亡伤残赔偿 明细
        }
    }

    /**显示 医疗费用赔偿 明细*/
    private void disPlayMedicalPaid(Set<MedicalPaid> medicalPaid){
        if (medicalPaid!=null){
            medicalPaidLine.removeAllViews();
            for (MedicalPaid temp:medicalPaid){
                if (temp!=null){
                    mFeeMapView.put(temp.itemValue,getItemView(temp));
                    medicalPaidLine.addView(mFeeMapView.get(temp.itemValue));
                    ((CheckBox)contentView.findViewById(mFeeIdArr[temp.itemValue])).setChecked(true); //设置选中
                }
            }
        }
    }
    /**显示 死亡伤残赔偿 明细*/
    private void disPlayCasualtiesPaid(Set<MedicalPaid> casualtiesPaid){
        if (casualtiesPaid!=null){
            casualtiesPaidLine.removeAllViews();
            for (MedicalPaid temp:casualtiesPaid){
                if (temp!=null){
                    cFeeMapView.put(temp.itemValue,getItemView(temp));
                    casualtiesPaidLine.addView(cFeeMapView.get(temp.itemValue));
                    ((CheckBox)contentView.findViewById(cFeeIdArr[temp.itemValue])).setChecked(true); //设置选中
                }
            }
        }
    }

    private View getItemView(MedicalPaid mpTemp){
        View view = inflater.inflate(R.layout.cxrs_injury_mediate_list_item,null);
        SetTextUtil.setTextViewText(view.findViewById(R.id.cxInMe_itemLabel),mpTemp.itemLabel);
        SetTextUtil.setEditText(view.findViewById(R.id.cxInMe_itemFee),mpTemp.itemFee==null?"":mpTemp.itemFee+"");
        SetTextUtil.setEditText(view.findViewById(R.id.cxInMe_explain),mpTemp.explain);
        SetTextUtil.setEditText(view.findViewById(R.id.cxInMe_basis),mpTemp.basis);
        addOnchangeLin((EditText)view.findViewById(R.id.cxInMe_itemFee)); //设置输入费用监听
        return view;
    }

    private void addOnchangeLin(EditText view) {
        view.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countFee();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    public void countFee() {
        float mFeeCount = 0;
        float cFeeCount = 0;
        float allCount = 0;
        InjuryMediateWorkEntity tempImw = activity.taskEntity.data.contentJson;
        for (Integer key : mFeeMapView.keySet()) {  //huqu
            View view = mFeeMapView.get(key);
            if (view!=null){
                String itemFeeStr = ((EditText)view.findViewById(R.id.cxInMe_itemFee)).getText().toString();
                if (!TextUtils.isEmpty(itemFeeStr)) mFeeCount +=Float.parseFloat(itemFeeStr);
            }
        }
        for (Integer key : cFeeMapView.keySet()) {
            View view = cFeeMapView.get(key);
            if (view != null) {
                String itemFeeStr = ((EditText) view.findViewById(R.id.cxInMe_itemFee)).getText().toString();
                if (!TextUtils.isEmpty(itemFeeStr)) cFeeCount += Float.parseFloat(itemFeeStr);
            }
        }
        allCount = mFeeCount+cFeeCount;
        medicalSubtotal.setText(mFeeCount+"");
        casualtiesSubtotal.setText(cFeeCount+"");
        total.setText(allCount+"");

    }

    /***保存数据到实体类*/
    @Override
    public void SaveDataToEntity() {
        if (activity==null) return;  //activity说明没有初始化这个Fragment，也就没有任何操作，没有不要保存了，
        InjuryMediateWorkEntity tempImw = activity.taskEntity.data.contentJson;
        String mstStr = medicalSubtotal.getText().toString();
        tempImw.medicalSubtotal = TextUtils.isEmpty(mstStr)?null:Float.parseFloat(mstStr);//	医疗费用小计

        String csStr = casualtiesSubtotal.getText().toString();
        tempImw.casualtiesSubtotal = TextUtils.isEmpty(csStr)?null:Float.parseFloat(csStr);//	死亡伤残赔偿小计

        String totalStr = total.getText().toString();
        tempImw.total = TextUtils.isEmpty(totalStr)?null:Float.parseFloat(totalStr);//	总计
        tempImw.DeductibleExplain = DeductibleExplain.getText().toString();//	免赔说明

        savemFeeList();
        savecFeeList();
    }

    private void savecFeeList() {
        InjuryMediateWorkEntity tempImw = activity.taskEntity.data.contentJson;
        for (Integer key:cFeeMapView.keySet()){
            View view = cFeeMapView.get(key);
            if (view!=null){
                for (MedicalPaid tMp:tempImw.casualtiesPaid){
                    if (tMp.itemValue == key){
                        MedicalPaid tempMp = tMp;
                        String itemFeeStr = ((EditText)view.findViewById(R.id.cxInMe_itemFee)).getText().toString();
                        tempMp.itemFee = TextUtils.isEmpty(itemFeeStr)?null:Float.valueOf(itemFeeStr);
                        tempMp.explain = ((EditText)view.findViewById(R.id.cxInMe_explain)).getText().toString();
                        tempMp.basis = ((EditText)view.findViewById(R.id.cxInMe_basis)).getText().toString();
                    }
                }
            }
        }
    }
    private void savemFeeList() {
        InjuryMediateWorkEntity tempImw = activity.taskEntity.data.contentJson;
        for (Integer key:mFeeMapView.keySet()){
            View view = mFeeMapView.get(key);
           if (view!=null){
               for (MedicalPaid tMp:tempImw.medicalPaid){
                   if (tMp.itemValue == key){
                       MedicalPaid tempMp = tMp;
                       String itemFeeStr = ((EditText)view.findViewById(R.id.cxInMe_itemFee)).getText().toString();
                       tempMp.itemFee = TextUtils.isEmpty(itemFeeStr)?null:Float.valueOf(itemFeeStr);
                       tempMp.explain = ((EditText)view.findViewById(R.id.cxInMe_explain)).getText().toString();
                       tempMp.basis = ((EditText)view.findViewById(R.id.cxInMe_basis)).getText().toString();
                   }
               }
           }
        }
    }
}

