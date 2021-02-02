package com.cninsure.cp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cninsure.cp.entity.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeHelper {

    private Activity  activity;
    private TextView textView;  //显示信息的TextView
    private long baseTime;  //用来计算的日期
    private Timer timer;   //定时器
    private String prefixStr;  //显示时间的前缀
    private SimpleDateFormat SF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private TimeHelper(){}
    public TimeHelper(Activity activity){
        this.activity = activity;
    }

    /**定时器控制刷新频率**/
    public void setUseTime(TextView textView,String prefixStr,long baseTime, final int item){
        this.textView  = textView ;
        this.baseTime  = baseTime ;
        this.prefixStr  = prefixStr ;
        if (activity.isFinishing() || textView==null || baseTime<=0 || prefixStr==null){
            SetTextUtil.setTextViewText(textView,prefixStr + "无信息！");  //数据有误是中断
            return;
        }
            timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message=new Message();
                    if (!activity.isDestroyed()) {
                        message.what=item;
                        handler.sendMessage(message);
                    }
                }
            }, 0, 1000);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setTime(msg.what);
        }
    };

    /**计算并显示用时**/
    @SuppressWarnings("deprecation")
    private void setTime(int item) {
        try {
            Date nowdate = new Date();
            long nowtime = nowdate.getTime();
            long differenceTime = nowtime - baseTime;
            if (differenceTime >= 0) {
                SetTextUtil.setTextViewText(textView,prefixStr + formatDuring(differenceTime));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**毫秒转化为时分秒*/
    public String formatDuring(long mss) {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        String hourstr=hours>=10?(hours+""):("0"+hours);
        String minutessr=minutes>=10?(minutes+""):("0"+minutes);
        String secondsstr=seconds>=10?(seconds+""):("0"+seconds);

        if (days>0) {
            return days+"天"+hourstr + ":" + minutessr + ":" + secondsstr;
        }else {
            return hourstr + ":" + minutessr + ":" + secondsstr;
        }
    }

    public static void setTimePickerDialog(Context context, TextView timeTv){
        timeTv.setOnClickListener(v -> {
            TimePickerDialog tpkd = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    timeTv.setText(hourOfDay+":"+minute+":00");
                }
            },new Date().getHours(),new Date().getMinutes(),true);
            tpkd.show();
        });
    }
}
