package com.cninsure.cp.cx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.cx.CxDsWorkEntity;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.ActivityFinishUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.cx.TypePickeUtil;
import com.cninsure.cp.view.MyWebView;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DsUtilActivity extends BaseActivity implements View.OnClickListener {
    public WebView webview;
    private String tempstr;
    public CxDsWorkEntity contentJson;
    private LinearLayout cxDsUtillinear;
    private CxDsWorkActivity workActivity;
    private boolean isSend = false; //是否传数据到后台。


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        workActivity = CxDsWorkActivity.getContext();
        /**WebView使用中的那些坑之软键盘遮挡输入框，下面的方法刚获得焦点的时候，还是会被覆盖。但是软键盘一输入，会上升和滚动*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        setContentView(R.layout.cx_ds_util_activity);
        cxDsUtillinear= findViewById(R.id.cxDsUtil_linear);
        initData();
    }

    private void initData() {
        contentJson= (CxDsWorkEntity) getIntent().getSerializableExtra("contentJson");
//        tempstr = "javascript:getMobileWeb(\'{\"contentJson\":" + JSON.toJSONString(contentJson) + "}\')";
//        tempstr = "javascript:callJsFunction(\\'"+JSON.toJSONString(contentJson)+"\\')";//"javascript:getMobileWeb(\'{\"contentJson\":" + JSON.toJSONString(contentJson) + "}\')";
        tempstr = "javascript:callJsFunction(\\'"+JSON.toJSONString(contentJson)+"\\')";//"javascript:getMobileWeb(\'{\"contentJson\":" + JSON.toJSONString(contentJson) + "}\')";
        initWebView();
        initView();
    }

    private void initView() {
        //保存或提交单击事件
        findViewById(R.id.CX_Act_Back_Tv).setOnClickListener(this);
        findViewById(R.id.CX_Act_More_Tv).setOnClickListener(this);
        ((TextView)findViewById(R.id.CX_Act_Title_Tv)).setText("智能定损");
        ((TextView)findViewById(R.id.CX_Act_More_Tv)).setText("保存/提交");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.CX_Act_More_Tv: //showSaveDialog(); break; //点击保存或暂存键
            case R.id.CX_Act_Back_Tv: ActivityFinishUtil.showFinishAlert(this); break; //退出
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void initWebView() {
        // 获取webView 控件
//        webview = new MyWebView(this);
        webview = findViewById(R.id.cxDsUtil_MyWebView);
//		webview = new WebView(this);
//        cxDsUtillinear.addView(webview);
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


// 设置可以支持缩放
        webview.getSettings().setSupportZoom(true);
// 设置出现缩放工具
        webview.getSettings().setBuiltInZoomControls(true);
//扩大比例的缩放
        webview.getSettings().setUseWideViewPort(true);
//自适应屏幕
        webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webview.getSettings().setLoadWithOverviewMode(true);



        webview.addJavascriptInterface(new JsInteration(), "control");
        webview.setWebChromeClient(new WebChromeClient() {
        });
        webview.setWebViewClient(new WebViewClientDemo());

//        String loadUrlStr = "http://10.80.60.14:8080/";
        String loadUrlStr = "http://sysweb.cnsurvey.cn:8084/parth5?time="+new Date().getTime();
        webview.loadUrl(loadUrlStr);
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
            //安卓调用js方法。注意需要在 onPageFinished 回调里调用
            webview.post(new Runnable() {
                @Override
                public void run() {
//                    webview.loadUrl(tempstr);
                    if (!isSend){
                        String sendStr = JSON.toJSONString(contentJson);
                        webview.loadUrl("javascript:callJsFunction('" + sendStr + "')");
                    }
                }
            });
        }
    }


//    Timer temer =;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==HttpRequestTool.SAVE_WORK_INFO) {
//                showLoadingDialog();
            }else if (msg.what==HttpRequestTool.SET_VISIVILITY) {
//                setVisibilityMain((String)msg.obj);
            }else if (msg.what==HttpRequestTool.WEB_BACK_FLASH) {
//                getDefaulMessage(1);
            }
        }

    };

    /**
     * JS调用java保存或提交审核
     **/
    public class JsInteration {  //
        @JavascriptInterface
        public void requestShowData(String head, String message) {
            try {
                if (!TextUtils.isEmpty(message) && message.equals("loaded")){
                    isSend = true;  //后台已获取到Android传递数据
                }else if (!TextUtils.isEmpty(message)) {
                    workActivity.taskEntity.data.contentJson = JSON.parseObject(message, CxDsWorkEntity.class);
//                    workActivity.displayWorkInfo(); //刷新信息
                    workActivity.handler.sendEmptyMessage(0); //刷新信息
                    DsUtilActivity.this.finish();  //message为空或null就关闭activity
                }else if (TextUtils.isEmpty(message)){
                    DsUtilActivity.this.finish();  //message为空或null就关闭activity
                }
            } catch (Exception e) {
                DsUtilActivity.this.finish();  //message为空或null就关闭activity
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void setVisibility(String head) {
            Message msg = new Message();
            msg.obj = head;
            msg.what = HttpRequestTool.SET_VISIVILITY;
            handler.sendMessage(msg);
        }
    }
}
