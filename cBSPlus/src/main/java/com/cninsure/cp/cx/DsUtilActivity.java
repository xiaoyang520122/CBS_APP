package com.cninsure.cp.cx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

import java.util.Timer;
import java.util.TimerTask;

public class DsUtilActivity extends BaseActivity implements View.OnClickListener {
    public MyWebView webview;
    private String tempstr;
    public CxDsWorkEntity contentJson;
    private LinearLayout cxDsUtillinear;
    private CxDsWorkActivity workActivity;


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
        webview = new MyWebView(this);
//		webview = new WebView(this);
        cxDsUtillinear.addView(webview);
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

//        String loadUrlStr = URLs.WORK_SPACE + workUrls;
        String loadUrlStr = "http://10.80.60.14:8080/#/";
        webview.loadUrl(loadUrlStr);
        webview.setDf(new MyWebView.PlayFinish() {
            @Override
            public void After() {
//                loadurlM();
            }
        });
    }

    private boolean isLoadur = false;
    private Timer timer = new Timer();

    private void loadurlM() {
        if (!isLoadur) {
            isLoadur = true;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = 1002;
                    handler2.sendMessage(msg);
                }
            }, 100, 100);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1002) {
                webview.loadUrl(tempstr);
                Log.e("JsonHttpUtils", "handler23********************************==" + tempstr);
            }
        }
    };

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
//            webview.loadUrl(tempstr);
//            if (!flag_get_deviceid) {
//                mLoadUrl();
//            }
            //安卓调用js方法。注意需要在 onPageFinished 回调里调用
            webview.post(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl(tempstr);
                }
            });
        }
    }

    /***测试功能**/
    private boolean flag_get_deviceid=false;
    @SuppressLint("NewApi")
    private void mLoadUrl(){
        String androidID="";
        try{
            androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }catch(Exception e){
        }finally{
            webview.evaluateJavascript(tempstr, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    if(value!=null){
                        flag_get_deviceid=true;
                    }
                }});
        }
    }

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
    public class JsInteration {
        @JavascriptInterface
        public void requestShowData(String head, String message) {
            try {
                if (TextUtils.isEmpty(message)) {
                    workActivity.taskEntity.data.contentJson = JSON.parseObject(message, CxDsWorkEntity.class);
                    workActivity.displayWorkInfo(); //刷新信息
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            DsUtilActivity.this.finish();
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
