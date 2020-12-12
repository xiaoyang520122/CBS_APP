package com.cninsure.cp.activty;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.BaseActivity;
import com.cninsure.cp.R;
import com.cninsure.cp.cx.fragment.CxThirdFragment;
import com.cninsure.cp.entity.BaseEntity;
import com.cninsure.cp.entity.OCREntity;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.UserInfo;
import com.cninsure.cp.entity.extract.ExtUserEtity;
import com.cninsure.cp.entity.yjx.ImagePathUtil;
import com.cninsure.cp.ocr.BankScanningHelp;
import com.cninsure.cp.ocr.CxWorkPhotoHelp;
import com.cninsure.cp.photo.PickPhotoUtil;
import com.cninsure.cp.utils.BankLogoManage;
import com.cninsure.cp.utils.CheckHttpResult;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.HttpRequestTool;
import com.cninsure.cp.utils.HttpUtils;
import com.cninsure.cp.utils.LoadDialogUtil;
import com.cninsure.cp.utils.PhotoUploadUtil;
import com.cninsure.cp.utils.ToastUtil;
import com.cninsure.cp.utils.UriUtils;
import com.cninsure.cp.utils.permission_util.PermissionApplicationUtil;
import com.cninsure.cp.utils.photo.OutputTool;
import com.cninsure.cp.view.GridViewForScrollView;
import com.cninsure.cp.view.LoadingDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AddBanckCardActivity extends BaseActivity {

	/**公估师账户信息**/
	private ExtUserEtity extUserEtity;
//	private UserInfo userInfo;
	private GridViewForScrollView banckGridView;//选择银行列表
	private TextView cardUserTv,cancelTv,submitTv;//持卡人名称，取消，确定提交
	private EditText cardNumberEdit,banckPartNameEdit;//银行卡卡号，支行名称
	private MyBankAdapter bankAdapter;
	private int choiceBank=-1;//选中的银行在GridView中的位置
	private List<NameValuePair> logoList;//银行logo集合
	private String name,bankNumber,partName,bankName;
	private LoadingDialog loadDialog;
	public OCREntity ocrEntity; //OCR识别内容
	public BankScanningHelp scanningHelp; //调用摄像头拍照的帮助类**/
    private PermissionApplicationUtil permissionUtil;//20191025获取相机和读写权限的帮助类 by xy

	@ViewInject(R.id.ADDBC_bank_scanning) private TextView scanningTv; //银行卡扫描按钮
	@ViewInject(R.id.ADDBC_card_img_view) private ImageView bankCardImg; //银行卡照片View
	@ViewInject(R.id.ADDBC_phg_card) private TextView bankCardPhgTv; //银行卡拍照按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_backcard_view_layout);
		EventBus.getDefault().register(this);
		ViewUtils.inject(this);
		initView();
		setBankOnclick();
	}

	private void setBankOnclick() {
		ocrEntity = new OCREntity();
		scanningHelp=new BankScanningHelp(this,ocrEntity);
		//银行卡
		scanningTv.setOnClickListener(v -> this.scanningHelp.startCamera(2)); //2为银行卡识别
        /**点击拍摄银行卡*/
        bankCardPhgTv.setOnClickListener(arg0 -> permissionUtil.openCameraPermission(() -> {
            OutputTool.getInstance().takeCamera(AddBanckCardActivity.this,1);//这里图省事，1应该封装成常量
        }));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ) {  //拍照返回
            if (requestCode == 1){
                String url = OutputTool.getInstance().getmCurrentPhotoPath();
                Log.i("result_ok", "onActivityResult: "+url);
                Uri imgUrl = UriUtils.getUri(AddBanckCardActivity.this,new File(url));
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imgUrl));
//                    extUserEtity.data.bankCardPhoto = OutputTool.getInstance().getmCurrentPhotoPath();  //照片名称
//                    Glide.with(this).load(bitmap).into(bankCardImg);
                    uploadPhoto(OutputTool.getInstance().getmCurrentPhotoPath()); //上传照片
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else if(requestCode == 2){  //扫描返回
                if (data!=null) {
                    File file =(File)data.getSerializableExtra("FilePath");
                    scanningHelp.forString(requestCode,file);
                }
            }
        }

	}

    /**点击弹出框确认按钮后调用次方法完成图片上传，在CxWorkActivity中的eventbus方法中调用下面的sendMsgToBack方法传递数据给后台**/
    private void uploadPhoto(String imgPath) {
        LoadDialogUtil.setMessageAndShow(this,"上传中……");
        List<NameValuePair> fileUrls=new ArrayList<NameValuePair>();
        fileUrls.add(new BasicNameValuePair("0", imgPath));
        PhotoUploadUtil.uploadOCR(this, fileUrls, URLs.UP_OCR_PHOTO, HttpRequestTool.UP_OCR_PHOTO);
    }

	/**上传签字后返回成功与图片名称及后缀 * 1 身份证识别 * 2 银行卡识别* 3 驾驶证识别* 4 行驶证识别 * **/
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventmeth(List<NameValuePair> responseValue){
		int type=Integer.parseInt(responseValue.get(0).getValue());
		if (type==2) {
			disBankCardInfo(responseValue.get(1).getValue());
		}else if (HttpRequestTool.UP_OCR_PHOTO == type){
            extUserEtity.data.bankCardPhoto = ImagePathUtil.BaseUrl+responseValue.get(1).getValue();  //照片全称
            Glide.with(this).load(extUserEtity.data.bankCardPhoto).placeholder(R.drawable.loadingwait_hui).into(bankCardImg);
        }
	}
	/**显示OCR识别的 银行卡信息*/
	public void disBankCardInfo(String imgName) {
		cardNumberEdit.setText(ocrEntity.insuredBankNo); //账号
		extUserEtity.data.bankNo = ocrEntity.insuredBankNo;
		extUserEtity.data.bankCardPhoto = ImagePathUtil.BaseUrl+imgName;  //照片名称
		Glide.with(this).load(ImagePathUtil.BaseUrl+imgName).into(bankCardImg);
	}
	private void initView() {
        permissionUtil = new PermissionApplicationUtil(this); //20191025相机和读写权限请求 by xy
		banckGridView=(GridViewForScrollView) findViewById(R.id.addBC_GridView);
		cardUserTv=(TextView) findViewById(R.id.addBC_CardUserName);
		cancelTv=(TextView) findViewById(R.id.addBC_cancel);
		submitTv=(TextView) findViewById(R.id.addBC_submit);
		cardNumberEdit=(EditText) findViewById(R.id.addBC_CardNumber);
		banckPartNameEdit=(EditText) findViewById(R.id.addBC_CardpartName);
		extUserEtity = (ExtUserEtity) getIntent().getSerializableExtra("extUserEtity");
		isIdCardEmpty();
		loadDialog = new LoadingDialog(AddBanckCardActivity.this);
		setValue();
		bankAdapter=new MyBankAdapter();
		banckGridView.setAdapter(bankAdapter);
		setAction();
	}
	
	private void setValue() {
		logoList=BankLogoManage.getbankLogo();
		cardUserTv.setText(extUserEtity.data.name);
		if (!TextUtils.isEmpty(extUserEtity.data.bankNo)) {
			cardNumberEdit.setText(extUserEtity.data.bankNo);
		}
		if (!TextUtils.isEmpty(extUserEtity.data.bankCardPhoto))
			Glide.with(this).load(extUserEtity.data.bankCardPhoto).into(bankCardImg);
		if (!TextUtils.isEmpty(extUserEtity.data.bankBranchName)) {
			banckPartNameEdit.setText(extUserEtity.data.bankBranchName);
		}
		bankCardImg.setOnClickListener(v -> {  //点击查勘照片
			Intent intent = new Intent(this, DisplayPictureActivity.class);
			intent.putExtra("picUrl", extUserEtity.data.bankCardPhoto);
			startActivity(intent);
		});
		if (!TextUtils.isEmpty(extUserEtity.data.bankName)) {
			for (int i = 0; i < logoList.size(); i++) {
				if (logoList.get(i).getValue().equals(extUserEtity.data.bankName)) {
					choiceBank=i;
					return;
				}
			}
		}
	}

	private void isIdCardEmpty() {
		if (TextUtils.isEmpty(extUserEtity.data.idCard)) {
			Dialog dialog=DialogUtil.getAlertOneButton(AddBanckCardActivity.this, "请后台补全身份证号码信息后再添加银行卡信息！",null);
			dialog.setOnDismissListener(arg0 -> AddBanckCardActivity.this.finish());
			dialog.show();
		} 
		
	}

	/**设置标题栏点击事件*/
	private void setAction() {
		/**确认提交*/
		submitTv.setOnClickListener(arg0 -> getvalue());
		
		cancelTv.setOnClickListener(arg0 -> AddBanckCardActivity.this.finish());
	}
	
	/**获取控件上的数据**/
	private void getvalue() {
		name = cardUserTv.getText().toString();
		bankNumber = cardNumberEdit.getText().toString();
		partName = banckPartNameEdit.getText().toString();
		if (choiceBank!=-1) {
			bankName = logoList.get(choiceBank).getValue();
		}
		if (TextUtils.isEmpty(name) || TextUtils.isEmpty(bankNumber) 
				|| TextUtils.isEmpty(partName) || TextUtils.isEmpty(bankName) ) {
			DialogUtil.getAlertOneButton(AddBanckCardActivity.this, "请填写和选择全部信息后提交！", null).show();
		}else {
			String paramString = "请再次确定银行卡信息!\n银行名称："+bankName
					+"\n银行卡号："+bankNumber;
			DialogUtil.getAlertOnelistener(AddBanckCardActivity.this, paramString, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					submit();
				}
			}).show();
		}
	}

	public void submit(){
		List<NameValuePair> NVparames = new ArrayList<NameValuePair>(1);
		NVparames.add(new BasicNameValuePair("uid", AppApplication.getUSER().data.userId));
		NVparames.add(new BasicNameValuePair("userId", AppApplication.getUSER().data.userId));
		NVparames.add(new BasicNameValuePair("id", extUserEtity.data.id+""));
//		NVparames.add(new BasicNameValuePair("payeeUserName", name)); //持卡人姓名
//		NVparames.add(new BasicNameValuePair("payeeUserIdcard", extUserEtity.data.idCard)); //持卡人身份证号
		NVparames.add(new BasicNameValuePair("bankCardPhoto", extUserEtity.data.bankCardPhoto));  //银行卡照片
		NVparames.add(new BasicNameValuePair("bankName", bankName));  //银行名称
		NVparames.add(new BasicNameValuePair("bankBranchName", partName));  //支行名称
		NVparames.add(new BasicNameValuePair("bankNo", bankNumber)); //卡号
//		HttpUtils.requestPost(URLs.SAVE_BANK_CARD,NVparames, HttpRequestTool.SAVE_BANK_INFO);
		HttpUtils.requestPost(URLs.EXTACT_USER,NVparames, HttpRequestTool.EXTACT_USER);
		loadDialog.setMessage("数据请求中……").show();
	}
	
	@Subscribe(threadMode=ThreadMode.MAIN)
	public void eventdata(List<NameValuePair> value) {
		int typecode = Integer.parseInt(value.get(0).getName());
		if (typecode == HttpRequestTool.EXTACT_USER ) {
			loadDialog.dismiss();
		}
		switch (CheckHttpResult.checkList(value, AddBanckCardActivity.this)) {
		case HttpRequestTool.EXTACT_USER:
			showSubmitResponse(value.get(0).getValue());

			break;

		default:
			break;
		}
	}

	public void showSubmitResponse(String value){
		BaseEntity ben = JSON.parseObject(value,BaseEntity.class);
		if (ben.success){
			ToastUtil.showToastLong(AddBanckCardActivity.this, "操作成功！");
			AddBanckCardActivity.this.finish();
			extUserEtity.data.bankBranchName=partName;//":null,
			/** 银行名称 */
			extUserEtity.data.bankName=bankName;//":null,
			/** 银行卡号 */
			extUserEtity.data.bankNo=bankNumber;//":null,
			/** 持卡人身份证号 */
			extUserEtity.data.idCard=extUserEtity.data.idCard;//":null,
			/** 持卡人姓名 */
			extUserEtity.data.name=name;//"
		}else{
			DialogUtil.getAlertOneButton(this,ben.msg,null).show();
		}

	}

	private class MyBankAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return logoList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return logoList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int itemPoint, View conView, ViewGroup arg2) {
			conView=LayoutInflater.from(AddBanckCardActivity.this).inflate(R.layout.add_bank_img_item, null);
			ImageView logoImg=(ImageView) conView.findViewById(R.id.addBII_logo);
			ImageView choiceImg=(ImageView) conView.findViewById(R.id.addBII_choiceTage);
			int res=Integer.valueOf(logoList.get(itemPoint).getName());
			logoImg.setImageResource(res);
			if (choiceBank==itemPoint) {
				choiceImg.setVisibility(View.VISIBLE);
			}else {
				choiceImg.setVisibility(View.GONE);
			}
			conView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					choiceBank = itemPoint;
					bankAdapter.notifyDataSetChanged();
				}
			});
			return conView;
		}
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
