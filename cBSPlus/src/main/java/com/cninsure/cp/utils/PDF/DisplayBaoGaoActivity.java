package com.cninsure.cp.utils.PDF;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;


import org.apache.commons.net.util.Base64;

import java.io.UnsupportedEncodingException;

public class DisplayBaoGaoActivity extends BaseActivity {

	private WebView pdfViewerWeb;// baogao_webView
	private String docPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display_baogao_activtity);
		setTitle("排查报告");
		enableBack();
		initView();
	}
	
	public void enableBack() {
		FrameLayout leftLayout = (FrameLayout) findViewById(R.id.left_btn_layout);
		if (leftLayout != null) {
			leftLayout.setVisibility(View.VISIBLE);
			View backView = LayoutInflater.from(this).inflate(R.layout.view_titlebar_back, null);
			leftLayout.addView(backView);

			leftLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();	///销毁当前Activity**/
				}
			});
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressLint("NewApi")
	private void initView() {
		pdfViewerWeb = (WebView) findViewById(R.id.baogao_webView);
		docPath=getIntent().getStringExtra("baogaoPath");
		WebSettings settings = pdfViewerWeb.getSettings();
		settings.setSavePassword(false);
		settings.setJavaScriptEnabled(true);
		settings.setAllowFileAccessFromFileURLs(true);
		settings.setAllowUniversalAccessFromFileURLs(true);
		settings.setBuiltInZoomControls(true);
		pdfViewerWeb.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		pdfViewerWeb.setWebChromeClient(new WebChromeClient());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// api >= 19
			pdfViewerWeb.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + docPath);
		} else {
			if (!TextUtils.isEmpty(docPath)) {
				byte[] bytes = null;
				try {// 获取以字符编码为utf-8的字符
					bytes = docPath.getBytes("UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (bytes != null) {
//					 docPath = new BASE64Encoder().encode(bytes);// BASE64转码
					 docPath = Base64.encodeBase64URLSafeString(bytes);
				}
			}
			pdfViewerWeb.loadUrl("file:///android_asset/pdfjs_compatibility/web/viewer.html?file=" + docPath);
		}
	}

}
