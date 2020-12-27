package com.cninsure.cp.utils.url;

import android.util.Log;


import java.net.URLEncoder;

/**
 * @author :xy-wm
 * date:2020/12/27 23:16
 * usefuLness: CBS_APP
 */
public class URLEncodedUtil {


    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            Log.e("URLEncodedUtil","toURLEncoded error:" + paramString);
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
            Log.e("URLEncodedUtil","toURLEncoded error:"+paramString, localException);
        }

        return "";
    }
}
