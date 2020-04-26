package com.cninsure.cp.photo;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.karics.library.zxing.view.PhotoView;

public class DisplayPhotoActivity extends BaseActivity {

	private PhotoView img2;
	private Uri uri;
	private String largeUrlList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.display_photo_activity);
		largeUrlList=getIntent().getStringExtra("largeUrlList");
		getUri();
		getImg2();
	}
	
	private void getUri(){
		String temp=getIntent().getStringExtra("uri");
		if (!TextUtils.isEmpty(temp)) {
			uri= Uri.parse(temp);
		}
	}
	
	private void getImg2(){
		img2=(PhotoView) findViewById(R.id.displayPhotoActivity_img0);
		img2.setImageResource(R.drawable.waiting_photo100);
		img2.enable();
		img2.enableRotate();
		displayImg();
		img2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DisplayPhotoActivity.this.finish();
			}
		});
	}

	private void displayImg() { 
		img2.enable();
		img2.setScaleType(ImageView.ScaleType.FIT_CENTER);
		if (!TextUtils.isEmpty(largeUrlList)) {
			Glide.with(DisplayPhotoActivity.this).load(largeUrlList).error(R.drawable.warn_photo100).into(img2);
		}else if (uri!=null){
			Glide.with(DisplayPhotoActivity.this).load(uri).error(R.drawable.warn_photo100) .into(img2);
		}
	}
}
