package com.cninsure.cp.activity.yjx;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.yjx.EInsureCompanyEntity;
import com.cninsure.cp.entity.yjx.EInsureCompanyEntity.TableData.EWTRenDataEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;

/**
 * 选择委托人帮助类
 * @author Administrator
 *
 */
public class YjxChoiceWTRhelp  {

	private Activity activity;
	private LayoutInflater inflater;
	private ListView listView;
	public EditText wtperName;
	private Button serchButton;
	public ProgressBar waitProgress;
	/**选择的委托人对象*/
	private EWTRenDataEntity wtrEntity;
	private int choiceWtrPoint=-1;
	public List<EWTRenDataEntity> WTRlist=new ArrayList<EWTRenDataEntity>();;
	private View wtView;
	
	public YjxChoiceWTRhelp(Activity activity, View wtView){
		this.activity=activity;
		this.wtView=wtView;
		inflater=LayoutInflater.from(activity);
	}
	
	public void showChoiceDialog(){
		Dialog dilog=DialogUtil.getDialogByViewOnlistener(activity, getView(), "选择委托人", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (wtrEntity!=null && wtrEntity.name!=null) {
						setdeputeLinkInfo(wtrEntity);
					}
				}
		});
		setDismissListener(dilog);
		dilog.show(); //
	}
	
	private void setDismissListener(Dialog dilog) {
		dilog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				choiceWtrPoint=-1;
				wtrEntity=null;
			}
		});
	}

	/**回写委托人联系人，电话，Email信息**/
	public void setdeputeLinkInfo(EWTRenDataEntity wtrEntity){
		((TextView)wtView.findViewById(R.id.YJXBAINP_wtName)).setText(wtrEntity.name); //委托人名称
//		((EditText)wtView.findViewById(R.id.YJXBAINP_wtCotact)).setText(wtrEntity.master); //委托方联系人
//		((EditText)wtView.findViewById(R.id.YJXBAINP_wtContactTel)).setText(wtrEntity.phone); //委托方联系人电话
//		((EditText)wtView.findViewById(R.id.)).setText(wtrEntity.email); //委托方联系人email
	}

	private View getView() {
		View view =inflater.inflate(R.layout.choice_wtren_dialog_view, null);
		listView=(ListView) view.findViewById(R.id.ChoiceWTR_lisetView);
		wtperName=(EditText) view.findViewById(R.id.ChoiceWTR_name);
		 //获取焦点，解决无法编辑
		wtperName.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (v.getId()) {    
		        case R.id.ChoiceWTR_name:    
		            v.getParent().requestDisallowInterceptTouchEvent(true);    
		            switch (event.getAction()) {    
		                case MotionEvent.ACTION_UP:    
		                    v.getParent().requestDisallowInterceptTouchEvent(false);    
		                    break;    
		            }    
		    }
				return false;
			}
		});    
	    
		wtperName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
//		wtperName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
		serchButton=(Button) view.findViewById(R.id.ChoiceWTR_search);
		waitProgress=(ProgressBar) view.findViewById(R.id.ChoiceWTR_progressBar1);
		setSearch();
		return view;
	}
	
	/**开始新的搜索前会清空之前的选择*/
	private void setSearch() {
		serchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				choiceWtrPoint=-1;
				wtrEntity=null;
				waitProgress.setVisibility(View.VISIBLE);
				getWTRList();
			}
		});
	}
	
	/**获取委托人信息*/
	public void getWTRList() {
		getWtrArr();
//		waitProgress.setVisibility(View.GONE);
	}
	
	/**显示委托人列表*/
	public void displayWTR(String values){
		try {
			EInsureCompanyEntity wtRenData= JSON.parseObject(values, EInsureCompanyEntity.class);
			WTRlist = wtRenData.tableData.data;
			WTRAdapter adapter=new WTRAdapter();
			listView.setAdapter(adapter);
			setonItemOnclick();
		} catch (Exception e) {
			ToastUtil.showToastShort(activity, "未获取到委托人！");
			e.printStackTrace();
		}
		waitProgress.setVisibility(View.GONE);
	}
	
	public EWTRenDataEntity getWtrEn(String name){
		if (WTRlist!=null) {
			for (EWTRenDataEntity wtrEnTem:WTRlist) {
				if (wtrEnTem.name.equals(name)) {
					return wtrEnTem;
				}
			}
			return null;
		}else {
			return null;
		}
	}

	/**获取输入委托人*/
	private void getWtrArr() {
		String sparams=getparams();
		getWtRen(sparams);
	}
	
	/** 获取调度任务的作业信息 */
	/**获取委托人信息*/
	private void getWtRen(String namePatams){
		List<String> params=new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.USER.data.userId+"");
		
		params.add("size");
		params.add("10000");
		
		params.add("start");
		params.add("0");
		
		params.add("name");
		params.add(namePatams);
		
		params.add("type");
		params.add("3");
		
		params.add("isLeaf");
		params.add("-1");
		
		HttpUtils.requestGet(URLs.GET_E_WT_REN, params, HttpRequestTool.GET_E_WT_REN);
	}


	private String getparams() {
		String params = wtperName.getText().toString();
		if (TextUtils.isEmpty(params)) {
			ToastUtil.showToastLong(activity, "请先输入信息后查询！");
//			DialogUtil.getErrDialog(activity, "未输入信息！").show();
			return "";
		}
		return params;
	}

	private void setonItemOnclick() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int point, long arg3) {
				wtrEntity=WTRlist.get(point);
				choiceWtrPoint=point;
				
				Drawable nav_green=activity.getResources().getDrawable(R.drawable.choice_green32);  
				nav_green.setBounds(0, 0, nav_green.getMinimumWidth(), nav_green.getMinimumHeight());
				
				Drawable nav_hui=activity.getResources().getDrawable(R.drawable.choice_hui32);  
				nav_hui.setBounds(0, 0, nav_hui.getMinimumWidth(), nav_hui.getMinimumHeight());
				for (int i = 0; i < arg0.getCount(); i++) {
					if (null!=arg0.getChildAt(i) ) {
						((TextView)(arg0.getChildAt(i).findViewById(R.id.wtspinner_simple_item_wtName))).setCompoundDrawables(null,null,nav_hui,null);
					}
				}
				((TextView)(view.findViewById(R.id.wtspinner_simple_item_wtName))).setCompoundDrawables(null,null,nav_green,null);
			}
		});
	}
	
	class WTRAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return WTRlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return WTRlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int point, View conView, ViewGroup arg2) {
			//R.layout.choice_wtr_simple_item
			conView=LayoutInflater.from(activity).inflate(R.layout.choice_wtr_simple_item, null);
			TextView name=(TextView) conView.findViewById(R.id.wtspinner_simple_item_wtName);
			TextView deputeName=(TextView) conView.findViewById(R.id.wtspinner_simple_item_deputeName);
			TextView deputePhone=(TextView) conView.findViewById(R.id.wtspinner_simple_item_deputePhone);
			EWTRenDataEntity dataMap=WTRlist.get(point);
			name.setText(dataMap.name);
//			deputeName.setText(dataMap.master);
//			deputePhone.setText(dataMap.phone);
			deputeName.setVisibility(View.GONE);
			deputePhone.setVisibility(View.GONE);
			if (choiceWtrPoint==point) {
				Drawable nav_green=activity.getResources().getDrawable(R.drawable.choice_green32);  
				nav_green.setBounds(0, 0, nav_green.getMinimumWidth(), nav_green.getMinimumHeight());
				name.setCompoundDrawables(null,null,nav_green,null);
			}
			return conView;
		}
		
	}
	
	
}
