package com.cninsure.cp.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.IndexActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.activity.yjx.YjxNoShenheOrderActivity;
import com.cninsure.cp.activity.yjx.YjxTempStorageActivity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.UserInfo;
import com.cninsure.cp.entity.extract.ExtUserEtity;
import com.cninsure.cp.utils.APPDownloadUtils;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.view.LoadingDialog;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

public class MyCenterFragment extends Fragment {

	// private LinearLayout
	// userinfoLin,protectLin,musicLin,wifiLin,helpLin,msgLin,editionLin,aboutLin,shareLin,excetLin;
	private TextView ggsNameTv, ggsTypeTv, deptTv, editionTv,signStatusTv;
	private List<String> paramsList;
	private LoadingDialog loadDialog;
	public UserInfo userInfo;
	private ToggleButton togMusic, togWifiSet;

	private View contentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.fragment_mycenter, null);
		EventBus.getDefault().register(this);
		initView();
		downLoadUserInfo();
		return contentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
		displaySHView();
	}

	@Override
	public void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}
	
	/**判断用户是否有医健险审核权限，有就显示审核界面*/
	private void displaySHView(){
		String roleIds = AppApplication.getUSER().data.roleIds;
		if (roleIds.indexOf(URLs.getSHId()+"")>-1) { //
			contentView.findViewById(R.id.my_menu_yjxShenhe).setVisibility(View.VISIBLE);
			contentView.findViewById(R.id.my_menu_yjxShenhe).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) { //打开审核案件列表界面
					getActivity().startActivity(new Intent(getActivity(), YjxNoShenheOrderActivity.class));
				}
			});
		}else {
			contentView.findViewById(R.id.my_menu_yjxShenhe).setVisibility(View.GONE);
		}
	}

	private void initView() {
		togMusic = (ToggleButton) contentView.findViewById(R.id.editer_setmarker_type);
		togWifiSet = (ToggleButton) contentView.findViewById(R.id.editer_setmarker_wifi);
		loadDialog = new LoadingDialog(getActivity());

		ggsNameTv = (TextView) contentView.findViewById(R.id.my_menu_username);
		ggsTypeTv = (TextView) contentView.findViewById(R.id.my_menu_usertype);
		signStatusTv = (TextView) contentView.findViewById(R.id.my_menu_signStatus);
		deptTv = (TextView) contentView.findViewById(R.id.my_menu_usDept);
		editionTv = (TextView) contentView.findViewById(R.id.my_menu_dqbb);

		editionTv.setText("当前版本：" + IndexActivity.instance.getAppVersion(1));
		setToggleButton();
	}

	private void setToggleButton() {
		if (AppApplication.sp.getString("setLoginName", "").equals(AppApplication.getUSER().data.loginName)) {
			togMusic.setToggleOn(AppApplication.sp.getBoolean("isPlayMusic", true));
			togWifiSet.setToggleOn(AppApplication.sp.getBoolean("isWifiUp", false));
		} else {
			togMusic.setToggleOn(true);
			togWifiSet.setToggleOn(false);
		}

		togMusic.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				Editor editor = AppApplication.sp.edit();
				editor.putString("setLoginName", AppApplication.getUSER().data.loginName);
				editor.putBoolean("isPlayMusic", on);
				editor.commit();
				editor.clear();
			}
		});

		togWifiSet.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				Editor editor = AppApplication.sp.edit();
				editor.putString("setLoginName", AppApplication.getUSER().data.loginName);
				editor.putBoolean("isWifiUp", on);
				editor.commit();
				editor.clear();
			}
		});
	}

	private void downLoadUserInfo() {
		paramsList = new ArrayList<String>();
		paramsList.add("userId");
		paramsList.add(AppApplication.getUSER().data.userId);
		paramsList.add("targetUserId");
		paramsList.add(AppApplication.getUSER().data.userId);
		HttpUtils.requestGet(URLs.GetUserInfo(), paramsList, HttpRequestTool.GET_USER_INFO);
		loadDialog.setMessage("数据加载中……").show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventDownLoadMyorderList(List<NameValuePair> value) {
		int typecode = Integer.parseInt(value.get(0).getName());
		if (typecode == HttpRequestTool.GET_USER_INFO || typecode == HttpRequestTool.GET_VERSION_INFO) {
			loadDialog.dismiss();
		}
		if (typecode == HttpRequestTool.CLEAN_CID && CheckHttpResult.checkList(value, getActivity())!=HttpRequestTool.CLEAN_CID ) {
			ToastUtil.showToastLong(getActivity(), "退出用户失败!");
			((IndexActivity)getActivity()).loadDialog.dismiss();
		}
		switch (CheckHttpResult.checkList(value, getActivity())) {
		case HttpRequestTool.GET_USER_INFO:
			userInfo = JSON.parseObject(value.get(0).getValue(), UserInfo.class);
			showUserInfo();
			break;
		case HttpRequestTool.GET_VERSION_INFO://版本信息
			handleVersion(value.get(0).getValue());
			break;
		case HttpRequestTool.CLEAN_CID:
			((IndexActivity)getActivity()).excetUser();//indexActivity中请求清空服务器端CID成功后在这里调用indexActivity中方法退出用户
			break;

		default:
			break;
		}

	}

	private void handleVersion(String value) {
		try {
			final JSONObject object = new JSONObject(value).getJSONObject("data");
			int versioncose=Integer.valueOf(object.optString("versionCode"));
			int sysVersonCode=Integer.valueOf((IndexActivity.instance.getAppVersion(2)));
			final String DownloadUrl=object.getString("clientUrl");
			if (versioncose <= sysVersonCode) {
				DialogUtil.getAlertOneButton(getActivity(), "现在已是最新版本，无需更新！", null).show();
			} else {
				Dialog dialog=DialogUtil.getAlertOneButton(getActivity(), "有新的版本可以更新！\n最新版本号：" + object.optString("versionName") + "\n更新信息：" + object.optString("message"),null);
				dialog.show();
				dialog.setOnDismissListener(new DialogInterface. OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface arg0) {
//						HttpUtils.openUrl(IndexActivity.instance,URLs.APP_DOWNLOAD_URL);
//						getActivity().finish();
						new APPDownloadUtils((IndexActivity) getActivity()).downloadAPK(DownloadUrl, "CBSPlus");
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showUserInfo() {
		if (userInfo != null && userInfo.data != null) {
			ggsNameTv.setText(userInfo.data.name);
			if (!userInfo.data.allRoleNames.equals("null") || !TextUtils.isEmpty(userInfo.data.allRoleNames)) {
				String rolesName="";
				if (userInfo.data.rolesName!=null) {
					rolesName=userInfo.data.rolesName;
				}
				ggsTypeTv.setText(userInfo.data.allRoleNames+","+rolesName);//"用户类型：" + 
			}else {
				ggsTypeTv.setText("暂无用户角色信息！");
			}
			deptTv.setText("归属机构：" + userInfo.data.organizationSelfName);
		}
		setExtactSignStatus();
	}

	/***
	 * 如果是外部公估师，显示签约状态
	 */
	private void setExtactSignStatus() {
		ExtUserEtity extUserEtity = OrderNowFragment.extactUserUtil.extUserEtity;
		if (extUserEtity!=null && extUserEtity.data!=null && extUserEtity.data.status!=null ){
			if (extUserEtity.data.status == 0) signStatusTv.setText("未签约");
			if (extUserEtity.data.status == 1) signStatusTv.setText("已签约");
			contentView.findViewById(R.id.my_menu_signStatusLineaLayout).setOnClickListener(v -> {
				OrderNowFragment.extactUserUtil.jumpToSignView(getActivity());
			});
		}else {
			signStatusTv.setText("");
		}
	}

}
