package com.cninsure.cp.cargo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.cargo.CargoCaseBaoanTable;
import com.cninsure.cp.entity.cargo.CargoCaseWorkImagesTable;
import com.cninsure.cp.entity.cx.DictData;
import com.cninsure.cp.photo.CargoCamreaActivity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.ImageDisplayUtil;
import com.cninsure.cp.utils.ImageUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.UriUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CargoExpandablelistAdapter extends BaseExpandableListAdapter {

	private Activity context;
	/**大标题分类*/
	private List<DictData> parentPtoTypeDict;
	/**拍照分类小类*/
	private Map<String,List<DictData>> childPtoTypeDicts;
	/**分类后的照片*/
	private Map<String, List<CargoCaseWorkImagesTable>> classImgMap;
	private LayoutInflater inflater;
	/**GridView适配器集合**/
	private Map<String , CargoGalleryAdapter> gridAdapters;
//	private Map<Long , List<CargoCaseWorkImagesTable>> classImgMap;
//	private String baoanUid;
	private CargoCaseBaoanTable caseBaoanTable;


	private CargoExpandablelistAdapter(){}
	public CargoExpandablelistAdapter(Activity context, List<DictData> parentPtoTypeDict, Map<String, List<DictData>> childPtoTypeDicts,
									  Map<String, List<CargoCaseWorkImagesTable>> classImgMap, CargoCaseBaoanTable caseBaoanTable){
		this.context = context;
		this.parentPtoTypeDict = parentPtoTypeDict;
		this.childPtoTypeDicts = childPtoTypeDicts;
		this.classImgMap = classImgMap;
		inflater = LayoutInflater.from(context);
		gridAdapters=new HashMap<>();
		this.caseBaoanTable = caseBaoanTable;
	}

		
		@SuppressLint("SimpleDateFormat")
		private SimpleDateFormat sFormat=new SimpleDateFormat("yyyy-MM-dd");
		
		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public Object getChild(int arg0, int arg1) {
			return childPtoTypeDicts.get(parentPtoTypeDict.get(arg0).value).get(arg1) ;
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(int arg0, int arg1, boolean arg2, View contentview, ViewGroup arg4) {

			//初始化控件
			contentview = inflater.inflate(R.layout.cargo_gridview_layout, null);
			TextView title = (TextView) contentview.findViewById(R.id.cargo_gridView_wstitle);
			title.setTextColor(context.getResources().getColor(R.color.hui_text_xxxh));
			title.setText(childPtoTypeDicts.get(parentPtoTypeDict.get(arg0).value).get(arg1).label+"("
					+getChildCount(childPtoTypeDicts.get(parentPtoTypeDict.get(arg0).value).get(arg1).value)+")"); //小类标题

			GridView gridView=contentview.findViewById(R.id.cargo_gridView_gridView1);
			CargoGalleryAdapter gAdapter=new CargoGalleryAdapter(arg0,arg1);
			gridAdapters.put(arg0+"_"+arg1, gAdapter);
			gridView.setAdapter(gAdapter);
			
			setgalleryOnclick(gridView,arg0,arg1);
			return contentview;
		}

		/**获取子类已拍照数量*/
		private int getChildCount(String type){
			List<CargoCaseWorkImagesTable> tempImgList = classImgMap.get(type);
			if (tempImgList!=null) return tempImgList.size();
			else return 0;
		}

		private void setgalleryOnclick(final GridView gridView, final int groupID, final int childID) {
			gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					String largeUrlList=null;
					List<CargoCaseWorkImagesTable> imgList = classImgMap.get(childPtoTypeDicts.get(parentPtoTypeDict.get(groupID).value).get(childID).value);
					if (imgList!=null && imgList.size()!=arg2) {
						largeUrlList = classImgMap.get(childPtoTypeDicts.get(parentPtoTypeDict.get(groupID).value).get(childID).value).get(arg2).fileUrl;
					}
					//作业完成或者审核通过后不能再上传图片，而是直接浏览图片（作业完成但是该张图片需要整改的可以拍摄和上传）
					if (largeUrlList!=null && largeUrlList.indexOf("/")==-1) { //未上传图片或者未拍摄
						largeUrlList = AppApplication.getUSER().data.qiniuUrl+largeUrlList;
						ImageDisplayUtil.displayByMyView(context, largeUrlList);
					}else {
						getimg(childPtoTypeDicts.get(parentPtoTypeDict.get(groupID).value).get(childID).value,arg2,gridView);
					}
				}
			});
		}

		@Override
		public int getChildrenCount(int arg0) {
			return childPtoTypeDicts.get(parentPtoTypeDict.get(arg0).value).size();
		}

		@Override
		public long getCombinedChildId(long arg0, long arg1) {
			return 0;
		}

		@Override
		public long getCombinedGroupId(long arg0) {
			return 0;
		}

		@Override
		public Object getGroup(int arg0) {
			return parentPtoTypeDict.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return parentPtoTypeDict.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int groupPostion, boolean arg1, View contentview, ViewGroup arg3) {
			TextView titleTv;
			ImageView imgtitle;
			contentview = inflater.inflate(R.layout.item_exlist_title_tv, null);
			titleTv =  contentview.findViewById(R.id.expandablelist_item_title);
			titleTv.setTextColor(context.getResources().getColor(R.color.bulue_main));
			imgtitle =  contentview.findViewById(R.id.expandablelist_item_title_imgl);
			imgtitle.setImageResource(R.drawable.leaving_hui36);
			titleTv.setText(parentPtoTypeDict.get(groupPostion).label+"("+
					getGroupCountt(parentPtoTypeDict.get(groupPostion).value)+")");
			contentview.setBackgroundResource(android.R.color.white);
			return contentview;
		}

		private int getGroupCountt(String value){
			int gCount = 0;
			for (DictData tempDict:childPtoTypeDicts.get(value)){
				gCount += classImgMap.get(tempDict.value).size();
			}
			return gCount;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public void onGroupCollapsed(int arg0) {
		}

		@Override
		public void onGroupExpanded(int arg0) {
		}

		@Override
		public void registerDataSetObserver(DataSetObserver arg0) {
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver arg0) {
		}


	public class CargoGalleryAdapter extends BaseAdapter {

		int groupPostion,childPostion;
		public CargoGalleryAdapter instans;

		public CargoGalleryAdapter(int G,int C){
			groupPostion=G;
			childPostion=C;
			instans=this;
		}

		@Override
		public int getCount() {
			List<CargoCaseWorkImagesTable> imgList = classImgMap.get(childPtoTypeDicts.get(parentPtoTypeDict.get(groupPostion).value).get(childPostion).value);
			if (caseBaoanTable.status==5 || caseBaoanTable.status ==10){
				if (imgList==null) return 1;
				else return imgList.size()+1;
			}else{
				if (imgList==null) return 0;
				else return imgList.size();
			}

		}

		@Override
		public Object getItem(int arg0) {
			return childPtoTypeDicts.get(parentPtoTypeDict.get(groupPostion).value);
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
			List<CargoCaseWorkImagesTable> imgList = classImgMap.get(childPtoTypeDicts.get(parentPtoTypeDict.get(groupPostion).value).get(childPostion).value);
			if (imgList==null || imgList.size()==arg0) {
//
			}else {
				String tempPath=classImgMap.get(childPtoTypeDicts.get(parentPtoTypeDict.get(groupPostion).value).get(childPostion).value).get(arg0).fileUrl;
				if (!TextUtils.isEmpty(tempPath)) {
					if (tempPath.indexOf("/")==-1) tempPath = AppApplication.getUSER().data.qiniuUrl+tempPath;
					if (tempPath.indexOf("://")>-1) {
						Glide.with(context).load(tempPath+"?imageView2/2/w/200").placeholder(R.drawable.loadingwait_hui)
								.error(R.drawable.loadingerror_).into(img);
					}else {
						Glide.with(context).load(tempPath).placeholder(R.drawable.loadingwait_hui).error(R.drawable.loadingerror_)
								.centerCrop().into(img);
						deleteimg.setVisibility(View.VISIBLE);
						deleteimg.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								deleteAndRemovePhoto(childPtoTypeDicts.get(parentPtoTypeDict.get(groupPostion).value).get(childPostion).value,arg0);
								instans.notifyDataSetChanged();
							}
						});
					}
				}else {
					Glide.with(context).load(R.drawable.image123).placeholder(R.drawable.loadingwait_hui).centerCrop().into(img);
//					Glide.with(context).load(imgIds[arg0]).centerCrop().into(img);
				}
			}
			contentview.setVisibility(View.VISIBLE);
			return contentview;
		}
	}

	/***图片选择与显示*===============================================================================================**/
	private int I;
	private String TId;
	private String currPicturePath;
	private GridView GV;
	private Dialog dialog;
	private void getimg(String typeId,int i,GridView gridView) {
		I=i;
		GV=gridView;
		TId = typeId;
		View view = LayoutInflater.from(context).inflate(R.layout.photograph_view, null);
		View camera = (View) view.findViewById(R.id.photography_camera);
		View photos = (View) view.findViewById(R.id.photography_photos);
		View display = (View) view.findViewById(R.id.photography_display);
		View delete = (View) view.findViewById(R.id.photography_deleteplay);
		if (classImgMap.get(TId).size()>I && !TextUtils.isEmpty(classImgMap.get(TId).get(I).fileUrl)) {
			display.setVisibility(View.VISIBLE);
			if (classImgMap.get(TId).get(I).fileUrl.indexOf("://")==-1) {//未上传图片显示删除按钮，已上传的图片不能删除
				delete.setVisibility(View.VISIBLE);
			}else {//已上传图片直接显示
				displayPictrue();
				return;
			}
		}
		dialog = DialogUtil.getDialogByView(context, view);
		dialog.show();
		setOnClicks(camera);
		setOnClicks(photos);
		setOnClicks(display);
		setOnClicks(delete);
	}

	/**显示网络图片*/
	private void displayPictrue(){
		LoadDialogUtil.setMessageAndShow(context,"加载中……");
		String largeUrlList = classImgMap.get(TId).get(I).fileUrl;
		largeUrlList = AppApplication.getUSER().data.qiniuUrl+largeUrlList;
		ImageDisplayUtil.displayByMyView(context, largeUrlList);
	}

	private void setOnClicks(final View butt) {

		butt.setOnClickListener(new View.OnClickListener() {
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
						String largeUrlList = classImgMap.get(TId).get(I).fileUrl;
						if (largeUrlList != null) {
							if (largeUrlList.indexOf("/")!=-1) {
								ImageDisplayUtil.displayByMyView(context, largeUrlList);
							}else {
								displayPictrue(); //显示网络图片
							}
						}
						break;
					case R.id.photography_deleteplay://删除选择
						deleteAndRemovePhoto(TId,I);
						break;
					default:
						break;
				}
			}
		});
	}

	private void deleteAndRemovePhoto(String typeId,int It){
		String filePath = classImgMap.get(typeId).get(It).fileUrl;
		File file=new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		classImgMap.get(typeId).remove(It);
		adapterNotify();
	}

	private void cameraphoto() {
//		// path为保存图片的路径，执行完拍照以后能保存到指定的路径下
		Intent cameraIntent = new Intent(context, CargoCamreaActivity.class);
		cameraIntent.putExtra("typeId", TId);
		cameraIntent.putExtra("baoanUid", caseBaoanTable.caseNo);
		context.startActivity(cameraIntent);
	}

	private void checkPhotos() {
		Intent getAlbum=new Intent(context, CargoPhotoChoiceActivity.class);
		getAlbum.putExtra("typeId", TId);
		getAlbum.putExtra("baoanUid", caseBaoanTable.caseNo);
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


	public void getCameraPhoto() {
		adapterNotify();
	}

	public void adapterNotify(){
		if (this!=null) {
			this.notifyDataSetChanged();
		}
		if (GV!=null) {
			GV.deferNotifyDataSetChanged();
//			GV.setAdapter(new MyParkGalleryAdapter(G,C));
		}
	}

	/**相册选择图片返回Intent ，压缩，打水印后显示**/
//	private void getphotos(Intent data) {
//		try {
//			Uri originalUri = data.getData(); // 获得图片的uri
//
//			currPicturePath= PhotoPathUtil.getPictureCreatePath(context.getIntent().getStringExtra("orderUid"));
//
//			@SuppressWarnings("unused")
//			Bitmap mBitmap= BitmapFactory.decodeFile(UriUtils.getFileUrl(context, originalUri));
//
//			ImageUtil.compressBitmap(context, mBitmap, currPicturePath);
//
//			WorkPhotos.TableData.WorkPhotoEntitiy photoEntitiy=new WorkPhotos.TableData.WorkPhotoEntitiy();
//			photoEntitiy.location=currPicturePath;
//			resousePathList.get(G).get(C).add(photoEntitiy) ;
//			expanAdapter.notifyDataSetChanged();
//			GV.setAdapter(new MyParkGalleryAdapter(G,C));
//		} catch (Exception e) {
//			Log.e("getphotos", e.toString());
//		}
//	}

//	public void upload() {
////		resousePathList;photoType
//		final List<NameValuePair> params=new ArrayList<NameValuePair>();
//		for (int i = 0; i < photoType.tableData.data.size(); i++) {
//			List<List<WorkPhotos.TableData.WorkPhotoEntitiy>> list=resousePathList.get(i);
//			for (int k = 0; k < list.size(); k++) {
//				for (int j = 0; j < list.get(k).size(); j++) {
//					if ( list.get(k).get(j)!=null) {
//						String mathstr=list.get(k).get(j).location;
//						if (mathstr.indexOf("://")==-1) {
//							params.add(new BasicNameValuePair( photoType.tableData.data.get(i).id+"", mathstr));
//						}
//					}
//				}
//			}
//		}
//		Log.e("JsonHttpUtils", "10000"+JSON.toJSONString(params));
//
//		if (params.size()!=0) {
//			final List<NameValuePair> httpParams=new ArrayList<NameValuePair>();
//			httpParams.add(new BasicNameValuePair("workId", context.getIntent().getStringExtra("orderUid")));
//			if (AppApplication.sp.getBoolean("isWifiUp", false) && !context.isWifiConnected()) {//用户设置wifi条件下才能上传时提示用户
//				DialogUtil.getAlertOnelistener(context, "非Wifi条件下是否强制上传图片？",new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//						PhotoUploadUtil.upload(context, params, URLs.UploadWorkPhoto(), httpParams);
//					}
//				}).show();
//				return;
//			}
//			PhotoUploadUtil.upload(context, params, URLs.UploadWorkPhoto(), httpParams);
//		}else {
//			DialogUtil.getErrDialog(context, "未拍照").show();
//		}
//	}


		
	}