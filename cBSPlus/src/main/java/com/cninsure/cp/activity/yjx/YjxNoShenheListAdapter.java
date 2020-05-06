package com.cninsure.cp.activity.yjx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.yjx.YjxCaseDispatchTable;
import com.cninsure.cp.entity.yjx.YjxDispatchStatus;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;

public class YjxNoShenheListAdapter extends BaseAdapter {
	
	/**接报案对应的调度列表*/
	private List<YjxCaseDispatchTable> dispatchList;
	private LayoutInflater inflater;
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private YjxNoShenheOrderActivity context;
	
	@SuppressWarnings("unused")
	private YjxNoShenheListAdapter(){}
	
	public YjxNoShenheListAdapter (YjxNoShenheOrderActivity context,List<YjxCaseDispatchTable> dispatchList){
		this.dispatchList = dispatchList;
		inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		if (dispatchList==null) { //为空或者没有数据显示提示VIEW，以便headView能正常显示。
			return 1;
		}else if (dispatchList.size()==0) {//为空或者没有数据显示提示VIEW，以便headView能正常显示。
			return 1;
		}else {
			return dispatchList.size();
		}
	}

	@Override
	public Object getItem(int arg0) {
		if (dispatchList==null) { 
			return null;
		}else if (dispatchList.size()==0) {
			return null;
		}else {
			return dispatchList.get(arg0);
		}
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View conView, ViewGroup arg2) {
		try {
			if (dispatchList!=null && dispatchList.size()>0) { //没有调度数据是显示提示按钮
				conView = inflater.inflate(R.layout.yjx_dispatch_list_item, null);
				YjxCaseDispatchTable caseDisTemp = dispatchList.get(arg0);
				setStatusView(conView,caseDisTemp);//调度状态
				SetTextUtil.setTextViewText((TextView)conView.findViewById(R.id.YJXDSPListI_ggsName),caseDisTemp.ggsName); //作业公估师
				SetTextUtil.setTextViewText((TextView)conView.findViewById(R.id.YJXDSPListI_workOrg),caseDisTemp.workOrg); //作业机构
				SetTextUtil.setTextViewText((TextView)conView.findViewById(R.id.YJXDSPListI_aging),caseDisTemp.aging+""); //作业时效
				SetTextUtil.setTextViewText((TextView)conView.findViewById(R.id.YJXDSPListI_product),caseDisTemp.product);//产品
				SetTextUtil.setTextViewText((TextView)conView.findViewById(R.id.YJXDSPListI_BussType),caseDisTemp.bussType);//作业类型
				SetTextUtil.setTextViewText((TextView)conView.findViewById(R.id.YJXDSPListI_taskAddress),caseDisTemp.taskAddress); //作业地点
				if (caseDisTemp.workTime!=null) {
					SetTextUtil.setTextViewText((TextView)conView.findViewById(R.id.YJXDSPListI_workTime),sf.format(caseDisTemp.workTime));//预约作业时间
				}
				if (caseDisTemp.createDate!=null) {
					SetTextUtil.setTextViewText((TextView)conView.findViewById(R.id.YJXDSPList_createDate),"调度时间："+sf.format(caseDisTemp.createDate));//调度业时间
				}
				SetTextUtil.setTextViewText((TextView)conView.findViewById(R.id.YJXDSPListI_uid),caseDisTemp.uid); //任务编号
				CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.YJXDSPListI_taskAddress), caseDisTemp.taskAddress);//复制地址
				CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.YJXDSPListI_uid), caseDisTemp.uid);//复制任务编号
				CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.YJXDSPListI_copy_taskAddress), caseDisTemp.taskAddress);//复制地址
				CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.YJXDSPListI_copy_uid), caseDisTemp.uid);//复制任务编号
			}else {
				return inflater.inflate(R.layout.empty_dispatch_view, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return conView;
		}
		return conView;
	}

	/**设置状态*/
	private void setStatusView(View conView, final YjxCaseDispatchTable caseDisTemp) {
		if (caseDisTemp!=null && caseDisTemp.status!=null) {
			((TextView)conView.findViewById(R.id.YJXDSPListI_status)).setText(YjxDispatchStatus.getStatuString(caseDisTemp.status));
			switch (caseDisTemp.status) {
			case 3://调度发起（待处理） 可以取消调度
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setText("退回"); 
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setText("审核"); 
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setTextColor(context.getResources().getColor(R.color.yellow_logo)); //黄色
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setTextColor(context.getResources().getColor(R.color.bulue_main)); //蓝色
				setCancelOnclick(conView.findViewById(R.id.YJXDSPListI_button1),caseDisTemp);
				setGaipaiOnclick(conView.findViewById(R.id.YJXDSPListI_button2),caseDisTemp);
				break;

			default: //非"待接收"状态的任务不能取消，提示用户。
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setText("退回"); //黄色
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setText("审核"); //蓝色
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setTextColor(context.getResources().getColor(R.color.hui_line)); //其他状态不能点击取消
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setTextColor(context.getResources().getColor(R.color.hui_line)); //其他状态不能改派
				break;
			}
		}else {
			((TextView)conView.findViewById(R.id.YJXDSPListI_status)).setText("状态未知");
		}
	}
	
	/**
	 * 取消任务
	 * @param view
	 * @param caseDisTemp
	 */
	private void setCancelOnclick(final View view , final YjxCaseDispatchTable caseDisTemp){//点击取消
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { //点击取消
				cancelOrder(caseDisTemp);
			}
		});
	}
	
	/**调用审核退回任务接口*/
	private void cancelOrder(final YjxCaseDispatchTable caseDisTemp){
		DialogUtil.getAlertOnelistener(context, "确定要审核退回吗？", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
//				LoadDialogUtil.setMessageAndShow(context, "努力处理中……");
//				final List<NameValuePair> params = new ArrayList<NameValuePair>();
//				params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId + ""));
//				params.add(new BasicNameValuePair("orderUid", caseDisTemp.uid));
//				params.add(new BasicNameValuePair("id", caseDisTemp.id+""));
//				params.add(new BasicNameValuePair("surveyDescription", caseDisTemp.surveyDescription));
//				params.add(new BasicNameValuePair("surveyConclusion", ));
//				params.add(new BasicNameValuePair("analysis", ));
//				params.add(new BasicNameValuePair("conclusion", ));
//				params.add(new BasicNameValuePair("auditMessage", ));
//				HttpUtils.requestPost(URLs.YJX_SHENHE_REJECT, params, HttpRequestTool.YJX_SHENHE_REJECT);
			}
		}).show();
	}
	
	/**
	 * 改派任务
	 * @param view
	 * @param caseDisTemp
	 */
	private void setGaipaiOnclick(View view , final YjxCaseDispatchTable caseDisTemp){//点击取消
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { 
				Intent intent = new Intent(context, YjxDispatchShenheActivity.class);
				intent.putExtra("dispatchUid", caseDisTemp.uid);
				intent.putExtra("uid", caseDisTemp.caseBaoanUid);
				intent.putExtra("id", caseDisTemp.id+"");
				context.startActivity(intent);
			}
		});
	}

}
