package com.cninsure.cp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 实现跑马灯的自定义TextView
 * 应用方法：（注意org.angmarch.views修改成当前工程的中对应的包名）
 * <org.angmarch.views.MarqueeTextView  
        android:layout_width="fill_parent"  
        android:layout_height="wrap_content"  
        android:ellipsize="marquee"  
        android:marqueeRepeatLimit="marquee_forever"  
        android:singleLine="true"  
        android:text="@string/marquee_text1" />
 * @author lenovo
 *
 */
public class MarqueeTextView extends TextView {  
	  
    public MarqueeTextView(Context context) {  
        super(context);  
    }  
      
    public MarqueeTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public MarqueeTextView(Context context, AttributeSet attrs,  
            int defStyle) {  
        super(context, attrs, defStyle);  
    }  
      
    @Override  
    public boolean isFocused() {  
        return true;  
    }  
  
}
