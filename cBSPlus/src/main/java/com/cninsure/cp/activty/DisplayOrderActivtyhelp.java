package com.cninsure.cp.activty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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
import com.cninsure.cp.R;
import com.cninsure.cp.entity.WorkPhotos;
import com.cninsure.cp.entity.WorkPhotos.TableData.WorkPhotoEntitiy;
import com.cninsure.cp.entity.WorkType;

public class DisplayOrderActivtyhelp {
	private ExpandableListView expandableListView;
	private PhotoExpandablelistAdapter expanAdapter;
	private DisplayOrderActivity activity;
	private View view;
	private LayoutInflater inflater;
	private int[] groupimgs = new int[] { R.drawable.renchehy_hui48, R.drawable.shigud_hui48, R.drawable.biaodicds_hui48, R.drawable.caichanssz_hui48, R.drawable.renshang_hui48,
			R.drawable.shigud_hui48, R.drawable.xingshiz_hui48, R.drawable.jiashiz_hui48 };

	private List<List<Map<String, String>>> resousePathList;
	/** 组是否拍齐照片的标识图标集合 **/
	private List<TextView> hintTvS;
	private WorkType photoType;
	private WorkPhotos workphotos;
	private List<ImageView> Jimags;

	public DisplayOrderActivtyhelp(DisplayOrderActivity workOrderActivty, View uploadView, WorkType photoType, WorkPhotos workphoto) {
		activity = workOrderActivty;
		view = uploadView;
		this.photoType = photoType;
		workphotos = workphoto;
		inflater = LayoutInflater.from(activity);
		getdefaulValue();
	}

	public ExpandableListView getExpandableListView(Activity context, View v) {
		initView();
		return expandableListView;
	}

	private void initView() {
		Jimags = new ArrayList<ImageView>(8);
		expandableListView = (ExpandableListView) view.findViewById(R.id.workorderactivity_expandablelistview);
		expanAdapter = new PhotoExpandablelistAdapter();
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(expanAdapter);

		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3) {
				ImageView imageView = Jimags.get(arg2);
				int degrees = (Integer) imageView.getTag() % 360;
				RotateAnimation animation = new RotateAnimation(degrees, degrees + 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				animation.setFillAfter(true);
				imageView.startAnimation(animation);
				imageView.setTag(degrees + 180);
				return false;
			}
		});

	}

	private void getdefaulValue() {
		// workphotos
		hintTvS = new ArrayList<TextView>(8);
		resousePathList = new ArrayList<List<Map<String, String>>>(8);
		List<Map<String, String>> childList;
		Map<String, String> resourseMap;
		for (int i = 0; i < photoType.tableData.data.size(); i++) {
			childList = new ArrayList<Map<String, String>>();
			for (int k = 0; k < 1; k++) {
				resourseMap = new HashMap<String, String>();
				List<WorkPhotoEntitiy> dataEntitiy = workphotos.getByTypeId(photoType.tableData.data.get(i).id + "");
				for (int j = 0; j < dataEntitiy.size(); j++) {
					resourseMap.put(j + "", dataEntitiy.get(j).location);
				}
				childList.add(resourseMap);
			}
			resousePathList.add(childList);
		}
	}

	private class PhotoExpandablelistAdapter extends BaseExpandableListAdapter {

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
			contentview = inflater.inflate(R.layout.item_expandablelistview_photo_upload, null);
			GridView gridView = (GridView) contentview.findViewById(R.id.item_for_exlist_photoup_gridView1);
			gridView.setAdapter(new MyParkGalleryAdapter(arg0, arg1));
			setgalleryOnclick(gridView, arg0, arg1);
			return contentview;
		}

		private void setgalleryOnclick(final GridView gridView, final int groupID, final int childID) {
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					String largeUrlList = resousePathList.get(groupID).get(childID).get(arg2 + "");
					if (largeUrlList != null) {
						Intent intent = new Intent(activity, DisplayPictureActivity.class);
						intent.putExtra("picUrl", largeUrlList);
						// getimg(groupID,childID,arg2,gridView);
					}
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
			return photoType.tableData.data.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return photoType.tableData.data.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int groupPostion, boolean arg1, View contentview, ViewGroup arg3) {
			TextView titleTv, hintTv;
			ImageView imgtitle, imghint;
			final ImageView imgtitleJ;
			if (contentview == null) {
				contentview = inflater.inflate(R.layout.item_exlist_title_tv, null);
			}
			titleTv = (TextView) contentview.findViewById(R.id.expandablelist_item_title);
			titleTv.setText(photoType.tableData.data.get(groupPostion).description);

			hintTv = (TextView) contentview.findViewById(R.id.expandablelist_item_title_hinttext);
			int count = 0;
			List<List<Map<String, String>>> temp = resousePathList;
			@SuppressWarnings("unused")
			int lenght = resousePathList.get(groupPostion).get(0).size();
			for (int i = 0; i < resousePathList.get(groupPostion).get(0).size(); i++) {
				if (!TextUtils.isEmpty(resousePathList.get(groupPostion).get(0).get(i + "")) && resousePathList.get(groupPostion).get(0).get(i + "").indexOf("://") > -1) {
					count++;
				}
			}
			hintTv.setText( count + "张");

			imgtitle = (ImageView) contentview.findViewById(R.id.expandablelist_item_title_imgl);
			if ("commonInfo".equals(photoType.tableData.data.get(groupPostion).type)) {
				imgtitle.setImageResource(groupimgs[groupPostion]);
			}
			if (Jimags.size() < photoType.tableData.data.size()) {
				imgtitleJ = (ImageView) contentview.findViewById(R.id.expandablelist_item_title_imgj);
				imgtitleJ.setTag(0);
				Jimags.add(imgtitleJ);
			} else {
				imgtitleJ = Jimags.get(groupPostion);
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

	private class MyParkGalleryAdapter extends BaseAdapter {

		int groupPostion, childPostion;

		public MyParkGalleryAdapter(int i, int j) {
			groupPostion = i;
			childPostion = j;
		}

		@Override
		public int getCount() {
			List<List<Map<String, String>>> temp = resousePathList;
			return resousePathList.get(groupPostion).get(childPostion).size();
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
		public View getView(int arg0, View contentview, ViewGroup arg2) {
			contentview = inflater.inflate(R.layout.item_gridview_for_exlistview_photoup, null);
			ImageView img = (ImageView) contentview.findViewById(R.id.item_gridviewForExlist_photoup_img);
			Glide.with(activity).load(R.drawable.image123).centerCrop().into(img);
			@SuppressWarnings("unused")
			List<List<Map<String, String>>> temp = resousePathList;
			if (resousePathList.get(groupPostion).get(childPostion).size() == arg0) {
			} else {
				String tempPath = resousePathList.get(groupPostion).get(childPostion).get(arg0 + "");
				if (!TextUtils.isEmpty(tempPath)) {
					if (tempPath.indexOf("://") > -1) {
						Glide.with(activity).load(tempPath + "?imageView2/2/w/200").placeholder(R.drawable.loadingwait_hui).error(R.drawable.loadingerror_).centerCrop().into(img);
					} else {
						Glide.with(activity).load(tempPath).placeholder(R.drawable.loadingwait_hui).error(R.drawable.loadingerror_).centerCrop().into(img);
					}

				} else {
					Glide.with(activity).load(R.drawable.image123).centerCrop().into(img);
				}
			}
			contentview.setVisibility(View.VISIBLE);
			return contentview;
		}
	}


	public String dataToString() {
		return JSON.toJSONString(resousePathList);
	}

}
