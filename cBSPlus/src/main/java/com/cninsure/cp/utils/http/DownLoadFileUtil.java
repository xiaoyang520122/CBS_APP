package com.cninsure.cp.utils.http;

import android.app.Activity;
import android.app.Dialog;
import android.os.Environment;
import android.text.TextUtils;

import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

public class DownLoadFileUtil {

private Activity ac;
    String FileLoad = "CBSFile/";

    private DownLoadFileUtil(){};
    public DownLoadFileUtil(Activity ac){
        this.ac = ac;
        FileDownloader.setup(ac);   //ac为activity的上下文对象
    }

    /**开始下载文件*/
    /**
     *
     * @param url
     * @param fileName 必须包含扩展名的文件名 如：“某某某341.pdf”
     * @param errorMsg
     * @param progressDialog
     */
    public void startDownLoad(String url , String fileName , final String errorMsg , final Dialog progressDialog, final Downloadlistener dlistener){
        final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+FileLoad+fileName; //文件保存路径
        FileDownloader.getImpl().create(url)
            .setPath(filePath)
                .setForceReDownload(true)
                .setListener(new FileDownloadListener() {
                    //等待
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (null != progressDialog)
                        progressDialog.show();
                    }
                    //下载进度回调
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                        if (null != progressDialog)
//                        progressDialog.setProgress((soFarBytes * 100 / totalBytes));
                    }
                    //完成下载
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        if (null != progressDialog)
                        progressDialog.cancel();
                        if (dlistener!=null){
                            dlistener.successDo(filePath);
                        }else{
                            ToastUtil.showToastLong(ac,"下载成功！");
//                            Toast.makeText(ac,"下载成功！",Toast.LENGTH_SHORT).show();
                        }
                    }
                    //暂停
                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }
                    //下载出错
                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        ToastUtil.showToastShort(ac,"下载出错！");
//                        Toast.makeText(ac,"下载出错！",Toast.LENGTH_SHORT).show();
                        if (!TextUtils.isEmpty(errorMsg)){
                            DialogUtil.getErrDialog(ac,errorMsg).show();
                        }else{
                            DialogUtil.getErrDialog(ac,"下载出错！").show();
                        }
                        if (null != progressDialog)
                        progressDialog.cancel();
                    }
                    //已存在相同下载
                    @Override
                    protected void warn(BaseDownloadTask task) {
                        if (null != progressDialog)
                        progressDialog.cancel();
                    }
                }).start();
    }

    public interface Downloadlistener{
       public void successDo(String filePath);
    }
}
