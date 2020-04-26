//package com.cninsure.cp.utils;
//
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.security.KeyManagementException;
//import java.security.NoSuchAlgorithmException;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
//import org.apache.http.Header;
//import org.apache.http.HttpException;
//import org.apache.http.HttpRequest;
//import org.apache.http.HttpRequestInterceptor;
//import org.apache.http.HttpResponse;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.NTCredentials;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.auth.params.AuthPNames;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.params.AuthPolicy;
//import org.apache.http.conn.scheme.Scheme;
//import org.apache.http.conn.scheme.SchemeRegistry;
//import org.apache.http.conn.ssl.SSLSocketFactory;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.auth.NTLMSchemeFactory;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.CoreConnectionPNames;
//import org.apache.http.params.HttpParams;
//import org.apache.http.protocol.HttpContext;
//import org.apache.http.util.EntityUtils;
//
//public class HttpClientUtils {
//	// private static Logger logger = Logger.getLogger("HttpClientUtils");
//
//	/** 连接超时时间（默认3秒 3000ms） 单位毫秒（ms） */
//	private int connectionTimeout = 30000;
//
//	/** 读取数据超时时间（默认30秒 30000ms） 单位毫秒（ms） */
//	private int soTimeout = 30000;
//
//	/** 代理主机名 */
//	private String proxyHost;
//
//	/** 代理端口 */
//	private int proxyPort;
//
//	/** 代理主机用户名 */
//	private String proxyUser;
//
//	/** 代理主机密码 */
//	private String proxyPwd;
//
//	/** 代理主机域 */
//	private String proxyDomain;
//
//	/** 字符集设置，默认UTF-8 */
//	private String charset = "UTF-8";
//
//	private Header[] httpsCookieHeaders;
//
//	public String getProxyUser() {
//		return proxyUser;
//	}
//
//	public void setProxyUser(String proxyUser) {
//		this.proxyUser = proxyUser;
//	}
//
//	public String getProxyPwd() {
//		return proxyPwd;
//	}
//
//	public void setProxyPwd(String proxyPwd) {
//		this.proxyPwd = proxyPwd;
//	}
//
//	public String getProxyDomain() {
//		return proxyDomain;
//	}
//
//	public void setProxyDomain(String proxyDomain) {
//		this.proxyDomain = proxyDomain;
//	}
//
//	public int getConnectionTimeout() {
//		return connectionTimeout;
//	}
//
//	public void setConnectionTimeout(int connectionTimeout) {
//		this.connectionTimeout = connectionTimeout;
//	}
//
//	public String getProxyHost() {
//		return proxyHost;
//	}
//
//	public void setProxyHost(String proxyHost) {
//		this.proxyHost = proxyHost;
//	}
//
//	public int getProxyPort() {
//		return proxyPort;
//	}
//
//	public void setProxyPort(int proxyPort) {
//		this.proxyPort = proxyPort;
//	}
//
//	public int getSoTimeout() {
//		return soTimeout;
//	}
//
//	public void setSoTimeout(int soTimeout) {
//		this.soTimeout = soTimeout;
//	}
//
//	public String getCharset() {
//		return charset;
//	}
//
//	public void setCharset(String charset) {
//		this.charset = charset;
//	}
//
//	private static X509TrustManager tm = new X509TrustManager() {
//		public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
//		}
//
//		public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
//		}
//
//		public X509Certificate[] getAcceptedIssuers() {
//			return null;
//		}
//	};
//
//	/**
//	 * 获取一个针对http的HttpClient
//	 */
//	private HttpClient getHttpClient()// boolean useHttps
//			throws KeyManagementException, NoSuchAlgorithmException {
//		HttpParams httpParams = new BasicHttpParams();
//		// 设置代理
//		// if (!StringUtils.isEmpty(proxyHost)) {
//		// HttpHost proxy = new HttpHost(proxyHost, proxyPort);
//		// httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//		// }
//
//		// 设置超时时间
//		httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);
//		httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//		httpclient.setParams(httpParams);
//		// 代理需要认证
//		if (proxyUser != null) {
//			if (proxyDomain != null) {// NTLM认证模式
//				httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
//				httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, new NTCredentials(proxyUser, proxyPwd, proxyHost, proxyDomain));
//				List<String> authpref = new ArrayList<String>();
//				authpref.add(AuthPolicy.NTLM);
//				httpclient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
//			} else {// BASIC模式
//				CredentialsProvider credsProvider = new BasicCredentialsProvider();
//				credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(proxyUser, proxyPwd));
//				httpclient.setCredentialsProvider(credsProvider);
//			}
//		}
//		httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
//			public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
//				if (!request.containsHeader("Accept")) {
//					request.addHeader("Accept", "*/*");
//				}
//				if (request.containsHeader("User-Agent")) {
//					request.removeHeaders("User-Agent");
//				}
//				if (request.containsHeader("Connection")) {
//					request.removeHeaders("Connection");
//				}
//				request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:8.0) Gecko/20100101 Firefox/8.0");
//				request.addHeader("Connection", "keep-alive");
//			}
//		});
//		return httpclient;
//	}
//
//	/**
//	 * 获取一个针对https的HttpClient
//	 */
//	private HttpClient getHttpsClient() throws KeyManagementException, NoSuchAlgorithmException {
//		HttpClient httpclient = getHttpClient();
//		SSLContext sslcontext = SSLContext.getInstance("TLS");
//		sslcontext.init(null, new TrustManager[] { tm }, null);
//		SSLSocketFactory ssf = new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//		httpclient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, ssf));
//		return httpclient;
//	}
//
//	/**
//	 * 创建post请求
//	 * 
//	 * @param url
//	 * @return HttpPost
//	 */
//	private HttpPost getHttpPost(String url) {
//		// 创建post请求
//		HttpPost post = new HttpPost(url);
//		if (httpsCookieHeaders != null && httpsCookieHeaders.length > 0) {
//			post.setHeaders(httpsCookieHeaders);
//		}
//		return post;
//	}
//
//	/**
//	 * 创建get请求
//	 * 
//	 * @param url
//	 * @return HttpGet
//	 */
//	private HttpGet getHttpGet(String url) {
//		HttpGet get = new HttpGet(url);
//		if (httpsCookieHeaders != null && httpsCookieHeaders.length > 0) {
//			get.setHeaders(httpsCookieHeaders);
//		}
//		return get;
//	}
//
//	/**
//	 * 获取response里的cookies
//	 * 
//	 * @param response
//	 */
//	private void getRequestCookieHeader(HttpResponse response) {
//		Header[] responseHeaders = response.getHeaders("Set-Cookie");
//		if (responseHeaders == null || responseHeaders.length <= 0) {
//			return;
//		}
//		httpsCookieHeaders = new BasicHeader[responseHeaders.length];
//		for (int i = 0; i < responseHeaders.length; i++) {
//			httpsCookieHeaders[i] = new BasicHeader("Cookie", responseHeaders[i].getValue());
//		}
//
//	}
//
//	/**
//	 * 以get方式请求，返回String型结果
//	 * 
//	 * @param url
//	 * @return
//	 * @throws Exception
//	 */
//	public String doGet(String url) throws Exception {
//		HttpClient httpclient = getHttpsClient();
//		HttpGet get = getHttpGet(url);
//		String responseBody = null;
//		try {
//			HttpResponse response = httpclient.execute(get);
//			getRequestCookieHeader(response);
//
//		} catch (java.net.SocketTimeoutException ste) {
//			responseBody = ste.getMessage();
//		} catch (Exception e) {
//			responseBody = e.getMessage();
//			e.printStackTrace();
//		} finally {
//			httpclient.getConnectionManager().shutdown();
//		}
//		return responseBody;
//	}
//
//	String sessionID = "";
//
//	/**
//	 * 避免HttpClient的”SSLPeerUnverifiedException: peer not authenticated”异常
//	 * 不用导入SSL证书
//	 * 
//	 * @author shipengzhi(shipengzhi@sogou-inc.com)
//	 * 
//	 */
//	public static class WebClientDevWrapper {
//
//		public static org.apache.http.client.HttpClient wrapClient(org.apache.http.client.HttpClient base) {
//			try {
//				SSLContext ctx = SSLContext.getInstance("TLS");
//				X509TrustManager tm = new X509TrustManager() {
//					public X509Certificate[] getAcceptedIssuers() {
//						return null;
//					}
//
//					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//					}
//
//					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//					}
//				};
//				ctx.init(null, new TrustManager[] { tm }, null);
//				SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//				SchemeRegistry registry = new SchemeRegistry();
//				registry.register(new Scheme("https", 443, ssf));
//				ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
//				return new DefaultHttpClient(mgr, base.getParams());
//			} catch (Exception ex) {
//				ex.printStackTrace();
//				return null;
//			}
//		}
//	}
//
//	/**
//	 * 以post方式请求，返回String型结果
//	 * 
//	 * @param url
//	 * @param nvps
//	 * @return
//	 * @throws Exception
//	 */
//	public String doPost(String url, String sendJSOn, boolean bl) throws Exception {
//		HttpClient httpclient = getHttpsClient();
//		HttpPost post = getHttpPost(url);
//		String responseBody = null;
//		try {
//			logger.info("访问地址:" + url);
//			logger.info("发送数据:" + sendJSOn);
//			// 解决中文乱码问题
//			StringEntity entity = new StringEntity(sendJSOn.toString(), "utf-8");
//			entity.setContentEncoding("UTF-8");
//			entity.setContentType("application/json;charset=UTF-8");
//
//			post.setEntity(entity);
//			post.addHeader("Content-Type", "application/json; charset=utf-8");
//			if (!sessionID.equals(""))
//				post.setHeader("Cookie", sessionID);
//			HttpResponse response = httpclient.execute(post);
//			getRequestCookieHeader(response);
//
//			if (bl) {
//				StringBuffer sbf = new StringBuffer();
//				Header[] map = response.getHeaders("Set-Cookie");
//				for (Header o : map) {
//					sbf.append(o.getValue()).append(";");
//				}
//				sessionID = sbf.toString();
//				// logger.info("SESSIONID:" + sessionID);
//			}
//			responseBody = EntityUtils.toString(response.getEntity(), "utf-8");
//			// responseBody=new String(responseBody.getBytes(),"utf-8");
//			logger.info(response.getStatusLine().getStatusCode() + "结果：" + responseBody);
//			response.getEntity().getContent();
//			// responseBody = IOUtils.toString(,
//			// charset);
//
//		} catch (java.net.SocketTimeoutException ste) {
//			responseBody = ste.getMessage();
//		} catch (Exception e) {
//			responseBody = e.getMessage();
//			e.printStackTrace();
//		} finally {
//			httpclient.getConnectionManager().shutdown();
//		}
//		return responseBody;
//	}
//
//	// static String url =
//	// "http://localhost:8080/haochehang_new/system/do/getBalance";
//	static String url = "http://localhost:8080/haochehang_new/mobile/app/getBalance";
//	static String data = "";
//
//	/**
//	 * 给车行负责人发送车行账号
//	 */
//	private static void sendAccount2Factory() {
//		HttpClientUtils httpClient2 = new HttpClientUtils();
//		httpClient2.setConnectionTimeout(60000);
//		httpClient2.setSoTimeout(60000);
//		url = "http://172.25.118.3:8080/claim/mobile/app/do";
//		data = "{\"head\":{\"function\":\"appointment\",\"method\":\"saveAppoint\"},\"data\":{" + "\"garageCode\":\"21819\",\"appointDate\":\"2017-05-03 14:20:00\",\"periodFlag\":\"C\","
//				+ "\"customName\":\"测试唐\",\"plateNo\":\"粤A22222\",\"tel\":\"13249629626\"," + "\"carModel\":\"奥迪A4L 家庭版\",\"item\":\"3\",\"promotionName\":\"\"}}";
//		try {
//			httpClient2.doPost(url, data, false);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * 领券接口
//	 */
//	private static void getCoupons() {
//		HttpClientUtils httpClient2 = new HttpClientUtils();
//		httpClient2.setConnectionTimeout(60000);
//		httpClient2.setSoTimeout(60000);
//		url = "http://192.168.46.39:8080/claim/mobile/aass3/do";
//		data = "{'head':{'function':'promotion','method':'userGetCoupons'},'data':{" + "'plateNo':'粤B12345','tel':'18711670930','promotionID':'19','garageCode':'21819'}}";
//		try {
//			httpClient2.doPost(url, data, false);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * 解析中银短信
//	 * 
//	 * @param
//	 */
//	private static void getSim() {
//		try {
//			String body = "报案号：605012017440600001246(示范综合)，被保人：李科，普通客户，代理人：广东力恒保险代理有限公司，联系人：潘华杰，75722630583，司机：李科，丰田TV7181GL-iD，牌照：粤X9A089，2017-06-13 07:30:00，广东省佛山市顺德区大良，单方事故，责任类型：单方肇事，碰撞，建议报警，非现场，本车车损，第1次出险，乘客、商三、盗抢、基本不计、自燃、车损、司机、风挡、附加不计。【中银保险】";
//
//			if (body.contains("中银保险")) {
//				body = body.replaceAll("【", "[").replaceAll("】", "]").replaceAll("，", ",");
//				String[] bodys = body.split(",");
//				if (body.contains("[中银保险]") && bodys.length > 8) {
//					String reportNo = bodys[0].substring(bodys[0].indexOf("：") + 1, bodys[0].indexOf("("));
//					String phone = bodys[5];
//					String name = bodys[6].substring(bodys[6].indexOf("：") + 1);
//					String carModel = bodys[7];
//					String carNo = bodys[8].substring(bodys[8].indexOf("：") + 1);
//					String address = bodys[10];
//
//					System.out.println(reportNo + "--" + phone + "--" + name + "--" + carModel + "--" + carNo + "--" + address);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private static String obtainLocalMD5(String requestStr, String key) throws UnsupportedEncodingException {
//
//		// 本地密钥(加密因子) 从t_dept表中的secretkey获取
//		String cipherCode = key;
//		// String cipherCode = SziipipData.USER_KEY;
//		// String cipherCode = "A98Gdgksg8763uasiPP0";
//
//		// 移除key,然后转换成json。 也就是获取原来的json
//		JSONObject json = JSONObject.fromObject(requestStr);
//		// // json.remove("key");
//		String str = json.toString();
//		// 将数据转换UTF-8，保持数据统一(数据带中文时需要特殊处理)
//		String originStr = URLEncoder.encode(requestStr, "UTF-8");
//
//		logger.info(originStr + cipherCode);
//		// 转换成md5
//		return MD5.GetMD5Code(originStr + cipherCode);
//	}
//
//	private static String obtainLocalMD5(String requestStr) throws UnsupportedEncodingException {
//
//		// 本地密钥(加密因子) 从t_dept表中的secretkey获取
//		String cipherCode = SziipipData.USER_KEY;
//		// String cipherCode = "A98Gdgksg8763uasiPP0";
//
//		// 移除key,然后转换成json。 也就是获取原来的json
//		JSONObject json = JSONObject.fromObject(requestStr);
//		// // json.remove("key");
//		String str = json.toString();
//		// 将数据转换UTF-8，保持数据统一(数据带中文时需要特殊处理)
//		String originStr = URLEncoder.encode(requestStr, "UTF-8");
//
//		logger.info(originStr + cipherCode);
//		// 转换成md5
//		return MD5.GetMD5Code(originStr + cipherCode);
//	}
//
//	/**
//	 * 行业协会 代理机构上报接口X
//	 */
//	private static void agent() {
//		HttpClientUtils httpClient2 = new HttpClientUtils();
//		httpClient2.setConnectionTimeout(60000);
//		httpClient2.setSoTimeout(60000);
//		// url =
//		// "http://127.0.0.1:8080/regulation/interface/intermediary/agent";
//		url = "http://115.29.175.29:8080/regulation/interface/intermediary/agent";
//
//		String sendJson = "\"data\":{\"deptcode\":\"210051000000800\",\"year\":\"2017\",\"month\":\"7\"," + "\"deptpeoplenow\":\"250\",\"deptpeoplepre\":\"520\",\"certificatepeoplenow\":\"150\","
//				+ "\"certificatepeoplepre\":\"510\",\"premiumnow\":\"1528\",\"premiumpre\":\"25528\"," + "\"turnovernow\":\"475\",\"turnoverpre\":\"7180\",\"profitnow\":\"237\","
//				+ "\"profitpre\":\"270\"},"
//
//				+ "\"listproperty\":[{"
//
//				+ "\"arearange\":\"深圳地区\",\"insname\":\"其他产险\"," + "\"insurancename\":\"企业财产保险\",\"premiumnow\":\"47\",\"premiumpre\":\"1128\","
//				+ "\"premiumsurrender\":\"50\",\"premiumadd\":\"70\",\"premiumminus\":\"60\"," + "\"commissionnow\":\"16\",\"commissionsurrender\":\"60\",\"commissionadd\":\"70\","
//				+ "\"commissionminus\":\"25\",\"commissionreinsurance\":\"61\",\"commissionpre\":\"275\"," + "\"premiumnet\":\"15\",\"premiumtel\":\"50\"}],"
//
//				+ "\"listlife\":[{"
//
//				+ "\"arearange\":\"非深圳地区\",\"insname\":\"泰康人寿保险股份有限公司深圳分公司\"," + "\"insurancename\":\"分红寿险\",\"premiumnow\":\"100\",\"premiumpre\":\"200\","
//				+ "\"premiumrenewalnow\":\"100\",\"premiumrenewalpre\":\"200\",\"commissionnow\":\"50\"," + "\"commissionpre\":\"100\",\"commissionrenewalnow\":\"50\","
//				+ "\"commissionrenewalpre\":\"100\",\"premiumnet\":\"100\",\"premiumtel\":\"50\""
//
//				+ "}]";
//
//		try {
//
//			String key = obtainLocalMD5("{" + sendJson + "}");
//			logger.info(key);
//
//			data = "{\"key\":\"" + key + "\"," + sendJson + "}";
//			httpClient2.doPost(url, data, false);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public static String agent(String postUrl, String sendJson, String privateKey) {
//		HttpClientUtils httpClient2 = new HttpClientUtils();
//		httpClient2.setConnectionTimeout(60000);
//		httpClient2.setSoTimeout(60000);
//		// url =
//		// "http://127.0.0.1:8080/regulation/interface/intermediary/agent";
//
//		try {
//			sendJson = sendJson.replace("listins", "listproperty").replace("listelse", "listlife");
//			String key = obtainLocalMD5("{" + sendJson + "}", privateKey);
//			logger.info(key);
//
//			data = "{\"key\":\"" + key + "\"," + sendJson + "}";
//			return httpClient2.doPost(postUrl, data, false);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "";
//		}
//
//	}
//
//	public static String claim(String postUrl, String sendJson, String privateKey) {
//		HttpClientUtils httpClient2 = new HttpClientUtils();
//		httpClient2.setConnectionTimeout(60000);
//		httpClient2.setSoTimeout(60000);
//
//		url = "http://115.29.175.29:8080/regulation/interface/intermediary/claim";
//		if (postUrl.isEmpty())
//			postUrl = url;
//
//		if (sendJson.isEmpty())
//			sendJson = "\"data\":{\"deptcode\":\"280401440304800\",\"year\":\"2017\",\"month\":\"7\"," + "\"deptname\":\"广州天信保险公估有限公司深圳分公司\",\"deptpeoplenow\":\"354\",\"deptpeoplepre\":\"230\","
//					+ "\"casenow\":\"75\",\"casepre\":\"43\",\"caseclosenow\":\"75\",\"caseclosepre\":\"43\"," + "\"turnovernow\":\"1000.00\",\"turnoverpre\":\"1000.00\",\"profitnow\":\"500.00\","
//					+ "\"profitpre\":\"500.00\",\"insureamountnow\":\"200.00\",\"insureamountpre\":\"200.00\"},"
//
//					+ "\"listins\":[{" + "\"arearange\":\"深圳地区\",\"insname\":\"其他产险\",\"casenow\":\"40\"," + "\"casepre\":\"40\",\"caseclosenow\":\"40\",\"caseclosepre\":\"40\","
//					+ "\"turnovernow\":\"500.11\",\"turnoverpre\":\"500.00\",\"turnovernowins\":\"300.00\"," + "\"turnoverpreins\":\"300.00\",\"turnovernowclaim\":\"200.00\","
//					+ "\"turnoverpreclaim\":\"200.00\"},{" + "\"arearange\":\"深圳地区\",\"insname\":\"安心财产保险有限责任公司深分（虚拟）\",\"casenow\":\"40\","
//					+ "\"casepre\":\"40\",\"caseclosenow\":\"40\",\"caseclosepre\":\"40\"," + "\"turnovernow\":\"500.00\",\"turnoverpre\":\"500.15\",\"turnovernowins\":\"300.00\","
//					+ "\"turnoverpreins\":\"300.00\",\"turnovernowclaim\":\"200.00\"," + "\"turnoverpreclaim\":\"200.00\"}],"
//
//					+ "\"listelse\":[{" + "\"arearange\":\"非深圳地区\",\"insname\":\"交银康联人寿保险有限公司深圳市分公司\"," + "\"casenow\":\"35\",\"casepre\":\"35\",\"caseclosenow\":\"35\",\"caseclosepre\":\"35\","
//					+ "\"turnovernow\":\"500.00\",\"turnoverpre\":\"500.00\",\"turnovernowins\":\"200.00\"," + "\"turnoverpreins\":\"200.00\",\"turnovernowclaim\":\"100.00\","
//					+ "\"turnoverpreclaim\":\"100.00\"}]";
//
//		try {
//			String key = obtainLocalMD5("{" + sendJson + "}", privateKey);
//			System.out.println("sendJson=" + sendJson);
//			// String key = obtainLocalMD5("{"+sendJson+"}");
//			data = "{\"key\":\"" + key + "\"," + sendJson + "}";
//			System.out.println("key sendJson1=" + privateKey + "---" + data);
//			return httpClient2.doPost(postUrl, data, false);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "";
//		}
//
//	}
//
//	private static void property() {
//		HttpClientUtils httpClient2 = new HttpClientUtils();
//		httpClient2.setConnectionTimeout(60000);
//		httpClient2.setSoTimeout(60000);
//
//		// url="http://115.29.175.29:8080/regulation/interface/insurer/property";
//
//		url = "http://127.0.0.1:8080/regulation/interface/insurer/property";
//
//		String sendJson = "\"data\":{\"deptcode\":\"Cxyza\",\"year\":\"2017\",\"month\":\"7\"},\"listagent\":[{\"deptname\":\"太平财产保险有限公司深圳分公司\",\"type\":\"6\",\"insurancename\":\"家庭财产保险\",\"premiumnow\":\"100.0000\",\"premiumpre\":\"200.00\",\"commissionnow\":\"100.00\",\"commissionpre\":\"200.00\"}],\"listclaim\":[{\"deptname\":\"太平财产保险有限公司深圳分公司\",\"caseclosenow\":\"100\",\"turnovernow\":\"100.00\"}],\"listsum\":[{\"typesub\":\"6\",\"amount\":\"10000.0000\",\"premiumproperty\":\"100.00\",\"premiumauto\":\"10.0000\",\"premiumlife\":\"100.00\",\"premiumlifenew\":\"10.0000\",\"premiumsum\":\"200.0000\",\"premiumnet\":\"10.0000\",\"premiumtel\":\"10.0000\"}]";
//
//		try {
//			String key = obtainLocalMD5("{" + sendJson + "}");
//			data = "{\"key\":\"" + key + "\"," + sendJson + "}";
//
//			httpClient2.doPost(url, data, false);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public static void main(String args[]) throws Exception {
//		// agent();
//		// System.out.println(claim("",""));
//		// property();
//		String url = "http://127.0.0.1:8080/new_cbs/app/interface/case/listWorkCase";
//		// String url = "http://cbs.cnsurvey.cn/sys/hr/interface";
//		// String url = "http://127.0.0.1:8080/new_cbs/sys/hr/interface";
//		HttpClientUtils httpClient2 = new HttpClientUtils();
//		httpClient2.setConnectionTimeout(60000 * 3);
//		httpClient2.setSoTimeout(60000 * 3);
//		// String requestJson =
//		// "{\"userToken\":\"2432\",\"requestData\":{\"caseType\":7,\"m\":{\"id\":63199},"
//		// +
//		// "\"devote\":[{\"account\":\"tongjs\",\"name\":\"童加松\",\"isZhuBan\":100,\"onSite\":1,\"dataCollection\":100,\"aggregate\":100"
//		// +
//		// ",\"xunJiaDingSun\":100,\"communicateWithDeputer\":1,\"negotiationWithCustomer\":1,\"writeReport\":100,\"modifyRport\":100}]}}";
//		String requestJson = "{\"requestData\":{\"pageNo\":1,\"pageSize\":999999},\"userToken\":\"-1\"}";
//		// String requestJson =
//		// "{\"userToken\":\"2432\",\"requestData\":{\"pageNo\":1,\"pageSize\":99999}}";
//		// String requestJson =
//		// "{\"key\": \"123456\",\"fgs\": \"\", \"yyb\": \"\",\"date\": \"2017-12\",\"method\": \"seManage\"}";
//		// String requestJson =
//		// "{\"key\": \"123456\",\"fgs\": \"\", \"yyb\": \"\",\"date\": \"2017-12\",\"method\": \"performanceEvaluation\"}";
//		System.out.println("" + requestJson);
//		System.out.println(httpClient2.doPost(url, requestJson, true));
//		/*
//		 * String requestStr =
//		 * "{\"data\":{\"caseclosenow\":\"0\",\"caseclosepre\":\"0\",\"casenow\":\"0\",\"casepre\":\"0\",\"certificatepeoplenow\":\"0\",\"certificatepeoplepre\":\"0\",\"claimamountnow\":\"0\",\"claimamountpre\":\"0\",\"deptcode\":\"280495000000800\",\"deptname\":\"\",\"deptpeoplenow\":\"0\",\"deptpeoplepre\":\"0\",\"insureamountnow\":\"0\",\"insureamountpre\":\"0\",\"month\":\"12\",\"profitnow\":\"0\",\"profitpre\":\"0\",\"turnovernow\":\"0\",\"turnoverpre\":\"0\",\"year\":\"2017\"},\"listelse\":[],\"listins\":[]}"
//		 * ; String requestStr1 =
//		 * "%7B%22data%22%3A%7B%22caseclosenow%22%3A%220%22%2C%22caseclosepre%22%3A%220%22%2C%22casenow%22%3A%220%22%2C%22casepre%22%3A%220%22%2C%22certificatepeoplenow%22%3A%220%22%2C%22certificatepeoplepre%22%3A%220%22%2C%22claimamountnow%22%3A%220%22%2C%22claimamountpre%22%3A%220%22%2C%22deptcode%22%3A%22280495000000800%22%2C%22deptname%22%3A%22%22%2C%22deptpeoplenow%22%3A%220%22%2C%22deptpeoplepre%22%3A%220%22%2C%22insureamountnow%22%3A%220%22%2C%22insureamountpre%22%3A%220%22%2C%22month%22%3A%2212%22%2C%22profitnow%22%3A%220%22%2C%22profitpre%22%3A%220%22%2C%22turnovernow%22%3A%220%22%2C%22turnoverpre%22%3A%220%22%2C%22year%22%3A%222017%22%7D%2C%22listelse%22%3A%5B%5D%2C%22listins%22%3A%5B%5D%7D"
//		 * ; System.out.println("没encode="+MD5.GetMD5Code(requestStr));
//		 * System.out.println("encode="+MD5.GetMD5Code(requestStr1));
//		 * System.out.
//		 * println("没encode+密钥="+MD5.GetMD5Code(requestStr+"H238A2jnb8JHniqe19n"
//		 * )); System.out.println("encode+密钥="+MD5.GetMD5Code(requestStr1+
//		 * "H238A2jnb8JHniqe19n"));
//		 */
//	}
//
//}
