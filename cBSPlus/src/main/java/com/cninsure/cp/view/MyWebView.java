package com.cninsure.cp.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.cninsure.cp.interfaces.MyWebInterface;

public class MyWebView extends WebView{  
	
	 private MyWebInterface callback;
	 
    public interface PlayFinish{    
        void After(); 
    }  
    
    PlayFinish df;    
    public void setDf(PlayFinish playFinish) {    
        this.df = playFinish;    
    }  
    
    public MyWebView(Activity context, AttributeSet attrs) {    
        super(context, attrs);    
    } 
    
    public MyWebView(Activity context) {    
        super(context);    
    }

    //onDraw表示显示完毕
    @Override    
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);    
        df.After();    
    }
    
}  
