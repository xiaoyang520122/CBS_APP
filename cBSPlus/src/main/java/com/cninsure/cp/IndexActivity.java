package com.cninsure.cp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.cninsure.cp.activity.yjx.YjxTempStorageActivity;
import com.cninsure.cp.activty.AboutUsAvtivity;
import com.cninsure.cp.activty.BankCardActivity;
import com.cninsure.cp.activty.CancelAndSubmitActivity;
import com.cninsure.cp.activty.ChangPassActivity;
import com.cninsure.cp.activty.HelpCenterActivity;
import com.cninsure.cp.activty.MessageCenterActivity;
import com.cninsure.cp.activty.ScoreActivity;
import com.cninsure.cp.activty.SecurityCenterActivity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.fc.activity.CaseInputActivity;
import com.cninsure.cp.fc.activity.DispersiveCaseInputActivity;
import com.cninsure.cp.fc.activity.WaterCaseInputActivity;
import com.cninsure.cp.fragment.MyCenterFragment;
import com.cninsure.cp.fragment.MyOrderListFragment;
import com.cninsure.cp.fragment.OrderNowFragment;
import com.cninsure.cp.fragment.WaitCaseFragment;
import com.cninsure.cp.utils.ActivityManagerUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.PopupWindowUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.permission_util.FloatingWindowPermissionUtil;
import com.cninsure.cp.utils.permission_util.PermissionApplicationUtil;
import com.cninsure.cp.view.LoadingDialog;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.karics.library.zxing.android.CaptureActivity;

public class IndexActivity extends BaseActivity implements OnClickListener {

	private TextView ordersearchTV, ordernowTV, mycenterTV, moreTv,WaitTv;
	private FragmentManager fm;
	private FragmentTransaction ft;
	private Fragment f1;
	private MyCenterFragment f3;
	private OrderNowFragment f2;
	private WaitCaseFragment f4;
	public static IndexActivity instance;
	private Spinner moreSpinner;
	private static final int REQUEST_CODE_SCAN = 0x0000;
	private static final String DECODED_CONTENT_KEY = "codedContent";
	private static final String DECODED_BITMAP_KEY = "codedBitmap";
	private SlidingMenu menu;
	public LoadingDialog loadDialog;
	private ImageView addOrderImg;
	private PopupWindow popupWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activty_index);
		ActivityManagerUtil.getInstance().addToList(this);
		instance = this;
		initView();
		new PermissionApplicationUtil(this); //申请读写权限和拍照权限
		FloatingWindowPermissionUtil.isAppOps(this);  //悬浮弹框权限检查
	}

	private void initView() {
		loadDialog=new LoadingDialog(this);
		ordersearchTV = (TextView) findViewById(R.id.bmenu_orderList_tv);
		ordernowTV = (TextView) findViewById(R.id.bmenu_orderNow_tv);
		mycenterTV = (TextView) findViewById(R.id.bmenu_myCenter_tv);
		moreTv = (TextView) findViewById(R.id.index_fragment_more);
		WaitTv = (TextView) findViewById(R.id.bmenu_localSave_tv);
		moreSpinner = (Spinner) findViewById(R.id.index_fragment_spinner);
		addOrderImg = (ImageView) findViewById(R.id.bmenu_add_order_img);
		
		initFragment();

		ordersearchTV.setOnClickListener(this);
		ordernowTV.setOnClickListener(this);
		mycenterTV.setOnClickListener(this);
		addOrderImg.setOnClickListener(this);
		WaitTv.setOnClickListener(this);
		findViewById(R.id.index_fragment_V_LTV).setOnClickListener(this);

//		List<String> paramsList = new ArrayList<String>();
//		paramsList.add("扫 码");
//		paramsList.add("非车案件录入");
		moreSpinner.setAdapter(new SpinnerAdapter());
		moreTv.setOnClickListener(this);
		initslidingmenu();
		getVersionInfo();
	}

	private List<HashMap<String, String>> getData() {
		List<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> hmMap3 = new HashMap<String, String>();
		hmMap3.put("image", R.drawable.scan_bule_36 + "");
		hmMap3.put("text", "扫一扫");
		maps.add(hmMap3);

//		HashMap<String, String> hmMap4 = new HashMap<String, String>();
//		hmMap4.put("image", R.drawable.add_new_order_bule36 + "");
//		hmMap4.put("text", "非车接报案管理");
//		maps.add(hmMap4);

		HashMap<String, String> hmMap5 = new HashMap<String, String>();
		hmMap5.put("image", R.drawable.uped_bule36 + "");
		hmMap5.put("text", "已提交审核订单");
		maps.add(hmMap5);

		HashMap<String, String> hmMap6 = new HashMap<String, String>();
		hmMap6.put("image", R.drawable.delete_order_bule36 + "");
		hmMap6.put("text", "已取消订单");
		maps.add(hmMap6);
		
		HashMap<String, String> hmMap7 = new HashMap<String, String>();
		hmMap7.put("image", R.drawable.reflush_blue36 + "");
		hmMap7.put("text", "刷新");
		maps.add(hmMap7);
		return maps;
	}

	private void initFragment() {
		this.fm = this.getSupportFragmentManager();
		displayFragment(2);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bmenu_orderList_tv:
			displayFragment(1);
			break;
		case R.id.bmenu_orderNow_tv:
			displayFragment(2);
			break;
		case R.id.bmenu_myCenter_tv:
			displayFragment(3);
			break;
		case R.id.bmenu_localSave_tv:
			displayFragment(4);
			break; 
		case R.id.bmenu_add_order_img:
			showChoiceAddTypePopup();
			break;
		case R.id.index_fragment_more:
			moreSpinner.setVisibility(View.VISIBLE);
			moreSpinner.performClick();
			break;
		case R.id.index_fragment_V_LTV:
			menu.showMenu();
			break;
			
		case R.id.leftmenu_changpass://修改密码
			Intent intent=new Intent(this, ChangPassActivity.class);
			intent.putExtra("isChangPass", true);
			startActivity(intent);
			menu.toggle();
			break;

		case R.id.leftmenu_changphone:// 修改手机
			Intent intent2=new Intent(this, ChangPassActivity.class);
			intent2.putExtra("isChangPass", false);
			startActivity(intent2);
			menu.toggle();
			break;

		case R.id.leftmenu_mymsg:// 消息中心
			startActivity(new Intent(this, MessageCenterActivity.class));
			menu.toggle();
			break;

		case R.id.leftmenu_mymoney:// 我的业绩
			startActivity(new Intent(this, ScoreActivity.class));
			menu.toggle();
			break;

		case R.id.leftmenu_help:// 帮助中心
			startActivity(new Intent(this, HelpCenterActivity.class));
			menu.toggle();
			break;

		case R.id.leftmenu_shard://分享
			showShare();
			menu.toggle();
			break;

		case R.id.leftmenu_excit:// 退出用户
			excetAlert();
			menu.toggle();
			break;

		case R.id.addFCpopup_order_cai:// 增加财险接报案
			popupWindow.dismiss();
			startActivity(new Intent(this, CaseInputActivity.class));
			break;

		case R.id.addFCpopup_order_shui:// 增加水险接报案
			popupWindow.dismiss();
			startActivity(new Intent(this, WaterCaseInputActivity.class));
			break; 

		case R.id.addFCpopup_order_fenshanxin:// 创新及分散型接报案
			popupWindow.dismiss();
			startActivity(new Intent(this, DispersiveCaseInputActivity.class));
			break;  

		case R.id.addFCpopup_order_yjx:// 医健险接报案
			popupWindow.dismiss();
			jumpToYjxActivty();
			break; 
			
		default:
			break;
		}
	}
	
	/**判断用户是否有医健险案件录入权限，有就进入录入界面*/
	private void jumpToYjxActivty(){
		String roleIds = AppApplication.getUSER().data.roleIds;
		if (roleIds.indexOf(URLs.getRoleId()+"")>-1 || roleIds.indexOf(URLs.getDispatchId()+"")>-1) {
			startActivity(new Intent(this, YjxTempStorageActivity.class));
		}else {
			DialogUtil.getErrDialog(this, "暂时没有医健险接报案录入或调度权限！").show();
		}
	}

	/**设置十字图标旋转动画**/
	private void showChoiceAddTypePopup() {
		popupWindow=PopupWindowUtils.showPopupWindowUp(getPopupView(), addOrderImg, IndexActivity.this,handler);
		showImageAnimation();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			showImageAnimation();
		}
		
	};
	
	/**十字图标显示动画**/
	private void showImageAnimation(){
		AnimationSet animationSet = new AnimationSet(true);
		RotateAnimation rotateAnimation=new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimation.setDuration(500);
		animationSet.addAnimation(rotateAnimation);
		addOrderImg.startAnimation(animationSet);
	}
	/**获取，初始化popupwindow显示View**/
	private View getPopupView() {
		View view = LayoutInflater.from(this).inflate(R.layout.add_new_order_popup_view, null);
		view.findViewById(R.id.addFCpopup_order_cai).setOnClickListener(this);
		view.findViewById(R.id.addFCpopup_order_shui).setOnClickListener(this);
		view.findViewById(R.id.addFCpopup_order_fenshanxin).setOnClickListener(this);
		view.findViewById(R.id.addFCpopup_order_yjx).setOnClickListener(this);
		return view;
	}

	private void displayFragment(int itemfragment) {
		switch (itemfragment) {
		case 1:
			checkFragment(1);
			break;
		case 2:
			checkFragment(2);
			break;
		case 3:
			checkFragment(3);
			break;
		case 4:
			checkFragment(4);
			break;

		default:
			break;
		}
	}
	
	/**切换Fragment的方法**/
	public void checkFragment(int item){
		ft = fm.beginTransaction();
		hidFragment(ft);
			switch (item) {
			case 1:
				recoverView(ordersearchTV, R.drawable.searchorder);
				if (f1 == null){
				f1 = new MyOrderListFragment();
				ft.add(R.id.index_fragment, f1);}
				ft.show(f1);
				break;
			case 2:
				recoverView(ordernowTV, R.drawable.ordernow);
				if (f2 == null){
				f2 = new OrderNowFragment();
				ft.add(R.id.index_fragment, f2);}
				ft.show(f2);
				break;
			case 3:
				recoverView(mycenterTV, R.drawable.mycenter);
				if (f3 == null){
				f3 = new MyCenterFragment();
				ft.add(R.id.index_fragment, f3);}
				ft.show(f3);
				break;
			case 4:
				recoverView(WaitTv, R.drawable.waitcase);
				if (f4 == null){
				f4 = new WaitCaseFragment();
				ft.add(R.id.index_fragment, f4);}
				ft.show(f4);
				break;

			default:
				break;
			}
		ft.commit();
	}

	private void hidFragment(FragmentTransaction transaction) {
		if (f1 != null) {
			transaction.hide(f1);
		}
		if (f2 != null) {
			transaction.hide(f2);
		}
		if (f3 != null) {
			transaction.hide(f3);
		}
		if (f4 != null) {
			transaction.hide(f4);
		}
	}

	private void recoverView(TextView tv, int drawadbleId) {
		ordersearchTV.setTextColor(this.getResources().getColor(R.color.hui_text_h));
		ordernowTV.setTextColor(this.getResources().getColor(R.color.hui_text_h));
		mycenterTV.setTextColor(this.getResources().getColor(R.color.hui_text_h));
		WaitTv.setTextColor(this.getResources().getColor(R.color.hui_text_h));

		ordersearchTV.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.searchorder_hui), null, null);
		ordernowTV.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.ordernow_hui), null, null);
		mycenterTV.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.mycenter_hui), null, null);
		WaitTv.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(R.drawable.waitcase_hui), null, null);

		tv.setCompoundDrawablesWithIntrinsicBounds(null, this.getResources().getDrawable(drawadbleId), null, null);
		tv.setTextColor(this.getResources().getColor(R.color.bule_text_h));
	}

	public void goToPage(View v) {
		switch (v.getId()) {
		case R.id.my_menu_info:// 个人信息
			ToastUtil.showToastLong(this, "功能开发中……");
			break;
		case R.id.my_menu_protect:// 安全中心
			startActivity(new Intent(this, SecurityCenterActivity.class));
			break;

		case R.id.my_menu_score:// 我的业绩
			startActivity(new Intent(this, ScoreActivity.class));
			break;

		case R.id.my_menu_backcard:// 银行卡
			Intent intent = new Intent(this, BankCardActivity.class);
//			UserInfo ui=f3.userInfo;
//			intent.putExtra("UserInfo", ui);
			startActivity(intent);
			break;

		case R.id.my_menu_help:// 帮助
			startActivity(new Intent(this, HelpCenterActivity.class));
			break;

		case R.id.my_menu_msgcenter:// 消息中心
			startActivity(new Intent(this, MessageCenterActivity.class));
			break;

		case R.id.my_menu_uploadbanben:// 版本更新
			getVersionInfo();
			break;

		case R.id.my_menu_aboutus:// 关于我们
			startActivity(new Intent(this, AboutUsAvtivity.class));
			break;

		case R.id.my_menu_share:// 分享
			showShare();
			break;

		case R.id.my_menu_excetUser:/** 退出用户**/
			excetAlert();
			break;

		default:
			break;
		}
	}

	private void excetAlert() {
		if (f3==null) 
			f3 = new MyCenterFragment();
		DialogUtil.getAlertOnelistener(this, "确认退出当前用户“" + AppApplication.getUSER().data.name + "”吗？", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				loadDialog.setMessage("努力加载中……！").show();
				List<NameValuePair> NVparames = new ArrayList<NameValuePair>(1);
				NVparames.add(new BasicNameValuePair("clientId", "0"));
				NVparames.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
				HttpUtils.requestPost(URLs.UpCid(), NVparames, HttpRequestTool.CLEAN_CID);
			}
		}).show();
	}
	
	/** 退出当前用户 **/
	public void excetUser() {
		loadDialog.dismiss();
		Editor editor = AppApplication.sp.edit();
		editor.putString("tenantPinyinInitials", "");
		editor.putString("loginName", "");
		editor.putString("password", "");
		editor.commit();
		editor.clear();
		AppApplication.emptyUSER();
		startActivity(new Intent(this, LoginActivity.class));
		this.finish();
	}

	private void getVersionInfo() {
		try {
			List<String> params = new ArrayList<String>();
			params.add("userId");
			params.add(AppApplication.getUSER().data.userId);
			params.add("type");
			params.add("1");
			HttpUtils.requestGet(URLs.GetVersionInfo(), params, HttpRequestTool.GET_VERSION_INFO);
		} catch (Exception e) {
			startActivity(new Intent(AppApplication.mInstance, LoadingActivity.class));
			e.printStackTrace();
		}
	}

	@SuppressLint("SdCardPath")
	private void showShare() {
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不 调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("公估CBS");
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(URLs.APP_DOWNLOAD_URL);
		// text是分享文本，所有平台都需要这个字段
		oks.setText("下载泛华公估掌上作业平台-公估CBS");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(URLs.APP_DOWNLOAD_URL);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("小伙伴们快来下载公估CBSAPP和我一起轻松作业吧！");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(URLs.APP_DOWNLOAD_URL);

		// 启动分享GUI
		oks.show(this);
	}

	/**
	 * 二维码扫描返回值接收
	 */
	@SuppressWarnings("unused")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 扫描二维码/条码回传
		if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
			if (data != null) {

				String content = data.getStringExtra(DECODED_CONTENT_KEY);
				Log.i("JsonHttpUtils", "扫码内容：" + content);
				Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
				if (content.indexOf("type=") > -1 && content.indexOf("&uid=") > -1) {//保证扫描的是我们的二维码
					int typeCode = Integer.parseInt(content.substring(content.indexOf("=") + 1, content.indexOf("&")));
					switch (typeCode) {
					case 1:
						ScannerLogin(content);
						break;

					default:
						break;
					}
				} else {
					DialogUtil.getAlertOneButton(this, "未知的二维码,请确认二维码后重试。\n\n扫码内容：" + content, null).show();
				}
			}
		}
	}

	private void ScannerLogin(String content) {// type=1&uid=123456789
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String uid = content.substring(content.indexOf("&uid=") + 5, content.length());
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		params.add(new BasicNameValuePair("status", "2"));
		HttpUtils.requestPost(URLs.SCANNER_LOGIN, params, HttpRequestTool.SCANNER_SUCCESS);
		Intent intent = new Intent(this, ScannerLoginActivity.class);
		startActivity(intent.putExtra("uid", content.substring(content.indexOf("&uid=") + 5, content.length())));
	}

	private class SpinnerAdapter extends BaseAdapter {
		LayoutInflater inflater;
		List<? extends Map<String, String>> data;

		public SpinnerAdapter() {
			data = getData();
			inflater = LayoutInflater.from(IndexActivity.this);
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int arg0) {
			return data.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int arg0, View conView, ViewGroup arg2) {
			conView = inflater.inflate(R.layout.spinner_item_bule, null);
			((ImageView) conView.findViewById(R.id.spinner_item_img)).setImageResource(Integer.parseInt((String) data.get(arg0).get("image")));
			((TextView) conView.findViewById(R.id.spinner_item_textone)).setText((String) data.get(arg0).get("text"));
			setOnclickJump(arg0,conView);
			return conView;
		}

		private void setOnclickJump(int arg0, View conView) {
			if (arg0==0) {
				conView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(IndexActivity.this, CaptureActivity.class);
						startActivityForResult(intent, REQUEST_CODE_SCAN);
						moreSpinner.setVisibility(View.GONE);
					}
				});
//			}else if (arg0==1)  {//跳转到飞车接报案录入界面
//				conView.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						IndexActivity.this.startActivity(new Intent(IndexActivity.this, CaseReportActivity.class));
//						moreSpinner.setVisibility(View.GONE);
//					}
//				});
				
			}else if (arg0==1)  {//跳转到已提交审核订单查询界面
				conView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						IndexActivity.this.startActivity(new Intent(IndexActivity.this, CancelAndSubmitActivity.class).putExtra("actionType", 0));
						
						moreSpinner.setVisibility(View.GONE);
					}
				});
				
			}else if (arg0==2)  {//跳转到已取消订单查询界面
				conView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						IndexActivity.this.startActivity(new Intent(IndexActivity.this, CancelAndSubmitActivity.class).putExtra("actionType", 1));
						moreSpinner.setVisibility(View.GONE);
					}
				});
			}else if (arg0==3)  {//跳转到已取消订单查询界面
				conView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						IndexActivity.this.f2.downloadOrderData(1);
						moreSpinner.setVisibility(View.GONE);
					}
				});
			}
		}
	}
	
	/***点击右上角下拉spinner的item的单击事件调用**/
//	private void checkToFragment(int type){
//		checkFragment(2);
//		f2 .changCheckWorkType(type+1);
//	}
	
	/**
	 * 初始化侧滑菜单并设置
	 */
	public void initslidingmenu() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		// 设置触摸屏幕的模式
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width_menu);
		menu.setShadowDrawable(R.drawable.shadow_two);
		// 设置滑动菜单视图的宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset_two);
		// 设置渐入渐出效果的值
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// 为侧滑菜单设置布局
		menu.setMenu(R.layout.leftmenu);
		getmenuView();
	}

	private void getmenuView() {
		View view=menu.getMenu();
		if (view!=null) {
			TextView name=(TextView) view.findViewById(R.id.leftmenu_name);
			try {
				if (!TextUtils.isEmpty(AppApplication.getUSER().data.name)) {
					name.setText(AppApplication.getUSER().data.name);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			view.findViewById(R.id.leftmenu_changpass).setOnClickListener(this);
			view.findViewById(R.id.leftmenu_changphone).setOnClickListener(this);
			view.findViewById(R.id.leftmenu_mymsg).setOnClickListener(this);
			view.findViewById(R.id.leftmenu_mymoney).setOnClickListener(this);
			view.findViewById(R.id.leftmenu_help).setOnClickListener(this);
			view.findViewById(R.id.leftmenu_shard).setOnClickListener(this);
			view.findViewById(R.id.leftmenu_mymoney).setOnClickListener(this);
			view.findViewById(R.id.leftmenu_excit).setOnClickListener(this);
		}
	}

	/**
	 * 显示侧滑菜单的方法
	 */
	public void shoumenuu() {
		menu.showMenu();
	}

}
