package com.cninsure.cp.fc.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.CalculationEntity;
import com.cninsure.cp.entity.CalculationEntity.CalculationData;
import com.cninsure.cp.entity.FenPeiTypeUtil;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.APPRequestModel;
import com.cninsure.cp.entity.fc.CaseRelate;
import com.cninsure.cp.entity.fc.CaseRelateDevote;
import com.cninsure.cp.entity.fc.GGSEntity;
import com.cninsure.cp.entity.fc.GGSEntity.GGSTableData.GGSData;
import com.cninsure.cp.entity.fc.WorkBean;
import com.cninsure.cp.entity.fc.YYBEntity;
import com.cninsure.cp.entity.fc.YYBEntity.YYBtableData.YYBDataEntity;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.LoadingDialog;

public class EditSurveyGgsActivity extends BaseActivity implements OnClickListener {

	private ExpandableListView expandableListView;
	private TextView cancleTv, addGgsTv;
	private LayoutInflater inflater;
	private MyExpandableAdapter expandAdapter;
	private WorkBean workBean;
	/** 检验师列表 */
	private List<CaseRelate> Myrels;
	/** 检验师贡献列表 **/
	private List<CaseRelateDevote> Mydevote;
	private List<NameValuePair> params;
	private LoadingDialog loadDialog;
	private Spinner caseTypeSpinner;
	/** 添加公估师的帮助类 */
	private SurveyChoiceGGShelp choiceGGShelp;
	/**resultCode**/
	public final static int ResultCode=778;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_survey_ggs_activity);
		EventBus.getDefault().register(this);
		initView();
		downLoadAlldept();
	}

	private void initView() {
		choiceGGShelp = new SurveyChoiceGGShelp(this);
		loadDialog = new LoadingDialog(this);
		inflater = LayoutInflater.from(this);
		expandableListView = (ExpandableListView) findViewById(R.id.EDSGGSA_expandablelistview);
		cancleTv = (TextView) findViewById(R.id.EDSGGSA_lTv);
		addGgsTv = (TextView) findViewById(R.id.EDSGGSA_rTv);
		caseTypeSpinner = (Spinner) findViewById(R.id.GGSFPL_fenPeiType);
		setSpinnerAdapter();
		getData();
		expandAdapter = new MyExpandableAdapter();
		expandableListView.setAdapter(expandAdapter);
		cancleTv.setOnClickListener(this);
		addGgsTv.setOnClickListener(this);
	}

	/** spinner设置adapter分配模式数据 **/
	private void setSpinnerAdapter() {
		caseTypeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, FenPeiTypeUtil.getCaseTypes()));
	}

	private void getData() {
		workBean = (WorkBean) getIntent().getSerializableExtra("WorkBean");
		if (workBean != null && workBean.data != null && workBean.data.rels != null) {
			Myrels = workBean.data.rels;
		} else { // 如果公估师列表为空，提示用户联系后台人员
			Dialog dialog = DialogUtil.getAlertOneButton(this, "没有主办信息，请联系管理员！", null);
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					EditSurveyGgsActivity.this.finish();
				}
			});
			dialog.show();
		}
		if (workBean != null && workBean.data != null && workBean.data.devote != null) {
			Mydevote = workBean.data.devote;
		} else {
			Mydevote = new ArrayList<CaseRelateDevote>();
		}
	}

	/** 获取所有营业部 传3请求所有的影营业部信息 **/
	private void downLoadAlldept() {
		List<String> params = new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.getUSER().data.userId);
		params.add("type");
		params.add("4");
		params.add("grade");
		params.add("4");// type=4&grade=4
		params.add("organizationId");
		params.add("3");// 传3请求所有的影营业部信息
		loadDialog.setMessage("努力加载中……").show();
		HttpUtils.requestGet(URLs.DOWNLOAD_DEPT_YYB, params, HttpRequestTool.DOWNLOAD_DEPT_YYBALL);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.EDSGGSA_lTv:// 返回
			this.finish();
			break;

		case R.id.EDSGGSA_rTv:// 新增公估师
			choiceGGShelp.showChoiceDialog();
			break;

		default:
			break;
		}
	}

	/**
	 * 有数据修改就添加到集合中去
	 * 
	 * @param i
	 */
	private void addChangeListener(final TextView textView, final int groupPostion, final int i) {
		textView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

				boolean flag = false;
				for (int j = 0; j < Mydevote.size(); j++) {
					if (Myrels.get(groupPostion).accounts.equals(Mydevote.get(j).account)) {
						flag = true;
						setValueForView(j, textView);
					}
				}
				if (!flag) {
					CaseRelateDevote devoteItem = new CaseRelateDevote();
					devoteItem.account = Myrels.get(groupPostion).accounts;
					Mydevote.add(devoteItem);
					setValueForView(groupPostion, textView);
				}
			}

			private void setValueForView(int j, TextView textView) {
				if (TextUtils.isEmpty(textView.getText().toString())) {
					return;
				}
				switch (i) {
				case 1:
					Mydevote.get(j).isZhuBan = Double.valueOf(textView.getText().toString());// 主办比例
					break;
				case 2:
					Mydevote.get(j).onSite = Integer.valueOf(textView.getText().toString());// 现场处理
					break;
				case 3:
					Mydevote.get(j).dataCollection = Double.valueOf(textView.getText().toString());// 资料收集
					break;
				case 4:
					Mydevote.get(j).aggregate = Double.valueOf(textView.getText().toString());// 资料汇总
					break;
				case 5:
					Mydevote.get(j).xunJiaDingSun = Double.valueOf(textView.getText().toString());// 询价定价
					break;
				case 6:
					Mydevote.get(j).communicateWithDeputer = Integer.valueOf(textView.getText().toString());// 保险人沟通
					break;
				case 7:
					Mydevote.get(j).negotiationWithCustomer = Integer.valueOf(textView.getText().toString());// 谈判确认
					break;
				case 8:
					Mydevote.get(j).writeReport = Double.valueOf(textView.getText().toString());// 撰写装订
					break;
				case 9:
					Mydevote.get(j).modifyRport = Double.valueOf(textView.getText().toString());// 报告校改
					break;

				default:
					break;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	/** 填写的贡献数据格式是否争取，不能为空，百分比的每一列加起来等于100% **/
	private void theDecoteIsRight() {
		if (Mydevote == null || Mydevote.size() == 0) {
			ToastUtil.showToastShort(this, "请先填写数据！");
			return;
		}
		double dataCollectionValue = 0, aggregateValue = 0, xunJiaDingSunValue = 0, writeReportValue = 0, modifyRportValue = 0;
		int communicateWithDeputer=0,onSite=0,negotiationWithCustomer=0;
		for (int i = 0; i < Mydevote.size(); i++) {
			dataCollectionValue += Double.valueOf(Mydevote.get(i).dataCollection);
			aggregateValue += Double.valueOf(Mydevote.get(i).aggregate);
			xunJiaDingSunValue += Double.valueOf(Mydevote.get(i).xunJiaDingSun);
			writeReportValue += Double.valueOf(Mydevote.get(i).writeReport);
			modifyRportValue += Double.valueOf(Mydevote.get(i).modifyRport);
			
			communicateWithDeputer+=Integer.valueOf(Mydevote.get(i).communicateWithDeputer);
			onSite+=Integer.valueOf(Mydevote.get(i).onSite);
			negotiationWithCustomer+=Integer.valueOf(Mydevote.get(i).negotiationWithCustomer);
		}
		if (dataCollectionValue != 100) {
			ToastUtil.showToastLong(this, "请保证所有公估师“资料收集”比例之和为100%！目前总和为“" + dataCollectionValue + "%”");
			return;
		}
		if (aggregateValue != 100) {
			ToastUtil.showToastLong(this, "请保证所有公估师“资料汇总”比例之和为100%！目前总和为“" + aggregateValue + "%”");
			return;
		}
		if (xunJiaDingSunValue != 100) {
			ToastUtil.showToastLong(this, "请保证所有公估师“询价定价”比例之和为100%！目前总和为“" + xunJiaDingSunValue + "%”");
			return;
		}
		if (writeReportValue != 100) {
			ToastUtil.showToastLong(this, "请保证所有公估师“撰写装订”比例之和为100%！目前总和为“" + writeReportValue + "%”");
			return;
		}
		if (modifyRportValue != 100) {
			ToastUtil.showToastLong(this, "请保证所有公估师“报告改写”比例之和为100%！目前总和为“" + modifyRportValue + "%”");
			return;
		}
		if (communicateWithDeputer <= 0) {
			ToastUtil.showToastLong(this, "所有公估师“现场处理”天数总和不能小于0！");
			return;
		}
		if (onSite <= 0) {
			ToastUtil.showToastLong(this, "所有公估师“保险人沟通”天数总和不能小于0！");
			return;
		}
		if (negotiationWithCustomer <= 0) {
			ToastUtil.showToastLong(this, "所有公估师“谈判确认”天数总和不能小于0！");
			return;
		}
		calculation();
	}

	private void calculation() {
		params = new ArrayList<NameValuePair>();
		loadDialog.setMessage("努力加载中……").show();
		APPRequestModel<Map<String, Object>> appre0 = new APPRequestModel<Map<String, Object>>();
		appre0.userToken = AppApplication.getUSER().data.targetOid;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("caseType", caseTypeSpinner.getSelectedItemPosition() + 1);
		map.put("devote", Mydevote);
		Map<String, String> m = new HashMap<String, String>();
		m.put("id", workBean.data.m.id + "");
		map.put("m", m);
		appre0.requestData = map;
		params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre0)));
		HttpUtils.requestPost(URLs.CALCULATION, params, HttpRequestTool.CALCULATION);
	}

	private void mySetText(TextView tv, String value) {
		if (!TextUtils.isEmpty(value)) { // 首先保证显示的数据不为空
			try {
				double dvalue = Double.valueOf(value); // 通过报错保证显示的数据是数字

				if (dvalue == 0) {
					tv.setText("");
				} else {
					tv.setText(value);
				}
			} catch (NumberFormatException e) {
				tv.setText("");
				e.printStackTrace();
			}
		}
	}

	class MyExpandableAdapter extends BaseExpandableListAdapter implements OnClickListener {

		@Override
		public Object getChild(int arg0, int arg1) {
			return Myrels.get(arg1);
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public View getChildView(int groupPostion, int childPostion, boolean arg2, View convertView, ViewGroup groupView) {
			convertView = inflater.inflate(R.layout.ggs_fenpei_layout, null);
			TextView isZhuBan = (TextView) convertView.findViewById(R.id.GGSFPL_isZhuBan);// 主办比例
			TextView calcu = (TextView) convertView.findViewById(R.id.GGSFPL_calcu); // 开始计算
			EditText onSite = (EditText) convertView.findViewById(R.id.GGSFPL_onSite); // 现场处理
			EditText dataCollection = (EditText) convertView.findViewById(R.id.GGSFPL_dataCollection); // 资料收集
			EditText aggregate = (EditText) convertView.findViewById(R.id.GGSFPL_aggregate); // 资料汇总
			EditText xunJiaDingSun = (EditText) convertView.findViewById(R.id.GGSFPL_xunJiaDingSun); // 询价定价
			EditText communicateWithDeputer = (EditText) convertView.findViewById(R.id.GGSFPL_communicateWithDeputer); // 保险人沟通
			EditText negotiationWithCustomer = (EditText) convertView.findViewById(R.id.GGSFPL_negotiationWithCustomer); // 谈判确认
			EditText writeReport = (EditText) convertView.findViewById(R.id.GGSFPL_writeReport); // 撰写装订
			EditText modifyRport = (EditText) convertView.findViewById(R.id.GGSFPL_modifyRport); // 报告校改

			/** 回显贡献数据 **/
			if (Mydevote != null && Mydevote.size() > 0) {
				for (CaseRelateDevote tempDevote : Mydevote) {
					if (tempDevote.account.equals(Myrels.get(groupPostion).accounts)) {
						if (Myrels.get(groupPostion).relType.equals("主办")) {
							mySetText(isZhuBan, "100.0");// 主办比例
						} else {
							mySetText(isZhuBan, "0");// 主办比例
						}
						mySetText(onSite, tempDevote.onSite + ""); // 现场处理
						mySetText(dataCollection, tempDevote.dataCollection + ""); // 资料收集
						mySetText(aggregate, tempDevote.aggregate + ""); // 资料汇总
						mySetText(xunJiaDingSun, tempDevote.xunJiaDingSun + ""); // 询价定价
						mySetText(communicateWithDeputer, tempDevote.communicateWithDeputer + ""); // 保险人沟通
						mySetText(negotiationWithCustomer, tempDevote.negotiationWithCustomer + ""); // 谈判确认
						mySetText(writeReport, tempDevote.writeReport + ""); // 撰写装订
						mySetText(modifyRport, tempDevote.modifyRport + ""); // 报告校改
						break;
					}
				}
			}

			addChangeListener(isZhuBan, groupPostion, 1);
			addChangeListener(onSite, groupPostion, 2);
			addChangeListener(dataCollection, groupPostion, 3);
			addChangeListener(aggregate, groupPostion, 4);
			addChangeListener(xunJiaDingSun, groupPostion, 5);
			addChangeListener(communicateWithDeputer, groupPostion, 6);
			addChangeListener(negotiationWithCustomer, groupPostion, 7);
			addChangeListener(writeReport, groupPostion, 8);
			addChangeListener(modifyRport, groupPostion, 9);
			calcu.setOnClickListener(this);
			return convertView;
		}

		@Override
		public void onClick(View arg0) {
			// 检查填写数据是否满足要求
			theDecoteIsRight();
		}

		@Override
		public int getChildrenCount(int arg0) {
			return 1;
		}

		@Override
		public Object getGroup(int arg0) {
			return Myrels.get(arg0);
		}

		@Override
		public int getGroupCount() {
			return Myrels.size();
		}

		@Override
		public long getGroupId(int arg0) {
			return arg0;
		}

		@Override
		public View getGroupView(int groupPostion, boolean arg1, View groupView, ViewGroup arg3) {
			groupView = inflater.inflate(R.layout.survey_ggs_edit_expandlelistview_item, null);
			CaseRelate relsTemp = Myrels.get(groupPostion);
			((TextView) groupView.findViewById(R.id.sggsedAc_nameAndDept)).setText(relsTemp.userName + " | " + relsTemp.homeInstitution);// 公估师名称及归属部门
			try {
				((TextView) groupView.findViewById(R.id.sggsedAc_zxBan)).setText(relsTemp.relType.subSequence(0, relsTemp.relType.length() - 1));// 主协办
			} catch (Exception e) {
				((TextView) groupView.findViewById(R.id.sggsedAc_zxBan)).setText("协");
				e.printStackTrace();
			}
			if (TextUtils.isEmpty(relsTemp.contributionRatio)) {
				((TextView) groupView.findViewById(R.id.sggsedAc_gxbl)).setText("0");// 贡献比例
			}else {
				((TextView) groupView.findViewById(R.id.sggsedAc_gxbl)).setText(relsTemp.contributionRatio);// 贡献比例
			}
			return groupView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			return false;
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventcal(List<NameValuePair> value) {
		int rcode = Integer.valueOf(value.get(0).getName());
		if (rcode == HttpRequestTool.CALCULATION || rcode == HttpRequestTool.DOWNLOAD_DEPT_YYBALL) {
			loadDialog.dismiss();
		}
		if (rcode == HttpRequestTool.GET_GGS_LIST) {
			loadDialog.dismiss();
			choiceGGShelp.waitProgress.setVisibility(View.INVISIBLE);
		}
		switch (CheckHttpResult.checkList(value, this)) {

		case HttpRequestTool.CALCULATION:
			CalculationEntity calcuData = JSON.parseObject(value.get(0).getValue(), CalculationEntity.class);
			getggsTotal(calcuData);
			break;

		case HttpRequestTool.DOWNLOAD_DEPT_YYBALL:
			getALLYYBInfo(value.get(0).getValue());
			break;

		case HttpRequestTool.GET_GGS_LIST:
			choiceGGShelp.setGGSList(value.get(0).getValue());
			break;

		default:
			break;
		}
	}

	/** 获取公估师比例添加到rels中 */
	private void getggsTotal(CalculationEntity calcuData) {
		if (calcuData.code != 0) {
			DialogUtil.getAlertOneButton(this, "计算失败！", null).show();
		} else {
			for (CalculationData caldata : calcuData.data) {
				for (int i = 0; i < Myrels.size(); i++) {
					CaseRelate ggs = Myrels.get(i);
					if (caldata.account.equals(ggs.accounts)) {
						Myrels.get(i).contributionRatio = caldata.total + "";
						break;
					}
				}
			}
			setResult();
		}
		expandAdapter.notifyDataSetChanged();// 刷新数据
	}

	private YYBEntity YYBALLdata;
	private List<YYBDataEntity> YYBAllList;

	/** 获取所有的营业部信息 并传递到帮助类里面 **/
	private void getALLYYBInfo(String value) {
		YYBALLdata = JSON.parseObject(value, YYBEntity.class);
		if (null != YYBALLdata && null != YYBALLdata.tableData && null != YYBALLdata.tableData.data) {
			YYBAllList = new ArrayList<YYBDataEntity>();
			for (int i = 0; i < YYBALLdata.tableData.data.size(); i++) {
				YYBAllList.add(YYBALLdata.tableData.data.get(i));
			}
		}
		choiceGGShelp.setValue(YYBAllList);
	}

	public void setResult(){
		Intent intent=new Intent(); //("ResultMyrels", Myrels)
		intent.putExtra("ResultMyrels", (Serializable) Myrels);
		setResult(ResultCode, intent);
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void addGGS(GGSEntity choiceGGS) {
		String ggsNames = "";
		for (GGSData ggs : choiceGGS.tableData.data) {
			boolean flag = true;
			for (CaseRelate myrel : Myrels) {
				if (ggs.loginName.equals(myrel.accounts)) { // 如果公估师已存在，记录公估师名称，并修改标记，跳出本次循环
					ggsNames += ggs.name + "，";
					flag = false;
					break;
				}
			}
			if (flag) {// 如果公估师不在列表中则添加
				CaseRelate temprels = new CaseRelate();
				temprels.id = (ggs.id);
				temprels.accounts = ggs.loginName;
				temprels.userName = ggs.name;
				temprels.homeInstitution = ggs.organizationSelfName;
				temprels.dispatchStatus = "已调度";
				temprels.relType = "协办";
				Myrels.add(temprels);
			}
		}
		if (TextUtils.isEmpty(ggsNames)) {// 如果选择的公估师有已在列表中的，提示用户！
			DialogUtil.getAlertOneButton(this, "公估师" + ggsNames + "已在列表中未追加！", null);
		}
		expandAdapter.notifyDataSetChanged();
	}
}
