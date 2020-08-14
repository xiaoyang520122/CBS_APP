package com.cninsure.cp.cx.fragment;

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
import com.cninsure.cp.cx.CxInjuryMediateActivity;
import com.cninsure.cp.cx.CxInjuryTrackActivity;
import com.cninsure.cp.cx.util.CxFileUploadUtil;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxInjuryTrackWorkEntity;
import com.cninsure.cp.entity.cx.InjuryMediateWorkEntity;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.ActivityFinishUtil;
import com.cninsure.cp.utils.FileChooseUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CxRiDeliveryFragment  extends BaseFragment implements View.OnClickListener {

    private View contentView;
    private CxInjuryMediateActivity activity;
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
        activity = (CxInjuryMediateActivity) getActivity();
        ViewUtils.inject(this,contentView);
        intView();
        return contentView;
    }

    private void intView() {
        enclosureAddTv.setOnClickListener(this); //上传附件按钮点击，选择文件
        deliveryBill.setOnClickListener(this); //点击上传快递单
    }

    /***保存数据到实体类*/
    @Override
    public void SaveDataToEntity() {

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
        InjuryMediateWorkEntity workEntity = activity.taskEntity.data.contentJson;
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
