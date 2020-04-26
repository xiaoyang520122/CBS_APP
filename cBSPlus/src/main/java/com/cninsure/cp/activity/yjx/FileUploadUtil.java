package com.cninsure.cp.activity.yjx;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.utils.PhotoUploadUtil;

public class FileUploadUtil {
	
	public static void uploadYjxFile(List<NameValuePair> TempFileArrs ,final Activity activity, DialogInterface.OnClickListener listener) {
		
		
//		resousePathList;photoType
		final List<NameValuePair> params=new ArrayList<NameValuePair>();
		for (int i = 0; i < TempFileArrs.size(); i++) {
			if (!TextUtils.isEmpty(TempFileArrs.get(i).getValue())) {
				params.add(TempFileArrs.get(i));
			}
		}
		Log.e("JsonHttpUtils", "10000"+JSON.toJSONString(params));
		
		if (params!=null && params.size()!=0) {
			PhotoUploadUtil.uploadYjxFile(activity, params, URLs.UPLOAD_FILE_PHOTO,listener);
		}
	}

}
