package com.cninsure.cp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapUtil {


    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bitmap( byte buf[]){
        Bitmap photo_bmp = BitmapFactory.decodeByteArray(buf, 0, buf.length);
        return  photo_bmp;
    }
}
