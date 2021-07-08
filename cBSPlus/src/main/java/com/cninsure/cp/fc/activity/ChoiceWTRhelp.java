package com.cninsure.cp.fc.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.fc.InsureCompanyEntity.WTRenData.WTRenDataEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.ToastUtil;


public class ChoiceWTRhelp  {

	private Activity activity;
	private LayoutInflater inflater;
	private ListView listView;
	private EditText wtperName;
	private Button serchButton;
	public ProgressBar waitProgress;
//	private String [] wtArr;
	private WTRenDataEntity wtrEntity;
	private int choiceWtrPoint=-1;
	private AutoCompleteTextView search;
	private List<WTRenDataEntity> WTRlist,choiceWTRList;
	private int masterId,phoneId,emailId;
	private View wtView;
	
	public ChoiceWTRhelp(Activity activity,String [] wtArr,AutoCompleteTextView search, List<WTRenDataEntity> list, 
			 View wtView,int masterId,int phoneId,int emailId){
		this.activity=activity;
//		this.wtArr=wtArr;
		this.search=search;
		WTRlist=list;
		this.masterId=masterId;
		this.phoneId=phoneId;
		this.emailId=emailId;
		this.wtView=wtView;
		inflater=LayoutInflater.from(activity);
	}
	
	public void showChoiceDialog(){
		Dialog dilog=DialogUtil.getDialogByViewTwoButton(activity, getView(), "选择委托人", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (wtrEntity!=null && wtrEntity.name!=null) {
					search.setText(wtrEntity.name);
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
	public void setdeputeLinkInfo(WTRenDataEntity wtrEntity){
		((EditText)wtView.findViewById(masterId)).setText(wtrEntity.master); //委托方联系人
		((EditText)wtView.findViewById(phoneId)).setText(wtrEntity.phone); //委托方联系人电话
		((EditText)wtView.findViewById(emailId)).setText(wtrEntity.email); //委托方联系人email
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
				setWTRList();
			}
		});
	}
	
//private List<WTRenDataEntity> wtrparams;
	/**获取委托人信息*/
	public void setWTRList() {
		getWtrarr();
//		if (null!=wtArrtemp && wtArrtemp.length>0) {
//			for (int i = 0; i < wtArrtemp.length; i++) {
//				params.add(wtArr[i]);
//			}
//		}
		waitProgress.setVisibility(View.GONE);
//		listView.setAdapter(new ArrayAdapter<String>(activity, R.layout.choice_wtper_item, params));
		 displayWTR();
		setonItemOnclick();
	}
	
	/**显示委托人列表*/
	private void displayWTR(){
		List<Map<String, String>> dateArr=new ArrayList<Map<String,String>>();
		for (WTRenDataEntity wtrEntity:choiceWTRList) {
			Map<String, String> map=new HashMap<String, String>();
			map.put("name", wtrEntity.name);
			map.put("master", wtrEntity.master);
			map.put("phone", wtrEntity.phone);
			dateArr.add(map);
		}
		String [] from=new String[]{"name","master","phone"};
		int [] to=new int[]{R.id.wtspinner_simple_item_wtName,R.id.wtspinner_simple_item_deputeName,R.id.wtspinner_simple_item_deputePhone};
		WTRAdapter adapter=new WTRAdapter(dateArr);
		listView.setAdapter(adapter);
//		listView.setAdapter(new SimpleAdapter(activity, dateArr, R.layout.choice_wtr_simple_item, from, to));
	}

	private void getWtrarr() {
		String sparams=getparams();
		choiceWTRList=new ArrayList<WTRenDataEntity>();
//		wtrparams=new ArrayList<WTRenDataEntity>();
		if (null!=WTRlist && WTRlist.size()>0) {
			for (int i = 0; i < WTRlist.size(); i++) {
				getlikeString(sparams,WTRlist.get(i).name,i);
			}
		}else {
			DialogUtil.getErrDialog(activity, "委托人信息未获取完毕或者未加载完成，请请稍后重试！");
		}
	}

	/***/
	private void getlikeString(String params, String wtrString, int point) {
		boolean flag=true;
		for (int i = 0; i < params.length(); i++) {
			String tempchar=params.substring(i,i+1);
			if (wtrString.indexOf(tempchar)==-1) {
				flag=false;
				break;
			}
		}
		if (flag) {
//			temp.add(wtrString);
			choiceWTRList.add(WTRlist.get(point));
		}
	}

	private String getparams() {
		String params = wtperName.getText().toString();
		if (TextUtils.isEmpty(params)) {
			ToastUtil.showToastLong(activity, "请先输入信息在查询！");
//			DialogUtil.getErrDialog(activity, "未输入信息！").show();
			return "";
		}
		return params;
	}

	private void setonItemOnclick() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int point, long arg3) {
				wtrEntity=choiceWTRList.get(point);
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
		List<Map<String, String>> dateArr;
		
		public WTRAdapter(List<Map<String, String>> dateArr) {
			this.dateArr=dateArr;
		}

		@Override
		public int getCount() {
			return dateArr.size();
		}

		@Override
		public Object getItem(int arg0) {
			return dateArr.get(arg0);
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
			Map<String, String> dataMap=dateArr.get(point);
			name.setText(dataMap.get("name"));
			deputeName.setText(dataMap.get("master"));
			deputePhone.setText(dataMap.get("phone"));
			if (choiceWtrPoint==point) {
				Drawable nav_green=activity.getResources().getDrawable(R.drawable.choice_green32);  
				nav_green.setBounds(0, 0, nav_green.getMinimumWidth(), nav_green.getMinimumHeight());
				name.setCompoundDrawables(null,null,nav_green,null);
			}
			return conView;
		}
		
	}
	
	
}
