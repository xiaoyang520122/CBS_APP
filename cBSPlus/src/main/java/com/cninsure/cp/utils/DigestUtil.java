package com.cninsure.cp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.NameValuePair;

import android.util.Log;

public class DigestUtil {

	public static String getDigestByNamevaluepairList(List<NameValuePair> value) {

		Map<String, String> map1 = new HashMap<String, String>();
		for (NameValuePair nv : value) {
			map1.put(nv.getName(), nv.getValue());
		}

		Map<String, String> map = new TreeMap<String, String>(map1);

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String mapKey = entry.getKey();
			String mapValue = entry.getValue();

			sb.append(mapKey).append("=").append(mapValue).append("&");
		}
		String sbStr = sb.toString();
		sbStr = sbStr.substring(0, sbStr.length() - 1);
		Log.i("JsonHttpUtils", "拼接的字符串=======：" + sbStr);
		return MD5Test.GetMD5Code(sbStr);

	}

	public static String getDigestByStringList(List<String> value) {
		
		Map<String, String> map1 = new HashMap<String, String>();
		for (int i = 0; i < value.size()-1; i += 2) {
			map1.put(value.get(i), value.get(i + 1));
		}

		Map<String, String> map = new TreeMap<String, String>(map1);

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String mapKey = entry.getKey();
			String mapValue = entry.getValue();

			sb.append(mapKey).append("=").append(mapValue).append("&");
		}
		String sbStr = sb.toString();
		sbStr = sbStr.substring(0, sbStr.length() - 1);
		Log.i("JsonHttpUtils", "拼接的字符串=======：" + sbStr);
		return MD5Test.GetMD5Code(sbStr);

	}

}
