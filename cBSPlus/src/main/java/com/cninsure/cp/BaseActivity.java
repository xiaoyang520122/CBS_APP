package com.cninsure.cp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cninsure.cp.entity.dispersive.DispersiPayloadEntity;
import com.cninsure.cp.entity.dispersive.DispersiveDispatchEntity;
import com.cninsure.cp.utils.DialogUtil;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        始终保持竖屏setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        如果要横屏，代码是 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//禁止横竖屏
    }
    
  //是否连接WIFI
    public static boolean isWifiConnected()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)AppApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
        return false ;
    }
    
    /** 
	 * 返回当前程序版本名 ;1返回版本名称，其他返回版本号
	 */  
	public String getAppVersion(int code) {  
	    String versionName = ""; 
	    String versioncode="";
	    try {  
	        // ---get the package info---  
	        PackageManager pm = this.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);  
	        versionName = pi.versionName;  
	        versioncode = pi.versionCode+"";
	        if (versionName == null || versionName.length() <= 0) {  
	            return "";  
	        }  
	    } catch (Exception e) {  
	        Log.e("VersionInfo", "Exception", e);  
	    }  
	    if (code==1) {
	    	return versionName;  
		}else {
			return versioncode;  
		}
	} 
	
	@Override
	protected void onResume() {
		super.onResume();
		isNetWork();
	}
	
	static Timer  timer;
	public void isNetWork() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (!isNetworkAvailable()) {
						handler.sendEmptyMessage(0);
					}
				}
			}, 0, 10 * 1000);// 每隔10秒检查一次网络连接情况
		}
	}
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what==0) {
//				ToastUtil.showToastLong(getApplicationContext(), "网络连接失败！");
				DialogUtil.getErrDialog(getApplicationContext(), "网络已断开，请链接网络后继续操作！");
			}
		}
	};
	
	/**检查网络连接的代码*/
	ConnectivityManager cm;
	/**判断网络连接是否可用**/
	public boolean isNetworkAvailable() {   
        cm = (ConnectivityManager)BaseActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (cm == null) {   
        } else { //如果仅仅是用来判断网络连接   //则可以使用 cm.getActiveNetworkInfo().isAvailable();  
            NetworkInfo[] info = cm.getAllNetworkInfo();   
            if (info != null) {   
                for (int i = 0; i < info.length; i++) {   
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
                        return true;   
                    }   
                }   
            }   
        }   
        return false;   
    }

	public static void disPlayAlertMessage(String value) {
		/**显示警报弹出框**/
		Context context=AppApplication.mInstance.getApplicationContext();
		DispersiPayloadEntity dispatchEntity= JSON.parseObject(value,DispersiPayloadEntity.class);
		View alertView = LayoutInflater.from(context).inflate(R.layout.alert_gif_dialog_view, null);
		((TextView)alertView.findViewById(R.id.ALERT_GIF_DIALOG_VIEW_insuranceSmallType)).setText(dispatchEntity.data.insuranceSmallType);//案件类型
		((TextView)alertView.findViewById(R.id.ALERT_GIF_DIALOG_VIEW_commissionFee)).setText(dispatchEntity.data.commissionFee+"");//佣金
		((TextView)alertView.findViewById(R.id.ALERT_GIF_DIALOG_VIEW_exploreAddress)).setText(dispatchEntity.data.exploreAddress);//查勘地点
		((TextView)alertView.findViewById(R.id.ALERT_GIF_DIALOG_VIEW_damagedState)).setText(dispatchEntity.data.damagedState);//受损基本情况
		((TextView)alertView.findViewById(R.id.ALERT_GIF_DIALOG_VIEW_keyPoint)).setText(dispatchEntity.data.keyPoint); //查勘重点要求

		Dialog alert=DialogUtil.getDialogByView(context, alertView);
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//			setCallOnclick(alertView,alertInfoArr[9],alert);
//			displayMap(alertView, alertInfoArr,alert);
		alert.show();
	}
	
}
