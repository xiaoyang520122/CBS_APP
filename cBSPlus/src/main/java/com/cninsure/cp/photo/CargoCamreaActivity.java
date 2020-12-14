package com.cninsure.cp.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cninsure.cp.R;
import com.cninsure.cp.cargo.CargoWorkActivity;
import com.cninsure.cp.cargo.adapter.CargoPhotoChoiceActivity;
import com.cninsure.cp.entity.cargo.CargoCaseWorkImagesTable;
import com.cninsure.cp.utils.GlideCircleTransform;
import com.cninsure.cp.utils.ImageUtil;
import com.cninsure.cp.utils.PhotoPathUtil;
import com.karics.library.zxing.view.VerticalViewPager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class CargoCamreaActivity extends Activity implements SurfaceHolder.Callback {

	private SurfaceView surfaceView;
	private ImageView displayimageView;
	private VerticalViewPager verticalViewPager;

	private Camera camera; // 相机
	/** 拍照按钮被重复点击时会出现bug,通过标识来控制 **/
	private boolean safeToTakePicture = true;
	private Intent intent;
	private boolean mIsLight = false;
	private Camera.Parameters parameters;
	private CheckBox flashLightCheckBox;
	private SimpleDateFormat sf;
	private String typeId;
	private String baoanUid;
	private Map<String , List<CargoCaseWorkImagesTable>> classImgMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* 隐藏标题栏 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* 全屏 */
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/* 设定屏幕显示为横向 */
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.camera_photo);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		initData();
		initview();
		initListener();
	}

	private void initData() {
        classImgMap = CargoWorkActivity.classImgMap;
		typeId =  getIntent().getStringExtra("typeId");
		baoanUid = getIntent().getStringExtra("baoanUid");
	}

	/**
	 * 初始化监听器
	 */
	private void initListener() {
		surfaceView = this.findViewById(R.id.CXPA_surfaceview);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(this);
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@SuppressLint("SimpleDateFormat")
	private void initview() {
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			verticalViewPager=(VerticalViewPager) findViewById(R.id.CXPA_viewpager);
//			verticalViewPager.setPageMargin(10);
//			verticalViewPager.setOffscreenPageLimit(5);
//			verticalViewPager.setAdapter(new MyVpAdater());
//			verticalViewPager.setCurrentItem(GroupId);
			verticalViewPager.setVisibility(View.GONE);
//			setViewPagerOnPageChangeListener();
		displayimageView=(ImageView) findViewById(R.id.CXPA_displaypicture);
	}

	/*
	 * 设置摄像头参数
	 */
	@SuppressLint("NewApi")
	public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open();
			if (camera == null) {
				int cametacount = Camera.getNumberOfCameras();
				camera = Camera.open(cametacount - 1);
			}
	}


	/** 设置开关摄像头电筒 **/
	private void openFlashLight() {
		flashLightCheckBox = (CheckBox) findViewById(R.id.CXPA_light);

		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mIsLight) {// 关闭手电筒
					List<String> flashModes = parameters.getSupportedFlashModes();
					if (flashModes == null) {
						return;
					}
					String flashMode = parameters.getFlashMode();
					if (!flashMode.contains(Camera.Parameters.FLASH_MODE_OFF)) {
						parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
						camera.setParameters(parameters);
						flashLightCheckBox.setChecked(false);
						// Toast.makeText(OpenLightActivity.this, "关闭手电筒成功",
						// Toast.LENGTH_SHORT).show();
					}
					mIsLight = false;
				} else {// 打开手电筒
					List<String> flashModes = parameters.getSupportedFlashModes();
					if (flashModes == null) {
						return;
					}
					String flashMode = parameters.getFlashMode();
					if (!flashMode.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
						parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
						camera.setParameters(parameters);
						flashLightCheckBox.setChecked(true);
						// Toast.makeText(OpenLightActivity.this, "打开手电筒成功",
						// Toast.LENGTH_SHORT).show();
					}
					mIsLight = true;
				}
			}
		};
		findViewById(R.id.CXPA_lightt).setOnClickListener(clickListener);
		flashLightCheckBox.setOnClickListener(clickListener);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int heigh) {
		// 获取摄像头参数对象
		parameters = camera.getParameters();
		// 设置摄像头分辨率
		// parameters.setPreviewSize(800, 480);
		// getBestCameraResolution(parameters);
		// parameters.setJpegQuality(80);//照片质量
		// // parameters.setPictureSize(1024, 768);//图片分辨率
		// parameters.setPreviewFrameRate(5);//预览帧率
		// camera.setParameters(parameters);
		// camera.setDisplayOrientation(90);//旋转90度
		/**
		 * 设置预显示
		 */
		parameters.setPreviewSize(getScreenWH().widthPixels, getScreenWH().heightPixels);
		parameters.setPictureSize(getScreenWH().widthPixels, getScreenWH().heightPixels);
		/**
		 * 开启预览
		 */
//		camera.setParameters(parameters);

		try {
			camera.setParameters(parameters);
		} catch (Exception e) {
		    //非常罕见的情况
		    //个别机型在SupportPreviewSizes里汇报了支持某种预览尺寸，但实际是不支持的，设置进去就会抛出RuntimeException.
		    e.printStackTrace();
		    try {
		        //遇到上面所说的情况，只能设置一个最小的预览尺寸
		    	parameters.setPreviewSize(1920, 1080);
		        camera.setParameters(parameters);
		    } catch (Exception e1) {
		        //到这里还有问题，就是拍照尺寸的锅了，同样只能设置一个最小的拍照尺寸
		        e1.printStackTrace();
		        try {
		        	parameters.setPictureSize(1920, 1080);
		            camera.setParameters(parameters);
		        } catch (Exception ignored) {
		        }
		    }
		}

		try {
			// 设置显示
			camera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			camera.release();
			camera = null;
		}
		camera.startPreview();
		openFlashLight();
		intent = null;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// 如果摄像头不使用时，关闭摄像头
		if (camera != null) {
			try {
				camera.setPreviewCallback(null);
				camera.stopPreview();
				camera.release();
				camera = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * 屏幕被触摸事件屏幕被按下后，可以响应自动对焦
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// layout.setVisibility(ViewGroup.VISIBLE);
		}
		return super.onTouchEvent(event);
	}

	/*
	 * 通过switch (v.getId()) 选择拍照事件和对焦事件
	 */
	public void takepicture(View v) {
		if (camera != null) {
			switch (v.getId()) {
			case R.id.CXPA_takepicture:
				// 拍照片经过压缩处理后的图片调用MyPictureCallback方法
				// camera.takePicture(null, null, new MyPictureCallback());
				if (safeToTakePicture) {
					camera.takePicture(null, null, new MyPictureCallback());
					safeToTakePicture = false;
				}
				break;
			case R.id.CXPA_autofocus:
				// 如果不想得到对焦事件，传送NULL事件进去
				camera.autoFocus(null);
				break;
			case R.id.CXPA__back:
				finish();
				break;
			case R.id.CXPA_takepictureAuml:  //相册选择照片
				checkPhotos();
				break;
			default:
				break;
			}
		}
	}

	private void checkPhotos() {
		Intent getAlbum=new Intent(this, CargoPhotoChoiceActivity.class);
		getAlbum.putExtra("typeId", typeId);
		getAlbum.putExtra("baoanUid", baoanUid);
		startActivity(getAlbum);
		this.finish();
	}


	/*
	 * 获取图片对象
	 */
	private final class MyPictureCallback implements PictureCallback {
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				// 将文件存储在SD卡的根目录，并以系统时间将文件命名
				String PicturePath=PhotoPathUtil.getPictureCreatePath(baoanUid);
				File jpgFile = new File(PicturePath);
				// 文件输出流对象
				FileOutputStream outStream = new FileOutputStream(jpgFile);
				// 将文件数据存储到文件中
				outStream.write(data);
				// 关闭输出流
				outStream.close();

				Intent intentdata=new Intent();
				intentdata.setData(Uri.fromFile(new File(PicturePath)));
		    	ImageUtil.compressBmp(CargoCamreaActivity.this, intentdata, PicturePath);//压缩、水印、储存
				// 开始预览照片
				try{camera.startPreview();} catch (Exception e){e.printStackTrace();}
				safeToTakePicture = true;
				displayPhoto(PicturePath);
			} catch (Exception e) {
				intent = new Intent();
				setResult(0, intent);
//				finish();
				e.printStackTrace();
			}
			// finish(); // 结束当前的activity的生命周期
		}
	}

	/** 预览拍照并提供重拍和确定选择 **/
	private void displayPhoto(final String jpgFilePath) {
		Glide.with(this).load(jpgFilePath)
				.placeholder(R.drawable.photoyulan_130)
				.centerCrop().transform(new GlideCircleTransform(this)).into(displayimageView);
		CargoCaseWorkImagesTable imgEn = new CargoCaseWorkImagesTable();
		imgEn.fileUrl = jpgFilePath;
		imgEn.source = 2;
		imgEn.fileSuffix = jpgFilePath.substring(jpgFilePath.lastIndexOf("."));
		imgEn.type = typeId;
		imgEn.baoanUid = baoanUid;
		imgEn.fileName = jpgFilePath.substring(jpgFilePath.lastIndexOf("/")+1);
		classImgMap.get(typeId).add(imgEn);
		CargoWorkActivity.adapter2.getCameraPhoto();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 如果摄像头不使用时，关闭摄像头
		try {
			if (camera != null) {
				camera.setPreviewCallback(null);
				camera.stopPreview();
				camera.release();
				camera = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (intent == null) {
			intent = new Intent();
			setResult(0, intent);
		}
	}

	protected DisplayMetrics getScreenWH() {
		DisplayMetrics dMetrics = new DisplayMetrics();
		dMetrics = this.getResources().getDisplayMetrics();
		return dMetrics;
	}

}
