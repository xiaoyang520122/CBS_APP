package com.cninsure.cp.fc.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
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
import com.cninsure.cp.entity.DictEntity.DictDatas.publicData;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.APPRequestModel;
import com.cninsure.cp.entity.fc.APPResponseModel;
import com.cninsure.cp.entity.fc.CaseChaKan;
import com.cninsure.cp.entity.fc.CasePolicyLevel;
import com.cninsure.cp.entity.fc.CaseRelate;
import com.cninsure.cp.entity.fc.WorkBean;
import com.cninsure.cp.entity.fc.WorkBean.DataBean;
import com.cninsure.cp.utils.AddSurveyRecordUtil;
import com.cninsure.cp.utils.AlerViewUtil;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.PatternUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.LoadingDialog;

public class SurveyActivity extends BaseActivity implements OnPageChangeListener, OnCheckedChangeListener, OnClickListener {

	private List<NameValuePair> params;
	/**
	 * case_progress 案件进度
		case_filing 案件归档
		loss_currency 币别
		feiche_case_account 估损范围
		depute_sf 委托人身份
		business_type 业务类型
		rev_case_type 接案方式
		case_industry 所属行业
		danger_res 出险原因*/
	String[] ZDparamsStr=new String[]{"case_progress","case_filing","loss_currency","feiche_case_account"
			,"depute_sf","business_type","rev_case_type","case_industry","danger_res","file_type_id"};
	private LoadingDialog loadDialog;
	public DictEntity dictEntity;
	private View basicView,baodanView,baoanView,uploadView;
	private TextView actionTV1, actionTV2, actionTV3;
	private LayoutInflater inflater;
	private LinearLayout linearLayout,serveyRecordLayout,ggsListLayout;
	/**spinner ID集合*/
	private int[] spinnerId1=new int[]{R.id.SBI_SPINN1,R.id.SBI_SPINN2,R.id.SBI_SPINN3,R.id.SBI_SPINN4,R.id.SBI_SPINN5
			,R.id.SBI_SPINN6,R.id.SBI_SPINN7,R.id.SBI_SPINN8,R.id.SBI_SPINN9,R.id.SBI_SPINN10
			,R.id.SBI_SPINN11,R.id.SBI_SPINN12,R.id.SBI_SPINN13,R.id.SBI_SPINN14,R.id.SBI_SPINN15,R.id.SBI_SPINN16};
	
	/**必填字段ID集合*/
	private int[] requiredIds=new int[]{R.id.SBI_Edit2,R.id.SBI_Edit3,R.id.SBI_Edit4,R.id.SBI_Edit5,R.id.SBI_Edit6
			,R.id.SBI_Edit7,R.id.SBI_Edit8,R.id.SBI_Edit9,R.id.SBI_Edit10,R.id.SBI_Edit11,R.id.SBI_Edit12
			,R.id.SBI_Edit15,R.id.SBI_Edit16,R.id.SBI_Edit17,R.id.SBI_Edit23,R.id.SBI_Edit26,R.id.SBI_Edit27
			,R.id.SBI_Edit28,R.id.SBI_Edit29,R.id.SBI_Edit31,R.id.SBI_Edit32,R.id.SBI_Edit33,R.id.SBI_Edit34,R.id.SBI_Edit36};
	/**需要设置onclick事件的ID集合*/
	private int[] onclickIDs=new int[]{R.id.SBI_Edit20,R.id.SBI_Edit21,R.id.SBI_Edit22,R.id.SBI_Edit33};
	private List<Spinner> spinnerView1;
	private ViewPager vpager;
	private List<View> viewlist;
	private PagerAdapter pagerAdapter;
	private RadioGroup radgrup;
	public WorkBean workBean;
	private Button submitButton;
	private AlerViewUtil alertutil;
	private AddSurveyRecordUtil addURUtil;
	private SurveyActivityHelp surveyActivityHelp;
	/**如果当前作业没有保存时是没有workId的，上传影像资料前如果么有workId就先保存作业信息，成功后会传workId过来*/
	private String workId;
	/**是否上传影像资料*/
	private boolean isUpLFile=false;
	/**如果需要刷新首页列表就填写“NEW_ORDER”*/
	private String postEvent="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activty_fc_work);
		EventBus.getDefault().register(this);
		initaction();
		initView();
		download(1);
	}
	private void initaction() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();
	}

	private void setAction() {
		actionTV2.setText("非车查勘信息");
		actionTV3.setText("上传影像");
		actionTV3.setCompoundDrawables (null, null, null, null);
		actionTV1.setOnClickListener (new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				HintOut();
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(workId) || "null".equals(workId) ) {
					isUpLFile=true;
					submitData();
				}else {
					surveyActivityHelp.upload(getIntent().getStringExtra("caseNo"),workId);
				}
			}
		});
	}
	
	private void initView() {
		loadDialog=new LoadingDialog(this);
		vpager=(ViewPager) findViewById(R.id.ACFCWORK_viewpager);
		radgrup = (RadioGroup) findViewById(R.id.ACFCWORK_BtnG);
		submitButton=(Button) findViewById(R.id.ACFCWORK_button);
		
		submitButton.setOnClickListener(this);
		radgrup.setOnCheckedChangeListener(this);
		vpager.setOnPageChangeListener(this);
		inflater=LayoutInflater.from(this);
		viewlist=new ArrayList<View>();
		spinnerView1=new ArrayList<Spinner>(11);
		basicView=inflater.inflate(R.layout.fc_basic_info, null);
		baodanView=inflater.inflate(R.layout.fc_baodan_info, null);
		baoanView=inflater.inflate(R.layout.fc_baoan_info, null);
		uploadView = LayoutInflater.from(this).inflate(R.layout.imageupload_view, null);
		surveyActivityHelp=new SurveyActivityHelp(this,uploadView);
		setDataCheckOnclicEvent();
		/**设置邮件输入格式,错误是提示用户*/
		PatternUtil.setEmailInput((EditText)baoanView.findViewById(R.id.SBI_Edit28));
		
		linearLayout = (LinearLayout) baodanView.findViewById(R.id.SBI_LineaLayout);
		serveyRecordLayout = (LinearLayout) basicView.findViewById(R.id.SBI_surveyRecord_LineaLayout);
		ggsListLayout=(LinearLayout) basicView.findViewById(R.id.SBI_ggslist_LineaLayout);
		viewlist.add(basicView);
		viewlist.add(baodanView);
		viewlist.add(baoanView);
		viewlist.add(uploadView);
		initviewpageradapter();
		vpager.setAdapter(pagerAdapter);
		
		for (int i=0;i<spinnerId1.length;i++) {
			if (i<10) {
				Spinner viSpinner=(Spinner) basicView.findViewById(spinnerId1[i]);
				spinnerView1.add(viSpinner);
			}else {
				Spinner viSpinner=(Spinner) baoanView.findViewById(spinnerId1[i]);
				spinnerView1.add(viSpinner);
			}
		}
		baodanView.findViewById(R.id.SBI_Text6).setOnClickListener(this);
		basicView.findViewById(R.id.SBI_add_ckHistory_Tc).setOnClickListener(this);//新增查勘记录
		basicView.findViewById(R.id.SBI_add_ggs_TV).setOnClickListener(this);//新公估师和计算公估师比例
	}


	/**循环添加单击事件，并获取时间并赋值**/
	private void setDataCheckOnclicEvent() {
		for (int i = 0; i < onclickIDs.length; i++) {
			if (i<3) {
				setonclickandValue((TextView)baodanView.findViewById(onclickIDs[i]));
			}else {
				setonclickandValue((TextView)baoanView.findViewById(onclickIDs[i]));
			}
		}
	}
	
	/**获取时间并赋值**/
	public void setonclickandValue(final TextView textTv) {
		final Calendar cal = Calendar.getInstance();
		textTv.setOnClickListener(new OnClickListener() {
			@SuppressLint("InlinedApi")
			@Override
			public void onClick(View arg0) {
				DatePickerDialog pickerDialog=new DatePickerDialog(SurveyActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
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
	
	 public void getTimeByCalendar(){
	        Calendar cal = Calendar.getInstance();
	        int year = cal.get(Calendar.YEAR);//获取年份
	        int month=cal.get(Calendar.MONTH);//获取月份
	        int day=cal.get(Calendar.DATE);//获取日
	        int hour=cal.get(Calendar.HOUR);//小时
	        int minute=cal.get(Calendar.MINUTE);//分           
	        int second=cal.get(Calendar.SECOND);//秒
	        int WeekOfYear = cal.get(Calendar.DAY_OF_WEEK);//一周的第几天
	        System.out.println("现在的时间是：公元"+year+"年"+month+"月"+day+"日      "+hour+"时"+minute+"分"+second+"秒       星期"+WeekOfYear);
	    }
	
	/**1下载字典数据，2下载案件信息，3现在图片信息。**/
	private void download(int code){
		params = new ArrayList<NameValuePair>();
		loadDialog.setMessage("努力加载中……").show();
		switch (code) {
		case 1:
			APPRequestModel<String[]> appre = new APPRequestModel<String[]>();
			appre.userToken = AppApplication.getUSER().data.targetOid;
			appre.requestData = ZDparamsStr;
			params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre)));
			HttpUtils.requestPost(URLs.FC_GET_DICT_LIST, params, HttpRequestTool.FC_GET_DICT_LIST);
			break;
		case 2:
			APPRequestModel<Map<String, Integer>> appre0 = new APPRequestModel<Map<String, Integer>>();
			appre0.userToken = AppApplication.getUSER().data.targetOid;
			Map<String, Integer> map=new HashMap<String, Integer>();
			int id=getIntent().getIntExtra("id", 0);
			map.put("id", id);
			appre0.requestData = map;
			params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre0)));
			HttpUtils.requestPost(URLs.FC_GET_WORK_INFO, params, HttpRequestTool.FC_GET_WORK_INFO);
			break;
		case 3:/**历史上次影像文件**/
			APPRequestModel<Map<String, Object>> appre1 = new APPRequestModel<Map<String, Object>>();
			appre1.userToken = AppApplication.getUSER().data.targetOid;
			Map<String, Object> map1=new HashMap<String, Object>();
			map1.put("caseNo", workBean.data.m.caseNo);
			if (!TextUtils.isEmpty(workId)) {
				map1.put("id",workId);
			}else if (null==workBean.data.work || null==workBean.data.work.id) {
				map1.put("id", 0);
			}else {
				map1.put("id", Long.valueOf(workBean.data.work.id));
			}
			appre1.requestData = map1;
			params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre1)));
			HttpUtils.requestPost(URLs.DOWNLOAD_WORK_FILE, params, HttpRequestTool.DOWNLOAD_WORK_FILE);
			break;

		default:
			break;
		}
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void evnetdata(List<NameValuePair> value){
		int rcode = Integer.valueOf(value.get(0).getName());
		if (rcode == HttpRequestTool.FC_GET_DICT_LIST || rcode == HttpRequestTool.FC_GET_WORK_INFO
				|| rcode == HttpRequestTool.DOWNLOAD_WORK_FILE || rcode == HttpRequestTool.FC_SAVE_WORK_INFO ) {
			loadDialog.dismiss();
		}
		switch (CheckHttpResult.checkList(value, this)) {
		case HttpRequestTool.FC_GET_DICT_LIST:
			String valuestr=replaceString(value.get(0).getValue());;
			dictEntity=JSON.parseObject(valuestr, DictEntity.class);
			download(2);
			displayDictValue();
			break;
			
		case HttpRequestTool.FC_GET_WORK_INFO:
			workBean=JSON.parseObject(value.get(0).getValue(), WorkBean.class);
			download(3);
			displayWorkInfo();
			break;
			
		case HttpRequestTool.FC_SAVE_WORK_INFO:
			saveIsSsuccess(value.get(0).getValue());
			break;
			
		case HttpRequestTool.DOWNLOAD_WORK_FILE:
			surveyActivityHelp.displayHistoryFile(value.get(0).getValue());
				break;

		default:
			break;
		}
	}
	
	/**将字符串中的转义字符替换*/
	private String replaceString(String value) {
		value=value.replace("&quot;", "\"");
		value=value.replace("&apos;", ",");
		value=value.replace("&amp;", "&");
		value=value.replace("&gt;=", ">=");
		value=value.replace("&lt;=", "≤");
		value=value.replace("&gt;", ">");
		value=value.replace("&lt;", "<");
		value=value.replace("&le;", "≤");
		return value;
	}
	/**检查是否保存成功**/
	private void saveIsSsuccess(String value) {
		@SuppressWarnings("unchecked")
		APPResponseModel<String> responsedata=JSON.parseObject(value, APPResponseModel.class);
		if ("0".equals(responsedata.code)) {
			//返回数据格式{"code":"0","message":"成功","exception":"","data":"51010"}
			workId=responsedata.data;
			postEvent="NEW_ORDER";
			if (isUpLFile) {//如果是为了上传影像资料儿保存的信息，就不提示用户直接跳到上传影像资料方法
				isUpLFile=false;
				if (TextUtils.isEmpty(workId)) {
					DialogUtil.getAlertOneButton(this, "数据保存失败，请联系管理员！ 错误信息："+responsedata.exception, null).show();
				}else {
					surveyActivityHelp.upload(getIntent().getStringExtra("caseNo"),workId);
				}
			}else {//不是保存影像资料儿保存的就提示用户保存成功
				DialogUtil.getAlertOneButton(this, "数据保存成功！！", null).show();
			}
		}else {
			DialogUtil.getAlertOneButton(this, "数据保存失败，请联系管理员！ 错误信息："+responsedata.exception, null).show();
		}
	}
	
	/**设置spinner信息**/
	private void displayDictValue() {
		List<publicData> psinnerValues;
		for (int i = 0; i < spinnerView1.size(); i++) {
			
			switch (spinnerId1[i]) {
			case R.id.SBI_SPINN1:
				psinnerValues=dictEntity.data.case_progress;
				break;
			case R.id.SBI_SPINN2:
				psinnerValues=dictEntity.data.case_filing;
				break;
			case R.id.SBI_SPINN10:
				psinnerValues=dictEntity.data.feiche_case_account;
				break;
			case R.id.SBI_SPINN11:
				psinnerValues=dictEntity.data.depute_sf;
				break;
			case R.id.SBI_SPINN12:
				psinnerValues=dictEntity.data.rev_case_type;
				break;
			case R.id.SBI_SPINN13:
				psinnerValues=dictEntity.data.case_industry;
				break;
			case R.id.SBI_SPINN14:
				psinnerValues=dictEntity.data.business_type;
				break;
			case R.id.SBI_SPINN15:
				psinnerValues=dictEntity.data.danger_res;
				break;

			default:
				psinnerValues=dictEntity.data.loss_currency;
				break;
			}
			spinnerView1.get(i).setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, 
					dictEntity.getDictArr(psinnerValues)));
		}
	}
	
	/**显示案件信息
	 * basicView=inflater.inflate(R.layout.fc_basic_info, null);
		baodanView=inflater.inflate(R.layout.fc_baodan_info, null);
		baoanView=inflater.inflate(R.layout.fc_baoan_info, null);
	 * **/
	private void displayWorkInfo() {
		if (workBean.code!=0) {
			DialogUtil.getAlertOneButton(this, workBean.message+",请联系管理员！案件ID："+getIntent().getIntExtra("id", 0), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					SurveyActivity.this.finish();
				}
			}).show();
			return;
		}
		((EditText)basicView.findViewById(R.id.SBI_Edit1)).setText(workBean.data.work.filed3); 
		if (null!=workBean.data) {
			displayGGSList();
			displaySurveyRecord();
			if (null!=workBean.data.work) {
				workId=workBean.data.work.id+"";
				
				if (TextUtils.isEmpty(workBean.data.work.mainChecker)) {
					((TextView)basicView.findViewById(R.id.SBI_Text1)).setText(AppApplication.getUSER().data.name);//默认主办公估师
				}else {
					((TextView)basicView.findViewById(R.id.SBI_Text1)).setText(workBean.data.work.mainChecker);//主办公估师
				}
				
				if (TextUtils.isEmpty(workBean.data.work.checkAdd)) {
					((EditText)basicView.findViewById(R.id.SBI_Edit2)).setText(workBean.data.m.checkAdd);
				}else {
					((EditText)basicView.findViewById(R.id.SBI_Edit2)).setText(workBean.data.work.checkAdd);
				}
				((EditText)basicView.findViewById(R.id.SBI_Edit4)).setText(workBean.data.work.bsAmount+"");
				((EditText)basicView.findViewById(R.id.SBI_Edit5)).setText(workBean.data.work.gsAmount+"");
				((EditText)basicView.findViewById(R.id.SBI_Edit6)).setText(workBean.data.work.readyAmount+"");
				((EditText)basicView.findViewById(R.id.SBI_Edit7)).setText(workBean.data.work.dsAmount+"");
				((EditText)basicView.findViewById(R.id.SBI_Edit8)).setText(workBean.data.work.lsAmount+"");
				((EditText)basicView.findViewById(R.id.SBI_Edit9)).setText(workBean.data.work.dbAmount+"");
				((EditText)basicView.findViewById(R.id.SBI_Edit10)).setText(workBean.data.work.cbAmount+"");
				((EditText)basicView.findViewById(R.id.SBI_Edit11)).setText(workBean.data.work.yuguAmount+"");
				
				int position=getPositionByValue(workBean.data.work.caseProgress,dictEntity.data.case_progress );
						((Spinner)basicView.findViewById(R.id.SBI_SPINN1)).setSelection(position, true); //案件进度
				
				position=getPositionByValue(workBean.data.work.caseFiling,dictEntity.data.case_filing );
				((Spinner)basicView.findViewById(R.id.SBI_SPINN2)).setSelection(position, true);
				
				position=getPositionByValue(workBean.data.work.bsCurr,dictEntity.data.loss_currency );
				((Spinner)basicView.findViewById(R.id.SBI_SPINN3)).setSelection(position, true);//报损金额
				
				position=getPositionByValue(workBean.data.work.gsCurr,dictEntity.data.loss_currency);
				((Spinner)basicView.findViewById(R.id.SBI_SPINN4)).setSelection(position, true);//估损金额
				
				position=getPositionByValue(workBean.data.work.readyCurr,dictEntity.data.loss_currency);
				((Spinner)basicView.findViewById(R.id.SBI_SPINN5)).setSelection(position, true);//准备金
				
				position=getPositionByValue(workBean.data.work.dsCurr,dictEntity.data.loss_currency);
				((Spinner)basicView.findViewById(R.id.SBI_SPINN6)).setSelection(position, true);//定损金额
				
				position=getPositionByValue(workBean.data.work.lsCurr,dictEntity.data.loss_currency);
				((Spinner)basicView.findViewById(R.id.SBI_SPINN7)).setSelection(position, true);//理算金额
				
				position=getPositionByValue(workBean.data.work.dbCurr,dictEntity.data.loss_currency);
				((Spinner)basicView.findViewById(R.id.SBI_SPINN8)).setSelection(position, true);//担保金额
				
				position=getPositionByValue(workBean.data.work.cbCurr,dictEntity.data.loss_currency);
				((Spinner)basicView.findViewById(R.id.SBI_SPINN9)).setSelection(position, true);//残值
				
				position=getPositionByValue(workBean.data.work.filed4,dictEntity.data.feiche_case_account );
				((Spinner)basicView.findViewById(R.id.SBI_SPINN10)).setSelection(position, true);//估损范围
			}else {
				((TextView)basicView.findViewById(R.id.SBI_Text1)).setText(AppApplication.getUSER().data.name);//默认主办公估师
			}
				
			if (null!=workBean.data.m) {
				com.cninsure.cp.entity.fc.CaseManage tm=workBean.data.m;
				((EditText)basicView.findViewById(R.id.SBI_Edit3)).setText(workBean.data.work.dangerRes);
				((EditText)baoanView.findViewById(R.id.SBI_Edit26)).setText(workBean.data.m.deputeLinkTel);//委托方联系人电话
				((EditText)baoanView.findViewById(R.id.SBI_Edit27)).setText(workBean.data.m.deputeLinkPhone);
				((EditText)baoanView.findViewById(R.id.SBI_Edit28)).setText(workBean.data.m.deputeLinkEmail);
				((EditText)baoanView.findViewById(R.id.SBI_Edit29)).setText(workBean.data.m.deputeLnikZz);
				((EditText)baoanView.findViewById(R.id.SBI_Edit31)).setText(workBean.data.m.recCasePer);
				((EditText)baoanView.findViewById(R.id.SBI_Edit32)).setText(workBean.data.m.insurerCaseLno);
				((TextView)baoanView.findViewById(R.id.SBI_Edit33)).setText(workBean.data.m.dangerPeriod);//出险日期
				((EditText)baoanView.findViewById(R.id.SBI_Edit34)).setText(workBean.data.m.dangerAdd);
				((EditText)baoanView.findViewById(R.id.SBI_Edit35)).setText(workBean.data.m.lossAmout+"");
				((EditText)baoanView.findViewById(R.id.SBI_Edit36)).setText(workBean.data.m.deputeLinkPer);
				
				int position=getPositionByValue(workBean.data.m.deputeSf,dictEntity.data.depute_sf  );
				((Spinner)baoanView.findViewById(R.id.SBI_SPINN11)).setSelection(position, true);//委托人身份
				
				position=getPositionByValue(workBean.data.m.revCaseType,dictEntity.data.rev_case_type );//报案方式
				((Spinner)baoanView.findViewById(R.id.SBI_SPINN12)).setSelection(position, true);
				
				position=getPositionByValue(workBean.data.m.caseIndustry,dictEntity.data.case_industry );
				((Spinner)baoanView.findViewById(R.id.SBI_SPINN13)).setSelection(position, true);//所属行业
				
				position=getPositionByValue(workBean.data.m.businessType+"",dictEntity.data.business_type );
				((Spinner)baoanView.findViewById(R.id.SBI_SPINN14)).setSelection(position, true);//业务类型
				
				position=getPositionByValue(workBean.data.m.dangerRes,dictEntity.data.danger_res );
				((Spinner)baoanView.findViewById(R.id.SBI_SPINN15)).setSelection(position, true);//出险原因
				
				position=getPositionByValue(workBean.data.m.lossCurrency,dictEntity.data.loss_currency );
				((Spinner)baoanView.findViewById(R.id.SBI_SPINN16)).setSelection(position, true);//报损币别
			}
			if (null!=workBean.data.op) {
				@SuppressWarnings("unused")
				WorkBean workB=workBean;
				((EditText)baodanView.findViewById(R.id.SBI_Edit12)).setText(workBean.data.op.policyNumber+"");
				((EditText)baodanView.findViewById(R.id.SBI_Edit13)).setText(workBean.data.op.applicantName);
				String applicantNamestr=((EditText)baodanView.findViewById(R.id.SBI_Edit13)).getText().toString();
				((EditText)baodanView.findViewById(R.id.SBI_Edit14)).setText(workBean.data.op.applicantLinkTel);
				((EditText)baodanView.findViewById(R.id.SBI_Edit15_1)).setText(workBean.data.op.insuredName);
				if (!TextUtils.isEmpty(workBean.data.op.insuredBussiness)) {
					((EditText)baodanView.findViewById(R.id.SBI_Edit15)).setText(workBean.data.op.insuredBussiness);
				}else {
					((EditText)baodanView.findViewById(R.id.SBI_Edit15)).setText(workBean.data.m.cxUintLink);
				}
				
				if (!TextUtils.isEmpty(workBean.data.op.insuredBussiness)) {
					((EditText)baodanView.findViewById(R.id.SBI_Edit16)).setText(workBean.data.op.insuredLinkTel);
				}else {
					((EditText)baodanView.findViewById(R.id.SBI_Edit16)).setText(workBean.data.m.lxPhone);
				}
				
				((EditText)baodanView.findViewById(R.id.SBI_Edit17)).setText(workBean.data.op.insuredAdd);
				((EditText)baodanView.findViewById(R.id.SBI_Edit18)).setText(workBean.data.op.freeOdds+"");
				((EditText)baodanView.findViewById(R.id.SBI_Edit19)).setText(workBean.data.op.absoluteDeductible);
				((TextView)baodanView.findViewById(R.id.SBI_Edit20)).setText(workBean.data.op.insuranceDate);
				((TextView)baodanView.findViewById(R.id.SBI_Edit21)).setText(workBean.data.op.protectionDate);
				((TextView)baodanView.findViewById(R.id.SBI_Edit22)).setText(workBean.data.op.endDate);
				((EditText)baodanView.findViewById(R.id.SBI_Edit23)).setText(workBean.data.op.insuranceAmount+"");
				((EditText)baodanView.findViewById(R.id.SBI_Edit24)).setText(workBean.data.op.premium);
				((EditText)baodanView.findViewById(R.id.SBI_Edit25)).setText(workBean.data.op.specialAgreement);//特别预定
			}
			if (null!=workBean.data.lel) {
				displayLelDate();
			}
			((TextView)basicView.findViewById(R.id.SBI_Text2)).setText(workBean.data.yuguAmountByUser+"");
			((TextView)basicView.findViewById(R.id.SBI_Text3)).setText(workBean.data.yuguAmountByOrg+"");
			((TextView)basicView.findViewById(R.id.SBI_Text4)).setText(workBean.data.lossAmountByUser+"");
			((TextView)basicView.findViewById(R.id.SBI_Text5)).setText(workBean.data.lossAmountByOrg+"");
			
		}
	}
	
	/** case_progress 案件进度
	case_filing 案件归档
	loss_currency 币别
	feiche_case_account 估损范围
	depute_sf 委托人身份
	business_type 业务类型
	rev_case_type 接案方式
	case_industry 所属行业
	danger_res 出险原因
	
	根据value得到spinner中的位置 */
	private int getPositionByValue(String value,List<publicData> listdata){
		for (int i = 0; i < listdata.size(); i++) {
			String valuestr=listdata.get(i).value+"";
			if (value.equals(valuestr)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().post(postEvent);
		EventBus.getDefault().unregister(this);
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
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@SuppressLint("NewApi")
	@Override
	public void onPageSelected(int pi) {
		switch (pi) {
		case 0:
			submitButton.setVisibility(View.VISIBLE);
			radgrup.check(R.id.ACFCWORK_btn_0);
			break;
		case 1:
			submitButton.setVisibility(View.VISIBLE);
			radgrup.check(R.id.ACFCWORK_btn_1);
			break;
		case 2:
			submitButton.setVisibility(View.VISIBLE);
			radgrup.check(R.id.ACFCWORK_btn_2);
			break;
		case 3:
			radgrup.check(R.id.ACFCWORK_btn_3);
			submitButton.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup grpView, int buttonId) {
		switch (buttonId) {
		case R.id.ACFCWORK_btn_0:
			vpager.setCurrentItem(0);
			break;
		case R.id.ACFCWORK_btn_1:
			vpager.setCurrentItem(1);
			break;
		case R.id.ACFCWORK_btn_2:
			vpager.setCurrentItem(2);
			break;
		case R.id.ACFCWORK_btn_3:
			vpager.setCurrentItem(3);
			break;
		default:
			break;
		}
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.SBI_Text6:
			getAddData(0);
			break;
		case R.id.ACFCWORK_button:
			submitData();
			break;
		case R.id.SBI_add_ckHistory_Tc:
			showAddCKHistoryDialog(-1);
			break;
		case R.id.SBI_add_ggs_TV:
			toAddGgsActivity();
			break;

		default:
			break;
		}
	}

	/**编辑公估师（新增和计算贡献比例）**/
	public final static int GGS_EDIT=77;
	/**跳转到添加公估师和计算公估师比例的界面**/
	private void toAddGgsActivity() {
		Intent intent=new Intent(this, EditSurveyGgsActivity.class);
		intent.putExtra("WorkBean", workBean);
		startActivityForResult(intent, GGS_EDIT);
	}
	
	/**弹出新增查勘记录对话框Dialog*/
	 private void showAddCKHistoryDialog(int code) {
		 addURUtil=new AddSurveyRecordUtil(this,code);
		 addURUtil.showlayout();
	}
	/**弹出框录入保单承保险别 
	  *code=0是新增 其他是编辑（集合下标）**/
	private void getAddData(final int position) {
		alertutil=new AlerViewUtil(this,R.layout.xb_amount_alert_view);
		DialogUtil.getDialogByViewOnlistener(this, alertutil.getView(),"保单承保标的险别",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				CasePolicyLevel cplel=alertutil.getdata();
				float insuranceAm=Float.parseFloat(cplel.insuranceAmount);
				if (insuranceAm == 0 || TextUtils.isEmpty(cplel.insuranceName)) {
					DialogUtil.getAlertOneButton(SurveyActivity.this, "险别名称和保险金额不能为空，请重录！", null).show();
					return;
				}
				if (position==-1) {
					workBean.data.lel.add(cplel);
				}else {
					workBean.data.lel.add(position, cplel);
				}
				displayLelDate(); //显示
			}
		}).show();
	}

	private void displayLelDate(){
		float isAmount = 0;
		for (CasePolicyLevel lelTemp:workBean.data.lel) {
			float insuranceAm2=Float.parseFloat(lelTemp.insuranceAmount);
			isAmount+=insuranceAm2;
		}
		((EditText)baodanView.findViewById(R.id.SBI_Edit23)).setText(isAmount+"");//显示保险总金额
		
		linearLayout.removeAllViews();
		if (null!=workBean.data && null!=workBean.data.lel) {
			for (int i = 0; i <workBean.data.lel.size(); i++) {
				CasePolicyLevel lelTemp=workBean.data.lel.get(i);
				View view = inflater.inflate(R.layout.xb_amount_item, null);
				((TextView)view.findViewById(R.id.XBAMOUNT_item_text1)).setText("险别名称："+lelTemp.insuranceName);
				((TextView)view.findViewById(R.id.XBAMOUNT_item_text2)).setText("保险金额："+lelTemp.insuranceAmount);
				viewSetOnclickEvent(view,i);
				linearLayout.addView(view);
			}
		}
	}
	

	private void viewSetOnclickEvent(View v, final int i) {
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DialogUtil.getItemDialog(SurveyActivity.this, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface df, int position) {
						if (position==0) {
							getAddData(i);
						}else {
							workBean.data.lel.remove(i);
							displayLelDate(); //显示
						}
					}
				}, new String[]{"编辑","删除"}).show();
			}
		});
	}
	/**提交案件信息**/
	private void submitData() {
//		if (seeRequired()) {
//			DialogUtil.getAlertOneButton(this, "请填写所有的必填项目后提交！", null).show();
//			return;
//		}
		int errorCount=0;
		int [] tempCount=seeRequired();
		String hint="";
		String[] hintStr=new String[]{".查勘信息中有",".保单信息中有",".报案信息中有"};
		for (int i = 0; i < tempCount.length; i++) {
			errorCount+=tempCount[i];
			if (tempCount[i]>0) {
				hint+=" "+hintStr[i]+tempCount[i]+"个";
			}
		}
		if (errorCount>0) {
			DialogUtil.getAlertOnelistener(this, hint+"必填项目未填写，是否保存？", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					loadingupData();
				}
			}, "继续保存", "补全信息", null).show();
		} else {
			loadingupData();
		}
	}
	
	public void loadingupData(){
		loadDialog.setMessage("努力处理中……").show();
		try {
			saveWorkData();
		} catch (ParseException e) {
			loadDialog.dismiss();
			ToastUtil.showToastLong(getApplication(), "获取信息时，日期格式化错误");
			e.printStackTrace();
		}
		params=new ArrayList<NameValuePair>();
		APPRequestModel<DataBean> appre0 = new APPRequestModel<DataBean>();
		appre0.userToken = AppApplication.getUSER().data.targetOid;
		appre0.requestData = workBean.data;
		params.add(new BasicNameValuePair("requestData", JSON.toJSONString(appre0)));
		HttpUtils.requestPost(URLs.FC_SAVE_WORK_INFO, params, HttpRequestTool.FC_SAVE_WORK_INFO);
	}
	
	/**检查必填字段填写情况 requiredIds
	 * basicView,baodanView,baoanView**/
	private int[] seeRequired(){
		int basiccount = 0,baodancount=0,baoancount=0;
		for (int i = 0; i < requiredIds.length; i++) {
			if (i<10) {
				if (((EditText)basicView.findViewById(requiredIds[i])).getText().toString().isEmpty()) {
					basiccount++;
//					return true;
				}
			}else if (i<15) {
				if (((EditText)baodanView.findViewById(requiredIds[i])).getText().toString().isEmpty()) {
					baodancount++;
//					return true;
				}
			}else if(i<requiredIds.length) {
				if (requiredIds[i]==R.id.SBI_Edit33) {
					if (((TextView)baoanView.findViewById(requiredIds[i])).getText().toString().isEmpty()) {
						baoancount++;
//						return true;
					}
				}else {
					if (((EditText)baoanView.findViewById(requiredIds[i])).getText().toString().isEmpty()) {
						baoancount++;
//						return true;
					}
				}
			}//spinner值为空
		}
		if (((Spinner)basicView.findViewById(R.id.SBI_SPINN1)).getSelectedItemPosition()==0) 
			basiccount++;
		if (((Spinner)basicView.findViewById(R.id.SBI_SPINN2)).getSelectedItemPosition()==0) 
			basiccount++;
		if (((Spinner)baoanView.findViewById(R.id.SBI_SPINN11)).getSelectedItemPosition()==0) 
			baoancount++;
//				return true;
			if (((Spinner)baoanView.findViewById(R.id.SBI_SPINN12)).getSelectedItemPosition()==0) 
				baoancount++;
//				return true;
			if (((Spinner)baoanView.findViewById(R.id.SBI_SPINN13)).getSelectedItemPosition()==0)
				baoancount++; 
//				return true;
			if (((Spinner)baoanView.findViewById(R.id.SBI_SPINN14)).getSelectedItemPosition()==0) 
				baoancount++;
//				return true;
		String inString=((EditText)baodanView.findViewById(R.id.SBI_Edit24)).getText().toString();
		if (inString.isEmpty()) {
			baodancount++;
//			return true;
		}else {
			float in=Float.parseFloat(inString);
			if (in==0.0) {//保险总金额不能为零
				baodancount++;
//				return true;
			}
		}
		
		return new int[]{basiccount,baodancount,baoancount};
	}
	
	/**将案件信息装到workBean
	 * @throws ParseException */
	@SuppressLint("SimpleDateFormat")
	private void saveWorkData() throws ParseException {
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");//HH:mm:ss
		SimpleDateFormat sfS=new SimpleDateFormat("yyyy-MM-dd");
		workBean.data.work.filed3=((EditText)basicView.findViewById(R.id.SBI_Edit1)).getText().toString();
		workBean.data.work.mainChecker=((TextView)basicView.findViewById(R.id.SBI_Text1)).getText().toString();//主办公估师
		workBean.data.work.bsAmount=((EditText)basicView.findViewById(R.id.SBI_Edit4)).getText().toString();
		workBean.data.work.gsAmount=((EditText)basicView.findViewById(R.id.SBI_Edit5)).getText().toString();
		workBean.data.work.readyAmount=((EditText)basicView.findViewById(R.id.SBI_Edit6)).getText().toString();
		workBean.data.work.dsAmount=((EditText)basicView.findViewById(R.id.SBI_Edit7)).getText().toString();
		workBean.data.work.lsAmount=((EditText)basicView.findViewById(R.id.SBI_Edit8)).getEditableText().toString();
		workBean.data.work.dbAmount=((EditText)basicView.findViewById(R.id.SBI_Edit9)).getText().toString();
		workBean.data.work.cbAmount=((EditText)basicView.findViewById(R.id.SBI_Edit10)).getText().toString();
		workBean.data.work.yuguAmount=((EditText)basicView.findViewById(R.id.SBI_Edit11)).getText().toString();
		workBean.data.work.checkAdd=((EditText)basicView.findViewById(R.id.SBI_Edit2)).getText().toString();
		workBean.data.work.dangerRes=((EditText)basicView.findViewById(R.id.SBI_Edit3)).getText().toString();
		workBean.data.m.deputeLinkTel=((EditText)baoanView.findViewById(R.id.SBI_Edit26)).getText().toString();
		workBean.data.m.deputeLinkPhone=((EditText)baoanView.findViewById(R.id.SBI_Edit27)).getText().toString();
		workBean.data.m.deputeLinkEmail=((EditText)baoanView.findViewById(R.id.SBI_Edit28)).getText().toString();
		if (!PatternUtil.isEmail(workBean.data.m.deputeLinkEmail)) {
			DialogUtil.getErrDialog(this, "邮箱地址错误！").show();
			return;
		}
		
		workBean.data.m.deputeLnikZz=	((EditText)baoanView.findViewById(R.id.SBI_Edit29)).getText().toString();
		workBean.data.m.recCasePer=	((EditText)baoanView.findViewById(R.id.SBI_Edit31)).getText().toString();
		workBean.data.m.insurerCaseLno=	((EditText)baoanView.findViewById(R.id.SBI_Edit32)).getText().toString();
		
		String dateString=((TextView)baoanView.findViewById(R.id.SBI_Edit33)).getText().toString();
		if (dateString.length()>0) {
			workBean.data.m.dangerPeriod= sf.format(sfS.parse(dateString));
		}
		workBean.data.m.dangerAdd=	((EditText)baoanView.findViewById(R.id.SBI_Edit34)).getText().toString();
		workBean.data.m.lossAmout=	((EditText)baoanView.findViewById(R.id.SBI_Edit35)).getText().toString();
		workBean.data.m.deputeLinkPer=	((EditText)baoanView.findViewById(R.id.SBI_Edit36)).getText().toString();
		workBean.data.op.policyNumber=	((EditText)baodanView.findViewById(R.id.SBI_Edit12)).getText().toString();
		workBean.data.op.applicantName=	((EditText)baodanView.findViewById(R.id.SBI_Edit13)).getText().toString();
		workBean.data.op.applicantLinkTel=	((EditText)baodanView.findViewById(R.id.SBI_Edit14)).getText().toString();
		workBean.data.op.insuredName=	((EditText)baodanView.findViewById(R.id.SBI_Edit15_1)).getText().toString();
		workBean.data.op.insuredBussiness=	((EditText)baodanView.findViewById(R.id.SBI_Edit15)).getText().toString();
		workBean.data.op.insuredLinkTel=	((EditText)baodanView.findViewById(R.id.SBI_Edit16)).getText().toString();
		workBean.data.op.insuredAdd=	((EditText)baodanView.findViewById(R.id.SBI_Edit17)).getText().toString();
		workBean.data.op.freeOdds=	((EditText)baodanView.findViewById(R.id.SBI_Edit18)).getText().toString();
		workBean.data.op.absoluteDeductible=	((EditText)baodanView.findViewById(R.id.SBI_Edit19)).getText().toString();

		dateString=((TextView)baodanView.findViewById(R.id.SBI_Edit20)).getText().toString();
		if (dateString.length()>0) {
			workBean.data.op.insuranceDate=sf.format(sfS.parse(dateString));}
		dateString=((TextView)baodanView.findViewById(R.id.SBI_Edit21)).getText().toString();
		if (dateString.length()>0) {
		workBean.data.op.protectionDate=sf.format(sfS.parse(dateString));}
		dateString=((TextView)baodanView.findViewById(R.id.SBI_Edit22)).getText().toString();
		if (dateString.length()>0) {
		workBean.data.op.endDate=sf.format(sfS.parse(dateString));}
		
		workBean.data.op.insuranceAmount=	((EditText)baodanView.findViewById(R.id.SBI_Edit23)).getText().toString();
		workBean.data.op.premium=	((EditText)baodanView.findViewById(R.id.SBI_Edit24)).getText().toString();
		workBean.data.op.specialAgreement=	((EditText)baodanView.findViewById(R.id.SBI_Edit25)).getText().toString();
		
		int position=((Spinner)basicView.findViewById(R.id.SBI_SPINN1)).getSelectedItemPosition();
		workBean.data.work.caseProgress=dictEntity.data.case_progress.get(position).value+"";//案件进度
		
		position=((Spinner)basicView.findViewById(R.id.SBI_SPINN2)).getSelectedItemPosition();
		workBean.data.work.caseFiling=dictEntity.data.case_filing.get(position).value+"";//案件归档
		
		position=((Spinner)basicView.findViewById(R.id.SBI_SPINN3)).getSelectedItemPosition();
		workBean.data.work.bsCurr=dictEntity.data.loss_currency.get(position).value+"";//报损金额
		
		position=((Spinner)basicView.findViewById(R.id.SBI_SPINN4)).getSelectedItemPosition();
		workBean.data.work.gsCurr=dictEntity.data.loss_currency.get(position).value+"";//估损金额
		
		position=((Spinner)basicView.findViewById(R.id.SBI_SPINN5)).getSelectedItemPosition();
		workBean.data.work.readyCurr=dictEntity.data.loss_currency.get(position).value+"";//准备金
		
		position=((Spinner)basicView.findViewById(R.id.SBI_SPINN6)).getSelectedItemPosition();
		workBean.data.work.dsCurr=dictEntity.data.loss_currency.get(position).value+"";//定损金额
		
		position=((Spinner)basicView.findViewById(R.id.SBI_SPINN7)).getSelectedItemPosition();
		workBean.data.work.lsCurr=dictEntity.data.loss_currency.get(position).value+"";//理算金额
		
		position=((Spinner)basicView.findViewById(R.id.SBI_SPINN8)).getSelectedItemPosition();
		workBean.data.work.dbCurr=dictEntity.data.loss_currency.get(position).value+"";//担保金额
		
		position=((Spinner)basicView.findViewById(R.id.SBI_SPINN9)).getSelectedItemPosition();
		workBean.data.work.cbCurr=dictEntity.data.loss_currency.get(position).value+"";//残值
		
		position=((Spinner)basicView.findViewById(R.id.SBI_SPINN10)).getSelectedItemPosition();
		workBean.data.work.filed4=dictEntity.data.feiche_case_account.get(position).value+"";//估损范围
		
		position=((Spinner)baoanView.findViewById(R.id.SBI_SPINN11)).getSelectedItemPosition();
		workBean.data.m.deputeSf=dictEntity.data.depute_sf.get(position).value+"";//委托人身份
		
		position=((Spinner)baoanView.findViewById(R.id.SBI_SPINN12)).getSelectedItemPosition();
		workBean.data.m.revCaseType=dictEntity.data.rev_case_type.get(position).value+"";//接案方式
		
		position=((Spinner)baoanView.findViewById(R.id.SBI_SPINN13)).getSelectedItemPosition();
		workBean.data.m.caseIndustry=dictEntity.data.case_industry.get(position).value+"";//所属行业
		
		position=((Spinner)baoanView.findViewById(R.id.SBI_SPINN14)).getSelectedItemPosition();
		workBean.data.m.businessType=dictEntity.data.business_type.get(position).value;//业务类型
		
		position=((Spinner)baoanView.findViewById(R.id.SBI_SPINN15)).getSelectedItemPosition();
		workBean.data.m.dangerRes=dictEntity.data.danger_res.get(position).value+"";//出险原因
		
		position=((Spinner)baoanView.findViewById(R.id.SBI_SPINN16)).getSelectedItemPosition();
		workBean.data.m.lossCurrency=dictEntity.data.loss_currency.get(position).value+"";//报损币别
		
	}
	/** case_progress 案件进度
	case_filing 案件归档
	loss_currency 币别
	feiche_case_account 估损范围
	depute_sf 委托人身份
	business_type 业务类型
	rev_case_type 接案方式
	case_industry 所属行业
	danger_res 出险原因
	
//	根据spinner中的位置得到value */
//	private int getValueByPosition(String value,List<publicData> listdata){
//		return return listdata.get(i).value;;
//	}
	
	/**在SurveyActivityHelp帮助类里面启动系统选择文件，在这里获取图片后返回给帮助类**/
	public final int FILE_SELECT_CODE=10001;//必须与SurveyActivityHelp中定义的一模一样
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
	        if (resultCode == RESULT_OK) {
//	            Uri uri = data.getData();
//	        	if (requestCode==GGS_EDIT) { //
					surveyActivityHelp.eventresultcode(requestCode, resultCode, data);
//				}
	        } else if(resultCode == EditSurveyGgsActivity.ResultCode) {
	        	if (requestCode==GGS_EDIT) { //公估师编辑界面返回公估师列表
	        		workBean.data.rels=(List<CaseRelate>) data.getSerializableExtra("ResultMyrels");
	        		displayGGSList();
				}
			}          
	super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**上传影像资料成功后刷新界面**/
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventmeth(String successCode){
		if ("UPLOAD_SUCCESS".equals(successCode)) {
			clearStorage();
			download(3);
		}
	}
	
	/**上传成功后清空暂存数据*/
	private void clearStorage(){
		Editor editor=AppApplication.sp.edit();
		editor.putString(getIntent().getIntExtra("id", 0)+"FCfileDataList", "");
		editor.commit();
		editor.clear();
	}
	
	/**添加查勘记录信息
	 * @param ckbean **/
	public void addSurveyRecord(CaseChaKan ckbean,int code) {
		if (workBean.data.ck==null) { //接受查勘记录的集合不能为空
			workBean.data.ck=new ArrayList<CaseChaKan>();
		}
		if (code==-1) {//-1代表新增
			workBean.data.ck.add(ckbean);
		}else if (code>=0){
			workBean.data.ck.set(code, ckbean);
		}
		displaySurveyRecord();
	}
	
	/**显示全部查勘记录信息到LinearLayout*/
	private void displaySurveyRecord() {
		serveyRecordLayout.removeAllViews();
		if (null!=workBean.data && null!=workBean.data.ck) {
			for (int i = 0; i <workBean.data.ck.size(); i++) {
				CaseChaKan ckTemp=workBean.data.ck.get(i);
				View view = inflater.inflate(R.layout.display_survey_record_layout, null);
				((TextView) view.findViewById(R.id.DSPSRADDLO_ckdate)).setText(ckTemp.ckdate);// 查勘时间
				((TextView) view.findViewById(R.id.DSPSRADDLO_ckjl)).setText(ckTemp.ckjl);// 查勘记录
				((TextView) view.findViewById(R.id.DSPSRADDLO_cyry)).setText(ckTemp.cyry);// 参与人员
				((TextView) view.findViewById(R.id.DSPSRADDLO_gsze)).setText(ckTemp.gsze);// 工时总额
				((TextView) view.findViewById(R.id.DSPSRADDLO_renshu)).setText(ckTemp.renshu);// 人数
				((TextView) view.findViewById(R.id.DSPSRADDLO_tianshu)).setText(ckTemp.tianshu );// 天数
//				viewSetOnclickEvent(view,i);
				setonclickde(view,i);
				serveyRecordLayout.addView(view);
			}
		}
	}
	
	/**设置查勘记录的删除和编辑功能**/
	private void setonclickde(final View view, final int i) {
		/*删除记录*/
		view.findViewById(R.id.DSPSRADDLO_delete).setOnClickListener(new OnClickListener() { // 删除功能
			@Override
			public void onClick(View arg0) {
				showDeleteDialog(i);
			}
		});
		/*编辑记录*/
		view.findViewById(R.id.DSPSRADDLO_edit).setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg0) {
				showAddCKHistoryDialog(i);
			}
		});
	}
	
	/**删除指定的查勘记录**/
	private void showDeleteDialog(final int i) {
		DialogUtil.getAlertOnelistener(this, "确定删除该条记录吗？", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				workBean.data.ck.remove(i);
				displaySurveyRecord();
			}
		}).show();
	}
	public CaseChaKan getchaKandata(int code) {
		return workBean.data.ck.get(code);
	}
	
	/**显示公估师列表*/
	private void displayGGSList(){
		ggsListLayout.removeAllViews();
		if (null!=workBean.data && null!=workBean.data.rels) {
			for (int i = 0; i <workBean.data.rels.size(); i++) {
				CaseRelate relsTemp=workBean.data.rels.get(i);
				View view = inflater.inflate(R.layout.ggslist_survey_layout_item, null);
				((TextView) view.findViewById(R.id.ggslsli_nameAndDept)).setText(relsTemp.userName+" | "+relsTemp.homeInstitution);// 公估师名称及归属部门
				try {
					((TextView) view.findViewById(R.id.ggslsli_zxBan)).setText(relsTemp.relType.subSequence(0, relsTemp.relType.length()-1));// 主协办
				} catch (Exception e) {
					((TextView) view.findViewById(R.id.ggslsli_zxBan)).setText("协");
					e.printStackTrace();
				}
				((TextView) view.findViewById(R.id.ggslsli_diaoduTime)).setText(relsTemp.dispatchDate);// 调度时间
				((TextView) view.findViewById(R.id.ggslsli_diaoduStatus)).setText(relsTemp.dispatchStatus);// 调度状态
				((TextView) view.findViewById(R.id.ggslsli_ggsphoneNumber)).setText(relsTemp.linkTel);// 公估师电话
				if (TextUtils.isEmpty(relsTemp.contributionRatio)) {
					((TextView) view.findViewById(R.id.ggslsli_gxbl)).setText("0" );// 贡献比例
				}else {
					((TextView) view.findViewById(R.id.ggslsli_gxbl)).setText(relsTemp.contributionRatio );// 贡献比例
				}
				setDeleteOnClick(view.findViewById(R.id.ggslsli_deleteGgs),relsTemp,i);
				ggsListLayout.addView(view);
			}
		}
	}
	
	/**删除协办
	 * @param relsTemp2 **/
	private void setDeleteOnClick(View view, CaseRelate relsTemp2, final int i) {
		if (relsTemp2.relType.equals("主办")) {
			view.setVisibility(View.GONE);
		}else {
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					showDeleteggsDialog(i);
				}
			});
		}
	}
	
	/**删除公估师**/
	private void showDeleteggsDialog(final int i) {
		DialogUtil.getAlertOnelistener(this, "确定删除公估师吗？", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				workBean.data.rels.remove(i);
				displayGGSList();
			}
		}).show();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (surveyActivityHelp!=null) {
			surveyActivityHelp.refreshExlist();
		}
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
				SurveyActivity.this.finish();
			}
		});
		dialog.show();
	}
}
