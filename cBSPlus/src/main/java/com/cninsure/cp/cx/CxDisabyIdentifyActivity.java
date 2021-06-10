package com.cninsure.cp.cx;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.util.CxFileUploadUtil;
import com.cninsure.cp.cx.util.CxWorkSubmitUtil;
import com.cninsure.cp.cx.util.ErrorDialogUtil;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxDisabyIdentifyEntity;
import com.cninsure.cp.entity.cx.CxDisabyIdentTaskEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.ActivityFinishUtil;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.FileChooseUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CxDisabyIdentifyActivity extends BaseActivity implements View.OnClickListener {
    private CxDisabyIdentTaskEntity taskEntity; //物损任务信息
    private String QorderUid;
    public PublicOrderEntity orderInfoEn; //任务信息
    private CxDictEntity cxDict; //拍照类型字典数据
    private LayoutInflater inflater ;
    private boolean isDeliveryBill=false;  //是否选择的文件是快递单  true是，false否


    @ViewInject(R.id.CxRsDisabyI_injuredName) EditText injuredName; //伤者姓名
    @ViewInject(R.id.CxRsDisabyI_injuredTel) EditText injuredTel; //	伤者电话
    @ViewInject(R.id.CxRsDisabyI_appraisalTime) TextView appraisalTime; //	残定时间
    @ViewInject(R.id.CxRsDisabyI_appraisalOffice) EditText appraisalOffice; //	残定机构
    @ViewInject(R.id.CxRsDisabyI_appraisalAddress) EditText appraisalAddress; //	残定地点
    @ViewInject(R.id.CxRsDisabyI_appraisalPerson) EditText appraisalPerson	; //鉴定人
    @ViewInject(R.id.CxRsDisabyI_appraisalTel) EditText appraisalTel; //	鉴定电话
    @ViewInject(R.id.CxRsDisabyI_appraisalResult) EditText appraisalResult; //	鉴定结果
    @ViewInject(R.id.CxRsDisabyI_appraisalRemarks) EditText appraisalRemarks; //	备注

    @ViewInject(R.id.CxRsDisabyI_deliveryMode_RG) RadioGroup deliveryMode; //	快递方式
    @ViewInject(R.id.CxRsDisabyI_deliveryCompany) TextView deliveryCompany; //快递公司
    @ViewInject(R.id.CxRsDisabyI_deliveryNo) EditText deliveryNo; //快递单号
    @ViewInject(R.id.CxRsDisabyI_consignee) EditText consignee; //收货人
    @ViewInject(R.id.CxRsDisabyI_shippingAddress) EditText shippingAddress; //	收货地址
    @ViewInject(R.id.CxRsDisabyI_deliveryBill) TextView deliveryBill; //	快递单

    @ViewInject(R.id.CxRsDisabyI_enclosureList_line) LinearLayout enclosureLin; //	附件信息



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //官方推荐使用这种方式保持亮屏
        setContentView(R.layout.cxrs_disaby_identify_activity);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        cxDict = new CxDictEntity();
        QorderUid = getIntent().getStringExtra("orderUid");
        orderInfoEn = (PublicOrderEntity) getIntent().getSerializableExtra("PublicOrderEntity");
        inflater = LayoutInflater.from(this);
        dowloadDictType();
    }

    private void initView() {
        DateChoiceUtil.setLongDatePickerDialogOnClick(this,appraisalTime);
        TypePickeUtil.setTypePickerDialog(this,deliveryCompany,cxDict,"deliveryCompany");//损失类型 绑定点击事件
        //保存或提交单击事件
        findViewById(R.id.CX_Act_Back_Tv).setOnClickListener(this);
        findViewById(R.id.CX_Act_More_Tv).setOnClickListener(this);
        ((TextView)findViewById(R.id.CX_Act_Title_Tv)).setText("陪同残定");

        if (orderInfoEn.status==4 || orderInfoEn.status==6 || orderInfoEn.status==10 ){ //4已接单，6作业中、10审核退回 状态可以提交。
            ((TextView)findViewById(R.id.CX_Act_More_Tv)).setText("保存/提交");
        }else{
            ((TextView)findViewById(R.id.CX_Act_More_Tv)).setVisibility(View.INVISIBLE);
            DialogUtil.getErrDialog(this,"当前任务状态只可查看，不能提交或暂存！").show();
        }

        findViewById(R.id.CxRsDisabyI__add).setOnClickListener(this); //上传附件按钮点击，选择文件
        findViewById(R.id.CxRsDisabyI_deliveryBill).setOnClickListener(this); //点击上传快递单isDeliveryBill
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.CX_Act_More_Tv: showSaveDialog(); break; //点击保存或暂存键
            case R.id.CX_Act_Back_Tv: ActivityFinishUtil.showFinishAlert(this); break; //退出
            case R.id.CxRsDisabyI__add:
                PickPhotoUtil.albumPhoto(CxDisabyIdentifyActivity.this, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE);
                isDeliveryBill = false;
                break; //上传附件按钮点击，选择文件
            case R.id.CxRsDisabyI_deliveryBill: setDeliveryBillOnclick(); break; //点击上传快递单
        }
    }

    private void setDeliveryBillOnclick(){
        PickPhotoUtil.albumPhoto(CxDisabyIdentifyActivity.this, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE);
        isDeliveryBill = true;
    }

    private void showSaveDialog() {
        CxWorkSubmitUtil.showSaveDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSubmit = which;
                SaveDataToEntity();
                CxWorkSubmitUtil.submit(CxDisabyIdentifyActivity.this,which,QorderUid,JSON.toJSONString(taskEntity.data.contentJson),taskEntity.data.id); //提交
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE & data!=null){  //上传附件选择的文件。
            inspectFileSize(data); //判断文件大小是否小于20M
        }
    }

    /**
     * 判断文件大小是否小于20M,小于就上传。
     * @param data
     */
    public void inspectFileSize(Intent data) {
        String FilePath = FileChooseUtil.getInstance(this).getChooseFileResultPath(data.getData());
        File fileTemp = new File(FilePath);
        if (fileTemp!=null && fileTemp.length()>0 && (fileTemp.length() < 20971520)) { //必须小于20M（20971520 byte）
            List<NameValuePair> fileUrls = new ArrayList<NameValuePair>();
            fileUrls.add(new BasicNameValuePair("0", FilePath));
            CxFileUploadUtil.uploadCxFile(this, fileUrls, URLs.UPLOAD_FILE_PHOTO,null); //上传
        }
    }

    /**保存控件数据到实体类*/
    private void SaveDataToEntity() {

        PublicOrderEntity orderInfoEn = (PublicOrderEntity) getIntent().getSerializableExtra("PublicOrderEntity");
        CxDisabyIdentifyEntity conJson = taskEntity.data.contentJson;
        conJson.areaNo = orderInfoEn.areaNo;
        conJson.area = orderInfoEn.area;
        conJson.province = orderInfoEn.province;
        conJson.caseProvince = orderInfoEn.caseProvince;
        conJson.city = orderInfoEn.city;

        conJson.injuredName = injuredName.getText().toString();//	伤者姓名
        conJson.injuredTel = injuredTel.getText().toString();//	伤者电话
        conJson.appraisalTime = appraisalTime.getText().toString();//	残定时间
        conJson.appraisalOffice = appraisalOffice.getText().toString();//	残定机构
        conJson.appraisalAddress = appraisalAddress.getText().toString();//	残定地点
        conJson.appraisalPerson = appraisalPerson.getText().toString();//	鉴定人
        conJson.appraisalTel = appraisalTel.getText().toString();//	鉴定电话
        conJson.appraisalResult = appraisalResult.getText().toString();//	鉴定结果
        conJson.appraisalRemarks = appraisalRemarks.getText().toString();//	备注

        switch (deliveryMode.getCheckedRadioButtonId()){  //快递方式
            case R.id.CxRsDisabyI_deliveryMode_RBT: conJson.deliveryMode = 1;break;
            case R.id.CxRsDisabyI_deliveryMode_RBF: conJson.deliveryMode = 0;break;
        }
        conJson.deliveryCompany = TypePickeUtil.getValue(deliveryCompany.getText().toString(),cxDict,"deliveryCompany"); //快递公司
        conJson.deliveryNo = deliveryNo.getText().toString();//	快递单号
        conJson.consignee = consignee.getText().toString();//	收货人
        conJson.shippingAddress = shippingAddress.getText().toString();//	收货地址

    }


    /**先下载字典库*/
    private void dowloadDictType() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("type");
        params.add("deliveryCompany");
        HttpUtils.requestGet(URLs.CX_NEW_GET_IMG_TYPE_DICT, params, HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT);
    }
    private void dowloadTaskView() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("userId");
        params.add(AppApplication.getUSER().data.userId);
        params.add("orderUid");
        params.add(QorderUid);
        HttpUtils.requestGet(URLs.CX_NEW_GET_ORDER_VIEW_BY_UID, params, HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnet(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT:
                dowloadTaskView();
                LoadDialogUtil.dismissDialog();
                cxDict.list = JSON.parseArray(values.get(0).getValue(), DictData.class);
                break;
            case HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID: //获取订单信息
                LoadDialogUtil.dismissDialog();
                getTaskInfo(values.get(0).getValue());
                break;
            case HttpRequestTool.CX_NEW_WORK_SAVE: // 保存或提交审核返回数据
                LoadDialogUtil.dismissDialog();
                getTaskWorkSavaInfo(values.get(0).getValue());
                break;
            case HttpRequestTool.UPLOAD_FILE_PHOTO: //上传附件成功
                getUploadFileInfo(values);
                break;
            default:
                break;
        }
    }

    /**显示上传成功的附件*/
    public void getUploadFileInfo(List<NameValuePair> values) {
        String UpedFileName = values.get(0).getValue();
        if (isDeliveryBill){
            taskEntity.data.contentJson.deliveryBill = UpedFileName;
            deliveryBill.setText(UpedFileName);
        }else{
            if (taskEntity.data.contentJson.enclosureList==null)taskEntity.data.contentJson.enclosureList = new ArrayList<>();
            taskEntity.data.contentJson.enclosureList.add(UpedFileName);
            displayFileToList();
        }
    }

    /**解析获取的到的任务作业信息
     * @param value*/
    private void getTaskInfo(String value) {
        try {
            taskEntity = JSON.parseObject(value, CxDisabyIdentTaskEntity.class);
        } catch (Exception e) {  //解析失败，关闭界面
            ErrorDialogUtil.showErrorAndFinish(this,"获取任务信息失败，请联系管理员！");
            e.printStackTrace();
        }
        if (taskEntity == null )  taskEntity = new CxDisabyIdentTaskEntity();
        if (taskEntity.data == null  ) taskEntity.data = new CxDisabyIdentTaskEntity.CxDisabyIdentTaskDataEntity();
        if (taskEntity.data.contentJson == null) taskEntity.data.contentJson = new CxDisabyIdentifyEntity();
        initView();
        displayWorkInfo();
    }

    /**回显作业信息*/
    private void displayWorkInfo() {
        CxDisabyIdentifyEntity conJson = taskEntity.data.contentJson;
        SetTextUtil.setEditText(injuredName, taskEntity.data.contentJson.injuredName);//伤者姓名
        SetTextUtil.setEditText(injuredTel, conJson.injuredTel);//伤者电话
        SetTextUtil.setTextViewText(appraisalTime, conJson.appraisalTime);//残定时间
        SetTextUtil.setEditText(appraisalOffice, conJson.appraisalOffice);//残定机构
        SetTextUtil.setEditText(appraisalAddress, conJson.appraisalAddress);//残定地点
        SetTextUtil.setEditText(appraisalPerson, conJson.appraisalPerson);//鉴定人
        SetTextUtil.setEditText(appraisalTel, conJson.appraisalTel);//	鉴定电话
        SetTextUtil.setEditText(appraisalResult, conJson.appraisalResult);//鉴定结果
        SetTextUtil.setEditText(appraisalRemarks, conJson.appraisalRemarks);//	备注
        //	快递方式
        if (conJson.deliveryMode == 1) deliveryMode.check(R.id.CxRsDisabyI_deliveryMode_RBT);
        if (conJson.deliveryMode == 0) deliveryMode.check(R.id.CxRsDisabyI_deliveryMode_RBF);
        SetTextUtil.setTvTextForArr(deliveryCompany, TypePickeUtil.getDictLabelArr(cxDict.getDictByType("deliveryCompany")), conJson.deliveryCompany);//快递公司
        SetTextUtil.setEditText(deliveryNo, conJson.deliveryNo);//快递单号
        SetTextUtil.setEditText(consignee, conJson.consignee);//收货人
        SetTextUtil.setEditText(shippingAddress, conJson.shippingAddress);//收货地址
        SetTextUtil.setTextViewText(deliveryBill, conJson.deliveryBill);//	快递单
        displayFileToList() ;  //附件信息列表
    }


    private void displayFileToList() {
        enclosureLin.removeAllViews();  //添加前清空，避免重复加载
        CxDisabyIdentifyEntity workEntity = taskEntity.data.contentJson;
        for (int i = 0; i < workEntity.enclosureList.size(); i++) {
            View view = inflater.inflate(R.layout.expandable_child_item, null);
            SetTextUtil.setTextViewText(view.findViewById(R.id.UPPHOTO_LI_name),workEntity.enclosureList.get(i)); //文件名称
            enclosureLin.addView(view);  //添加到LineLayout
            int finalI = i;
            view.findViewById(R.id.UPPHOTO_LI_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    workEntity.enclosureList.remove(finalI); //移除名称集合
                    enclosureLin.removeView(view);  //移除LineLayout，不在显示
                }
            });
        }
    }

    private int isSubmit;
    /**保存或提交审核返回数据*/
    private void getTaskWorkSavaInfo(String value) {
        BaseEntity baseEntity = JSON.parseObject(value,BaseEntity.class);
        if (baseEntity.success) {
            Dialog dialog = DialogUtil.getAlertDialog(this,baseEntity.msg,"提示！");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (isSubmit==1)
                        CxDisabyIdentifyActivity.this.finish();
                    else  dowloadTaskView();
                }
            });
            dialog.show();
        }else DialogUtil.getErrDialog(this,"操作失败："+baseEntity.msg).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**返回键提示是否退出*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            ActivityFinishUtil.showFinishAlert(this);
            return false;//拦截事件
        }
        return super.onKeyDown(keyCode, event);
    }

}
