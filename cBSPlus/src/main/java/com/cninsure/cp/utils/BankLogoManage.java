package com.cninsure.cp.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import com.cninsure.cp.R;

public class BankLogoManage {

	/**获取完成银行logo图标（包括图标和文本）**/
	@SuppressLint("UseSparseArrays")
	public static List<NameValuePair> getbankLogo(){
		List<NameValuePair> logoMap=new ArrayList<NameValuePair>();
		logoMap.add(new BasicNameValuePair(R.drawable.bank_zhongxin+"","中信银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_gongshang+"","工商银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_guangda+"","光大银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_youzheng+"","邮政银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_guangfa+"","广发银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_huaxia+"","华夏银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_jianhang+"","建设银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_jiaotong+"","交通银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_minsheng+"","民生银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_nonghang+"","农业银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_pingan+"","平安银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_pudong+"","上海浦发"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_xingye+"","兴业银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_zhaoshang+"","招商银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.bank_zhongyin+"","中国银行"));
		return logoMap;
	}
	
	/**获取银行logo图标（只有图标部分）**/
	@SuppressLint("UseSparseArrays")
	public static List<NameValuePair> getbanklitleLogo(){
		List<NameValuePair> logoMap=new ArrayList<NameValuePair>();
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_zhongxin+"","中信银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_gongshang+"","工商银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_guangda+"","光大银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_youzheng+"","邮政银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_guangfa+"","广发银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_huaxia+"","华夏银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_jianshe+"","建设银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_jiaotong+"","交通银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_minsheng+"","民生银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_nongye+"","农业银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_pingan+"","平安银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_pufa+"","上海浦发"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_xingye+"","兴业银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_zhaoshang+"","招商银行"));
		logoMap.add(new BasicNameValuePair(R.drawable.litlelogo_zhongyin+"","中国银行"));
		return logoMap;
	}
}
