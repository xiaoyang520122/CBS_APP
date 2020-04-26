package com.cninsure.cp.activity.yjx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
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

public class DispatchListAdapter extends BaseAdapter {
	
	/**接报案对应的调度列表*/
	private List<YjxCaseDispatchTable> dispatchList;
	private LayoutInflater inflater;
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private YjxDispatchActivity context;
	
	@SuppressWarnings("unused")
	private DispatchListAdapter(){}
	
	public DispatchListAdapter (YjxDispatchActivity context,List<YjxCaseDispatchTable> dispatchList){
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
			setStatusTvBacground((TextView)conView.findViewById(R.id.YJXDSPListI_status),caseDisTemp.status);
			switch (caseDisTemp.status) {
			case 0://调度暂存，可以继续编辑
//				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setText("删除"); //黄色
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setText("编辑"); //蓝色
//				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setTextColor(context.getResources().getColor(R.color.yellow_logo)); //黄色
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setTextColor(context.getResources().getColor(R.color.bulue_main)); //蓝色
//				setCancelOnclick(conView.findViewById(R.id.YJXDSPListI_button1),caseDisTemp);
				setGaipaiOnclick(conView.findViewById(R.id.YJXDSPListI_button2),caseDisTemp);
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setTextColor(context.getResources().getColor(R.color.hui_line)); //其他状态不能点击取消
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setTextColor(context.getResources().getColor(R.color.hui_line)); //其他状态不能改派
				break;
			case 1://调度发起（待处理） 可以改派
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setText("改派"); //蓝色
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setTextColor(context.getResources().getColor(R.color.yellow_logo)); //黄色
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setTextColor(context.getResources().getColor(R.color.bulue_main)); //蓝色
				setCancelOnclick(conView.findViewById(R.id.YJXDSPListI_button1),caseDisTemp);
				setGaipaiOnclick(conView.findViewById(R.id.YJXDSPListI_button2),caseDisTemp);
				break;

			case 99://调度拒绝，可以改派
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setTextColor(context.getResources().getColor(R.color.hui_line)); //拒绝的不能取消
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setText("改派"); //蓝色
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setTextColor(context.getResources().getColor(R.color.yellow_logo)); //黄色
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setTextColor(context.getResources().getColor(R.color.bulue_main)); //蓝色
//				setCancelOnclick(conView.findViewById(R.id.YJXDSPListI_button1),caseDisTemp); //拒绝的不能取消
				setGaipaiOnclick(conView.findViewById(R.id.YJXDSPListI_button2),caseDisTemp);
				break;

			default: //非"待接收"状态的任务不能取消，提示用户。
				((TextView)conView.findViewById(R.id.YJXDSPListI_button1)).setTextColor(context.getResources().getColor(R.color.hui_line)); //其他状态不能点击取消
				((TextView)conView.findViewById(R.id.YJXDSPListI_button2)).setTextColor(context.getResources().getColor(R.color.hui_line)); //其他状态不能改派
//				conView.findViewById(R.id.YJXDSPListI_button1).setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
////						DialogUtil.getErrDialog(context, "该任务状态为"+YjxDispatchStatus.getStatuString(caseDisTemp.status)+"不能取消！").show();
//					}
//				});
				break;
			}
		}else {
			((TextView)conView.findViewById(R.id.YJXDSPListI_status)).setText("状态未知");
		}
	}
	
	/**为不同调度状态设置不同颜色的背景**/
	private void setStatusTvBacground(TextView sTv, Integer status) {
		switch (status) {
		case 0:
			sTv.setBackgroundResource(R.drawable.corners_yellow_4_30dp);
			break;
//			return "调度暂存";
		case 1:
			sTv.setBackgroundResource(R.drawable.corners_bule_4_30dp);
			break;
//			return "待处理";
		case 2:
			sTv.setBackgroundResource(R.drawable.corners_readlightl_4_30dp);
			break;
//			return "作业中";
		case 3:
			sTv.setBackgroundResource(R.drawable.corners_bule_4_30dp);
			break;
//			return "等待审核";
		case 4:
			sTv.setBackgroundResource(R.drawable.corners_green_4_30dp);
			break;
//			return "审核完成";
		case 5:
			sTv.setBackgroundResource(R.drawable.corners_greenblue_4_30dp);
			break;
//			return "一审完成";
		case 88:
			sTv.setBackgroundResource(R.drawable.corners_read_4_30dp);
			break;
//			return "审核驳回";
		case 99:
			sTv.setBackgroundResource(R.drawable.corners_read_4_30dp);
			break;
//			return "退回";

		case 999:
			sTv.setBackgroundResource(R.drawable.corners_yellow_4_30dp);
			break;
//			return "改派";

		default:
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
	
	/**调用取消任务接口*/
	private void cancelOrder(final YjxCaseDispatchTable caseDisTemp){
		DialogUtil.getAlertOnelistener(context, "确定取消吗？", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				LoadDialogUtil.setMessageAndShow(context, "努力处理中……");
				List<String> paramsList = new ArrayList<String>();
				//取消订单
				paramsList = new ArrayList<String>(2);
				paramsList.add("id");
				paramsList.add(caseDisTemp.id+"");
				paramsList.add("userId");
				paramsList.add(AppApplication.USER.data.id+"");
				HttpUtils.requestGet(URLs.YJX_BAOAN_CASE_DISPATCH_DELETE, paramsList, HttpRequestTool.YJX_BAOAN_CASE_DISPATCH_DELETE);
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
				context.showDispatchWindow(0,caseDisTemp);//改派调度
			}
		});
	}

}
