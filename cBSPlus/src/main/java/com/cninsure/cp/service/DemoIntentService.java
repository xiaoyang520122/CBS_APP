package com.cninsure.cp.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cninsure.cp.entity.PushType;
import com.cninsure.cp.utils.HttpRequestTool;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 继承 GTIntentService 接收来自个推的消息，所有消息在线程中回调，如果注册了该服务，则务必要在 AndroidManifest 中声明，否则无法接受消息
 */
public class DemoIntentService extends GTIntentService {

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    // 处理透传消息
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result1 = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result1 ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            Log.d(TAG, "receiver payload = " + data);

//            sendMessage(data, DemoApplication.DemoHandler.RECEIVE_MESSAGE_DATA);
        }

        Log.d(TAG, "----------------------------------------------------------------------------------------------");

        String result="透传信息 = " +" appid="+appid+" taskid="+taskid+" messageid="+messageid+" payload="
                +new String(payload)+" pkg="+pkg+" cid="+cid;
        Log.e(TAG, "onReceiveClientId -> " + result);
        clickMusic(new String(payload));
//        EventBus.getDefault().post(new BasicNameValuePair(""+HttpRequestTool.RECIVE_CID, clientid));
    }

    // 接收 cid
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        EventBus.getDefault().post(new BasicNameValuePair(""+ HttpRequestTool.RECIVE_CID, clientid));
    }

    // cid 离线上线通知
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    // 各种事件处理回执
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.e(TAG, "onReceiveClientId -> " + "各种事件处理回执 = " + cmdMessage.getAction());
    }

    // 通知到达，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage msg) {
        Log.e(TAG, "onReceiveClientId -> " + "通知到达，只有个推通道下发的通知会回调此方法 = " + msg.getTitle()+msg.getContent());
    }

    // 通知点击，只有个推通道下发的通知会回调此方法
    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage msg) {
        Log.e(TAG, "onReceiveClientId -> " + "通知点击，只有个推通道下发的通知会回调此方法 = " + msg.getTitle()+msg.getContent());
    }

    private void clickMusic(String isonStr){
        try {
            JSONObject jObject=new JSONObject(isonStr);
            String type=jObject.getString("type");
            if (type.equals(PushType.NEW_ORDER)) {
                Intent intent=new Intent(getApplicationContext(), Music.class);//鸣响音乐
                startService(intent);//鸣响音乐
                EventBus.getDefault().post("NEW_ORDER");
            }else if(type.equals(PushType.CANCEL_ORDER) || type.equals(PushType.AUDIT_PASS)
                    || type.equals(PushType.AUDIT_REJECT) || type.equals(PushType.GGS_RECEIVE)
                    || type.equals(PushType.GGS_CANCEL) || type.equals(PushType.GGS_WORK_SUBMIT)
                    || type.equals(PushType.GGS_WORK_AUDIT)) { //订单状态改变后APP端刷新界面
                EventBus.getDefault().post("NEW_ORDER");//发送订阅刷新界面
            }else if (type.equals(PushType.FSX_NEW_ORDER)) {
                Intent intent=new Intent(getApplicationContext(), Music.class);//鸣响音乐
                startService(intent);//鸣响音乐
                EventBus.getDefault().post(new BasicNameValuePair(type, jObject.getString("data")));//分散型新订单
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
