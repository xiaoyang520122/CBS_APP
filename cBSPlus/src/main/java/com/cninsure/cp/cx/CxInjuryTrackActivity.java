package com.cninsure.cp.cx;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
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
import com.cninsure.cp.entity.cx.CxInjuryTrackTaskEntity;
import com.cninsure.cp.entity.cx.CxInjuryTrackWorkEntity;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.ActivityFinishUtil;
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

public class CxInjuryTrackActivity extends BaseActivity implements View.OnClickListener {

    private CxInjuryTrackTaskEntity taskEntity; //物损任务信息
    private String QorderUid;
    private CxDictEntity cxDict; //拍照类型字典数据
    private LayoutInflater inflater ;
    private boolean isDeliveryBill=false;  //是否选择的文件是快递单  true是，false否

   @ViewInject(R.id.CxrsIjtr_injuredName) EditText injuredName;    //伤者姓名
   @ViewInject(R.id.CxrsIjtr_injuredTel) EditText injuredTel;    //伤者电话
   @ViewInject(R.id.CxrsIjtr_workAddress) EditText workAddress;    //作业地点
    @ViewInject(R.id.CxrsIjtr_newInjuries_RG) RadioGroup newInjuries;    //新增伤情  1有、0无
   @ViewInject(R.id.CxrsIjtr_recovery) EditText recovery;    //恢复情况
   @ViewInject(R.id.CxrsIjtr_otherExplain) EditText otherExplain;    //其他说明
   @ViewInject(R.id.CxrsIjtr_deliveryMode_RG) RadioGroup deliveryMode;    //快递方式  0自行送达、1到付
   @ViewInject(R.id.CxrsIjtr_deliveryCompany) TextView deliveryCompany;    //快递公司  0、韵达	1、中通快递	2、宅急送	3、EMS	4、圆通快递	5、顺丰快递	6、申通快递
   @ViewInject(R.id.CxrsIjtr_add_Ask) TextView addAskTv;    //添加询问项目
   @ViewInject(R.id.CxrsIjtr_companyNo) EditText companyNo;    //快递单号
   @ViewInject(R.id.CxrsIjtr_consignee) EditText consignee;    //收货人
   @ViewInject(R.id.CxrsIjtr_shippingAddress) EditText shippingAddress;    //收货地址
   @ViewInject(R.id.CxrsIjtr_deliveryBill) TextView deliveryBill;    //快递单
   @ViewInject(R.id.CxrsIjtr_enclosureList)  LinearLayout enclosureLinear;    //附件信息列表
   @ViewInject(R.id.CxrsIjtr_askList_linear)  LinearLayout askListLinear;    //询问项目信息列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cxrs_injurytrack_activity);
        ViewUtils.inject(this); //注入view和事件
        EventBus.getDefault().register(this);
        cxDict = new CxDictEntity();
        QorderUid = getIntent().getStringExtra("orderUid");
        inflater = LayoutInflater.from(this);
        dowloadDictType();
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
                cxDict.list = JSON.parseArray(values.get(0).getValue(), CxDictEntity.DictData.class);
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
                        CxInjuryTrackActivity.this.finish();
                    else  dowloadTaskView();
                }
            });
            dialog.show();
        }else DialogUtil.getErrDialog(this,"操作失败："+baseEntity.msg).show();
    }

    /**解析获取的到的任务作业信息
     * @param value*/
    private void getTaskInfo(String value) {
        try {
            taskEntity = JSON.parseObject(value, CxInjuryTrackTaskEntity.class);
        } catch (Exception e) {  //解析失败，关闭界面
            ErrorDialogUtil.showErrorAndFinish(this,"获取任务信息失败，请联系管理员！");
            e.printStackTrace();
        }
        if (taskEntity == null )  taskEntity = new CxInjuryTrackTaskEntity();
        if (taskEntity.data == null  ) taskEntity.data = new CxInjuryTrackTaskEntity.InjuryTrackWorkEntity();
        if (taskEntity.data.contentJson == null) taskEntity.data.contentJson = new CxInjuryTrackWorkEntity();
        initView();
        displayDamageInfo();
    }

    /**显示任务信息*/
    private void displayDamageInfo() {
        if (taskEntity!=null && taskEntity.data!=null && taskEntity.data.contentJson!=null);
        else return;
        CxInjuryTrackWorkEntity workEntity = taskEntity.data.contentJson;
        SetTextUtil.setEditText(injuredName,workEntity.injuredName);    //伤者姓名
        SetTextUtil.setEditText(injuredTel,workEntity.injuredTel) ;    //伤者电话
        SetTextUtil.setEditText(workAddress,workEntity.workAddress) ;    //作业地点
        //新增伤情  1有、0无
        if (workEntity.newInjuries==1) newInjuries.check(R.id.CxrsIjtr_newInjuries_RBT);
        if (workEntity.newInjuries==0) newInjuries.check(R.id.CxrsIjtr_newInjuries_RBF);
        SetTextUtil.setEditText(recovery,workEntity.recovery) ;    //恢复情况
        disPlayAskObject();  //显示询问项目
        SetTextUtil.setEditText(otherExplain,workEntity.otherExplain) ; //其他说明
        //快递方式  0自行送达、1到付
        if (workEntity.deliveryMode==0) deliveryMode.check(R.id.CxrsIjtr_deliveryMode_RBT);
        if (workEntity.deliveryMode==1) deliveryMode.check(R.id.CxrsIjtr_deliveryMode_RBF);
        SetTextUtil.setTvTextForArr(deliveryCompany,TypePickeUtil.getDictLabelArr(cxDict.getDictByType("deliveryCompany")),workEntity.deliveryCompany);   //快递公司  0、韵达	1、中通快递	2、宅急送	3、EMS	4、圆通快递	5、顺丰快递	6、申通快递
        SetTextUtil.setEditText(companyNo,workEntity.deliveryNo) ;    //快递单号
        SetTextUtil.setEditText(consignee,workEntity.consignee) ;    //收货人
        SetTextUtil.setEditText(shippingAddress,workEntity.shippingAddress) ;    //收货地址
        SetTextUtil.setTextViewText(deliveryBill,workEntity.deliveryBill); ;    //快递单
        displayFileToList() ;  //附件信息列表
    }

    private void displayFileToList() {
        enclosureLinear.removeAllViews();  //添加前清空，避免重复加载
        CxInjuryTrackWorkEntity workEntity = taskEntity.data.contentJson;
        for (int i = 0; i < workEntity.enclosureList.size(); i++) {
            View view = inflater.inflate(R.layout.expandable_child_item, null);
            SetTextUtil.setTextViewText(view.findViewById(R.id.UPPHOTO_LI_name),workEntity.enclosureList.get(i)); //文件名称
            enclosureLinear.addView(view);  //添加到LineLayout
            int finalI = i;
            view.findViewById(R.id.UPPHOTO_LI_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    workEntity.enclosureList.remove(finalI); //移除名称集合
                    enclosureLinear.removeView(view);  //移除LineLayout，不在显示
                }
            });
        }
    }

    private void initView() {
        TypePickeUtil.setTypePickerDialog(this,deliveryCompany,cxDict,"deliveryCompany");//损失类型 绑定点击事件
        addAskTv.setOnClickListener(this);
        //保存或提交单击事件
        findViewById(R.id.CX_Act_Back_Tv).setOnClickListener(this);
        findViewById(R.id.CX_Act_More_Tv).setOnClickListener(this);
        ((TextView)findViewById(R.id.CX_Act_Title_Tv)).setText("人伤跟踪");
        ((TextView)findViewById(R.id.CX_Act_More_Tv)).setText("保存/提交");
        findViewById(R.id.CxrsIjtr_enclosureList_add).setOnClickListener(this); //上传附件按钮点击，选择文件
        findViewById(R.id.CxrsIjtr_deliveryBill).setOnClickListener(this); //点击上传快递单
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE & data!=null){  //上传附件选择的文件。
            inspectFileSize(data); //判断文件大小是否小于20M
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.CX_Act_More_Tv: showSaveDialog(); break; //点击保存或暂存键
            case R.id.CX_Act_Back_Tv: ActivityFinishUtil.showFinishAlert(this); break; //退出
            case R.id.CxrsIjtr_add_Ask:  ShowEditDialog(null); break; //传null为添加项目
            case R.id.CxrsIjtr_enclosureList_add:
                PickPhotoUtil.albumPhoto(CxInjuryTrackActivity.this, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE);
                isDeliveryBill = false;
                break; //上传附件按钮点击，选择文件
            case R.id.CxrsIjtr_deliveryBill: setDeliveryBillOnclick(); break; //点击上传快递单
        }
    }

    private void setDeliveryBillOnclick(){
        PickPhotoUtil.albumPhoto(CxInjuryTrackActivity.this, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE);
        isDeliveryBill = true;
    }
    /**
     * 添加或者编辑项目，position不为null就是编辑，为null就是添加。
     * @param position
     */
    private void ShowEditDialog(Integer position) {
        View alertView = getAskView(position,View.GONE);
        DialogUtil.getDialogByViewOnlistener(this, alertView, position != null ? "编辑项目" +(position+1) : "添加项目",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addMaterials(alertView,position);
                    }
                }).show();
    }

    /**
     * 增加项目
     * @param alertView
     * @param position
     */
    private void addMaterials(View alertView, Integer position) {
        CxInjuryTrackWorkEntity.InjuryTrackAskObject addM = new CxInjuryTrackWorkEntity.InjuryTrackAskObject();
        addM.askObject =((EditText)alertView.findViewById(R.id.CxrsIjtr_askObject)).getText().toString(); //询问对象
        addM.askObjectTel =((EditText)alertView.findViewById(R.id.CxrsIjtr_askObjectTel)).getText().toString(); //询问对象
        if (position==null){
            if(taskEntity.data.contentJson.askList==null) taskEntity.data.contentJson.askList = new ArrayList<>();  //添加项目到集合
            taskEntity.data.contentJson.askList.add(addM);  //添加项目到集合
        }else
            taskEntity.data.contentJson.askList.set(position,addM); //编辑项目时只更新内容
        disPlayAskObject();  //显示数据
    }

    /**
     * 显示询问列表
     */
    private void disPlayAskObject() {
        List<CxInjuryTrackWorkEntity.InjuryTrackAskObject> askListTemp = taskEntity.data.contentJson.askList;
        askListLinear.removeAllViews();
        if (askListTemp!=null)
            for (int i = 0;i<askListTemp.size();i++){
                askListLinear.addView(getAskView(i,View.VISIBLE));
            }

    }

    /**
     * 编辑项目时，先回显内容到View
     * @param position  等于null就不填充数据
     * @param visibility 是否显示编辑栏目 View.GONE
     */
    private View getAskView(Integer position,int visibility) {
        View view = inflater.inflate(R.layout.cxrs_injurytrack_ask_item,null);
        view.findViewById(R.id.CxrsIjtr_EditTitle).setVisibility(visibility); //是否显示编辑标题
        if (position==null) return view;
        CxInjuryTrackWorkEntity.InjuryTrackAskObject TempM = taskEntity.data.contentJson.askList.get(position);
        SetTextUtil.setEditText(view.findViewById(R.id.CxrsIjtr_askObject),TempM.askObject); //询问对象
        SetTextUtil.setEditText(view.findViewById(R.id.CxrsIjtr_askObjectTel),TempM.askObjectTel); //询问电话
        setDeleteOnclickc(view.findViewById(R.id.CxrsIjtr_ask_delete),position);//删除项目
        setEditOnclickc(view.findViewById(R.id.CxrsIjtr_edit),position);//编辑项目
        SetTextUtil.setTextViewText(view.findViewById(R.id.CxrsIjtr_askIndex),"项目"+(position+1)); //项目编号
        if (visibility == View.VISIBLE){ //显示编辑栏说明不是弹出框，这里不能编辑数据，只做显示。            setNotOnClick(view.findViewById(R.id.CxrsIjtr_askObject));
            setNotOnClick(view.findViewById(R.id.CxrsIjtr_askObjectTel));
        }
       return view;
    }

    //设置不可编辑
    private void setNotOnClick(View view){
        view.setEnabled(false);
        view.setClickable(false);
    }

    /**
     * 编辑项目
     * @param view
     * @param position
     */
    private void setEditOnclickc(View view, Integer position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditDialog(position);
            }
        });
    }

    /**
     * 删除项目
     * @param view
     * @param position
     */
    private void setDeleteOnclickc(View view, int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskEntity.data.contentJson.askList.remove(position);
                disPlayAskObject();
            }
        });
    }


    private void showSaveDialog() {
        CxWorkSubmitUtil.showSaveDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSubmit = which;
                SaveDataToEntity();
                CxWorkSubmitUtil.submit(CxInjuryTrackActivity.this,which,QorderUid,JSON.toJSONString(taskEntity.data.contentJson),taskEntity.data.id); //提交
            }
        });
    }

    private void SaveDataToEntity(){
        CxInjuryTrackWorkEntity workEntity  = taskEntity.data.contentJson;
        PublicOrderEntity orderInfoEn = (PublicOrderEntity) getIntent().getSerializableExtra("PublicOrderEntity");
        workEntity.areaNo = orderInfoEn.areaNo;
        workEntity.area = orderInfoEn.area;
        workEntity.province = orderInfoEn.province;
        workEntity.caseProvince = orderInfoEn.caseProvince;
        workEntity.city = orderInfoEn.city;

        workEntity.injuredName = injuredName.getText().toString();    //伤者姓名
        workEntity.injuredTel = injuredTel.getText().toString() ;    //伤者电话
        workEntity.workAddress = workAddress.getText().toString()  ;    //作业地点
        //新增伤情  1有、0无
        switch (newInjuries.getCheckedRadioButtonId()){
            case R.id.CxrsIjtr_newInjuries_RBT: workEntity.newInjuries=1;break;
            case R.id.CxrsIjtr_newInjuries_RBF: workEntity.newInjuries=0;break;
        }
        workEntity.recovery = recovery.getText().toString()  ;    //恢复情况
       workEntity.otherExplain = otherExplain.getText().toString()  ; //其他说明
        //快递方式  0自行送达、1到付
       switch (deliveryMode.getCheckedRadioButtonId()){
           case R.id.CxrsIjtr_deliveryMode_RBT: workEntity.deliveryMode=0;break;
           case R.id.CxrsIjtr_deliveryMode_RBF: workEntity.deliveryMode=1;break;
       }
        workEntity.deliveryCompany = TypePickeUtil.getValue(deliveryCompany.getText().toString(),cxDict,"deliveryCompany"); //快递公司
        workEntity.deliveryNo = companyNo.getText().toString()  ;    //快递单号
        workEntity.consignee = consignee.getText().toString()  ;    //收货人
        workEntity.shippingAddress = shippingAddress.getText().toString() ;    //收货地址
       workEntity.deliveryBill = deliveryBill.getText().toString();    //快递单
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
