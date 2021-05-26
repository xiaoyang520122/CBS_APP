package com.cninsure.cp.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.activity.yjx.YjxSurveyActivity;
import com.cninsure.cp.entity.FCBasicEntity;
import com.cninsure.cp.entity.cx.CxImagEntity;
import com.cninsure.cp.entity.cx.CxOrderMediaTypeEntity;
import com.cninsure.cp.entity.dispersive.DisWorkImageEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class PhotoUploadUtil {

	private static int uploadPoint = 0;
	private static TextView progresstitle, progresscurrent;
	private static ProgressBar progressBar;
	private static Dialog progressDialog;
	private static String successful="success";

	public static void upload(final Activity context, final List<NameValuePair> fileUrls, 
			final String uploadPath,final List<NameValuePair> httpParams) {

		Log.e("JsonHttpUtils", "上传图片请求地址："+uploadPath);
		
		RequestParams params = new RequestParams("UTF-8");
		params.addBodyParameter("userId",  AppApplication.getUSER().data.userId);
		for (NameValuePair valuePair:httpParams) {
			if ("FSX_UP_WORK_IMG".equals(valuePair.getName())){  //分散型的需要增加一个图片类型参数
				params.addBodyParameter("imageType", fileUrls.get(uploadPoint).getName());
			}else{
				params.addBodyParameter(valuePair.getName(), valuePair.getValue());
			}
		}
		boolean addTypeIdBle = true;
		for (NameValuePair valuePair:httpParams) {  //分散型不用添加typeId字段
			if ("FSX_UP_WORK_IMG".equals(valuePair.getName())){
				addTypeIdBle = false;
				break;
			}
		}
		if (addTypeIdBle){  //分散型不用添加typeId字段
			params.addBodyParameter("typeId", fileUrls.get(uploadPoint).getName());
		}
		params.addBodyParameter("client", "android");
		params.addBodyParameter("timestamp", new Date().getTime()+"");
		params.addBodyParameter("digest", MD5Test.GetMD5Code("nomessagedigest"));

		// 传图片时，要写3个参数
		// imageFile：键名
		// new File(path)：要上传的图片，path图片路径
		// image/jpg：上传图片的扩展名
		if (fileUrls.size()>0) {
			//添加水印后上传
//			WaterMaskUtil.set(context, fileUrls.get(uploadPoint).getValue());
			params.addBodyParameter("file", new File(fileUrls.get(uploadPoint).getValue()), "image/jpg");
		}
		HttpUtils http = new HttpUtils(60*1000);
		
		/**设置次cookie**/
		http.configCookieStore(HttpRequestTool.cookie);
		
		http.send(HttpRequest.HttpMethod.POST, uploadPath, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				
				final String resultinfo=responseInfo.result;
				successful="success";
				uploadPoint++;
				if (uploadPoint < fileUrls.size()) {
					upload(context, fileUrls, uploadPath,httpParams);
				} else {
					uploadPoint = 0;
					progressDialog.dismiss();
					DialogUtil.getAlertOneButton(context, "上传完成!",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							EventBus.getDefault().post("UPLOAD_SUCCESS");
							String result=resultinfo.substring(1, resultinfo.length()-1);
							Log.e("JsonHttpUtils", "车险上传图片成功返回数据："+resultinfo);
//							EventBus.getDefault().post(new BasicNameValuePair("UPLOAD_SUCCESS",result));
						}
					}).show();
					sendEvent();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				try {
					String msge = error.getMessage();
					FCBasicEntity fcentity=JSON.parseObject(error.getMessage(), FCBasicEntity.class);
					DialogUtil.getErrDialog(context, "上传失败!"+fcentity.message).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
				progresstitle.setText(String.format(titleBase, uploadPoint + 1, fileUrls.size()));
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
	 * 上传医健险文件
	 * @param context
	 * @param fileUrls
	 * @param uploadPath
	 */
	public static void uploadYjxFile(final Activity context, final List<NameValuePair> fileUrls, final String uploadPath
			,final DialogInterface.OnClickListener listener) {

		Log.e("JsonHttpUtils", "上传图片请求地址：" + uploadPath);

		RequestParams params = new RequestParams("UTF-8");
		params.addBodyParameter("userId", AppApplication.getUSER().data.userId);
		params.addBodyParameter("client", "android");
		params.addBodyParameter("timestamp", new Date().getTime() + "");
		params.addBodyParameter("digest", MD5Test.GetMD5Code("nomessagedigest"));

		// 传图片时，要写3个参数
		// imageFile：键名
		// new File(path)：要上传的图片，path图片路径
		// image/jpg：上传图片的扩展名
		if (fileUrls.size() > 0) {
			params.addBodyParameter("file", new File(fileUrls.get(uploadPoint).getValue()), "image/jpg");
		}
		HttpUtils http = new HttpUtils(60 * 1000 * 2);
		/** 设置次cookie **/
		http.configCookieStore(HttpRequestTool.cookie);
		http.send(HttpRequest.HttpMethod.POST, uploadPath, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				final String resultinfo = (responseInfo.result).replace("\"", "");
				successful = "success";
				//返回数据样式："新建 Microsoft PowerPoint 演示文稿.pptx_29642_file-20190621174129-85539-B7F67.pptx"
				String result =  resultinfo;
				Log.e("JsonHttpUtils", "医健险上传图片成功返回数据：" + resultinfo);
				List<NameValuePair> values= new ArrayList<NameValuePair>();
				values.add(new BasicNameValuePair(""+HttpRequestTool.UPLOAD_FILE_PHOTO,result));
				values.add(new BasicNameValuePair(""+uploadPoint,fileUrls.get(uploadPoint).getValue()));
				values.add(new BasicNameValuePair(fileUrls.get(uploadPoint).getName(),fileUrls.get(uploadPoint).getName()));
				EventBus.getDefault().post(values);
				uploadPoint++;
				if (uploadPoint < fileUrls.size()) {
					uploadYjxFile(context, fileUrls, uploadPath,listener);
				} else {
					uploadPoint = 0;
					progressDialog.dismiss();
					DialogUtil.getAlertOneButton(context, "上传文件成功!", listener).show();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				try {
					FCBasicEntity fcentity = JSON.parseObject(error.getMessage(), FCBasicEntity.class);
					DialogUtil.getErrDialog(context, "上传失败!" + fcentity.message).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				successful = "error";
				progressDialog.dismiss();
				// sendEvent();
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
				progresstitle.setText(String.format(titleBase, uploadPoint + 1, fileUrls.size()));
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

	private static void sendEvent() {
		NameValuePair param = new BasicNameValuePair(HttpRequestTool.UPLOAD_WORK_PHOTO + "", successful);
		EventBus.getDefault().post(param);
	}
	
	
	
	/**非车专用**************************************/
	public static void FCupload(final Activity context, final List<NameValuePair> fileUrls, 
			final String uploadPath,final String caseNo,final String workId) {

		Log.e("JsonHttpUtils", uploadPath);
		
		RequestParams params = new RequestParams("UTF-8");
		params.addBodyParameter("caseNo",  caseNo);
		params.addBodyParameter("workId", workId);
		params.addBodyParameter("fileTypeId", fileUrls.get(uploadPoint).getName());
//		params.addHeader("content-type", "application/x-www-form-urlencoded");
//		params.setContentType("application/x-www-form-urlencoded");
		
		if ((uploadPoint+1) == fileUrls.size()) {
			params.addBodyParameter("isEnd", "1");
		}else {
			params.addBodyParameter("isEnd", "0");
		}

		// 传图片时，要写3个参数
		// imageFile：键名
		// new File(path)：要上传的图片，path图片路径
		// image/jpg：上传图片的扩展名
		if (fileUrls.size()>0) {
			params.addBodyParameter("file", new File(fileUrls.get(uploadPoint).getValue()), "*/*");
//			Log.e("JsonHttpUtils", "10000+"+fileUrls.get(uploadPoint).getValue()+" workId=="+workId);
		}
		final HttpUtils http = new HttpUtils(60*1000);
		
		/**设置次cookie**/
		http.configCookieStore(HttpRequestTool.cookie);
		Log.e("JsonHttpUtils", "params=="+params.getCharset());
		
		http.send(HttpRequest.HttpMethod.POST, uploadPath, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				Log.e("JsonHttpUtils", "http=="+JSON.toJSONString(http.getHttpClient().getParams()));
				Log.e("JsonHttpUtils", "10001=="+JSON.toJSONString(responseInfo));
				String requestMessage=JSON.parseObject(JSON.toJSONString(responseInfo)).getString("result");
				int code=-1;
				try {
					code=new JSONObject(requestMessage).getInt("code");
				} catch (JSONException e) {
					e.printStackTrace();
				}
//				DialogUtil.getAlertOneButton(context, requestMessage, null).show();
				successful="success";
				uploadPoint++;
				if (uploadPoint < fileUrls.size()) {
					FCupload(context, fileUrls, uploadPath,caseNo,workId);
				} else {
					uploadPoint = 0;
					progressDialog.dismiss();
					if (code == 0) {
						try {
							FCBasicEntity fcentity = JSON.parseObject(requestMessage, FCBasicEntity.class);
							DialogUtil.getAlertOneButton(context, "上传成功：" + fcentity.message, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									EventBus.getDefault().post("UPLOAD_SUCCESS");
								}
							}).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {

						try {
							FCBasicEntity fcentity = JSON.parseObject(requestMessage, FCBasicEntity.class);
							DialogUtil.getAlertOneButton(context, "上传失败：" + fcentity.message, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									successful = "error";
								}
							}).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					sendEvent();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				try {
					FCBasicEntity fcentity=JSON.parseObject(error.getMessage(), FCBasicEntity.class);
					DialogUtil.getErrDialog(context, "上传失败!"+fcentity.message).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
				progresstitle.setText(String.format(titleBase, uploadPoint + 1, fileUrls.size()));
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
	
	/**上传OCR相关图片（包括签字）**/
	public static void uploadOCR(final Activity context, final List<NameValuePair> fileUrls, 
			final String uploadPath,final int type) {

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
		if (fileUrls.size()>0) {
			params.addBodyParameter("file", new File(fileUrls.get(uploadPoint).getValue()), "image/jpg");
		}
		HttpUtils http = new HttpUtils(60*1000);
		
		/**设置次cookie**/
		http.configCookieStore(HttpRequestTool.cookie);
		
		http.send(HttpRequest.HttpMethod.POST, uploadPath, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				
				final String resultinfo=responseInfo.result;
				successful="success";
				uploadPoint++;
				if (uploadPoint < fileUrls.size()) {
					uploadOCR(context, fileUrls, uploadPath,type);
				} else {
					uploadPoint = 0;
					progressDialog.dismiss();

					Dialog dialog = DialogUtil.getAlertOneButton(context, "上传完成!",null);
					dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							String result=resultinfo.substring(1, resultinfo.length()-1);
							Log.e("JsonHttpUtils", "车险上传图片成功返回数据："+resultinfo);
							List<NameValuePair> responsePrams=new ArrayList<NameValuePair>();
							responsePrams.add(new BasicNameValuePair("type",type+""));//返回上传图片类型
							responsePrams.add(new BasicNameValuePair("UPLOAD_SUCCESS",result));//返回上传成功后的图片名称（不包含完整路径只有文件名称）
							EventBus.getDefault().post(responsePrams);
						}
					});
					dialog.show();
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
				progresstitle.setText(String.format(titleBase, uploadPoint + 1, fileUrls.size()));
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
	 * f分散型图片上传
	 * @param context
	 * @param submitImgEnList
	 * @param uploadPath
	 * @param httpParams
	 */
	public static void dispersiveUpload(final Activity context, final List<DisWorkImageEntity.DisWorkImgData> submitImgEnList,
							  final String uploadPath,final List<NameValuePair> httpParams){
		Log.e("JsonHttpUtils", "上传图片请求地址："+uploadPath);
		RequestParams params = new RequestParams("UTF-8");
		params.addBodyParameter("userId",  AppApplication.getUSER().data.userId);
		params.addBodyParameter("workUid",  httpParams.get(0).getValue());
		params.addBodyParameter("dispatchUid",  httpParams.get(1).getValue());

		params.addBodyParameter("imageType", submitImgEnList.get(uploadPoint).imageType+"");
		if (submitImgEnList.get(uploadPoint).imageSubType!=null)
			params.addBodyParameter("imageSubType", submitImgEnList.get(uploadPoint).imageSubType+"");

		params.addBodyParameter("client", "android");
		params.addBodyParameter("timestamp", new Date().getTime()+"");
		params.addBodyParameter("digest", MD5Test.GetMD5Code("nomessagedigest"));

		// 传图片时，要写3个参数
		// imageFile：键名
		// new File(path)：要上传的图片，path图片路径
		// image/jpg：上传图片的扩展名
		if (submitImgEnList.size()>0) {
			//添加水印后上传
//			WaterMaskUtil.set(context, fileUrls.get(uploadPoint).getValue());
			params.addBodyParameter("file", new File(submitImgEnList.get(uploadPoint).getImageUrl()), "image/jpg");
		}
		HttpUtils http = new HttpUtils(60*1000);

		/**设置次cookie**/
		http.configCookieStore(HttpRequestTool.cookie);

		http.send(HttpRequest.HttpMethod.POST, uploadPath, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				final String resultinfo=responseInfo.result;
				successful="success";
				uploadPoint++;
				Log.e("JsonHttpUtils", "车险上传图片成功返回数据："+resultinfo);
				if (uploadPoint < submitImgEnList.size()) {
					dispersiveUpload(context, submitImgEnList, uploadPath,httpParams);
				} else {
					uploadPoint = 0;
					progressDialog.dismiss();
					DialogUtil.getAlertOneButton(context, "上传完成!", (arg0, arg1) -> {
						EventBus.getDefault().post("UPLOAD_SUCCESS");
						String result=resultinfo.substring(1, resultinfo.length()-1);
						Log.e("JsonHttpUtils", "车险上传图片成功返回数据："+resultinfo);
					}).show();
					sendEvent();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				try {
					Log.e("JsonHttpUtils", "车险上传图片失败返回数据："+msg);
					String msge = error.getMessage();
					FCBasicEntity fcentity=JSON.parseObject(error.getMessage(), FCBasicEntity.class);
					DialogUtil.getErrDialog(context, "上传失败!"+fcentity.message).show();
					ToastUtil.showToastLong(context,"上传失败!"+fcentity.message);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (uploadPoint < submitImgEnList.size()) {
					dispersiveUpload(context, submitImgEnList, uploadPath,httpParams);
				} else {
					uploadPoint = 0;
					progressDialog.dismiss();
					DialogUtil.getAlertOneButton(context, "上传完成，部分上传失败!", (arg0, arg1) -> EventBus.getDefault().post("UPLOAD_SUCCESS")).show();
					sendEvent();
				}
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
				View view = (context).getLayoutInflater().inflate(R.layout.uploadprogress_dialog, null);
				progresstitle = view.findViewById(R.id.upprogress_title);
				progresscurrent = view.findViewById(R.id.upprogress_bfb);
				progressBar = view.findViewById(R.id.upprogress_progress);
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
	 * f分散型图片上传
	 * @param context
	 * @param submitImgEnList
	 * @param uploadPath
	 * @param httpParams
	 */
	public static void newCxImgSave(final Activity context, final List<CxImagEntity> submitImgEnList,
									final String uploadPath, final List<NameValuePair> httpParams, CxOrderMediaTypeEntity cxMediaTypes){
		Log.e("JsonHttpUtils", "上传图片请求地址："+uploadPath);
		RequestParams params = new RequestParams("UTF-8");
		params.addBodyParameter("userId",  AppApplication.getUSER().data.userId);
		params.addBodyParameter("orderUid",  httpParams.get(0).getValue());
		params.addBodyParameter("baoanUid",  httpParams.get(1).getValue());
		params.addBodyParameter("fullPath", cxMediaTypes==null?"默认分类":cxMediaTypes.getFullPathByValue(submitImgEnList.get(uploadPoint).type));

		String url = submitImgEnList.get(uploadPoint).fileUrl;
		String fileUrl = url.substring(url.lastIndexOf("/")+1);

		params.addBodyParameter("source",   submitImgEnList.get(uploadPoint).source);
		params.addBodyParameter("type", submitImgEnList.get(uploadPoint).type+"");
		params.addBodyParameter("fileUrl",fileUrl );
		params.addBodyParameter("fileName", submitImgEnList.get(uploadPoint).fileName);
		params.addBodyParameter("fileSuffix", submitImgEnList.get(uploadPoint).fileSuffix);
		params.addBodyParameter("digest", MD5Test.GetMD5Code("nomessagedigest"));

		HttpUtils http = new HttpUtils(60*1000);

		/**设置次cookie**/
		http.configCookieStore(HttpRequestTool.cookie);

		http.send(HttpRequest.HttpMethod.POST, uploadPath, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				final String resultinfo=responseInfo.result;
				successful="success";
				uploadPoint++;
				Log.e("JsonHttpUtils", "车险图片信息保存成功返回数据："+resultinfo);
				if (uploadPoint < submitImgEnList.size()) {
                    newCxImgSave(context, submitImgEnList, uploadPath,httpParams,cxMediaTypes);
				} else {
					uploadPoint = 0;
					progressDialog.dismiss();
					DialogUtil.getAlertOneButton(context, "保存完成!", (arg0, arg1) -> {
						EventBus.getDefault().post("UPLOAD_SUCCESS");
						String result=resultinfo.substring(1, resultinfo.length()-1);
					}).show();
					sendEvent();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				try {
					Log.e("JsonHttpUtils", "车险上传图片失败返回数据："+msg);
					String msge = error.getMessage();
					FCBasicEntity fcentity=JSON.parseObject(error.getMessage(), FCBasicEntity.class);
					DialogUtil.getErrDialog(context, "保存失败!"+fcentity.message).show();
					ToastUtil.showToastLong(context,"保存失败!"+fcentity.message);
				} catch (Exception e) {
					e.printStackTrace();
				}

				uploadPoint++;
				if (uploadPoint < submitImgEnList.size()) {
                    newCxImgSave(context, submitImgEnList, uploadPath,httpParams,cxMediaTypes);
				} else {
					uploadPoint = 0;
					progressDialog.dismiss();
					DialogUtil.getAlertOneButton(context, "保存完成，部分上传失败!", (arg0, arg1) -> EventBus.getDefault().post("UPLOAD_SUCCESS")).show();
					sendEvent();
				}
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
				View view = (context).getLayoutInflater().inflate(R.layout.uploadprogress_dialog, null);
				progresstitle = view.findViewById(R.id.upprogress_title);
				progresscurrent = view.findViewById(R.id.upprogress_bfb);
				progressBar = view.findViewById(R.id.upprogress_progress);
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

}
