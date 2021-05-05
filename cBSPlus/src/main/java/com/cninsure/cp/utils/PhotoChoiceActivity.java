package com.cninsure.cp.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cninsure.cp.R;
import com.cninsure.cp.activity.yjxnew.entity.YjxNewCaseDispatchTable;
import com.cninsure.cp.activty.WorkOrderActivty;
import com.cninsure.cp.activty.WorkOrderActivtyhelp;
import com.cninsure.cp.cx.CxJieBaoanInfoActivity;
import com.cninsure.cp.cx.adapter.CxImagAdapter;
import com.cninsure.cp.cx.adapter.SaveImgCallBack;
import com.cninsure.cp.cx.jiebaoanfragment.CxImagFragment;
import com.cninsure.cp.dispersive.DispersiveWorkActivity;
import com.cninsure.cp.entity.WorkPhotos.TableData.WorkPhotoEntitiy;
import com.cninsure.cp.entity.cx.CxImagEntity;

public class PhotoChoiceActivity extends Activity {

	// 显示图片名称的list
	private GridView show_list;
	private TextView cancelTv, titleTv, sureTv;
	private ArrayList<String> names = null;
	private ArrayList<String> descs = null;
	private ArrayList<String> fileNames = null;
	private Map<Integer, String> choiceImgs;
	private int GroupId;
	private List<WorkPhotoEntitiy> temPhotoEntitiys;

	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_choice_layout);
		initView();
		names = new ArrayList<String>();
		descs = new ArrayList<String>();
		fileNames = new ArrayList<String>();
		choiceImgs = new HashMap<Integer, String>();
		getAndShowImg();

		// /list的点击事件
		show_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//				Intent intent = new Intent();
//				intent.setAction(android.content.Intent.ACTION_VIEW);
//				intent.setDataAndType(Uri.fromFile(new File(fileNames.get(i))), "image/*");
//				PublicPhotoChoiceActivity.this.startActivity(intent);
				ImageDisplayUtil.displayByMyView(PhotoChoiceActivity.this, fileNames.get(i));
			}
		});
	}

	/** 初始化控件 **/
	private void initView() {
		GroupId = getIntent().getIntExtra("GroupId", 0);
		temPhotoEntitiys = new ArrayList<WorkPhotoEntitiy>();
		show_list = (GridView) findViewById(R.id.PCHOICE_GridView);
		cancelTv = (TextView) findViewById(R.id.PCHOICE_LTV);
		sureTv = (TextView) findViewById(R.id.PCHOICE_RTV);
		titleTv = (TextView) findViewById(R.id.PCHOICE_CTV);
		String actionActivityName=getIntent().getStringExtra("ActivityName");
		if ("NEW_CX".equals(actionActivityName)){  //车险新全流程
//			String title = WorkOrderActivty.photoType.tableData.data.get(GroupId).description;
//			titleTv.setText(title);
		}else if (!"DispersiveWorkActivity".equals(actionActivityName)){
			String title = WorkOrderActivty.photoType.tableData.data.get(GroupId).description;
			titleTv.setText(title);
		}
		cancelTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PhotoChoiceActivity.this.finish();
			}
		});

		sureTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (temPhotoEntitiys != null && temPhotoEntitiys.size() > 0  && actionActivityName.equals("DispersiveWorkActivity")) { //分散型的请求
					int imgType = getIntent().getIntExtra("photoType",0);
					int imageSubType = getIntent().getIntExtra("imageSubType",0);
					DispersiveWorkActivity.instence.addPhoto(temPhotoEntitiys,imgType,imageSubType);
				}else if (temPhotoEntitiys != null && temPhotoEntitiys.size() > 0  && actionActivityName.equals("NEW_CX")){ //新车险全流程
					addCxNewImg();
				}else if(temPhotoEntitiys != null && temPhotoEntitiys.size() > 0  ) { //其他的请求
					WorkOrderActivtyhelp.resousePathList.get(GroupId).get(0).addAll(temPhotoEntitiys);
				}
				PhotoChoiceActivity.this.finish();
			}
		});
	}

	/**添加新车险全流程图片**/
	private void addCxNewImg(){
		List<CxImagEntity> cieListTemp = new ArrayList<>();
		String imgType = getIntent().getStringExtra("photoType");
		for (WorkPhotoEntitiy wpe:temPhotoEntitiys){
			CxImagEntity cieEn = new CxImagEntity();
			cieEn.type = imgType;
			cieEn.source = "2";
			cieEn.fileUrl = wpe.location;
			cieEn.fileName = wpe.location.substring(wpe.location.lastIndexOf("/")+1,wpe.location.length()-4);
			cieListTemp.add(cieEn);
		}
		CxImagFragment.adapter.addScb.addImg(cieListTemp,getIntent().getIntExtra("groupPosition",0));
	}

	/** 判断是选择还是取消选择 **/
	private void saveOrDelete(int postion, boolean isSave) {
		if (isSave) {
			handleAndSaveImag(postion);
		} else {
			deleteChoice(postion);
		}
	}

	/** 删除选择 **/
	private void deleteChoice(int postion) {
		for (int i = 0; i < temPhotoEntitiys.size(); i++) {
			String filePath = temPhotoEntitiys.get(i).location;
			if (choiceImgs!=null && choiceImgs.get(postion)!=null && choiceImgs.get(postion).equals(filePath)) {
				temPhotoEntitiys.remove(i);
				File file = new File(filePath);
				if (file.exists()) {// 照片存在就删除
					file.delete();
				}
			}
		}
		choiceImgs.remove(postion);
	}

	/** 压缩，打水印并储存照片到制定路径，然后存入照片列表对象中 **/
	private void handleAndSaveImag(int postion) {
		// 将文件存储在SD卡的根目录，并以系统时间将文件命名
		String PicturePath = PhotoPathUtil.getPictureCreatePath(getIntent().getStringExtra("orderUid"),PhotoChoiceActivity.this);
		choiceImgs.put(postion, PicturePath);

		Intent data = new Intent();
		data.setData(Uri.fromFile(new File(fileNames.get(postion))));
		ImageUtil.compressBmp(PhotoChoiceActivity.this, data, PicturePath);// 压缩、水印、储存

		WorkPhotoEntitiy photoEntitiy = new WorkPhotoEntitiy();
		photoEntitiy.location = PicturePath;
		temPhotoEntitiys.add(photoEntitiy);
		// DispersiveWorkActivtyhelp.resousePathList.get(GroupId).get(0).add(photoEntitiy);
	}

	public void getAndShowImg() {
		Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				null, null, null, android.provider.ContactsContract.Contacts._ID + " DESC");
		while (cursor.moveToNext()) {
			// 获取图片的名称
			String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
			// 获取图片的生成日期
			byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			// 获取图片的详细信息
			String desc = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION));
			names.add(name);
			descs.add(desc);
			fileNames.add(new String(data, 0, data.length - 1));
		}
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < names.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", names.get(i));
			map.put("desc", descs.get(i));
			listItems.add(map);
		}
		// 设置adapter
		show_list.setAdapter(new MyGridAdapter());
	}

	private class MyGridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return fileNames.size();
		}

		@Override
		public Object getItem(int arg0) {
			return fileNames.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int itemPostion, View conview, ViewGroup arg2) {
			ViewHolder vh = null;
			if (conview != null) {
				vh = (ViewHolder) conview.getTag();
			} else {
				vh = new ViewHolder();
				conview = LayoutInflater.from(PhotoChoiceActivity.this).inflate(R.layout.gride_image_view, null);
				vh.img = (ImageView) conview.findViewById(R.id.imageView1);
				vh.checkbox = (CheckBox) conview.findViewById(R.id.checkBox1);
				conview.setTag(vh);
			}
			vh.img.getLayoutParams().height = getImageWeight();
			vh.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
			String imgPath = fileNames.get(itemPostion);
			Glide.with(PhotoChoiceActivity.this).load(imgPath).into(vh.img);
			vh.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean choice) {
					if (choice) {
						saveOrDelete(itemPostion, true);
						// handleAndSaveImag(itemPostion);
					} else {
						saveOrDelete(itemPostion, false);
						// choiceImgs.remove(itemPostion);
					}
				}
			});
			if (choiceImgs.get(itemPostion) != null) {
				vh.checkbox.setChecked(true);
			} else {
				vh.checkbox.setChecked(false);
			}
			return conview;
		}

		private class ViewHolder {
			public ImageView img;
			public CheckBox checkbox;
		}
	}

	/** 计算ImageView的尺寸 */
	public int getImageWeight() {
		// 通过WindowManager获取
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float w = dm.widthPixels;
		return (int) (w - 15 - 32) / 4;
	}

	@Override
	protected void onStop() {
		super.onStop();
		Intent intent = new Intent();
		setResult(0, intent);
	}

}