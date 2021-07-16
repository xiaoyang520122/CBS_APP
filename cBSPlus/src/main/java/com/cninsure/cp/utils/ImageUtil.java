package com.cninsure.cp.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.greenrobot.eventbus.EventBus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.lidroid.xutils.BitmapUtils;

/**
 * 图片工具类 压缩图片：图片压缩：先是大小比例压缩再进行质量压缩，这样压缩时间相对较短
 * 
 * public static Bitmap File2BitmapUpload(String srcPath) {
 * BitmapFactory.Options newOpts = new BitmapFactory.Options(); //
 * 开始读入图片，此时把options.inJustDecodeBounds 设回true了 newOpts.inJustDecodeBounds =
 * true; Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
 * newOpts.inJustDecodeBounds = false; int w = newOpts.outWidth; int h =
 * newOpts.outHeight; float hh = 800f;// 这里设置高度为800f float ww = 480f;//
 * 这里设置宽度为480f // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可 int be = 1;// be=1表示不缩放 if (w
 * > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放 be = (int) (newOpts.outWidth / ww); }
 * else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放 be = (int) (newOpts.outHeight
 * / hh); } if (be <= 0) be = 1; newOpts.inSampleSize = be;// 设置缩放比例 //
 * 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了 bitmap =
 * BitmapFactory.decodeFile(srcPath, newOpts); return compressImage(bitmap);//
 * 压缩好比例大小后再进行质量压缩 // return bitmap; } public static Bitmap compressImage(Bitmap
 * image) { ByteArrayOutputStream baos = new ByteArrayOutputStream();
 * image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
 * 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中 int options = 100; while
 * (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
 * baos.reset();// 重置baos即清空baos image.compress(Bitmap.CompressFormat.JPEG,
 * options, baos);// 这里压缩options%，把压缩后的数据存放到baos中 options -= 10;// 每次都减少10 }
 * ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//
 * 把压缩后的数据baos存放到ByteArrayInputStream中 Bitmap bitmap =
 * BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
 * return bitmap; }
 */
public class ImageUtil {
	
	private static int PHOTO_REQUEST_CUT=5;
	@SuppressLint("SimpleDateFormat")
	static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 设置水印图片在左上角
	 * 
	 * @param src
	 * @param watermark
	 * @param paddingLeft
	 * @param paddingTop
	 * @return
	 */
	public static Bitmap createWaterMaskLeftTop(Context context, Bitmap src, Bitmap watermark, int paddingLeft, int paddingTop) {
		return createWaterMaskBitmap(src, watermark, dp2px(context, paddingLeft), dp2px(context, paddingTop));
	}

	private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark, int paddingLeft, int paddingTop) {
		if (src == null) {
			return null;
		}
		int width = src.getWidth();
		int height = src.getHeight();
		// 创建一个bitmap
		Bitmap newb = Bitmap.createBitmap(width, height, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		// 将该图片作为画布
		Canvas canvas = new Canvas(newb);
		// 在画布 0，0坐标上开始绘制原始图片
		canvas.drawBitmap(src, 0, 0, null);
		// 在画布上绘制水印图片
		canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
		// 保存
//		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.save();
		// 存储
		canvas.restore();
		return newb;
	}

	/**
	 * 设置水印图片在右下角
	 * 
	 * @param Context
	 * @param src
	 * @param watermark
	 * @param paddingRight
	 * @param paddingBottom
	 * @return
	 */
	public static Bitmap createWaterMaskRightBottom(Context context, Bitmap src, Bitmap watermark, int paddingRight, int paddingBottom) {
		return createWaterMaskBitmap(src, watermark, src.getWidth() - watermark.getWidth() - dp2px(context, paddingRight), src.getHeight()
				- watermark.getHeight() - dp2px(context, paddingBottom));
	}

	/**
	 * 设置水印图片到右上角
	 * 
	 * @param Context
	 * @param src
	 * @param watermark
	 * @param paddingRight
	 * @param paddingTop
	 * @return
	 */
	public static Bitmap createWaterMaskRightTop(Context context, Bitmap src, Bitmap watermark, int paddingRight, int paddingTop) {
		return createWaterMaskBitmap(src, watermark, src.getWidth() - watermark.getWidth() - dp2px(context, paddingRight), dp2px(context, paddingTop));
	}

	/**
	 * 设置水印图片到左下角
	 * 
	 * @param Context
	 * @param src
	 * @param watermark
	 * @param paddingLeft
	 * @param paddingBottom
	 * @return
	 */
	public static Bitmap createWaterMaskLeftBottom(Context context, Bitmap src, Bitmap watermark, int paddingLeft, int paddingBottom) {
		return createWaterMaskBitmap(src, watermark, dp2px(context, paddingLeft),
				src.getHeight() - watermark.getHeight() - dp2px(context, paddingBottom));
	}

	/**
	 * 设置水印图片到中间
	 * 
	 * @param Context
	 * @param src
	 * @param watermark
	 * @return
	 */
	public static Bitmap createWaterMaskCenter(Bitmap src, Bitmap watermark) {
		return createWaterMaskBitmap(src, watermark, (src.getWidth() - watermark.getWidth()) / 2, (src.getHeight() - watermark.getHeight()) / 2);
	}

	/**
	 * 给图片添加文字到左上角
	 * 
	 * @param context
	 * @param bitmap
	 * @param text
	 * @return
	 */
	public static Bitmap drawTextToLeftTop(Context context, Bitmap bitmap, String text, int size, int color, int paddingLeft, int paddingTop) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(context, bitmap, text, paint, bounds, dp2px(context, paddingLeft), dp2px(context, paddingTop) + bounds.height());
	}

	/**
	 * 绘制文字到右下角
	 * 
	 * @param context
	 * @param bitmap
	 * @param text
	 * @param size
	 * @param color
	 * @param paddingLeft
	 * @param paddingTop
	 * @return
	 */
	public static Bitmap drawTextToRightBottom(Context context, Bitmap bitmap, String text, int size, int color, int paddingRight, int paddingBottom) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(context, bitmap, text, paint, bounds, bitmap.getWidth() - bounds.width() - dp2px(context, paddingRight),
				bitmap.getHeight() - dp2px(context, paddingBottom));
	}
	
	/**
	 * 绘制文字到右下角
	 * 
	 * @param context
	 * @param bitmap
	 * @param text
	 * @param size
	 * @param color
	 * @return
	 */
	public static Bitmap drawTextByShadowToRightBottom(Context context, Bitmap bitmap, String text, int size, int color, int paddingRight, int paddingBottom) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		int buttonShadow = dp2px(context, 0.5f);
		return drawTextToBitmap(context, bitmap, text, paint, bounds, bitmap.getWidth() - bounds.width() - dp2px(context, paddingRight),
				bitmap.getHeight() - dp2px(context, paddingBottom)+buttonShadow);
	}
	

	/**
	 * 绘制文字到右上方
	 * 
	 * @param context
	 * @param bitmap
	 * @param text
	 * @param size
	 * @param color
	 * @param paddingRight
	 * @param paddingTop
	 * @return
	 */
	public static Bitmap drawTextToRightTop(Context context, Bitmap bitmap, String text, int size, int color, int paddingRight, int paddingTop) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(context, bitmap, text, paint, bounds, bitmap.getWidth() - bounds.width() - dp2px(context, paddingRight),
				dp2px(context, paddingTop) + bounds.height());
	}

	/**
	 * 绘制文字到左下方
	 * 
	 * @param context
	 * @param bitmap
	 * @param text
	 * @param size
	 * @param color
	 * @param paddingLeft
	 * @param paddingBottom
	 * @return
	 */
	public static Bitmap drawTextToLeftBottom(Context context, Bitmap bitmap, String text, int size, int color, int paddingLeft, int paddingBottom) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(context, bitmap, text, paint, bounds, dp2px(context, paddingLeft), bitmap.getHeight() - dp2px(context, paddingBottom));
	}

	/**
	 * 绘制文字到中间
	 * 
	 * @param context
	 * @param bitmap
	 * @param text
	 * @param size
	 * @param color
	 * @return
	 */
	public static Bitmap drawTextToCenter(Context context, Bitmap bitmap, String text, int size, int color) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(color);
		paint.setTextSize(dp2px(context, size));
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		return drawTextToBitmap(context, bitmap, text, paint, bounds, (bitmap.getWidth() - bounds.width()) / 2,
				(bitmap.getHeight() + bounds.height()) / 2);
	}

	// 图片上绘制文字
	private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text, Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
		android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

		paint.setDither(true); // 获取跟清晰的图像采样
		paint.setFilterBitmap(true);// 过滤一些
		if (bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		}
		bitmap = bitmap.copy(bitmapConfig, true);
		Canvas canvas = new Canvas(bitmap);

		canvas.drawText(text, paddingLeft, paddingTop, paint);
		return bitmap;
	}
	
	/**
	 * 缩放图片
	 * 
	 * @param src
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
		if (w == 0 || h == 0 || src == null) {
			return src;
		} else {
			// 记录src的宽高
			int width = src.getWidth();
			int height = src.getHeight();
			// 创建一个matrix容器
			Matrix matrix = new Matrix();
			// 计算缩放比例
			float scaleWidth = (float) (w / width);
			float scaleHeight = (float) (h / height);
			// 开始缩放
			matrix.postScale(scaleWidth, scaleHeight);
			// 创建缩放后的图片
			return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
		}
	}

	/**
	 * dip转pix
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public void getBitmap(Context context, String srcPath) {
		BitmapUtils mt = new BitmapUtils(context);
	}

	/**
	 * 压缩图片compress 压缩图片、添加水印并储存到指定目录
	 */

	public static void compressBitmap(final Context context, final Bitmap mBitmap, final String savePath) {
		new Thread() {

			@Override
			public void run() {
				super.run();
				// Bitmap tempBitmap=null;
				// try {
				// tempBitmap=read(context,originalUri);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				compressBitmapss(context, mBitmap, savePath);
				// smallBitmap(context,mBitmap,savePath);
				EventBus.getDefault().post("compressSuccess");
			}
		}.start();
	}

	// private static Handler handlerqq=new Handler(){
	//
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	//
	// }
	//
	// };

	/**
	 * 测试通过URI获取bitmap 读取数据
	 * 
	 * @param filename
	 *            文件名称
	 * @return
	 */
	public static Bitmap read(final Context context, final Uri originalUri) throws Exception {
		try {
			// @SuppressWarnings("unused")
			// Bitmap myBitmap = Glide.with(context)
			// .load(originalUri)
			// .asBitmap() //必须
			// .centerCrop()
			// .diskCacheStrategy(DiskCacheStrategy.ALL)
			// .into(1024, 800)
			// .get();
			// @SuppressWarnings("unused")
			Bitmap myBitmap = BitmapFactory.decodeFile(UriUtils.getFileUrl(context, originalUri));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			return myBitmap;
			// byte[] datas = baos.toByteArray();
			// int lenghts=datas.length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 压缩图片、添加水印并储存到指定目录
	 */
	@SuppressLint("SimpleDateFormat")
	public static void smallBitmap(Context context, Bitmap bitmap, String savePath) {

		if (bitmap == null) {
			ToastUtil.showToastShort(context, "图片压缩失败！");
			return;
		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			// 将文件存储在SD卡的根目录，并以系统时间将文件命名
			File jpgFile = new File(savePath);
			// 文件输出流对象
			FileOutputStream outStream = new FileOutputStream(jpgFile);
			// 将文件转化为bitmap以便添加日期水印
			// Bitmap bitmap=BitmapFactory.decodeByteArray(data, 0,
			// data.length);
			// bitmap=compressImage(bitmap);//压缩图片到100k
			Log.i("JsonHttpUtils", "开始压缩图片");
			// ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
			// byte[] datas1 = baos1.toByteArray();
			bitmap = compressAvatar(bitmap);

			bitmap = ImageUtil.drawTextToRightBottom(context, bitmap, AppApplication.getUSER().data.name + " " + sf.format(new Date()), 10,
					Color.parseColor("#FF0000"), 5, 5);
			Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_water_mask);
			// 为bitmap以便添加图片水印
			bitmap = ImageUtil.createWaterMaskLeftBottom(context, bitmap, watermark, 5, 5);
			// 再将bitmap转化为字节数组
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] datas = baos.toByteArray();

			// 将文件数据存储到文件中
			outStream.write(datas);
			// 关闭输出流
			outStream.close();
			Log.i("JsonHttpUtils", "压缩图片完成！");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static void compressBitmapss(Context context, Bitmap bitmap, String savePath) {
		// 将文件存储在SD卡的根目录，并以系统时间将文件命名
		File jpgFile = new File(savePath);
		// 文件输出流对象
		FileOutputStream outStream;

		try {
			int bitmapwidth = bitmap.getWidth();
			int bitmapheight = bitmap.getHeight();
			if (bitmapwidth > bitmapheight) {

			}
			outStream = new FileOutputStream(jpgFile);
			// 再将bitmap转化为字节数组
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 压缩质量为50
			byte[] datas = baos.toByteArray();

			Bitmap bitmapText = ImageUtil.drawTextToRightBottom(context, BitmapFactory.decodeByteArray(datas, 0, datas.length),
					AppApplication.getUSER().data.name + " " + sf.format(new Date()), 10, Color.parseColor("#FF0000"), 5, 5);
			// 将文件转化为bitmap以便添加日期水印
			Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_water_mask);
			// 为bitmap以便添加图片水印
			bitmapText = ImageUtil.createWaterMaskLeftBottom(context, bitmapText, watermark, 5, 5);

			ByteArrayOutputStream baoss = new ByteArrayOutputStream();
			bitmapText.compress(Bitmap.CompressFormat.JPEG, 50, baoss);// 意图是将Bitmap转化为字节数组
			byte[] datass = baoss.toByteArray();

			// 将文件数据存储到文件中
			outStream.write(datass);
			// 关闭输出流
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片压缩：先是大小比例压缩再进行质量压缩，这样压缩时间相对较短
	 * 
	 **/

	// public static Bitmap File2BitmapUpload(String srcPath) {
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
	// newOpts.inJustDecodeBounds = false;
	// int w = newOpts.outWidth;
	// int h = newOpts.outHeight;
	// float hh = 800f;// 这里设置高度为800f
	// float ww = 480f;// 这里设置宽度为480f
	// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	// int be = 1;// be=1表示不缩放
	// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
	// be = (int) (newOpts.outWidth / ww);
	// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
	// be = (int) (newOpts.outHeight / hh);
	// }
	// if (be <= 0)
	// be = 1;
	// newOpts.inSampleSize = be;// 设置缩放比例
	// // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	// bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
	// return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	// // return bitmap;
	// }

	/**
	 * 按比例压缩图片
	 * 
	 * @param picture
	 * @return
	 */

	public static Bitmap compressAvatar(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		BitmapFactory.Options opts = new BitmapFactory.Options();
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		float standardW = 768f;
		float standardH = 1024f;

		int zoomRatio = 1;
		if (w > h && w > standardW) {
			zoomRatio = (int) (w / standardW);
		} else if (w < h && h > standardH) {
			zoomRatio = (int) (h / standardH);
		}
		if (zoomRatio <= 0)
			zoomRatio = 1;
		opts.inSampleSize = zoomRatio;

		bitmap.compress(Bitmap.CompressFormat.JPEG, opts.inSampleSize, baos);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmapTemp = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片

		return bitmapTemp;
	}

	/**
	 * 按比例压缩图片
	 * 
	 * @param picture
	 * @return
	 */

	@SuppressLint("NewApi")
	public static Bitmap compressBitmap(Context context, Bitmap bitmap, Intent data) {
		// 从选取相册的Activity中返回后
		Uri imageUri = data.getData();
		String[] filePathColumns = { MediaStore.Images.Media.DATA };
		Cursor c = context.getContentResolver().query(imageUri, filePathColumns, null, null, null);
		c.moveToFirst();
		int columnIndex = c.getColumnIndex(filePathColumns[0]);
		String imagePath = c.getString(columnIndex);
		c.close();
		// 设置参数
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
		BitmapFactory.decodeFile(imagePath, options);
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 2; // 默认像素压缩比例，压缩为原图的1/2
		int minLen = Math.min(height, width); // 原图的最小边长
		if (minLen > 100) { // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
			float ratio = (float) minLen / 100.0f; // 计算像素压缩比例
			inSampleSize = (int) ratio;
		}
		options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
		options.inSampleSize = inSampleSize; // 设置为刚才计算的压缩比例
		Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
		Log.w("TAG", "size: " + bm.getByteCount() + " width: " + bm.getWidth() + " heigth:" + bm.getHeight()); // 输出图像数据

		return bm;
	}

	/**
	 * 按比例压缩图片,并保存至指定路径中，如果是相机拍照，指定路径则是原照片路径，压缩后覆盖原图片。
	 * 
	 * @param PicturePath
	 * @param picture
	 * @return
	 */

	@SuppressLint("NewApi")
	public static Bitmap compressBmp(Context context, Intent data, String PicturePath) {
		String imagePath;
		File jpgFile = new File(PicturePath);
		if (jpgFile.exists() && jpgFile.length() > 0) {// 相机拍摄的照片已经存在于指定路径中，直接取路径
			imagePath = PicturePath;
		} else { // 相册选择的照片需要通过data获取文件路径
			// 从选取相册的Activity中返回后
//			Uri originalUri = data.getData(); // 获得图片的uri
			// imagePath = UriUtils.getFileUrl(context, originalUri);
			imagePath = data.getDataString();
			String tempString = Uri.decode(imagePath);
			imagePath = tempString.substring(7, tempString.length());
		}
		// 设置参数
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
		BitmapFactory.decodeFile(imagePath, options);
		int height = options.outHeight;
		int width = options.outWidth;

		float standardW = 768f;
		float standardH = 1024f;

		int zoomRatio = 1;
		if (height > width && height > standardW) {
			zoomRatio = (int) (height / standardW);
		} else if (height < width && width > standardH) {
			zoomRatio = (int) (width / standardH);
		}
		if (zoomRatio <= 0)
			zoomRatio = 1;
		options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
		options.inSampleSize = zoomRatio; // 设置为刚才计算的压缩比例
		Bitmap bm = BitmapFactory.decodeFile(imagePath, options); // 解码文件
		try {
			FileOutputStream FOstream = new FileOutputStream(jpgFile);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String address;
			if (null!=AppApplication.LOCATION && !TextUtils.isEmpty(AppApplication.LOCATION.getAddrStr())) {
				address ="\n拍摄地址："+AppApplication.LOCATION.getAddrStr();
			}else {
				address = "无定位信息！";
			}
			bm = ImageUtil.drawTextByShadowToRightBottom(context, bm, AppApplication.getUSER().data.name + " " + getPhotoData(imagePath)
					+address, 5,Color.parseColor("#8a8a8a"), 5, 5); //先加一层灰色的底，避免变色图片加上白色文本看不见
			bm = ImageUtil.drawTextToRightBottom(context, bm, AppApplication.getUSER().data.name + " " + getPhotoData(imagePath)
					+address, 5,Color.parseColor("#FFFFFF"), 5, 5); //加上白色的公估师、时间和地址信息。
			Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_water_mask_alph);
			// 为bitmap以便添加图片水印
			bm = ImageUtil.createWaterMaskLeftTop(context, bm, watermark, 5, 5);

			int compressParm = 80;
			if (zoomRatio == 1) {// 上一步没有对图片进行压缩，说明图片已经满足要求，这一步就不做压缩了；
				compressParm = 100;
			}

			bm.compress(Bitmap.CompressFormat.JPEG, compressParm, baos);// 意图是将Bitmap存储空间缩小50（占用内存不会缩小）再转化为字节数组
			byte[] bs = baos.toByteArray();
			FOstream.write(bs);
			FOstream.close();
			baos.close();
			EventBus.getDefault().post("compressSuccess");// 发送订阅刷新主界面
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bm;
	}

	/** 获取照片拍照时间 **/
	@SuppressLint("SimpleDateFormat")
	public static String getPhotoData(String photoPath) {
		String dateTime = "-无时间信息-";
		try {
			Long fileTime = new File(photoPath).lastModified();
			Date date = new Date(fileTime);
			SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			dateTime = simpleDateFormat1.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTime;
	}
	
	/**
	 * 调用系统裁剪功能
	 * @param uri
	 */
	private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        System.out.println("22================");
//        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

}