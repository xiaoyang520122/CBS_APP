package com.cninsure.cp.ocr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.utils.ImageUtil;
import com.cninsure.cp.utils.PhotoPathUtil;

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

	private SurfaceView surfaceView;
	private ImageView imageView;
	private Button takepictureButton;
	private TextView submitTv, rePhotoTv, flashlightTv;

	private Camera camera; // 相机
	/** 拍照按钮被重复点击时会出现bug,通过标识来控制 **/
	private boolean safeToTakePicture = true;
	private Intent intent;
	private boolean mIsLight = false;
	private Camera.Parameters parameters;
	private CheckBox flashLightCheckBox;
	private SimpleDateFormat sf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* 隐藏标题栏 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* 全屏 */
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		/* 设定屏幕显示为横向 */
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.camera_ocr);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		initview();
		initListener();
	}

	/**
	 * 初始化监听器
	 */
	private void initListener() {
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceview);
		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(this);
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@SuppressLint("SimpleDateFormat")
	private void initview() {
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		imageView = (ImageView) findViewById(R.id.takepicture_imageView);
		takepictureButton = (Button) findViewById(R.id.takepicture);
		submitTv = (TextView) findViewById(R.id.takepicture_sumit);
		rePhotoTv = (TextView) findViewById(R.id.takepicture_rephotography);
		flashlightTv = (TextView) findViewById(R.id.cameraOcr_lightt);
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

	/*** 选择最佳尺寸方法 */
	@SuppressLint("NewApi")
	private Point getBestCameraResolution(Camera.Parameters parameters) { // 去掉参数
																			// ,
																			// Point
																			// screenResolution
		/**
		 * getRealMetrics - 屏幕的原始尺寸，即包含状态栏。 version >= 4.2.2
		 */
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;

		float tmp = 0f;
		float mindiff = 100f;
		// float x_d_y = (float)screenResolution.x / (float)screenResolution.y;
		float x_d_y = (float) width / (float) height;
		Size best = null;
		List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
		for (Size s : supportedPreviewSizes) {
			tmp = Math.abs(((float) s.height / (float) s.width) - x_d_y);
			if (tmp < mindiff) {
				mindiff = tmp;
				best = s;
				int wh = best.width;
				int he = best.height;
				parameters.setPreviewSize(best.width, best.height);
			}
		}
		return new Point(best.width, best.height);
	}

	/** 设置开关摄像头电筒 **/
	private void openFlashLight() {
		flashLightCheckBox = (CheckBox) findViewById(R.id.cameraOcr_light);

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
		findViewById(R.id.cameraOcr_lightt).setOnClickListener(clickListener);
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
		// camera.setPreviewDisplay(surfaceView.getHolder());
		// try {
		// camera.setPreviewDisplay(holder);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// camera.setPreviewCallback(new Camera.PreviewCallback() {
		// @Override
		// public void onPreviewFrame(byte[] data, Camera camera) {
		//
		// }
		// });
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
			case R.id.takepicture:
				// 拍照片经过压缩处理后的图片调用MyPictureCallback方法
				// camera.takePicture(null, null, new MyPictureCallback());
				if (safeToTakePicture) {
					camera.takePicture(null, null, new MyPictureCallback());
					safeToTakePicture = false;
				}
				break;
			case R.id.autofocus:
				// 如果不想得到对焦事件，传送NULL事件进去
				camera.autoFocus(null);
				break;
			case R.id.cameraOcr_back:
				finish();
				break;
			default:
				break;
			}
		}
	}

	/*
	 * 获取图片对象
	 */
	private final class MyPictureCallback implements PictureCallback {
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				// 将文件存储在SD卡的根目录，并以系统时间将文件命名
				File jpgFile = new File(PhotoPathUtil.getPictureCreatePath(getIntent().getStringExtra("orderUid"),CameraActivity.this));
				// 文件输出流对象
				FileOutputStream outStream = new FileOutputStream(jpgFile);
				// 将文件数据存储到文件中
				// outStream.write(data);

				// 将文件转化为bitmap以便添加日期水印
				Bitmap bitmap = ImageUtil.drawTextToRightBottom(CameraActivity.this, BitmapFactory.decodeByteArray(data, 0, data.length), AppApplication.getUSER().data.name + " " + sf.format(new Date()),
						10, Color.parseColor("#FF0000"), 5, 5);
				Bitmap watermark = BitmapFactory.decodeResource(getResources(), R.drawable.logo_water_mask);
				// 为bitmap以便添加图片水印
				bitmap = ImageUtil.createWaterMaskLeftBottom(CameraActivity.this, bitmap, watermark, 5, 5);
				// 再将bitmap转化为字节数组
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				byte[] datas = baos.toByteArray();

				// 将文件数据存储到文件中
				outStream.write(datas);
				// 关闭输出流
				outStream.close();
				// 开始预览照片
				camera.startPreview();
				safeToTakePicture = true;
				displayPhoto(jpgFile);
			} catch (IOException e) {
				intent = new Intent();
				setResult(0, intent);
				finish();
				e.printStackTrace();
			}
			// finish(); // 结束当前的activity的生命周期
		}
	}

	/** 预览拍照并提供重拍和确定选择 **/
	private void displayPhoto(final File jpgFile) {
		imageView.setVisibility(View.VISIBLE);
		Glide.with(this).load(jpgFile).into(imageView);
		surfaceView.setVisibility(View.GONE);
		submitTv.setVisibility(View.VISIBLE);
		rePhotoTv.setVisibility(View.VISIBLE);
		takepictureButton.setVisibility(View.GONE);
		flashLightCheckBox.setVisibility(View.GONE);// 隐藏闪光灯按钮
		flashlightTv.setVisibility(View.GONE);// 隐藏闪光灯按钮文本

		submitTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				intent = new Intent();
				intent.putExtra("FilePath", jpgFile);
				setResult(102, intent);
				CameraActivity.this.finish();
			}
		});

		rePhotoTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				imageView.setVisibility(View.GONE);
				surfaceView.setVisibility(View.VISIBLE);
				submitTv.setVisibility(View.GONE);
				rePhotoTv.setVisibility(View.GONE);
				takepictureButton.setVisibility(View.VISIBLE);
				flashLightCheckBox.setVisibility(View.VISIBLE);// 显示闪光灯按钮
				flashLightCheckBox.setChecked(false);// 调整闪光灯开关选择状态为关
				flashlightTv.setVisibility(View.VISIBLE);// 显示闪光灯按钮文本
			}
		});

		findViewById(R.id.cameraOcr_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CameraActivity.this.finish();
			}
		});
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
