package com.cninsure.cp.fc.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.activty.DisplayPictureActivity;
import com.cninsure.cp.entity.DictEntity.DictDatas.publicData;
import com.cninsure.cp.entity.MIMEUtil;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.WorkFile;
import com.cninsure.cp.entity.fc.WorkFile.FCFileEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.PhotoChoiceActivityFc;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.UriUtils;

public class SurveyActivityHelp {
	
	private static SurveyActivity activity;
	/**文件分类**/
	public static List<publicData> FileTypes;
	private ExpandableListView expandableListView;
	public UpPhotoExpandablelistAdapter expanAdapter;
//	private String [] fileTypes=new String[]{"查勘资料","承保资料","索赔资料","公估委托资料","财务账册","事故证明"
//			,"定损资料","结案资料","现场查勘图片","往来函件","影音文件"};
	private LayoutInflater inflater;
	private WorkFile workFile;
	public final int FILE_SELECT_CODE=10001;
	private final int IMAGE_CODE = 0;
	private final int CAMERA_IMAGE_CODE = 1;
	private final String IMAGE_TYPE = "image/*";
	public static List<List<FCFileEntity>> fileDataList ;
	

	@SuppressWarnings("unused")
	private SurveyActivityHelp(){};
	
	public SurveyActivityHelp (SurveyActivity activity,View uploadView){
		this.activity=activity;
		inflater=LayoutInflater.from(activity);
		expandableListView=(ExpandableListView) uploadView.findViewById(R.id.workorderactivity_expandablelistview);
	}
	
	/**获取到上次文件后回显**/
	public void displayHistoryFile(String value){
		FileTypes=activity.dictEntity.data.file_type_id;
		workFile=JSON.parseObject(value, WorkFile.class);
		fileDataList=new ArrayList<List<FCFileEntity>>(11);
		getfileDataList();
		expanAdapter=new UpPhotoExpandablelistAdapter();
		expandableListView.setAdapter(expanAdapter);
		setExpandableOnitemClick();
	}
	
	private void setExpandableOnitemClick() {
		expandableListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1, int goupPostion, int childPostion, long chileId) {
				if (childPostion<fileDataList.get(goupPostion).size()) {
					FCFileEntity  fileEntity = fileDataList.get(goupPostion).get(childPostion);
					String photoPath=fileEntity.filePath;
					if ("11".equals(fileEntity.fileType)) {
						Intent intent = new Intent(activity, DisplayPictureActivity.class);
						intent.putExtra("picUrl", photoPath);
						activity.startActivity(intent);
					}else {
						openFile(new File(photoPath));
					}
				}
				return true;
			}
		});
	}
	
	 /**
	* 打开文件
	* @param file
	*/ 
	private void openFile(File file){ 

	try {
		Intent intent = new Intent(); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		//设置intent的Action属性 
		intent.setAction(Intent.ACTION_VIEW); 
		//获取文件file的MIME类型 
		String type = getMIMEType(file); 
		//设置intent的data和Type属性。 
		intent.setDataAndType(/*uri*/Uri.fromFile(file), type); 
		//跳转 
		activity.startActivity(intent); //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
	} catch (Exception e) {
		ToastUtil.showToastShort(activity, "未知的文件类型！");
		e.printStackTrace();
	}

	} 
	
	/**
	* 根据文件后缀名获得对应的MIME类型。
	* @param file
	*/ 
	private String getMIMEType(File file) {

		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIMEUtil.MIME_MapTable.length; i++) { // MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if (end.equals(MIMEUtil.MIME_MapTable[i][0]))
				type = MIMEUtil.MIME_MapTable[i][1];
		}
		return type;
	} 

	/**将得到的数据按分类顺序装到集合中，如果workFile.data==null 就 装入11个空的集合**/
	private void getfileDataList() {
		
		for (int i = 0; i < FileTypes.size(); i++) {
			List<FCFileEntity> tempdata=new ArrayList<WorkFile.FCFileEntity>();
			if (null!=workFile.data) {//如果workFile.data==null 就 装入1个空的集合
				for (int j = 0; j < workFile.data.size(); j++) {
					if ((FileTypes.get(i).value+"").equals(workFile.data.get(j).fileTypeId)) {
						tempdata.add(workFile.data.get(j));
					}
				}
			}
			fileDataList.add(tempdata);
		}
		getLocalstorageFile();//去读取暂处本地的文件
	}
	
	/**获取暂存本地的文件信息列表**/
	private void getLocalstorageFile() {
		String storagestr = AppApplication.sp.getString(activity.getIntent().getIntExtra("id", 0) + "FCfileDataList", "");
		if (!TextUtils.isEmpty(storagestr)) {
			// List<List<FCFileEntity>> temparr=
//			 JSON.parseArray(storagestr, ArrayList<ArrayList<FCFileEntity>>.class);
			// WorkFile storageFile=JSON.parseObject(storagestr,
			// WorkFile.class);
//			 try {
//				 List<List<FCFileEntity>> obj = (List<List<FCFileEntity>>) JSON.parse(storagestr);
//				 List<FCFileEntity> obj2=obj.get(0);
//				 List<FCFileEntity> obj3=obj2;
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			try {
				JSONArray maxarr = new JSONArray(storagestr);
				for (int i = 0; i < FileTypes.size(); i++) {
					List<FCFileEntity> tempdata=new ArrayList<WorkFile.FCFileEntity>();
					for (int j = 0; j < maxarr.length(); j++) {
						JSONArray mixArr = maxarr.getJSONArray(j);
						for (int k = 0; k < mixArr.length(); k++) {
							FCFileEntity tempfile=JSON.parseObject(mixArr.get(k).toString(), FCFileEntity.class);
							if ((FileTypes.get(i).value+"").equals(tempfile.fileTypeId)
									&& tempfile.filePath.indexOf("ftp://")==-1) { //类型匹配且文件不是网络图片
								tempdata.add(tempfile);
							}
						}
					}
					fileDataList.get(i).addAll(tempdata);//追加到对应的位置
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// if (storageFile!=null && storageFile.data.size()>0) {
			// getfileDataList(storageFile);
			// }
		}
	}
	
	/**将得到的数据按分类顺序装到集合中，集合已经在上上一个步骤新建好了，不用重建了**/
	private void getfileDataList(WorkFile storageFile) {
		
		for (int i = 0; i < FileTypes.size(); i++) {
			List<FCFileEntity> tempdata=new ArrayList<WorkFile.FCFileEntity>();
				for (int j = 0; j < storageFile.data.size(); j++) {
					if ((FileTypes.get(i).value+"").equals(storageFile.data.get(j).fileTypeId)
							&& storageFile.data.get(j).filePath.indexOf("ftp://")==-1) { //类型匹配且文件不是网络图片
						tempdata.add(storageFile.data.get(j));
					}
			}
			fileDataList.add(tempdata);
		}
	}

	class UpPhotoExpandablelistAdapter extends BaseExpandableListAdapter {
		
		Handler handler;
		public UpPhotoExpandablelistAdapter() {
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					notifyDataSetChanged();
					super.handleMessage(msg);
				}
			};
		}

		public void refresh() {
			handler.sendMessage(new Message());
		}


		
		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public Object getChild(int arg0, int arg1) {
				return fileDataList.get(arg0);
		}
		
		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(final int groupId, final int childId, boolean arg2, View contentview, ViewGroup arg4) {
			TextView nameTv,statuTv,deleteTv;
			ImageView typeImg;
			contentview=inflater.inflate(R.layout.expandable_child_item, null);
			nameTv=(TextView) contentview.findViewById(R.id.UPPHOTO_LI_name);
			statuTv=(TextView) contentview.findViewById(R.id.UPPHOTO_LI_statu);
			deleteTv=(TextView) contentview.findViewById(R.id.UPPHOTO_LI_delete);
			typeImg=(ImageView) contentview.findViewById(R.id.UPPHOTO_LI_imageV);
			
			if (childId==fileDataList.get(groupId).size()) {//显示添加文件按钮
				nameTv.setVisibility(View.GONE);
				deleteTv.setVisibility(View.GONE);
				typeImg.setVisibility(View.GONE);
				statuTv.setVisibility(View.VISIBLE);
				statuTv.setText("添加");
				statuTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						setonclickmoth(groupId,childId);
						showCheckFileDialog();
					}
				});
			}else {//显示信息
				
				nameTv.setText(fileDataList.get(groupId).get(childId).fileName);
				if (fileDataList.get(groupId).get(childId).filePath.indexOf("ftp://")==-1) {
					statuTv.setText("未上传");
					deleteTv.setVisibility(View.VISIBLE);
					deleteTv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							setonclickmoth(groupId,childId);
							 Log.i("choiceFlage","groupId="+groupId+" childId="+childId+" "+ JSON.toJSONString(fileDataList));
							fileDataList.get(groupId).remove(childId);
//							refresh();123
							//下面两行刷新expandableListView
							expandableListView.collapseGroup(groupId);
							expandableListView.expandGroup(groupId);
						}
					});
				}else {
					statuTv.setText("");
					deleteTv.setVisibility(View.INVISIBLE);
				}
				/**图片类型更换图标*/
				if ("11".equals(fileDataList.get(groupId).get(childId).fileType)) {
					typeImg.setImageResource(R.drawable.add_photos_bule36);
				}else {
					typeImg.setImageResource(R.drawable.add_new_order_bule36);
				}
			}
			
			return contentview;
		}
		

		@Override
		public int getChildrenCount(int groupId) {
			return fileDataList.get(groupId).size()+1;
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
			return FileTypes.get(arg0).getLabel();
		}

		@Override
		public int getGroupCount() {
			return FileTypes.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		/****/
		@Override
		public View getGroupView(int groupPostion, boolean arg1, View contentview, ViewGroup arg3) {
			TextView titleTv,hintTv;
			ImageView imgtitle,imghint;
			contentview=inflater.inflate(R.layout.item_exlist_title_tv, null);
			titleTv=(TextView)contentview.findViewById(R.id.expandablelist_item_title);
			hintTv=(TextView)contentview.findViewById(R.id.expandablelist_item_title_hinttext);
			titleTv.setText(FileTypes.get(groupPostion).getLabel());
			int count=0;
			for (int i = 0; i < fileDataList.get(groupPostion).size(); i++) {
				count++;
			}
			hintTv.setText("文件数量："+count);
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
		
		
		/**设置选择文件的单机时间**/
		private void setonclickmoth(int groupId, int childId) {
			G=groupId;
			C=childId;
		}

	}
	
	private int G,C,I;
	private String currPicturePath;
	
	
	/**弹框选择文件**/
	private void showCheckFileDialog() {
		DialogUtil.getItemDialog(activity, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int position) {
				switch (position) {
				case 0://拍照
					cameraphoto();
					break;
					
				case 1://选择照片
					checkPhotos();
					break;
					
				case 2://选择文件
					showFileChooser();
					break;

				default:
					break;
				}
			}
		}, new String[]{"拍照","选择照片","选择文件"}).show();
	}
	
	/**选择文件**/
	private void showFileChooser() {
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
	    intent.setType("*/*"); 
	    intent.addCategory(Intent.CATEGORY_OPENABLE);
	 
	    try {
	    	activity.startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
	    } catch (android.content.ActivityNotFoundException ex) {
	        DialogUtil.getAlertOneButton(activity, "抱歉，无法打开文件管理器！您可以安装一个文件管理器再试一次。", null).show();
	    }
	}
	
	/**选择照片**/
	private void checkPhotos() {
		//系统相册选择照片
//		Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
//		getAlbum.setType(IMAGE_TYPE);
//		activity.startActivityForResult(getAlbum, IMAGE_CODE);
		
		//自定义相册选择照片
		Intent getAlbum=new Intent(activity, PhotoChoiceActivityFc.class);
//		getAlbum.putExtra("photoType", photoType);
		getAlbum.putExtra("GroupId", G);
		getAlbum.putExtra("orderUid", activity.getIntent().getStringExtra("orderUid"));
		activity.startActivity(getAlbum);
	}
	
	/**拍照**/
	private void cameraphoto() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// path为保存图片的路径，执行完拍照以后能保存到指定的路径下
		currPicturePath = getPictureCreatePath();
		File file = new File(currPicturePath);
		Uri imageUri = Uri.fromFile(file);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		activity.startActivityForResult(cameraIntent, CAMERA_IMAGE_CODE);
	}
	
	public void eventresultcode(int requestCode, int resultCode, Intent data){
		switch (requestCode) {
		case FILE_SELECT_CODE:
			getFilePath(data);
			break;
			
		case IMAGE_CODE:
			getphotos(data);
			break;
			
		case CAMERA_IMAGE_CODE:
			getCameraPhoto(data);
			break;

		default:
			break;
		}
	}
	
	/**获取文件路径
	 * @param data **/
	private void getFilePath(Intent data) {
		 Uri uri = data.getData();
		 currPicturePath = FileUtils.getPath(uri);
		 if (currPicturePath==null) {
			DialogUtil.getAlertOneButton(activity, "选取文件失败！", null);
		}else {
			  saveAndNotify("22");
		}
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
	
	/**获取照片的真实路径**/
	private void getphotos(Intent data) {
		try {
			Uri originalUri = data.getData(); // 获得图片的uri
			currPicturePath = UriUtils.getFileUrl(activity, originalUri);
			saveAndNotify("11");
		} catch (Exception e) {
			Log.e("getphotos", e.toString());
		}
	}
	private void getCameraPhoto(Intent data) {
		saveAndNotify("11");
	}
	
	/**获取选择文件路径**/
	public static class FileUtils {
	    public static String getPath(Uri uri) {
	 
	        if ("content".equalsIgnoreCase(uri.getScheme())) {
	            String[] projection = { "_data" };
	            Cursor cursor = null;
	 
	            try {
	                cursor = activity.getContentResolver().query(uri, projection,null, null, null);
	                int column_index = cursor.getColumnIndexOrThrow("_data");
	                if (cursor.moveToFirst()) {
	                    return cursor.getString(column_index);
	                }
	            } catch (Exception e) {
	                // Eat it
	            }
	        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
	            return uri.getPath();
	        }
	        return null;
	    }
	}
	/**添加获得的文件信息并刷新界面*/
	private void saveAndNotify(String type){
		Log.e("JsonHttpUtils", "得到的文件路径"+currPicturePath);
		FCFileEntity tempdata=new WorkFile.FCFileEntity();
		tempdata.fileName=currPicturePath.substring(currPicturePath.lastIndexOf("/")+1);
		if (type.equals("22")) {
			if (currPicturePath.lastIndexOf(".jpg")>0 ||currPicturePath.lastIndexOf(".png")>0 ) {
				tempdata.fileType="11";
			}else {
				tempdata.fileType="22";
			}
		}else {
			tempdata.fileType="11";
		}
		tempdata.fileTypeId=G+"";
		tempdata.filePath=currPicturePath;
		fileDataList.get(G).add(tempdata);
		Log.e("JsonHttpUtils", "储存后的集合打印："+JSON.toJSONString(fileDataList));
		saveToLocalStorge(JSON.toJSONString(fileDataList));//暂存
		refresh();
	}
	
	/**暂存到共享参数文件中**/
	private void saveToLocalStorge(String fileDatasStr){ //String storagestr = AppApplication.sp.getString("FCfileDataList", "");
		Editor editor=AppApplication.sp.edit();
		editor.putString(activity.getIntent().getIntExtra("id", 0)+"FCfileDataList", fileDatasStr);
		editor.commit();
		editor.clear();
	}
	
	/**跟新数据并刷新界面，查了一查SDK 文档, 发现Group 的伸缩会引起getChildView(int, int, boolean, View, ViewGroup)  的运行!
所以刷新的ChildView 的方法很简单. 
只有伸缩一次就可以了!*/
	public void refresh() {  
		expanAdapter.notifyDataSetChanged();
		expandableListView.collapseGroup(G);  
		expandableListView.expandGroup(G); 
    }

	/**上传新新选择的文件**/
	public void upload(final String caseNo,final String workId) {
		final List<NameValuePair> params=new ArrayList<NameValuePair>();
		for (int i = 0; i < fileDataList.size(); i++) {
			List<FCFileEntity> list=fileDataList.get(i);
			for (int j = 0; j < list.size(); j++) {
				if ( list.get(j)!=null) {
					String mathstr=list.get(j).filePath;
					if (mathstr.indexOf("ftp://")==-1) {
						params.add(new BasicNameValuePair( list.get(j).fileTypeId, mathstr));
					}
				}
			}
		}
		Log.e("JsonHttpUtils", "10000"+JSON.toJSONString(params));
		
		if (params.size()!=0) {
			if (AppApplication.sp.getBoolean("isWifiUp", false) && !activity.isWifiConnected()) {//用户设置wifi条件下才能上传时提示用户
				DialogUtil.getAlertOnelistener(activity, "非Wifi条件下是否强制上传图片？",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						PhotoUploadUtil.FCupload(activity, params, URLs.UPLOAD_WORK_FILE,caseNo,workId );
					}
				}).show();
				return;
			}
			PhotoUploadUtil.FCupload(activity, params, URLs.UPLOAD_WORK_FILE,caseNo,workId );
		}else {
			DialogUtil.getErrDialog(activity, "没有新的上传内容！").show();
		}
	}
	
	public void refreshExlist(){
		try {
			if (expandableListView!=null) {
				expandableListView.collapseGroup(G);
				expandableListView.expandGroup(G);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	};
}
