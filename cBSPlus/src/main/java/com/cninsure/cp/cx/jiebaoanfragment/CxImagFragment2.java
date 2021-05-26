package com.cninsure.cp.cx.jiebaoanfragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.cx.CxSurveyWorkActivity;
import com.cninsure.cp.cx.adapter.CxImagAdapter;
import com.cninsure.cp.cx.adapter.CxImagAdapter2;
import com.cninsure.cp.cx.adapter.SaveImgCallBack;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.cx.util.CxWorkImgUploadUtil;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxBaoanTaskEntity;
import com.cninsure.cp.entity.cx.CxDamageTaskEntity;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxDsTaskEntity;
import com.cninsure.cp.entity.cx.CxImagEntity;
import com.cninsure.cp.entity.cx.CxOrderEntity;
import com.cninsure.cp.entity.cx.CxOrderMediaTypeEntity;
import com.cninsure.cp.entity.cx.CxOrderWorkMediaTypeTable;
import com.cninsure.cp.entity.cx.CxSurveyTaskEntity;
import com.cninsure.cp.entity.cx.CxSurveyWorkEntity;
import com.cninsure.cp.entity.cx.CxTaskModelEntity;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.entity.cx.injurysurvey.CxInjurySurveyWorkEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CxImagFragment2 extends BaseFragment {


    private LayoutInflater inflater;
    private Activity activity;
    private View contentView;
    public CxOrderMediaTypeEntity cxMediaTypes ; //拍照类型字典数据


    public String QorderUid;
    public PublicOrderEntity orderInfoEn; //任务信息
//    public List<CxOrderEntity.CxOrderTable> orderList; //接报案对应任务列表
//    public CxSurveyTaskEntity surveyEn; //查勘-作业信息
//    public CxDsTaskEntity bdDsEn; //标的定损-任务信息
//    public List<CxDsTaskEntity> thDsEn; //三者定损-任务信息
//    public List<CxDamageTaskEntity> damageEn; //物损任务-任务信息
    public List<CxInjurySurveyWorkEntity> injuEn; //人伤任务-任务信息
    public CxBaoanTaskEntity baoanTaskEntity; //接报案下面所有任务的作业数据


    @ViewInject(R.id.newCximg_expandablelistview) private ExpandableListView photoListView;
    @ViewInject(R.id.newCximg_ImgFragment_button) private Button submitImgBut;
    @ViewInject(R.id.cx_img_expandablellistview_RDGroup) private RadioGroup radgroup;
    /**图片合集*/
    private List<CxImagEntity> imgEnList;
    public static CxImagAdapter2 adapter;
    private List<CxImagEntity> submitImgEnList;  //上传图片合集
    private List<NameValuePair> params;
    private int largeClass=0; //记录选择的顶部table位置。
    private List<CxOrderWorkMediaTypeTable> tableTypes; //顶部标题集合（第一级的parentId是空的，这里通过parentid==""的对象value值作为parentId获取）
    private List<RadioButton> radioBArr; //顶部标题RadioButton集合。
    private int orderCount=0 , getworkInfoCount=0; //该接报案下面的任务数量和已经下载作业信息的数量。

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_img_expandablelistview,null); //cx_img_expandablelistview
        activity =  getActivity();
        ViewUtils.inject(this,contentView);
        orderInfoEn = (PublicOrderEntity) activity.getIntent().getSerializableExtra("PublicOrderEntity");
        QorderUid = activity.getIntent().getStringExtra("orderUid");
        downloadOrderList();
        setSubmitOnclick();
        return contentView;
    }


    private void setSubmitOnclick() {
        submitImgBut.setOnClickListener(v -> {
            uploadImg();
        });
    }
    /**上传作业图片**/
    private void uploadImg() {
        params = new ArrayList<>();
        submitImgEnList = new ArrayList<>();  // 待上传图片类集合
        for (CxImagEntity tempImglist : imgEnList) {
            if (tempImglist != null && tempImglist.getImageUrl() != null && tempImglist.getImageUrl().indexOf("://") == -1) {
                params.add(new BasicNameValuePair(tempImglist.type + "", tempImglist.getImageUrl()));
                submitImgEnList.add(tempImglist);
            }
        }
        if (submitImgEnList.size() > 0) {
            CxWorkImgUploadUtil.uploadCxImg(activity, submitImgEnList, URLs.UPLOAD_FILE_PHOTO);
        } else {
            DialogUtil.getAlertOneButton(activity, "没有需要上传的图片！", null).show();
        }
    }
    /**上传作业图片路径**/
    private void saveImg() {
        List<NameValuePair> httpParams = new ArrayList<>();
//        httpParams.add(new BasicNameValuePair( "id", tempImgData.id==0?"":tempImgData.id+""));  //替换图片时使用
        httpParams.add(new BasicNameValuePair("orderUid", orderInfoEn.uid));
        httpParams.add(new BasicNameValuePair("baoanUid", orderInfoEn.caseBaoanUid));
        httpParams.add(new BasicNameValuePair("version", "1"));

        if (submitImgEnList.size() > 0) {
            PhotoUploadUtil.newCxImgSave(activity, submitImgEnList, URLs.CX_UP_WORK_IMG, httpParams,cxMediaTypes);
        } else {
            DialogUtil.getAlertOneButton(activity, "没有需要上传的图片！", null).show();
        }
    }
    /**根据报案编号查询接报案下面所有的订单信息*/
    private void downloadOrderList() {
        LoadDialogUtil.setMessageAndShow(getActivity(),"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("baoanUid");
        params.add(orderInfoEn.caseBaoanUid);
        params.add("userId");
        params.add(AppApplication.getUSER().data.userId);
        HttpUtils.requestGet(URLs.CX_GET_WORK_BY_BAOANUID, params, HttpRequestTool.CX_GET_WORK_BY_BAOANUID);
    }

    /**
     * 下载查勘作业信息
     */
//    private void dowloadSurveyView(String orderUid) {
////        LoadDialogUtil.setMessageAndShow(getActivity(),"载入中……");
//        List<String> params = new ArrayList<String>(2);
//        params.add("userId");
//        params.add(AppApplication.getUSER().data.userId);
//        params.add("orderUid");
//        params.add(orderUid);
//        HttpUtils.requestGet(URLs.CX_NEW_GET_ORDER_VIEW_BY_UID, params, HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID_SURVEY);
//    }


    /**
     * 下载标的定损任务信息
     */
//    private void dowloadBdDSView(String orderUid) {
////        LoadDialogUtil.setMessageAndShow(getActivity(),"载入中……");
//        List<String> params = new ArrayList<String>(2);
//        params.add("userId");
//        params.add(AppApplication.getUSER().data.userId);
//        params.add("orderUid");
//        params.add(orderUid);
//        HttpUtils.requestGet(URLs.CX_NEW_GET_ORDER_VIEW_BY_UID, params, HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID_BDDS);
//    }

    /**
     * 下载三者定损任务信息
     */
//    private void dowloadThDSView(String orderUid) {
////        LoadDialogUtil.setMessageAndShow(getActivity(), "载入中……");
//        List<String> params = new ArrayList<String>(2);
//        params.add("userId");
//        params.add(AppApplication.getUSER().data.userId);
//        params.add("orderUid");
//        params.add(orderUid);
//    }


    /**
     * 下载三者定损任务信息
     */
//    private void dowloadDamageView(String orderUid) {
////        LoadDialogUtil.setMessageAndShow(getActivity(),"载入中……");
//        List<String> params = new ArrayList<String>(2);
//        params.add("userId");
//        params.add(AppApplication.getUSER().data.userId);
//        params.add("orderUid");
//        params.add(orderUid);
//        HttpUtils.requestGet(URLs.CX_NEW_GET_ORDER_VIEW_BY_UID, params, HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID_DAMAGE);
//    }


    /**
     * 下载人伤任务定损任务信息
     */
//    private void dowloadInjuView(String orderUid) {
////        LoadDialogUtil.setMessageAndShow(getActivity(),"载入中……");
//        List<String> params = new ArrayList<String>(2);
//        params.add("userId");
//        params.add(AppApplication.getUSER().data.userId);
//        params.add("orderUid");
//        params.add(orderUid);
//        HttpUtils.requestGet(URLs.CX_NEW_GET_ORDER_VIEW_BY_UID, params, HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID_INJU);
//    }


    private void dowloadDictType() {
        if (cxMediaTypes!=null && cxMediaTypes.data!=null && cxMediaTypes.data.size()>0) {
            dowloadWorkImg(); return;
        }
        LoadDialogUtil.setMessageAndShow(activity,"载入中……");
        List<String> params = new ArrayList<>(2);
        params.add("orderUid");
        params.add(orderInfoEn.uid);
        HttpUtils.requestGet(URLs.CX_GET_ORDER_MEDIATYPE, params, HttpRequestTool.CX_GET_ORDER_MEDIATYPE);
    }

    /**
     * 下载接报案对应作业图片
     */
    private void dowloadWorkImg() {
        if (imgEnList!=null && imgEnList.size()>0) {
            setAdapter((String)radioBArr.get(largeClass).getTag());
        }else dowloadImg();
    }
    /**
     * 下载接报案对应作业图片
     */
    private void dowloadImg() {
        LoadDialogUtil.setMessageAndShow(activity,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("baoanUid");
        params.add(orderInfoEn.caseBaoanUid);
        params.add("isDelete");
        params.add("0");
        HttpUtils.requestGet(URLs.CX_GET_WORK_IMG, params, HttpRequestTool.CX_GET_WORK_IMG);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnetResult(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_GET_WORK_IMG: //获取案件图片列表
                LoadDialogUtil.dismissDialog();
                displayImg(values.get(0).getValue());
                break;
            case HttpRequestTool.CX_GET_ORDER_MEDIATYPE:
                dowloadWorkImg();
                LoadDialogUtil.dismissDialog();
                cxMediaTypes = JSON.parseObject(values.get(0).getValue(), CxOrderMediaTypeEntity.class);
                displayTable();
                break;
            case HttpRequestTool.UPLOAD_FILE_PHOTO:
                LoadDialogUtil.dismissDialog();
                saveImg();
                break;
            case HttpRequestTool.UPLOAD_WORK_PHOTO:
                LoadDialogUtil.dismissDialog();
                dowloadImg();
                break;
            case HttpRequestTool.CX_GET_WORK_BY_BAOANUID: //获取接报案下面的调度任务信息
                LoadDialogUtil.dismissDialog();
                getOrderListInfo(values.get(0).getValue());
                break;
//            case HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID_SURVEY: //查勘任务信息
//                LoadDialogUtil.dismissDialog();
//                getTaskWorkInfo(values.get(0).getValue());
//                break;
//            case HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID_BDDS: //标的定损信息
//                LoadDialogUtil.dismissDialog();
//                getBdDsWorkInfo(values.get(0).getValue());
//                break;
//            case HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID_THDS: //三者定损信息
//                LoadDialogUtil.dismissDialog();
//                getThDsWorkInfo(values.get(0).getValue());
//                break;
//            case HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID_DAMAGE: //物损任务信息
//                LoadDialogUtil.dismissDialog();
//                getDamageWorkInfo(values.get(0).getValue());
//                break;
//            case HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID_INJU: //人伤任务信息
//                LoadDialogUtil.dismissDialog();
//                getInjuWorkInfo(values.get(0).getValue());
//                break;
            default:
                break;
        }
    }


    /**查勘 任务作业信息
     * @param value*/
//    private void getTaskWorkInfo(String value) {
//        try {
//            surveyEn = JSON.parseObject(value, CxSurveyTaskEntity.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        dowloadDictType();
//    }

    /**标的定损 任务作业信息
     * @param value*/
//    private void getBdDsWorkInfo(String value) {
//        try {
//            bdDsEn = JSON.parseObject(value, CxDsTaskEntity.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**三者定损 任务作业信息
     * @param value*/
//    private void getThDsWorkInfo(String value) {
//        if (thDsEn==null) thDsEn = new ArrayList<>();
//        try {
//            CxDsTaskEntity bdDsEnT = JSON.parseObject(value, CxDsTaskEntity.class);
//            if (bdDsEnT != null)thDsEn.add(bdDsEnT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**三者定损 任务作业信息
     * @param value*/
//    private void getDamageWorkInfo(String value) {
//        if (damageEn==null) damageEn = new ArrayList<>();
//        try {
//            CxDamageTaskEntity damageEnT = JSON.parseObject(value, CxDamageTaskEntity.class);
//            if (damageEnT != null)damageEn.add(damageEnT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**三者定损 任务作业信息
     * @param value*/
//    private void getInjuWorkInfo(String value) {
//        if (injuEn==null) injuEn = new ArrayList<>();
//        try {
//            CxInjurySurveyWorkEntity injuEnT = JSON.parseObject(value, CxInjurySurveyWorkEntity.class);
//            if (injuEnT != null)injuEn.add(injuEnT);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**提示错误后，并在关闭dialog的时候结束*/
    private void disPlayErrorDialog() {
        DialogUtil.getErrDialogAndFinish(CxImagFragment2.this.getActivity(), "获取任务信息失败，请联系管理员！", dialog ->
                CxImagFragment2.this.getActivity().finish()).show();
    }

    /**
     * 获取接报案下面的调度任务信息 并显示
     * @param value
     */
    private void getOrderListInfo(String value) {
        try {
            baoanTaskEntity = JSON.parseObject(value, CxBaoanTaskEntity.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        dowloadDictType();
    }

    /**
     * 获取个任务对应的作业信息。
     *   case 2 :  intent.setClass(getActivity(), CxSurveyWorkActivity.class);break;  //现场查勘
     * //				case 39 :  intent.setClass(getActivity(), CxInjurySurveyActivity.class);break;  //人伤查勘
     *             case 40 :  intent.setClass(getActivity(), CxDsWorkActivity.class);break;  //标的定损 //
     *             case 41 :  intent.setClass(getActivity(), CxDsWorkActivity.class);break;  //三者定损 - 界面同“标的定损” //CxDsWorkActivity
     *             case 42 :  intent.setClass(getActivity(), CxDamageActivity.class);break;  //物损定损
     *             case 392 :  intent.setClass(getActivity(), CxInjuryTrackActivity.class);break;  //人伤跟踪
     *             case 394 :  intent.setClass(getActivity(), CxDisabyIdentifyActivity.class);break; //陪同残定
     *             case 393 :  intent.setClass(getActivity(), CxInjuryMediateActivity.class);break; //人伤调解
     *             case 395 :   //人伤调查 investigationType
     */
//    private void getWorkInfo() {
//        if (orderList!=null){
//            orderCount = 0; //需下载任务数置空
//            for ( CxOrderEntity.CxOrderTable order:orderList){
//                if (order!=null && order.bussTypeId!=null && order.bussTypeId==2) dowloadSurveyView(order.uid); //现场查勘-作业信息下载
//                else if (order!=null && order.bussTypeId!=null && order.bussTypeId==40) dowloadBdDSView(order.uid); //标的定损-作业信息下载
//                else if (order!=null && order.bussTypeId!=null && order.bussTypeId==41) dowloadThDSView(order.uid);  //三者定损-作业信息下载
//                else if (order!=null && order.bussTypeId!=null && order.bussTypeId==42) dowloadDamageView(order.uid); //物损定损-作业信息下载
//                else if (order!=null && order.bussTypeId!=null && order.bussTypeId==39) dowloadInjuView(order.uid); //人伤查勘-作业信息下载
//            }
//        }
//    }
    private List<CxOrderWorkMediaTypeTable> getInjuTable(CxOrderWorkMediaTypeTable mtTem){
        List<CxOrderWorkMediaTypeTable> injuTable = new ArrayList<>();
        if (baoanTaskEntity!=null && baoanTaskEntity.data!=null) {
            for (CxTaskModelEntity modelItem : baoanTaskEntity.data) {
                if (modelItem != null && modelItem.bussTypeId != null && modelItem.bussTypeId == 39 && !TextUtils.isEmpty(modelItem.content)) {
                    CxInjurySurveyWorkEntity injuTastItem = JSON.parseObject(modelItem.content, CxInjurySurveyWorkEntity.class);
                    if (injuTastItem != null && !TextUtils.isEmpty(injuTastItem.injuredType) && !TextUtils.isEmpty(injuTastItem.injuredName)) {
                        injuTable.add(mtTem.copyNewMediaType(mtTem.value+"_"+(injuTable.size()+1),
                                injuTastItem.injuredType+":"+injuTastItem.injuredName ));
                    }
                }
            }
        }
        return injuTable;
    }

    /**
     * 显示顶部大标题。
     */
    private void displayTable() {
        tableTypes = new ArrayList<>(5);
        if (cxMediaTypes != null && cxMediaTypes.data != null) {
            for (CxOrderWorkMediaTypeTable mtTem : cxMediaTypes.data) {
                if (mtTem != null && "1".equals(mtTem.parentId)) {
                    if ("13".equals(mtTem.value) ){ //人伤查勘的Table标题需要自动生成
                        tableTypes.addAll(getInjuTable(mtTem));
                    }else if (hasBussTypeBle(mtTem.value)){
                        tableTypes.add(mtTem);
                    }
                }
            }
        }
        disPlayTable(); //显示顶部菜单
    }

    /**
     * 有就返回true
     * @param value
     * @return
     */
    public boolean hasBussTypeBle(String value){
        if ("14".equals(value) ||"16".equals(value)) return true;
        if (baoanTaskEntity!=null && baoanTaskEntity.data!=null){
            for ( CxTaskModelEntity model:baoanTaskEntity.data){
                if (model!=null && model.bussTypeId!=null && model.bussTypeId==40 && (value.equals("11") ||value.equals("15"))) return true; //标的定损
                else if (model!=null && model.bussTypeId!=null && model.bussTypeId==2 && (value.equals("10"))) return true;  //现场查勘
                else if (model!=null && model.bussTypeId!=null && model.bussTypeId==41 && (value.equals("11") ||value.equals("15"))) return true;  //三者定损
                else if (model!=null && model.bussTypeId!=null && model.bussTypeId==42 && value.equals("12")) return true; //物损定损
//                else if (model!=null && model.bussTypeId!=null && model.bussTypeId==39 && value.equals("13")) return true; //人伤查勘
            }
        }
        return false;
    }

    /**
     * 显示顶部菜单，并绑定事件。
     */
    private void disPlayTable() {
        radioBArr = new ArrayList<>(10);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        radgroup.removeAllViews();
        for (int i = 0;i<tableTypes.size();i++){  //for循环给RadioGroup中添加RadioButton
            RadioButton rb = (RadioButton) inflater.inflate(R.layout.table_layout_radiobutton,null);
            radgroup.addView(rb);
            if (i==largeClass) radgroup.check(rb.getId());  //设置显示的button
            radioBArr.add(rb); //收集添加的RadioButton
            rb.setText(tableTypes.get(i)!=null?tableTypes.get(i).label:"No Title");
            rb.setTag(tableTypes.get(i).value); //在每一个RadioButton选项卡中装入父级id信息（parentId），也就是当前CxOrderWorkMediaTypeTable对象的Value值
        }
        radgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                for (int i = 0;i<radioBArr.size();i++){
                    if (arg1 == radioBArr.get(i).getId()){
                        largeClass=i;
                        setAdapter((String)radioBArr.get(i).getTag());
                        break;
                    }
                }
            }
        });
    }

    /**上传影像资料成功后刷新界面**/
    @Subscribe(threadMode=ThreadMode.MAIN)
    public void eventmeth(String successCode){
        if ("UPLOAD_SUCCESS".equals(successCode)) {
//            photoListView.deferNotifyDataSetChanged();
            dowloadImg();
        }
    }

    private void displayImg(String value) {
        imgEnList = JSON.parseArray(value,CxImagEntity.class);
        setAdapter(radioBArr==null?"":(String)radioBArr.get(largeClass).getTag());
    }

    private void setAdapter(String parentId){
        adapter = new CxImagAdapter2(activity, imgEnList, cxMediaTypes, new SaveImgCallBack() {
            @Override
            public void addImg(List<CxImagEntity> imgList,int position) {
                imgEnList.addAll(imgList);
                adapter.notifyDataSetChanged();
                photoListView.collapseGroup(position); //收起
                photoListView.expandGroup(position); //展开
            }
            @Override
            public void deleteImg(CxImagEntity deleteImgEn,int position) {
                imgEnList.remove(deleteImgEn);
                adapter.notifyDataSetChanged();
                photoListView.collapseGroup(position); //收起
                photoListView.expandGroup(position); //展开
            }
        },parentId,QorderUid,CxImagFragment2.this);
        photoListView.setAdapter(adapter);
    }

    @Override
    public void SaveDataToEntity() {
    }
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();  }
    @Override
    public void onResume() {
        super.onResume();
        displayTable();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }  }
    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
