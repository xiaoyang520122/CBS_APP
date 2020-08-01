package com.cninsure.cp.activty;

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
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.activty.WorkOrderActivty.JsInteration;
import com.cninsure.cp.entity.LeavingMessageEntity;
import com.cninsure.cp.entity.OCREntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.WorkMessageEntity;
import com.cninsure.cp.entity.WorkPhotos;
import com.cninsure.cp.entity.WorkType;
import com.cninsure.cp.entity.fc.ShenheMsgEntity;
import com.cninsure.cp.utils.ActivityManagerUtil;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.LoadingDialog;
import com.cninsure.cp.view.MyWebView;
import com.cninsure.cp.view.MyWebView.PlayFinish;

public class DisplayOrderActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {

	// private ExpandableListView expandableListView;
	private MyWebView webview;
	private View uploadView,leavingMsgMainView;//,leavingMsgView;
	private ScrollView scrollview;
	private String request1, QorderUid;
	private TextView actionTV1, actionTV2, actionTV3;
	private RadioButton title1Tv, title2Tv,title3Tv;
	private RadioGroup radioGroup;
	private ViewPager vpager;
	private List<View> viewlist;
	private PagerAdapter pagerAdapter;
	private TranslateAnimation moveanim;
	private DisplayOrderActivtyhelp workhelp;
	private LoadingDialog loaddialog;
	private WorkType photoType;
	private WorkPhotos workphotos;
	/** 不同作业类型加载界面 **/
	private String[] workUrls = new String[] { "/m_survey_index", "/m_assess_index", "/m_material_index", "/m_yyts_index", "/m_mediation_index"};
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
	private int isFirstLoadingWebview;
	/**暂存类型：0为手动暂存，1为提示暂存，为1时暂存后需要结束当前界面*/
	private int TemporaryType=0;
	/**如果需要刷新首页列表就填写“NEW_ORDER”*/
	private String postEvent="";
	/**留言返回数据**/
	private LeavingMessageEntity leavingMsgdata;
	/**审核信息**/
	private ShenheMsgEntity SHHMsg;
	/**留言列表listView*/
	private ListView LeavingListView;
	private LayoutInflater inflater;

	/**OCR解析信息及图片路径1,身份证，2银行卡，3驾驶证，4行驶证，5签名**/
	public OCREntity ocrEntity1,ocrEntity2,ocrEntity3,ocrEntity4,ocrEntity5;
	public WorkMessageEntity contentMessage;
	/**是否提交审核**/
	private boolean isSubmit=false,isLoadingSuccess=false;
//	private Button backButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_worder_activity);
		ActivityManagerUtil.getInstance().addToList(this);
		EventBus.getDefault().register(this);
		loaddialog = new LoadingDialog(this);
		status = getIntent().getStringExtra("status");
		if (status.equals("6")) {//如果状态是6就
			isSubmit=true;
		}
		dowloadPhotoType();
		getDefaulMessage(1);
		initaction();
		initView();
	}

	private void dowloadPhotoType() {
		List<String> params = new ArrayList<String>(2);
		params.add("type");
		if ("6".equals(getIntent().getStringExtra("taskType"))) {
			params.add("hospitaLPI");
		} else {
			params.add("commonInfo");
		}
		HttpUtils.requestGet(URLs.GetPhotoType(), params, HttpRequestTool.GET_PHOTO_TYPE);
		showLoadingDialog();
	}
	
	private void showLoadingDialog(){
		if (!loaddialog.isShowing()) {
			try {
				loaddialog.setMessage("努力加载中……").show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void initView() {
		inflater=LayoutInflater.from(this);
		title1Tv = (RadioButton) findViewById(R.id.DisplayOrder_title1);
		title2Tv = (RadioButton) findViewById(R.id.DisplayOrder_title2);
		title3Tv = (RadioButton) findViewById(R.id.DisplayOrder_leavingMsg);
//		backButton = (Button) findViewById(R.id.DisplayOrder_back_button);
		vpager = (ViewPager) findViewById(R.id.DisplayOrder_viewpager);
		radioGroup  = (RadioGroup) findViewById(R.id.DisplayOrder_btnG);
		uploadView = inflater.inflate(R.layout.imageupload_view, null);
//		leavingMsgView = inflater.inflate(R.layout.leaving_message_head_view, null);
		leavingMsgMainView = inflater.inflate(R.layout.teaving_message_view, null);
		scrollview = (ScrollView) inflater.inflate(R.layout.display_orderinfo_scrollview, null);
		
		LeavingListView=(ListView) leavingMsgMainView.findViewById(R.id.LeavingMsg_listView);
//		LeavingListView.addHeaderView(leavingMsgView);
		LeavingListView.setEmptyView(inflater.inflate(R.layout.empty_view, null));
//		backButton.setOnClickListener(this);
//		displayLeavingMsg();//获取留言信息
		setonclick();
	}
	
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
	
//		if (webview==null) {
//			initWebView();
//			viewlist = new ArrayList<View>(2);
//			scrollview.addView(webview);
//			viewlist.add(scrollview);
//			viewlist.add(uploadView);
//			viewlist.add(leavingMsgMainView);
//			initviewpageradapter();
//			vpager.setAdapter(pagerAdapter);
//		}else {
//			webview.loadUrl(tempstr);
//		}
	}

	private void setonclick() {
		title1Tv.setOnClickListener(this);
		title2Tv.setOnClickListener(this);
		title3Tv.setOnClickListener(this);
		vpager.setOnPageChangeListener(this);
//		leavingMsgView.findViewById(R.id.LeavingMsg_submit_button).setOnClickListener(this);
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
		
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});

		webview.loadUrl(getWorkSpace(URLs.WORK_SPACE, workUrls));
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
	
	private Handler handler2=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==1002) {
				webview.loadUrl(tempstr);
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
					DisplayOrderActivity.this.finish();
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
		actionTV2.setText("订单信息");
		actionTV3.setText("");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DisplayOrderActivity.this.finish();
			}
		});
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.DisplayOrder_title1:
			vpager.setCurrentItem(0);
			break;
		case R.id.DisplayOrder_title2:
			vpager.setCurrentItem(1);
			break;
		case R.id.DisplayOrder_leavingMsg:
			vpager.setCurrentItem(2);
			break;

		case R.id.LeavingMsg_submit_button:
			submitLeavingMsg();
			break;

//		case R.id.DisplayOrder_back_button:
//			this.finish();
//			break;

		default:
			break;
		}
	}

/**上传留言信息**/
	private void submitLeavingMsg() {
//		EditText leavingText=((EditText)leavingMsgView.findViewById(R.id.LeavingMsg_edit));
//		if (TextUtils.isEmpty(leavingText.getText().toString())) {
//			DialogUtil.getAlertOneButton(this, "留言不能为空", null).show();
//			return;
//		}
		showLoadingDialog();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		params.add(new BasicNameValuePair("workUid", getIntent().getStringExtra("orderUid")));
//		params.add(new BasicNameValuePair("message", leavingText.getText().toString()));
		HttpUtils.requestPost(URLs.SAVE_LEAVING_MESSAGE, params, HttpRequestTool.SAVE_LEAVING_MESSAGE);
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
//		if (pi == 0) {
//			radioGroup.check(R.id.DisplayOrder_title1);
//			backButton.setVisibility(View.VISIBLE);
//		}else if (pi == 1) {
//			radioGroup.check(R.id.DisplayOrder_title2);
//			backButton.setVisibility(View.GONE);
//		} else {
//			radioGroup.check(R.id.DisplayOrder_leavingMsg);
//			backButton.setVisibility(View.GONE);
//		}
	}

	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventmeth(String successCode){
		if ("UPLOAD_SUCCESS".equals(successCode)) {
			downloadWorkPhotos();
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
		Log.e("JsonHttpUtils", workhelp.dataToString());
		EventBus.getDefault().post(postEvent);
		EventBus.getDefault().unregister(this);
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
					ToastUtil.showToastLong(DisplayOrderActivity.this, "已经来拿去了头部信息");
					Log.e("JsonHttpUtils", "3##########"+"已经来拿去了头部信息"+tempstr);
					loadurlM();
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
		public void onSumResult(int result) {
			Log.i(LOGTAG, "onSumResult result=" + result);
		}
	}
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==HttpRequestTool.SAVE_WORK_INFO) {
				showLoadingDialog();
			}
		}
		
	};

	/**
	 *  SAVE_EVENT：保存事件，AUDIT_EVENT： 保存并提交审核事件,TEMPORARY_STORAGE_EVENT暂存,LOAD_SUCCESS获取回显信息成功
	 * @throws JSONException
	 **/
	private void saveWorkInfo(String head, String data) throws JSONException {
		Log.e("LOAD_SUCCESS", "页面接收到消息了！"+data);
		Log.e("LOAD_SUCCESS", "LOAD_SUCCESS=="+head);
		if ("LOAD_SUCCESS".equals(head)) {//如果页面接收到回显信息后会返回小消息，然后停止传递数据
			Log.e("JsonHttpUtils", "LOAD_SUCCESS=="+head);
			timer.cancel();
			loaddialog.dismiss();
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
			value = jsonObject.getString(key);
			params.add(new BasicNameValuePair(key, value));
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
				|| responsecode == HttpRequestTool.SAVE_LEAVING_MESSAGE || responsecode == HttpRequestTool.GET_ORDER_STATUS) {
			loaddialog.dismiss();
		}
		/**这里是加载HTML作业界面前调用接口走的地方，在HTML界面调用结束后才调用的接口不能走这里关闭loaddialog**/
		if (responsecode == HttpRequestTool.GET_WORK_MESSAGE || responsecode == HttpRequestTool.GET_WORK_MESSAGES || 
				responsecode == HttpRequestTool.GET_PHOTO_TYPE || responsecode == HttpRequestTool.GET_WORK_PHOTO) {
			if (++isFirstLoadingWebview>4) {//保证第一次打开该界面时，在这四个数据加载完成之后不要关闭遮罩，等与web交互后由web关闭
				loaddialog.dismiss();
			}
		}
		switch (CheckHttpResult.checkList(values, this)) {
		case HttpRequestTool.GET_WORK_MESSAGE:
			headMessage=values.get(0).getValue();
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
//			isLoadingSuccess=false;
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
			workhelp = new DisplayOrderActivtyhelp(this, uploadView, photoType, workphotos);
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
//			showSaveMessage(values.get(0).getValue());
			break; 
			
		case HttpRequestTool.GET_ORDER_STATUS: //审核信息
			showSHHMessage(values.get(0).getValue());
			break; 

		default:
			break;
		}

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
	
	/**显示获取的留言信息*/
	private void showLeavingMsg(String value) {
		downloadSHHMsg();
		leavingMsgdata = JSON.parseObject(value, LeavingMessageEntity.class);
	}
	
	/**解析审核信息**/
	private void showSHHMessage(String value) {
		SHHMsg= JSON.parseObject(value, ShenheMsgEntity.class);
		List<Map<String, String>> params=new ArrayList<Map<String, String>>();
		Map<String, String> map;
		map=new HashMap<String, String>();
		map.put("data", "");
		map.put("msg", "审核列表");
		params.add(map);
		
		if (SHHMsg!=null && SHHMsg.tableData != null && SHHMsg.tableData.data != null && SHHMsg.tableData.data.get(0).createDate!=null) {
			for (int i = 0; i < SHHMsg.tableData.data.size(); i++) {
				String cctype="";
				for (int j = 0; j < SHHMsg.tableData.data.get(i).auditEvaluateTables.size(); j++) {
					cctype+="\n"+(j+1)+"、错误类型："+SHHMsg.tableData.data.get(i).auditEvaluateTables.get(j).errorType+
							"\t差错原因："+SHHMsg.tableData.data.get(i).auditEvaluateTables.get(j).errorMessage+
							"\t差错扣分："+SHHMsg.tableData.data.get(i).auditEvaluateTables.get(j).errorPoints;
				}
					map=new HashMap<String, String>();
					map.put("data", SHHMsg.tableData.data.get(i).createDate);
					boolean ispasss=SHHMsg.tableData.data.get(i).isPass.equals("1");
					String ispass=(ispasss)?"通过":"不通过";
					map.put("msg", "审核结果："+ispass+"\t\t审核意见："+
							SHHMsg.tableData.data.get(i).auditMessage+cctype);
					params.add(map);
			}
		}else {
			map=new HashMap<String, String>();
			map.put("data", "");
			map.put("msg", "无");
			params.add(map);
		}
		map=new HashMap<String, String>();
		map.put("data", "");
		map.put("msg", "留言列表");
		params.add(map);
		if (leavingMsgdata!=null && leavingMsgdata.tableData != null && leavingMsgdata.tableData.data != null ) {
			try {
			for (int i = 0; i < leavingMsgdata.tableData.data.size(); i++) {
				map=new HashMap<String, String>();
				map.put("data", leavingMsgdata.tableData.data.get(i).createDate);
				map.put("msg", leavingMsgdata.tableData.data.get(i).message);
				params.add(map);
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			map=new HashMap<String, String>();
			map.put("data", "");
			map.put("msg", "无");
			params.add(map);
		}
		SimpleAdapter simpleAdapter=new SimpleAdapter(this, params, R.layout.leaving_message_item,
				new String[]{"data","msg"}, new int[]{R.id.LEAVINGitem_time,R.id.LEAVINGitem_message});
		LeavingListView.setAdapter(simpleAdapter);
	}

	private void displayLeavingMsg() {
		downloadLeavingMsg();
//		try {
//			JSONObject headMsg=new JSONObject(headMessage);
//			JSONObject data=headMsg.getJSONObject("data");
//			((TextView)leavingMsgView.findViewById(R.id.LeavingMsg_wtren)).setText(data.optString("entrusterName", "委托人名称（无）"));
//			((TextView)leavingMsgView.findViewById(R.id.LeavingMsg_busstype)).setText(data.optString("bussTypeName", "归属机构（无）"));
//			((TextView)leavingMsgView.findViewById(R.id.LeavingMsg_address)).setText(data.optString("caseLocationFull", "地址（无）"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
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
	/**获取审核信息 **/
	private void downloadSHHMsg(){
		/**获取审核信息*/
		List<String> params2 = new ArrayList<String>();
		params2.add("userId");
		params2.add(AppApplication.getUSER().data.userId);
		params2.add("orderUid");
		params2.add(getIntent().getStringExtra("orderUid"));
		HttpUtils.requestGet(URLs.GET_ORDER_STATUS, params2, HttpRequestTool.GET_ORDER_STATUS);
		
	}

	/**区分“保存”和“保存并提交审核”请求后分别进行处理**/
	private void SubmitOrder(List<NameValuePair> values) {
		ToastUtil.showToastShort(getApplicationContext(), values.get(0).getValue());
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
		
		Dialog dialog2=DialogUtil.getAlertOnelistener(this, values.get(0).getValue(), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				DisplayOrderActivity.this.finish();
			}
		}, "退出", "继续操作", null);
		
		dialog2.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				String temp=getIntent().getStringExtra("status");
//				if (getIntent().getStringExtra("status").equals("2")) {//保证是作业中的订单保存后状态修改了才刷新界面
					status="5";
					getDefaulMessage(1);
//				}
			}
		});
		dialog2.show();
	}
	
}
