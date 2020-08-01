package com.cninsure.cp.activty;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.LeavingMessageEntity;
import com.cninsure.cp.entity.OCREntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.WorkMessageEntity;
import com.cninsure.cp.entity.WorkPhotos;
import com.cninsure.cp.entity.WorkType;
import com.cninsure.cp.ocr.LinePathActivity;
import com.cninsure.cp.ocr.PhotographyHelp;
import com.cninsure.cp.utils.ActivityManagerUtil;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.cninsure.cp.utils.PopupWindowUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.LoadingDialog;
import com.cninsure.cp.view.MyWebView;
import com.cninsure.cp.view.MyWebView.PlayFinish;

public class WorkOrderActivty extends BaseActivity implements OnClickListener, OnPageChangeListener {

	// private ExpandableListView expandableListView;
	public MyWebView webview;
	private View uploadView,leavingMsgMainView,leavingMsgView,actionLine,RidoLine;
	public String request1, QorderUid;
	private TextView actionTV1, actionTV2, actionTV3;
	private RadioButton title1Tv, title2Tv,title3Tv;
	private RadioGroup radioGroup;
	private ViewPager vpager;
	private List<View> viewlist;
	private PagerAdapter pagerAdapter;
	private TranslateAnimation moveanim;
	private WorkOrderActivtyhelp workhelp;
	private LoadingDialog loaddialog;
	public static WorkType photoType;
	private WorkPhotos workphotos;
	/**作业类型**/
	private int bussTypeId;
	/** 不同作业类型加载界面 **/
	private String[] workUrls = new String[] { "/m_survey_index", "/m_assess_index", "/m_material_index", "/m_yyts_index", "/m_mediation_index" };
	/** 不同作业类型获取回显信息的路径 **/
	private String[] workInfoUrls = new String[] { "/survey/message", "/assessment/message", "/damage/message", "/hospital/show", "/mediation/show" };
	/** 不同作业类型保存的路径 **/
	private String[] saveWorkInfoUrls = new String[] { "/survey/do", "/assessment/do", "/damage/do", "/hospital/do", "/mediation/do" };
	/** 不同作业类型修改的路径 **/
	private String[] changeWorkInfoUrls = new String[] { "/survey/modify", "/assessment/modify", "/damage/modify", "/hospital/modify", "/mediation/modify" };
	/** 不同作业类型暂存的路径 **/
	private String[] saveTempWorkInfoUrls = new String[] { "/worksurvey/input", "/workassessment/input",
			"/workdamage/input", "/workhospital/input", "/workmediation/input" };
	/** 不同作业类型暂存回显的路径 **/
	private String[] saveTempWorkShowUrls = new String[] { "/worksurvey/show", "/workassessment/show",
			"/workdamage/show", "/workhospital/show", "/wrokmediation/show" };
	/**是否需要提交审核**/
	private boolean isAudit = false;
	private String headMessage;
	private String status;
	private String tempstr;
	/**暂存类型：0为手动暂存，1为提示暂存，为1时暂存后需要结束当前界面*/
	private int TemporaryType=0;
	/**如果需要刷新首页列表就填写“NEW_ORDER”*/
	private String postEvent="";
	/**留言返回数据**/
	private LeavingMessageEntity leavingMsgdata;
	/**留言列表listView*/
	private ListView LeavingListView;
	private LayoutInflater inflater;
	/**是否提交审核**/
	private boolean isSubmit=false,isLoadingSuccess=false;
	/**拍摄照片路径**/
	private File file;
	/**调用摄像头拍照的帮助类**/
	private PhotographyHelp cameraHelp;
	/**签字图片路径**/
	private String signpath;
	/**OCR解析信息及图片路径1,身份证，2银行卡，3驾驶证，4行驶证，5签名**/
	public OCREntity ocrEntity1,ocrEntity2,ocrEntity3,ocrEntity4,ocrEntity5;
	/***/
	public WorkMessageEntity contentMessage;
	/**追加订单popup*/
	private PopupWindow popupWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		/**WebView使用中的那些坑之软键盘遮挡输入框，下面的方法刚获得焦点的时候，还是会被覆盖。但是软键盘一输入，会上升和滚动*/
		getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
				| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE );
		
		setContentView(R.layout.activty_orderwork);
		
		ActivityManagerUtil.getInstance().addToList(this);
		EventBus.getDefault().register(this);
		loaddialog = new LoadingDialog(this);
		status = getIntent().getStringExtra("status");
		if (status.equals("6")) {//如果状态是6就
			isSubmit=true;
		}
		QorderUid = getIntent().getStringExtra("orderUid");
		dowloadPhotoType();
		getDefaulMessage(1);
		initaction();
		initView();
	}

	private void dowloadPhotoType() {
		List<String> params = new ArrayList<String>(2);
		bussTypeId=Integer.parseInt(getIntent().getStringExtra("taskType"));
		params.add("type");
		if (bussTypeId==6) {
			params.add("hospitaLPI");
		} else {
			params.add("commonInfo");
		}
		HttpUtils.requestGet(URLs.GetPhotoType(), params, HttpRequestTool.GET_PHOTO_TYPE);
		showLoadingDialog();
	}
	
	public void showLoadingDialog(){
		if (!loaddialog.isShowing()) {
			loaddialog.setMessage("努力加载中……").show();
		}
	}
	
	public void dialogdismiss(){
		if (loaddialog.isShowing()) {
			loaddialog.dismiss();
		}
	}

	private void initView() {
		inflater=LayoutInflater.from(this);
		title1Tv = (RadioButton) findViewById(R.id.OrderWork_title1);
		title2Tv = (RadioButton) findViewById(R.id.OrderWork_title2);
		title3Tv = (RadioButton) findViewById(R.id.OrderWork_leavingMsg);
		vpager = (ViewPager) findViewById(R.id.OrderWork_viewpager);
		radioGroup  = (RadioGroup) findViewById(R.id.OrderWork_btnG);
		uploadView = inflater.inflate(R.layout.imageupload_view, null);
		leavingMsgView = inflater.inflate(R.layout.leaving_message_head_view, null);
		leavingMsgMainView = inflater.inflate(R.layout.teaving_message_view, null);
		actionLine = (View) findViewById(R.id.OrderWork_include);
		RidoLine = (View) findViewById(R.id.OrderWork_btnGLine);
		LeavingListView=(ListView) leavingMsgMainView.findViewById(R.id.LeavingMsg_listView);
		LeavingListView.addHeaderView(leavingMsgView);
		LeavingListView.setEmptyView(inflater.inflate(R.layout.empty_view, null));
		
//		displayLeavingMsg();//获取留言信息
		setonclick();
	}
	
	/****/
	private void initPager(){
		if (webview==null) {
			initWebView();
			viewlist = new ArrayList<View>(2);
			viewlist.add(webview);
			viewlist.add(uploadView);
			viewlist.add(leavingMsgMainView);
			initviewpageradapter();
			vpager.setAdapter(pagerAdapter);
		}else {
			webview.loadUrl(tempstr);
			Log.e("JsonHttpUtils", "第二次3********************************=="+tempstr);
		}
	}

	private void setonclick() {
		title1Tv.setOnClickListener(this);
		title2Tv.setOnClickListener(this);
		title3Tv.setOnClickListener(this);
		vpager.setOnPageChangeListener(this);
		leavingMsgView.findViewById(R.id.LeavingMsg_submit_button).setOnClickListener(this);
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

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	private void initWebView() {
		// 获取webView 控件
		webview=new MyWebView(this);
//		webview = new WebView(this);
		
		  //允许webview对文件的操作  
		webview.getSettings().setAllowUniversalAccessFromFileURLs(true);  
		webview.getSettings().setAllowFileAccess(true);  
		webview.getSettings().setAllowFileAccessFromFileURLs(true); 
		
		// 加上这句话才能使用javascript方法
		webview.getSettings().setJavaScriptEnabled(true);
		// webView拓展的api是否打开：
		webview.getSettings().setDomStorageEnabled(true);
		// 3、在高版本的时候我们是需要使用允许访问文件的urls：
		webview.getSettings().setAllowFileAccessFromFileURLs(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // 不让webView从缓存中读取，每次都去网络获取

		webview.addJavascriptInterface(new JsInteration(), "control");
		webview.setWebChromeClient(new WebChromeClient() {
		});
		webview.setWebViewClient(new WebViewClientDemo());

		String loadUrlStr = getWorkSpace(URLs.WORK_SPACE, workUrls);
		webview.loadUrl(loadUrlStr);
		webview.setDf(new PlayFinish() {
			@Override
			public void After() {
				loadurlM();
			}
		});
	}
	
	private boolean isLoadur=false;
	private Timer timer=new Timer();
	private void loadurlM(){
		if (!isLoadur) {
			isLoadur=true;
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Message msg=new Message();
					msg.what=1002;
					handler2.sendMessage(msg);
				}
			}, 100,100);
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler2=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==1002) {
				webview.loadUrl(tempstr);
				Log.e("JsonHttpUtils", "handler23********************************=="+tempstr);
			}
		}
	};
	
	
	
	
	

	/** 获取对应作业类型的作业界面路径 **/
	private String getWorkSpace(String baseUrl, String[] urls) {
		int taskTypes = Integer.parseInt(getIntent().getStringExtra("taskType"));
		switch (taskTypes) {
		case 2:// 现场查勘
			return baseUrl + urls[0];
		case 3:// 定损作业(车辆定损)
			return baseUrl + urls[1];
		case 5:// 物损作业(三者物损定损)
			return baseUrl + urls[2];
		case 19:// 医院探视（住院探视）
			return baseUrl + urls[3];
		case 38:// 一次性调解
			return baseUrl + urls[4];

		default:	
			ToastUtil.showToastLong(this, "未知的作业类型，请联系管理员！");
			return "";
		}
	}
	/** 获取对应作业类型的作业界面路径 **/
	private boolean  isInsureType(){
		int taskTypes = Integer.parseInt(getIntent().getStringExtra("taskType"));
		switch (taskTypes) {
		case 2:// 现场查勘
			return true;
		case 3:// 定损作业(车辆定损)
			return  true;
		case 5:// 物损作业(三者物损定损)
			return  true;
		case 19:// 医院探视（住院探视）
			return  true;
		case 38:// 一次性调解
			return  true;

		default:	
			Dialog dialog=DialogUtil.getAlertOneButton(this, "未知的作业类型，请联系管理员！", null);
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					WorkOrderActivty.this.finish();
				}
			});
			dialog.show();
			return false;
		}
		
	}
	

	private void initaction() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();
	}

	private void setAction() {
		actionTV2.setText("填写作业信息");
		if (bussTypeId==2) {
			actionTV3.setText("追加任务");
		}else {
			actionTV3.setText("上传");
		}
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				WorkOrderActivty.this.finish();
				backHint(); 
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//如果是查勘任务且viewpager选择第一页 时触发新增调入时间
				if (bussTypeId==2 && vpager.getCurrentItem()==0 ) {
					popupWindow=PopupWindowUtils.showPopupWindow(getAddOrderView(), actionTV3, WorkOrderActivty.this);
				}else {
					workhelp.upload();
				}
			}
		});
	}
	
	/**
	 * 追加任务的选项View
	 * @return
	 */
	private View getAddOrderView() {
		View addView=LayoutInflater.from(this).inflate(R.layout.popupwindow_list, null);
		ListView listView=(ListView) addView.findViewById(R.id.popupwindow_listView);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.spinner_text_item_blue, getAddNames());
		listView.setAdapter(adapter);
		setListViewOnclick(listView);
		return addView;
	}
	
	
	/***
	 * 新增调度事件请求
	 * @param listView
	 */
	private void setListViewOnclick(ListView listView) {
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				popupWindow.dismiss();
				List<NameValuePair> params=new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
				params.add(new BasicNameValuePair("fromOrderUid",QorderUid ));
				
				switch (arg2) {
				case 0://新增定损
					params.add(new BasicNameValuePair("bussTypeId", 3+""));
					params.add(new BasicNameValuePair("bussTypeName", "车辆定损"));
					showHintDialog(params,"车辆定损",3);
					break;
				case 1://新增物损定损
					params.add(new BasicNameValuePair("bussTypeId", 5+""));
					params.add(new BasicNameValuePair("bussTypeName", "物损定损"));
					params.add(new BasicNameValuePair("bussSmallId", 5+""));
					params.add(new BasicNameValuePair("bussSmallName", "物损定损"));
					showHintDialog(params,"物损定损",5);
					break;
				case 2://新增一次性调解
					params.add(new BasicNameValuePair("bussTypeId", 38+""));
					params.add(new BasicNameValuePair("bussTypeName", "一次性调解"));
					params.add(new BasicNameValuePair("bussSmallId", 38+""));
					params.add(new BasicNameValuePair("bussSmallName", "一次性调解"));
					showHintDialog(params,"一次性调解",38);
					break;

				default:
					break;
				}
			}
		});
	}
	
	private void showHintDialog(final List<NameValuePair> params,String busstypeName,int type) {
		if (type==3) {
			showDSHintDialog(params);
		}else {
			DialogUtil.getAlertOnelistener(this, "确认追加"+busstypeName+"订单？",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					showLoadingDialog();
					HttpUtils.requestPost(URLs.ADD_NEW_ORDER_SELF, params, HttpRequestTool.ADD_NEW_ORDER_SELF);
				}
			}).show();
		}
	}
	
	List<NameValuePair> DSparams;
	/**弹出选框选择定损作业小类！**/
	private void showDSHintDialog(final List<NameValuePair> params) {
		DSparams=new ArrayList<NameValuePair>();
		DSparams.add(new BasicNameValuePair("bussSmallId", 31+""));
		DSparams.add(new BasicNameValuePair("bussSmallName", "定损(标的)"));
		
		new AlertDialog.Builder(this).setTitle("请选择定损作业小类！")
		.setSingleChoiceItems(new String[]{"标的定损","三者定损"}, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (arg1==0) {
					DSparams=new ArrayList<NameValuePair>();
					DSparams.add(new BasicNameValuePair("bussSmallId", 31+""));
					DSparams.add(new BasicNameValuePair("bussSmallName", "定损(标的)"));
				}else {
					DSparams=new ArrayList<NameValuePair>();
					DSparams.add(new BasicNameValuePair("bussSmallId", 32+""));
					DSparams.add(new BasicNameValuePair("bussSmallName", "定损(三者)"));
				}
			}
		}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				params.addAll(DSparams);
				HttpUtils.requestPost(URLs.ADD_NEW_ORDER_SELF, params, HttpRequestTool.ADD_NEW_ORDER_SELF);
			}
		}).setNeutralButton("取消", null).create().show();
	}

	/**追加任务调用接口成功后判断，并提示用户**/
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void evnetAddOrder(List<NameValuePair> values) {
		int requestType = Integer.parseInt(values.get(0).getName());
		if (requestType==HttpRequestTool.ADD_NEW_ORDER_SELF) {
			dialogdismiss();
			DialogUtil.getAlertOneButton(WorkOrderActivty.this, values.get(0).getValue(), null).show();
		}
	}

	/**新增任务类型名称**/
	private List<String> getAddNames(){
		 List<String> strings=new ArrayList<String>();
		 strings.add("车辆定损");
		 strings.add("物损定损");
		 strings.add("一次性调解");
		return strings;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.OrderWork_title1:
			vpager.setCurrentItem(0);
			break;
		case R.id.OrderWork_title2:
			vpager.setCurrentItem(1);
			break;
		case R.id.OrderWork_leavingMsg:
			vpager.setCurrentItem(2);
			break;

		case R.id.LeavingMsg_submit_button:
			submitLeavingMsg();
			break;

		default:
			break;
		}
	}

/**上传留言信息**/
	private void submitLeavingMsg() {
		EditText leavingText=((EditText)leavingMsgView.findViewById(R.id.LeavingMsg_edit));
		if (TextUtils.isEmpty(leavingText.getText().toString())) {
			DialogUtil.getAlertOneButton(this, "留言不能为空", null).show();
			return;
		}
		showLoadingDialog();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		params.add(new BasicNameValuePair("workUid", getIntent().getStringExtra("orderUid")));
		params.add(new BasicNameValuePair("message", leavingText.getText().toString()));
//		if (leavingMsgObj!=null && !TextUtils.isEmpty(leavingMsgObj.optString("id", "")) ) {//修改留言
//			params.add(new BasicNameValuePair("id", leavingMsgObj.optString("id", "")));
//			HttpUtils.requestPost(URLs.CHANG_LEAVING_MESSAGE, params, HttpRequestTool.SAVE_LEAVING_MESSAGE);
//		}else {//保存留言
			HttpUtils.requestPost(URLs.SAVE_LEAVING_MESSAGE, params, HttpRequestTool.SAVE_LEAVING_MESSAGE);
//		}
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
		if (pi == 0) {
			if (bussTypeId==2) {
				actionTV3.setText("追加任务");
			}else {
				actionTV3.setText("上传");
			}
			radioGroup.check(R.id.OrderWork_title1);
		}else if (pi == 1) {
			actionTV3.setText("上传");
			radioGroup.check(R.id.OrderWork_title2);
		} else {
			actionTV3.setText("上传");
			radioGroup.check(R.id.OrderWork_leavingMsg);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode==1 || requestCode==2 || requestCode==3 || requestCode==4) {
			if (data!=null) {
				file=(File)data.getSerializableExtra("FilePath");
				cameraHelp.forString(requestCode,file);
			}
		}else if (requestCode==HttpRequestTool.LINEPATH) { //签字返回图片
			upSignPhoto(data,5);
		}else if (resultCode != RESULT_OK) { // 此处的 RESULT_OK 是系统自定义得一个常量
			Log.e("getphotos", "ActivityResult resultCode error");
//			ToastUtil.showToastLong(this, "操作失败！");
			return;
		}else {
			workhelp.eventresultcode(requestCode, resultCode, data);
			EventBus.getDefault().post(resultCode);
		}
	}
	
	/**上传签字图片**/
	private void upSignPhoto(Intent data,int type) {//(String)data.getStringExtra("LinePathFilePath");
		if (null!=data&&null!=data.getStringExtra("LinePathFilePath")) {
			List<NameValuePair> fileUrls=new ArrayList<NameValuePair>();
			fileUrls.add(new BasicNameValuePair("0", (String)data.getStringExtra("LinePathFilePath")));
			PhotoUploadUtil.uploadOCR(WorkOrderActivty.this, fileUrls, URLs.UP_OCR_PHOTO, type);
		}
	}

	/**处理签字图片*/
	private void signMeath(String url){
		if (url!=null) {
			ocrEntity5=new OCREntity();
			signpath=url;
			ocrEntity5.url=url;//签字图盘名称，不包含完整路径
			String call = "javascript:showPhotographInfo(5,"+JSON.toJSONString(ocrEntity5)+")";
//			DialogUtil.getAlertDialog(this, call).show();
			webview.loadUrl(call);
		}
	}

	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventmeth(String successCode){
		if ("UPLOAD_SUCCESS".equals(successCode)) {
			/**上传图片成功以后清空本地保存的选择图片路径，以免回显错误或者下次上传是重复上传**/
			//通过共享参数储存已经拍摄的照片信息（路径）
			Editor mEditor=AppApplication.sp.edit();
			mEditor.putString("PathList:"+QorderUid, "");
			mEditor.commit();
			mEditor.clear();
			downloadWorkPhotos();
		}
	}
	
	/**上传OCR图片和签字后返回成功与图片名称及后缀**/
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventmeth(NameValuePair valuePair){
		if ("UPLOAD_SUCCESS".equals(valuePair.getName())) {
			cameraHelp.sendMsgToBack(valuePair.getValue());
		}
	}
	/**上传签字后返回成功与图片名称及后缀**/
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventmeth(List<NameValuePair> responseValue){
		int type=Integer.parseInt(responseValue.get(0).getValue());
		if (type==5) {
			if ("UPLOAD_SUCCESS".equals(responseValue.get(1).getName())) {
				signMeath(responseValue.get(1).getValue());
			}
		}else  if (type==4||type==3||type==2||type==1) {
			cameraHelp.sendMsgToBack(responseValue.get(1).getValue());
		}
	}
	private void downloadWorkPhotos() {
		List<String> params = new ArrayList<String>(2);
		params.add("userId");
		params.add(AppApplication.getUSER().data.userId);
		params.add("workId");
		params.add(getIntent().getStringExtra("orderUid"));
		HttpUtils.requestGet(URLs.GetWorkPhoto(), params, HttpRequestTool.GET_WORK_PHOTO);
		showLoadingDialog();
	}

	@Override
	protected void onDestroy() {
		try {
			Log.e("JsonHttpUtils", workhelp.dataToString());
			EventBus.getDefault().post(postEvent);
			EventBus.getDefault().unregister(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	/************************************/
//	private void testMethod(WebView webView) {
//		String call = "javascript:sayHello()";// 调用js无参无返回值函数
//		call = "javascript:alertMessage(\"" + "content" + "\")";// 调用js有参无返回值函数
//		call = "javascript:toastMessage(\"" + "content" + "\")";
//		call = "javascript:window.goMobileTaskHandle(1,2)";// 调用js有参数有返回值的函数
////		Log.i("JsonHttpUtils", "调用JS传递字符串=" + request);
//		// webView.loadUrl(call);
//	}

	private static final String LOGTAG = "MainActivity";

	/** JS调用java保存或提交审核 **/
	public class JsInteration {
		@JavascriptInterface
		public void requestShowData(String head, String message) {
			try {
				if ("SHOW_MESSAGE".equals(head)) {
//					ToastUtil.showToastLong(WorkOrderActivty.this, "已经来拿去了头部信息");
//					Log.e("JsonHttpUtils", "3##########"+"已经来拿去了头部信息"+tempstr);
					loadurlM();
				}else if ("RELOAD_EVENT".equals(head)) {//重新加载显示数据 getDefaulMessage(1);
					Message msg=new Message();
					msg.what=HttpRequestTool.WEB_BACK_FLASH;
					handler.sendMessage(msg);
				} else if ("GET_PARTS".equals(head)) {
					HttpUtils.requestGet(message, null, HttpRequestTool.DOWN_PARTS_INFO);
				}else {
					Message msg=new Message();
					msg.what=HttpRequestTool.SAVE_WORK_INFO;
					handler.sendMessage(msg);
					saveWorkInfo(head, message);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@JavascriptInterface
		public void setVisibility(String head) {
			Message msg=new Message();
			msg.obj=head;
			msg.what=HttpRequestTool.SET_VISIVILITY;
			handler.sendMessage(msg);
		}
		@JavascriptInterface
		public void onSumResult(int result) {
			Log.i(LOGTAG, "onSumResult result=" + result);
		}
		
		/**调用摄像头识别身份证行驶证等信息
		 * 1 身份证识别
		 * 2 银行卡识别
		 * 3 驾驶证识别
		 * 4 行驶证识别
		 * **/
		@JavascriptInterface
		public void photographyEvent(int event){
			if (event==5) {//签字
				startSign();
			}else { //OCR识别
				cameraHelp.startCamera(event);
			}
		}
	}
	
	/**启动签字**/
	private void startSign(){
//		if (ocrEntity5!=null) {
			Intent intent=new Intent(WorkOrderActivty.this, LinePathActivity.class);
			intent.putExtra("ocrEntity5", ocrEntity5);
			intent.putExtra("orderUid", QorderUid);
			startActivityForResult(intent, HttpRequestTool.LINEPATH);
//		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==HttpRequestTool.SAVE_WORK_INFO) {
				showLoadingDialog();
			}else if (msg.what==HttpRequestTool.SET_VISIVILITY) {
				setVisibilityMain((String)msg.obj);
			}else if (msg.what==HttpRequestTool.WEB_BACK_FLASH) {
				getDefaulMessage(1);
			}
		}
		
	};
	
	
	/**
	 *  SAVE_EVENT：保存事件，AUDIT_EVENT： 保存并提交审核事件,TEMPORARY_STORAGE_EVENT暂存,LOAD_SUCCESS获取回显信息成功
	 * @throws JSONException
	 **/
	private void setVisibilityMain(String head) {
		if ("EVENT_GONE".equals(head)) {//隐藏导航栏
			actionLine.setVisibility(View.GONE);
			RidoLine.setVisibility(View.GONE); 
			return;
		}if ("EVENT_VISIBLE".equals(head)) {//显示导航栏
			actionLine.setVisibility(View.VISIBLE);
			RidoLine.setVisibility(View.VISIBLE); 
			return;
		}}
	

	/**
	 *  SAVE_EVENT：保存事件，AUDIT_EVENT： 保存并提交审核事件,TEMPORARY_STORAGE_EVENT暂存,LOAD_SUCCESS获取回显信息成功
	 * @throws JSONException
	 **/
	private void saveWorkInfo(String head, String data) throws JSONException {
		Log.e("LOAD_SUCCESS", "页面接收到消息了！"+data);
		if ("LOAD_SUCCESS".equals(head)) {//如果页面接收到回显信息后会返回消息，然后停止传递数据
			timer.cancel();
			loaddialog.dismiss();
			isLoadingSuccess=true;
			return;
		}if ("EVENT_GONE".equals(head)) {//隐藏导航栏
			actionLine.setVisibility(View.GONE);
			RidoLine.setVisibility(View.GONE); 
			return;
		}if ("EVENT_VISIBLE".equals(head)) {//显示导航栏
			actionLine.setVisibility(View.VISIBLE);
			RidoLine.setVisibility(View.VISIBLE); 
			return;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		params.add(new BasicNameValuePair("orderUid", QorderUid));
		// if ("SAVE_EVENT".equals(head)) {
//		ToastUtil.showToastShort(getApplicationContext(), data);
		String key, value;
		JSONObject jsonObject = new JSONObject(data);
		Iterator iterator = jsonObject.keys();
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			String tempStr = jsonObject.getString(key);
			if ( !TextUtils.isEmpty(tempStr) && !"null".equals(tempStr)) {
				if (!"myToken".equals(key) ) { //过滤重复提交myToken，以免请求无响应
					if ((key.indexOf("thirdCommands[")>-1 && key.indexOf("updateDate")>-1) || (key.indexOf("thirdCommands[")>-1 && key.indexOf("createDate")>-1)) { 
						continue;
					}
					value = tempStr;
					params.add(new BasicNameValuePair(key, value));
				}
			}
		}
		if (!isInsureType()) {
			return;
		}
		if ("TEMPORARY_STORAGE_EVENT".equals(head)) {//暂存
			HttpUtils.requestPost(getWorkSpace(URLs.SAVE_WORK_TEMP, saveTempWorkInfoUrls), params, HttpRequestTool.SAVE_WORK_TEMP);
		}else {
			if (isSubmit) {//已提交审核的订单提示用户不能保存修改
				loaddialog.dismiss();
				ToastUtil.showToastLong(this, "订单已提交审核，不能再保存修改！");
			}else {//未提交审核的订单可以修改
				if (status.equals("5") || status.equals("7")) {//changeWorkInfoUrls 修改
					HttpUtils.requestPost(getWorkSpace(URLs.SaveWorkInfo(), changeWorkInfoUrls), params, HttpRequestTool.SAVE_WORK_INFO);
					}else { //保存
						HttpUtils.requestPost(getWorkSpace(URLs.SaveWorkInfo(), saveWorkInfoUrls), params, HttpRequestTool.SAVE_WORK_INFO);
					}
			}
		}
		
		if ("SAVE_AND_AUDIT_EVENT".equals(head)) {
			isAudit = true;
		}
	}


	/** 获取案件回显信息/暂存回显 **/
	public void getDefaulMessage(int code) {
		List<String> params = new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.getUSER().data.userId);
		params.add("orderUid");
		params.add(getIntent().getStringExtra("orderUid"));
		showLoadingDialog();
		if (!isInsureType()) {
			return;
		}
		if (code == 1) {//请求头内容
			HttpUtils.requestGet(URLs.GetWorkMessage(), params, HttpRequestTool.GET_WORK_MESSAGE);
		} else if (code == 2) {//请求头回显内容
			if ("2".equals(status)) {//暂存回显信息
				HttpUtils.requestGet(getWorkSpace(URLs.SAVE_WORK_TEMP, saveTempWorkShowUrls), params, HttpRequestTool.GET_WORK_MESSAGES);
			}else {//保存、驳回回显信息
				HttpUtils.requestGet(getWorkSpace(URLs.GetWorkMessages(), workInfoUrls), params, HttpRequestTool.GET_WORK_MESSAGES);
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void evnetH5(List<NameValuePair> values) {
		int responsecode = Integer.parseInt(values.get(0).getName());
		if (responsecode == HttpRequestTool.SAVE_WORK_INFO || responsecode == HttpRequestTool.SUBMIT_WORK
				|| responsecode == HttpRequestTool.SAVE_WORK_TEMP|| responsecode == HttpRequestTool.GET_LEAVING_MESSAGE
				|| responsecode == HttpRequestTool.SAVE_LEAVING_MESSAGE || responsecode == HttpRequestTool.GET_WORK_PHOTO) {
			if (isLoadingSuccess) {
				loaddialog.dismiss();
			}
		}
		/**这里是加载HTML作业界面前调用接口走的地方，在HTML界面调用结束后才调用的接口不能走这里关闭loaddialog**/
		if (responsecode == HttpRequestTool.GET_WORK_MESSAGE || responsecode == HttpRequestTool.GET_WORK_MESSAGES || 
				responsecode == HttpRequestTool.GET_PHOTO_TYPE) {
//			if (++isFirstLoadingWebview>4) {//保证第一次打开该界面时，在这四个数据加载完成之后不要关闭遮罩，等与web交互后由web关闭
////				loaddialog.dismiss();
//			}
			}
		switch (CheckHttpResult.checkList(values, this,HttpRequestTool.GET_WORK_MESSAGES)) {
		case HttpRequestTool.GET_WORK_MESSAGE:
			headMessage=values.get(0).getValue();
			isLoadingSuccess=false;
			displayLeavingMsg();//获取留言信息
			getDefaulMessage(2);
			break;
		case HttpRequestTool.GET_WORK_MESSAGES:
			tempstr="javascript:getMobileWeb(\'{\"headMessage\":" + headMessage +"," +
					"\"contentMessage\":" + values.get(0).getValue() + "," +
					"\"userInfo\":" + JSON.toJSONString(AppApplication.getUSER()) + ",\"orderStatus\":"+status+"}\')";
			contentMessage=JSON.parseObject(values.get(0).getValue(), WorkMessageEntity.class);
			getOCREntity();
			Log.e("JsonHttpUtils", "3********************************=="+tempstr);
			isLoadingSuccess=false;
			getorderStatu(values.get(0).getValue());
			initPager(); 
			break;
			
		case HttpRequestTool.SAVE_WORK_INFO:
			SubmitOrder(values);
			break;
			
		case HttpRequestTool.SUBMIT_WORK:
			isSubmit=true;//在这里修改是否提交审核的状态前提是只有提交审核成功才能到这里
			setFinisDialog(values);
			break;
			
		case HttpRequestTool.GET_PHOTO_TYPE:
			photoType = JSON.parseObject(values.get(0).getValue(), WorkType.class);
			downloadWorkPhotos();
			break;
			
		case HttpRequestTool.GET_WORK_PHOTO:
			workphotos = JSON.parseObject(values.get(0).getValue(), WorkPhotos.class);
			workhelp = new WorkOrderActivtyhelp(this, uploadView, photoType, workphotos);
			workhelp.getExpandableListView(this, uploadView);
			break;
		case HttpRequestTool.SAVE_WORK_TEMP:
			if (TemporaryType==0) {
				DialogUtil.getAlertOneButton(this, values.get(0).getValue(), null).show();
			}else {
				setFinisDialog(values);
			}
			
			break;
		case HttpRequestTool.GET_LEAVING_MESSAGE:
			showLeavingMsg(values.get(0).getValue());
			break;
		case HttpRequestTool.SAVE_LEAVING_MESSAGE:
			showSaveMessage(values.get(0).getValue());
			break;
		case HttpRequestTool.DOWN_PARTS_INFO://请求定损维修数据传给后台
			String partInfo="javascript:getPartList("+values.get(0).getValue()+")";
			webview.loadUrl(partInfo);
			break;

		default:
			break;
		}

	}
	
	/**获保存的OCR信息**/
	private void getOCREntity() {
		if (!TextUtils.isEmpty(contentMessage.data.pathBank)) { //银行卡照片路径不能为空
			ocrEntity2=new OCREntity();
			ocrEntity2.insuredBankNo=contentMessage.data.insuredBankNo;
			ocrEntity2.url=contentMessage.data.pathBank;
		}else {
			ocrEntity2=null;
		}
		if (!TextUtils.isEmpty(contentMessage.data.pathDriverLicense)) { //驾驶证照片路径不能为空
			ocrEntity3=new OCREntity();
			ocrEntity3.bdDriverNo=contentMessage.data.pathDriverLicense;
			ocrEntity3.bdDrivingType=contentMessage.data.bdDrivingType;
			ocrEntity3.bdDriverName=contentMessage.data.bdDriverName;
			ocrEntity3.url=contentMessage.data.pathDriverLicense;
		}else {
			ocrEntity3=null;
		}
		if (!TextUtils.isEmpty(contentMessage.data.pathMoveLicense)) { //行驶证照片路径不能为空
			ocrEntity4=new OCREntity();
			ocrEntity4.bdCarNumber=contentMessage.data.bdCarNumber;
			ocrEntity4.bdCarVin=contentMessage.data.bdCarVin;
			ocrEntity4.bdEngineNo=contentMessage.data.bdEngineNo;
			ocrEntity4.url=contentMessage.data.pathMoveLicense;
		}else {
			ocrEntity4=null;
		}
	}

	private void showSaveMessage(String value) {
		final EditText leavingText=((EditText)leavingMsgView.findViewById(R.id.LeavingMsg_edit));
		Dialog dialog =DialogUtil.getAlertOneButton(this, value, null);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				downloadLeavingMsg();
				leavingText.setText("");
			}
		});
		dialog.show();
	}

	/**获取订单状态**/
	private void getorderStatu(String value) {
		try {
			JSONObject object=new JSONObject(value);
			JSONObject data=object.getJSONObject("data");
			String id=data.optString("id", "");
			if (!"null".equals(id) && !TextUtils.isEmpty(id)) {
				status="5";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**显示获取的留言信息*/
	private void showLeavingMsg(String value) {
		leavingMsgdata = JSON.parseObject(value, LeavingMessageEntity.class);
		List<Map<String, String>> params=new ArrayList<Map<String, String>>();
		if (leavingMsgdata!=null && leavingMsgdata.tableData != null && leavingMsgdata.tableData.data != null ) {
			try {
			for (int i = 0; i < leavingMsgdata.tableData.data.size(); i++) {
				Map<String, String> map=new HashMap<String, String>();
				map.put("data", leavingMsgdata.tableData.data.get(i).createDate);
				map.put("msg", leavingMsgdata.tableData.data.get(i).message);
				params.add(map);
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			Map<String, String> map=new HashMap<String, String>();
			params.add(map);
		}
		SimpleAdapter simpleAdapter=new SimpleAdapter(this, params, R.layout.leaving_message_item,
				new String[]{"data","msg"}, new int[]{R.id.LEAVINGitem_time,R.id.LEAVINGitem_message});
		LeavingListView.setAdapter(simpleAdapter);
	}

	private void displayLeavingMsg() {
		downloadLeavingMsg();
		try {
			JSONObject headMsg=new JSONObject(headMessage);
			JSONObject data=headMsg.getJSONObject("data");
			((TextView)leavingMsgView.findViewById(R.id.LeavingMsg_wtren)).setText(data.optString("entrusterName", "委托人名称（无）"));
			((TextView)leavingMsgView.findViewById(R.id.LeavingMsg_busstype)).setText(data.optString("bussTypeName", "归属机构（无）"));
			((TextView)leavingMsgView.findViewById(R.id.LeavingMsg_address)).setText(data.optString("caseLocationFull", "地址（无）"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**获取留言信息**/
	private void downloadLeavingMsg(){
		List<String> params = new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.getUSER().data.userId);
		params.add("workUid");
		params.add(getIntent().getStringExtra("orderUid"));
		showLoadingDialog();//可以不显示等待界面
		HttpUtils.requestGet(URLs.GET_LEAVING_MESSAGE, params, HttpRequestTool.GET_LEAVING_MESSAGE);
	}

	/**区分“保存”和“保存并提交审核”请求后分别进行处理**/
	private void SubmitOrder(List<NameValuePair> values) {
		ToastUtil.showToastShort(getApplicationContext(), values.get(0).getValue());
		int responsecode = Integer.parseInt(values.get(1).getValue());
		String msg = values.get(0).getValue();
		if (responsecode!=200 || TextUtils.isEmpty(msg)) {
			DialogUtil.getErrDialog(this, TextUtils.isEmpty(msg)?"操作失败！":values.get(0).getValue()).show();
			return;
		}
		if (isAudit) {//是否需要提交审核
			isAudit = false;
			showLoadingDialog();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
			params.add(new BasicNameValuePair("orderUid", QorderUid));
			HttpUtils.requestPost(URLs.SubmitWork(), params, HttpRequestTool.SUBMIT_WORK);
		}else {
			setFinisDialog(values);
		}
	}
	
	
	/**弹出提示框，并在提示框关闭的时候结束当前Activity**/
	private void setFinisDialog(List<NameValuePair> values){
		postEvent="NEW_ORDER";
//		Dialog dialog=DialogUtil.getAlertOneButton(this, values.get(0).getValue(), null);
		
		Dialog dialog2=DialogUtil.getAlertOneButton(this, values.get(0).getValue(), null);
		
		dialog2.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				WorkOrderActivty.this.finish();
			}
		});
		dialog2.show();
	}
	
	/**  
	    * 监听Back键按下事件,方法2:  
	    * 注意:  
	    * 返回值表示:是否能完全处理该事件  
	    * 在此处返回false,所以会继续传播该事件.  
	    * 在具体项目中此处的返回值视情况而定.  
	    */    
	    @Override    
	    public boolean onKeyDown(int keyCode, KeyEvent event) {    
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {    
	        	backHint(); 
	             return false;    
	        }else {    
	            return super.onKeyDown(keyCode, event);    
	        }    
	    } 
	    /**用户按下返回键提醒用户是否暂存**/
	   private void backHint(){
		   DialogUtil.getAlertOnelistener(this, "请确认填写信息已保存或已暂存！", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				TemporaryType=1;
				webview.loadUrl("javascript:getMobileWeb(\'"+(("2".equals(status))?"TEMPORARY":"SAVE_TAG")+"\')");//暂存或者保存
			}
		},("2".equals(status))?"暂存":"保存","继续退出",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				 WorkOrderActivty.this.finish();
			}
		}).show();
		   
	   }
	   
	   @Subscribe(threadMode=ThreadMode.MAIN)
		public void choiceEvent(String eventMsg){
			if (eventMsg.equals("compressSuccess")) {
				workhelp.adapterNotify();
			}
		}
	   
	   /**初始化**/
	   @Override
	protected void onResume() {
		super.onResume();
		if (workhelp!=null && workhelp.expanAdapter!=null) {
			workhelp.adapterNotify();
		}
		if (cameraHelp==null) {
			cameraHelp=new PhotographyHelp(this);
		}
	}
	   @Override
	protected void onPause() {
		super.onPause();
		timer.cancel();
		dialogdismiss();
	}
	   
	   @SuppressLint("CommitPrefEdits")
	@Override
	protected void onStop() {
		super.onStop();
		//通过共享参数储存已经拍摄的照片信息（路径）
		Editor mEditor=AppApplication.sp.edit();
		mEditor.putString("PathList:"+QorderUid, JSON.toJSONString(workhelp.resousePathList));
		mEditor.commit();
		mEditor.clear();
		Log.i("resousePathList", JSON.toJSONString(workhelp.resousePathList));
	}
	   
	   /***测试功能**/
	   private boolean flag_get_deviceid=false;
	   @SuppressLint("NewApi")
	private void mLoadUrl(){
//			String key="";
			String androidID="";
			try{
				androidID = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
			}catch(Exception e){
			}finally{
//				String script=String.format("javascript:getDeviceID('"+androidID+"')");
				webview.evaluateJavascript(tempstr, new ValueCallback<String>() {
					  @Override
					  public void onReceiveValue(String value) {
					      if(value!=null){
					    	  flag_get_deviceid=true;
					      }
					  }});
//				webview.loadUrl(tempstr);
				//mWebview.loadUrl("javascript:getDeviceID('maomao')");
			}
		}
	   
	   private class WebViewClientDemo extends WebViewClient {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);// 当打开新链接时，使用当前的 WebView，不会使用系统其他浏览器
				return true;
			}
			
			@Override
			  public void onPageFinished(WebView view, String url) {
			      super.onPageFinished(view, url);
			      //在这里执行你想调用的js函数
			      webview.loadUrl(tempstr);
			      if(!flag_get_deviceid){
			    	  mLoadUrl();
			      }
			  }
		}
	   
	   
}
