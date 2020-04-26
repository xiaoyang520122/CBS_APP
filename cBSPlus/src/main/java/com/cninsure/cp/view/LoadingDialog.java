package com.cninsure.cp.view;

import com.cninsure.cp.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

/**
 * 自定义加载进度对话框
 * Created by Dylan on 2016-10-28.
 */

public class LoadingDialog extends Dialog {
    private TextView tv_text;

    public LoadingDialog(Context context) {
        super(context);
        try{  
            int dividerID=context.getResources().getIdentifier("android:id/titleDivider", null, null);  
            View divider=findViewById(dividerID);  
            divider.setBackgroundColor(Color.TRANSPARENT);  
        }catch(Exception e){  
            //上面的代码，是用来去除Holo主题的蓝色线条
            e.printStackTrace();  
        }  
        
        /**设置对话框背景透明*/
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.loading2);
        tv_text = (TextView) findViewById(R.id.tv_text);
        setCanceledOnTouchOutside(false);
    }
    

    /**
     * 为加载进度个对话框设置不同的提示消息
     *
     * @param message 给用户展示的提示信息
     * @return build模式设计，可以链式调用
     */
    public LoadingDialog setMessage(String message) {
        tv_text.setText(message);
        return this;
    }
    
}