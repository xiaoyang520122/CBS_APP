package com.cninsure.cp.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.fc.CaseChaKan;
import com.cninsure.cp.fc.activity.SurveyActivity;

public class AddSurveyRecordUtil implements  TextWatcher {
	
	private int layoutId=R.layout.survey_record_add_layout;
	private SurveyActivity activity;
	private LayoutInflater inflater;
	private View view;
	private Dialog dialog;
//	/**新增查勘记录标识**/
//	public static int ADD_NEW=0;
//	/**修改查勘记录标识**/
//	public static int EDIT_RECORD=1;
	/**请求操作类型**/
	private int requestCode;
	
	
	public AddSurveyRecordUtil(SurveyActivity activity,int requestCode){
		this.requestCode=requestCode;
		this.activity=activity;
		inflater=LayoutInflater.from(this.activity);
		view=getView();
		dialog=DialogUtil.getDialogByViewTwoButton(activity, view, "添加查勘记录！", clickListener);
	}
	
	DialogInterface.OnClickListener clickListener=new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			CaseChaKan ckbean=getdata();
			if (ckbean!=null) { //录入查勘记录不为空
				activity.addSurveyRecord(ckbean,requestCode);
			}
		}
	};
	

	private View getView() {
		view=inflater.inflate(layoutId, null);
		((TextView) view.findViewById(R.id.SRADDLO_renshu)).addTextChangedListener(this);// 查勘时间
		((TextView) view.findViewById(R.id.SRADDLO_tianshu)).addTextChangedListener(this);// 查勘记录
		activity.setonclickandValue((TextView) view.findViewById(R.id.SRADDLO_ckdate));
		setValue();
		return view;
	}
	
	private void setValue() {
		if (requestCode>=0) {
			CaseChaKan chaKanBean=activity.getchaKandata(requestCode);
			((TextView) view.findViewById(R.id.SRADDLO_ckdate)).setText(chaKanBean.ckdate);// 查勘时间
			((EditText) view.findViewById(R.id.SRADDLO_ckjl)).setText(chaKanBean.ckjl);// 查勘记录
			((EditText) view.findViewById(R.id.SRADDLO_cyry)).setText(chaKanBean.cyry);// 参与人员
			((TextView) view.findViewById(R.id.SRADDLO_gsze)).setText(chaKanBean.gsze );// 工时总额
			((EditText) view.findViewById(R.id.SRADDLO_renshu)).setText(chaKanBean.renshu);// 人数
			((EditText) view.findViewById(R.id.SRADDLO_tianshu)).setText(chaKanBean.tianshu);// 天数
		}
	}

	public CaseChaKan getdata() {
		CaseChaKan chaKanBean = new CaseChaKan();
		chaKanBean.ckdate = ((TextView) view.findViewById(R.id.SRADDLO_ckdate)).getText().toString();// 查勘时间
		chaKanBean.ckjl = ((EditText) view.findViewById(R.id.SRADDLO_ckjl)).getText().toString();// 查勘记录
		chaKanBean.cyry = ((EditText) view.findViewById(R.id.SRADDLO_cyry)).getText().toString();// 参与人员
		chaKanBean.gsze = ((TextView) view.findViewById(R.id.SRADDLO_gsze)).getText().toString();// 工时总额
		chaKanBean.renshu = ((EditText) view.findViewById(R.id.SRADDLO_renshu)).getText().toString();// 人数
		chaKanBean.tianshu = ((EditText) view.findViewById(R.id.SRADDLO_tianshu)).getText().toString();// 天数
		String tempstrString = chaKanBean.ckdate + chaKanBean.ckjl + chaKanBean.cyry 
				+ chaKanBean.gsze + chaKanBean.renshu + chaKanBean.tianshu;
		if (TextUtils.isEmpty(tempstrString)) {
			ToastUtil.showToastShort(activity, "未填写任何信息！");
			return null;
		}
		return chaKanBean;
	}
	
	public void showlayout(){
		dialog.show();
	}


	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		try {
			int ts=Integer.valueOf(((EditText) view.findViewById(R.id.SRADDLO_tianshu)).getText().toString());
			int rs=Integer.valueOf(((EditText) view.findViewById(R.id.SRADDLO_renshu)).getText().toString());
			int amount=ts*rs;
			((TextView) view.findViewById(R.id.SRADDLO_gsze)).setText(amount+"");
		} catch (NumberFormatException e) {
			((TextView) view.findViewById(R.id.SRADDLO_gsze)).setText("0");
			e.printStackTrace();
		}
	}
	

}
