package com.cninsure.cp.cx.autoloss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.autoloss.entity.CategoryTable;
import com.cninsure.cp.cx.autoloss.entity.ModelTable;
import com.cninsure.cp.entity.DictEntity;
import com.cninsure.cp.entity.DictYjxEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDsWorkEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.AutoLinefeedLayout;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zcw.togglebutton.ToggleButton;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 @author :xy-wm
  * date:2021/5/24 10:22
  * usefuLness: CBS_APP
 */
public class AutoLossMainActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.autoLossMain_insuredPerson) private TextView insuredPerson; //被保险人
    @ViewInject(R.id.autoLossMain_riskDate) private TextView riskDate; //出险时间
    @ViewInject(R.id.autoLossMain_dsCarNumber) private TextView dsCarNumber; //出险车牌
    @ViewInject(R.id.autoLossMain_carnoType) private TextView carnoType; //号牌种类。
    @ViewInject(R.id.autoLossMain_carFacturer) private TextView carFacturer; //厂家
    @ViewInject(R.id.autoLossMain_carFacturerToggleButton) private ToggleButton carFacturerToggleButton; //厂家是否自定义按钮
    @ViewInject(R.id.autoLossMain_carBrand) private TextView carBrand; //品牌
    @ViewInject(R.id.autoLossMain_carBrandToggleButton) ToggleButton carBrandToggleButton; //品牌是否自定义
    @ViewInject(R.id.autoLossMain_carSeries) private TextView carSeries; //车系
    @ViewInject(R.id.autoLossMain_carSeriesToggleButton) private ToggleButton carSeriesToggleButton; //车系是否自定义
    @ViewInject(R.id.autoLossMain_carType) private TextView carType; //车型
    @ViewInject(R.id.autoLossMain_carTypeToggleButton) private ToggleButton carTypeToggleButton; //车型是否自定义
    @ViewInject(R.id.autoLossMain_dsServiceDepot) private EditText dsServiceDepot; //维修厂
    @ViewInject(R.id.autoLossMain_dsAptitudes_RG) private RadioGroup dsAptitudes_RG; //维修厂类型
    @ViewInject(R.id.autoLossMain_carStructure) private TextView carStructure; //车辆结构
    @ViewInject(R.id.autoLossMain_agreementAutoLine) private AutoLinefeedLayout agreementAutoLine; //损失部位
    @ViewInject(R.id.autoLossMain_replaceInfos) private LinearLayout replaceInfos; //换件项目
    @ViewInject(R.id.autoLossMain_HjTotal) private TextView HjTotal; //换件项目金额
    @ViewInject(R.id.autoLossMain_HJ_size) private TextView HJ_size; //换件项目个数
    @ViewInject(R.id.autoLossMain_timeInfos) private LinearLayout timeInfos; //工时信息
    @ViewInject(R.id.autoLossMain_GsTotal) private TextView GsTotal; //工时金额合计
    @ViewInject(R.id.autoLossMain_timeInfos_size) private TextView timeInfos_size; //工时项目个数
    @ViewInject(R.id.autoLossMain_repairInfos) private LinearLayout repairInfos; //外修项目
    @ViewInject(R.id.autoLossMain_WxTotal) private TextView WxTotal; //外修项目金额合计
    @ViewInject(R.id.autoLossMain_Wx_size) private TextView Wx_size; //外修项目个数
    @ViewInject(R.id.autoLossMain_Total) private TextView Total; //定损金额合计
    @ViewInject(R.id.autoLossMain_submit) private TextView submit; //确定提交按钮

    private CxDsWorkEntity workEntity; //智能定损信息。
    private DictYjxEntity dictEntity; //字典值

    public CategoryTable FacturerTable,BrandTable,SeriesTable; //厂家、品牌、车系表
    public ModelTable carTypeTable; //车型
    private boolean b1 = false, b2 = false, b3 = false, b4 = false; //厂家，品牌，车系，车型是否自定义的标识。
    public Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_loss_main_activity);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        getCxDsWorkEntity();
    }

    private void getCxDsWorkEntity() {
        workEntity = (CxDsWorkEntity) getIntent().getSerializableExtra("CxDsWorkEntity");
        downloadDict();
    }


    /**获取字典值*/
    private void downloadDict() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> paramlist = new ArrayList<String>();
        paramlist.add("userId");
        paramlist.add(AppApplication.USER.data.userId+"");
        paramlist.add("type");
        paramlist.add("carStructure,ds_aptitudes,lossPosition,carno_type,car_principal_part");
        HttpUtils.requestGet(URLs.YJXNEW_GET_DICT, paramlist, HttpRequestTool.GET_DICT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnet(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.GET_DICT:
                LoadDialogUtil.dismissDialog();
                getDictInfo(responsecode,values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    /**
     * 选择界面返回数据类型：1：厂家；2：品牌；3：车系
     * @param values
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnet(Map<Integer, CategoryTable> values) {
        int responsecode = 0;
        CategoryTable cateTable= null;
        for (Integer cateLevel :values.keySet()){
            responsecode = cateLevel;
            cateTable = values.get(cateLevel);
        }
        switch (responsecode) {
            case 1: //厂家
                if (cateTable!=null){
                    workEntity.carFacturerId = cateTable.cateId+"";
                    workEntity.carFacturer = cateTable.cateName;
                    workEntity.carFacturerCountry = cateTable.cateCountry;
                    SetTextUtil.setTextViewText(carFacturer,cateTable.cateName);
                }
                break;
            case 2: //品牌
                if (cateTable!=null){
                    workEntity.carBrandId = cateTable.cateId+"";
                    workEntity.carBrand = cateTable.cateName;
                    workEntity.carBrandCountry = cateTable.cateCountry;
                    SetTextUtil.setTextViewText(carBrand,cateTable.cateName);
                }
                break;
            case 3: //车系
                if (cateTable!=null){
                    workEntity.carSeriesId = cateTable.cateId+"";
                    workEntity.carSeries = cateTable.cateName;
                    workEntity.carSeriesCountry = cateTable.cateCountry;
                    SetTextUtil.setTextViewText(carSeries,cateTable.cateName);
                }
                break;
            default:
                break;
        }
    }
    /**
     * 选择界面返回数据类型：1：厂家；2：品牌；3：车系
     * @param values
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnetModel(Map<Integer, ModelTable> values) {
        int responsecode = 0;
        ModelTable cateTable= null;
        for (Integer cateLevel :values.keySet()){
            responsecode = cateLevel;
            cateTable = values.get(cateLevel);
        }
        switch (responsecode) {
            case 4: //车型
                if (cateTable!=null){
                    workEntity.carTypeId = cateTable.modelId+"";
                    workEntity.carType = cateTable.modelStandardName;
                    workEntity.carTypeCountry = cateTable.modelCountry;
                    SetTextUtil.setTextViewText(carType,cateTable.modelStandardName);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 解析下载的字典值。
     * @param values
     */
    /**获取 申请人与被保人关系字典值*/
    private void getDictInfo(int conncode, String value) { //relationshipDict
            try {
                dictEntity = JSON.parseObject(value, DictYjxEntity.class);
                displayDsWorkInfo(); //显示信息
            } catch (Exception e) {
                ToastUtil.showToastLong(this, "获取字典信息失败！");
                AutoLossMainActivity.this.finish();
                e.printStackTrace();
            }
    }

    /**
     * 绑定字典值，并显示定损信息。
     */
    private void displayDsWorkInfo() {
        addDsAptitudes(); //添加维修厂类型选项
        setLossPosition(); //添加损失部位类型选项
        setCarStructure(); //设置车辆结构可选项目
        setToggleButtonOnclick(); //设置是否自定义点击事件


        SetTextUtil.setTextViewText(carnoType, dictEntity.getLableByValue(dictEntity.carno_type, workEntity.carnoType)); //号牌种类

        SetTextUtil.setTextViewText(insuredPerson, workEntity.insuredPerson); //被保险人
        SetTextUtil.setTextViewText(riskDate, workEntity.riskDate); //出险时间
        SetTextUtil.setTextViewText(dsCarNumber, workEntity.dsCarNumber); //出险车牌
        SetTextUtil.setTextViewText(carnoType, workEntity.carnoType); //号牌种类。
        SetTextUtil.setTextViewText(carFacturer, workEntity.carFacturer); //厂家
        carFacturer.setOnClickListener(this);//厂家
        SetTextUtil.setTextViewText(carBrand, workEntity.carBrand); //品牌
        carBrand.setOnClickListener(this);//品牌
        SetTextUtil.setTextViewText(carSeries, workEntity.carSeries); //车系
        carSeries.setOnClickListener(this);//车系
        SetTextUtil.setTextViewText(carType, workEntity.carType); //车型
        carType.setOnClickListener(this);//车型
        SetTextUtil.setTextViewText(dsServiceDepot, workEntity.dsServiceDepot); //维修厂
//        SetTextUtil.setTextViewText(carStructure) ; //车辆结构
        replaceInfos.setOnClickListener(this); //跳转到换件项目选择界面
        SetTextUtil.setTextViewText(HjTotal, workEntity.getHjTotal()); //换件项目金额
        SetTextUtil.setTextViewText(HJ_size, (workEntity.replaceInfos == null ? "0" : workEntity.replaceInfos.size() + "")); //换件项目个数
        timeInfos.setOnClickListener(this); //跳转到工时信息选择界面
        SetTextUtil.setTextViewText(GsTotal, workEntity.getGsTotal()); //工时金额合计
        SetTextUtil.setTextViewText(timeInfos_size, (workEntity.timeInfos==null ? "0" :workEntity.timeInfos.size()+"" )); //工时项目个数
        repairInfos.setOnClickListener(this); //跳转到外修项目选择界面
        SetTextUtil.setTextViewText(WxTotal, workEntity.getWxTotal()); //外修项目金额合计
        SetTextUtil.setTextViewText(Wx_size, workEntity.repairInfos==null ? "0" : workEntity.repairInfos.size()+""); //外修项目个数
        SetTextUtil.setTextViewText(Total, "总计："+workEntity.getHjTotal()+workEntity.getGsTotal()+workEntity.getWxTotal()+""); //定损金额合计
        submit.setOnClickListener(this); //确定提交按钮
    }

    /**
     * //设置是否自定义点击事件
     */
    private void setToggleButtonOnclick() {
        //厂家是否自定义按钮
        carFacturerToggleButton.setOnToggleChanged(on -> {
            b1 = on;
            workEntity.carFacturerId = "";//清空
            workEntity.carFacturer = "";//清空
            workEntity.carFacturerCountry = "";//清空
            if (on){ //上级自定义，下级必定是自定义
                carBrandToggleButton.setToggleOn(true);
                carBrandToggleButton.setEnabled(false);carBrandToggleButton.setClickable(false);

                carSeriesToggleButton.setToggleOn(true);
                carSeriesToggleButton.setEnabled(false); carSeriesToggleButton.setClickable(false);

                carTypeToggleButton.setToggleOn(true);
                carTypeToggleButton.setEnabled(false);carTypeToggleButton.setClickable(false);
                b2=b3=b4=true;
            }else{
                carBrandToggleButton.setEnabled(true);carBrandToggleButton.setClickable(true);
                carSeriesToggleButton.setEnabled(true); carSeriesToggleButton.setClickable(true);
                carTypeToggleButton.setEnabled(true);carTypeToggleButton.setClickable(true);
            }
            displayDsWorkInfo();
        });
        //品牌是否自定义
        carBrandToggleButton.setOnToggleChanged(on -> {
            b2 = on;
            workEntity.carBrandId = "";//清空
            workEntity.carBrand = "";//清空
            workEntity.carBrandCountry = "";//清空
            if (on){ //上级自定义，下级必定是自定义
                carSeriesToggleButton.setToggleOn(true);
                carSeriesToggleButton.setEnabled(false); carSeriesToggleButton.setClickable(false);

                carTypeToggleButton.setToggleOn(true);
                carTypeToggleButton.setEnabled(false);carTypeToggleButton.setClickable(false);
                b3=b4=true;
            }else{
                carSeriesToggleButton.setEnabled(true); carSeriesToggleButton.setClickable(true);
                carTypeToggleButton.setEnabled(true);carTypeToggleButton.setClickable(true);
            }
            displayDsWorkInfo();
        });
        //车系是否自定义
        carSeriesToggleButton.setOnToggleChanged(on -> {
            b3 = on;
            workEntity.carSeriesId = "";//清空
            workEntity.carSeries = "";//清空
            workEntity.carSeriesCountry = "";//清空
            if (on){ //上级自定义，下级必定是自定义
                carTypeToggleButton.setToggleOn(true);
                carTypeToggleButton.setEnabled(false);carTypeToggleButton.setClickable(false);
                b4=true;
            }else{
                carTypeToggleButton.setEnabled(true);carTypeToggleButton.setClickable(true);
            }
            displayDsWorkInfo();
        });
        //车型是否自定义
        carTypeToggleButton.setOnToggleChanged(on -> {
            b4 = on;
            workEntity.carTypeId = "";//清空
            workEntity.carType = "";//清空
            workEntity.carTypeCountry = "";//清空
            displayDsWorkInfo();
        });
    }

    /**
     * 设置车辆结构可选项目
     */
    private void setCarStructure() {
        if (dictEntity.carStructure!=null){
            String[] tempArr = dictEntity.getLabletArr(dictEntity.carStructure);
            dictEntity.getDictArr(dictEntity.carStructure);
            Dialog dialog = new AlertDialog.Builder(this).setTitle("选择车辆结构")
                    .setItems(tempArr, (dialog1, which) -> {
                        workEntity.carStructure = dictEntity.getValueByLable(dictEntity.carStructure,tempArr[which]);
                        SetTextUtil.setTextViewText(carStructure,tempArr[which]);
                    }).setNeutralButton("取消",null).create();
            carStructure.setOnClickListener(v -> dialog.show());
        }
    }

    /**
     * 损失部位类型
     */
    private void setLossPosition() {
        if (workEntity.lossPosition == null) workEntity.lossPosition = new ArrayList<>();
        agreementAutoLine.removeAllViews();
        if (dictEntity.lossPosition!=null){
            for (DictYjxEntity.publicData dict : dictEntity.lossPosition){
                CheckBox cb = new CheckBox(this);
                cb.setText(dict.getLabel());
                //如果已是选中值则显示选中状态。
                if (workEntity.lossPosition!=null){
                    for (String valueStr:workEntity.lossPosition){
                        if (!TextUtils.isEmpty(valueStr) && valueStr.equals(dict.value)){
                            cb.setChecked(true);
                            break;
                        }
                    }
                }
                agreementAutoLine.addView(cb);
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    //损失部位类型添加或删除
                    if (isChecked){
                        workEntity.lossPosition.add(dictEntity.getValueByLable(dictEntity.lossPosition,cb.getText().toString()));
                    }else{
                        workEntity.lossPosition.remove(dictEntity.getValueByLable(dictEntity.lossPosition,cb.getText().toString()));
                    }
                });
            }
        }
    }


    /**
     * 添加维修厂类型选项
     */
    private void addDsAptitudes() {
        List<RadioButton> dsAptitudesBts = new ArrayList<>();
        dsAptitudes_RG.removeAllViews();
        for (DictYjxEntity.publicData dict: dictEntity.ds_aptitudes){
            if (dict!=null){
                RadioButton rb = new RadioButton(this);
                rb.setText(dict.getLabel());
                dsAptitudesBts.add(rb);
                dsAptitudes_RG.addView(rb);
                if (workEntity.dsAptitudes!=null && workEntity.dsAptitudes.equals(dict.value)) dsAptitudes_RG.check(rb.getId());
            }
        }
        //选择维修厂类型后赋值到实体类
        dsAptitudes_RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                workEntity.dsAptitudes = getDsAptitudesValue(dsAptitudesBts,checkedId);
            }
        });

    }

    /**
     * 获取选择维修厂类型的value值
     * @param dsAptitudesBts
     * @param checkedId
     * @return
     */
    public String getDsAptitudesValue( List<RadioButton> dsAptitudesBts, int checkedId){
        for (RadioButton rb:dsAptitudesBts){
            if (checkedId == rb.getId()){
                return dictEntity.getValueByLable(dictEntity.ds_aptitudes,rb.getText().toString());
            }
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.autoLossMain_replaceInfos: //换件项目
                addPart();
                break;
            case R.id.autoLossMain_timeInfos: //工时信息

                break;
            case R.id.autoLossMain_repairInfos: //外修项目

                break;

            case R.id.autoLossMain_carFacturer: //厂家--跳转到选择厂家的界面
                if (isCustomPart(1)) return; //自定义配件不用条状到选择界面
                Intent intent = new Intent(this, ChoiceFactoryActivity.class);
                intent.putExtra("searchCateLevel", 1);
                intent.putExtra("cateParentId", "1");
                startActivity(intent);
                break;

            case R.id.autoLossMain_carBrand: //品牌--跳转到选择品牌的界面
                if (isCustomPart(2)) return; //自定义配件不用条状到选择界面
                Intent intent2 = new Intent(this, ChoiceFactoryActivity.class);
                intent2.putExtra("searchCateLevel", 2);
                intent2.putExtra("cateParentId", workEntity.carFacturerId);
                startActivity(intent2);
                break;

            case R.id.autoLossMain_carSeries: //车系--跳转到选择车系的界面
                if (isCustomPart(3)) return; //自定义配件不用条状到选择界面
                Intent intent3 = new Intent(this, ChoiceFactoryActivity.class);
                intent3.putExtra("searchCateLevel", 3);
                intent3.putExtra("cateParentId", workEntity.carBrandId);
                startActivity(intent3);
                break;

            case R.id.autoLossMain_carType: //车型--跳转到选择车型的界面
                if (isCustomPart(4)) return; //自定义配件不用条状到选择界面
                Intent intent4 = new Intent(this, ChoiceFactoryActivity.class);
                intent4.putExtra("searchCateLevel", 4);
                intent4.putExtra("cateParentId", workEntity.carSeriesId);
                startActivity(intent4);
                break;

            case R.id.autoLossMain_submit: //确认

                break;
        }
    }

    private void addPart() {
        Intent intent = new Intent(this, AddPartInfoActivity.class);
        intent.putExtra("searchCateLevel", 1);
        startActivity(intent);
    }

    /**
     * 1：厂家；2：品牌；3：车系；4:车型
     * 如果是自定义，则弹框显示，如果不是就跳转到界面去查询选择
     * @param cateLevel
     * @return
     */
    public boolean isCustomPart(int cateLevel){
        boolean bValue = false;
        switch (cateLevel){
            //厂家--选择，如果是自定义，则弹框显示，如果不是就跳转到界面去查询选择
            case 1: bValue = b1; break;
            case 2: bValue = b2; break;
            case 3: bValue = b3; break;
            case 4: bValue = b4; break;
        }
        if (bValue){
            showCustomDialog(); //弹框显示自定义配件设置框
            return true;
        }
        return false;
    }

    private void showCustomDialog() {
        dialog = new AlertDialog.Builder(this)
                .setView(new CustomPartTool().getView(this,workEntity,dictEntity.car_principal_part))
                .create();
        dialog.setOnDismissListener(dialog1 -> displayDsWorkInfo());
        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
