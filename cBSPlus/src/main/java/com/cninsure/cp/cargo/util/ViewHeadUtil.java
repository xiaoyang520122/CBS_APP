package com.cninsure.cp.cargo.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cargo.ContainerRecords;
import com.cninsure.cp.entity.cargo.ListRecordsEntity;
import com.cninsure.cp.entity.cargo.QandAEntity;
import com.cninsure.cp.entity.cargo.SurveyAskRecordsEntity;
import com.cninsure.cp.entity.cargo.SurveyListRecordsEntity;
import com.cninsure.cp.entity.cargo.SurveyRecordsEntity;
import com.cninsure.cp.entity.yjx.ImagePathUtil;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.OpenFileUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.TimeHelper;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.http.DownLoadFileUtil;
import com.cninsure.cp.utils.url.URLEncodedUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :xy-wm
 * date:2021/1/27 10:10
 * usefuLness: CBS_APP
 */
public class ViewHeadUtil {

    private Activity context;
    public static final int FILE_SELECT_CODE=10002;
    public static final int FILE_SELECT_CODE_RA =10003;
    public static final int FILE_SELECT_CODE_LR=10004;
    private LinearLayout QALinear,LRLinear;
    private List<View> QAViews,LRViews;
    private LayoutInflater inflater;

    private ViewHeadUtil() {
    }

    public ViewHeadUtil(Activity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 初始化控件，加载查勘记录，询问笔录和清点记录view（此时没有下载作业数据，都加载空数据，默认线上填写内容）
     *
     * @param addView 默认显示的线上填写View
     * @return
     */
    public View getHeadView(View addView,int code) {
        View view = LayoutInflater.from(context).inflate(R.layout.cargo_survey_head_view, null);
        ((LinearLayout) view.findViewById(R.id.CargoSR_OnLineView)).removeAllViews();
        if (addView != null)
            ((LinearLayout) view.findViewById(R.id.CargoSR_OnLineView)).addView(addView);
        setOnOrOffOnlineCheck(view);
        setDowloadMouldOnclick(view.findViewById(R.id.CargoSR_TemplateDownloadNOt));//下载模板单击事件绑定
        setDateOnclick(view,code);
        return view;
    }

    /**
     * 设置日期点击选择
     * @param view
     * @param code
     */
    private void setDateOnclick(View view, int code) {
        switch (code) {
            case 2:
                DateChoiceUtil.setShortDatePickerDialog(context, view.findViewById(R.id.cargoSA_askDate));
                TimeHelper.setTimePickerDialog(context, view.findViewById(R.id.cargoSA_start_askTimeRange));
                TimeHelper.setTimePickerDialog(context, view.findViewById(R.id.cargoSA_end_askTimeRange));
                disPlayQAInfo(view,null); //加载一个空的询问笔记
                break;
            case 3:
                DateChoiceUtil.setShortDatePickerDialog(context, view.findViewById(R.id.cargoSLR_inventoryDate));
                disPlayLRInfo(view,null); //加载一个空的清单记录
                break;
        }
    }

    /**
     * 显示清点清单记录
     * @param view
     * @param LrEn
     */
    private void disPlayLRInfo(View view, SurveyListRecordsEntity LrEn) {
        if (LRLinear==null) LRLinear = view.findViewById(R.id.cargoSLR_listRecords_parent);
        LRLinear.removeAllViews();
        if (LRViews==null) LRViews = new ArrayList<>();
        LRViews.clear();
        if (LrEn!=null && LrEn.listRecords!=null){
            for (int i=0;i<LrEn.listRecords.size();i++){
                ListRecordsEntity rdEn = LrEn.listRecords.get(i);
                LRViews.add(getListRecordItem(rdEn,i+1,LrEn));
                LRLinear.addView(LRViews.get(i));
            }
        }else {
            //回显一个空的清单记录
            ListRecordsEntity rdEn = new ListRecordsEntity();
            LRViews.add(getListRecordItem(rdEn,1,LrEn));
            LRLinear.addView(LRViews.get(0));
        }
        setAddOnclick(view,LrEn);
    }

    /**
     * 谈价一个清单记录的点击监听事件
     */
    private void setAddOnclick(View view, SurveyListRecordsEntity lrEn) {
        view.findViewById(R.id.cargoSLR_listRecords_add).setOnClickListener(v -> {
            //增加一个空的清单记录
            ListRecordsEntity rdEn = new ListRecordsEntity();
            LRViews.add(getListRecordItem(rdEn,LRViews.size(),lrEn));
            LRLinear.addView(LRViews.get(LRViews.size()-1));
            setDeleteButtonVisibleAndVisible();
        });
    }

    /**
     * 显示调查对象item
     * @param rdEn
     * @param i
     * @param lrEn
     * @return
     */
    private View getListRecordItem(ListRecordsEntity rdEn, int i, SurveyListRecordsEntity lrEn) {
        View view = inflater.inflate(R.layout.cargo_survey_list_record_item, null);
        SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSLRI_listNumber), +i + "");
        if (rdEn != null) {
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSLRI_name), rdEn.name);
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSLRI_nametype), rdEn.type);
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSLRI_nameunit), rdEn.unit);
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSLRI_namecount), rdEn.count);
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSLRI_namedescription), rdEn.description);
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSLRI_nameremark), rdEn.remark);
        }
        /*设置删除功能*/
        if (lrEn != null && lrEn.listRecords != null && lrEn.listRecords.size() > 1) {
            view.findViewById(R.id.cargoSLRI_Delete).setOnClickListener(v -> {
                LRViews.remove(view);
                LRLinear.removeView(view);
                setDeleteButtonVisibleAndVisible();
            });
        }else{ //只有一条记录的时候隐藏掉删除按钮
            view.findViewById(R.id.cargoSLRI_Delete).setVisibility(View.GONE);
        }
        return view;
    }

    /**
     * 设置清单记录删除按钮是否可见
     */
    private void setDeleteButtonVisibleAndVisible() {
        if (LRViews==null || LRViews.size()==0) return;
        for (int i=0;i<LRViews.size();i++){
            if (LRViews.size()<2) LRViews.get(i).findViewById(R.id.cargoSLRI_delete_parent).setVisibility(View.GONE);
            else LRViews.get(i).findViewById(R.id.cargoSLRI_delete_parent).setVisibility(View.VISIBLE);
            SetTextUtil.setTextViewText(LRViews.get(i).findViewById(R.id.cargoSLRI_listNumber),(i+1)+"");
        }
    }


    /**
     * 线上填写或者下载后线下填写
     *
     * @param view
     */
    public void setOnOrOffOnlineCheck(View view) {
        RadioGroup rg = view.findViewById(R.id.CargoSRN_editOnOrOffRadioGroup);
        rg.check(R.id.CargoSRN_editOnline); //默认选中线上
        view.findViewById(R.id.CargoSR_OffLineLinelayout).setVisibility(View.GONE);  //默认隐藏线下操作View
        /**选择线上或者线下单击事件*/
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.CargoSRN_editOnline) {//线上填写
                view.findViewById(R.id.CargoSR_OffLineLinelayout).setVisibility(View.GONE);
                view.findViewById(R.id.CargoSR_OnLineView).setVisibility(View.VISIBLE);
            } else { //线下填写
                view.findViewById(R.id.CargoSR_OffLineLinelayout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.CargoSR_OnLineView).setVisibility(View.GONE);
            }
        });
    }


    public void setDowloadMouldOnclick(View v) { //CargoSR_TemplateDownload
        String[] nameArr = new String[]{"查勘记录(非集装箱).doc", "查勘记录(集装箱).doc", "清点记录.doc", "询问笔录.doc"};
        Dialog loadDialog = LoadDialogUtil.getMessageDialog(context, "下载中……");
        v.setOnClickListener(v1 -> {
            new AlertDialog.Builder(context).setTitle("选择下载模板")
                    .setItems(nameArr, (dialog, which) -> {
                        String FilePath = URLs.CARGO_TEMPLATE + URLEncodedUtil.toURLEncoded(nameArr[which]);
                        String incod = URLEncoder.encode(FilePath);
                        new DownLoadFileUtil(context).startDownLoad(FilePath, nameArr[which], "下载出错!", loadDialog, filePath -> { //下载成功后可以分享文件。
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

    /**
     * 显示作业信息。
     */
    public void disPlayworkInfo(SurveyRecordsEntity sREn, SurveyAskRecordsEntity RaEn, SurveyListRecordsEntity LrEn, View... views) {
//        disPlayRecordDocUrlInfo(views[0], sREn == null ? null : sREn.recordDocUrl,FILE_SELECT_CODE);
//        disPlayRecordDocUrlInfo(views[1], RaEn == null ? null : RaEn.recordDocUrl, FILE_SELECT_CODE_RA);
//        disPlayRecordDocUrlInfo(views[2], LrEn == null ? null : LrEn.recordDocUrl,FILE_SELECT_CODE_LR);
        disPlayDocInfo(sREn,RaEn,LrEn,views);
        if (sREn.records == null) sREn.records = new ContainerRecords();
        displayAskRecords(views[1],RaEn);
        displayListRecords(views[2],LrEn);
    }

    public void disPlayDocInfo(SurveyRecordsEntity sREn, SurveyAskRecordsEntity RaEn, SurveyListRecordsEntity LrEn, View... views) {
        disPlayRecordDocUrlInfo(views[0], sREn == null ? null : sREn.recordDocUrl,FILE_SELECT_CODE);
        disPlayRecordDocUrlInfo(views[1], RaEn == null ? null : RaEn.recordDocUrl, FILE_SELECT_CODE_RA);
        disPlayRecordDocUrlInfo(views[2], LrEn == null ? null : LrEn.recordDocUrl,FILE_SELECT_CODE_LR);
    }

    private void displayListRecords(View view, SurveyListRecordsEntity LrEn) {
        if (LrEn!=null) {
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSLR_insured),LrEn.insured);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSLR_address),LrEn.address);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSLR_projectType),LrEn.projectType);
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSLR_inventoryDate),LrEn.inventoryDate);
            disPlayLRSignImg(view,LrEn);
        }
        disPlayLRInfo(view,LrEn);
    }

    /**
     * 显示询问笔记信息
     * @param view
     * @param RaEn
     */
    private void displayAskRecords(View view, SurveyAskRecordsEntity RaEn) {
        if (RaEn!=null) {
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSA_askDate), RaEn.askDate);
            if (RaEn.askTimeRange!=null && RaEn.askTimeRange.size()>0) SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSA_start_askTimeRange),RaEn.askTimeRange.get(0));
            if (RaEn.askTimeRange!=null && RaEn.askTimeRange.size()>0) SetTextUtil.setTextViewText(view.findViewById(R.id.cargoSA_end_askTimeRange), RaEn.askTimeRange.get(1));
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_address),RaEn.address);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_askPeople),RaEn.askPeople);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_recordPeople),RaEn.recordPeople);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_askedPeople),RaEn.askedPeople);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_askedSex),RaEn.askedSex);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_idCardType),RaEn.idCardType);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_idCardNo),RaEn.idCardNo);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_householdAddress),RaEn.householdAddress);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_currentAddress),RaEn.currentAddress);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_company),RaEn.company);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_askedPhone),RaEn.askedPhone);
            SetTextUtil.setEditText(view.findViewById(R.id.cargoSA_caseName),RaEn.caseName);
            /**显示签名信息*/
            disPlayAskSignImg(view,RaEn);
        }
        /**显示询问记录*/
        disPlayQAInfo(view,RaEn);
    }

    /**
     * 显示询问笔记客户签字
     * @param view
     * @param RaEn
     */
    public void disPlayAskSignImg(View view, SurveyAskRecordsEntity RaEn) {
        String ggsSignPath = RaEn.signatureUrl;
        if (TextUtils.isEmpty(ggsSignPath)) {
            (view.findViewById(R.id.cargoSA_signatureUrlImg)).setVisibility(View.GONE);
        } else {
            ggsSignPath = AppApplication.getUSER().data.qiniuUrl + ggsSignPath;
            Glide.with(context).load(ggsSignPath).into(((ImageView) (view.findViewById(R.id.cargoSA_signatureUrlImg))));
            (view.findViewById(R.id.cargoSA_signatureUrlImg)).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示询问记录
     */
    private void disPlayQAInfo(View view, SurveyAskRecordsEntity RaEn){
        if (QALinear==null) QALinear = view.findViewById(R.id.cargoSA_QaLinearLayout);
        QALinear.removeAllViews();
        if (QAViews==null) QAViews = new ArrayList<>();
        QAViews.clear();
        if (RaEn!=null && RaEn.questionsAndAnswers!=null){
            for (int i=0;i<RaEn.questionsAndAnswers.size();i++){
                QandAEntity qaEn = RaEn.questionsAndAnswers.get(i);
                QAViews.add(getInSurveyAskItem(qaEn,i+1));
                QALinear.addView(QAViews.get(i));
                setAskOnclick(QAViews.get(i));
            }
        }else {
            //回显一个空的询问记录
            QandAEntity qaEn = new QandAEntity();
            QAViews.add(getInSurveyAskItem(qaEn,1));
            QALinear.addView(QAViews.get(0));
            setAskOnclick(QAViews.get(0));
        }
    }

    /**
     * 增删询问笔记
     */
    private void setAskOnclick(View pointView) {
        pointView.findViewById(R.id.cargo_ii_askTel_delete).setOnClickListener(v -> {
            QALinear.removeView(pointView);
            QAViews.remove(pointView);
            disPlayAskButton();
        });
        pointView.findViewById(R.id.cargo_ii_askTel_add).setOnClickListener(v -> {
            View newView = getInSurveyAskItem(null,QAViews.size()+1);
            QAViews.add(newView);
            QALinear.addView(newView);
            setAskOnclick(newView);
            disPlayAskButton();
        });
        disPlayAskButton();
    }

    private void disPlayAskButton(){
        for (int j = 0; j < QAViews.size(); j++) {
            if (QAViews.size()==1){
                QAViews.get(j).findViewById(R.id.cargo_ii_askTel_delete).setVisibility(View.GONE); //只有一个调查对象时，只能添加不能删除。
                QAViews.get(j).findViewById(R.id.cargo_ii_askTel_add).setVisibility(View.VISIBLE); //只有一个调查对象时，只能添加不能删除。
            }else if (j == (QAViews.size() - 1)) {
                QAViews.get(j).findViewById(R.id.cargo_ii_askTel_delete).setVisibility(View.VISIBLE); //只有一个调查对象时，只能添加不能删除。
                QAViews.get(j).findViewById(R.id.cargo_ii_askTel_add).setVisibility(View.VISIBLE); //只有一个调查对象时，只能添加不能删除。
            } else {
                QAViews.get(j).findViewById(R.id.cargo_ii_askTel_delete).setVisibility(View.VISIBLE); //最后一个询问对象既可以删除，也可以增加。
                QAViews.get(j).findViewById(R.id.cargo_ii_askTel_add).setVisibility(View.GONE); //最后一个询问对象既可以删除，也可以增加。
            }
        }
    }

    /**
     * 显示调查对象item
     * @param qaEn
     * @param i
     * @return
     */
    private View getInSurveyAskItem(QandAEntity qaEn, int i) {
        View view = inflater.inflate(R.layout.cxrsdc_injury_survey_list_item_item,null);
        SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askObjTiele),"问"+i+"：");
        SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askTelTitle),"答"+i+"：");
        if (qaEn!=null){
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askObj),qaEn.answer);
            SetTextUtil.setTextViewText(view.findViewById(R.id.cargo_ii_askTel),qaEn.question);
        }
        return view;
    }

    /**
     * 显示上传报告按钮
     * 如果有上传报告就显示*/
    private void disPlayRecordDocUrlInfo(View view, String DocUrl,int typeCode){

        if (TextUtils.isEmpty(DocUrl)) {
            setChioceFileOnclick(view.findViewById(R.id.CargoSR_upFile),typeCode);
            SetTextUtil.setTextViewText(view.findViewById(R.id.CargoSR_upFile), "上传");
            view.findViewById(R.id.CargoSR_downFile).setVisibility(View.INVISIBLE);
        } else {
            setChioceFileOnclick(view.findViewById(R.id.CargoSR_upFile),typeCode);
            SetTextUtil.setTextViewText(view.findViewById(R.id.CargoSR_upFile), "重新上传");
            view.findViewById(R.id.CargoSR_downFile).setVisibility(View.VISIBLE);
            setDownBaogaoOnclick(view.findViewById(R.id.CargoSR_downFile), DocUrl);
        }
    }

    /**
     * 点击选择上传报告文件
     * @param view
     */
    private void setChioceFileOnclick(View view,int typeCode) {
        view.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                context.startActivityForResult( Intent.createChooser(intent, "选择报告！"), typeCode);
            } catch (android.content.ActivityNotFoundException ex) {
                DialogUtil.getAlertOneButton(context, "抱歉，无法打开文件管理器！您可以安装一个文件管理器再试一次。", null).show();
            }
        });
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
     * 从控件提取信息到实体类
     * @param raEn
     * @param lrEn
     * @param askHeadView
     * @param recordsHeadView
     */
    public void reflashData(SurveyAskRecordsEntity raEn, SurveyListRecordsEntity lrEn, View askHeadView, View recordsHeadView) {
        getAskInfo(raEn,askHeadView);
        getLRInfo(lrEn,recordsHeadView);
    }

    /**
     * 从控件上获取询问笔录
     * @param lrEn
     * @param view
     */
    public void getLRInfo(SurveyListRecordsEntity lrEn, View view){
        if (lrEn==null) lrEn = new SurveyListRecordsEntity();
        lrEn.insured =((EditText)(view.findViewById(R.id.cargoSLR_insured))).getText().toString();
        lrEn.address =((EditText)(view.findViewById(R.id.cargoSLR_address))).getText().toString();
        lrEn.projectType =((EditText)(view.findViewById(R.id.cargoSLR_projectType))).getText().toString();
        lrEn.inventoryDate =((TextView)(view.findViewById(R.id.cargoSLR_inventoryDate))).getText().toString();
        if (lrEn.listRecords==null) lrEn.listRecords = new ArrayList<>();
        for (int i=0;i<LRViews.size();i++){
            View viewTemp = LRViews.get(i);
            if (lrEn.listRecords.size()==i) lrEn.listRecords.add(new ListRecordsEntity());
            ListRecordsEntity tempLR = lrEn.listRecords.get(i);
            tempLR.name = ((EditText)viewTemp.findViewById(R.id.cargoSLRI_name)).getText().toString();
            tempLR.type = ((EditText)viewTemp.findViewById(R.id.cargoSLRI_nametype)).getText().toString();
            tempLR.unit = ((EditText)viewTemp.findViewById(R.id.cargoSLRI_nameunit)).getText().toString();
            tempLR.count = ((EditText)viewTemp.findViewById(R.id.cargoSLRI_namecount)).getText().toString();
            tempLR.description = ((EditText)viewTemp.findViewById(R.id.cargoSLRI_namedescription)).getText().toString();
            tempLR.remark = ((EditText)viewTemp.findViewById(R.id.cargoSLRI_nameremark)).getText().toString();
        }
    }

    /**
     * 保存询问笔录
     * @param raEn
     * @param view
     */
    private void getAskInfo(SurveyAskRecordsEntity raEn, View view){
        if (raEn==null) raEn = new SurveyAskRecordsEntity();
        raEn.askDate = ((TextView)view.findViewById(R.id.cargoSA_askDate)).getText().toString();
        if (raEn.askTimeRange == null) raEn.askTimeRange = new ArrayList<>(2);
        raEn.askTimeRange.clear();
        raEn.askTimeRange.add (((TextView)view.findViewById(R.id.cargoSA_start_askTimeRange)).getText().toString());
        raEn.askTimeRange.add (((TextView)view.findViewById(R.id.cargoSA_end_askTimeRange)).getText().toString());
        raEn.address = ((EditText)view.findViewById(R.id.cargoSA_address)).getText().toString();
        raEn.askPeople = ((EditText)view.findViewById(R.id.cargoSA_askPeople)).getText().toString();
        raEn.recordPeople = ((EditText)view.findViewById(R.id.cargoSA_recordPeople)).getText().toString();
        raEn.askedPeople = ((EditText)view.findViewById(R.id.cargoSA_askedPeople)).getText().toString();
        raEn.askedSex = ((EditText)view.findViewById(R.id.cargoSA_askedSex)).getText().toString();
        raEn.idCardType = ((EditText)view.findViewById(R.id.cargoSA_idCardType)).getText().toString();
        raEn.idCardNo = ((EditText)view.findViewById(R.id.cargoSA_idCardNo)).getText().toString();
        raEn.householdAddress = ((EditText)view.findViewById(R.id.cargoSA_householdAddress)).getText().toString();
        raEn.currentAddress = ((EditText)view.findViewById(R.id.cargoSA_currentAddress)).getText().toString();
        raEn.company = ((EditText)view.findViewById(R.id.cargoSA_company)).getText().toString();
        raEn.askedPhone = ((EditText)view.findViewById(R.id.cargoSA_askedPhone)).getText().toString();
        raEn.caseName = ((EditText)view.findViewById(R.id.cargoSA_caseName)).getText().toString();

        if (raEn.questionsAndAnswers==null) raEn.questionsAndAnswers = new ArrayList<>();
        for (int i = 0;i<QAViews.size();i++){
            View tempView = QAViews.get(i);
            if (raEn.questionsAndAnswers.size()== i) raEn.questionsAndAnswers.add(new QandAEntity());
            QandAEntity tempQa = raEn.questionsAndAnswers.get(i);
            tempQa.answer = ((EditText)tempView.findViewById(R.id.cargo_ii_askObj)).getText().toString();
            tempQa.question = ((EditText)tempView.findViewById(R.id.cargo_ii_askTel)).getText().toString();
        }
    }

    public void disPlayLRSignImg(View view, SurveyListRecordsEntity lrEn) {
        String ggsSignPath = lrEn.signatureUrl;
        if (TextUtils.isEmpty(ggsSignPath)) {
            (view.findViewById(R.id.cargoSLR_signatureUrl_img)).setVisibility(View.GONE);
        } else {
            ggsSignPath = AppApplication.getUSER().data.qiniuUrl + ggsSignPath;
            Glide.with(context).load(ggsSignPath).into(((ImageView) (view.findViewById(R.id.cargoSLR_signatureUrl_img))));
            (view.findViewById(R.id.cargoSLR_signatureUrl_img)).setVisibility(View.VISIBLE);
        }
    }
}
