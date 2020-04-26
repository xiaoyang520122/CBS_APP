package com.cninsure.cp.dispersive;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.dispersive.DisWorkImageEntity;
import com.cninsure.cp.photo.CXPhotoActivity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.ImageDisplayUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoChoiceActivity;
import com.cninsure.cp.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DispersiveGalleryAdapter extends BaseAdapter{

	/**图片集合*/
	public List<DisWorkImageEntity.DisWorkImgData> resousePathList;  //图片实体合集
	private DispersiveGalleryAdapter instans;
	private DispersiveWorkActivity context;  //上下文
	private LayoutInflater inflater ;  //资源解析器
	private Dialog dialog;  //图片操作弹框
	private int gImageType;  //所在的ImageType

	private DispersiveGalleryAdapter(){}

	public DispersiveGalleryAdapter(DispersiveWorkActivity context,List<DisWorkImageEntity.DisWorkImgData> resousePathList,int imageType){
			this.resousePathList = resousePathList;
			instans=this;
			inflater = LayoutInflater.from(context);
			this.context  = context;
			gImageType  = imageType;
		}

		@Override
		public int getCount() {
			if (DispersiveWorkActivity.instence.caseDisTemp!=null &&
					(DispersiveWorkActivity.instence.caseDisTemp.status==11 || DispersiveWorkActivity.instence.caseDisTemp.status==5)) { //驳回和已到达现场的才能提交审核
				return resousePathList.size()+1;
			} else{
				return resousePathList.size();
			}

		}

		@Override
		public Object getItem(int arg0) {
			return 1;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int arg0, View contentview, ViewGroup arg2) {
			contentview=inflater.inflate(R.layout.item_gridview_for_exlistview_photoup, null);
			ImageView img=(ImageView) contentview.findViewById(R.id.item_gridviewForExlist_photoup_img);
			ImageView deleteimg=(ImageView) contentview.findViewById(R.id.item_gridviewForExlist_delete_img);
			Glide.with(context).load(R.drawable.image123).into(img);
			if (resousePathList.size()==arg0) {
//
			}else {
				String tempPath=resousePathList.get(arg0).getImageUrl();
				if (!TextUtils.isEmpty(tempPath)) {
					if (tempPath.indexOf("://")>-1) {
//						Glide.with(context).load(tempPath+"?imageView2/2/w/200").placeholder(R.drawable.loadingwait_hui)
						Glide.with(context).load(tempPath).placeholder(R.drawable.loadingwait_hui)
						.error(R.drawable.loadingerror_).into(img);
					}else {
						Glide.with(context).load(tempPath).placeholder(R.drawable.loadingwait_hui).error(R.drawable.loadingerror_)
						.centerCrop().into(img);
						deleteimg.setVisibility(View.VISIBLE);
						deleteimg.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								deleteAndRemovePhoto(arg0);
								instans.notifyDataSetChanged();
								context.changeCountNum(); //刷新照片小类对应数量信息
							}
						});
					}
				}else {
					Glide.with(context).load(R.drawable.image123).placeholder(R.drawable.loadingwait_hui).centerCrop().into(img);
//					Glide.with(context).load(imgIds[arg0]).centerCrop().into(img);
				}
			}
			setgalleryOnclick(img,arg0);
			contentview.setVisibility(View.VISIBLE);
			return contentview;
		}

	private void setgalleryOnclick(View img, final int postion) {
		img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getimg(postion);
			}
		});
	}

	/***图片选择与显示*===============================================================================================**/
	private int I;
	private void getimg(int postion) {
		I = postion;
		View view = LayoutInflater.from(context).inflate(R.layout.photograph_view, null);
		View camera = (View) view.findViewById(R.id.photography_camera);
		View photos = (View) view.findViewById(R.id.photography_photos);
		View display = (View) view.findViewById(R.id.photography_display);
//		View delete = (View) view.findViewById(R.id.photography_deleteplay);
		if (resousePathList!=null && I<resousePathList.size() && !TextUtils.isEmpty(resousePathList.get(I).getImageUrl())) {
			display.setVisibility(View.VISIBLE);
			if (resousePathList.get(I).getImageUrl().indexOf("://")==-1) {//删除选择未上传的，而不是已上传的，已上传的图片不能删除
//				delete.setVisibility(View.VISIBLE);
			}else {//已上传图片直接显示
//				displayPictrue();
				ImageDisplayUtil.displayByMyView(context, resousePathList.get(I).getImageUrl());
				return;
			}
		}
		dialog = DialogUtil.getDialogByView(context, view);
		dialog.show();
		setOnClicks(camera);
		setOnClicks(photos);
		setOnClicks(display);
//		setOnClicks(delete);
	}

	/**显示网络图片*/
	private void displayPictrue(){
		LoadDialogUtil.setMessageAndShow(context,"加载中……");
		String largeUrlList=resousePathList.get(I).getImageUrl();
		if (largeUrlList != null) {
			final Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			Glide.with(context).load(largeUrlList).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                	//将bitmap转换为uri
					Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), resource, null, null));
					intent.setDataAndType(uri, "image/*");
					//设置intent数据和图片格式
					context.startActivity(intent);//显示图片
					LoadDialogUtil.dismissDialog();
                }
            }); //方法中设置asBitmap可以设置回调类型
		}
	}

	private void setOnClicks(final View butt) {

		butt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				dialog.dismiss();

				switch (butt.getId()) {
				case R.id.photography_camera://拍照
					cameraphoto();
					break;
				case R.id.photography_photos://相册选择
					checkPhotos();
					break;
				case R.id.photography_display://浏览图片
					String largeUrlList=resousePathList.get(I).getImageUrl();
					if (largeUrlList != null) {
						ImageDisplayUtil.displayByMyView(context, largeUrlList);
					}
					break;
				case R.id.photography_deleteplay://删除选择
					deleteAndRemovePhoto(I);
					break;
				default:
					break;
				}
			}
		});
	}

	private void deleteAndRemovePhoto(int It){
		String filePath=resousePathList.get(It).getImageUrl();
		File file=new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		resousePathList.remove(It);
//		adapterNotify();
	}

	private void cameraphoto() {
//		// path为保存图片的路径，执行完拍照以后能保存到指定的路径下
		Intent cameraIntent = new Intent(context, CXPhotoActivity.class);
		cameraIntent.putExtra("photoType", gImageType);
		cameraIntent.putExtra("ActivityName", "DispersiveWorkActivity");
//		cameraIntent.putExtra("orderUid", context.getStringExtra("orderUid"));
		context.startActivity(cameraIntent);
	}

	private void checkPhotos() {
//		Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//		getAlbum.setType(IMAGE_TYPE);
//		context.startActivityForResult(getAlbum, IMAGE_CODE);
		Intent getAlbum=new Intent(context, PhotoChoiceActivity.class);
		getAlbum.putExtra("photoType", gImageType);
		getAlbum.putExtra("ActivityName", "DispersiveWorkActivity");
//		getAlbum.putExtra("GroupId", G);
//		getAlbum.putExtra("orderUid", context.getIntent().getStringExtra("orderUid"));
		context.startActivity(getAlbum);
	}

	protected String getPictureCreatePath() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			ToastUtil.showToastLong(context, "SD卡不可用");
			return null;
		}
		@SuppressWarnings("static-access")
		String name = new DateFormat().format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";

		String fileDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera";

		File fileDir = new File(fileDirPath);
		if (!fileDir.exists() || !fileDir.isDirectory()) {
			fileDir.mkdirs();
		}
		String fileName = fileDirPath + File.separator + name;
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		return file.getAbsolutePath();
	}

//	public void eventresultcode(int requestCode, int resultCode, Intent data){
//
//		switch (requestCode) {
//		case IMAGE_CODE:
////			ToastUtil.showToastShort(context, "返回编号："+requestCode);
//			getphotos(data);//相册选择
//			break;
//		case CAMERA_IMAGE_CODE:
//			getCameraPhoto(data);//拍照返回
//			break;
//
//		default:
//			break;
//		}
//	}

//	private void getCameraPhoto(Intent data) {
//		File file = new File(currPicturePath);
//		Uri imageUri = Uri.fromFile(file);
//
//
//		Bitmap mBitmap= BitmapFactory.decodeFile(UriUtils.getFileUrl(context, imageUri));
//		ImageUtil.compressBitmap(context, mBitmap, currPicturePath);
//
//		WorkPhotoEntitiy photoEntitiy=new WorkPhotoEntitiy();
//		photoEntitiy.location=currPicturePath;
//		resousePathList.add(photoEntitiy) ;
//		adapterNotify();
//	}
//
//	public void adapterNotify(){
//		if (expanAdapter!=null) {
//			expanAdapter.notifyDataSetChanged();
//		}
//		if (GV!=null) {
//			GV.setAdapter(new MyParkGalleryAdapter(G,C));
//		}
//		if (adapters!=null) {
//			String adapterArr[]=adapters.keySet().toArray(new String[0]);
//			for (int i = 0; i < adapterArr.length; i++) {
//				adapters.get(adapterArr[i]).notifyDataSetChanged();
//			}
//		}
//	}
//
///**相册选择图片返回Intent ，压缩，打水印后显示**/
//	private void getphotos(Intent data) {
//		try {
//			Uri originalUri = data.getData(); // 获得图片的uri
//
//			currPicturePath=PhotoPathUtil.getPictureCreatePath(context.getIntent().getStringExtra("orderUid"));
//
//			 @SuppressWarnings("unused")
//			Bitmap mBitmap= BitmapFactory.decodeFile(UriUtils.getFileUrl(context, originalUri));
//
//			ImageUtil.compressBitmap(context, mBitmap, currPicturePath);
//
//			WorkPhotoEntitiy photoEntitiy=new WorkPhotoEntitiy();
//			photoEntitiy.location=currPicturePath;
//			resousePathList.get(G).get(C).add(photoEntitiy) ;
//			expanAdapter.notifyDataSetChanged();
//			GV.setAdapter(new MyParkGalleryAdapter(G,C));
//		} catch (Exception e) {
//			Log.e("getphotos", e.toString());
//		}
//	}
//
//	public String dataToString(){
//		return JSON.toJSONString(resousePathList);
//	}
}
