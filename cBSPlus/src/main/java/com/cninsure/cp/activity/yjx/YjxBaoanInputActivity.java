package com.cninsure.cp.activity.yjx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.viewpager.widget.PagerAdapter;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.fc.YYBEntity;
import com.cninsure.cp.entity.yjx.AreaEntity;
import com.cninsure.cp.entity.yjx.AreaEntity.AreaTableData;
import com.cninsure.cp.entity.yjx.EInsureCompanyEntity.TableData.EWTRenDataEntity;
import com.cninsure.cp.entity.yjx.EYYBListEntity;
import com.cninsure.cp.entity.yjx.EYYBListEntity.TableData.EYYBDataEntity;
import com.cninsure.cp.entity.yjx.ImagePathUtil;
import com.cninsure.cp.entity.yjx.InsuranceTypeUtil;
import com.cninsure.cp.entity.yjx.ProductTypeUtil;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanEntity;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanInjuredTable;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.DateChoiceUtil;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.FileChooseUtil;
import com.cninsure.cp.utils.FileDownOpenUtil2;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.IDCardUtil;
import com.cninsure.cp.utils.ImageDisplayUtil;
import com.cninsure.cp.utils.ImageUtil;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.OpenFileUtil;
import com.cninsure.cp.utils.PhotoPathUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ViewDisableUtil;
import com.cninsure.cp.view.MarqueeTextView;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

public class YjxBaoanInputActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, OnPageChangeListener{
	
	private View liAnView, riskView, finishView; // 三个现象卡内容View
	private TextView actionTV1, actionTV2, actionTV3,wtrTv,yybTv; // 顶部返回按钮，标题和暂存按钮。
	private ViewPager vpager;
	private List<View> viewlist; // ViewPager加载的View集合
	private PagerAdapter pagerAdapter;
	private RadioGroup radgrup; // 切换选项卡的RadioGroup
	private LayoutInflater inflater;
	private Button storageButton ,submitButton; // 提交按钮
	private Spinner TypeSp ,smallTypeSp,productSp ,bussTypeSp;
	private String FilePath; //文件路径
	private ArrayAdapter<String> typeAdapter,smallTypeAdatper,smallBussTypeAdatper;
	private List<String> smallTypeValues = new ArrayList<String>();
	private List<String> smallBussTypeValues = new ArrayList<String>();
	private YYBEntity YYBALLdata;
	/****显示的时候会在字符串数组中加一个空字符串，避免显示时有值，同时注意取值时需要在YYBDataList中获取ID时加一（+1）**/
	private List<EYYBDataEntity> YYBAllList;
	/**选择的营业部*/
	private EYYBDataEntity selecteYYB;
	private List<NameValuePair> params;
//	private InsureCompanyEntity wtRenData;
	private YjxChoiceWTRhelp choiceWTRhelp; //选择医健险委托人帮助类
	private EYYBListEntity yybEntityAll;
	/**等待上传的委托影响资料*/
	private List<String> TempFileArrs=new ArrayList<String>();
	/**委托信息或者是作业信息中的fileUrls*/
	private String fileUrlStrs="";
	/**上传的委托资料*/
	private LinearLayout wtFileLinear,injuredLinear;
	private AreaEntity provinceEn,cityEn,riskCityEn;
	/**伤者信息*/
	private List<YjxCaseBaoanInjuredTable> injuredData;
	/**编辑接报案时传过来的接报案数据*/
	private YjxCaseBaoanEntity tempStorageJieBaoanEn;
	private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**身份集合*/
	private List<String> provinceList;
	/**是否是回显是请求城市信息*/
	private boolean isEchoedCity=false,isEchoedRiskCity=false;
	private EWTRenDataEntity ChoicewtrEn;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yjx_baoan_input_activity);
		EventBus.getDefault().register(this);
		initaction();
		initView();
		downLoadData();
	}


	private void initView() {
		vpager = (ViewPager) findViewById(R.id.AYJXBAOAN_viewpager);
		radgrup = (RadioGroup) findViewById(R.id.AYJXBAOAN_BtnG);
		submitButton = (Button) findViewById(R.id.AYJXBAOAN_submitButton);
		storageButton = (Button) findViewById(R.id.AYJXBAOAN_tempButton);

		submitButton.setOnClickListener(this);
		storageButton.setOnClickListener(this);
		actionTV3.setOnClickListener(this);
		radgrup.setOnCheckedChangeListener(this);
		// vpager.setOnPageChangeListener(this);
		inflater = LayoutInflater.from(this);
		vpager.setOnPageChangeListener(this);
		viewlist = new ArrayList<View>();
		// spinnerView1=new ArrayList<Spinner>(11);
		
		liAnView = inflater.inflate(R.layout.yjx_baoan_input_lian_info_view, null);
		riskView = inflater.inflate(R.layout.yjx_baoan_input_risk_info_view, null);
		finishView = inflater.inflate(R.layout.yjx_baoan_input_fee_info_view, null);
		
		if ("seeBaoanInfo".equals(getIntent().getStringExtra("requestType"))) {
			ViewDisableUtil.disableSubControls((ScrollView)liAnView);
			ViewDisableUtil.disableSubControls((ScrollView)riskView);
			ViewDisableUtil.disableSubControls((ScrollView)finishView);
			findViewById(R.id.AYJXBAOAN_ButtonLayout).setVisibility(View.GONE); //隐藏提交暂存
			actionTV3.setVisibility(View.INVISIBLE);
		}
		
		SetTextUtil.setOnclickShowLongDatePickerDialog((TextView)finishView.findViewById(R.id.YJXBAINP_finishDate), this);
		liAnInfoViewInit();
		liAnRiskViewInit();
		viewlist.add(liAnView);
		viewlist.add(riskView);
		viewlist.add(finishView);
		initviewpageradapter();
		vpager.setAdapter(pagerAdapter);
	}
	
	/**出险控件加载*/
	private void liAnRiskViewInit() {
		SetTextUtil.setOnclickShowLongDatePickerDialog((TextView)riskView.findViewById(R.id.YJXRISKINP_riskDate), this);
		((ToggleButton)riskView.findViewById(R.id.YJXRISKINP_localButton)).setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				if (on) {
					((TextView)riskView.findViewById(R.id.YJXRISKINP_localBoolean)).setText("本地");
				}else {
					((TextView)riskView.findViewById(R.id.YJXRISKINP_localBoolean)).setText("异地");
				}
			}
		});
		//添加伤者信息
		injuredLinear = (LinearLayout)riskView.findViewById(R.id.YJXRISKINP_injuredLayout);
		View view = (TextView)riskView.findViewById(R.id.add_Layout_button);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getAndDisplayInjured(-1);
			}
		});
		disPlayInjured();
	}
	
	/**弹出窗口填写伤者信息，并在传递监听事件中完成对当前Activity中伤者信息的添加并显示
	 *itemId>-1代表修改，==-1表示添加 */
	private void getAndDisplayInjured(final int itemId){
		AddInjuredUtil addInjuredUtil = new AddInjuredUtil();
		if (itemId==-1) {
			addInjuredUtil.getInjured(YjxBaoanInputActivity.this,null, getDismisListener(addInjuredUtil,itemId));
		}else {
			addInjuredUtil.getInjured(YjxBaoanInputActivity.this,injuredData.get(itemId), getDismisListener(addInjuredUtil,itemId));
		}
	}
	
	/***/
	private DialogInterface.OnDismissListener getDismisListener(final AddInjuredUtil addInjuredUtil,final int itemId){
		DialogInterface.OnDismissListener listener =new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				if (addInjuredUtil.InjValue.name!=null) {
					if (itemId==-1) { //新增
						injuredData.add(addInjuredUtil.InjValue);
					}else { //修改
						
					}
					disPlayInjured();
				}
			}
		};
		return listener;
	}
	
	/**显示伤者信息*/
	public void disPlayInjured(){
		injuredLinear.removeAllViews();
		if (injuredData==null || injuredData.size()==0) {
			injuredLinear.addView(inflater.inflate(R.layout.yjx_injured_empty, null));
			if(injuredData==null){
				injuredData = new ArrayList<YjxCaseBaoanInjuredTable>();
			}
			return;
		}
		for (int i = 0; i < injuredData.size(); i++) {
			View conView = inflater.inflate(R.layout.yjx_shangzhe_info_view, null);
			SetTextUtil.setTextViewText((TextView) conView.findViewById(R.id.YJXSZV_SZtitle), " ▋伤者" + (i + 1));
			final YjxCaseBaoanInjuredTable InJEn = injuredData.get(i);
			if (InJEn != null) {
				SetTextUtil.setTextViewText((TextView) conView.findViewById(R.id.YJXSZV_SZname), InJEn.name); //伤者姓名
				final TextView cardTv = (TextView) conView.findViewById(R.id.YJXSZV_SZcardNo);
				SetTextUtil.setTextViewText(cardTv, InJEn.idCard); // 伤者身份号码
				SetTextUtil.setTextViewText((TextView) conView.findViewById(R.id.YJXSZV_SZHospital), InJEn.hospital); // 医院
				SetTextUtil.setTextViewText((TextView) conView.findViewById(R.id.YJXSZV_SZDiagnosis), InJEn.diagnostic); // 诊断结果
				final TextView sexTv = (TextView) conView.findViewById(R.id.YJXSZV_SZsex);
				SetTextUtil.setTextViewText(sexTv, IDCardUtil.getSex(InJEn.idCard));
				setDeleteOnclick(i, (TextView) conView.findViewById(R.id.YJXSZV_SZDelete));
				setConViewOnclick(conView.findViewById(R.id.YJXSZV_SZtitle),i);
				if ("seeBaoanInfo".equals(getIntent().getStringExtra("requestType"))) {
					conView.findViewById(R.id.YJXSZV_SZDelete).setVisibility(View.GONE);
				}
			}
			injuredLinear.addView(conView);
		}
		if ("seeBaoanInfo".equals(getIntent().getStringExtra("requestType"))) {
			ViewDisableUtil.disableSubControls(injuredLinear);
		}
	}
	
private void setConViewOnclick(View conView, final int itemCode) {
		
		conView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AddInjuredUtil addInjuredUtil = new AddInjuredUtil();
				addInjuredUtil.getInjured(YjxBaoanInputActivity.this,injuredData.get(itemCode), getDismisListener(addInjuredUtil,itemCode));
			}
		});
	}

	private void setDeleteOnclick(final int postion, final TextView deleteTv) {
		deleteTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showHintDialog(postion, deleteTv);
			}
		});
	}
	
	/**提示删除三者信息*/
	private void showHintDialog(final int postion, TextView deleteTv) {
		DialogUtil.getAlertOnelistener(this, "您确定要删除该条伤者信息吗？", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				injuredData.remove(postion);
				disPlayInjured();
			}
		}).show();
	}

	/**立案控件加载*/
	private void liAnInfoViewInit() {
		//添加点击选择时间的功能
		SetTextUtil.setOnclickShowLongDatePickerDialog((TextView)liAnView.findViewById(R.id.YJXBAINP_insuranceStartDate), this);
		SetTextUtil.setOnclickShowLongDatePickerDialog((TextView)liAnView.findViewById(R.id.YJXBAINP_insuranceEndDate), this);
		SetTextUtil.setOnclickShowLongDatePickerDialog((TextView)liAnView.findViewById(R.id.YJXBAINP_wtDate), this);
		yybTv = (TextView)liAnView.findViewById(R.id.YJXBAINP_gsOrg);
		
		wtFileLinear = (LinearLayout) liAnView.findViewById(R.id.YJXBAINP_wtFile);
		
		wtrTv = (TextView)liAnView.findViewById(R.id.YJXBAINP_wtName);
		liAnView.findViewById(R.id.YJXBAINP_chocieBycamera).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FilePath = PhotoPathUtil.getPictureCreatePath("CBS_Yjx_JieBaoAn",YjxBaoanInputActivity.this);
				PickPhotoUtil.cameraPhotoToUrl(YjxBaoanInputActivity.this,FilePath,PickPhotoUtil.PHOTO_REQUEST_CAMERAPHOTO);
			}
		});
		liAnView.findViewById(R.id.YJXBAINP_chocieByAlbum).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PickPhotoUtil.albumPhoto(YjxBaoanInputActivity.this, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO);
			}
		});
		final TextView wtDTv = (TextView)liAnView.findViewById(R.id.YJXBAINP_wtDate);
		wtDTv .setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DateChoiceUtil.showLongDatePickerDialog(YjxBaoanInputActivity.this, wtDTv);
			}
		});
		choiceWTRhelp=new YjxChoiceWTRhelp(this,liAnView);
		wtrTv .setOnClickListener(new OnClickListener() { //选择委托人
			@Override
			public void onClick(View arg0) {
				choiceWTRhelp.showChoiceDialog();
			}
		});
		//产品信息
		TypeSp = (Spinner)liAnView.findViewById(R.id.YJXBAINP_caseType);
		smallTypeSp = (Spinner)liAnView.findViewById(R.id.YJXBAINP_insuranceSmallType);
		final List<NameValuePair>  typeMap=InsuranceTypeUtil.getinsuranceTypeCollection();
		setadapter(TypeSp,typeMap);
		setSmallTypeAdapter(typeMap,0);
		TypeSp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				List<NameValuePair> smalltypeMap = InsuranceTypeUtil.getinsuranceSmallTypeCollection(typeMap.get(arg2).getName());
				smallTypeValues.clear();
				smallTypeValues.addAll(InsuranceTypeUtil.MapToList(smalltypeMap));
				smallTypeAdatper.notifyDataSetChanged();
				smallTypeSp.setSelection(0);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		
		//险种信息
		productSp  = (Spinner)liAnView.findViewById(R.id.YJXBAINP_product); 
		bussTypeSp = (Spinner)liAnView.findViewById(R.id.YJXBAINP_bussType);
		//委托类型
		setWTType();
		//选择文件类型
		setChoiceWtFileOnclick();
		final List<NameValuePair>  producttypeMap=ProductTypeUtil.caseSmallProductType();
		setadapter(productSp,producttypeMap);
		setBussTypeAdapter(producttypeMap,0);
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
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}
	
	private void setChoiceWtFileOnclick() {
		liAnView.findViewById(R.id.YJXBAINP_chocieBycamera).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FilePath = PhotoPathUtil.getPictureCreatePath("YJX-Input-Case",YjxBaoanInputActivity.this);
				PickPhotoUtil.cameraPhotoToUrl(YjxBaoanInputActivity.this,FilePath,PickPhotoUtil.PHOTO_REQUEST_CAMERAPHOTO);
			}
		});
		liAnView.findViewById(R.id.YJXBAINP_chocieByAlbum).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				PickPhotoUtil.albumPhoto(YjxBaoanInputActivity.this, PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO);
			}
		});
	}
	
	/**选择委托文件返回*/
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (resultCode != Activity.RESULT_OK) {
			 return;
		 }
	        switch (requestCode) {
	        //**委托文件**/
	        case PickPhotoUtil.PHOTO_REQUEST_CAMERAPHOTO:// 委托相机
	        	ImageUtil.compressBmp(this, data, FilePath);
	        	TempFileArrs.add(FilePath);
	        	disWtPlayImage(wtFileLinear, null);
	            break;
	        case PickPhotoUtil.PHOTO_REQUEST_ALBUMPHOTO:// 委托文件
	        	FilePath = FileChooseUtil.getInstance(this).getChooseFileResultPath(data.getData());
	        	TempFileArrs.add(FilePath);
	        	disWtPlayImage(wtFileLinear, null);
	            break;
	        default:
	        	break;
	        }
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	
	/**
	 * 添加图片到LinearLayout中,如果fileUrls==null就是显示选择的文件列表
	 * @param boxView
	 * @param fileUrls
	 * @param typeCode //接报案文件1
	 */
	private void disWtPlayImage(LinearLayout boxView , String fileUrls) {
		if (!TextUtils.isEmpty(fileUrls)  ) { //存文件的字符串不为空
			if (fileUrlStrs !=null && fileUrlStrs.length()>0) { //保证作业信息中有影像资料的时候只显示作业信息中的资料
			}else {
				fileUrlStrs = fileUrls;
			}
		}
		String [] fileNames= removeEmptyString(fileUrlStrs.split(","));
		for (int i = 0; i < fileNames.length; i++) { //网络图片是七牛云，需要添加接口地址。
			fileNames[i]=ImagePathUtil.BaseUrl+fileNames[i];
		}
		boxView.removeAllViews();
		int size = 1;
		size = addViewToView(boxView, fileNames , size, false,1);
		/**选择*/
		if (TempFileArrs!=null && TempFileArrs.size()>0) { //未上传的文件列表
			String [] temparr = new String[]{}; 
			size = addViewToView(boxView, TempFileArrs.toArray(temparr) , size ,true,1);
		}
	}
	
	/**剔除数组中空字符串*/
	private String[] removeEmptyString(String [] arrStr){
		List<String> arrList=new ArrayList<String>();
		for (String fileName:arrStr) {
			if (!TextUtils.isEmpty(fileName)){
				arrList.add(fileName);
			}
		}
		String [] arrtemp=new String[]{};
		return arrList.toArray(arrtemp);
	}
	
	/**
	 * 往指定布局文件中添加数组中文列表视图
	 * @param boxView
	 * @param fileArrs
	 * @param size 
	 * @param workType 1为委托资料，2为作业资料 
	 */
	private int addViewToView(LinearLayout boxView , String [] fileArrs, int size, boolean deleteIsVisble,int workType){
		int i=size;
		if (fileArrs!=null && fileArrs.length>0) {
			for (int j=0;j<fileArrs.length;j++) {
				String fileName=fileArrs[j];
				if (!TextUtils.isEmpty(fileName)){
					View view = inflater.inflate(R.layout.yjx_img_item, null);
					
					if (deleteIsVisble) {
						String [] pathArr = fileName.split("/");
						((MarqueeTextView)view.findViewById(R.id.YJXVI_title)).setText(pathArr[pathArr.length-1]); //刚选择的文件直接显示文件名称
						((TextView)view.findViewById(R.id.YJXVI_delete)).setVisibility(View.VISIBLE);
						setDeleteOnclick(fileArrs,j,(TextView)view.findViewById(R.id.YJXVI_delete),workType); //添加单击删除功能
					}else {
						((MarqueeTextView)view.findViewById(R.id.YJXVI_title)).setText("委托影像资料-"+i++);
					}
					ImageView imgV=((ImageView)view.findViewById(R.id.item_gridviewForExlist_photoup_img));
					if ("image".equals(OpenFileUtil.getFileType(fileName))) { //是图片就加在缩略图
						Glide.with(this).load(fileName).centerCrop().error(R.drawable.ssdk_weibo_empty_failed).into(imgV);
					}else { //非图片就显示对应图标
						Glide.with(this).load(OpenFileUtil.getTypeResouse(fileName, YjxBaoanInputActivity.this)).
						centerCrop().error(R.drawable.ssdk_weibo_empty_failed).into(imgV);
					}
					boxView.addView(view);
					setViewOnclick(view,fileName);
				}
			}
		}
		return i;
	}
	
	/**删除选择文件
	 * @param j 
	 * @param fileArrs 
	 * @param workType */
	private void setDeleteOnclick(final String[] fileArrs, final int j,TextView deleteTv, final int workType) {
		deleteTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (workType==1) { //委托文件删除
					TempFileArrs.remove(j);
					disWtPlayImage(wtFileLinear, fileUrlStrs);
				}else {//作业文件删除
//					workTempFileArrs.remove(j);
//					disPlayWorkfile();
				}
			}
		});
	}
	
	private void setViewOnclick(View view , final String fileName) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ("image".equals(OpenFileUtil.getFileType(fileName))) { //是图片就用图片工具加载
					ImageDisplayUtil.displayByMyView(YjxBaoanInputActivity.this, fileName);
				}else { //非图片就判断是本地或者是网络，从而判断是否下载。
					if (fileName.indexOf("://")>-1) {
						dowLoadAndOpenFile(fileName);
					}else {
						OpenFileUtil.openFileByPath(YjxBaoanInputActivity.this, fileName);
					}
				}
			}
		});
	}
	
	@SuppressWarnings("static-access")
	private void dowLoadAndOpenFile(final String fileName) {
		LoadDialogUtil.setMessageAndShow(this, "努力加载中……");
		String[] pathArr = fileName.split("/");
		String name = pathArr[pathArr.length - 1];
		String dirName = Environment.getExternalStorageDirectory().getAbsolutePath();
		new FileDownOpenUtil2().downloadAndOpen(this, dirName, name, fileName);
		String filepath = dirName + name;
		Message message = new Message();
		message.obj = filepath;
	}

	private List<String> wtTypeArr;
	/**设置委托类型*/
	private void setWTType() {
		wtTypeArr = new ArrayList<String>();
		wtTypeArr.add("--请选择--");
		wtTypeArr.add("函");
		wtTypeArr.add("邮件");
		wtTypeArr.add("微信");
		wtTypeArr.add("短信");
		((Spinner)liAnView.findViewById(R.id.YJXBAINP_entrustLetter)).setAdapter(new ArrayAdapter<String>(
				this,android.R.layout.simple_list_item_1,wtTypeArr));
	}


	public void setSmallTypeAdapter(List<NameValuePair> typeMap, int point){
		List<NameValuePair> smalltypeMap = InsuranceTypeUtil.getinsuranceSmallTypeCollection(typeMap.get(point).getName());
		smallTypeValues.clear();
		smallTypeValues.addAll(InsuranceTypeUtil.MapToList(smalltypeMap));
		smallTypeAdatper = new ArrayAdapter<String>(YjxBaoanInputActivity.this, android.R.layout.simple_list_item_1, smallTypeValues);
		smallTypeSp.setAdapter(smallTypeAdatper);
	}
	public void setBussTypeAdapter(List<NameValuePair> typeMap, int point){
		List<NameValuePair> bussTypeMap = ProductTypeUtil.getBussType(typeMap.get(point).getName());
		smallBussTypeValues.clear();
		smallBussTypeValues.addAll(ProductTypeUtil.MapToList(bussTypeMap));
		smallBussTypeAdatper = new ArrayAdapter<String>(YjxBaoanInputActivity.this, android.R.layout.simple_list_item_1, smallBussTypeValues);
		bussTypeSp.setAdapter(smallBussTypeAdatper);
	}

	/**给Spinner添加适配器*/
	private void setadapter(Spinner typeSp, List<NameValuePair> maplist) {
		ArrayAdapter<String> spAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, InsuranceTypeUtil.MapToList(maplist));
		typeSp.setAdapter(spAdapter);
	}


	/** 初始化pagerAdapter */
	private void initviewpageradapter() {
		pagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return viewlist.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				container.removeView(viewlist.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				container.addView(viewlist.get(position));
				return viewlist.get(position);
			}
		};
	}
	
	private void initaction() {
		actionTV1 = (TextView) findViewById(R.id.ACTION_V_LTV);
		actionTV2 = (TextView) findViewById(R.id.ACTION_V_CTV);
		actionTV3 = (TextView) findViewById(R.id.ACTION_V_RTV);
		setAction();
	}
	
	private void setAction() {
		actionTV2.setText("医健险接报案录入");
		actionTV3.setText("暂存");
		actionTV3.setCompoundDrawables(null, null, null, null);
		actionTV1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				HintOut(); //
			}
		});
		actionTV3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				submitChoiceFile(0);
			}
		});
	}
	
	/** 提示用户是否真的要退出该界面，避免勿退出！ **/
	private void HintOut() {
		Dialog dialog = DialogUtil.getAlertOnelistener(this, "确定要退出该页面吗！", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				YjxBaoanInputActivity.this.finish();
			}
		});
		dialog.show();
	}


	@Override
	public void onCheckedChanged(RadioGroup arg0, int buttonId) {
		// TODO Auto-generated method stub
		switch (buttonId) {
		case R.id.AYJXBAOAN_btn_0:
			vpager.setCurrentItem(0);
			break;
		case R.id.AYJXBAOAN_btn_1:
			vpager.setCurrentItem(1);
			break;
		case R.id.AYJXBAOAN_btn_2:
			vpager.setCurrentItem(2);
			break;
		default:
			break;
		}
	}


	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ACTION_V_RTV:
//			status = 0;
//			submitChoiceFile();
			break;
		case R.id.AYJXBAOAN_tempButton: //暂存
			submitChoiceFile(0);
			break;
		case R.id.AYJXBAOAN_submitButton:
			submitChoiceFile(1);
			break;

		default:
			break;
		}
	}
	
	/**判断并上传选择文件 0是暂存，1是提交*/ 
	 private void submitChoiceFile(final int code) {
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (TempFileArrs.size()>0) {
			for (int i = 0; i < TempFileArrs.size(); i++) {
				params.add(new BasicNameValuePair("1", TempFileArrs.get(i)));
			}
		}
		if (params.size()>0) { //有需要上传的文件就先上传文件
			FileUploadUtil.uploadYjxFile(params, this,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					caseTempStorage(code);
				}
			});
		}else {//没有需要提交的文件就直接上传作业、委托信息。
			caseTempStorage(code);
		}
	}
	 
	/**暂存/提交案件，0是暂存，1是提交*/
	private void caseTempStorage(int code) {
		IsFull = true;
		status = code;
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId + ""));
		params.add(new BasicNameValuePair("caseTypeId", "200")); // 医健险固定值id
		params.add(new BasicNameValuePair("caseType", "医健险")); // 医健险固定值-医健险
		if (productSp.getSelectedItemPosition() > 0) { // 产品细类
			List<NameValuePair> products = ProductTypeUtil.caseSmallProductType();
			params.add(new BasicNameValuePair("product", products.get(productSp.getSelectedItemPosition()).getValue()));
			params.add(new BasicNameValuePair("productId", products.get(productSp.getSelectedItemPosition()).getName()));
		} else {
			DialogUtil.getErrDialog(this, "请选择产品细类！").show();
			return;
		}
		// 业务品种
		if (bussTypeSp.getSelectedItemPosition() > 0) { // 业务品种
			String productId = ProductTypeUtil.caseSmallProductType().get(productSp.getSelectedItemPosition()).getName();
			List<NameValuePair> bussTypes = ProductTypeUtil.getBussType(productId);
			params.add(new BasicNameValuePair("bussType", bussTypes.get(bussTypeSp.getSelectedItemPosition()).getValue()));
			params.add(new BasicNameValuePair("bussTypeId", bussTypes.get(bussTypeSp.getSelectedItemPosition()).getName()));
		} else {
			DialogUtil.getErrDialog(this, "请选择业务品种！").show();
			return;
		}
		// 险种类型
		if (TypeSp.getSelectedItemPosition() > 0) {
			List<NameValuePair> insuranceTypes = InsuranceTypeUtil.getinsuranceTypeCollection();
			params.add(new BasicNameValuePair("insuranceBigType", insuranceTypes.get(TypeSp.getSelectedItemPosition()).getValue()));
			params.add(new BasicNameValuePair("insuranceBigTypeId", insuranceTypes.get(TypeSp.getSelectedItemPosition()).getName()));
		} else {
			DialogUtil.getErrDialog(this, "请选择险种类型！").show();
			return;
		}
		// 险种细类
		if (smallTypeSp.getSelectedItemPosition() > 0) {
			String codestr = InsuranceTypeUtil.getinsuranceTypeCollection().get(TypeSp.getSelectedItemPosition()).getName();
			List<NameValuePair> smallInsuranceTypes = InsuranceTypeUtil.getinsuranceSmallTypeCollection(codestr);
			params.add(new BasicNameValuePair("insuranceSmallType", smallInsuranceTypes.get(smallTypeSp.getSelectedItemPosition()).getValue()));
			params.add(new BasicNameValuePair("insuranceSmallTypeId", smallInsuranceTypes.get(smallTypeSp.getSelectedItemPosition()).getName()));
		} else {
			DialogUtil.getErrDialog(this, "请选择险种细类！").show();
			return;
		}
		if (selecteYYB!=null && !TextUtils.isEmpty(selecteYYB.name)) { //营业部
			params.add(new BasicNameValuePair("gsOrg", selecteYYB.name));
			params.add(new BasicNameValuePair("gsOrgId", selecteYYB.id+""));
		} else {
			DialogUtil.getErrDialog(this, "请选归属营业部！").show();
			return;
		}
		//委托人
		String wtrStr = wtrTv.getText().toString();
		EWTRenDataEntity wtrEn = choiceWTRhelp.getWtrEn(wtrStr);
		if (wtrEn==null) {
			wtrEn = ChoicewtrEn;
		}
		if (wtrEn!=null && !TextUtils.isEmpty(wtrEn.name)) { //委托人
			params.add(new BasicNameValuePair("wtName", wtrEn.name));
			params.add(new BasicNameValuePair("wtId", wtrEn.id+""));
		} else {
			if (code==1) {
				DialogUtil.getErrDialog(this, "请选择委托人！").show();
				return;
			}
		}

		String insuranceStartDate = getTextInfo((TextView)liAnView.findViewById(R.id.YJXBAINP_insuranceStartDate), true);
		if (TextUtils.isEmpty(insuranceStartDate)) {
			if (code==1) {
			DialogUtil.getErrDialog(this, "保险起期未选择！").show();
			return;}
		}else {
			params.add(new BasicNameValuePair("insuranceStartDate",insuranceStartDate )); //保险起期
		}

		String insuranceEndDate = getTextInfo((TextView)liAnView.findViewById(R.id.YJXBAINP_insuranceEndDate), true);
		if (TextUtils.isEmpty(insuranceEndDate)) {
			if (code==1) {
			DialogUtil.getErrDialog(this, "保险止期未选择！").show();
			return;}
		}else {
			params.add(new BasicNameValuePair("insuranceEndDate",insuranceEndDate )); //保险止期
		}

		String wtDate = getTextInfo((TextView)liAnView.findViewById(R.id.YJXBAINP_wtDate), true);
		if (TextUtils.isEmpty(wtDate)) {
			if (code==1) {
				DialogUtil.getErrDialog(this, "委托日期未选择！").show();
				return;
			}
		}else {
			params.add(new BasicNameValuePair("wtDate",wtDate )); //委托日期
		}
		
		String riskDate = getTextInfo((TextView)riskView.findViewById(R.id.YJXRISKINP_riskDate), true);
		if (TextUtils.isEmpty(riskDate)) {
			if (code==1) {
				DialogUtil.getErrDialog(this, "出险时间未选择！").show();
				return;
			}
		}else {
			params.add(new BasicNameValuePair("riskDate",riskDate )); //出险日期
		}
		
		params.add(new BasicNameValuePair("wtAreaName", getTextInfo((EditText)liAnView.findViewById(R.id.YJXBAINP_wtAreaName), false))); //委托人所在地
		params.add(new BasicNameValuePair("wtCotact", getTextInfo((EditText)liAnView.findViewById(R.id.YJXBAINP_wtCotact), true))); //委托方联系人
		params.add(new BasicNameValuePair("wtContactTel", getTextInfo((EditText)liAnView.findViewById(R.id.YJXBAINP_wtContactTel), true))); //委托方联系人电话
		params.add(new BasicNameValuePair("policyNo", getTextInfo((EditText)liAnView.findViewById(R.id.YJXBAINP_policyNo), true))); //保单号
		params.add(new BasicNameValuePair("caseBaoanNo", getTextInfo((EditText)liAnView.findViewById(R.id.YJXBAINP_caseBaoanNo), true))); //报案号
		params.add(new BasicNameValuePair("aging", getTextInfo((EditText)liAnView.findViewById(R.id.YJXBAINP_aging), true))); //时效
		params.add(new BasicNameValuePair("remark", getTextInfo((EditText)liAnView.findViewById(R.id.YJXBAINP_remark), false))); //备注
		params.add(new BasicNameValuePair("insuredPerson", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_insuredPerson), true))); //被保险人
		params.add(new BasicNameValuePair("insuredPersonCardNo", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_insuredPersonCardNo), true))); //被保险人证件号
		params.add(new BasicNameValuePair("insuredPersonTel", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_insuredPersonTel), true))); //被保险人联系方式
		//获取委托人所在省份
		AreaTableData provinceData = AreaEntity.getAreaTableByPosition(provinceEn, ((Spinner)riskView.findViewById(R.id.YJXRISKINP_PersonProvince)).getSelectedItemPosition()-1);
		if (provinceData!=null) {
			params.add(new BasicNameValuePair("insuredPersonProvince", provinceData.name)); 
			params.add(new BasicNameValuePair("insuredPersonProvinceId", provinceData.id+""));
		}else {
			if (code==1) {
				DialogUtil.getErrDialog(this, "被保险人所在省份未选择！").show();
				return;
			}
		}
		//获取委托人所在市
		AreaTableData wtCityData = AreaEntity.getAreaTableByPosition(cityEn, ((Spinner)riskView.findViewById(R.id.YJXRISKINP_PersonCity)).getSelectedItemPosition()-1);
		if (wtCityData!=null) {
			params.add(new BasicNameValuePair("insuredPersonCity", wtCityData.name)); 
			params.add(new BasicNameValuePair("insuredPersonCityId", wtCityData.id+""));
		}else {
			if (code==1) {
			DialogUtil.getErrDialog(this, "被保险人所在地市未选择！").show();
			return;
			}
		}
		params.add(new BasicNameValuePair("insuredPersonAddress", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_PersonAddress), true))); //被保险人所在地址
		params.add(new BasicNameValuePair("insuredPersonContact", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_PersonContact), true))); //被保险人联系人
		params.add(new BasicNameValuePair("insuredPersonContactTel", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_PersonContactTel), true))); //被保险人联系人电话！
		params.add(new BasicNameValuePair("baoanPerson", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_baoanPerson), false))); //报案人
		params.add(new BasicNameValuePair("baoanPersonTel", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_baoanPersonTel), true))); //报案人联系方式
		// 出险省份
		AreaTableData riskProvinceData = AreaEntity.getAreaTableByPosition(provinceEn,((Spinner) riskView.findViewById(R.id.YJXRISKINP_riskprovince)).getSelectedItemPosition() - 1);
		if (riskProvinceData != null) {
			params.add(new BasicNameValuePair("riskProvince", riskProvinceData.name));
			params.add(new BasicNameValuePair("riskProvinceId", riskProvinceData.id + ""));
		} else {
			if (code==1) {
			DialogUtil.getErrDialog(this, "出险省份未选择！").show();
			return;}
		}
		// 出险市
		AreaTableData riskCityData = AreaEntity.getAreaTableByPosition(riskCityEn,((Spinner) riskView.findViewById(R.id.YJXRISKINP_riskCity)).getSelectedItemPosition() - 1);
		if (riskCityData != null) {
			params.add(new BasicNameValuePair("riskCity", riskCityData.name));
			params.add(new BasicNameValuePair("riskCityId", riskCityData.id + ""));
		} else {
			if (code==1) {
			DialogUtil.getErrDialog(this, "被保险人所在地市未选择！").show();
			return;}
		}
		String localText = ((TextView)riskView.findViewById(R.id.YJXRISKINP_localBoolean)).getText().toString();
		if ("本地".equals(localText)) { //
			params.add(new BasicNameValuePair("local", "1")); //是否本地
		}else if ("异地".equals(localText)) {
			params.add(new BasicNameValuePair("local", "0")); //是否本地
		}else { //未选择
			IsFull = false;
		}
		params.add(new BasicNameValuePair("riskAddress", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_riskAddress), true))); //
		params.add(new BasicNameValuePair("riskReason", getTextInfo((EditText)riskView.findViewById(R.id.YJXRISKINP_riskReason), true))); //
		//伤者列表
		if (code==1 && injuredData!=null && injuredData.size()<0) {
			if (code==1) {
			 DialogUtil.getErrDialog(this, "没有填写伤者信息！").show();
			 return;}
		}
		 for (int j = 0; j < injuredData.size(); j++) {
			 if (injuredData==null || injuredData.get(j)==null || TextUtils.isEmpty(injuredData.get(j).diagnostic) || TextUtils.isEmpty(injuredData.get(j).hospital) || 
					 TextUtils.isEmpty(injuredData.get(j).idCard) || TextUtils.isEmpty(injuredData.get(j).name) || 
					 TextUtils.isEmpty(injuredData.get(j).sex)) {
				 if (code==1) { //只有提交的时候才判断必填
					 DialogUtil.getErrDialog(this, "伤者信息明细填写不全！").show();
					 return;
				}
			}
		 }
		 if (code==1 && fileUrlStrs.length()<3) {//请上传委托资料文件后操作
			 DialogUtil.getErrDialog(this, "操作失败，请重试！").show();
			 return;
		}
		 try {
			 for (int j = 0; j < injuredData.size(); j++) {
				 if (injuredData.get(j)==null) {
					continue;
				}
				 params.add(new BasicNameValuePair("injuredList["+j+"].diagnostic",getTextInfo(injuredData.get(j).diagnostic)));
				 params.add(new BasicNameValuePair("injuredList["+j+"].hospital",getTextInfo(injuredData.get(j).hospital)));
				 params.add(new BasicNameValuePair("injuredList["+j+"].idCard",getTextInfo(injuredData.get(j).idCard)));
				 params.add(new BasicNameValuePair("injuredList["+j+"].name",getTextInfo(injuredData.get(j).name)));
				 params.add(new BasicNameValuePair("injuredList["+j+"].sex",getTextInfo(injuredData.get(j).sex)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

			params.add(new BasicNameValuePair("finishDate", getTextInfo((TextView)finishView.findViewById(R.id.YJXBAINP_finishDate), true))); //结案时间
			params.add(new BasicNameValuePair("yuguAmount", getTextInfo((EditText)finishView.findViewById(R.id.YJXBAINP_yuguAmount), true))); //预估公估费
			params.add(new BasicNameValuePair("clAmount", getTextInfo((EditText)finishView.findViewById(R.id.YJXBAINP_clAmount), true))); //差旅费
			
			params.add(new BasicNameValuePair("entrustDataList[0].entrustLetter", ((Spinner)liAnView.findViewById(R.id.YJXBAINP_entrustLetter)).getSelectedItem().toString())); //委托函
			params.add(new BasicNameValuePair("entrustDataList[0].entrustRequest", getTextInfo((EditText)liAnView.findViewById(R.id.YJXBAINP_entrustRequest), true))); //委托要求
			params.add(new BasicNameValuePair("entrustDataList[0].policy", getTextInfo((EditText)liAnView.findViewById(R.id.YJXBAINP_policy), true))); // 保单
			params.add(new BasicNameValuePair("entrustDataList[0].fileUrls", fileUrlStrs)); //文件URL
//			params.add(new BasicNameValuePair("entrustDataList[0].id", getTextInfo((EditText)riskView.findViewById(R.id.000), true))); //
//			params.add(new BasicNameValuePair("entrustDataList[0].caseId", getTextInfo((EditText)riskView.findViewById(R.id.000), true))); //
			params.add(new BasicNameValuePair("status",code+"")); //状态
			params.add(new BasicNameValuePair("delFlag","0")); //是否删除delFlag
		if (tempStorageJieBaoanEn!=null && tempStorageJieBaoanEn.id!=null) {
			params.add(new BasicNameValuePair("id",tempStorageJieBaoanEn.id+"")); //如果保存返回数据中已经有ID那么就是更新保存信息。
		}
		if (code==0 || IsFull) {
			LoadDialogUtil.setMessageAndShow(this, "请稍后……");
			submintJieBaoan(params, code);
		}else {
			DialogUtil.getErrDialog(this, "请填写所有必填内容后再提交！").show();
		}

	}
	/**判断提交提示用户*/
	private void submintJieBaoan(final List<NameValuePair> params, int code) {
		if (code == 1) {// 提交
			 DialogUtil.getAlertOnelistener(YjxBaoanInputActivity.this, "确定提交吗？", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						HttpUtils.requestPost(URLs.POST_YJX_BAOAN_SAVE, params, HttpRequestTool.POST_YJX_BAOAN_SAVE);
					}
				}).show();
		} else {
			HttpUtils.requestPost(URLs.POST_YJX_BAOAN_SAVE, params, HttpRequestTool.POST_YJX_BAOAN_SAVE);
		}
	}
	

	 private boolean IsFull = true;
	/**如果字段为空，而且是必填就报错
	 * @param ismust 是否必填
	 * @param view */
 private String getTextInfo(TextView view, boolean ismust) {
	 String dateStr = view.getText().toString();
		if (!TextUtils.isEmpty(dateStr)) {
				return dateStr;
		}else if (ismust) {
			view.setError("未填写！");
			IsFull = false;
		}
		return "";
}
 private String getTextInfo(String value) {
		if (value!=null) {
				return value;
		}else {
			return "";
		}
}


	@Override
	public void onPageScrollStateChanged(int arg0) {
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}
	@Override
	public void onPageSelected(int pi) {
		switch (pi) {
		case 0:
			radgrup.check(R.id.AYJXBAOAN_btn_0);
			break;
		case 1:
			radgrup.check(R.id.AYJXBAOAN_btn_1);
			break;
		case 2:
			radgrup.check(R.id.AYJXBAOAN_btn_2);
			break;

		default:
			break;
		}
	}
	
	/** 获取调度任务的作业信息 */
	private void downLoadData() {
		LoadDialogUtil.setMessageAndShow(YjxBaoanInputActivity.this, "加载中……");
		//获取省份信息
		getCityInfo(0,HttpRequestTool.GET_PROVINCE_LIST);
		// 获取作业营业部信息
		downLoadAlldept();
	}
	
	/**
	 * 获取接报案详情
	 */
	private void downJiebaoanInfo() {
		LoadDialogUtil.setMessageAndShow(YjxBaoanInputActivity.this, "加载中……");
		// 获取接报案信息
		String uid = getIntent().getStringExtra("uid");
		if (!TextUtils.isEmpty(uid)) {
			List<String> paramsd = new ArrayList<String>();
			paramsd.add("uid");
			paramsd.add(uid);
			HttpUtils.requestGet(URLs.GET_BAOAN_INFO, paramsd, HttpRequestTool.GET_BAOAN_INFO);
		}else {
			LoadDialogUtil.dismissDialog();
		}
	}
	
	/**获取省市区信息*/
	private void getCityInfo(int id , int code){
		List<String> paramProvince = new ArrayList<String>();
		paramProvince.add("parentId");
		paramProvince.add(id+"");
		HttpUtils.requestGet(URLs.getCityUrlInterface(code), paramProvince,code);
	}
	
	/**获取所有营业部 传3请求所有的营业部信息**/
	private void downLoadAlldept(){
		List<String> params=new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.getUSER().data.userId);
		params.add("type");
		params.add("4");
		params.add("grade");
		params.add("4");//type=4&grade=4
		params.add("organizationId");
		params.add("3");//传3请求所有的营业部信息
		HttpUtils.requestGet(URLs.DOWNLOAD_DEPT_YYB, params, HttpRequestTool.DOWNLOAD_DEPT_YYBALL);
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void eventData(List<NameValuePair> values) {
		int rcode = Integer.valueOf(values.get(0).getName());
		switch (rcode) {
		case HttpRequestTool.DOWNLOAD_DEPT_YYB:
//			LoadDialogUtil.dismissDialog();
//			getALLYYBInfo(values.get(0).getValue());
			break; 
		case HttpRequestTool.GET_E_WT_REN: //获取委托人
			LoadDialogUtil.dismissDialog();
			choiceWTRhelp.displayWTR(values.get(0).getValue());
			break;  
		case HttpRequestTool.DOWNLOAD_DEPT_YYBALL: //获取营业部信息
//			LoadDialogUtil.dismissDialog();
			choiceDept(values.get(0).getValue());
			break;  
		case HttpRequestTool.GET_PROVINCE_LIST: //获取省份信息
//			LoadDialogUtil.dismissDialog();
			disPlayProvinceSpinner(values.get(0).getValue());
			break;  
		case HttpRequestTool.GET_CITYE_LIST: //获取市信息-委托人
			LoadDialogUtil.dismissDialog();
			disPlayCitySpinner(values.get(0).getValue());
			break;   
		case HttpRequestTool.GET_CITYE_LIST2: //获取市信息-出险地
			LoadDialogUtil.dismissDialog();
			disPlayCityRiskSpinner(values.get(0).getValue());
			break; 
		case HttpRequestTool.UPLOAD_FILE_PHOTO: //医健险上传图片成功
			changeImgType(values);
			break;
		case HttpRequestTool.POST_YJX_BAOAN_SAVE: //医健险接报案保存成功
			showWtSavaInfo(values);
			break;
		case HttpRequestTool.GET_BAOAN_INFO: //医健险接报案详情
			LoadDialogUtil.dismissDialog();
			getJiebaoanInfo(values);
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * 获取接报案详情并显示* @param values
	 */
	 private void getJiebaoanInfo(List<NameValuePair> values) {
		if (values!=null && values.get(0).getValue()!=null) {
			tempStorageJieBaoanEn = JSON.parseObject(values.get(0).getValue(), YjxCaseBaoanEntity.class);
			displayEditJieBaoanInfo();
		}
	}

	/** =0是暂存，=1是提交*/
	 private int status;
	/**接报案提交成功与否*/
	private void showWtSavaInfo(List<NameValuePair> values) {
		LoadDialogUtil.dismissDialog();
		int responsecode = Integer.parseInt(values.get(1).getValue());
		if (responsecode == 200 ) { //不是暂存，提示后关闭当前页面。
			if (status==1) {
				Dialog dialog = DialogUtil.getAlertOneButton(this, "提交成功！", null);
				dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface arg0) {
						YjxBaoanInputActivity.this.finish();
						YjxTempStorageActivity.instance.getBuyPullDown();
					}
				});
				dialog.show();
			}else { //提示后留在当前界面。
				DialogUtil.getAlertOneButton(this, "暂存成功！", null).show();
				YjxTempStorageActivity.instance.getBuyPullDown();
			}
		}else {
			LoadDialogUtil.dismissDialog(); //关闭遮罩层
			DialogUtil.getErrDialog(this, "操作失败！").show();
		}
	}
	
	/**将选择的文件存到已上传中，并移除未上传文件
	 * @param values */
	private void changeImgType(List<NameValuePair> values) {
		String UpedFileName = values.get(0).getValue();
		String oldFileName = values.get(1).getValue();
		int fileType = Integer.parseInt(values.get(2).getValue()); //1委托文件上传成功，2是作业文件上传成功
		if (!TextUtils.isEmpty(UpedFileName) && !TextUtils.isEmpty(UpedFileName) && fileType==1) {
			for (int i = 0; i < TempFileArrs.size(); i++) {
				if (oldFileName.equals(TempFileArrs.get(i))) {
					TempFileArrs.remove(i);  //移除选择
					fileUrlStrs = fileUrlStrs+UpedFileName+","; //添加到已上传
					disWtPlayImage(wtFileLinear, null);//刷新界面
				}
			}
		}
	}
	
	/**解析并加载市信息适配器-出险信息*/
	private void disPlayCityRiskSpinner(String value) {

		riskCityEn = JSON.parseObject(value, AreaEntity.class);
		if (riskCityEn!=null && riskCityEn.tableData!=null && riskCityEn.tableData.size()>0) {
			List<String> cityList = new ArrayList<String>();
			cityList.add("--请选择--");
			for (int i = 0; i < riskCityEn.tableData.size(); i++) {
				cityList.add(riskCityEn.tableData.get(i).name);
			}
			((Spinner)riskView.findViewById(R.id.YJXRISKINP_riskCity)).setAdapter(new ArrayAdapter<String>(
					this,android.R.layout.simple_list_item_1,cityList));
			//如果是回显信息时请求的，显示对应出险城市信息
			if (isEchoedRiskCity) {
				isEchoedRiskCity = false ;
				for (int i = 0; i < cityList.size(); i++) {
					if (cityList.get(i).equals(tempStorageJieBaoanEn.riskCity)) {
						((Spinner)riskView.findViewById(R.id.YJXRISKINP_riskCity)).setSelection(i);
						break;
					}
				}
			}
		}
	
	}


	/**解析并加载市信息适配器*/
	private void disPlayCitySpinner(String value) {
		cityEn = JSON.parseObject(value, AreaEntity.class);
		if (cityEn!=null && cityEn.tableData!=null && cityEn.tableData.size()>0) {
			List<String> cityList = new ArrayList<String>();
			cityList.add("--请选择--");
			for (int i = 0; i < cityEn.tableData.size(); i++) {
				cityList.add(cityEn.tableData.get(i).name);
			}
			((Spinner)riskView.findViewById(R.id.YJXRISKINP_PersonCity)).setAdapter(new ArrayAdapter<String>(
					this,android.R.layout.simple_list_item_1,cityList));
			//如果是回显信息时请求的，显示被保险人所在地市信息
			if (isEchoedCity) {
				isEchoedCity = false ;
				for (int i = 0; i < cityList.size(); i++) {
					if (cityList.get(i).equals(tempStorageJieBaoanEn.insuredPersonCity)) {
						((Spinner)riskView.findViewById(R.id.YJXRISKINP_PersonCity)).setSelection(i);
						break;
					}
				}
			}
		}
	}


	/**解析并加载省份信息适配器*/
	private void disPlayProvinceSpinner(String value) {
		provinceEn = JSON.parseObject(value, AreaEntity.class);
		if (provinceEn!=null && provinceEn.tableData!=null && provinceEn.tableData.size()>0) {
			provinceList = new ArrayList<String>();
			provinceList.add("--请选择--");
			for (int i = 0; i < provinceEn.tableData.size(); i++) {
				provinceList.add(provinceEn.tableData.get(i).name);
			}
			((Spinner)riskView.findViewById(R.id.YJXRISKINP_PersonProvince)).setAdapter(new ArrayAdapter<String>(
					this,android.R.layout.simple_list_item_1,provinceList));
			((Spinner)riskView.findViewById(R.id.YJXRISKINP_riskprovince)).setAdapter(new ArrayAdapter<String>(
					this,android.R.layout.simple_list_item_1,provinceList));
			setOnselectChangeLisner();
			setOnriskProvinceselectChangeLisner();
		}
	}

	private void setOnriskProvinceselectChangeLisner() {
		((Spinner)riskView.findViewById(R.id.YJXRISKINP_riskprovince)).setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int point, long arg3) {
				if (point>0) {
					LoadDialogUtil.setMessageAndShow(YjxBaoanInputActivity.this, "加载中……");
					point--; //因为前面加了一个请选择，所以要减一
					getCityInfo(provinceEn.tableData.get(point).id,HttpRequestTool.GET_CITYE_LIST2);//获取市信息
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
	
	}


	/**选择省后加载市区信息*/
	private void setOnselectChangeLisner() {
		((Spinner)riskView.findViewById(R.id.YJXRISKINP_PersonProvince)).setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int point, long arg3) {
				if (point>0) {
					LoadDialogUtil.setMessageAndShow(YjxBaoanInputActivity.this, "加载中……");
					point--; //因为前面加了一个请选择，所以要减一
					getCityInfo(provinceEn.tableData.get(point).id,HttpRequestTool.GET_CITYE_LIST);//获取市信息
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
	}


	/**解析获取的营业部并设置选择营业部的弹窗*/
	private void choiceDept(String value) {
		try {
			yybEntityAll = JSON.parseObject(value, EYYBListEntity.class);
			if (yybEntityAll!=null && yybEntityAll.tableData!=null && yybEntityAll.tableData.data!=null) {
				setOnclickYYBChoice();
			}else {
				DialogUtil.getErrDialog(this, "无法获取营业部信息，请重启APP后重试，如果还是不行请联系管理员！").show();
			}
		} catch (Exception e) {
			DialogUtil.getErrDialog(this, "无法获取营业部信息（解析失败），请重启APP后重试，如果还是不行请联系管理员！").show();
			e.printStackTrace();
		}
		getALLYYBInfo();
		downJiebaoanInfo();
	}
	
	/**是否已经回显*/
	private boolean isDisplayEditInfo = false;
	/**如果是编辑跳转判断是否已经回显接报案信息*/
	private void displayEditJieBaoanInfo() {
		if (isDisplayEditInfo) { //如果已经回显过就跳过
			return;
		}else { //没有回显过就不跳过并将isDisplayEditInfo=true，以标记回显过该信息
			isDisplayEditInfo=true;
		}
//		tempStorageJieBaoanEn = (YjxCaseBaoanEntity) getIntent().getSerializableExtra("YjxCaseBaoanEntity")
		if (tempStorageJieBaoanEn!=null) {
			displayJieBaoanInfo();
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
			displayteshuInfo();
		}
	};
	
	private void displayteshuInfo(){
		int bussTypeChoicePostion = ProductTypeUtil.getPostionBygetBussType(tempStorageJieBaoanEn.productId,tempStorageJieBaoanEn.bussTypeId);
		bussTypeSp.setSelection(bussTypeChoicePostion);  //回显作业类型（业务品种）信息
		
		int smallTypeChoicePostion = InsuranceTypeUtil.getPostionBySmallInsuretype(tempStorageJieBaoanEn.insuranceBigTypeId,tempStorageJieBaoanEn.insuranceSmallTypeId);
		smallTypeSp.setSelection(smallTypeChoicePostion);  //险种细类
	}
	

	/**显示接报案信息**/
	private void displayJieBaoanInfo() {
		int productChoicePostion = ProductTypeUtil.getPostionBycaseSmallProductType(tempStorageJieBaoanEn.productId);
		productSp.setSelection(productChoicePostion,false);  //回显产品信息
		int InsuranceTypeChoicePostion = InsuranceTypeUtil.getPostionByInsuranceType(tempStorageJieBaoanEn.insuranceBigTypeId);
		TypeSp.setSelection(InsuranceTypeChoicePostion,false);  //险种类型
		new Thread(){
			@Override
			public void run() {
				super.run();
				sleepLater();
			}
		}.start();
		yybTv.setText(tempStorageJieBaoanEn.gsOrg); //归属营业部
		selecteYYB=new EYYBDataEntity();
		selecteYYB.id = tempStorageJieBaoanEn.gsOrgId; //回写选择营业部信息到选择营业部实体类，以便提交、保存时获取
		selecteYYB.name = tempStorageJieBaoanEn.gsOrg;//回写选择营业部信息到选择营业部实体类，以便提交、保存时获取
		
		setTvText(wtrTv,tempStorageJieBaoanEn.wtName);//委托人信息 
		if (!TextUtils.isEmpty(tempStorageJieBaoanEn.wtName) && tempStorageJieBaoanEn.wtId!=null) {
			ChoicewtrEn = new EWTRenDataEntity();
			ChoicewtrEn.name = tempStorageJieBaoanEn.wtName;
			ChoicewtrEn.id = tempStorageJieBaoanEn.wtId;
		}
		setTvText((EditText)liAnView.findViewById(R.id.YJXBAINP_wtAreaName), tempStorageJieBaoanEn.wtAreaName);//委托人所在地
		setTvText((EditText)liAnView.findViewById(R.id.YJXBAINP_wtCotact), tempStorageJieBaoanEn.wtCotact);//委托联系人
		setTvText((EditText)liAnView.findViewById(R.id.YJXBAINP_wtContactTel), tempStorageJieBaoanEn.wtContactTel);//委托联系人电话
		setTvText((EditText)liAnView.findViewById(R.id.YJXBAINP_policyNo), tempStorageJieBaoanEn.policyNo);//保单号
		setTvText((EditText)liAnView.findViewById(R.id.YJXBAINP_caseBaoanNo), tempStorageJieBaoanEn.caseBaoanNo);//
		setTvDate((TextView)liAnView.findViewById(R.id.YJXBAINP_insuranceStartDate), (tempStorageJieBaoanEn.insuranceStartDate));//保险起期
		setTvDate((TextView)liAnView.findViewById(R.id.YJXBAINP_insuranceEndDate), (tempStorageJieBaoanEn.insuranceEndDate));//保险止期
		setTvDate((TextView)liAnView.findViewById(R.id.YJXBAINP_wtDate), (tempStorageJieBaoanEn.wtDate));//委托日期
		setTvText((EditText)liAnView.findViewById(R.id.YJXBAINP_aging), tempStorageJieBaoanEn.aging+"");//时效
		setTvText((EditText)liAnView.findViewById(R.id.YJXBAINP_remark), tempStorageJieBaoanEn.remark);//备注
		if (tempStorageJieBaoanEn.entrustDataList!=null) {
			for (int i = 0; i < wtTypeArr.size(); i++) {
				if (wtTypeArr.get(i).equals(tempStorageJieBaoanEn.entrustDataList.get(0).entrustLetter)) {
					 ((Spinner)liAnView.findViewById(R.id.YJXBAINP_entrustLetter)).setSelection(i); //委托函
					 break;
				}
			}
			setTvText((EditText)liAnView.findViewById(R.id.YJXBAINP_policy), tempStorageJieBaoanEn.entrustDataList.get(0).policy);//保单信息
			setTvText((EditText)liAnView.findViewById(R.id.YJXBAINP_entrustRequest), tempStorageJieBaoanEn.entrustDataList.get(0).entrustRequest);//委托要求
			fileUrlStrs = tempStorageJieBaoanEn.entrustDataList.get(0).fileUrls;
			disWtPlayImage(wtFileLinear, fileUrlStrs); //委托资料
		}
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_insuredPerson), tempStorageJieBaoanEn.insuredPerson);//被保险人
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_insuredPersonCardNo), tempStorageJieBaoanEn.insuredPersonCardNo);//被保险人证件号
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_insuredPersonTel), tempStorageJieBaoanEn.insuredPersonTel);//被保险人联系方式
		if (provinceList!=null && tempStorageJieBaoanEn.insuredPersonProvinceId!=null) {
			for (int i = 0; i < provinceEn.tableData.size(); i++) {
				if (provinceList.get(i).equals(tempStorageJieBaoanEn.insuredPersonProvince)) { //被保险人所在省份
					((Spinner)riskView.findViewById(R.id.YJXRISKINP_PersonProvince)).setSelection(i);
					isEchoedCity=true;
					break;
				}
			}
		}
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_PersonAddress), tempStorageJieBaoanEn.insuredPersonAddress);//被保险人所在地址
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_PersonContact), tempStorageJieBaoanEn.insuredPersonContact);//被保险人联系人
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_PersonContactTel), tempStorageJieBaoanEn.insuredPersonContactTel);//被保险人联系人联系方式
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_baoanPerson), tempStorageJieBaoanEn.baoanPerson);//报案人
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_baoanPersonTel), tempStorageJieBaoanEn.baoanPersonTel);//报案人联系方式
		setTvDate((TextView)riskView.findViewById(R.id.YJXRISKINP_riskDate), (tempStorageJieBaoanEn.riskDate));//出险时间
		if (provinceList!=null && tempStorageJieBaoanEn.insuredPersonProvinceId!=null) {
			for (int i = 0; i < provinceEn.tableData.size(); i++) {
				if (provinceList.get(i).equals(tempStorageJieBaoanEn.riskProvince)) { //出险省份
					((Spinner)riskView.findViewById(R.id.YJXRISKINP_riskprovince)).setSelection(i);
					isEchoedRiskCity=true;
					break;
				}
			}
		}
		if (null != tempStorageJieBaoanEn.local && tempStorageJieBaoanEn.local==1) { 
			((ToggleButton)riskView.findViewById(R.id.YJXRISKINP_localButton)).setToggleOn(true); //是否本地
			((TextView)riskView.findViewById(R.id.YJXRISKINP_localBoolean)).setText("本地");
		}else if (null != tempStorageJieBaoanEn.local && tempStorageJieBaoanEn.local==0) {
			((ToggleButton)riskView.findViewById(R.id.YJXRISKINP_localButton)).setToggleOn(false); //是否本地
			((TextView)riskView.findViewById(R.id.YJXRISKINP_localBoolean)).setText("异地");
		} 
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_riskAddress), tempStorageJieBaoanEn.riskAddress);//出险地点
		setTvText((EditText)riskView.findViewById(R.id.YJXRISKINP_riskReason), tempStorageJieBaoanEn.riskReason);//出险原因
		injuredData = tempStorageJieBaoanEn.injuredList;
		disPlayInjured();
		setTvDate((TextView)finishView.findViewById(R.id.YJXBAINP_finishDate), (tempStorageJieBaoanEn.finishDate));//结案时间
		setTvText((EditText)finishView.findViewById(R.id.YJXBAINP_yuguAmount), tempStorageJieBaoanEn.yuguAmount+"");//预估公估费
		setTvText((EditText)finishView.findViewById(R.id.YJXBAINP_clAmount), tempStorageJieBaoanEn.clAmount+"");//差旅费
	}
	
	/**设置TextView或者Edittext中的值*/
	private void setTvText(TextView edTv , String value){
		if (!TextUtils.isEmpty(value) && !"null".equals(value)) {
			edTv.setText(value);
		}
	}
	/**设置TextView或者Edittext中的日期时间值*/
	private void setTvDate(TextView edTv , Date value){
		if (value!=null) {
			try {
				String dateStr = dateFormat.format(value);
				edTv.setText(dateStr);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private void setOnclickYYBChoice(){
		final String[] items = new String[yybEntityAll.tableData.data.size()];
		for (int i = 0; i < yybEntityAll.tableData.data.size(); i++) {
			items[i] = yybEntityAll.tableData.data.get(i).name;
		}
		yybTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showYYBChoiceDialog(items,yybTv );
			}
		});
	}
	
	private void showYYBChoiceDialog(final String[] items , final TextView yybTv){
		new AlertDialog.Builder(this).setTitle("选择营业部")
		.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int itemId) {
				selecteYYB = YYBAllList.get(itemId);
				yybTv.setText(items[itemId]);
			}
		})
		.show();
	}


	/**获取营业部信息,并显示在Spinner中*/
	/**获取所有的营业部信息 并传递到帮助类里面**/
	private void getALLYYBInfo() {
//		YYBALLdata=JSON.parseObject(value, YYBEntity.class);
		if (null!=yybEntityAll && null!=yybEntityAll.tableData && null!=yybEntityAll.tableData.data) {
			YYBAllList=new ArrayList<EYYBDataEntity>();
			for (int i = 0; i < yybEntityAll.tableData.data.size(); i++) {
				YYBAllList.add(yybEntityAll.tableData.data.get(i));
			}
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	/**返回键提示是否退出*/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode==KeyEvent.KEYCODE_BACK) {
			HintOut();
			 return false;//拦截事件
		}
		return super.onKeyDown(keyCode, event);
	}

}
