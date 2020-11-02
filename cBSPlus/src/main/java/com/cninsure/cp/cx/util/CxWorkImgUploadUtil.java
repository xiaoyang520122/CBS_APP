package com.cninsure.cp.cx.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.FCBasicEntity;
import com.cninsure.cp.entity.cx.CxImagEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.MD5Test;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CxWorkImgUploadUtil {

    private static int uploadPoint = 0;
    private static TextView progresstitle, progresscurrent;
    private static ProgressBar progressBar;
    private static Dialog progressDialog;
    private static String successful="success";

    /**
     * 上传车险文件
     * @param context
     * @param uploadPath
     */
    public static void uploadCxImg(final Activity context, final List<CxImagEntity> submitImgEnList, final String uploadPath) {

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
        if (submitImgEnList.size() > 0) {
            params.addBodyParameter("file", new File(submitImgEnList.get(uploadPoint).getImageUrl()), "*/*");
        }
        HttpUtils http = new HttpUtils(60 * 1000 * 2);
        /** 设置次cookie **/
        http.configCookieStore(HttpRequestTool.cookie);
        http.send(HttpRequest.HttpMethod.POST, uploadPath, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                final String resultinfo = (responseInfo.result).replace("\"", "");
                successful = "success";
                String result =  resultinfo;  //返回数据样式："新建 Microsoft PowerPoint 演示文稿.pptx_29642_file-20190621174129-85539-B7F67.pptx"
                Log.e("JsonHttpUtils", "车险上传图片成功返回数据：" + resultinfo);
                submitImgEnList.get(uploadPoint).fileUrl = resultinfo;  //回写返回文件名称到实体类，以便后续上传保存
                uploadPoint++;
                if (uploadPoint < submitImgEnList.size()) {
                    uploadCxImg(context, submitImgEnList, uploadPath);
                } else {
                    uploadPoint = 0;
                    progressDialog.dismiss();
                    sendEvent();
                }
            }
            /**图片上传完成后，保存文件信息*/
            private void sendEvent(){
                List<NameValuePair> values= new ArrayList<NameValuePair>();
                values.add(new BasicNameValuePair(""+HttpRequestTool.UPLOAD_FILE_PHOTO,""));
                EventBus.getDefault().post(values);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                try {
                    FCBasicEntity fcentity = JSON.parseObject(error.getMessage(), FCBasicEntity.class);
                    DialogUtil.getErrDialog(context, "上传失败!" + fcentity.message).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //一张上传失败不影响，继续上传剩下内容
                uploadPoint++;
                if (uploadPoint < submitImgEnList.size()) {
                    uploadCxImg(context, submitImgEnList, uploadPath);
                } else {
                    uploadPoint = 0;
                    progressDialog.dismiss();
                    sendEvent();
                }
            }

            @Override
            public void onStart() {

                if (progressDialog == null || !progressDialog.isShowing()) {
                    View view = getprogressView();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

}
