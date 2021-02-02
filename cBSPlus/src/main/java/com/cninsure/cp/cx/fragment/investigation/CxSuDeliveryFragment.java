package com.cninsure.cp.cx.fragment.investigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxInjuryExamineActivity;
import com.cninsure.cp.cx.fragment.BaseFragment;
import com.cninsure.cp.cx.util.CxFileUploadUtil;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.injurysurvey.InjuryExamineWorkEntity;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.FileChooseUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CxSuDeliveryFragment extends BaseFragment implements View.OnClickListener {

    private View contentView;
    private CxInjuryExamineActivity activity;
    private boolean isDeliveryBill=false;  //是否选择的文件是快递单  true是，false否
    private LayoutInflater inflater;

    @ViewInject(R.id.cxInMe_deliveryMode_RG)  RadioGroup deliveryMode;    //快递方式  0自行送达、1到付
    @ViewInject(R.id.cxInMe_deliveryCompany) TextView deliveryCompany;    //快递公司  0、韵达	1、中通快递	2、宅急送	3、EMS	4、圆通快递	5、顺丰快递	6、申通快递
    @ViewInject(R.id.cxInMe_deliveryNo)  EditText companyNo;    //快递单号
    @ViewInject(R.id.cxInMe_consignee) EditText consignee;    //收货人
    @ViewInject(R.id.cxInMe_shippingAddress) EditText shippingAddress;    //收货地址
    @ViewInject(R.id.cxInMe_deliveryBill) TextView deliveryBill;    //快递单
    @ViewInject(R.id.cxInMe_enclosureList_add) TextView enclosureAddTv;    //上传附件按钮
    @ViewInject(R.id.cxInMe_enclosureList_line) LinearLayout enclosureLinear;    //附件信息列表

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        contentView = inflater.inflate(R.layout.cxrs_injury_mediate_scfj_fragment, null);
        activity = (CxInjuryExamineActivity) getActivity();
        ViewUtils.inject(this,contentView);
        intView();
        return contentView;
    }

    private void intView() {
        TypePickeUtil.setTypePickerDialog(activity,deliveryCompany,activity.cxDict,"deliveryCompany");//损失类型 绑定点击事件
        enclosureAddTv.setOnClickListener(this); //上传附件按钮点击，选择文件
        deliveryBill.setOnClickListener(this); //点击上传快递单
        displayDamageInfo();
    }

    /**显示任务信息*/
    private void displayDamageInfo() {
        if (activity.taskEntity!=null && activity.taskEntity.data!=null && activity.taskEntity.data.contentJson!=null);
        else return;
        InjuryExamineWorkEntity workEntity = activity.taskEntity.data.contentJson;
        //快递方式  0自行送达、1到付
        if ("0".equals(workEntity.deliveryMode)) deliveryMode.check(R.id.cxInMe_deliveryMode_RBT);
        if ("1".equals(workEntity.deliveryMode)) deliveryMode.check(R.id.cxInMe_deliveryMode_RBF);
//        SetTextUtil.setTvTextForArr(deliveryCompany,TypePickeUtil.getDictLabelArr(activity.cxDict.getDictByType("deliveryCompany")),Integer.parseInt(workEntity.deliveryCompany));   //快递公司  0、韵达	1、中通快递	2、宅急送	3、EMS	4、圆通快递	5、顺丰快递	6、申通快递
        SetTextUtil.setTextViewText(deliveryCompany,TypePickeUtil.getLable(workEntity.deliveryCompany,activity.cxDict,"deliveryCompany"));   //快递公司  0、韵达	1、中通快递	2、宅急送	3、EMS	4、圆通快递	5、顺丰快递	6、申通快递
        SetTextUtil.setEditText(companyNo,workEntity.deliveryNo) ; //快递单号
        SetTextUtil.setEditText(consignee,workEntity.consignee) ; //收货人
        SetTextUtil.setEditText(shippingAddress,workEntity.shippingAddress) ;  //收货地址
        SetTextUtil.setTextViewText(deliveryBill,workEntity.deliveryBill);  //快递单
        displayFileToList() ;  //附件信息列表
    }

    /***保存数据到实体类*/
    @Override
    public void SaveDataToEntity() {
        if (activity==null) return;  //activity说明没有初始化这个Fragment，也就没有任何操作，没有不要保存了，
        InjuryExamineWorkEntity workEntity  = activity.taskEntity.data.contentJson;
        //快递方式  0自行送达、1到付
        switch (deliveryMode.getCheckedRadioButtonId()){
            case R.id.cxInMe_deliveryMode_RBT: workEntity.deliveryMode="0";break;
            case R.id.cxInMe_deliveryMode_RBF: workEntity.deliveryMode="1";break;
        }
        workEntity.deliveryCompany = TypePickeUtil.getValue(deliveryCompany.getText().toString(),activity.cxDict,"deliveryCompany")+""; //快递公司
        workEntity.deliveryNo = companyNo.getText().toString()  ;  //快递单号
        workEntity.consignee = consignee.getText().toString()  ; //收货人
        workEntity.shippingAddress = shippingAddress.getText().toString() ;  //收货地址
        workEntity.deliveryBill = deliveryBill.getText().toString(); //快递单
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cxInMe_enclosureList_add:
                PickPhotoUtil.albumPhoto(activity, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE);
                isDeliveryBill = false;
                break; //上传附件按钮点击，选择文件
            case R.id.cxInMe_deliveryBill:
                PickPhotoUtil.albumPhoto(activity, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_CX_FILE);
                isDeliveryBill = true;
                break; //点击上传快递单
        }
    }

    /**
     * 判断文件大小是否小于20M,小于就上传。
     * @param data
     */
    public void inspectFileSize(Intent data) {
        if (data==null) return;
        String FilePath = FileChooseUtil.getInstance(activity).getChooseFileResultPath(data.getData());
        File fileTemp = new File(FilePath);
        if (fileTemp!=null && fileTemp.length()>0 && (fileTemp.length() < 20971520)) { //必须小于20M（20971520 byte）
            List<NameValuePair> fileUrls = new ArrayList<NameValuePair>();
            fileUrls.add(new BasicNameValuePair("0", FilePath));
            CxFileUploadUtil.uploadCxFile(activity, fileUrls, URLs.UPLOAD_FILE_PHOTO,null); //上传
        }
    }

    /**显示上传成功的附件*/
    public void getUploadFileInfo(List<NameValuePair> values) {
        String UpedFileName = values.get(0).getValue();
        if (isDeliveryBill){
            activity.taskEntity.data.contentJson.deliveryBill = UpedFileName;
            deliveryBill.setText(UpedFileName);
        }else{
            if (activity.taskEntity.data.contentJson.enclosureList==null)activity.taskEntity.data.contentJson.enclosureList = new ArrayList<>();
            activity.taskEntity.data.contentJson.enclosureList.add(UpedFileName);
            displayFileToList();
        }
    }

    private void displayFileToList() {
        enclosureLinear.removeAllViews();  //添加前清空，避免重复加载
        InjuryExamineWorkEntity workEntity = activity.taskEntity.data.contentJson;
        if (workEntity.enclosureList==null)workEntity.enclosureList = new ArrayList<>();
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
}
