package com.cninsure.cp.activity.yjx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.PublicOrderEntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanEntity;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanStatus;
import com.cninsure.cp.utils.CopyUtils;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.ToastUtil;

public class YjxCaseBaoanAdapter extends BaseAdapter {
	
	public List<YjxCaseBaoanEntity> listDate;
	private LayoutInflater inflater;
	private SimpleDateFormat sf;
	private YjxTempStorageActivity context;
	
	@SuppressWarnings("unused")
	private YjxCaseBaoanAdapter(){}
	public YjxCaseBaoanAdapter(YjxTempStorageActivity context,List<YjxCaseBaoanEntity> listDate){
		inflater = LayoutInflater.from(context);
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.context = context;
		if (listDate!=null) {
			this.listDate = listDate;
		}else {
			this.listDate = new ArrayList<YjxCaseBaoanEntity>();
		}
	}

	@Override
	public int getCount() {
		return listDate.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listDate.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View conView, ViewGroup arg2) {
		conView = inflater.inflate(R.layout.yjx_jba_list_item, null);
		YjxCaseBaoanEntity tempDate = listDate.get(arg0);
		((TextView)conView.findViewById(R.id.YJXDSPList_createDate)).setText("创建时间："+sf.format(tempDate.createDate));
		//产品细类
		if (tempDate.product!=null && tempDate.product.length()>2) {
			String productSmall = tempDate.product.substring(tempDate.product.length()-2, tempDate.product.length()-1);
			((TextView)conView.findViewById(R.id.YJXDSPList_product)).setText(productSmall);
		}
		((TextView)conView.findViewById(R.id.YJXDSPList_insuranceBigType)).setText(tempDate.insuranceBigType+" - "+tempDate.insuranceSmallType);//险种类型+险种细类
		((TextView)conView.findViewById(R.id.YJXDSPList_bussType)).setText(tempDate.bussType); //业务品种
		setStatus(tempDate.status, ((TextView)conView.findViewById(R.id.YJXDSPList_status)));//接报案状态
		((TextView)conView.findViewById(R.id.YJXDSPList_uid)).setText(tempDate.uid);
		if (!TextUtils.isEmpty(tempDate.wtName) && !"null".equals(tempDate.wtName)) {
			((TextView)conView.findViewById(R.id.YJXDSPList_wtName)).setText(tempDate.wtName); //委托人名称
		}
		if (!TextUtils.isEmpty(tempDate.caseBaoanNo) && !"null".equals(tempDate.caseBaoanNo)) { 
			((TextView)conView.findViewById(R.id.YJXDSPList_caseBaoanNo)).setText(tempDate.caseBaoanNo); //报案号
			CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.YJXDSPList_copy_baoan_no) , tempDate.caseBaoanNo);
		}
		setButtonOnclick(arg0, (TextView)conView.findViewById(R.id.YJXDSPList_button1), (TextView)conView.findViewById(R.id.YJXDSPList_button2));
		CopyUtils.setCopyOnclickListener(context, conView.findViewById(R.id.YJXDSPList_copy_jiebaoan_no), tempDate.uid);
		return conView;
	}
	
	/**设置接报案状态显示*/
	private void setStatus(int status,TextView Tv){
		Tv.setText(YjxCaseBaoanStatus.getStausString(status));
		switch (status) {
		case 0: //暂存
			Drawable dra = context.getResources().getDrawable(R.drawable.tempstorage_hui48);
			Tv.setCompoundDrawablesWithIntrinsicBounds(null, dra , null, null);
			break;
		case 1:
			Tv.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.wait_dispatch_hui48), null, null);//"待调度";
			break;
		case 2:
			Tv.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.dispatch_hui48), null, null);// "作业";
			break;
		case 3:
			Tv.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.working_hui48), null, null);// "作业完成";
			break;
		case 4:
			Tv.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.dispatch_end_hui48), null, null);// "结案";
			break;

		default:
			Tv.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(R.drawable.wt_info_blue35), null, null);// "状态未知";
		}
	}
	
	/**根据接报案状态设置Item下方两个按钮的单击事件*/
	private void setButtonOnclick(int point,TextView button1,TextView button2){
		YjxCaseBaoanEntity tempDate = listDate.get(point);
		int status;
		if (tempDate.status==null) {
			status = -1;
		}else {
			status = tempDate.status;
		}
		
		switch (status) {
		case 0: //暂存状态
			setTempOclick(point,button1,button2);
			break;
		case 1: //待调度状态
			setNoDispatchOclick(point,button1,button2);
			break;
		case 2: //已调度状态
			setDispatchOclick(point,button1,button2);
			break;
		case 3: //已调度有任务作业完成状态
			setDispatchOclick(point,button1,button2);
			break;
		case 4: //已结案
			setFinishOclick(point,button1,button2);
			break;

		default:
			setFinishOclick(point,button1,button2);
			break;
		}
	}
	
	/**结案及其他位置状态操作*/
	private void setFinishOclick(final int point, TextView button1, TextView button2) {
		button1.setText("详情");
		button2.setText("调度列表");
		button1.setOnClickListener(new OnClickListener() { //退回接报案
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, YjxBaoanInputActivity.class);
				intent.putExtra("uid", listDate.get(point).uid);
				intent.putExtra("requestType", "seeBaoanInfo");
				context.startActivity(intent);
			}
		});
		//调度任务
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { //跳转到调度界面，因为已结案接报案不用调度，所以不通过jumpTDispatchActivtity(point);方法跳转
				Intent intent = new Intent(context, YjxDispatchActivity.class);
				intent.putExtra("YjxCaseBaoanEntity", listDate.get(point));
				context.startActivity(intent);
			}
		});
	}
	/**已调度点击事件设置*/
	private void setDispatchOclick(final int point, TextView button1, TextView button2) {
		button1.setText("详情");
		button2.setText("追加调度");
		button1.setOnClickListener(new OnClickListener() { //退回接报案
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, YjxBaoanInputActivity.class);
				intent.putExtra("uid", listDate.get(point).uid);
				intent.putExtra("requestType", "seeBaoanInfo");
				context.startActivity(intent);
			}
		});
		//调度任务
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { //跳转到调度界面
				jumpTDispatchActivtity(point);
//				Intent intent = new Intent(context, YjxDispatchActivity.class);
//				intent.putExtra("YjxCaseBaoanEntity", listDate.get(point));
//				context.startActivity(intent);
			}
		});
	}
	/**待调度的按钮单击事件设置*/
	private void setNoDispatchOclick(final int point, TextView button1, TextView button2) {
		button1.setText("退回接报案");
		button2.setText("调度任务");
		button1.setOnClickListener(new OnClickListener() { //退回接报案
			@Override
			public void onClick(View arg0) {
				DialogUtil.getAlertOnelistener(context, "确定退回该接报案吗？", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (listDate.get(point).id!=null) {
							context.backCaseBaoan(listDate.get(point).id);
						}else {
							ToastUtil.showToastLong(context, "该接报案缺少ID，不能退回，请联系管理员！");
						}
					}
				}).show();
			}
		});
		//调度任务
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { //跳转到调度界面
				jumpTDispatchActivtity(point);
//				Intent intent = new Intent(context, YjxDispatchActivity.class);
//				intent.putExtra("YjxCaseBaoanEntity", listDate.get(point));
//				context.startActivity(intent);
			}
		});
	}
	
	/**跳转到调度界面*/
	private void jumpTDispatchActivtity(int point){
		String roleIds = AppApplication.getUSER().data.roleIds;
		if (roleIds.indexOf(URLs.getDispatchId()+"")>-1) {
			Intent intent = new Intent(context, YjxDispatchActivity.class);
			intent.putExtra("YjxCaseBaoanEntity", listDate.get(point));
			context.startActivity(intent);
		}else {
			DialogUtil.getErrDialog(context, "暂时没有医健险调度权限！").show();
		}
	}
	/**暂存通过的按钮单击事件设置*/
	private void setTempOclick(final int point, TextView button1, TextView button2) {
		button1.setText("删除接报案");
		button2.setText("继续编辑");
		button1.setOnClickListener(new OnClickListener() { //删除接报案
			@Override
			public void onClick(View arg0) {
				DialogUtil.getAlertOnelistener(context, "确定删除该接报案吗？", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if (listDate.get(point).id!=null) {
							context.deleteCaseBaoan(listDate.get(point).id);
						}else {
							ToastUtil.showToastLong(context, "该接报案缺少ID，不能删除，请联系管理员！");
						}
					}
				}).show();
			}
		});
		//编辑接报案
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, YjxBaoanInputActivity.class);
				intent.putExtra("uid", listDate.get(point).uid);
				context.startActivity(intent);
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
