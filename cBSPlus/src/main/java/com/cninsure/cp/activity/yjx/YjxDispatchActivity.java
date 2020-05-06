package com.cninsure.cp.activity.yjx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.GGSEntity.GGSTableData.GGSData;
import com.cninsure.cp.entity.yjx.EYYBListEntity;
import com.cninsure.cp.entity.yjx.EYYBListEntity.TableData.EYYBDataEntity;
import com.cninsure.cp.entity.yjx.InsuranceTypeUtil;
import com.cninsure.cp.entity.yjx.ProductTypeUtil;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanEntity;
import com.cninsure.cp.entity.yjx.YjxCaseDispatchTable;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.YYBUtils;

public class YjxDispatchActivity extends BaseActivity {
	private TextView actionTV1, actionTV2, actionTV3; // 顶部返回按钮，标题和暂存按钮。
	/**接报案信息*/
	private YjxCaseBaoanEntity caseBaoanEn;
	private LayoutInflater inflater;
	private View headView;
	private ListView listView;
	/**接报案对应的调度列表*/
	private List<YjxCaseDispatchTable> dispatchList;
	/**适配器*/
	private DispatchListAdapter disAdapter;
	/**所有营业部*/
	private EYYBListEntity yybEntityAll;
	private View DispatchDialogView;
	/**选择调度归属营业部位置*/
	private int choiceInt;
	/**归属营业部*/
	private EYYBDataEntity ChoiceOrgYybEn,ChoiceWorkYybEn;
	/**选择公估师的帮助类*/
	private ChoiceGGShelp choiceGGShelp;
	/**修改编辑的调度任务*/
	private YjxCaseDispatchTable editDiapatchEn;
	private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		setContentView(R.layout.yjx_dipatch_activity);
		initaction();
		initView();
		YYBUtils.downLoadAlldept();
	}
	
	private void initaction() {
		actionTV1 = (TextView) findViewById(R.id.YJXD_ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.YJXD_ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.YJXD_ACTION_V_RTV);
		setAction();
	}
	
	private void setAction() {
		actionTV2.setText("医健险调度");
		actionTV3.setText("+调度");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				YjxDispatchActivity.this.finish(); 
			}
		});
		
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDispatchWindow(1,null);//新建调度
			}
		});
	}
	
	/**
	 * 弹出调度发起窗口
	 * @param dispatchType:0是修改后台暂存调度，1是新建的调度信息
	 */
	public void showDispatchWindow(final int dispatchType,YjxCaseDispatchTable editDiapcEn){
		editDiapatchEn = editDiapcEn;
		Dialog dialog = DialogUtil.getDialogByViewOnlistener(YjxDispatchActivity.this, getDispatchView(dispatchType), "调度", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				submitDispatchInfo(dispatchType);
			}
		});
		dialog.setCancelable(false); //避免误点返回键返回
		dialog.setCanceledOnTouchOutside(false); //避免误点外围返回
		if (yybEntityAll!=null && yybEntityAll.tableData!=null && yybEntityAll.tableData.data!=null) {
			dialog.show();
		}else {
			DialogUtil.getErrDialog(YjxDispatchActivity.this, "请等待营业部信息加载完成后操作！").show();
		}
	
	}
	
	/**获取窗口View
	 * @param dispatchType 
	 * @param editDiapatchEn2 */
	private View getDispatchView(int dispatchType) {
		DispatchDialogView = inflater.inflate(R.layout.yjx_dispatch_dialog_view, null);
		DispatchDialogView.findViewById(R.id.YJXDispatchDialog_gsOrg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { //选择归属营业部
				dalogChoiceOrgYYB(1);
			}
		});
		DispatchDialogView.findViewById(R.id.YJXDispatchDialog_workOrg).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { //选择作业营业部
				dalogChoiceOrgYYB(2);
			}
		});
		DispatchDialogView.findViewById(R.id.YJXDispatchDialog_ggsName).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) { //选择公估师
				if (yybEntityAll == null) {
					DialogUtil.getErrDialog(YjxDispatchActivity.this, "请等待营业部信息加载完成后操作！").show();
				}
				choiceGGShelp.showChoiceDialog();
			}
		});
		DateChoiceUtil.setLongDatePickerDialogOnClick(this, (TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_workTime));//到达时间
		setBussTypeChoice();
		if (dispatchType==0 ) {
			displayEidtIngfo();
		}
		return DispatchDialogView;
	}
	
	

	private void displayEidtIngfo() {
		if (editDiapatchEn!=null) { //显示暂存调度信息
			//归属营业部
			SetTextUtil.setTextViewText((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_gsOrg), editDiapatchEn.gsOrg);//归属营业部
			ChoiceOrgYybEn=new EYYBDataEntity();
			ChoiceOrgYybEn.id = editDiapatchEn.gsOrgId;
			ChoiceOrgYybEn.name = editDiapatchEn.gsOrg;
			//作业机构
			if (!TextUtils.isEmpty(editDiapatchEn.workOrg)) {
				SetTextUtil.setTextViewText((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_workOrg), editDiapatchEn.workOrg);//作业机构
				ChoiceWorkYybEn = new EYYBDataEntity();
				ChoiceWorkYybEn.id = editDiapatchEn.workOrgId;
				ChoiceWorkYybEn.name = editDiapatchEn.workOrg;
			}
			//公估师
			if (!TextUtils.isEmpty(editDiapatchEn.ggsName) && choiceGGShelp!=null) {
				choiceGGShelp.choiceGGS = new GGSData();
				choiceGGShelp.choiceGGS.userId =editDiapatchEn.ggsId;
				choiceGGShelp.choiceGGS.name = editDiapatchEn.ggsName;
			}
			SetTextUtil.setTextViewText((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_ggsName), editDiapatchEn.ggsName);//公估师
			SetTextUtil.setTextViewText((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_ggsTel), editDiapatchEn.ggsTel);//公估师电话
			SetTextUtil.setTextViewText((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_aging), editDiapatchEn.aging+"");//时效
			if (editDiapatchEn.workTime!=null) {
				SetTextUtil.setTextViewText((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_workTime),
						sf.format(editDiapatchEn.workTime));//预约作业时间
			}
			SetTextUtil.setTextViewText((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_taskAddress), editDiapatchEn.taskAddress);//任务地点
			//回显产品信息
			if (!TextUtils.isEmpty(editDiapatchEn.product)) {
				int productChoicePostion = ProductTypeUtil.getPostionBycaseSmallProductType(editDiapatchEn.productId);
				((Spinner) DispatchDialogView.findViewById(R.id.YJXDispatchDialog_product)).setSelection(productChoicePostion,false);  //回显产品信息
				new Thread(){
					@Override
					public void run() {
						super.run();
						sleepLater();
					}
				}.start();
			}
		}else {
			DialogUtil.getErrDialog(this, "无法获取暂存调度信息！").show();
		}
	}
	
	/**延时显示产品信息等*/
	private void sleepLater(){
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		}, 500);
	}
	
	/**延时显示产品信息等*/
	@SuppressLint("HandlerLeak")
	Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (!TextUtils.isEmpty(editDiapatchEn.insuranceBigType)) {
				int bussTypeChoicePostion = ProductTypeUtil.getPostionBygetBussType(editDiapatchEn.productId,editDiapatchEn.bussTypeId);
				((Spinner) DispatchDialogView.findViewById(R.id.YJXDispatchDialog_bussType)).setSelection(bussTypeChoicePostion);  //回显作业类型（业务品种）信息
			}
		}
	};



	private ArrayAdapter<String> smallBussTypeAdatper;
	private List<String> smallBussTypeValues = new ArrayList<String>();
	/**设置选择险种*/
	private void setBussTypeChoice() {
		// 险种信息
		Spinner productSp = (Spinner) DispatchDialogView.findViewById(R.id.YJXDispatchDialog_product);
		final Spinner bussTypeSp = (Spinner) DispatchDialogView.findViewById(R.id.YJXDispatchDialog_bussType);
		final List<NameValuePair> producttypeMap = ProductTypeUtil.caseSmallProductType();
		setadapter(productSp, producttypeMap);
		setBussTypeAdapter(producttypeMap, 0,bussTypeSp);
		productSp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				List<NameValuePair> bussTypeMap = ProductTypeUtil.getBussType(producttypeMap.get(arg2).getName());
				smallBussTypeValues.clear();
				smallBussTypeValues.addAll(ProductTypeUtil.MapToList(bussTypeMap));
				smallBussTypeAdatper.notifyDataSetChanged();
				bussTypeSp.setSelection(0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	/**给Spinner添加适配器*/
	private void setadapter(Spinner typeSp, List<NameValuePair> maplist) {
		ArrayAdapter<String> spAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, InsuranceTypeUtil.MapToList(maplist));
		typeSp.setAdapter(spAdapter);
	}
	/**
	 * 
	 * @param typeMap 显示内容
	 * @param point 选中数据
	 * @param bussTypeSp 显示数据的Spinner
	 */
	public void setBussTypeAdapter(List<NameValuePair> typeMap, int point,Spinner bussTypeSp){
		List<NameValuePair> bussTypeMap = ProductTypeUtil.getBussType(typeMap.get(point).getName());
		smallBussTypeValues.clear();
		smallBussTypeValues.addAll(ProductTypeUtil.MapToList(bussTypeMap));
		smallBussTypeAdatper = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, smallBussTypeValues);
		bussTypeSp.setAdapter(smallBussTypeAdatper);
	}
	
	/**选择的是归属营业部还是作业营业部*/
	public void dalogChoiceOrgYYB(final int yybChoType) {
		choiceInt=-1;
		String[] resource = new String[yybEntityAll.tableData.data.size()];
		for (int i = 0; i < yybEntityAll.tableData.data.size(); i++) {
			resource[i] = (yybEntityAll.tableData.data.get(i).name);
		}
		if (resource.length == 0) {
			DialogUtil.getErrDialog(this, "无可选营业部！").show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("选择归属营业部")
					.setSingleChoiceItems(resource, -1, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							choiceInt = arg1;
						}
					});
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (choiceInt==-1) { //未选择，不操作
						return;
					}
					if (yybChoType == 1) {
						ChoiceOrgYybEn = yybEntityAll.tableData.data.get(choiceInt);
						((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_gsOrg)).setText(ChoiceOrgYybEn.name);
					}else if (yybChoType == 2) {
						ChoiceWorkYybEn = yybEntityAll.tableData.data.get(choiceInt);
						((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_workOrg)).setText(ChoiceWorkYybEn.name);
					}
					
				}
			});
			builder.setNegativeButton("取消", null);
			final Dialog dialog = builder.create();
			dialog.show();
		}
	}
	
	/** 提交调度保存
	 * @param dispatchType 
	 */
	private void submitDispatchInfo(int dispatchType) {
		
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId + ""));
		params.add(new BasicNameValuePair("dispatchList[0].caseBaoanUid", caseBaoanEn.uid)); //接报案UID
		params.add(new BasicNameValuePair("dispatchList[0].caseBaoanNo", caseBaoanEn.caseBaoanNo)); //接报案编号
		if (ChoiceOrgYybEn==null) {
			params.add(new BasicNameValuePair("dispatchList[0].gsOrgId", ChoiceOrgYybEn.id+""));
			params.add(new BasicNameValuePair("dispatchList[0].gsOrg", ChoiceOrgYybEn.name)); //归属机构
		}else {
			params.add(new BasicNameValuePair("dispatchList[0].gsOrgId", caseBaoanEn.gsOrgId+""));
			params.add(new BasicNameValuePair("dispatchList[0].gsOrg", caseBaoanEn.gsOrg)); //归属机构
		}
		int productPosition = ((Spinner)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_product)).getSelectedItemPosition();
		if (productPosition<1) {
			showHintDialog("请选择公估产品！");
			return;
		}
		List<NameValuePair> products = ProductTypeUtil.caseSmallProductType();
		params.add(new BasicNameValuePair("dispatchList[0].productId", products.get(productPosition).getName()));
		params.add(new BasicNameValuePair("dispatchList[0].product", products.get(productPosition).getValue())); //产品细类
		int bussTypePosition = ((Spinner)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_bussType)).getSelectedItemPosition();
		if (bussTypePosition<1) {
			showHintDialog("请选择业务品种！");
			return;
		}
		String productId = ProductTypeUtil.caseSmallProductType().get(productPosition).getName();
		List<NameValuePair> bussTypes = ProductTypeUtil.getBussType(productId);
		params.add(new BasicNameValuePair("dispatchList[0].bussTypeId", bussTypes.get(bussTypePosition).getName()));
		params.add(new BasicNameValuePair("dispatchList[0].bussType", bussTypes.get(bussTypePosition).getValue())); //作业类型
		params.add(new BasicNameValuePair("dispatchList[0].insuranceBigTypeId", caseBaoanEn.insuranceBigTypeId));
		params.add(new BasicNameValuePair("dispatchList[0].insuranceBigType", caseBaoanEn.insuranceBigType));
		params.add(new BasicNameValuePair("dispatchList[0].insuranceSmallTypeId", caseBaoanEn.insuranceSmallTypeId));
		params.add(new BasicNameValuePair("dispatchList[0].insuranceSmallType", caseBaoanEn.insuranceSmallType));
		if (TextUtils.isEmpty(((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_aging)).getText().toString())) {
			showHintDialog("请填写时效！");
			return;
		}
		params.add(new BasicNameValuePair("dispatchList[0].aging", ((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_aging)).getText().toString()));
		if (TextUtils.isEmpty(((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_workTime)).getText().toString())) {
			showHintDialog("请填写预约作业时间！");
			return;
		}
		params.add(new BasicNameValuePair("dispatchList[0].workTime", ((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_workTime)).getText().toString()));
		if (TextUtils.isEmpty(((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_taskAddress)).getText().toString())) {
			showHintDialog("请填写任务地址！");
			return;
		}
		params.add(new BasicNameValuePair("dispatchList[0].taskAddress", ((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_taskAddress)).getText().toString()));
		if (choiceGGShelp.choiceGGS!=null && !TextUtils.isEmpty(choiceGGShelp.choiceGGS.userId)) { //作业公估师信息
			params.add(new BasicNameValuePair("dispatchList[0].ggsId", choiceGGShelp.choiceGGS.userId+""));
			params.add(new BasicNameValuePair("dispatchList[0].ggsName", choiceGGShelp.choiceGGS.name));
		}else {
			showHintDialog("请选择作业公估师！");
			return;
		}
		if (ChoiceWorkYybEn==null) {//作业机构
			showHintDialog("请选择作业机构！");
			return;
		}else {
			params.add(new BasicNameValuePair("dispatchList[0].workOrgId", ChoiceWorkYybEn.id+""));
			params.add(new BasicNameValuePair("dispatchList[0].workOrg", ChoiceWorkYybEn.name) );//作业机构
		}
		if (TextUtils.isEmpty(((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_ggsTel)).getText().toString())) {
			showHintDialog("请填写公估师电话！"); 
			return;
		}
		params.add(new BasicNameValuePair("dispatchList[0].ggsTel", ((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_ggsTel)).getText().toString())); //公估师电话
		if (dispatchType==0 ) { //修改后台暂存调度并发起调度
			if (editDiapatchEn!=null) {
				params.add(new BasicNameValuePair("dispatchList[0].id", editDiapatchEn.id+""));
				params.add(new BasicNameValuePair("dispatchList[0].uid", editDiapatchEn.uid));
			}else {
				DialogUtil.getErrDialog(this, "修改暂存调度失败，请重试！").show();
				return;
			}
		}
		params.add(new BasicNameValuePair("finish", "1")); //是否暂存：暂存为0，调度发起为1
		params.add(new BasicNameValuePair("dispatchList[0].status", "1")); //暂存和调度发起都传0
		LoadDialogUtil.setMessageAndShow(this, "加载中……");
		HttpUtils.requestPost(URLs.YJX_BAOAN_CASE_DISPATCH_SAVE, params, HttpRequestTool.YJX_BAOAN_CASE_DISPATCH_SAVE);
		editDiapatchEn = null;
	}
	
	private void showHintDialog(String hintStr) {
		new AlertDialog.Builder(this).setTitle("提示！")
		.setMessage(hintStr).setNegativeButton("确定", null).create().show();
	}

	private void initView() {
		inflater = LayoutInflater.from(this);
		listView = (ListView) findViewById(R.id.YJXDSPAc_listView);
		initHeadView();
		choiceGGShelp = new ChoiceGGShelp(this,new DialogInterface.OnClickListener() { //选择调度任务是，公估师选择的回调。
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (DispatchDialogView!=null && choiceGGShelp.choiceGGS!=null) {
					((TextView)DispatchDialogView.findViewById(R.id.YJXDispatchDialog_ggsName)).setText(choiceGGShelp.choiceGGS.name);
				}
			}
		});
	}
	
	/**获取案件对应的调度任务列表*/
	private void downLoadDispatchList() {
		LoadDialogUtil.setMessageAndShow(this, "载入中……");
		String uid = caseBaoanEn.uid;
		List<String> paramsDisptch = new ArrayList<String>();
		paramsDisptch.add("caseBaoanUid");
		paramsDisptch.add(uid);
		HttpUtils.requestGet(URLs.YJX_BAOAN_CASE_DISPATCH_LIST, paramsDisptch, HttpRequestTool.YJX_BAOAN_CASE_DISPATCH_LIST);
	}
	
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventData(List<NameValuePair> values) {
		int rcode = Integer.valueOf(values.get(0).getName());
		
		if (rcode == HttpRequestTool.GET_GGS_LIST ) {
			choiceGGShelp.waitProgress.setVisibility(View.INVISIBLE);
		}
		switch (rcode) {
		case HttpRequestTool.YJX_BAOAN_CASE_DISPATCH_LIST:
			LoadDialogUtil.dismissDialog();
			getDisPatchData(values.get(0).getValue());
			break;

		case HttpRequestTool.GET_BAOAN_INFO: // 医健险接报案详情
			LoadDialogUtil.dismissDialog();
			downLoadDispatchList(); // 有接报案信息就加载对应的调度任务列表
			displayHeadInfo(values);
			break;
			
		case HttpRequestTool.DOWNLOAD_DEPT_YYBALL: //获取营业部信息
			LoadDialogUtil.dismissDialog();
			choiceDept(values.get(0).getValue());
			break;  
		case HttpRequestTool.GET_GGS_LIST:
			choiceGGShelp.setGGSList(values.get(0).getValue());
			break;   
		case HttpRequestTool.YJX_BAOAN_CASE_DISPATCH_SAVE:
			LoadDialogUtil.dismissDialog();
			getDispatchInfo(values);
			break;    
		case HttpRequestTool.YJX_BAOAN_CASE_DISPATCH_DELETE: //取消调度
			LoadDialogUtil.dismissDialog();
			setCacelDispatchInfo(values);
			break;  
			
			
		default:
			break;
		}
	}
	
	/**
	 * 取消调度返回信息解析，如果取消成功，刷新界面调度数据
	 * @param values
	 */
	private void setCacelDispatchInfo(List<NameValuePair> values) {
		if (values!=null && values.get(0)!=null) {
			String responseStr = values.get(0).getValue();
			if (responseStr.indexOf("成功")>-1) {
				DialogUtil.getErrDialog(this, "取消调度成功！").show();
				downLoadDispatchList(); //取消成功，重新加载调度列表！
			}else {
				DialogUtil.getErrDialog(this, "取消调度失败！").show();
			}
		}else {
			DialogUtil.getErrDialog(this, "取消调度失败！").show();
		}
	}

	/**解析调度发起保存返回数据*/
	private void getDispatchInfo(List<NameValuePair> values) {
		if (values!=null && values.get(0)!=null) {
			String responseStr = values.get(0).getValue();
			if (responseStr.indexOf("成功")>-1) {
				DialogUtil.getErrDialog(this, "调度发起成功！").show();
				downLoadDispatchList(); //调度成功，重新加载调度列表！
			}else {
				DialogUtil.getErrDialog(this, "调度发起失败！").show();
			}
		}else {
			DialogUtil.getErrDialog(this, "调度发起失败！").show();
		}
	}

	/**解析获取的营业部并设置选择营业部的弹窗*/
	private void choiceDept(String value) {
		try {
			yybEntityAll = JSON.parseObject(value, EYYBListEntity.class);
			choiceGGShelp.setValue(yybEntityAll.tableData.data);
		} catch (Exception e) {
			DialogUtil.getErrDialog(this, "无法获取营业部信息，请重启APP后重试，如果还是不行请联系管理员！").show();
			e.printStackTrace();
		}
	}
	
	/**获取接报案信息*/
	private void getDisPatchData(String value) {
		dispatchList = JSON.parseArray(value, YjxCaseDispatchTable.class);
		disAdapter = new DispatchListAdapter(this, dispatchList);
		listView.setAdapter(disAdapter);
	}
	
	/**获取ListView显示内容，如果没有就不显示*/
	private void initHeadView() {
		caseBaoanEn = (YjxCaseBaoanEntity) getIntent().getSerializableExtra("YjxCaseBaoanEntity");
		if (caseBaoanEn!=null && !TextUtils.isEmpty(caseBaoanEn.uid)) { //
			headView = inflater.inflate(R.layout.yjx_dispatch_head, null);  //有头部数据才加载头部
			downJiebaoanInfo();
		}else { //获取传递的接报案信息错误时提示用户并退出！
			Dialog dialog = DialogUtil.getAlertOneButton(this, "接报案信息有误，请联系管理员！", null);
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					YjxDispatchActivity.this.finish();//提示用户并退出！
				}
			});
			dialog.show();
		}
		if (caseBaoanEn!=null && caseBaoanEn.status==4) { //已经结案的接报案不用再调度了
			actionTV3.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 获取接报案详情
	 */
	private void downJiebaoanInfo() {
		LoadDialogUtil.setMessageAndShow(this, "加载中……");
		// 获取接报案信息
		String uid = caseBaoanEn.uid;
		if (!TextUtils.isEmpty(uid)) {
			List<String> paramsd = new ArrayList<String>();
			paramsd.add("uid");
			paramsd.add(uid);
			HttpUtils.requestGet(URLs.GET_BAOAN_INFO, paramsd, HttpRequestTool.GET_BAOAN_INFO);
		}else {
			LoadDialogUtil.dismissDialog();
		}
	}
	
	/**显示头部信息，基本内容*/
	private void displayHeadInfo(List<NameValuePair> values) {
		try {
			caseBaoanEn = JSON.parseObject(values.get(0).getValue(), YjxCaseBaoanEntity.class); 
			SetTextUtil.setTextViewText((TextView) headView.findViewById(R.id.YJXDSPAcHead_wtName), caseBaoanEn.wtName);// 委托人名称
			SetTextUtil.setTextViewText((TextView) headView.findViewById(R.id.YJXDSPAcHead_product), caseBaoanEn.product);// 产品名称
			SetTextUtil.setTextViewText((TextView) headView.findViewById(R.id.YJXDSPAcHead_aging), caseBaoanEn.aging + "");// 时效
			SetTextUtil.setTextViewText((TextView) headView.findViewById(R.id.YJXDSPAcHead_uid), caseBaoanEn.uid);// 接报案编号
			SetTextUtil.setTextViewText((TextView) headView.findViewById(R.id.YJXDSPAcHead_insuranceBigTypeSmallType), caseBaoanEn.insuranceBigType
					+ "-" + caseBaoanEn.insuranceSmallType);// 险种大类+细类
			SetTextUtil.setTextViewText((TextView) headView.findViewById(R.id.YJXDSPAcHead_riskAddress_province_city), caseBaoanEn.riskProvince + "-"
					+ caseBaoanEn.riskCity + "-" + caseBaoanEn.riskAddress);// 出险地址
			if (caseBaoanEn.entrustDataList != null && caseBaoanEn.entrustDataList.size() > 0 && caseBaoanEn.entrustDataList.get(0) != null) {// 委托要求
				SetTextUtil.setTextViewText((TextView) headView.findViewById(R.id.YJXDSPAcHead_entrustRequest),
						caseBaoanEn.entrustDataList.get(0).entrustRequest);
			}
			listView.addHeaderView(headView); //数据正常家在显示就加载头部
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
