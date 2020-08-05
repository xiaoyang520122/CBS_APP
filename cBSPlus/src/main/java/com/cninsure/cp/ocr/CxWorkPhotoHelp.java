package com.cninsure.cp.ocr;

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
import com.cninsure.cp.cx.CxWorkActivity;
import com.cninsure.cp.cx.fragment.CxThirdFragment;
import com.cninsure.cp.entity.OCREntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxWorkEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CxWorkPhotoHelp implements OnClickListener {
	
	private File file2;
	private CxWorkActivity activity;
	/** * 1 身份证识别
		 * 2 银行卡识别
		 * 3 驾驶证识别
		 * 4 行驶证识别**/
	private int OCR_TYPE=0;
	private LayoutInflater inflater;
	private Dialog dialog;
	public OCREntity ocrEntityTemp;

	public CxWorkPhotoHelp(CxWorkActivity activity){
		this.activity=activity;
		inflater=LayoutInflater.from(activity);
		initOCR();
	}
	
	@SuppressWarnings("unused")
	private CxWorkPhotoHelp(){}
	
	
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
	
	/**启动拍照并返回照片路径，如果已存在照片则显示**/
	public void startCamera(int event){
//		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (!exists(event)) {
			LoadDialogUtil.setMessageAndShow(activity,"相机启动中……");
			startCameraNow(event);
		}
	}
	
	public void startCameraNow(int event){
		Intent intent = new Intent(activity, CameraActivity.class);
		intent.putExtra("orderUid", activity.getIntent().getStringExtra("orderUid"));
		activity.startActivityForResult(intent, event);
	}
	
	/**如果OCR照片已存在返回true,否则返回false**/
	private boolean exists(int event) {
		switch (event) {
			case 1:
				if (activity.ocrEntity1 == null) {
					return false;
				} else {
//				showOcrMsg(activity.ocrEntity1,1);
					return true;
				}
			case 2:
				if (activity.cxWorkEntity.subjectInfo.bankCarLicense == null) {
					return false;
				} else {
					String displayInfo = "银行卡号：" + activity.cxWorkEntity.subjectInfo.insuredBankNo;
					sendmessage(displayInfo, event);
					return true;
				}
			case 3:
				if (activity.cxWorkEntity.subjectInfo.pathDriverLicense == null) {
					return false;
				} else {
					String displayInfo = "驾驶证号码：" + activity.cxWorkEntity.subjectInfo.bdDriverNo + "\n准驾车型：" + activity.cxWorkEntity.subjectInfo.bdDrivingType + "\n驾驶员：" + activity.cxWorkEntity.subjectInfo.bdDriverName;
					sendmessage(displayInfo, event);
					return true;
				}
			case 4:
				if (activity.cxWorkEntity.subjectInfo.pathMoveLicense == null) {
					return false;
				} else {
					String displayInfo = "车牌号码：" + activity.cxWorkEntity.subjectInfo.bdCarNumber + "\n车架号：" + activity.cxWorkEntity.subjectInfo.bdCarVin + "\n发动机号码：" + activity.cxWorkEntity.subjectInfo.bdEngineNo;
					sendmessage(displayInfo, event);
					return true;
				}
			case CxWorkActivity.THIRD_SZ_JSZ_OCR:
				int position = ((CxThirdFragment) activity.fragmentMap.get(2)).OcrPosition;
				CxWorkEntity.ThirdPartyEntity thirdPartyEnt = activity.cxWorkEntity.thirdPartys.get(position);
				if (thirdPartyEnt.pathDriverLicense == null) {
					return false;
				} else {
					String displayInfo = "驾驶证号码：" + thirdPartyEnt.driverLicense + "\n准驾车型：" + thirdPartyEnt.drivingMode + "\n驾驶员：" + thirdPartyEnt.carPerson;
					sendmessage(displayInfo, event);
					return true;
				}
			case CxWorkActivity.THIRD_SZ_XSZ_OCR:
				int positionX = ((CxThirdFragment) activity.fragmentMap.get(2)).OcrPosition;
			CxWorkEntity.ThirdPartyEntity thirdPartyEntT = activity.cxWorkEntity.thirdPartys.get(positionX);
			if (thirdPartyEntT.pathMoveLicense == null) {
				return false;
			} else {
				String displayInfo = "车牌号码：" + thirdPartyEntT.carNumber + "\n车架号：" + thirdPartyEntT.frameNumber + "\n发动机号码：" + thirdPartyEntT.engineNumber ;
				sendmessage(displayInfo, event);
				return true;
			}

			default:
				break;
		}
		return false;
	}
	
	private void sendmessage(String msg,int event){
		Message message=new Message();
		message.obj=msg;
		message.what=event;
		handler2.sendMessage(message);
	}

	/**已拍数据的回显**/
	private void displayDialog(String msg,int event){
		dialog=DialogUtil.getDialogByView(activity, getDialogView2(msg,event));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private Handler handler2=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			displayDialog((String)msg.obj,msg.what);
		}
	};
	
	/**已拍数据的回显，只有重拍和取消按钮**/
	private View getDialogView2(String info,final int event){
		View view=inflater.inflate(R.layout.banc_dilog_view, null);
		ImageView img=(ImageView)view.findViewById(R.id.bancdialog_img);
		//车险 上传OCR图片路径,上传成功后返回图片名称及后缀 例如："picture-20180310151556-69210-E2638.jpg"，访问是需要加上登录时获取的头部分
		String imgPath=AppApplication.getUSER().data.qiniuUrl+ getOCRUrl(event);
		Glide.with(activity).load(imgPath).into(img);
		((TextView)view.findViewById(R.id.bancdialog_info)).setText(info);
		view.findViewById(R.id.bancdialog_submit).setVisibility(View.INVISIBLE);
		view.findViewById(R.id.bancdialog_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		view.findViewById(R.id.bancdialog_rep).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startCameraNow(event);
			}
		});
		return view;
	}

	public void forString(int event, File file) {
		if (!file.exists()) {
			DialogUtil.getAlertOneButton(activity, "拍摄照片失败，请重新拍照！", null).show();
		} else {
			LoadDialogUtil.setMessageAndShow(activity,"识别中……");
			file2=file;
			OCR_TYPE = event;
			switch (event) {
			case 1:
				jiexieIDCARD(file.getPath());
				break;
			case 2:
				jiexieYHK(file.getPath());
//				DialogUtil.getDialogByView(activity, getDialogView("xiaoyagn")).show();
				break;
			case 3:
				jiexieJSZ(file.getPath());
				break;
			case 4:
				jiexieXSZ(file.getPath());
				break;
			case CxWorkActivity.THIRD_SZ_JSZ_OCR:
				jiexieJSZ(file.getPath());
				break;
			case CxWorkActivity.THIRD_SZ_XSZ_OCR:
				jiexieXSZ(file.getPath());
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
		        // 调用成功，返回BankCardResult对象
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

	/**行驶证**/
private void jiexieXSZ(String filePath) {
	Log.e("JsonHttpUtils", "---------" + "解析行驶证！" + filePath);
	// 行驶证识别参数设置
	OcrRequestParams param = new OcrRequestParams();

	// 设置image参数
	param.setImageFile(new File(filePath));
	// 设置其他参数
	param.putParam("detect_direction", true);
	// 调用行驶证识别服务
	OCR.getInstance().recognizeVehicleLicense(param, new OnResultListener<OcrResponseResult>() {
	    @Override
	    public void onResult(OcrResponseResult result) {
	        // 调用成功，返回OcrResponseResult对象
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

/**驾驶证**/
	private void jiexieJSZ(String filePath) {
		Log.e("JsonHttpUtils", "---------" + "解析驾驶证！" + filePath);
		// 驾驶证识别参数设置
		OcrRequestParams param = new OcrRequestParams();
		// 设置image参数
		param.setImageFile(new File(filePath));
		// 设置其他参数
		param.putParam("detect_direction", true);
		// 调用驾驶证识别服务
		OCR.getInstance().recognizeDrivingLicense(param, new OnResultListener<OcrResponseResult>() {
		    @Override
		    public void onResult(OcrResponseResult result) {
		        // 调用成功，返回OcrResponseResult对象
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
					break;
				case 3:
					showDriveDilog((OcrResponseResult) msg.obj);
					break;
				case 4:
					showTravelDilog((OcrResponseResult) msg.obj);
					break;
				case CxWorkActivity.THIRD_SZ_JSZ_OCR:
					showDriveDilog((OcrResponseResult) msg.obj);
					break;
				case CxWorkActivity.THIRD_SZ_XSZ_OCR:
					showTravelDilog((OcrResponseResult) msg.obj);
					break;

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
		ocrEntityTemp=new OCREntity();
//		ocrEntity.insuredBankNo=result.getBankCardNumber();
		String displayInfo="身份证号："+result.getIdNumber();
		displayDialog(displayInfo);
	}
	
	/**显示银行卡识别信息供确认*/
	private void showBancDilog(BankCardResult result){
		ocrEntityTemp=new OCREntity();
		ocrEntityTemp.insuredBankNo=result.getBankCardNumber();
		String displayInfo="银行卡号："+result.getBankCardNumber();
		displayDialog(displayInfo);
	}
	/**显示驾驶证识别信息供确认*/
	private void showDriveDilog(OcrResponseResult result){
		ocrEntityTemp=new OCREntity();
		String displayInfo="解析失败";
		try {
			JSONObject object=new JSONObject(result.getJsonRes());
			JSONObject jsonDate=object.getJSONObject("words_result");
			ocrEntityTemp.bdDrivingType = jsonDate.getJSONObject("准驾车型").optString("words"); //准驾车型
			ocrEntityTemp.bdDriverName = jsonDate.getJSONObject("姓名").optString("words"); //驾驶员姓名
			ocrEntityTemp.bdDriverNo = jsonDate.getJSONObject("证号").optString("words"); //驾驶证
			ocrEntityTemp.setBdDriverRegisterDate( jsonDate.getJSONObject("初次领证日期").optString("words")); //初次领证日期
			ocrEntityTemp.setBdDriverEffectiveStar(jsonDate.getJSONObject("有效起始日期").optString("words")); //有效起始日期
			displayInfo="驾驶证号码："+ ocrEntityTemp.bdDriverNo+"\n准驾车型："+ocrEntityTemp.bdDrivingType+"\n驾驶员："+ocrEntityTemp.bdDriverName
					+"\n初次领证日期："+ocrEntityTemp.getBdDriverRegisterDate()+"\n有效起始日期："+ocrEntityTemp.getBdDriverEffectiveStar();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		displayDialog(displayInfo);
	}
	
	/**显示行驶证识别信息供确认*/
	private void showTravelDilog(OcrResponseResult result){
		ocrEntityTemp=new OCREntity();
		String displayInfo="解析失败";
		try {
			JSONObject object=new JSONObject(result.getJsonRes());
			JSONObject jsonDate=object.getJSONObject("words_result");

			ocrEntityTemp. bdCarNumber=jsonDate.getJSONObject("号牌号码").optString("words"); //车牌号
			ocrEntityTemp. bdCarVin=jsonDate.getJSONObject("车辆识别代号").optString("words"); //车架号
			ocrEntityTemp. bdEngineNo=jsonDate.getJSONObject("发动机号码").optString("words"); //发动机号
			ocrEntityTemp. setBdCarRegisterDate(jsonDate.getJSONObject("注册日期").optString("words")); //初登日期
			ocrEntityTemp. setBdCarUseType(jsonDate.getJSONObject("使用性质").optString("words")); //使用性质

			displayInfo="车牌号码："+ ocrEntityTemp.bdCarNumber+"\n车架号："+ocrEntityTemp.bdCarVin+"\n发动机号码："+ocrEntityTemp.bdEngineNo
					+"\n注册日期："+ocrEntityTemp.getBdCarRegisterDate()+"\n使用性质："+ocrEntityTemp.getBdCarUseType();
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
			setOCREntity(OCR_TYPE,ocrEntityTemp);
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
//		List<NameValuePair> fileUrls=new ArrayList<NameValuePair>();
//		fileUrls.add(new BasicNameValuePair("0", file2.getPath()));
//		List<NameValuePair> httpparams=new ArrayList<NameValuePair>();
//		PhotoUploadUtil.upload(activity, fileUrls, URLs.UP_OCR_PHOTO, httpparams);
		
		List<NameValuePair> fileUrls=new ArrayList<NameValuePair>();
		fileUrls.add(new BasicNameValuePair("0", file2.getPath()));
		PhotoUploadUtil.uploadOCR(activity, fileUrls, URLs.UP_OCR_PHOTO, OCR_TYPE);
	}

//	public void sendMsgToBack(String url) {
//		if (url.indexOf(".j")==-1) {
//			DialogUtil.getErrDialog(activity, "图片保存失败！").show();
//		}else {
//			getOCREntity(OCR_TYPE).url=url;
////			String call = "javascript:showPhotographInfo("+OCR_TYPE+","+JSON.toJSONString(getOCREntity(OCR_TYPE))+")";
////			activity.webview.loadUrl(call);
//		}
//	}

	public String getOCRUrl(int event) {
		switch (event) {
			case 1:
				return ""; //身份证
			case 2:
				return activity.cxWorkEntity.subjectInfo.bankCarLicense; //银行卡
			case 3:
				return activity.cxWorkEntity.subjectInfo.pathDriverLicense; //驾驶证
			case 4:
				return activity.cxWorkEntity.subjectInfo.pathMoveLicense;  //行驶证
			case CxWorkActivity.THIRD_SZ_JSZ_OCR:
				int position = ((CxThirdFragment)activity.fragmentMap.get(2)).OcrPosition;
				return activity.cxWorkEntity.thirdPartys.get(position).pathDriverLicense;
			case CxWorkActivity.THIRD_SZ_XSZ_OCR:
				int positionX = ((CxThirdFragment)activity.fragmentMap.get(2)).OcrPosition;
				return activity.cxWorkEntity.thirdPartys.get(positionX).pathMoveLicense;

			default:
				break;
		}
		return null;
	}
	
//	public OCREntity getOCREntity(int event) {
//		switch (event) {
//			case 1:
//				return activity.ocrEntity1;
//			case 2:
//				return activity.ocrEntity2;
//			case 3:
//				return activity.ocrEntity3;
//			case 4:
//				return activity.ocrEntity4;
//
//			default:
//				break;
//		}
//		return null;
//	}
	
	public void setOCREntity(int event,OCREntity ocrEntity ){
		switch (event) {
		case 1:
			activity.ocrEntity1=ocrEntity;
			break;
		case 2:
			activity.ocrEntity2=ocrEntity;
			break;
		case 3:
			activity.ocrEntity3=ocrEntity;
			break;
		case 4:
			activity.ocrEntity4=ocrEntity;
			break;
			case CxWorkActivity.THIRD_SZ_JSZ_OCR: //三者中的驾驶证识别
				CxThirdFragment.ocrEntityJsz = ocrEntity;
				break;
			case CxWorkActivity.THIRD_SZ_XSZ_OCR://三者中的行驶证识别
				CxThirdFragment.ocrEntityXsz = ocrEntity;
				break;

		default:
			break;
		}
	}
	
	
}
