package com.cninsure.cp.navi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.cninsure.cp.AppApplication;
import com.cninsure.cp.utils.ToastUtil;

public class GetGeoCoderUtil {
	
	private static String AK="q4qVT2D0Lhykff5LWwg6js2ISag2Hq3F";//百度APIKey

	// 调用百度地图API根据地址，获取坐标
	public static LatLng getCoordinate(String address) {
		if (address != null && !"".equals(address)) {
			address = address.replaceAll("\\s*", "").replace("#", "栋");
			
			try {
				address = URLEncoder.encode(address, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			String url = "http://api.map.baidu.com/geocoder/v2/?address=" + address + "&output=json&ak=" + AK 
					+"&callback=showLocation&mcode=5B:B0:60:3D:65:16:A8:87:5F:6A:56:82:C7:56:7B:B5:E4:67:E9:2A;com.cninsure.cp";
			String json = loadJSON(url);
			if (json != null && !"".equals(json)) {
				int indexOf=json.indexOf("(");
				if (indexOf>-1) {
					json=json.substring(indexOf+1, json.length()-1);
				}
				try {
					JSONObject obj = new JSONObject(json);
					if ("0".equals(obj.getString("status"))) {
						double lng = obj.getJSONObject("result").getJSONObject("location").getDouble("lng"); // 经度
						double lat = obj.getJSONObject("result").getJSONObject("location").getDouble("lat"); // 纬度
						DecimalFormat df = new DecimalFormat("#.######");
						if (lat>0) {
							LatLng pt_end = new LatLng(lat, lng);
//							return df.format(lng) + "," + df.format(lat);
							return pt_end;
						}
						return null;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static String loadJSON(String url) {
		StringBuilder json = new StringBuilder();
		try {
			URL oracle = new URL(url);
			URLConnection yc = oracle.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream(), "UTF-8"));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				json.append(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
		Log.e("JsonHttpUtils", json.toString());
		ToastUtil.showToastLong(AppApplication.getInstance(), json.toString());
		return json.toString();
	}

	// 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
	/*
	 * public String MD5(String md5) { try { java.security.MessageDigest md =
	 * java.security.MessageDigest .getInstance("MD5"); byte[] array =
	 * md.digest(md5.getBytes()); StringBuffer sb = new StringBuffer(); for (int
	 * i = 0; i < array.length; ++i) { sb.append(Integer.toHexString((array[i] &
	 * 0xFF) | 0x100) .substring(1, 3)); } return sb.toString(); } catch
	 * (java.security.NoSuchAlgorithmException e) { } return null; }
	 */
}
