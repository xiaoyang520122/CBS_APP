package com.cninsure.cp.view;

import android.content.Context;  
import android.graphics.Canvas;  
import android.util.AttributeSet;  
import android.widget.TextView;  
  
/** 
 * 旋转90度的文本
 * @author Tyler 
 * @time 2015-11-25 下午1:45:07 
 */  
public class RotateTextView extends TextView{  
  
      
    public RotateTextView(Context context) {  
        super(context);  
    }  
      
    public RotateTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  
        //倾斜度90,上下左右居中  
        canvas.rotate(90, getMeasuredWidth()/2, getMeasuredHeight()/2);  
        super.onDraw(canvas);  
    }  
      
}  