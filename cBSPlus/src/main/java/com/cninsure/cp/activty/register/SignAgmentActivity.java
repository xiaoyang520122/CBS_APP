package com.cninsure.cp.activty.register;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.activty.AddBanckCardActivity;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.ctagreement.SignAgreementRequest;
import com.cninsure.cp.entity.extract.ExtUserEtity;
import com.cninsure.cp.entity.yjx.ImagePathUtil;
import com.cninsure.cp.ocr.LinePathActivity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ImageDisplayUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PDF.PDFShowUtil;
import com.cninsure.cp.utils.PhotoChoiceActivity;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.UriUtils;
import com.cninsure.cp.utils.photo.OutputTool;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author :xy-wm
 * date:2020/11/23 15:26
 * usefuLness: CBS_APP
 */
public class SignAgmentActivity extends BaseActivity {

    private ExtUserEtity extUserEtity; //外部查勘员信息
    private int CAMERA_F=1;
    private int CAMERA_B=2;

    @ViewInject(R.id.IDCAC_card_first) private ImageView cardImgF; //身份证正面
    @ViewInject(R.id.IDCAC_card_scend) private ImageView cardImgB; //身份证反面
    @ViewInject(R.id.IDCAC_name) private TextView userName; //公估师名称
    @ViewInject(R.id.IDCAC_No) private TextView idCardName; //身份证号码
    @ViewInject(R.id.IDCAC_next) private Button nextButton; //下一步
    @ViewInject(R.id.signAgreement_display) private Button showAgreementButton; //查看协议
    @ViewInject(R.id.signAgreement_statu) private TextView signStatuTv; //协议签署状态
    @ViewInject(R.id.signAgreement_cancel) private TextView cancelTv; //返回按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_card_activity);
        ViewUtils.inject(this);
        EventBus.getDefault().register(this);
        extUserEtity = (ExtUserEtity) getIntent().getSerializableExtra("extUserEtity");
        initView();
        displayInfo();
    }

    /**
     * 设置单击事件
     */
    private void initView() {
        cardImgF.setOnClickListener(v -> {
            showOrCamrea(CAMERA_F);
        });
        cardImgB.setOnClickListener(v -> {
            showOrCamrea(CAMERA_B);
        });
        nextButton.setOnClickListener(v -> {
            canNext();
        });
        cancelTv.setOnClickListener(v -> {
            SignAgmentActivity.this.finish();
        });
    }
     /**拍摄或者显示照片，如果已经签署协议就只能查看，如果没有签署协议就只能拍照*/
     private void showOrCamrea(int code){
         if (extUserEtity!=null && extUserEtity.data!=null && extUserEtity.data.status!=null && extUserEtity.data.status==1){
             if (code == CAMERA_F) ImageDisplayUtil.displayByMyView(this,extUserEtity.data.idCardPhotoFront); //显示正面
             if (code == CAMERA_B) ImageDisplayUtil.displayByMyView(this, extUserEtity.data.idCardPhotoBack);//显示反面
     }else{
             if (code == CAMERA_F) OutputTool.getInstance().takeCamera(SignAgmentActivity.this,CAMERA_F); //拍摄正面
             if (code == CAMERA_B)  OutputTool.getInstance().takeCamera(SignAgmentActivity.this,CAMERA_B); //拍摄反面
         }
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ) {  //拍照返回
            try {
                if (requestCode == CAMERA_F){
                    String url = OutputTool.getInstance().getmCurrentPhotoPath();
                    Uri imgUrl = UriUtils.getUri(this,new File(url));
                    uploadPhoto(OutputTool.getInstance().getmCurrentPhotoPath(),HttpRequestTool.UP_ID_CARD_F); //上传身份证正面
                }else if (requestCode == CAMERA_B){
                    String url = OutputTool.getInstance().getmCurrentPhotoPath();
                    Uri imgUrl = UriUtils.getUri(this,new File(url));
                    uploadPhoto(OutputTool.getInstance().getmCurrentPhotoPath(),HttpRequestTool.UP_ID_CARD_B); //上传身份证反面
                }
            } catch (Exception e) {
                ToastUtil.showToastLong(this,"拍照失败！"); //有时候 Uri imgUrl = UriUtils.getUri(this,new File(url));会报空指针异常。
                e.printStackTrace();
            }
        }
    }

    /**上传签字图片**/
    private void upSignPhoto(Intent data,int type) {//(String)data.getStringExtra("LinePathFilePath");
        if (null!=data&&null!=data.getStringExtra("LinePathFilePath")) {
            List<NameValuePair> fileUrls=new ArrayList<NameValuePair>();
            fileUrls.add(new BasicNameValuePair("0", (String)data.getStringExtra("LinePathFilePath")));
            PhotoUploadUtil.uploadOCR(this, fileUrls, URLs.UP_OCR_PHOTO, type);
        }
    }

    /**上传照片**/
    private void uploadPhoto(String imgPath,int code) {
//        LoadDialogUtil.setMessageAndShow(this,"上传中……");
        List<NameValuePair> fileUrls=new ArrayList<NameValuePair>();
        fileUrls.add(new BasicNameValuePair("0", imgPath));
        PhotoUploadUtil.uploadOCR(this, fileUrls, URLs.UP_OCR_PHOTO, code);
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void eventmeth(List<NameValuePair> responseValue){
        LoadDialogUtil.dismissDialog();

        try {
            int type=Integer.parseInt(responseValue.get(0).getValue());
            if (type==HttpRequestTool.UP_ID_CARD_F) { //身份证正面
                extUserEtity.data.idCardPhotoFront = ImagePathUtil.BaseUrl+responseValue.get(1).getValue();
            }else if (HttpRequestTool.UP_ID_CARD_B == type){ //身份证反面
                extUserEtity.data.idCardPhotoBack = ImagePathUtil.BaseUrl+responseValue.get(1).getValue();  //照片全称
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        try {
            int type2=Integer.parseInt(responseValue.get(0).getName());
            if (HttpRequestTool.EXTACT_USER == type2){ //保存身份证照片路径
                getIdCardUpRequest(responseValue.get(0).getValue());
            }else if (HttpRequestTool.EXTACT_USER_SIGN == type2){ //签署协议
                getSignRequestInfo(responseValue.get(0).getValue());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        displayIdCard();
    }

    private void getSignRequestInfo(String value) {
        SignAgreementRequest saEn = JSON.parseObject(value,SignAgreementRequest.class);
        if (saEn.success){  //签署协议成功提示用户后关闭该界面
            DialogUtil.getRightDialogAndFinish(this, "成功签署协议！", dialog -> SignAgmentActivity.this.finish()).show();
        }else{ //签署失败提示用户
            DialogUtil.getAlertOneButton(this,"签署失败！！",null).show();
        }
    }

    /**获取签字图上传信息，上传成功就发起协议签署*/
    private void getSignImgUpInfo(String value) {
        if (!TextUtils.isEmpty(value)){
            requestSign(value); //发起协议签署
        }
    }

    /**
     * 发起协议签署
     * @param s 签字图路径
     */
    private void requestSign(String s) {
        List<NameValuePair> NVparames = new ArrayList<NameValuePair>(1);
        NVparames.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        NVparames.add(new BasicNameValuePair("id", extUserEtity.data.id+""));
        NVparames.add(new BasicNameValuePair("requestUrl", s));  //签字图路径
        HttpUtils.requestPost(URLs.EXTACT_USER_SIGN,NVparames, HttpRequestTool.EXTACT_USER_SIGN);
        LoadDialogUtil.setMessageAndShow(this,"努力加载中……");
    }

    /**是否保存成功，成功跳转到协议界面*/
    private void getIdCardUpRequest(String value) {
        BaseEntity ben = JSON.parseObject(value,BaseEntity.class);
        if (ben.success){ //修改身份证路径上传成功
            alertAgreement();  //去签约界面
        }else{ //修改路径失败提示用户
            DialogUtil.getAlertOneButton(this,"提交身份证信息失败，请重试！",null).show();
        }
    }

    /**
     * 判断是否能继续下一步操作。
     */
    private void canNext() {
        if (!TextUtils.isEmpty(extUserEtity.data.idCardPhotoFront) && !TextUtils.isEmpty(extUserEtity.data.idCardPhotoBack)){
            submitIdCard(); //保存身份证照片路径
        }else{
            DialogUtil.getAlertOneButton(this,"请先补充身份证照片信息！",null).show();
        }
    }

    /**
     * 保存身份证照片路径，成功后弹出协议信息并签署
     */
    public void submitIdCard(){
        List<NameValuePair> NVparames = new ArrayList<NameValuePair>(1);
        NVparames.add(new BasicNameValuePair("uid", AppApplication.getUSER().data.userId));
        NVparames.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
        NVparames.add(new BasicNameValuePair("id", extUserEtity.data.id+""));
        NVparames.add(new BasicNameValuePair("mobile", extUserEtity.data.mobile));  //多余参数传递，避免后台报错
        NVparames.add(new BasicNameValuePair("idCardPhotoFront", extUserEtity.data.idCardPhotoFront));  //身份证正面
        NVparames.add(new BasicNameValuePair("idCardPhotoBack", extUserEtity.data.idCardPhotoBack));  //身份背面
        HttpUtils.requestPost(URLs.EXTACT_USER,NVparames, HttpRequestTool.EXTACT_USER);
        LoadDialogUtil.setMessageAndShow(this,"努力加载中……");
    }

    /**
     * 弹框显示协议内容
     */
    private void alertAgreement() {
        WebView webView = new WebView(this);
        webView.loadUrl("file:///android_asset/cx_agreement.htm");
        new AlertDialog.Builder(this).setTitle("协议内容")
                .setView(webView).setPositiveButton("同意并签署协议", (dialog, which) -> {
            jumpToSignView();
        })
                .setNegativeButton("取消", null).create().show();
    }

    /**跳转到签字界面*/
    private void jumpToSignView() {
        LoadDialogUtil.setMessageAndShow(this,"载入中……");
        String agreementUrl = URLs.EXTACT_CREAT_AGREEMENT_PDF + "?userId=" + AppApplication.getUSER().data.userId + "&qmtype=text";
        getSignImgUpInfo(agreementUrl);
    }

    /**
     * 显示信息
     */
    private void displayInfo() {
        if (extUserEtity==null || extUserEtity.data==null){
            DialogUtil.getErrDialogAndFinish(this, "无法获取外部查勘员信息！", dialog -> {
                this.finish();
            }).show();
            return;
        }
        SetTextUtil.setTextViewText(userName,extUserEtity.data.name);
        SetTextUtil.setTextViewText(idCardName,extUserEtity.data.idCard);
        displaySignStatus();
        displayIdCard();
        displayAgreement();
    }

    /**如果签署协议，则显示查看按钮，否则显示下一步按钮*/
    private void displayAgreement() {
        if (extUserEtity.data.status == 1 && !TextUtils.isEmpty(extUserEtity.data.agreementUrl)) {
            showAgreementButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            showAgreementButton.setOnClickListener(v -> {
                new PDFShowUtil().startActivity(SignAgmentActivity.this, URLs.IPHOME+extUserEtity.data.agreementUrl);
            });
        } else {
            showAgreementButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    /**回显协议签署状态*/
    public void displaySignStatus(){
        if (extUserEtity.data.status==1){ //未签署协议
            SetTextUtil.setTextViewText(signStatuTv,"已签署√");
        }else {
            SetTextUtil.setTextViewText(signStatuTv,"未签署！");
        }
    }

    /**
     * 显示照片
     */
    private void displayIdCard(){
        Glide.with(this).load(extUserEtity.data.idCardPhotoFront).placeholder(R.drawable.idcard_f512).into(cardImgF);
        Glide.with(this).load(extUserEtity.data.idCardPhotoBack).placeholder(R.drawable.idcard_b512).into(cardImgB);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}


