package com.cninsure.cp.activity.yjx;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.GGSEntity;
import com.cninsure.cp.entity.fc.GGSEntity.GGSTableData.GGSData;
import com.cninsure.cp.entity.yjx.EYYBListEntity.TableData.EYYBDataEntity;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;

public class ChoiceGGShelp {

	private List<EYYBDataEntity> YYBAllList;
	private Context context;
	private LayoutInflater inflater;
	private List<String> yybNameList;
	private Spinner deptSpinner;
	private ListView listView;
	private EditText ggsName;
	private long deptid = 0;
	private Button serchButton;
	public GGSData choiceGGS;
	private GGSEntity ggsobj;
	public ProgressBar waitProgress;
	private Dialog dialog;
	private DialogInterface.OnClickListener listener;
	private ChoiceGgsAdapter adapter;

	@SuppressWarnings("unused")
	private ChoiceGGShelp(){}
	
	public ChoiceGGShelp(Context activity,DialogInterface.OnClickListener listener) {
		this.context = activity;
		this.listener = listener ;
		inflater = LayoutInflater.from(activity);
	}

	public void setValue(List<EYYBDataEntity> YYBAllList) {
		this.YYBAllList = YYBAllList;
	}

	public void showChoiceDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("选择公估师")
				.setView(getView());
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", listener);
		dialog = builder.create();
		dialog.show();
	}

	private View getView() {
		View view = inflater.inflate(R.layout.choice_ggs_dialog_view, null);
		listView = (ListView) view.findViewById(R.id.ChoiceGGs_lisetView);
		deptSpinner = (Spinner) view.findViewById(R.id.ChoiceGGs_SPINNYYB);
		ggsName = (EditText) view.findViewById(R.id.ChoiceGGs_name);
		ggsName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
		serchButton = (Button) view.findViewById(R.id.ChoiceGGs_search);
		waitProgress = (ProgressBar) view.findViewById(R.id.ChoiceGGs_progressBar1);
		setspinner();
		setSearch();
		return view;
	}

	/** 开始新的搜索前会清空之前的选择 */
	private void setSearch() {
		serchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (choiceGGS != null) {
					choiceGGS = new GGSData();// 开始新的搜索前清空之前的选择
				}
				waitProgress.setVisibility(View.VISIBLE);
				getGGSInfo();
			}
		});
	}

	private void getGGSInfo() {
		List<String> params = new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.getUSER().data.userId);
		params.add("organizationId");
		params.add(deptid + "");
		params.add("name");
		params.add(ggsName.getText().toString());
		params.add("size");
		params.add("9999");
//		params.add("targetRoles");
//		params.add(",8,");//
		HttpUtils.requestGet(URLs.GET_GGS_LIST, params, HttpRequestTool.GET_GGS_LIST);
	}

	private void setspinner() {
		if (YYBAllList != null) {
			int countcu = -1;
			yybNameList = new ArrayList<String>();
			yybNameList.add("");
			/** 增加一个空字符串，避免一定会选中营业部，减低可操作性，记得在取ID的时候加一（+1） */
			int deputId = AppApplication.getUSER().data.organizationId;
			for (int i = 0; i < YYBAllList.size(); i++) {
				yybNameList.add(YYBAllList.get(i).name);
				if (YYBAllList.get(i).id == deputId) {
					countcu = i;
				}
			}
			deptSpinner.setAdapter(new ArrayAdapter<String>(context, R.layout.spinner_item, yybNameList));
			if (countcu > -1) {
				deptSpinner.setSelection(countcu + 1);
			}
		}
		deptSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2 > 0) {
					deptid = YYBAllList.get(arg2 - 1).id;
				}else {
					deptid = 1;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public void setGGSList(String value) {
		ggsobj = JSON.parseObject(value, GGSEntity.class);
		List<String> params = new ArrayList<String>();
		if (null != ggsobj && null != ggsobj.tableData && null != ggsobj.tableData.data) {
			for (int i = 0; i < ggsobj.tableData.data.size(); i++) {
				waitProgress.setVisibility(View.GONE);
				params.add(ggsobj.tableData.data.get(i).organizationSelfName + "\t" + ggsobj.tableData.data.get(i).name);
			}
		}
		adapter = new ChoiceGgsAdapter();
		listView.setAdapter(adapter);
		choiceGGS = new GGSData();
		setonItemOnclick();
	}

	private void setonItemOnclick() {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int point, long arg3) {
				choiceGGS = (ggsobj.tableData.data.get(point));
				ToastUtil.showToastShort(context, "已选择：" + ggsobj.tableData.data.get(point).name);
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	private class ChoiceGgsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if (null != ggsobj && null != ggsobj.tableData && null != ggsobj.tableData.data) {
				return ggsobj.tableData.data.size();
			}else {
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			if (null != ggsobj && null != ggsobj.tableData && null != ggsobj.tableData.data) {
				return ggsobj.tableData.data.get(arg0);
			}else {
				return null;
			}
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View conView, ViewGroup arg2) {
			
			ViewHodler vh = new ViewHodler();
			if (conView==null) {
				conView = inflater.inflate(R.layout.choice_ggs_item, null);
				vh = new ViewHodler();
				vh.nameTv = (TextView) conView.findViewById(R.id.spinner_item_textone);
				conView.setTag(vh);
			}else {
				vh = (ViewHodler) conView.getTag();
			}
			//下面开始显示数据
			GGSData tempData = ggsobj.tableData.data.get(arg0);
			if (tempData!=null && !TextUtils.isEmpty(tempData.name)) {
				((TextView) vh.nameTv).setText(tempData.name);
			}
			if (choiceGGS != null && choiceGGS.id==tempData.id) {
				((TextView) vh.nameTv).setCompoundDrawables(null, null, vh.nav_green, null);
			}else {
				((TextView) vh.nameTv).setCompoundDrawables(null, null, vh.nav_hui, null);
			}
			return conView;
		}
		
		private class ViewHodler{
			TextView nameTv;
			Drawable nav_green = context.getResources().getDrawable(R.drawable.choice_green32);
			Drawable nav_hui = context.getResources().getDrawable(R.drawable.choice_hui32);
			
			public ViewHodler(){
				nav_green.setBounds(0, 0, nav_green.getMinimumWidth(), nav_green.getMinimumHeight());
				nav_hui.setBounds(0, 0, nav_hui.getMinimumWidth(), nav_hui.getMinimumHeight());
			}
		}
		
	}

}
