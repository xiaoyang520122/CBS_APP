package com.cninsure.cp.dispersive;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.LoadingActivity;
import com.cninsure.cp.LoginActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.activty.AboutUsAvtivity;
import com.cninsure.cp.activty.AccountActivity;
import com.cninsure.cp.activty.HelpCenterActivity;
import com.cninsure.cp.activty.MessageCenterActivity;
import com.cninsure.cp.activty.ScoreActivity;
import com.cninsure.cp.activty.SecurityCenterActivity;
import com.cninsure.cp.cargo.CargoCaseListActivity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.UserInfo;
import com.cninsure.cp.utils.APPDownloadUtils;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.permission_util.FloatingWindowPermissionUtil;
import com.cninsure.cp.view.LoadingDialog;
import com.zcw.togglebutton.ToggleButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;

public class DiapersiveUserCenterActivity extends BaseActivity {

    private TextView ggsNameTv, ggsTypeTv, deptTv, editionTv;
    private List<String> paramsList;
    private LoadingDialog loadDialog;
    public UserInfo userInfo;
    private ToggleButton togMusic, togWifiSet;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mycenter);
        EventBus.getDefault().register(this);
        initView();
        downLoadUserInfo();
        FloatingWindowPermissionUtil.isAppOps(this);  //悬浮弹框权限检查
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
            findViewById(R.id.my_menu_yjxShenhe).setVisibility(View.VISIBLE);
            findViewById(R.id.my_menu_yjxShenhe).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) { //打开审核案件列表界面
//                    this.startActivity(new Intent(this, YjxNoShenheOrderActivity.class));
                }
            });
        }else {
            findViewById(R.id.my_menu_yjxShenhe).setVisibility(View.GONE);
        }
    }

    private void initView() {
        togMusic = (ToggleButton) findViewById(R.id.editer_setmarker_type);
        togWifiSet = (ToggleButton) findViewById(R.id.editer_setmarker_wifi);
        loadDialog = new LoadingDialog(this);

        ggsNameTv = (TextView) findViewById(R.id.my_menu_username);
        ggsTypeTv = (TextView) findViewById(R.id.my_menu_usertype);
        deptTv = (TextView) findViewById(R.id.my_menu_usDept);
        editionTv = (TextView) findViewById(R.id.my_menu_dqbb);

        findViewById(R.id.my_menu_disperdive).setVisibility(View.GONE);

        editionTv.setText("当前版本：" + getAppVersion(1));
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

        togMusic.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                SharedPreferences.Editor editor = AppApplication.sp.edit();
                editor.putString("setLoginName", AppApplication.getUSER().data.loginName);
                editor.putBoolean("isPlayMusic", on);
                editor.commit();
                editor.clear();
            }
        });

        togWifiSet.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                SharedPreferences.Editor editor = AppApplication.sp.edit();
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
        if (typecode == HttpRequestTool.CLEAN_CID && CheckHttpResult.checkList(value, this)!=HttpRequestTool.CLEAN_CID ) {
            ToastUtil.showToastLong(this, "退出用户失败!");
            (DiapersiveUserCenterActivity.this).loadDialog.dismiss();
        }
        switch (CheckHttpResult.checkList(value, this)) {
            case HttpRequestTool.GET_USER_INFO:
                userInfo = JSON.parseObject(value.get(0).getValue(), UserInfo.class);
                showUserInfo();
                break;
            case HttpRequestTool.GET_VERSION_INFO://版本信息
                handleVersion(value.get(0).getValue());
                break;
            case HttpRequestTool.CLEAN_CID:
                (DiapersiveUserCenterActivity.this).excetUser();//indexActivity中请求清空服务器端CID成功后在这里调用indexActivity中方法退出用户
                break;

            default:
                break;
        }

    }

    /** 退出当前用户 **/
    public void excetUser() {
        loadDialog.dismiss();
        SharedPreferences.Editor editor = AppApplication.sp.edit();
        editor.putString("tenantPinyinInitials", "");
        editor.putString("loginName", "");
        editor.putString("password", "");
        editor.commit();
        editor.clear();
        AppApplication.emptyUSER();
        startActivity(new Intent(this, LoginActivity.class));
        DispersiveUserActivity.instence.finish();
        try {
            DispersiveWorkActivity.stopActivity();
        }catch (Exception e){
            e.printStackTrace();
        }
        this.finish();
    }

    private void handleVersion(String value) {
        try {
            final JSONObject object = new JSONObject(value).getJSONObject("data");
            int versioncose=Integer.valueOf(object.optString("versionCode"));
            int sysVersonCode=Integer.valueOf((getAppVersion(2)));
            final String DownloadUrl=object.getString("clientUrl");
            if (versioncose <= sysVersonCode) {
                DialogUtil.getAlertOneButton(this, "现在已是最新版本，无需更新！", null).show();
            } else {
                Dialog dialog=DialogUtil.getAlertOneButton(this, "有新的版本可以更新！\n最新版本号：" + object.optString("versionName") + "\n更新信息：" + object.optString("message"),null);
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface. OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface arg0) {
//						HttpUtils.openUrl(IndexActivity.instance,URLs.APP_DOWNLOAD_URL);
//						this.finish();
                        new APPDownloadUtils(DiapersiveUserCenterActivity.this).downloadAPK(DownloadUrl, "CBSPlus");
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
                ggsTypeTv.setText("用户类型："+userInfo.data.allRoleNames+","+rolesName);//用户类型：
            }else {
                ggsTypeTv.setText("暂无用户角色信息！");
            }
            deptTv.setText("归属机构：" + userInfo.data.organizationSelfName);
        }
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

            case R.id.my_menu_account:// 银行卡
                Intent intent = new Intent(this, AccountActivity.class);
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
                break;case R.id.my_menu_cargo:     //货运险-全流程
                startActivity(new Intent(this, CargoCaseListActivity.class));
                break;

            default:
                break;
        }
    }

    private void excetAlert() {
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
        oks.setTitle("千县万店");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(URLs.APP_DOWNLOAD_URL);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("下载泛华公估掌上作业平台-千县万店");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(URLs.APP_DOWNLOAD_URL);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("小伙伴们快来下载千县万店APP和我一起轻松作业吧！");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(URLs.APP_DOWNLOAD_URL);

        // 启动分享GUI
        oks.show(this);
    }

}
