package com.cninsure.cp.utils;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 使用场景：禁用布局里所有的子控件，让界面只能看，但不能编辑，如果一个一个控件去设置肯定是很麻烦的，于是想了个好方法。可根据需要扩展更多控件，只要注意控件是ViewGroup类型还是View类型就行了。
 * @author Administrator
 *
 */
public class ViewDisableUtil {
	
	private static String TAG = "ViewDisableUtil_TAG";

	/**
	 * 使用场景：禁用布局里所有的子控件，让界面只能看，但不能编辑，如果一个一个控件去设置肯定是很麻烦的，于是想了个好方法。可根据需要扩展更多控件，只要注意控件是ViewGroup类型还是View类型就行了。
     * 遍历布局，并禁用所有子控件
     * @param viewGroup  布局对象
     */ 
    public static void disableSubControls(ViewGroup viewGroup) { 
        for (int i = 0; i < viewGroup.getChildCount(); i++) { 
            View v = viewGroup.getChildAt(i); 
            if (v instanceof ViewGroup) { 
                if (v instanceof Spinner) { 
                    Spinner spinner = (Spinner) v; 
                    spinner.setClickable(false); 
                    spinner.setEnabled(false); 
 
                    Log.i(TAG, "A Spinner is unabled"); 
                } else if (v instanceof ListView) { 
                    ((ListView) v).setClickable(false); 
                    ((ListView) v).setEnabled(false); 
 
                    Log.i(TAG, "A ListView is unabled"); 
                } else { 
                    disableSubControls((ViewGroup) v); 
                } 
            } else if (v instanceof EditText) { 
                ((EditText) v).setEnabled(false); 
                ((EditText) v).setClickable(false); 
 
                Log.i(TAG, "A EditText is unabled"); 
            } else if (v instanceof Button) { 
                ((Button) v).setEnabled(false); 
 
                Log.i(TAG, "A Button is unabled"); 
            } else if (v instanceof TextView) { 
                ((TextView) v).setEnabled(false); 
                ((TextView) v).setClickable(false); 
 
                Log.i(TAG, "A TextView is unabled"); 
            }
        } 
    }
}
