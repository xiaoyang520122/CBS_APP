package com.cninsure.cp.activty;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.utils.ActivityManagerUtil;

public class MessageCenterActivity extends BaseActivity implements OnClickListener {
	
	private TextView MyMsgTv,SysMsgTv;
	private ListView msgListView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		ActivityManagerUtil.getInstance().addToList(this);
		setContentView(R.layout.activity_message_center);
		initView();
	}

	private void initView() {
		findViewById(R.id.MSGCENTER_Back).setOnClickListener(this);
		findViewById(R.id.MSGCENTER_More).setOnClickListener(this);
		MyMsgTv  =(TextView) findViewById(R.id.MSGCENTER_MyMsg);
		SysMsgTv =(TextView) findViewById(R.id.MSGCENTER_SysMsg);
		msgListView=(ListView) findViewById(R.id.MSGCENTER_listView);
		setlistView();
		MyMsgTv.setOnClickListener(this);
		SysMsgTv.setOnClickListener(this);
	}
	
	private void setlistView(){
		msgListView.setEmptyView(findViewById(R.id.MSGCENTER_empty));
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{});
		msgListView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.MSGCENTER_Back:
			this.finish();
			break;
		case R.id.MSGCENTER_More:
			this.finish();
			break;
		case R.id.MSGCENTER_MyMsg:
			recoverBg(MyMsgTv,R.drawable.corners_bule_left_30dp);
			break;
		case R.id.MSGCENTER_SysMsg:
			recoverBg(SysMsgTv,R.drawable.corners_bule_right_30dp);
			break;

		default:
			break;
		}
	}

	private void recoverBg(TextView tv,int reid) {
		MyMsgTv.setBackgroundResource(R.drawable.corners_white_left_30dp);
		SysMsgTv.setBackgroundResource(R.drawable.corners_white_right_30dp);
		
		MyMsgTv.setTextColor(getResources().getColor(R.color.hui_text_h));
		SysMsgTv.setTextColor(getResources().getColor(R.color.hui_text_h));
		setTvBg(tv,reid);
	}

	private void setTvBg(TextView tv,int reid){
		tv.setBackgroundResource(reid);
		tv.setTextColor(getResources().getColor(android.R.color.white));
	}
	
}
