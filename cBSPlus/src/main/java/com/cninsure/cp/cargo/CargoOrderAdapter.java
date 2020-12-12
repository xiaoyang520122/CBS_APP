package com.cninsure.cp.cargo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cargo.CargoCaseBaoanTable;
import com.cninsure.cp.entity.cargo.DispatchMatter;
import com.cninsure.cp.navi.NaviHelper;
import com.cninsure.cp.utils.CallUtils;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.TimeHelper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CargoOrderAdapter extends BaseAdapter {

    /**接报案对应的调度列表*/
    private List<CargoCaseBaoanTable> dispatchList;
    private LayoutInflater inflater;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Activity context;

    @SuppressWarnings("unused")
    private CargoOrderAdapter(){}

    public CargoOrderAdapter(CargoListActivity context, List<CargoCaseBaoanTable> dispatchList){
        this.dispatchList = dispatchList;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        if (dispatchList==null) { //为空或者没有数据显示提示VIEW，以便headView能正常显示。
            return 1;
        }else if (dispatchList.size()==0) {//为空或者没有数据显示提示VIEW，以便headView能正常显示。
            return 1;
        }else {
            return dispatchList.size();
        }
    }

    @Override
    public Object getItem(int arg0) {
        if (dispatchList==null) {
            return null;
        }else if (dispatchList.size()==0) {
            return null;
        }else {
            return dispatchList.get(arg0);
        }
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View conView, ViewGroup arg2) {
        try {
            if (dispatchList!=null && dispatchList.size()>0) { //没有调度数据是显示提示按钮
                conView = inflater.inflate(R.layout.cargo_survey_list_item, null);
                CargoCaseBaoanTable caseDisTemp = dispatchList.get(arg0);
                setStatusView(conView,caseDisTemp);//调度状态
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_phone),caseDisTemp.ggsName + " "+caseDisTemp.ggsMobile); //案件对接人
                setCallOnclick(conView.findViewById(R.id.HYXList_phone),caseDisTemp);
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_workOrg),caseDisTemp.gsOrgName); //业务归属机构
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_book_time),caseDisTemp.surveyTime); //预约时间
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_taskAddress),caseDisTemp.surveyAddress); //查勘地点
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_surveyorFee),caseDisTemp.surveyPrice+""); //查勘费用
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_insured),caseDisTemp.insured);//出险人
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_chuxian_time),caseDisTemp.riskTime);//出险时间
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_chuxian_reason),caseDisTemp.riskReasonName); //出险原因
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_biaodi),caseDisTemp.damageSubject);//出险标的
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_paigong_items),new DispatchMatter().getMatterForString(caseDisTemp.dispatchMatter));//派工事项
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_replenish),caseDisTemp.dispatchMatterAdd);//派工事项补充
                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_wtName),caseDisTemp.wtName);//委托人
                if (caseDisTemp.createTime!=null) {
                    SetTextUtil.setTextViewText(conView.findViewById(R.id.CARGOList_createDate),"调度时间："+(caseDisTemp.createTime));//调度业时间
                    //状态为2（待作业）时，显示用时
                    if (caseDisTemp.status==2) {
                        new TimeHelper(context).setUseTime(conView.findViewById(R.id.CargoDSPList_UseTime),"",sf.parse(caseDisTemp.createTime).getTime(),arg0);//委托人标签显示用时信息
                    }else{
                        conView.findViewById(R.id.CargoDSPList_UseTime).setVisibility(View.GONE);
                    }
                }

                SetTextUtil.setTextViewText(conView.findViewById(R.id.HYXList_caseNo),caseDisTemp.caseNo); //任务编号
                CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.HYXList_taskAddress), caseDisTemp.surveyAddress);//复制地址
                CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.HYXList_caseNo), caseDisTemp.caseNo);//复制地址
               String naviAddress = caseDisTemp.province+caseDisTemp.city+caseDisTemp.surveyAddress;
                NaviHelper.setNaviOnclick(conView.findViewById(R.id.HYXList_copy_taskAddress),
                        context,null,null,naviAddress, caseDisTemp. ggsMobile);//线路规划
                CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.HYXList_copy_wtName), caseDisTemp.wtName);//复制委托人信息
                CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.HYXList_wtName), caseDisTemp.wtName);//复制委托人信息
                DialogUtil.setOnclickToShowDialogAlert(context,caseDisTemp.riskReasonName,"出险原因!",conView.findViewById(R.id.HYXList_chuxian_reason));
                DialogUtil.setOnclickToShowDialogAlert(context,new DispatchMatter().getMatterlistString(caseDisTemp.dispatchMatter),"派工事项!",conView.findViewById(R.id.HYXList_paigong_items));
                DialogUtil.setOnclickToShowDialogAlert(context,caseDisTemp.dispatchMatterAdd,"派工事项补充!",conView.findViewById(R.id.HYXList_replenish));
            }else {
                return inflater.inflate(R.layout.empty_dispatch_view, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return conView;
        }
        return conView;
    }

    /**q前端查勘员只能联系现场联系人和案件对接人，不能联系委托人对接人*/
    private void setCallOnclick(View view, CargoCaseBaoanTable caseDisTemp) {
        if (caseDisTemp==null){
            DialogUtil.getAlertOneButton(context,"无法获取拨号信息！",null).show();
        }else {
//            List<String> telArr = new ArrayList<String>(2);
//            if (!TextUtils.isEmpty(caseBaoanTable.ggsMobile)){
//                telArr.add("案件对接人："+caseBaoanTable.takerName+caseBaoanTable.ggsMobile +" ☎");
//            }
//            if (!TextUtils.isEmpty(caseBaoanTable.takerTel)){
//                telArr.add("现场联系人："+caseBaoanTable.sceneName+caseBaoanTable.sceneTel +" ☎");
//            }
//            if (telArr.size() ==0){
//                DialogUtil.getAlertOneButton(context,"无法获取拨号信息！",null).show();
//            }else{
                view.setOnClickListener(v -> {
//                        showCallAlert(telArr,caseBaoanTable);
                    CallUtils.call(context,caseDisTemp.ggsMobile);
                });
//            }
        }
    }

    /**弹框并拨号*/
//    private void showCallAlert(List<String> telArr, CargoCaseBaoanTable caseBaoanTable) {
//        String [] items = new String[telArr.size()];
//        for (int i = 0;i<telArr.size();i++){
//            items[i] = telArr.get(i);
//        }
//        new AlertDialog.Builder(context, R.style.MyDialogTheme).setTitle("选择拨号！")
//                .setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case 0:  //拨号 案件对接人
//                                CallUtils.call(context,caseBaoanTable.takerTel);
//                                break;
//                            case 1:  //拨号 现场联系人
//                                CallUtils.call(context,caseBaoanTable.sceneTel);
//                                break;
//                        }
//                    }
//                }).setNegativeButton("取消",null)
//                .create().show();
//    }

    /**设置状态*/
    private void setStatusView(View conView, final CargoCaseBaoanTable caseDisTemp) {
        if (caseDisTemp!=null && caseDisTemp.status!=null) {
            ((TextView)conView.findViewById(R.id.HYXList_status)).setText(CargoCaseStatuUtil.getStatuString(caseDisTemp.status));
            switch (caseDisTemp.status) {
//                case 0://调度暂存，可以继续编辑
//                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setText("编辑"); //蓝色
//                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setTextColor(context.getResources().getColor(R.color.bulue_main)); //蓝色
//                    ((TextView)conView.findViewById(R.id.HYXList_button1)).setTextColor(context.getResources().getColor(R.color.hui_line)); //其他状态不能点击取消
//                    ((TextView)conView.findViewById(R.id.HYXList_button1)).setTextColor(context.getResources().getColor(R.color.hui_line)); //其他状态不能改派
//                    break;
                case 4://调度发起（待处理）
                    ((TextView)conView.findViewById(R.id.HYXList_button1)).setText("拒绝派工订单"); //黄色、
                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setText("接受派工订单"); //蓝色
                    ((TextView)conView.findViewById(R.id.HYXList_button1)).setTextColor(context.getResources().getColor(R.color.yellow_logo)); //黄色
                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setTextColor(context.getResources().getColor(R.color.bulue_main)); //蓝色
                    setCancelOnclick(conView.findViewById(R.id.HYXList_button1),caseDisTemp); //拒绝任务
                    setDispatchSureOnclick(conView.findViewById(R.id.HYXList_button2),caseDisTemp); //接受任务
                    break;

                case 5:// 接收（待作业）
                    ((TextView)conView.findViewById(R.id.HYXList_button1)).setVisibility(View.GONE);  //无操作隐藏掉
                    ((TextView)conView.findViewById(R.id.HYXList_button_line)).setVisibility(View.GONE);  //隐藏竖线
                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setText("开始作业"); //蓝色
                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setTextColor(context.getResources().getColor(R.color.bulue_main)); //蓝色
//                    setDispatchSureOnclick(conView.findViewById(R.id.HYXList_button2),caseBaoanTable);  //确认到达现场
                    gouToDisWorkActivity(conView.findViewById(R.id.HYXList_button2),caseDisTemp);
                    break;

                case 10:// 审核退回（待作业）
                    ((TextView)conView.findViewById(R.id.HYXList_button1)).setVisibility(View.GONE);  //无操作隐藏掉
                    ((TextView)conView.findViewById(R.id.HYXList_button_line)).setVisibility(View.GONE);  //隐藏竖线
                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setText("完善作业信息"); //蓝色
                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setTextColor(context.getResources().getColor(R.color.bulue_main)); //蓝色
//                    setDispatchSureOnclick(conView.findViewById(R.id.HYXList_button2),caseBaoanTable);  //确认到达现场
                    gouToDisWorkActivity(conView.findViewById(R.id.HYXList_button2),caseDisTemp);
                    break;

                default: //非"待接收"状态的任务不能取消，提示用户。
                    ((TextView)conView.findViewById(R.id.HYXList_button1)).setVisibility(View.GONE);  //无操作隐藏掉
                    ((TextView)conView.findViewById(R.id.HYXList_button_line)).setVisibility(View.GONE);  //隐藏竖线
                    ((TextView)conView.findViewById(R.id.HYXList_button1)).setTextColor(context.getResources().getColor(R.color.hui_line)); //拒绝的不能取消
                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setText("查看任务详情"); //蓝色
                    ((TextView)conView.findViewById(R.id.HYXList_button1)).setTextColor(context.getResources().getColor(R.color.yellow_logo)); //黄色
                    ((TextView)conView.findViewById(R.id.HYXList_button2)).setTextColor(context.getResources().getColor(R.color.bulue_main)); //蓝色
                    gouToDisWorkActivity(conView.findViewById(R.id.HYXList_button2),caseDisTemp);
                    break;
            }
        }else {
            ((TextView)conView.findViewById(R.id.HYXList_status)).setText("状态未知");
        }
    }

    /**跳转导作业界面*/
    private void gouToDisWorkActivity(View view , CargoCaseBaoanTable caseDisTemp){
        view.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(context,CargoWorkActivity.class);
            intent.putExtra("caseBaoanTable",caseDisTemp);
            context.startActivity(intent );  //开始作业
        });
    }

    /**
     * 任务回退
     * @param view
     * @param caseDisTemp
     */
    private void setCancelOnclick(final View view , final CargoCaseBaoanTable caseDisTemp){//点击回退任务
//        if (caseBaoanTable.createDate!=null){ //调度时间不能为空
            view.setOnClickListener(arg0 -> {
                    switch (caseDisTemp.status){
                        case 4:
//                                if (new Date().getTime() - caseBaoanTable.createDate.getTime()< (5*60*1000)){  //调度时间超过五分钟
                                showBackAlertDialog(caseDisTemp);//回退任务
//                                }else{
//                                    DialogUtil.getAlertOneButton(context,"任务已超时（五分钟内未接受）！",null).show();
//                                }
                            break;
                        case 2:
                            break;
                    }
            });
//        }
    }

    /**
     * 弹框填写拒绝原因
     * @param caseDisTemp
     */
    private void showBackAlertDialog(final CargoCaseBaoanTable caseDisTemp){
        View bacView = LayoutInflater.from(context).inflate(R.layout.back_order_layout,null);

        DialogUtil.getDialogByViewOnlistener(context,bacView,"填写拒绝原因！", (dialog, which) -> {
            if (TextUtils.isEmpty(((EditText)bacView.findViewById(R.id.Back_Order_EditText)).getText().toString())){  //未填写拒绝原因
                DialogUtil.getAlertOneButton(context,"拒绝原因不能为空！",null).show();
            }else{
                acceptOrder(caseDisTemp,"确定拒绝该任务吗！",0,((EditText)bacView.findViewById(R.id.Back_Order_EditText)).getText().toString());
            }
        }).show();
    }

    /**公估师拒绝或接受任务接口
     * accCode=0拒绝；1接受
     * */
    private void acceptOrder(final CargoCaseBaoanTable caseDisTemp, String alertMsg, int accCode, String refuseReason){
        DialogUtil.getAlertOnelistener(context, alertMsg, (arg0, arg1) -> {
            LoadDialogUtil.setMessageAndShow(context, "努力处理中……");
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>(5);
            //取消订单
//                paramsList = new ArrayList<String>(2);
            paramsList.add(new BasicNameValuePair("userId",AppApplication.getUSER().data.userId));
            paramsList.add(new BasicNameValuePair("id",caseDisTemp.id+""));
            if (accCode==1) {//接受
                HttpUtils.requestPost(URLs.CARGO_ORDER_ACCEPT, paramsList, HttpRequestTool.CARGO_ORDER_ACCEPT);
            }else if (accCode==0)//拒绝
                HttpUtils.requestPost(URLs.CARGO_ORDER_REFUSE, paramsList, HttpRequestTool.CARGO_ORDER_REFUSE);
        }).show();
    }

    /**
     * 弹框填写取消原因
     * @param caseDisTemp
     */
    private void showCancelAlertDialog(final CargoCaseBaoanTable caseDisTemp){
        View bacView = LayoutInflater.from(context).inflate(R.layout.back_order_layout,null);

        DialogUtil.getDialogByViewOnlistener(context,bacView,"任务提示！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(((EditText)bacView.findViewById(R.id.Back_Order_EditText)).getText().toString())){  //未填写拒绝原因
                    DialogUtil.getAlertOneButton(context,"取消原因不能为空！",null).show();
                }else{
//                    arriveOrder(caseBaoanTable,"确定取消该任务吗！",0,((EditText)bacView.findViewById(R.id.Back_Order_EditText)).getText().toString());
                }
            }
        }).show();
    }

    /**公估师取消或接受任务接口
     * arrive	0为取消1为到达
     * */
//    private void arriveOrder(final CargoCaseBaoanTable caseBaoanTable, String alertMsg, int arrive, String cancelReason){
//        DialogUtil.getAlertOnelistener(context, alertMsg, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                LoadDialogUtil.setMessageAndShow(context, "努力处理中……");
//                List<NameValuePair> paramsList = new ArrayList<NameValuePair>(5);
//                paramsList.add(new BasicNameValuePair("userId",AppApplication.getUSER().data.userId+""));
//                if (arrive==0){ //
//                    paramsList.add(new BasicNameValuePair("uid",caseBaoanTable.uid));
//                    paramsList.add(new BasicNameValuePair("cancelReason",cancelReason+""));
//                    HttpUtils.requestPost(URLs.FSX_GGS_CANCEL_ORDER, paramsList, HttpRequestTool.FSX_GGS_CANCEL_ORDER);
//                }else if (arrive==1){  //到达现场
//                    paramsList.add(new BasicNameValuePair("dispatchUid",caseBaoanTable.uid));
//                    getLocationInfo(paramsList);
//                    HttpUtils.requestPost(URLs.FSX_GGS_SAVE_ORDER, paramsList, HttpRequestTool.FSX_GGS_SAVE_ORDER);
//                }
//            }
//        }).show();
//    }

    private void getLocationInfo(List<NameValuePair> paramsList) {
        if (AppApplication.LOCATION!=null){
            paramsList.add(new BasicNameValuePair("province",AppApplication.LOCATION.getProvince())); //省
            paramsList.add(new BasicNameValuePair("provinceCode","-1"));//省编码  百度定位无法获取，用-1默认
            paramsList.add(new BasicNameValuePair("city",AppApplication.LOCATION.getCity())); //市
            paramsList.add(new BasicNameValuePair("cityCode",AppApplication.LOCATION.getCityCode()));//市编码
            paramsList.add(new BasicNameValuePair("district",AppApplication.LOCATION.getDistrict()));//区
            paramsList.add(new BasicNameValuePair("districtCode",-1+""));//区编码  百度定位无法获取，用-1默认
            paramsList.add(new BasicNameValuePair("longitude",AppApplication.LOCATION.getLongitude()+""));
            paramsList.add(new BasicNameValuePair("latitude",AppApplication.LOCATION.getLatitude()+""));
            paramsList.add(new BasicNameValuePair("signInAddr",AppApplication.LOCATION.getAddrStr()));
        }
    }

    /**
     * 任务确认
     * @param view
     * @param caseDisTemp
     */
    private void setDispatchSureOnclick(View view , final CargoCaseBaoanTable caseDisTemp){//任务确认
//        if (caseBaoanTable.createTime!=null){ //调度时间不能为空
              view.setOnClickListener(arg0 -> {
                  switch (caseDisTemp.status){
                      case 4:
//                               if (new Date().getTime() - sf.parse(caseBaoanTable.createTime).getTime()< (5*60*1000)){  //调度时间超过五分钟
                              acceptOrder(caseDisTemp,"确定接受任务？",1,"");
//                               }else{
//                                   DialogUtil.getAlertOneButton(context,"任务已超时（五分钟内未接受）！",null).show();
//                               }
                          break;
                      case 2:
//                          arriveOrder(caseBaoanTable,"确定已到达现场？",1,"");//到达现场
                          break;
                  }
              });
//        }


    }

}