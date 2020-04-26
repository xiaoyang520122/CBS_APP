package com.cninsure.cp.entity.yjx;

import android.text.TextUtils;

import com.cninsure.cp.entity.yjx.ErrorTypeEntity.ErrortableData.ErrorType;

public class errorTypeUtil {
	
	public static String getErrorTypeMsgById(ErrorTypeEntity errorTypeEn,String id){
		String lableStr = "未知";
		if (!TextUtils.isEmpty(id) && errorTypeEn!=null && errorTypeEn.tableData!=null && errorTypeEn.tableData.data!=null) {
			for (int i = 0; i < errorTypeEn.tableData.data.size(); i++) {
				ErrorType etTemp = errorTypeEn.tableData.data.get(i);
						if (id.equals(etTemp.id)) {
							lableStr = etTemp.label;
							break;
						}
			}
		}
		return lableStr;
	}

}
