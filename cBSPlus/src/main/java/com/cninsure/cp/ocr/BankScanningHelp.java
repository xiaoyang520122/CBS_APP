package com.cninsure.cp.ocr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.sdk.model.OcrRequestParams;
import com.baidu.ocr.sdk.model.OcrResponseResult;
import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxSurveyWorkActivity;
import com.cninsure.cp.cx.fragment.CxThirdFragment;
import com.cninsure.cp.entity.OCREntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxSurveyWorkEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.cninsure.cp.utils.ToastUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BankScanningHelp implements OnClickListener {

	private File file2;
	private Activity activity;
	/** * 1 身份证识别
		 * 2 银行卡识别
		 * 3 驾驶证识别
		 * 4 行驶证识别**/
	private int OCR_TYPE=0;
	private LayoutInflater inflater;
	private Dialog dialog;
	public OCREntity ocrEntityTemp;

	public BankScanningHelp(Activity activity,OCREntity ocrEntityTemp){
		this.activity=activity;
		inflater=LayoutInflater.from(activity);
		this.ocrEntityTemp =ocrEntityTemp;
		initOCR();
	}

	@SuppressWarnings("unused")
	private BankScanningHelp(){}
	
	
	private void initOCR() {
		OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
			@Override
			public void onResult(AccessToken result) {
				// 调用成功，返回AccessToken对象
				String token = result.getAccessToken();
				Log.e("JsonHttpUtils", "---------" + "初始化OCR单例成功！" + token);
			}

			@Override
			public void onError(OCRError error) {
				// 调用失败，返回OCRError子类SDKError对象
				Log.e("JsonHttpUtils", "---------" + "初始化OCR单例失败" + error.getMessage());
			}
		},activity. getApplicationContext());
	}
	
	/**启动拍照并返回照片路径**/
	public void startCamera(int event) {
		LoadDialogUtil.setMessageAndShow(activity, "相机启动中……");
		startCameraNow(event);
	}
	
	public void startCameraNow(int event){
		Intent intent = new Intent(activity, CameraActivity.class);
		intent.putExtra("orderUid", activity.getIntent().getStringExtra("orderUid"));
		activity.startActivityForResult(intent, event);
	}
	


	public void forString(int event, File file) {
		if (!file.exists()) {
			DialogUtil.getAlertOneButton(activity, "拍摄照片失败，请重新拍照！", null).show();
		} else {
//			LoadDialogUtil.setMessageAndShow(activity,"识别中……");
			file2=file;
			OCR_TYPE = event;
			switch (event) {
			case 1:
				jiexieIDCARD(file.getPath());
				break;
			case 2:
				jiexieYHK(file.getPath());
				ToastUtil.showToastLong(activity,"识别中……");
//				LoadDialogUtil.setMessageAndShow(AppApplication.getInstance().getApplicationContext(),"识别中……").show();
				break;

			default:
				break;
			}
		}
	}
	
	/**银行卡**/
	private void jiexieYHK(String filePath) {
		// 银行卡识别参数设置
		BankCardParams param = new BankCardParams();
		param.setImageFile(new File(filePath));

		// 调用银行卡识别服务
		OCR.getInstance().recognizeBankCard(param, new OnResultListener<BankCardResult>() {
		    @Override
		    public void onResult(BankCardResult result) {
				LoadDialogUtil.dismissDialog();
		        // 调用成功，返回BankCardResult对象
		    	Log.e("JsonHttpUtils", "---------" + "解析成功！" + JSON.toJSONString(result));
				Message message = new Message();
				message.obj = result;
				handler.sendMessage(message);
		    }
		    @Override
		    public void onError(OCRError error) {
				LoadDialogUtil.dismissDialog();
		        // 调用失败，返回OCRError对象
				Message message = new Message();
				message.what=110;
				handler.sendMessage(message);
		    	Log.e("JsonHttpUtils", "---------" + "解析失败" + error.getMessage());
		    	}
		});
	}


	private void jiexieIDCARD(String filePath) {
		// 身份证识别参数设置
		IDCardParams param = new IDCardParams();
		param.setImageFile(new File(filePath));
		// 设置身份证正反面
		// IDCardParams.ID_CARD_SIDE_BACK 反面
		param.setIdCardSide(IDCardParams.ID_CARD_SIDE_FRONT);
		// 设置方向检测
		param.setDetectDirection(true);
		// 调用身份证识别服务
		OCR.getInstance().recognizeIDCard(param, new OnResultListener<IDCardResult>() {
			@Override
			public void onResult(IDCardResult result) {
				// 调用成功，返回IDCardResult对象
				Log.e("JsonHttpUtils", "---------" + "解析成功！" + JSON.toJSONString(result));
				Message message = new Message();
				message.obj = result;
				handler.sendMessage(message);
			}
			@Override
			public void onError(OCRError error) {
				// 调用失败，返回OCRError对象
				Message message = new Message();
				message.what=110;
				handler.sendMessage(message);
				Log.e("JsonHttpUtils", "---------" + "解析失败" + error.getMessage());
			}
		});
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LoadDialogUtil.dismissDialog();
			if (msg.what==110) { /**解析失败，提示用户是否是拍照问题**/
				DialogUtil.getErrDialog(activity, "解析失败，可能是拍照质量问题，请重拍！").show();
			}else {
				switch (OCR_TYPE) {
				case 1:
					showIdCardDilog((IDCardResult) msg.obj);
					break;
				case 2:
					showBancDilog((BankCardResult) msg.obj);
				default:
					break;
				}
			}
		}
	};
	
	/**显示身份证识别信息供确认
	 *  * 1 身份证识别
		 * 2 银行卡识别
		 * 3 驾驶证识别
		 * 4 行驶证识别*/
	private void showIdCardDilog(IDCardResult result){
//		ocrEntityTemp=new OCREntity();
//		ocrEntity.insuredBankNo=result.getBankCardNumber();
		String displayInfo="身份证号："+result.getIdNumber();
		displayDialog(displayInfo);
	}
	
	/**显示银行卡识别信息供确认*/
	private void showBancDilog(BankCardResult result){
//		ocrEntityTemp=new OCREntity();
		ocrEntityTemp.insuredBankNo=result.getBankCardNumber();
		String displayInfo="银行卡号："+result.getBankCardNumber();
		displayDialog(displayInfo);
	}

	private void displayDialog(String msg){
		dialog=DialogUtil.getDialogByView(activity, getDialogView(msg));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private View getDialogView(String info ){
		View view=inflater.inflate(R.layout.banc_dilog_view, null);
		ImageView img=(ImageView)view.findViewById(R.id.bancdialog_img);
		Glide.with(activity).load(file2).into(img);
		((TextView)view.findViewById(R.id.bancdialog_info)).setText(info);
		view.findViewById(R.id.bancdialog_cancel).setOnClickListener(this);
		view.findViewById(R.id.bancdialog_submit).setOnClickListener(this);
		view.findViewById(R.id.bancdialog_rep).setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bancdialog_cancel://取消-关闭dialog后不做任何操作
			dialog.dismiss();
			break;
		case R.id.bancdialog_submit://确定-关闭dialog后提交扫描结果到后台并保存拍照照片，根据OCR_TYPE判断类型
//			setOCREntity(OCR_TYPE,ocrEntityTemp);
			dialog.dismiss();
			uploadPhoto();
			break;
		case R.id.bancdialog_rep://重拍，根据OCR_TYPE判断类型
			dialog.dismiss();
			startCameraNow(OCR_TYPE);
			break;

		default:
			break;
		}
	}

	/**点击弹出框确认按钮后调用次方法完成图片上传，在CxWorkActivity中的eventbus方法中调用下面的sendMsgToBack方法传递数据给后台**/
	private void uploadPhoto() {
		List<NameValuePair> fileUrls=new ArrayList<NameValuePair>();
		fileUrls.add(new BasicNameValuePair("0", file2.getPath()));
		PhotoUploadUtil.uploadOCR(activity, fileUrls, URLs.UP_OCR_PHOTO, OCR_TYPE);
	}



}
