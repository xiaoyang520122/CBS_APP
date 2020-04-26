package com.cninsure.cp.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.entity.URLs;
import com.cninsure.cp.entity.yjx.EYYBListEntity;
import com.cninsure.cp.entity.yjx.EYYBListEntity.TableData.EYYBDataEntity;

public class YYBUtils {

	/** 获取所有CBSPlus营业部 传3请求所有的营业部信息 **/
	public static void downLoadAlldept() {
		List<String> params = new ArrayList<String>();
		params.add("userId");
		params.add(AppApplication.USER.data.userId);
		params.add("type");
		params.add("4");
		params.add("grade");
		params.add("4");// type=4&grade=4
		params.add("organizationId");
		params.add("3");// 传3请求所有的营业部信息
		HttpUtils.requestGet(URLs.DOWNLOAD_DEPT_YYB, params, HttpRequestTool.DOWNLOAD_DEPT_YYBALL);
	}
}
