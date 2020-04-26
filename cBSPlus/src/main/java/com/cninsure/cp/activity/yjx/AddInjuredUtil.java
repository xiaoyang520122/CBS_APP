package com.cninsure.cp.activity.yjx;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.yjx.YjxCaseBaoanInjuredTable;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.IDCardUtil;
import com.cninsure.cp.utils.SetTextUtil;
import com.cninsure.cp.utils.ValidateMatch;

public class AddInjuredUtil {
	private  Activity activity; //上下文
	public  YjxCaseBaoanInjuredTable InjValue; //获取到的值
	private  LayoutInflater inflater;
	private  Dialog dilog;
	
	public  void getInjured(Activity act,YjxCaseBaoanInjuredTable Value,DialogInterface.OnDismissListener listener){
		if (Value!=null) { //修改
			InjValue = Value;
		}else { //新增
			InjValue=new YjxCaseBaoanInjuredTable();
		}
		activity = act;
		inflater = LayoutInflater.from(activity);
		dilog = DialogUtil.getDialogByView(activity, getInPutView(listener));
		dilog.setOnDismissListener(listener);
		dilog.setCancelable(false);
		dilog.setCanceledOnTouchOutside(false);
		dilog.show();
	}

	/**
	 * @return
	 */
	private  View getInPutView(DialogInterface.OnDismissListener listener) {
		final View conView = inflater.inflate(R.layout.yjx_shangzhe_input_view, null);
		displaValue(conView);
		conView.findViewById(R.id.YJXSZIP_cancel_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dilog.dismiss();
			}
		});
		conView.findViewById(R.id.YJXSZIP_accept_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (getValue(conView)) {
					dilog.dismiss();
				}else {
					new AlertDialog.Builder(activity).setTitle("提示！").setMessage("请填写完整所有信息，并检查身份证号码是否有误后再提交！").setNeutralButton("确定", null).show();
				}
			}
		});
		((TextView) conView.findViewById(R.id.YJXSZIP_SZcardNo)).setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) { //填写完身份证后获取身份证对应性别并显示
		        if (hasFocus) {
		            // 此处为得到焦点时的处理内容
		        } else {
		            // 此处为失去焦点时的处理内容
		        	String idCard = ((EditText) conView.findViewById(R.id.YJXSZIP_SZcardNo)).getText().toString();//身份号码
//		        	if (!TextUtils.isEmpty(idCard)) {
		        	if (ValidateMatch.isIdCard(idCard)) {
		        		String sexStr = IDCardUtil.getSex(idCard);
		        		((TextView) conView.findViewById(R.id.YJXSZIP_SZsex)).setText(sexStr);
					}else {
						((TextView) conView.findViewById(R.id.YJXSZIP_SZcardNo)).setError("身份证号码有误！");
					}
		        }
		    }
		});
		return conView;
	}
	
	/**如果InjValue里面有数据，那么就显示*/
	private  void displaValue(View conView) {
		if (InjValue!=null && !TextUtils.isEmpty(InjValue.name)) {
			SetTextUtil.setEditText((EditText) conView.findViewById(R.id.YJXSZIP_SZname), InjValue.name);//伤者姓名
			SetTextUtil.setEditText((EditText) conView.findViewById(R.id.YJXSZIP_SZcardNo), InjValue.idCard);//身份号码
			SetTextUtil.setTextViewText((TextView) conView.findViewById(R.id.YJXSZIP_SZsex), InjValue.sex);//性别
			SetTextUtil.setEditText((EditText) conView.findViewById(R.id.YJXSZIP_SZHospital), InjValue.hospital);//就诊医院
			SetTextUtil.setEditText((EditText) conView.findViewById(R.id.YJXSZIP_SZDiagnosis), InjValue.diagnostic);//诊断结果
		}
	}

	private  boolean getValue(View conView) {
		boolean isFull = true;
		InjValue.name = ((EditText) conView.findViewById(R.id.YJXSZIP_SZname)).getText().toString();//伤者姓名
		InjValue.idCard = ((EditText) conView.findViewById(R.id.YJXSZIP_SZcardNo)).getText().toString();//身份号码
		InjValue.sex = ((TextView) conView.findViewById(R.id.YJXSZIP_SZsex)).getText().toString();//性别
		InjValue.hospital = ((EditText) conView.findViewById(R.id.YJXSZIP_SZHospital)).getText().toString();//就诊医院
		InjValue.diagnostic = ((EditText) conView.findViewById(R.id.YJXSZIP_SZDiagnosis)).getText().toString();//诊断结果
		if (TextUtils.isEmpty(InjValue.sex)) {
			InjValue.sex=IDCardUtil.getSex(InjValue.idCard); //赋值性别。
		}
		if (TextUtils.isEmpty(InjValue.name)) {
			isFull=false;
			((EditText) conView.findViewById(R.id.YJXSZIP_SZname)).setError("伤者姓名未填写!");
		}
		if (TextUtils.isEmpty(InjValue.idCard)) {
			isFull=false;
			((EditText) conView.findViewById(R.id.YJXSZIP_SZcardNo)).setError("身份号码未填写!");
		}
		if (TextUtils.isEmpty(InjValue.sex)) {
			isFull=false;
//			((TextView) conView.findViewById(R.id.YJXSZIP_SZsex)).setError("未填写!");
		}
		if (TextUtils.isEmpty(InjValue.hospital)) {
			isFull=false;
			((EditText) conView.findViewById(R.id.YJXSZIP_SZHospital)).setError("就诊医院未填写!");
		}
		if (TextUtils.isEmpty(InjValue.diagnostic)) {
			isFull=false;
			((EditText) conView.findViewById(R.id.YJXSZIP_SZDiagnosis)).setError("诊断结果未填写!");
		}
		
		return isFull;
	}
}
