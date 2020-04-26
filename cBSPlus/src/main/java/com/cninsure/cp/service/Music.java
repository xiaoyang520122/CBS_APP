package com.cninsure.cp.service;

import java.util.Timer;
import java.util.TimerTask;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

 
public class Music extends Service {
    private MediaPlayer mp;
    private Vibrator vibrator;
    public static int MUSIC_CODE=1;
    public static boolean IS_BELL=true;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    @SuppressWarnings("deprecation")
	@Override  
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        IS_BELL=AppApplication.sp.getBoolean("isPlayMusic", true);
        Log.d("GTIntentService", "开始播放提示音-******************************");
        creatMusic();
        mp.start();
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
        long [] pattern = {100,500,300,500};   // 停止 开启 停止 开启   
        vibrator.vibrate(pattern,-1);           //重复两次上面的pattern 如果只想震动一次，index设为-1   
        
        new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				Music.this.stopSelf();
			}
		}, 1000*20);
    } 
    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.stop();
        Log.d("GTIntentService", "服务停止-******************************");
    }
    
    private void creatMusic(){
    	 if (IS_BELL) {
         	if (MUSIC_CODE==1) {
             	mp=MediaPlayer.create(this,R.raw.new_order_girl);
     		}else if (MUSIC_CODE==2) {
     			mp=MediaPlayer.create(this,R.raw.new_order_girl);
     		}else if (MUSIC_CODE==3) {
     			mp=MediaPlayer.create(this,R.raw.new_order_girl);
     		}
 		}
    }
 
    
//   private  Timer timer;
//   private TimerTask task;
//	private void stopservice() {
//		task = new TimerTask() {
//			@Override
//			public void run() {
//				if (!mp.isPlaying()) {
//					timer.cancel();
//					Log.d("GTIntentService", "停止播放提示音-******************************");
//					task.cancel();
////					stopService(new Intent(getApplicationContext(), Music.class) );
//				}
//			}
//		};
//		timer=new Timer();
//		timer.schedule(task, 1, 500);
//	}
    
}