package com.cninsure.cp.utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.cninsure.cp.IndexActivity;
import com.cninsure.cp.R;
import com.tamsiree.rxkit.RxAppTool;

public class APPDownloadUtils {

	 //下载器
	  private DownloadManager downloadManager;
	  //上下文
	  private Activity mContext;
	  //下载的ID
	  private long downloadId;
	  /**下载进度显示Dialog*/
	  private Dialog downdialog;
	  public APPDownloadUtils(Activity context){
		    this.mContext = context;
		  }
	  @SuppressWarnings("unused")
	private APPDownloadUtils( ){ }
	 
	  //下载apk
	  @SuppressLint({ "InlinedApi", "NewApi" })
	public void downloadAPK(String url, String name) {
	 
	    //创建下载任务
	    Request request = new Request(Uri.parse(url));
	    //移动网络情况下是否允许漫游
	    request.setAllowedOverRoaming(false);
	    //在通知栏中显示，默认就是显示的
	    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
	    request.setTitle("新版公估Apk");
	    request.setDescription("Apk Downloading");
	    request.setVisibleInDownloadsUi(true);
	 
	    //设置下载的路径
	    request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getAbsolutePath() , name);
	 
	    //获取DownloadManager
	    downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
	    //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
	    downloadId = downloadManager.enqueue(request);
	 
	    //注册广播接收者，监听下载状态
	    mContext.registerReceiver(receiver,
	        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	    showDownloadDialog();//显示展示下载进度信息的Dialog
	  }

	  /**进度信息*/
	  private TextView downloadProgressTv;
	  private ProgressBar progressBar;
	  
	  /**显示下载进度的Dialog**/
	  private void showDownloadDialog(){
		  downdialog =  DialogUtil.getDialogByView(mContext, getDownloadView());
		  downdialog.setCancelable(false);
		  downdialog.setCanceledOnTouchOutside(false);
		  downdialog.show();
		  setTimer();
	  }
	  
	  /**获取显示现在进度的View**/
	  private View getDownloadView(){
		  View view=LayoutInflater.from(mContext).inflate(R.layout.uploadprogress_dialog, null);
		  ImageView imageView=(ImageView)view.findViewById(R.id.upprogress_titleimg);
		  imageView.setImageResource(R.drawable.download_yellow48);
		  downloadProgressTv=(TextView) view.findViewById(R.id.upprogress_bfb);
		  progressBar=(ProgressBar) view.findViewById(R.id.upprogress_progress);
		  TextView titleTv=(TextView) view.findViewById(R.id.upprogress_title);
		  titleTv.setText("更新中，请耐心等待！");
		return view;
	  }
	  
	  /**更新下载进度显示信息**/
//	  private void upDataDownloadMsg( Cursor c){
//		  int downloadBytesIdx = c.getColumnIndexOrThrow(
//                 DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
//         int totalBytesIdx = c.getColumnIndexOrThrow(
//                 DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
//         long totalBytes = c.getLong(totalBytesIdx);
//         long downloadBytes = c.getLong(downloadBytesIdx);
//         int total=(int) (downloadBytes * 100 / totalBytes);  
//         downloadProgressTv.setText("已完成："+total+"%");
//	  }
	  
	  private void setTimer(){
		  Timer timer=new Timer();
		  timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(getDownloadPercent(downloadId));
			}
		}, 0, 200);
	  }
	  
	  /**获取下载进度**/
	  @SuppressLint("NewApi")
	private int getDownloadPercent(long downloadId){
		  try {
	        DownloadManager.Query query = new Query().setFilterById(downloadId);
	        Cursor c =  downloadManager.query(query);
	        if(c!=null && c.moveToFirst()){
	            int downloadBytesIdx = c.getColumnIndexOrThrow(
	                    DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
	            int totalBytesIdx = c.getColumnIndexOrThrow(
	                    DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
	            long totalBytes = c.getLong(totalBytesIdx);
	            long downloadBytes = c.getLong(downloadBytesIdx);
	            return (int) (downloadBytes * 100 / totalBytes);            
	        }
	        return 0;
		  } catch (IllegalArgumentException e) {
				e.printStackTrace();
				return 0;
			}
	    }
	  
	  /**通过handler更新控件*/
	  @SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			downloadProgressTv.setText("已完成："+msg.what+"%");
			progressBar.setProgress(msg.what);
		}
		  
	  };
	  
	 
	  //广播监听下载的各个状态
	  public BroadcastReceiver receiver = new BroadcastReceiver() {
	    @SuppressLint("NewApi")
		@Override
	    public void onReceive(Context context, Intent intent) {
	      checkStatus();
	    }
	  };
	 
	 
	  //检查下载状态
	  @SuppressLint("NewApi")
	private void checkStatus() {
	    Query query = new Query();
	    //通过下载的id查找
	    query.setFilterById(downloadId);
	    Cursor c = downloadManager.query(query);
	    if (c!=null && c.moveToFirst()) {
//	    	upDataDownloadMsg(c);
	    	int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
	      switch (status) {
	         //下载暂停
	        case DownloadManager.STATUS_PAUSED:
	          break;
	        //下载延迟
	        case DownloadManager.STATUS_PENDING:
	          break;
	        //正在下载
	        case DownloadManager.STATUS_RUNNING:
	          break;
	        //下载完成
	        case DownloadManager.STATUS_SUCCESSFUL:
	        	downdialog.dismiss();
//				mContext.finish();
				mContext.unregisterReceiver(receiver); //下载完成注销广播，避免报错。
	          //下载完成安装APK
	          installAPK();
				mContext.finish();
	          break;
	        //下载失败
	        case DownloadManager.STATUS_FAILED:
	        downdialog.dismiss();
//	          Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
	        showerrorDialog();
	          break;
	      }
	    }
	  }
	  
	  /**下载失败提示Dialog*/
	  private void showerrorDialog(){
		  Dialog errDialog=DialogUtil.getAlertOneButton(mContext, "下载失败!", null);
		  errDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				 mContext.finish();
			}
		});
	  }
	  
	 
	  //下载到本地后执行安装
	  @SuppressLint("NewApi")
	private void installAPK() {
//		  File imagePath = new File(Context.getFilesDir(), "images");
//		  File newFile = new File(imagePath, "default_image.jpg");
//		  Uri contentUri = getUriForFile(mContext, "com.cninsure.cp.fileprovider", newFile); //包名.fileprovider


	    //获取下载文件的Uri
	    Uri downloadFileUri = downloadManager.getUriForDownloadedFile(downloadId);
	    if (downloadFileUri != null) {
	      String filePath=UriUtils.getFileUrl(mContext, downloadFileUri);
			if(!isGrantExternalRW(mContext)){
				ToastUtil.showToastLong(mContext,"没有读写权限！");
			}else {
				try {
					Intent install = new Intent(Intent.ACTION_VIEW);
					if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
						File file = new File(filePath);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						//        Uri uri = Uri.fromFile(file);7.0以前版本可以用这种方式 获取uri
						Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", file);//7.0以后 就要使用fileprovider来封装Uri
						intent.setFlags(flags);
						intent.setDataAndType(uri, "application/vnd.android.package-archive");
						mContext.startActivity(intent);
					} else {
						install.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
						install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity(install);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

//			Intent install = new Intent(Intent.ACTION_VIEW);
//			if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
//				install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
//				install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
//				mContext.startActivity(install);
//			} else {
//				install.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
//				install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(install);
//			}
	      
	    }
	  }

	/**
	 * 获取储存权限
	 * @return
	 */

	public static boolean isGrantExternalRW(Activity mContext) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext.checkSelfPermission(
				Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

			mContext.requestPermissions(new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE
			}, 1);

			return false;
		}
		return true;
	}

}