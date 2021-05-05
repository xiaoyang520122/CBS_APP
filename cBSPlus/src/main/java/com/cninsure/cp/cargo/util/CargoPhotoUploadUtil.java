package com.cninsure.cp.cargo.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.FCBasicEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cargo.CargoCaseWorkImagesTable;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.MD5Test;
import com.cninsure.cp.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Date;
import java.util.List;

public class CargoPhotoUploadUtil {

	private static int uploadPoint = 0;
	private static TextView progresstitle, progresscurrent;
	private static ProgressBar progressBar;
	private static Dialog progressDialog;
	private static String successful="success";



	private static void sendEvent() {
		NameValuePair param = new BasicNameValuePair(HttpRequestTool.UPLOAD_WORK_PHOTO + "", successful);
		EventBus.getDefault().post(param);
	}

	/**上传图片**/
	public static void imgUpload(final Activity context,  final List<CargoCaseWorkImagesTable> submitImgEnList, final String uploadPath) {

		Log.e("JsonHttpUtils", "OCR上传图片请求地址："+uploadPath);

		RequestParams params = new RequestParams("UTF-8");
		params.addBodyParameter("userId",  AppApplication.getUSER().data.userId);
		params.addBodyParameter("client", "android");
		params.addBodyParameter("timestamp", new Date().getTime()+"");
		params.addBodyParameter("digest", MD5Test.GetMD5Code("nomessagedigest"));

		// 传图片时，要写3个参数
		// imageFile：键名
		// new File(path)：要上传的图片，path图片路径
		// image/jpg：上传图片的扩展名
		if (submitImgEnList.size()>0) {
			params.addBodyParameter("file", new File(submitImgEnList.get(uploadPoint).fileUrl), "image/jpg");
		}
		HttpUtils http = new HttpUtils(60*1000);

		/**设置次cookie**/
		http.configCookieStore(HttpRequestTool.cookie);

		http.send(HttpRequest.HttpMethod.POST, uploadPath, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				final String tinfo=responseInfo.result;
				String resultinfo = JSON.parseObject(tinfo,String.class);
				submitUpload(context,submitImgEnList.get(uploadPoint), URLs.CARGO_SURVEY_IMG_SAVE,resultinfo);

				uploadPoint++;
				if (uploadPoint < submitImgEnList.size()) {
					imgUpload(context, submitImgEnList, uploadPath);
				} else {
					uploadPoint = 0;
					progressDialog.dismiss();
					DialogUtil.getAlertOneButton(context, "上传完成!", (arg0, arg1) -> {
						EventBus.getDefault().post("UPLOAD_SUCCESS");
						String result=resultinfo.substring(1, resultinfo.length()-1);
						Log.e("JsonHttpUtils", "货运险上传图片成功返回数据："+resultinfo);
					}).show();
					sendEvent();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				DialogUtil.getErrDialog(context, "上传失败!"+error.getMessage()).show();
				successful="error";
				progressDialog.dismiss();
//				sendEvent();
			}

			@Override
			public void onStart() {

				if (progressDialog == null || !progressDialog.isShowing()) {
					View view = getprogressView();
					Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("上传中……");
					builder.setView(view);
					progressDialog = builder.create();
					progressDialog.setCancelable(false);
					progressDialog.show();
				}
				String titleBase = context.getString(R.string.progress_current) + context.getString(R.string.progress_current2);
				progresstitle.setText(String.format(titleBase, uploadPoint + 1, submitImgEnList.size()));
			}

			private View getprogressView() {
				View view = ((Activity) context).getLayoutInflater().inflate(R.layout.uploadprogress_dialog, null);
				progresstitle = (TextView) view.findViewById(R.id.upprogress_title);
				progresscurrent = (TextView) view.findViewById(R.id.upprogress_bfb);
				progressBar = (ProgressBar) view.findViewById(R.id.upprogress_progress);
				return view;
			}

			// 计算百分比进度
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				if (isUploading) {
					progressBar.setProgress((int) (((int) current / (float) total) * 100));
					progresscurrent.setText((int) ((current / (float) total) * 100) + "%");
				}
			}
		});
	}

	/**
	 * 保存查勘图片（路径）信息
	 * @param context
	 * @param submitImgEn
	 * @param uploadPath
	 */
	public static void submitUpload(final Activity context, final CargoCaseWorkImagesTable submitImgEn,final String uploadPath,String httpUrl){
		Log.e("JsonHttpUtils", "上传图片请求地址："+uploadPath);
		RequestParams params = new RequestParams("UTF-8");
		params.addBodyParameter("userId",  AppApplication.getUSER().data.userId);
		params.addBodyParameter("source",  "2");

		params.addBodyParameter("type",  submitImgEn.type+"");
		params.addBodyParameter("fileUrl",  httpUrl);
		params.addBodyParameter("fileName",  submitImgEn.fileName);
		params.addBodyParameter("fileSuffix",  submitImgEn.fileSuffix);
		params.addBodyParameter("baoanUid",  submitImgEn.baoanUid);
//		params.addBodyParameter("uid",  submitImgEnList.baoanUid);

		params.addBodyParameter("client", "android");
		params.addBodyParameter("timestamp", new Date().getTime()+"");
		params.addBodyParameter("digest", MD5Test.GetMD5Code("nomessagedigest"));

		// 传图片时，要写3个参数
		// imageFile：键名
		// new File(path)：要上传的图片，path图片路径
		// image/jpg：上传图片的扩展名
		if (submitImgEn!=null) {
			//添加水印后上传
//			WaterMaskUtil.set(context, fileUrls.getValue());
			params.addBodyParameter("file", new File(submitImgEn.fileUrl), "image/jpg");
		}
		HttpUtils http = new HttpUtils(60*1000);

		/**设置次cookie**/
		http.configCookieStore(HttpRequestTool.cookie);

		http.send(HttpRequest.HttpMethod.POST, uploadPath, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				final String resultinfo=responseInfo.result;
				BaseEntity baseEn = JSON.parseObject(resultinfo,BaseEntity.class);
				if (baseEn.success){ //上传成功修改图片路径
					successful="success";
					submitImgEn.fileUrl = httpUrl;
				}else{
					ToastUtil.getToastShort(context,"一张图片保存路径失败！");
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				try {
					Log.e("JsonHttpUtils", "货运险上传图片失败返回数据："+msg);
					String msge = error.getMessage();
					FCBasicEntity fcentity=JSON.parseObject(error.getMessage(), FCBasicEntity.class);
					DialogUtil.getErrDialog(context, "上传失败!"+fcentity.message).show();
					ToastUtil.getToastShort(context,"一张图片保存路径失败！");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onStart() {

//				if (progressDialog == null || !progressDialog.isShowing()) {
//					View view = getprogressView();
//					Builder builder = new Builder(context);
//					builder.setTitle("上传中……");
//					builder.setView(view);
//					progressDialog = builder.create();
//					progressDialog.setCancelable(false);
//					progressDialog.show();
//				}
//				String titleBase = context.getString(R.string.progress_current) + context.getString(R.string.progress_current2);
//				progresstitle.setText(String.format(titleBase, uploadPoint + 1, submitImgEn.size()));
			}

//			private View getprogressView() {
//				View view = (context).getLayoutInflater().inflate(R.layout.uploadprogress_dialog, null);
//				progresstitle = view.findViewById(R.id.upprogress_title);
//				progresscurrent = view.findViewById(R.id.upprogress_bfb);
//				progressBar = view.findViewById(R.id.upprogress_progress);
//				return view;
//			}

			// 计算百分比进度
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
//				if (isUploading) {
//					progressBar.setProgress((int) (((int) current / (float) total) * 100));
//					progresscurrent.setText((int) ((current / (float) total) * 100) + "%");
//				}
			}
		});
	}

}
