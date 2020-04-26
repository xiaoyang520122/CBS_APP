package com.cninsure.cp.fc.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
import com.cninsure.cp.entity.fc.GGSEntity.GGSTableData;
import com.cninsure.cp.entity.fc.YYBEntity.YYBtableData.YYBDataEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;


public class WaterChoiceGGShelp  {

	private List<YYBDataEntity> YYBAllList;
	private WaterCaseInputActivity activity;
	private LayoutInflater inflater;
	private List<String> yybNameList;
	private Spinner deptSpinner;
	private ListView listView;
	private EditText ggsName;
	private long deptid=0;
	private Button serchButton;
	private GGSEntity ggsobj,choiceGGS;
	public ProgressBar waitProgress;
	
	public WaterChoiceGGShelp(WaterCaseInputActivity activity){
		this.activity=activity;
		inflater=LayoutInflater.from(activity);
	}
	
	public void setValue(List<YYBDataEntity> YYBAllList){
		this.YYBAllList=YYBAllList;
	}
	
	
	public void showChoiceDialog(){
		DialogUtil.getDialogByViewOnlistener(activity, getView(), "选择公估师", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.addGGS(choiceGGS);
			}
		}).show();
	}

	private View getView() {
		View view =inflater.inflate(R.layout.choice_ggs_dialog_view, null);
		listView=(ListView) view.findViewById(R.id.ChoiceGGs_lisetView);
		deptSpinner=(Spinner) view.findViewById(R.id.ChoiceGGs_SPINNYYB);
		ggsName=(EditText) view.findViewById(R.id.ChoiceGGs_name);
		ggsName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
		serchButton=(Button) view.findViewById(R.id.ChoiceGGs_search);
		waitProgress=(ProgressBar) view.findViewById(R.id.ChoiceGGs_progressBar1);
		setspinner();
		setSearch();
		return view;
	}
	
	/**开始新的搜索前会清空之前的选择*/
	private void setSearch() {
		serchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (choiceGGS!=null) {
					choiceGGS.tableData.data=new ArrayList<GGSEntity.GGSTableData.GGSData>();//开始新的搜索前清空之前的选择
				}
				waitProgress.setVisibility(View.VISIBLE);
				getGGSInfo();
			}
		});
	}
	
	private void getGGSInfo(){
		List<String> params=new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.USER.data.userId);
		params.add("organizationId");
		params.add(deptid+"");
		params.add("name");
		params.add(ggsName.getText().toString());
		params.add("size");
		params.add("9999");
		params.add("targetRoles");
		params.add(",8,");//
		HttpUtils.requestGet(URLs.GET_GGS_LIST, params, HttpRequestTool.GET_GGS_LIST);
	}

	private void setspinner(){
		if (YYBAllList!=null) {
			int countcu=-1;
			yybNameList=new ArrayList<String>();
			yybNameList.add("");/**增加一个空字符串，避免一定会选中营业部，减低可操作性，记得在取ID的时候加一（+1）*/
			int deputId=AppApplication.USER.data.organizationId;
			for (int i = 0; i < YYBAllList.size(); i++) {
				yybNameList.add(YYBAllList.get(i).name);
				if (YYBAllList.get(i).id==deputId) {
					countcu=i;
				}
			}
			deptSpinner.setAdapter(new ArrayAdapter<String>(activity, R.layout.spinner_item, yybNameList));
			if (countcu>-1) {
				deptSpinner.setSelection(countcu+1);
			}
		}
		deptSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2>0) {
					deptid = YYBAllList.get(arg2-1).id;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public void setGGSList(String value) {
		ggsobj=JSON.parseObject(value, GGSEntity.class);
		List<String> params=new ArrayList<String>();
		if (null!=ggsobj  && null!=ggsobj.tableData  &&null!=ggsobj.tableData.data) {
			for (int i = 0; i < ggsobj.tableData.data.size(); i++) {
				waitProgress.setVisibility(View.GONE);
				params.add(ggsobj.tableData.data.get(i).organizationSelfName+"\t"+ggsobj.tableData.data.get(i).name);
			}
		}
		listView.setAdapter(new ArrayAdapter<String>(activity, R.layout.choice_ggs_item, params));
		choiceGGS = new GGSEntity();
		choiceGGS.tableData=new GGSTableData();
		choiceGGS.tableData.data=new ArrayList<GGSEntity.GGSTableData.GGSData>();
		setonItemOnclick();
	}

	private void setonItemOnclick() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int point, long arg3) {
				Drawable nav_green=activity.getResources().getDrawable(R.drawable.choice_green32);  
				nav_green.setBounds(0, 0, nav_green.getMinimumWidth(), nav_green.getMinimumHeight());
				
				Drawable nav_hui=activity.getResources().getDrawable(R.drawable.choice_hui32);  
				nav_hui.setBounds(0, 0, nav_hui.getMinimumWidth(), nav_hui.getMinimumHeight());
				
				if (view.getTag() == null) {// 第一次点击默认为选中
					view.setTag(true);
					choiceGGS.tableData.data.add(ggsobj.tableData.data.get(point));
					((TextView)view).setCompoundDrawables(null,null,nav_green,null);
					ToastUtil.showToastShort(activity, "已选择："+ggsobj.tableData.data.get(point).name);
				}else {
					if ((Boolean) view.getTag()) {//之前选中就取消
						view.setTag(false);
						choiceGGS.tableData.data.remove(ggsobj.tableData.data.get(point));
						((TextView)view).setCompoundDrawables(null,null,nav_hui,null);
						ToastUtil.showToastShort(activity, "取消选择："+ggsobj.tableData.data.get(point).name);
					} else {	//之前取消就选中
						view.setTag(true);
						choiceGGS.tableData.data.add(ggsobj.tableData.data.get(point));
						((TextView)view).setCompoundDrawables(null,null,nav_green,null);
						ToastUtil.showToastShort(activity, "已选择："+ggsobj.tableData.data.get(point).name);
					}
				}
				
			}
		});
	}
	
	
}
