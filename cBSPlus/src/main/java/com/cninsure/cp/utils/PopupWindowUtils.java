package com.cninsure.cp.utils;

import com.cninsure.cp.R;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PopupWindowUtils {
	private static PopupWindow popupWindow;
	/**
	 * 
	 * @param contentView popupwindow显示的内容
	 * @param view 点击弹出popupwindow的View
	 * @param context
	 */
	public static PopupWindow showPopupWindow(View contentView,View view,Activity context) {
//		// 一个自定义的布局，作为显示的内容
//		View contentView = LayoutInflater.from(context).inflate(R.layout.main_more_menu, null);
//		TextView feedbackTv =(TextView) contentView.findViewById(R.id.main_more_menu_feedback);
//		TextView helpTv =(TextView) contentView.findViewById(R.id.main_more_menu_help);

		popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("LONGING", "onTouch : ");
				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});
		// 实例化一个ColorDrawable颜色透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(dw);
		// 设置好参数之后再show
		popupWindow.showAsDropDown(view);
		setBasic(context);//只能在下列代码之前调用
		 WindowManager.LayoutParams lp=context.getWindow().getAttributes();
		    lp.alpha=0.8f;
		    context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		    context.getWindow().setAttributes(lp);
		return popupWindow;
	}
	
	/**
	 * 
	 * @param contentView popupwindow显示的内容
	 * @param view 点击弹出popupwindow的View
	 * @param context
	 */
	public static PopupWindow showPopupWindowUp(View contentView,View view,Activity context,Handler handler) {
//		// 一个自定义的布局，作为显示的内容
//		View contentView = LayoutInflater.from(context).inflate(R.layout.main_more_menu, null);
//		TextView feedbackTv =(TextView) contentView.findViewById(R.id.main_more_menu_feedback);
//		TextView helpTv =(TextView) contentView.findViewById(R.id.main_more_menu_help);

//		PopUpwindowLayout popUpwindowLayout = (PopUpwindowLayout) contentView.findViewById(R.id.llayout_popupwindow);
//		popUpwindowLayout.initViews(mContext, titles, false);
//		popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
//		popupWindow = new PopupWindow(contentView, 300,500);
		popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		 //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
		contentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);   
		int popupWidth = contentView.getMeasuredWidth();    //  获取测量后的宽度
		int popupHeight = contentView.getMeasuredHeight();  //获取测量后的高度
		// 实例化一个ColorDrawable颜色透明
				ColorDrawable dw = new ColorDrawable(0000000000);
				// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
				// 我觉得这里是API的一个bug
				popupWindow.setBackgroundDrawable(dw);
		int[] location = new int[2];
		// 允许点击外部消失
//		popupWindow.setBackgroundDrawable(new BitmapDrawable());//注意这里如果不设置，下面的setOutsideTouchable(true);允许点击外部消失会失效
		popupWindow.setOutsideTouchable(true);   //设置外部点击关闭ppw窗口
		popupWindow.setFocusable(true);
		// 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
		view.getLocationOnScreen(location);
		popupWindow.setAnimationStyle(R.style.popwin_anim_style);  //设置动画
		//这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (location[0] + view.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);  
		setBasic(context,handler);//只能在下列代码之前调用
		 WindowManager.LayoutParams lp=context.getWindow().getAttributes();
		    lp.alpha=0.8f;
		    context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		    context.getWindow().setAttributes(lp);
		return popupWindow;
	}
	
	private static void setBasic(final Activity context){
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
	        @Override
	        public void onDismiss() {
	            WindowManager.LayoutParams lp=context.getWindow().getAttributes();
	            lp.alpha=1.0f;
	            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	            context.getWindow().setAttributes(lp);
	        }
	    });
	}
	
	private static void setBasic(final Activity context,final Handler handler){
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
	        @Override
	        public void onDismiss() {
	        	handler.sendEmptyMessage(100010);
	            WindowManager.LayoutParams lp=context.getWindow().getAttributes();
	            lp.alpha=1.0f;
	            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	            context.getWindow().setAttributes(lp);
	        }
	    });
	}
}
