package com.cninsure.cp.cx;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.WorkPhotos;
import com.cninsure.cp.entity.WorkPhotos.TableData.WorkPhotoEntitiy;
import com.cninsure.cp.entity.WorkType;
import com.cninsure.cp.entity.cx.CxDictEntity;
import com.cninsure.cp.photo.CXPhotoActivity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.ImageUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoChoiceActivity;
import com.cninsure.cp.utils.PhotoPathUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.UriUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CxWorkhelp {
	private ExpandableListView expandableListView;
	public PhotoExpandablelistAdapter expanAdapter;
	private CxWorkActivity activity;
	private View view;
	private Dialog dialog;
	private LayoutInflater inflater;
	private final String IMAGE_TYPE = "image/*";
	private final int IMAGE_CODE = 0;
	private final int CAMERA_IMAGE_CODE = 222;
//	private String[] exlistTitleArr=new String[]{"人车合影照","事故现场照","标的车定损照","财产损失照","人伤损失照","事故单证照","行驶证照","驾驶证照"};
	private int []groupimgs=new int[]{R.drawable.renchehy_hui48,R.drawable.shigud_hui48,R.drawable.biaodicds_hui48,R.drawable.caichanssz_hui48
			,R.drawable.renshang_hui48,R.drawable.shigud_hui48,R.drawable.xingshiz_hui48,R.drawable.jiashiz_hui48};

	public static List<List<List<WorkPhotoEntitiy>>> resousePathList;
	/**组是否拍齐照片的标识图标集合**/
	private List<TextView> hintTvS;
	public static List<CxDictEntity.DictData> photoType; //拍照类型字典数据
	private WorkPhotos workphotos;
	private List<ImageView> Jimags;
	/**显示照片的MyParkGalleryAdapter集合**/
	private Map<String,MyParkGalleryAdapter> adapters;
	
	@SuppressWarnings("unused")
	private CxWorkhelp(){}
	
	public CxWorkhelp(CxWorkActivity CxWorkActivity, View uploadView, List<CxDictEntity.DictData> photoType, WorkPhotos workphoto) {
		resousePathList=null;
		activity=CxWorkActivity;
		view=uploadView;
		this.photoType=photoType;
		workphotos=workphoto;
		inflater=LayoutInflater.from(activity);
		getdefaulValue();
	}


	public ExpandableListView getExpandableListView(Activity context,View v){
		initView();
		return expandableListView;
	}


	private void initView() {
		Jimags=new ArrayList<ImageView>(8);
		expandableListView=(ExpandableListView) view.findViewById(R.id.workorderactivity_expandablelistview);
		expanAdapter=new PhotoExpandablelistAdapter();
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(expanAdapter);
		
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3) {
				ImageView imageView= Jimags.get(arg2);
				int degrees=(Integer) imageView.getTag() % 360;
				RotateAnimation animation=new RotateAnimation(degrees, degrees+180,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
				animation.setFillAfter(true);
				imageView.startAnimation(animation);
				imageView.setTag(degrees+180);
				return false;
			}
		});
	}

	private void getdefaulValue() {
		hintTvS = new ArrayList<TextView>(8);
		resousePathList = new ArrayList<List<List<WorkPhotoEntitiy>>>(8);
		List<List<WorkPhotoEntitiy>> childList;
		List<WorkPhotoEntitiy> resourseMap;
		for (int i = 0; i < photoType.size(); i++) {
			childList = new ArrayList<List<WorkPhotoEntitiy>>();
			for (int k = 0; k < 1; k++) {
				resourseMap = new ArrayList<WorkPhotoEntitiy>();
				List<WorkPhotoEntitiy> dataEntitiy = workphotos.getByTypeId(photoType.get(i).id+"");
					for (int j = 0; j < dataEntitiy.size(); j++) {
						resourseMap.add(dataEntitiy.get(j));
					}
				childList.add(resourseMap);
			}
			resousePathList.add(childList);
		}
		redHistoryChiocePhoto();
	}

	/**获取之前拍摄但是未上传的照片信息并存到集合中*/
	private void redHistoryChiocePhoto(){
		String jsonString=AppApplication.sp.getString("PathList:"+activity.QorderUid, "");
		if (TextUtils.isEmpty(jsonString) || resousePathList==null) {
			return;
		}
		try {
			JSONArray jsonArray=new JSONArray(jsonString);
			for (int i = 0; i < photoType.size(); i++) {
				JSONArray childArray = jsonArray.getJSONArray(i);
				JSONArray childarr=childArray.getJSONArray(0);//默认每个大类下面只有一个小类20180507
				for (int j = 0; j < childarr.length(); j++) {
					String photoEntityString=childarr.optString(j);//获取WorkPhotoEntitiy对象的json数据
					WorkPhotoEntitiy photoEntitiy=JSON.parseObject(photoEntityString, WorkPhotoEntitiy.class);
					if (TextUtils.isEmpty(photoEntityString)) {//没有数据跳出
						continue;
					}else if (photoEntitiy.location==null || photoEntitiy.location.indexOf("://")!=-1) {//http代表是网页图片，不用添加，已经在上上一步添加，再添加就重复了
						continue;
					}else {
						resousePathList.get(i).get(0).add(photoEntitiy);
					}
				}
			}
			Log.w("TempLog", JSON.toJSONString(resousePathList));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	 class PhotoExpandablelistAdapter extends BaseExpandableListAdapter {
		 
		 public PhotoExpandablelistAdapter(){
			 adapters=new HashMap<String, CxWorkhelp.MyParkGalleryAdapter>(8);
		 }
		
		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public Object getChild(int arg0, int arg1) {
			return resousePathList.get(arg0);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(int arg0, int arg1, boolean arg2, View contentview, ViewGroup arg4) {
			contentview=inflater.inflate(R.layout.item_expandablelistview_photo_upload, null);
			GridView gridView=(GridView) contentview.findViewById(R.id.item_for_exlist_photoup_gridView1);
			adapters.put(""+arg0,new MyParkGalleryAdapter(arg0,arg1));
			gridView.setAdapter(adapters.get(arg0+""));
			setgalleryOnclick(gridView,arg0,arg1);
			return contentview;
		}

		private void setgalleryOnclick(final GridView gridView, final int groupID, final int childID) {
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					getimg(groupID,childID,arg2,gridView);   
				}
			});
		}

		@Override
		public int getChildrenCount(int arg0) {
			return 1;
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
			return photoType.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return photoType.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int groupPostion, boolean arg1, View contentview, ViewGroup arg3) {
			TextView titleTv,hintTv;
			ImageView imgtitle,imghint;
			final ImageView imgtitleJ;
			if (contentview==null) {
				contentview=inflater.inflate(R.layout.item_exlist_title_tv, null);
			}
			titleTv=(TextView)contentview.findViewById(R.id.expandablelist_item_title);
			titleTv.setText(photoType.get(groupPostion).description);
			
			
			
//			if (hintTvS.size()==photoType.tableData.data.size()) {
////			hintImageS=new ArrayList<TextView>();
//				hintTv=hintTvS.get(groupPostion);
//			}else {
				hintTv=(TextView)contentview.findViewById(R.id.expandablelist_item_title_hinttext);
//				hintTvS.add(hintTv);
//			}
				int count=0;
				List<List<List<WorkPhotoEntitiy>>> temp=resousePathList;
				@SuppressWarnings("unused")
				int lenght= resousePathList.get(groupPostion).get(0).size();
				for (int i = 0; i < resousePathList.get(groupPostion).get(0).size(); i++) {
					if (!TextUtils.isEmpty(resousePathList.get(groupPostion).get(0).get(i).location) &&
							resousePathList.get(groupPostion).get(0).get(i).location.indexOf("://")>-1) {
						count++;
					}
				}
				hintTv.setText("已上传"+count+"张");
//			if (resousePathList.get(groupPostion).size()>0 && hintTvS.size()<photoType.tableData.data.size()) {
//				hintTv.setText("已上传"+resousePathList.get(groupPostion).get(0).size()+"张");
//			}
//			RotateAnimation 
			
			imgtitle=(ImageView) contentview.findViewById(R.id.expandablelist_item_title_imgl);
			if ("commonInfo".equals(photoType.get(groupPostion).type)) {
				imgtitle.setImageResource(groupimgs[groupPostion]);
			}
			if (Jimags.size()<photoType.size()) {
				imgtitleJ=(ImageView) contentview.findViewById(R.id.expandablelist_item_title_imgj);
				imgtitleJ.setTag(0);
				Jimags.add(imgtitleJ);
			}else {
				imgtitleJ=Jimags.get(groupPostion);
			}
			return contentview;
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
		
	}
private class MyParkGalleryAdapter extends BaseAdapter{
		
		int [] imgIds;
		int groupPostion,childPostion;
		MyParkGalleryAdapter instans;
		
		public MyParkGalleryAdapter(int i,int j){
			groupPostion=i;
			childPostion=j;
			instans=this;
		}
		
		@Override
		public int getCount() {
			return resousePathList.get(groupPostion).get(childPostion).size()+1;
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
			Glide.with(activity).load(R.drawable.image123).into(img);
//			String tempPath=resousePathList.get(groupPostion).get(childPostion).get(arg0+"");
//			Glide.with(activity).load(tempPath).placeholder(R.drawable.loadingwait_hui).error(R.drawable.loadingerror_).centerCrop().into(img);
			
//			hintTvS.get(groupPostion).setText(arg0+"张");
			@SuppressWarnings("unused")
			List<List<List<WorkPhotoEntitiy>>> temp=resousePathList;
			if (resousePathList.get(groupPostion).get(childPostion).size()==arg0) {
//				if (!"completed".equals(AccidentStatu)) {
//					Glide.with(activity).load(R.drawable.image123).centerCrop().into(img);
//					contentview.setVisibility(View.VISIBLE);
//				}else {
//					contentview.setVisibility(View.GONE);
//				}
			}else {
				String tempPath=resousePathList.get(groupPostion).get(childPostion).get(arg0).location;
				if (!TextUtils.isEmpty(tempPath)) {
					if (tempPath.indexOf("://")>-1) {
						Glide.with(activity).load(tempPath+"?imageView2/2/w/200").placeholder(R.drawable.loadingwait_hui)
						.error(R.drawable.loadingerror_).into(img);
					}else {
						Glide.with(activity).load(tempPath).placeholder(R.drawable.loadingwait_hui).error(R.drawable.loadingerror_)
						.centerCrop().into(img);
						deleteimg.setVisibility(View.VISIBLE);
						deleteimg.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								deleteAndRemovePhoto(groupPostion,childPostion,arg0);
								instans.notifyDataSetChanged();
							}
						});
					}
				}else {
					Glide.with(activity).load(R.drawable.image123).placeholder(R.drawable.loadingwait_hui).centerCrop().into(img);
//					Glide.with(activity).load(imgIds[arg0]).centerCrop().into(img);
				}
			}
//			Animation animationstar=AnimationUtils.loadAnimation(activity, R.anim.expand_title_scale);
//			contentview.startAnimation(animationstar);
			contentview.setVisibility(View.VISIBLE);
			return contentview;
		}
	}
	
	/***图片选择与显示*===============================================================================================**/
	private int G,C,I;
	private String currPicturePath;
	private GridView GV;
	private void getimg(int groupID,int childID,int arg2,GridView gridView) {
		G=groupID;
		C=childID;
		I=arg2;
		GV=gridView;
		View view = LayoutInflater.from(activity).inflate(R.layout.photograph_view, null);
		View camera = (View) view.findViewById(R.id.photography_camera);
		View photos = (View) view.findViewById(R.id.photography_photos);
		View display = (View) view.findViewById(R.id.photography_display);
		View delete = (View) view.findViewById(R.id.photography_deleteplay);
		if (resousePathList.get(G).get(C).size()>I && !TextUtils.isEmpty(resousePathList.get(G).get(C).get(I).location)) {
			display.setVisibility(View.VISIBLE);
			if (resousePathList.get(G).get(C).get(I).location.indexOf("://")==-1) {//删除选择，而不是已上传的，已上传的图片不能删除
				delete.setVisibility(View.VISIBLE);
			}else {//已上传图片直接显示
				displayPictrue();
				return;
			}
		}
		dialog = DialogUtil.getDialogByView(activity, view);
		dialog.show();
		setOnClicks(camera);
		setOnClicks(photos);
		setOnClicks(display);
		setOnClicks(delete);
	}
	
	/**显示网络图片*/
	private void displayPictrue(){
		LoadDialogUtil.setMessageAndShow(activity,"处理中……");
		String largeUrlList=resousePathList.get(G).get(C).get(I).location;
		if (largeUrlList != null) {
			final Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			Glide.with(activity).load(largeUrlList).asBitmap().into(new SimpleTarget<Bitmap>() {  
                @Override  
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {  
                	//将bitmap转换为uri
					Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), resource, null, null));
					intent.setDataAndType(uri, "image/*");
					//设置intent数据和图片格式
					activity.startActivity(intent);
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
					String largeUrlList=resousePathList.get(G).get(C).get(I).location;
					if (largeUrlList != null) {
						final Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						if (largeUrlList.indexOf("://")==-1) {
							File file = new File(largeUrlList);
							//下方是是通过Intent调用系统的图片查看器的关键代码
							intent.setDataAndType(Uri.fromFile(file), "image/*");
							activity.startActivity(intent);
						}else {
							displayPictrue(); //显示网络图片
//							Glide.with(activity).load(largeUrlList).asBitmap().into(new SimpleTarget<Bitmap>() {  
//				                @Override  
//				                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {  
//				                	//将bitmap转换为uri
//									Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), resource, null, null));
//									intent.setDataAndType(uri, "image/*");
//									//设置intent数据和图片格式
//									activity.startActivity(intent);
//				                }
//				            }); //方法中设置asBitmap可以设置回调类型  
						}
					}
					break;
				case R.id.photography_deleteplay://删除选择
					deleteAndRemovePhoto(G,C,I);
					break;
				default:
					break;
				}
			}
		});
	}
	
	private void deleteAndRemovePhoto(int Gr,int Ch,int It){
		String filePath=resousePathList.get(Gr).get(Ch).get(It).location;
		File file=new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		resousePathList.get(Gr).get(Ch).remove(It);
		adapterNotify();
	}

	private void cameraphoto() {
//		// path为保存图片的路径，执行完拍照以后能保存到指定的路径下
		Intent cameraIntent = new Intent(activity, CXPhotoActivity.class);
//		cameraIntent.putExtra("photoType", photoType);
		cameraIntent.putExtra("GroupId", G);
		cameraIntent.putExtra("orderUid", activity.getIntent().getStringExtra("orderUid"));
		activity.startActivity(cameraIntent);
	}

	private void checkPhotos() {
//		Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//		getAlbum.setType(IMAGE_TYPE);
//		activity.startActivityForResult(getAlbum, IMAGE_CODE);
		Intent getAlbum=new Intent(activity, PhotoChoiceActivity.class);
//		getAlbum.putExtra("photoType", photoType);
		getAlbum.putExtra("GroupId", G);
		getAlbum.putExtra("orderUid", activity.getIntent().getStringExtra("orderUid"));
		activity.startActivity(getAlbum);
	}
	
	protected String getPictureCreatePath() {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			ToastUtil.showToastLong(activity, "SD卡不可用");
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
	
	public void eventresultcode(int requestCode, int resultCode, Intent data){
		
		switch (requestCode) {
		case IMAGE_CODE:
//			ToastUtil.showToastShort(activity, "返回编号："+requestCode);
			getphotos(data);//相册选择
			break;
		case CAMERA_IMAGE_CODE:
			getCameraPhoto(data);//拍照返回
			break;

		default:
			break;
		}
	}
	
	private void getCameraPhoto(Intent data) {
		File file = new File(currPicturePath);
		Uri imageUri = Uri.fromFile(file);
		
		
		Bitmap mBitmap= BitmapFactory.decodeFile(UriUtils.getFileUrl(activity, imageUri));
		ImageUtil.compressBitmap(activity, mBitmap, currPicturePath); 
		
		WorkPhotoEntitiy photoEntitiy=new WorkPhotoEntitiy();
		photoEntitiy.location=currPicturePath;
		resousePathList.get(G).get(C).add(photoEntitiy) ;
		adapterNotify();
	}
	
	public void adapterNotify(){
		if (expanAdapter!=null) {
			expanAdapter.notifyDataSetChanged();
		}
		if (GV!=null) {
			GV.setAdapter(new MyParkGalleryAdapter(G,C));
		}
		if (adapters!=null) {
			String adapterArr[]=adapters.keySet().toArray(new String[0]);
			for (int i = 0; i < adapterArr.length; i++) {
				adapters.get(adapterArr[i]).notifyDataSetChanged();
			}
		}
	}
	
/**相册选择图片返回Intent ，压缩，打水印后显示**/
	private void getphotos(Intent data) {
		try {
			Uri originalUri = data.getData(); // 获得图片的uri
			
			currPicturePath=PhotoPathUtil.getPictureCreatePath(activity.getIntent().getStringExtra("orderUid"));
			
			 @SuppressWarnings("unused")
			Bitmap mBitmap= BitmapFactory.decodeFile(UriUtils.getFileUrl(activity, originalUri));
			 
			ImageUtil.compressBitmap(activity, mBitmap, currPicturePath); 
			
			WorkPhotoEntitiy photoEntitiy=new WorkPhotoEntitiy();
			photoEntitiy.location=currPicturePath;
			resousePathList.get(G).get(C).add(photoEntitiy) ;
			expanAdapter.notifyDataSetChanged();
			GV.setAdapter(new MyParkGalleryAdapter(G,C));
		} catch (Exception e) {
			Log.e("getphotos", e.toString());
		}
	}
	
	public String dataToString(){
		return JSON.toJSONString(resousePathList);
	}


	public void upload() {
//		resousePathList;photoType
		final List<NameValuePair> params=new ArrayList<NameValuePair>();
		for (int i = 0; i < photoType.size(); i++) {
			List<List<WorkPhotoEntitiy>> list=resousePathList.get(i);
			for (int k = 0; k < list.size(); k++) {
				for (int j = 0; j < list.get(k).size(); j++) {
					if ( list.get(k).get(j)!=null) {
						String mathstr=list.get(k).get(j).location;
						if (mathstr.indexOf("://")==-1) {
							params.add(new BasicNameValuePair( photoType.get(i).id+"", mathstr));
						}
					}
				}
			}
		}
		Log.e("JsonHttpUtils", "10000"+JSON.toJSONString(params));
		
		if (params.size()!=0) {
			final List<NameValuePair> httpParams=new ArrayList<NameValuePair>();
			httpParams.add(new BasicNameValuePair("workId", activity.getIntent().getStringExtra("orderUid")));
			if (AppApplication.sp.getBoolean("isWifiUp", false) && !activity.isWifiConnected()) {//用户设置wifi条件下才能上传时提示用户
				DialogUtil.getAlertOnelistener(activity, "非Wifi条件下是否强制上传图片？",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						PhotoUploadUtil.upload(activity, params, URLs.UploadWorkPhoto(), httpParams);
					}
				}).show();
				return;
			}
			PhotoUploadUtil.upload(activity, params, URLs.UploadWorkPhoto(), httpParams);
		}else {
			DialogUtil.getErrDialog(activity, "未拍照").show();
		}
	}
	
}
