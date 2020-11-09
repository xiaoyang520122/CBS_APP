package com.cninsure.cp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.greenrobot.eventbus.EventBus;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.cninsure.cp.AppApplication;
import com.cninsure.cp.entity.PersistentCookieStore;

public class HttpRequestTool {


	/**保存clientID**/
	public final static int RECIVE_CID = 1000;
	/**通过密码登录**/
	public final static int LOGIN_BY_PASS = 1001;
	/**上传clientID**/
	public final static int UPLOAD_CID = 1002;
	/**上传经纬度**/
	public final static int UPLOAD_LOCATION = 1003;
	/**获取用户当前任务列表**/
	public final static int GET_SELFORDER = 1004;
	/**接收订单调度post**/
	public final static int RECEIVE_ORDER = 1005;
	/**取消订单调度post**/
	public final static int CANCEL_ORDER = 1006;
	/**车险业务-查看案件的详细信息（所有的信息） GET**/
	public final static int GET_ORDER_INFO = 1007;
	/**统计用户作业订单各个类型案件量**/
	public final static int GET_STATUS_CONUT=1008;
	/**获取用户指定状态调度任务列表**/
	public final static int GET_STATU_SELFORDER = 1009;
	/**获取用户详细信息 GET**/
	public final static int GET_USER_INFO = 1010;
	/**作业信息-文件上传-上传图片 POST**/
	public final static int UPLOAD_WORK_PHOTO = 1011;
	/**获取上传图片类型**/
	public final static int GET_PHOTO_TYPE = 1012;
	/**获取上传图片类型get**/
	public final static int GET_WORK_PHOTO = 1013;
	/**车险作业界面头部回显**/
	public final static int GET_WORK_MESSAGE = 1014;
	/**获取版本信息GET **/
	public final static int GET_VERSION_INFO = 1015;
	/**修改个人用户名密码 POST**/
	public final static int CHANG_PASS = 1016;
	/**查勘回显 get**/
	public final static int GET_WORK_MESSAGES = 1017;
	/**查勘作业保存 post**/
	public final static int SAVE_WORK_INFO = 1018;
	/**查勘作业提交审核 post**/
	public final static int SUBMIT_WORK = 1019;
	/**获取非车任务列表 post**/
	public final static int FC_GET_WORK_CASE_LIST = 1020;
	/**获取非作业所需字典库数据 post**/
	public final static int FC_GET_DICT_LIST=1021;
	/**获取非作业保存回显信息 post**/
	public final static int FC_GET_WORK_INFO=1022;
	/**上传保存非车作业信息 post**/
	public final static int FC_SAVE_WORK_INFO=1023;
	/**扫码成功 post**/
	public final static int SCANNER_SUCCESS=1024;
	/**扫码登录 post**/
	public final static int SCANNER_LOGIN=1025;
	/**获取非车上次影像信息 post**/
	public final static int DOWNLOAD_WORK_FILE=1026;
	/****作业暂存 post post**/
	public final static int SAVE_WORK_TEMP=1027;
	/**获取营业部信息 GET userId用户信息；organizationId查询营业部信息的父级ID**/
	public final static int DOWNLOAD_DEPT_YYB=1028;
	/**非车查询作业完成案件列表接口 POST**/
	public final static int GET_FC_WORKED_LIST=1029;
	/**获取车险用户完成任务列表**/
	public final static int GET_SELFORDER_END = 1030;
	/**获取非车指定案件状态的列表数据 POST**/
	public final static int GET_FC_SC_STATUS_LIST = 1031;
	/**获取车险用户提交审核或者取消订单任务列表**/
	public final static int GET_CANCEL_SUBMIT = 1032;
	/**车险 作业操作-留言-查看回显 GET **/
	public final static int GET_LEAVING_MESSAGE = 1033;
	/**车险 作业操作-留言-提交留言 POST **/
	public final static int SAVE_LEAVING_MESSAGE = 1034;
	/**非车 月公估师薪酬明细汇总接口 POST **/
	public final static int GET_FC_GGS_SCORE=1035; 
	/**车险 公估师薪酬明细汇总接口月度 **/
	public final static int GET_CX_GGS_SCORE=1036; 
	/**车险 公估师薪酬明细汇总接口年度 **/
	public final static int GET_CX_GGS_SCOREY=1037;
	/**非车 年公估师薪酬明细汇总接口 POST **/
	public final static int GET_FC_GGS_SCOREY=1038; 
	/**获取委托人，"data":{"name":"安诚"}为模糊查询，"data":null或者"data":{"name":"%"} 为查询所有**/
	public final static int GET_WT_REN=1039;
	/**获取营业部信息 GET userId用户信息；organizationId查询营业部信息的父级ID**/
	public final static int DOWNLOAD_DEPT_YYBALL=1040;
	/**获取公估师列表**/
	public final static int GET_GGS_LIST=1041;
	/**获取订单状态 userId=……&orderUid=……    GET**/
	public final static int GET_ORDER_STATUS=1042;
	/**清空服务器端上传clientID**/
	public final static int CLEAN_CID = 1043;
	/**WEB隐藏或者显示车险作业界面标题栏**/
	public final static int SET_VISIVILITY = 1044;
	/**提交非车接报案信息 post**/
	public final static int SUBMIT_FC_NEW_CASE = 1045; 
	/**大灾选项列表**/
	public final static int  GET_FC_DZ_DICT=1046; 
	/**签字请求**/
	public final static int  LINEPATH=1047; 
	/**webView请求刷新界面**/
	public final static int  WEB_BACK_FLASH=1048; 
	/**案件自调度 POST**/
	public final static int  ADD_NEW_ORDER_SELF=1049; 
	/**请求定损维修数据**/
	public final static int  DOWN_PARTS_INFO=1050; 
	/**保存用户银行卡信息**/
	public final static int  SAVE_BANK_INFO=1051;  
	/**非车计算公估师贡献比例**/
	public final static int  CALCULATION=1052;  
	/**获取非车接报案回显信息 post**/
	public final static int  FC_GET_CASE_INFO=1053;  
	/**获取医健险公估师任务列表**/
	public final static int  YJX_GGS_ORDER_LIST=1054;  
	/**医健险调度退回**/
	public final static int  YJX_ORDER_BACK=1055;  
	/**接受医健险调度**/
	public final static int  YJX_ORDER_ACCEPT=1056; 
	/**作业详情*/
	public final static int  GET_WORK_INFO=1057; 
	/**接报案详情*/
	public final static int  GET_BAOAN_INFO=1058;  
	/**单个调度详情-GET*/
	public final static int  GET_DISPATCH_INFO=1059;   
	/**医健险-文件上传 POST*/
	public final static int  UPLOAD_FILE_PHOTO=1060;   
	/**医健险作业保存POST*/
	public final static int  POST_YJX_WORK_SAVE=1061;   
	/**接报案保存-作业处专用POST*/
	public final static int  POST_YJX_BAOAN_UPDATE=1062; 
	/**E公估委托人获取*/
	public final static int GET_E_WT_REN =1063; 
	/**E公估获取省份信息*/
	public final static int GET_PROVINCE_LIST =1064; 
	/**E公估获取市信息*/
	public final static int GET_CITYE_LIST =1065; 
	/**E公估获取区信息*/
	public final static int GET_AREA_LIST =1066; 
	/**E公估获取市信息*/
	public final static int GET_CITYE_LIST2 =1067; 
	/**E公估医健险保存接报案*/
	public final static int POST_YJX_BAOAN_SAVE =1068;  
	/**E公估医健险接报案列表*/
	public final static int GET_YJX_BAOAN_LIST =1069; 
	/**E公估医健险接报案删除：POST*/
	public final static int DELETE_YJX_BAOAN_CASE = 1070;
	/**E公估医健险接报案退回：POST*/
	public final static int BACK_YJX_BAOAN_CASE = 1071;
	/**E公估医健险接报案对应调度列表：GET*/
	public final static int YJX_BAOAN_CASE_DISPATCH_LIST = 1072;
	/**E公估医健险接报案调度保存：POST*/
	public final static int YJX_BAOAN_CASE_DISPATCH_SAVE = 1073;
	/**E公估医健险接报案调度删除：POST*/
	public final static int YJX_BAOAN_CASE_DISPATCH_DELETE = 1074;
	/**获取医健险待审核任务列表：GET*/
	public final static int YJX_SHENHE_ORDER_LIST = 1075;
	/**医健险审核退回：post*/
	public final static int YJX_SHENHE_REJECT = 1076;
	/**医健险审核通过：post*/
	public final static int YJX_SHENHE_PASS = 1077;
	/**获取审核差错类型*/
	public final static int GET_ERROR_TYPE = 1078;

	/**获取医健险公估师任务列表**/
	public final static int  FSX_GGS_ORDER_LIST=1079;
	/**公估师接受或拒绝任务 --GET**/
	public final static int  FSX_GGS_ACCEPT_ORDER=1080;
	/**公估师取消任务 --GET**/
	public final static int  FSX_GGS_CANCEL_ORDER=1081;
	/**公估师取消任务或到达现场 --GET**/
	public final static int  FSX_GGS_SAVE_ORDER=1082;
	/**作业图像信息下载 --GET**/
	public final static int  FSX_WORK_IMG_DOWLOAD=1083;
	/**作业详细信息查看 --GET**/
	public final static int  FSX_WORK_INFO=1084;
	/**作业信息保存 --POST**/
	public final static int  FSX_WORK_INFO_SAVE=1085;
	/**作业提交审核 --POST**/
	public final static int  FSX_WORK_SUBMIT_RIVIEW=1086;

	/**********************************车险新全流程*************************************/

	/**作业提交审核 --POST**/
	public final static int  CX_NEW_GET_GGS_ORDER=1087;
	/**作业提交审核 --POST**/
	public final static int  CX_NEW_GET_IMG_TYPE_DICT=1088;
	/**POST 作业保存 userId:User-20180103101603-687B671A  ,orderUid:ORDER--20200706105235-30E90  content{“key”:"value"}作业内容，保存为JSON对象 ,status 0：暂存；1：提交（送审）**/
	public final static int  CX_NEW_WORK_SAVE=1089;
	/**  GET 查看订单作业详情 userId:User-20180103101603-687B671A    orderUid:ORDER--20200706105235-30E90 */
	public final static int  CX_NEW_GET_ORDER_VIEW_BY_UID=1090;
	/**GET 查看接报案详情 uid报案UID，userid用户Uid**/
	public final static int  CX_JIE_BAOAN_INFO=1091;
	/**GET查看委托人简称详情 **/
	public final static int  CX_GET_WT_SHORT_INFO=1092;
	/**Get 根据报案编号查询订单信息 **/
	public final static int  CX_GET_ORDER_LIST_BY_BAOAN_UID=1093;
	/**Get 根据报案编号查询订单信息 **/
	public final static int  CX_GET_ORDER_VIEW=1094;
	/**Get 作业图片查询 **/
	public final static int  CX_GET_WORK_IMG=1095;
	/**作业提交审核 ImagFragment用 --POST**/
	public final static int  CX_NEW_GET_IMG_TYPE_DICT_IF=1096;
	/**查询订单留言信息 */
	public final static int  CX_GET_EXAMINE_INFO=1097;
	/**留言保存 */
	public final static int  CX_SAVE_EXAMINE_INFO=1098;
	/**获取用户账户信息 */
	public final static int  CX_EXT_USER=1099;
	/**获取提现列表 */
	public final static int  CHOICE_EXTRACT_ORDER_REQUEST=1100;
	/**提交提现申请 */
	public final static int  POS_APPLY_SUBMIT=1101;
	/**提现历史记录 */
	public final static int  POS_APPLY_HISTORY=1102;










	public static void sendGet(String url, List<String> params,int typecode){
		
		String result = "";
		BufferedReader in = null;
		int responsecode = 0;
		String path = url;
		
		if (url.indexOf("?")==-1) {
			params.add("client");
			params.add("android");
			params.add("timestamp");
			params.add(new Date().getTime()+"");
			params.add("digest");
			params.add(DigestUtil.getDigestByStringList(params));
			
			/** 拼接请求地址 **/
			if (params != null) {
				path = url + "?";

				for (int i = 0; i < params.size(); i++) {
					path = path + params.get(i);
					if (i % 2 != 1) {
						path = path + "=";
					} else if (i < (params.size() - 1)) {
						path = path + "&";
					}
				}
			}
		}
		
		Log.i("JsonHttpUtils",typecode+ "提交地址：" + path);
		try {
			URL url2 = new URL(path);
			URI uri=new URI(url2.getProtocol(), url2.getUserInfo(), url2.getHost(), url2.getPort(), url2.getPath(), url2.getQuery(), null);
			// 定义HttpClient
			HttpClient client = new DefaultHttpClient();
			// 实例化HTTP方法
			HttpGet request;
			if (GET_CX_GGS_SCORE==typecode || GET_CX_GGS_SCOREY==typecode) { //这个接口请求参数日期格式中有空格需要特殊处理，将空格装换为转义字符，否则会报错
				request = new HttpGet(uri);
			}else {
				request = new HttpGet();
				request.setURI(new URI(path));
			}
			HttpResponse response;
			Log.i("requst_code", "开始添加头：" + typecode);
//			if (GET_COUPON == typecode) {
				PersistentCookieStore cookieStore = new PersistentCookieStore(AppApplication.getInstance().getApplicationContext());
				((AbstractHttpClient) client).setCookieStore(cookieStore);
				Log.i("JsonHttpUtils", typecode +"请求内容头cookieStores==" + cookieStore.getCookies().toString());
//			}
			response = client.execute(request);

			responsecode = response.getStatusLine().getStatusCode();
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();
			result = sb.toString();

		} catch (Exception e) {
			responsecode=400;
			result="网络连接出错！";
			e.printStackTrace();
		}
		Log.i("JsonHttpUtils", "请求返回数据" + typecode + "为：" + result);
		if (!TextUtils.isEmpty(result)) {
			// isUserLoging(result);
			List<NameValuePair> listparms = new ArrayList<NameValuePair>(2);
			listparms.add(new BasicNameValuePair(typecode + "", result));
			listparms.add(new BasicNameValuePair("responsecode", responsecode + ""));
			EventBus.getDefault().post(listparms);
		}
		
	}

	
	
	@SuppressLint("NewApi")
	public static String sendPost(String url, List<NameValuePair> params, int typecode) {
		
		String result = "{'doStatu':'false','doMsg':'请求失败'}";
		int responsecode = 0;
		
		if (url.indexOf("app/interface")==-1) {//非车接口不用加密
			params.add(new BasicNameValuePair("client", "android"));
			params.add(new BasicNameValuePair("timestamp", new Date().getTime()+""));
			params.add(new BasicNameValuePair("digest", DigestUtil.getDigestByNamevaluepairList(params)));
		
		HttpPost httpPost = new HttpPost(url);
		DefaultHttpClient httpClient= new DefaultHttpClient();

		String str = "";
		for (NameValuePair nv : params) {
			str += nv.getName() + "=" + nv.getValue() + ":";
		}
		Log.i("JsonHttpUtils", "请求网址" + typecode + "为：" + url);
		Log.i("JsonHttpUtils", "请求参数" + typecode + "为：" + str);

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse httpResp;
			if (typecode != LOGIN_BY_PASS) {
				PersistentCookieStore cookieStore = new PersistentCookieStore(AppApplication.getInstance().getApplicationContext());
				((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			}
			httpResp = httpClient.execute(httpPost);
			responsecode = httpResp.getStatusLine().getStatusCode();
			if (httpResp.getStatusLine().getStatusCode() == 200 || httpResp.getStatusLine().getStatusCode() == 400) {
				saveCookie(httpClient, typecode);
				result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
				Log.i("requst_code", "HttpPost方式请求成功，返回数据如下：");
				Log.i("requst_code", result);
			} else {
				Log.e("JsonHttpUtils", "1**********************************************************************************");
				Log.i("HttpPost", "HttpPost方式请求失败，非200,400");
				System.out.println("0000===>" + EntityUtils.toString(httpResp.getEntity(), "UTF-8"));
				try {
					result = "{success:false,msg:'请求失败1'"+EntityUtils.toString(httpResp.getEntity(), "UTF-8")+"}";
				} catch (Exception e) {
					result = "{success:false,msg:'请求失败1'}";
					e.printStackTrace();
				}
			}
		} catch (ConnectTimeoutException e) {
			responsecode=400;
			result="网络连接出错！";
			Log.e("JsonHttpUtils", "2**********************************************************************************");
			e.printStackTrace();
			result = "{success:false,msg:'TIME_OUT' }";
		} catch (UnsupportedEncodingException e) {
			responsecode=400;
			result="网络连接出错！";
			Log.e("JsonHttpUtils", "3**********************************************************************************");
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			responsecode=400;
			result="网络连接出错！";
			Log.e("JsonHttpUtils", "4**********************************************************************************");
			e.printStackTrace();
		} catch (IOException e) {
			responsecode=400;
			result="网络连接出错！";
			Log.e("JsonHttpUtils", "5**********************************************************************************");
			e.printStackTrace();
		} finally {
			httpPost.abort();
			httpClient.getConnectionManager().shutdown();
		}
		}else {//非车接口调用指定请求获取数据
			if (params.size()>1) {//参数长度大于1代表非车接口需要通过form表单的形式提交
				HttpPost httpPost = new HttpPost(url);
				DefaultHttpClient httpClient= new DefaultHttpClient();

				String str = "";
				for (NameValuePair nv : params) {
					str += nv.getName() + "=" + nv.getValue() + ":";
				}
				Log.i("JsonHttpUtils", "请求网址" + typecode + "为：" + url);
				Log.i("JsonHttpUtils", "请求参数" + typecode + "为：" + str);

				try {
					httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
					HttpResponse httpResp;
					if (typecode != LOGIN_BY_PASS) {
						PersistentCookieStore cookieStore = new PersistentCookieStore(AppApplication.getInstance().getApplicationContext());
						((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
					}
					httpResp = httpClient.execute(httpPost);
					responsecode = httpResp.getStatusLine().getStatusCode();
					if (httpResp.getStatusLine().getStatusCode() == 200 || httpResp.getStatusLine().getStatusCode() == 400) {
						saveCookie(httpClient, typecode);
						result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
						Log.i("requst_code", "HttpPost方式请求成功，返回数据如下：");
						Log.i("requst_code", result);
					} else {
						Log.e("JsonHttpUtils", "1**********************************************************************************");
						Log.i("HttpPost", "HttpPost方式请求失败");
						System.out.println("0000===>" + EntityUtils.toString(httpResp.getEntity(), "UTF-8"));
						result = "{success:false,msg:'请求失败1'}";
					}
				} catch (ConnectTimeoutException e) {
					responsecode=400;
					result="网络连接出错！";
					Log.e("JsonHttpUtils", "2**********************************************************************************");
					e.printStackTrace();
					result = "{success:false,msg:'TIME_OUT' }";
				} catch (UnsupportedEncodingException e) {
					responsecode=400;
					result="网络连接出错！";
					Log.e("JsonHttpUtils", "3**********************************************************************************");
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					responsecode=400;
					result="网络连接出错！";
					Log.e("JsonHttpUtils", "4**********************************************************************************");
					e.printStackTrace();
				} catch (IOException e) {
					responsecode=400;
					result="网络连接出错！";
					Log.e("JsonHttpUtils", "5**********************************************************************************");
					e.printStackTrace();
				} finally {
					httpPost.abort();
					httpClient.getConnectionManager().shutdown();
				}
			}else {//参数长度等于1代表非车接口需要通过字符串形式提交
				List<String> resposnseList=doPost(url, params.get(0).getValue(),true);
				result=resposnseList.get(1);
				Log.i("JsonHttpUtils", "请求参数" + typecode + "为：url="+url+"请求参数："+params.get(0).getValue()+"返回参数：" + result);
				responsecode=Integer.parseInt(resposnseList.get(0));
			}
		}
		Log.i("JsonHttpUtils", "请求返回数据" + typecode + ",网页返回码："+responsecode+"为：" + result);
//		if (!TextUtils.isEmpty(result)) {
			List<NameValuePair> listparms = new ArrayList<NameValuePair>(2);
			listparms.add(new BasicNameValuePair(typecode + "", result));
			listparms.add(new BasicNameValuePair("responsecode", responsecode + ""));
			EventBus.getDefault().post(listparms);
//		}
		return result;
	}
	
	

	private static void saveCookie(DefaultHttpClient httpClient, int typecode) {
		PersistentCookieStore myCookieStore = AppApplication.getInstance() .getPersistentCookieStore();
		if (cookie!=null) {
			cookie.clear();
		}
		cookie=httpClient.getCookieStore();
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		for (Cookie cookie : cookies) {
			myCookieStore.addCookie(cookie);
		}
	}
	public static CookieStore cookie;
	public static int ii;

	public static List<String> doPost(String url, String sendJSOn, boolean bl)  {
		Log.i("JsonHttpUtils", "请求返回数据1020 请求次数"+(++ii));
		HttpClient httpclient =new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		String responseBody = null;
		List<String> resposnseList=new ArrayList<String>(2);
		try {
			// 解决中文乱码问题
			StringEntity entity = new StringEntity(sendJSOn.toString(), "utf-8");
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json;charset=UTF-8");
			post.setEntity(entity);
			
			post.addHeader("Content-Type", "application/json; charset=utf-8");
			HttpResponse response = httpclient.execute(post);
			resposnseList.add(""+response.getStatusLine().getStatusCode());
			responseBody = EntityUtils.toString(response.getEntity(), "utf-8");

		} catch (java.net.SocketTimeoutException ste) {
			responseBody = ste.getMessage();
		} catch (Exception e) {
			responseBody = e.getMessage();
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		resposnseList.add(responseBody);
		return resposnseList;
	}
}
