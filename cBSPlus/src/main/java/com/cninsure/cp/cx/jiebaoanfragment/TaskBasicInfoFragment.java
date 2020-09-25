package com.cninsure.cp.cx.jiebaoanfragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.cx.CxSurveyWorkActivity;
import com.cninsure.cp.cx.CxWorkhelp;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxBaoanTable;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.entity.cx.CxSurveyTaskEntity;
import com.cninsure.cp.entity.cx.CxSurveyWorkEntity;
import com.cninsure.cp.entity.cx.JieBaoanEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class TaskBasicInfoFragment extends BaseFragment {



    private View contentView ,footerView;
    private ListView mlistView;
    private LayoutInflater inflater;
    private CxJieBaoanInfoActivity activity;
    private MyOrderInfoAdapter adapter;

    private JieBaoanEntity baoanInfo; //接报案信息
    public CxSurveyTaskEntity cxTaskWorkEntity; //包含作业信息的任务信息
    private CxSurveyWorkEntity workEntity;  //作业信息
    public CxDictEntity cxSurveyDict = new CxDictEntity(); //拍照类型字典数据
    @ViewInject(R.id.CxBaBasicFgmt_list)  ListView orderListView;  //任务列表ListView
    @ViewInject(R.id.CxBaBasicFgmt_button) Button workButton;  //作业按钮


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cx_baoan_basic_info_fragment,null);
        activity = (CxJieBaoanInfoActivity) getActivity();
        ViewUtils.inject(this,contentView);
        dowloadDictType();
        setJumpOnclick();//跳转到作业界面
        return contentView;
    }


    private void dowloadDictType() {
        LoadDialogUtil.setMessageAndShow(activity,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("type");
        params.add("cxOrderWorkImageType,accident_type,accident_small_type,accident_reason,accident_small_reason,survey_type,damage_loss_type," +
                "accident_liability,loss_type,loss_object_type,compensation_method,survey_conclusion,carno_type,car_usetype,injured_type");
        HttpUtils.requestGet(URLs.CX_NEW_GET_IMG_TYPE_DICT, params, HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT);
    }

    /***chax*/
    private void dowloadOderView() {
        LoadDialogUtil.setMessageAndShow(activity,"载入中……");
        List<String> params = new ArrayList<String>(2);
        params.add("userId");
        params.add(AppApplication.getUSER().data.userId);
        params.add("orderUid");
        params.add(activity.QorderUid);
        HttpUtils.requestGet(URLs.CX_NEW_GET_ORDER_VIEW_BY_UID, params, HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID);
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void evnetH5(List<NameValuePair> values) {
        int responsecode = Integer.parseInt(values.get(0).getName());
        switch (responsecode) {
            case HttpRequestTool.CX_NEW_GET_IMG_TYPE_DICT:
                dowloadOderView();
                LoadDialogUtil.dismissDialog();
                cxSurveyDict.list = JSON.parseArray(values.get(0).getValue(), CxDictEntity.DictData.class);
                break;
            case HttpRequestTool.UPLOAD_FILE_PHOTO: //上传附件成功
//                fg1.getUploadFileInfo(values);
                break;
            case HttpRequestTool.CX_NEW_GET_ORDER_VIEW_BY_UID: //获取订单信息
                LoadDialogUtil.dismissDialog();
                getTaskWorkInfo(values.get(0).getValue());
                break;
            default:
                break;
        }
    }

    /**解析获取的到的任务作业信息
     * @param value*/
    private void getTaskWorkInfo(String value) {
        try {
            cxTaskWorkEntity = JSON.parseObject(value, CxSurveyTaskEntity.class);
        } catch (Exception e) {  //解析失败，关闭界面
            disPlayErrorDialog();
            e.printStackTrace();
        }
        //没有数据就初始化一下
        if (cxTaskWorkEntity == null )  cxTaskWorkEntity = new CxSurveyTaskEntity();
        if (cxTaskWorkEntity.data == null  ) cxTaskWorkEntity.data = new CxSurveyTaskEntity.CxTaskSurveyEntity();
        if (cxTaskWorkEntity.data.contentJson == null) cxTaskWorkEntity.data.contentJson = new CxSurveyWorkEntity();
        workEntity = cxTaskWorkEntity.data.contentJson;
        adapter = new MyOrderInfoAdapter(); //加载adapter显示数据
        orderListView.setAdapter(adapter);
    }

    private void setJumpOnclick(){
        workButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setJumpToWorkActivity();
            }
        });
    }

    private void setJumpToWorkActivity() {
        Intent intent = new Intent();
		intent.setClass(getActivity(), CxSurveyWorkActivity.class);  //现场查勘
        intent.putExtra("orderUid",activity.getIntent().getStringExtra("orderUid"));
        intent.putExtra("taskType", activity.getIntent().getStringExtra("taskType"));
        intent.putExtra("status", activity.getIntent().getStringExtra("status"));
        intent.putExtra("PublicOrderEntity", activity.getIntent().getSerializableExtra("PublicOrderEntity"));
        getActivity().startActivity(intent);
    }


    /**提示错误后，并在关闭dialog的时候结束*/
    private void disPlayErrorDialog() {
        DialogUtil.getErrDialogAndFinish(activity, "获取任务信息失败，请联系管理员！", new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.finish();
            }
        }).show();
    }






    @Override
    public void SaveDataToEntity() {  }
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();  }
    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }  }
    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private class MyOrderInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder= new ViewHolder();
            convertView = inflater.inflate(R.layout.cx_baoan_basic_info_fragment_item,null);
            ViewUtils.inject(vHolder,convertView);
            switch (position){
                case 0://订单号
                    vHolder.title.setText("▍订单号:");
                    vHolder.content.setVisibility(View.GONE);
                    SetTextUtil.setTextViewText(vHolder.fTitle,activity.orderInfoEn.uid);
                    break;
                case 1://费用信息
                    vHolder.title.setText("▍费用信息:");
                    vHolder.content.setVisibility(View.GONE);
                    SetTextUtil.setTextViewText(vHolder.fTitle,activity.orderInfoEn.bargainPrice+"");
                    break;
                case 2://审核信息
                    vHolder.title.setText("▍审核信息:");
                    vHolder.content.setVisibility(View.GONE);
                    break;
                case 3:
                    vHolder.title.setText("▍标的信息:");
                    SetTextUtil.setTextViewText(vHolder.content,getBiaodiInfoText());
                    break;
                case 4:
                    vHolder.title.setText("▍被保险人银行卡信息:");
                    SetTextUtil.setTextViewText(vHolder.content,getBeiBXRbankInfoText());
                    break;
                case 5:
                    vHolder.title.setText("查勘信息:");
                    SetTextUtil.setTextViewText(vHolder.content,getsSurveyInfoText());
                    break;
                case 6:
                    vHolder.title.setText("▍三者信息:");
                    vHolder.content.setVisibility(View.GONE);
                    break;
                case 7:
                    vHolder.title.setText("▍物损信息:");
                    vHolder.content.setVisibility(View.GONE);
                    break;
                case 8:
                    vHolder.title.setText("▍人伤信息:");
                    vHolder.content.setVisibility(View.GONE);
                    break;
                case 9:
                    vHolder.title.setText("▍查勘结论:");
                    SetTextUtil.setTextViewText(vHolder.content,getSurveyConclusionInfoText());
                    break;
                case 10:
                    vHolder.title.setText("▍签名（分享告知书）:");
                    disPlaySign(vHolder.contentLineLayout);
                    break;
                case 11:
                    vHolder.title.setText("▍附件信息:");
                    SetTextUtil.setTextViewText(vHolder.content,getEnclosureListInfoText());
                    break;

            }
            return convertView;
        }
    }

    /***
     * 查勘结论信息
     * @return
     */
    private String getSurveyConclusionInfoText() {
        if (workEntity.subjectInfo==null) workEntity.subjectInfo = new CxSurveyWorkEntity.SubjectInfoEntity();
        StringBuffer sb = new StringBuffer();
        CxSurveyWorkEntity.SurveyInfoEntity suInTemp = workEntity.surveyInfo;
        sb.append("赔付方式："+getTextInfo(activity.cxDict.getLabelByValue("compensation_method",suInTemp!=null?suInTemp.compensationMethod+"":""))+"\n");
       Integer ckIsInsuranceLiabilityItg = suInTemp!=null?suInTemp.ckIsInsuranceLiability:-1;
        sb.append("是否属于保险责任：" + (ckIsInsuranceLiabilityItg==null?"--":(ckIsInsuranceLiabilityItg==1?"是":(ckIsInsuranceLiabilityItg==0?"否":"--") +"\n")));
        Integer isDaiweiItg = suInTemp!=null?suInTemp.isDaiwei:-1;
        sb.append("是否代位："+ (isDaiweiItg==null?"--":(isDaiweiItg==1?"是":(isDaiweiItg==0?"否":"--" ) +"\n")));
        sb.append("估损金额："+getTextInfo(suInTemp!=null?suInTemp.lossAmount:""+"\n"));
        Integer isSceneItg = suInTemp!=null?suInTemp.isScene:-1;
        sb.append("是否现场报案："+(isSceneItg==null?"--":(isSceneItg==1?"是":(isSceneItg==0?"否":"--")))+"\n");
        Integer isHsLoadItg = suInTemp!=null?suInTemp.isHsLoad:-1;
        sb.append("是否在高速公路："+(isHsLoadItg==null?"--":(isHsLoadItg==1?"是":(isHsLoadItg==0?"否":"--")))+"\n");
        sb.append("查勘结论："+getTextInfo(activity.cxDict.getLabelByValue("survey_conclusion",suInTemp!=null?suInTemp.surveyConclusion+"":"--") +"\n"));
        sb.append("查勘概述："+getTextInfo(suInTemp!=null?suInTemp.surveySummary:"")+"\n");
        Integer ckIsMajorCaseItg = suInTemp!=null?suInTemp.ckIsMajorCase:-1;
        sb.append("是否标为重大案件："+ (ckIsMajorCaseItg==null?"--":(ckIsMajorCaseItg==1?"是":(ckIsMajorCaseItg==0?"否":"--")))+"\n");
        return sb.toString();
    }

    /**
     * 显示签字图片
     * @param contentLineLayout
     */
    private void disPlaySign(LinearLayout contentLineLayout) {
        if (workEntity.surveyInfo!=null && !TextUtils.isEmpty(workEntity.surveyInfo.signLicense)){
            String imgPath=AppApplication.getUSER().data.qiniuUrl+ workEntity.surveyInfo.signLicense;
            ImageView img = new ImageView(activity);
            Glide.with(getActivity()).load(imgPath).into( img);
            contentLineLayout.addView(img);
        }
    }

    /**
     * 返回附件信息列表
     * @return
     */
    private String getEnclosureListInfoText() {
        if (workEntity.subjectInfo==null) workEntity.surveyInfo = new CxSurveyWorkEntity.SurveyInfoEntity();
        StringBuffer sb = new StringBuffer();
        if (workEntity.surveyInfo.enclosureList==null) return "";
        for (String url:workEntity.surveyInfo.enclosureList)  sb.append(url+"\n");
        return  sb.toString();
    }

    /**
     * 获取 查勘信息 文本
     * @return
     */
    private String getsSurveyInfoText() {
        if (workEntity.subjectInfo==null) workEntity.subjectInfo = new CxSurveyWorkEntity.SubjectInfoEntity();
        StringBuffer sb = new StringBuffer();
        CxSurveyWorkEntity.SurveyInfoEntity suInTemp = workEntity.surveyInfo;
        sb.append("查勘时间："+getTextInfo(suInTemp!=null?suInTemp.ckDate:"")+"\n");
        sb.append("事故类型："+getTextInfo(activity.cxDict.getLabelByValue("accident_type",suInTemp!=null?suInTemp.ckAccidentType+"":""))+"\n");
        sb.append("事故详细类型："+getTextInfo(activity.cxDict.getLabelByValue("accident_small_type",suInTemp!=null?suInTemp.ckAccidentSmallType+"":""))+"\n");
        sb.append("出险原因："+getTextInfo(activity.cxDict.getLabelByValue("accident_reason",suInTemp!=null?suInTemp.ckAccidentReason+"":""))+"\n");
        sb.append("查勘类型："+getTextInfo(activity.cxDict.getLabelByValue("survey_type",suInTemp!=null?suInTemp.surveyType+"":""))+"\n");
        sb.append("事故责任："+getTextInfo(activity.cxDict.getLabelByValue("accident_liability",suInTemp!=null?suInTemp.ckAccidentLiability+"":""))+"\n");
        sb.append("责任比例（%）："+getTextInfo(suInTemp!=null?suInTemp.liabilityRatio:"")+"\n");
        sb.append("损失类型："+getTextInfo(suInTemp!=null?getLossTypeText(suInTemp.lossType):"")+"\n");
        sb.append("损失情况："+getTextInfo(getLossObjectTypeText())+"\n");
        sb.append("报案驾驶员："+getTextInfo(suInTemp!=null?suInTemp.baoanDriverName:"")+"\n");
        sb.append("车辆能否正常行驶："+getTextInfo(suInTemp!=null?(suInTemp.canDriveNormally==1?"是":(suInTemp.canDriveNormally==0?"否":"--")):""+"\n"));
        return sb.toString();
    }

    /**
     * 损失类型
     * @param lossType
     * @return
     */
    private String getLossTypeText(Integer [] lossType){
        StringBuffer sb = new StringBuffer();
        if (lossType==null) return "";
        for (int i:lossType){
            switch (i){ //0三者、1物损、2人伤
                case 0: sb.append("三者");break;
                case 1: sb.append("物损\n");break;
                case 2: sb.append("人伤");break;
            }
        }
        return sb.toString();
    }

    /**
     * 损失情况
     * @return
     */
    private String getLossObjectTypeText(){
        StringBuffer sb = new StringBuffer();
        if (workEntity.surveyInfo==null || workEntity.surveyInfo.lossObjectType==null) return "";
        for (int i:workEntity.surveyInfo.lossObjectType){
            sb.append(activity.cxDict.getLabelByValue("loss_object_type",i+"")+"\n");
        }
        return sb.toString();
    }

    /**
     * 获取 被保险人银行卡信息 文本
     * @return
     */
    private String getBeiBXRbankInfoText() {
        if (workEntity.subjectInfo==null) workEntity.subjectInfo = new CxSurveyWorkEntity.SubjectInfoEntity();
        StringBuffer sb = new StringBuffer();
        sb.append("持卡人姓名："+getTextInfo(workEntity.subjectInfo.insuredPersonName)+"\n");
        sb.append("开户行："+getTextInfo(workEntity.subjectInfo.insuredBankDeposit)+"\n");
        sb.append("卡号："+getTextInfo(workEntity.subjectInfo.insuredBankNo)+"\n");
        return sb.toString();
    }

//    标的信息
    private String getBiaodiInfoText() {
        StringBuffer sb = new StringBuffer();
        if (workEntity.subjectInfo==null) workEntity.subjectInfo = new CxSurveyWorkEntity.SubjectInfoEntity();
        CxSurveyWorkEntity.SubjectInfoEntity subjectInfo = workEntity.subjectInfo;
        sb.append("车牌号："+getTextInfo(subjectInfo!=null?subjectInfo.bdCarNumber:"")+"\n"); //车牌号
        sb.append("车架号："+getTextInfo(subjectInfo!=null?subjectInfo.bdCarVin:"")+"\n"); //车架号
        sb.append("发动机号："+getTextInfo(subjectInfo!=null?subjectInfo.bdEngineNo:"")+"\n"); //发动机号
        sb.append("初登日期："+getTextInfo(subjectInfo!=null?subjectInfo.bdCarRegisterDate:"")+"\n"); //初登日期
        sb.append("行驶证有效期至："+getTextInfo(subjectInfo!=null?subjectInfo.bdCarEffectiveDate:"")+"\n"); //行驶证有效期至
        sb.append("准驾车型："+getTextInfo(subjectInfo!=null?subjectInfo.bdDrivingType:"")+"\n"); //准驾车型
        sb.append("号牌种类："+getTextInfo(activity.cxDict.getLabelByValue("carno_type",subjectInfo.bdCarNumberType+""))+"\n"); //号牌种类  szCarNumberType
        sb.append("使用性质："+getTextInfo(activity.cxDict.getLabelByValue("car_usetype",subjectInfo.bdCarUseType+""))+"\n"); //使用性质
        sb.append("驾驶员姓名："+getTextInfo(subjectInfo!=null?subjectInfo.bdDriverName:"")+"\n"); //驾驶员姓名
        sb.append("驾驶员电话："+getTextInfo(subjectInfo!=null?subjectInfo.bdDriverPhone:"")+"\n"); //驾驶员电话
        sb.append("初次领证日期："+getTextInfo(subjectInfo!=null?subjectInfo.bdDriverRegisterDate:"")+"\n"); //初次领证日期
        sb.append("有效起始日期："+getTextInfo(subjectInfo!=null?subjectInfo.bdDriverEffectiveStar:"")+"\n"); //有效起始日期
        sb.append("驾驶证有效期至："+getTextInfo(subjectInfo!=null?subjectInfo.bdDriverEffectiveEnd:"")+"\n");
        sb.append("行驶证是否有效："+getTextInfo(subjectInfo.bdCardIsEffective==0?"未验":
                (subjectInfo.bdCardIsEffective==1?"有效":"无效"))+"\n");
        sb.append("驾驶证："+getTextInfo(subjectInfo!=null?subjectInfo.bdDriverNo:"")+"\n");
        sb.append("查勘地点："+getTextInfo(workEntity.surveyInfo!=null?workEntity.surveyInfo.surveyAddress:"")+"\n");

        return sb.toString();
    }

    public String getTextInfo(String str){
        if (TextUtils.isEmpty(str))
            return "--";
        else return str;
    }

    private class ViewHolder{
        @ViewInject(R.id.CxBaBasicFgmt_Title) TextView title;
        @ViewInject(R.id.CxBaBasicFgmt_fTitle) TextView fTitle;
        @ViewInject(R.id.CxBaBasicFgmt_TexView) TextView content;
        @ViewInject(R.id.CxBaBasicFgmt_LinearLayout) LinearLayout contentLineLayout;
    }
}
