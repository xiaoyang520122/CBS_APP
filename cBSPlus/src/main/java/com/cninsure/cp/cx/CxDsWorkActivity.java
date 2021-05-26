package com.cninsure.cp.cx;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.autoloss.AutoLossMainActivity;
import com.cninsure.cp.cx.util.CxFileUploadUtil;
import com.cninsure.cp.cx.util.CxWorkSubmitUtil;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDamageTaskEntity;
import com.cninsure.cp.entity.cx.CxDamageWorkEntity;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxDsTaskEntity;
import com.cninsure.cp.entity.cx.CxDsWorkEntity;
import com.cninsure.cp.entity.cx.CxInjuryTrackWorkEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.ActivityFinishUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.FileChooseUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CxDsWorkActivity extends BaseActivity implements View.OnClickListener {


    public CxDsTaskEntity taskEntity; //任务信息
    private String QorderUid;  //任务编号
    private CxDictEntity cxDict; //拍照类型字典数据
    private LayoutInflater inflater ; //
    public final int DS_REQUEST_CODE = 1; //请求定损界面code
    public PublicOrderEntity orderInfoEn ; //任务信息
    private static CxDsWorkActivity instence;

    @ViewInject(R.id.CxDsM_intelligent_button) private TextView intelligentButton;  //智能定损**
    @ViewInject(R.id.CxDsM_dsCarNumber) private EditText dsCarNumber;  //车牌号**
    @ViewInject(R.id.CxDsM_carVinNo) private EditText carVinNoEdt;  //VIN码**
    @ViewInject(R.id.CxDsM_carType) private TextView carType;  //车型**
    @ViewInject(R.id.CxDsM_dsServiceDepot)  private EditText dsServiceDepot;//维修厂**
    @ViewInject(R.id.CxDsM_dsAptitudes_RG)  private RadioGroup dsAptitudes;//资质**
    @ViewInject(R.id.CxDsM_carSeries)  private TextView carSeries;//车系**
    @ViewInject(R.id.CxDsM_carTypeName)  private TextView carBrand;//车品牌**
    @ViewInject(R.id.CxDsM_carStructure)  private TextView carStructure;//车辆结构**
    @ViewInject(R.id.CxDsM_claimLevel)  private TextView claimLevel;//索赔险别**
    @ViewInject(R.id.CxDsM_dsLocation)  private EditText dsLocation;//定损地点**
    @ViewInject(R.id.CxDsM_dsLocation_local)  private TextView dsLocationLocal;//定损地点**

    @ViewInject(R.id.CxDsM_dsRescueAmount)  private EditText dsRescueAmount;//定损施救费
    @ViewInject(R.id.CxDsM_hsRescueAmount)  private TextView hsRescueAmount;//核损施救费
    @ViewInject(R.id.CxDsM_dsAllTotalAmount)  private TextView dsAllTotalAmount;//定损总金额
    @ViewInject(R.id.CxDsM_dsInstructions)  private EditText dsInstructions;//定损说明
    @ViewInject(R.id.CxDsM_enclosureList_add)  private TextView enclosureList;//附件信息
    @ViewInject(R.id.CxDsM_enclosureList_line)  private LinearLayout enclosureLin;//附件信息

    @ViewInject(R.id.CxDsM_dsTotalFee)  private TextView dsTotalFee;//换件信息-合计
    @ViewInject(R.id.CxDsM_dsFeeManagement)  private EditText dsFeeManagement;//管理费
    @ViewInject(R.id.CxDsM_dsFeeResidual)  private EditText dsFeeResidual;//残值
    @ViewInject(R.id.CxDsM_hsFeeManagement)  private TextView hsFeeManagement;//核价管理费
    @ViewInject(R.id.CxDsM_hsFeeResidual)  private TextView hsFeeResidual;//核价残值
    @ViewInject(R.id.CxDsM_hjTotalFee)  private TextView hjTotalFee;//换件项目-数量
    @ViewInject(R.id.CxDsM_hj_more)  private TextView hjTotalFeeMore;//换件项目-查看
    @ViewInject(R.id.CxDsM_timeFeeTotal)  private TextView timeFeeTotal;//工时信息-合计
    @ViewInject(R.id.CxDsM_replaceInfos_more)  private TextView replaceInfosMore;//工时信息-查看
    @ViewInject(R.id.CxDsM_dsTotalAmount)  private TextView dsTotalAmount;//外修合计
    @ViewInject(R.id.CxDsM_hsTotalAmount)  private TextView hsTotalAmount;//外修合计-核

    public static CxDsWorkActivity getContext() {
        return instence;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cx_ds_work_main_activity);
        ViewUtils.inject(this); //注入view和事件
        EventBus.getDefault().register(this);
        instence = this;
        inflater = LayoutInflater.from(this);
        cxDict = new CxDictEntity();
        QorderUid = getIntent().getStringExtra("orderUid");
        orderInfoEn = (PublicOrderEntity) getIntent().getSerializableExtra("PublicOrderEntity");
        dowloadDictType();
        setdsLocationLocalOnclick(); //获取当前地点的赋值到定损地点的点击事件
    }

    private void setdsLocationLocalOnclick() {
        dsLocationLocal.setOnClickListener(v -> {
            if (AppApplication.LOCATION!=null){
                SetTextUtil.setEditText(dsLocation,AppApplication.LOCATION.getAddrStr());
            }
        });
    }

    /**先下载字典库*/
    private void dowloadDictType() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("type");
        params.add("ds_aptitudes,claim_level_type,carStructure");
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
                setCarStructureCheck(); //绑定车辆结构选择内容
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

    /**
     * 绑定车辆结构选择内容
     */
    private void setCarStructureCheck() {
        TypePickeUtil.setTypePickerDialog(this,carStructure,cxDict,"carStructure");
    }

    private int isSubmit;
    /**保存或提交审核返回数据*/
    private void getTaskWorkSavaInfo(String value) {
        BaseEntity baseEntity = JSON.parseObject(value,BaseEntity.class);
        if (baseEntity.success) {
            Dialog dialog = DialogUtil.getAlertDialog(this,(isSubmit==1?"提交成功！":"保存成功！"),"提示！");
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (isSubmit==1)
                        CxDsWorkActivity.this.finish();
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
            taskEntity = JSON.parseObject(value, CxDsTaskEntity.class);
        } catch (Exception e) {  //解析失败，关闭界面
            disPlayErrorDialog();
            e.printStackTrace();
        }
        if (taskEntity == null )  taskEntity = new CxDsTaskEntity();
        if (taskEntity.data == null  ) taskEntity.data = new CxDsTaskEntity.CxDsTaskDataEntity();
        if (taskEntity.data.contentJson == null) {
            taskEntity.data.contentJson = new CxDsWorkEntity();
//            taskEntity.data.contentJson.riskDate = orderInfoEn.riskDate; //车类型
        }
        taskEntity.data.contentJson.insuredPerson = orderInfoEn.baoanPersonName; //被保险人暂时存 出险单位联系人
        taskEntity.data.contentJson.riskDate = orderInfoEn.riskDate; //出险时间
        if (40==(getIntent().getIntExtra("bussTypeId",0))) //标的定损回显车牌号
            taskEntity.data.contentJson.dsCarNumber = orderInfoEn.licensePlateBiaoDi; //出险车牌
        initView();
        displayWorkInfo();
    }

    /**
     * 索赔险别：必填，点选项//这里的定损任务是不是表示“三者定损”只能选择06和07，其他只能在“标的定损”里面选择//是的
     * 编码	定损任务	索赔险别
     * 01	标的定损	交强险
     * 02	标的定损	机动车损失保险
     * 03	标的定损	附加车轮单独损失险
     * 04	标的定损	附加新增设备损失险
     * 05	标的定损	附加车身划痕损失险
     * 06	三者定损	交强险
     * 07	三者定损	机动车第三者责任保险
     */
    private void initView() {
        hjTotalFeeMore.setOnClickListener(this);//换件项目-查看
        replaceInfosMore.setOnClickListener(this);//工时信息-查看
        intelligentButton.setOnClickListener(this);
        if (getIntent().getIntExtra("bussTypeId",0) == 40) {
            TypePickeUtil.setTypePickerDialogByValus(this, claimLevel, cxDict, "claim_level_type", "01,02,03,04,05");//索赔险别
        }else if (getIntent().getIntExtra("bussTypeId",0) == 41){
            TypePickeUtil.setTypePickerDialogByValus(this, claimLevel, cxDict, "claim_level_type", "06,07");//索赔险别
        }
        enclosureList.setOnClickListener(this);
        //保存或提交单击事件
        findViewById(R.id.CX_Act_Back_Tv).setOnClickListener(this);
        findViewById(R.id.CX_Act_More_Tv).setOnClickListener(this);
        ((TextView)findViewById(R.id.CX_Act_Title_Tv)).setText("定损");
        ((TextView)findViewById(R.id.CX_Act_More_Tv)).setText("保存/提交");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.CxDsM_intelligent_button: jumpTointelligentActivity(); break; //跳转只能定损界面
            case R.id.CX_Act_More_Tv: showSaveDialog(); break; //点击保存或暂存键
            case R.id.CX_Act_Back_Tv: ActivityFinishUtil.showFinishAlert(this); break; //退出
            case R.id.CxDsM_hj_more: showHjDialog(); break; //显示换件信息
            case R.id.CxDsM_replaceInfos_more: showGsDialog(); break; //显示工时信息
            case R.id.CxDsM_enclosureList_add:  PickPhotoUtil.albumPhoto(CxDsWorkActivity.this, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE);; break; //添加附件
        }
    }

    /***
     * 跳转到智能定损界面
     */
    private void jumpTointelligentActivity() {
        Intent intent = new Intent(this,DsUtilActivity.class);
//        Intent intent = new Intent(this, AutoLossMainActivity.class);
        intent.putExtra("contentJson",taskEntity.data.contentJson);
        intent.putExtra("CxDsWorkEntity",taskEntity.data.contentJson);
        startActivityForResult(intent,DS_REQUEST_CODE);
    }

    private void showSaveDialog() {
        CxWorkSubmitUtil.showSaveDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isSubmit = which;
                SaveDataToEntity();
                CxWorkSubmitUtil.submit(CxDsWorkActivity.this,which,QorderUid,JSON.toJSONString(taskEntity.data.contentJson),taskEntity.data.id); //提交
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE & data != null) {  //上传附件选择的文件。
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

    /**显示上传成功的附件*/
    public void getUploadFileInfo(List<NameValuePair> values) {
        String UpedFileName = values.get(0).getValue();
        if (taskEntity.data.contentJson.enclosureList == null)
            taskEntity.data.contentJson.enclosureList = new ArrayList<>();
        taskEntity.data.contentJson.enclosureList.add(UpedFileName);
        displayFileToList();
    }

    private void displayFileToList() {
        enclosureLin.removeAllViews();  //添加前清空，避免重复加载
        CxDsWorkEntity workEntity = taskEntity.data.contentJson;
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

   public Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            displayWorkInfo();
        }
    };

    /**显示任务信息*/
    public void displayWorkInfo() {
        if (taskEntity!=null && taskEntity.data!=null && taskEntity.data.contentJson!=null);
        else return;
        CxDsWorkEntity damageEnt = taskEntity.data.contentJson;
//        SetTextUtil.setEditText(intelligentButton;  //智能定损 页面吊起，不能放到这里，因为这里不一定能进入
        SetTextUtil.setEditText(dsCarNumber,damageEnt.dsCarNumber);  //车牌号**
        SetTextUtil.setEditText(carVinNoEdt,damageEnt.carVinNo);  //VIN码**
        SetTextUtil.setTextViewText(carType,damageEnt.carType);  //车型**
        SetTextUtil.setEditText(dsServiceDepot,damageEnt.dsServiceDepot);//维修厂**
        //资质**
        if (damageEnt.dsAptitudes.equals("0")) dsAptitudes.check(R.id.CxDsM_dsAptitudes_RB1);
        if (damageEnt.dsAptitudes.equals("1")) dsAptitudes.check(R.id.CxDsM_dsAptitudes_RB2);
        SetTextUtil.setTextViewText(carSeries,damageEnt.carSeries);//车系**
        SetTextUtil.setTextViewText(carBrand,damageEnt.carBrand);//车品牌**
        SetTextUtil.setTextViewText(carStructure,cxDict.getLabelByValue("carStructure",damageEnt.carStructure));//车辆结构**
        SetTextUtil.setTextViewText(claimLevel, cxDict.getLabelByValue("claim_level_type",damageEnt.claimLevel));

        SetTextUtil.setEditText(dsLocation,damageEnt.dsLocation);//定损地点**

        SetTextUtil.setEditText(dsRescueAmount,damageEnt.dsRescueAmount+"");//定损施救费
        SetTextUtil.setTextViewText(hsRescueAmount,damageEnt.hsRescueAmount+"");//核损施救费
        SetTextUtil.setTextViewText(dsAllTotalAmount,damageEnt.getDsAllTotalAmount()+"");//定损总金额
        SetTextUtil.setEditText(dsInstructions,damageEnt.dsInstructions);//定损说明
        displayFileToList() ;  //附件信息列表

        SetTextUtil.setTextViewText(dsTotalFee,damageEnt.getHjTotal());//换件信息-合计
        SetTextUtil.setTextViewText(dsFeeManagement,damageEnt.replaceInfosTotal.dsFeeManagement+"");//管理费
        SetTextUtil.setEditText(dsFeeResidual,damageEnt.replaceInfosTotal.dsFeeResidual+"");//残值
        SetTextUtil.setTextViewText(hsFeeManagement,damageEnt.replaceInfosTotal.hsFeeManagement+"");//核价管理费
        SetTextUtil.setTextViewText(hsFeeResidual,damageEnt.replaceInfosTotal.hsFeeManagement+"");//核价残值
        SetTextUtil.setTextViewText(hjTotalFee,damageEnt.replaceInfos.size()+"");//换件项目-数量
        SetTextUtil.setTextViewText(timeFeeTotal,damageEnt.getGsTotal());//工时信息-合计
        if (damageEnt.repairInfos!=null){
            float dsTotalAmountTemp =0;
            float HsTotalAmountTemp =0;
            for (CxDsWorkEntity.CxDsRepairInfos dsrItem:damageEnt.repairInfos){
                if (dsrItem.dsAmount!=null) dsTotalAmountTemp+=dsrItem.dsAmount;
                if (dsrItem.hsAmount!=null) HsTotalAmountTemp+=dsrItem.hsAmount;
            }
            SetTextUtil.setTextViewText(dsTotalAmount,dsTotalAmountTemp+"");//外修合计
            SetTextUtil.setTextViewText(hsTotalAmount,HsTotalAmountTemp+"");//外修合计-核
        }
    }

    private void showHintStr(String text,String title){
        TextView tv = new TextView(this);
        tv.setLineSpacing(7.0f,1);
        tv.setText(text);
        tv.setPadding(20,10,10,10);
        tv.setTextSize(16);
        DialogUtil.getDialogByViewOnlistener(this,tv,title,null).show();
    }
    private void showHjDialog() {
        CxDsWorkEntity damageEnt = taskEntity.data.contentJson;
        if (damageEnt.replaceInfos==null){
            ToastUtil.showToastLong(this,"无换件信息！");
            return;
        }
        StringBuffer infoData= new StringBuffer();
        int i=1;
        for (CxDsWorkEntity.CxDsReplaceInfos items:damageEnt.replaceInfos){
            infoData.append("----换件"+(i++)+"\n");
            infoData.append("*换件名称："+ (TextUtils.isEmpty(items.partName)?"":items.partName) +"\n");
            infoData.append("  配件编码："+ (TextUtils.isEmpty(items.partCode)?"":items.partCode) +"\n");
            infoData.append("*定损单价："+ items.unitPrice+"\n");
            infoData.append("*定损数量："+ items.unitCount+"\n");
            infoData.append("*定损小计（元）："+ items.unitTotalPrice+"\n");
            infoData.append("  定损备注："+ items.remark+"\n\n");
        }
        showHintStr(infoData.toString(),"换件信息");
    }
    private void showGsDialog() {
        CxDsWorkEntity damageEnt = taskEntity.data.contentJson;
        if (damageEnt.timeInfos==null){
            ToastUtil.showToastLong(this,"无工时信息！");
            return;
        }
        StringBuffer infoData= new StringBuffer();
        int i = 0;
        for (CxDsWorkEntity.CxDsTimeInfos items:damageEnt.timeInfos){
            infoData.append("--工时"+(i++)+"\n");
            infoData.append("*类型："+ (TextUtils.isEmpty(items.timeType)?"":items.timeType) +"\n");
            infoData.append("*工时项目："+ (TextUtils.isEmpty(items.timeProject)?"":items.timeProject) +"\n");
            infoData.append("  定损金额："+ items.dsAmount+"\n");
            infoData.append("  定损备注："+ items.dsRemark+"\n\n");
        }
        showHintStr(infoData.toString(),"工时信息");
    }

    private void SaveDataToEntity(){
        CxDsWorkEntity damageEnt = taskEntity.data.contentJson;
        damageEnt.areaNo = orderInfoEn.areaNo;
        damageEnt.area = orderInfoEn.area;
        damageEnt.province = orderInfoEn.province;
        damageEnt.caseProvince = orderInfoEn.caseProvince;
        damageEnt.city = orderInfoEn.city;


//        SetTextUtil.setEditText(intelligentButton;  //智能定损 页面吊起，不能放到这里，因为这里不一定能进入
        damageEnt.dsCarNumber = dsCarNumber.getText().toString();  //车牌号**
        damageEnt.carVinNo = carVinNoEdt.getText().toString();  //VIN码**
        damageEnt.carType =carType.getText().toString();  //车型**
        damageEnt.dsServiceDepot = dsServiceDepot.getText().toString();//维修厂**
        //资质**
        if (dsAptitudes.getCheckedRadioButtonId()==R.id.CxDsM_dsAptitudes_RB1) damageEnt.dsAptitudes = "0";
        if (dsAptitudes.getCheckedRadioButtonId()==R.id.CxDsM_dsAptitudes_RB2) damageEnt.dsAptitudes = "1";
//        SetTextUtil.setTextViewText(carSeries,damageEnt.carSeries);//车系**
//        SetTextUtil.setTextViewText(carTypeName,damageEnt.carTypeName);//车品牌**
        damageEnt.carStructure = cxDict.getValueByLabel("carStructure",carStructure.getText().toString());//车辆结构**
        damageEnt.claimLevel = cxDict.getValueByLabel("claim_level_type",claimLevel.getText().toString());//索赔险别**
        damageEnt.dsLocation = dsLocation.getText().toString();//定损地点**

        damageEnt.dsRescueAmount = dsRescueAmount.getText().toString();//定损施救费
//        SetTextUtil.setTextViewText(hsRescueAmount,damageEnt.hsRescueAmount+"");//核损施救费
//        SetTextUtil.setTextViewText(dsAllTotalAmount,damageEnt.dsAllTotalAmount+"");//定损总金额
        damageEnt.dsInstructions = dsInstructions.getText().toString();//定损说明
//        displayFileToList() ;  //附件信息列表

//        SetTextUtil.setTextViewText(dsTotalFee,damageEnt.getHjTotal());//换件信息-合计
        if (damageEnt.replaceInfosTotal == null) damageEnt.replaceInfosTotal = new CxDsWorkEntity.CxDsReplaceInfosTotal();
        damageEnt.replaceInfosTotal.dsFeeManagement = dsFeeManagement.getText().toString();//管理费
        if (damageEnt.replaceInfosTotal==null) damageEnt.replaceInfosTotal = new CxDsWorkEntity.CxDsReplaceInfosTotal();
        damageEnt.replaceInfosTotal.dsFeeResidual = dsFeeResidual.getText().toString();//残值
//        SetTextUtil.setTextViewText(hsFeeManagement,damageEnt.replaceInfosTotal.hsFeeManagement+"");//核价管理费
//        SetTextUtil.setTextViewText(hsFeeResidual,damageEnt.replaceInfosTotal.hsFeeManagement+"");//核价残值
//        SetTextUtil.setTextViewText(hjTotalFee,damageEnt.replaceInfos.size()+"");//换件项目-数量
//        SetTextUtil.setTextViewText(timeFeeTotal,damageEnt.getGsTotal());//工时信息-合计
//        SetTextUtil.setTextViewText(dsTotalAmount,damageEnt.repairInfosTotal.dsTotalAmount+"");//外修合计
//        SetTextUtil.setTextViewText(hsTotalAmount,damageEnt.repairInfosTotal.hsTotalAmount+"");//外修合计-核
    }


    /**提示错误后，并在关闭dialog的时候结束*/
    private void disPlayErrorDialog() {
        DialogUtil.getErrDialogAndFinish(this, "获取任务信息失败，请联系管理员！", new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CxDsWorkActivity.this.finish();
            }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (taskEntity!=null){
        String tt = taskEntity.data.contentJson.carFacturer;}
    }
}
