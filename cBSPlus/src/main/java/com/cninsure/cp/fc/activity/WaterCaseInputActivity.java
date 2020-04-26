package com.cninsure.cp.fc.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.DictEntity;
import com.cninsure.cp.entity.PagedRequest;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.APPRequestModel;
import com.cninsure.cp.entity.fc.CaseBean;
import com.cninsure.cp.entity.fc.CaseDepute;
import com.cninsure.cp.entity.fc.CaseLaboratorian;
import com.cninsure.cp.entity.fc.CaseManage;
import com.cninsure.cp.entity.fc.CaseRelate;
import com.cninsure.cp.entity.fc.CaseShip;
import com.cninsure.cp.entity.fc.DZXXDictEntity;
import com.cninsure.cp.entity.fc.FCCaseTypeEntity;
import com.cninsure.cp.entity.fc.GGSEntity;
import com.cninsure.cp.entity.fc.GGSEntity.GGSTableData;
import com.cninsure.cp.entity.fc.GGSEntity.GGSTableData.GGSData;
import com.cninsure.cp.entity.fc.InsureCompanyEntity;
import com.cninsure.cp.entity.fc.YYBEntity;
import com.cninsure.cp.entity.fc.YYBEntity.YYBtableData.YYBDataEntity;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.PatternUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.LoadingDialog;

public class WaterCaseInputActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnPageChangeListener {

	/***
risk_type 险种大类
rev_case_type 接案方式
feicheBaoxianType 水险险种
is_yd 是否异地
business_type 业务类型
大灾选项 需要另外一个接口
险种分类 --
case_industry 所属行业
depute_sf 委托人身份
urgency_level 紧急程度
danger_res 出险原因
loss_currency 报损币别
ship_type 船舶类型
ship_dw 船舶吨位
mainLoss_type 主要受损类型mainLossType
	 */
	String[] ZDparamsStr=new String[]{"risk_type","rev_case_type","feicheBaoxianType","is_yd","deputeItem",
			"business_type","case_industry","depute_sf","urgency_level","danger_res","loss_currency","ship_type","ship_dw"
			,"mainLoss_type"};
	
	/**需要设置onclick事件的ID集合*/
	private int[] onclickIDs=new int[]{R.id.WCI_Text_deputeDate,R.id.WCI_Text_revCaseDate,R.id.WCI_text_preCheckDate,
			R.id.WCI_text_dangerPeriod};

	private View creatCaseView,wtView,otherView,relationalInputView,LaboratorianDisplayView,otherShpView;
	private TextView actionTV1, actionTV2, actionTV3;
	private ViewPager vpager;
	private List<View> viewlist;
	private PagerAdapter pagerAdapter;
	private LoadingDialog loadDialog;
	private RadioGroup radgrup;
	private LayoutInflater inflater;
	private Button submitButton;
	private List<NameValuePair> params;
	private DictEntity dictEntity;
	/**险种分类：**/
	private Spinner XZSpinner;
	/**次委托方详细信息**/
	private List<CaseDepute> depsData;
	/**关联方详细信息**/
	private List<CaseLaboratorian> labsData;
	/**第三方船舶信息*/
	private List<CaseShip> caseShipList;
	/**显示次委托方，关联关系方和公估师你列表的LinearLayout*/
	private LinearLayout otherShipLayout,RelationalsLayout,LaboratorianLayout,ggslLayout;
	/**委托人信息*/
	private AutoCompleteTextView search;
	private InsureCompanyEntity wtRenData;
	private YYBEntity YYBdata,YYBALLdata;
	/****显示的时候会在字符串数组中加一个空字符串，避免显示时有值，同时注意取值时需要在YYBDataList中获取ID时加一（+1）**/
	private List<YYBDataEntity> YYBDataList;
	private List<YYBDataEntity> YYBAllList;
	private WaterChoiceGGShelp choiceGGShelp;
	private ChoiceWTRhelp choiceWTRhelp;
	private GGSEntity chickGGS;
	/**提交数据封装类**/
	private APPRequestModel<CaseBean> appRequestData;
	/**险种分类**/
	private List<String> caseTypeList;
	/***大灾选项数据**/
	private DZXXDictEntity dZXXdataEntity;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activty_water_fc_case_input);
		EventBus.getDefault().register(this);
		initaction();
		initView() ;
		initRequestData();
		downloadDict();
	}

	/**初始化请求对象**/
	private void initRequestData() {
		appRequestData=new APPRequestModel<CaseBean>();
		appRequestData.requestData=new CaseBean();
		appRequestData.requestData.casem=new CaseManage();
	}


	private void downloadDict() {
		params = new ArrayList<NameValuePair>();
		loadDialog.setMessage("努力加载中……").show();
		APPRequestModel<String[]> appre = new APPRequestModel<String[]>();
		appre.userToken = AppApplication.USER.data.targetOid;
		appre.requestData = ZDparamsStr;
		params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre)));
		HttpUtils.requestPost(URLs.FC_GET_DICT_LIST, params, HttpRequestTool.FC_GET_DICT_LIST);
//		downloadDZDict();
		getWtRen();
	}
	
	
	/**获取委托人信息*/
	private void getWtRen(){
		params = new ArrayList<NameValuePair>();
		@SuppressWarnings("rawtypes")
		APPRequestModel<PagedRequest> appre = new APPRequestModel<PagedRequest>();
		appre.userToken = AppApplication.USER.data.targetOid;
		@SuppressWarnings("rawtypes")
		PagedRequest<Map> requestData = new PagedRequest<Map>();
		requestData.pageNo = 1;
		requestData.pageSize = 10000;
		Map<String, String> map=new HashMap<String, String>(1);
		map.put("name", "%");
		requestData.data = map;
		appre.requestData = requestData;
		params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre)));
		HttpUtils.requestPost(URLs.GET_WT_REN, params, HttpRequestTool.GET_WT_REN);
	}
	
	/**获取营业部信息*/
	private void downloadDeptInfo() {
		List<String> params=new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.USER.data.userId);
		params.add("type");
		params.add("4");
		params.add("grade");
		params.add("4");//type=4&grade=4
		downLoadAlldept();
		params.add("organizationLoginId");
		params.add(AppApplication.USER.data.organizationLoginId+"");
		HttpUtils.requestGet(URLs.DOWNLOAD_DEPT_YYB, params, HttpRequestTool.DOWNLOAD_DEPT_YYB);
	}
	
	/**获取所有营业部 传3请求所有的影营业部信息**/
	private void downLoadAlldept(){
		List<String> params=new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.USER.data.userId);
		params.add("type");
		params.add("4");
		params.add("grade");
		params.add("4");//type=4&grade=4
		params.add("organizationId");
		params.add("3");//传3请求所有的影营业部信息
		
		HttpUtils.requestGet(URLs.DOWNLOAD_DEPT_YYB, params, HttpRequestTool.DOWNLOAD_DEPT_YYBALL);
	}

	private void initaction() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();
	}

	private void setAction() {
		actionTV2.setText("非车接报案录入");
		actionTV3.setText("提交");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				HintOut();
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SubmitCase(1);
			}
		});
	}
	
	
	private void initView() {
		choiceGGShelp=new WaterChoiceGGShelp(this);
		loadDialog=new LoadingDialog(this);
		vpager=(ViewPager) findViewById(R.id.ACWAFCINPUT_viewpager);
		radgrup = (RadioGroup) findViewById(R.id.ACWAFCINPUT_BtnG);
		submitButton=(Button) findViewById(R.id.ACWAFCINPUT_button);
		if (submitButton!=null) {
			submitButton.setOnClickListener(this);
		}else {
			ToastUtil.showToastLong(this, "submitButton居然为空！");
		}
		radgrup.setOnCheckedChangeListener(this);
		vpager.setOnPageChangeListener(this);
		inflater=LayoutInflater.from(this);
		viewlist=new ArrayList<View>();
//		spinnerView1=new ArrayList<Spinner>(11);
		creatCaseView=inflater.inflate(R.layout.water_fc_take_case_creatinfo, null);
		wtView=inflater.inflate(R.layout.water_fc_input_case_summary, null);
		otherView=inflater.inflate(R.layout.water_fc_other_info, null);
		LaboratorianLayout=(LinearLayout) otherView.findViewById(R.id.WCI_Edit_glgxfInfoLinear);
		RelationalsLayout=(LinearLayout) otherView.findViewById(R.id.WCI_Edit_cwtfInfoLinear);
		otherShipLayout=(LinearLayout) wtView.findViewById(R.id.WCI_Linear_othership);
		ggslLayout=(LinearLayout) otherView.findViewById(R.id.WCI_Text_GGSInfoLinear);
		depsData=new ArrayList<CaseDepute>();
		labsData=new ArrayList<CaseLaboratorian>();
		caseShipList=new ArrayList<CaseShip>();
		otherView.findViewById(R.id.WCI_Edit_cwtfInfo).setOnClickListener(this);
		otherView.findViewById(R.id.WCI_Edit_glgxfInfo).setOnClickListener(this);
		otherView.findViewById(R.id.WCI_Text_GGSInfo).setOnClickListener(this);
		wtView.findViewById(R.id.WCI_text_othership).setOnClickListener(this);
		/**设置邮件输入格式,错误是提示用户*/
		PatternUtil.setEmailInput((EditText)creatCaseView.findViewById(R.id.WCI_Edit_deputeLinkEmail));
		
		search = (AutoCompleteTextView) creatCaseView.findViewById(R.id.WCI_deputePer);  
//		((AutoCompleteTextView)creatCaseView.findViewById(R.id.WCI_deputePer)).setError("委托人必须为从自动弹出的下拉列表中选择，否则录入可能会失败！");//设置委托人录入的规则
//		linearconnect = (LinearLayout) otherView.findViewById(R.id.);
//		linearggs = (LinearLayout) otherView.findViewById(R.id.);
		setDataCheckOnclicEvent();
		viewlist.add(creatCaseView);
		viewlist.add(wtView);
		viewlist.add(otherView);
		initviewpageradapter();
		vpager.setAdapter(pagerAdapter);
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void evnetdata(List<NameValuePair> value){
		int rcode = Integer.valueOf(value.get(0).getName());
		if (rcode == HttpRequestTool.FC_GET_DICT_LIST || rcode == HttpRequestTool.GET_WT_REN
			|| rcode == HttpRequestTool.DOWNLOAD_DEPT_YYB || rcode == HttpRequestTool.GET_FC_DZ_DICT
			|| rcode == HttpRequestTool.SUBMIT_FC_NEW_CASE) {
			loadDialog.dismiss();
		}
		if (rcode == HttpRequestTool.GET_GGS_LIST ) {
			loadDialog.dismiss();
			choiceGGShelp.waitProgress.setVisibility(View.INVISIBLE);
		}
		switch (CheckHttpResult.checkList(value, this,HttpRequestTool.GET_WT_REN)) {
		case HttpRequestTool.FC_GET_DICT_LIST:
			dictEntity=JSON.parseObject(value.get(0).getValue(), DictEntity.class);
			downloadDeptInfo();
			displayDictValue();
			break; 
		case HttpRequestTool.GET_WT_REN:
			getWTRenInfo(value.get(0).getValue());
			break; 

		case HttpRequestTool.DOWNLOAD_DEPT_YYB:
			getYYBInfo(value.get(0).getValue());
			break;  
		case HttpRequestTool.DOWNLOAD_DEPT_YYBALL:
			getALLYYBInfo(value.get(0).getValue());
			break; 
		case HttpRequestTool.GET_GGS_LIST:
			choiceGGShelp.setGGSList(value.get(0).getValue());
			break;  
//		case HttpRequestTool.GET_FC_DZ_DICT:
//			getDZDict(value.get(0).getValue());
//			break;   
		case HttpRequestTool.SUBMIT_FC_NEW_CASE:
			submitresponse(value.get(0).getValue());
			break;  
			
		default:
			break;
		}
	}

	String code="";	String msg="";String caseNober="";
	private void submitresponse(final String value) {
		
//		APPResponseModel<String> response=JSON.parseObject(value, APPResponseModel.class);
		try {
			final JSONObject json=new JSONObject(value);
			code=(String) json.optString("code", "");
			msg=(String) json.optString("message", "");
			caseNober=(String) json.optString("data", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		/**提示用户*/
		Dialog dialog;
		if (code.equals("0")) {
			 dialog=DialogUtil.getRightDialog(this,"关闭并复制案件编号","操作"+msg+"\n案件编号："+ caseNober+"\n注：待案件后台审核通过后可在APP中查询和操作！");
		}else {
			 dialog=DialogUtil.getErrDialog(this,"操作失败！"+"\n"+msg);
		}
		
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				if (code.equals("0")) {
					CopyUtils.copy(WaterCaseInputActivity.this, caseNober);
					ToastUtil.showToastShort(WaterCaseInputActivity.this, "案件编号已复制到剪贴板");
					WaterCaseInputActivity.this.finish();
				}
			}
		});
		dialog.show();
		
	}

	/**解析并显示大灾选项数据**/
//	private void getDZDict(String value) {
//		value=value.replace("&ldquo;", "*");
//		dZXXdataEntity=JSON.parseObject(value, DZXXDictEntity.class);
//		List<String> paramsList=new ArrayList<String>();
//		paramsList.add("非大灾案件");
//		for (int i = 0; i < dZXXdataEntity.data.size(); i++) {
//			paramsList.add(dZXXdataEntity.data.get(i).projectName);
//		}
//		/**大灾选项：**/
//		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN7)).setAdapter(new ArrayAdapter<String>
//		(this, R.layout.spinner_item, paramsList));
//	}

	/**获取所有的营业部信息 并传递到帮助类里面**/
	private void getALLYYBInfo(String value) {
		YYBALLdata=JSON.parseObject(value, YYBEntity.class);
		if (null!=YYBALLdata && null!=YYBALLdata.tableData && null!=YYBALLdata.tableData.data) {
			YYBAllList=new ArrayList<YYBDataEntity>();
			for (int i = 0; i < YYBALLdata.tableData.data.size(); i++) {
				YYBAllList.add(YYBALLdata.tableData.data.get(i));
			}
		}
		choiceGGShelp.setValue(YYBAllList);
	}


	/**获取营业部信息,并显示在Spinner中*/
	private void getYYBInfo(String value) {
		YYBdata=JSON.parseObject(value, YYBEntity.class);
		if (null!=YYBdata && null != YYBdata.tableData && null!=YYBdata.tableData.data) {
			YYBDataList=YYBdata.tableData.data;
		}else {
			YYBDataList=new ArrayList<YYBDataEntity>();
		}
		List<String> tempYYB=new ArrayList<String>();
		tempYYB.add("");//加一个空字符串，避免显示时有值，同时注意取值时需要在YYBDataList中获取ID时加一（+1）
		for (int i = 0; i <YYBDataList.size(); i++) {
			tempYYB.add(YYBDataList.get(i).name);
		}
		((Spinner)findViewById(R.id.WCI_SPINN_gsOrg)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, tempYYB));
	}


	/**解析委托人信息*/
	private void getWTRenInfo(String value) {
		wtRenData=JSON.parseObject(value, InsureCompanyEntity.class);
		int wtrSize=wtRenData.data.list.size();
		String [] wtArr=new String[wtrSize];
		for (int i = 0; i < wtRenData.data.list.size(); i++) {
			wtArr [i]=wtRenData.data.list.get(i).name;
		}
		  // 自动提示适配器  
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, wtArr);  
      // 支持拼音检索  
//      SearchAdapter<String> adapter = new SearchAdapter<String>(MainActivity.this,  
//              android.R.layout.simple_list_item_1, wtArr, SearchAdapter.ALL);  
//      search.setAdapter(adapter);
      search.setOnClickListener(this);
      choiceWTRhelp=new ChoiceWTRhelp(this, wtArr,search,wtRenData.data.list,creatCaseView 
    		  ,R.id.WCI_Edit_deputeLinkPer,R.id.WCI_Edit_deputeLinkTel,R.id.WCI_Edit_deputeLinkEmail);
      ToastUtil.showToastShort(this,"委托人可以用了");
	}


	private void displayDictValue() {
		/**险种大类： 1财险，2水险,spinner选中0是财险，1是水险**/
//		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN1)).setAdapter(new ArrayAdapter<String>
//		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.risk_type)));
		((EditText)creatCaseView.findViewById(R.id.WCI_Text_recCasePer)).setText(AppApplication.USER.data.name);
		Spinner spinner=(Spinner)creatCaseView.findViewById(R.id.WCI_SPINN_riskType);
		spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item,new String[]{"财险","水险"}));
		spinner.setSelection(1);
		setonXZclick(spinner);
		
		/**是否异地：：**/
//		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN2)).setAdapter(new ArrayAdapter<String>
//		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.is_yd)));
		
		
		/**接案方式：**/
		((Spinner)creatCaseView.findViewById(R.id.WCI_Spinn_revCaseType)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.rev_case_type)));
		
		
		/**险种分类**/
		XZSpinner=((Spinner)creatCaseView.findViewById(R.id.WCI__SPINN_feicheBaoxianType));
		XZSpinner.setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.feicheBaoxianType)));
		setXZSpinneronlick();
		/**业务类型：**/
//		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN6)).setAdapter(new ArrayAdapter<String>
//		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.business_type )));
		
		/**所属行业：**/
//		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN8)).setAdapter(new ArrayAdapter<String>
//		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.case_industry )));
		/** 委托人身份：**/
//		((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN9)).setAdapter(new ArrayAdapter<String>
//		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.depute_sf )));
		/** 紧急程度：**/
		((Spinner)creatCaseView.findViewById(R.id.WCI_Spinn_urgencyLevel)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.urgency_level )));
		/**船舶类型： **/
		((Spinner)wtView.findViewById(R.id.WCI_Spinn_shipType)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.ship_type )));
		/**船舶吨位： **/
		((Spinner)wtView.findViewById(R.id.WCI_Spinn_shipDw)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.ship_dw )));
		/**主要受损类型： **/
		((Spinner)wtView.findViewById(R.id.WCI_Spinn_mainLossType)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.mainLoss_type )));
		/**币别： **/
		((Spinner)wtView.findViewById(R.id.WCI_Spinn_lossCurrency1)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.loss_currency )));
	}

	/**如果选择了财险就跳转到财险录入界面*/
	private void setonXZclick(final Spinner findViewById) {
		
		findViewById.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2==0) {
					WaterCaseInputActivity.this.startActivity(
							new Intent(WaterCaseInputActivity.this, CaseInputActivity.class));
					WaterCaseInputActivity.this.finish();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}

	private void setXZSpinneronlick() {
		XZSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				/**业务类型：**/
				caseTypeList=FCCaseTypeEntity.getWaterTypeList(dictEntity.data.feicheBaoxianType.get(arg2).value);
				((Spinner)creatCaseView.findViewById(R.id.WCI_SPINNER_businessType)).setAdapter(new ArrayAdapter<String>
				(WaterCaseInputActivity.this, R.layout.spinner_item, caseTypeList));
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ACWAFCINPUT_button://暂存报案录入信息
			SubmitCase(0);
			break;
		case R.id.WCI_Edit_cwtfInfo://添加次委托方
			addRelationals();
			break;
		case R.id.WCI_Edit_glgxfInfo://添加关联关系方
			addLaboratorian();
			break;
		case R.id.WCI_Text_GGSInfo://添加作业人员信息
			choiceGGShelp.showChoiceDialog();
			break;
		case R.id.WCI_text_othership://添加作业人员信息
			addOthership();
			break;
		case R.id.WCI_deputePer://选择委托人
			choiceWTRhelp.showChoiceDialog();
			break;

		default:
			break;
		}
		
	}

	/**添加其他方船舶信息**/
	private void addOthership() {
		otherShpView=inflater.inflate(R.layout.water_fc_input_other_shp, null);
		/**船舶类型： **/
		((Spinner)otherShpView.findViewById(R.id.WCI_Spinn_other_shipType)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.ship_type )));
		/**船舶吨位： **/
		((Spinner)otherShpView.findViewById(R.id.WCI_Spinn_other_shipDw)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.ship_dw )));
		DialogUtil.getDialogByViewOnlistener(this, otherShpView, "添加其他方船舶信息", 
				new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					saveAndDisplayOthership();
				}
			}).show();
	}
	
	/**显示其他方船舶信息**/
	private void saveAndDisplayOthership() {
		CaseShip caseShip = new CaseShip();
		caseShip.deputeShipname =((EditText)otherShpView.findViewById(R.id.WCI_Edit_other_deputeShipname)).getText().toString(); //其他方船名：
		caseShip.shipType = dictEntity.data.ship_type.get(((Spinner)otherShpView.findViewById(R.id.WCI_Spinn_other_shipType))
				.getSelectedItemPosition()).value+"";//船舶类型：
		caseShip.shipDw = dictEntity.data.ship_dw.get(((Spinner)otherShpView.findViewById(R.id.WCI_Spinn_other_shipDw))
				.getSelectedItemPosition()).value+"";//船舶吨位

		if (!TextUtils.isEmpty(caseShip.deputeShipname) && !TextUtils.isEmpty(caseShip.shipType) && !TextUtils.isEmpty(caseShip.shipDw)) {
			caseShipList.add(caseShip);
			displayOthership(); //otherShipLayout
		} else {
			ToastUtil.showToastShort(this, "未填写信息！");
		}
		appRequestData.requestData.shp = caseShipList;
	}

	/**显示第三方船舶信息*/
	private void displayOthership() {
		otherShipLayout.removeAllViews();
		ToastUtil.showToastLong(this, "显示关系方信息");
		for (int i = 0; i < caseShipList.size(); i++) {
			CaseShip shipTemp=caseShipList.get(i);
			View view=inflater.inflate(R.layout.spinner_item, null);
			TextView teView=(TextView) view.findViewById(R.id.spinner_item_textone);
			teView.setText((i+1)+"、"+shipTemp.deputeShipname+" "+shipTemp.shipType+" "+shipTemp.shipDw);
			teView.setGravity(Gravity.LEFT);
			otherShipLayout.addView(view);
		}
	}

	/**添加关联方信息*/
	private void addLaboratorian() {
		LaboratorianDisplayView=inflater.inflate(R.layout.water_input_laboratorian_info_view, null);
		DialogUtil.getDialogByViewOnlistener(this, LaboratorianDisplayView, "添加关联方信息", 
			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				saveAndDisplayLaboratorian();
			}
		}).show();
	}
	
	/**保存并显示关系方信息**/
	private void saveAndDisplayLaboratorian() {
		CaseLaboratorian laboratorian=new CaseLaboratorian();
		laboratorian.deputeSf=((EditText)LaboratorianDisplayView.findViewById(R.id.WLI_party_identity)).getText().toString();
		laboratorian.depute=((EditText)LaboratorianDisplayView.findViewById(R.id.WLI_party_per)).getText().toString();
		laboratorian. deputeLinkPhone=((EditText)LaboratorianDisplayView.findViewById(R.id.WLI_party_conectPerPhoneNumber)).getText().toString();
		laboratorian. workTel=((EditText)LaboratorianDisplayView.findViewById(R.id.WLI_party_workPhoneNumber)).getText().toString();
		laboratorian.eMail=((EditText)LaboratorianDisplayView.findViewById(R.id.WLI_party_eMail)).getText().toString();
		laboratorian. pose=((EditText)LaboratorianDisplayView.findViewById(R.id.WLI_party_zipCode)).getText().toString();
		laboratorian. address =((EditText)LaboratorianDisplayView.findViewById(R.id.WLI_party_address)).getText().toString();
		laboratorian. deputeLink=((EditText)LaboratorianDisplayView.findViewById(R.id.WLI_party_conectper)).getText().toString();
		
		if (!TextUtils.isEmpty(laboratorian.deputeSf) || !TextUtils.isEmpty(laboratorian.depute) || !TextUtils.isEmpty(laboratorian.deputeLink) || 
				!TextUtils.isEmpty(laboratorian.deputeLinkPhone) || !TextUtils.isEmpty(laboratorian.workTel) || 
				!TextUtils.isEmpty(laboratorian.eMail) ||!TextUtils.isEmpty(laboratorian.address)||!TextUtils.isEmpty(laboratorian.pose)) {
			labsData.add(laboratorian);
			displayLaboratorianInfo();
		}else {
			ToastUtil.showToastShort(this, "未填写信息！");
		}
		appRequestData.requestData.labs=labsData;
	}
	
	/**显示关系方信息
	 * @param labsTemp **/
	private void displayLaboratorianInfo() {
		LaboratorianLayout.removeAllViews();
		ToastUtil.showToastLong(this, "显示关系方信息");
		for (int i = 0; i < labsData.size(); i++) {
			CaseLaboratorian labsTemp=labsData.get(i);
			View view=inflater.inflate(R.layout.spinner_item, null);
			TextView teView=(TextView) view.findViewById(R.id.spinner_item_textone);
			teView.setText((i+1)+"、"+labsTemp.depute+" "+labsTemp.deputeLink+" "+labsTemp.workTel);
			teView.setGravity(Gravity.LEFT);
			LaboratorianLayout.addView(view);
		}
	}

	/**提交接报案信息**/
	private void SubmitCase(int submitCode) {
		appRequestData.userToken=AppApplication.USER.data.targetOid;
		CaseManage casem=new CaseManage();
		casem.riskType=dictEntity.data.risk_type.get(((Spinner)creatCaseView.findViewById(R.id.WCI_SPINN_riskType))
				.getSelectedItemPosition()).value;//险种大类
		
		int orgPostion=((Spinner)creatCaseView.findViewById(R.id.WCI_SPINN_gsOrg)).getSelectedItemPosition();
		if (orgPostion>0) {
			casem.gsOrg=YYBDataList.get(orgPostion-1).id+"";//归属机构
		}
		casem.caseName=((EditText)creatCaseView.findViewById(R.id.WCI_Edit_caseName)).getText().toString();//案件名称
		
		casem.policyNo=((EditText)creatCaseView.findViewById(R.id.WCI_Edit_policyNo)).getText().toString();//保单号
		
		casem.deputeDate=((TextView)creatCaseView.findViewById(R.id.WCI_Text_deputeDate)).getText().toString();//委托日期
		
		casem.revCaseType=dictEntity.data.rev_case_type.get(((Spinner)creatCaseView.findViewById(R.id.WCI_Spinn_revCaseType))
				.getSelectedItemPosition()).value+"";//接案方式
		
		casem.feicheBaoxianType=dictEntity.data.feicheBaoxianType.get(((Spinner)creatCaseView.findViewById(R.id.WCI__SPINN_feicheBaoxianType))
				.getSelectedItemPosition()).value+"";//险种分类
		
//		String caseTypeStr=caseTypeList.get((((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN5)).getSelectedItemPosition()));
//		casem.caseType=FCCaseTypeEntity.getCaseTypeId(Integer.parseInt(casem.feicheBaoxianType),caseTypeStr);//险种分类
		
		casem.businessType=dictEntity.data.business_type.get(((Spinner)creatCaseView.findViewById(R.id.WCI_SPINNER_businessType))
				.getSelectedItemPosition()).value; //业务类型
		
//		int DzxxPostion=((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINNYYB)).getSelectedItemPosition();
//		if (DzxxPostion>0) {
//			casem.immenseOption=dZXXdataEntity.data.get(DzxxPostion).id+"";//大灾选项dZXXdataEntity
//		}
//		casem.caseIndustry=dictEntity.data.case_industry.get(((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN6))
//						.getSelectedItemPosition()).value+""; //所属行业
		
		casem.recCasePer= ((EditText)creatCaseView.findViewById(R.id.WCI_Text_recCasePer)).getText().toString();//接案人 
//		casem.recCasePer=AppApplication.USER.data.name;//接案人
		casem.reciverPhone=AppApplication.USER.data.mobile;//接案人电话
		
		casem.revCaseDate=((TextView)creatCaseView.findViewById(R.id.WCI_Text_revCaseDate)).getText().toString(); //接案日期
//		casem.casePick=((EditText)creatCaseView.findViewById(R.id.WCI_Edit_remark)).getText().toString(); //报案摘要(2000字)
		/**委托信息***/
		String wtName=((AutoCompleteTextView)creatCaseView.findViewById(R.id.WCI_deputePer)).getText().toString(); //委托人
		casem.deputePer=wtName;
		if (wtRenData!=null && wtRenData.data!=null && wtRenData.data.list!=null) {
		for (int i = 0; i < wtRenData.data.list.size(); i++) {
			if (wtName.equals(wtRenData.data.list.get(i).name)) {
				casem.deputeId=wtRenData.data.list.get(i).id+"";
				break;
			}
		}}
		casem.deputeLinkPer=((EditText)creatCaseView.findViewById(R.id.WCI_Edit_deputeLinkPer)).getText().toString(); //委托人联系人
		casem.deputeLinkTel=((EditText)creatCaseView.findViewById(R.id.WCI_Edit_deputeLinkTel)).getText().toString(); //委托人联系人电话
		casem.deputeLinkEmail=((TextView)creatCaseView.findViewById(R.id.WCI_Edit_deputeLinkEmail)).getText().toString(); //委托人联系人Email
		casem.fgLinker=((TextView)creatCaseView.findViewById(R.id.WCI_Edit_fgLinker)).getText().toString(); //付款方联系人：
		casem.fgLinkTel=((TextView)creatCaseView.findViewById(R.id.WCI_Edit_fgLinkTel)).getText().toString(); //付款方联系人电话
//		casem.deputeLinkPhone=((EditText)wtView.findViewById(R.id.CaseINPUT_edit9)).getText().toString(); //委托人联系人手机
		casem.deputeCaseNo=((EditText)creatCaseView.findViewById(R.id.WCI_Edit_deputeCaseNo)).getText().toString(); //委托方案件编号
		casem.payer=((EditText)creatCaseView.findViewById(R.id.WCI_Edit_payer)).getText().toString(); //付款方
//		casem.deputeSf=dictEntity.data.depute_sf.get(((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN9))
//				.getSelectedItemPosition()).value+""; //委托人身份
		casem.urgencyLevel=dictEntity.data.urgency_level.get(((Spinner)creatCaseView.findViewById(R.id.WCI_Spinn_urgencyLevel))
				.getSelectedItemPosition()).value+""; //紧急程度
//		casem.wtfYq=((EditText)wtView.findViewById(R.id.CaseINPUT_edit12)).getText().toString(); //委托方要求
//		casem.deputeItem=dictEntity.data..get(((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN10))
//				.getSelectedItemPosition()).value+"";//委托事项
		/**出险信息***/
		casem.insurerCaseNo=((EditText)creatCaseView.findViewById(R.id.WCI_Edit_insurerCaseNo)).getText().toString(); //保险公司报案号

		
		
		casem.deputeShipname=((EditText)wtView.findViewById(R.id.WCI_Edit_deputeShipname)).getText().toString(); //委托方船名：
		casem.shipType=dictEntity.data.ship_type.get(((Spinner)wtView.findViewById(R.id.WCI_Spinn_shipType))
				.getSelectedItemPosition()).value;//船舶类型：
		casem.shipDw=dictEntity.data.ship_dw.get(((Spinner)wtView.findViewById(R.id.WCI_Spinn_shipDw))
				.getSelectedItemPosition()).value+"";//船舶吨位
		casem.mainLossType=dictEntity.data.ship_dw.get(((Spinner)wtView.findViewById(R.id.WCI_Spinn_mainLossType))
				.getSelectedItemPosition()).value;//主要受损类型：
		casem.mainGoodsType=((EditText)wtView.findViewById(R.id.WCI_Edit_mainGoodsType)).getText().toString(); //主要货物种类
		casem.lossCurrency=dictEntity.data.loss_currency.get(((Spinner)wtView.findViewById(R.id.WCI_Spinn_lossCurrency1))
				.getSelectedItemPosition()).value+"";//报损币别
		casem.lossAmout=((EditText)wtView.findViewById(R.id.WCI_Edit_lossAmout1)).getText().toString(); //货物数量
		casem.preCheckDate=((TextView)wtView.findViewById(R.id.WCI_text_preCheckDate)).getText().toString(); //预计查勘日期：
		casem.dangerPeriod=((TextView)wtView.findViewById(R.id.WCI_text_dangerPeriod)).getText().toString(); //出险日期：
		casem.filed4=((EditText)wtView.findViewById(R.id.WCI_Edit_sum1)).getText().toString(); //货物数量
		casem.filed3=((EditText)wtView.findViewById(R.id.WCI_Edit_danwei1)).getText().toString(); //货物单位
		casem.dangerAdd=((EditText)wtView.findViewById(R.id.WCI_Edit_dangerAdd)).getText().toString();//出险地点
		casem.checkAdd=((EditText)wtView.findViewById(R.id.WCI_Edit_checkAdd)).getText().toString(); //查勘地点
		
		casem.remark=((EditText)wtView.findViewById(R.id.WCI_Edit_remark)).getText().toString(); //备注(2000字)：
		if (submitCode==0) { //暂存
			casem.status=0;
		}else if (submitCode==1) { //保存
			casem.status=1;
		}
		appRequestData.requestData.casem=casem;
		submitCaseInput(submitCode);
	}

	/**提交接报案信息**/
	private void submitCaseInput(int submitCode) {
		
		if (submitCode==1 && !isFull()) {
			return;
		}
			loadDialog.setMessage("努力提交中……").show();
			params=new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appRequestData)));
			HttpUtils.requestPost(URLs.SUBMIT_FC_NEW_CASE, params, HttpRequestTool.SUBMIT_FC_NEW_CASE);
	}

	/**必填项是否已填写*/
	private boolean isFull() {
		List<String> mustFullStrings=new ArrayList<String>();
		CaseManage casem = appRequestData.requestData.casem;
		List<CaseRelate> rels=appRequestData.requestData.rels;
		mustFullStrings.add(casem.riskType!=0?(casem.riskType+""):"" );//0
		mustFullStrings.add(casem.gsOrg );//1
		mustFullStrings.add(casem.caseName );//2
		mustFullStrings.add(casem.deputeDate );//3
		mustFullStrings.add(casem.deputeId );//4
		mustFullStrings.add(casem.feicheBaoxianType );//5
		mustFullStrings.add(casem.businessType+"" );//6
		mustFullStrings.add(casem.deputeLinkPer );
		mustFullStrings.add(casem.deputeLinkTel );
		mustFullStrings.add(casem.deputeLinkEmail );
		mustFullStrings.add(casem.checkAdd );
		
		if (!PatternUtil.isEmail(casem.deputeLinkEmail)) {
			DialogUtil.getErrDialog(this, "邮箱地址错误！").show();
			return false;
		}
		if (rels!=null && rels.size()>0 && !TextUtils.isEmpty(rels.get(0).userName)) {
			
		}else {//
			DialogUtil.getErrDialog(this, "未选择公估师！").show();
			return false;
		}
		for (int i = 0; i < mustFullStrings.size(); i++) {
			if (TextUtils.isEmpty(mustFullStrings.get(i))) {
				DialogUtil.getErrDialog(this, "请填写第"+(i+1)+"个必填项目后提交！").show();
				return false;
			}
		}
//		for (String str:mustFullStrings) {
//			if (TextUtils.isEmpty(str)) {
//				DialogUtil.getErrDialog(this, "请填写所有必填项目！").show();
//				return false;
//			}
//		}
		return true;
	}

	/**添加次委托方*/
	private void addRelationals() {
		relationalInputView=inflater.inflate(R.layout.water_input_relational_info_view, null);
		DialogUtil.getDialogByViewOnlistener(this, relationalInputView, "添加关联方信息", 
			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				saveAndDisplay();
			}
		}).show();
	}
	
	/**保存添加的保险关系方到对象中并显示列表*/
	private void saveAndDisplay(){
		CaseDepute labsTemp=new CaseDepute();
		labsTemp.deputeSf=((EditText)relationalInputView.findViewById(R.id.WRI_party_identity)).getText().toString();
		labsTemp.depute=((EditText)relationalInputView.findViewById(R.id.WRI_party_per)).getText().toString();
		labsTemp. deputeLinkPhone=((EditText)relationalInputView.findViewById(R.id.WRI_party_conectPerPhoneNumber)).getText().toString();
		labsTemp. workTel=((EditText)relationalInputView.findViewById(R.id.WRI_party_workPhoneNumber)).getText().toString();
		labsTemp.eMail=((EditText)relationalInputView.findViewById(R.id.WRI_party_eMail)).getText().toString();
		labsTemp. pose=((EditText)relationalInputView.findViewById(R.id.WRI_party_zipCode)).getText().toString();
		labsTemp. address =((EditText)relationalInputView.findViewById(R.id.WRI_party_address)).getText().toString();
		labsTemp. deputeLink=((EditText)relationalInputView.findViewById(R.id.WRI_party_conectper)).getText().toString();
		
		if (!TextUtils.isEmpty(labsTemp.deputeSf) || !TextUtils.isEmpty(labsTemp.depute) || !TextUtils.isEmpty(labsTemp.deputeLink) || 
				!TextUtils.isEmpty(labsTemp.deputeLinkPhone) || !TextUtils.isEmpty(labsTemp.workTel) || 
				!TextUtils.isEmpty(labsTemp.eMail) ||!TextUtils.isEmpty(labsTemp.address)||!TextUtils.isEmpty(labsTemp.pose)) {
			depsData.add(labsTemp);
			displayRelationalInfo();
		}else {
			ToastUtil.showToastShort(this, "未填写信息！");
		}
		appRequestData.requestData.deps=depsData;
	}

	/**显示添加的委托方信息
	 * @param labsTemp **/
	private void displayRelationalInfo() {

		ToastUtil.showToastLong(this, "显示添加的委托方信息");
		RelationalsLayout.removeAllViews();
		for (int i = 0; i < depsData.size(); i++) {
			CaseDepute labsTemp=depsData.get(i);
			View view=inflater.inflate(R.layout.spinner_item, null);
			TextView teView=(TextView) view.findViewById(R.id.spinner_item_textone);
			teView.setText((i+1)+"、"+labsTemp.depute+" "+labsTemp.deputeLink+" "+labsTemp.workTel);
			teView.setGravity(Gravity.LEFT);
			RelationalsLayout.addView(view);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int buttonId) {
		// TODO Auto-generated method stub
		switch (buttonId) {
		case R.id.ACWAFCINPUT_btn_0:
			vpager.setCurrentItem(0);
			break;
		case R.id.ACWAFCINPUT_btn_1:
			vpager.setCurrentItem(1);
			break;
		case R.id.ACWAFCINPUT_btn_2:
			vpager.setCurrentItem(2);
			break;
		default:
			break;
		}
	}


	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int pi) {
		switch (pi) {
		case 0:
			radgrup.check(R.id.ACWAFCINPUT_btn_0);
			break;
		case 1:
			radgrup.check(R.id.ACWAFCINPUT_btn_1);
			break;
		case 2:
			radgrup.check(R.id.ACWAFCINPUT_btn_2);
			break;

		default:
			break;
		}
	}
	
	private void initviewpageradapter() {
		pagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override	
			public int getCount() {
				return viewlist.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView(viewlist.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(viewlist.get(position));
				return viewlist.get(position);
			}
		};
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	
	/**循环添加单击事件，并获取时间并赋值**/
	private void setDataCheckOnclicEvent() {
		for (int i = 0; i < onclickIDs.length; i++) {
			if (i<2) {
				setonclickandValue((TextView)creatCaseView.findViewById(onclickIDs[i]));
			}else {
				setonclickandValue((TextView)wtView.findViewById(onclickIDs[i]));
			}
		}
	}
	/**获取时间并赋值**/
	private void setonclickandValue(final TextView textTv) {
		final Calendar cal = Calendar.getInstance();
		textTv.setOnClickListener(new OnClickListener() {
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(View arg0) {
				DatePickerDialog pickerDialog=new DatePickerDialog(WaterCaseInputActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
						, new OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker arg0, int y, int m, int d) {
								String checkDate=y+"-"+(m+1)+"-"+d;
								textTv.setText(checkDate);
							}
				}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
				pickerDialog.show();
			}
		});
	}


	public void addGGS(GGSEntity choiceGGS) {
		if (chickGGS == null) {//第一次直接添加
			chickGGS = new GGSEntity();
			chickGGS.tableData=new GGSTableData();
			chickGGS.tableData.data=new ArrayList<GGSEntity.GGSTableData.GGSData>();
			for (GGSData ggs:choiceGGS.tableData.data) {
				chickGGS.tableData.data.add(ggs);
			}
		}else {//第二次判断是否有重复
			for (GGSData ggs:choiceGGS.tableData.data) {
				boolean flag=true;
				for (int i = 0; i < chickGGS.tableData.data.size(); i++) {
					if (chickGGS.tableData.data.get(i).id==ggs.id) {
						flag=false;
					}
				}
				if (flag) {
					chickGGS.tableData.data.add(ggs);
				}
			}
		}
		castGGS(0);
	}
	
	/**将车险的公估师实体类转为非车的公估师实体类,传递的int参数作为主办所在位置**/
	private void castGGS(int zbPoint) {
		appRequestData.requestData.rels=new ArrayList<CaseRelate>();
		for (int i = 0; i < chickGGS.tableData.data.size(); i++) {
			GGSData ggsT=chickGGS.tableData.data.get(i);
			CaseRelate temprels=new CaseRelate();
			temprels.id=(ggsT.id);
			temprels.accounts=ggsT.loginName;
			temprels.userName=ggsT.name;
			temprels.homeInstitution=ggsT.organizationSelfName;
//			temprels.linkTel=ggsT.;
			temprels.dispatchStatus="未调度";
			if (chickGGS.tableData.data.size()<=zbPoint ) { //传递的参数大于数组长度时第一个公估师为主办
				if ( i==0) {
					temprels.relType="主办";
				}
			}else if (i==zbPoint) {
				temprels.relType="主办";
			}else {
				temprels.relType="协办";
			}
			appRequestData.requestData.rels.add(temprels);
		}
		displayGGSInfo(appRequestData.requestData.rels);
	}

	/**显示添加的公估师信息 到ggslLayout
	 * @param rels 
	 * @param labsTemp **/
	private void displayGGSInfo(List<CaseRelate> rels) {
		ggslLayout.removeAllViews();
		for (int i = 0; i < rels.size(); i++) {
			CaseRelate ggsT=rels.get(i);
			ggslLayout.addView(getGGSview(ggsT,i));
		}
	}

/**根据传递的公估师对象获取显示的item view*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private View getGGSview(CaseRelate ggsT,int i) {
		View view=inflater.inflate(R.layout.ggs_list_item, null);
		Spinner spinner=(Spinner) view.findViewById(R.id.GGSLIST_item_SPINN1);
		spinner.setAdapter(new ArrayAdapter(this, R.layout.spinner_item, new String[]{"主办","协办"}));
		setZXB(spinner,ggsT,i);
		TextView teView=(TextView) view.findViewById(R.id.GGSLIST_item_textone);
		teView.setText((i+1)+"、"+ggsT.homeInstitution+"\t"+ggsT.userName);
		return view;
	}
	
	/**设置主协办选择
	 * @param ggsT 
	 * @param spinner 
	 * @param i */
	private void setZXB(Spinner spinner, CaseRelate ggsT, int i){
		if (ggsT.relType.equals("主办")) {
			spinner.setSelection(0);
		}else {
			spinner.setSelection(1);
		}
		setOnItemSelectl(spinner,i);
	}

	/** 设置协办被修改后的时间，保证只有一个主办 
	 * @param i **/
	private void setOnItemSelectl(final Spinner spinner, final int i) {
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (spinner.getTag()==null) {
					spinner.setTag(true);
				}else {
					ToastUtil.showToastShort(WaterCaseInputActivity.this, "选择公估师"+appRequestData.requestData.rels.get(i).userName+"为主办");
					castGGS(i);
				}
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}
	
	/**监听返回键，并调用退出提示方法**/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			HintOut();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**提示用户是否真的要退出该界面，避免勿退出！**/
	private void HintOut(){
		Dialog  dialog=DialogUtil.getAlertOnelistener(this, "确定要退出该页面吗！", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				WaterCaseInputActivity.this.finish();
			}
		});
		dialog.show();
	}

}
