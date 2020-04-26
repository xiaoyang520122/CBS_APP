package com.cninsure.cp.service;

import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cninsure.cp.entity.PushType;
import com.cninsure.cp.utils.HttpRequestTool;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
/**
 * 关于原有广播方式和新的IntentService方式兼容性说明：
1. 如果调用了registerPushIntentService方法注册自定义IntentService，则SDK仅通过IntentService回调推送服务事件；
2. 如果未调用registerPushIntentService方法进行注册，则原有的广播接收器仍然可以继续使用。
 * @author Administrator
 *
 */
/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */

public class CbsIntentService extends GTIntentService {

    public CbsIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
    	String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();
        String result="透传信息 = " +" appid="+appid+" taskid="+taskid+" messageid="+messageid+" payload="
    			+new String(payload)+" pkg="+pkg+" cid="+cid;
    	Log.e(TAG, "onReceiveClientId -> " + result);
    	clickMusic(new String(payload));
//        EventBus.getDefault().post(new BasicNameValuePair(""+HttpRequestTool.RECIVE_CID, clientid));
    }
    
    private void clickMusic(String isonStr){
    	try {
			JSONObject jObject=new JSONObject(isonStr);
			String type=jObject.getString("type");
			if (type.equals(PushType.NEW_ORDER)) {
				Intent intent=new Intent(getApplicationContext(), Music.class);
				startService(intent);
				EventBus.getDefault().post("NEW_ORDER");
			}else if(type.equals(PushType.CANCEL_ORDER) || type.equals(PushType.AUDIT_PASS)
					|| type.equals(PushType.AUDIT_REJECT) || type.equals(PushType.GGS_RECEIVE)
					|| type.equals(PushType.GGS_CANCEL) || type.equals(PushType.GGS_WORK_SUBMIT)
					|| type.equals(PushType.GGS_WORK_AUDIT)) { //订单状态改变后APP端刷新界面
				EventBus.getDefault().post("NEW_ORDER");//发送订阅刷新界面
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        EventBus.getDefault().post(new BasicNameValuePair(""+HttpRequestTool.RECIVE_CID, clientid));
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }
}
