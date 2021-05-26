package com.cninsure.cp.cx.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.dispersive.DispersiveWorkActivity;
import com.cninsure.cp.entity.cx.CxImagEntity;
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


public class CxGalleryAdapter extends BaseAdapter{

	/**图片集合*/
	public List<CxImagEntity>  imgResources;//,BasicImgList;  //图片实体合集
	private CxGalleryAdapter instans;
	private Activity context;  //上下文
	private LayoutInflater inflater ;  //资源解析器
	private Dialog dialog;  //图片操作弹框
	private String imageType;  //所在的ImageType
	private int groupPosition;
	private SaveImgCallBack addScb; //通过该接口的回调将数据写入到activity的图片实体类中，并刷新adapter
	private String orderUid; //订单编号，用来生成本地文件夹存拍照和选择的照片。

	private CxGalleryAdapter(){}

	public CxGalleryAdapter(Activity context, List<CxImagEntity> imgResources , String imageType, int groupPosition, SaveImgCallBack addScb,String orderUid) {
		this.imgResources = imgResources;
		instans = this;
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.imageType = imageType;
		this.addScb = addScb;
		this.groupPosition = groupPosition;
		this.orderUid = orderUid;
	}

		@Override
		public int getCount() {
			return imgResources.size()+1;
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
			ImageView img= contentview.findViewById(R.id.item_gridviewForExlist_photoup_img);
			ImageView deleteimg= contentview.findViewById(R.id.item_gridviewForExlist_delete_img);
			Glide.with(context).load(R.drawable.image123).into(img);
			if (imgResources.size()==arg0) {
//
			}else {
				String tempPath;
				tempPath=imgResources.get(arg0).getImageUrl();
				if (!TextUtils.isEmpty(tempPath)) {
					if (imgResources.get(arg0).id!=null) {
						Glide.with(context).load(tempPath).placeholder(R.drawable.loadingwait_hui)
						.error(R.drawable.loadingerror_).into(img);
					}else {
						Glide.with(context).load(tempPath).placeholder(R.drawable.loadingwait_hui).error(R.drawable.loadingerror_)
						.centerCrop().into(img);
						deleteimg.setVisibility(View.VISIBLE);
						deleteimg.setOnClickListener(v -> {
							deleteAndRemovePhoto(arg0);
							instans.notifyDataSetChanged();
						});
					}
				}else {
					Glide.with(context).load(R.drawable.image123).placeholder(R.drawable.loadingwait_hui).centerCrop().into(img);
				}
			}
			setgalleryOnclick(img,arg0);
			contentview.setVisibility(View.VISIBLE);
			return contentview;
		}

	private void setgalleryOnclick(View img, final int postion) {
		img.setOnClickListener(v -> getimg(postion));
	}

	/***图片选择与显示*===============================================================================================**/
	private int I;
	private void getimg(int postion) {
		I = postion;
		View view = LayoutInflater.from(context).inflate(R.layout.photograph_view, null);
		View camera = view.findViewById(R.id.photography_camera);
		View photos =  view.findViewById(R.id.photography_photos);
		View display = view.findViewById(R.id.photography_display);
//		View delete = (View) view.findViewById(R.id.photography_deleteplay);
		if (imgResources!=null && I<imgResources.size() && !TextUtils.isEmpty(imgResources.get(I).getImageUrl())) {
			display.setVisibility(View.VISIBLE);
			if (imgResources.get(I).id==null) {//删除选择未上传的，而不是已上传的，已上传的图片不能删除
//				delete.setVisibility(View.VISIBLE);
			}else {//已上传图片直接显示
				ImageDisplayUtil.displayByMyView(context, imgResources.get(I).getImageUrl());
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
		String largeUrlList=imgResources.get(I).getImageUrl();
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

		butt.setOnClickListener(arg0 -> {
			dialog.dismiss();
			switch (butt.getId()) {
			case R.id.photography_camera://拍照
				cameraphoto();
				break;
			case R.id.photography_photos://相册选择
				checkPhotos();
				break;
			case R.id.photography_display://浏览图片
				displayImag();
				break;
			case R.id.photography_deleteplay://删除选择
				deleteAndRemovePhoto(I);
				break;
			default:
				break;
			}
		});
	}

	/**
	 * 显示图片
	 */
	private void displayImag(){
		String largeUrlList = imgResources.get(I).getImageUrl();
		if (largeUrlList != null) {
			ImageDisplayUtil.displayByMyView(context, largeUrlList);
		}
	}

	/**
	 * 移除操作，如果是标的受损基本情况，除了移除imgResources内容，还需要移除MDImag，其他只移除imgResources内容。
	 * @param It
	 */
	private void deleteAndRemovePhoto(int It){
		CxImagEntity imgData=imgResources.get(It);
		addScb.deleteImg(imgData,groupPosition);
		File file=new File(imgData.getImageUrl());
		if (file.exists()) {file.delete();}
		imgResources.remove(imgData);
	}

	private void cameraphoto() {
//		// path为保存图片的路径，执行完拍照以后能保存到指定的路径下
		Intent cameraIntent = new Intent(context, CXPhotoActivity.class);
		cameraIntent.putExtra("photoType", imageType);
		cameraIntent.putExtra("ActivityName", "NEW_CX");
		cameraIntent.putExtra("GroupId", groupPosition);
		cameraIntent.putExtra("orderUid", orderUid);
		context.startActivity(cameraIntent);
	}

	private void checkPhotos() {
		Intent getAlbum=new Intent(context, PhotoChoiceActivity.class);
		getAlbum.putExtra("photoType", imageType);
		getAlbum.putExtra("ActivityName", "NEW_CX");
		getAlbum.putExtra("orderUid", orderUid);
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
}
