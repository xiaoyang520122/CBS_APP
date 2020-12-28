package com.cninsure.cp.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @author :xy-wm
 * date:2020/12/28 10:09
 * usefuLness: CBS_APP
 */

/**
 * Created by csonezp on 15-11-11.
 * 可以直接控制所有子控件是否可点击的LinearLayout
 * 然后就像正常LinearLayout一样使用这个控件就可以了。在需要的时候调用一下setChildClickable，参数为true则所有子控件可以点击，false则不可点击。
 */
public class ChildClickableLinearLayout extends LinearLayout {
    //子控件是否可以接受点击事件
    private boolean childClickable = true;

    public ChildClickableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildClickableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChildClickableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ChildClickableLinearLayout(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //返回true则拦截子控件所有点击事件，如果childclickable为true，则需返回false
        return !childClickable;
    }

    /**然后就像正常LinearLayout一样使用这个控件就可以了。在需要的时候调用一下setChildClickable，参数为true则所有子控件可以点击，false则不可点击。*/
    public void setChildClickable(boolean clickable) {
        childClickable = clickable;
    }

}