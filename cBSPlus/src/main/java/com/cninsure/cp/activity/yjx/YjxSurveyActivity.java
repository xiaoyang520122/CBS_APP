package com.cninsure.cp.activity.yjx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.yjx.ImagePathUtil;
import com.cninsure.cp.entity.yjx.InsuranceTypeUtil;
import com.cninsure.cp.entity.yjx.SurveyType;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanEntity;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanInjuredTable;
import com.cninsure.cp.entity.yjx.YjxCaseDispatchTable;
import com.cninsure.cp.entity.yjx.YjxCaseWorkEntity;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.FileChooseUtil;
import com.cninsure.cp.utils.FileDownOpenUtil2;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ImageDisplayUtil;
import com.cninsure.cp.utils.ImageUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.OpenFileUtil;
import com.cninsure.cp.utils.PhotoPathUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ViewDisableUtil;
import com.cninsure.cp.view.MarqueeTextView;

public class YjxSurveyActivity extends BaseActivity implements OnCheckedChangeListener, OnClickListener, OnPageChangeListener {
	private View wtInfoView, workView; // 三个现象卡内容View
	private ListView riskersView;
	private TextView actionTV1, actionTV2, actionTV3; // 顶部返回按钮，标题和暂存按钮。
//	private TextView picCameraTv, picAlbumTv; // 拍照和相册选择照片
	private ViewPager vpager;
	private List<View> viewlist; // ViewPager加载的View集合
	private PagerAdapter pagerAdapter;
	private RadioGroup radgrup; // 切换选项卡的RadioGroup
	private LayoutInflater inflater;
	private Button storageButton ,submitButton; // 提交按钮
	private YjxCaseWorkEntity workDataEn;
	/** 报案信息 */
	private YjxCaseBaoanEntity baoanDataEn;
	/**任务调度信息**/
	private YjxCaseDispatchTable dispatchEn;
	/**上传的委托资料*/
	private LinearLayout wtFileLinear,workFileLinear;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private InjAdapter adapter;
	private List<YjxCaseBaoanInjuredTable> injuredData;
	private String FilePath;
	/**等待上传的委托影响资料*/
	private List<String> TempFileArrs,workTempFileArrs;
	/**委托信息或者是作业信息中的fileUrls*/
	private String fileUrlStrs,surveyDataTem;
	private Spinner TypeSp ;
	private  Spinner smallTypeSp;
	private ArrayAdapter<String> smallTypeAdatper;
	private List<String> smallTypeValues = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//避免ListView重绘导致点击Edittext点击编辑，弹出软键盘后失去焦点问题
		setContentView(R.layout.activty_yjx_work);
		EventBus.getDefault().register(this);
		initaction();
		initView();
		downLoadData();
	}
	
	/**
	 * 如果是查看作业详情的就不能编辑
	 */
	@Override
	protected void onResume() {
		if ("seeWorkInfo".equals(getIntent().getStringExtra("requestType"))) {
			ViewDisableUtil.disableSubControls((ScrollView)wtInfoView);
			ViewDisableUtil.disableSubControls((ScrollView)workView);
			for (int i = 0; i < injuredData.size(); i++) {
				try {
					ViewDisableUtil.disableSubControls((LinearLayout)riskersView.getChildAt(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		super.onResume();
	}

	private void initaction() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();
	}

	private void initView() {
		vpager = (ViewPager) findViewById(R.id.ACYJXWORK_viewpager);
		radgrup = (RadioGroup) findViewById(R.id.ACYJXWORK_BtnG);
		submitButton = (Button) findViewById(R.id.ACYJXWORK_submitButton);
		storageButton = (Button) findViewById(R.id.ACYJXWORK_button);

		submitButton.setOnClickListener(this);
		storageButton.setOnClickListener(this);
		actionTV3.setOnClickListener(this);
		radgrup.setOnCheckedChangeListener(this);
		// vpager.setOnPageChangeListener(this);
		inflater = LayoutInflater.from(this);
		vpager.setOnPageChangeListener(this);
		viewlist = new ArrayList<View>();
		// spinnerView1 = new ArrayList<Spinner>(11);
		
		wtInfoView = inflater.inflate(R.layout.yjx_wt_info_view, null);
		riskersView = (ListView) inflater.inflate(R.layout.listview_layout, null);
		workView = inflater.inflate(R.layout.yjx_work_info_view, null);
		setlookModeSpinner();//设置Spinner选择调查模式的适配器
		wtInfoViewInit();
		workInfoViewInit();
		wtFileLinear = (LinearLayout) wtInfoView.findViewById(R.id.YJXWWV_wtPhoto);
		workFileLinear = (LinearLayout) workView.findViewById(R.id.YJXWV_workFileList);
		viewlist.add(wtInfoView);
		viewlist.add(riskersView);
		viewlist.add(workView);
		initviewpageradapter();
		vpager.setAdapter(pagerAdapter);
		ListViewAddFooterView(); //添加FooterView并设置添加伤者信息的单击事件
		disPlayriskersView(); //显示伤者信息
		TempFileArrs = new ArrayList<String>();
		workTempFileArrs = new ArrayList<String>();
	}
	
	/**设置工作内容选项卡*/
	private void workInfoViewInit() {

		workView.findViewById(R.id.YJXWV_chocieBycamera).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FilePath = PhotoPathUtil.getPictureCreatePath(baoanDataEn.uid,YjxSurveyActivity.this);
				PickPhotoUtil.cameraPhotoToUrl(YjxSurveyActivity.this,FilePath,PickPhotoUtil.PHOTO_REQUEST_CAMERAPHOTO_W);
			}
		});
		workView.findViewById(R.id.YJXWV_chocieByAlbum).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PickPhotoUtil.albumPhoto(YjxSurveyActivity.this, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_W);
			}
		});
		SetTextUtil.setOnclickShowLongDatePickerDialog((TextView)workView.findViewById(R.id.YJXWV_lookTime), this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ACTION_V_RTV:
			status = 0;
			submitChoiceFile();
			break;
		case R.id.ACYJXWORK_button:
			status = 0;
			submitChoiceFile();
			break;
		case R.id.ACYJXWORK_submitButton:
			 status = 1;
			submitChoiceFile();
			break;

		default:
			break;
		}
	}

/**判断并上传选择文件*/ 
	 private void submitChoiceFile() {
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (TempFileArrs.size()>0) {
			for (int i = 0; i < TempFileArrs.size(); i++) {
				params.add(new BasicNameValuePair("1", TempFileArrs.get(i)));
			}
		}
		if (workTempFileArrs.size()>0) {
			for (int i = 0; i < workTempFileArrs.size(); i++) {
				params.add(new BasicNameValuePair("2", workTempFileArrs.get(i)));
			}
		}
		if (params.size()>0) { //有需要上传的文件就先上传文件
			FileUploadUtil.uploadYjxFile(params, this,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					submitWorkInfo();
				}
			});
		}else {//没有需要提交的文件就直接上传作业、委托信息。
			submitWorkInfo();
		}
	}
	 
	 /**
	  * 提交作业信息
	  * status =0是暂存，=1是提交审核
	 * @throws JSONException 
	  */
	 private boolean IsFull = true;
	 private List<NameValuePair> wtparams;
	 /** =0是暂存，=1是提交审核*/
	 private int status;
	 public void submitWorkInfo() { 
		 final List<NameValuePair> params=new ArrayList<NameValuePair>();
		 IsFull = true;
		 if (status==1 && injuredData!=null && injuredData.size()<0) {
			 DialogUtil.getErrDialog(this, "没有填写伤者信息！").show();
			 return;
		}
		 for (int j = 0; j < injuredData.size(); j++) {
			 if (injuredData==null || injuredData.get(j)==null || TextUtils.isEmpty(injuredData.get(j).diagnostic) || TextUtils.isEmpty(injuredData.get(j).hospital) || 
					 TextUtils.isEmpty(injuredData.get(j).idCard) || TextUtils.isEmpty(injuredData.get(j).name) || 
					 TextUtils.isEmpty(injuredData.get(j).sex)) {
				 if (status==1) { //只有提交的时候才判断必填
					 DialogUtil.getErrDialog(this, "伤者信息明细填写不全！").show();
					 return;
				}
			}
		 }
		 if (status==1 && fileUrlStrs.length()<3) {
			 DialogUtil.getErrDialog(this, "请上传委托资料文件后操作！").show();
			 return;
		}else if (status==1 && surveyDataTem.length()<3) {
			DialogUtil.getErrDialog(this, "请上传作业调查资料后操作！").show();
			 return;
		}
		 
		 try {
			 for (int j = 0; j < injuredData.size(); j++) {
				 if (injuredData.get(j)==null) {
					continue;
				}
//				 params.add(new BasicNameValuePair("injuredList["+j+"].caseId",injuredData.get(j).caseId+""));
				 params.add(new BasicNameValuePair("injuredList["+j+"].diagnostic",getTextInfo(injuredData.get(j).diagnostic)));
				 params.add(new BasicNameValuePair("injuredList["+j+"].hospital",getTextInfo(injuredData.get(j).hospital)));
//				 params.add(new BasicNameValuePair("injuredList["+j+"].id",injuredData.get(j).id+""));
				 params.add(new BasicNameValuePair("injuredList["+j+"].idCard",getTextInfo(injuredData.get(j).idCard)));
				 params.add(new BasicNameValuePair("injuredList["+j+"].name",getTextInfo(injuredData.get(j).name)));
				 params.add(new BasicNameValuePair("injuredList["+j+"].sex",getTextInfo(injuredData.get(j).sex)));
//				 params.add(new BasicNameValuePair("injuredList["+j+"].workId",injuredData.get(j).workId+""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		params.add(new BasicNameValuePair("userId",AppApplication.getUSER().data.userId+""));
		params.add(new BasicNameValuePair("dispatchId",dispatchEn.id+""));
		params.add(new BasicNameValuePair("dispatchUid",dispatchEn.uid));
		params.add(new BasicNameValuePair("surveyTime",getTextInfo((TextView)workView.findViewById(R.id.YJXWV_lookTime),true)));
		params.add(new BasicNameValuePair("surveyAddress",getTextInfo((EditText)workView.findViewById(R.id.YJXWV_VisitingAddress),true)));
		params.add(new BasicNameValuePair("surveyType",getTextInfo((Spinner)workView.findViewById(R.id.YJXWV_lookMode),true,"未选择调查方式！")));
		params.add(new BasicNameValuePair("surveyDescription",getTextInfo((EditText)workView.findViewById(R.id.YJXWV_Investigation),true)));
		params.add(new BasicNameValuePair("surveyConclusion",getTextInfo((EditText)workView.findViewById(R.id.YJXWV_VisitResults),true)));
		params.add(new BasicNameValuePair("fileUrls",fileUrlStrs));
		params.add(new BasicNameValuePair("surveyData",surveyDataTem));
		params.add(new BasicNameValuePair("status",status+""));
		if (TextUtils.isEmpty(((TextView)workView.findViewById(R.id.YJXWV_lookTime)).getText().toString())) { //暂存和提交调查时间都不能为空！
			DialogUtil.getErrDialog(this, "请填写作业信息中的调查时间！").show();
			return;
		}
		//委托信息
		wtparams=new ArrayList<NameValuePair>();
		wtparams.add(new BasicNameValuePair("userId",AppApplication.getUSER().data.userId+""));
		String typeName = getTextInfo(TypeSp,true,"未选择险种方式！");
		String typeId = InsuranceTypeUtil.getTypeIdByName(typeName);
		String SmallType = getTextInfo(smallTypeSp,true,"未选择险种细类方式！");
		String smallTypeId = InsuranceTypeUtil.getSmallTypeId(typeId, SmallType);
		wtparams.add(new BasicNameValuePair("insuranceBigType",typeName)); //险种大类
		wtparams.add(new BasicNameValuePair("insuranceBigTypeId",typeId)); //险种大类id
		wtparams.add(new BasicNameValuePair("insuranceSmallType",SmallType)); //险种细类
		wtparams.add(new BasicNameValuePair("insuranceSmallTypeId",smallTypeId)); //险种细类Id
		wtparams.add(new BasicNameValuePair("id",baoanDataEn.id+""));
		wtparams.add(new BasicNameValuePair("riskDate",getTextInfo((TextView)wtInfoView.findViewById(R.id.YJXWWV_caseTime),true)));
		wtparams.add(new BasicNameValuePair("riskAddress",getTextInfo((EditText)wtInfoView.findViewById(R.id.YJXWWV_caseAddress),true)));
		wtparams.add(new BasicNameValuePair("wtCotact",getTextInfo((EditText)wtInfoView.findViewById(R.id.YJXWWV_wtContacts),true)));
		wtparams.add(new BasicNameValuePair("wtContactTel",getTextInfo((EditText)wtInfoView.findViewById(R.id.YJXWWV_wtContactsPhone),true)));
		wtparams.add(new BasicNameValuePair("policyNo",getTextInfo((EditText)wtInfoView.findViewById(R.id.YJXWWV_baoDanNo),true)));
		wtparams.add(new BasicNameValuePair("insuredPerson",getTextInfo((EditText)wtInfoView.findViewById(R.id.YJXWWV_InsuredName),true)));
		wtparams.add(new BasicNameValuePair("insuredPersonTel",getTextInfo((EditText)wtInfoView.findViewById(R.id.YJXWWV_InsuredPhone),true)));
		wtparams.add(new BasicNameValuePair("riskReason",getTextInfo((EditText)wtInfoView.findViewById(R.id.YJXWWV_caseReasons),true)));
		
		if (status==0) {
			LoadDialogUtil.setMessageAndShow(this, "请稍后……");
			HttpUtils.requestPost(URLs.POST_YJX_WORK_SAVE, params, HttpRequestTool.POST_YJX_WORK_SAVE);
		}else if (IsFull) {
			LoadDialogUtil.setMessageAndShow(this, "请稍后……");
			HttpUtils.requestPost(URLs.POST_YJX_WORK_SAVE, params, HttpRequestTool.POST_YJX_WORK_SAVE);
		}else {
			DialogUtil.getErrDialog(this, "请填写所有必填内容后再提交！").show();
		}
		 
	 }
	 
	 private String getTextInfo(String value) {
			if (value!=null) {
					return value;
			}else {
				return "";
			}
	}

	 private String getTextInfo(Spinner spView, boolean ismust,String hintText) {
//		 List<String> st = SurveyType.getType();
		 String dateStr = spView.getSelectedItem().toString();
			if (!TextUtils.isEmpty(dateStr)) {
					return dateStr;
			}else if (ismust) {
				DialogUtil.getErrDialog(this, hintText).show();
				IsFull = false;
			}
		return "";
	}

	/**如果字段为空，而且是必填就报错
		 * @param IsFull 是否为空
		 * @param ismust 是否必填
		 * @param view */
	 private String getTextInfo(TextView view, boolean ismust) {
		 String dateStr = view.getText().toString();
			if (!TextUtils.isEmpty(dateStr)) {
					return dateStr;
			}else if (ismust) {
				view.setError("未填写！");
				IsFull = false;
			}
			return "";
	}

	/**如果字段为空，而且是必填就报错
	 * @param IsFull 是否为空
	 * @param ismust 是否必填
	 * @param view */
	private Date getDateInfo(TextView view, boolean ismust) {
		String dateStr = view.getText().toString();
		if (!TextUtils.isEmpty(dateStr)) {
			try {
				Date date = sf.parse(dateStr);
				return date;
			} catch (ParseException e) {
				view.setError("填写格式错误");
				e.printStackTrace();
			}
		}else if (ismust) {
			view.setError("未填写！");
			IsFull = false;
		}
		return null;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (resultCode != Activity.RESULT_OK) {
			 return;
		 }
	        switch (requestCode) {
	        //**委托文件**/
	        case PickPhotoUtil.PHOTO_REQUEST_CAMERAPHOTO:// 委托相机
	        	ImageUtil.compressBmp(this, data, FilePath);
	        	TempFileArrs.add(FilePath);
	        	disWtPlayImage(wtFileLinear, null , 3);
	            break;
	        case PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO:// 委托文件
	        	FilePath = FileChooseUtil.getInstance(this).getChooseFileResultPath(data.getData());
	        	ImageUtil.compressBmp(this, data, FilePath); // TODO ：测试，后面需要删除
	        	TempFileArrs.add(FilePath);
	        	disWtPlayImage(wtFileLinear, null , 3);
	            break;
	           //**作业文件**/
	        case PickPhotoUtil.PHOTO_REQUEST_CAMERAPHOTO_W:// 作业相机
	        	ImageUtil.compressBmp(this, data, FilePath);
	        	workTempFileArrs.add(FilePath);
	        	disPlayWorkfile();
	            break;
	        case PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO_W:// 作业文件
	        	FilePath = FileChooseUtil.getInstance(this).getChooseFileResultPath(data.getData());
	        	workTempFileArrs.add(FilePath);
	        	disPlayWorkfile();
	            break;
	        default:
	        	break;
	        }
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	
	private void wtInfoViewInit(){
		wtInfoView.findViewById(R.id.YJXWWV_chocieBycamera).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FilePath = PhotoPathUtil.getPictureCreatePath(baoanDataEn.uid,YjxSurveyActivity.this);
				PickPhotoUtil.cameraPhotoToUrl(YjxSurveyActivity.this,FilePath,PickPhotoUtil.PHOTO_REQUEST_CAMERAPHOTO);
			}
		});
		wtInfoView.findViewById(R.id.YJXWWV_chocieByAlbum).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PickPhotoUtil.albumPhoto(YjxSurveyActivity.this, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO);
			}
		});
		final TextView wtTv = (TextView)wtInfoView.findViewById(R.id.YJXWWV_caseTime);
		wtTv .setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateChoiceUtil.showLongDatePickerDialog(YjxSurveyActivity.this, wtTv);
			}
		});
		
		TypeSp = (Spinner)wtInfoView.findViewById(R.id.YJXWWV_caseType);
		smallTypeSp = (Spinner)wtInfoView.findViewById(R.id.YJXWWV_caseSmallType);
		final List<NameValuePair>  typeMap=InsuranceTypeUtil.getinsuranceTypeCollection();
		setadapter(TypeSp,typeMap);
		setSmallTypeAdapter(typeMap,0);
		TypeSp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//				setadapter(smallTypeSp,InsuranceTypeUtil.getinsuranceSmallTypeCollection(typeMap.get(arg2).getName()));
				List<NameValuePair> smalltypeMap = InsuranceTypeUtil.getinsuranceSmallTypeCollection(typeMap.get(arg2).getName());
				smallTypeValues.clear();
				smallTypeValues.addAll(InsuranceTypeUtil.MapToList(smalltypeMap));
				smallTypeAdatper.notifyDataSetChanged();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}
	
	public void setSmallTypeAdapter(List<NameValuePair> typeMap, int point){
		List<NameValuePair> smalltypeMap = InsuranceTypeUtil.getinsuranceSmallTypeCollection(typeMap.get(point).getName());
		smallTypeValues.clear();
		smallTypeValues.addAll(InsuranceTypeUtil.MapToList(smalltypeMap));
		smallTypeAdatper = new ArrayAdapter<String>(YjxSurveyActivity.this, android.R.layout.simple_list_item_1, smallTypeValues);
		smallTypeSp.setAdapter(smallTypeAdatper);
	}

	/**给Spinner添加适配器*/
	private void setadapter(Spinner typeSp, List<NameValuePair> maplist) {
		ArrayAdapter<String> spAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, InsuranceTypeUtil.MapToList(maplist));
		typeSp.setAdapter(spAdapter);
	}

	/**为riskersView添加*/
	private void ListViewAddFooterView() {
		riskersView.setOnItemClickListener(new OnItemClickListener() { //点击编辑伤者信息
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				getAndDisplayInjured(arg2);
			}
		});
		
		View view = inflater.inflate(R.layout.add_layout, null);
		view.findViewById(R.id.add_Layout_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				addNullInjured();
				getAndDisplayInjured(-1);
			}
		});
//		riskersView.setEmptyView(inflater.inflate(R.layout.yjx_injured_empty, null)); //放出来后addFooterView不显示，无法添加伤者信息
		riskersView.addFooterView(view);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler addInjuredHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int code= msg.what;
			getAndDisplayInjured(code);
		}
		
	};
	
	/**弹出窗口填写伤者信息，并在传递监听事件中完成对当前Activity中伤者信息的添加并显示
	 *itemId>-1代表修改，==-1表示添加 */
	private void getAndDisplayInjured(final int itemId){
		AddInjuredUtil addInjuredUtil = new AddInjuredUtil();
		if (itemId==-1) {
			addInjuredUtil.getInjured(YjxSurveyActivity.this,null, getDismisListener(addInjuredUtil,itemId));
		}else {
			addInjuredUtil.getInjured(YjxSurveyActivity.this,injuredData.get(itemId), getDismisListener(addInjuredUtil,itemId));
		}
	}
	
	/***/
	private DialogInterface.OnDismissListener getDismisListener(final AddInjuredUtil addInjuredUtil,final int itemId){
		DialogInterface.OnDismissListener listener =new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				if (addInjuredUtil.InjValue.name!=null) {
					if (itemId==-1) { //新增
						injuredData.add(addInjuredUtil.InjValue);
					}else { //修改
						
					}
					adapter.notifyDataSetChanged();
				}
			}
		};
		return listener;
	}
	
	/**添加一个空的伤者信息对象到伤者信息集合中*/
	private void addNullInjured(){
		if (injuredData==null) {
			injuredData = new ArrayList<YjxCaseBaoanInjuredTable>();
		}
		injuredData.add(new YjxCaseBaoanInjuredTable());
		adapter.notifyDataSetChanged();
	}

	/**初始化adapter，并添加到riskersView*/
	private void disPlayriskersView() {
		if (injuredData==null) {
			injuredData =new ArrayList<YjxCaseBaoanInjuredTable>();
			injuredData.add(null);
		}
		adapter = new InjAdapter(injuredData, this,addInjuredHandler);
		riskersView.setAdapter(adapter);
	}

	/** 获取调度任务的作业信息 */
	private void downLoadData() {
		LoadDialogUtil.setMessageAndShow(YjxSurveyActivity.this, "加载中……");
		// 获取作业信息
		String dispatchUid = getIntent().getStringExtra("dispatchUid");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("dispatchUid", dispatchUid));
		HttpUtils.requestPost(URLs.GET_WORK_INFO, params, HttpRequestTool.GET_WORK_INFO);
		// 获取接报案信息
		String uid = getIntent().getStringExtra("uid");
		List<String> paramsd = new ArrayList<String>();
		paramsd.add("uid");
		paramsd.add(uid);
		HttpUtils.requestGet(URLs.GET_BAOAN_INFO, paramsd, HttpRequestTool.GET_BAOAN_INFO);
		// 获取调度人信息
		String id = getIntent().getStringExtra("id");
		List<String> paramsDisptch = new ArrayList<String>();
		paramsDisptch.add("id");
		paramsDisptch.add(id);
		HttpUtils.requestGet(URLs.GET_DISPATCH_INFO, paramsDisptch, HttpRequestTool.GET_DISPATCH_INFO);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventData(List<NameValuePair> values) {
		int rcode = Integer.valueOf(values.get(0).getName());
		switch (rcode) {
		case HttpRequestTool.GET_WORK_INFO:
			LoadDialogUtil.dismissDialog();
			getAndDisplayWorkData(values.get(0).getValue());
			break;
		case HttpRequestTool.GET_BAOAN_INFO:
			LoadDialogUtil.dismissDialog();
			getAndDisplayBaoanData(values.get(0).getValue());
			break;
		case HttpRequestTool.GET_DISPATCH_INFO:
			LoadDialogUtil.dismissDialog();
			getAndDisplayDispatchData(values.get(0).getValue());
			break;
		case HttpRequestTool.UPLOAD_FILE_PHOTO: //医健险上传图片成功
			changeImgType(values);
			break;
		case HttpRequestTool.POST_YJX_WORK_SAVE: //医健险作业保存成功
			showWorkSavaInfo(values);
			break;
		case HttpRequestTool.POST_YJX_BAOAN_UPDATE: //医健险作业界面接报案保存成功
			showWtSavaInfo(values);
			break;
			
		default:
			break;
		}
	}
	
	
	/**作业保存成功与否*/
	private void showWorkSavaInfo(List<NameValuePair> values) {
		int responsecode = Integer.parseInt(values.get(1).getValue());
		if (responsecode == 200) {
			HttpUtils.requestPost(URLs.POST_YJX_BAOAN_UPDATE, wtparams, HttpRequestTool.POST_YJX_BAOAN_UPDATE);
//			workDataEn = JSON.parseObject(values.get(0).getValue(), YjxCaseWorkEntity.class);
		}else {
			LoadDialogUtil.dismissDialog();
			DialogUtil.getErrDialog(this, "操作失败！").show();
		}
	}
	
	/**接报案提交成功与否*/
	private void showWtSavaInfo(List<NameValuePair> values) {
		LoadDialogUtil.dismissDialog();
		int responsecode = Integer.parseInt(values.get(1).getValue());
		if (responsecode == 200 ) { //不是暂存，提示后关闭当前页面。
			if (workDataEn!=null && status==1) {
				Dialog dialog = DialogUtil.getAlertOneButton(this, "提交成功！", null);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface arg0) {
						YjxSurveyActivity.this.finish();
					}
				});
				dialog.show();
			}else { //提示后留在当前界面。
				DialogUtil.getAlertOneButton(this, "暂存成功！", null).show();
			}
		}else {
			LoadDialogUtil.dismissDialog(); //关闭遮罩层
			DialogUtil.getErrDialog(this, "操作失败！").show();
		}
	}

	/**将选择的文件存到已上传中，并移除未上传文件
	 * @param values */
	private void changeImgType(List<NameValuePair> values) {
		String UpedFileName = values.get(0).getValue();
		String oldFileName = values.get(1).getValue();
		int fileType = Integer.parseInt(values.get(2).getValue()); //1委托文件上传成功，2是作业文件上传成功
		if (!TextUtils.isEmpty(UpedFileName) && !TextUtils.isEmpty(UpedFileName) && fileType==1) {
			for (int i = 0; i < TempFileArrs.size(); i++) {
				if (oldFileName.equals(TempFileArrs.get(i))) {
					TempFileArrs.remove(i);  //移除选择
					fileUrlStrs = fileUrlStrs+UpedFileName+","; //添加到已上传
					disWtPlayImage(wtFileLinear, fileUrlStrs, 2); //刷新界面
				}
			}
		}else if (!TextUtils.isEmpty(UpedFileName) && !TextUtils.isEmpty(UpedFileName) && fileType==2) {
			for (int i = 0; i < workTempFileArrs.size(); i++) {
				if (oldFileName.equals(workTempFileArrs.get(i))) {
					workTempFileArrs.remove(i);  //移除选择
					surveyDataTem = surveyDataTem+UpedFileName+","; //添加到已上传
					disPlayWorkfile(); //刷新界面
				}
			}
		}
	}
	
	/**解析调度信息并显示*/
	private void getAndDisplayDispatchData(String value) {
		
		try {
			dispatchEn = JSON.parseObject(value, YjxCaseDispatchTable.class);
			if (dispatchEn!=null && dispatchEn.user!=null) { //显示调度人信息
				setTextInfo((MarqueeTextView)wtInfoView.findViewById(R.id.YJXWWV_Dispatcher),dispatchEn.user.name); //调度人
				setTextInfo((MarqueeTextView)wtInfoView.findViewById(R.id.YJXWWV_DispatcherPhone),dispatchEn.user.mobile); //调度人电话
			}
		} catch (Exception e) {
			DialogUtil.getErrDialog(this, "无法查看调度人信息！！！").show();
			e.printStackTrace();
		}
	}

	private void getAndDisplayBaoanData(String value) {
		try {
			baoanDataEn = JSON.parseObject(value, YjxCaseBaoanEntity.class);
			disPlayBaoanInfo();
		} catch (Exception e) { // 如果解析接报案数据失败就提示用户，并在提示关闭是退出该界面。
			hintAndFinish("获取作业信息失败！\n" + value);
			e.printStackTrace();
		}
	}

	/** 解析返回作业数据并显示 */
	private void getAndDisplayWorkData(String value) {
		try {
			workDataEn = JSON.parseObject(value, YjxCaseWorkEntity.class);
			disPlayWorkInfo();
		} catch (Exception e) { // 如果解析数据失败就提示用户
			e.printStackTrace();
		}
	}

	/** 如果解析接报案数据失败就提示用户，并在提示关闭是退出该界面。 */
	private void hintAndFinish(String mesg) {
		Dialog dialog = DialogUtil.getErrDialog(this, mesg);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				YjxSurveyActivity.this.finish();
			}
		});
		dialog.show();
	}

	/** 回显作业数据。 */
	private void disPlayWorkInfo() {
		if (workDataEn != null) {
			surveyDataTem = workDataEn.surveyData;
			setWorkInfo();
		}else {
			surveyDataTem= "";
		}
	}

	/** 回显作业数据。 */
	private void disPlayBaoanInfo() {
		if (baoanDataEn != null && baoanDataEn.bussType != null) {
			setBaoanInfo();
		} else {
			hintAndFinish("获取接报案信息失败！");
		}
	}

	/** 显示接报案信息 */
	private void setBaoanInfo() {
		setTextInfo((MarqueeTextView) wtInfoView.findViewById(R.id.YJXWWV_WTRen), (baoanDataEn.wtName)); // 委托人
		setTextInfo((TextView) wtInfoView.findViewById(R.id.YJXWWV_wtContacts), (baoanDataEn.wtCotact)); // 委托方联系人
		setTextInfo((TextView) wtInfoView.findViewById(R.id.YJXWWV_wtContactsPhone), (baoanDataEn.wtContactTel)); // 委托联系人电话
		setTextInfo((EditText) wtInfoView.findViewById(R.id.YJXWWV_baoDanNo), (baoanDataEn.policyNo)); // 保单号
		setTextInfo((EditText) wtInfoView.findViewById(R.id.YJXWWV_InsuredName), (baoanDataEn.insuredPerson));// 被保险人
		setTextInfo((EditText) wtInfoView.findViewById(R.id.YJXWWV_InsuredPhone), (baoanDataEn.insuredPersonTel)); // 被保险人电话
		setTextInfo((TextView) wtInfoView.findViewById(R.id.YJXWWV_caseTime), sf.format(baoanDataEn.riskDate)); // 出险时间
		setTextInfo((EditText) wtInfoView.findViewById(R.id.YJXWWV_caseAddress), (baoanDataEn.riskAddress)); // 出险地址
		setTextInfo((EditText) wtInfoView.findViewById(R.id.YJXWWV_caseReasons), (baoanDataEn.riskReason)); // 出险原因
		if (baoanDataEn.entrustDataList != null && baoanDataEn.entrustDataList.size() > 0) {
			setTextInfo((TextView) wtInfoView.findViewById(R.id.YJXWWV_wtHanType), (baoanDataEn.entrustDataList.get(0).entrustLetter)); // 委托函类型
			setTextInfo((TextView) wtInfoView.findViewById(R.id.YJXWWV_baodanInfo), (baoanDataEn.entrustDataList.get(0).policy)); // 保单信息
			setTextInfo((TextView) wtInfoView.findViewById(R.id.YJXWWV_wtRequirement), (baoanDataEn.entrustDataList.get(0).entrustRequest)); // 委托要求
			disWtPlayImage(wtFileLinear, baoanDataEn.entrustDataList.get(0).fileUrls , 1);
		}
		//回显险种信息
		List<NameValuePair>  smalltypeMap = new ArrayList<NameValuePair>();
		List<NameValuePair>  typeMap=InsuranceTypeUtil.getinsuranceTypeCollection();
		for (int i = 0; i < typeMap.size(); i++) {
			if (typeMap.get(i).getName().equals(baoanDataEn.insuranceBigTypeId+"")) {
				TypeSp.setSelection(i);
				smalltypeMap = InsuranceTypeUtil.getinsuranceSmallTypeCollection(typeMap.get(i).getName());
				smallTypeValues.clear();
				smallTypeValues.addAll(InsuranceTypeUtil.MapToList(smalltypeMap));
				break;
			}
		}
		
		String insuranceSmallTypeId = baoanDataEn.insuranceSmallTypeId;
		for (int j = 0; j < smalltypeMap.size(); j++) {
			String smallTypeId = smalltypeMap.get(j).getName();
			if (smallTypeId.equals(insuranceSmallTypeId)) {
				smallTypeAdatper.notifyDataSetChanged();
				smallTypeSp.setSelection(j,true);
				break;
			}
		}
		// setTextInfo((EditText)wtInfoView.findViewById(R.id.YJXWWV),(baoanDataEn.aaa));
		// setTextInfo((EditText)wtInfoView.findViewById(R.id.YJXWWV),(baoanDataEn.aaa));
		injuredData = baoanDataEn.injuredList;
		disPlayriskersView();

	}
	
	/**
	 * 添加图片到LinearLayout中,如果fileUrls==null就是显示选择的文件列表
	 * @param boxView
	 * @param fileUrls
	 * @param typeCode //接报案文件1、作业信息中的接报案图片2
	 */
	private void disWtPlayImage(LinearLayout boxView , String fileUrls,int typeCode) {
		if (!TextUtils.isEmpty(fileUrls)  ) { //存文件的字符串不为空
			if (fileUrlStrs !=null &&typeCode == 1 && fileUrlStrs.length()>0) { //保证作业信息中有影像资料的时候只显示作业信息中的资料
//				fileUrlStrs = fileUrls;
			}else {
				fileUrlStrs = fileUrls;
			}
		}
		if (TextUtils.isEmpty(fileUrlStrs)){
			fileUrlStrs = "";
		}
		String [] fileNames= removeEmptyString(fileUrlStrs.split(","));
		for (int i = 0; i < fileNames.length; i++) { //网络图片是七牛云，需要添加接口地址。
			fileNames[i]=ImagePathUtil.BaseUrl+fileNames[i];
		}
		boxView.removeAllViews();
		int size = 1;
		size = addViewToView(boxView, fileNames , size, false,1);
		/**选择*/
		if (TempFileArrs!=null && TempFileArrs.size()>0) { //未上传的文件列表
			String [] temparr = new String[]{}; 
			size = addViewToView(boxView, TempFileArrs.toArray(temparr) , size ,true,1);
		}
	}
	
	/**
	 * 添加图片到LinearLayout中,如果fileUrls==null就是显示选择的文件列表
	 * @param boxView
	 * @param fileUrls
	 */
	private void disPlayWorkfile( ) {
		int size = 1;
		workFileLinear.removeAllViews();
		if (!TextUtils.isEmpty(surveyDataTem)) {
			String [] fileNames= removeEmptyString(surveyDataTem.split(","));
			for (int i = 0; i < fileNames.length; i++) { //网络图片是七牛云，需要添加接口地址。
				fileNames[i]=ImagePathUtil.BaseUrl+fileNames[i];
			}
			size = addViewToView(workFileLinear, fileNames , size, false,2);
		}
		
		/**选择*/
		if (workTempFileArrs!=null && workTempFileArrs.size()>0) { //未上传的文件列表
			String [] temparr = new String[]{}; 
			size = addViewToView(workFileLinear, workTempFileArrs.toArray(temparr) , size ,true,2);
		}
	}
	
	/**
	 * 往指定布局文件中添加数组中文列表视图
	 * @param boxView
	 * @param fileArrs
	 * @param size 
	 * @param workType 1为委托资料，2为作业资料 
	 */
	private int addViewToView(LinearLayout boxView , String [] fileArrs, int size, boolean deleteIsVisble,int workType){
		int i=size;
		if (fileArrs!=null && fileArrs.length>0) {
			for (int j=0;j<fileArrs.length;j++) {
				String fileName=fileArrs[j];
				if (!TextUtils.isEmpty(fileName)){
					View view = inflater.inflate(R.layout.yjx_img_item, null);
					
					if (deleteIsVisble) {
						String [] pathArr = fileName.split("/");
						((MarqueeTextView)view.findViewById(R.id.YJXVI_title)).setText(pathArr[pathArr.length-1]); //刚选择的文件直接显示文件名称
						((TextView)view.findViewById(R.id.YJXVI_delete)).setVisibility(View.VISIBLE);
						setDeleteOnclick(fileArrs,j,(TextView)view.findViewById(R.id.YJXVI_delete),workType); //添加单击删除功能
					}else {
						((MarqueeTextView)view.findViewById(R.id.YJXVI_title)).setText("影像资料-"+i++);
					}
					ImageView imgV=((ImageView)view.findViewById(R.id.item_gridviewForExlist_photoup_img));
					if ("image".equals(OpenFileUtil.getFileType(fileName))) { //是图片就加在缩略图
						Glide.with(this).load(fileName).centerCrop().error(R.drawable.ssdk_weibo_empty_failed).into(imgV);
					}else { //非图片就显示对应图标
						Glide.with(this).load(OpenFileUtil.getTypeResouse(fileName, YjxSurveyActivity.this)).
						centerCrop().error(R.drawable.ssdk_weibo_empty_failed).into(imgV);
					}
					boxView.addView(view);
					setViewOnclick(view,fileName);
				}
			}
		}
		return i;
	}
	
	
	/**删除选择文件
	 * @param j 
	 * @param fileArrs 
	 * @param workType */
	private void setDeleteOnclick(final String[] fileArrs, final int j,TextView deleteTv, final int workType) {
		deleteTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (workType==1) { //委托文件删除
					TempFileArrs.remove(j);
					disWtPlayImage(wtFileLinear, fileUrlStrs, 2);
				}else {//作业文件删除
					workTempFileArrs.remove(j);
					disPlayWorkfile();
				}
			}
		});
	}

	private void setViewOnclick(View view , final String fileName) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				if ("image".equals(OpenFileUtil.getFileType(fileName))) { //是图片就用图片工具加载
					ImageDisplayUtil.displayByMyView(YjxSurveyActivity.this, fileName);
				}else { //非图片就判断是本地或者是网络，从而判断是否下载。
					if (fileName.indexOf("://")>-1) {
						dowLoadAndOpenFile(fileName);
//						String [] pathArr = fileName.split("/");
//						String name = pathArr[pathArr.length-1];
//						new FileDownOpenUtil(YjxSurveyActivity.this).downloadAndOpen(fileName, name);
					}else {
						OpenFileUtil.openFileByPath(YjxSurveyActivity.this, fileName);
					}
				}
			}
		});
	}
	
	private void dowLoadAndOpenFile(final String fileName) {
		LoadDialogUtil.setMessageAndShow(this, "努力加载中……");
		String[] pathArr = fileName.split("/");
		String name = pathArr[pathArr.length - 1];
		String dirName = Environment.getExternalStorageDirectory().getAbsolutePath();
		new FileDownOpenUtil2().downloadAndOpen(this, dirName, name, fileName);
		String filepath = dirName + name;
		Message message = new Message();
		message.obj = filepath;
	}
	
	/**剔除数组中空字符串*/
	private String[] removeEmptyString(String [] arrStr){
		List<String> arrList=new ArrayList<String>();
		for (String fileName:arrStr) {
			if (!TextUtils.isEmpty(fileName)){
				arrList.add(fileName);
			}
		}
		String [] arrtemp=new String[]{};
		return arrList.toArray(arrtemp);
	}

	private void setTextInfo(TextView Tv,String value){
		if (value!=null) {
			Tv.setText(value);
		}
	}

	/** 回显作业数据。 */
	private void setWorkInfo() {
		disWtPlayImage(wtFileLinear, workDataEn.fileUrls , 2);
		surveyDataTem = workDataEn.surveyData;
		disPlayWorkfile();
		injuredData = workDataEn.injuredList;
		disPlayriskersView();
		((Spinner)workView.findViewById(R.id.YJXWV_lookMode)).setSelection(SurveyType.getPostion(workDataEn.surveyType), true); //调查类型
		SetTextUtil.setEditText((EditText)workView.findViewById(R.id.YJXWV_VisitingAddress), workDataEn.surveyAddress); //调查地点
		SetTextUtil.setEditText((EditText)workView.findViewById(R.id.YJXWV_Investigation), workDataEn.surveyDescription); //调查经过
		SetTextUtil.setEditText((EditText)workView.findViewById(R.id.YJXWV_VisitResults), workDataEn.surveyConclusion); //调查结论
		setTextInfo((TextView) workView.findViewById(R.id.YJXWV_lookTime), sf.format(workDataEn.surveyTime)); // 调查时间
	}

	/**设置选择调查类型的Spinner*/
	private void setlookModeSpinner() {
		Spinner surveysp = (Spinner)workView.findViewById(R.id.YJXWV_lookMode);
		ArrayAdapter<String> spiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SurveyType.getType());
		(surveysp).setAdapter(spiAdapter);
	}

	private void setAction() {
		actionTV2.setText("医健险作业");
		actionTV3.setText("暂存");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				HintOut(); //
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// SubmitCase(1);
			}
		});
	}
	
	/**返回键提示是否退出*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK) {
			HintOut();
			 return false;//拦截事件
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 提示用户是否真的要退出该界面，避免勿退出！ **/
	private void HintOut() {
		Dialog dialog = DialogUtil.getAlertOnelistener(this, "确定要退出该页面吗！", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				YjxSurveyActivity.this.finish();
			}
		});
		dialog.show();
	}

	/** 初始化pagerAdapter */
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
	public void onCheckedChanged(RadioGroup arg0, int buttonId) {
		// TODO Auto-generated method stub
		switch (buttonId) {
		case R.id.ACYJXWORK_btn_0:
			vpager.setCurrentItem(0);
			break;
		case R.id.ACYJXWORK_btn_1:
			vpager.setCurrentItem(1);
			break;
		case R.id.ACYJXWORK_btn_2:
			vpager.setCurrentItem(2);
			break;
		default:
			break;
		}
	}
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
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
			radgrup.check(R.id.ACYJXWORK_btn_0);
			break;
		case 1:
			radgrup.check(R.id.ACYJXWORK_btn_1);
			break;
		case 2:
			radgrup.check(R.id.ACYJXWORK_btn_2);
			break;

		default:
			break;
		}
	}

}
