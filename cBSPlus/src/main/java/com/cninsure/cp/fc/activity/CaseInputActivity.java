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
import com.cninsure.cp.entity.fc.CaseBeanEntity;
import com.cninsure.cp.entity.fc.CaseDepute;
import com.cninsure.cp.entity.fc.CaseManage;
import com.cninsure.cp.entity.fc.CaseRelate;
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

public class CaseInputActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnPageChangeListener {

	/***
	 * risk_type 险种大类
rev_case_type 接案方式
feiche_baoxian_type 财险险种
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
depute_item 财险委托事项
deputeItem 水险委托事项
	 */
	String[] ZDparamsStr=new String[]{"risk_type","rev_case_type","feiche_baoxian_type","is_yd","depute_item",
			"business_type","case_industry","depute_sf","urgency_level","danger_res","loss_currency"};
	
	/**需要设置onclick事件的ID集合*/
	private int[] onclickIDs=new int[]{R.id.CaseINPUT_text2,R.id.CaseINPUT_text4,R.id.CaseINPUT_text6};

	private View creatCaseView,wtView,chuxianView,otherView,relationalInputView;
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
	/**保险关系方**/
	private List<CaseDepute> depsData;
	private LinearLayout relationalLayout,ggslLayout;
	/**委托人信息*/
	public AutoCompleteTextView search;
	private InsureCompanyEntity wtRenData;
	private YYBEntity YYBdata,YYBALLdata;
	/****显示的时候会在字符串数组中加一个空字符串，避免显示时有值，同时注意取值时需要在YYBDataList中获取ID时加一（+1）**/
	private List<YYBDataEntity> YYBDataList;
	private List<YYBDataEntity> YYBAllList;
	private ChoiceGGShelp choiceGGShelp;
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
		setContentView(R.layout.activty_fc_case_input);
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
		appre.userToken = AppApplication.getUSER().data.targetOid;
		appre.requestData = ZDparamsStr;
		params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre)));
		HttpUtils.requestPost(URLs.FC_GET_DICT_LIST, params, HttpRequestTool.FC_GET_DICT_LIST);
		downloadDZDict();
		getWtRen();
	}
	
	/**获取大灾选项数据**/
	private void downloadDZDict(){
		params = new ArrayList<NameValuePair>();
		loadDialog.setMessage("努力加载中……").show();
		APPRequestModel<String> appre=new APPRequestModel<String>();
		appre.userToken=AppApplication.getUSER().data.targetOid;
		params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre)));
		HttpUtils.requestPost(URLs.GET_FC_DZ_DICT, params, HttpRequestTool.GET_FC_DZ_DICT);
	}
	
	
	/**获取委托人信息*/
	private void getWtRen(){
		params = new ArrayList<NameValuePair>();
		@SuppressWarnings("rawtypes")
		APPRequestModel<PagedRequest> appre = new APPRequestModel<PagedRequest>();
		appre.userToken = AppApplication.getUSER().data.targetOid;
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
		params.add(AppApplication.getUSER().data.userId);
		params.add("type");
		params.add("4");
		params.add("grade");
		params.add("4");//type=4&grade=4
		downLoadAlldept();
		params.add("organizationLoginId");
		params.add(AppApplication.getUSER().data.organizationLoginId+"");
		HttpUtils.requestGet(URLs.DOWNLOAD_DEPT_YYB, params, HttpRequestTool.DOWNLOAD_DEPT_YYB);
	}
	
	/**获取所有营业部 传3请求所有的影营业部信息**/
	private void downLoadAlldept(){
		List<String> params=new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.getUSER().data.userId);
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
//				workhelp.upload();
				SubmitCase(1);
			}
		});
	}
	
	
	private void initView() {
		choiceGGShelp=new ChoiceGGShelp(this,null,0);
		loadDialog=new LoadingDialog(this);
		vpager=(ViewPager) findViewById(R.id.ACFCINPUT_viewpager);
		radgrup = (RadioGroup) findViewById(R.id.ACFCINPUT_BtnG);
		submitButton=(Button) findViewById(R.id.ACFCINPUT_button);
		
		submitButton.setOnClickListener(this);
		radgrup.setOnCheckedChangeListener(this);
		vpager.setOnPageChangeListener(this);
		inflater=LayoutInflater.from(this);
		viewlist=new ArrayList<View>();
//		spinnerView1=new ArrayList<Spinner>(11);
		creatCaseView=inflater.inflate(R.layout.fc_take_case_creatinfo, null);
		wtView=inflater.inflate(R.layout.fc_take_case_wt_info, null);
		chuxianView=inflater.inflate(R.layout.fc_take_case_event_info, null);
		otherView=inflater.inflate(R.layout.fc_take_case_other_info, null);
		relationalLayout=(LinearLayout) otherView.findViewById(R.id.CaseINPUT_relationalLear);
		ggslLayout=(LinearLayout) otherView.findViewById(R.id.CaseINPUT_ggsLear);
		depsData=new ArrayList<CaseDepute>();
		otherView.findViewById(R.id.CaseINPUT_relationalAdd).setOnClickListener(this);
		otherView.findViewById(R.id.CaseINPUT_GGSAdd).setOnClickListener(this);
		/**设置邮件输入格式,错误是提示用户*/
		PatternUtil.setEmailInput((EditText)wtView.findViewById(R.id.CaseINPUT_edit7));
		
		search = (AutoCompleteTextView) wtView.findViewById(R.id.CaseINPUT_text5);  
//		((AutoCompleteTextView)wtView.findViewById(R.id.CaseINPUT_text5)).setError("委托人必须为从自动弹出的下拉列表中选择，否则录入可能会失败！");//设置委托人录入的规则
//		linearconnect = (LinearLayout) otherView.findViewById(R.id.);
//		linearggs = (LinearLayout) otherView.findViewById(R.id.);
		setDataCheckOnclicEvent();
		viewlist.add(creatCaseView);
		viewlist.add(wtView);
		viewlist.add(chuxianView);
		viewlist.add(otherView);
		initviewpageradapter();
		vpager.setAdapter(pagerAdapter);
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void evnetdata(List<NameValuePair> value){
		int rcode = Integer.valueOf(value.get(0).getName());
		if (rcode == HttpRequestTool.FC_GET_DICT_LIST || rcode == HttpRequestTool.GET_WT_REN
			|| rcode == HttpRequestTool.DOWNLOAD_DEPT_YYB || rcode == HttpRequestTool.GET_FC_DZ_DICT
			|| rcode == HttpRequestTool.SUBMIT_FC_NEW_CASE || rcode == HttpRequestTool.FC_GET_CASE_INFO) {
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
		case HttpRequestTool.GET_FC_DZ_DICT:
			getDZDict(value.get(0).getValue());
			break;   
		case HttpRequestTool.SUBMIT_FC_NEW_CASE:
			submitresponse(value.get(0).getValue());
			break;    
		case HttpRequestTool.FC_GET_CASE_INFO:
			caseBeanEntity=JSON.parseObject(value.get(0).getValue(), CaseBeanEntity.class);
			displayCaseDate();
			break;  
			
		default:
			break;
		}
	}
	
	private CaseBeanEntity caseBeanEntity;

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
					CopyUtils.copy(CaseInputActivity.this, caseNober);
					ToastUtil.showToastShort(CaseInputActivity.this, "案件编号已复制到剪贴板");
					CaseInputActivity.this.finish();
				}
			}
		});
		dialog.show();
		
	}

	/**解析并显示大灾选项数据**/
	private void getDZDict(String value) {
		value=value.replace("&ldquo;", "*");
		dZXXdataEntity=JSON.parseObject(value, DZXXDictEntity.class);
		List<String> paramsList=new ArrayList<String>();
		paramsList.add("非大灾案件");
		for (int i = 0; i < dZXXdataEntity.data.size(); i++) {
			paramsList.add(dZXXdataEntity.data.get(i).projectName);
		}
		/**大灾选项：**/
		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN7)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, paramsList));
	}

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
		((Spinner)findViewById(R.id.CaseINPUT_SPINNYYB)).setAdapter(new ArrayAdapter<String>
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
//      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, wtArr);  
      // 支持拼音检索  
//      SearchAdapter<String> adapter = new SearchAdapter<String>(MainActivity.this,  
//              android.R.layout.simple_list_item_1, wtArr, SearchAdapter.ALL);  
//      search.setAdapter(adapter);
      search.setOnClickListener(this);
      choiceWTRhelp=new ChoiceWTRhelp(this, wtArr,search,wtRenData.data.list,wtView,
    		  R.id.CaseINPUT_edit5,R.id.CaseINPUT_edit6,R.id.CaseINPUT_edit7);
      ToastUtil.showToastShort(this,"委托人加载完毕！");
      //获取暂存/保存信息
//      getTemporaryStorage();
      downloadCase();
	}

	/**获取案件信息（暂存或者保存）以便回显**/
	private void getTemporaryStorage() {
//		appRequestData=new APPRequestModel<CaseBean>();
//		appRequestData.requestData=new CaseBean();
//		appRequestData.requestData.casem=new CaseManage();
//		int requestCode=getIntent().getIntExtra("caseInputRequestCode", -1);
//		if (requestCode==-1) { //编辑类型的请求
//			
//		}else if(requestCode==1) { //暂存回显
//			String tempCase=AppApplication.sp.getString("caseInputRequestCodeLocal", "");
//			if (!TextUtils.isEmpty(tempCase)) {
//				appRequestData=JSON.parseObject(tempCase, APPRequestModel.class);
//				if (appRequestData!=null) {
//					displayLocalDate();
//				}else {
//					initRequestData();
//				}
//			}
//		}
	}

	/**回显暂存到本地的信息**/
	private void displayCaseDate() {
		
//		appRequestData=new APPRequestModel<CaseBean>();
//		appRequestData.requestData=new CaseBean();
//		appRequestData.requestData.casem=new CaseManage();
//		CaseManage casem=appRequestData.requestData.casem;
		
		CaseManage casem ;
		if (caseBeanEntity!=null && caseBeanEntity.data!=null && caseBeanEntity.data.casem!=null) {
			appRequestData.requestData.casem=casem=caseBeanEntity.data.casem;
			displayRelationalInfo();
		}else {
			return;
		}
		
		
		if (casem.riskType!=0 && casem.riskType==1) {
			((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN1)).setSelection(0);//险种大类-财险
		}else if (casem.riskType!=0 && casem.riskType==2) {
			((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN1)).setSelection(1);//险种大类-水险
		}
		
		for (int i = 0; i < YYBDataList.size(); i++) { //归属机构
			if (casem.gsOrg.equals(YYBDataList.get(i).id+"")) {
				((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINNYYB)).setSelection(i+1);
				break;
			}
		}
		((EditText)creatCaseView.findViewById(R.id.CaseINPUT_edit1)).setText(casem.caseName);//案件名称
		
		
		for (int i = 0; i < dictEntity.data.is_yd.size(); i++) {
			if (casem.isYd.equals(dictEntity.data.is_yd.get(i).value+"")) { //是否异地
				((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN2)).setSelection(i);//是否异地
			}
		}
		((TextView)creatCaseView.findViewById(R.id.CaseINPUT_text2)).setText(casem.deputeDate);//委托日期
		
		for (int i = 0; i < dictEntity.data.rev_case_type.size(); i++) {
			if (casem.revCaseType.equals(dictEntity.data.rev_case_type.get(i).value+"")) {//接案方式
				((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN3)).setSelection(i);
			}
		}
		
		for (int i = 0; i < dictEntity.data.feiche_baoxian_type.size(); i++) {
			if (casem.feicheBaoxianType.equals(dictEntity.data.feiche_baoxian_type.get(i).value+"")) {
				((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN4)).setSelection(i);//险种
				Map<String, String> map=FCCaseTypeEntity.getTypeMap(i);
				List<String>  strings=caseTypeList;
				for (int j = 0; j < caseTypeList.size(); j++) {
					if (casem.caseType!=null && (casem.caseType+"").equals(map.get(caseTypeList.get(j)))) {
						((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN5)).setSelection(j);//险种分类
					}
				}
				break;
			}
		}
		@SuppressWarnings("unused")
		List<String>  strings=caseTypeList;
		for (int i = 0; i < dictEntity.data.business_type.size(); i++) {
			if (casem.businessType.equals(dictEntity.data.business_type.get(i))) {
				((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN6)).setSelection(i);//业务类型
			}
		}
		for (int i = 1; i < dZXXdataEntity.data.size(); i++) {
			if (casem.immenseOption.equals(dZXXdataEntity.data.get(i).id+"")) {
				((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN7)).setSelection(i); //大灾选项dZXXdataEntity
			}
		}
		
		for (int i = 0; i < dictEntity.data.case_industry.size(); i++) {
			if (casem.caseIndustry.equals(dictEntity.data.case_industry.get(i).value+"")) {
				((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN6)).setSelection(i); //所属行业
			}
		}

		((TextView)creatCaseView.findViewById(R.id.CaseINPUT_text4)).setText(casem.revCaseDate); //接案日期
		((EditText)creatCaseView.findViewById(R.id.CaseINPUT_edit3)).setText(casem.checkAdd);//查勘地点
		((EditText)creatCaseView.findViewById(R.id.CaseINPUT_edit4)).setText(casem.casePick); //报案摘要(2000字)
		
		/**委托信息***/
//		String wtName=((AutoCompleteTextView)wtView.findViewById(R.id.CaseINPUT_text5)).getText().toString(); //委托人
//		casem.deputePer=wtName;
		if (wtRenData!=null && wtRenData.data!=null && wtRenData.data.list!=null) {
			for (int i = 0; i < wtRenData.data.list.size(); i++) {
				if (casem.deputeId.equals(wtRenData.data.list.get(i).id+"")) {
					((AutoCompleteTextView)wtView.findViewById(R.id.CaseINPUT_text5)).setText(wtRenData.data.list.get(i).name);//委托人
					break;
				}
			}
		}
		((EditText)wtView.findViewById(R.id.CaseINPUT_edit5)).setText(casem.deputeLinkPer); //委托人联系人
		((EditText)wtView.findViewById(R.id.CaseINPUT_edit6)).setText(casem.deputeLinkTel); //委托人联系人电话
		((EditText)wtView.findViewById(R.id.CaseINPUT_edit7)).setText(casem.deputeLinkEmail); //委托人联系人Email
		((TextView)wtView.findViewById(R.id.CaseINPUT_edit8)).setText(casem.deputeLnikZz); //委托人联系人
		((EditText)wtView.findViewById(R.id.CaseINPUT_edit9)).setText(casem.deputeLinkPhone); //委托人联系人手机
		((EditText)wtView.findViewById(R.id.CaseINPUT_edit10)).setText(casem.deputeCaseNo); //委托方案件编号
		((EditText)wtView.findViewById(R.id.CaseINPUT_edit11)).setText(casem.payer); //付款方
		for (int i = 0; i < dictEntity.data.depute_sf.size(); i++) {
			if (casem.deputeSf.equals(dictEntity.data.depute_sf.get(i).value+"")) {
				((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN9)).setSelection(i); //委托人身份
			}
		}
		for (int i = 0; i < dictEntity.data.urgency_level.size(); i++) {
			if (casem.urgencyLevel.equals(dictEntity.data.urgency_level.get(i).value+"")) {
				((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN10)).setSelection(i); //紧急程度
			}	
		}
		
		((EditText)wtView.findViewById(R.id.CaseINPUT_edit12)).setText(casem.wtfYq); //委托方要求
//		casem.deputeItem=dictEntity.data..get(((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN10))
//				.getSelectedItemPosition()).value+"";//委托事项
		/**出险信息***/
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit13)).setText(casem.insurerCaseNo); //保险公司报案号
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit14)).setText(casem.insurerCaseLno); //保险公司立案号
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit15)).setText(casem.carNo); //车牌号
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit16)).setText(casem.remark); //出险单位
		((TextView)chuxianView.findViewById(R.id.CaseINPUT_text6)).setText(casem.dangerPeriod); //出险日期
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit17)).setText(casem.dangerAdd);//出险地点
		for (int i = 0; i < dictEntity.data.danger_res.size(); i++) {
			if (casem.dangerRes.equals(dictEntity.data.danger_res.get(i).value+"")) {
				((Spinner)chuxianView.findViewById(R.id.CaseINPUT_SPINNres)).setSelection(i);//出险原因
			}
		}
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit19)).setText(casem.lossAmout); //报损/索赔金额
		
		for (int i = 0; i < dictEntity.data.loss_currency.size(); i++) {
			if (casem.lossCurrency.equals(dictEntity.data.loss_currency.get(i).value+"")) {
				((Spinner)chuxianView.findViewById(R.id.CaseINPUT_SPINN12)).setSelection(i);//报损币别
			}
		}
		
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit20)).setText(casem.cxUintLink); //出险单位联系人
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit21)).setText(casem.lxPhone); //联系人电话
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit22)).setText(casem.emial); //邮箱
		((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit23)).setText(casem.cxJg); //出险经过
		((EditText)otherView.findViewById(R.id.CaseINPUT_edit24)).setText(casem.remarks); //备注(2000字)：
//		casem.status=1;
	}

	private void displayDictValue() {
		if (dictEntity==null){
			Dialog dialog = DialogUtil.getAlertOneButton(this,"无法获取字典信息，请联系管理员！",null);
			dialog.setOnDismissListener(dialog1 -> CaseInputActivity.this.finish());
			dialog.show();
		}
		/**险种大类： 1财险，2水险,spinner选中0是财险，1是水险**/
//		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN1)).setAdapter(new ArrayAdapter<String>
//		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.risk_type)));
		((TextView)creatCaseView.findViewById(R.id.CaseINPUT_text3)).setText(AppApplication.getUSER().data.name);
		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN1)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item,new String[]{"财险","水险"}));
		setonXZclick((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN1));
		/**是否异地：：**/
		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN2)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.is_yd)));
		/**接案方式：**/
		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN3)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.rev_case_type)));
		/**险种**/
		XZSpinner=((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN4));
		XZSpinner.setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.feiche_baoxian_type)));
		setXZSpinneronlick();
		/**业务类型：**/
		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN6)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.business_type )));
		
		/**所属行业：**/
		((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN8)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.case_industry )));
		/** 委托人身份：**/
		((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN9)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.depute_sf )));
		/** 紧急程度：**/
		((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN10)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.urgency_level )));
		/**委托事项： **/
		((TextView)wtView.findViewById(R.id.CaseINPUT_SPINN11)).setOnClickListener(this);
//		setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.depute_item )));
		/** 报损币别：**/
		((Spinner)chuxianView.findViewById(R.id.CaseINPUT_SPINN12)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.loss_currency )));
		/**出险原因**/
		((Spinner)chuxianView.findViewById(R.id.CaseINPUT_SPINNres)).setAdapter(new ArrayAdapter<String>
		(this, R.layout.spinner_item, dictEntity.getDictArr(dictEntity.data.danger_res )));
	}

/**下载案件信息**/
	private void downloadCase() {
		long id=getIntent().getLongExtra("id", 0);
		if (id!=0) {
			params = new ArrayList<NameValuePair>();
			loadDialog.setMessage("努力加载中……").show();
			APPRequestModel<Map<String, Long>> appre0 = new APPRequestModel<Map<String, Long>>();
			appre0.userToken = AppApplication.getUSER().data.targetOid;
			Map<String, Long> map=new HashMap<String, Long>();
			map.put("id", id);
			appre0.requestData = map;
			params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre0)));
			HttpUtils.requestPost(URLs.FC_GET_CASE_INFO, params, HttpRequestTool.FC_GET_CASE_INFO);
		}
	}

	private void setonXZclick(Spinner findViewById) {
		findViewById.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (arg2==1) {
					CaseInputActivity.this.startActivity(
							new Intent(CaseInputActivity.this, WaterCaseInputActivity.class));
					CaseInputActivity.this.finish();
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
				/**险种分类：**/
				caseTypeList=FCCaseTypeEntity.getTypeList(dictEntity.data.feiche_baoxian_type.get(arg2).value);
				((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN5)).setAdapter(new ArrayAdapter<String>
				(CaseInputActivity.this, R.layout.spinner_item, caseTypeList));
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ACFCINPUT_button://暂存报案录入信息
			SubmitCase(0);
			break;
		case R.id.CaseINPUT_relationalAdd://添加保险关系方
			addRelationals();
			break;
		case R.id.CaseINPUT_GGSAdd://添加作业人员信息
			choiceGGShelp.showChoiceDialog();
			break;
		case R.id.CaseINPUT_text5://选择委托人
			choiceWTRhelp.showChoiceDialog();
			break;
		case R.id.CaseINPUT_SPINN11://选择委托事项
			showChoiceWindow();
			break;

		default:
			break;
		}
		
	}

	/**选择的委托事项**/
	boolean []checkedItems;
	String[] wtinfos;
	String wtTempstr = "",wtidTempstr = "",wtTempstr1 = "",wtidTempstr1 = "";
	/**选择委托事项*/
	private void showChoiceWindow() {
		
		if (dictEntity.data.depute_item.size()>0) {
			wtinfos=new String[dictEntity.data.depute_item.size()];
			checkedItems=new boolean[dictEntity.data.depute_item.size()];
			for (int i = 0; i < wtinfos.length; i++) {
				wtinfos[i]=dictEntity.data.depute_item.get(i).getLabel();
				checkedItems[i]=false;
			}
			
			new AlertDialog.Builder(this).setTitle("选择委托项目！")
			.setMultiChoiceItems(wtinfos, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int postion, boolean arg2) {
					checkedItems[postion]=arg2;
					wtTempstr1 = "";wtidTempstr1 = "";
					for (int i = 0; i < wtinfos.length; i++) {
						if (checkedItems[i]) {
							wtTempstr1+=dictEntity.data.depute_item.get(i).getLabel()+",";
							wtidTempstr1+=dictEntity.data.depute_item.get(i).value+",";
						}
					}
				}
			}).setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
					wtTempstr = wtTempstr1.substring(0, wtTempstr1.length()-1);
					wtidTempstr = wtidTempstr1.substring(0, wtidTempstr1.length()-1);
					
					((TextView)wtView.findViewById(R.id.CaseINPUT_SPINN11)).setText(wtTempstr);
					appRequestData.requestData.casem.deputeItem=wtidTempstr;
				}
			}).create().show();
			
		}else {
			ToastUtil.showToastShort(this, "无法获取委托信息，请刷新界面或联系管理员！");
		}
	}

	/**提交接报案信息**/
	private void SubmitCase(int submitCode) {
		appRequestData.userToken=AppApplication.getUSER().data.targetOid;
		CaseManage casem;
		if (caseBeanEntity!=null && caseBeanEntity.data!=null && caseBeanEntity.data.casem!=null) {
			casem=caseBeanEntity.data.casem;
		}else {
			casem = new CaseManage();
		}
		
		casem.riskType=
				dictEntity.data.risk_type.get(
						((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN1))
				.getSelectedItemPosition()).value;//险种大类
		
		int orgPostion=((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINNYYB)).getSelectedItemPosition();
		if (orgPostion>0) {
			casem.gsOrg=YYBDataList.get(orgPostion-1).id+"";//归属机构
		}
		casem.caseName=((EditText)creatCaseView.findViewById(R.id.CaseINPUT_edit1)).getText().toString();//案件名称
		
		casem.isYd=dictEntity.data.is_yd.get(((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN2))
				.getSelectedItemPosition()).value+"";//是否异地
		
		casem.deputeDate=((TextView)creatCaseView.findViewById(R.id.CaseINPUT_text2)).getText().toString();//委托日期
		
		casem.revCaseType=dictEntity.data.rev_case_type.get(((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN3))
				.getSelectedItemPosition()).value+"";//接案方式
		
		casem.feicheBaoxianType=dictEntity.data.feiche_baoxian_type.get(((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN4))
				.getSelectedItemPosition()).value+"";//险种
		
		String caseTypeStr=caseTypeList.get((((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN5)).getSelectedItemPosition()));
		casem.caseType=FCCaseTypeEntity.getCaseTypeId(Integer.parseInt(casem.feicheBaoxianType),caseTypeStr);//险种分类
		
		casem.businessType=dictEntity.data.business_type.get(((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN6))
				.getSelectedItemPosition()).value; //业务类型
		
		int DzxxPostion=((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN7)).getSelectedItemPosition();
		if (DzxxPostion>0) {
			casem.immenseOption=dZXXdataEntity.data.get(DzxxPostion).id+"";//大灾选项dZXXdataEntity
		}
		casem.caseIndustry=dictEntity.data.case_industry.get(((Spinner)creatCaseView.findViewById(R.id.CaseINPUT_SPINN8))
						.getSelectedItemPosition()).value+""; //所属行业
		
		casem.recCasePer= AppApplication.getUSER().data.name;//接案人
		casem.reciverPhone=AppApplication.getUSER().data.mobile;//接案人电话
		
		casem.revCaseDate=((TextView)creatCaseView.findViewById(R.id.CaseINPUT_text4)).getText().toString(); //接案日期
		casem.checkAdd=((EditText)creatCaseView.findViewById(R.id.CaseINPUT_edit3)).getText().toString(); //查勘地点
		casem.casePick=((EditText)creatCaseView.findViewById(R.id.CaseINPUT_edit4)).getText().toString(); //报案摘要(2000字)
		
		/**委托信息***/
		String wtName=((AutoCompleteTextView)wtView.findViewById(R.id.CaseINPUT_text5)).getText().toString(); //委托人
		casem.deputePer=wtName;
		if (wtRenData!=null && wtRenData.data!=null && wtRenData.data.list!=null) {
			for (int i = 0; i < wtRenData.data.list.size(); i++) {
				if (wtName.equals(wtRenData.data.list.get(i).name)) {
					casem.deputeId = wtRenData.data.list.get(i).id+"";
					break;
				}
			}
		}
		casem.deputeLinkPer=((EditText)wtView.findViewById(R.id.CaseINPUT_edit5)).getText().toString(); //委托人联系人
		casem.deputeLinkTel=((EditText)wtView.findViewById(R.id.CaseINPUT_edit6)).getText().toString(); //委托人联系人电话
		casem.deputeLinkEmail=((EditText)wtView.findViewById(R.id.CaseINPUT_edit7)).getText().toString(); //委托人联系人Email
		casem.deputeLnikZz=((TextView)wtView.findViewById(R.id.CaseINPUT_edit8)).getText().toString(); //委托人联系人
		casem.deputeLinkPhone=((EditText)wtView.findViewById(R.id.CaseINPUT_edit9)).getText().toString(); //委托人联系人手机
		casem.deputeCaseNo=((EditText)wtView.findViewById(R.id.CaseINPUT_edit10)).getText().toString(); //委托方案件编号
		casem.payer=((EditText)wtView.findViewById(R.id.CaseINPUT_edit11)).getText().toString(); //付款方
		casem.deputeSf=dictEntity.data.depute_sf.get(((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN9))
				.getSelectedItemPosition()).value+""; //委托人身份
		casem.urgencyLevel=dictEntity.data.urgency_level.get(((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN10))
				.getSelectedItemPosition()).value+""; //紧急程度
		casem.wtfYq=((EditText)wtView.findViewById(R.id.CaseINPUT_edit12)).getText().toString(); //委托方要求
//		casem.deputeItem=dictEntity.data..get(((Spinner)wtView.findViewById(R.id.CaseINPUT_SPINN10))
//				.getSelectedItemPosition()).value+"";//委托事项
		/**出险信息***/
		casem.insurerCaseNo=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit13)).getText().toString(); //保险公司报案号
		casem.insurerCaseLno=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit14)).getText().toString(); //保险公司立案号
		casem.carNo=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit15)).getText().toString(); //车牌号
		casem.remark=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit16)).getText().toString(); //出险单位
		casem.dangerPeriod=((TextView)chuxianView.findViewById(R.id.CaseINPUT_text6)).getText().toString(); //出险日期
		casem.dangerAdd=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit17)).getText().toString();//出险地点
		casem.dangerRes=dictEntity.data.danger_res.get(((Spinner)chuxianView.findViewById(R.id.CaseINPUT_SPINNres))
				.getSelectedItemPosition()).value+"";//出险原因
		casem.lossAmout=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit19)).getText().toString(); //报损/索赔金额
		casem.lossCurrency=dictEntity.data.loss_currency.get(((Spinner)chuxianView.findViewById(R.id.CaseINPUT_SPINN12))
				.getSelectedItemPosition()).value+"";//报损币别
		casem.cxUintLink=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit20)).getText().toString(); //出险单位联系人
		casem.lxPhone=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit21)).getText().toString(); //联系人电话
		casem.emial=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit22)).getText().toString(); //邮箱
		casem.cxJg=((EditText)chuxianView.findViewById(R.id.CaseINPUT_edit23)).getText().toString(); //出险经过
		casem.remarks=((EditText)otherView.findViewById(R.id.CaseINPUT_edit24)).getText().toString(); //备注(2000字)：
		
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
		mustFullStrings.add(casem.riskType!=0?(casem.riskType+""):"" );
		mustFullStrings.add(casem.gsOrg );
		mustFullStrings.add(casem.caseName );
		mustFullStrings.add(casem.isYd );
		mustFullStrings.add(casem.deputeDate );
		mustFullStrings.add(casem.feicheBaoxianType );
		mustFullStrings.add(casem.caseType!=0?(casem.caseType+""):"" );
		mustFullStrings.add(casem.businessType!=0?(casem.businessType+""):"" );
		mustFullStrings.add(casem.immenseOption );
		mustFullStrings.add(casem.caseIndustry );
		mustFullStrings.add(casem.revCaseDate );
		mustFullStrings.add(casem.deputeId );
		mustFullStrings.add(casem.deputeLinkPer );
		mustFullStrings.add(casem.deputeLinkTel );
		mustFullStrings.add(casem.deputeLinkEmail );
		mustFullStrings.add(casem.urgencyLevel);
		mustFullStrings.add(casem.wtfYq );
		mustFullStrings.add(casem.insurerCaseNo );
//		mustFullStrings.add(casem.remark );
		mustFullStrings.add(casem.dangerPeriod );
		mustFullStrings.add(casem.dangerAdd );
		mustFullStrings.add(casem.dangerRes );
		mustFullStrings.add(casem.cxUintLink );
		mustFullStrings.add(casem.lxPhone );
		mustFullStrings.add(casem.cxJg );
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

	/**添加保险关系方*/
	private void addRelationals() {
		relationalInputView=inflater.inflate(R.layout.input_relational_info_view, null);
		final Spinner spinner=(Spinner)relationalInputView.findViewById(R.id.relationalINPUT_SPINN1);
		spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item,
				getResources().getStringArray(R.array.guanxiType)));
		DialogUtil.getDialogByViewOnlistener(this, relationalInputView, "保险关系方", 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				saveAndDisplay(spinner.getSelectedItemPosition());
			}
		}).show();
	}
	
	/**保存添加的保险关系方到对象中并显示列表*/
	private void saveAndDisplay(int postion){
		CaseDepute labsTemp=new CaseDepute();
		labsTemp.deputeSf=getResources().getStringArray(R.array.guanxiType)[postion];
		labsTemp.depute=((EditText)relationalInputView.findViewById(R.id.relationalINPUT_deptName)).getText().toString();
		labsTemp.cbfe=((EditText)relationalInputView.findViewById(R.id.relationalINPUT_CBAmount)).getText().toString();
		labsTemp.deputeLink=((EditText)relationalInputView.findViewById(R.id.relationalINPUT_deputeName)).getText().toString();
		labsTemp.workTel=((EditText)relationalInputView.findViewById(R.id.relationalINPUT_deputeLinkPhone)).getText().toString();
		labsTemp.eMail=((EditText)relationalInputView.findViewById(R.id.relationalINPUT_eMail)).getText().toString();
		labsTemp.pose=((EditText)relationalInputView.findViewById(R.id.relationalINPUT_address)).getText().toString();
		
		if (!TextUtils.isEmpty(labsTemp.depute) || !TextUtils.isEmpty(labsTemp.cbfe) || !TextUtils.isEmpty(labsTemp.deputeLink) || 
				!TextUtils.isEmpty(labsTemp.workTel) || !TextUtils.isEmpty(labsTemp.eMail) ||!TextUtils.isEmpty(labsTemp.pose)) {
			depsData.add(labsTemp);
			displayRelationalInfo();
		}else {
			ToastUtil.showToastShort(this, "未填写信息！");
		}
		appRequestData.requestData.deps=depsData;
	}

	/**显示添加的保险关系方信息
	 * @param labsTemp **/
	private void displayRelationalInfo() {
		relationalLayout.removeAllViews();
		for (int i = 0; i < appRequestData.requestData.deps.size(); i++) {
			CaseDepute labsTemp=appRequestData.requestData.deps.get(i);
			View view=inflater.inflate(R.layout.spinner_item, null);
			TextView teView=(TextView) view.findViewById(R.id.spinner_item_textone);
			teView.setText((i+1)+"、"+labsTemp.depute+" "+labsTemp.deputeLink+" "+labsTemp.workTel);
			teView.setGravity(Gravity.LEFT);
			relationalLayout.addView(view);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int buttonId) {
		// TODO Auto-generated method stub
		switch (buttonId) {
		case R.id.ACFCINPUT_btn_0:
			vpager.setCurrentItem(0);
			break;
		case R.id.ACFCINPUT_btn_1:
			vpager.setCurrentItem(1);
			break;
		case R.id.ACFCINPUT_btn_2:
			vpager.setCurrentItem(2);
			break;
		case R.id.ACFCINPUT_btn_3:
			vpager.setCurrentItem(3);
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
			radgrup.check(R.id.ACFCINPUT_btn_0);
			break;
		case 1:
			radgrup.check(R.id.ACFCINPUT_btn_1);
			break;
		case 2:
			radgrup.check(R.id.ACFCINPUT_btn_2);
			break;
		case 3:
			radgrup.check(R.id.ACFCINPUT_btn_3);
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
				setonclickandValue((TextView)chuxianView.findViewById(onclickIDs[i]));
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
				DatePickerDialog pickerDialog=new DatePickerDialog(CaseInputActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
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
					ToastUtil.showToastShort(CaseInputActivity.this, "选择公估师"+appRequestData.requestData.rels.get(i).userName+"为主办");
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
				CaseInputActivity.this.finish();
			}
		});
		dialog.show();
	}


}
