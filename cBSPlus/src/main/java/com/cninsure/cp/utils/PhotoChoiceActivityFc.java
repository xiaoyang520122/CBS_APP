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
import com.cninsure.cp.entity.fc.WorkFile.FCFileEntity;
import com.cninsure.cp.fc.activity.SurveyActivityHelp;

public class PhotoChoiceActivityFc extends Activity {

	// 显示图片名称的list
	private GridView show_list;
	private TextView cancelTv, titleTv, sureTv;
	private ArrayList<String> names = null;
	private ArrayList<String> descs = null;
	private ArrayList<String> fileNames = null;
	private Map<Integer, String> choiceImgs;
	private int GroupId;
	private List<FCFileEntity> temPhotoEntitiys;

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
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(fileNames.get(i))), "image/*");
				PhotoChoiceActivityFc.this.startActivity(intent);
			}
		});
	}

	/** 初始化控件 **/
	private void initView() {
		GroupId = getIntent().getIntExtra("GroupId", 0);
		temPhotoEntitiys = new ArrayList<FCFileEntity>();
		show_list = (GridView) findViewById(R.id.PCHOICE_GridView);
		cancelTv = (TextView) findViewById(R.id.PCHOICE_LTV);
		sureTv = (TextView) findViewById(R.id.PCHOICE_RTV);
		titleTv = (TextView) findViewById(R.id.PCHOICE_CTV);

		String title = SurveyActivityHelp.FileTypes.get(GroupId).getLabel();
		titleTv.setText(title);
		cancelTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PhotoChoiceActivityFc.this.finish();
			}
		});

		sureTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (temPhotoEntitiys != null && temPhotoEntitiys.size() > 0) {
					SurveyActivityHelp.fileDataList.get(GroupId).addAll(temPhotoEntitiys);
				}
				PhotoChoiceActivityFc.this.finish();
			}
		});
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
			String filePath = temPhotoEntitiys.get(i).filePath;
			if (filePath.equals(choiceImgs.get(postion))) {
				temPhotoEntitiys.remove(i);
			}
		}
		choiceImgs.remove(postion);
	}

	/**存入照片列表对象中 **/
	private void handleAndSaveImag(int postion) {
		String PicturePath = fileNames.get(postion);
		choiceImgs.put(postion, PicturePath);

//		Intent data = new Intent();
//		data.setData(Uri.fromFile(new File(fileNames.get(postion))));
//		ImageUtil.compressBmp(PhotoChoiceActivityFc.this, data, PicturePath);// 压缩、水印、储存

		FCFileEntity photoEntitiy = new FCFileEntity();
		String workIdstr=getIntent().getStringExtra("orderUid");
		photoEntitiy.workId = workIdstr!=null? Long.valueOf(workIdstr):0;
		photoEntitiy.filePath = PicturePath;
		photoEntitiy.fileType="11";
		photoEntitiy.fileName=PicturePath.substring(PicturePath.lastIndexOf("/")+1);
		photoEntitiy.fileTypeId=GroupId+"";
		temPhotoEntitiys.add(photoEntitiy);
		// DispersiveWorkActivtyhelp.resousePathList.get(GroupId).get(0).add(photoEntitiy);
	}

	public void getAndShowImg() {
		Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,  android.provider.ContactsContract.Contacts._ID + " DESC");
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
				conview = LayoutInflater.from(PhotoChoiceActivityFc.this).inflate(R.layout.gride_image_view, null);
				vh.img = (ImageView) conview.findViewById(R.id.imageView1);
				vh.checkbox = (CheckBox) conview.findViewById(R.id.checkBox1);
				conview.setTag(vh);
			}
			vh.img.getLayoutParams().height = getImageWeight();
			vh.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
			String imgPath = fileNames.get(itemPostion);
			Glide.with(PhotoChoiceActivityFc.this).load(imgPath).into(vh.img);
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