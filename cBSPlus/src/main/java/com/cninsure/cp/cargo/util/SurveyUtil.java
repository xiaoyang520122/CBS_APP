package com.cninsure.cp.cargo.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cargo.CargoWorkActivity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cargo.ContainerRecords;
import com.cninsure.cp.entity.cargo.SurveyRecordsEntity;
import com.cninsure.cp.entity.fc.WorkFile;
import com.cninsure.cp.entity.yjx.ImagePathUtil;
import com.cninsure.cp.fc.activity.SurveyActivityHelp;
import com.cninsure.cp.fragment.OrderNowFragment;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.OpenFileUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.http.DownLoadFileUtil;
import com.cninsure.cp.utils.url.URLEncodedUtil;
import com.cninsure.cp.view.ChildClickableLinearLayout;

import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :xy-wm
 * date:2020/12/10 12:02
 * usefuLness: CBS_APP
 */
public class SurveyUtil {
    private CargoWorkActivity context;
    private View surveyView,surveyNotView;
    private SurveyRecordsEntity sREn;
    public static final int FILE_SELECT_CODE=10002;

    private SurveyUtil (){}
    public SurveyUtil (CargoWorkActivity context,View surveyView,View surveyNotView,SurveyRecordsEntity sREn){
        this.context = context;
        this.sREn = sREn;
        this. surveyNotView = surveyNotView;
        this. surveyView = surveyView;
    }


    /**显示集装箱信息*/
    public void disPlayContainerInfo(String caseNo,String insured) {
        SetTextUtil.setTextViewText(surveyView.findViewById(R.id.CargoSR_caseNo), caseNo); //案件号
        SetTextUtil.setTextViewText(surveyView.findViewById(R.id.CargoSR_insured), insured); //被保险人
        if(sREn!=null && sREn.records!=null) {
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_consigneed), sREn.records.consigneed);//现场人姓名
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_consigneedPhone), sREn.records.consigneedPhone);//电话号码
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_carrierNo), sREn.records.carrierNo);//承运车辆车号
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_containerNo), sREn.records.containerNo);//集装箱号
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_sealNo), sREn.records.sealNo);//封条号
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_pickUpPort), sREn.records.pickUpPort);//提货港
           SetTextUtil.setTextViewText(surveyView.findViewById(R.id.CargoSR_pickUpTime), sREn.records.pickUpTime);//提货日期
           SetTextUtil.setTextViewText(surveyView.findViewById(R.id.CargoSR_riskTime), sREn.records.riskTime);//出险日期
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_riskAddress), sREn.records.riskAddress);//出险地点
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_freightName), sREn.records.freightName);//货物名称
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_freightPrice), sREn.records.freightPrice);//货物单价
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_packaging), sREn.records.packaging);//包装规格
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_transportCount), sREn.records.transportCount);//运输总件数
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_damageCount), sREn.records.damageCount);//受损数量
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_causeAndCourse), sREn.records.causeAndCourse);//事故发生原因及经过
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_comfirmFreightName), sREn.records.comfirmFreightName);//货物名称
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_comfirmFreightPrice), sREn.records.comfirmFreightPrice);//货主申报单价
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_comfirmPackage), sREn.records.comfirmPackage);//包装情况
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_comfirmSpecification), sREn.records.comfirmSpecification);//货物规格
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_comfirmTransportCount), sREn.records.comfirmTransportCount);//运载总数量
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_comfirmDamageCount), sREn.records.comfirmDamageCount);//受损数量
           SetTextUtil.setEditText(surveyView.findViewById(R.id.CargoSR_addedExplain), sREn.records.addedExplain);//受损数量
            displaySilverNitrateDetection(); //硝酸银检测/包装是否破损
            disPlaySign();
            disPlayDamageInfo();
//            setDowloadMouldOnclick(surveyView.findViewById(R.id.CargoSR_TemplateDownload)); //下载模板单击事件绑定
//            disPlayRecordDocUrlInfo();
        }
        DateChoiceUtil.setLongDatePickerDialogOnClick(context,surveyView.findViewById(R.id.CargoSR_pickUpTime));//提货日期
        DateChoiceUtil.setLongDatePickerDialogOnClick(context,surveyView.findViewById(R.id.CargoSR_riskTime)); //出险日期
    }


    /**
     * 查看报告的单击事件
     * @param view
     * @param recordDocUrl
     */
    private void setDownBaogaoOnclick(View view, String recordDocUrl) {
        if (TextUtils.isEmpty(recordDocUrl)) {
            ToastUtil.showToastLong(context,"无文件！");
            return;
        }
        String FilePath = ImagePathUtil.BaseUrl + recordDocUrl;
        view.setOnClickListener(v -> {
            new DownLoadFileUtil(context).startDownLoad(FilePath ,recordDocUrl,"下载出错!",null , filePath -> { //下载成功后可以分享文件。
                alertFilePath(filePath);
            });
        });
    }

    /**
     * 点击选择上传报告文件
     * @param view
     */
    private void setChioceFileOnclick(View view) {
        view.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                context.startActivityForResult( Intent.createChooser(intent, "选择报告！"), FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                DialogUtil.getAlertOneButton(context, "抱歉，无法打开文件管理器！您可以安装一个文件管理器再试一次。", null).show();
            }
        });
    }

    /**
     * 硝酸银检测\包装是否破损回显和监听
     */
    private void displaySilverNitrateDetection() {
        RadioGroup sndRgrp = surveyView.findViewById(R.id.CargoSR_silverNitrateDetection_RG);
        if (sREn.records.silverNitrateDetection!=null && sREn.records.silverNitrateDetection==0) sndRgrp.check(R.id.CargoSR_silverNitrateDetection_RBT);
        else sndRgrp.check(R.id.CargoSR_silverNitrateDetection_RBF);
        sndRgrp.setOnCheckedChangeListener((group, checkedId) -> { //硝酸银检测
            switch (checkedId){
                case R.id.CargoSR_silverNitrateDetection_RBT: sREn.records.silverNitrateDetection=0;break;
                case R.id.CargoSR_silverNitrateDetection_RBF:sREn.records.silverNitrateDetection=1;break;
            }
        });

        RadioGroup ibpRgrp = surveyView.findViewById(R.id.CargoSR_isBrokenPackge_RG);
        if (sREn.records.isBrokenPackge!=null && sREn.records.isBrokenPackge==0) ibpRgrp.check(R.id.CargoSR_isBrokenPackge_RBT);
        else ibpRgrp.check(R.id.CargoSR_isBrokenPackge_RBF);
        ibpRgrp.setOnCheckedChangeListener((group, checkedId) -> { //包装是否破损
            switch (checkedId){
                case R.id.CargoSR_isBrokenPackge_RBT: sREn.records.isBrokenPackge=0;break;
                case R.id.CargoSR_isBrokenPackge_RBF:sREn.records.isBrokenPackge=1;break;
            }
        });
    }

    /**
     * 显示 检查部位信息
     */
    public LinearLayout damageLiner;
    private void disPlayDamageInfo(){
        if (damageLiner==null) damageLiner = surveyView.findViewById(R.id.CargoSR_damageLinearLayout);
        damageLiner.removeAllViews();
        Map<String, String[]> damageMap = sREn.records.getDamageMap();
        for (String damageName:damageMap.keySet())
        damageLiner.addView(getDamageView(damageName,sREn.records.getDamageRemarkValue(damageName)));
    }

    /**
     * 获取对应的检查部位View
     * @param DamageName
     * @param forearmRemark
     * @return
     */
    @SuppressLint("WrongViewCast")
    private View getDamageView(String DamageName, String forearmRemark) {
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.cargo_survey_records_iscontainer_item,null);
        String values = sREn.records.getDamageValueByName(DamageName);
        if (!TextUtils.isEmpty(values)){
            SetTextUtil.setTextViewText(itemView.findViewById(R.id.cargo_item_inspection_results),sREn.records.getDamageLabelStr(DamageName,values)); //内容
        }
        SetTextUtil.setTextViewText(itemView.findViewById(R.id.cargo_item_examination_site),DamageName); //检查部位名称
        SetTextUtil.setEditText(itemView.findViewById(R.id.cargo_item_remarks),forearmRemark); //备注
        itemView.findViewById(R.id.cargo_item_inspection_results).setOnClickListener(v -> {
            showChoiceDialog(DamageName,v,sREn.records.getDamageValue(DamageName));  //弹框显示检查部位受损情况，并将确认后的值回显和储存到对象。
        });
        ((EditText)itemView.findViewById(R.id.cargo_item_remarks)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sREn.records.setDamageRemarkValue(DamageName,s.toString());  //实时监听并将备注信息回写到对象中
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        return itemView;
    }

    /**
     * 弹框显示内容
     * @param damageName
     * @param v
     * @param damageValue
     */
    private void showChoiceDialog(String damageName, View v, String[] damageValue) {
        Map<Integer,Integer> valueMap = new HashMap<>(5);
        boolean[] checkeditems = new boolean[damageValue.length];
        String valueSave = sREn.records.getDamageValueByName(damageName);
        for(int i=0;i<damageValue.length;i++){
            if (valueSave.indexOf(i+"")==-1){
                checkeditems[i] = false;
            }else {
                checkeditems[i] = true;
                valueMap.put(i,i);
            }
        }
        new AlertDialog.Builder(context).setTitle(damageName+"受损情况")
                .setMultiChoiceItems(damageValue, checkeditems, (dialog, which, isChecked) -> {
                    if (isChecked) valueMap.put(which,which);
                    else valueMap.remove(which);
                    if ((which+1) == damageValue.length && isChecked){  //如果选择完好，就去掉前面所有的选择。
                        for (int i=0;i<which;i++){ //选择的是最后一项（完好），这里是为了不去掉其勾选。
                            checkeditems[i] = false;
                            ((AlertDialog)dialog).getListView().setItemChecked(i,false);
                        }
                        valueMap.clear();
                        valueMap.put(which,which);
                    }else{  //如果如果选择了非“完好”则需要将“完好”内容移除。
                        valueMap.remove(damageValue.length-1);
                        checkeditems[damageValue.length-1] = false;
                        ((AlertDialog)dialog).getListView().setItemChecked(damageValue.length-1,false);
                    }
                }).setNeutralButton("确定", (dialog, which) -> {  //确定后回显界面数据，设置对象值
                    String values = "";
                    String labels = "";
                    for (Integer tempInt:valueMap.keySet()){
                        if (TextUtils.isEmpty(labels)){
                            values = ""+tempInt;
                            labels = damageValue[tempInt];
                        }else{
                            values = values+","+tempInt;
                            labels = labels+","+damageValue[tempInt];
                        }
                    }
                    SetTextUtil.setTextViewText((TextView) v,labels);
                    sREn.records.setDamageValues(damageName,values);
                }).setNegativeButton("取消",null)
                .create().show();
    }

    public void disPlayNotContainerInfo(String insured,String riskTime) {
        SetTextUtil.setTextViewText(surveyNotView.findViewById(R.id.CargoSRN_insured), insured); //被保险人
        SetTextUtil.setTextViewText(surveyNotView.findViewById(R.id.CargoSRN_riskTime), riskTime); //出险时间
        if(sREn!=null && sREn.records!=null) {
            SetTextUtil.setEditText(surveyNotView.findViewById(R.id.CargoSRN_caseName), sREn.records.caseName); //案件名称
        SetTextUtil.setTextViewText(surveyNotView.findViewById(R.id.CargoSRN_ckTime), sREn.records.ckTime); //查勘日期
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.CargoSRN_surveyAddress), sREn.records.surveyAddress); //收货人
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_insurerContact), sREn.records.insurerContact); //保险人代表
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_insurerContactPhone), sREn.records.insurerContactPhone); //保险人代表电话
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_insuredContact), sREn.records.insuredContact); //被保险人代表
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_insuredContactPhone), sREn.records.insuredContactPhone); //被保险人代表电话
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_ggsContact), sREn.records.ggsContact); //公估方代表
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.cti_ggsContactPhone), sREn.records.ggsContactPhone); //公估方代表电话
        SetTextUtil.setEditText(surveyNotView.findViewById(R.id.CargoSRN_causeAndCourse), sREn.records.causeAndCourse); //查勘内容
        Glide.with(context).load(sREn.records.signatureUrl).into((ImageView) surveyNotView.findViewById(R.id.CargoSRN_signatureUrl)); //签字图片
        disPlaySign();
//        setDowloadMouldOnclick(surveyNotView.findViewById(R.id.CargoSR_TemplateDownloadNOt));//下载模板单击事件绑定
        }
        DateChoiceUtil.setLongDatePickerDialogOnClick(context,surveyNotView.findViewById(R.id.CargoSRN_ckTime));
//        disPlayRecordDocUrlInfo();
    }

    public SurveyRecordsEntity reflashData(){
        if (sREn==null) return null;
        if(sREn.records ==null) sREn.records = new ContainerRecords();
        if (sREn.ckDocType.equals("0")) getContainerInfo();  //刷新界面集装箱信息到实体类
        if (sREn.ckDocType.equals("1")) getNotContainerInfo();  //刷新界面集装箱信息到实体类
        return sREn;
    }

    /**获取集装箱数据*/
    private void getContainerInfo() {
        sREn.records.caseNo = ((TextView)(surveyView.findViewById(R.id.CargoSR_caseNo))).getText().toString(); //案件号
        sREn.records.insured = ((TextView)(surveyView.findViewById(R.id.CargoSR_insured))).getText().toString(); //被保险人
        sREn.records.consigneed = ((EditText)(surveyView.findViewById(R.id.CargoSR_consigneed))).getText().toString();//现场人姓名
        sREn.records.consigneedPhone = ((EditText)(surveyView.findViewById(R.id.CargoSR_consigneedPhone))).getText().toString();//电话号码
        sREn.records.carrierNo = ((EditText)(surveyView.findViewById(R.id.CargoSR_carrierNo))).getText().toString();//承运车辆车号
        sREn.records.containerNo = ((EditText)(surveyView.findViewById(R.id.CargoSR_containerNo))).getText().toString();//集装箱号
        sREn.records.sealNo = ((EditText)(surveyView.findViewById(R.id.CargoSR_sealNo))).getText().toString();//封条号
        sREn.records.pickUpPort = ((EditText)(surveyView.findViewById(R.id.CargoSR_pickUpPort))).getText().toString();//提货港
        sREn.records.pickUpTime = ((TextView)(surveyView.findViewById(R.id.CargoSR_pickUpTime))).getText().toString();//提货日期
        sREn.records.riskTime = ((TextView)(surveyView.findViewById(R.id.CargoSR_riskTime))).getText().toString();//出险日期
        sREn.records.riskAddress = ((EditText)(surveyView.findViewById(R.id.CargoSR_riskAddress))).getText().toString();//出险地点
        sREn.records.freightName = ((EditText)(surveyView.findViewById(R.id.CargoSR_freightName))).getText().toString();//货物名称
        sREn.records.freightPrice = ((EditText)(surveyView.findViewById(R.id.CargoSR_freightPrice))).getText().toString();//货物单价
        sREn.records.packaging = ((EditText)(surveyView.findViewById(R.id.CargoSR_packaging))).getText().toString();//包装规格
        sREn.records.transportCount = ((EditText)(surveyView.findViewById(R.id.CargoSR_transportCount))).getText().toString();//运输总件数
        sREn.records.damageCount = ((EditText)(surveyView.findViewById(R.id.CargoSR_damageCount))).getText().toString();//受损数量
        sREn.records.causeAndCourse = ((EditText)(surveyView.findViewById(R.id.CargoSR_causeAndCourse))).getText().toString();//事故发生原因及经过
        sREn.records.comfirmFreightName = ((EditText)(surveyView.findViewById(R.id.CargoSR_comfirmFreightName))).getText().toString();//货物名称
        sREn.records.comfirmFreightPrice = ((EditText)(surveyView.findViewById(R.id.CargoSR_comfirmFreightPrice))).getText().toString();//货主申报单价
        sREn.records.comfirmPackage = ((EditText)(surveyView.findViewById(R.id.CargoSR_comfirmPackage))).getText().toString();//包装情况
        sREn.records.comfirmSpecification = ((EditText)(surveyView.findViewById(R.id.CargoSR_comfirmSpecification))).getText().toString();//货物规格
        sREn.records.comfirmTransportCount = ((EditText)(surveyView.findViewById(R.id.CargoSR_comfirmTransportCount))).getText().toString();//运载总数量
        sREn.records.comfirmDamageCount = ((EditText)(surveyView.findViewById(R.id.CargoSR_comfirmDamageCount))).getText().toString();//受损数量
        sREn.records.addedExplain = ((EditText)(surveyView.findViewById(R.id.CargoSR_addedExplain))).getText().toString();//受损数量
//        Glide.with(context).load(sREn.records.signatureUrl).into((ImageView) surveyView.findViewById(R.id.CargoSR_signatureUrl)); //签字图片
    }

    /**获取非集装箱数据*/
    private void getNotContainerInfo() {
        sREn.records.caseName = ((EditText)(surveyNotView.findViewById(R.id.CargoSRN_caseName))).getText().toString(); //案件名称
        sREn.records.insured = ((TextView)(surveyNotView.findViewById(R.id.CargoSRN_insured))).getText().toString(); //被保险人
        sREn.records.riskTime = ((TextView)(surveyNotView.findViewById(R.id.CargoSRN_riskTime))).getText().toString(); //出险时间
        sREn.records.ckTime = ((TextView)(surveyNotView.findViewById(R.id.CargoSRN_ckTime))).getText().toString(); //查勘日期
        sREn.records.surveyAddress = ((EditText)(surveyNotView.findViewById(R.id.CargoSRN_surveyAddress))).getText().toString(); //收货人
        sREn.records.insurerContact = ((EditText)(surveyNotView.findViewById(R.id.cti_insurerContact))).getText().toString(); //保险人代表
        sREn.records.insurerContactPhone = ((EditText)(surveyNotView.findViewById(R.id.cti_insurerContactPhone))).getText().toString(); //保险人代表电话
        sREn.records.insuredContact = ((EditText)(surveyNotView.findViewById(R.id.cti_insuredContact))).getText().toString(); //被保险人代表
        sREn.records.insuredContactPhone = ((EditText)(surveyNotView.findViewById(R.id.cti_insuredContactPhone))).getText().toString(); //被保险人代表电话
        sREn.records.ggsContact = ((EditText)(surveyNotView.findViewById(R.id.cti_ggsContact))).getText().toString(); //公估方代表
        sREn.records.ggsContactPhone = ((EditText)(surveyNotView.findViewById(R.id.cti_ggsContactPhone))).getText().toString(); //公估方代表电话
        sREn.records.causeAndCourse = ((EditText)(surveyNotView.findViewById(R.id.CargoSRN_causeAndCourse))).getText().toString(); //查勘内容
//        Glide.with(context).load(sREn.records.signatureUrl).into((ImageView) surveyNotView.findViewById(R.id.CargoSRN_signatureUrl)); //签字图片
    }

    public void disPlaySign() {
        if(sREn.records ==null) sREn.records = new ContainerRecords();
        String signPath= sREn.records.signatureUrl;
        String ggsSignPath= sREn.records.ckGgsUrl;
        if (!TextUtils.isEmpty(signPath) && sREn.ckDocType.equals("0")){
                Glide.with(context).load(AppApplication.getUSER().data.qiniuUrl+signPath).into(((ImageView)(surveyView.findViewById(R.id.CargoSR_signatureUrl_img))));   //刷新界面集装箱信息收货人/代理人
                (surveyView.findViewById(R.id.CargoSR_signatureUrl_img)).setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(ggsSignPath) && sREn.ckDocType.equals("0")){
            ggsSignPath = AppApplication.getUSER().data.qiniuUrl+ggsSignPath;
                Glide.with(context).load(ggsSignPath).into(((ImageView)(surveyView.findViewById(R.id.CargoSR_ckGgsUrl_img)))); //刷新界面集装箱现场查勘人
                (surveyView.findViewById(R.id.CargoSR_ckGgsUrl_img)).setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(ggsSignPath) && sREn.ckDocType.equals("1"))
            Glide.with(context).load(AppApplication.getUSER().data.qiniuUrl+ggsSignPath).into(((ImageView)(surveyNotView.findViewById(R.id.CargoSRN_signatureUrl)))); ;  //刷新界面非集装箱信息到实体类
        (surveyNotView.findViewById(R.id.CargoSRN_signatureUrl)).setVisibility(View.VISIBLE);
    }

    public void setDowloadMouldOnclick(View v){ //CargoSR_TemplateDownload
        String[] nameArr = new String[]{"查勘记录(非集装箱).doc","查勘记录(集装箱).doc","清点记录.doc","询问笔录.doc"};
        Dialog loadDialog = LoadDialogUtil.getMessageDialog(context,"下载中……");
        v.setOnClickListener(v1 -> {
            new AlertDialog.Builder(context).setTitle("选择下载模板")
                    .setItems(nameArr, (dialog, which) -> {
                        String FilePath = URLs.CARGO_TEMPLATE+ URLEncodedUtil.toURLEncoded(nameArr[which]);
                        String incod = URLEncoder.encode(FilePath);
                        new DownLoadFileUtil(context).startDownLoad(FilePath ,nameArr[which],"下载出错!",loadDialog , filePath -> { //下载成功后可以分享文件。
                            alertFilePath(filePath);
                        });
                    }).create().show();
        });
    }

    private void alertFilePath(String filePath){
        String simplePath = null;
        if (filePath.indexOf("/0/")!=-1) {
            simplePath = filePath.substring(filePath.indexOf("/0/")+3);
        }
        new AlertDialog.Builder(context).setTitle("下载提示!")
                .setMessage("下载成功! 文件路径："+ (simplePath==null?filePath:simplePath))
                .setNeutralButton("打开文件", (dialog, which) -> OpenFileUtil.openFileByPath(context,filePath)).create().show();
    }
}
