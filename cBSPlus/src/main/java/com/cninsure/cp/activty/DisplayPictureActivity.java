package com.cninsure.cp.activty;

import java.io.File;

import org.apache.commons.net.ftp.FTPClient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.cninsure.cp.R;
import com.cninsure.cp.utils.Common;
import com.cninsure.cp.utils.FtpUpload;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.GestureImageView;

public class DisplayPictureActivity extends Activity {

	private GestureImageView photoView;
	private String picUrl;
	private Bitmap bitmap = null;
	private FTPClient ftpClient = null; 
	public  FTPClient ftp;
	  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_show_picture);
		photoView = (GestureImageView) findViewById(R.id.photoview);
		final View titleLayoutView = findViewById(R.id.title_layout);
		picUrl = getIntent().getStringExtra("picUrl");
//		titleLayoutView.setVisibility(View.GONE);
		ftpClient = new FTPClient(); 
//		photoView.setOnSingleTapListener(new OnSingleTapListener() {
//			@Override
//			public void onSingleClick() {
//				if (titleLayoutView.getVisibility() == View.GONE) {
//					titleLayoutView.setVisibility(View.VISIBLE);
//				} else if (titleLayoutView.getVisibility() == View.VISIBLE) {
//					titleLayoutView.setVisibility(View.GONE);
//				}
//			}
//		});
		 photoView.setOnClickListener(new OnClickListener() {
		 @Override
		 public void onClick(View arg0) {
		 finish();
		 }
		 });

		findViewById(R.id.back_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		if (picUrl != null) {
			if (picUrl.indexOf("ftp://") > -1) {// http图片直接显示
				init();
			} else {// ftp服务器图片需要先下载
				Glide.with(this).load(picUrl).placeholder(R.drawable.waiting_photo100).error(R.drawable.warn_photo100).into(photoView);

			}

		}
	}


	
	/**
    *
    * 初始化目录
    *
    * */
	private String savePath="";
    private static String PATH_LOGCAT;
   public void init() {
       if (Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
           PATH_LOGCAT = Environment.getExternalStorageDirectory()
                   .getAbsolutePath() + File.separator + "TempImag";
       } else {// 如果SD卡不存在，就保存到本应用的目录下
           PATH_LOGCAT = this.getFilesDir().getAbsolutePath()
                   + File.separator + ("TempImag");
       }
       final File file = new File(PATH_LOGCAT);
       if (!file.exists()) {
           file.mkdirs();
       }
       savePath = file.getPath()+picUrl.substring(picUrl.lastIndexOf("/"));
    	   new Thread(new Runnable() {
			@Override
			public void run() {
				try {
//					downLoadImage(savePath,picUrl);
					downLoadImage();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
   }
   //String localPath, String remotePath
	public void downLoadImage () throws Exception {
		FtpUpload t = new FtpUpload();
		Log.e("LOAD_SUCCESS", "开始ftp连接");
		if (t.connect("", Common.FTP_IP, 21, Common.FTP_USERNAME,Common.FTP_PASSWORD)) {
			Log.e("LOAD_SUCCESS", "ftp连接成功");
			ToastUtil.showToastLong(this, "连接成功：");
		}else {
			Log.e("LOAD_SUCCESS", "ftp连接失败");
		}
		boolean successF=t.downloadT(savePath, picUrl);
		Log.e("LOAD_SUCCESS", "开始ftp连接");
		if (successF) {
			Log.e("LOAD_SUCCESS","是否下载成功："+successF+"路径："+savePath);
		}else {
			Log.e("LOAD_SUCCESS","下载失败："+successF);
		}
		handler.sendMessage(new Message());
	}
	
	Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			try {
				BitmapFactory bmf=new BitmapFactory();
				photoView.setImageBitmap(bmf.decodeFile(savePath));
				super.handleMessage(msg);
			} catch (Exception e) {
				Glide.with(DisplayPictureActivity.this).load(savePath)
				.placeholder(R.drawable.waiting_photo100).error(R.drawable.warn_photo100).into(photoView);
				e.printStackTrace();
			}
		}
	};
	
	@Override
	protected void onStop() {
		File file=new File(savePath);
		 // 路径为文件且不为空则进行删除  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	    } 
		super.onStop();
	}
}