package com.cninsure.cp.utils;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.fc.CasePolicyLevel;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AlerViewUtil {
	
	private int layoutId;
	private Activity activity;
	private LayoutInflater inflater;
	private View view;
	
	public AlerViewUtil(Activity activity,int layoutId){
		this.layoutId=layoutId;
		this.activity=activity;
		inflater=LayoutInflater.from(this.activity);
	}
	
	public View getView(){
		view=inflater.inflate(layoutId, null);
		return view;
	}

	public CasePolicyLevel getdata(){
		CasePolicyLevel level=new CasePolicyLevel();
		level.insuranceName=((EditText)view.findViewById(R.id.XBAMOUNT_Edit1)).getText().toString();
		level.insuranceNo=((EditText)view.findViewById(R.id.XBAMOUNT_Edit2)).getText().toString();
		level.address=((EditText)view.findViewById(R.id.XBAMOUNT_Edit3)).getText().toString();
		String insAmount=((EditText)view.findViewById(R.id.XBAMOUNT_Edit4)).getText().toString();//保险金额
		if (TextUtils.isEmpty(insAmount)) {
			level.insuranceAmount=0.0+"";
		}else {
			level.insuranceAmount=insAmount;
		}
		level.touBao=((EditText)view.findViewById(R.id.XBAMOUNT_Edit5)).getText().toString();
		level.annualRate=((EditText)view.findViewById(R.id.XBAMOUNT_Edit6)).getText().toString();
		level.premium=((EditText)view.findViewById(R.id.XBAMOUNT_Edit7)).getText().toString();
		level.currency=((EditText)view.findViewById(R.id.XBAMOUNT_Edit8)).getText().toString();
		level.deductibleAmount=((EditText)view.findViewById(R.id.XBAMOUNT_Edit9)).getText().toString();
		return level;
	}
}

