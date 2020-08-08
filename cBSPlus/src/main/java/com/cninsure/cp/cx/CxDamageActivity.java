package com.cninsure.cp.cx;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDamageTaskEntity;
import com.cninsure.cp.entity.cx.CxDamageWorkEntity;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.utils.ActivityFinishUtil;
import com.cninsure.cp.utils.DialogUtil;
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

import java.util.ArrayList;
import java.util.List;

public class CxDamageActivity extends BaseActivity implements View.OnClickListener {

    private CxDamageTaskEntity taskEntity; //物损任务信息
    private String QorderUid;
    private CxDictEntity cxDict; //拍照类型字典数据
    private LayoutInflater inflater ;


    @ViewInject(R.id.CXWA_Damage_LTv) private TextView backTv; //返回键
    @ViewInject(R.id.CxDamgWM_belongPerson) private EditText belongPerson;  //归属人
    @ViewInject(R.id.CxDamgWM_damageName) private EditText damageName; //物损名称
    @ViewInject(R.id.CxDamgWM_damageType) private TextView damageType; //损失类型
    @ViewInject(R.id.CxDamgWM_dsTotalAmount) private TextView dsTotalAmount; //合计
    @ViewInject(R.id.CxDamgWM_dsRescueAmount) private EditText dsRescueAmount; //施救费
    @ViewInject(R.id.CxDamgWM_dsInstructions) private EditText dsInstructions; //定损说明
    @ViewInject(R.id.CxDamgWM_damag_linear) private LinearLayout damagLinear; //定损项目LinearLayout
    @ViewInject(R.id.CxDamgWM_add_damag) private LinearLayout addLinear; //添加项目LinearLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cx_damag_worke_activity);
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
        params.add("damage_loss_type,damage_type");
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
            default:
                break;
        }
    }
    /**保存或提交审核返回数据*/
    private void getTaskWorkSavaInfo(String value) {
        BaseEntity baseEntity = JSON.parseObject(value,BaseEntity.class);
        if (baseEntity.success) DialogUtil.getAlertDialog(this,baseEntity.msg,"提示！").show();
    }

    /**解析获取的到的任务作业信息
     * @param value*/
    private void getTaskInfo(String value) {
        try {
            taskEntity = JSON.parseObject(value, CxDamageTaskEntity.class);
        } catch (Exception e) {  //解析失败，关闭界面
            disPlayErrorDialog();
            e.printStackTrace();
        }
        if (taskEntity == null )  taskEntity = new CxDamageTaskEntity();
        if (taskEntity.data == null  ) taskEntity.data = new CxDamageTaskEntity.DamageWorkEntity();
        if (taskEntity.data.contentJson == null) taskEntity.data.contentJson = new CxDamageWorkEntity();
        initView();
        displayDamageInfo();
    }

    /**显示任务信息*/
    private void displayDamageInfo() {
        if (taskEntity!=null && taskEntity.data!=null && taskEntity.data.contentJson!=null);
            else return;
        CxDamageWorkEntity damageEnt = taskEntity.data.contentJson;
        SetTextUtil.setEditText(belongPerson,damageEnt.belongPerson);  //归属人
        SetTextUtil.setEditText(damageName,damageEnt.damageName); //物损名称
        SetTextUtil.setTvTextForArr(damageType, TypePickeUtil.getDictLabelArr(cxDict.getDictByType("damage_loss_type")),damageEnt.damageType); //损失类型
        SetTextUtil.setTextViewText(dsTotalAmount,damageEnt.dsTotalAmount+""); //合计
        SetTextUtil.setEditText(dsRescueAmount,damageEnt.dsRescueAmount+""); //施救费
        SetTextUtil.setEditText(dsInstructions,damageEnt.dsInstructions); //定损说明
        disPlaySmallMaterials();
    }

    private void SaveDataToEntity(){
        CxDamageWorkEntity damageEnt = taskEntity.data.contentJson;
        damageEnt.belongPerson = belongPerson.getText().toString();  //归属人
        damageEnt.damageName = damageName.getText().toString(); //物损名称
        damageEnt.damageType = TypePickeUtil.getValue(damageType.getText().toString(),cxDict ,"damage_loss_type") ; //损失类型
        @SuppressLint("WrongViewCast") String tempDta = dsTotalAmount.getText().toString();
        damageEnt.dsTotalAmount = TextUtils.isEmpty(tempDta)?0:Float.parseFloat(dsTotalAmount.getText().toString()); //合计
        @SuppressLint("WrongViewCast") String tempDra = dsRescueAmount.getText().toString();
        damageEnt.dsRescueAmount = TextUtils.isEmpty(tempDra)?0:Float.parseFloat(dsRescueAmount.getText().toString()); //施救费
        damageEnt.dsInstructions = dsInstructions.getText().toString(); //定损说明
    }

    /**显示物损定损项目*/
    private void disPlaySmallMaterials() {
        damagLinear.removeAllViews();
        List<CxDamageWorkEntity.MaterialsEntity> smallMaterialsTemp = taskEntity.data.contentJson.smallMaterials;
        if (smallMaterialsTemp == null) return;
        float dsSum = 0;  //定损金额合计
        for (int i = 0; i < smallMaterialsTemp.size(); i++) {
            CxDamageWorkEntity.MaterialsEntity tempMaterials = smallMaterialsTemp.get(i);
            damagLinear.addView(getDamageView(tempMaterials, i));
            if (tempMaterials.dsUnitPrice != null && tempMaterials.dsUnitCount != null)
                dsSum = dsSum + (tempMaterials.dsUnitPrice * tempMaterials.dsUnitCount); //单价乘以数量
            if (tempMaterials.dsSalvageValue != null)
                dsSum = dsSum - tempMaterials.dsSalvageValue; //扣除残值
        }
        taskEntity.data.contentJson.dsTotalAmount = dsSum;//赋值到定损合计
        SetTextUtil.setTextViewText(dsTotalAmount,taskEntity.data.contentJson.dsTotalAmount+""); //合计
    }

    /**
     * 获取定损项目
     * @param materialsEntity
     * @param position
     * @return
     */
    private View getDamageView(CxDamageWorkEntity.MaterialsEntity materialsEntity,int position) {
        View view = inflater.inflate(R.layout.cx_damage_work_item,null);
        SetTextUtil.setEditText(view.findViewById(R.id.CxDamAlert_name),materialsEntity.name); //项目名称
        SetTextUtil.setTvTextForArr(view.findViewById(R.id.CxDamAlert_smallType),
                TypePickeUtil.getDictLabelArr(cxDict.getDictByType("damage_type")),materialsEntity.smallType);  //物损类别
        SetTextUtil.setEditText(view.findViewById(R.id.CxDamAlert_dsUnitPrice),materialsEntity.dsUnitPrice+"");//单价
        SetTextUtil.setEditText(view.findViewById(R.id.CxDamAlert_dsUnitCount),materialsEntity.dsUnitCount+"");//数量
        SetTextUtil.setEditText(view.findViewById(R.id.CxDamAlert_dsSalvageValue),materialsEntity.dsSalvageValue+"");//残值
        SetTextUtil.setEditText(view.findViewById(R.id.CxDamAlert_dsRemark),materialsEntity.dsRemark); //备注
        SetTextUtil.setTextViewText(view.findViewById(R.id.CxDamIt_damageIndex),"项目"+(position+1)); //编号
        setDeleteOnclick(view.findViewById(R.id.CxDamIt__delete),position);
        setEditOnclick(view.findViewById(R.id.CxDamIt_edit),position); //编辑

        setNotOnClick(view.findViewById(R.id.CxDamAlert_name));
        setNotOnClick(view.findViewById(R.id.CxDamAlert_dsUnitPrice));
        setNotOnClick(view.findViewById(R.id.CxDamAlert_dsUnitCount));
        setNotOnClick(view.findViewById(R.id.CxDamAlert_dsSalvageValue));
        setNotOnClick(view.findViewById(R.id.CxDamAlert_dsRemark));
        return view;
    }

    //设置不可编辑
    private void setNotOnClick(View view){
        view.setEnabled(false);
        view.setClickable(false);
    }

    /**
     * 编辑项目
     * @param viewById
     * @param position
     */
    private void setEditOnclick(View viewById, int position) {
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditDialog(position);
            }
        });
    }

    /**
     * 添加或者编辑项目，position不为null就是编辑，为null就是添加。
     * @param position
     */
    private void ShowEditDialog(Integer position) {
        View alertView = inflater.inflate(R.layout.cx_damage_alert_view,null);
        TypePickeUtil.setTypePickerDialog(this,alertView.findViewById(R.id.CxDamAlert_smallType),cxDict,"damage_type");//物损类别 绑定点击事件
        if (position!=null){
            displayAlertView(alertView,position);
        }
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
        CxDamageWorkEntity.MaterialsEntity addM = new CxDamageWorkEntity.MaterialsEntity();
        addM.name =((EditText)alertView.findViewById(R.id.CxDamAlert_name)).getText().toString(); //项目名称
        @SuppressLint("WrongViewCast") String tempDup = ((EditText)alertView.findViewById(R.id.CxDamAlert_dsUnitPrice)).getText().toString();
        addM.dsUnitPrice = TextUtils.isEmpty(tempDup) ? 0 : Float.parseFloat(tempDup); //单价

        @SuppressLint("WrongViewCast") String tempDuc = ((EditText)alertView.findViewById(R.id.CxDamAlert_dsUnitCount)).getText().toString();
        addM.dsUnitCount =TextUtils.isEmpty(tempDuc) ? 0 : Integer.parseInt(tempDuc); //数量

        @SuppressLint("WrongViewCast") String tempDsv = ((EditText)alertView.findViewById(R.id.CxDamAlert_dsSalvageValue)).getText().toString();
        addM.dsSalvageValue =TextUtils.isEmpty(tempDsv) ? 0 : Float.parseFloat(tempDsv); //残值
        addM.dsRemark =((EditText)alertView.findViewById(R.id.CxDamAlert_dsRemark)).getText().toString(); //备注
        @SuppressLint("WrongViewCast") String tempSmt = ((TextView)alertView.findViewById(R.id.CxDamAlert_smallType)).getText().toString();
        addM.smallType = TypePickeUtil.getValue(tempSmt,cxDict ,"damage_type"); //物损类别
        if (position==null){
            if(taskEntity.data.contentJson.smallMaterials==null) taskEntity.data.contentJson.smallMaterials = new ArrayList<>();  //添加项目到集合
            taskEntity.data.contentJson.smallMaterials.add(addM);  //添加项目到集合
        }else
            taskEntity.data.contentJson.smallMaterials.set(position,addM); //编辑项目时只更新内容
        disPlaySmallMaterials();  //显示数据
    }


    /**
     * 编辑项目时，先回显内容到View
     * @param alertView
     * @param position
     */
    private void displayAlertView(View alertView, Integer position) {
        CxDamageWorkEntity.MaterialsEntity TempM = taskEntity.data.contentJson.smallMaterials.get(position);
        SetTextUtil.setEditText(alertView.findViewById(R.id.CxDamAlert_name),TempM.name); //项目名称
        SetTextUtil.setEditText(alertView.findViewById(R.id.CxDamAlert_dsUnitPrice),TempM.dsUnitPrice+""); //单价
        SetTextUtil.setEditText(alertView.findViewById(R.id.CxDamAlert_dsUnitCount),TempM.dsUnitCount+""); //数量
        SetTextUtil.setEditText(alertView.findViewById(R.id.CxDamAlert_dsSalvageValue),TempM.dsSalvageValue+""); //残值
        SetTextUtil.setEditText(alertView.findViewById(R.id.CxDamAlert_dsRemark),TempM.dsRemark); //备注
        SetTextUtil.setTvTextForArr(alertView.findViewById(R.id.CxDamAlert_smallType),TypePickeUtil.getDictLabelArr(cxDict.getDictByType("damage_type")),TempM.smallType); //物损类别
    }

    /**
     * 删除项目
     * @param CxDamItDelete
     * @param position
     */
    private void setDeleteOnclick(View CxDamItDelete, int position) {
        CxDamItDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskEntity.data.contentJson.smallMaterials.remove(position);
                disPlaySmallMaterials();
            }
        });
    }

    private void initView() {
        backTv.setOnClickListener(this);
        TypePickeUtil.setTypePickerDialog(this,damageType,cxDict,"damage_loss_type");//损失类型 绑定点击事件
        addLinear.setOnClickListener(this);
        //保存或提交单击事件
        findViewById(R.id.CXWA_Damage_RTv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.CXWA_Damage_RTv: showSaveDialog(); break; //点击保存或暂存键
            case R.id.CXWA_Damage_LTv: ActivityFinishUtil.showFinishAlert(CxDamageActivity.this); break; //退出
            case R.id.CxDamgWM_add_damag:  ShowEditDialog(null); break; //传null为添加项目
        }
    }

    /**弹框选择是1保存还是0暂存*/
    private void showSaveDialog() {

        new AlertDialog.Builder(CxDamageActivity.this).setTitle("请选择")
                .setItems(new String[]{"保存", "提交审核"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveDataToEntity();
                        submitWorkInfo(which);
                    }
                }).setNeutralButton("取消", null).create().show();
//                .setNegativeButton("取消", null).create().show();
    }

    /**作业暂存或提交审核*/
    private void submitWorkInfo(int status) {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        paramsList.add(new BasicNameValuePair("orderUid", QorderUid));  //订单uid
        paramsList.add(new BasicNameValuePair("content", JSON.toJSONString(taskEntity.data.contentJson)));  //作业内容，保存为JSON对象
        paramsList.add(new BasicNameValuePair("status", status + ""));  //0：暂存；1：提交（送审）
        if (taskEntity.data.id != null && taskEntity.data.id > 0)
            paramsList.add(new BasicNameValuePair("id", taskEntity.data.id+""));  //作业id
        HttpUtils.requestPost(URLs.CX_NEW_WORK_SAVE, paramsList, HttpRequestTool.CX_NEW_WORK_SAVE);
        LoadDialogUtil.setMessageAndShow(this, "处理中……");
    }

    /**提示错误后，并在关闭dialog的时候结束*/
    private void disPlayErrorDialog() {
        DialogUtil.getErrDialogAndFinish(this, "获取任务信息失败，请联系管理员！", new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CxDamageActivity.this.finish();
            }
        }).show();
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
