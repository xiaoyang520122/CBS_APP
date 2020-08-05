package com.cninsure.cp.ocr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.OCREntity;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.PhotoPathUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.SignView;

public class LinePathActivity extends BaseActivity implements OnClickListener {
	private SignView mPathView;
	private String filePath;
	/**已上传图片路径**/
	private String httpSignPath;
//	private LinearLayout parentView;
//	private LinearLayout parentLinea;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* 隐藏标题栏 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* 全屏 */
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/* 设定屏幕显示为横向 */
//		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.linepath_layout);
		initView();
	}

	private void initView() {
		if (null!=getIntent().getSerializableExtra("ocrEntity5")) { //获取已上传的签名地址
			OCREntity ocrEntity=(OCREntity) getIntent().getSerializableExtra("ocrEntity5");
			httpSignPath=AppApplication.getUSER().data.qiniuUrl+ocrEntity.url;
		}
		mPathView=(SignView) findViewById(R.id.LinePath_view);

		findViewById(R.id.LinePath_cancle).setOnClickListener(this);
		findViewById(R.id.LinePath_submit).setOnClickListener(this);
		findViewById(R.id.LinePath_delete).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.LinePath_cancle:
			finish();
			break;
		case R.id.LinePath_submit:
			saveLinePath();
			break;
		case R.id.LinePath_delete:
			mPathView.clearPath();
			break;

		default:
			break;
		}
	}
	
//	public void LinePathOnclick(View v){
//		switch (v.getId()) {
//		case R.id.LinePath_cancle:
//			finish();
//			break;
//		case R.id.LinePath_submit:
//			saveLinePath();
//			break;
//		case R.id.LinePath_delete:
//			mPathView.clearPath();
//			break;
//
//		default:
//			break;
//		}
//	}
	
	/**保存签字
	 *  view.setDrawingCacheEnabled(true);  
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);  
        view.setDrawingCacheBackgroundColor(Color.WHITE);  
  
        // 把一个View转换成图片  
        Bitmap cachebmp = loadBitmapFromView(view);  
	 * */
	private void saveLinePath(){
		if (mPathView.getTouched()) {
		    try {
		    	filePath=PhotoPathUtil.getPictureCreatePath(getIntent().getStringExtra("orderUid"));
//		        mPathView.saveImageToFile(filePath);
		    	
		    	savaSignPhoto();
		        Intent intent=new Intent();
		        intent.putExtra("LinePathFilePath", filePath);
		        setResult(HttpRequestTool.LINEPATH, intent);
		        ToastUtil.showToastShort(LinePathActivity.this, "签名保存成功！");
		        finish();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		} else {
	        ToastUtil.showToastShort(LinePathActivity.this, "您没有签名~");
		}
	}

	private void savaSignPhoto() throws IOException{
        // 获得图片  
        Bitmap bitmap = loadBitmapFromView(mPathView);
        //再将bitmap转化为字节数组
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		rotaingImageView(270,bitmap).compress(Bitmap.CompressFormat.JPEG, 100, baos);  
		byte[] datas = baos.toByteArray(); 
		// 将文件存储在SD卡的根目录，并以系统时间将文件命名
		File jpgFile = new File(filePath);
		// 文件输出流对象
		FileOutputStream outStream = new FileOutputStream(jpgFile);
		// 将文件数据存储到文件中
		outStream.write(datas);
		// 关闭输出流
		outStream.close();
	}
	
	/**
	 * 将View转换为图片
	 * @param v
	 * @return
	 */
	private Bitmap loadBitmapFromView(View v) {  
        int w = v.getWidth();  
        int h = v.getHeight();  
  
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);  
        Canvas c = new Canvas(bmp);  
  
        c.drawColor(Color.WHITE);  
        /** 如果不设置canvas画布为白色，则生成透明 */  
  
        v.layout(0, 0, w, h);  
        v.draw(c);  
  
        return bmp;  
    }  
	
	@Override
	protected void onStop() {
		super.onPause();
		  Intent intent=new Intent();
	      intent.putExtra("LinePathFilePath", filePath);
	      setResult(HttpRequestTool.LINEPATH, intent);
	}
	
	/**
	    * 旋转图片 
	    * @param angle 
	    * @param bitmap 
	    * @return Bitmap 
	    */ 
	   public Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
	       //旋转图片 动作   
	       Matrix matrix = new Matrix();
	       matrix.postRotate(angle);
	       // 创建新的图片   
	       return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
	   }

}
