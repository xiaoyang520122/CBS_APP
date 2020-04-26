package com.cninsure.cp.utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

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
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cninsure.cp.R;

public class FileDownOpenUtil {

	 //下载器
	  private DownloadManager downloadManager;
	  //上下文
	  private Activity mContext;
	  //下载的ID
	  private long downloadId;
	  /**下载进度显示Dialog*/
	  private Dialog downdialog;
	  public FileDownOpenUtil(Activity context){
		    this.mContext = context;
		  }
	  @SuppressWarnings("unused")
	private FileDownOpenUtil( ){ }
	 
	  //下载apk
	  @SuppressLint({ "InlinedApi", "NewApi" })
	public void downloadAndOpen(String url, String name) {
	 
	    //创建下载任务
	    Request request = new Request(Uri.parse(url));
	    //移动网络情况下是否允许漫游
	    request.setAllowedOverRoaming(false);
	    //在通知栏中显示，默认就是显示的
	    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
	    request.setTitle("医健险资料下载");
	    request.setDescription("File Downloading");
	    request.setVisibleInDownloadsUi(true);
	 
	    //设置下载的路径
	    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + name;
	    File file = new File(filePath);
	    if (file.exists()) {
	    	 OpenFileUtil.openFileByPath(mContext, filePath);
	    	 return;
		}else {
			request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getAbsolutePath() , name);
		}
	 
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
	  private int getDownloadPercent(long downloadId){
	        DownloadManager.Query query = new Query().setFilterById(downloadId);
	        Cursor c =  downloadManager.query(query);
	        if(c.moveToFirst()){
	            int downloadBytesIdx = c.getColumnIndexOrThrow(
	                    DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
	            int totalBytesIdx = c.getColumnIndexOrThrow(
	                    DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
	            long totalBytes = c.getLong(totalBytesIdx);
	            long downloadBytes = c.getLong(downloadBytesIdx);
	            return (int) (downloadBytes * 100 / totalBytes);            
	        }
	        return 0;
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
	  private BroadcastReceiver receiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	      checkStatus();
	    }
	  };
	 
	 
	  //检查下载状态
	  private void checkStatus() {
	    Query query = new Query();
	    //通过下载的id查找
	    query.setFilterById(downloadId);
	    Cursor c = downloadManager.query(query);
	    if (c.moveToFirst()) {
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
	        	mContext.finish();
	          //下载完成打开文件
	          openFile();
	          c.close();
	          break;
	        //下载失败
	        case DownloadManager.STATUS_FAILED:
		          c.close();
	        downdialog.dismiss();
	        DialogUtil.getAlertOneButton(mContext, "下载失败!", null).show();
	          break;
	      }
	    }
	  }
	  
	  //下载到本地后执行安装
	  @SuppressLint("NewApi")
	private void openFile() {
		  
	    //获取下载文件的Uri
	    Uri downloadFileUri = downloadManager.getUriForDownloadedFile(downloadId);
	    if (downloadFileUri != null) {
//	      Intent intent= new Intent(Intent.ACTION_VIEW);
	      String filePath=UriUtils.getFileUrl(mContext, downloadFileUri);
//	      File apkfile=new File(filePath);
	      OpenFileUtil.openFileByPath(mContext, filePath);
	      
//	      Intent install = new Intent(Intent.ACTION_VIEW);
//	      if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
//	    	    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	    	    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
//	    	    install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
//	    	    mContext.startActivity(install);
//	    	}else {
//	    		install.setDataAndType(Uri.parse("file://"+filePath), "application/vnd.android.package-archive");
//	    		install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	    	    mContext.startActivity(install);
//	    	}
	    }
	  }
}