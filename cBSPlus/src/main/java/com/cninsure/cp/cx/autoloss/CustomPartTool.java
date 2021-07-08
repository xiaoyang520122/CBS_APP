package com.cninsure.cp.cx.autoloss;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.DictYjxEntity;
import com.cninsure.cp.entity.cx.CxDsWorkEntity;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :xy-wm
 * date:2021/5/25 12:35
 * usefuLness: CBS_APP
 * 自定义配件工具
 */
public class CustomPartTool {
    private CxDsWorkEntity workEntity; //智能定损信息
    private List<DictYjxEntity.publicData> carPrincipalPart; //车险  自定义配件类型（厂商，品牌，车系，车型通用）取值有：全部、国产、合资、独自
    private String carFacturerCountry,carBrandCountry,carSeriesCountry,carTypeCountry; //厂商，品牌，车系，车型对应生产类型

    public View getView(AutoLossMainActivity context, CxDsWorkEntity workEntity,List<DictYjxEntity.publicData> car_principal_part){
        this.workEntity = workEntity;
        this.carPrincipalPart = car_principal_part;
        View view = LayoutInflater.from(context).inflate(R.layout.custom_part_edit_view,null);
        initData();
        initView(view,context);
        displayView(context,view);
        return view;
    }

    /**
     * 初始化生产类型
     */
    private void initData() {
        carFacturerCountry = workEntity.carFacturerCountry;
        carBrandCountry = workEntity.carBrandCountry;
        carSeriesCountry = workEntity.carSeriesCountry;
        carTypeCountry = workEntity.carTypeCountry;
    }

    /**
     * 初始化输入框，并赋值
     * @param view
     */
    private void displayView(AutoLossMainActivity context, View view) {
        EditText edT1 = view.findViewById(R.id.customPart_carFacturer);
        EditText edT2 = view.findViewById(R.id.customPart_carBrand);
        EditText edT3 = view.findViewById(R.id.customPart_carSeries);
        EditText edT4 = view.findViewById(R.id.customPart_carType);
        TextView cacelTv = view.findViewById(R.id.customPart_cacel);
        TextView submitTv = view.findViewById(R.id.customPart_submit);

        SetTextUtil.setEditText(edT1,workEntity.carFacturer);
        SetTextUtil.setEditText(edT2,workEntity.carBrand);
        SetTextUtil.setEditText(edT3,workEntity.carSeries);
        SetTextUtil.setEditText(edT4,workEntity.carType);

        //绑定确定按钮
        submitTv.setOnClickListener(v -> {
            boolean isfull = true;
            if (TextUtils.isEmpty(edT1.getText().toString())){
                edT1.setError("请填写内容!"); isfull = false;
            }
            if (TextUtils.isEmpty(edT2.getText().toString())){
                edT2.setError("请填写内容!"); isfull = false;
            }
            if (TextUtils.isEmpty(edT3.getText().toString())){
                edT3.setError("请填写内容!"); isfull = false;
            }
            if (TextUtils.isEmpty(edT4.getText().toString())){
                edT4.setError("请填写内容!"); isfull = false;
            }
            if (TextUtils.isEmpty(carFacturerCountry)){
                ToastUtil.showToastLong(context,"厂商类型未选择！"); isfull = false;
            }
            if (TextUtils.isEmpty(carBrandCountry)){
                ToastUtil.showToastLong(context,"品牌类型未选择！"); isfull = false;
            }
            if (TextUtils.isEmpty(carSeriesCountry)){
                ToastUtil.showToastLong(context,"车系类型未选择！"); isfull = false;
            }
            if (TextUtils.isEmpty(carTypeCountry)){
                ToastUtil.showToastLong(context,"车型类型未选择！"); isfull = false;
            }
            if (isfull){
                workEntity.carFacturer = edT1.getText().toString();
                workEntity.carBrand = edT2.getText().toString();
                workEntity.carSeries = edT3.getText().toString();
                workEntity.carType = edT4.getText().toString();

                workEntity.carFacturerCountry = carFacturerCountry;
                workEntity.carBrandCountry = carBrandCountry;
                workEntity.carSeriesCountry = carSeriesCountry;
                workEntity.carTypeCountry = carTypeCountry;
                context.dialog.dismiss();
            }
        });
        cacelTv.setOnClickListener(v1 -> context.dialog.dismiss());
    }

    /**
     * 初始化生产类型选择的按钮，并回显值，绑定点击事件
     * @param view
     * @param context
     */
    private void initView(View view,AutoLossMainActivity context) {
        RadioGroup rg1 = view.findViewById(R.id.customPart_carFacturer_RG);
        RadioGroup rg2 = view.findViewById(R.id.customPart_carBrand_RG);
        RadioGroup rg3 = view.findViewById(R.id.customPart_carSeries_RG);
        RadioGroup rg4 = view.findViewById(R.id.customPart_carType_RG);

        List<RadioButton> rbs1 = new ArrayList<>(4);
        List<RadioButton> rbs2 = new ArrayList<>(4);
        List<RadioButton> rbs3 = new ArrayList<>(4);
        List<RadioButton> rbs4 = new ArrayList<>(4);

        rg1.removeAllViews();
        rg2.removeAllViews();
        rg3.removeAllViews();
        rg4.removeAllViews();

        if (carPrincipalPart!=null){
            for (DictYjxEntity.publicData dicTem:carPrincipalPart){

                RadioButton rb1 = new RadioButton(context); //厂家生产类型
                rb1.setText(dicTem.getLabel());
                rg1.addView(rb1);
                if (workEntity.carFacturerCountry!=null && workEntity.carFacturerCountry.equals(dicTem.value))rg1.check(rb1.getId());
                rbs1.add(rb1);

                RadioButton rb2 = new RadioButton(context);  //品牌
                rb2.setText(dicTem.getLabel());
                rg2.addView(rb2);
                if (workEntity.carBrandCountry!=null && workEntity.carBrandCountry.equals(dicTem.value))rg2.check(rb2.getId());
                rbs2.add(rb2);

                RadioButton rb3 = new RadioButton(context); //车系
                rb3.setText(dicTem.getLabel());
                rg3.addView(rb3);
                if (workEntity.carSeriesCountry!=null && workEntity.carSeriesCountry.equals(dicTem.value))rg3.check(rb3.getId());
                rbs3.add(rb3);

                RadioButton rb4 = new RadioButton(context); //车型
                rb4.setText(dicTem.getLabel());
                rg4.addView(rb4);
                if (workEntity.carTypeCountry!=null && workEntity.carTypeCountry.equals(dicTem.value))rg4.check(rb4.getId());
                rbs4.add(rb4);
            }
        }

        //厂家生产类型
        rg1.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i=0;i<rbs1.size();i++){
                if (rbs1.get(i).getId() == checkedId){
                    carFacturerCountry = carPrincipalPart.get(i).value;
                }
            }
        });
        //品牌生产类型
        rg2.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i=0;i<rbs2.size();i++){
                if (rbs2.get(i).getId() == checkedId){
                    carBrandCountry = carPrincipalPart.get(i).value;
                }
            }
        });
        //车系生产类型
        rg3.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i=0;i<rbs3.size();i++){
                if (rbs3.get(i).getId() == checkedId){
                    carSeriesCountry = carPrincipalPart.get(i).value;
                }
            }
        });
        //车型生产类型
        rg4.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i=0;i<rbs4.size();i++){
                if (rbs4.get(i).getId() == checkedId){
                    carTypeCountry = carPrincipalPart.get(i).value;
                }
            }
        });
    }
}
