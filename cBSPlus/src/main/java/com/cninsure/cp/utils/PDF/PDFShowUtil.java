package com.cninsure.cp.utils.PDF;

import android.content.Context;
import android.content.Intent;

/**
 * @author :xy-wm
 * date:2020/12/4 10:25
 * usefuLness: CBS_APP
 */
public class PDFShowUtil {

    public void startActivity(Context context, String path){
        Intent intent=new Intent(context, DisplayBaoGaoActivity.class);
        intent.putExtra("baogaoPath", path);
        context.startActivity(intent);
    }
}
