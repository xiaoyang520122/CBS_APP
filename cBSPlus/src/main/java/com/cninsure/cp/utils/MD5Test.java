package com.cninsure.cp.utils;


import android.annotation.SuppressLint;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Test {
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public MD5Test() {
    }

    // return Hexadecimal
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 转锟斤拷锟街斤拷锟斤拷锟斤拷为16锟斤拷锟斤拷锟街达拷
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString().toUpperCase();
    }

    @SuppressLint("DefaultLocale")
	public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 锟矫猴拷锟斤拷锟街滴拷锟脚癸拷希值锟斤拷锟斤拷byte锟斤拷锟斤拷
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString.toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println(MD5Test.GetMD5Code("aaaa"));
    }
}