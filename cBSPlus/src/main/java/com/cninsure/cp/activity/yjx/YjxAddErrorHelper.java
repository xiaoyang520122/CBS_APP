package com.cninsure.cp.activity.yjx;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.cninsure.cp.R;
import com.cninsure.cp.entity.yjx.ErrorTypeEntity;
import com.cninsure.cp.entity.yjx.YjxShenheMsgEntity.SHHTableData.SHHData.SHHListEntity;
import com.cninsure.cp.utils.DialogUtil;
import com.cninsure.cp.utils.SetTextUtil;

public class YjxAddErrorHelper {
	
	private SHHListEntity errorEntity;
	private LayoutInflater inflater;
	private View cView;
	/**差错类型*/
	public String errorType;
	private List<String> errtypeArr;
	private Context context;
	private ErrorTypeEntity errorTypeEn;
	
	/**
	 * @param context
	 * @param errorEn
	 * @param changePoint 如果是修改就传差错所在位置，如果是新增就传-1；
	 * @param errorTypeEn 
	 * @param bcset
	 */
	public void addOrEditError(Context context,SHHListEntity errorEn,final int changePoint,ErrorTypeEntity errorTypeEn, final BcallSet bcset ){
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.errorTypeEn = errorTypeEn;
		errtypeArr = new ArrayList<String>();
		errtypeArr.add("--请选择--");
		for (int i = 0; i < errorTypeEn.tableData.data.size(); i++) {
			errtypeArr.add(errorTypeEn.tableData.data.get(i).label);
		}
		DialogUtil.getDialogByViewOnlistener(context, getView(errorEn, changePoint), "添加差错信息", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				errorEntity = new SHHListEntity();
				errorEntity.errorMessage = ((EditText)cView.findViewById(R.id.YJXaddErrorV_errorMessage)).getText().toString();
				errorEntity.errorPoints = ((EditText)cView.findViewById(R.id.YJXaddErrorV_errorPoints)).getText().toString();
				errorEntity.errorType = errorType;
				bcset.setValue(errorEntity,changePoint);
			}
		}).show();
	}
	
	
	private View getView(SHHListEntity errorEn,int point) {
		cView = inflater.inflate(R.layout.add_error_view, null);
		Spinner typeSp = ((Spinner)cView.findViewById(R.id.YJXaddErrorV_errorType));
		ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, errtypeArr);
		typeSp.setAdapter(spAdapter);
		if (errorEn!=null) {
			SetTextUtil.setTextViewText(((EditText)cView.findViewById(R.id.YJXaddErrorV_errorMessage)),errorEn.errorMessage);
			SetTextUtil.setTextViewText(((EditText)cView.findViewById(R.id.YJXaddErrorV_errorPoints)),errorEn.errorPoints);
			for (int i = 0; i < errorTypeEn.tableData.data.size(); i++) {
				if (errorTypeEn.tableData.data.get(i).id.equals(errorEn.errorType)) {
					typeSp.setSelection(i+1);
				}
			}
		}
		
		typeSp.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				for (int i = 0; i < errorTypeEn.tableData.data.size(); i++) {
					if (errorTypeEn.tableData.data.get(i).label.equals(errtypeArr.get(arg2))) {
						errorType = errorTypeEn.tableData.data.get(i).id;
					}
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
		});
		return cView;
	}


	public interface BcallSet{
		void setValue(SHHListEntity eEn,int point);
	}
	

}
